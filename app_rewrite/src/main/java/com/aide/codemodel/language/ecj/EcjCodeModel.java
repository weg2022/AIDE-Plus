package com.aide.codemodel.language.ecj;
import com.aide.codemodel.api.FileEntry;
import com.aide.codemodel.api.SyntaxTree;
import com.aide.codemodel.api.SyntaxTreeStyles;
import com.aide.codemodel.api.abstraction.CodeCompiler;
import com.aide.codemodel.api.abstraction.CodeModel;
import com.aide.codemodel.api.abstraction.Debugger;
import com.aide.codemodel.api.abstraction.Language;
import com.aide.codemodel.api.abstraction.Preprocessor;
import java.io.Reader;
import java.util.List;
import java.util.Map;

public class EcjCodeModel implements CodeModel {

	@Override
	public void closeArchive() {
		// TODO: Implement this method
	}

	@Override
	public void fillSyntaxTree(FileEntry fileEntry, Reader reader, Map<Language, SyntaxTreeStyles> map) {
		// TODO: Implement this method
	}

	@Override
	public void fillSyntaxTree(FileEntry fileEntry, Reader reader, Map<Language, SyntaxTree> map, boolean p) {
		SyntaxTree SyntaxTree;
	}

	@Override
	public String[] getArchiveEntries(String string) {
		// TODO: Implement this method
		return null;
	}

	@Override
	public Reader getArchiveEntryReader(String string, String string1, String string2) {
		// TODO: Implement this method
		return null;
	}

	@Override
	public long getArchiveVersion(String string) {
		// TODO: Implement this method
		return 0;
	}

	@Override
	public CodeCompiler getCodeCompiler() {
		// TODO: Implement this method
		return null;
	}

	@Override
	public Debugger getDebugger() {
		// TODO: Implement this method
		return null;
	}

	@Override
	public String[] getDefaultFilePatterns() {
		// TODO: Implement this method
		return null;
	}

	@Override
	public String[] getExtendFilePatterns() {
		// TODO: Implement this method
		return null;
	}

	@Override
	public List<Language> getLanguages() {
		// TODO: Implement this method
		return null;
	}

	@Override
	public String getName() {
		// TODO: Implement this method
		return null;
	}

	@Override
	public Preprocessor getPreprocessor() {
		// TODO: Implement this method
		return null;
	}

	@Override
	public boolean isSupportArchiveFile() {
		// TODO: Implement this method
		return false;
	}

	@Override
	public void processVersion(FileEntry fileEntry, Language language) {
		// TODO: Implement this method
	}

	@Override
	public boolean u7() {
		// TODO: Implement this method
		return false;
	}

	@Override
	public void update() {
		// TODO: Implement this method
	}
	
}
