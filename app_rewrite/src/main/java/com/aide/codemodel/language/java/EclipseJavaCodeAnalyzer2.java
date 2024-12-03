package com.aide.codemodel.language.java;

import android.util.SparseArray;
import com.aide.codemodel.api.ErrorTable;
import com.aide.codemodel.api.FileEntry;
import com.aide.codemodel.api.FileSpace;
import com.aide.codemodel.api.Model;
import com.aide.codemodel.api.SyntaxTree;
import com.aide.codemodel.api.abstraction.Language;
import com.aide.codemodel.api.collections.MapOfIntLong;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EclipseJavaCodeAnalyzer2 extends JavaCodeAnalyzer {
	JavaCodeModelPro javaCodeModel;
	Model model;
	JavaLanguage javaLanguage;

	ErrorTable errorTable;
	FileSpace fileSpace;

	final MapOfIntLong map;
	Map<String, List<ErrorInfo>> errorInfosMap = new HashMap<>();

	public EclipseJavaCodeAnalyzer2(JavaCodeModelPro codeModel, Model model, JavaLanguage javaLanguage) {
		super(model, javaLanguage);
		this.javaCodeModel = codeModel;
		this.model = model;
		this.javaLanguage = javaLanguage;
		map = new MapOfIntLong();
		
		if (model == null) {
			return;
		}
		this.errorTable = model.errorTable;
		this.fileSpace = model.fileSpace;

	}

	// resolve
	@Override
	public void v5(SyntaxTree syntaxTree) {

		super.v5(syntaxTree);

		// 清除AIDE语义分析器错误
		clearErrors(syntaxTree);


		// 当前文件error count
		FileEntry fileEntry = syntaxTree.getFile();
		// 根据文件版本原则更新解析
		int fileId = fileEntry.getId();
		// get
		long oldVersion = map.v5(fileId);
		long nowVersion = fileEntry.getVersion();
		String pathString = fileEntry.getPathString();

		if (oldVersion == nowVersion) {
			// 使用缓存的错误信息
			List<ErrorInfo> errorInfos = this.errorInfosMap.get(pathString);
			if (errorInfos == null) {
				return;
			}
			for (ErrorInfo errorInfo : errorInfos) {
				// Hw会 put compileErrors里导致 编译器不调用
				errorTable.lg(errorInfo.file, errorInfo.language, errorInfo.startLine, errorInfo.startColumn, errorInfo.endLine, errorInfo.endColumn, errorInfo.msg, errorInfo.kind);
			}
			return;
		}

		// 更新
		map.VH(fileId, nowVersion);

		// 解析
		ProjectEnvironment projectEnvironment = getProjectEnvironment(fileEntry);
		projectEnvironment.resolve3(syntaxTree);

		errorInfosMap.put(pathString, getErrorInfos(syntaxTree));

	}


	private ProjectEnvironment getProjectEnvironment(FileEntry file) {
		int assemblyId = fileSpace.getAssembly(file);
		SparseArray<ProjectEnvironment> projectEnvironments = javaCodeModel.projectEnvironments;
		if (projectEnvironments == null) {
			return null;
		}
		ProjectEnvironment projectEnvironment = projectEnvironments.get(assemblyId);
		if (projectEnvironment == null) {
			return null;
		}
		return projectEnvironment;
	}

	private List<ErrorInfo> getErrorInfos(SyntaxTree syntaxTree) {
		try {
			FileEntry fileEntry = syntaxTree.getFile();
			int count = errorTable.SI(fileEntry, syntaxTree.getLanguage());
			List<ErrorInfo> errorInfos = new ArrayList<>();

			for (int index = 0; index < count;index++) {
				FileEntry file = syntaxTree.getFile();
				Language language = syntaxTree.getLanguage();

				int startLine = errorTable.getErrorStartLine(file, language, index);
				int startColumn = errorTable.getErrorStartColumn(file, language, index);

				int endLine = errorTable.getErrorEndLine(file, language, index);
				int endColumn = errorTable.getErrorEndColumn(file, language, index);

				String msg = errorTable.getErrorText(file, language, index);

				int kind = errorTable.getErrorKind(file, language, index);

				// 静态方法
				if (kind != 300) {
					errorInfos.add(new ErrorInfo(file, language, startLine, startColumn, endLine, endColumn, msg, kind));
				}
			}
			return errorInfos;
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
		return null;
	}

	private void clearErrors(SyntaxTree syntaxTree) {
		try {
			FileEntry fileEntry = syntaxTree.getFile();
			int count = errorTable.SI(fileEntry, syntaxTree.getLanguage());
			List<ErrorInfo> errorInfos = new ArrayList<>();

			for (int index = 0; index < count;index++) {
				FileEntry file = syntaxTree.getFile();
				Language language = syntaxTree.getLanguage();

				int startLine = errorTable.getErrorStartLine(file, language, index);
				int startColumn = errorTable.getErrorStartColumn(file, language, index);

				int endLine = errorTable.getErrorEndLine(file, language, index);
				int endColumn = errorTable.getErrorEndColumn(file, language, index);

				String msg = errorTable.getErrorText(file, language, index);

				int kind = errorTable.getErrorKind(file, language, index);

				// 静态方法
				if (kind == 300) {
					errorInfos.add(new ErrorInfo(file, language, startLine, startColumn, endLine, endColumn, msg));
				}  else {
					// System.out.println(String.format("kind %s，msg: %s，filepath: %s", kind, msg, file.getPathString()));
				}
			}

			this.errorTable.clearNonParserErrors(syntaxTree.getFile(), syntaxTree.getLanguage());

			for (ErrorInfo errorInfo : errorInfos) {
				// Hw会 put compileErrors里导致 编译器不调用
				errorTable.lg(errorInfo.file, errorInfo.language, errorInfo.startLine, errorInfo.startColumn, errorInfo.endLine, errorInfo.endColumn, errorInfo.msg, 300);
			}
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
	}

	public static class ErrorInfo {
		public FileEntry file;
		public Language language;
		public int startLine;
		public int startColumn;
		public int endLine;
		public int endColumn;
		public String msg;

		public int kind;

		public ErrorInfo(FileEntry file, Language language, int startLine, int startColumn, int endLine, int endColumn, String msg) {
			this.file = file;
			this.language = language;
			this.startLine = startLine;
			this.startColumn = startColumn;
			this.endLine = endLine;
			this.endColumn = endColumn;
			this.msg = msg;
			this.kind = 300;
		}
		public ErrorInfo(FileEntry file, Language language, int startLine, int startColumn, int endLine, int endColumn, String msg, int kind) {
			this.file = file;
			this.language = language;
			this.startLine = startLine;
			this.startColumn = startColumn;
			this.endLine = endLine;
			this.endColumn = endColumn;
			this.msg = msg;
			this.kind = kind;
		}

		@Override
		public String toString() {
			return String.format(" %s -> %s", msg, file.getPathString());
		}
	}
}
