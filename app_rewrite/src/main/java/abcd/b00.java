//
// Decompiled by Jadx - 871ms
//
package abcd;

import android.util.Log;
import java.io.PrintWriter;
import java.io.StringWriter;

public class b00 {
    private static boolean j6 = false;

    public static void DW(String str, String str2, Throwable th) {
        if (j6) {
            return;
        }
        /*if (uz.DW()) {
            Log.d("PROBELYTICS", str + ": " + str2, th);
            return;
        }*/
        Hw(3, str + ": " + str2, th);
    }

    public static void FH(boolean z) {
        //j6 = z;
    }

    private static void Hw(int i, String str, Throwable th) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        th.printStackTrace(printWriter);
        printWriter.flush();
        v5(i, str);
        v5(i, stringWriter.toString());
    }

    public static void VH(String str, Throwable th) {
        Zo(str, th);
    }

    private static void Zo(String str, Throwable th) {
		Log.d("Zo", str, th);
    }

    public static void j6(String str, String str2) {
        v5(3, str + ": " + str2);
    }

    private static void v5(int i, String str) {
		Log.d("v5", str);
    }
}

