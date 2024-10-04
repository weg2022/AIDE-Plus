package io.github.zeroaicy.aide.services;

import android.text.TextUtils;
import com.aide.ui.build.packagingservice.ExternalPackagingService;
import io.github.zeroaicy.aide.preference.ZeroAicySetting;
import io.github.zeroaicy.aide.utils.AndroidManifestParser;
import io.github.zeroaicy.aide.utils.ZeroAicyBuildGradle;
import io.github.zeroaicy.util.Log;
import io.github.zeroaicy.util.MD5Util;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.Set;
import com.aide.common.AppLog;

public abstract class PackagingWorkerWrapper extends ExternalPackagingService.ExternalPackagingServiceWorker {

	ExternalPackagingService externalPackagingService;
	private String noBackupFilesDirPath;

	public PackagingWorkerWrapper(ExternalPackagingService externalPackagingService) {
		//父类没有使用封闭类[ExternalPackagingService]
		null.super();
		this.externalPackagingService = externalPackagingService;
	}

	public static String getFileName(String path) {
		int fileNameStartIndex = path.lastIndexOf('/');
		if (fileNameStartIndex >= 0) {
			return path.substring(fileNameStartIndex);
		}
		return path;
	}
	
	private String libEnsureCapacityPath;
	public String getLibEnsureCapacityPathPath() {
		if (this.libEnsureCapacityPath == null) {
			this.libEnsureCapacityPath = externalPackagingService.getApplicationInfo().nativeLibraryDir + "/libEnsureCapacity.so";
		}
		return this.libEnsureCapacityPath;
	}
	
	public String getUserAndroidJar() {
		String defaultAndroidJar = getNoBackupFilesDirPath() + "/.aide/android.jar";
		String userAndroidJar = ZeroAicySetting.getDefaultSpString("user_androidjar", noBackupFilesDirPath);
		
		if(TextUtils.isEmpty( userAndroidJar ) ){
			return defaultAndroidJar;
		}
		return userAndroidJar;
	}

	/**
	 * no_backup路径
	 */
	public String getNoBackupFilesDirPath() {
		if (noBackupFilesDirPath == null) {
			noBackupFilesDirPath = externalPackagingService.getNoBackupFilesDir().getAbsolutePath();
		}
		return this.noBackupFilesDirPath;
	}

	@Override
	public final void Zo(String str, String[] strArr, String[] strArr2, String[] strArr3, String str2, String str3, String str4, String[] strArr4, String str5, String str6, String str7, String str8, String str9, boolean z, boolean z2, boolean z3) {
		if (this.Hw == null) {
			this.Hw = new ArrayList<>();
		}
		//添加打包任务
		this.Hw.add(getTaskWrapper(str, strArr, strArr2, strArr3, str2, str3, str4, strArr4, str5, str6, str7, str8, str9, z, z2, z3));
	}

	public abstract TaskWrapper getTaskWrapper(String str, String[] strArr, String[] strArr2, String[] strArr3, String str2, String str3, String str4, String[] strArr4, String str5, String str6, String str7, String str8, String str9, boolean z, boolean z2, boolean z3);

	public abstract class TaskWrapper extends Task {
		//class转dex默认输出目录
		private final String defaultClassDexCacheDirPath;

		private final String defaultIntermediatesDirPath;

		/**
		 * android:extractNativeLibs="false"必须无压缩
		 */
		private boolean androidFxtractNativeLibs = true;
		// 低于21时d8无法dexing AIDE产生的class文件
		private int minSdk = 21;

		/*************************参数*********************************/
		// 主项目.class file缓存目录
		private final String mainClassCacheDir;
		//所有class文件，顶级父目录
		private String[] allClassFileRootDirs;

		//构建时缓存文件的父目录
		private String outDirPath;
		// 若为gradle项目才有
		private ZeroAicyBuildGradle zeroAicyBuildGradle;

