package com.aide.codemodel.language.java;

import android.util.SparseArray;
import com.aide.codemodel.api.FileEntry;
import com.aide.codemodel.api.FileSpace;
import com.aide.codemodel.api.Model;
import com.aide.codemodel.api.collections.FunctionOfIntInt;
import com.aide.codemodel.api.collections.OrderedMapOfIntInt;
import com.aide.codemodel.api.collections.SetOfFileEntry;
import com.aide.codemodel.api.collections.SetOfInt;
import com.aide.ui.services.AssetInstallationService;
import io.github.zeroaicy.util.reflect.ReflectPie;
import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.eclipse.jdt.internal.compiler.CompilationResult;
import org.eclipse.jdt.internal.compiler.ICompilerRequestor;
import org.eclipse.jdt.internal.compiler.IErrorHandlingPolicy;
import org.eclipse.jdt.internal.compiler.IProblemFactory;
import org.eclipse.jdt.internal.compiler.batch.FileSystem;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileConstants;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.problem.DefaultProblemFactory;
import java.io.IOException;

/**
 * 使用 Eclipse JDT Compiler 进行增量语义分析
 * 此类是Module独立环境 因为R.java文件不止一个
 * 每一个Module会有 源码 库依赖 库项目依赖
 */
public class ProjectEnvironment {
	/**
	 * AssemblyId -> Assembly[assemblyName，assembly路径，]
	 */
	public static HashMap<Integer, FileSpace.Assembly> getAssemblyMap(ReflectPie fileSpaceReflect) {
		return fileSpaceReflect.get("assemblyMap");
	}

	/**
	 * assembly之间的依赖关系
	 * key -> value[被依赖]
	 */
	public static OrderedMapOfIntInt getAssemblyReferences(ReflectPie fileSpaceReflect) {
		return fileSpaceReflect.get("assemblyReferences");
	}

	/**
	 * 文件与所在项目
	 */
	public static FunctionOfIntInt getFileAssembles(ReflectPie fileSpaceReflect) {
		return fileSpaceReflect.get("fileAssembles");
	}

	/*
	 * 注册文件容器
	 */
	public static SetOfFileEntry getRegisteredSolutionFiles(ReflectPie fileSpaceReflect) {
		return fileSpaceReflect.get("registeredSolutionFiles");
	}

	public static void init(Model model, SparseArray<ProjectEnvironment> projectEnvironments) {
		FileSpace fileSpace = model.fileSpace;
		ReflectPie fileSpaceReflect = ReflectPie.on(fileSpace);

		// 置空
		SparseArray<SolutionProject> projects = new SparseArray<>();


		Map<Integer, FileSpace.Assembly> assemblyMap = getAssemblyMap(fileSpaceReflect);

		// 构建项目依赖信息并返回 androidJarAssemblyId(bootclasspath)
		int androidJarAssemblyId = initSolutionProjects(projects, assemblyMap, fileSpaceReflect);
		// 填充项目依赖
		fillProjectReferences(androidJarAssemblyId, projects, assemblyMap, fileSpaceReflect);

		if (androidJarAssemblyId < 0) {
			throw new Error("not found [android.jar | rt.jar](bootclasspath)");
		}
		// android.jar AssemblyId[路径为android.jar]
		String bootclasspath = FileSpace.Assembly.Zo(assemblyMap.get(androidJarAssemblyId));

		// 填充项目信息
		for (int i = 0, size = projects.size(); i < size; i++) {
			SolutionProject project = projects.valueAt(i);

			if (!project.isModule) {
				continue;
			}
			// gradle module
			// 创建 ProjectEnvironment
			int assemblyId = project.getAssemblyId();
			ProjectEnvironment projectEnvironment = new ProjectEnvironment(project, bootclasspath);
			projectEnvironments.put(assemblyId, projectEnvironment);
		}
	}

	private static int initSolutionProjects(SparseArray<SolutionProject> projects, Map<Integer, FileSpace.Assembly> assemblyMap, ReflectPie fileSpaceReflect) {
		int androidJarAssemblyId = -1;
		for (Map.Entry<Integer, FileSpace.Assembly> entry : assemblyMap.entrySet()) {
			Integer assemblyId = entry.getKey();
			FileSpace.Assembly assembly = entry.getValue();

			String assemblyName = FileSpace.Assembly.VH(assembly);
			if ("rt.jar".equals(assemblyName)
				|| "android.jar".equals(assemblyName)) {
				androidJarAssemblyId = assemblyId;
				continue;
			}

			// 创建项目
			SolutionProject project = new SolutionProject(assemblyId, assembly);
			projects.put(assemblyId, project);
		}
		return androidJarAssemblyId;
	}

