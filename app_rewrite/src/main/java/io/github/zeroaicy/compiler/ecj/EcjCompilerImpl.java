package io.github.zeroaicy.compiler.ecj;
import io.github.zeroaicy.compiler.ecj.annotation.BatchAnnotationProcessorManager;
import java.io.File;
import java.io.IOException;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticListener;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import org.eclipse.jdt.core.compiler.CompilationProgress;
import org.eclipse.jdt.internal.compiler.AbstractAnnotationProcessorManager;
import org.eclipse.jdt.internal.compiler.ICompilerRequestor;
import org.eclipse.jdt.internal.compiler.batch.ClasspathJar;
import org.eclipse.jdt.internal.compiler.batch.ClasspathJrt;
import org.eclipse.jdt.internal.compiler.batch.FileSystem;
import org.eclipse.jdt.internal.compiler.batch.Main;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileConstants;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.problem.DefaultProblemFactory;
import org.eclipse.jdt.internal.compiler.tool.EclipseCompilerRequestor;
import org.eclipse.jdt.internal.compiler.util.Util;
import io.github.zeroaicy.compiler.ecj.util.EmptyWrite;
import org.eclipse.jdt.internal.compiler.batch.ClasspathUtils;
import java.util.Arrays;

public class EcjCompilerImpl extends Main {
	public JavaFileManager fileManager;
	
	public DiagnosticListener<? super JavaFileObject> diagnosticListener;


	protected PrintWriter err;
	private String[] expandedCommandLine = new String[0];
	
	private static PrintWriter PrintWriter = new PrintWriter(new EmptyWrite());
	public static PrintWriter getPrintWriter(){
		return PrintWriter;
	}
	public EcjCompilerImpl(){
		super(getPrintWriter(), getPrintWriter(), false);
	}
	
	public EcjCompilerImpl( PrintWriter outWriter, PrintWriter errWriter, boolean systemExitWhenFinished ) {
		super(outWriter, errWriter, systemExitWhenFinished);
	}

	public EcjCompilerImpl( PrintWriter outWriter, PrintWriter errWriter, boolean systemExitWhenFinished, Map<String, String> customDefaultOptions ) {
		super(outWriter, errWriter, systemExitWhenFinished, customDefaultOptions);
	}

	public EcjCompilerImpl( PrintWriter outWriter, PrintWriter errWriter, boolean systemExitWhenFinished, Map<String, String> customDefaultOptions, CompilationProgress compilationProgress ) {
		super(outWriter, errWriter, systemExitWhenFinished, customDefaultOptions, compilationProgress);
	}

	public void setDiagnosticListener( DiagnosticListener<? super JavaFileObject> diagnosticListener ) {
		this.diagnosticListener = diagnosticListener;
	}

	public DiagnosticListener<? super JavaFileObject> getDiagnosticListener( ) {
		return this.diagnosticListener;
	}

	@Override
	protected void initialize( PrintWriter outWriter, PrintWriter errWriter, boolean systemExit, Map<String, String> customDefaultOptions, CompilationProgress compilationProgress ) {
		super.initialize(outWriter, errWriter, systemExit, customDefaultOptions, compilationProgress);
		this.err = errWriter;
		// this.logger = new Logger(this, outWriter, errWriter);

//		this.proceed = true;
//		this.out = outWriter;
//		// this.err = errWriter;
//		this.systemExitWhenFinished = systemExit;
//		this.options = new CompilerOptions().getMap();
//		this.ignoreOptionalProblemsFromFolders = null;
//
//		this.progress = compilationProgress;
//		if (customDefaultOptions != null) {
//			// 指定源码版本
//			this.didSpecifySource = customDefaultOptions.get(CompilerOptions.OPTION_Source) != null;
//			// 指定目标版本
//			this.didSpecifyTarget = customDefaultOptions.get(CompilerOptions.OPTION_TargetPlatform) != null;
//			for (Iterator<Map.Entry<String, String>> iter = customDefaultOptions.entrySet().iterator(); iter.hasNext();) {
//				Map.Entry<String, String> entry = iter.next();
//				this.options.put(entry.getKey(), entry.getValue());
//			}
//		} else {
//			this.didSpecifySource = false;
//			this.didSpecifyTarget = false;
//		}
//		this.classNames = null;
	}



	@Override
	public boolean compile( String[] argv ) {
		return super.compile(argv);
	}

