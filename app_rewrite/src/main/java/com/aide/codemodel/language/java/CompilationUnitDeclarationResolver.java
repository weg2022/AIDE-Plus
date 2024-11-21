package com.aide.codemodel.language.java;

import com.aide.codemodel.api.FileEntry;
import io.github.zeroaicy.util.IOUtils;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.jdt.internal.compiler.CompilationResult;
import org.eclipse.jdt.internal.compiler.Compiler;
import org.eclipse.jdt.internal.compiler.ICompilerRequestor;
import org.eclipse.jdt.internal.compiler.IErrorHandlingPolicy;
import org.eclipse.jdt.internal.compiler.IProblemFactory;
import org.eclipse.jdt.internal.compiler.ast.CompilationUnitDeclaration;
import org.eclipse.jdt.internal.compiler.ast.ImportReference;
import org.eclipse.jdt.internal.compiler.batch.CompilationUnit;
import org.eclipse.jdt.internal.compiler.env.ICompilationUnit;
import org.eclipse.jdt.internal.compiler.env.INameEnvironment;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.problem.AbortCompilation;
import org.eclipse.jdt.internal.compiler.util.HashtableOfType;
import org.eclipse.jdt.internal.compiler.ast.TypeDeclaration;
import java.util.Set;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import com.aide.common.AppLog;
import java.io.FileNotFoundException;

public class CompilationUnitDeclarationResolver extends Compiler {



	public CompilationUnitDeclarationResolver(
		INameEnvironment environment,
		IErrorHandlingPolicy policy,
		CompilerOptions compilerOptions,
		ICompilerRequestor requestor,
		IProblemFactory problemFactory) {
		super(environment, policy, compilerOptions, requestor, problemFactory);
	}













	@Override
	public void reset() {
		super.reset();
		// this.lookupEnvironment.reset();
		// this.compilationUnitDeclarationMap.clear();
	}

	@Override
	public CompilationUnitDeclaration resolve(ICompilationUnit sourceUnit, boolean verifyMethods, boolean analyzeCode, boolean generateCode) {
		return super.resolve(sourceUnit, verifyMethods, analyzeCode, generateCode);
	}
	
	
	Map<String, CompilationUnitDeclaration> compilationUnitDeclarationCacheMap = new HashMap<>();


	public CompilationUnitDeclaration resolve(String fileName) {
		
		// 使用已经缓存好的
		return null;
	}
	
	Map<String, CompilationUnitDeclaration> resolvedMap = new HashMap<>();
	/**
	 * 更新改动的源文件 -> resolve
	 */
	public CompilationUnitDeclaration updateSourceFile(Set<String> sourcePaths, String fileName, Reader reader) throws IOException {
		
		/*if( resolvedMap.containsKey(fileName)){
			return resolvedMap.get(fileName);
		}*/
		
		// 有文件变动 重新解析
		this.reset();
		
		// 移除自己
		sourcePaths.remove(fileName);
		
		// Map<String, CompilationUnitDeclaration> unitCacheMap = new HashMap<>();
		// 添加源文件依赖
		for (String filePath : sourcePaths) {
			// unitCacheMap.put(filePath, 
			buildTypeBindings(filePath);
			//);
		}
		sourcePaths.add(fileName);
		
		// 会自动 buildTypeBindings
		if( reader == null ){
			reader = new InputStreamReader(new FileInputStream(fileName));
		}
		
		CompilationUnitDeclaration resolve = dietParse(fileName, reader, this);
		// binding resolution
		this.lookupEnvironment.completeTypeBindings();
		
		// 必须 completeTypeBindings 后才能 resolve
		// resolve
		/*
		for (String filePath : sourcePaths) {
			// 是不是，不是必须的🤔
			resolve(unitCacheMap.get(filePath));
		}*/
		
		// 重新解析
		resolve( resolve );
		
		// resolvedMap.put(fileName, resolve);
		
		return resolve;
	}
	
	/**
	 * 重新绑定
	 */
	private CompilationUnitDeclaration buildTypeBindings(String filePath) throws AbortCompilation, FileNotFoundException {
		CompilationUnitDeclaration unit;
		// 从缓存中重新填充
		/*
		if (compilationUnitDeclarationCacheMap.containsKey(filePath)) {
			unit = compilationUnitDeclarationCacheMap.get(filePath);
			CompilationResult unitResult = unit.compilationResult;
			unit.cleanUp();

			this.lookupEnvironment.buildTypeBindings(unit, null);

			ImportReference currentPackage = unit.currentPackage;
			if (currentPackage != null) {
				unitResult.recordPackageName(currentPackage.tokens);
			}
		} else {
			//*/
			// AppLog.println_d("dietParse form %s", filePath);
			Reader reader = new InputStreamReader(new FileInputStream(filePath));
			unit = dietParse(filePath, reader, this);
		//}
		return unit;
	}

	private void resolve(CompilationUnitDeclaration unit) {
		boolean verifyMethods = true;
		boolean analyzeCode = true;
		boolean generateCode = !true;

		this.lookupEnvironment.unitBeingCompleted = unit;

		// 解析
		this.parser.getMethodBodies(unit);
		if (unit.scope != null) {
			// fault in fields & methods
			unit.scope.faultInTypes();
			if (unit.scope != null && verifyMethods) {
				// http://dev.eclipse.org/bugs/show_bug.cgi?id=23117
				// verify inherited methods
				unit.scope.verifyMethods(this.lookupEnvironment.methodVerifier());
			}
			// type checking
			unit.resolve();

			// flow analysis
			if (analyzeCode) unit.analyseCode();

			// code generation
			if (generateCode) unit.generateCode();

			// finalize problems (suppressWarnings)
			unit.finalizeProblems();
		}

		this.lookupEnvironment.unitBeingCompleted = null;
	}

	private static CompilationUnitDeclaration dietParse(String fileName, Reader reader, CompilationUnitDeclarationResolver resolver) throws AbortCompilation {
		char[] contents;
		try {
			contents = IOUtils.readAllChars(reader, true);
		}
		catch (IOException e) {
			contents = new char[0];
		}
		CompilationUnit sourceUnit2 = new CompilationUnit(contents, fileName, "UTF-8");
		CompilationResult unitResult = new CompilationResult(sourceUnit2, 0, 1, resolver.options.maxProblemsPerUnit);
		CompilationUnitDeclaration parsedUnit;

		try {

			parsedUnit = resolver.parser.dietParse(sourceUnit2, unitResult);
			
			// 缓存 有些状态并没有清除
			resolver.compilationUnitDeclarationCacheMap.put(fileName, parsedUnit);

			// initial type binding creation
			resolver.lookupEnvironment.buildTypeBindings(parsedUnit, null);
			ImportReference currentPackage = parsedUnit.currentPackage;
			if (currentPackage != null) {
				unitResult.recordPackageName(currentPackage.tokens);
			}
		}
		catch (AbortCompilation a) {
			// best effort to find a way for reporting this problem:
			if (a.compilationResult == null)
				a.compilationResult = unitResult;
			throw a;
		}
		return parsedUnit;
	}


}
