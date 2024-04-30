//
// Decompiled by Jadx - 931ms
//
package com.aide.common;

import io.github.zeroaicy.util.Log;

/**
 * AIDE日志类
 */
public class AppLog {
    public AppLog() {}
	
	private static final boolean isPrintLog = false;
    public static void DW(String str) {
		if( isPrintLog ) Log.d("AIDE", str);
    }

    public static void FH(String str) {
		if( isPrintLog ) Log.e("AIDE", str);
    }

    public static void Hw(String str, Throwable th) {
		if( isPrintLog ) Log.e("AIDE", str, th);
    }

    public static void VH(String str) {
        if( isPrintLog ) Log.i("AIDE", str);
    }

    public static void Zo(Object obj, String str) {
		if( isPrintLog ) Log.i("AIDE", obj.getClass().getName() + "." + str);
    }

    public static void gn(String str) {
		if( isPrintLog ) Log.w("AIDE", str);
    }

    public static void j6(Throwable th) {
		if( isPrintLog ) Log.e("AIDE", th.toString(), th);
		
    }

    public static void v5(Throwable th) {
        if( isPrintLog ) Log.e("AIDE", th.toString(), th);
    }
}

