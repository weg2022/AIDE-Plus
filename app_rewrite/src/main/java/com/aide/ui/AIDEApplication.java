package com.aide.ui;

//
// Decompiled by Jadx - 508ms
//

import abcd.cy;
import abcd.ey;
import abcd.fy;
import abcd.gy;
import abcd.iy;
import android.app.ActivityManager;
import android.os.Process;
import android.support.multidex.MultiDexApplication;
import com.aide.ui.marketing.b;

@cy(clazz = 1065319494111370971L, container = 1065319494111370971L, user = true)
public class AIDEApplication extends MultiDexApplication {
    @fy
    private static boolean WB;
    @gy
    private static boolean mb;

    static {
        iy.Zo(AIDEApplication.class);
    }

    @ey(method = -121640325556619400L)
    public AIDEApplication() {
        try {
            if (WB) {
                iy.gn(-5416299027244692041L, (Object) null);
            }
        } catch (Throwable th) {
            if (mb) {
                iy.aM(th, -5416299027244692041L, (Object) null);
            }
        }
    }

    /* JADX WARN: Multi-variable type inference failed */
    @ey(method = 912515893424268100L)
    private boolean DW() {
        try {
            if (WB) {
                iy.gn(-3468928647262445676L, this);
            }
            int myPid = Process.myPid();
            for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : ((ActivityManager) getSystemService("activity")).getRunningAppProcesses()) {
                if (runningAppProcessInfo.pid == myPid) {
                    return !runningAppProcessInfo.processName.contains(":");
                }
            }
            return true;
        } catch (Throwable th) {
            if (mb) {
                iy.aM(th, -3468928647262445676L, this);
            }
        }
		return false;
    }

    @ey(method = -3049527737687236309L)
    private void j6() {
        try {
            if (WB) {
                iy.gn(-2913545304546855693L, this);
            }
        } catch (Throwable th) {
            if (mb) {
                iy.aM(th, -2913545304546855693L, this);
            }
        }
    }


    @ey(method = -1658610373383307725L)
    public void onCreate() {
		super.onCreate();
		try {
            iy.VH(this, 1615898270903L, "androidRelease", "fe5d2222", true, false, "https://probes.probelytics.com/project/iF0snISTR5Ko3hbTUY88bg/audience/dev/current.probes", "https://probes.probelytics.com/project/iF0snISTR5Ko3hbTUY88bg/audience/public/current.probes", "https://ingest.probelytics.com/api/report/v1alpha1/data/project/iF0snISTR5Ko3hbTUY88bg");
            if (WB) {
                iy.gn(-1010737715683701269L, this);
            }

            j6();
			//更改包名[共存版]
            App.lp("com.aide.ui");
            if (DW()) {
                if (App.P8.equals("com.aide.ui")) {
                    b.j6(this, 43453, 0x7f070078, "New version of AIDE installed", "Check out what's new!", MainActivity.Mz(this));
                }
				else if (App.P8.equals("com.aide.web")) {
                    b.j6(this, 43453, 0x7f07007f, "New version of AIDE Web installed", "Check out what's new!", MainActivity.Mz(this));
                }
            }
        } catch (Throwable th) {
            if (mb) {
                iy.aM(th, -1010737715683701269L, this);
            }
        }
    }
}

