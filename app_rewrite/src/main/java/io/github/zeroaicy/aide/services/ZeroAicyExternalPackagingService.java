package io.github.zeroaicy.aide.services;

import com.aide.ui.util.FileSystem;
import android.text.TextUtils;
import com.aide.ui.build.packagingservice.ExternalPackagingService;
import com.android.apksig.ApkSigner;
import io.github.zeroaicy.aide.CompileOnlyJar;
import io.github.zeroaicy.util.Log;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import sun1.security.pkcs.PKCS8Key;
import com.aide.ui.util.FileSystem;

public class ZeroAicyExternalPackagingService extends ExternalPackagingServiceWrapper {

	public static final String ZeroAicyD8Dir = "/classesd8";
	//public static final String D8OutDir2 = "/classesd8-2";

	public class DexingThreadPoolExecutor extends ExternalPackagingServiceWrapper.DxThreadPoolExecutor{
		public DexingThreadPoolExecutor(){
			super();
		}
		@Override
		public ExternalPackagingServiceWrapper.DxThreadPoolExecutor.DxFromD8 getDxFromD8(String str, String[] strArr, String[] strArr2, String[] strArr3, String str2, String str3, String str4, String[] strArr4, String str5, String str6, String str7, String str8, String str9, boolean z, boolean z2, boolean z3){

			//str2 -> 输出目录
			// (build/bin | bin/[ debug | release]/dex)

			//str3 -> 默认jardex输出路径
			// bin/[ debug | release]/dex/jardex -> bin/[ debug | release]/dex/classesd8/jardex
			// build/bin/jardex -> build/bin/classesd8/jardex
			//重定向jardexDir
			str3 = str2 + ZeroAicyD8Dir + "/jardex";
			
			return new DexingFromD8(str, strArr, strArr2, strArr3, str2, str3, str4, strArr4, str5, str6, str7, str8, str9, z, z2, z3);
		}
		//c$d start
		public class DexingFromD8 extends ExternalPackagingServiceWrapper.DxThreadPoolExecutor.DxFromD8{
			public DexingFromD8(String j6, String[] DW, String[] FH, String[] Hw, String v5, String Zo, String VH, String[] gn, String u7, String tp, String EQ, String we, String J0, boolean J8, boolean Ws, boolean QX){
				super(j6, DW, FH, Hw, v5, Zo, VH, gn, u7, tp, EQ, we, J0, J8, Ws, QX);

				Log.d("this.j6", this.j6);

				Log.d("this.DW", Arrays.toString(this.DW));

				Log.d("this.FH", Arrays.toString(this.FH));

				Log.d("this.Hw", Arrays.toString(this.Hw));
				//j6 classesrelease class文件目录

				//this.FH Java目录(aidl|java|gen)

				//this.gn jniLibs目录
				//this.Hw libs(.jar)
				//this.DW 所有class输出路径

				//VH resources.ap_文件

				//v5 build/bin
				//this.Zo 主项目+build/bin/jardex -> build/bin/classesd8/jardex
				//      主项目+/bin/classes[release | debug]/dex/jardex -> /bin/classes[release | debug]/dex/classesd8/jardex 
			}
			
			public String getOutFilePath(){
				return this.u7;
			}
			@Override
			public void packaging() throws Throwable{

				long now = nowTime();
				printlnDebug("开始dxing");
				List<String> runD8Dexing = runD8Dexing();
				printlnDebug("dxing共用时: " + (nowTime() - now) + "ms");

				//Java工程
				if ( getOutFilePath().endsWith(".zip") ){
					printlnDebug("Java工程 " + getOutFilePath());
					packagingJavaProject(runD8Dexing);
					return;
				}

				//输出路径
				File apk_file = new File(getOutFilePath());
				//打包
				printlnDebug("开始打包: " + apk_file.getAbsolutePath());
				now = nowTime();
				//打包安卓项目
				packagingAndroidProject(runD8Dexing);
				printlnDebug("打包共用时: " + (nowTime() - now) + "ms");


				//Zipalign Apk
				a8("ZeroAicy Zipalign APK ", 90);
				printlnDebug("开始Zipalign APK: ");
				now = nowTime();
				zipalignApk();
				printlnDebug("Zipalign APK共用时: " + (nowTime() - now) + "ms");

			}