		//构建文件地址[类型分为apk和zip]
		private String outFilePath;
		// .ap_文件(aapt2 link产物)
		private String aaptResourcePath;


		//项目的jardex默认目录
		private String defaultJarDexDirPath;

		//所有依赖库
		private String[] dependencyLibs;
		// 使用替代
		private ScopeTypeQuerier scopeTypeQuerier;

		//源码目录
		private String[] sourceDirs;
		//源码目录
		private String[] nativeLibDirs;
		//构建刷新
		private final boolean buildRefresh;

		private final String zipalignLibPath;

		private final String signaturePath;

		private final String signaturePassword;

		private final String signatureAlias;

		private final String signatureAliasPassword;

		private String mergerCachePath;



		public int getMinSdk() {
			return minSdk;
		}

		public String getZipalignLibPath() {
			return this.zipalignLibPath;
		}

		public final boolean isNotDebugFormAIDE;

		public TaskWrapper(String mainClassCacheDir, String[] classFileRootDirs, String[] sourceDirs, String[] dependencyLibs, String outDirPath, String Zo, String aaptResourcePath, String[] nativeLibDirs, String outFilePath, String signaturePath, String signaturePassword, String signatureAlias, String signatureAliasPassword, boolean buildRefresh, boolean Ws, boolean QX) {
			super(mainClassCacheDir, classFileRootDirs, sourceDirs, dependencyLibs, outDirPath, Zo, aaptResourcePath, nativeLibDirs, outFilePath, signaturePath, signaturePassword, signatureAlias, signatureAliasPassword, buildRefresh, Ws, QX);

			this.mainClassCacheDir = mainClassCacheDir;

			File mainProjectClassCacheDirFile = new File(this.mainClassCacheDir);
			String classCacheDirFileName = mainProjectClassCacheDirFile.getName();

			File parentFile = mainProjectClassCacheDirFile.getParentFile();
			this.defaultIntermediatesDirPath = new File(parentFile, "intermediates").getAbsolutePath();

			this.defaultClassDexCacheDirPath = this.getIntermediatesChildDirPath(classCacheDirFileName);
			this.mergerCachePath = getIntermediatesChildDirPath("dex");

			this.allClassFileRootDirs = classFileRootDirs;
			this.sourceDirs = sourceDirs;
			this.dependencyLibs = dependencyLibs;

			//  build/bin | bin/[debug | release]/dex
			this.outDirPath = outDirPath;
			this.zeroAicyBuildGradle = getZeroAicyBuildGradle();
			
			//Zo
			this.defaultJarDexDirPath = this.getIntermediatesChildDirPath("jardex");

			this.aaptResourcePath = aaptResourcePath;
			this.nativeLibDirs = nativeLibDirs;
			this.outFilePath = outFilePath;

			this.signaturePath = signaturePath;
			this.signaturePassword = signaturePassword;
			this.signatureAlias = signatureAlias;
			this.signatureAliasPassword = signatureAliasPassword;

			this.buildRefresh = buildRefresh;

			this.minSdk = this.getProjectMinSdk();
			this.zipalignLibPath = externalPackagingService.getApplicationInfo().nativeLibraryDir + "/libzipalign.so";


			// 安卓且输出是classesdebug 也没debug-aide
			this.isNotDebugFormAIDE = isAndroidProject() && !classCacheDirFileName.endsWith("debug");
		}

		private int getProjectMinSdk() {
			final int defaultProjectMinSdk = 21;
			final int projectMinSdk;

			if (isAndroidProject()) {
				AndroidManifestParser androidManifestParser = getAndroidManifestParser();
				if (androidManifestParser == null) {
					projectMinSdk = defaultProjectMinSdk;
				} else {
					this.androidFxtractNativeLibs = androidManifestParser.getExtractNativeLibs();

					final int androidProject ;
					try {
						String minSdkVersion = androidManifestParser.getMinSdkVersion();
						androidProject = Integer.parseInt(minSdkVersion);
					}
					catch (NumberFormatException e) {
						androidProject = defaultProjectMinSdk;
					}
					projectMinSdk = androidProject;
				}
			} else {
				//Java项目为当前设备
				projectMinSdk = ZeroAicySetting.getJavaProjectMinSdkLevel();
			}
			return projectMinSdk;
		}

