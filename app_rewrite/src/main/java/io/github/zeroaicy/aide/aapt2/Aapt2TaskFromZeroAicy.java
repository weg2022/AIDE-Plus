package io.github.zeroaicy.aide.aapt2;
import android.text.TextUtils;
import io.github.zeroaicy.aide.preference.ZeroAicySetting;
import io.github.zeroaicy.util.ContextUtil;
import io.github.zeroaicy.util.FileUtil;
import io.github.zeroaicy.util.MD5Util;
import io.github.zeroaicy.util.reflect.ReflectPie;
import java.io.File;
import java.io.FileFilter;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import com.aide.ui.build.android.AaptService$b;
import io.github.zeroaicy.aide.utils.AndroidManifestParser;
import java.util.Set;
import java.util.HashSet;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.io.FileInputStream;
import java.util.zip.Deflater;
import java.util.zip.CRC32;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;

public class Aapt2TaskFromZeroAicy{

	private static final String TAG = "aapt2";

	public static boolean fileExists(String filePath){
		if ( TextUtils.isEmpty(filePath) ) return false;

		return new File(filePath).exists();
	}

	//com.aide.ui.build.android.AaptService.b b;
	private static String aapt$b = "com.aide.ui.build.android.AaptService$b";
	private static void streamTransfer(InputStream bufferedInputStream, OutputStream outputStream) throws IOException{
		byte[] data = new byte[4096];
		int read;
		while ( (read = bufferedInputStream.read(data)) > 0 ){
			outputStream.write(data, 0, read);
		}
	}
	public static long getFileCRC32(File file) throws IOException{
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

	public static AaptService$b proxyAapt(Object aapt$c) throws Exception{
		long oldTime = System.currentTimeMillis();
		AaptServiceArgs aaptServiceArgs = new AaptServiceArgs(aapt$c);
		AaptService$b proxyAapt = null;
		try{
			proxyAapt = proxyAapt2(aaptServiceArgs);

		}
		catch (Throwable e){
			e.printStackTrace(aaptServiceArgs.log);
		}

		float diffTime = System.currentTimeMillis() - oldTime;
		aaptServiceArgs.log.println("aapt2 总耗时 " + diffTime / 1000.0f + "s");

		return proxyAapt;
	}

	public static AaptService$b proxyAapt2(AaptServiceArgs aaptServiceArgs) throws Exception{

		PrintStream log = aaptServiceArgs.log;
		//构建刷新
		if ( aaptServiceArgs.isBuildRefresh ){
			//AIDE
			aaptServiceArgs.buildRefresh();
		}
		// 合并清单
		merged:{
			//AaptService$b
			AaptService$b mergedErrorInfo = aaptServiceArgs.mergedAndroidManifestxml();
			if ( mergedErrorInfo != null && mergedErrorInfo.DW != null ){
				log.println("merged error " + mergedErrorInfo.DW);
				return mergedErrorInfo;
			}
			else{
				log.println("merged: " + mergedErrorInfo.DW);
			}
		}
		//编译
		compile:{
			Map<String, String> allResourceMap = aaptServiceArgs.allResourceMap;
			//记录有效的flat缓存目录
			Set<String> flatCacheDirs = new HashSet<>();

			//顺序有问题(resCompiledSet)
			for ( String resDir : allResourceMap.keySet() ){
				if ( !new File(resDir).exists() ){
					continue;
				}
				//实现更细的颗粒度
				String aapt2ResCacheDir = getAapt2ResCacheDir(aaptServiceArgs, resDir);
				File aapt2ResCacheDirFile = new File(aapt2ResCacheDir);

				//记录使用的flat缓存目录
				flatCacheDirs.add(aapt2ResCacheDir);

				//兼容旧版本
				if ( aapt2ResCacheDirFile.isFile() ){
					aapt2ResCacheDirFile.delete();
				}
				if ( !aapt2ResCacheDirFile.exists() ){
					//保证输出路径是文件夹
					aapt2ResCacheDirFile.mkdirs();
				}

				AaptService$b aaptError = null;

				if ( !aapt2ResCacheDirFile.exists() 
					|| FileUtil.findFile(aapt2ResCacheDirFile, null).isEmpty() ){
					//全量编译
					aaptError = fullCompile(aaptServiceArgs, resDir, aapt2ResCacheDir);
					//全量编译 合并zip[二级缓存]
					String flatsZipFile = getMergedCacheDirFile(aaptServiceArgs, resDir);


					ZipOutputStream out = new ZipOutputStream(new FileOutputStream(flatsZipFile));
					out.setLevel(Deflater.NO_COMPRESSION);

					File[] flatFiles = aapt2ResCacheDirFile.listFiles();
					if ( flatFiles != null ){
						for ( File file : flatFiles ){
							ZipEntry zipEntry = new ZipEntry(file.getName());
							zipEntry.setMethod(ZipEntry.STORED);
							zipEntry.setSize(file.length());
							zipEntry.setCrc(getFileCRC32(file));


							out.putNextEntry(zipEntry);

							FileInputStream input = new FileInputStream(file);
							streamTransfer(input, out);
							input.close();
							out.closeEntry();
						}
					}
					out.close();

					//添加输出
					aaptServiceArgs.resCompiledSet.add(flatsZipFile);

				}
				else{
					//增量编译
					aaptError = incrementalCompile(aaptServiceArgs, resDir, aapt2ResCacheDir);

				}

				if ( aaptError != null ){
					return aaptError;
				}
			}
			//删除无用的flat缓存目录
			File[] flatDirs = aaptServiceArgs.getCompileDirFile().listFiles();
			if ( flatDirs != null ){
				for ( File flatDir : flatDirs ){
					if ( !flatCacheDirs.contains(flatDir.getAbsolutePath()) ){
						FileUtil.deleteFolder(flatDir);
					}
				}						
			}
		}

		link:{

			MainProject:{
				List<String> linkFilesList = new ArrayList<>();

				List<String> assetsList = new ArrayList<>();
				//u7 -> this.VH
				List<String> u7 = aaptServiceArgs.assetsList;
				if ( u7 != null || !u7.isEmpty() ){
					assetsList.addAll(u7);
				}
				String mainProjectGenDir = aaptServiceArgs.mainProjectGenDir;
				List<String> resDirs = aaptServiceArgs.genResDirsMap.get(mainProjectGenDir);

				//优先添加主项目res的缓存文件
				for ( String mainProjectResPath : resDirs ){
					String aapt2ResCacheFile = getMergedCacheDirFile(aaptServiceArgs, mainProjectResPath);
					if ( aaptServiceArgs.resCompiledSet.remove(aapt2ResCacheFile) ){
						linkFilesList.add(aapt2ResCacheFile);
					}
				}
				//添加已编译的缓存路径
				linkFilesList.addAll(aaptServiceArgs.resCompiledSet);

				//反序 aapt2 link -R 末尾优先

				Collections.reverse(linkFilesList);
				aaptServiceArgs.resCompiledSet.addAll(linkFilesList);


				//tp -> gn
				String resourcesApPath = aaptServiceArgs.resourcesApPath;

				String aapt_rules = aaptServiceArgs.buildBin + "/intermediates/aapt_rules.txt";
				String rTxt = aaptServiceArgs.buildBin + "/intermediates/R.txt";

				AaptService$b aaptError = link35(aaptServiceArgs, linkFilesList, assetsList, mainProjectGenDir, resourcesApPath, false, aapt_rules, rTxt);

				if ( aaptError != null ){
					return aaptError;
				}
			}
		}




		deleteCache:{
			//删除无效缓存
			File[] aapt2Cache = aaptServiceArgs.getMergedDirFile().listFiles();
			for ( File file : aapt2Cache ){
				if ( !aaptServiceArgs.resCompiledSet.contains(file.getAbsolutePath()) ){
					FileUtil.deleteFolder(file);
				}
			}
		}

		//需要将主项目R.java复制到主项目gen--以不同的包名
		long genRjavaTimeMillis = System.currentTimeMillis();
		//复制R.java到其它包
		Map<String, String> genPackageNameMap = aaptServiceArgs.genPackageNameMap;

		//主项目gen目录
		String mainProjectGenDir = aaptServiceArgs.mainProjectGenDir;
		//主项目包名: 
		String mainProjectPackageName = genPackageNameMap.get(mainProjectGenDir);

		//主项目R.java相对gen路径
		String mainRJavaChildPath = mainProjectPackageName.replace('.', '/') + "/R.java";

		//主项目R.java文件
		File mainRJavaFile = new File(mainProjectGenDir, mainRJavaChildPath);

		//R.java的内容按行储存
		List<String> rJavaLinelist = aaptServiceArgs.listLine(mainRJavaFile);
		// R.java包名所在行
		String packageNameLine = null;
		//R.java包名所在行数
		int packageNameLineCount = -1;

		for ( int i = 0; i < rJavaLinelist.size(); i++ ){
			String line = rJavaLinelist.get(i);
			if ( line.contains(mainProjectPackageName) ){
				packageNameLine = line;
				packageNameLineCount = i;
				break;
			}
		}

		//消除 final
		for ( int i = 0; i < rJavaLinelist.size(); i++ ){
			rJavaLinelist.set(i, rJavaLinelist.get(i).replace(" final int ", " int "));
		}

		if ( packageNameLineCount < 0 || TextUtils.isEmpty(packageNameLine) ){
			return new AaptService$b("R.java 生成错误，没有找到Rpackage");
		}

		for ( Map.Entry<String, String> subProjectGen : genPackageNameMap.entrySet() ){
			String subGenDirPath = subProjectGen.getKey();
			if ( mainProjectPackageName.equals(subProjectGen.getValue()) ){
				// 主进程包名跳过
				log.println("跳过主项目 " + mainProjectPackageName);

				continue;
			}
			//子项目包名
			String subProjectPackageName = genPackageNameMap.get(subGenDirPath);
			//子项目R.java相对gen路径
			String subRJavaChildPath = subProjectPackageName.replace('.', '/') + "/R.java";

			//向子项目gen目录写入
			File subRJavaFile = new File(subProjectGen.getKey(), subRJavaChildPath);
			rJavaLinelist.set(packageNameLineCount, packageNameLine.replace(mainProjectPackageName, subProjectPackageName));

			AaptServiceArgs.writeLines(subRJavaFile, rJavaLinelist);

			//向主项目gen目录写入
			subRJavaFile = new File(mainProjectGenDir, subRJavaChildPath);
			AaptServiceArgs.writeLines(subRJavaFile, rJavaLinelist);
		}
		aaptServiceArgs.log.println("aapt2 生成R耗时: " + (System.currentTimeMillis() - genRjavaTimeMillis) + "ms");

		if ( ZeroAicySetting.isEnableViewBinding() ){
			try{
				// viewbinding
				GenerateViewBindingTask.run(aaptServiceArgs.mainProjectResPath, mainProjectGenDir, mainProjectPackageName, ZeroAicySetting.isViewBindingAndroidX());
			}
			catch (Throwable e){
				log.println("ViewBindingTask：");
				e.printStackTrace(log);
				log.println();
			}
		}

		//生成主项目DataBinderMapperImpl
		//生成Binding Java
		aaptServiceArgs.getDataBindingBuilder().generateJava();

		// 生成BuildConfig.java
		// R怎么只包含自己的资源呢🤔🤔🤔🤔
		aaptServiceArgs.generateBuildConfigJava();

		return new AaptService$b(false);
	}


	// 半成品apk
	public static AaptService$b link35(AaptServiceArgs aaptServiceArgs, List<String> resourceList, List<String> assetsList,  String genDir, String outputPath, boolean isNonFinalIds , String proguardPath, String rTxtPath) throws Exception{

		String androidJar = aaptServiceArgs.androidJar;

		//merged
		String androidManifestXml = getAndroidManifestXml(aaptServiceArgs, genDir);

		AndroidManifestParser androidManifestRead = AndroidManifestParser.get(androidManifestXml);

		final int min;
		try{
			min = Integer.parseInt(androidManifestRead.getMinSdkVersion());
		}
		catch (Throwable e){
			min = aaptServiceArgs.defaultMinSdk;
		}

		final int target;
		try{
			target = Integer.parseInt(androidManifestRead.getTargetSdkVersion());
		}
		catch (Throwable e){
			target = aaptServiceArgs.defaultTargetSdk;
		}

		/*****/
		List<String> args = new ArrayList<>();
		args.add(aaptServiceArgs.getAapt2Path());
		args.add("link");

		args.add("-I");
		args.add(androidJar);
		args.add("--allow-reserved-package-id");
		args.add("--no-version-vectors");
		args.add("--no-version-transitions");
		args.add("--auto-add-overlay");
		if ( min <= 0 ){
			min = 21;
		}
		if ( target <= 0 ){
			target = 28;
		}

		args.add("--min-sdk-version");
		args.add(String.valueOf(min));
		args.add("--target-sdk-version");
		args.add(String.valueOf(target));

		if ( !TextUtils.isEmpty(proguardPath) ){
			args.add("--proguard");
			args.add(proguardPath);
		}
		if ( !TextUtils.isEmpty(genDir) ){
			args.add("--java");
			args.add(genDir);
		}
		//子项目
		if ( isNonFinalIds ){
			args.add("--non-final-ids");
		}
		if ( !TextUtils.isEmpty(androidManifestXml) ){
			args.add("--manifest");
			args.add(androidManifestXml);
		}
		if ( !TextUtils.isEmpty(outputPath) ){
			//输出文件
			args.add("-o");
			args.add(outputPath);
		}

		if ( !TextUtils.isEmpty(rTxtPath) ){
			args.add("--output-text-symbols");
			args.add(rTxtPath);
		}

		if ( !assetsList.isEmpty() ){
			for ( String s : assetsList ){
				File f = new File(s);
				if ( f.exists() ){
					args.add("-A");
					args.add(f.getAbsolutePath());
				}
			}
		}

		// 自定义命令的实现
//		if (AdvancedSetting.isEnableCustomCommand()) {
//			String[] commandLines = AdvancedSetting.getCustomCommands().split("\n");
//			if (commandLines != null) {
//				args.addAll(Arrays.asList(commandLines));
//			}
//		}

		if ( !resourceList.isEmpty() ){
			for ( String path : resourceList ){
				File flatsZipFile = new File(path);
				if ( !flatsZipFile.exists() ){
					continue;
				}
				args.add("-R");
				args.add(flatsZipFile.getAbsolutePath());
			}
		}


		long currentTimeMillis = System.currentTimeMillis();

		aaptServiceArgs.log.println(to(args));
		//
		abcd.wf j62 = abcd.xf.j6(args, null, null, true, null, null);

		aaptServiceArgs.log.println("aapt2 call link " + (System.currentTimeMillis() - currentTimeMillis) + "ms");

		if ( j62.DW() != 0 ){
			String s = aaptServiceArgs.getAapt2Error(j62);
			aaptServiceArgs.log.println("aapt2 错误: -> " + s);

			if ( s != null ){
				return new AaptService$b(s);
			}
		}
		return null;
	}

	public static String to(List<String> args){
		StringBuilder a = new StringBuilder("\n");
		for ( String arg : args ){
			a.append(arg);
			a.append(" ");
		}
		return a.toString();
	}

	private static String getAndroidManifestXml(AaptServiceArgs aaptServiceArgs, String subProjectGen) throws RuntimeException{
		String manifestXml = aaptServiceArgs.mergedAManifestMap.get(subProjectGen);
		if ( !fileExists(manifestXml) ){
			manifestXml = aaptServiceArgs.injectedAManifestMap.get(subProjectGen);
			if ( !fileExists(manifestXml) ){
				manifestXml = aaptServiceArgs.aManifestMap.get(manifestXml);
				if ( !fileExists(manifestXml) ){
					aaptServiceArgs.log.println("没有AndroidManifest文件玩尼玛\n");
					//没辙了
					throw new RuntimeException("没有AndroidManifest文件玩尼玛\n" + "Fuck you! Not found AndroidManifest file!!!");
				}
			}
		}
		return manifestXml;
	}

	// 编译成aapt2格式文件
	public static AaptService$b fullCompile(AaptServiceArgs aaptServiceArgs, String resDir, String output){

		PrintStream log = aaptServiceArgs.log;

		long currentTimeMillis = System.currentTimeMillis();

		List<String> args = new ArrayList<>();

		args.add(aaptServiceArgs.getAapt2Path());
		args.add("compile");

		args.add("--dir");
		args.add(resDir);

		args.add("-o");
		args.add(output);

		//执行aapt2 compile命令
		log.println(to(args));
		abcd.wf j62 = abcd.xf.j6(args, null, null, true, null, null);
		log.println("aapt2 call compile " + (System.currentTimeMillis() - currentTimeMillis) + " ms");
		if ( j62.DW() != 0 ){
			//j6 -> VH
			String errorInfo = aaptServiceArgs.getAapt2Error(j62);
			if ( errorInfo != null ){
				return new AaptService$b(errorInfo);
			}
		}

		return null;
	}

	public static boolean hasError(Object aaptService$bObject){
		if ( aaptService$bObject != null ){
			//com.aide.ui.build.android.AaptService.b b;
			// 类 com.aide.ui.build.android.AaptService$b
			ReflectPie on = ReflectPie.on(aaptService$bObject);
			if ( on.get("DW") != null ){
				//aaptService$b 有错误信息
				return true;
			}
		}
		return false;
	}

	/**
	 * 优化
	 */
	private static AaptService$b incrementalCompile(AaptServiceArgs aaptServiceArgs, String resDir, String resFlatCacheDir) throws IOException{
		//增量编译
		List<String> inputFiles = new ArrayList<>();
		List<File> outFiles = new ArrayList<>();

		for ( File resourceFile : FileUtil.findFile(new File(resDir), null) ){
			File flatFile = getAapt2FlatCacheFile(resFlatCacheDir, resourceFile);
			outFiles.add(flatFile);

			if ( !flatFile.exists() || flatFile.lastModified() < resourceFile.lastModified() ){
				inputFiles.add(resourceFile.getAbsolutePath());
			}
		}

		//所有flat中间文件，去除需要的，剩下都是以删除的
		File aapt2ResCacheDirFile = new File(resFlatCacheDir);

		List<File> oldFlatFiles = FileUtil.findFile(aapt2ResCacheDirFile , null);
		oldFlatFiles.removeAll(outFiles);

		for ( File oldFlatFile : oldFlatFiles ){
			oldFlatFile.delete();
		}

		if ( ! inputFiles.isEmpty() ){
			AaptService$b incrementalCompile = incrementalCompile(aaptServiceArgs, inputFiles, resFlatCacheDir);
			if ( incrementalCompile != null ){
				//有错误，直接返回
				return incrementalCompile;
			}
		}


		File[] flatFiles = aapt2ResCacheDirFile.listFiles();
		if ( flatFiles == null || flatFiles.length == 0 ){
			return null;
		}

		String flatsZipFile = getMergedCacheDirFile(aaptServiceArgs, resDir);
		//被使用，添加输出
		aaptServiceArgs.resCompiledSet.add(flatsZipFile);

		if ( inputFiles.isEmpty() 
			&& new File(flatsZipFile).exists() ){
			return null;
		}
		long currentTimeMillis = System.currentTimeMillis();
		//合并成zip
		//增量编译后的[二级缓存]
		//后面再实现增量更新zip
		// 从zip中删除oldFlatFile名称的文件
		// 添加inputFiles的中间文件
		ZipOutputStream out = new ZipOutputStream(new FileOutputStream(flatsZipFile));
		out.setMethod(ZipEntry.STORED);

		FlatByteArray bytesOutput = new FlatByteArray(1024 * 30);
		CRC32 crc = new CRC32();
		for ( File file : flatFiles ){
			bytesOutput.reset();
			
			FileInputStream input = new FileInputStream(file);
			streamTransfer(input, bytesOutput);
			input.close();
			
			crc.reset();
			crc.update(bytesOutput.getBuf(), 0, bytesOutput.size());
			
			ZipEntry zipEntry = new ZipEntry(file.getName());
			zipEntry.setMethod(ZipEntry.STORED);
			zipEntry.setSize(file.length());
			zipEntry.setCrc(crc.getValue());
			
			out.putNextEntry(zipEntry);
			out.write(bytesOutput.getBuf(), 0, bytesOutput.size());
			out.closeEntry();
		}
		
		bytesOutput.close();
		out.close();
		
		aaptServiceArgs.log.println("打包 " + flatsZipFile + " 耗时" + (System.currentTimeMillis() - currentTimeMillis) + " ms");
		
		return null;
	}


	//增量编译
	public static AaptService$b incrementalCompile(AaptServiceArgs aaptServiceArgs, List<String> inputFiles, String output){
		PrintStream log = aaptServiceArgs.log;
		long currentTimeMillis = System.currentTimeMillis();

		List<String> args = new ArrayList<>();
		args.add(aaptServiceArgs.getAapt2Path());
		args.add("compile");
		args.addAll(inputFiles);

		args.add("-o");
		args.add(output);

		abcd.wf j62 = abcd.xf.j6(args, null, null, true, null, null);
		log.println("aapt2 call compile " + (System.currentTimeMillis() - currentTimeMillis) + " ms");
		if ( j62.DW() != 0 ){
			//j6 -> VH
			String errorInfo = aaptServiceArgs.getAapt2Error(j62);
			if ( errorInfo != null ){
				return new AaptService$b(errorInfo);
			}
		}

		return null;
	}
	private static File getAapt2FlatCacheFile(String aapt2ResCacheDir, File resourceFile){
		String parentFileName = resourceFile.getParentFile().getName();
		String resourceFileName = resourceFile.getName();

		String flatName = resourceFileName;
		if ( parentFileName.startsWith("values") ){
			int flatSimpleNameEnd = resourceFileName.lastIndexOf('.');
			if ( flatSimpleNameEnd > 0 ){
				flatName = resourceFileName.substring(0, flatSimpleNameEnd);	
			}
			flatName = flatName + ".arsc.flat";
		}
		else{
			flatName = flatName + ".flat";
		}

		flatName = parentFileName + "_" + flatName;


		return new File(aapt2ResCacheDir, flatName);
	}

	// 返回编译输出路径 + 路径的md5码
	private static String getAapt2ResCacheDir(AaptServiceArgs aaptServiceArgs, String resPath){
		return (aaptServiceArgs.getCompileDirPath() + "/" + MD5Util.stringMD5(resPath));
	}
	private static String getMergedCacheDirFile(AaptServiceArgs aaptServiceArgs, String resPath){
		return (aaptServiceArgs.getMergedDirFile() + "/" + MD5Util.stringMD5(resPath) + ".zip");
	}
	/**
	 * 弃用
	 */
	// 路径的md5码
	private static String getAapt2ResCacheFile2(AaptServiceArgs aaptServiceArgs, String resPath){
		return (aaptServiceArgs.getCompileDirPath() + "/" + MD5Util.stringMD5(resPath) + ".zip");
	}
	
	public static class FlatByteArray extends ByteArrayOutputStream{
		public FlatByteArray() {}

		public FlatByteArray(int size) {
			super(size);
		}
		
		
		public byte[] getBuf(){
			return this.buf;
		}
	}
}
