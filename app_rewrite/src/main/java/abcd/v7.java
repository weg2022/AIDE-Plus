package abcd;


/*
 author : 罪慾
 date : 2024/10/2 22:46
 description : QQ3115093767
 */

import androidx.annotation.Keep;
import com.aide.codemodel.api.FileEntry;
import com.aide.codemodel.api.FileSpace;
import com.aide.codemodel.api.Model;
import com.aide.codemodel.api.SyntaxTree;
import com.aide.codemodel.language.xml.XmlCodeModel;
import io.github.zeroaicy.aide.completion.XmlCompletionUtils;
import java.util.HashMap;
import io.github.zeroaicy.util.reflect.ReflectPie;
import java.util.Map;
import java.io.File;
import com.aide.ui.project.internal.GradleTools;
import io.github.zeroaicy.aide.ui.services.ThreadPoolService;


/**
 * @author: 罪慾
 * @github: https://github.com/neu233/
 * @mail: 3115093767@qq.com
 * @createTime: 2024/11/1
 * @apiNote: 必需Keep下面的方法，因为这些方法都被外部调用了
 * </br>
 * j6(SyntaxTree syntaxTree, int i)
 * </br>
 * Hw(FileEntry fileEntry)
 * </br>
 * FH(SyntaxTree syntaxTree, int i, int i2)
 * </br>
 * DW(SyntaxTree syntaxTree, int i, int i2)
 */

@Keep
public class v7 {

    @Keep
    private Model model;

	private FileSpace fileSpace;

	private ReflectPie fileSpaceReflect;

    @Keep
    public v7(Model model) {
        this.model = model;
		if (model != null) {
			this.fileSpace = model.fileSpace;
			this.fileSpaceReflect = ReflectPie.on(this.fileSpace);
		}
    }

    @Keep
    public void DW(SyntaxTree syntaxTree, int tag, int element) {
		init();
        XmlCompletionUtils.completionAttrs(model, syntaxTree, tag, element);
    }

    @Keep
    public void FH(SyntaxTree syntaxTree, int line, int column) {
		init();
		XmlCompletionUtils.completionTag(model, syntaxTree, line, column);
    }


    @Keep
    public void j6(SyntaxTree syntaxTree, int index) {
		init();
		int property = syntaxTree.getIdentifier(syntaxTree.getChildNode(syntaxTree.getChildNode(index, 0), 2));
        XmlCompletionUtils.completionValue(model, syntaxTree, property, index);
    }


    @Keep
    public boolean Hw(FileEntry fileEntry) {
        init();
        return fileEntry.getCodeModel() instanceof XmlCodeModel;
    }

	boolean inted = false;
	private void init() {
		if (inted) {
			return;
		}
		ThreadPoolService defaultThreadPoolService = ThreadPoolService.getDefaultThreadPoolService();
		defaultThreadPoolService.submit(new Runnable(){
				@Override
				public void run() {
					initAsync();
				}
			});
	}


	public void initAsync() {

		XmlCompletionUtils.initAndroidSDK();
		
		HashMap<Integer, FileSpace.Assembly> assemblyMap = getAssemblyMap();
		synchronized (assemblyMap) {
			// 遍历创建项目
			for (Map.Entry<Integer, FileSpace.Assembly> entry : assemblyMap.entrySet()) {
				FileSpace.Assembly assembly = entry.getValue();

				String assemblyName = FileSpace.Assembly.VH(assembly);
				if ("rt.jar".equals(assemblyName)
					|| "android.jar".equals(assemblyName)) {
					continue;
				}
				String projectPath = FileSpace.Assembly.Zo(assembly);
				File projectFile = new File(projectPath);

				if (projectFile.isFile()) {
					// jar没有
					return;
				}

				if (GradleTools.isAndroidGradleProject(projectPath)) {

					File resDir = new File(projectFile, "src/main/res");
					if (resDir.exists()) {
						XmlCompletionUtils.getResourceUtil().forPackage("app", resDir);
					}
					return;
				}

				File resDir = new File(projectFile, "res");
				if (resDir.exists()) {
					XmlCompletionUtils.getResourceUtil().forPackage("app", resDir);
				}
			}
		}

	}
	/**
	 * AssemblyId -> Assembly[assemblyName，assembly路径，]
	 */
	public HashMap<Integer, FileSpace.Assembly> getAssemblyMap() {
		return this.fileSpaceReflect.get("assemblyMap");
	}
}