		/**
		 * 在outDirPath赋值后
		 */
		public ZeroAicyBuildGradle getZeroAicyBuildGradle() {
			if (zeroAicyBuildGradle != null) {
				return zeroAicyBuildGradle;
			}
			ZeroAicyBuildGradle singleton = ZeroAicyBuildGradle.getSingleton();

			String buildOutDirPath = this.getBuildOutDirPath();

			// 不是gradle项目
			String suffix = "/build/bin";

			if (!buildOutDirPath.endsWith(suffix)) {
				this.zeroAicyBuildGradle = singleton;
				return singleton;
			}

			File buildGradleFile = new File(buildOutDirPath.substring(0, buildOutDirPath.length() - suffix.length()), "build.gradle");
			if (!buildGradleFile.exists()) {
				this.zeroAicyBuildGradle = singleton;
				return singleton;				
			}
			return this.zeroAicyBuildGradle = singleton.getConfiguration(buildGradleFile.getAbsolutePath());
		}

		private AndroidManifestParser getAndroidManifestParser() {
			String buildOutDirPath = getBuildOutDirPath();
			AndroidManifestParser androidManifestParser = null;  
			for (String androidManifestAbsolutePath : new String[]{
				"injected/AndroidManifest.xml",
				"merged/AndroidManifest.xml",
				"../AndroidManifest.xml"}) {
				File androidManifestXmlFile = new File(buildOutDirPath, androidManifestAbsolutePath);
				if (androidManifestXmlFile.exists()) {
					androidManifestParser = AndroidManifestParser.get(androidManifestXmlFile);
					break;
				}
			}
			return androidManifestParser;
		}

		public boolean isAndroidProject() {
			return getOutFilePath().endsWith(".apk");
		}

		// intermediates目录地址
		public String getDefaultIntermediatesDirPath() {
			return defaultIntermediatesDirPath;
		}
		
		public String getMergerCacheDirPath() {
			return this.mergerCachePath;
		}

		//返回 intermediates子文件夹
		public String getIntermediatesChildDirPath(String childDirName) {
			File childFile = new File(getDefaultIntermediatesDirPath(), childDirName);
			if (!childFile.exists() && !childFile.mkdirs()) {
				AppLog.e("Could not create dir: " + childFile);
			}
			return childFile.getAbsolutePath();
		}

		@Override
		public final void Mr() {
			try {
				packaging();
			}
			catch (Throwable e) {
				// 将错误信息保存到日志中
				AppLog.e("packaging()", e);
				
				if (e instanceof Error) {
					throw (Error)e;
				}
				if (e instanceof RuntimeException) {
					throw (RuntimeException)e;
				}
				throw new Error(e);
			}
		}
		/*************************抽象层，屏蔽所有父类方法*********************************/
		/**
		 * 打包
		 */
		public abstract void packaging() throws Throwable;
		public abstract void packagingJavaProject(List<String> dexZipPathList) throws Throwable;
		public abstract void packagingAndroidProject(List<String> dexZipPathList) throws Throwable;
		/**
		 * d8 class jar 并返回classesDexZip[d8输出zip]
		 */
		public abstract List<String> getClassesDexZipList() throws Throwable;


		/******************************************/
		/**
		 * 返回android:extractNativeLibs="false"的值
		 */
		public boolean getAndroidFxtractNativeLibs() {
			return this.androidFxtractNativeLibs;
		}


