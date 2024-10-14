package com.aide.codemodel.language.kotlin;
import com.aide.codemodel.api.Model;
import com.aide.codemodel.api.SyntaxTree;
import com.aide.codemodel.api.abstraction.Language;
import java.util.List;
import com.aide.codemodel.api.abstraction.CodeModel;
import java.util.ArrayList;
import java.io.File;
//import io.github.aide.kotlin.KotlinK2JVMCompile;

public class KotlinCodeCompiler implements com.aide.codemodel.api.abstraction.CodeCompiler {

	@Override
	public void init(CodeModel codeModel) {}

	private Language language;
	private final Model model;

	public KotlinCodeCompiler(Model model, Language language) {
		this.model = model;
		this.language = language;
	}

	@Override
	public void compile(List<SyntaxTree> list, boolean p) {
		//List<File> files = new ArrayList<>();
		for (SyntaxTree syntaxTree : list) {
			if (syntaxTree.getLanguage() == this.language) {
				try {
					String pathString = syntaxTree.getFile().getPathString();
					System.out.println( pathString );
					//files.add(new File(pathString));
				}
				catch (Exception e) {
					e.printStackTrace();
					return;
				}
			}
		}
		/*
		try {
			KotlinK2JVMCompile kotlinK2JVMCompile = KotlinK2JVMCompile.getINSTANCE();
			kotlinK2JVMCompile.setKotlinHomeDir(new File("/data/user/0/io.github.zeroaicy.aide/files"));
			kotlinK2JVMCompile.addProjectSrcDir(files);
			kotlinK2JVMCompile.run();
		}
		catch (Exception e) {
			e.printStackTrace();
			return;
		}
		//*/
	}
}
