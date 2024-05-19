package com.aide.ui.project;

import abcd.c0;
import abcd.cb;
import abcd.fe;
import abcd.g3;
import abcd.it;
import abcd.iy;
import abcd.x9;
import com.aide.common.ValueRunnable;
import com.aide.engine.EngineSolution;
import com.aide.ui.App;
import com.aide.ui.build.BuildServiceCollect;
import com.aide.ui.project.internal.MakeJavaEngineSolution;
import com.aide.ui.services.ProjectSupport;
import com.aide.ui.services.TemplateService;
import com.aide.ui.util.ClassPath;
import com.aide.ui.util.FileSystem;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

class JavaProjectSupport2 implements ProjectSupport {

	public JavaProjectSupport2() {
		try {

		} catch (Throwable th) {

			throw new Error(th);
		}
	}

	public static String I(String str, String str2) {
		try {

			for (String str3 : dx(str)) {
				if (FileSystem.nw(str3, str2)) {
					return FileSystem.BT(str3, str2).replace('/', '.');
				}
			}
			return null;
		} catch (Throwable th) {

			throw new Error(th);
		}
	}

	public static String Mz(String str, boolean z) {
		try {

			return vJ(str, z) + "/dex/jars";
		} catch (Throwable th) {

			throw new Error(th);
		}
	}

	private boolean Qq() {
		try {

			int i = 0;
			for (String str : dx(App.getProjectService().getCurrentAppHome())) {
				i += FileSystem.J8(str, x9(), new String[]{".java"});
				if (i >= x9()) {
					return true;
				}
			}
			return false;
		} catch (Throwable th) {

			throw new Error(th);
		}
	}

	public static String Sf(String str, boolean z) {
		try {

			return vJ(str, z) + "/dex/classes.dex.zip";
		} catch (Throwable th) {

			throw new Error(th);
		}
	}

	public static String ca(Map<String, List<String>> map, String str) {
		try {

			for (String str2 : sG(map)) {
				if (FileSystem.nw(str2, str)) {
					return FileSystem.BT(str2, str).replace('/', '.');
				}
			}
			return null;
		} catch (Throwable th) {

			throw new Error(th);
		}
	}

	public static String[] cb(String str) {
		try {

			ArrayList<String> arrayList = new ArrayList<String>();
			for (ClassPath.Entry entry : new ClassPath().getConfiguration(g3(str)).Zo) {
				if (entry.isLibKind()) {
					arrayList.add(entry.VH(str));
				}
			}
			return arrayList.toArray(new String[arrayList.size()]);
		} catch (Throwable th) {

			throw new Error(th);
		}
	}

	public static String[] dx(String str) {
		try {

			ArrayList<String> arrayList = new ArrayList<>();
			for (ClassPath.Entry entry : new ClassPath().getConfiguration(g3(str)).Zo) {
				if (entry.isSrcKind()) {
					arrayList.add(entry.VH(str));
				}
			}
			return arrayList.toArray(new String[arrayList.size()]);
		} catch (Throwable th) {

			throw new Error(th);
		}
	}

	public static String ef(String str, boolean isDebug) {
		try {

			return vJ(str, isDebug) + "/dex";
		} catch (Throwable th) {

			throw new Error(th);
		}
	}

	public static String g3(String str) {
		try {

			return str + "/.classpath";
		} catch (Throwable th) {

			throw new Error(th);
		}
	}

	public static String[] sG(Map<String, List<String>> map) {
		try {

			ArrayList arrayList = new ArrayList();
			for (String str : map.keySet()) {
				for (ClassPath.Entry entry : new ClassPath().getConfiguration(g3(str)).Zo) {
					if (entry.isSrcKind()) {
						arrayList.add(entry.VH(str));
					}
				}
			}
			String[] strArr = new String[arrayList.size()];
			arrayList.toArray(strArr);
			return strArr;
		} catch (Throwable th) {

			throw new Error(th);
		}
	}

	public static String vJ(String str, boolean isDebug) {
		try {

			for (ClassPath.Entry entry : new ClassPath().getConfiguration(g3(str)).Zo) {
				if (entry.isOutputIKind()) {
					String VH = entry.VH(str);
					if (isDebug) {
						return VH + "/debug";
					}
					return VH + "/release";
				}
			}
			if (isDebug) {
				return str + "/bin/debug";
			}
			return str + "/bin/release";
		} catch (Throwable th) {

			throw new Error(th);
		}
	}

	private int x9() {
		try {
			return 2;
		} catch (Throwable th) {

			throw new Error(th);
		}
	}

	public void DW(boolean z) {
		try {

			BuildServiceCollect.DW.XL(z, App.getProjectService().er());
		} catch (Throwable th) {

			throw new Error(th);
		}
	}
	
