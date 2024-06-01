package com.aide.ui.services;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.widget.Toast;
import com.aide.engine.EngineSolution;
import com.aide.ui.ServiceContainer;
import com.aide.ui.firebase.FireBaseLogEvent;
import com.aide.ui.project.AndroidProjectSupport;
import com.aide.ui.project.internal.GradleTools;
import com.aide.ui.util.BuildGradle;
import com.aide.ui.util.FileSystem;
import io.github.zeroaicy.aide.extend.ZeroAicyExtensionInterface;
import io.github.zeroaicy.aide.ui.services.ExecutorsService;
import io.github.zeroaicy.util.Log;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import com.aide.ui.util.BuildGradle.MavenDependency;

public class ZeroAicyProjectService extends ProjectService {

	private static final String TAG = "ZeroAicyProjectService";

	private static ProjectService singleton;

	public static ProjectService getSingleton() {
		if (singleton == null) {
			singleton = new ZeroAicyProjectService();
			Log.d(TAG,  "替换ZeroAicyProjectService");
		}
		return singleton;
	}

	public ZeroAicyProjectService() {
		super();

		synchronized (this) {
			this.libraryMapping = new ConcurrentHashMap<>();

			this.Hw = new Vector<String>();

			// Debugger必须在主线程中创建
			// 因为创建了 Handler
			ServiceContainer.getDebugger();
		}
	}

	/**
	 * 在父类构造器调用后立即被调用
	 */
	@Override
	public synchronized List<String> yS() {
		return this.Hw;			
	}

	// 耗时任务 MavenService -> J8 [resolveFullDependencyTree]
	ExecutorsService executorsService = ExecutorsService.getExecutorsService();
	@Override
	protected void jJ() {
		// 空项目
		if (this.currentAppHome == null 
			|| this.pojectSupport == null) {
			List emptyList = Collections.emptyList();
			final EngineSolution engineSolution = new EngineSolution(emptyList, null, com.aide.engine.service.CodeModelFactory.j6(ServiceContainer.Hw()), ServiceContainer.Hw());
            if (ExecutorsService.isUiThread()) {
                ServiceContainer.getEngineService().er(engineSolution);
                ServiceContainer.getEngineService().ef();
                ServiceContainer.getEngineService().ei();
                return;
            }
            sendEngineSolution(engineSolution);
            return;
        }

		executorsService.submit(new Runnable(){
				@Override
				public void run() { 
					// 不要以为使用了同步集合就万事大吉，
					// 同步集合只能保证本身的操作是同步的，
					// 但是它所属的代码块不是同步的话，
					// 多线程情况下也会出问题

					// 如果集合正在遍历，这时又有写入操作就会触发并发错误
					// 如上所述，并发集合仅是集合自己的操作是有锁
					// 但是集合的遍历器不是
					// 防止并发错误
					synchronized (yS()) {
						ProjectSupport dW = ZeroAicyProjectService.this.pojectSupport;
						final EngineSolution engineSolution = dW.makeEngineSolution();
						sendEngineSolution(engineSolution);
					}
				}
			});
	}
	private void sendEngineSolution(final EngineSolution engineSolution) {
		ServiceContainer.aj(new Runnable(){
				@Override
				public void run() {
					ServiceContainer.getEngineService().er(engineSolution);
					ServiceContainer.getEngineService().ef();
					ServiceContainer.getEngineService().ei();
				}
			});
	}

	/*****************************************************************/

