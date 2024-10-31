package com.aide.codemodel.language.java17;

import com.aide.codemodel.api.FileEntry;
import com.aide.codemodel.api.FileSpace;
import com.aide.codemodel.api.Model;
import com.aide.codemodel.api.SyntaxTree;
import com.aide.codemodel.api.SyntaxTreeStyles;
import com.aide.codemodel.api.abstraction.CodeCompiler;
import com.aide.codemodel.api.abstraction.CodeModel;
import com.aide.codemodel.api.abstraction.Debugger;
import com.aide.codemodel.api.abstraction.Language;
import com.aide.codemodel.api.abstraction.Preprocessor;
import com.aide.codemodel.api.collections.SetOfFileEntry;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Java17CodeModel implements CodeModel {
	
	
	String targetVersion;
	private FileSpace fileSpace;
	private SetOfFileEntry solutionFiles;
	
	private final Model model;
	private final boolean disable;
	Java17Language java17Language;
	List<Language> java17Languages = new ArrayList<>();
	
	Java17Compiler Java17Compiler;
	
	public Java17CodeModel(Model model) {
		this.model = model;
		
		if (model == null) {
			this.disable = true;
		} else {
			this.disable = false;
			this.java17Language = new Java17Language(this.model);
			this.java17Languages.add(this.java17Language);
			this.Java17Compiler = new Java17Compiler(this.model);
			
			this.fileSpace = this.model.fileSpace;
			this.solutionFiles = this.fileSpace.getSolutionFiles();
			
			// get()
			FileEntry fristFile = solutionFiles.get();
			this.targetVersion = fileSpace.getTargetVersion(fristFile);
			
		}
		
	}
	
	// 词法分析器 高亮
	@Override
	public void fillSyntaxTree(FileEntry fileEntry, Reader reader, Map<Language, SyntaxTreeStyles> syntaxTreeStylesMap) {
		
	}

	// 语法树填充
	@Override
	public void fillSyntaxTree(FileEntry fileEntry, Reader reader, Map<Language, SyntaxTree> map, boolean p) {

	}
	
	
	@Override
	public CodeCompiler getCodeCompiler() {
		return this.Java17Compiler;
	}

	@Override
	public String[] getDefaultFilePatterns() {
		return new String[]{".java"};
	}

	@Override
	public String[] getExtendFilePatterns() {
		return new String[0];
	}

	@Override
	public List<Language> getLanguages() {
		return this.java17Languages;
	}

	@Override
	public String getName() {
		return "Java";
	}

	@Override
	public void update() {

	}

	
	/******************************no support start ******************************************/

	@Override
	public boolean isSupportArchiveFile() {
		return false;
	}

	@Override
	public void closeArchive() {

	}
	
	@Override
	public Reader getArchiveEntryReader(String string, String string1, String string2) {
		return null;
	}
	@Override
	public String[] getArchiveEntries(String string) {
		return null;
	}

	@Override
	public long getArchiveVersion(String string) {
		return 0;
	}
	@Override
	public Preprocessor getPreprocessor() {
		return null;
	}
	@Override
	public Debugger getDebugger() {
		return null;
	}

	@Override
	public void processVersion(FileEntry fileEntry, Language language) {

	}
	
	@Override
	public boolean u7() {
		return false;
	}
	
	/******************************no support end******************************************/
	
}