	// compile() call
	@Override
	public void configure( String[] argv ) {
		if ( ( argv == null ) || ( argv.length == 0 ) ) {
			// 打印用法
			printUsage();
			return;
		}
		fillExpandedCommandLine(argv);
		super.configure(argv);
	}

	private void fillExpandedCommandLine( String[] argv ) throws IllegalArgumentException {
		int index = -1;
		int argCount = argv.length;
		// expand the command line if necessary
		boolean needExpansion = false;
		loop: for ( int i = 0; i < argCount; i++ ) {
			if ( argv[i].startsWith("@") ) { //$NON-NLS-1$
				needExpansion = true;
				break loop;
			}
		}

		String[] newCommandLineArgs = null;
		if ( needExpansion ) {
			newCommandLineArgs = new String[argCount];
			index = 0;
			for ( int i = 0; i < argCount; i++ ) {
				String[] newArgs = null;
				String arg = argv[i].trim();
				if ( arg.startsWith("@") ) { //$NON-NLS-1$
					try {
						LineNumberReader reader = new LineNumberReader(new StringReader(new String(Util.getFileCharContent(new File(arg.substring(1)), null))));
						StringBuilder buffer = new StringBuilder();
						String line;
						while ( ( line = reader.readLine() ) != null ) {
							line = line.trim();
							if ( !line.startsWith("#") ) { //$NON-NLS-1$
								buffer.append(line).append(" "); //$NON-NLS-1$
							}
						}
						newArgs = tokenize(buffer.toString());
					}
					catch (IOException e) {
						throw new IllegalArgumentException(
							this.bind("configure.invalidexpansionargumentname", arg)); //$NON-NLS-1$
					}
				}
				if ( newArgs != null ) {
					int newCommandLineArgsLength = newCommandLineArgs.length;
					int newArgsLength = newArgs.length;
					System.arraycopy(newCommandLineArgs, 0, ( newCommandLineArgs = new String[newCommandLineArgsLength + newArgsLength - 1] ), 0, index);
					System.arraycopy(newArgs, 0, newCommandLineArgs, index, newArgsLength);
					index += newArgsLength;
				} else {
					newCommandLineArgs[index++] = arg;
				}
			}
			index = -1;
		} else {
			newCommandLineArgs = argv;
			for ( int i = 0; i < argCount; i++ ) {
				newCommandLineArgs[i] = newCommandLineArgs[i].trim();
			}
		}
		argCount = newCommandLineArgs.length;
		this.expandedCommandLine = newCommandLineArgs;
	}


	// compile() call
	@Override
	public void performCompilation( ) {
		super.performCompilation();

	}

	// performCompilation() call
	@Override
	protected void initializeAnnotationProcessorManager( ) {
		//super.initializeAnnotationProcessorManager();

		AbstractAnnotationProcessorManager annotationManager = new BatchAnnotationProcessorManager();
		annotationManager.configure(this, this.expandedCommandLine);
		annotationManager.setErr(this.err);
		annotationManager.setOut(this.out);
		this.batchCompiler.annotationProcessorManager = annotationManager;

	}

