package com.google.firebase.analytics;
import androidx.annotation.Keep;
import android.content.Context;
import android.os.Bundle;
import android.app.Activity;

public class FirebaseAnalytics {
	private static volatile FirebaseAnalytics DW;

	//private final zzbw j6;

	//private FirebaseAnalytics(zzbw zzbw) {}
	private FirebaseAnalytics() {}
	
	public final void DW(boolean p) {}

	public final void FH(String string, String string1) {}

	@Keep
	public final String getFirebaseInstanceId() {
		return "";
	}
	
	static FirebaseAnalytics instance;
	@Keep
	public static FirebaseAnalytics getInstance(Context context) {
		if( instance == null){
			DW = instance = new FirebaseAnalytics();
		}
		return instance;
	}

	public final void j6(String string, Bundle bundle) {}

	@Keep
	public final void setCurrentScreen(Activity activity, String string, String string1) {}
	
}
