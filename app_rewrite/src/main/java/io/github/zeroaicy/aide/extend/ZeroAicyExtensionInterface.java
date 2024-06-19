


package io.github.zeroaicy.aide.extend;


import android.text.TextUtils;
import androidx.annotation.Keep;
import com.aide.codemodel.api.Model;
import com.aide.codemodel.api.abstraction.CodeModel;
import com.aide.codemodel.language.classfile.ClassFilePreProcessor;
import com.aide.ui.MainActivity;
import com.aide.ui.ServiceContainer;
import com.aide.ui.build.packagingservice.ExternalPackagingService;
import com.aide.ui.services.ProjectService;
import com.aide.ui.services.ZeroAicyProjectService;
import com.aide.ui.services.ZeroAicyTrainerService;
import com.aide.ui.util.BuildGradle;
import io.github.zeroaicy.aide.activity.ZeroAicyMainActivity;
import io.github.zeroaicy.aide.preference.ZeroAicySetting;
import io.github.zeroaicy.aide.services.ZeroAicyExternalPackagingService;
import io.github.zeroaicy.aide.utils.ZeroAicyBuildGradle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import android.app.Activity;

/**
 * 1.aapt2
 * 2.class解析器
 * 3.d8[打包流程]
 * 
 */

/**
 * AIDE+底包的修改点都将调用此类
 * 优点是可以随时更换实现
 */
public class ZeroAicyExtensionInterface {
	// 预扩展
	public static void createCodeModels(Model model, List<String> codeModelNames, List<CodeModel> codeModels) {
		// AIDE是根据 codeModelNames来选择是否添加 CodeModel
		// codeModelNames来源之一 ServiceContainer.Hw()
		// 但我不遵守😕😕😕，即表示所有项目都会支持添加的CodeModel
		//codeModels.add(new SmaliCodeModel(model))
	}

	//扩展接口

	/**
	 * 返回入口Activity类
	 * 主要是替换点击通知后的启动
	 */
	public static Class<? extends MainActivity> getLaunchActivityClass() {
		return ZeroAicyMainActivity.class;
	}

	//打包服务替换
	public static Class<?extends ExternalPackagingService> getExternalPackagingServiceClass() {
		return ZeroAicyExternalPackagingService.class;
	}

	//替换ClassFilePreProcessor实现
	@Keep
	public static ClassFilePreProcessor getClassFilePreProcessor() {
		return ZeroAicyClassFilePreProcessor.getSingleton();
	}
	//拦截类默认接口方法
	@Keep
	public static boolean isDefaultMethod(String methodSignature) {
		return ZeroAicyClassFilePreProcessor.isDefaultMethod(methodSignature);
	}

	//替换默认安装，true则拦截，false则不拦截
	@Keep
	public static boolean instalApp(final String apkFilePath) {
		return DistributeEvents.instalApp(apkFilePath);
	}

	//在Java项目中解除android.jar限制
	@Keep
	public static boolean isEnableAndroidApi() {
		return ZeroAicySetting.isEnableAndroidApi();
	}

	@Keep
	public static boolean isEnableADRT() {
		return ZeroAicySetting.enableADRT();
	}
	/*
	 * 控制台是否启用分屏
	 */
	@Keep
	public static boolean isEnableSplitScreenConsole() {
		return false;
	}
	/**
	 * 修改maven默认下载路径
	 */
	@Keep
	public static String getUserM2Repositories() {
		return ZeroAicySetting.getDefaultSpString("user_m2repositories", null);
	}

	/**
	 * 替换BuildGradle解析实现
	 */
	@Keep
	public static BuildGradle getBuildGradle() {
		return ZeroAicyBuildGradle.getSingleton();
	}

	@Keep
	public static ProjectService getProjectService() {
		return ZeroAicyProjectService.getSingleton();
	}

	/**
	 * 优化冷启动
	 */
	@Keep
	public static abcd.mf getTrainerService() {
		return ZeroAicyTrainerService.getSingleton();
	}

	/**
	 * 实现渠道包添加额外依赖
	 * configuration.dependencies -> getFlavorDependencies
	 */
	public static List<BuildGradle.Dependency> getFlavorDependencies(BuildGradle buildGradle) {
		if( buildGradle == null ){
			return Collections.emptyList();
		}
		
		List<BuildGradle.Dependency> defaultDependencies = buildGradle.dependencies;
		if (!(buildGradle instanceof ZeroAicyBuildGradle)) {
			return defaultDependencies;
		}

		// 渠道包
		String buildVariant = ServiceContainer.getProjectService().getBuildVariant();
		
		//Log.d("getFlavorDependencies", "buildVariant", buildVariant);
		if (TextUtils.isEmpty(buildVariant)) {
			return defaultDependencies;
		}
		
		List<BuildGradle.Dependency> flavorDependencies = ((ZeroAicyBuildGradle)buildGradle).getFlavorDependencies(buildVariant);
		if( flavorDependencies.isEmpty()){
			return defaultDependencies;			
		}
		// 合并 flavorDependencies与defaultDependencies
		List<BuildGradle.Dependency> dependencies = new ArrayList<BuildGradle.Dependency>();
		dependencies.addAll(flavorDependencies);
		dependencies.addAll(defaultDependencies);

		return dependencies;
	}
	
	// 用于修复MessageBox::gW()可能在子线程运行的情况
	public static void showDialogMessageBox(final Activity activity, final int id){
		if( activity == null ) return;
		//保证在主线程调用
		activity.runOnUiThread(new Runnable(){
				@Override
				public void run() {
					activity.showDialog(id);
				}
		});
	}
}
