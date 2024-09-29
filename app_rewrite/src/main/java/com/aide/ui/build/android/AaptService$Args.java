package com.aide.ui.build.android;

import abcd.ey;
import abcd.wf;
import abcd.xf;
import android.os.Build;
import com.aide.common.AppLog;
import com.aide.common.StreamUtilities;
import com.aide.ui.build.android.AaptService;
import com.aide.ui.project.AndroidProjectSupport;
import com.aide.ui.util.FileSystem;
import io.github.zeroaicy.aide.aapt2.Aapt2Task;
import io.github.zeroaicy.aide.preference.ZeroAicySetting;
import io.github.zeroaicy.util.IOUtils;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AaptService$Args {

    public final Map<String, List<String>> DW;
    public final Map<String, String> genPackageNameMap;

	// aaptPath
    public final String FH;

	//androidJar File
    public final String androidSdkFilePath;

    public final List<String> variantManifestPaths;


    final AaptService aaptService;

	// aManifestMap
	// key: injected xml value: original manifest xml
    public final Map<String, String> aManifestMap;
	// injectedAManifestMap
	// key: gen路径 value: injected xml
	public final Map<String, String> injectedAManifestMap;
	// mergedAManifestMap
	// key: gen路径 value: merged xml
    public final Map<String, String> mergedAManifestMap;

    public final List<String> assetDirPaths;

    private boolean XL;

	// mainProjectGenDir
    public final String mainProjectGenDir;

    public boolean isBuildRefresh;

	//resource.ap_
    public final String resourcesApPath;
    private boolean j3;

    public final String j6;
    public final Map<String, List<String>> genResDirsMap;
    public final Map<String, String> allResourceMap;
    public final String v5;

	// subProjectGens
    public final List<String> subProjectGens;

	//被Callable调用
    public AaptService$ErrorResult we( ) {
        try {

			if ( ZeroAicySetting.isEnableAapt2() ) {
				//aapt2实现
				return Aapt2Task.proxyAapt(this);
			}

            // aapt实现
            AndroidProjectSupport.Qq(this.DW, this.v5);
            AaptService$ErrorResult mergedAndroidManifestxml = mergedAndroidManifestxml();
            if ( mergedAndroidManifestxml.errorInfo != null ) {
                return mergedAndroidManifestxml;
            }

            for ( String str : this.injectedAManifestMap.keySet() ) {
                File file = new File(str);
				if ( !file.exists() ) {
                    file.mkdirs();
                }
            }

            j6();

            File parentFile = new File(this.resourcesApPath).getParentFile();
            if ( !parentFile.exists() ) {
                parentFile.mkdirs();
            }

            AaptService$ErrorResult J0 = J0();

            if ( J0.errorInfo != null ) {
                return J0;
            }

            generateBuildConfigJava();
            if ( !this.XL ) {
                if ( this.isBuildRefresh ) {
                    buildRefresh();
                }
                AaptService$ErrorResult Ws = Ws();
                return Ws.errorInfo != null ? Ws : QX();
            }
            return new AaptService$ErrorResult(false);
        }
		catch (Throwable th) {
            throw new Error(th);
        }
    }

    public AaptService$Args( AaptService aaptService, 
							String aaptPath, String str2, String str3, 
							Map<String, List<String>> map, List<String> subProjectGens, List<String> variantManifestPaths, 
							String androidJarFilePath, String mainProjectGenDir, List<String> assetDirPaths, 
							String resourceAp_FilePath, Map<String, String> genPackageNameMap, Map<String, String> allResourceMap, 
							Map<String, String> map4, Map<String, String> mergedAManifestMap, Map<String, List<String>> genResDirsMap, 
							Map<String, String> map7, boolean z, boolean isBuildRefresh, boolean z3 ) {

		this.aaptService = aaptService;
        this.j6 = str2;
        this.DW = map;
        this.v5 = str3;
        this.FH = aaptPath;
        this.subProjectGens = subProjectGens;
        this.variantManifestPaths = variantManifestPaths;
        this.androidSdkFilePath = androidJarFilePath;
        this.mainProjectGenDir = mainProjectGenDir;
        this.assetDirPaths = assetDirPaths;
        this.resourcesApPath = resourceAp_FilePath;

		//genDir -> packageName，但只有子项目，子项目的子项目没有
        this.genPackageNameMap = genPackageNameMap;

        this.allResourceMap = allResourceMap;
        this.mergedAManifestMap = mergedAManifestMap;
        this.genResDirsMap = genResDirsMap;
        this.XL = z;
        this.isBuildRefresh = isBuildRefresh;
        this.j3 = z3;
        this.injectedAManifestMap = new HashMap<String, String>(map4);
        this.aManifestMap = new HashMap<>();
        for ( Map.Entry<String, String> entry : map7.entrySet() ) {
            this.aManifestMap.put(entry.getValue(), entry.getKey());
        }
    }

    public static String DW( AaptService$Args aaptService$c ) {
        return aaptService$c.j6;
    }

	// 合并清单文件
    public AaptService$ErrorResult mergedAndroidManifestxml( ) {
        try {

            if ( this.subProjectGens.size() <= 0 
				&& this.variantManifestPaths.size() <= 0 ) {
				return new AaptService$ErrorResult(false);
			}

			// injectedAManifestMap
			String mainProjectInjectedManifestPath = this.injectedAManifestMap.get(this.mainProjectGenDir);
			// aManifestMap
			String mainProjectManifestPath = this.aManifestMap.get(mainProjectInjectedManifestPath);
			// mergedAManifestMap
			String mainProjectMergedManifestPath = this.mergedAManifestMap.get(this.mainProjectGenDir);

			// subProjectGens
			int subProjectGensSize = this.subProjectGens.size();

			String[] subProjectInjectedManifestPaths = new String[subProjectGensSize];
			for ( int index = 0; index < subProjectGensSize; index++ ) {
				// 所有子项目的injectedManifest
				subProjectInjectedManifestPaths[index] = this.injectedAManifestMap.get(this.subProjectGens.get(index));
			}

			int variantManifestPathsSize = this.variantManifestPaths.size();
			String[] variantManifestPaths = new String[variantManifestPathsSize];
			for ( int index = 0; index < this.variantManifestPaths.size(); index++ ) {
				variantManifestPaths[index] = this.variantManifestPaths.get(index);
			}

			/**
			 * 将所有xmlmMap中的主项目manifest xml更新为 merged xml
			 */
			// key: gen路径 value: injected xml
			this.injectedAManifestMap.put(this.mainProjectGenDir, mainProjectMergedManifestPath);
			// key: injected xml value: original manifest xml
			this.aManifestMap.put(mainProjectMergedManifestPath, mainProjectManifestPath);

			ArrayList<File> manifestPaths = new ArrayList<>();
			// 添加 主项目的injected xml[属于合并的输入xml]
			manifestPaths.add(new File(mainProjectInjectedManifestPath));

			for ( int index = 0; index < subProjectGensSize; index++ ) {
				manifestPaths.add(new File(subProjectInjectedManifestPaths[index]));
			}

			for ( int index = 0; index < variantManifestPathsSize; index++ ) {
				manifestPaths.add(new File(variantManifestPaths[index]));
			}

			// merge目录
			File mainProjectMergedManifestFile = new File(mainProjectMergedManifestPath);
			File inputInfoFile = new File(mainProjectMergedManifestFile.getParent(), "merged-manifest-inputs-info");

			if ( mainProjectMergedManifestFile.exists() 
				&& !isChangerInputs(manifestPaths, inputInfoFile) 
				&& u7(manifestPaths, Collections.singletonList(mainProjectMergedManifestFile)) ) {
				// 省略合并
				AppLog.DW("Omitting merge " + mainProjectMergedManifestPath);
			} else {
				AppLog.DW("Merging " + mainProjectMergedManifestPath);
				// 合并
				String mergedInfo = com.aide.ui.build.android.l.j6(AaptService.Zo(this.aaptService), mainProjectMergedManifestPath, mainProjectInjectedManifestPath, variantManifestPaths, subProjectInjectedManifestPaths);
				if ( mergedInfo != null ) {
					return new AaptService$ErrorResult(mergedInfo);
				}
			}

            return new AaptService$ErrorResult(false);
        }
		catch (Throwable th) {
            throw new Error(th);
        }
    }

	/**
	 * 检查输入源是否改变
	 */
	private boolean isChangerInputs( List<File> inputFiles, File inputInfoFile ) {

		Set<String> inputPaths = new HashSet<String>();
		for ( File inputFile : inputFiles ) {
			inputPaths.add(inputFile.getAbsolutePath());
		}

		boolean isChangerInputs = false;

		// 与inputInfoFile检查输入源
		if ( !inputInfoFile.exists() ) {
			// 已改变
			isChangerInputs = true;
		} else {
			// 从inputInfoFile读取上一次inputFiles
			Set<String> lastInputJarFilesSet = readInputInfoFile(inputInfoFile);
			//比较
			isChangerInputs = inputPaths.size() != lastInputJarFilesSet.size()
				|| !inputPaths.containsAll(lastInputJarFilesSet);			
		}

		// 只有已改变才重写写入
		if ( isChangerInputs ) {
			//写入inputJarFilesSet
			IOUtils.writeLines(inputPaths, inputInfoFile);
		}
		return false;

	}

	/**
	 * 读取输入源文件
	 */
	private Set<String> readInputInfoFile( File inputInfoFile ) {
		Set<String> lastInputJarFilesSet = new HashSet<>();


		try {
			IOUtils.readLines(new FileInputStream(inputInfoFile), lastInputJarFilesSet);
		}
		catch (Exception e) {}

		return lastInputJarFilesSet;
	}

    public static Map<String, String> FH( AaptService$Args aaptService$c ) {
        return aaptService$c.aManifestMap;
    }

    @ey(method = 2668790741732965889L)
    private void Hw( File file, String str, List<File> list ) {
        try {
            File[] listFiles = file.listFiles();
            if ( listFiles == null ) {
                return;
            }
            for ( File file2 : listFiles ) {
                if ( file2.isDirectory() ) {
                    Hw(file2, str, list);
                } else if ( str == null || str.equals(file2.getName()) ) {
                    list.add(file2);
                }
            }
        }
		catch (Throwable th) {
            throw new Error(th);
        }
    }

    private AaptService$ErrorResult J0( ) {
        try {
            for ( Map.Entry<String, List<String>> entry : this.genResDirsMap.entrySet() ) {

				if ( Thread.interrupted() ) {
					throw new InterruptedException();
				}

				String key = entry.getKey();
				List<String> value = entry.getValue();
				if ( !key.equals(this.mainProjectGenDir) ) {
					AaptService$ErrorResult J8 = J8(key, value);
					if ( J8.errorInfo != null ) {
						return J8;
					}
				}

            }
            if ( this.XL ) {
                AaptService$ErrorResult J82 = J8(this.mainProjectGenDir, this.genResDirsMap.get(this.mainProjectGenDir));
                if ( J82.errorInfo != null ) {
                    return J82;
                }
            }
            return new AaptService$ErrorResult(false);
        }
		catch (Throwable th) {
			throw new Error(th);
        }
    }

    @ey(method = -917848650515497287L)
    private AaptService$ErrorResult J8( String str, List<String> list ) {
        try {

            String str2 = this.injectedAManifestMap.get(str);
            ArrayList<File> arrayList = new ArrayList<>();
            for ( String str3 : list ) {
                Hw(new File(str3), null, arrayList);
            }

            if ( arrayList.isEmpty() ) {
                return new AaptService$ErrorResult(false);
            }
            if ( new File(str2).exists() ) {
                arrayList.add(new File(str2));
            }

            ArrayList<File> arrayList2 = new ArrayList<>();
            Hw(new File(str), "R.java", arrayList2);
            if ( !this.isBuildRefresh && !arrayList2.isEmpty() && u7(arrayList, arrayList2) ) {
                AppLog.DW("Omitting aapt call to regenerate R.java in " + str + " (is uptodate)");
                return new AaptService$ErrorResult(false);
            }
            ArrayList<String> arrayList3 = new ArrayList<>();
            if ( str.equals(this.mainProjectGenDir) ) {
                arrayList3.addAll(Arrays.asList(new String[]{this.FH, "package", "--auto-add-overlay", "-m", "-J", str, "-M", str2, "-I", this.androidSdkFilePath, "--no-version-vectors"}));
            } else {
                arrayList3.addAll(Arrays.asList(new String[]{this.FH, "package", "--non-constant-id", "--auto-add-overlay", "-m", "-J", str, "-M", str2, "-I", this.androidSdkFilePath, "--no-version-vectors"}));
            }
            for ( String str4 : list ) {
                if ( new File(str4).exists() ) {
                    arrayList3.addAll(Arrays.asList(new String[]{"-S", str4}));
                }
            }
            if ( str.equals(this.mainProjectGenDir) ) {
                String gn = gn();
                if ( gn.length() != 0 ) {
                    arrayList3.add("--extra-packages");
                    arrayList3.add(gn.toString());
                }
            }
            tp(arrayList3);
            long currentTimeMillis = System.currentTimeMillis();
            wf j6 = xf.j6(arrayList3, (String) null, null, true, (OutputStream) null, (byte[]) null);
            AppLog.DW("aapt call elapsed " + ( System.currentTimeMillis() - currentTimeMillis ));
            if ( j6.DW() == 0 ) {
                for ( File file : arrayList2 ) {
                    if ( file.lastModified() < currentTimeMillis ) {
                        FileSystem.aj(file.getPath());
                    }
                }
                return new AaptService$ErrorResult(false);
            }
            return new AaptService$ErrorResult(getAaptError(j6.j6(), j6.DW()));
        }
		catch (Throwable th) {
			throw new Error(th);
        }
    }

    private AaptService$ErrorResult QX( ) {
        try {

            if ( !Thread.interrupted() ) {
                List<String> list = this.genResDirsMap.get(this.mainProjectGenDir);
                String str = this.injectedAManifestMap.get(this.mainProjectGenDir);
                ArrayList<File> arrayList = new ArrayList<>();
                ArrayList<File> arrayList2 = new ArrayList<>();
                for ( String str2 : list ) {
                    Hw(new File(str2), null, arrayList2);
                }
                if ( new File(str).exists() ) {
                    arrayList2.add(new File(str));
                }
                for ( String str3 : this.assetDirPaths ) {
                    if ( new File(str3).exists() ) {
                        Hw(new File(str3), null, arrayList2);
                    }
                }
                arrayList2.add(new File(this.androidSdkFilePath));
                Hw(new File(this.mainProjectGenDir), "R.java", arrayList);
                arrayList.add(new File(this.resourcesApPath));
                if ( !this.isBuildRefresh && !arrayList.isEmpty() && new File(this.resourcesApPath).exists() && u7(arrayList2, arrayList) ) {
                    AppLog.DW("Omitting aapt package call (is uptodate)");
                    return new AaptService$ErrorResult(false);
                }
                ArrayList<String> arrayList3 = new ArrayList<>();
                if ( this.j3 ) {
                    arrayList3.addAll(Arrays.asList(new String[]{this.FH, "package", "-f", "--no-crunch", "--auto-add-overlay", "-I", this.androidSdkFilePath, "-F", this.resourcesApPath}));
                } else {
                    arrayList3.addAll(Arrays.asList(new String[]{this.FH, "package", "-f", "--no-crunch", "--auto-add-overlay", "--debug-mode", "-I", this.androidSdkFilePath, "-F", this.resourcesApPath}));
                }
                for ( String str4 : this.assetDirPaths ) {
                    if ( new File(str4).exists() ) {
                        arrayList3.addAll(Arrays.asList(new String[]{"-A", str4}));
                    }
                }
                arrayList3.addAll(Arrays.asList(new String[]{"-M", str}));
                for ( String str5 : list ) {
                    if ( new File(str5).exists() ) {
                        String str6 = this.allResourceMap.get(str5);
                        new File(str6).mkdirs();
                        arrayList3.addAll(Arrays.asList(new String[]{"-S", str6, "-S", str5}));
                    }
                }
                arrayList3.addAll(Arrays.asList(new String[]{"-m", "-J", this.mainProjectGenDir, "--no-version-vectors"}));
                String gn = gn();
                if ( gn.length() != 0 ) {
                    arrayList3.add("--extra-packages");
                    arrayList3.add(gn.toString());
                }
                tp(arrayList3);
                long currentTimeMillis = System.currentTimeMillis();
                wf j6 = xf.j6(arrayList3, (String) null, null, true, (OutputStream) null, (byte[]) null);
                AppLog.DW("aapt call elapsed " + ( System.currentTimeMillis() - currentTimeMillis ));
                if ( j6.DW() == 0 ) {
                    for ( File file : arrayList ) {
                        if ( file.lastModified() < currentTimeMillis ) {
                            FileSystem.aj(file.getPath());
                        }
                    }
                    return new AaptService$ErrorResult(true);
                }
                return new AaptService$ErrorResult(getAaptError(j6.j6(), j6.DW()));
            }
            throw new InterruptedException();
        }
		catch (Throwable th) {
            throw new Error(th);
        }
    }

    public String getAaptError( byte[] bArr, int i ) {
        try {

			String FH = StreamUtilities.FH(new InputStreamReader(new ByteArrayInputStream(bArr)));

            String trim = FH.trim();
            if ( trim.length() == 0 ) {
                return "aapt exited with code " + i;
            } else if ( i != 1 ) {
                return trim + "\naapt exited with code " + i;
            } else {
                return trim;
            }
        }
		catch (Throwable th) {
            throw new Error(th);
        }
    }

    private AaptService$ErrorResult Ws( ) {
        try {

            for ( Map.Entry<String, String> entry : this.allResourceMap.entrySet() ) {
                if ( Thread.interrupted() ) {
					throw new InterruptedException();
				}
				String key = entry.getKey();
				String value = entry.getValue();

				if ( new File(key).exists() ) {
					ArrayList<File> arrayList = new ArrayList<>();
					Hw(new File(key), null, arrayList);
					ArrayList<File> arrayList2 = new ArrayList<>();
					Hw(new File(value), null, arrayList2);
					if ( u7(arrayList, arrayList2) ) {
						AppLog.DW("Omitting aapt crunch call (is uptodate)");
					} else {
						List<String> asList = Arrays.asList(new String[]{this.FH, "crunch", "-S", key, "-C", value, "--no-version-vectors"});
						tp(asList);
						long currentTimeMillis = System.currentTimeMillis();
						wf j6 = xf.j6(asList, (String) null, null, true, (OutputStream) null, (byte[]) null);
						AppLog.DW("aapt call elapsed " + ( System.currentTimeMillis() - currentTimeMillis ));
						if ( j6.DW() != 0 ) {
							return new AaptService$ErrorResult(getAaptError(j6.j6(), j6.DW()));
						}
					}
				}
            }
            return new AaptService$ErrorResult(false);
        }
		catch (Throwable th) {
            throw new Error(th);
        }
    }

	/**
	 * 生成 BuildConfig.java
	 */
    public void generateBuildConfigJava( ) {
        try {
            for ( Map.Entry<String, String> entry : this.genPackageNameMap.entrySet() ) {
                String value = entry.getValue();
                File file = new File(entry.getKey(), value.replace('.', File.separatorChar));
                if ( !file.exists() && !file.mkdirs() ) {
                    throw new IOException("Could not create directory " + file);
                }
                File buildConfigJavaFile = new File(file, "BuildConfig.java");
                if ( !this.XL || !buildConfigJavaFile.exists() ) {
                    c.j6(buildConfigJavaFile, value, this.j3);
                }
            }
        }
		catch (Throwable th) {
            throw new Error(th);
        }
    }

    private String gn( ) {
        try {
            HashSet<String> hashSet = new HashSet<>();
            for ( Map.Entry<String, String> entry : this.genPackageNameMap.entrySet() ) {
                String key = entry.getKey();
                String value = entry.getValue();
                if ( !value.equals(this.genPackageNameMap.get(this.mainProjectGenDir)) && !hashSet.contains(value) && new File(new File(key, value.replace('.', File.separatorChar)), "R.java").exists() ) {
                    hashSet.add(value);
                }
            }
            if ( FileSystem.getName(FileSystem.getParent(this.injectedAManifestMap.get(this.mainProjectGenDir))).equals("AIDEExp") ) {
                hashSet.add("com.aide.ui");
            }
            StringBuilder sb = new StringBuilder();
            for ( String str : hashSet ) {
                if ( sb.length() != 0 ) {
                    sb.append(':');
                }
                sb.append(str);
            }
            return sb.toString();
        }
		catch (Throwable th) {
            throw new Error(th);
        }
    }

    private void j6( ) {
        try {
            if ( Thread.interrupted() ) {
				throw new InterruptedException();
			}
			if ( Build.VERSION.SDK_INT < 29 && !AaptService.VH() ) {
				File aaptFile = new File(this.FH);
				boolean isSetExecutable = aaptFile.setReadable(true, false) 
					&& aaptFile.setExecutable(true, false);
				if ( !isSetExecutable ) {
					throw new IOException("Could not make " + this.FH + " executable - exit code ");
				}
				AaptService.gn(true);
				return;
			}
			return;

        }
		catch (Throwable th) {
            throw new Error(th);
        }
    }

    private void tp( List<String> list ) {
        try {
            StringBuilder sb = new StringBuilder();
            sb.append("Running aapt ");
            for ( int i = 1; i < list.size(); i++ ) {
                sb.append('\"');
                sb.append(list.get(i));
                sb.append('\"');
                if ( i != list.size() - 1 ) {
                    sb.append(" ");
                }
            }
            AppLog.DW(sb.toString());
        }
		catch (Throwable th) {
            throw new Error(th);
        }
    }
	/**
	 * 检查输出文件 是否新于输入文件
	 */
    private boolean u7( List<File> inputFiles, List<File> outputFiles ) {
        try {
			// ! (inputFiles.isEmpty() || !outputFiles.isEmpty())
			if ( !inputFiles.isEmpty() && outputFiles.isEmpty() ) {
				return false;
			}
			// 获取最旧输出文件的时间戳
			long outputFileMinLastModified = Long.MAX_VALUE;
			for ( File outputFile : outputFiles ) {
				outputFileMinLastModified = Math.min(outputFileMinLastModified, outputFile.lastModified());
			}
			// 获取最新输入文件的时间戳
			long inputFileMaxLastModified = 0;
			for ( File inputFile : inputFiles ) {
				inputFileMaxLastModified = Math.max(inputFileMaxLastModified, inputFile.lastModified());
			}
			// 如果最旧输出文件的时间戳，仍新于最新输入文件的时间戳
			// 则为true，可忽略
			return outputFileMinLastModified > inputFileMaxLastModified;

        }
		catch (Throwable th) {
            throw new Error(th);
        }
    }

    public void buildRefresh( ) {
        try {
            for ( Map.Entry<String, String> entry : this.allResourceMap.entrySet() ) {
                if ( Thread.interrupted() ) {
					throw new InterruptedException();

				}
				String value = entry.getValue();
				if ( new File(value).exists() ) {
					try {
						FileSystem.VH(value);
					}
					catch (Throwable e) {

					}
				}
            }
        }
		catch (Throwable th) {
            throw new Error(th);
        }
    }
}

