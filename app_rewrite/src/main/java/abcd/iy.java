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

public class iy {
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
        try {
            if (ry.j6.contains(Long.valueOf(j))) {
                py.aM(j, obj, new Object[]{obj2, obj3});
            }
        } catch (Throwable th) {
            b00.VH("Runtime Error", th);
        }
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
        try {
            if (ry.j6.contains(Long.valueOf(j))) {
                py.aM(j, obj, new Object[]{obj2, obj3, obj4, obj5});
            }
        } catch (Throwable th) {
            b00.VH("Runtime Error", th);
        }
    }

    public static void J8(long j, Object obj, Object obj2, Object obj3, Object obj4, Object obj5, Object obj6) {
        try {
            if (ry.j6.contains(Long.valueOf(j))) {
                py.aM(j, obj, new Object[]{obj2, obj3, obj4, obj5, obj6});
            }
        } catch (Throwable th) {
            b00.VH("Runtime Error", th);
        }
    }

    public static void Mr(Throwable th, long j, Object obj, Object obj2, Object obj3) {
        try {
            if (ry.DW.contains(Long.valueOf(j))) {
                py.sh(j, th, obj, new Object[]{obj2, obj3});
            }
        } catch (Throwable th2) {
            b00.VH("Runtime Error", th2);
        }
    }

    public static void P8(Object obj, int i, Notification notification) {
        try {
            if (DW == null || !(obj instanceof Service)) {
                return;
            }
            wy.gn((String) null, i, notification);
        } catch (Throwable th) {
            b00.VH("Runtime Error", th);
        }
    }

    public static void QX(long j, Object obj, Object[] objArr) {
        try {
            if (ry.j6.contains(Long.valueOf(j))) {
                py.aM(j, obj, objArr);
            }
        } catch (Throwable th) {
            b00.VH("Runtime Error", th);
        }
    }

    public static void U2(Throwable th, long j, Object obj, Object obj2, Object obj3, Object obj4) {
        try {
            if (ry.DW.contains(Long.valueOf(j))) {
                py.sh(j, th, obj, new Object[]{obj2, obj3, obj4});
            }
        } catch (Throwable th2) {
            b00.VH("Runtime Error", th2);
        }
    }

    public static void VH(Object obj, long j, String str, String str2, boolean z, boolean z2, String str3, String str4, String str5) {
        Application FH2;
        try {
            synchronized (Hw) {
                if (j6 == null && Build.VERSION.SDK_INT >= 14 && (FH2 = FH(obj)) != null) {
                    j6 = FH2;
                    FH = str;
                    b00.FH(z2);
                    d00.VH(str2);
                    zz.j6();
                    zy.wc();
                    if (!yz.Hw()) {
                        if (uz.DW()) {
                            zz.DW(0x4c3b02ab, 0x0108008a, "probelytics requires INTERNET permission");
                        }
                        return;
                    }
                    File DW2 = d00.DW(FH2, str5);
                    if (DW2 != null && DW2.exists()) {
                        return;
                    }
                    DW = FH2;
                    boolean FH3 = tz.FH();
                    rz.Hw(z);
                    sz.VH();
                    py.gn(FH3);
                    oz.J0(FH3, j, str3, str4);
                    sy.XL();
                    az.j6();
                    ez.FH(str5);
                    xy.Hw();
                    py.Ws();
                    uy.DW();
                    ty.we();
                }
            }
        } catch (Throwable th) {
            b00.VH("Runtime Error", th);
        }
    }

    public static void Ws(long j, Object obj, boolean z) {
        try {
            if (ry.j6.contains(Long.valueOf(j))) {
                py.aM(j, obj, new Object[]{Boolean.valueOf(z)});
            }
        } catch (Throwable th) {
            b00.VH("Runtime Error", th);
        }
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
        try {
            if (ry.DW.contains(Long.valueOf(j))) {
                py.sh(j, th, obj, new Object[]{obj2, obj3, obj4, obj5});
            }
        } catch (Throwable th2) {
            b00.VH("Runtime Error", th2);
        }
    }

    public static void aM(Throwable th, long j, Object obj) {
        try {
			Log.d("iy.aM", obj, th);
            if (ry.DW.contains(Long.valueOf(j))) {
                py.sh(j, th, obj, new Object[0]);
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
        try {
            if (ry.DW.contains(Long.valueOf(j))) {
                py.sh(j, th, obj, new Object[]{obj2, obj3, obj4, obj5, obj6});
            }
        } catch (Throwable th2) {
            b00.VH("Runtime Error", th2);
        }
    }

    public static void nw(boolean z) {
        try {
            if (DW == null || rz.v5() == z) {
                return;
            }
            rz.VH(z);
            py.SI(z);
        } catch (Throwable th) {
            b00.VH("Runtime Error", th);
        }
    }

    public static void rN(Throwable th, long j, Object obj, Object[] objArr) {
        try {
            if (ry.DW.contains(Long.valueOf(j))) {
                py.sh(j, th, obj, objArr);
            }
        } catch (Throwable th2) {
            b00.VH("Runtime Error", th2);
        }
    }

    public static void tp(long j, Object obj, Object obj2) {
        try {
            if (ry.j6.contains(Long.valueOf(j))) {
                py.aM(j, obj, new Object[]{obj2});
            }
        } catch (Throwable th) {
            b00.VH("Runtime Error", th);
        }
    }

    public static void u7(long j, Object obj, int i) {
        try {
            if (ry.j6.contains(Long.valueOf(j))) {
                py.aM(j, obj, new Object[]{Integer.valueOf(i)});
            }
        } catch (Throwable th) {
            b00.VH("Runtime Error", th);
        }
    }

    public static void v5(Object obj, int i, int i2, Intent intent) {
        try {
            if (DW != null) {
                ty.EQ(obj, i, i2, intent);
            }
        } catch (Throwable th) {
            b00.VH("Runtime Error", th);
        }
    }

    private static void vy(Object obj, Intent intent, Bundle bundle) {
        try {
            if (DW == null || !(obj instanceof Context)) {
                return;
            }
            vy.j6((Context) obj, intent, bundle);
        } catch (Throwable th) {
            b00.VH("Runtime Error", th);
        }
    }

    public static void we(long j, Object obj, Object obj2, Object obj3, Object obj4) {
        try {
            if (ry.j6.contains(Long.valueOf(j))) {
                py.aM(j, obj, new Object[]{obj2, obj3, obj4});
            }
        } catch (Throwable th) {
            b00.VH("Runtime Error", th);
        }
    }

    public static void yS(Bundle bundle, Object obj, int i, String str, String str2, Bundle bundle2) {
        try {
            if (DW != null) {
                ty.Ws(bundle, obj, i, str, str2, bundle2);
            }
        } catch (Throwable th) {
            b00.VH("Runtime Error", th);
        }
    }
}

