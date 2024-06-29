package com.aide.codemodel.language.aidl;
import abcd.tp;
import com.aide.codemodel.DefaultTools;
import com.aide.codemodel.api.FileEntry;
import com.aide.codemodel.api.Model;
import com.aide.codemodel.api.SyntaxTree;
import com.aide.codemodel.api.abstraction.Language;
import com.aide.codemodel.api.callback.CodeCompleterCallback;

public class AidlTools extends DefaultTools {
	public AidlTools(Model model) {
        super(model);
    }

    @Override
    public void er(SyntaxTree syntaxTree, FileEntry fileEntry, Language language, int line, int column) {
        String pathString = fileEntry.getPathString();
		CodeCompleterCallback codeCompleterCallback = model.codeCompleterCallback;
		if (pathString.endsWith(".aidl") ) {

			codeCompleterCallback.j6();

            codeCompleterCallback.Zo("true");
			codeCompleterCallback.Zo("false");
			codeCompleterCallback.Zo("null");
			codeCompleterCallback.Zo("abstract");
			codeCompleterCallback.Zo("boolean");
			codeCompleterCallback.Zo("break");
			codeCompleterCallback.Zo("byte");
			codeCompleterCallback.Zo("case");
			codeCompleterCallback.Zo("catch");
			codeCompleterCallback.Zo("char");
			codeCompleterCallback.Zo("class");
			codeCompleterCallback.Zo("const");
			codeCompleterCallback.Zo("continue");
			codeCompleterCallback.Zo("default");
			codeCompleterCallback.Zo("do");
			codeCompleterCallback.Zo("double");
			codeCompleterCallback.Zo("else");
			codeCompleterCallback.Zo("extends");
			codeCompleterCallback.Zo("final");
			codeCompleterCallback.Zo("finally");
			codeCompleterCallback.Zo("float");
			codeCompleterCallback.Zo("for");
			codeCompleterCallback.Zo("goto");
			codeCompleterCallback.Zo("if");
			codeCompleterCallback.Zo("implements");
			codeCompleterCallback.Zo("import");
			codeCompleterCallback.Zo("instanceof");
			codeCompleterCallback.Zo("int");
			codeCompleterCallback.Zo("interface");
			codeCompleterCallback.Zo("long");
			codeCompleterCallback.Zo("native");
			codeCompleterCallback.Zo("new");
			codeCompleterCallback.Zo("package");
			codeCompleterCallback.Zo("private");
			codeCompleterCallback.Zo("public");
			codeCompleterCallback.Zo("short");
			codeCompleterCallback.Zo("super");
			codeCompleterCallback.Zo("switch");
			codeCompleterCallback.Zo("synchronized");
			codeCompleterCallback.Zo("this");
			codeCompleterCallback.Zo("throw");
			codeCompleterCallback.Zo("protected");
			codeCompleterCallback.Zo("transient");
			codeCompleterCallback.Zo("return");
			codeCompleterCallback.Zo("void");
			codeCompleterCallback.Zo("static");
			codeCompleterCallback.Zo("strictfp");
			codeCompleterCallback.Zo("while");
			codeCompleterCallback.Zo("try");
			codeCompleterCallback.Zo("volatile");
			codeCompleterCallback.Zo("throws");
			
			codeCompleterCallback.Zo("parcelable");
			codeCompleterCallback.Zo("import");
			codeCompleterCallback.Zo("package");
			codeCompleterCallback.Zo("in");
			codeCompleterCallback.Zo("out");
			codeCompleterCallback.Zo("inout");
			codeCompleterCallback.Zo("cpp_header");
			codeCompleterCallback.Zo("ndk_header");
			codeCompleterCallback.Zo("rust_type");
			codeCompleterCallback.Zo("const");
			codeCompleterCallback.Zo("true");
			codeCompleterCallback.Zo("false");
			codeCompleterCallback.Zo("interface");
			codeCompleterCallback.Zo("oneway");
			codeCompleterCallback.Zo("enum");
			codeCompleterCallback.Zo("union");
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
