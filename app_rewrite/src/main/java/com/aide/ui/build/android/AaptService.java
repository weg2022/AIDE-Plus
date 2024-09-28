
// j3 EQ aM Mr

//
// Decompiled by Jadx - 858ms
//
package com.aide.ui.build.android;

import android.content.Context;
import android.os.Build;
import com.aide.common.AppLog;
import com.aide.engine.SyntaxError;
import com.aide.ui.ServiceContainer;
import com.aide.ui.project.AndroidProjectSupport;
import com.aide.ui.services.AssetInstallationService;
import com.aide.ui.util.FileSystem;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import io.github.zeroaicy.aide.ui.services.ThreadPoolService;
import com.aide.ui.services.ZeroAicyProjectService;

public class AaptService {

    private static boolean v5;

    private AaptService$d DW;

    private e FH;

    private Context Hw;

	// 只有此类使用 j6 -> 
    private final ThreadPoolService executorsService;
	
    public AaptService(Context context) {
		this.Hw = context;
		this.executorsService =  ZeroAicyProjectService.getProjectServiceThreadPoolService();
    }

    static void DW(AaptService aaptService, Map<String, List<SyntaxError>> map) {
        aaptService.XL(map);
    }

    static void FH(AaptService aaptService) {
        aaptService.QX();
    }

    static void Hw(AaptService aaptService, Throwable th) {
        aaptService.Ws(th);
    }

	public void putSyntaxError(HashMap<String, List<SyntaxError>> hashMap, String str, String line) {
		if (!hashMap.containsKey(str)) {
			hashMap.put(str, new ArrayList<SyntaxError>());
		}
		List<SyntaxError> syntaxErrors = hashMap.get(str);
		syntaxErrors.add(u7("aapt", 1, line));
	}

    private Map<String, List<SyntaxError>> J0(String str, Map<String, String> map, String str2) {
        HashMap<String, List<SyntaxError>> hashMap = new HashMap<>();
		try {

			String[] lines = str2.split("\n");
			int i2 = 0;
			for (String line : lines) {

				if (line == null || line.length() == 0) {
					putSyntaxError(hashMap, str, line);
					continue;
				}

				try {
					int indexOf = line.indexOf(':');

					if (indexOf <= 0) {
						putSyntaxError(hashMap, str, line);
						continue;
					}

					String substring = line.substring(i2, indexOf);
					if (FileSystem.KD(substring)) {
						putSyntaxError(hashMap, str, line);
						continue;
					}

					int i4 = indexOf + 1;
					int indexOf2 = line.indexOf(':', i4);

					if (indexOf2 < 0) {
						indexOf2 = line.indexOf(' ', i4);
					}
					int i;

					if (indexOf2 > 0) {
						try {
							i = Integer.parseInt(line.substring(i4, indexOf2));
						}
						catch (NumberFormatException unused) {
							i = 1;
						}
						String line2 = line.substring(indexOf2 + 1, line.length()).trim();

						while (line2.toLowerCase().startsWith("error:")) {
							line2 = line2.substring(6, line2.length()).trim();
						}

						if (map.containsKey(substring)) {
							substring = map.get(substring);
							line2 = "in generated file: " + line2;
							i = 1;
						}
						SyntaxError u7 = u7("aapt", i, line2);
						if (!hashMap.containsKey(substring)) {
							hashMap.put(substring, new ArrayList<SyntaxError>());
						}
						hashMap.get(substring).add(u7);
					}
				}
				catch (Exception e) {
					AppLog.v5(e);
				}
			}
			return hashMap;
		}
		catch (Throwable th) {
			throw new Error(th);
		}

    }

	/**
	 * 会调用AndroidProjectBuildService$c::vJ
	 * 然后在切换到主线程运行AndroidProjectBuildService$c$c类
	 * 
	 */
    private void J8(boolean z) {
        if (this.FH != null) {
			this.FH.vJ(z);
		}
    }

    private void QX() {

		if (this.FH != null) {
			this.FH.J0();
		}
    }

    static boolean VH() {
        return v5;
    }

    private void Ws(Throwable th) {

		AppLog.v5(th);
		if (this.FH != null) {
			this.FH.g3();
		}
    }

    private void XL(Map<String, List<SyntaxError>> map) {
		if (this.FH != null) {
			this.FH.Mz(map);
		}
    }

    static Context Zo(AaptService aaptService) {
        return aaptService.Hw;
    }

    static boolean gn(boolean z) {
        v5 = z;
        return z;
    }

	// AaptService$d::done调用
    static void j6(AaptService aaptService, boolean z) {
        aaptService.J8(z);
    }

