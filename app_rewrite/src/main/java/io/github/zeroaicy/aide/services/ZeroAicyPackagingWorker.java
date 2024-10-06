package io.github.zeroaicy.aide.services;

import android.content.SharedPreferences;
import android.text.TextUtils;
import com.aide.common.AppLog;
import com.aide.ui.build.packagingservice.ExternalPackagingService;
import com.android.apksig.ApkSigner;
import io.github.zeroaicy.aide.preference.ZeroAicySetting;
import io.github.zeroaicy.aide.utils.ZeroAicyBuildGradle;
import io.github.zeroaicy.aide.utils.jks.JksKeyStore;
import io.github.zeroaicy.util.FileUtil;
import io.github.zeroaicy.util.IOUtils;
import io.github.zeroaicy.util.Log;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.concurrent.atomic.AtomicInteger;
import io.github.zeroaicy.aide.services.DexingJarTask.Configuration;
import io.github.zeroaicy.aide.ui.services.ThreadPoolService;
import java.util.concurrent.Future;

public class ZeroAicyPackagingWorker extends PackagingWorkerWrapper{

	private static final String TAG = "Worker";

	private static void logDebug(String msg){
		Log.i(TAG, msg);
	}

	private static long nowTime(){
		return System.currentTimeMillis();
	}

	public ZeroAicyPackagingWorker(ExternalPackagingService service){
		super(service);
		//j$.util.Optional F;
	}
	@Override
	public PackagingWorkerWrapper.TaskWrapper 
	getTaskWrapper(String mainClassCacheDir, String[] classFileRootDirs, String[] sourceDirs, 
				   String[] dependencyLibs, String outDirPath, String jardexPath, 
				   String aAptResourcePath, String[] nativeLibDirs, String outFilePath, 
				   String signaturePath, String signaturePassword, String signatureAlias, 
				   String signatureAliasPassword, boolean buildRefresh, boolean Ws, boolean QX){

		/**
		 // Zo jardex路径
		 Log.d("Zo", Zo);

		 // Ws dex优化[optimze_dex]
		 Log.d("Ws", Ws);

		 // QX minsdk是否大于等于21
		 Log.d("QX", QX);
		 */

		return new ZeroAicyR8Task(mainClassCacheDir, classFileRootDirs, sourceDirs, 
								  dependencyLibs, outDirPath, jardexPath, aAptResourcePath, 
								  nativeLibDirs, outFilePath, signaturePath, signaturePassword, 
								  signatureAlias, signatureAliasPassword, buildRefresh, Ws, QX);
	}

	public class ZeroAicyR8Task extends TaskWrapper{
		private HashMap<String, String> environment = new HashMap<String, String>();

		public ZeroAicyR8Task(String mainClassCacheDir, String[] classFileRootDirs, String[] sourceDirs, String[] dependencyLibs, String outDirPath, String Zo, String aAptResourcePath, String[] nativeLibDirs, String outFilePath, String signaturePath, String signaturePassword, String signatureAlias, String signatureAliasPassword, boolean buildRefresh, boolean Ws, boolean QX){
			super(mainClassCacheDir, classFileRootDirs, sourceDirs, dependencyLibs, outDirPath, Zo, aAptResourcePath, nativeLibDirs, outFilePath, signaturePath, signaturePassword, signatureAlias, signatureAliasPassword, buildRefresh, Ws, QX);

			// 从文件夹添加原生库文件，
			this.nativeLibZipEntryTransformer = new ZipEntryTransformer.NativeLibFileTransformer(getAndroidFxtractNativeLibs());
			this.libgdxNativesTransformer = new ZipEntryTransformer.LibgdxNativesTransformer(getAndroidFxtractNativeLibs());
			if( ZeroAicySetting.isEnableEnsureCapacity()){
				this.environment.put("EnsureCapacity", getLibEnsureCapacityPathPath());				
			}
			// 初始化
			DexingJarTask.init(this.environment);
		}

		@Override
		public void packaging() throws Throwable{
			long now = nowTime();

			this.initBuildEnvironment();

			// 混淆
			if ( isAndroidProject() 
				&& isMinify() ){

				packagingAndroidMinify();
				logDebug("混淆打包共用时: " + (nowTime() - now) + "ms");
				return;
			}

			logDebug("开始dxing class");
			List<String> classesDexZipList = getClassesDexZipList();
			logDebug("dxing class 共用时: " + (nowTime() - now) + "ms");

			now = nowTime();

			//Java工程
			if ( getOutFilePath().endsWith(".zip") ){
				packagingJavaProject(classesDexZipList);
			}else{
				//打包安卓项目
				packagingAndroidProject(classesDexZipList);
			}

			logDebug("打包共用时: " + (nowTime() - now) + "ms");
		}

