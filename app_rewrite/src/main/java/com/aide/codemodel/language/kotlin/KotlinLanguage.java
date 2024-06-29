package com.aide.codemodel.language.kotlin;

import java.util.Set;

import abcd.ga;
import abcd.ha;
import abcd.ia;
import abcd.na;
import abcd.oa;
import abcd.pa;
import abcd.qa;
import abcd.ra;
import com.aide.codemodel.api.abstraction.Language;
import com.aide.codemodel.HighlighterSyntax;
import com.aide.codemodel.api.abstraction.Syntax;
import com.aide.codemodel.api.abstraction.Tools;
import com.aide.codemodel.api.abstraction.FormatOption;
import com.aide.codemodel.api.abstraction.TypeSystem;
import com.aide.codemodel.api.abstraction.CodeAnalyzer;
import com.aide.codemodel.api.abstraction.SignatureAnalyzer;
import com.aide.codemodel.api.abstraction.CodeRenderer;
import com.aide.codemodel.api.Model;
import com.aide.codemodel.language.cpp.CppTools;

public class KotlinLanguage implements Language {
    private final KotlinCodeModel codeModel;
    private KotlinTools kotlinTools;
    public KotlinLanguage(KotlinCodeModel codeModel) {
        this.codeModel = codeModel;
        Model model = codeModel.model;
		if (model != null)
            this.kotlinTools = new KotlinTools(model);
    }
	
    @Override
    public void shrink() {

    }

    @Override
    public TypeSystem getTypeSystem() {
        return null;
    }

    @Override
    public CodeAnalyzer getCodeAnalyzer() {
        return null;
    }

    @Override
    public SignatureAnalyzer getSignatureAnalyzer() {
        return null;
    }

    @Override
    public Set<? extends FormatOption> getFormatOptionSet() {
        return null;
    }

    @Override
    public CodeRenderer getCodeRenderer() {
        return null;
    }

    @Override
    public String getName() {
        return "Kotlin";
    }

    @Override
    public boolean isParenChar(char c) {
        return false;
    }

    @Override
    public Tools getTools() {
        return kotlinTools;
    }

    @Override
    public Syntax getSyntax() {
        return new HighlighterSyntax();
    }
}