	@Override
	public void openProject(final String string) {
		try {

			final ProgressDialog show = ProgressDialog.show(ServiceContainer.getMainActivity(), null, "Opening project...", true, false);
			show.getWindow().addFlags(128);
			show.getWindow().clearFlags(2);

			final Runnable dismissRunnable = new Runnable(){
				@Override
				public void run() {
					show.dismiss();
				}
			};
			executorsService.submit(new Runnable(){
					@Override
					public void run() {
						try {
							super_openProject(string);
						}
						catch (Throwable e) {
							Log.e(" run", "super_cb", e);
						}
						ServiceContainer.aj(dismissRunnable);
					}
				});


		}
		catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public void super_openProject(String str) {
		SharedPreferences sharedPreferences = ServiceContainer.getContext().getSharedPreferences("ProjectService", 0);
		if (!ServiceContainer.isTrainerMode() 
			&& ServiceContainer.getMainActivity().checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE")) {
			if (str != null) {
				FireBaseLogEvent.tp("App init: From intent");
				saveCurrentAppHome(SI(str));
			} else {
				String string = sharedPreferences.getString("CurrentAppHome", null);
				this.currentAppHome = string;
				if (string != null && getProjectSupport(string) == null) {
					this.currentAppHome = null;
				}
			}
		}

		this.pojectSupport = getProjectSupport(this.currentAppHome);

		init();

		if (ZeroAicyProjectService.this.pojectSupport != null) {

			ServiceContainer.getDebugger().P8(ZeroAicyProjectService.this.pojectSupport.yS(), true);
		}
		if (ZeroAicyProjectService.this.currentAppHome != null) {
			FireBaseLogEvent.tp("App init: Opened existing project");
			et(null, false);
			sy("init");
		}
	}



	/*****************************************************************/
	@Override
	protected void init() {
		if (!ExecutorsService.isUiThread()) {
			super_init();
			return;
		}
		executorsService.submit(new Runnable(){
				@Override
				public void run() {
					super_init();
				}
			});
	}

	protected void super_init() {
		this.Hw.clear();
		this.libraryMapping.clear();

		if (this.currentAppHome != null) {
			this.pojectSupport.U2(this.currentAppHome, this.libraryMapping, this.Hw);
		}
	}


	/*****************************************************************/

	protected void super_et(List<String> list, boolean p) {
		if (this.pojectSupport != null) {
			this.pojectSupport.cn(list, p);
		}
	}
	@Override
	public void et(final List<String> list, final boolean p) {
		if (!ExecutorsService.isUiThread()) {
			super_et(list, p);
			return;
		}

		executorsService.submit(new Runnable(){
				@Override
				public void run() {
					super_et(list, p);
				}
			});
	}

	/*****************************************************************/
	public void Ws() {
        try {
            if (this.currentAppHome != null) {
				saveCurrentAppHome(null);
                ServiceContainer.J0().aM();
                ServiceContainer.getNavigateService().Hw();
                ServiceContainer.getOpenFileService().Zo();
                this.Hw.clear();
				//this.FH = new HashMap<>();
				this.libraryMapping.clear();

                ServiceContainer.getDebugger().v5();
                ServiceContainer.getMainActivity().q7();

                jJ();
            }
        }
		catch (Throwable th) {
			th.printStackTrace();
        }
    }
	/*****************************************************************/
	@Override
	public void wc() {
		if (!ExecutorsService.isUiThread()) {
			super_wc();
			return;
		}
		executorsService.submit(new Runnable(){
				@Override
				public void run() {
					super_wc();
				}
			});
	}

	private void super_wc() {
		if (this.currentAppHome != null && getProjectSupport(this.currentAppHome) == null) {
			Ws();
		}
		if (this.currentAppHome != null) {
			init();
		}
		jJ();
	}

	/*****************************************************************/

	public void super_reloadingProject() {
		if (this.currentAppHome != null 
			&& getProjectSupport(this.currentAppHome) == null) {
			Ws();
		}
		ServiceContainer.getDebugger().ef();
		if (this.currentAppHome != null) {
			ServiceContainer.aj(new Runnable(){
					@Override
					public void run() {
						ServiceContainer.sy(ServiceContainer.getMainActivity(), "Reloading project...", new e(), new f());
					}
				});
		} else {
			init();
			jJ();
		}

	}
	@Override
	public void reloadingProject() {

		if (!ExecutorsService.isUiThread()) {
			super_reloadingProject();
			return;
		}

		executorsService.submit(new Runnable(){
				@Override
				public void run() {
					super_reloadingProject();
				}
			});
	}
	/*****************************************************************/

	/*****************************************************************/
	/**
	 * return not download maven
	 */
	public static List<BuildGradle.MavenDependency> qp() {
		ArrayList<BuildGradle.MavenDependency> mavenDependencyList = new ArrayList<>();
		for (String projectPath : ServiceContainer.getProjectService().getLibraryMapping().keySet()) {
			if (!GradleTools.isGradleProject(projectPath)) {
				continue;
			}
			for (BuildGradle.Dependency mavenDependency : getProjectMavenDependencyList(projectPath)) {
				if (mavenDependency instanceof BuildGradle.MavenDependency) {
					List<BuildGradle.MavenDependency> notExistsLocalCache = ServiceContainer.getMavenService().getNotExistsLocalCache(getFlatRepositoryPathMap(projectPath), ((BuildGradle.MavenDependency)mavenDependency));
					for (BuildGradle.MavenDependency dep : notExistsLocalCache) {
						mavenDependencyList.add(dep);							
					}
				}
			}
		}
		return mavenDependencyList;

	}

	private static Map<String, String> getFlatRepositoryPathMap(String str) {
		HashMap<String, String> hashMap = new HashMap<>();
		for (BuildGradle.Repository flatLocalRepository : ZeroAicyExtensionInterface.getBuildGradle().getConfiguration(GradleTools.Zo(str)).curProjectsRepositorys) {
			if (flatLocalRepository instanceof BuildGradle.FlatLocalRepository) {
				hashMap.put(((BuildGradle.FlatLocalRepository)flatLocalRepository).getFlatDir(str), GradleTools.EQ(str));
			}
		}
		return hashMap;
	}
	
	private static List<BuildGradle.Dependency> getProjectMavenDependencyList(String str) {
		BuildGradle configuration = ZeroAicyExtensionInterface.getBuildGradle().getConfiguration(GradleTools.Zo(str));
		String P8 = GradleTools.P8(str);
		if (FileSystem.isFileAndNotZip(P8)) {
			BuildGradle configuration2 = ZeroAicyExtensionInterface.getBuildGradle().getConfiguration(P8);
			if (configuration2.subProjectsDependencies.size() > 0 || configuration2.allProjectsDependencies.size() > 0) {
				ArrayList<BuildGradle.Dependency> arrayList = new ArrayList<>();
				for (BuildGradle.Dependency dependency : configuration2.subProjectsDependencies) {
					if (dependency instanceof BuildGradle.MavenDependency) {
						arrayList.add(dependency);
					}
				}
				for (BuildGradle.Dependency dependency2 : configuration2.allProjectsDependencies) {
					if (dependency2 instanceof BuildGradle.MavenDependency) {
						arrayList.add(dependency2);
					}
				}
				arrayList.addAll(ZeroAicyExtensionInterface.getFlavorDependencies(configuration));
				return arrayList;
			}
		}
		return ZeroAicyExtensionInterface.getFlavorDependencies(configuration);

	}

	private static List<BuildGradle.RemoteRepository> WB(String str) {
		ArrayList<BuildGradle.RemoteRepository> arrayList = new ArrayList<>();
		if (GradleTools.isGradleProject(str)) {
			for (BuildGradle.Repository remoteRepository : ZeroAicyExtensionInterface.getBuildGradle().getConfiguration(GradleTools.Zo(str)).curProjectsRepositorys) {
				if (remoteRepository instanceof BuildGradle.RemoteRepository) {
					arrayList.add((BuildGradle.RemoteRepository)remoteRepository);
				}
			}
			String P8 = GradleTools.P8(str);
			if (FileSystem.isFileAndNotZip(P8)) {
				BuildGradle configuration = ZeroAicyExtensionInterface.getBuildGradle().getConfiguration(P8);
				for (BuildGradle.Repository remoteRepository2 : configuration.allProjectsRepositorys) {
					if (remoteRepository2 instanceof BuildGradle.RemoteRepository) {
						arrayList.add((BuildGradle.RemoteRepository)remoteRepository2);
					}
				}
				for (BuildGradle.Repository remoteRepository3 : configuration.subProjectsRepositorys) {
					if (remoteRepository3 instanceof BuildGradle.RemoteRepository) {
						arrayList.add((BuildGradle.RemoteRepository)remoteRepository3);
					}
				}
			}
		}
		return arrayList;

	}

	Runnable DW_Hw = new Runnable(){
		@Override
		public void run() {
			final ArrayList<BuildGradle.MavenDependency> depList = new ArrayList<>();
			depList.addAll(qp());
			if (depList.isEmpty()) {
				return;
			}
			ServiceContainer.aj(new Runnable(){
					@Override
					public void run() {
						ServiceContainer.getDownloadService().a8(ServiceContainer.gn(), 
							depList, 
							WB(ServiceContainer.getProjectService().getCurrentAppHome()), 
							new Runnable(){
								@Override
								public void run() {
									ServiceContainer.getMavenService().resetDepPathMap();
									ServiceContainer.getProjectService().reloadingProject();
								}
							});
					}
				});

		}
	};
	@Override
	public boolean sG() {

		if (this.pojectSupport instanceof AndroidProjectSupport) {
			executorsService.submit(DW_Hw);
			return true;
		}
		return super.sG();
	}

	/*****************************************************************/
	@Override
	public void buildProject(final boolean isBuildRefresh) {
		if (!ExecutorsService.isUiThread()) {
			ServiceContainer.aj(new Runnable(){
					@Override
					public void run() {
						Toast.makeText(ServiceContainer.getMainActivity(), "开始构建刷新", 0).show();
					}
				});
			if (isBuildRefresh) {
				wc();		
			}
			buildProject2(isBuildRefresh);
			return;
		}

		Toast.makeText(ServiceContainer.getMainActivity(), "开始构建", 0).show();

		try {
			// 等待 wc()
			executorsService.submit(new Runnable(){
					@Override
					public void run() {
						if (isBuildRefresh) {
							wc();		
						}
						ServiceContainer.aj(new Runnable(){
								@Override
								public void run() {
									buildProject2(isBuildRefresh);
								}
							});
					}
				})
				//.get()
				;

		}
		catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void buildProject2(boolean isBuildRefresh) {
		if (J0()) {
			this.v5 = true;
			this.pojectSupport.buildProject(isBuildRefresh);
		} else if (isBuildRefresh) {
			ServiceContainer.getEngineService().vy();
		}
	}

	/*****************************************************************/

	// FireBaseLogEvent??
	public void sy(String str) {
		HashMap<String,String> hashMap = new HashMap<>();
		hashMap.put("isPremium", Boolean.toString(isPremium()));
		hashMap.put("libraryCount", Integer.toString(this.getLibraryMapping().size()));
		hashMap.put("referrer", str); // 来自
		if (AndroidProjectSupport.iW(getCurrentAppHome())) {
			hashMap.put("package", AndroidProjectSupport.getProjectPackageName(getCurrentAppHome(), (String) null));
		}
		FireBaseLogEvent.EQ("Project opened", hashMap);
    }

	private void saveCurrentAppHome(String str) {
		this.currentAppHome = null;
		SharedPreferences.Editor edit = ServiceContainer.getContext().getSharedPreferences("ProjectService", 0).edit();
		edit.putString("CurrentAppHome", str);
		edit.commit();
	}


	private ProjectSupport getProjectSupport(String str) {
		if (str == null) {
			return null;
		}
		for (ProjectSupport projectSupport : ServiceContainer.getProjectSupports()) {
			if (projectSupport.isSupport(str)) {
				return projectSupport;
			}
		}
		return null;
    }
}
