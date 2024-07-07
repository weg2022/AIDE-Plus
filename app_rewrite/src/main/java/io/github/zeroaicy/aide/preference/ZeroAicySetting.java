package io.github.zeroaicy.aide.preference;
import android.annotation.Nullable;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceManager;
import com.aide.ui.rewrite.R;
import java.util.LinkedHashMap;
import java.util.Map;
import android.content.pm.PackageManager;
import java.io.File;

public class ZeroAicySetting {

	private static SharedPreferences defaultSp;
	public static SharedPreferences projectServiceSharedPreferences;
	private static final Map<String, String> gradleCmdLineMap = new LinkedHashMap<String, String>();

	public static SharedPreferences getDefaultSp() {
		return defaultSp;
	}
	public static String getDefaultSpString(String key, @Nullable String defValue) {
		return defaultSp.getString(key, defValue);
	}
	
	private static boolean isWatch;
	
	public static void init(Context context) {
		if (ZeroAicySetting.defaultSp != null) return;
		
		ZeroAicySetting.defaultSp = PreferenceManager.getDefaultSharedPreferences(context);
		ZeroAicySetting.projectServiceSharedPreferences = context.getSharedPreferences("ProjectService", 0);
		//初始化一些行为
		//主题跟随系统实现
		initFollowSystem(context);
		isWatch = context.getResources().getBoolean(R.bool.watch);
		
		updateApkInstallTimes(context);
	}
	
	private static boolean isReinstall;
	public static boolean isReinstall() {
		return isReinstall;
	}
	
	private static void updateApkInstallTimes(Context context) {
		long lastInstallTime = ZeroAicySetting.defaultSp.getLong("apkInstallationTime", 0);
		long apkInstallationTime = getApkInstallationTime(context);
		if (lastInstallTime !=  apkInstallationTime) {
			isReinstall = true;
			ZeroAicySetting.defaultSp.edit().putLong("apkInstallationTime", apkInstallationTime).apply();
		}
	}

	private static long getApkInstallationTime(Context context) {
        try {
			String sourceDir = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).applicationInfo.sourceDir;
			return new File(sourceDir).lastModified();
		}
		catch (PackageManager.NameNotFoundException unused) {
			return -1L;
		}
    }
	public static boolean isWatch(){
		return isWatch;
	}
	/*
	 * 有问题的重写，
	 */
	private static void initFollowSystem(final Context context) {
		if (enableFollowSystem()) {
			//注册监听器
			ZeroAicySetting.defaultSp.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener(){
					@Override
					public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
						if ("light_theme".equals(key)) {
							//如果没有启用主题跟随系统，则不处理
							if (!enableFollowSystem()) return;

							if (isNightMode(context)) {
								//修改主题为暗主题
								if (isLightTheme()) {
									//是亮主题才修改防止循环调用
									setLightTheme(false);						
								}
							} else {
								//是暗主题才修改防止循环调用
								//修改主题为亮主题
								if (!isLightTheme()) setLightTheme(true);
							}
						}
					}
				});
		}
	}
	public static boolean isNightMode(Context context) {
		Configuration configuration = context.getResources().getConfiguration();
		return (configuration.uiMode & Configuration.UI_MODE_NIGHT_YES)  != 0;
	}

	//等效i.BT()
	public static boolean isLightTheme() {
		return ZeroAicySetting.defaultSp.getBoolean("light_theme", true);
	}
	public static void setLightTheme(boolean isLightTheme) {
		ZeroAicySetting.defaultSp.edit().putBoolean("light_theme", isLightTheme).commit();
	}

	/*
	 * 界面
	 */
	//启用Drawer
	public static boolean enableActionDrawerLayout() {
		return ZeroAicySetting.defaultSp.getBoolean("zero_aicy_enable_actionbar_drawer_layout", false);
	}
	public static boolean enableActionBarSpinner() {
		return ZeroAicySetting.defaultSp.getBoolean("zero_aicy_enable_actionbar_tab_spinner", true);
	}
	public static boolean enableFollowSystem() {
		return ZeroAicySetting.defaultSp.getBoolean("zero_aicy_enable_follow_system", false);
	}

	/*
	 * 构建运行
	 */
	public static boolean isShizukuInstaller() {
		//借用root的开关
		return ZeroAicySetting.defaultSp.getBoolean("zero_aicy_enable_shizuku_installer", true);
	}
	//使用自定义安装器安装
	public static boolean isCustomInstaller() {
		return ZeroAicySetting.defaultSp.getBoolean("zero_aicy_enable_custom_installer", false);
	}
	
	//获得自定义安装器
	public static String getApkInstallPackageName() {
		String defApkInstallValue = "com.android.packageinstaller";
		if (isCustomInstaller()) {
			defApkInstallValue = ZeroAicySetting.defaultSp.getString("zero_aicy_apk_install_package_name", defApkInstallValue);
		}
		return defApkInstallValue;
	}

	public static boolean enableADRT() {
		return ZeroAicySetting.defaultSp.getBoolean("zero_aicy_enable_adrt", false);
	}
	/*Java项目解除API限制*/
	public static boolean isEnableAndroidApi() {
		return ZeroAicySetting.defaultSp.getBoolean("zero_aicy_remove_javaproject_api_limitations", true);
	}

	/**
	 * 实验室
	 */
	public static boolean isEnableMinify(){
		return ZeroAicySetting.defaultSp.getBoolean("test_zero_aicy_enable_minify", false);
	}
	
	public static boolean isEnableAapt2() {
		return ZeroAicySetting.defaultSp.getBoolean("test_zero_aicy_enable_aapt2", true);
	}
	public static boolean isEnableViewBinding() {
		return ZeroAicySetting.defaultSp.getBoolean("test_zero_aicy_enable_view_binding", false);
	}
	public static boolean isViewBindingAndroidX() {
		return ZeroAicySetting.defaultSp.getBoolean("test_zero_aicy_enable_view_binding_use_androidx", true);
	}

	public static String getCurrentAppHome() {
		return getProjectService().getString("CurrentAppHome", null);
	}
	public static SharedPreferences getProjectService() {
		return projectServiceSharedPreferences;	
	}

	/*半成品*/
	public static boolean isEnableDataBinding() {
		return ZeroAicySetting.defaultSp.getBoolean("test_zero_aicy_enable_data_binding_use", true);
	}

	/**
	 * 未实现
	 */
	public static boolean isEnableAab() {
		return ZeroAicySetting.defaultSp.getBoolean("test_zero_aicy_enable_build_aab_apks", false);
	}
	
	
	
	public static Map<String, String> getCommands() {
		if (gradleCmdLineMap.isEmpty()) {
			gradleCmdLineMap.put("clean", "gradle clean");
			gradleCmdLineMap.put("assembleDebug", "gradle assembleDebug");
			gradleCmdLineMap.put("assembleRelease", "gradle assembleRelease");			
		}
        return gradleCmdLineMap;
    }
}