		/**
		 * 返回 合并class文件缓存后的输出路径？

		 */
		private String getMainClassesDexZipFilePath(){
			return getMergerCacheDirPath() + "/classes.dex.zip";
		}

		private String getDependencyMergerFilePath(){
			return getMergerCacheDirPath() + "/dependency_merger.dex.zip";
		}

		//混淆
		private File getMixUpDexZipFile(boolean delete){
			File file = new File(getMergerCacheDirPath() , "/classes_mix_up.dex.zip");
			if ( file.exists() && delete ){
				file.delete();
			}
			return file;
		}

		/**
		 * d8 class jar 并返回classesDexZip[d8输出zip]
		 */
		@Override
		public List<String> getClassesDexZipList() throws Throwable{
			List<String> classesDexZipList = new ArrayList<>();
			//先dexing 主classes.dex，即从源码编译的class
			checkInterrupted();

			showProgress("Run D8 Dexing", 60);

			String dexingMergingJarDexFiles = null;
			if ( !getDexingLibs().isEmpty() ){
				dexingMergingJarDexFiles = dexingMergingJarDexFiles();
			}

			//dexing merging class文件
			String dexingMergingClassFiles = dexingMergingClassFiles();
			//保证主dex优先添加
			classesDexZipList.add(0, dexingMergingClassFiles);

			//添加依赖库dexing缓存
			if ( dexingMergingJarDexFiles != null ){
				classesDexZipList.add(dexingMergingJarDexFiles);
			}
			return classesDexZipList;
		}
		/**
		 * 检查线程中断信号
		 */
		private void checkInterrupted() throws InterruptedException{
			if ( Thread.interrupted() ){
				throw new InterruptedException();
			}
		}
		/**
		 * dexing And merging Jar Class Files
		 */
		private String dexingMergingJarDexFiles() throws InterruptedException, Throwable{

			showProgress("Dexing - Libraries", 62);

			List<String> dependencyLibDexs = new ArrayList<>();

			//缓存目录都不存在，全量dexing
			boolean existsCacheDir = new File(getDefaultJarDexDirPath()).exists();
			boolean isBuildRefresh = isBuildRefresh() || ! existsCacheDir;
			//dexing 没有Jardex缓存的依赖
			List<String> dexingLibs = getDexingLibs();
			List<String> needDexingLibs = new ArrayList<>();

			for ( String inputJarFilePath: dexingLibs ){
				checkInterrupted();

				File inputJarFile = new File(inputJarFilePath);
				String outputDexZipFile = getJarDexCachePath(inputJarFilePath);
				File dexCacheFile = new File(outputDexZipFile);

				//根据时间戳判断是否需要dexing
				if ( isBuildRefresh
					|| !dexCacheFile.exists() 
					|| inputJarFile.lastModified() > dexCacheFile.lastModified() ){
					// 需要重写dexing，添加进dexing列表
					needDexingLibs.add(inputJarFilePath);
				}else{
					dependencyLibDexs.add(outputDexZipFile);
				}
			}

			if ( !needDexingLibs.isEmpty() ){

				final AtomicInteger dexingingCount = new AtomicInteger(1);
				DexingJarTask.Configuration configuration = new DexingJarTask.Configuration();
				configuration.minSdkVersion = getMinSdk();
				configuration.user_android_jar = getUserAndroidJar();
				configuration.dependencyLibs = getDexingLibs();
				configuration.dexingingCount = dexingingCount;

				List<DexingJarTask> tasks = new ArrayList<>();

				final int needDexingLibsSize = needDexingLibs.size();
				DexingJarTask.TaskDoneLister taskDoneLister = new DexingJarTask.TaskDoneLister(){
					@Override
					public synchronized void done(){
						showProgress(String.format("Dexing - Libraries (%d/%d)", dexingingCount.get(), needDexingLibsSize), 64);
					}
				};
				// 先显示一下
				taskDoneLister.done();

				// 填充任务列表
				fillDexingJarTasks(needDexingLibs, configuration, taskDoneLister, tasks);

				long now = nowTime();
				
				// (DexingJarTask.ThreadPoolServiceName, 4);
				ThreadPoolService threadPoolService = ThreadPoolService.getDefaultThreadPoolService();
				List<Future<DexingJarTask>> futures = threadPoolService.invokeAll(tasks);
				for ( Future<DexingJarTask> future : futures ){
					// // 这会阻塞直到任务完成或抛出异常
					DexingJarTask dexingJarTask = future.get();
					// 添加dex.zip路径
					if ( dexingJarTask.isBatchMode ){
						dependencyLibDexs.addAll(dexingJarTask.outputDexZipFiles);
					}else{
						dependencyLibDexs.add(dexingJarTask.outputDexZipFile);
					}
				}
				
				logDebug("Dexing - Libraries 共用时: " + (nowTime() - now) + "ms");
			}
			String dependencyMergerFilePath = getDependencyMergerFilePath();
			
			if ( !isMergingJarDexFiles(dependencyLibDexs) ){
				AppLog.d(TAG, "缓存文件没有更新，不需要合并");
				return dependencyMergerFilePath;
			}
			
			// 合并依赖
			MergingJarDexFiles(dependencyLibDexs, dependencyMergerFilePath);

			return dependencyMergerFilePath;
		}
		/**
		 * 对需要dexing的Jar进行分组
		 */
		private void fillDexingJarTasks(List<String> needDexingLibs, DexingJarTask.Configuration configuration, DexingJarTask.TaskDoneLister taskDoneLister, List<DexingJarTask> tasks){
			
			final int needDexingLibsSize = needDexingLibs.size();
			
			long filterThreshold = 8 * 1024 * 1024;
			long filterThreshold2 = 8 * 1024 * 1024;

			for ( int index = 0; index < needDexingLibsSize; index++ ){

				String inputJarFile = needDexingLibs.get(index);
				String outputDexZipFile = getJarDexCachePath(inputJarFile);
				long bigInputJarFileSize = new File(inputJarFile).length();

				// 单个Jar文件大于6MB，则不用批量处理模式
				// 防止OOM
				if ( bigInputJarFileSize > filterThreshold ){
					DexingJarTask dexingJarTask = new DexingJarTask(inputJarFile, outputDexZipFile, configuration);
					dexingJarTask.setTaskDoneLister(taskDoneLister);
					tasks.add(dexingJarTask);
					continue;
				}

				// 处理小文件
				// 小文件集合
				List<String> smallInputJarFiles = new ArrayList<String>();
				List<String> smallOutputDexZipFiles = new ArrayList<String>();
				// 添加当前输入小文件
				smallInputJarFiles.add(inputJarFile);
				smallOutputDexZipFiles.add(outputDexZipFile);
				// 添加完了就应该更新索引
				index++;

				long smallInputJarFilesSize = bigInputJarFileSize;
				// 在阈值内
				while (smallInputJarFilesSize < filterThreshold2 
					   && index < needDexingLibsSize ){
					String inputJarFile2 = needDexingLibs.get(index);
					String outputDexZipFile2 = getJarDexCachePath(inputJarFile2);

					long fileSize2 = new File(inputJarFile2).length();
					// 仍然是大文件模式
					if ( fileSize2 > filterThreshold ){
						DexingJarTask dexingJarTask = new DexingJarTask(inputJarFile2, outputDexZipFile2, configuration);
						dexingJarTask.setTaskDoneLister(taskDoneLister);
						tasks.add(dexingJarTask);
					}else{
						// 小文件
						// 更新小文件整体大小
						smallInputJarFilesSize += fileSize2;
						// 添加当前输入小文件
						smallInputJarFiles.add(inputJarFile2);
						smallOutputDexZipFiles.add(outputDexZipFile2);
					}
					// 下一个文件
					index++;
				}
				// 此时说明小文件集合大于阈值
				// 启用批量模式
				DexingJarTask dexingJarTask = new DexingJarTask(smallInputJarFiles, smallOutputDexZipFiles, configuration);
				dexingJarTask.setTaskDoneLister(taskDoneLister);
				tasks.add(dexingJarTask);
				logDebug(String.format("DexingJarTask内文件 %d", smallInputJarFiles.size()));
			}
		}