	@Override
	protected void setPaths( ArrayList<String> bootclasspaths, String sourcepathClasspathArg, ArrayList<String> sourcepathClasspaths, ArrayList<String> classpaths, String modulePath, String moduleSourcepath, ArrayList<String> extdirsClasspaths, ArrayList<String> endorsedDirClasspaths, String customEncoding ) {
		// super.setPaths(bootclasspaths, sourcepathClasspathArg, sourcepathClasspaths, classpaths, modulePath, moduleSourcepath, extdirsClasspaths, endorsedDirClasspaths, customEncoding);


		if ( this.complianceLevel == 0 ) {
			String version = this.options.get(CompilerOptions.OPTION_Compliance);
			this.complianceLevel = CompilerOptions.versionToJdkLevel(version);
		}
		// process bootclasspath, classpath and sourcepaths
		ArrayList<FileSystem.Classpath> allPaths = null;

		// 验证bootclasspaths参数
		long jdkLevel = validateClasspathOptions(bootclasspaths, endorsedDirClasspaths, extdirsClasspaths);

		// 安卓中releaseVersion为null
		if ( this.releaseVersion != null && this.complianceLevel < jdkLevel ) {
			// TODO: Revisit for access rules
			// allPaths = new ArrayList<FileSystem.Classpath>();
			// allPaths.add(
			// FileSystem.getOlderSystemRelease(this.javaHomeCache.getAbsolutePath(), this.releaseVersion, null));

			allPaths = new ArrayList<FileSystem.Classpath>();
			allPaths.add(
				FileSystem.getOlderSystemRelease(getJavaHome().getAbsolutePath(), this.releaseVersion, null));

		} else {
			allPaths = handleBootclasspath(bootclasspaths, customEncoding);
		}

		List<FileSystem.Classpath> cp = handleClasspath(classpaths, customEncoding);

		List<FileSystem.Classpath> mp = handleModulepath(modulePath);

		List<FileSystem.Classpath> msp = handleModuleSourcepath(moduleSourcepath);

		ArrayList<FileSystem.Classpath> sourcepaths = new ArrayList<>();
		if ( sourcepathClasspathArg != null ) {
			processPathEntries(DEFAULT_SIZE_CLASSPATH, sourcepaths,
							   sourcepathClasspathArg, customEncoding, true, false);
		}

		/*
		 * Feed endorsedDirClasspath according to:
		 * - -extdirs first if present;
		 * - else java.ext.dirs if defined;
		 * - else default extensions directory for the platform.
		 */
		List<FileSystem.Classpath> extdirs = handleExtdirs(extdirsClasspaths);

		List<FileSystem.Classpath> endorsed = handleEndorseddirs(endorsedDirClasspaths);

		/*
		 * Concatenate classpath entries
		 * We put the bootclasspath at the beginning of the classpath
		 * entries, followed by the extension libraries, followed by
		 * the sourcepath followed by the classpath.  All classpath
		 * entries are searched for both sources and binaries except
		 * the sourcepath entries which are searched for sources only.
		 */
		allPaths.addAll(0, endorsed);
		allPaths.addAll(extdirs);
		allPaths.addAll(sourcepaths);
		allPaths.addAll(cp);
		allPaths.addAll(mp);
		allPaths.addAll(msp);
		allPaths = FileSystem.ClasspathNormalizer.normalize(allPaths);
		this.checkedClasspaths = new FileSystem.Classpath[allPaths.size()];
		allPaths.toArray(this.checkedClasspaths);
		this.logger.logClasspath(this.checkedClasspaths);

		if ( this.annotationPaths != null 
			&& CompilerOptions.ENABLED.equals(this.options.get(CompilerOptions.OPTION_AnnotationBasedNullAnalysis)) ) {
			for ( FileSystem.Classpath c : this.checkedClasspaths ) {
				if ( c instanceof ClasspathJar )
				// ( (ClasspathJar) c ).annotationPaths = this.annotationPaths;
					ClasspathUtils.setAnnotationPaths((ClasspathJar) c, this.annotationPaths);

				else if ( c instanceof ClasspathJrt )
				// ( (ClasspathJrt) c ).annotationPaths = this.annotationPaths;
					ClasspathUtils.setAnnotationPaths((ClasspathJrt) c, this.annotationPaths);
			}
		}
	}

	@Override
	protected long validateClasspathOptions( ArrayList<String> bootclasspaths, ArrayList<String> endorsedDirClasspaths, ArrayList<String> extdirsClasspaths ) {

		// complianceLevel > 1.8[java8] 且设置了 -bootclasspath
		// 处理编译Java8以上不能设置 -bootclasspath的问题
		if ( this.complianceLevel > ClassFileConstants.JDK1_8 && 
			bootclasspaths != null && bootclasspaths.size() > 0 ) {

			long jdkLevel = CompilerOptions.versionToJdkLevel(CompilerOptions.getLatestVersion());
			return jdkLevel;
		}
		return super.validateClasspathOptions(bootclasspaths, endorsedDirClasspaths, extdirsClasspaths);
	}


	@Override
	public DefaultProblemFactory getProblemFactory( ) {
		return new DefaultProblemFactory();
	}
	@Override
	public ICompilerRequestor getBatchRequestor( ) {
		DiagnosticListener<? super JavaFileObject > diagnosticListener = getDiagnosticListener();
		if ( diagnosticListener == null ) {
			return super.getBatchRequestor();
		}
		return new EclipseCompilerRequestor(this, diagnosticListener, getDefaultProblemFactory());
	}

	public DefaultProblemFactory getDefaultProblemFactory( ) {
		return new DefaultProblemFactory(this.compilerLocale);
	}
} 
