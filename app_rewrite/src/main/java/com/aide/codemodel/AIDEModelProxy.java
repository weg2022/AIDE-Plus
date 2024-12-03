package com.aide.codemodel;
import com.aide.codemodel.api.callback.*;

import abcd.q2;
import abcd.x2;
import abcd.y2;
import com.aide.codemodel.api.BomReaderFactory;
import com.aide.codemodel.api.Model;
import com.aide.codemodel.api.ErrorTable;
import io.github.zeroaicy.util.reflect.ReflectPie;
import com.aide.codemodel.api.abstraction.Language;
import com.aide.codemodel.api.FileEntry;
import com.aide.codemodel.language.java.JavaCodeModelPro;
import com.aide.codemodel.api.abstraction.CodeModel;
import com.aide.engine.EngineSolution;


/**
 * debug版使用
 */
public class AIDEModelProxy extends Model {

	public AIDEModelProxy(
		OpenFileCallback openFileCallback, 
		StopCallback stopCallback, 
		StructureCallback structureCallback, 
		HighlighterCallback highlighterCallback, 
		SymbolSearcherCallback symbolSearcherCallback, 
		CodeCompleterCallback codeCompleterCallback, 
		RefactoringCallback refactoringCallback, 
		UsageSearcherCallback usageSearcherCallback,
		CodeMetricsCallback codeMetricsCallback, 
		APISearcherCallback aPISearcherCallback, 
		DebugMetadataCallback debugMetadataCallback,
		TemplateEvaluatorCallback templateEvaluatorCallback, 
		y2 y2Var, 
		q2 q2Var, 
		x2 x2Var, 
		BomReaderFactory bomReaderFactory) {
        super(structureCallback, 
			  highlighterCallback, symbolSearcherCallback, codeCompleterCallback, refactoringCallback, usageSearcherCallback, codeMetricsCallback, aPISearcherCallback, debugMetadataCallback, stopCallback, openFileCallback, templateEvaluatorCallback, y2Var, q2Var, x2Var, bomReaderFactory);
	}

	public AIDEModelProxy(
		StructureCallback structureCallback, 
		HighlighterCallback highlighterCallback, 
		SymbolSearcherCallback symbolSearcherCallback, 
		CodeCompleterCallback codeCompleterCallback, 
		RefactoringCallback refactoringCallback,
		UsageSearcherCallback usageSearcherCallback, 
		CodeMetricsCallback codeMetricsCallback, 
		APISearcherCallback aPISearcherCallback, 
		DebugMetadataCallback debugMetadataCallback,
		StopCallback stopCallback, 
		OpenFileCallback openFileCallback,
		TemplateEvaluatorCallback templateEvaluatorCallback, 
		y2 y2Var, 
		q2 q2Var, 
		x2 x2Var, 
		BomReaderFactory bomReaderFactory) {
		super(structureCallback, highlighterCallback, symbolSearcherCallback, codeCompleterCallback, refactoringCallback, usageSearcherCallback, codeMetricsCallback, aPISearcherCallback, debugMetadataCallback, stopCallback, openFileCallback, templateEvaluatorCallback, y2Var, q2Var, x2Var, bomReaderFactory);
		// ReflectPie.on(this).set("errorTable", new ErrorTablePro(this));
	}


	// reset
	@Override
	public void J0() {

		for (CodeModel codeModel : getCodeModels()) {
			if (codeModel instanceof JavaCodeModelPro) {
				JavaCodeModelPro javaCodeModelPro = (JavaCodeModelPro)codeModel;
				// 重置
				javaCodeModelPro.reset();
			}
		}
		super.J0();
	}        

	private EngineSolution engineSolution;
	
	public void setEngineSolution(EngineSolution engineSolution) {
        this.engineSolution = engineSolution;
		
    }
	
	public EngineSolution getEngineSolution() {
        return this.engineSolution;
    }
}
