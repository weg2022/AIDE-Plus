//
// Decompiled by Jadx - 853ms
//
package abcd;

import com.aide.common.MessageBox;
import com.aide.common.ValueRunnable;
import com.aide.ui.ServiceContainer;
import com.aide.ui.project.AndroidProjectSupport;
import com.aide.ui.util.FileSystem;
import java.io.File;
import com.aide.ui.rewrite.R;

/**
 * 安卓项目 添加xxx文件
 * 异步导致的启用不正确
 */
public class be {

    public be() {

    }

    public static void DW(final String dirPath, final ValueRunnable<String> valueRunnable) {
		if (Zo(dirPath)) {
			MessageBox.XL(ServiceContainer.getMainActivity(), 0x7f0d0021, 0x7f0d05ca, "", new ValueRunnable<String>(){
					@Override
					public void j6(String name) {
						if (name.endsWith(".java")) {
							name = name.substring(0, name.length() - 5);
						}
						String javaPath = dirPath + File.separator + name + ".java";
						String Ev = AndroidProjectSupport.Ev(ServiceContainer.getProjectService().getLibraryMapping(), ServiceContainer.getProjectService().getBuildVariant(), dirPath);
						String content = "";
						if (Ev.length() > 0) {
							content = "package " + Ev + ";\n\n";
						}
						FileSystem.v5(javaPath, content + "public class " + name + "\n{\n}");
						valueRunnable.j6(javaPath);
					}
				});
		} else if (v5(dirPath)) {
			MessageBox.XL(ServiceContainer.getMainActivity(), R.string.command_files_add_new_xml, 0x7f0d05ca, "", new ValueRunnable<String>(){
					@Override
					public void j6(String name) {
						if (name.endsWith(".xml")) {
							name = name.substring(0, name.length() - 4);
						}
						String xmlPath = dirPath + File.separator + name + ".xml";
						String content;
						if (FileSystem.getName(FileSystem.getParent(xmlPath)).startsWith("layout")) {
							content = "<LinearLayout xmlns:android=\"http://schemas.android.com/apk/res/android\"\n    android:layout_width=\"fill_parent\"\n    android:layout_height=\"fill_parent\"\n    android:orientation=\"vertical\">\n    \n</LinearLayout>\n";
						} else {
							content = FileSystem.getName(FileSystem.getParent(xmlPath)).startsWith("menu") ? "<menu xmlns:android=\"http://schemas.android.com/apk/res/android\">\n    \n    <item\n        android:id=\"@+id/item\"\n        android:title=\"Item\"/>\n    \n</menu>\n" : "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n";
						}
						FileSystem.v5(xmlPath, content);
						valueRunnable.j6(xmlPath);
					}
				});
		}
    }

	/**
	 * getDrawableId
	 */
    public static int FH(String str) {
		return 0x7f07003e;
    }
	public static int Hw(String dirPath) {
		if (Zo(dirPath)) {
			return R.string.command_files_add_new_class;
		}
		// 是否是res资源
		if (v5(dirPath)) {
			return R.string.command_files_add_new_xml;
		}
		String currentAppHome = ServiceContainer.getProjectService().getCurrentAppHome();

		// Java源码目录
		if (!dirPath.startsWith(FileSystem.getParent(currentAppHome))) {
			return 0;
		}
		if (dirPath.contains("/java")) {
			return R.string.command_files_add_new_class;				
		} else if (dirPath.endsWith("/res/layout") 
			|| dirPath.endsWith("/res/menu")) {
			// 是layout目录
			return R.string.command_files_add_new_xml;
		}
		return 0;
    }

    public static int Hw2(String dirPath) {
		if (Zo(dirPath)) {
			return R.string.command_files_add_new_class;
		}
		return v5(dirPath) ? R.string.command_files_add_new_xml : 0;
    }

	/**
	 * is
	 */
    private static boolean Zo(String dirPath) {
		return AndroidProjectSupport.Ev(ServiceContainer.getProjectService().getLibraryMapping(), ServiceContainer.getProjectService().getBuildVariant(), dirPath) != null;
    }

    public static boolean j6(String dirPath) {

		if (ZeroAicy(dirPath)
			|| Zo(dirPath) 
			|| v5(dirPath)) {
			return true;
		}		
		return false;
	}


	/**
	 * 异步导致的向getProjectService 获取项目信息时[数据正在异步]
	 * 并用AndroidProjectSupport判断是否启用时出现数据不同步
	 * 符合返回true
	 */
	private static boolean ZeroAicy(String dirPath) {

		String currentAppHome = ServiceContainer.getProjectService().getCurrentAppHome();
		// 不是项目里
		if (!dirPath.startsWith(FileSystem.getParent(currentAppHome))) {
			return false;
		}
		// 是Java目录
		if (dirPath.contains("/java")) {
			return true;
		}
		// 是layout目录
		if (dirPath.endsWith("/res/layout") 
			|| dirPath.endsWith("/res/menu")) {
			return true;
		}

		return false;
	}

    private static boolean v5(String str) {
		if (FileSystem.Ws(str, "res") != null) {
			if (FileSystem.nw(ServiceContainer.getProjectService().getCurrentAppHome(), str)) {
				return true;
			}
		}
		return false;
    }


	public static boolean isJavaDir(String dirPath) {
		String currentAppHome = ServiceContainer.getProjectService().getCurrentAppHome();
		// 不是项目里
		if (dirPath.startsWith(FileSystem.getParent(currentAppHome))) {
			return true;
		}
		// 是Java目录
		if (dirPath.contains("/java")) {
			return true;
		}

		return false;
	}
	public static boolean isXmlDir(String dirPath) {
		String currentAppHome = ServiceContainer.getProjectService().getCurrentAppHome();
		// 不是项目里
		if (dirPath.startsWith(FileSystem.getParent(currentAppHome))) {
			return true;
		}
		// 是layout目录
		if (dirPath.endsWith("/res/layout") 
			|| dirPath.endsWith("/res/menu")) {
			return true;
		}
		return false;
	}
}