			public void zipalignApk() throws Throwable{
				//输入Apk 未优化, 未签名
				File unsignedFile = new File(getOutFilePath() + "-unsigned");
				
				//优化后，未签名
				File zipalignedFile = new File(getOutFilePath() + "-zipaligned-unsigned");
				
				String zipalignPath = ZeroAicyExternalPackagingService.this.getApplicationInfo().nativeLibraryDir + "/libzipalign.so";

				List<String> args = new ArrayList<>();
				args.add(zipalignPath);
				
				args.add("-p");
				args.add("-v");
				args.add("4");
				args.add(unsignedFile.getAbsolutePath());
				args.add(zipalignedFile.getAbsolutePath());
				
				abcd.wf j62 = abcd.xf.j6(args, null, null, true, null, null);
				if( j62.DW() != 0 ){
					throw new Exception(" zipalign Error: " + String.valueOf( j62.j6()));
				}
				//删除输入
				unsignedFile.delete();
				
				//签名
				printlnDebug("开始Signing APK: ");
				long now = nowTime();
				
				//-zipaligned-unsigned 
				this.proxySign(zipalignedFile, new File(getOutFilePath()) );
				
				printlnDebug("Signing APK共用时: " + (nowTime() - now) + "ms");
				
			}

			//签名, 默认
			public void proxySign() throws Throwable{
				File outApkFile = new File(getOutFilePath());
				File unsignedFile = new File(getOutFilePath() + "-unsigned");
				proxySign(unsignedFile, outApkFile);
			}
			
