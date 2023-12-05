package io.github.zeroaicy.aide.services;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import com.aide.ui.build.packagingservice.ExternalPackagingService;
import io.github.zeroaicy.aide.preference.ZeroAicySetting;
import io.github.zeroaicy.util.Log;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public abstract class ExternalPackagingServiceWrapper extends ExternalPackagingService {

	public static void fillD8Args(List<String> argsList, boolean file_per_class_file, boolean intermediate, String androidSDK, String[] allLibraries, String outPath) {
		argsList.add("--min-api");
		argsList.add("21");

		if (file_per_class_file) {
			argsList.add("--file-per-class-file");
		}
		if (intermediate) {
			argsList.add("--intermediate");
		}
		if (!TextUtils.isEmpty(androidSDK)) {
			argsList.add("--lib");
			argsList.add(androidSDK);
		}

		if (allLibraries != null) {
			for (String librarie : allLibraries) {
				argsList.add("--classpath");
				argsList.add(librarie);
			}
		}
		argsList.add("--output");
		argsList.add(outPath);
	}

	//隔离层
	public abstract class DxThreadPoolExecutor extends ExternalPackagingService.ExternalPackagingServiceWorker {

		@Override
		public void J0() {
			Log.d("DxThreadPoolExecutor", "called J0()");
			super.J0();
		}

		@Override
		public void Zo(String str, String[] strArr, String[] strArr2, String[] strArr3, String str2, String str3, String str4, String[] strArr4, String str5, String str6, String str7, String str8, String str9, boolean z, boolean z2, boolean z3) {
			if (this.Hw == null) {
				this.Hw = new ArrayList<>();
			}
			Log.d("DxThreadPoolExecutor", "called Zo()");
			//添加打包任务
			this.Hw.add(getDxFromD8(str, strArr, strArr2, strArr3, str2, str3, str4, strArr4, str5, str6, str7, str8, str9, z, z2, z3));
		}

		public abstract DxFromD8 getDxFromD8(String str, String[] strArr, String[] strArr2, String[] strArr3, String str2, String str3, String str4, String[] strArr4, String str5, String str6, String str7, String str8, String str9, boolean z, boolean z2, boolean z3);

		public abstract class DxFromD8 extends ExternalPackagingService.ExternalPackagingServiceWorker.Task {
			public class FilterELFFile extends DxFromD8.a {
				//过滤zipEntryFileName
				@Override
				public boolean j6(String zipEntryFileName) {
					//libs都添加
					return true;
				}
			}
			public DxFromD8(String j6, String[] DW, String[] FH, String[] Hw, String v5, String Zo, String VH, String[] gn, String u7, String tp, String EQ, String we, String J0, boolean J8, boolean Ws, boolean QX) {
				super(j6, DW, FH, Hw, v5, Zo, VH, gn, u7, tp, EQ, we, J0, J8, Ws, QX);
			}
			//所有源码路径
			protected String[] getSourcePaths() {
				return this.FH;
			}
			//所有项目
			protected String[] getProjectPaths() {
				return this.gn;
			}
			//所有Jar依赖
			protected String[] getJarDependencys() {
				return this.Hw;
			}
			//主项目的[build/bin]目录
			protected String getOutDir() {
				return this.v5;
			}

			//打包，唯一外部调用
			@Override
			public final void Mr() {
				//屏蔽
				try {
					//super.Mr();
					packaging();
				} catch (Throwable e) {
					Log.d("打包错误", "堆栈 -> ", e);
					throw new RuntimeException(e);
				}
			}

			@Override
			public final List<String> U2() {
				//不会被调用，因为super.Mr();未执行
				//返回添加的dex到apk或zip
				/*try {
				 return runD8Dexing();
				 } catch (Throwable e) {
				 Log.d("打包错误", "", e);
				 }*/
				return null;
			}

			//从jar中读取class文件并dx为dex
			@Override
			public void tp(String string) {
				//此方法被super.U2调用

			}
			@Override
			public void u7(String str, File file) {
				//此方法被tp调用
				throw new RuntimeException("u7() Not Supported");
			}

			//jar文件路径 -> jardex路径
			@Override
			public final String aM(String jarPath) {
				return getDexingJarPath(jarPath);
			}

			//根据class文件路径查找该类dex路径
			@Override
			public final String XL(String classPath) {
				return getDexingClassPath(classPath);
			}
			//获取dexing jar 输出路径(是文件夹--jar.dex父目录)
			//默认jardex路径
			@Override
			public final String QX(String jarFilePath) {
				return getDexingJarOutPath(jarFilePath);
			}

			//Java工程打包
			@Override
			public final void rN(List<String> dexList) {
				packagingJavaProject(dexList);
			}

			//Android工程打包
			@Override
			public void v5(List<String> dexList) {
				super.v5(dexList);
			}

			//Android工程打包
			protected final void packagingAndroidProject(List<String> dexList) {
				this.v5(dexList);
			}

			@Override
			public void FH(String zipEntryFileName, File parentFile, File file, ExternalPackagingService.f apkZipOutputStream, ExternalPackagingService.b filter, boolean z) {
				if (!addFileToZipOut(zipEntryFileName, parentFile, file, apkZipOutputStream, filter, z)) {
					//不是d8打包的zip(是dex)
					super.FH(zipEntryFileName, parentFile, file, apkZipOutputStream, filter, z);
				}
			}

			@Override
			public void DW(String zipFilePath, ExternalPackagingService.b zipEntryFilter, ExternalPackagingService.f zipOutputStream, boolean followOriginalZipEntryMethod) {
				super.DW(zipFilePath, zipEntryFilter, zipOutputStream, followOriginalZipEntryMethod);
			}

			@Override
			public void Zo(ZipInputStream zipInputStream, ExternalPackagingService.f zipOutputStream, ExternalPackagingService.b zipEntryFilter, boolean followOriginalZipEntryMethod) {
				try {
					while (true) {
						ZipEntry nextEntry = zipInputStream.getNextEntry();
						if (nextEntry == null) {
							return;
						}
						Hw(nextEntry, zipInputStream, zipOutputStream, zipEntryFilter, followOriginalZipEntryMethod);
					}
				} catch (IOException e) {
					log_e(e);
				}
			}

			@Override
			public void Hw(ZipEntry zipEntry, ZipInputStream zipInputStream, ExternalPackagingService.f zipOutputStream, ExternalPackagingService.b zipEntryFilter, boolean followOriginalZipEntryMethod) {
				super.Hw(zipEntry, zipInputStream, zipOutputStream, zipEntryFilter, followOriginalZipEntryMethod);
			}


			/**
			 * 抽象实现兼容层
			 **/
			//添加文件到输出Zip(apk或zip);
			public abstract boolean addFileToZipOut(String zipEntryFileName, File parentFile, File file, ExternalPackagingService.f apkZipOutputStream, ExternalPackagingService.b filter, boolean z);

			//dx Jar文件输出目录
			public abstract String getDexingJarOutPath(String jarPath);
			//jar的dex缓存一般为(jar.dex.zip)
			public abstract String getDexingJarPath(String jarPath);
			//dx class文件输出缓存文件
			public abstract String getDexingClassPath(String classFilePath);

			//打包
			public abstract void packaging()throws Throwable;
			//Java工程打包
			public abstract void packagingJavaProject(List<String> dexList);

			//使用d8 dx class/jar文件
			public abstract List<String> runD8Dexing() throws Throwable;
		}
	}

	public static void printlnDebug(String log) {
		Log.d("AIDE", log);
	}
	public static void log_e(Throwable e) {
		Log.e("AIDE", e.toString(), e);
	}
	public static long nowTime() {
		long startTime = System.currentTimeMillis();
		return startTime;
	}

	public ExternalPackagingServiceWrapper() {
		super();
	}

	public abstract DxThreadPoolExecutor getDxThreadPoolExecutor();

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
	}

	protected SharedPreferences defaultSharedPreferences;

	public SharedPreferences getSp() {
		return this.defaultSharedPreferences;
	}
	public String getUserAndroidJar() {

		String user_androidjar = defaultSharedPreferences.getString("user_androidjar", getNoBackupDir() + "/.aide/android.jar");

		return user_androidjar;
	}
	@Override
	public void onCreate() {
		defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());

		try {
			if (ZeroAicySetting.isEnableD8()) {
				DxThreadPoolExecutor dxThreadPoolExecutor = getDxThreadPoolExecutor();
				if (dxThreadPoolExecutor != null) {
					this.WB.we();//释放
					this.WB = dxThreadPoolExecutor;
				}
			}
		} catch (Throwable e) {
			Log.d("ExternalPackagingServiceWrapper", "替换打包实现失败", e);
		}

		super.onCreate();
	}


	public final File getNoBackupDir() {
		return getNoBackupFilesDir(this.getApplicationContext());
	}
    public static final File getNoBackupFilesDir(Context context) {
        if (Build.VERSION.SDK_INT >= 21) {
            return context.getNoBackupFilesDir();
        }
        return getNoBackupFilesDir(new File(context.getApplicationInfo().dataDir, "no_backup"));
    }

	private static synchronized File getNoBackupFilesDir(File file) {
        synchronized (ExternalPackagingServiceWrapper.class) {
            if (file.exists() || file.mkdirs()) {
                return file;
            }
            if (file.exists()) {
                return file;
            }
            return null;
        }
    }
}
