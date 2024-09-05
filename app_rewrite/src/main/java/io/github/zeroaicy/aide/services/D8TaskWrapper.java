package io.github.zeroaicy.aide.services;
import java.util.List;

public class D8TaskWrapper {
	
	public static void runD8Task(List<String> argList ){
		
		// 使用 app_process运行 d8 || r8
		
		// 临时先使用 这个
		com.android.tools.r8.D8.main(argList.toArray(new String[argList.size()]));
		
	}
	
	public static void runR8Task(List<String> argList ){
		
		// 使用 app_process运行 d8 || r8
		
	}
	
}
