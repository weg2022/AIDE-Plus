package io.github.zeroaicy;


import android.content.Context;
import com.aide.ui.AIDEApplication;
import io.github.zeroaicy.aide.preference.ZeroAicySetting;
import io.github.zeroaicy.aide.shizuku.ShizukuUtil;
import io.github.zeroaicy.util.ContextUtil;
import io.github.zeroaicy.util.DebugUtil;
import io.github.zeroaicy.util.Log;
import io.github.zeroaicy.util.crash.CrashApphandler;
import io.github.zeroaicy.util.reflect.ReflectPie;

public class ZeroAicyAIDEApplication extends AIDEApplication {

	private static final String TAG = "ZeroAicyAIDEApplication";
	
	private static long now = System.currentTimeMillis();
	
	static{
		DebugUtil.debug();
		//FindANR.startSendMsg();
	}
	
	@Override
	protected void attachBaseContext(Context base) {
		DebugUtil.debug(base);
		super.attachBaseContext(base);
	}
	
	
	@Override
	public void onCreate() {
		super.onCreate();
		CrashApphandler.getInstance().onCreated();
		
		//初始化ZeroAicy设置
		ZeroAicySetting.init(this);
		
		Log.d(TAG, "onCreate: " + ContextUtil.getProcessName());
		//反射全部
		Log.d(TAG, "reflectAll: " + ReflectPie.reflectAll());
		
		Log.d(TAG, "耗时: " + (System.currentTimeMillis() - now) + "ms");
		
		
	}

}
