//
// Decompiled by Jadx - 987ms
//
package com.aide.codemodel.language.aidl;

import abcd.cy;
import abcd.dy;
import abcd.ey;
import abcd.fy;
import abcd.g6;
import abcd.gy;
import abcd.iy;
import abcd.t6;
import com.aide.codemodel.api.FileEntry;
import com.aide.codemodel.api.Model;
import com.aide.codemodel.api.SyntaxTree;
import com.aide.codemodel.api.SyntaxTreeSytles;
import com.aide.codemodel.api.abstraction.CodeModel;
import com.aide.codemodel.api.abstraction.Compiler;
import com.aide.codemodel.api.abstraction.Debugger;
import com.aide.codemodel.api.abstraction.Language;
import com.aide.codemodel.api.abstraction.Preprocessor;
import com.aide.codemodel.language.java.JSharpCommentsLanguage;
import com.aide.codemodel.language.java.JavaCompiler;
import com.aide.codemodel.language.java.JavaDebugger;
import com.aide.codemodel.language.java.JavaParser;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.aide.codemodel.language.classfile.JavaBinaryLanguage;
import com.aide.codemodel.language.java.JavaSyntax;

public class AidlCodeModel implements CodeModel {


    private final JavaBinaryLanguage language;

    private final JSharpCommentsLanguage commentsLanguage;

    private g6 VH;

    private JavaParser parser;

    //private JavaCompiler gn;
	//private JavaDebugger u7;

    private final Model model;


    private t6 v5;


    public AidlCodeModel(Model model) {
        try {
            this.model = model;
            this.language = new AidlLanguage(model);
            this.commentsLanguage = new JSharpCommentsLanguage(model, false);
            if (model != null) {
                //this.gn = new JavaCompiler(model, this.language);
                //this.u7 = new JavaDebugger(model, this.language, this);
                this.v5 = new t6(model.identifierSpace, model.Mr, false, this.language, this.commentsLanguage);
                this.parser = new JavaParser(model.identifierSpace, model.Mr, model.entitySpace, (JavaSyntax)this.language.getSyntax(), true){
					@Override
					public void DW(String string) {}
				};
                this.VH = new g6(model);
            }
        }
		catch (Throwable th) {
            throw new Error(th);
        }
    }

    public void closeArchive() {

    }
	@Override
    public void fillSyntaxTree(FileEntry fileEntry, Reader reader, Map<Language, SyntaxTreeSytles> map) {
        try {
            this.v5.Zo(fileEntry, reader, false, false, false, false, map.get(this.language), map.get(this.commentsLanguage));
        }
		catch (Throwable th) {
            throw new Error(th);
        }
    }

	@Override
    public void fillSyntaxTree(FileEntry fileEntry, Reader reader, Map<Language, SyntaxTree> map, boolean z) {
        try {
            SyntaxTreeSytles syntaxTreeSytles = this.model.U2.j6();
            SyntaxTreeSytles syntaxTreeSytles2 = this.model.U2.j6();
            this.v5.Zo(fileEntry, reader, false, false, map.containsKey(this.language), map.containsKey(this.commentsLanguage), syntaxTreeSytles, syntaxTreeSytles2);

            if (map.containsKey(this.language)) {
                this.parser.v5(syntaxTreeSytles, fileEntry, z, map.get(this.language));
            }
            this.model.U2.DW(syntaxTreeSytles);

            if (map.containsKey(this.commentsLanguage)) {
                this.VH.j6(syntaxTreeSytles2, fileEntry, z, map.get(this.commentsLanguage));
            }
            this.model.U2.DW(syntaxTreeSytles2);
        }
		catch (Throwable th) {
            throw new Error(th);
        }
    }

    @Override
    public String[] getArchiveEntries(String str) {
        return new String[0];
    }

    @Override
    public Reader getArchiveEntryReader(String str, String str2, String str3) {
        return null;
    }

    @Override
    public long getArchiveVersion(String str) {
        return 0L;
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
        return new String[]{"*.aidl"};
    }

    @Override
    public String[] getExtendFilePatterns() {
        return new String[0];
    }

    @Override
    public List<Language> getLanguages() {
        ArrayList<Language> arrayList = new ArrayList<>();
		arrayList.add(this.language);
		arrayList.add(this.commentsLanguage);
		return arrayList;
    }

    @Override
    public String getName() {
        return "AIDL";
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


