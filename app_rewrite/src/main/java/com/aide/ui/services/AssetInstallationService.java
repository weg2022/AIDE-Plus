//
// Decompiled by Jadx - 863ms
//
package com.aide.ui.services;

import abcd.ey;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.os.Build;
import com.aide.common.AppLog;
import com.aide.common.StreamUtilities;
import com.aide.ui.App;
import com.aide.ui.util.FileSystem;
import io.github.zeroaicy.aide.ui.services.ExecutorsService;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.GregorianCalendar;

public class AssetInstallationService {

    private String DW;

    private String FH;

    private String Hw;

    private String j6;

    private String v5;

    public AssetInstallationService() {

    }

    public static String DW(String str, boolean z) {
		return FH(str, z, false);
    }

    public static String FH(String str, boolean z, boolean z2) {
		j6(str, z, z2);
		return VH(str, z);
    }

    private static InputStream J0(String str) {
		if (App.Sf() && Build.VERSION.SDK_INT >= 20) {
			return J8(str, str + ".jet", "x86-pie" + File.separator + str, "x86" + File.separator + str + ".jet");
		}
		if (App.Sf()) {
			return J8(str, str + ".jet", "x86" + File.separator + str, "x86" + File.separator + str + ".jet");
		}
		if (Build.VERSION.SDK_INT >= 20) {
			return J8(str, str + ".jet", "armeabi-pie" + File.separator + str, "armeabi" + File.separator + str + ".jet");
		}
		return J8(str, str + ".jet", "armeabi" + File.separator + str, "armeabi" + File.separator + str + ".jet");
    }

    private static InputStream J8(String... strArr) {
        try {
			AssetManager assets = App.getContext().getAssets();
			for (String str : strArr) {
				InputStream QX = QX(assets, str);
				if (QX != null) {
					return QX;
				}
			}
			throw new FileNotFoundException("Asset " + strArr[0] + " not found.");
		}
		catch (FileNotFoundException e) {
			throw new Error(e);
		}
    }

    private static InputStream QX(AssetManager assetManager, String str) {
		try {
			return assetManager.open(str);
		}
		catch (Exception unused) {
			return null;
		}
    }

    private static String VH(String str, boolean z) {
		String str2 = FileSystem.yS() + "/.aide";
		if (!new File(str2).exists()) {
			new File(str2).mkdirs();
		}
		return str2 + File.separator + str;
    }

    private static boolean Ws(String str, boolean z) {
		String VH2 = VH(str, z);
		SharedPreferences sharedPreferences = App.getContext().getSharedPreferences("AssetInstallationService", 0);
		long j = sharedPreferences.getLong("ApkVersion", 0L);
		long Zo2 = Zo();
		long j2 = sharedPreferences.getInt("AndroidVersion", 0);
		if (!FileSystem.DW(j, Zo2) || j2 != Build.VERSION.SDK_INT) {
			SharedPreferences.Editor edit = sharedPreferences.edit();
			edit.clear();
			edit.putLong("ApkVersion", Zo2);
			edit.putInt("AndroidVersion", Build.VERSION.SDK_INT);
			edit.commit();
		}
		if (sharedPreferences.getBoolean(str, false) && new File(VH2).exists()) {
			return false;
		}
		SharedPreferences.Editor edit2 = sharedPreferences.edit();
		edit2.putBoolean(str, true);
		edit2.commit();
		try {
			if (new File(VH2).exists()) {
				if (StreamUtilities.j6(J0(str), new FileInputStream(VH2))) {
					return false;
				}
			}
			return true;
		}
		catch (IOException unused) {
			return true;
		}
    }

    private static long Zo() {
        try {
			return new File(App.getContext().getPackageManager().getPackageInfo(App.getContext().getPackageName(), 0).applicationInfo.sourceDir).lastModified();
		}
		catch (PackageManager.NameNotFoundException unused) {
			return -1L;
		}
    }

    private static boolean j6(String str, boolean z, boolean z2) {
        if (Ws(str, z)) {
			try {
				String VH2 = VH(str, z);
				InputStream J0 = J0(str);
				if (z2) {
					FileSystem.u7(J0, VH2, false);
				} else {
					new File(VH2).getParentFile().mkdirs();
					StreamUtilities.Zo(J0, new FileOutputStream(VH2));
				}
				AppLog.DW("Extracted asset " + str);
				return true;
			}
			catch (IOException e) {
				AppLog.v5(e);
			}
		}
		return false;
    }

    public String EQ() {
        String VH2 = VH("weardebug.keystore", true);
		if (!new File(VH2).exists()) {
			GregorianCalendar gregorianCalendar = new GregorianCalendar();
			gregorianCalendar.add(1, 100);
			App.j3().Hw(VH2, "xxxxxx", "weardebug", "xxxxxx", new GregorianCalendar().getTime(), gregorianCalendar.getTime(), BigInteger.ONE, "Wear Debug", "", "", "", "", "");
		}
		return VH2;
    }

    @ey(method = -7359206029253165544L)
    public String Hw() {
        return this.DW;
    }

    @ey(method = 380133922599977211L)
    public String getAnnotationsJarPath() {
        return this.FH;
    }

    @ey(method = 5870138251101009991L)
    public String getJavaScriptAPIJsPath() {
        return this.j6;
    }

    @ey(method = -909859927891154784L)
    public String gn() {
        return this.Hw;
    }

    public String tp() {
        if (this.v5 == null) {
			this.v5 = DW("merger.zip", true);
		}
		return this.v5;
    }
	ExecutorsService executorsService = ExecutorsService.getExecutorsService();
    public void we() {
		executorsService.submit(new Runnable(){
				@Override
				public void run() {
					try {
						AssetInstallationService.this.j6 = DW("JavaScriptAPI.js", true);
						AssetInstallationService.this.DW = DW("android.jar", true);
						AssetInstallationService.this.FH = DW("annotations.jar", true);
						AssetInstallationService.this.Hw = DW("framework.aidl", true);
						
					}
					catch (Throwable e) {
						e.printStackTrace();
					}
				}
			});
    }
}