		public void MergingJarDexFiles(List<String> dependencyLibDexs, String dependencyMergerFile) throws Throwable{
			showProgress("Merging - Libraries", 65);
			List<String> argsList  = new ArrayList<String>();
			String user_androidjar = null;
			// 合并*.jar.dex
			D8TaskWrapper.fillD8Args(argsList, getMinSdk(), false, false, user_androidjar, null, dependencyMergerFile);

			//输入dexs
			argsList.addAll(dependencyLibDexs);
			try{
				// 将采用 子进程方式，防止oom
				D8TaskWrapper.runD8Task(argsList, environment);
			}
			catch (Throwable e){
				
				// 有错误时，删除缓存
				new File(dependencyMergerFile).delete();
				
				throw e;
			}
			logDebug("合并依赖库，已输出: " + dependencyMergerFile);
		}

		private boolean isMergingJarDexFiles(List<String> inputLibDexs) throws FileNotFoundException, IOException{
			File dependencyMergerFile = new File(getDependencyMergerFilePath());
			long lastModified = dependencyMergerFile.lastModified();


			boolean isMergingJarDexFiles = false;

			//对比时间戳
			for ( String dependencyLibDex : inputLibDexs ){
				if ( new File(dependencyLibDex).lastModified() > lastModified ){
					isMergingJarDexFiles = true;
					break;
				}
			}

			//查看输入文件集合是否一致
			Set<String> inputJarFilesSet = new HashSet<String>(inputLibDexs);
			File inputInfoFile = new File(getDependencyMergerFilePath() + "_inputInfo.txt");
			if ( !inputInfoFile.exists() ){
				isMergingJarDexFiles = true;
			} 

			if ( !isMergingJarDexFiles ){
				Set<String> lastInputJarFilesSet = new HashSet<>();
				BufferedReader br = null;
				try{
					br = new BufferedReader(new InputStreamReader(new FileInputStream(inputInfoFile)));
					String line;
					while ( (line = br.readLine()) != null ){
						lastInputJarFilesSet.add(line);
					}
				}
				finally{
					IOUtils.close(br);
				}
				//比较
				isMergingJarDexFiles = inputJarFilesSet.size() != lastInputJarFilesSet.size()
					|| !inputJarFilesSet.containsAll(lastInputJarFilesSet);
			}

			if ( isMergingJarDexFiles ){
				//写入inputJarFilesSet
				FileOutputStream output =null;
				try{ 
					output = new FileOutputStream(inputInfoFile);
					for ( String input : inputJarFilesSet ){
						output.write(input.getBytes());
						output.write('\n');
					}
					output.close();
				}
				finally{
					IOUtils.close(output);
				}
			}
			return isMergingJarDexFiles;
		}