		/**********共用层，共用一些相同的代码逻辑***********************************/

		
		/**
		 * 是否启用混淆
		 */
		public boolean isMinify() {
			// 不是debug-aide变体 无法简单区分 debug 与 release
			// 可以从用 sp的ProjectService 但是 sp不能跨进程
			// 还是算了
			ZeroAicyBuildGradle zeroAicyBuildGradle = getZeroAicyBuildGradle();
			
			return !zeroAicyBuildGradle.isSingleton()
				&& this.isNotDebugFormAIDE 
				&& zeroAicyBuildGradle.isMinifyEnabled();
		}
		/**
		 * 根据isBuildRefresh是否增量
		 */
		public void fillClassFileCache(String classCacheRootDirPath, List<String> incrementalClassFiles, Set<String> classFileSet) {
			fillClassFileCacheMap(classCacheRootDirPath, new File(classCacheRootDirPath), incrementalClassFiles, classFileSet, this.isBuildRefresh());
		}
		/**
		 * 默认不增量
		 */
		public void fillAllClassFileCache(String classCacheRootDirPath, List<String> incrementalClassFiles, Set<String> classFileSet) {
			fillClassFileCacheMap(classCacheRootDirPath, new File(classCacheRootDirPath), incrementalClassFiles, classFileSet, true);
		}

		/**
		 * 递归填充classCacheRootDirPath目录下的所有class文件
		 */
		public void fillClassFileCacheMap(String classCacheRootDirPath, File dirFile, List<String> incrementalClassFiles, Set<String> classFileSet, boolean isBuildRefresh) {
			File[] listFiles = dirFile.listFiles();
			if (listFiles == null) {
				return;
			}

			for (File classFile : listFiles) {
				if (classFile.isDirectory()) {
					fillClassFileCacheMap(classCacheRootDirPath, classFile, incrementalClassFiles, classFileSet, isBuildRefresh);
					continue;
				}

				if (classFile.isFile()) {
					String classFilePath = classFile.getPath();
					if (!getFileName(classFilePath).toLowerCase().endsWith(".class")) {
						continue;
					}

					String classFileSubPath = classFilePath.substring(classCacheRootDirPath.length());
					if (classFileSet.contains(classFileSubPath)) {
						//AppLog.DW("忽略重复 .class 文件 " + classFilePath);
						continue;
					}
					classFileSet.add(classFileSubPath);

					if (isBuildRefresh) {
						//构建刷新，直接添加
						incrementalClassFiles.add(classFilePath);
						continue;
					}

					//判断是否更新
					String classDexFileCache = getClassDexFileCache(classFileSubPath);

					File dexFile = new File(classDexFileCache);
					if (classFile.lastModified() > dexFile.lastModified()) {
						//需要重新dexing的class
						incrementalClassFiles.add(classFilePath);
					}
				}
			}
		}
		/**
		 * 签名路径
		 */
		public String getSignaturePath() {
			return this.signaturePath;
		}
		//签名密码
		public String getSignaturePassword() {
			return signaturePassword;
		}
		//别名
		public String getSignatureAlias() {
			return signatureAlias;
		}
		//别名密码
		public String getSignatureAliasPassword() {
			return signatureAliasPassword;
		}

		/**
		 * 构建刷新
		 */
		public boolean isBuildRefresh() {
			return this.buildRefresh;
		}

		public String[] getAllClassFileRootDirs() {
			return this.allClassFileRootDirs;
		}
		/**
		 * 返回主项目class文件缓存路径，即主项目class文件的输出目录
		 */
		public String getMainClassCacheDir() {
			return this.mainClassCacheDir;
		}
		/**
		 * 返回默认dex文件缓存路径，[从class文件dexing的]
		 */
		public String getDefaultClassDexCacheDirPath() {
			return defaultClassDexCacheDirPath;
		}

