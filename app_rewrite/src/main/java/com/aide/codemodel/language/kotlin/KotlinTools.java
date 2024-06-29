package com.aide.codemodel.language.kotlin;

import com.aide.codemodel.DefaultTools;
import com.aide.codemodel.api.FileEntry;
import com.aide.codemodel.api.Model;
import com.aide.codemodel.api.SyntaxTree;
import com.aide.codemodel.api.abstraction.Language;
import com.aide.codemodel.api.callback.CodeCompleterCallback;

public class KotlinTools extends DefaultTools {

    public KotlinTools(Model model) {
        super(model);
    }

    @Override
    public void er(SyntaxTree syntaxTree, FileEntry fileEntry, Language language, int line, int column) {
        String pathString = fileEntry.getPathString();
		CodeCompleterCallback codeCompleterCallback = model.codeCompleterCallback;
		if (pathString.endsWith(".kt") 
			|| pathString.endsWith(".kts")) {
				
			codeCompleterCallback.j6();
			
            codeCompleterCallback.Zo("abstract");
            codeCompleterCallback.Zo("actual");
            codeCompleterCallback.Zo("annotation");
            codeCompleterCallback.Zo("as");
            codeCompleterCallback.Zo("as?");
            codeCompleterCallback.Zo("assert");
            codeCompleterCallback.Zo("break");
            codeCompleterCallback.Zo("by");
            codeCompleterCallback.Zo("catch");
            codeCompleterCallback.Zo("class");
            codeCompleterCallback.Zo("companion");
            codeCompleterCallback.Zo("const");
            codeCompleterCallback.Zo("constuctor");
            codeCompleterCallback.Zo("continue");
            codeCompleterCallback.Zo("data");
            codeCompleterCallback.Zo("do");
            codeCompleterCallback.Zo("else");
            codeCompleterCallback.Zo("enum");
            codeCompleterCallback.Zo("expect");
            codeCompleterCallback.Zo("finally");
            codeCompleterCallback.Zo("for");
            codeCompleterCallback.Zo("fun");
            codeCompleterCallback.Zo("get");
            codeCompleterCallback.Zo("if");
            codeCompleterCallback.Zo("implements");
            codeCompleterCallback.Zo("import");
            codeCompleterCallback.Zo("interface");
            codeCompleterCallback.Zo("in");
            codeCompleterCallback.Zo("infix");
            codeCompleterCallback.Zo("init");
            codeCompleterCallback.Zo("internal");
            codeCompleterCallback.Zo("inline");
            codeCompleterCallback.Zo("is");
            codeCompleterCallback.Zo("lateinit");
            codeCompleterCallback.Zo("native");
            codeCompleterCallback.Zo("object");
            codeCompleterCallback.Zo("open");
            codeCompleterCallback.Zo("operator");
            codeCompleterCallback.Zo("or");
            codeCompleterCallback.Zo("out");
            codeCompleterCallback.Zo("override");
            codeCompleterCallback.Zo("package");
            codeCompleterCallback.Zo("private");
            codeCompleterCallback.Zo("protected");
            codeCompleterCallback.Zo("public");
            codeCompleterCallback.Zo("reified");
            codeCompleterCallback.Zo("return");
            codeCompleterCallback.Zo("sealed");
            codeCompleterCallback.Zo("set");
            codeCompleterCallback.Zo("super");
            codeCompleterCallback.Zo("this");
            codeCompleterCallback.Zo("throw");
            codeCompleterCallback.Zo("try");
            codeCompleterCallback.Zo("typealias");
            codeCompleterCallback.Zo("val");
            codeCompleterCallback.Zo("var");
            codeCompleterCallback.Zo("vararg");
            codeCompleterCallback.Zo("when");
            codeCompleterCallback.Zo("where");
            codeCompleterCallback.Zo("while");
            codeCompleterCallback.Zo("true");
            codeCompleterCallback.Zo("false");
            codeCompleterCallback.Zo("null");
			// j6 -> J8
            codeCompleterCallback.J8(fileEntry, language, line, column, tp(fileEntry, line, column), false, false);
			return;
        }
		codeCompleterCallback.a8(fileEntry, line, column);
    }

	protected int tp(FileEntry fileEntry, int line, int column) {
        
		String Mr = fileEntry.Mr(line, column);
		int length = Mr.length() - 1;
		while (length >= 0) {
			if (!Character.isLetter(Mr.charAt(length)) && Mr.charAt(length) != '.') {
				break;
			}
			length--;
		}
		return length + 2;
    }
}