		public void deleteADRTClassFile(String classFileCacheDir){
			if ( classFileCacheDir.endsWith("debug") || ZeroAicySetting.enableADRT() ){
				return;
			}

			String defaultClassDexCacheDirPath = getDefaultClassDexCacheDirPath();

			{
				String[] deleteClassFileNames = new String[]{"adrt/ADRTSender", "adrt/ADRTLogCatReader"};
				for ( String deleteClassFileName : deleteClassFileNames ){
					// adrt/ADRTSender
					//String deleteClassFileName = "adrt/ADRTSender.class";
					File deleteClassFile = new File(classFileCacheDir, deleteClassFileName + ".class");
					if ( deleteClassFile.exists() ){
						deleteClassFile.delete();
					}
					File adrtDexFile = new File(defaultClassDexCacheDirPath, deleteClassFileName + ".dex");
					if ( adrtDexFile.exists() ){
						adrtDexFile.delete();
					}
				}	
			}

		}
		/**
		 * dexing And merging Class Files
		 */
		private String dexingMergingClassFiles() throws InterruptedException, Throwable{
			checkInterrupted();

			List<String> incrementalClassFiles = new ArrayList<>();
			// 
			Set<String> classFileMap = new HashSet<>();


			//填充class文件

			// 主项目class缓存路径
			String mainProjectClassCacheDirPath = getMainClassCacheDir();
			// 填充前，删除 adrt注入的class文件
			deleteADRTClassFile(mainProjectClassCacheDirPath);

			// 优先填充填主项目的class缓存
			fillClassFileCache(mainProjectClassCacheDirPath, incrementalClassFiles, classFileMap);

			//遍历添加所有项目的class缓存目录
			for ( String classFileRootDir : getAllClassFileRootDirs() ){
				checkInterrupted();

				if ( classFileRootDir != null 
					|| !classFileRootDir.equals(mainProjectClassCacheDirPath) ){
					// 填充前，删除 adrt注入的class文件
					deleteADRTClassFile(classFileRootDir);

					// 填充子项目class缓存
					fillClassFileCache(classFileRootDir, incrementalClassFiles, classFileMap);
				}
			}

			String mainClassesDexZipFilePath = getMainClassesDexZipFilePath();
			//增量为0，不dexing
			if ( incrementalClassFiles.isEmpty() ){
				return mainClassesDexZipFilePath;
			}

			//logDebug("待dexing类文件数量: " + incrementalClassFiles.size());

			showProgress("Dexing - Classes", 67);
			//dexing classFile[增量]
			dexingClassFilesFromD8(getDefaultClassDexCacheDirPath(), incrementalClassFiles);

			showProgress("Merging - Classes", 69);

			// 查询需要合并的 dex
			List<String> classeDexFiles = FileUtil.Files2Strings(FileUtil.findFile(new File(getDefaultClassDexCacheDirPath()), ".dex"));
			// 合并dex
			mergingClassDexs(mainClassesDexZipFilePath, classeDexFiles);				

			return mainClassesDexZipFilePath;
		}

