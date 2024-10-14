package com.aide.codemodel.language.java17;
import com.sun.tools.javac.util.Context;
import javax.tools.JavaFileManager;
import com.sun.tools.javac.main.JavaCompiler;

public class JavacJavaCompiler extends JavaCompiler {
	
	static Context.Factory<com.sun.tools.javac.main.JavaCompiler> factory = new Context.Factory<JavaCompiler>(){
		@Override
		public JavaCompiler make(Context context) {
			return new JavacJavaCompiler(context);
		}
	};

	
	public static void preRegister(Context context) {
	    context.put(compilerKey, factory);
	}
	
	
	public JavacJavaCompiler(Context ex) {
		super(ex);
	}

	public JavaFileManager getFileManager() {
		return this.fileManager;
	}
	
}
