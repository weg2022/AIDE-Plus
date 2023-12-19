package io.github.zeroaicy.aide.services;

import android.content.SharedPreferences;
import android.text.TextUtils;
import com.aide.common.AppLog;
import com.aide.ui.build.packagingservice.ExternalPackagingService;
import com.android.apksig.ApkSigner;
import io.github.zeroaicy.aide.preference.ZeroAicySetting;
import io.github.zeroaicy.util.FileUtil;
import io.github.zeroaicy.util.Log;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import sun1.security.pkcs.PKCS8Key;
import java.util.zip.ZipFile;

public class ZeroAicyPackagingWorker extends PackagingWorkerWrapper{

	private static final String TAG = "Worker";
	public ZeroAicyPackagingWorker(ExternalPackagingService service){
		super(service);
	}
	@Override
	public PackagingWorkerWrapper.TaskWrapper
	getTaskWrapper(String mainClassCacheDir, String[] classFileRootDirs, String[] sourceDirs, 
				   String[] dependencyLibs, String outDirPath, String Zo, 
				   String aAptResourcePath, String[] nativeLibDirs, String outFilePath, 
				   String signaturePath, String signaturePassword, String signatureAlias, 
				   String signatureAliasPassword, boolean buildRefresh, boolean Ws, boolean QX){
		return new ZeroAicyR8Task(mainClassCacheDir, classFileRootDirs, sourceDirs, 
								  dependencyLibs, outDirPath, Zo, aAptResourcePath, 
								  nativeLibDirs, outFilePath, signaturePath, signaturePassword, 
								  signatureAlias, signatureAliasPassword, buildRefresh, Ws, QX);
	}

	public class ZeroAicyR8Task extends TaskWrapper{
		public ZeroAicyR8Task(String mainClassCacheDir, String[] classFileRootDirs, String[] sourceDirs, String[] dependencyLibs, String outDirPath, String Zo, String aAptResourcePath, String[] nativeLibDirs, String outFilePath, String signaturePath, String signaturePassword, String signatureAlias, String signatureAliasPassword, boolean buildRefresh, boolean Ws, boolean QX){
			super(mainClassCacheDir, classFileRootDirs, sourceDirs, dependencyLibs, outDirPath, Zo, aAptResourcePath, nativeLibDirs, outFilePath, signaturePath, signaturePassword, signatureAlias, signatureAliasPassword, buildRefresh, Ws, QX);
		}

		/**
		 * 返回 合并class文件缓存后的输出路径
		 */
		private String getMainClassesDexZipFilePath(){
			return getIntermediatesChildDirPath("dex") + "/classes.dex.zip";
		}

		private String getDependencyMergerFilePath(){
			return getIntermediatesChildDirPath("dex") + "/dependency_merger.dex.zip";
		}
		//混淆
		private String getMixUpDexZipFilePath(){
			return getIntermediatesChildDirPath("dex") + "/classes_mix_up.dex.zip";
		}