	private static void fillProjectReferences(int androidJarAssemblyId, SparseArray<SolutionProject> projects, Map<Integer, FileSpace.Assembly> assemblyMap, ReflectPie fileSpaceReflect) {
		OrderedMapOfIntInt assemblyReferences = getAssemblyReferences(fileSpaceReflect);
		OrderedMapOfIntInt.Iterator referencesIterator = assemblyReferences.default_Iterator;
		referencesIterator.init();
		// 遍历所有 SolutionProject的 AssemblyId
		while (referencesIterator.hasMoreElements()) {
			int projectAssemblyId = referencesIterator.nextKey();
			int referencedProjectAssembly = referencesIterator.nextValue();

			// 自己会依赖自己，排除
			if (projectAssemblyId == referencedProjectAssembly
			// 过滤referencedProjectAssembly
			// 这个单独指定
				|| referencedProjectAssembly == androidJarAssemblyId) {
				continue;
			}

			SolutionProject project = projects.get(projectAssemblyId);
			SolutionProject referencedProject = projects.get(referencedProjectAssembly);

			if (referencedProject == null) {
				FileSpace.Assembly assembly = assemblyMap.get(referencedProjectAssembly);
				String assemblyName = FileSpace.Assembly.VH(assembly);
				System.out.printf("没有创建 assemblyName %s id: %s\n ", assemblyName, referencedProjectAssembly);
				continue;
			}
			project.addProjectReferences(referencedProject);
		}
	}



	public static boolean needMake(FileSpace.Assembly assembly) {
		String projectPath = FileSpace.Assembly.Zo(assembly);
		boolean isAarProject = projectPath.endsWith(".aar");
		return !isAarProject && new File(projectPath).isDirectory();
	}

	public static final String coreLambdaStubsJarPath = AssetInstallationService.DW("core-lambda-stubs.jar", true);

	public static void fillFileEntry(SparseArray<ProjectEnvironment> projectEnvironments, Model model, FileEntry fileEntry) {

		FileSpace fileSpace = model.fileSpace;
		int assemblyId = fileSpace.getAssembly(fileEntry);
		for (int i = 0, size = projectEnvironments.size(); i < size; i++) {
			ProjectEnvironment projectEnvironment = projectEnvironments.valueAt(i);
			if (!projectEnvironment.containsId(assemblyId)) {
				continue;
			}
			try {
				//Set<String> sourcePaths = getSourcePaths(fileSpace, projectEnvironment, fileEntry);
				// projectEnvironment.update(sourcePaths, fileEntry);
			}
			catch (Exception e) {}
		}
	}

	private static CompilerOptions compilerOptions;

	private static CompilerOptions getCompilerOptions() {
		if (compilerOptions == null) {
			compilerOptions = new CompilerOptions();
			compilerOptions.parseLiteralExpressionsAsConstants = false;
			compilerOptions.produceDebugAttributes = 
				ClassFileConstants.ATTR_SOURCE
				| ClassFileConstants.ATTR_LINES 
				| ClassFileConstants.ATTR_VARS;
			compilerOptions.produceMethodParameters = true;

			compilerOptions.sourceLevel = ClassFileConstants.JDK17;
			compilerOptions.complianceLevel = ClassFileConstants.JDK17;
			compilerOptions.originalComplianceLevel = ClassFileConstants.JDK17;
			compilerOptions.originalSourceLevel = ClassFileConstants.JDK17;
		}

		return compilerOptions;
	}

	final SolutionProject solutionProject;
	final String bootclasspath;
	// 当前项目id;
	private final int assemblyId;
	public final String assemblyName;
	// 增量语义分析器实现以及增量编译器实现
	public final CompilationUnitDeclarationResolver resolver;

	SetOfInt referenceIds = new SetOfInt();

	// 项目
	public ProjectEnvironment(SolutionProject solutionProject, String bootclasspath) {
		this.solutionProject = solutionProject;
		this.bootclasspath = bootclasspath;

		this.assemblyId = solutionProject.assemblyId;
		this.assemblyName = solutionProject.assemblyName;

		Set<String> classpaths = new HashSet<>();

		// 添加 bootclasspath
		classpaths.add(bootclasspath);
		// 添加 coreLambdaStubsJar
		classpaths.add(coreLambdaStubsJarPath);
		HashSet<SolutionProject> handleProjects = new HashSet<SolutionProject>();
		solutionProject.parserClassPath(handleProjects, classpaths);

		handleProjects.clear();
		solutionProject.parserReferenceIds(handleProjects, referenceIds);

		FileSystem environment = new FileSystem(classpaths.toArray(new String[classpaths.size()]) , null, "UTF-8");

		CompilerOptions compilerOptions = getCompilerOptions();

		this.resolver = new CompilationUnitDeclarationResolver(
			environment, 
			getHandlingPolicy(), 
			compilerOptions,
			getResolverRequestor(),
			getProblemFactory()
		);
	}

