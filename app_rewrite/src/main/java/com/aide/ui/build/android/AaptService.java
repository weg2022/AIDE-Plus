package com.aide.ui.build.android;

import android.content.Context;
import android.os.Build;
import com.aide.common.AppLog;
import com.aide.engine.SyntaxError;
import com.aide.ui.App;
import com.aide.ui.project.AndroidProjectSupport;
import com.aide.ui.services.AssetInstallationService;
import com.aide.ui.util.FileSystem;
import io.github.zeroaicy.aide.ui.services.ExecutorsService;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AaptService {
	// j3 EQ aM Mr
	//
    private static boolean v5;

    private AaptService$d DW;

    private e FH;

    private Context Hw;

    private final ExecutorService executorService;

    public AaptService(Context context) {
        try {

            this.Hw = context;
            this.executorService = Executors.newSingleThreadExecutor();
        }
		catch (Throwable th) {
            throw new Error(th);
        }
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

    private Map<String, List<SyntaxError>> J0(String str, Map<String, String> map, String str2) {
        int i;
        try {

            HashMap<String, List<SyntaxError>> hashMap = new HashMap<>();
            try {
                String[] split = str2.split("\n");
                int length = split.length;
                int i2 = 0;
                int i3 = 0;
                while (i3 < length) {
                    String trim = split[i3].trim();
                    if (trim.length() > 0) {
                        try {
                            int indexOf = trim.indexOf(58);
                            if (indexOf > 0) {
                                String substring = trim.substring(i2, indexOf);
                                if (FileSystem.KD(substring)) {
                                    int i4 = indexOf + 1;
                                    int indexOf2 = trim.indexOf(58, i4);
                                    if (indexOf2 < 0) {
                                        indexOf2 = trim.indexOf(32, i4);
                                    }
                                    if (indexOf2 > 0) {
                                        try {
                                            i = Integer.parseInt(trim.substring(i4, indexOf2));
                                        }
										catch (NumberFormatException unused) {
                                            i = 1;
                                        }
                                        String trim2 = trim.substring(indexOf2 + 1, trim.length()).trim();
                                        while (trim2.toLowerCase().startsWith("error:")) {
                                            trim2 = trim2.substring(6, trim2.length()).trim();
                                        }
                                        if (map.containsKey(substring)) {
                                            substring = map.get(substring);
                                            trim2 = "in generated file: " + trim2;
                                            i = 1;
                                        }
                                        SyntaxError u7 = u7("aapt", i, trim2);
                                        if (!hashMap.containsKey(substring)) {
                                            hashMap.put(substring, new ArrayList<SyntaxError>());
                                        }
										hashMap.get(substring).add(u7);
                                    }
                                }
                            }
                        }
						catch (Exception e) {
                            AppLog.v5(e);
                        }
                        if (!hashMap.containsKey(str)) {
                            hashMap.put(str, new ArrayList<SyntaxError>());
                        }
						hashMap.get(str).add(u7("aapt", 1, trim));
                    }
                    i3++;
                    i2 = 0;
                }
                return hashMap;
            }
			catch (Throwable th) {
                throw new Error(th);
            }
        }
		catch (Throwable th2) {
            throw new Error(th2);
        }
    }

    private void J8(boolean z) {
        try {

            if (this.FH != null) {
                this.FH.vJ(z);
            }
        }
		catch (Throwable th) {

            throw new Error(th);
        }
    }

    private void QX() {
        try {

            if (this.FH != null) {
                this.FH.J0();
            }
        }
		catch (Throwable th) {

            throw new Error(th);
        }
    }

    static boolean VH() {
        return v5;
    }

    private void Ws(Throwable th) {
        try {

            AppLog.v5(th);
            if (this.FH != null) {
                this.FH.g3();
            }
        }
		catch (Throwable th2) {

            throw new Error(th2);
        }
    }

    private void XL(Map<String, List<SyntaxError>> map) {
        try {

            if (this.FH != null) {
                this.FH.Mz(map);
            }
        }
		catch (Throwable th) {

            throw new Error(th);
        }
    }

    static Context Zo(AaptService aaptService) {
        return aaptService.Hw;
    }

    static boolean gn(boolean z) {
        v5 = z;
        return z;
    }

    static void j6(AaptService aaptService, boolean z) {
        aaptService.J8(z);
    }

    private AaptService$c tp(String str, boolean z, boolean z2, boolean z3, String str2, String str3, String aaptPath) {
        try {
            Map<String, List<String>> vy = App.getProjectService().vy(str);
            Map<String, String> jO = AndroidProjectSupport.jO(vy, str3);
            Map<String, String> cT = AndroidProjectSupport.cT(vy, str3);
            Map<String, String> aq = AndroidProjectSupport.aq(str, vy, str3);
            Map<String, String> FN = AndroidProjectSupport.FN(vy, str3);
            Map<String, List<String>> oY = AndroidProjectSupport.oY(vy, str3);
            Map<String, String> Z1 = AndroidProjectSupport.Z1(vy, str3);
            return new AaptService$c(this, aaptPath, str, str3, vy, AndroidProjectSupport.jw(str), AndroidProjectSupport.fY(str, str3), App.getProjectService().getAndroidJarPath(), AndroidProjectSupport.Eq(str), AndroidProjectSupport.yO(str, str2, str3), AndroidProjectSupport.kf(str), jO, cT, aq, FN, oY, Z1, z, z2, z3);
        }
		catch (Throwable th) {

            throw new Error(th);
        }
    }

    private SyntaxError u7(String str, int i, String str2) {
        try {

            SyntaxError syntaxError = new SyntaxError();
            syntaxError.jw = i;
            syntaxError.fY = 1;
            syntaxError.qp = i;
            syntaxError.k2 = 1000;
            syntaxError.zh = str + ": " + str2;
            return syntaxError;
        }
		catch (Throwable th) {

            throw new Error(th);
        }
    }

    static Map v5(AaptService aaptService, String str, Map<String, String> map, String str2) {
        return aaptService.J0(str, map, str2);
    }

    private String getAaptPath() {
        try {

            if (Build.VERSION.SDK_INT >= 29) {
                AppLog.DW("Using aapt: " + App.getContext().getApplicationInfo().nativeLibraryDir + "/libaapt.so");
                return App.getContext().getApplicationInfo().nativeLibraryDir + "/libaapt.so";
            }
            return AssetInstallationService.DW("aapt", false);
        }
		catch (Throwable th) {

            throw new Error(th);
        }
    }

    public void EQ(String str) {
        try {

            Map Z1 = AndroidProjectSupport.Z1(App.getProjectService().BT(), str);
            for (String str2 : App.getProjectService().BT().keySet()) {
                try {
                    String ye = AndroidProjectSupport.ye(str2, str);
                    FileSystem.aj(ye);
                    if (Z1.containsKey(ye)) {
                        FileSystem.aj((String) Z1.get(ye));
                    }
                    String Eq = AndroidProjectSupport.Eq(str2);
                    FileSystem.VH(Eq);
                    new File(Eq).mkdirs();
                }
				catch (Exception unused) {
                }
            }
        }
		catch (Throwable th) {

            throw new Error(th);
        }
    }

    public void Mr(String str) {
        try {

            String aaptPath = getAaptPath();
            if (this.DW != null) {
                this.DW.cancel(true);
                this.DW = null;
            }
            ArrayList<AaptService$c> arrayList = new ArrayList<>();
			for (String projectPath : App.getProjectService().yS()) {
				arrayList.add(tp(projectPath, true, false, false, null, str, aaptPath));
			}

            ExecutorService executorService = this.executorService;
            AaptService$d dVar = new AaptService$d(this, new AaptService$a(this, arrayList));
            this.DW = dVar;
            executorService.execute(dVar);
        }
		catch (Throwable th) {

            throw new Error(th);
        }
    }

    public void aM(e eVar) {
        try {

            this.FH = eVar;
        }
		catch (Throwable th) {

            throw new Error(th);
        }
    }
	public void j3(final String str, final String str2, final String str3, final boolean z, final boolean z2, final boolean z3) {

		ExecutorsService.getExecutorsService().submit(new Runnable(){
				@Override
				public void run() {
					j3_1(str, str2, str3, z, z2, z3);
				}
			});
	}
	public void j3_1(String str, String str2, String str3, boolean z, boolean z2, boolean z3) {
        try {
            String aaptPath = getAaptPath();
            if (this.DW != null) {
                this.DW.cancel(true);
                this.DW = null;
            }
            ArrayList<AaptService$c> arrayList = new ArrayList<>();

            if (z3) {
                for (String str4 : App.getProjectService().yS()) {
                    if (str.equals(str4)) {
						continue;
                    }
					arrayList.add(tp(str4, true, false, false, str2, str3, aaptPath));
                }
            }
            ArrayList<AaptService$c> arrayList3 = arrayList;
			// 添加 主项目[最顶层项目]
            arrayList3.add(tp(str, false, z, z2, str2, str3, aaptPath));

            ExecutorService executorService = this.executorService;
            AaptService$d dVar = new AaptService$d(this, new AaptService$a(this, arrayList3));
            this.DW = dVar;
            executorService.execute(dVar);
        }
		catch (Throwable th2) {
			th2.printStackTrace();
        }
	}
	public void j3_2(String str, String str2, String str3, boolean z, boolean z2, boolean z3) {
        try {
			String we = getAaptPath();
            if (this.DW != null) {
                this.DW.cancel(true);
                this.DW = null;
            }
            ArrayList<AaptService$c> arrayList2 = new ArrayList<>();
            if (z3) {
                for (String str4 : App.getProjectService().yS()) {
                    if (str.equals(str4)) {
                    } else {
						arrayList2.add(tp(str4, true, false, false, str2, str3, we));
                    }
                }
            }
            arrayList2.add(tp(str, false, z, z2, str2, str3, we));

            ExecutorService executorService = this.executorService;
            AaptService$d dVar = new AaptService$d(this, new AaptService$a(this, arrayList2));
            this.DW = dVar;
            executorService.execute(dVar);
        }
		catch (Throwable th2) {
			th2.printStackTrace();
        }
    }
}

