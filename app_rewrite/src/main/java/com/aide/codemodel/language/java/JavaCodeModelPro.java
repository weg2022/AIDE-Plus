package com.aide.codemodel.language.java;
import com.aide.codemodel.api.Model;
import com.aide.codemodel.api.abstraction.Compiler;

public class JavaCodeModelPro extends JavaCodeModel {
	ECJJavaCodeCompiler ecjJavaCodeCompiler;
	public JavaCodeModelPro(Model model) {
		super(model);
		if( model != null ){
			ecjJavaCodeCompiler = new ECJJavaCodeCompiler(getLanguages().get(0), model);			
		}
	}

	@Override
	public Compiler getCompiler() {
		return super.getCompiler();
	}
	
	
}
