package com.aide.ui.services;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.Window;
import com.aide.engine.EngineSolution;
import com.aide.ui.ServiceContainer;
import io.github.zeroaicy.aide.ui.services.ExecutorsService;
import io.github.zeroaicy.util.Log;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import com.aide.ui.util.FileSystem;
import java.util.Hashtable;
import java.util.HashMap;

public class ZeroAicyProjectService extends ProjectService {
	private static final String TAG = "ZeroAicyProjectService";

	// 使用ProjectService的实现类的类名作为线程池标记
	private final ExecutorsService executorsService = ExecutorsService.getExecutorsService(getClass().getName());
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
		// 防止并发
		synchronized (this) {
			//Collections.synchronizedMap(new HashMap<String, List<String>>());
			this.libraryMapping = new ConcurrentHashMap<String, List<String>>();
			this.Hw = new Vector<String>();
			// Debugger必须在主线程中创建
			// 因为创建了 Handler
			ServiceContainer.getDebugger();
		}
	}

	@Override
	public boolean tp(String string) {
		return super.tp(string);
	}
	/**
	 * 暂时没啥用
	 */
	@Override
	public synchronized List<String> yS() {
		return this.Hw;			
	}

	@Override
	public synchronized Map<String, List<String>> getLibraryMapping() {
		synchronized (this.libraryMapping) {
			if (this.libraryMapping == null || ExecutorsService.isDebug) {
				Log.printlnStack();
				Log.println("this.libraryMapping: " + this.libraryMapping);			
			}
			if( ExecutorsService.isUiThread()){
				return new HashMap<String, List<String>>(this.libraryMapping);
			}
			return this.libraryMapping;			
		}
	}

	@Override
	public String SI(String str) {
        try {
            if (FileSystem.cb(str)) {
                return null;
            }
            while (!FileSystem.Sf(str)) {
                if (getProjectSupport(str) != null) {
                    return str;
                }
                str = FileSystem.getParent(str);
            }
        }
		catch (Throwable th) {

        }
		return null;
    }
	// 返回当前已的文件是否支持[Design]
	@Override
	public boolean J8() {
		if (this.pojectSupport == null) {
			return false;
		}
		String curOpenFile = ServiceContainer.getOpenFileService().u7();
		if (curOpenFile == null) {
			return false;
		}
		/**
		 * if (SI(curOpenFile) == null) {
		 *	return false;
		 * }
		 */
		if (!ServiceContainer.isTrainerMode() 
			|| ServiceContainer.ro().CU(curOpenFile)) {
			// 非常耗时的操作
			// getProjectSupport(SI).u7(u7)
			// 我认为就应该只是当前ProjectSupport进行判断
			return this.pojectSupport.u7(curOpenFile);
		}
		return false;
	}
	/*****************************************************************/
	/**
	 * 需要异步原因是 com.aide.ui.build.android.AndroidProjectBuildService.dx
	 * 会使用 yS().get(yS().size() -1);
	 * 在一个线程应该就不会了
	 * 不对 yS().size()如果为0必然get(-1)
	 * 所以问题不在并发
	 */
	@Override
	public void buildProject(boolean p) {
		// TODO: Implement this method
		super.buildProject(p);
	}
	/*****************************************************************/
	/**
	 * 防止 Hw 与 libraryMapping值被覆盖
	 */
	@Override
	public void Ws() {
		if (this.currentAppHome == null) {
			return;
		}
        try {
			saveCurrentAppHome(null);
			ServiceContainer.J0().aM();
			ServiceContainer.getNavigateService().Hw();
			ServiceContainer.getOpenFileService().Zo();
			// 替换原实现
			this.Hw.clear();
			this.libraryMapping.clear();

			ServiceContainer.getDebugger().v5();
			ServiceContainer.getMainActivity().q7();

			jJ();
        }
		catch (Throwable th) {
			th.printStackTrace();
        }
    }
	/*****************************************************************/
	public void sGAsync() {
		super.sG();
	}

	/**
	 * verifyResourcesDownload
	 */
	@Override
	public boolean sG() {
		executorsService.submit(new Runnable(){
				@Override
				public void run() {
					sGAsync();
				}
			});
		return false;
	}

	/*****************************************************************/
	protected void etAsync(List<String> list, boolean p) {
		if (this.pojectSupport != null) {
			this.pojectSupport.cn(list, p);
		}
	}
	@Override
	public void et(final List<String> list, final boolean p) {
		executorsService.submit(new Runnable(){
				@Override
				public void run() {
					etAsync(list, p);
				}
			});
	}
	/*****************************************************************/

	@Override
	public void openProject(final String string) {
		try {
			//*
			final ProgressDialog show = ProgressDialog.show(ServiceContainer.getMainActivity(), null, "打开项目中[请等待]...", true, false);
			Window window = show.getWindow();
			window.addFlags(128);
			window.clearFlags(2);

			// 打开完毕，再取消
			final Runnable dismissRunnable = new Runnable(){
				@Override
				public void run() {
					show.dismiss();
				}
			};
			//*/

			executorsService.submit(new Runnable(){
					@Override
					public void run() {
						openProjectAsync(string);
						ServiceContainer.aj(dismissRunnable);
					}
				});


		}
		catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public void openProjectAsync(String projectPath) {

		SharedPreferences sharedPreferences = ServiceContainer.getContext().getSharedPreferences("ProjectService", Context.MODE_PRIVATE);

		if (!ServiceContainer.isTrainerMode() 
			&& ServiceContainer.getMainActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
			if (projectPath != null) {
				//FireBaseLogEvent.tp("App init: From intent");
				saveCurrentAppHome(SI(projectPath));
			} else {
				String string = sharedPreferences.getString("CurrentAppHome", null);
				this.currentAppHome = string;

				if (string != null && getProjectSupport(string) == null) {
					// 没有支持此目录的项目支持器，置空
					this.currentAppHome = null;
				}
			}
		}

		this.pojectSupport = getProjectSupport(this.currentAppHome);

		init();

		if (this.pojectSupport != null) {
			ServiceContainer.getDebugger().P8(this.pojectSupport.yS(), true);
		}

		if (this.currentAppHome != null) {
			//FireBaseLogEvent.tp("App init: Opened existing project");
			et(null, false);
			sy("init");
		}
	}

	/*******************************************************************/
	protected void jJAsync() {
		super.jJ();
	}

	@Override
	protected void jJ() {
		// updateEngineSolution
		executorsService.submit(new Runnable(){
				@Override
				public void run() {
					synchronized (yS()) {
						jJAsync();
					}
				}
			});
	}

	public void makeEngineSolutionAsync() {
		// 不要以为使用了同步集合就万事大吉，
		// 同步集合只能保证本身的操作是同步的，
		// 但是它所属的代码块不是同步的话，
		// 多线程情况下也会出问题

		// 如果集合正在遍历，这时又有写入操作就会触发并发错误
		// 如上所述，并发集合仅是集合自己的操作是有锁
		// 但是集合的遍历器不是
		// 防止并发错误

		asyncUpdateEngineSolution(this.pojectSupport.makeEngineSolution());

	}

	/**
	 * 在主线程中设置EngineService
	 */
	private void asyncUpdateEngineSolution(final EngineSolution engineSolution) {
		if (ExecutorsService.isUiThread()) {
			// 就在子进程设置试试，应该可以
			EngineService engineService = ServiceContainer.getEngineService();
			engineService.er(engineSolution);
			engineService.ef();

			engineService.ei();
			return;
		}

		ServiceContainer.aj(new Runnable(){
				@Override
				public void run() {
					EngineService engineService = ServiceContainer.getEngineService();
					engineService.er(engineSolution);
					engineService.ef();

					engineService.ei();
				}
			});
	}
	/*****************************************************************/
	@Override
	protected void init() {
		//异步
		executorsService.submit(new Runnable(){
				@Override
				public void run() {
					initAsync();
				}
			});
	}

	protected void initAsync() {
		// 重置
		this.Hw.clear();
		this.libraryMapping.clear();

		if (this.currentAppHome != null) {
			this.pojectSupport.U2(this.currentAppHome, this.libraryMapping, this.Hw);
		}
	}


	/*****************************************************************/

	private void saveCurrentAppHome(String projectPath) {
		this.currentAppHome = null;
		SharedPreferences.Editor edit = ServiceContainer.getContext().getSharedPreferences("ProjectService", 0).edit();
		edit.putString("CurrentAppHome", projectPath);
		edit.commit();
	}

	private ProjectSupport getProjectSupport(String projectPath) {
		if (projectPath == null) {
			return null;
		}
		for (ProjectSupport projectSupport : ServiceContainer.getProjectSupports()) {
			if (projectSupport.isSupport(projectPath)) {
				return projectSupport;
			}
		}
		return null;
    }


	/*****************************************************************/
	@Override
	public void wc() {
		if (!ExecutorsService.isUiThread()) {
			wcAsync();
			return;
		}
		executorsService.submit(new Runnable(){
				@Override
				public void run() {
					wcAsync();
				}
			});
	}

	private void wcAsync() {
		if (this.currentAppHome != null 
			&& getProjectSupport(this.currentAppHome) == null) {
			Ws();
		}
		if (this.currentAppHome != null) {
			init();
		}
		jJ();
	}

	/*****************************************************************/

	public void reloadingProjectAsync() {
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
		executorsService.submit(new Runnable(){
				@Override
				public void run() {
					reloadingProjectAsync();
				}
			});
	}
}
