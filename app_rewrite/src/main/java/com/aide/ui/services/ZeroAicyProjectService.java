package com.aide.ui.services;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.Window;
import com.aide.engine.EngineSolution;
import com.aide.ui.MainActivity;
import com.aide.ui.ServiceContainer;
import com.aide.ui.project.AndroidProjectSupport;
import com.aide.ui.project.internal.GradleTools;
import com.aide.ui.util.ClassPath;
import com.aide.ui.util.FileSystem;
import io.github.zeroaicy.aide.ui.services.ThreadPoolService;
import io.github.zeroaicy.util.Log;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.Iterator;
import com.aide.ui.util.BuildGradle;
import android.text.TextUtils;
import java.util.Arrays;
import com.aide.common.AppLog;
import com.aide.ui.services.ProjectService.e;
import com.aide.ui.services.ProjectService.f;

public class ZeroAicyProjectService extends ProjectService{
	/**
	 * 尽量将所有代码都从主线程挪到 ProjectServiceThreadPoolService
	 * 但是，其中混合UI报错，以及切换至线程的操作[runOnUiThread]
	 * 导致并不完全迁移到ProjectServiceThreadPoolService
	 * 除非重写
	 */
	private static final String TAG = "ZeroAicyProjectService";

	// 使用ProjectService的实现类的类名作为线程池标记
	private static final ThreadPoolService executorsService = ThreadPoolService.getThreadPoolService(ZeroAicyProjectService.class.getName(), 1);

	/**
	 * 使用此线程池的有: AaptService 
	 */
	public static ThreadPoolService getProjectServiceThreadPoolService(){
		return ZeroAicyProjectService.executorsService;
	}

	public static ExecutorService getProjectServiceExecutorService(){
		return getProjectServiceThreadPoolService();
	}

	private static ProjectService singleton;
	public static ProjectService getSingleton(){
		if ( singleton == null ){
			singleton = new ZeroAicyProjectService();
			Log.d(TAG,  "替换ZeroAicyProjectService");
		}
		return singleton;
	}

	private Map<String, List<String>> libraryMappingCopy;
	public ZeroAicyProjectService(){
		super();

		// 防止并发
		synchronized ( this ){
			// Collections.synchronizedMap(new HashMap<String, List<String>>());
			// 项目路径 -> 所有maven依赖
			this.libraryMapping = //new HashMap<>();
				new ConcurrentHashMap<String, List<String>>();
			// mainAppWearApps 主项目和wear app项目
			this.Hw = new Vector<String>();

			// Debugger必须在主线程中创建
			// 因为创建了 Handler
			ServiceContainer.getDebugger();
		}
	}

	@Override
	public boolean J0(){
		return super.J0();
	}

	public List<ClassPath.Entry> getClassPathEntrys(){
		List<ClassPath.Entry> classPathEntrys = this.classPathEntrys;
		if ( classPathEntrys == null ){
			// 可以做一些额外处理
			//this.classPathEntrys = classPathEntrys = AndroidProjectSupport.wc(this.currentAppHome, null);
		}
		return classPathEntrys;
	}
	// is_add_lib filebrowserMenuAddLibrary
	// canAddLib
	@Override
	public boolean gn(String  filePath){

		// 没有打开项目
		if ( this.currentAppHome == null 
			|| this.pojectSupport == null ){
			return false;
		}
		// 对AndroidProjectSupport进行特殊处理
		if ( this.pojectSupport instanceof AndroidProjectSupport ){

			List<ClassPath.Entry> classPathEntrys = getClassPathEntrys();
			if ( filePath.toLowerCase().endsWith(".jar") 
				&& classPathEntrys != null 
				&& !containsLib(this.currentAppHome, filePath, classPathEntrys) ){
				return true;
			}
			if ( GradleTools.nw(filePath) 
				&& !currentAppHome.equals(filePath) 
				&& !this.getLibraryMapping().get(this.currentAppHome).contains(filePath) ){
				return true;
			}
		}
		return super.gn(filePath);
	}

