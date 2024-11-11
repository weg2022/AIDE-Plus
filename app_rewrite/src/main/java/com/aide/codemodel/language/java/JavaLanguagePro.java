package com.aide.codemodel.language.java;

import com.aide.codemodel.api.Model;
import com.aide.codemodel.api.abstraction.CodeRenderer;

public class JavaLanguagePro extends JavaLanguage{

	@Override
	public CodeRenderer getCodeRenderer(){
		return we();
	}
	
	public JavaLanguagePro(Model model) {
		super(model);
		
	}

	@Override
	public JavaSignatureAnalyzer getSignatureAnalyzer(){
		return super.getSignatureAnalyzer();
	}
	
	
}
