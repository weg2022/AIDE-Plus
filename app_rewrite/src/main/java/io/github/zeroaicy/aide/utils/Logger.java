package io.github.zeroaicy.aide.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import io.github.zeroaicy.util.Log;
import android.content.Context;
import android.content.Intent;
import java.util.ArrayList;
import java.util.List;
import com.google.firebase.analytics.FirebaseAnalytics;

public class Logger implements Runnable {
	public static Logger sLogger = new Logger();


	public static synchronized void setLogger(Logger sLogger) {
		Logger.sLogger = sLogger;
	}

	public static Logger getLogger() {
		return sLogger;
	}

	//public String hostPackageName;

	public Logger() {
	}

	private Thread sThread;
	public void start() {
		if (sThread != null) return;
		sThread = new Thread(this);
		sThread.start();
	}
	
	
	@Override
    public void run() {
        try {
			Runtime.getRuntime().exec("logcat -c");
            Process exec = Runtime.getRuntime().exec("logcat -v threadtime");
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(exec.getInputStream()), 20);
            
			ArrayList<String> logs = new ArrayList<>();
			String[] empty = new String[0];
			while (true) {
				
				String readLine = bufferedReader.readLine();
				if( readLine == null){
					continue;
				}
                Log.println(readLine);
				
				logs.add(readLine);
				if( logs.size() > 30){
					sendLogcatLines(logs.toArray(empty));
					logs.clear();
				}
            }
        }
		catch (IOException e) {
			e.printStackTrace();
        }
    }
	private static Context context;
	private static String debuggerPackageName;
	
	private static boolean isSendLogcat;
	
	public static void onContext(Context context, String debuggerPackageName) {
		Logger.context = context;
		Logger.debuggerPackageName = debuggerPackageName;
		Logger.isSendLogcat = true;
	}

	public static void sendConnect(String str) {
		Intent intent = new Intent();
		intent.setPackage(debuggerPackageName);
		intent.setAction("com.adrt.CONNECT");
		intent.putExtra("package", str);
		context.sendBroadcast(intent);
	}

	public static void sendStop(String str) {
		Intent intent = new Intent();
		intent.setPackage(debuggerPackageName);
		intent.setAction("com.adrt.STOP");
		intent.putExtra("package", str);
		context.sendBroadcast(intent);
	}

	public static void sendBreakpointHit(String str, ArrayList<String> arrayList, ArrayList<String> arrayList2, ArrayList<String> arrayList3, ArrayList<String> arrayList4, ArrayList<String> arrayList5, ArrayList<String> arrayList6) {
		Intent intent = new Intent();
		intent.setPackage(debuggerPackageName);
		intent.setAction("com.adrt.BREAKPOINT_HIT");
		intent.putExtra("package", str);
		intent.putExtra("variables", arrayList);
		intent.putExtra("variableValues", arrayList2);
		intent.putExtra("variableKinds", arrayList3);
		intent.putExtra("stackMethods", arrayList4);
		intent.putExtra("stackLocations", arrayList5);
		intent.putExtra("stackLocationKinds", arrayList6);
		context.sendBroadcast(intent);
	}

	public static void sendFields(String str, String str2, ArrayList<String> arrayList, ArrayList<String> arrayList2, ArrayList<String> arrayList3) {
		Intent intent = new Intent();
		intent.setPackage(debuggerPackageName);
		intent.setAction("com.adrt.FIELDS");
		intent.putExtra("package", str);
		intent.putExtra("path", str2);
		intent.putExtra("fields", arrayList);
		intent.putExtra("fieldValues", arrayList2);
		intent.putExtra("fieldKinds", arrayList3);
		context.sendBroadcast(intent);
	}

	public static void sendLogcatLines(String[] logs) {
		Intent intent = new Intent();
		intent.setPackage(debuggerPackageName);
		intent.setAction("com.adrt.LOGCAT_ENTRIES");
		intent.putExtra("lines", logs);
		context.sendBroadcast(intent);
	}
	
}