		// dexing所有class
		private void dexingClassFilesFromD8(String outPath, List<String> dexingClassFiles) throws Throwable{
			List<String> argsList = new ArrayList<>();
			//检查输出目录
			File outDir = new File(outPath);
			if ( !outDir.exists() ){
				outDir.mkdirs();
			}
			String user_android_jar = getUserAndroidJar();
			int minSdk = getMinSdk();
			if ( minSdk < 21 ){
				//minSdk＜21，无法编译生成的class
				minSdk = 21;
			}
			// AIDE生成的dex不会与库依赖合并
			// 合并dex时 并不会脱糖
			// 因此必须在 dexing时添加库信息
			// 所以必须提前脱糖

			// dexing AIDE编译的 *.class
			D8TaskWrapper.fillD8Args(argsList, minSdk, true, true, user_android_jar, getDexingLibs(), outPath);
			for ( String compileOnlyLib : compileOnlyLibs ){
				argsList.add("--lib");
				argsList.add(compileOnlyLib);
			}

			//添加需要编译的jar
			argsList.addAll(dexingClassFiles);

			// 将采用 子进程方式，防止oom
			D8TaskWrapper.runD8Task(argsList, this.environment);
		}

		//合并AIDE生成的class.dex
		private void mergingClassDexs(String outDexZipPath, Collection<String> classeDexFiles) throws Throwable{
			File outDexZipFile = new File(outDexZipPath);
			//删除缓存文件
			outDexZipFile.delete();

			List<String> argsList  = new ArrayList<String>();
			String user_androidjar = null; 
			// 合并 AIDE编译的 *.class.dex
			D8TaskWrapper.fillD8Args(argsList, getMinSdk(), false, false, user_androidjar, null, outDexZipPath);

			//输入dexs
			argsList.addAll(classeDexFiles);
			// 将采用 子进程方式，防止oom
			D8TaskWrapper.runD8Task(argsList, this.environment);
			//logDebug("合并classes.dex，已输出: " + outDexZipPath);
		}

		/**
		 * dexing jar
		 */
		private List<String> dexingLibs = new ArrayList<>();
		public List<String> getDexingLibs(){
			return this.dexingLibs;
		}
		/**
		 * compileOnly
		 */
		private List<String> compileOnlyLibs  = new ArrayList<>();

		/**
		 * aar混淆规则文件
		 */
		private List<Path> proguardPaths  = new ArrayList<>();
		private List<String> proguardFiles  = new ArrayList<>();


		/**
		 * 初始化环境
		 */
		private void initBuildEnvironment() throws Throwable{

			if ( isBuildRefresh() ){
				deleteCacheDir();
			}

			ScopeTypeQuerier scopeTypeQuerier = getScopeTypeQuerier();
			this.compileOnlyLibs = scopeTypeQuerier.getCompileOnlyLibs();
			//this.runtimeOnlyLibs = scopeTypeQuerier.getRuntimeOnlyLibs();
			this.dexingLibs = scopeTypeQuerier.getDexingLibs();


		}
		private void deleteCacheDir(){
			File defaultJarDexDir = new File(getDefaultJarDexDirPath());
			File defaultClassDexCacheDir = new File(getDefaultClassDexCacheDirPath());
			File mergerCacheDir = new File(getMergerCacheDirPath());

			FileUtil.deleteFolder(new File(getDefaultIntermediatesDirPath()));

			defaultJarDexDir.mkdirs();
			defaultClassDexCacheDir.mkdirs();
			mergerCacheDir.mkdirs();
		}

		// dex.zip转换器，即根目录下有classes%d.dex的zip文件的转换器
		final ZipEntryTransformer.DexZipTransformer dexZipEntryTransformer = new ZipEntryTransformer.DexZipTransformer();
		// 从jar依赖添加资源的过滤器，
		final ZipEntryTransformer.ZipResourceTransformer zipResourceZipEntryTransformer = new ZipEntryTransformer.ZipResourceTransformer();
		// 从文件夹添加原生库文件，
		final ZipEntryTransformer.NativeLibFileTransformer nativeLibZipEntryTransformer;
		final ZipEntryTransformer.LibgdxNativesTransformer libgdxNativesTransformer;

