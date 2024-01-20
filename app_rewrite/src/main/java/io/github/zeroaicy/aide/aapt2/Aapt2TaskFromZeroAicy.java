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

public class Aapt2TaskFromZeroAicy {

	private static final String TAG = "aapt2";

	public static boolean fileExists(String filePath) {
		if ( TextUtils.isEmpty(filePath) ) return false;

		return new File(filePath).exists();
	}

	//com.aide.ui.build.android.AaptService.b b;
	private static String aapt$b = "com.aide.ui.build.android.AaptService$b";
	
	public static AaptService$b proxyAapt(Object aapt$c) throws Exception {
		long oldTime = System.currentTimeMillis();
		AaptServiceArgs aaptServiceArgs = new AaptServiceArgs(aapt$c);
		
		AaptService$b proxyAapt = proxyAapt2(aaptServiceArgs);
		
		float diffTime = System.currentTimeMillis() - oldTime;
		aaptServiceArgs.log.println("aapt2 总耗时 " + diffTime / 1000.0f + "s");
		return proxyAapt;
	}
	
	public static AaptService$b proxyAapt2(AaptServiceArgs aaptServiceArgs) throws Exception {

		PrintStream log = aaptServiceArgs.log;
		//构建刷新
		if (aaptServiceArgs.isBuildRefresh) {
			//AIDE
			aaptServiceArgs.buildRefresh();
		}
		// 合并清单
		merged: {
			//AaptService$b
			AaptService$b mergedErrorInfo = aaptServiceArgs.mergedAndroidManifestxml();
			if (mergedErrorInfo != null && mergedErrorInfo.DW != null ) {
				log.println("merged error " + mergedErrorInfo.DW);
				return mergedErrorInfo;
			} else {
				log.println("merged: " + mergedErrorInfo.DW);
			}
		}
		//编译
		compile: {
			Map<String, String> allResourceMap = aaptServiceArgs.allResourceMap;
			//顺序有问题(resCompiledSet)
			for (String resDir : allResourceMap.keySet()) {
				if (!new File(resDir).exists()) {
					continue;
				}
				File resCacheZipFile = new File(getAapt2ResCacheFile(aaptServiceArgs, resDir));
				String resCacheZipFilePath = resCacheZipFile.getAbsolutePath();

				AaptService$b aaptError = compile(aaptServiceArgs, resDir, resCacheZipFilePath);
				if (aaptError != null) {
					//ReflectPie on = ReflectPie.on(aaptError);
					return aaptError;
				}
			}
		}

		link: {

			MainProject: {
				List<String> resourceList = new ArrayList<>();
				List<String> assetsList = new ArrayList<>();

				//u7 -> this.VH
				List<String> u7 = aaptServiceArgs.assetsList;
				if (u7 != null || !u7.isEmpty()) {
					assetsList.addAll(u7);
				}
				String mainProjectGenDir = aaptServiceArgs.mainProjectGenDir;
				List<String> resDirs = aaptServiceArgs.genResDirsMap.get(mainProjectGenDir);

				//优先添加主项目res的缓存文件
				for (String mainProjectResPath : resDirs) {
					String aapt2ResCacheFile = getAapt2ResCacheFile(aaptServiceArgs, mainProjectResPath);
					if (aaptServiceArgs.resCompiledSet.remove(aapt2ResCacheFile)) {
						resourceList.add(aapt2ResCacheFile);
					}
				}
				resourceList.addAll(aaptServiceArgs.resCompiledSet);
				//反序 aapt2 link -R 末尾优先
				Collections.reverse(resourceList);
				aaptServiceArgs.resCompiledSet.addAll(resourceList);


				//tp -> gn
				String resourcesApPath = aaptServiceArgs.resourcesApPath;

				String aapt_rules = aaptServiceArgs.buildBin + "/intermediates/aapt_rules.txt";
				String rTxt = aaptServiceArgs.buildBin + "/intermediates/R.txt";

				AaptService$b aaptError = link35(aaptServiceArgs, resourceList, assetsList, mainProjectGenDir, resourcesApPath, false, aapt_rules, rTxt);
				if (aaptError != null) {
					return aaptError;
				}
			}
		}



		//需要将主项目R.java复制到主项目gen--以不同的包名
		deleteCache: {
			//根据生成的文件List，删除其它缓存
			File[] aapt2Cache = aaptServiceArgs.getCompileDirFile().listFiles(new FileFilter(){
					@Override
					public boolean accept(File pathname) {
						return pathname.isFile() && pathname.getName().endsWith(".zip");
					}
				});
			for (File file : aapt2Cache) {
				if (!aaptServiceArgs.resCompiledSet.contains(file.getAbsolutePath())) {
					file.delete();
				}
			}
		}

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

		for (int i = 0; i < rJavaLinelist.size(); i++) {
			String line = rJavaLinelist.get(i);
			if (line.contains(mainProjectPackageName)) {
				packageNameLine = line;
				packageNameLineCount = i;
				break;
			}
		}

		//消除 final
		for (int i = 0; i < rJavaLinelist.size(); i++) {
			rJavaLinelist.set(i, rJavaLinelist.get(i).replace(" final int ", " int "));
		}

		if (packageNameLineCount < 0 || TextUtils.isEmpty(packageNameLine)) {
			return new AaptService$b("R.java 生成错误，没有找到Rpackage");
		}

		for (Map.Entry<String, String> subProjectGen : genPackageNameMap.entrySet()) {
			String subGenDirPath = subProjectGen.getKey();
			if (mainProjectPackageName.equals(subProjectGen.getValue())) {
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

		if (ZeroAicySetting.isEnableViewBinding()) {
			try {
				// viewbinding
				GenerateViewBindingTask.run(aaptServiceArgs.mainProjectResPath, mainProjectGenDir, mainProjectPackageName, ZeroAicySetting.isViewBindingAndroidX());
			} catch (Throwable e) {
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

	// 路径的md5码
	private static String getAapt2ResCacheFile(AaptServiceArgs aaptServiceArgs, String resPath) {
		return (aaptServiceArgs.getCompileDirPath() + "/" + MD5Util.stringMD5(resPath) + ".zip");
	}


	// 半成品apk
	public static AaptService$b link35(AaptServiceArgs aaptServiceArgs, List<String> resourceList, List<String> assetsList,  String genDir, String outputPath, boolean isNonFinalIds , String proguardPath, String rTxtPath) throws Exception {

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
		args.add("-I");
		args.add(androidJar);
        args.add("--allow-reserved-package-id");
        args.add("--no-version-vectors");
		args.add("--no-version-transitions");
        args.add("--auto-add-overlay");
        if (target <= 0 && min <= 0) {
			target = 28;
			min = 21;
		}
		args.add("--min-sdk-version");
		args.add(String.valueOf(min));
		args.add("--target-sdk-version");
		args.add(String.valueOf(target));

		if (!TextUtils.isEmpty(proguardPath)) {
			//删除旧的proguard文件
			//clear(proguardPath);
			args.add("--proguard");
			args.add(proguardPath);
		}
		if (!resourceList.isEmpty()) {
			for (String path : resourceList) {
				File f = new File(path);
				if (f.exists()) {
					args.add("-R");
					args.add(path);
                }
			}
		}
		if (!TextUtils.isEmpty(genDir)) {
			args.add("--java");
			args.add(genDir);
		}
		//子项目
		if (isNonFinalIds) {
			args.add("--non-final-ids");
		}
		if (!TextUtils.isEmpty(androidManifestXml)) {
			args.add("--manifest");
			args.add(androidManifestXml);
        }
		if (!TextUtils.isEmpty(outputPath)) {
			//clear(outputPath);

			//输出文件
			args.add("-o");
			args.add(outputPath);
        }
		if (!TextUtils.isEmpty(rTxtPath)) {
			//clear(rTxtPath);
			args.add("--output-text-symbols");
			args.add(rTxtPath);
        }
		if (!assetsList.isEmpty()) {
			for (String s : assetsList) {
				File f = new File(s);
				if (f.exists()) {
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

		args.add(0, "link");
		args.add(0, aaptServiceArgs.getAapt2Path());
		long currentTimeMillis = System.currentTimeMillis();

		aaptServiceArgs.log.println(to(args));
		//
		abcd.wf j62 = abcd.xf.j6(args, null, null, true, null, null);

		aaptServiceArgs.log.println("aapt2 call link " + (System.currentTimeMillis() - currentTimeMillis) + "ms");
		if (j62.DW() != 0) {
			String s = aaptServiceArgs.getAapt2Error(j62);
			aaptServiceArgs.log.println("wf VH 错误信息: " + s);

			if (s != null) {
				return new AaptService$b(s);
			}
		}
		return null;
	}
	
	public static String to(List<String> args) {
		StringBuilder a = new StringBuilder("\n");
		for (String arg : args) {
			a.append(arg);
			a.append(" ");
		}
		return a.toString();
	}

	private static String getAndroidManifestXml(AaptServiceArgs aaptServiceArgs, String subProjectGen) throws RuntimeException {
		String manifestXml = aaptServiceArgs.mergedAManifestMap.get(subProjectGen);
		if (!fileExists(manifestXml)) {
			manifestXml = aaptServiceArgs.injectedAManifestMap.get(subProjectGen);
			if (!fileExists(manifestXml)) {
				manifestXml = aaptServiceArgs.aManifestMap.get(manifestXml);
				if (!fileExists(manifestXml)) {
					aaptServiceArgs.log.println("没有AndroidManifest文件玩尼玛\n");
					//没辙了
					throw new RuntimeException("没有AndroidManifest文件玩尼玛\n" + "Fuck you! Not found AndroidManifest file!!!");
				}
			}
		}
		return manifestXml;
	}

	// 编译成aapt2格式文件
	public static AaptService$b compile(AaptServiceArgs aaptServiceArgs, String resDir, String output) {

		PrintStream log = aaptServiceArgs.log;

		long currentTimeMillis = System.currentTimeMillis();

		//添加
		aaptServiceArgs.resCompiledSet.add(output);
		File cacheFile = new File(output);


		//缓存策略 dir下文件时间 < output时间 不编译
		if (cacheFile.exists()) {

			boolean breaked = true;
			long cacheFileModified = cacheFile.lastModified();
			for (File resXmlFile : FileUtil.findFile(new File(resDir), null)) {
				long inputFileModified = resXmlFile.lastModified();
				if (inputFileModified > cacheFileModified) {
					//只要一个文件新于输出文件
					//则不跳过
					breaked = false;
					log.println(resXmlFile + "改变，编译: " + resDir + " -> " + cacheFile);
					break;
				}
			}

			if (breaked) {
				log.println("使用缓存: " + cacheFile);
				return null;
			}
		}

		List<String> args = new ArrayList<>();

		args.add(aaptServiceArgs.getAapt2Path());
		args.add("compile");

		args.add("--dir");
		args.add(resDir);
		
		//DataBindingBuilder
		//*
		DataBindingBuilderProxy dataBindingBuilder = aaptServiceArgs.getDataBindingBuilder();
		boolean isNeedUseDataBinding = dataBindingBuilder.compilerRes(resDir);
		if (isNeedUseDataBinding) {
			// outResDir = $OutPath/bin/res
			String outResDir = aaptServiceArgs.allResourceMap.get(resDir);

			if (fileExists(outResDir)) {
				//队尾覆盖
				args.add("--dir");
				args.add(outResDir);
				FileUtil.copyNotCover(resDir, outResDir);
				//复制res但不覆盖 outres
			}
		}
		//*/

		args.add("-o");
		args.add(output);

		//执行aapt2 compile命令
		log.println(to(args));
		abcd.wf j62 = abcd.xf.j6(args, null, null, true, null, null);
		log.println("aapt2 call compile " + (System.currentTimeMillis() - currentTimeMillis) + " ms");
		if (j62.DW() != 0) {
			//j6 -> VH
			String errorInfo = aaptServiceArgs.getAapt2Error(j62);
			if (errorInfo != null) {
				return new AaptService$b(errorInfo);
			}
		}
		return null;
	}

	public static boolean hasError(Object aaptService$bObject) {
		if (aaptService$bObject != null) {
			//com.aide.ui.build.android.AaptService.b b;
			// 类 com.aide.ui.build.android.AaptService$b
			ReflectPie on = ReflectPie.on(aaptService$bObject);
			if (on.get("DW") != null) {
				//aaptService$b 有错误信息
				return true;
			}
		}
		return false;
	}
}
