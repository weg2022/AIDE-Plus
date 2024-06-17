package abcd;

//
// Decompiled by Jadx - 1509ms
//

import android.app.Activity;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Build;
import android.os.Bundle;
import java.io.File;
import java.lang.reflect.Field;
import io.github.zeroaicy.util.Log;
import io.github.zeroaicy.util.ContextUtil;

public class iy {
	private static Application DW2;
    
    private static Application DW;
    private static String FH;
    private static Object Hw = new Object();
    private static Application j6;

    public static void BT(Object obj, Intent intent) {
        vy(obj, intent, null);
    }

    public static Application DW() {
        return j6;
    }

    public static void EQ(long j, Object obj, Object obj2, Object obj3) {
    }

    private static Application FH(Object obj) {
        if (obj instanceof Application) {
            return (Application) obj;
        }
        if (obj instanceof Context) {
            Context context = (Context) obj;
            if (context.getApplicationContext() instanceof Application) {
                return (Application) context.getApplicationContext();
            }
        }
        return null;
    }

    public static String Hw() {
        return FH;
    }

    public static void J0(long j, Object obj, Object obj2, Object obj3, Object obj4, Object obj5) {
    }

    public static void J8(long j, Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6) {
        
    }

    public static void Mr(Throwable th, long j, Object obj, Object obj2, Object obj3) {
        
    }

    public static void P8(Object obj, int i, Notification notification) {
        
    }

    public static void QX(long j, Object obj, Object[] objArr) {
        
    }

    public static void U2(Throwable th, long j, Object obj, Object obj2, Object obj3, Object obj4) {
        
    }
	public static void VH(Application application) {
        try {
            synchronized (Hw) {
                if (j6 == null 
					&& Build.VERSION.SDK_INT >= 14 
					&& application != null) {
                    j6 = application;
					b00.FH(false);
                }
            }
        } catch (Throwable th) {
            b00.VH("Runtime Error", th);
        }
    }
    public static void VH(Object obj, long j, String str, String str2, boolean z, boolean z2, String str3, String str4, String str5) {
        Application application;
        try {
            synchronized (Hw) {
                if (j6 == null 
				&& Build.VERSION.SDK_INT >= 14 
				&& (application = FH(obj)) != null) {
                    j6 = application;
                }
            }
        } catch (Throwable th) {
            b00.VH("Runtime Error", th);
        }
    }

    public static void Ws(long j, Object obj, boolean z) {
        
    }

    public static void XL(Object obj, Intent intent) {
        try {
            if (DW == null || !(obj instanceof Activity)) {
                return;
            }
            wy.VH((Activity) obj, intent);
        } catch (Throwable th) {
            b00.VH("Runtime Error", th);
        }
    }

    public static void Zo(Class<?> cls) {
        try {
            ry.EQ(cls);
			for (Field field : cls.getDeclaredFields()) {
                if (field.getAnnotation(gy.class) != null) {
                    field.setAccessible(true);
                    field.set(null, Boolean.valueOf(true));
                    return;
                }
            }
        } catch (Throwable th) {
            b00.VH("Runtime Error", th);
        }
    }

    public static void a8(Throwable th, long j, Object obj, Object obj2, Object obj3, Object obj4, Object obj5) {
        
    }
	
	/**
	 * AIDE被注入的探针 catch(){}的错误
	 */
    public static void aM(Throwable th, long j, Object obj) {
        try {
			if( ContextUtil.isMainProcess()){
				// 拦截线程中断异常
				if( th instanceof java.lang.InterruptedException){
					return;
				}
				Log.d("iy.aM", obj, th);				
			}
        } catch (Throwable th2) {
            b00.VH("Runtime Error", th2);
        }
    }

    public static void ei(Object obj, IntentSender intentSender, int i, Intent intent, int i2, int i3, int i4) {
        try {
            if (DW != null) {
                ty.QX(obj, intentSender, i, intent, i2, i3, i4);
            }
        } catch (Throwable th) {
            b00.VH("Runtime Error", th);
        }
    }

    public static void er(Bundle bundle, Object obj, int i, String str, String str2, String str3, String str4) {
        try {
            if (DW != null) {
                ty.J8(bundle, obj, i, str, str2, str3, str4);
            }
        } catch (Throwable th) {
            b00.VH("Runtime Error", th);
        }
    }

    public static void gW(Object obj, int i, Notification notification) {
        try {
            if (DW == null || !(obj instanceof NotificationManager)) {
                return;
            }
            wy.gn((String) null, i, notification);
        } catch (Throwable th) {
            b00.VH("Runtime Error", th);
        }
    }

    public static void gn(long j, Object obj) {
        try {
            if (ry.j6.contains(Long.valueOf(j))) {
                py.aM(j, obj, new Object[0]);
            }
        } catch (Throwable th) {
            b00.VH("Runtime Error", th);
        }
    }

    public static void j3(Throwable th, long j, Object obj, Object obj2) {
        try {
            if (ry.DW.contains(Long.valueOf(j))) {
                py.sh(j, th, obj, new Object[]{obj2});
            }
        } catch (Throwable th2) {
            b00.VH("Runtime Error", th2);
        }
    }

    public static PendingIntent j6(Context context, int i, Intent intent, int i2) {
        if (DW != null) {
            return wy.DW(context, i, intent, i2, (Bundle) null);
        }
        return PendingIntent.getActivity(context, i, intent, i2);
    }

    public static void lg(Throwable th, long j, Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6) {
        
    }

    public static void nw(boolean z) {
        
    }

    public static void rN(Throwable th, long j, Object obj, Object[] objArr) {
        
    }

    public static void tp(long j, Object obj, Object obj2) {
        
    }

    public static void u7(long j, Object obj, int i) {
        
    }

    public static void v5(Object obj, int i, int i2, Intent intent) {
        
    }

    private static void vy(Object obj, Intent intent, Bundle bundle) {
        
    }

    public static void we(long j, Object obj, Object obj2, Object obj3, Object obj4) {
        
    }

    public static void yS(Bundle bundle, Object obj, int i, String str, String str2, Bundle bundle2) {
        
    }
}

