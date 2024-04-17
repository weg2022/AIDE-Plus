package io.github.zeroaicy;


import com.aide.ui.AIDEApplication;
import io.github.zeroaicy.aide.preference.ZeroAicySetting;
import io.github.zeroaicy.aide.shizuku.ShizukuUtil;
import io.github.zeroaicy.util.ContextUtil;
import io.github.zeroaicy.util.DebugUtil;
import io.github.zeroaicy.util.FileUtil;
import io.github.zeroaicy.util.Log;
import io.github.zeroaicy.util.crash.CrashApphandler;
import io.github.zeroaicy.util.reflect.ReflectPie;
import java.io.FileOutputStream;
import java.io.PrintStream;

public class ZeroAicyAIDEApplication extends AIDEApplication {

	private static final String TAG = "ZeroAicyAIDEApplication";

	public static final long now = System.currentTimeMillis();
	
	@Override
	public void onCreate() {
		super.onCreate();
		//解除反射
		Log.d(TAG, "解除反射: " + ReflectPie.reflectAll());
		
		String crashProcessName = getPackageName() + ".crash";
		String curProcessName = ContextUtil.getProcessName();
		if (crashProcessName.equals(curProcessName)) {
			return;
		}
		
		DebugUtil.debug();
		CrashApphandler.getInstance().onCreated();

		//初始化ZeroAicy设置
		ZeroAicySetting.init(this);
		//初始化Shizuku库
		ShizukuUtil.initialized(this);
		
		Log.d(TAG, "Application初始化耗时: " + (System.currentTimeMillis() - now) + "ms");
	}

	/**
	 * 当日志系统崩溃是进行修复，以便测试
	 * 依赖反射注意检查
	 */
	private void testLog() {
		boolean log = Log.getLog() == null;

		if (log) {
			Log.AsyncOutputStreamHold logHold = Log.getLogHold();
			String logCatPath = FileUtil.LogCatPath + "_test.txt";

			if (logHold == null) {
				Log.d(TAG, "LogHold 为 null");
				logHold = new Log.AsyncOutputStreamHold(logCatPath);
				ReflectPie.on(Log.class).set("mLogHold", logHold);
			}
			if (log = Log.getLog() == null) {
				Log.d(TAG, "LogHold mLog null");
				FileOutputStream createOutStream = Log.AsyncOutputStreamHold.createOutStream(logCatPath);
				Log.AsyncOutputStreamHold.AsyncOutStream asyncOutStream = new Log.AsyncOutputStreamHold.AsyncOutStream(createOutStream);
				PrintStream mLog = new PrintStream(asyncOutStream);
				ReflectPie.on(logHold).set("mLog", mLog);

			}
			ReflectPie.on(Log.class).call("printPreMsgList");
		}
	}

	static{

		//init();
	}

	private static void init() {
		ClassLoader apkClassLoader = ZeroAicyAIDEApplication.class.getClassLoader();

		ReflectPie apkClassLoaderReflectPie = ReflectPie.on(apkClassLoader);

		ClassLoader parent = apkClassLoaderReflectPie.get("parent");

		apkClassLoaderReflectPie.set("parent", new ProxyClassLoader2(parent));
	}
	public static class ProxyClassLoader2 extends ClassLoader {
		public ProxyClassLoader2(ClassLoader classLoader) {
			super(classLoader);
		}

		@Override
		protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
			Log.d("ProxyClassLoader2", "加载: " + name);
			return super.loadClass(name, resolve);
		}


	}
}
