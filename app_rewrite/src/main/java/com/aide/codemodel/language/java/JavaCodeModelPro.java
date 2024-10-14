package com.aide.codemodel.language.java;
import com.aide.codemodel.api.Model;
import com.aide.codemodel.api.abstraction.CodeCompiler;
import com.aide.codemodel.api.abstraction.Syntax;
import io.github.zeroaicy.util.reflect.ReflectPie;
import com.aide.codemodel.api.EntitySpace;
import com.aide.codemodel.api.ErrorTable;
import com.aide.codemodel.api.IdentifierSpace;
import io.github.zeroaicy.util.reflect.ReflectPieException;

public class JavaCodeModelPro extends JavaCodeModel {
	//ECJJavaCodeCodeCompiler ecjJavaCodeCodeCompiler;
	public JavaCodeModelPro(Model model) {
		super(model);
		if (model != null) {
			//ecjJavaCodeCodeCompiler = new ECJJavaCodeCodeCompiler(getLanguages().get(0), model);
			if (model != null) {
				ReflectPie that = ReflectPie.on(this);

				ReflectPie javaLanguageReflectPie;
				try {
					javaLanguageReflectPie = that.field("DW");
				}
				catch (ReflectPieException e) {
					javaLanguageReflectPie = that.field("javaLanguage");					
				}

				JavaLanguage javaLanguage = javaLanguageReflectPie.get();

				javaLanguageReflectPie.set("DW", new JavaSyntaxPro(model.identifierSpace));

				JavaSyntax javaSyntax = (JavaSyntax) javaLanguage.getSyntax();


				String javaParserFieldName = "VH";
				try {
					that.field(javaParserFieldName);
				}
				catch (ReflectPieException e) {
					javaParserFieldName = "javaParser";
				}

				that.set(javaParserFieldName, new JavaParserPro(model.identifierSpace, model.errorTable, model.entitySpace, javaSyntax, false));

            }
		}
	}

	@Override
	public CodeCompiler getCodeCompiler() {
		return super.getCodeCompiler();
	}


}
