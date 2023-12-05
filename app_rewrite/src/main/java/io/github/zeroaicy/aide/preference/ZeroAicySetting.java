package io.github.zeroaicy.aide.preference;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class ZeroAicySetting {

	public static SharedPreferences defaultSharedPreferences;
	public static SharedPreferences projectServiceSharedPreferences;
	private static final Map<String, String> gradleCmdLineMap = new LinkedHashMap<String, String>();
	
	public static Map<String, String> getCommands() {
		if( gradleCmdLineMap.isEmpty()){
			gradleCmdLineMap.put("clean", "gradle clean");
			gradleCmdLineMap.put("assembleDebug", "gradle assembleDebug");
			gradleCmdLineMap.put("assembleRelease", "gradle assembleRelease");			
		}
        return gradleCmdLineMap;
    }

	public static void init(Context context) {
		if (ZeroAicySetting.defaultSharedPreferences != null) return;

		ZeroAicySetting.defaultSharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
		ZeroAicySetting.projectServiceSharedPreferences = context.getSharedPreferences("ProjectService", 0);
		//初始化一些行为
		//主题跟随系统实现
		initFollowSystem(context);
	}

	private static void initFollowSystem(final Context context) {
		if (enableFollowSystem()) {
			//注册监听器
			ZeroAicySetting.defaultSharedPreferences.registerOnSharedPreferenceChangeListener(new SharedPreferences.OnSharedPreferenceChangeListener(){
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
							}
							else {
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
		return ZeroAicySetting.defaultSharedPreferences.getBoolean("light_theme", true);
	}
	public static void setLightTheme(boolean isLightTheme) {
		ZeroAicySetting.defaultSharedPreferences.edit().putBoolean("light_theme", isLightTheme).commit();
	}

	/*
	 * 界面
	 */
	public static boolean enableActionBarSpinner() {
		return ZeroAicySetting.defaultSharedPreferences.getBoolean("zero_aicy_enable_actionbar_tab_spinner", true);
	}
	public static boolean enableFollowSystem() {
		return ZeroAicySetting.defaultSharedPreferences.getBoolean("zero_aicy_enable_follow_system", false);
	}

	/*
	 * 构建运行
	 */
	public static boolean isShizukuInstaller() {
		//借用root的开关
		return ZeroAicySetting.defaultSharedPreferences.getBoolean("zero_aicy_enable_shizuku_installer", true);
	}
	//使用自定义安装器安装
	public static boolean isCustomInstaller() {
		return ZeroAicySetting.defaultSharedPreferences.getBoolean("zero_aicy_enable_custom_installer", false);
	}
	//获得自定义安装器
	public static String getApkInstallPackageName() {
		String defApkInstallValue = "com.android.packageinstaller";
		if (isCustomInstaller()) {
			defApkInstallValue = ZeroAicySetting.defaultSharedPreferences.getString("zero_aicy_apk_install_package_name", defApkInstallValue);
		}
		return defApkInstallValue;
	}

	public static boolean enableADRT() {
		return ZeroAicySetting.defaultSharedPreferences.getBoolean("zero_aicy_enable_adrt", false);
	}
	/*Java项目解除API限制*/
	public static boolean isEnableAndroidApi() {
		return ZeroAicySetting.defaultSharedPreferences.getBoolean("zero_aicy_remove_javaproject_api_limitations", false);
	}

	/**
	 * 实验室
	 */
	public static boolean isEnableD8() {
		return ZeroAicySetting.defaultSharedPreferences.getBoolean("test_zero_aicy_enable_d8", true);
	}
	public static boolean isEnableAapt2() {
		return ZeroAicySetting.defaultSharedPreferences.getBoolean("test_zero_aicy_enable_aapt2", false);
	}
	public static boolean isEnableViewBinding() {
		return ZeroAicySetting.defaultSharedPreferences.getBoolean("test_zero_aicy_enable_view_binding", false);
	}
	public static boolean isViewBindingAndroidX() {
		return ZeroAicySetting.defaultSharedPreferences.getBoolean("test_zero_aicy_enable_view_binding_use_androidx", true);
	}

	public static String getCurrentAppHome() {
		return getProjectService().getString("CurrentAppHome", null);
	}
	public static SharedPreferences getProjectService() {
		return projectServiceSharedPreferences;	
	}

	/*半成品*/
	public static boolean isEnableDataBinding() {
		return ZeroAicySetting.defaultSharedPreferences.getBoolean("test_zero_aicy_enable_data_binding_use", true);
	}

	/**
	 * 未实现
	 */
	public static boolean isEnableAab() {
		return ZeroAicySetting.defaultSharedPreferences.getBoolean("test_zero_aicy_enable_build_aab_apks", false);
	}
}