		/**
		 //jardex路径
		 Log.d("Zo", Zo);
		 //dex优化[optimze_dex]
		 Log.d("Ws", Ws);
		 //minsdk是否大于等于21
		 Log.d("QX", QX);
		 */
		/**
		 * d8 class jar 并返回classesDexZip[d8输出zip]
		 */
		@Override
		public List<String> getClassesDexZipList() throws Throwable{
			List<String> classesDexZipList = new ArrayList<>();
			//先dexing 主classes.dex，即从源码编译的class
			checkInterrupted();
			showProgress("Run D8 Dexing", 60);

			String dexingMergingJarDexFiles;
			if ( !getValidDependencyLibs().isEmpty() ){
				dexingMergingJarDexFiles = dexingMergingJarDexFiles();
			}
			else{
				dexingMergingJarDexFiles = null;
			}

			//dexing merging class文件
			String dexingMergingClassFiles = dexingMergingClassFiles();
			//保证主dex优先添加
			classesDexZipList.add(dexingMergingClassFiles);

			//添加依赖库dexing缓存
			if ( dexingMergingJarDexFiles != null ){
				classesDexZipList.add(dexingMergingJarDexFiles);
			}

			//混淆(false, classesDexZipList);
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
		private String dexingMergingJarDexFiles() throws InterruptedException, IOException{
			String dependencyLibDexZipFilePath = getDependencyMergerFilePath();
			//缓存目录都不存在
			boolean existsCacheFile = new File(dependencyLibDexZipFilePath).exists();

			//缓存文件不存在使用全量编译
			showProgress("Dexing - Libraries", 62);

			List<String> dependencyLibDexs = new ArrayList<>();

			//dexing 没有Jardex缓存的依赖
			for ( String dependencyLibPath : getValidDependencyLibs() ){
				checkInterrupted();

				File jarFile = new File(dependencyLibPath);
				String dexCachePath = getJarDexCachePath(dependencyLibPath);
				File dexCacheFile = new File(dexCachePath);

				//根据时间戳判断是否需要dexing
				if ( isBuildRefresh() || existsCacheFile
					|| !dexCacheFile.exists() || jarFile.lastModified() > dexCacheFile.lastModified() ){
					dexingDependencyLibFile(dependencyLibPath);
				}
				dependencyLibDexs.add(dexCachePath);
			}
			//合并
			//此缓存察觉不到依赖数量变化
			File libDexZipFile = new File(dependencyLibDexZipFilePath);
			long libDexZipFileLastModified = libDexZipFile.lastModified();
			boolean needMerg = false;

			for ( String dependencyLibDex : dependencyLibDexs ){
				if ( new File(dependencyLibDex).lastModified() > libDexZipFileLastModified ){
					needMerg = true;
					break;
				}
			}

			if ( !needMerg ){
				Log.d(TAG, "缓存文件没有更新，不需要合并");
				return dependencyLibDexZipFilePath;
			}

			checkInterrupted();
			showProgress("Merging - Libraries", 65);
			List<String> argsList  = new ArrayList<String>();
			String user_androidjar = null;
			fillD8Args(argsList, getMinSdk(), false, true, user_androidjar, null, dependencyLibDexZipFilePath);

			//输入dexs
			argsList.addAll(dependencyLibDexs);
			com.android.tools.r8.D8.main(argsList.toArray(new String[argsList.size()]));
			logDebug("合并依赖库，已输出: " + dependencyLibDexZipFilePath);

			return dependencyLibDexZipFilePath;
		}


		/**
		 * dexing库
		 */
		private String dexingDependencyLibFile(String dependencyLibPath) throws IOException{
			//out
			String dexCachePath = getJarDexCachePath(dependencyLibPath);
			File dexCacheFile = new File(dexCachePath);


			File dexZipTempFile = File.createTempFile(dexCacheFile.getName(), ".dex.zip", dexCacheFile.getParentFile());
			//更新时间
			dexZipTempFile.setLastModified(System.currentTimeMillis());

			List<String> argsList = new ArrayList<>();

			String user_androidjar = getUserAndroidJar();
			fillD8Args(argsList, getMinSdk(), false, false, user_androidjar, getValidDependencyLibs(), dexZipTempFile.getAbsolutePath());
			//添加需要编译的jar
			argsList.add(dependencyLibPath);

			Log.println(dependencyLibPath + " -> " + dexCachePath);

			try{
				//dexing jar
				com.android.tools.r8.D8.main(argsList.toArray(new String[argsList.size()]));

				//临时文件移动到实际输出文件
				dexZipTempFile.renameTo(dexCacheFile);
				//删除
				dexZipTempFile.delete();

				//更新文件修改时间
				dexCacheFile.setLastModified(System.currentTimeMillis() + 10);
			}
			finally{
				//临时文件用完时删除
				if ( dexZipTempFile.exists() ){
					logDebug(dexZipTempFile + "删除: " + dexZipTempFile.delete());						
				}
			}
			return dexCachePath;
		}

		/**
		 * dexing And merging Class Files
		 */
		private String dexingMergingClassFiles() throws InterruptedException{
			checkInterrupted();

			List<String> incrementalClassFiles = new ArrayList<>();
			Set<String> classFileMap = new HashSet<>();

			//填充class文件
			String mainProjectClassCacheDirPath = getMainClassCacheDir();
			fillClassFileCache(mainProjectClassCacheDirPath, incrementalClassFiles, classFileMap);

			//遍历添加所有项目的class缓存目录
			for ( String classFileRootDir : getAllClassFileRootDirs() ){
				checkInterrupted();
				if ( classFileRootDir != null 
					|| !classFileRootDir.equals(mainProjectClassCacheDirPath) ){
					fillClassFileCache(classFileRootDir, incrementalClassFiles, classFileMap);
				}
			}
			String mainClassesDexZipFilePath = getMainClassesDexZipFilePath();

			//增量为0，不dexing
			if ( incrementalClassFiles.isEmpty() ){
				return mainClassesDexZipFilePath;
			}

			showProgress("Dexing - Classes", 67);
			//dexing classFile[增量]
			dexingClassFilesFromD8(getDefaultClassDexCacheDirPath(), incrementalClassFiles);

			List<String> classeDexFiles = FileUtil.Files2Strings(FileUtil.findFile(new File(getDefaultClassDexCacheDirPath()), ".dex"));
			//无论增量与否都要合并所有dex
			// ./intermediates/dex/mainClassesDex.zip
			showProgress("Merging - Classes", 69);
			mergingClassDexs(mainClassesDexZipFilePath, classeDexFiles);				

			return mainClassesDexZipFilePath;
		}





		public void fillD8Args(List<String> argsList, int minSdk, boolean file_per_class_file, boolean intermediate, String user_androidjar, List<String> dependencyLibs, String outPath){
			argsList.add("--min-api");
			//待跟随minSDK
			argsList.add(String.valueOf(minSdk));

			if ( file_per_class_file ){
				argsList.add("--file-per-class-file");
			}
			if ( intermediate ){
				argsList.add("--intermediate");
			}
			if ( !TextUtils.isEmpty(user_androidjar) ){
				argsList.add("--lib");
				argsList.add(user_androidjar);
			}

			if ( dependencyLibs != null ){
				for ( String librarie : dependencyLibs ){
					argsList.add("--classpath");
					argsList.add(librarie);
				}
			}
			argsList.add("--output");
			argsList.add(outPath);
		}

		/**
		 * 是否仅编译，接受小写
		 */
		private boolean isCompileOnly(String dependencyLibLowerCase){
			return dependencyLibLowerCase.endsWith("_compileonly.jar");
		}
		/**
		 * 是否仅打包，接受小写
		 */
		private boolean isRuntimeOnly(String dependencyLibLowerCase){
			return dependencyLibLowerCase.endsWith("_resource.jar");
		}
		/**
		 * 存在的依赖，但不包括_resource.jar
		 */
		public List<String> getExistsDependencyLibs(){
			ArrayList<String> existsDependencyLibs = new ArrayList<>();
			//有效依赖为[不是 compile | runtime only，不存在，非jar]
			for ( String dependencyLib : getAllDependencyLibs() ){
				String dependencyLibLowerCase = dependencyLib.toLowerCase();
				if ( !dependencyLibLowerCase.endsWith(".jar")
					|| dependencyLibLowerCase.endsWith("_resource.jar") ){
					continue;
				}

				File libFile = new File(dependencyLib);
				if ( libFile.exists() ){
					existsDependencyLibs.add(dependencyLib);
				}
			}
			return existsDependencyLibs;
		}
		// dexing所有class
		private void dexingClassFilesFromD8(String outPath, List<String> dexingClassFiles){
			List<String> argsList = new ArrayList<>();
			//检查输出目录
			File outDir = new File(outPath);
			if ( !outDir.exists() ){
				outDir.mkdirs();
			}
			String user_androidjar = getUserAndroidJar();
			int minSdk = getMinSdk();
			if ( minSdk < 21 ){
				//minSdk＜21，无法编译生成的class
				minSdk = 21;
			}
			fillD8Args(argsList, minSdk, true, true, user_androidjar, getExistsDependencyLibs(), outPath);
			//添加需要编译的jar
			argsList.addAll(dexingClassFiles);

			Log.println("dexingClassFilesFromD8参数");
			Log.println(argsList);

			com.android.tools.r8.D8.main(argsList.toArray(new String[argsList.size()]));
		}

		//合并AIDE生成的class.dex
		private void mergingClassDexs(String outDexZipPath, Collection<String> classeDexFiles){
			File outDexZipFile = new File(outDexZipPath);
			//删除缓存文件
			outDexZipFile.delete();

			List<String> argsList  = new ArrayList<String>();
			String user_androidjar = null; 
			fillD8Args(argsList, getMinSdk(), false, false, user_androidjar, getValidDependencyLibs(), outDexZipPath);

			//输入dexs
			argsList.addAll(classeDexFiles);
			com.android.tools.r8.D8.main(argsList.toArray(new String[argsList.size()]));
			logDebug("合并classes.dex，已输出: " + outDexZipPath);
		}


		public void fillClassFileCache(String classCacheRootDirPath, List<String> incrementalClassFiles, Set<String> classFileSet){
			fillClassFileCacheMap(classCacheRootDirPath, new File(classCacheRootDirPath), incrementalClassFiles, classFileSet);
		}

		/**
		 * 递归填充classCacheRootDirPath目录下的所有class文件
		 */
		public void fillClassFileCacheMap(String classCacheRootDirPath, File dirFile, List<String> incrementalClassFiles, Set<String> classFileSet){
			File[] listFiles = dirFile.listFiles();
			if ( listFiles == null ){
				return;
			}
			boolean isBuildRefresh = isBuildRefresh();
			for ( File file : listFiles ){
				if ( file.isDirectory() ){
					fillClassFileCacheMap(classCacheRootDirPath, file, incrementalClassFiles, classFileSet);
					continue;
				}
				if ( file.isFile() ){
					String classFilePath = file.getPath();
					if ( !classFilePath.toLowerCase().endsWith(".class") ){
						continue;
					}
					String classFileSubPath = classFilePath.substring(classCacheRootDirPath.length());
					if ( classFileSet.contains(classFileSubPath) ){
						AppLog.DW("忽略重复 .class 文件 " + classFilePath);
						continue;
					}

					classFileSet.add(classFileSubPath);
					if ( isBuildRefresh ){
						//构建刷新，直接添加
						incrementalClassFiles.add(classFilePath);
						continue;
					}
					String classDexFileCache = getClassDexFileCache(classFileSubPath);
					if ( file.lastModified() > new File(classDexFileCache).lastModified() ){
						//需要重新dexing的class
						incrementalClassFiles.add(classFilePath);
					}

				}
			}
		}

		private List<String> validDependencyLibs;

		public List<String> getValidDependencyLibs(){
			if ( validDependencyLibs != null ){
				return validDependencyLibs;
			}
			validDependencyLibs = new ArrayList<>();
			//有效依赖为[不是 compile | runtime only，不存在，非jar]
			for ( String dependencyLib : getAllDependencyLibs() ){
				String dependencyLibLowerCase = dependencyLib.toLowerCase();
				if ( !dependencyLibLowerCase.endsWith(".jar")
					|| dependencyLibLowerCase.endsWith("_resource.jar")
					|| dependencyLibLowerCase.endsWith("_compileonly.jar") ){
					continue;
				}
				File libFile = new File(dependencyLib);
				if ( !libFile.exists() ){
					//不是依赖库跳过
					continue;
				}
				try{ 
					new ZipFile(libFile);
				}
				catch (IOException e){
					throw new Error(dependencyLib + "不是一个zip文件");
				}
				validDependencyLibs.add(dependencyLib);
			}
			return validDependencyLibs;
		}

		// dex.zip转换器，即根目录下有classes%d.dex的zip文件的转换器
		ZipEntryTransformer.DexZipTransformer dexZipEntryTransformer = new ZipEntryTransformer.DexZipTransformer();
		// 从jar依赖添加资源的过滤器，
		ZipEntryTransformer.ZipResourceTransformer zipResourceZipEntryTransformer = new ZipEntryTransformer.ZipResourceTransformer();

		@Override
		public void packaging() throws Throwable{
			long now = nowTime();
			logDebug("开始dxing");
			List<String> classesDexZipList = getClassesDexZipList();
			logDebug("dxing共用时: " + (nowTime() - now) + "ms");

			now = nowTime();
			//Java工程
			if ( getOutFilePath().endsWith(".zip") ){
				packagingJavaProject(classesDexZipList);
			}
			else{
				//打包安卓项目
				packagingAndroidProject(classesDexZipList);
			}
			logDebug("打包共用时: " + (nowTime() - now) + "ms");
		}


		/**
		 * 优化对齐apk
		 */
		private void zipalignApk() throws Exception, Throwable{
			showProgress("ZeroAicy Zipalign APK ", 85);

			//优化前，未签名
			File unZipAlignedUnSignedApkFile = getUnZipAlignedUnSignedApkFile();
			//优化后，未签名
			File unSignedApkFile = getUnSignedApkFile();

			// zipalign命令路径
			String zipalignPath = getZipalignPath();
			//填充参数
			List<String> args = new ArrayList<>();
			args.add(zipalignPath);
			args.add("-p");
			args.add("-v");
			args.add("4");
			//输入
			args.add(unZipAlignedUnSignedApkFile.getAbsolutePath());
			//输出
			args.add(unSignedApkFile.getAbsolutePath());
			abcd.wf j62 = abcd.xf.j6(args, null, null, true, null, null);
			if ( j62.DW() != 0 ){
				throw new Exception(" zipalign Error: " + new String(j62.j6()));
			}
			//删除缓存
			unZipAlignedUnSignedApkFile.delete();

			//签名
			logDebug("开始Signing APK: ");
			long now = nowTime();
			//-zipaligned-unsigned 
			proxySign(unSignedApkFile, new File(getOutFilePath()));

			logDebug("Signing APK共用时: " + (nowTime() - now) + "ms");
		}

		public void proxySign(File unsignedApk, File signedApk) throws Throwable{

			showProgress("ZeroAicy Signing APK ", 90);
			if ( signedApk.exists() ){
				signedApk.delete();
			}
			String keystorePath = getSignaturePath();

			PrivateKey privateKey;
			X509Certificate certificate;
			//自定义签名文件是存在
			if ( new File(keystorePath).exists() ){
				//支持 .pk8 与 .x509.pem签名文件
				if ( keystorePath.endsWith(".x509.pem") 
					|| keystorePath.endsWith(".pk8") ){
					String keyNamePrefix;
					if ( keystorePath.endsWith(".x509.pem") ){
						keyNamePrefix = keystorePath.substring(0, keystorePath.length() - 9);
					}
					else{
						keyNamePrefix = keystorePath.substring(0, keystorePath.length() - 4);
					}

					InputStream cert = new FileInputStream(new File(keyNamePrefix + ".x509.pem"));
					certificate = (X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(cert);
					cert.close();

					InputStream key = new FileInputStream(new File(keyNamePrefix + ".pk8"));
					PKCS8Key pkcs8 = new PKCS8Key();
					pkcs8.decode(key);
					privateKey = pkcs8;
					cert.close();
				}
				else{
					//支持AIDE创建的.keystore签名文件
					//然而有兼容性问题，不支持其它工具生成的
					String password = getSignaturePassword();
					String alias = getSignatureAlias();
					String keyPass = getSignatureAliasPassword();

					FileInputStream keystoreIs = new FileInputStream(keystorePath);

					KeyStore ks = new com.aide.ui.build.android.JKSKeyStore();
					ks.load(keystoreIs, password.toCharArray());

					privateKey = (PrivateKey) ks.getKey(alias, keyPass.toCharArray());
					certificate = (X509Certificate) ks.getCertificateChain(alias)[0];

					keystoreIs.close();
				}
			}
			else{
				//为设置签名文件使用内置签名文件
				String keyName = "testkey";
				Class clazz = getClass();
				InputStream certInputStream = clazz.getResourceAsStream("/keys/" + keyName + ".x509.pem");
				certificate = (X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(certInputStream);
				certInputStream.close();

				InputStream pk8InputStream = clazz.getResourceAsStream("/keys/" + keyName + ".pk8");
				PKCS8Key pkcs8 = new PKCS8Key();
				pkcs8.decode(pk8InputStream);
				privateKey = pkcs8;
				certInputStream.close();
			}

			SharedPreferences defaultSp = ZeroAicySetting.getDefaultSp();
			boolean isapksignv1 = defaultSp.getBoolean("v1", true);
			boolean isapksignv2 = defaultSp.getBoolean("v2", true);
			boolean isapksignv3 = defaultSp.getBoolean("v3", true);

			ApkSigner.SignerConfig signerConfig = 
				new ApkSigner.SignerConfig.Builder("ANDROID",  privateKey, Collections.singletonList(certificate))
				.build();

			new ApkSigner.Builder(Collections.singletonList(signerConfig))
				.setCreatedBy("Android Gradle 8.4")
				.setMinSdkVersion(getMinSdk())
				.setInputApk(unsignedApk)
				.setOutputApk(signedApk)
				.setV1SigningEnabled(isapksignv1)
				.setV2SigningEnabled(isapksignv2)
				.setV3SigningEnabled(isapksignv3)
				.build()
				.sign();
			//删除输入文件
			unsignedApk.delete();
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
			for ( String dexZipPath : dexZipPathList ){
				logDebug("Adding classes.dex from " + dexZipPath);
				packagingZipFile(dexZipPath, dexZipEntryTransformer, packagingZipOutput, false);
			}
		}
		/**
		 * 打包安卓即apk
		 */
		@Override
		public void packagingAndroidProject(List<String> dexZipPathList) throws Throwable{

			showProgress("构建APK", 80);
			//未zip优化，未签名
			File unZipAlignedUnSignedApkFile = getUnZipAlignedUnSignedApkFile();

			PackagingStream packagingZipOutput = new PackagingStream(new FileOutputStream(unZipAlignedUnSignedApkFile));
			//resources_ap_file
			String aAptResourceFilePath = getAAptResourceFilePath();
			logDebug("Adding aapt generated resources from " + aAptResourceFilePath);
			//打包resources.ap_ 文件
			packagingZipFile(aAptResourceFilePath, zipResourceZipEntryTransformer, packagingZipOutput, true);

			// 从文件夹添加原生库文件，
			ZipEntryTransformer.NativeLibFileTransformer nativeLibZipEntryTransformer = new ZipEntryTransformer.NativeLibFileTransformer(getAndroidFxtractNativeLibs());
			//从原生库目录添加so
			for ( String nativeLibDirPath : this.getNativeLibDirs() ){
				File nativeLibDirFile = new File(nativeLibDirPath);
				if ( nativeLibDirFile.exists() ){
					logDebug("从原生库添加" + nativeLibDirPath);
					packagingDirFile(nativeLibDirPath, nativeLibDirFile, nativeLibZipEntryTransformer, packagingZipOutput);
				}
			}
			//打包dex
			packagingDexs(dexZipPathList, packagingZipOutput);
			//打包源码目录下的资源
			packagingSourceDirsResource(packagingZipOutput);			
			//打包jar资源，优先第一个
			packagingJarResources(packagingZipOutput);
			//打包完成
			packagingZipOutput.close();

			//优化apk
			zipalignApk();
		}

		private void packagingSourceDirsResource(PackagingStream packagingZipOutput) throws IOException{

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
				logDebug("从源码目录添加资源" + sourceDir);
				packagingDirFile(sourceDir, new File(sourceDir), zipResourceZipEntryTransformer, packagingZipOutput);
			}
		}

		private void packagingJarResources(PackagingStream packagingZipOutput) throws IOException{
			String[] dependencyLibs = getAllDependencyLibs();
			if ( dependencyLibs == null ){
				return;
			}
			for ( String dependencyLibPath : dependencyLibs ){
				String dependencyLibLowerCase = dependencyLibPath.toLowerCase();
				if ( isCompileOnly(dependencyLibLowerCase) ){
					//仅编译文件不打包
					continue;
				}
				if ( isRuntimeOnly(dependencyLibLowerCase) ){
					logDebug("从仅打包依赖文件添加资源及classes.dex " + dependencyLibPath);
					this.packagingZipFile(dependencyLibPath, dexZipEntryTransformer, packagingZipOutput, false);
					continue;
				}

				logDebug("从JAR文件添加资源 " + dependencyLibPath);
				this.packagingZipFile(dependencyLibPath, zipResourceZipEntryTransformer, packagingZipOutput, false);
			}

		}

		/**
		 * 返回未优化,未签名时的apk输出文件，并清除已有缓存
		 */
		private File getUnZipAlignedUnSignedApkFile(){
			File unZipAlignedUnSignedApkFile = new File(getOutFilePath() + "-unzipaligned-unsigned");
			if ( unZipAlignedUnSignedApkFile.exists() ){
				unZipAlignedUnSignedApkFile.delete();
				//直接返回，文件存在所以父目录也存在
				return unZipAlignedUnSignedApkFile;
			}

			File parentFile = unZipAlignedUnSignedApkFile.getParentFile();
			if ( !parentFile.exists() ){
				parentFile.mkdirs();
			}
			return unZipAlignedUnSignedApkFile;
		}
		/**
		 * 返回未未签名apk的文件，并清除已有缓存
		 */
		private File getUnSignedApkFile(){
			File unSignedApkFile = new File(getOutFilePath() + "-unsigned");
			File parentFile = unSignedApkFile.getParentFile();
			//删除输出
			if ( unSignedApkFile.exists() ){
				unSignedApkFile.delete();
				return unSignedApkFile;
			}

			if ( !parentFile.exists() ){
				parentFile.mkdirs();
			}
			return unSignedApkFile;
		}



		public void packagingDirFile(String relativeRootDirFilePath, File file, ZipEntryTransformer transformer, PackagingStream packagingZipOutput) throws FileNotFoundException, IOException{
			if ( file.isDirectory() ){
				if ( file.isHidden() ){
					return;
				}
				for ( File childFile : file.listFiles() ){
					if ( childFile.isDirectory() && childFile.isHidden() ){
						return;
					}
					packagingDirFile(relativeRootDirFilePath, childFile, transformer, packagingZipOutput);
				}

			}
			else{
				String zipEntryName = getZipEntryName(file, relativeRootDirFilePath);
				ZipEntry zipEntry = new ZipEntry(zipEntryName);
				if ( transformer != null &&
					(zipEntry = transformer.transformer(zipEntry, packagingZipOutput)) == null ){
					//已被转换器过滤
					return;
				}

				if ( zipEntry.getMethod() == ZipEntry.STORED ){
					//未压缩时设置未压缩条目数据的CRC-32校验和
					zipEntry.setCrc(getFileCRC32(file));
				}

				zipEntry.setSize(file.length());
				zipEntry.setTime(file.lastModified());
				//添加zip条目
				packagingZipOutput.putNextEntry(zipEntry);

				BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(file));
				streamTransfer(inputStream, packagingZipOutput);
				inputStream.close();

				packagingZipOutput.closeEntry();
			}

		}

		public long getFileCRC32(File file) throws IOException{
			CRC32 crc = new CRC32();
			BufferedInputStream bufferedInputStream = new BufferedInputStream(new FileInputStream(file));
			byte[] data = new byte[4096];
			int count;
			while ( (count = bufferedInputStream.read(data)) > 0 ){
				crc.update(data, 0, count);
			}
			bufferedInputStream.close();
			return crc.getValue();
		}


		private String getZipEntryName(File file, String relativeRootDirFilePath){
			String filePath = file.getAbsolutePath();
			int index = relativeRootDirFilePath.length();
			int length =filePath.length();
			while ( index < length 
				   && filePath.charAt(index) == '/' ){
				index ++;
			}
			return filePath.substring(index);
		}



		/**
		 * 不能添加class文件
		 * 但是 
		 */
		private void packagingZipFile(String zipFilePath, ZipEntryTransformer transformer, PackagingStream packagingZipOutput, boolean followZipEntryMethod) throws IOException{
			if ( !new File(zipFilePath).exists() ){
                AppLog.gn("Zip file not found: " + zipFilePath);
                return;
            }
			ZipInputStream zipFileInput = null;
			try{
				zipFileInput = new ZipInputStream(new FileInputStream(zipFilePath));
				ZipEntry originalZipEntry;
				while ( (originalZipEntry = zipFileInput.getNextEntry()) != null ){
					ZipEntry newZipEntry;
					if ( transformer != null ){
						newZipEntry = transformer.transformer(originalZipEntry, packagingZipOutput);
						//转换器过滤此条目
						if ( newZipEntry == null ){
							continue;
						}
					}
					else{
						newZipEntry = originalZipEntry;
					}

					if ( newZipEntry == originalZipEntry ){
						newZipEntry = new ZipEntry(originalZipEntry.getName());
					}

					if ( followZipEntryMethod 
						&& originalZipEntry.getMethod() != -1 ){
						if ( originalZipEntry.getMethod() == ZipEntry.STORED ){
							newZipEntry.setCrc(originalZipEntry.getCrc());
							newZipEntry.setSize(originalZipEntry.getSize());
						}
						newZipEntry.setMethod(originalZipEntry.getMethod());
					}

					packagingZipOutput.putNextEntry(newZipEntry);
					streamTransfer(zipFileInput, packagingZipOutput);
					//Entry写入完成
					packagingZipOutput.closeEntry();
				}
			}
			finally{
				if ( zipFileInput != null ) zipFileInput.close();
			}
		}

		private void streamTransfer(InputStream bufferedInputStream, OutputStream packagingZipOutput) throws IOException{
			byte[] data = new byte[4096];
			int read;
			while ( (read = bufferedInputStream.read(data)) > 0 ){
				packagingZipOutput.write(data, 0, read);
			}
		}
		private void logDebug(String msg){
			Log.i(TAG, msg);
		}
		private long nowTime(){
			return System.currentTimeMillis();
		}
	}

}
