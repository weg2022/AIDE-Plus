package com.aide.codemodel.language.aidl;
import com.aide.codemodel.language.java.JavaLanguage;
import com.aide.codemodel.api.abstraction.SignatureAnalyzer;
import com.aide.codemodel.api.abstraction.CodeAnalyzer;
import com.aide.codemodel.api.abstraction.TypeSystem;
import com.aide.codemodel.api.abstraction.CodeRenderer;
import com.aide.codemodel.api.Model;
import com.aide.codemodel.api.abstraction.Language;
import com.aide.codemodel.api.abstraction.Tools;
import com.aide.codemodel.api.abstraction.Syntax;
import java.util.Set;
import com.aide.codemodel.api.abstraction.FormatOption;
import com.aide.codemodel.language.java.JavaSyntax;
import com.aide.codemodel.language.java.JavaTools;
import com.aide.codemodel.api.abstraction.CodeModel;
import com.aide.codemodel.language.java.JavaCodeModel;
import java.util.List;
import io.github.zeroaicy.util.Log;

public class AidlLanguage implements Language {


	JavaSyntax aidlSyntax;
	JavaCodeModel javaCodeModel;
	private JavaTools aidlTools;

	private Set<? extends FormatOption> formatOptionSet;


	Model model;
	public AidlLanguage(Model model) {
		this.model = model;

		if (model != null) {
			try {
				this.aidlSyntax = new JavaSyntax(model.identifierSpace);
			}
			catch (Throwable e) {
				Log.d("AidlLanguage", "create Syntax", e);
			}
			//init();
		}
	}

	private void init() {
		if (this.model == null) return;
		if (this.javaCodeModel != null) return;

		CodeModel[] codeModels = model.fileSpace.getCodeModels();
		if (codeModels == null) return;
		for (CodeModel codeModel : codeModels) {
			if (codeModel instanceof JavaCodeModel) {
				this.javaCodeModel = (JavaCodeModel) codeModel;
			}
		}
		if (this.javaCodeModel != null) {
			Language language = javaCodeModel.getLanguages().get(0);
			formatOptionSet = language.getFormatOptionSet();
			this.aidlTools = new JavaTools(model, (JavaLanguage)language);
		}
	}


	@Override
	public CodeAnalyzer getCodeAnalyzer() {
		// TODO: Implement this method
		return null;
	}

	@Override
	public CodeRenderer getCodeRenderer() {
		// TODO: Implement this method
		return null;
	}

	@Override
	public SignatureAnalyzer getSignatureAnalyzer() {
		// TODO: Implement this method
		return null;
	}

	@Override
	public TypeSystem getTypeSystem() {
		return null;
	}

	@Override
	public String getName() {
		return "AIDL";
	}
	@Override
	public Set<? extends FormatOption> getFormatOptionSet() {
		return this.formatOptionSet;
	}

	@Override
	public JavaSyntax getSyntax() {
		return this.aidlSyntax;
	}

	@Override
	public Tools getTools() {
		return this.aidlTools;
	}

	@Override
	public boolean isParenChar(char p) {
		// TODO: Implement this method
		return false;
	}

	@Override
	public void shrink() {
		// TODO: Implement this method
	}
}
