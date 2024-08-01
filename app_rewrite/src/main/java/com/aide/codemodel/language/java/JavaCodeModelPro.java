package com.aide.codemodel.language.java;
import com.aide.codemodel.api.Model;
import com.aide.codemodel.api.abstraction.Compiler;
import com.aide.codemodel.api.abstraction.Syntax;
import io.github.zeroaicy.util.reflect.ReflectPie;
import com.aide.codemodel.api.EntitySpace;
import com.aide.codemodel.api.ErrorTable;
import com.aide.codemodel.api.IdentifierSpace;

public class JavaCodeModelPro extends JavaCodeModel {
	//ECJJavaCodeCompiler ecjJavaCodeCompiler;
	public JavaCodeModelPro(Model model) {
		super(model);
		if( model != null ){
			//ecjJavaCodeCompiler = new ECJJavaCodeCompiler(getLanguages().get(0), model);
			if (model != null) {
				ReflectPie on = ReflectPie.on(this);
				JavaLanguage javaLanguage = on.get("DW");
				JavaSyntax javaSyntax = (JavaSyntax) javaLanguage.getSyntax();
				on.set("VH", new JavaParserPro(model.identifierSpace, model.errorTable, model.entitySpace, javaSyntax, false));
            }
		}
	}

	@Override
	public Compiler getCompiler() {
		return super.getCompiler();
	}
	
	
}