    private AaptService$Args tp(String str, boolean z, boolean z2, boolean z3, String str2, String str3, String str4) {
		Map<String, List<String>> vy = ServiceContainer.getProjectService().vy(str);
		
		Map<String, String> jO = AndroidProjectSupport.jO(vy, str3);
		Map<String, String> cT = AndroidProjectSupport.cT(vy, str3);
		Map<String, String> aq = AndroidProjectSupport.aq(str, vy, str3);
		Map<String, String> FN = AndroidProjectSupport.FN(vy, str3);
		Map<String, List<String>> oY = AndroidProjectSupport.oY(vy, str3);
		Map<String, String> Z1 = AndroidProjectSupport.Z1(vy, str3);
		return new AaptService$Args(this, str4, str, str3, vy, AndroidProjectSupport.jw(str), AndroidProjectSupport.fY(str, str3), ServiceContainer.getProjectService().getAndroidJarPath(), AndroidProjectSupport.Eq(str), AndroidProjectSupport.yO(str, str2, str3), AndroidProjectSupport.kf(str), jO, cT, aq, FN, oY, Z1, z, z2, z3);
    }

    private SyntaxError u7(String str, int i, String str2) {
		SyntaxError syntaxError = new SyntaxError();
		syntaxError.jw = i;
		syntaxError.fY = 1;
		syntaxError.qp = i;
		syntaxError.k2 = 1000;
		syntaxError.zh = str + ": " + str2;
		return syntaxError;
    }

    static Map<String, List<SyntaxError>> v5(AaptService aaptService, String str, Map<String, String> map, String str2) {
        return aaptService.J0(str, map, str2);
    }

    private String we() {
		if (Build.VERSION.SDK_INT >= 29) {
			AppLog.DW("Using aapt: " + ServiceContainer.getContext().getApplicationInfo().nativeLibraryDir + "/libaapt.so");
			return ServiceContainer.getContext().getApplicationInfo().nativeLibraryDir + "/libaapt.so";
		}
		return AssetInstallationService.DW("aapt", false);
    }

    public void EQ(String str) {
		Map Z1 = AndroidProjectSupport.Z1(ServiceContainer.getProjectService().getLibraryMapping(), str);
		for (String str2 : ServiceContainer.getProjectService().getLibraryMapping().keySet()) {
			String ye = AndroidProjectSupport.ye(str2, str);
			FileSystem.aj(ye);
			if (Z1.containsKey(ye)) {
				FileSystem.aj((String) Z1.get(ye));
			}
			String Eq = AndroidProjectSupport.Eq(str2);
			try {
				FileSystem.VH(Eq);
			}
			catch (Throwable e) {

			}
			new File(Eq).mkdirs();
		}
	}
	
	// AndroidProjectBuildService::yO -> Mr
    public void Mr(final String str) {
        try {
            final String we = we();
            if (this.DW != null) {
                this.DW.cancel(true);
                this.DW = null;
            }
			
			// 改成异步获取，在主线程获取会导致没有初始化完
			// 但此线程池用的是项目服务线程池
			AaptService$a.TaskFactory taskFactory = new AaptService$a.TaskFactory(){
				@Override
				public List<AaptService$Args> getTasks() {
					ArrayList<AaptService$Args> arrayList = new ArrayList<>();
					for (String next : ServiceContainer.getProjectService().yS()) {
						arrayList.add(tp(next, true, false, false, null, str, we));
					}
					return arrayList;
				}
			};
            ThreadPoolService executorService = this.executorsService;
            this.DW = new AaptService$d(this, new AaptService$a(this, taskFactory));
            executorService.execute(this.DW);
        }
		catch (Throwable th) {
        }
    }

    public void aM(e eVar) {
		this.FH = eVar;
    }

    public void j3(final String str, final String str2, final String str3, final boolean z, final boolean z2, final boolean z3) {
        try {
            final String we = we();
            if (this.DW != null) {
                this.DW.cancel(true);
                this.DW = null;
            }
			// 改成异步
			AaptService$a.TaskFactory taskFactory = new AaptService$a.TaskFactory(){
				@Override
				public List<AaptService$Args> getTasks() {
					ArrayList<AaptService$Args> arrayList = new ArrayList<>();
					if (z3) {
						for (String str4 : ServiceContainer.getProjectService().yS()) {
							if (str.equals(str4)) {
								continue;
							}
							arrayList.add(tp(str4, true, false, false, str2, str3, we));
						}
					}
					arrayList.add(tp(str, false, z, z2, str2, str3, we));
					return arrayList;
				}
			};
            ThreadPoolService executorService = this.executorsService;
            AaptService$d dVar = new AaptService$d(this, new AaptService$a(this, taskFactory));
            this.DW = dVar;
            executorService.execute(dVar);
        }
		catch (Throwable th2) {
        }
    }
}

