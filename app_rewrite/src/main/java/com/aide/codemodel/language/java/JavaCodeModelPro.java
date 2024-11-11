package com.aide.codemodel.language.java;
import com.aide.codemodel.api.Model;
import com.aide.codemodel.api.abstraction.CodeCompiler;
import com.aide.codemodel.api.abstraction.Syntax;
import io.github.zeroaicy.util.reflect.ReflectPie;
import com.aide.codemodel.api.EntitySpace;
import com.aide.codemodel.api.ErrorTable;
import com.aide.codemodel.api.IdentifierSpace;
import io.github.zeroaicy.util.reflect.ReflectPieException;
import java.io.Reader;
import com.aide.codemodel.api.FileEntry;
import java.util.Map;
import com.aide.codemodel.api.abstraction.Language;
import com.aide.codemodel.api.SyntaxTree;
import com.aide.codemodel.api.SyntaxTreeStyles;

public class JavaCodeModelPro extends JavaCodeModel {
	//ECJJavaCodeCodeCompiler ecjJavaCodeCodeCompiler;
	
	ErrorTable errorTable2;
	JavaLanguage language;
	
	
	public JavaCodeModelPro(Model model) {
		super(model);
		
		if( model == null){
			return;
		}
		System.out.println("替换 JavaCodeModel");
		this.errorTable2 = model.errorTable;
		IdentifierSpace identifierSpace = model.identifierSpace;
		EntitySpace entitySpace = model.entitySpace;
		
		
		ReflectPie that = ReflectPie.on(this);
		
		String javaLanguageFieldName = "javaLanguage";
		ReflectPie javaLanguageReflectPie = that.field(javaLanguageFieldName);
		this.language = javaLanguageReflectPie.get();		
		javaLanguageReflectPie.set("DW", new JavaSyntaxPro(model.identifierSpace));

		JavaSyntax javaSyntax = (JavaSyntax) language.getSyntax();

		String javaParserFieldName = "javaParser";
		that.set(javaParserFieldName, new JavaParserPro(identifierSpace, model.errorTable, entitySpace, javaSyntax, false));
		
	}

	@Override
	public CodeCompiler getCodeCompiler() {
		return super.getCodeCompiler();
	}

	@Override
	public void fillSyntaxTree(FileEntry fileEntry, Reader reader, Map<Language, SyntaxTree> map, boolean p){
		super.fillSyntaxTree(fileEntry, reader, map, p);
	}

	@Override
	public void fillSyntaxTree(FileEntry fileEntry, Reader reader, Map<Language, SyntaxTreeStyles> map){
		// TODO: Implement this method
		super.fillSyntaxTree(fileEntry, reader, map);
		
		// errorTable2.clearNonParserErrors(fileEntry, this.language);
		
	}
	
	

}
