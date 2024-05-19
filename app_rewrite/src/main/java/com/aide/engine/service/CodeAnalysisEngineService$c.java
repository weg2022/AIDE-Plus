//
// Decompiled by Jadx - 882ms
//
package com.aide.engine.service;

import abcd.cy;
import abcd.dy;
import abcd.ey;
import abcd.fy;
import abcd.gy;
import abcd.hy;
import abcd.iy;
import android.os.Process;
import android.os.RemoteException;
import com.aide.common.AppLog;
import com.aide.engine.c;
import io.github.zeroaicy.util.Log;

@cy(clazz = -146508955040731664L, container = 2006115082471780797L, user = true)
class CodeAnalysisEngineService$c implements c {

    @fy
    private static boolean DW;

    @gy
    private static boolean FH;

    @dy(field = 2173384649932939017L)
    @hy
    final CodeAnalysisEngineService j6;

    static {
        iy.Zo(CodeAnalysisEngineService$c.class);
    }

    @ey(method = 4468888686078178473L)
    CodeAnalysisEngineService$c(CodeAnalysisEngineService codeAnalysisEngineService) {
        this.j6 = codeAnalysisEngineService;
    }

    @ey(method = 3430457549353526812L)
    public void MP(Throwable th) {
        try {
            AppLog.v5(th);
			Log.e("CodeAnalysisEngineService", "CodeAnalysis", th);
            if (CodeAnalysisEngineService.DW(this.j6) != null) {
                try {
                    CodeAnalysisEngineService.DW(this.j6).rJ();
                } catch (Exception unused) {
                    AppLog.v5(th);
                }
            }
        } catch (Throwable th2) {
        }
    }

    @ey(method = 3523232398162695140L)
    public void oa() {
        try {
            if (DW) {
                iy.gn(-2147258966725242565L, this);
            }
            AppLog.FH("Engine process killed after OOM");
            if (CodeAnalysisEngineService.DW(this.j6) != null) {
                try {
                    CodeAnalysisEngineService.DW(this.j6).oa();
                } catch (Exception e) {
                    AppLog.v5(e);
                }
            }
            Process.killProcess(Process.myPid());
        } catch (Throwable th) {
            if (FH) {
                iy.aM(th, -2147258966725242565L, this);
            }
        }
    }

    @ey(method = -5871885491971028448L)
    public void qP() {
        try {
            if (DW) {
                iy.gn(-671623290527996957L, this);
            }
            AppLog.DW("Engine process killed after shutdown");
            Process.killProcess(Process.myPid());
        } catch (Throwable th) {
            if (FH) {
                iy.aM(th, -671623290527996957L, this);
            }
        }
    }
}

