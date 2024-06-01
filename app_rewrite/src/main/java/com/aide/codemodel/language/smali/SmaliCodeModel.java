package com.aide.codemodel.language.smali;

import com.aide.codemodel.api.FileEntry;
import com.aide.codemodel.api.SyntaxTree;
import com.aide.codemodel.api.SyntaxTreeStyles;
import com.aide.codemodel.api.abstraction.CodeModel;
import com.aide.codemodel.api.abstraction.Compiler;
import com.aide.codemodel.api.abstraction.Debugger;
import com.aide.codemodel.api.abstraction.Language;
import com.aide.codemodel.api.abstraction.Preprocessor;
import com.aide.codemodel.language.java.JavaSyntax;
import java.io.Reader;
import java.util.List;
import java.util.Map;
import com.aide.codemodel.api.Model;
import com.aide.codemodel.language.java.JavaLanguage;
import java.util.ArrayList;
import com.aide.codemodel.language.java.JavaParser;

public class SmaliCodeModel implements CodeModel {
	
	
	Model model;

	private JavaLanguage m29;
	
	private SmaliLanguage smaliLanguage;
	private List<Language> languages = new ArrayList<>();

	private JavaParser smaliParser;
	
	
	public SmaliCodeModel(Model model) {
		this.model = model;
		this.smaliLanguage = new SmaliLanguage(model);
		this.languages.add(this.smaliLanguage);
		
		if( this.model != null ){
			//this.smaliParser = new SmaliParser(model.identifierSpace, model.errorTable, model.entitySpace, this.smaliLanguage.getSyntax());
			this.smaliParser = new JavaParser(model.identifierSpace, model.errorTable, model.entitySpace, this.smaliLanguage.getSyntax(), false);
		}
	}

	@Override
	public void closeArchive() {}

	@Override
	public void fillSyntaxTree(FileEntry fileEntry, Reader reader, Map<Language, SyntaxTreeStyles> map) {
		System.out.println(fileEntry.getFullNameString());
	}

	@Override
	public void fillSyntaxTree(FileEntry fileEntry, Reader reader, Map<Language, SyntaxTree> map, boolean p) {
		SyntaxTreeStyles makeSyntaxTreeStyles = this.model.U2.makeSyntaxTreeStyles();
		if (map.containsKey(this.smaliLanguage)) {
			this.smaliParser.v5(makeSyntaxTreeStyles, fileEntry, p, map.get(this.smaliParser));
		}
	}

	@Override
	public String[] getArchiveEntries(String string) {
		return null;
	}

	@Override
	public Reader getArchiveEntryReader(String string, String string1, String string2) {
		return null;
	}

	@Override
	public long getArchiveVersion(String string) {
		return 0;
	}

	@Override
	public Compiler getCompiler() {
		return null;
	}

	@Override
	public Debugger getDebugger() {
		return null;
	}

	@Override
	public String[] getDefaultFilePatterns() {
		return new String[]{".smali"};
	}

	@Override
	public String[] getExtendFilePatterns() {
		return new String[0];
	}

	@Override
	public List<Language> getLanguages() {
		return this.languages;
	}

	@Override
	public String getName() {
		return "Smali";
	}

	@Override
	public Preprocessor getPreprocessor() {
		return null;
	}
	@Override
	public boolean isSupportArchiveFile() {
		return false;
	}

	@Override
	public void processVersion(FileEntry fileEntry, Language language) {
		
	}

	@Override
	public boolean u7() {
		return false;
	}

	@Override
	public void update() {
	}

}