	// isPremium
	public boolean EQ() {
		try {

			if (App.ca() || App.a8().Ws()) {
				return false;
			}
			// 允许在非 Premium 中保存一个 Java 文件
			if (c0.Ws(App.getMainActivity(), "AllowSavingOneJavaFileInNonPremium", new double[]{1.0d, 0.0d})) {
				return Qq();
			}
			return true;
		} catch (Throwable th) {

			throw new Error(th);
		}
	}

	public boolean FH(String str) {
		try {

			if (str.toLowerCase().endsWith(".jar")) {
				if (!Arrays.asList(cb(App.getProjectService().getCurrentAppHome())).contains(str)) {
					return true;
				}
			}
			return false;
		} catch (Throwable th) {

			throw new Error(th);
		}
	}

	public boolean Hw() {
		try {

			return false;
		} catch (Throwable th) {

			throw new Error(th);
		}
	}

	public void J0(String str) {
		try {

			String currentAppHome = App.getProjectService().getCurrentAppHome();
			List<com.aide.ui.util.ClassPath.Entry> list = new ClassPath().getConfiguration(g3(currentAppHome)).Zo;
			Iterator it = list.iterator();
			while (true) {
				if (!it.hasNext()) {
					break;
				}
				ClassPath.Entry entry = (ClassPath.Entry) it.next();
				if (entry.isLibKind() && entry.VH(currentAppHome).equals(str)) {
					list.remove(entry);
					break;
				}
			}
			ClassPath.Hw(g3(currentAppHome), list);
		} catch (Throwable th) {

			throw new Error(th);
		}
	}

	public boolean J8() {
		try {

			return false;
		} catch (Throwable th) {

			throw new Error(th);
		}
	}

	public int KD(String str) {
		try {
			// command_files_open_java_project 打开这个Java项目
			return 0x7f0d002e;
		} catch (Throwable th) {

			throw new Error(th);
		}
	}

	public void Mr() {
		try {

			App.a8().cn(App.getMainActivity(), 0x7f0d0611, "large_project");
		} catch (Throwable th) {

			throw new Error(th);
		}
	}

	public void P8(String str, String str2) {
		try {

		} catch (Throwable th) {

			throw new Error(th);
		}
	}

	public String QX() {
		try {

			String str = ("<b>Java Project:</b><br/><br/>" + App.getProjectService().getCurrentAppHome() + "<br/><br/>") + "<i>Library JARs:</i><br/><br/>";
			String[] cb = cb(App.getProjectService().getCurrentAppHome());
			if (cb.length == 0) {
				return str + "&lt;none&gt;<br/><br/>";
			}
			for (String str2 : cb) {
				if (!FileSystem.exists(str2)) {
					str = str + "(NOT FOUND) ";
				}
				str = str + str2 + "<br/><br/>";
			}
			return str;
		} catch (Throwable th) {

			throw new Error(th);
		}
	}

	public void SI(String str, ValueRunnable<String> valueRunnable) {
		try {

			fe.DW(str, valueRunnable);
		} catch (Throwable th) {

			throw new Error(th);
		}
	}

	public void U2(String str, Map<String, List<String>> map, List<String> list) {
		try {

			map.put(str, new ArrayList<String>());
			list.add(str);
		} catch (Throwable th) {
			throw new Error(th);
		}
	}

	public TemplateService.TemplateGroup[] VH() {
		boolean z;
		try {

			if (!App.ca() && !App.P8.equals("com.aide.ui")) {
				z = false;
				return new TemplateService.TemplateGroup[]{new TemplateService.TemplateGroup("Java Application", new TemplateService.Template(this, 3, "Java Application", "Java", "MyJavaConsoleApp", false, false, "com.aide.ui", "JAVA", "course_java", z), 0x7f07007b, "JavaConsole.zip", new String[]{"Main.java"}, (String) null)};
			}
			z = true;
			return new TemplateService.TemplateGroup[]{new TemplateService.TemplateGroup("Java Application", new TemplateService.Template(this, 3, "Java Application", "Java", "MyJavaConsoleApp", false, false, "com.aide.ui", "JAVA", "course_java", z), 0x7f07007b, "JavaConsole.zip", new String[]{"Main.java"}, (String) null)};
		} catch (Throwable th) {

			throw new Error(th);
		}
	}

	public EngineSolution Ws() {
		try {

			return MakeJavaEngineSolution.DW(App.getProjectService().getCurrentAppHome(), App.getProjectService().getAndroidJarPath(), (String) null);
		} catch (Throwable th) {

			throw new Error(th);
		}
	}

	public List<com.aide.ui.trainer.c.c> XL() {
		try {
			return Collections.singletonList(new com.aide.ui.trainer.c.c("course_java", 1, new String[]{"com.aide.ui", "com.aide.trainer.java"}));
		} catch (Throwable th) {

			throw new Error(th);
		}
	}

