


package io.github.zeroaicy.aide.extend;


import abcd.e4;
import android.app.Activity;
import android.text.TextUtils;
import androidx.annotation.Keep;
import com.aide.codemodel.api.Entity;
import com.aide.codemodel.api.EntitySpace;
import com.aide.codemodel.api.Member;
import com.aide.codemodel.api.Model;
import com.aide.codemodel.api.SyntaxTree;
import com.aide.codemodel.api.Type;
import com.aide.codemodel.api.abstraction.CodeModel;
import com.aide.codemodel.api.abstraction.Syntax;
import com.aide.codemodel.api.util.SyntaxTreeUtils;
import com.aide.codemodel.language.classfile.ClassFilePreProcessor;
import com.aide.codemodel.language.java.JavaCodeAnalyzer;
import com.aide.codemodel.language.java.JavaCodeModel;
import com.aide.codemodel.language.java.JavaCodeModelPro;
import com.aide.codemodel.language.java.JavaLanguage;
import com.aide.codemodel.language.java.JavaParser;
import com.aide.codemodel.language.java.JavaParserPro;
import com.aide.codemodel.language.kotlin.KotlinCodeModel;
import com.aide.codemodel.language.smali.SmaliCodeModel;
import com.aide.engine.SyntaxStyleType;
import com.aide.ui.MainActivity;
import com.aide.ui.ServiceContainer;
import com.aide.ui.build.packagingservice.ExternalPackagingService;
import com.aide.ui.project.internal.GradleTools;
import com.aide.ui.services.ProjectService;
import com.aide.ui.services.ZeroAicyProjectService;
import com.aide.ui.services.ZeroAicyTrainerService;
import com.aide.ui.util.BuildGradle;
import com.aide.ui.util.FileSystem;
import io.github.zeroaicy.aide.activity.ZeroAicyMainActivity;
import io.github.zeroaicy.aide.preference.ZeroAicySetting;
import io.github.zeroaicy.aide.services.ZeroAicyExternalPackagingService;
import io.github.zeroaicy.aide.utils.ZeroAicyBuildGradle;
import io.github.zeroaicy.util.Log;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import com.aide.codemodel.api.Parser.a;
import com.aide.codemodel.api.Parser;
import io.github.zeroaicy.util.ContextUtil;
import com.aide.common.AppLog;
import com.aide.codemodel.language.classfile.JavaBinaryLanguage;
import com.aide.codemodel.api.abstraction.Language;

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
	// 预扩展 由CodeModelFactory调用 采用[源码覆盖模式]
	public static void createCodeModels(Model model, List<String> codeModelNames, List<CodeModel> codeModels) {
		// AIDE是根据 codeModelNames来选择是否添加 CodeModel
		// codeModelNames来源之一 ServiceContainer.Hw()
		// 但我不遵守😕😕😕，即表示所有项目都会支持添加的CodeModel
		codeModels.add(new SmaliCodeModel(model));
		codeModels.add(new KotlinCodeModel(model));

		/* 覆盖JavaCodeModel
		 if (AppLog.isPrintLog) {
		 // 只在共存版生效
		 if (codeModels.get(0) instanceof JavaCodeModel) {
		 codeModels.set(0, new JavaCodeModelPro(model));
		 }
		 }
		 //*/
	}
	/**
	 * 测试 仅在共存版会被SyntaxTree::declareAttrType()调用
	 */
	@Keep
	public static void declareAttrType(SyntaxTree syntaxTree, int node, Type type) {

		if (type == null) return;
		// 禁用
		if (type != null) return;
		SyntaxTreeUtils.printlnNodeAttr(syntaxTree, "declareAttrType node ", node);
		SyntaxTreeUtils.printNode(syntaxTree, node);
		System.out.println(type);

		Log.printlnStack(2, 5);

		System.out.println("************************");
		System.out.println();


	}

	/**
	 * 过滤JavaCodeAnalyzer$a::Od(varNode)的返回值为
	 * 防止ParameterizedType被踢出泛型
	 */
	@Keep
	public static Type getVarNodeAttrType(SyntaxTree syntaxTree, int varParentNode) {
		return SyntaxTreeUtils.getVarNodeAttrType(syntaxTree, varParentNode);
	}



	/**
	 * 尝试支持 var [Java]
	 */
	@Keep
	public static Entity getVarAttrType(JavaCodeAnalyzer.a JavaCodeAnalyzer$a, int varNode) throws e4 {
		return SyntaxTreeUtils.getVarAttrType(JavaCodeAnalyzer$a, varNode);
	}

	@Keep
	public static boolean parserLambdaExpression(JavaParser javaParser) throws Parser.a {
		if (! (javaParser instanceof JavaParserPro)) {
			return false;
		}
		JavaParserPro javaParserPro = (JavaParserPro)javaParser;
		return javaParserPro.parserLambdaExpression();
	}

	/**
	 * 修正接口方法是否自动附加abstract
	 * 除default | static 方法除外
	 * JavaParser::oY()I
	 */
	@Keep
	public static int getModifiers(SyntaxTree syntaxTree, int nodeIndex, int flags) {
		// 显示声明 abstract 或 static，添加 public 就行
		if ((flags & 0x4000) != 0 
			|| (flags & 0x40) != 0) {
			return flags |= 0x1;
		}
		// 处理源码的接口默认方法
		Language language = syntaxTree.getLanguage();
		// 非class 且 非default或非static时才添加抽象标志
		if (! (language instanceof JavaBinaryLanguage)
			&& !SyntaxTreeUtils.isNoInterfaceAbstractMethod(syntaxTree, syntaxTree.getChildNode(nodeIndex, 0))) {
			// 非 default || 非static 才 |= 0x4000
			return flags |= 0x4001;
		}

		return flags |= 0x1;
	}

	/**
	 * is abstract method 是不是接口不重要
	 * 代替 y1.j6 [Modifiers::isAbstract(int)boolean]
	 * Modifiers::isAbstract(int)传入的参数是遍历树节点得到的
	 * 没有考虑接口类缺省abstract的方法
	 * 此时Member已填充完成，Member:isAbstract()可用
	 */
	@Keep
	public static boolean isInterfaceAbstractMethod(EntitySpace entitySpace, SyntaxTree syntaxTree, int nodeIndex) {
		// code 分析器 
		Member method = entitySpace.jw(syntaxTree.getFile(), syntaxTree.getLanguage(), syntaxTree.getDeclarationNumber(nodeIndex));
		// 此方法没有 abstract
		return method != null && method.isAbstract();
	}

	//扩展接口
	/**
	 * 重定义Apk构建路径
	 *
	 */
	public static String getApkBuildPath(String projectPath) {
		if (ZeroAicySetting.isEnableAdjustApkBuildPath()) {
			String currentAppHome = ServiceContainer.getProjectService().getCurrentAppHome();
			if (currentAppHome != null) {
				return GradleTools.Hw(currentAppHome) + "/" + FileSystem.getName(projectPath) + ".apk";
			}
		}
		return FileSystem.aM() + "/apk/" + FileSystem.getName(projectPath) + ".apk";
	}
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
	@Deprecated
	@Keep
	public static boolean isDefaultMethod(String methodSignature) {
		return false; //ZeroAicyClassFilePreProcessor.isDefaultMethod(methodSignature);
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
	 * 项目服务运行的线程服务
	 */
	@Keep
	public static ExecutorService getProjectExecutorService() {
		return ZeroAicyProjectService.getProjectServiceExecutorService();
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
		if (buildGradle == null) {
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
		if (flavorDependencies.isEmpty()) {
			return defaultDependencies;			
		}
		// 合并 flavorDependencies与defaultDependencies
		List<BuildGradle.Dependency> dependencies = new ArrayList<BuildGradle.Dependency>();
		dependencies.addAll(flavorDependencies);
		dependencies.addAll(defaultDependencies);

		return dependencies;
	}

	// 用于修复MessageBox::gW()可能在子线程运行的情况
	public static void showDialogMessageBox(final Activity activity, final int id) {
		if (activity == null) return;
		//保证在主线程调用
		activity.runOnUiThread(new Runnable(){
				@Override
				public void run() {
					activity.showDialog(id);
				}
			});
	}

	/**
	 * Lcom/aide/engine/Engine$c;->Ws
	 */
	@Keep
	public static SyntaxStyleType getSyntaxStyleType(Syntax syntax, int syntaxTag) {
        try {
            if (syntax.isOperator(syntaxTag)) {
                return SyntaxStyleType.OPERATOR;
            }
            if (syntax.isSeparator(syntaxTag)) {
                return SyntaxStyleType.SEPARATOR;
            }
            if (syntax.isTypeIdentifier(syntaxTag)) {
                return SyntaxStyleType.TYPE_IDENTIFIER;
            }
            if (syntax.isBooleanLiteral(syntaxTag)) {
                return SyntaxStyleType.LITERAL;
            }
            if (syntax.isToken(syntaxTag)) {
                return SyntaxStyleType.KEYWORD;
            }
            if (syntax.isDocComment(syntaxTag)) {
                return SyntaxStyleType.DOC_COMMENT;
            }
            if (syntax.isComment(syntaxTag)) {
                return SyntaxStyleType.COMMENT;
            }
			// 扩展
			if (syntax.isParameters(syntaxTag)) {
				//return SyntaxStyleType.PARAMETER;
			}
			if (syntax.isIdentifier(syntaxTag)) {
				// 测试一下斜体
				return SyntaxStyleType.IDENTIFIER;

			}
			/*if( syntax instanceof JavaSyntax){
			 AppLog.d("getSyntaxStyleType: ", syntax.getString(syntaxTag), "\n");
			 }*/

            return null;
        }
		catch (Throwable th) {
			return null;
        }
    }
}
