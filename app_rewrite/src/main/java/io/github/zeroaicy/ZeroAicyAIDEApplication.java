package io.github.zeroaicy;


import android.app.Application;
import android.content.Context;
import io.github.zeroaicy.aide.preference.ZeroAicySetting;
import io.github.zeroaicy.util.DebugUtil;
import io.github.zeroaicy.util.Log;
import io.github.zeroaicy.util.crash.CrashApphandler;
import io.github.zeroaicy.util.reflect.ReflectPie;
import com.aide.ui.AIDEApplication;

public class ZeroAicyAIDEApplication extends AIDEApplication {

	private static final String TAG = "ZeroAicyAIDEApplication";

	private static long now = System.currentTimeMillis();

	static{
		DebugUtil.debug();
		FindANR.startSendMsg();
	}

	@Override
	protected void attachBaseContext(Context base){
		super.attachBaseContext(base);

		//初始化ZeroAicy设置
		ZeroAicySetting.init(base);

	}
	
	
	@Override
	public void onCreate(){
		super.onCreate();
		CrashApphandler.getInstance().onCreated();
		
		//解除反射
		Log.d(TAG, "解除反射: " + ReflectPie.reflectAll());

		Log.d(TAG, "Application初始化耗时: " + (System.currentTimeMillis() - now) + "ms");

	}
}