	public boolean Zo(String str) {
		try {

			return fe.j6(str);
		} catch (Throwable th) {

			throw new Error(th);
		}
	}

	public boolean a8(String str) {
		try {

			String currentAppHome = App.getProjectService().getCurrentAppHome();
			return Arrays.asList(cb(currentAppHome)).contains(str);
		} catch (Throwable th) {

			throw new Error(th);
		}
	}

	public boolean aM(String str) {
		try {

			Iterator it = App.getProjectService().BT().keySet().iterator();
			while (it.hasNext()) {
				if (FileSystem.nw((String) it.next(), str)) {
					return true;
				}
			}
			for (String str2 : dx(App.getProjectService().getCurrentAppHome())) {
				if (FileSystem.nw(str2, str)) {
					return true;
				}
			}
			return false;
		} catch (Throwable th) {

			throw new Error(th);
		}
	}

	public void cn(List<String> list, boolean z) {
		try {

		} catch (Throwable th) {

			throw new Error(th);
		}
	}

	public void ei(String str) {
		try {

		} catch (Throwable th) {

			throw new Error(th);
		}
	}
	
	// isSupport
	public boolean er(String str) {
		try {

			if (FileSystem.exists(g3(str))) {
				if (!FileSystem.exists(str + "/AndroidManifest.xml")) {
					return true;
				}
			}
			return false;
		} catch (Throwable th) {

			throw new Error(th);
		}
	}

	public boolean gW() {
		try {
			return false;
		} catch (Throwable th) {

			throw new Error(th);
		}
	}
	
	// 原版Java不支持渠道包，Gradle Java可以尝试一下
	public List<String> getProductFlavors(String str) {
		try {

			return null;
		} catch (Throwable th) {

			throw new Error(th);
		}
	}

	public void gn() {
		try {

		} catch (Throwable th) {

			throw new Error(th);
		}
	}

	public boolean j3(String str) {
		try {
			if (App.ca() || App.P8.equals("com.aide.ui")) {
				return true;
			}
			if (!App.P8.equals("com.aide.trainer.java")) {
				return false;
			}
			return true;
		} catch (Throwable th) {

			throw new Error(th);
		}
	}

	public void j6() {
		try {

		} catch (Throwable th) {

			throw new Error(th);
		}
	}

	public boolean lg() {
		try {
			return true;
		} catch (Throwable th) {

			throw new Error(th);
		}
	}

	public void nw(String str) {
		try {
			String currentAppHome = App.getProjectService().getCurrentAppHome();
			ClassPath classPath = new ClassPath();
			List<ClassPath.Entry> entrys = classPath.getConfiguration(g3(currentAppHome)).Zo;
			entrys.add(new ClassPath.Entry("lib", FileSystem.lp(currentAppHome, str), false));
			ClassPath.Hw(g3(currentAppHome), entrys);
		} catch (Throwable th) {

			throw new Error(th);
		}
	}

	public int rN(String str) {
		try {
			return fe.Hw(str);
		} catch (Throwable th) {

			throw new Error(th);
		}
	}

	public List<String> ro(String str) {
		try {
			return null;
		} catch (Throwable th) {

			throw new Error(th);
		}
	}

	public String sh(String str) {
		try {

			String[] sG = sG(App.getProjectService().BT());
			if (!str.startsWith("/")) {
				str = "/" + str;
			}
			for (String str2 : sG) {
				String str3 = str2 + str;
				if (FileSystem.exists(str3)) {
					return str3;
				}
			}
			return null;
		} catch (Throwable th) {

			throw new Error(th);
		}
	}

	public String tp(String str) {
		try {

			if (j3(str)) {
				return null;
			}
			return "com.aide.ui";
		} catch (Throwable th) {

			throw new Error(th);
		}
	}

	public boolean u7(String str) {
		try {

			iy.tp(3903443620038632736L, this, str);
			return false;
		} catch (Throwable th) {

			throw new Error(th);
		}
	}

	public String v5(String str) {
		try {

			String ca = ca(App.getProjectService().BT(), FileSystem.getParent(str));
			if (ca == null) {
				return str;
			}
			return ca.replace('.', '/') + "/" + FileSystem.getName(str);
		} catch (Throwable th) {

			throw new Error(th);
		}
	}

	public boolean vy(String str) {
		try {

			return false;
		} catch (Throwable th) {

			throw new Error(th);
		}
	}

	public int we(String str) {
		try {

			return fe.FH(str);
		} catch (Throwable th) {

			throw new Error(th);
		}
	}

	public String yS() {
		try {

			return "com.aide.ui";
		} catch (Throwable th) {

			throw new Error(th);
		}
	}
}