	// 是否包含库
	// 用于移除依赖库，但Gradle项目无法移除
	// canRemoveLib
	@Override
	public boolean we(String filePath){
		// 没有打开项目
		if ( this.currentAppHome == null || this.pojectSupport == null ){
			return false;
		}
		// 对AndroidProjectSupport进行特殊处理
		if ( this.pojectSupport instanceof AndroidProjectSupport ){

			List<ClassPath.Entry> classPathEntrys = this.classPathEntrys;

			List<String> librarys = this.getLibraryMapping().get(this.currentAppHome);
			// 异步bug修复
			if ( librarys == null && librarys.contains(filePath) ){
				return true;
			}

			if ( classPathEntrys != null && containsLib(this.currentAppHome, filePath, classPathEntrys) ){
				return true;
			}
			return false;
		}

		return super.we(filePath);
	}

	private List<ClassPath.Entry> classPathEntrys;
	private static boolean containsLib(String currentAppHome, String filePath, List<ClassPath.Entry> classPathEntrys){
		for ( ClassPath.Entry entry : classPathEntrys ){
			if ( entry.isLibKind() 
				&& entry.VH(currentAppHome).equals(filePath) ){
				return true;
			}
		}
		return false;
	}


	@Override
	public boolean tp(String string){
		// 未初始化完毕
		if ( ! isInited() ){
			return false;
		}
		return super.tp(string);
	}
	/**
	 * 返回所有model路径
	 */
	@Override
	public List<String> P8(){
        try{
            ArrayList<String> arrayList = new ArrayList<>();
            HashSet<String> hashSet = new HashSet<String>(this.libraryMapping.keySet());
            hashSet.add(getCurrentAppHome());
			arrayList.addAll(hashSet);
            return arrayList;
        }
		catch (Error th){
            throw th;			
		}
		catch (Throwable th){
			throw new Error(th);
        }
    }

	@Override
	public synchronized Map<String, List<String>> getLibraryMapping(){
		if ( ThreadPoolService.isUiThread() ){
			if ( !isInited() && libraryMappingCopy == null ){
				return Collections.emptyMap();
			}
			// 返回已经初始化完成时，保存的副本[信息完整]
			return this.libraryMappingCopy;
		}
		return this.libraryMapping;
	}

	// 判断文件夹及父文件夹是否是项目
	@Override
	public String SI(String str){
        try{
            if ( FileSystem.cb(str) ){
                return null;
            }
            while ( !FileSystem.Sf(str) ){
                if ( getProjectSupport(str) != null ){
                    return str;
                }
                str = FileSystem.getParent(str);
            }
        }
		catch (Throwable th){}
		return null;
    }

	// is_open_project
	@Override
	public boolean Mz(String string){
		return super.Mz(string);
	}


