package com.aide.codemodel.language.java17;

import android.annotation.NonNull;
import com.aide.codemodel.api.FileEntry;
import com.aide.codemodel.api.FileSpace;
import com.aide.codemodel.api.Model;
import com.aide.codemodel.api.SyntaxTree;
import com.aide.codemodel.api.abstraction.CodeCompiler;
import com.aide.codemodel.api.abstraction.CodeModel;
import com.aide.codemodel.api.collections.SetOfFileEntry;
import com.sun.tools.javac.file.JavacFileManager;
import com.sun.tools.javac.util.Context;
import com.sun.tools.javac.util.Log;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import javax.tools.JavaFileObject;
import com.sun.tools.javac.main.JavaCompiler;

public class Java17Compiler implements CodeCompiler {

	Java17CodeModel java17CodeModel;

	private FileSpace fileSpace;

	public Java17Compiler(Model model) {
		this.model = model;
		this.fileSpace = this.model.fileSpace;
		
	}
	
	Model model;
	@Override
	public void init(CodeModel codeModel) {
		
		this.java17CodeModel = null;
		
		// 初始化编译器
		if (codeModel instanceof Java17CodeModel) {
			this.java17CodeModel = (Java17CodeModel) codeModel;
			try {
				fullCompile();
			}
			catch (Throwable e) {}
		}

	}
	private Context context;

	/**
	 * 全量编译
	 */
	private void fullCompile() throws Throwable {
		
		this.context = new Context();
		
		//Log.preRegister(context);
		Log.instance(context);
		JavacFileManager.preRegister(this.context);
		JavacJavaCompiler.preRegister(this.context);

		JavacFileManager javacFileManager = (JavacFileManager) context.get(javax.tools.JavaFileManager.class);
		
		JavacJavaCompiler javacJavaCodeCompiler = (JavacJavaCompiler) JavaCompiler.instance(context);
		
		List<JavaFileObject> sourceFileObject = new ArrayList<>();
		
		javacJavaCodeCompiler.compile(null);
		
		
	}

	@Override
	public void compile(List<SyntaxTree> syntaxTrees, boolean p) {
		// 编译 SyntaxTree[AST]

	}


	private List<FileEntry> getSourceFiles(@NonNull FileEntry file) {
		ArrayList<FileEntry> sourcePaths = new ArrayList<>();
        HashSet<String> paths = new HashSet<>();
		
		SetOfFileEntry files = new SetOfFileEntry(this.fileSpace);
		
		// checkedFiles
		files.put(this.fileSpace.KD());
		SetOfFileEntry.Iterator default_Iterator = files.default_Iterator;
		default_Iterator.init();
		
        while (default_Iterator.hasMoreElements()) {
            FileEntry checkedFile = default_Iterator.nextKey();
			
            if (checkedFile.getFullNameString().toLowerCase().endsWith(".java")) {
                String pathString = checkedFile.getPathString();
				if (!paths.contains(pathString)) {
                    sourcePaths.add(checkedFile);
                    paths.add(pathString);
                }
            }
        }
        return sourcePaths;
    }
}