			public void proxySign(File unsignedApk, File signedApk) throws Throwable{

				//File outApkFile = new File(getOutFilePath());
				//File unsignedFile = new File(getOutFilePath() + "-unsigned");

				a8("ZeroAicy Signing APK ", 90);


				if ( signedApk.exists() ){
					signedApk.delete();
				}
				String keystore = this.tp;

				//this.tp自定义签名文件
				// ag.sG == !new File(str).isFile()
				//Log.d(keystore, "ag.sG(keystore)=", ag.sG(keystore));

				PrivateKey privateKey;
				X509Certificate certificate;
				//自定义签名文件是存在
				if ( FileSystem.sG(keystore) ){
					//支持 .pk8 与 .x509.pem签名文件
					if ( keystore.endsWith(".x509.pem") || keystore.endsWith(".pk8") ){

						String keyNamePrefix = keystore.substring(0, keystore.lastIndexOf('.'));

						InputStream cert = new FileInputStream(new File(keyNamePrefix + ".x509.pem"));
						certificate = (X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(cert);
						cert.close();

						InputStream key = new FileInputStream(new File(keyNamePrefix + ".pk8"));
						PKCS8Key pkcs8 = new PKCS8Key();
						pkcs8.decode(key);
						privateKey = pkcs8;
						cert.close();
					}else{
						//支持AIDE创建的.keystore签名文件
						//然而有兼容性问题，不支持其它工具生成的
						String password = this.EQ;
						String alias = this.we;
						String keyPass = this.J0;

						FileInputStream keystoreIs = new FileInputStream(keystore);

						KeyStore ks = new com.aide.ui.build.android.JKSKeyStore();
						ks.load(keystoreIs, password.toCharArray());

						privateKey = (PrivateKey) ks.getKey(alias, keyPass.toCharArray());
						certificate = (X509Certificate) ks.getCertificateChain(alias)[0];

						keystoreIs.close();
					}
				}else{
					//为设置签名文件使用内置签名文件
					String keyName = "testkey";
					Class clazz = getClass();
					InputStream cert = clazz.getResourceAsStream("/keys/" + keyName + ".x509.pem");

					certificate = (X509Certificate) CertificateFactory.getInstance("X.509").generateCertificate(cert);
					cert.close();

					InputStream key = clazz.getResourceAsStream("/keys/" + keyName + ".pk8");
					PKCS8Key pkcs8 = new PKCS8Key();
					pkcs8.decode(key);
					privateKey = pkcs8;
					cert.close();
				}

				boolean isapksignv1 = getSp().getBoolean("v1", true);

				boolean isapksignv2 = getSp().getBoolean("v2", true);

				boolean isapksignv3 = getSp().getBoolean("v3", true);

				//boolean isapksignv4 = getSp().getBoolean("v4", true);

				ApkSigner.SignerConfig signerConfig = 
					new ApkSigner.SignerConfig.Builder("ANDROID", 
													   privateKey, 
													   Collections.singletonList(certificate))
					.build();

				//builder1.setMinSdkVersion(26);
				//builder1.setMinSdkVersion(FileOptions.getMinSdkVersion(unsignedFile.getAbsolutePath()));
				//builder1.setV4SigningEnabled(isapksignv4);

				new ApkSigner.Builder(Collections.singletonList(signerConfig))
					.setCreatedBy("Android Gradle 8.4")
					.setInputApk(unsignedApk)
					.setOutputApk(signedApk)
					.setV1SigningEnabled(isapksignv1)
					.setV2SigningEnabled(isapksignv2)
					.setV3SigningEnabled(isapksignv3)
					.build()
					.sign();

				unsignedApk.delete();
			}


			private ExternalPackagingService.b supportResourceJar = new ExternalPackagingService.b(){
				//转换zipEntryFileName
				@Override
				public String DW(String zipEntryFileName){
					//不做处理
					return zipEntryFileName;
				}
				//过滤zipEntryFileName
				@Override
				public boolean j6(String zipEntryFileName){
					//只要是_resource.jar的都添加
					return true;
				}
			};

			//打包Apk文件
			@Override
			public void v5(List<String> dexZipPaths){
				try{
					a8("构建APK", 80);
					File unsignedApkFile = new File(getOutFilePath() + "-unsigned");
					File unsignedApkParentFile = unsignedApkFile.getParentFile();

					if ( !unsignedApkParentFile.exists() ){
						unsignedApkParentFile.mkdirs();
					}
					if ( unsignedApkFile.exists() ){
						unsignedApkFile.delete();
					}

					FileOutputStream unsignedApkOutput = new FileOutputStream(unsignedApkFile);

					ExternalPackagingService.f unsignedApkZip = new ExternalPackagingService.f(unsignedApkOutput);
					//resources_ap_file
					printlnDebug("Adding aapt generated resources from " + this.VH);
					
					this.DW(this.VH, null, unsignedApkZip, true);

					for ( int count = 0; count < dexZipPaths.size(); count++ ){
						//xxx.dex.zip
						String filePath = dexZipPaths.get(count);

						printlnDebug("Adding classes.dex from " + filePath);

						String zipEntryFileName = count == 0 ? "classes.dex" : "classes" + (count + 1) + ".dex";

						this.FH(zipEntryFileName, null, new File(filePath), unsignedApkZip, null, false);
					}
					//从源码目录添加
					for ( String source_dir : this.getSourcePaths() ){
						//FH -> Java源码目录
						printlnDebug("Adding resources from source dir " + source_dir);
						File javaSourceDir = new File(source_dir);
						if ( javaSourceDir.exists() ){
							//从源码目录添加资源文件到apk
							j6(javaSourceDir, javaSourceDir, unsignedApkZip, new ExternalPackagingService.e(), false);
						}
					}

					//从jniLibs目录添加
					for ( String projectPath : this.getProjectPaths() ){
						File projectFile = new File(projectPath);
						if ( projectFile.exists() ){
							printlnDebug("Adding native libs from " + projectPath);

							j6(unsignedApkFile.getParentFile(), projectFile, unsignedApkZip, new DxFromD8.FilterELFFile(), false);
						}
					}
					//this.Hw所有jar
					for ( String jarFilePath : this.getJarDependencys() ){
						printlnDebug("Adding resources from JAR " + jarFilePath);

						//_resource.jar 只打包
						ExternalPackagingService.b filter;
						if ( jarFilePath.toLowerCase().endsWith("_resource.jar") ){
							filter = this.supportResourceJar;
						}else{
							//过滤jar中.class与.java等文件
							filter = new ExternalPackagingService.d();
						}
						DW(jarFilePath, filter, unsignedApkZip, false);
					}
					unsignedApkZip.close();
					unsignedApkOutput.close();
				}
				catch (Throwable e){
					throw new RuntimeException(e);
				}
			}


			@Override
			public boolean addFileToZipOut(String zipEntryFileName, File parentFile, File file, ExternalPackagingService.f apkZipOutputStream, ExternalPackagingService.b filter, boolean z){
				//兼容classezip
				return addDexZipFileToZipOut(file, zipEntryFileName, apkZipOutputStream);

			}

			private int classesCountDex = 1;
			private boolean addDexZipFileToZipOut(File file, String zipEntryFileName, ExternalPackagingService.f apkZipOutputStream){
				//zipEntryFileName 为 classes开头，.dex结尾
				//file实际是zip
				//
				if ( ! (file.exists()
					&& file.isFile()
					&& zipEntryFileName.startsWith("classes") 
					&&  zipEntryFileName.endsWith(".dex") 
					&& file.getName().endsWith("zip")) ){
					//d8因多dex情况而输出zip
					//zipEntryFileName为apk内的名称但不能以此为准
					//因为file这个zip包含多个dex
					return false;
				}

				try{
					//dex实际为zip
					//Adding classes .dex  from zip "
					ZipInputStream dexZip = new ZipInputStream(new FileInputStream(file));
					//从zip中添加dex
					for ( ZipEntry entry = dexZip.getNextEntry(); entry != null; entry = dexZip.getNextEntry() ){

						String name = entry.getName();

						if ( entry.isDirectory() || name.contains("/") ){
							//过滤文件夹和非根目录文件
							continue;
						}
						if ( name.endsWith(".dex") ){
							String dexEntryName = classesCountDex > 1 ? "classes" + classesCountDex + ".dex" : "classes.dex";
							while ( apkZipOutputStream.j6(dexEntryName) && classesCountDex < apkZipOutputStream.WB.size() + 1 ){
								classesCountDex++;
								dexEntryName = "classes" + classesCountDex + ".dex";
							}
							apkZipOutputStream.putNextEntry(new ZipEntry(dexEntryName));

							BufferedInputStream classesDexInputStream = new BufferedInputStream(dexZip);
							//从input读取写入out且不关闭input
							com.aide.common.StreamUtilities.VH(classesDexInputStream, apkZipOutputStream, false);
							apkZipOutputStream.closeEntry();
						}
					}
					dexZip.close();
					return true;
				}
				catch (IOException e){
					throw new RuntimeException(e);
				}
			}

			//Java工程打包
			@Override
			public void packagingJavaProject(List<String> dexPathList){

				try{
					ExternalPackagingService.f packOutput = new ExternalPackagingService.f(new FileOutputStream(getOutFilePath()));

					for ( int dexCount = 0; dexCount < dexPathList.size(); dexCount++ ){
						String dexPath = dexPathList.get(dexCount);

						if ( dexPath.endsWith(".dex.zip") ){

							FH(dexCount == 0 ? "classes.dex" : "classes" + (dexCount + 1) + ".dex", null, new File(dexPath), packOutput, null, false);

							continue;
						}

						File dexFile = new File(dexPath);
						j6(dexFile.getParentFile(), dexFile, packOutput, null, false);
					}

					//所有依赖库
					String[] allLibraries = getJarDependencys();

					if ( allLibraries != null ){
						for ( String librariePath : allLibraries ){
							printlnDebug("Adding resources from JAR " + librariePath);
							this.DW(librariePath, new ExternalPackagingService.d(), packOutput, false);
						}
					}

					packOutput.close();
				}
				catch (Throwable e){
					if ( e instanceof RuntimeException ){
						throw (RuntimeException)e;
					}
					throw new RuntimeException(e);
				}

			}


			//jar的dex缓存一般返回(jar.dex.zip)
			@Override
			public String getDexingJarPath(String jarPath){
				String dexingJarOutPath = getDexingJarOutPath(jarPath);

				String jarDexZipName = new File(jarPath).getName() + ".dex.zip";

				if ( dexingJarOutPath != null ){
					return dexingJarOutPath + "/" +  jarDexZipName;
				}
				return this.Zo + "/" + jarDexZipName;
			}
			
			//获取dexing jar 输出文件夹路径 不包含文件名
			@Override
			public String getDexingJarOutPath(String jarPath){
				try{
					if ( jarPath == null ){
						return null;
					}
					File jarFile = new File(jarPath);

					//maven下的依赖库 -> bin/jardex
					if ( isAarClassJar(jarFile)
						|| isUserM2repositories(jarPath) 
						//AIDE默认maven仓库路径
						|| jarPath.startsWith(getNoBackupDir() + "/.aide/maven") ){
						// aar库 自定义maven仓库 默认仓库
						// 则在jar同级目录的 /bin/jardex
						return jarFile.getParentFile().getPath() + "/bin/jardex";
					}
					//非maven依赖库
					//这this.Zo是被修改的
					//在/jardex前插入了 /classesd8
					return this.Zo;

				}
				catch (Throwable e){
					if ( e instanceof RuntimeException ){
						throw (RuntimeException)e;
					}
					throw new RuntimeException(e);
				}
			}
			
			//是否是AAr依赖的"classes.jar"
			private boolean isAarClassJar(File jarFile){
				return jarFile.getParentFile().getName().endsWith(".aar") 
					&& "classes.jar".equals(jarFile.getName());
			}
			//是否是在自定义maven路径
			private boolean isUserM2repositories(String jarPath){
				String userM2repositories = defaultSharedPreferences.getString("user_m2repositories", null);
				return (!TextUtils.isEmpty(userM2repositories) 
					&& jarPath.startsWith(userM2repositories));
			}


			//dexing 返回被打包的dex列表
			@Override
			public List<String> runD8Dexing() throws Throwable{
				if ( !Thread.interrupted() ){
					a8("Run D8 Dexing", 60);
					File jarDexDir = new File(this.Zo);
					if ( !jarDexDir.exists() && !jarDexDir.mkdirs() ){
						throw new IOException("Could not create DX JAR dir " + this.Zo);
					}

					long dexingStartTime = nowTime();
					//增量 dexing class文件
					List<String> dexingClassFiles = new ArrayList<>();
					//过滤类名重复class
					Map<String, String> allClassFileMap = new HashMap<>();
					//查找所有需要dexing的class以及所有AIDE生成的class文件
					findClassFiles(dexingClassFiles, allClassFileMap);

					List<String> dexingLibraries = new ArrayList<>();
					List<String> allJarLibraries = new ArrayList<>();
					//填充依赖
					Ws(getJarDependencys(), dexingLibraries, allJarLibraries, !isBuildRefresh());
					
					filterResourceJar(dexingLibraries, allJarLibraries);
					filterCompileOnlyJar(dexingLibraries, allJarLibraries);
					
					//dexing class文件
					a8("Run D8 Dexing - Classes", 60);
					if ( !dexingClassFiles.isEmpty() ){
						//没有考虑 aide-debug的class
						dexingClassFilesFromD8(getOutDir() + ZeroAicyD8Dir + "/classes_main", dexingClassFiles);
					}

					//增量dexing库
					if ( !dexingLibraries.isEmpty() ){
						//System.out.println("dexingJarLibraries: " + dexingLibraries);
						//此处应有d8
						dexingLibraries(dexingLibraries);
					}

					a8("Run D8 Dexing - Merging", 60);

					//被打包的dex列表
					List<String> packedDexList = new ArrayList<>();
					//合并所有classes的dex
					String classesZipPath = getOutDir() + ZeroAicyD8Dir + "/classes.dex.zip";
					mergingClassDexs(classesZipPath, allClassFileMap.values());

					//合并libraries dex
					String librariesZipPath = getOutDir() + ZeroAicyD8Dir + "/libraries.dex.zip";
					mergingLibraries(librariesZipPath, allJarLibraries);

					packedDexList.add(classesZipPath);
					packedDexList.add(librariesZipPath);

					printlnDebug("Run D8 Dexing elapsed " + (nowTime() - dexingStartTime) + "ms");
					return packedDexList;
				}
				throw new InterruptedException();
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
				fillD8Args(argsList, true, false, user_androidjar, getJarDependencys(), outPath);
				//添加需要编译的jar
				argsList.addAll(dexingClassFiles);
				com.android.tools.r8.D8.main(argsList.toArray(new String[argsList.size()]));
			}


			@Override
			public String getDexingClassPath(String classPath){
				//this.DW 所有classes的输出路径
				for ( String classesDir : this.DW ){
					if ( classPath.startsWith(classesDir) ){
						//AIDE生成的class都dexing到主项目的classes_main下
						//方便d8统一合并
						classPath =  getOutDir() + ZeroAicyD8Dir + "/classes_main" + classPath.substring(classesDir.length());
						break;
					}
				}
				//在class文件同级目录下生成dex
				return classPath.substring(0, classPath.length() - 6) + ".dex";
			}

			private void filterResourceJar(List<String> dexingLibraries, List<String> allJarLibraries){
				CompileOnlyJar.filterCompileOnlyJar(dexingLibraries, "_resource.jar");
				CompileOnlyJar.filterCompileOnlyJar(allJarLibraries, "_resource.jar");
			}
			private void filterCompileOnlyJar(List<String> dexingLibraries, List<String> allJarLibraries){
				CompileOnlyJar.filterCompileOnlyJar(dexingLibraries);
				CompileOnlyJar.filterCompileOnlyJar(allJarLibraries);
			}

			//合并AIDE生成的class.dex
			private void mergingClassDexs(String outDexZipPath, Collection<String> classeFiles){
				List<String> classeDexFiles = new ArrayList<>();

				for ( String classFilePath : classeFiles ){
					classeDexFiles.add(getDexingClassPath(classFilePath));
				}

				File outDexZipFile = new File(outDexZipPath);
				if ( outDexZipFile.exists() ){
					boolean breaked = true;
					long outDexZipFileModified = outDexZipFile.lastModified();
					for ( String otherDex : classeDexFiles ){
						long lastModified = new File(otherDex).lastModified();
						if ( outDexZipFileModified < lastModified ){
							breaked = false;
							break;
						}
					}
					if ( breaked ){
						Log.d("缓存策略", "mergingClassDexs");
						return;
					}
				}
				outDexZipFile.delete();

				List<String> argsList  = new ArrayList<String>();

				//检查输出文件父目录
				File outDir = outDexZipFile.getParentFile();
				if ( !outDir.exists() ){
					outDir.mkdirs();
				}
				String user_androidjar = null; //defaultSharedPreferences.getString("user_androidjar", "/data/user/0/com.aide.ui/no_backup/.aide/android.jar");
				fillD8Args(argsList, false, true, user_androidjar, getJarDependencys(), outDexZipPath);

				//输入dexs
				argsList.addAll(classeDexFiles);

				com.android.tools.r8.D8.main(argsList.toArray(new String[argsList.size()]));
				printlnDebug("合并classdexs，已输出: " + outDexZipPath);
			}

			//这是不经常改变的
			private void mergingLibraries(String dexOutFile, List<String> librarieJars){

				List<String> librarieDexs = new ArrayList<>();
				for ( String librarieJar : librarieJars ){
					librarieDexs.add(getDexingJarPath(librarieJar));
				}
				File dexOutZipFile = new File(dexOutFile);
				if ( dexOutZipFile.exists() ){
					boolean breaked = true;
					long outDexZipFileModified = dexOutZipFile.lastModified();
					for ( String otherDex : librarieDexs ){
						long lastModified = new File(otherDex).lastModified();
						if ( outDexZipFileModified < lastModified ){
							//只要一个文件新于输出文件
							//则不跳过
							breaked = false;
							break;
						}
					}
					if ( breaked ){
						Log.d("缓存策略", "mergingLibraries");
						return;
					}
				}
				//输入文件最大修改的时间

				long inputMaxLastModified = 0;
				for ( String otherDex : librarieDexs ){

					long lastModified = new File(otherDex).lastModified();
					if ( lastModified > inputMaxLastModified ){
						inputMaxLastModified = lastModified;
					}
				}
				if ( dexOutZipFile.lastModified() > inputMaxLastModified ){
					//输入dex没有更新，过滤
					return;
				}
				List<String> argsList  = new ArrayList<String>();


				File outDir = dexOutZipFile.getParentFile();
				if ( !outDir.exists() ){
					outDir.mkdirs();
				}
				String user_androidjar = null;
				fillD8Args(argsList, false, true, user_androidjar, null, dexOutFile);

				//输入dexs
				argsList.addAll(librarieDexs);

				com.android.tools.r8.D8.main(argsList.toArray(new String[argsList.size()]));
				printlnDebug("合并other，已输出: " + dexOutFile);
			}
			private void dexingLibraries(List<String> dexingLibraries) throws Throwable{
				a8("Run D8 Dexing - Libraries", 60);
				long dexingStartTime = System.currentTimeMillis();
				
				for ( int i = 0;i < dexingLibraries.size(); i++ ){
					String jarFilePath = dexingLibraries.get(i);

					if ( jarFilePath.endsWith("_resource.jar") ){
						continue;
					}
					long startTime = nowTime();
					//可能有多dex
					dexingLibrarie(jarFilePath);
					printlnDebug("Run D8 Dexing " + new File(jarFilePath).getName() + " elapsed " + (System.currentTimeMillis() - startTime) + "ms");
				}
				printlnDebug("Run D8 Dexing JAR files elapsed " + (System.currentTimeMillis() - dexingStartTime) + "ms");
			}

			private boolean isBuildRefresh(){
				return this.J8;
			}

			private void findClassFiles(List<String> dexingClassFiles, Map<String, String> allClassFileMap){

				J0(new File(this.j6), dexingClassFiles, allClassFileMap, !this.J8);

				for ( String otherSourceDir : this.DW ){
					//其它class输出路径
					if ( !otherSourceDir.equals(this.j6) ){
						J0(new File(otherSourceDir), dexingClassFiles, allClassFileMap, !this.J8);
					}
				}
			}


			//dexing Libraries(jar)
			private void dexingLibrarie(String dexedLibrariePath) throws Throwable{
				if ( Thread.interrupted() ){
					throw new InterruptedException();
				}
				//输出.jar.dex.zip
				String jarDexZipOutPath = getDexingJarPath(dexedLibrariePath);

				File outFile = new File(jarDexZipOutPath);

				File outParentFile = outFile.getParentFile();
				if ( !outParentFile.exists() && !outParentFile.mkdirs() ){
					throw new IOException("Could not create DX JAR dir " + outParentFile.getPath());
				}

				File jarFile = new File(dexedLibrariePath);
				printlnDebug(dexedLibrariePath + " -> " + jarDexZipOutPath + "\n");

				if ( isResourceJar(jarFile) ){
					return;
				}
				if ( outFile.lastModified() >= jarFile.lastModified() ){
					return;
				}


				File dexZipTempFile = File.createTempFile(new File(dexedLibrariePath).getName(), ".dex.zip", outParentFile);
				//更新时间
				dexZipTempFile.setLastModified(System.currentTimeMillis());

				//临时文件退出时删除，异常关闭会失效
				//dexZipTempFile.deleteOnExit();
				//这种方式更保险, 实测无效
				//DeleteOnExitHook.add(dexZipTempFile.getAbsolutePath());

				List<String> argsList = new ArrayList<>();

				String user_androidjar = getUserAndroidJar();

				fillD8Args(argsList, false, false, user_androidjar, getJarDependencys(), dexZipTempFile.getAbsolutePath());

				//添加需要编译的jar
				argsList.add(dexedLibrariePath);
				try{
					com.android.tools.r8.D8.main(argsList.toArray(new String[argsList.size()]));
					//临时文件移动到实际输出文件
					dexZipTempFile.renameTo(outFile);
					//删除
					dexZipTempFile.delete();

					outFile.setLastModified(System.currentTimeMillis());

					if ( outFile.lastModified() < jarFile.lastModified() ){
						outFile.setLastModified(jarFile.lastModified() + 1000);
					}
				}
				finally{
					//临时文件用完时删除
					if ( dexZipTempFile.exists() ){
						printlnDebug(dexZipTempFile + "删除: " + dexZipTempFile.delete());						
					}
				}
				if ( Thread.interrupted() ){
					throw new InterruptedException();
				}

			}

			private boolean isResourceJar(File jarFile){
				return jarFile.getName().toLowerCase().endsWith("_resource.jar");
			}
		}
	}

	public ZeroAicyExternalPackagingService(){
		super();
		Log.d("ZeroAicyExternalPackagingService", "");
	}

	@Override
	public ExternalPackagingServiceWrapper.DxThreadPoolExecutor getDxThreadPoolExecutor(){
		return new DexingThreadPoolExecutor();
	}
}
