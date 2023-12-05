package io.github.zeroaicy.aide.services;

import android.text.*;
import com.aide.ui.build.packagingservice.*;
import io.github.zeroaicy.util.*;
import java.util.*;

public abstract class PackagingServiceWorkerWrapper extends ExternalPackagingService.ExternalPackagingServiceWorker {
	
	public static void fillD8Args(List<String> argsList, boolean file_per_class_file, boolean intermediate, String androidSDK, String[] allLibraries, String outPath) {
		argsList.add("--min-api");
		argsList.add("21");

		if (file_per_class_file) {
			argsList.add("--file-per-class-file");
		}
		if (intermediate) {
			argsList.add("--intermediate");
		}
		if (!TextUtils.isEmpty(androidSDK)) {
			argsList.add("--lib");
			argsList.add(androidSDK);
		}

		if (allLibraries != null) {
			for (String librarie : allLibraries) {
				argsList.add("--classpath");
				argsList.add(librarie);
			}
		}
		argsList.add("--output");
		argsList.add(outPath);
	}
	
	public PackagingServiceWorkerWrapper() {
		//父类没有使用外部类[ExternalPackagingService]
		null.super();
	}

	@Override
	public void Zo(String str, String[] strArr, String[] strArr2, String[] strArr3, String str2, String str3, String str4, String[] strArr4, String str5, String str6, String str7, String str8, String str9, boolean z, boolean z2, boolean z3) {
		if (this.Hw == null) {
			this.Hw = new ArrayList<>();
		}
		Log.d("DxThreadPoolExecutor", "called Zo()");
		//添加打包任务
		this.Hw.add(getTaskWrapper(str, strArr, strArr2, strArr3, str2, str3, str4, strArr4, str5, str6, str7, str8, str9, z, z2, z3));
	}
	public abstract TaskWrapper getTaskWrapper(String str, String[] strArr, String[] strArr2, String[] strArr3, String str2, String str3, String str4, String[] strArr4, String str5, String str6, String str7, String str8, String str9, boolean z, boolean z2, boolean z3);
	
	public class TaskWrapper extends Task {
		//String j6 class文件缓存路径 Android项目 
		//String[] DW, String[] FH, String[] Hw, String v5, String Zo, String VH, String[] gn, String u7, String tp, String EQ, String we, String J0, boolean J8, boolean Ws, boolean QX)
		public TaskWrapper(String j6, String[] DW, String[] FH, String[] Hw, String v5, String Zo, String VH, String[] gn, String u7, String tp, String EQ, String we, String J0, boolean J8, boolean Ws, boolean QX) {
			super(j6, DW, FH, Hw, v5, Zo, VH, gn, u7, tp, EQ, we, J0, J8, Ws, QX);
		}
	}
}