		/**
		 * 返回class dexing后的dex缓存路径
		 */
		public String getClassDexFileCache(String classFileSubPath) {
			int endIndex = classFileSubPath.length() - ".class".length();
			return getDefaultClassDexCacheDirPath() 
				+ classFileSubPath.substring(0, endIndex) + ".dex";
		}
		/**
		 * 返回jar的dex.zip缓存路径
		 */
		public String getJarDexCachePath(String jarFilePath) {
			String jarDexCacheDirPath = getJarDexCacheDirPath(jarFilePath);
			// 

			File jarFile = new File(jarFilePath);
			String depFileName = jarFile.getName();
			if (jarFilePath.endsWith(".exploded.aar/classes.jar")) {
				String name = jarFile.getParentFile().getName();
				depFileName = name.substring(0, name.length() - ".exploded.aar".length()) + ".jar";
			} else {

			}
			String jarDexZipName = depFileName + ".dex.zip";

			//不会为null
			if (jarDexCacheDirPath == null) {
				jarDexCacheDirPath = getDefaultJarDexDirPath();
			}
			return jarDexCacheDirPath + "/" +  jarDexZipName;
		}

		/**
		 * 返回 getIntermediatesChildDirPath("jardex")目录下路径的md5码
		 * 如果是maven仓库则去掉仓库路径在进行计算
		 */
		public String getJarDexCacheDirPath(String dependencyLibPath) {
			String userM2repositories = ZeroAicySetting.getDefaultSpString("user_m2repositories", null);
			String defaultM2repositoriesDirPath = getNoBackupFilesDirPath() + "/.aide/maven";

			String key;
			/**
			 * 是否是在自定义仓库缓存文件夹中
			 */
			if (!TextUtils.isEmpty(userM2repositories) 
				&& dependencyLibPath.startsWith(userM2repositories)) {
				//指定了minsdk，会污染缓存，所以只能放项目目录
				key = dependencyLibPath.substring(userM2repositories.length());
			} else if (dependencyLibPath.startsWith(defaultM2repositoriesDirPath)) {
				key = dependencyLibPath.substring(defaultM2repositoriesDirPath.length());
			} else {
				key = dependencyLibPath;	
			}

			return getIntermediatesChildDirPath("jardex/" + MD5Util.stringMD5(key));
		}
		/**
		 * 全部依赖，不只是jar
		 */
		public String[] getAllDependencyLibs() {
			return this.dependencyLibs;
		}
		public ScopeTypeQuerier getScopeTypeQuerier() throws Throwable {
			if( this.scopeTypeQuerier == null ){
				this.scopeTypeQuerier = new ScopeTypeQuerier(this.dependencyLibs, getZeroAicyBuildGradle());

/*				try {
				}
				catch (Throwable e) {
					if (e instanceof Error) {
						throw (Error)e;
					}
					throw new Error("scopeTypeQuerier创建错误", e);
				}
*/
			}
			return this.scopeTypeQuerier;
		}

		public String getDefaultJarDexDirPath() {
			return this.defaultJarDexDirPath;
		}

		public String[] getSourceDirs() {
			return this.sourceDirs;
		}
		public String[] getNativeLibDirs() {
			return this.nativeLibDirs;
		}
		/**
		 * 输出
		 */
		public String getBuildOutDirPath() {
			return this.outDirPath;
		}

		/**
		 * 返回输出文件路径
		 * 可以在此处实现将apk生成项目缓存路径中
		 */
		public String getOutFilePath() {
			return this.outFilePath;
		}

		//resource.ap_文件
		public String getAAptResourceFilePath() {
			if (getZeroAicyBuildGradle().isShrinkResources()) {
				String resourcesAp_Dir = new File(this.aaptResourcePath).getParent();
				return new File(resourcesAp_Dir, "resources_optimize.ap_").getAbsolutePath();
			}
			return this.aaptResourcePath;
		}

		/**
		 * 显示构建总进度，只有一级进度
		 */
		public void showProgress(String progressTitle, int progress) {
			ExternalPackagingService.ExternalPackagingServiceWorker.v5(PackagingWorkerWrapper.this, progressTitle + "...", progress);
			//super.a8(progressTitle, progress);
		}
	}
}