	public static Set<String> getSourcePaths(FileSpace fileSpace, ProjectEnvironment projectEnvironment, FileEntry fileEntry) {

		int assemblyId = fileSpace.getAssembly(fileEntry);

		Set<String> sourcePaths = new HashSet<>();

		SetOfFileEntry solutionFiles = fileSpace.getSolutionFiles();
		SetOfFileEntry.Iterator solutionFilesIterator = solutionFiles.default_Iterator;
		solutionFilesIterator.init();
		while (solutionFilesIterator.hasMoreElements()) {
			FileEntry file = solutionFilesIterator.nextKey();
			int assembly = fileSpace.getAssembly(file);
			if (!projectEnvironment.containsId(assembly)) {
				continue;
			}
			if (fileSpace.isRJavaFileEntry(file) && assembly != assemblyId) {
				continue;
			}

			String pathString = file.getPathString();
			String toLowerCase = pathString.toLowerCase();
			if (toLowerCase.endsWith(".java")) {
				sourcePaths.add(pathString);
			}
		}
		sourcePaths.remove(fileEntry);
		return sourcePaths;
	}

	private void update(Set<String> sourcePaths, FileEntry fileEntry) throws IOException {
		try {
			this.resolver.updateSourceFile(sourcePaths, fileEntry.getPathString(), fileEntry.getReader());
		}
		catch (Throwable e) {
			e.printStackTrace();
		}

	}

	public boolean containsId(int id) {
		return referenceIds.contains(id);
	}
	/*
	 * Answer the component to which will be handed back compilation results from the compiler
	 */
	public ICompilerRequestor getResolverRequestor() {
		return new ICompilerRequestor() {
			@Override
			public void acceptResult(CompilationResult compilationResult) {

			}
		};
	}

	public IErrorHandlingPolicy getHandlingPolicy() {

		// passes the initial set of files to the batch oracle (to avoid finding more than once the same units when case insensitive match)
		return new IErrorHandlingPolicy() {
			@Override
			public boolean proceedOnErrors() {
				return !true; // stop if there are some errors
			}
			@Override
			public boolean stopOnFirstError() {
				return false;
			}
			@Override
			public boolean ignoreAllErrors() {
				return false;
			}
		};
	}
	public IProblemFactory getProblemFactory() {
		return new DefaultProblemFactory();
	}

	/*
	 //	public FileSystem getLibraryAccess() {
	 //		FileSystem nameEnvironment = new FileSystem(
	 //			this.checkedClasspaths, 
	 //			this.filenames,
	 //			this.annotationsFromClasspath && CompilerOptions.ENABLED.equals(this.options.get(CompilerOptions.OPTION_AnnotationBasedNullAnalysis)),
	 //			this.limitedModules);
	 //			
	 //		nameEnvironment.module = this.module;
	 //		processAddonModuleOptions(nameEnvironment);
	 //		return nameEnvironment;
	 //	}
	 */

	public int getAssemblyId() {
		return assemblyId;
	}

	public String getAssemblyName() {
		return assemblyName;
	}


	public void reset() {

	}


	/**
	 * 
	 */
	public static class SolutionProject {

		final int assemblyId;
		final String assemblyName;
		final FileSpace.Assembly assembly;

		final String projectPath;

		final boolean isModule;
		final boolean isJar;
		final boolean isAar;

		final String releaseOutputPath;

		final Set<SolutionProject> projectReferences = new HashSet<>();

		public SolutionProject(int assemblyId, FileSpace.Assembly assembly) {
			this.assemblyId = assemblyId;
			this.assembly = assembly;
			this.assemblyName = FileSpace.Assembly.VH(assembly);
			this.projectPath = FileSpace.Assembly.Zo(assembly);

			File projectFile = new File(projectPath);
			boolean isFile = projectFile.isFile();

			this.isAar = !isFile && projectPath.endsWith(".aar");
			this.isJar = isFile && projectPath.endsWith(".jar");
			// 非文件，aar，jar才是 gradle module
			this.isModule = !isAar && !isJar && !isFile;

			this.releaseOutputPath = FileSpace.Assembly.getReleaseOutputPath(assembly);

		}

		public void parserReferenceIds(Set<SolutionProject> handleProjects, SetOfInt referenceIds) {
			if (!isModule) {
				return;
			}
			referenceIds.put(this.assemblyId);

			// 已处理
			handleProjects.add(this);
			for (SolutionProject project : projectReferences) {
				// 防止jar aar 循环依赖
				if (handleProjects.contains(project)) {
					// 已处理
					continue;
				}
				project.parserReferenceIds(handleProjects, referenceIds);
			}
		}

		public void parserClassPath(Set<SolutionProject> handleProjects, Set<String> classpaths) {

			if (this.isJar) {
				classpaths.add(this.projectPath);
			}
			// 已处理
			handleProjects.add(this);

			for (SolutionProject project : projectReferences) {
				// 防止jar aar 循环依赖
				if (handleProjects.contains(project)) {
					// 已处理
					continue;
				}
				project.parserClassPath(handleProjects, classpaths);
			}
		}
		public void addProjectReferences(ProjectEnvironment.SolutionProject referencedProject) {
			// 此时 referencedProject也未填充完毕
			// 因此只能缓存起来
			this.projectReferences.add(referencedProject);
		}

		public int getAssemblyId() {
			return assemblyId;
		}

		public FileSpace.Assembly getAssembly() {
			return assembly;
		}
	}
}
