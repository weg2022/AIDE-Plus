//
// Decompiled by Jadx - 882ms
//
package com.aide.engine.service;

import android.os.Process;
import com.aide.common.AppLog;
import com.aide.engine.c;
import io.github.zeroaicy.util.Log;

class CodeAnalysisEngineService$c implements c {

    final CodeAnalysisEngineService j6;
	
    CodeAnalysisEngineService$c(CodeAnalysisEngineService codeAnalysisEngineService) {
        this.j6 = codeAnalysisEngineService;
    }

    public void MP(Throwable th) {
        try {
            AppLog.v5(th);
			
			Log.e("CodeAnalysisEngineService", "CodeAnalysis", th);
            
			if (CodeAnalysisEngineService.getEngineListener(this.j6) != null) {
                try {
                    CodeAnalysisEngineService.getEngineListener(this.j6).rJ();
                } catch (Exception unused) {
                    AppLog.v5(th);
                }
            }
			
        } catch (Throwable th2) {
        }
    }

    public void oa() {
        try {
            AppLog.FH("Engine process killed after OOM");
            if (CodeAnalysisEngineService.getEngineListener(this.j6) != null) {
                try {
                    CodeAnalysisEngineService.getEngineListener(this.j6).oa();
                } catch (Exception e) {
                    AppLog.v5(e);
                }
            }
            Process.killProcess(Process.myPid());
			
			Process.killProcess(Process.myPid());
			
        } catch (Throwable th) {}
    }

    public void qP() {
        try {
            AppLog.DW("Engine process killed after shutdown");
            Process.killProcess(Process.myPid());
        } catch (Throwable th) {
        }
    }
}

