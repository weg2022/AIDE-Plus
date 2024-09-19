//
// Decompiled by Jadx - 661ms
//
package com.aide.ui;

import abcd.cy;
import abcd.ey;
import abcd.fy;
import abcd.gy;
import abcd.iy;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import com.aide.ui.firebase.FireBaseLogEvent;

@cy(clazz = 1533015674575090552L, container = 1533015674575090552L, user = true)
public class PromoNotificationAlarmReceiver extends BroadcastReceiver {

    @gy
    private static boolean DW;

    @fy
    private static boolean j6;

    static {
        iy.Zo(PromoNotificationAlarmReceiver.class);
    }

    @ey(method = -1512152669004809752L)
    public PromoNotificationAlarmReceiver() {
        try {
            if (j6) {
                iy.gn(-665761837093559527L, (Object) null);
            }
        } catch (Throwable th) {
            if (DW) {
                iy.aM(th, -665761837093559527L, (Object) null);
            }
            if(th instanceof Error) throw (Error)th; else throw new Error(th);
        }
    }

    @ey(method = -3645032137445328080L)
    public static void DW(Context context) {
        try {
            if (j6) {
                iy.tp(-2982073378852694225L, (Object) null, context);
            }
            context = context.getApplicationContext();
            int flags = 0x08000000;
			if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
				flags |= PendingIntent.FLAG_IMMUTABLE;
			}
			
			((AlarmManager) context.getSystemService("alarm")).cancel(PendingIntent.getBroadcast(context, 0, new Intent(context, (Class<?>) PromoNotificationAlarmReceiver.class), flags));
        } catch (Throwable th) {
            if (DW) {
                iy.j3(th, -2982073378852694225L, (Object) null, context);
            }
            if(th instanceof Error) throw (Error)th; else throw new Error(th);
        }
    }
	//T argeting S+ (version 31 and above) requires that one of FLAG_IMMUTABLE or FLAG_MUTABLE be specified when creating a PendingIntent.
	// Strongly consider using FLAG_IMMUTABLE, only use FLAG_MUTABLE if some functionality depends on the PendingIntent being mutable, e.g.
	// if it needs to be used with inline replies or bubbles.
	//
    @ey(method = -938132574093077385L)
    public static void FH(Context context, long j, int i, String str, String str2, String str3) {
        Context context2;
        try {
            if (j6) {
                iy.QX(-592774404665771712L, (Object) null, new Object[]{context, new Long(j), new Integer(i), str, str2, str3});
            }
            context2 = context.getApplicationContext();
            try {
                Intent intent = new Intent(context2, (Class<?>) PromoNotificationAlarmReceiver.class);
                intent.putExtra("EXTRA_MSG", str);
                intent.putExtra("EXTRA_TEXT", str3);
                intent.putExtra("EXTRA_TITLE", str2);
                intent.putExtra("EXTRA_ICON", i);
                int flags = 0x08000000;
				if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
					flags |= PendingIntent.FLAG_IMMUTABLE;
				}
				
				((AlarmManager) context2.getSystemService("alarm")).set(1, j, PendingIntent.getBroadcast(context2, 0, intent, flags));
            } catch (Throwable th) {
                if (DW) {
                    iy.rN(th, -592774404665771712L, (Object) null, new Object[]{context2, new Long(j), new Integer(i), str, str2, str3});
                }
                if(th instanceof Error) throw (Error)th; else throw new Error(th);
            }
        } catch (Throwable th2) {
            context2 = context;
        }
    }

    @ey(method = 250640105624016960L)
    private void Hw(Context context, int i, String str, String str2, String str3) {
        try {
            if (j6) {
                iy.J8(-2648250657913834571L, this, context, new Integer(i), str, str2, str3);
            }
            FireBaseLogEvent.QX(str3);
            NotificationManager notificationManager = (NotificationManager) context.getSystemService("notification");
            long currentTimeMillis = System.currentTimeMillis();
            PendingIntent aj = MainActivity.aj(context);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "other");
            builder.setWhen(currentTimeMillis);
            builder.setTicker(str);
            builder.setSmallIcon(i);
            builder.setContentTitle(str2);
            builder.setContentText(str3);
            builder.setContentIntent(aj);
            builder.setAutoCancel(true);
            builder.setDefaults(1);
            j6(notificationManager, 0x27078e78, builder.build());
        } catch (Throwable th) {
            if (DW) {
                iy.lg(th, -2648250657913834571L, this, context, new Integer(i), str, str2, str3);
            }
            if(th instanceof Error) throw (Error)th; else throw new Error(th);
        }
    }

    private static void j6(NotificationManager notificationManager, int i, Notification notification) {
        notificationManager.notify(i, notification);
        iy.gW(notificationManager, i, notification);
    }

    @Override
    @ey(method = -1159758616834058325L)
    public void onReceive(Context context, Intent intent) {
        try {
            if (j6) {
                iy.EQ(629546312016223920L, this, context, intent);
            }
            Hw(context, intent.getIntExtra("EXTRA_ICON", 0), intent.getStringExtra("EXTRA_MSG"), intent.getStringExtra("EXTRA_TITLE"), intent.getStringExtra("EXTRA_TEXT"));
        } catch (Throwable th) {
            if (DW) {
                iy.Mr(th, 629546312016223920L, this, context, intent);
            }
            if(th instanceof Error) throw (Error)th; else throw new Error(th);
        }
    }
}