		public void minify2() throws Exception, Throwable{
			// proguardPaths
			// 
			// 初始化混淆环境

			// 查找主项目生成的 "aapt_rules.txt"
			File aaptRulesFile = new File(getDefaultIntermediatesDirPath(), "aapt_rules.txt");
			if ( aaptRulesFile.isFile() ){
				this.proguardFiles.add(aaptRulesFile.getAbsolutePath());
				this.proguardPaths.add(aaptRulesFile.toPath());			
			}

			/**
			 * 从主项目build.gradle添加混淆规则文件
			 */
			for ( String proguardPulesFilePath  : getZeroAicyBuildGradle().getProguardFiles() ){
				if ( TextUtils.isEmpty(proguardPulesFilePath) ){continue;}
				File proguardPulesFile  = new File(proguardPulesFilePath);
				if ( proguardPulesFile.isFile() ){
					this.proguardFiles.add(proguardPulesFile.getAbsolutePath());
					this.proguardPaths.add(proguardPulesFile.toPath());
				}
			}
			// 遍历并添加混淆规则文件
			for ( String dependencyLib : this.dexingLibs ){
				File jarFile = new File(dependencyLib);
				String fileName = jarFile.getName().toLowerCase();

				if ( fileName.equals("classes.jar") ){
					File proguardFile = new File(jarFile.getParentFile(), "proguard.txt");
					if ( proguardFile.isFile() ){
						this.proguardFiles.add(proguardFile.getAbsolutePath());
						this.proguardPaths.add(proguardFile.toPath());
					}					
				}
			}

			// 编译依赖库
			showProgress("配置混淆中", 60);

			logDebug("合并待混淆类库");


			// get混淆输出文件路径[dex.zip]
			File mixUpDexZipFile = getMixUpDexZipFile(true);


			//BaseDiagnosticsHandler baseDiagnosticsHandler = new BaseDiagnosticsHandler();
			//32线程
			int minSdk = getMinSdk();
			//主要是为了兼容AIDE的输出类
			minSdk = minSdk < 21 ? 21 : minSdk;

			List<String> argsList = new ArrayList<>();
			argsList.add("--thread-count");
			argsList.add("32");

			argsList.add("--lib");
			argsList.add(getUserAndroidJar());

			argsList.add("--min-api");
			argsList.add(String.valueOf(minSdk));

			// 输出路径
			argsList.add("--output ");
			argsList.add(mixUpDexZipFile.getAbsolutePath());
			for ( String compileOnlyLib : this.compileOnlyLibs ){
				argsList.add("--lib");
				argsList.add(compileOnlyLib);

			}
			// 添加 AIDE编译的类
			List<String> mainClassFilePaths = getMainClassFilePaths();
			// 添加java -> class的类文件
			argsList.addAll(mainClassFilePaths);

			// 添加库
			argsList.addAll(this.getDexingLibs());

			// 添加混淆规则
			for ( String proguardPath : this.proguardFiles ){
				argsList.add("--pg-conf");
				argsList.add(proguardPath);
			}

			//* 输出映射表
			if ( true ){
				argsList.add("--pg-map-output");
				File proguardMapFile = new File(getIntermediatesChildDirPath("r8"), "proguardMap.txt");
				argsList.add(proguardMapFile.getAbsolutePath());
			}
			//*/

			showProgress("混淆class中", 65);

			//运行r8
			D8TaskWrapper.runR8Task(argsList, this.environment);

		}


		/**
		 * 遍历AIDE Class缓存路径添加class文件
		 */
		private List<String> getMainClassFilePaths() throws InterruptedException{
			List<String> allClassFiles = new ArrayList<>();
			//类名
			Set<String> classFileSet = new HashSet<>();
			//填充class文件
			String mainProjectClassCacheDirPath = getMainClassCacheDir();
			fillAllClassFileCache(mainProjectClassCacheDirPath, allClassFiles, classFileSet);
			//遍历添加所有项目的class缓存目录
			for ( String classFileRootDir : getAllClassFileRootDirs() ){
				checkInterrupted();
				if ( classFileRootDir != null 
					|| !classFileRootDir.equals(mainProjectClassCacheDirPath) ){
					fillAllClassFileCache(classFileRootDir, allClassFiles, classFileSet);
				}
			}
			return allClassFiles;
		}