	// 返回当前的文件是否支持[Design]
	@Override
	public boolean J8(){
		if ( this.pojectSupport == null ){
			return false;
		}
		String curOpenFile = ServiceContainer.getOpenFileService().u7();
		if ( curOpenFile == null ){
			return false;
		}
		/**
		 * if (SI(curOpenFile) == null) {
		 *	return false;
		 * }
		 */
		if ( !ServiceContainer.isTrainerMode() 
			|| ServiceContainer.ro().CU(curOpenFile) ){
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
	 * 初步猜测是因为buildProject被调用时，ProjectService再次被初始化
	 * 即刚刚this.Hw.clear();
	 */

	@Override
	public void buildProject(boolean p){
		setUnBuildProjected();
		super.buildProject(p);
		setBuildProjected();
	}


	/**
	 * 返回主项目路径及子项目路径 [签名服务会回调]
	 */
	// yS() -> getMainAppWearApps
	@Override
	public synchronized List<String> yS(){
		synchronized ( this.Hw ){
			if ( this.Hw.size() == 0 
				&& ! isBuildProjected() ){
				// 返回一个拉倒
				return Arrays.<String>asList(new String[]{this.currentAppHome});
			}
		}

		// project中所有model文件夹路径
		return this.Hw;			
	}
	/*****************************************************************/
	/**
	 * 防止 Hw 与 libraryMapping值被覆盖
	 */
	// Ws() -> closeProject
	@Override
	public void Ws(){

		if ( this.currentAppHome == null ){
			return;
		}
        try{
			// 未初始化
			this.setUnInited();

			saveCurrentAppHome(null);

			ServiceContainer.J0().aM();
			ServiceContainer.getNavigateService().Hw();

			// closeFile 有Ui操作
			ThreadPoolService.post(new Runnable(){
					@Override
					public void run(){
						// 关闭项目 关闭所有已打开文件
						ServiceContainer.getOpenFileService().Zo();
					}
				});
			this.Hw.clear();
			this.libraryMapping.clear();

			this.classPathEntrys = null;
			this.libraryMappingCopy = null;

			ServiceContainer.getDebugger().v5();
			ServiceContainer.getMainActivity().q7();

			jJ();
        }
		catch (Throwable th){
			th.printStackTrace();
        }
    }
	/*****************************************************************/
	public void sGAsync(){
		super.sG();
	}
	/**
	 * verifyResourcesDownload
	 */
	@Override
	public boolean sG(){
		executorsService.submit(new Runnable(){
				@Override
				public void run(){
					sGAsync();
				}
			});
		return false;
	}

	/*****************************************************************/
	protected void etAsync(List<String> list, boolean p){
		if ( this.pojectSupport != null ){
			this.pojectSupport.cn(list, p);
		}
	}

	@Override
	public void et(final List<String> list, final boolean p){
		executorsService.submit(new Runnable(){
				@Override
				public void run(){
					etAsync(list, p);
				}
			});
	}


	/*****************************************************************/

	@Override
	public void kQ(final String str, final boolean p){
		// 打开新项目
		setUnInited();
		super.kQ(str, p);

		//kQAsync(str, p);
	}

	private void kQAsync(final String str, final boolean z){

		try{
            if ( !ef(str) 
				|| str == null 
				|| str.equals(getCurrentAppHome()) ){
                return;
            }

            saveCurrentAppHome(str);

            //ye();
            ServiceContainer.J0().aM();
            ServiceContainer.getNavigateService().Hw();
            ServiceContainer.getOpenFileService().Zo();

            ServiceContainer.Zo().QX();

            ServiceContainer.getMavenService().resetDepPathMap();

            this.pojectSupport = getProjectSupport(str);

			// 
			//ServiceContainer.sy(ServiceContainer.getMainActivity(), "打开项目中[请等待]...", c, d);
			// 打开新项目
			setUnInited();

			MainActivity activity = ServiceContainer.getMainActivity();
			final ProgressDialog show = ProgressDialog.show(activity, null, "打开项目中[请等待]...", true, false);
			show.getWindow().addFlags(128);
			show.getWindow().clearFlags(2);
			// ProjectService.c::run() -> DW() -> init()
			init(); 

			final ProjectService.d d = new ProjectService.d(z);
			ServiceContainer.aj(new Runnable(){
					@Override
					public void run(){
						show.dismiss();
						d.run();
					}
				});
        }
		catch (Error th){
            throw th;			
		}
		catch (Throwable th){
			throw new Error(th);
        }
	}

	private String projectProperties = null;
	@Override
	public String ei(){
		if ( projectProperties != null ){
			return this.projectProperties;
		}
		executorsService.submit(new Runnable(){
				@Override
				public void run(){
					ZeroAicyProjectService.this.projectProperties = eiAsync();
				}
			});

		return "异步加载中，请等待初始化...";
	}

	public String eiAsync(){
		return super.ei();
	}
	/*****************************************************************/

	@Override
	public void openProject(final String string){
		try{
			// 新打开项目，需要初始化
			this.setUnInited();

			//*
			final ProgressDialog show = ProgressDialog.show(ServiceContainer.getMainActivity(), null, "打开项目中[请等待]...", true, false);
			Window window = show.getWindow();
			window.addFlags(128);
			window.clearFlags(2);

			/* 打开完毕，再取消
			 final Runnable dismissRunnable = new Runnable(){
			 @Override
			 public void run() {
			 show.dismiss();
			 }
			 };
			 //*/

			executorsService.submit(new Runnable(){
					@Override
					public void run(){
						long time = System.currentTimeMillis();
						// 打开项目
						openProjectAsync(string);

						time = System.currentTimeMillis() - time;

						Log.d("openProject", "耗时 " + time + "毫秒");

						// dismiss可以在任何线程
						// show.dismiss();
						// ServiceContainer.aj(dismissRunnable);

						// 延迟并轮询 dismiss dialog
						Runnable pollDismissDialog = new Runnable(){
							@Override
							public void run(){
								if ( executorsService.isEmptyTask() ){
									// 取消轮询
									ThreadPoolService.removeCallbacksOfUi(this);
									try{				
										//可能结束时 activity被关闭，导致异常
										show.dismiss();										
									}
									catch (Throwable e){
									}
								}else{
									ThreadPoolService.removeCallbacksOfUi(this);						
									// 轮询速度1秒
									ThreadPoolService.postDelayedOfUi(this, 600);
								}
							}
						};
						// 开始轮询
						ThreadPoolService.postDelayedOfUi(pollDismissDialog, time);

					}
				});

		}
		catch (Throwable e){
			e.printStackTrace();
		}
	}

	public void openProjectAsync(String projectPath){

		// 置空inited
		setUnInited();

		SharedPreferences sharedPreferences = ServiceContainer.getContext().getSharedPreferences("ProjectService", Context.MODE_PRIVATE);

		if ( !ServiceContainer.isTrainerMode() 
			&& ServiceContainer.getMainActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) ){
			if ( projectPath != null ){
				saveCurrentAppHome(SI(projectPath));
			}else{
				String string = sharedPreferences.getString("CurrentAppHome", null);
				this.currentAppHome = string;

				if ( string != null && getProjectSupport(string) == null ){
					// 没有支持此目录的项目支持器，置空
					this.currentAppHome = null;
				}
			}
		}

		this.pojectSupport = getProjectSupport(this.currentAppHome);
		this.init();

		if ( this.pojectSupport != null ){
			ServiceContainer.getDebugger().P8(this.pojectSupport.yS(), true);
		}

		if ( this.currentAppHome != null ){
			// PojectSupport::cn()
			et(null, false);

			//sy("init");
		}
	}

	/*******************************************************************/
	protected void jJAsync(){
		super.jJ();
	}

	@Override
	protected void jJ(){
		// updateEngineSolution
		executorsService.submit(new Runnable(){
				@Override
				public void run(){
					synchronized ( yS() ){
						jJAsync();
					}
				}
			});
	}

	public void makeEngineSolutionAsync(){
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
	private void asyncUpdateEngineSolution(final EngineSolution engineSolution){
		if ( ThreadPoolService.isUiThread() ){
			// 就在子进程设置试试，应该可以
			EngineService engineService = ServiceContainer.getEngineService();
			engineService.er(engineSolution);
			engineService.ef();

			engineService.ei();
			return;
		}

		ServiceContainer.aj(new Runnable(){
				@Override
				public void run(){
					EngineService engineService = ServiceContainer.getEngineService();
					engineService.er(engineSolution);
					engineService.ef();

					engineService.ei();
				}
			});
	}
	/*****************************************************************/
	private final AtomicBoolean inited = new AtomicBoolean(false);

	public void setInited(){
		this.inited.set(true);
	}
	public void setUnInited(){

		this.inited.set(false);
	}
	public boolean isInited(){
		return this.inited.get();
	}

	/****************************buildProject*************************************/
	private final AtomicBoolean buildProjected = new AtomicBoolean(false);

	public void setBuildProjected(){
		this.buildProjected.set(true);
	}
	public void setUnBuildProjected(){
		this.buildProjected.set(false);
	}
	public boolean isBuildProjected(){
		return this.buildProjected.get();
	}

	/*****************************************************************/
	/**
	 * 异步重载
	 */
	public void reloadingProjectAsync(){
		// 刷新, 需要初始化
		setUnInited();

		if ( this.currentAppHome == null 
			|| getProjectSupport(this.currentAppHome) == null ){
			// 没有项目支持器支持
			// closeProject();
			Ws();

		}
		ServiceContainer.getDebugger().ef();

		
		ServiceContainer.aj(new Runnable(){
				@Override
				public void run(){
					MainActivity mainActivity = ServiceContainer.getMainActivity();
					// ProjectService$e -> ProjectService.DW() -> init 
					ProjectService.e e = new ProjectService.e();
					
					ProjectService.f f = new ProjectService.f();
					String title = "Reloading project...";
					ServiceContainer.sy(mainActivity, title, e, f);
				}
			});
	}
	@Override
	public void reloadingProject(){
		executorsService.submit(new Runnable(){
				@Override
				public void run(){
					reloadingProjectAsync();
				}
			});
	}
	/*****************************************************************/

	@Override
	protected void init(){
		//异步
		executorsService.submit(new Runnable(){
				@Override
				public void run(){
					initAsync();
				}
			});
	}

	protected void initAsync(){
		if ( isInited() ){
			// 老是重复初始化
			AppLog.d("项目服务, 已初始化: ");
			return;
		}

		this.Hw.clear();
		// 必须currentAppHome
		this.libraryMapping.clear();

		this.classPathEntrys = null;

		if ( this.currentAppHome != null ){
			// 填充this.libraryMapping[修改this.libraryMapping中]
			this.pojectSupport.U2(this.currentAppHome, this.libraryMapping, this.Hw);
		}
		// 已完成初始化，记录当时libraryMapping
		this.libraryMappingCopy = new HashMap<String, List<String>>(this.libraryMapping);
		if ( this.currentAppHome != null && this.pojectSupport instanceof AndroidProjectSupport ){
			this.classPathEntrys = AndroidProjectSupport.wc(this.currentAppHome, null);
		}

		this.projectProperties = eiAsync();
		// 初始化完成
		setInited();
	}


	/*****************************************************************/

	private void saveCurrentAppHome(String projectPath){
		this.currentAppHome = null;
		SharedPreferences.Editor edit = ServiceContainer.getContext().getSharedPreferences("ProjectService", 0).edit();
		edit.putString("CurrentAppHome", projectPath);
		edit.commit();
	}

	private ProjectSupport getProjectSupport(String projectPath){
		if ( projectPath == null ){
			return null;
		}
		for ( ProjectSupport projectSupport : ServiceContainer.getProjectSupports() ){
			if ( projectSupport.isSupport(projectPath) ){
				return projectSupport;
			}
		}
		return null;
    }


	/*****************************************************************/
	@Override
	public void wc(){
		if ( !ThreadPoolService.isUiThread() ){
			wcAsync();
			return;
		}

		executorsService.submit(new Runnable(){
				@Override
				public void run(){
					wcAsync();
				}
			});
	}

	// 频繁调用，且感觉无意义(明明已经inited了)，但还是总调用init() 与 jJ()
	private void wcAsync(){

		if ( this.currentAppHome != null 
			&& getProjectSupport(this.currentAppHome) == null ){
			// 关闭不支持的且已打开的项目
			Ws();
		}

		if ( this.currentAppHome != null ){
			// 只有在未初始化完成才执行
			// 且都在项目服务线程池中运行
			// 不会并发
			init();
		}
		// 刷新远程端[代码分析器]
		jJ();

	}

	/*****************************************************************/


	// 好像是 判断当前目录是否在项目目录中
	@Override
	public boolean Sf(String filePath){
		if ( TextUtils.isEmpty(filePath) ){
			return false;
		}
		if ( this.pojectSupport instanceof AndroidProjectSupport ){
			return isAndroidProjectInwhat(filePath);
		}
		return super.Sf(filePath);
	}

	public boolean isAndroidProjectInwhat(String filePath){
		String currentAppHome = getCurrentAppHome();
		if ( currentAppHome == null ){
			return false;
		}
		if ( filePath.startsWith(currentAppHome) ){
			return true;
		}

		Map<String, List<String>> libraryMapping = ServiceContainer.getProjectService().getLibraryMapping();
		for ( String key : libraryMapping.keySet() ){
			if ( filePath.startsWith(key) ){
				return true;
			}
		}
		return false;
    }
	/*****************************************************************/

}
