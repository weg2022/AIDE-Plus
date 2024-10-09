//
// Decompiled by Jadx - 931ms
//
package com.aide.common;

import io.github.zeroaicy.util.Log;
import io.github.zeroaicy.util.ContextUtil;
import io.github.zeroaicy.aide.preference.ZeroAicySetting;

/**
 * AIDE日志类
 */
public class AppLog{
    public AppLog(){}
	// (所有)共存版作为测试版本 日志全开ε٩(๑> ₃ <)۶з
	//public static boolean isPrintLog = !ContextUtil.getPackageName().equals("io.github.zeroaicy.aide");

    public static void DW(String str){
		if ( isPrintLog() ) Log.d("AIDE", str);
    }

	private static boolean isPrintLog(){
		return ZeroAicySetting.isEnableDetailedLog();
	}

    public static void FH(String msg){
		Log.e("AIDE", msg);
    }

    public static void Hw(String str, Throwable th){
		Log.e("AIDE", str, th);
    }

    public static void VH(String msg){
        if ( isPrintLog() ) Log.i("AIDE", msg);
    }

    public static void Zo(Object obj, String msg){
		if ( isPrintLog() ) Log.i("AIDE", obj.getClass().getName() + "." + msg);
    }

    public static void gn(String str){
		Log.w("AIDE", str);
    }

    public static void j6(Throwable th){
		Log.e("AIDE", th.toString(), th);

    }

    public static void v5(Throwable th){
        Log.e("AIDE", th.toString(), th);
    }

	public static void d(String TAG, Object... objects){
		if ( isPrintLog() ) Log.d(TAG, objects);
    }

	public static void i(String TAG, String msg){
		if ( isPrintLog() ) Log.i(TAG, msg);
    }
	public static void i(String msg){
		if ( isPrintLog() ) Log.i("AIDE", msg);
    }

	public static void d(String TAG, String msg){
		if ( isPrintLog() ) Log.d(TAG, msg);
    }
	public static void d(String TAG, String msg, Object... args){
		if ( isPrintLog() ) Log.d(TAG, String.format(msg, args));
    }

	public static void d(String msg){
		if ( isPrintLog() ) Log.d("AIDE", msg);
    }

	// 警告⚠️不拦截
	public static void w(String TAG, String msg){
		Log.w(TAG, msg);
	}
	public static void w(String msg){
		Log.w("AIDE", msg);
	}
	public static void e(String TAG, String msg){
		Log.e(TAG, msg);
	}
	public static void e(String msg){
		Log.e("AIDE", msg);
    }

	public static void e(String msg, Throwable th){
		Log.e("AIDE", msg, th);
    }
	public static void e(String TAG, String msg, Throwable th){
		Log.e(TAG, msg, th);
    }


	public static void println_d(String msg){
		if ( isPrintLog() ) Log.println(msg);
    }

	public static void println_d(String msg, Object... args){
		String format = String.format(msg, args);
		if ( isPrintLog() ) Log.println(format);
    }


	public static void println_e(String msg){
		Log.println(msg);
    }



}