		// 安卓支持混淆才有意义，也只能是安卓
		private void packagingAndroidMinify() throws Throwable{

			//混淆
			minify2();

			showProgress("构建APK", 80);			
			//未zip优化，未签名
			File unZipAlignedSignedApkFile = getUnZipAlignSignerApkFile(true);
			PackagingStream packagingZipOutput = new PackagingStream(new FileOutputStream(unZipAlignedSignedApkFile));

			logDebug("从aapt2生成文件添加资源");
			//resources_ap_file
			String aAptResourceFilePath = getAAptResourceFilePath();
			//打包resources.ap_ 文件
			ZipEntryTransformerService.packagingZipFile(aAptResourceFilePath, zipResourceZipEntryTransformer, packagingZipOutput, true);

			// 从文件夹添加原生库文件，
			ZipEntryTransformer.NativeLibFileTransformer nativeLibZipEntryTransformer = new ZipEntryTransformer.NativeLibFileTransformer(getAndroidFxtractNativeLibs());

			//从原生库目录添加so
			for ( String nativeLibDirPath : this.getNativeLibDirs() ){
				File nativeLibDirFile = new File(nativeLibDirPath);
				if ( !nativeLibDirFile.exists() ){
					continue;
				}
				logDebug("从原生库添加" + nativeLibDirPath);
				ZipEntryTransformerService.packagingDirFile(nativeLibDirPath, nativeLibDirFile, nativeLibZipEntryTransformer, packagingZipOutput);

			}
			//打包混淆后的dex
			ZipEntryTransformerService.packagingZipFile(getMixUpDexZipFile(false).getAbsolutePath(), dexZipEntryTransformer, packagingZipOutput, false);

			//打包源码目录下的资源
			packagingSourceDirsResource(packagingZipOutput);			
			//打包jar资源，优先第一个
			packagingJarResources(packagingZipOutput);
			// 打包 libgdxNatives依赖资源
			packagingLibgdxNativesResources(packagingZipOutput);
			//打包完成
			packagingZipOutput.close();

			//优化并签名apk
			zipalignSignerApk();
		}
		/**
		 * 打包Java项目即zip
		 */
		@Override
		public void packagingJavaProject(List<String> dexZipPathList) throws Throwable{
			showProgress("packaging - JavaProject", 73);

			File outFile = new File(getOutFilePath());
			File outDirFile = outFile.getParentFile();
			if ( !outDirFile.exists() ){
				outDirFile.mkdirs();
			}
			//构建输出文件
			PackagingStream packagingZipOutput = new PackagingStream(new FileOutputStream(outFile));
			//打包dex
			packagingDexs(dexZipPathList, packagingZipOutput);
			packagingSourceDirsResource(packagingZipOutput);
			//打包依赖库资源
			packagingJarResources(packagingZipOutput);
			packagingZipOutput.close();
		}

		private void packagingDexs(List<String> dexZipPathList, PackagingStream packagingZipOutput) throws IOException{
			logDebug("添加classes.dex");
			for ( String dexZipPath : dexZipPathList ){
				ZipEntryTransformerService.packagingZipFile(dexZipPath, dexZipEntryTransformer, packagingZipOutput, false);
			}
		}
		/**
		 * 打包安卓即apk
		 */
		@Override
		public void packagingAndroidProject(List<String> dexZipPathList) throws Throwable{

			showProgress("构建APK", 80);
			//未zip优化，未签名
			File unZipAlignedUnSignedApkFile = getUnZipAlignSignerApkFile(true);

			PackagingStream packagingZipOutput = new PackagingStream(new FileOutputStream(unZipAlignedUnSignedApkFile));
			//resources_ap_file
			String aAptResourceFilePath = getAAptResourceFilePath();
			logDebug("Adding aapt generated resources from " + aAptResourceFilePath);
			//打包resources.ap_ 文件
			ZipEntryTransformerService.packagingZipFile(aAptResourceFilePath, zipResourceZipEntryTransformer, packagingZipOutput, true);

			//从原生库目录添加so
			logDebug("添加原生库");
			for ( String nativeLibDirPath : this.getNativeLibDirs() ){
				File nativeLibDirFile = new File(nativeLibDirPath);
				if ( nativeLibDirFile.exists() ){
					ZipEntryTransformerService.packagingDirFile(nativeLibDirPath, nativeLibDirFile, nativeLibZipEntryTransformer, packagingZipOutput);
				}
			}
			//打包dex
			packagingDexs(dexZipPathList, packagingZipOutput);
			//打包源码目录下的资源
			packagingSourceDirsResource(packagingZipOutput);			
			//打包jar资源，优先第一个
			packagingJarResources(packagingZipOutput);
			// 打包 libgdxNatives依赖资源
			packagingLibgdxNativesResources(packagingZipOutput);

			//打包完成
			packagingZipOutput.close();

			//优化并签名apk
			zipalignSignerApk();

		}

