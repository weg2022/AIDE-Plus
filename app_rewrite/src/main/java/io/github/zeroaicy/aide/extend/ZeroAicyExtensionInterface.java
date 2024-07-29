


package io.github.zeroaicy.aide.extend;


import abcd.e4;
import android.app.Activity;
import android.text.TextUtils;
import androidx.annotation.Keep;
import com.aide.codemodel.api.Entity;
import com.aide.codemodel.api.EntitySpace;
import com.aide.codemodel.api.ErrorTable;
import com.aide.codemodel.api.Member;
import com.aide.codemodel.api.Model;
import com.aide.codemodel.api.SyntaxTree;
import com.aide.codemodel.api.Type;
import com.aide.codemodel.api.abstraction.CodeModel;
import com.aide.codemodel.api.abstraction.Syntax;
import com.aide.codemodel.language.classfile.ClassFilePreProcessor;
import com.aide.codemodel.language.java.JavaCodeAnalyzer;
import com.aide.codemodel.language.java.JavaLanguage;
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
import com.aide.codemodel.api.ParameterizedType;

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
		 if( codeModels.get(0) instanceof JavaCodeModel){
		 codeModels.set(0, new JavaCodeModelPro(model));
		 }
		 //*/
	}
	/**
	 * 测试 仅在共存版会被调用 SyntaxTree::declareAttrType()
	 */
	@Keep
	public static void declareAttrType(SyntaxTree syntaxTree, int node, Type type) {

		if (type == null) return;
		// 禁用
		if (type != null) return;
		
		
		if (! (type instanceof Type)) {
			return;
		}
		if (syntaxTree.hasAttrType(node) && syntaxTree.getAttrType(node) instanceof ParameterizedType) {
			System.out.println("declareAttrType NodeId: " + node + " Type: " + type.getClass().getName());
			System.out.println("ParameterizedTypeProxy 被覆盖为 -> " + (type == null ? null : type.getClass().getSimpleName()));
			Log.printlnStack();

		} else {
			System.out.println("declareAttrType NodeId: " + node + " Type: " + type.getClass().getName());
		}
		System.out.println();
	}

	/**
	 * 过滤JavaCodeAnalyzer$a::Od(I)的返回值为
	 * 防止ParameterizedType被踢出泛型
	 */
	@Keep
	public static Type getVarNodeAttrType(SyntaxTree syntaxTree, int varParentNode) {
		// varParentNode[TYPE_NAME]
		if (isVarNode(syntaxTree, varParentNode) 
			&& syntaxTree.hasAttrType(varParentNode)) {
			return syntaxTree.getAttrType(varParentNode);
		}
		return null;
	}

	public static boolean isVarNode(SyntaxTree syntaxTree, int varParentNode) {
		// varParentNode[TYPE_NAME]
		if (syntaxTree.getChildCount(varParentNode) < 1) {
			return false;
		}
		int varNode = syntaxTree.getChildNode(varParentNode, 0);
		if (!syntaxTree.isIdentifierNode(varNode)) {
			return false;
		}
		// 是var Node
		String varNodeIdentifierString = syntaxTree.getIdentifierString(varNode);
		if ("var".equals(varNodeIdentifierString)) {
			return true;
		}
		return false;
	}

	/**
	 * 尝试支持 var [Java]
	 */
	@Keep
	public static Entity getVarAttrType(JavaCodeAnalyzer.a JavaCodeAnalyzer$a, int varNode) throws e4 {
		SyntaxTree syntaxTree = JavaCodeAnalyzer$a.er(JavaCodeAnalyzer$a);
		// 不是var
		String varNodeIdentifierString = syntaxTree.getIdentifierString(varNode);
		if (!"var".equals(varNodeIdentifierString)) {
			return null;
		}

		// varNode必须getParentNode两次才是[JavaCodeAnalyzer$a::d8]中的node
		// 获得var节点的父节点

		// [IDENTIFIER]
		// varNode
		// [TYPE_NAME]
		int varParentNode = syntaxTree.getParentNode(varNode);
		// [VARIABLE_DECLARATION]
		int varRootNode = syntaxTree.getParentNode(varParentNode);

		//System.out.println("varRootNode[解析右侧表达式前]");
		//SyntaxTreeUtils.printNode(syntaxTree, varRootNode, 0);

		// [JavaCodeAnalyzer$a::d8]先计算 变量的类型
		// 也即会调用此方法，无论是否解析出来
		// 都会遍历子节点，解析右侧计算表达式类型
		int parentNodeCount = syntaxTree.getChildCount(varRootNode);
		int expressionNode = -1;

		// 计算右侧表达式类型
		for (int i6 = 3; i6 < parentNodeCount; i6 += 2) {
			int childNode = syntaxTree.getChildNode(varRootNode, i6);

			if (syntaxTree.getChildCount(childNode) > 2) {
				// JavaCodeAnalyzer$a::fY()
				expressionNode = syntaxTree.getChildNode(childNode, 3);
				JavaCodeAnalyzer.a.Zo(JavaCodeAnalyzer$a, expressionNode, null);
			}
		}

		//System.out.println("varRootNode[解析右侧表达式后]");
		//SyntaxTreeUtils.printNode(syntaxTree, varRootNode, 0);

		// 打印 expressionNode
		if (expressionNode != -1) {
			// 没有找到 expressionNode
			final Type expressionNodeType = syntaxTree.getAttrType(expressionNode);
			if (expressionNodeType != null && !"null".equals(expressionNodeType.getFullyQualifiedNameString())) {
				if (!expressionNodeType.isNamespace()) {
					// 解析后的 attrType带泛型
					// 但从 JavaCodeAnalyzer$a::Ej或者Od 之后被剔除泛型了
					// 此处会被覆盖所以 无用
					// 拦截覆盖了，所以必须declareAttrType
					// 由getVarAttrType处理被替换的问题
					syntaxTree.declareAttrType(varParentNode, expressionNodeType);
				}

				return expressionNodeType;
			}
		}
		
		// 变量名称节点
		// [VARIABLE]
		int varNameNode = syntaxTree.getChildNode(varRootNode, 3);
		// [VARIABLE]子节点[IDENTIFIER]
		int errorNode = syntaxTree.getChildNode(varNameNode, 0);

		ErrorTable errorTable = syntaxTree.getModel().errorTable;

		errorTable.Hw(syntaxTree.getFile(), 
					  syntaxTree.getLanguage(), 
					  syntaxTree.getStartLine(errorNode), 
					  syntaxTree.getStartColumn(errorNode), 
					  syntaxTree.getEndLine(errorNode), 
					  syntaxTree.getEndColumn(errorNode), 
					  "Variable </C>" + syntaxTree.getIdentifierString(errorNode) + "<//C> might not have been initialized", 12);
		// UnknownEntityException
		throw new abcd.e4();
	}



	/**
	 * 修正接口方法是否自动附加abstract
	 * 除default | static 方法除外
	 */
	@Keep
	public static int getModifiers(SyntaxTree syntaxTree, int nodeIndex, int flags) {
		// 显示声明 abstract 或 static，添加 public 就行
		if ((flags & 0x4000) != 0 || (flags & 0x40) != 0) {
			return flags |= 0x1;
		}
		// 不具有的，需要判断是否是Java源码
		if (JavaLanguage.class.equals(syntaxTree.getLanguage().getClass())) {
			// 源码暂时不支持 default method
			// 为后来的 default method做准备
			if (!isNoInterfaceAbstractMethod(syntaxTree, syntaxTree.getChildNode(nodeIndex, 0))) {
				// 非 default static 才 |= 0x4000
				return flags |= 0x4001;
			}
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

	/**
	 * 非abstract方法[接口]
	 */
	private static boolean isNoInterfaceAbstractMethod(SyntaxTree syntaxTree, int nodeIndex) {

		for (int i = 0, childCount = syntaxTree.getChildCount(nodeIndex); i < childCount; i++) {
			int syntaxTag = syntaxTree.getSyntaxTag(syntaxTree.getChildNode(nodeIndex, i));
			// abstract
			if (syntaxTag == 95) {
				return false;
			}
			// default || static
			if (syntaxTag == 90
				|| syntaxTag == 86) {
				return true;
			}
		}
		return false;
	}

	private int EQ(int syntaxTag) {
		// 5511177 [ 1 4 12 13 19 21 23 ]
		int newValue;  
		switch (syntaxTag) {  
				// final	
			case 75: 
				// 896 = 0x380 = 1 << 7 | 1 << 8 | 1 << 9  
				newValue = 896; // 0x380, 1 << 7 | 1 << 8 | 1 << 9  
				break;
				// static
			case 86: 
				// 64 = 0x40 = 1 << 6  
				newValue = 64; // 0x40, 1 << 6  
				break;
				// synchronized
			case 104: 
				// 2048 = 0x800 = 1 << 11  
				newValue = 2048; // 0x800, 1 << 11  
				break;
				// @
			case 115: 
				// 536870912 = 0x20000000 = 1 << 29  
				newValue = 0x20000000; // 1 << 29  
				break;
				// native
			case 83: 
				// 524288 = 0x80000 = 1 << 19  
				newValue = 524288; // 0x80000, 1 << 19  
				break;
				// public
			case 84: 
				newValue = 1; // 1 << 0  
				break;
				// private
			case 94: 
				newValue = 4; // 1 << 2  
				break;
				// abstract
			case 95: 
				// 16384 = 0x4000 = 1 << 14  
				newValue = 16384; // 0x4000, 1 << 14  
				break;
				// strictfp
			case 97: 
				// 8192 = 0x2000 = 1 << 13  
				newValue = 8192; // 0x2000, 1 << 13  
				break;
				// volatile
			case 98: 
				// 1024 = 0x400 = 1 << 10  
				newValue = 1024; // 0x400, 1 << 10  
				break;
				// protected
			case 100: 
				newValue = 8; // 1 << 3  
				break;
				// transient
			case 101: 
				// 4096 = 0x1000 = 1 << 12  
				newValue = 4096; // 0x1000, 1 << 12  
				break;
			default: 
				newValue = 0;  
				break;
		}
		// 如果需要，可以在这里将 newValue 赋值给 syntaxTag 或者其他变量  
		syntaxTag = newValue;

        return syntaxTag;
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
