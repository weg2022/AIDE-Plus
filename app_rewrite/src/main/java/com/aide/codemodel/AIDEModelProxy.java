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


/**
 * debug版使用
 */
public class AIDEModelProxy extends Model{
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
		BomReaderFactory bomReaderFactory){
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
		BomReaderFactory bomReaderFactory){
		super(structureCallback, highlighterCallback, symbolSearcherCallback, codeCompleterCallback, refactoringCallback, usageSearcherCallback, codeMetricsCallback, aPISearcherCallback, debugMetadataCallback, stopCallback, openFileCallback, templateEvaluatorCallback, y2Var, q2Var, x2Var, bomReaderFactory);
		// ReflectPie.on(this).set("errorTable", new ErrorTablePro(this));
	}        
	
	public static class ErrorTablePro extends ErrorTable{
		public ErrorTablePro(Model model){
			super(model);
		}

		@Override
		public void addSemanticError(FileEntry fileEntry, Language language, int p, int p1, int p2, int p3, String string, int p4){
			if( fileEntry.getCodeModel() instanceof JavaCodeModelPro){
				return;
			}
			super.addSemanticError(fileEntry, language, p, p1, p2, p3, string, p4);
		}

		@Override
		public void addParseError(FileEntry fileEntry, Language language, int p, int p1, int p2, int p3, String string, int p4){
			if( fileEntry.getCodeModel() instanceof JavaCodeModelPro){
				return;
			}
			super.addParseError(fileEntry, language, p, p1, p2, p3, string, p4);
		}

		@Override
		public void Hw(FileEntry fileEntry, Language language, int p, int p1, int p2, int p3, String string, int p4){
			if( fileEntry.getCodeModel() instanceof JavaCodeModelPro){
				return;
			}
			super.Hw(fileEntry, language, p, p1, p2, p3, string, p4);
		}

		@Override
		public void lg(FileEntry fileEntry, Language language, int p, int p1, int p2, int p3, String string, int p4){
			if( fileEntry.getCodeModel() instanceof JavaCodeModelPro){
				return;
			}
			super.lg(fileEntry, language, p, p1, p2, p3, string, p4);
		}
		
	}
}