		private void zipalignSignerApk() throws Throwable{

			//优化apk
			showProgress("ZeroAicy Zipalign APK ", 85);
			// zipalign命令路径 优化前，未签名 优化后，未签名
			File unSignedApkFile = ApkSignerService.zipalignApk(getZipalignLibPath(), getUnZipAlignSignerApkFile(false), getUnSignedApkFile(true));

			//签名
			logDebug("开始Signing APK: ");
			long now = nowTime();
			//-zipaligned-unsigned 
			showProgress("ZeroAicy Signing APK ", 90);
			ApkSignerService.signerApk(getMinSdk(),
									   getSignaturePath(),
									   getSignatureAlias(),
									   getSignatureAliasPassword(),
									   getSignaturePassword(),
									   unSignedApkFile, 
									   new File(getOutFilePath()));

			logDebug("Signing APK共用时: " + (nowTime() - now) + "ms");
		}

		private void packagingLibgdxNativesResources(PackagingStream packagingZipOutput) throws IOException, Throwable{
			logDebug("从LibgdxNatives添加资源");
			for ( String libgdxNativesLibPath : getScopeTypeQuerier().getLibgdxNativesLibs() ){
				this.libgdxNativesTransformer.setCurLibgdxNativesLibsPath(libgdxNativesLibPath);
				ZipEntryTransformerService.packagingZipFile(libgdxNativesLibPath, libgdxNativesTransformer, packagingZipOutput, false);
			}

		}

		private void packagingSourceDirsResource(PackagingStream packagingZipOutput) throws IOException{
			logDebug("从源码目录添加资源");
			//从源码目录添加
			String[] sourceDirs = getSourceDirs();
			if ( sourceDirs == null ){
				return;
			}
			for ( String sourceDir : sourceDirs ){
				File sourceDirFile = new File(sourceDir);
				if ( !sourceDirFile.exists() ){
					continue;
				}
				ZipEntryTransformerService.packagingDirFile(sourceDir, new File(sourceDir), zipResourceZipEntryTransformer, packagingZipOutput);
			}
		}

		private void packagingJarResources(PackagingStream packagingZipOutput) throws IOException, Throwable{

			logDebug("从JAR文件添加资源");

			// dexing jar资源
			for ( String dependencyLibPath : this.dexingLibs ){
				ZipEntryTransformerService.packagingZipFile(dependencyLibPath, zipResourceZipEntryTransformer, packagingZipOutput, false);
			}
			// runtimeOnly Jar资源
			for ( String runtimeOnlyJarPath : getScopeTypeQuerier().getRuntimeOnlyLibs() ){
				ZipEntryTransformerService.packagingZipFile(runtimeOnlyJarPath, dexZipEntryTransformer, packagingZipOutput, false);
			}

		}

		/**
		 * 返回未优化,未签名时的apk输出文件，并清除已有缓存
		 */
		private File getUnZipAlignSignerApkFile(boolean delete){
			String getUnZipAlignSignerApkPath = getOutFilePath() + "-unzipaligned-unsigned";
			File unZipAlignUnSignerApkFile = new File(getUnZipAlignSignerApkPath);
			if ( delete && unZipAlignUnSignerApkFile.exists() ){
				unZipAlignUnSignerApkFile.delete();
				//直接返回，文件存在所以父目录也存在
				return unZipAlignUnSignerApkFile;
			}

			File parentFile = unZipAlignUnSignerApkFile.getParentFile();
			if ( !parentFile.exists() ){
				parentFile.mkdirs();
			}
			return unZipAlignUnSignerApkFile;
		}
		/**
		 * 返回未未签名apk的文件，并清除已有缓存
		 */
		private File getUnSignedApkFile(boolean delete){
			File unSignedApkFile = new File(getOutFilePath() + "-unsigned");
			File parentFile = unSignedApkFile.getParentFile();
			//删除输出
			if ( delete && unSignedApkFile.exists() ){
				unSignedApkFile.delete();
				return unSignedApkFile;
			}

			if ( !parentFile.exists() ){
				parentFile.mkdirs();
			}
			return unSignedApkFile;
		}
	}
}
