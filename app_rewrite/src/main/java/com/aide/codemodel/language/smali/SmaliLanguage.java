package com.aide.codemodel.language.smali;

import com.aide.codemodel.api.abstraction.Language;
import com.aide.codemodel.api.abstraction.CodeAnalyzer;
import com.aide.codemodel.api.abstraction.CodeRenderer;
import com.aide.codemodel.api.abstraction.SignatureAnalyzer;
import java.util.Set;
import com.aide.codemodel.api.abstraction.FormatOption;
import com.aide.codemodel.api.abstraction.Tools;
import com.aide.codemodel.api.abstraction.TypeSystem;
import com.aide.codemodel.api.abstraction.Syntax;
import com.aide.codemodel.api.Model;
import com.aide.codemodel.language.java.JavaSyntax;

public class SmaliLanguage implements Language {
	
	Model model;
	
	private JavaSyntax javaSyntax;
	public SmaliLanguage(Model model) {
		this.model = model;
		if (model != null) {
			this.javaSyntax = new JavaSyntax(model.identifierSpace);
		}
		
	}
	
	@Override
	public CodeAnalyzer getCodeAnalyzer() {
		// TODO: Implement this method
		return null;
	}

	@Override
	public CodeRenderer getCodeRenderer() {
		// TODO: Implement this method
		return null;
	}

	@Override
	public Set<? extends FormatOption> getFormatOptionSet() {
		// TODO: Implement this method
		return null;
	}

	@Override
	public String getName() {
		return "Smali";
	}

	@Override
	public SignatureAnalyzer getSignatureAnalyzer() {
		// TODO: Implement this method
		return null;
	}

	@Override
	public JavaSyntax getSyntax() {
		return this.javaSyntax;
	}

	@Override
	public Tools getTools() {
		return null;
	}

	@Override
	public TypeSystem getTypeSystem() {
		// TODO: Implement this method
		return null;
	}

	@Override
	public boolean isParenChar(char p) {
		// TODO: Implement this method
		return false;
	}

	@Override
	public void shrink() {
		// TODO: Implement this method
	}
	
}
