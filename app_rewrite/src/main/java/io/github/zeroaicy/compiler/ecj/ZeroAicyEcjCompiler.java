package io.github.zeroaicy.compiler.ecj;


/**
 * java compiler from ecj
 */
public class ZeroAicyEcjCompiler {
	
	/*
	 java.lang.NullPointerException: Attempt to invoke virtual method 'int java.lang.String.length()' on a null object reference
	 at sun.nio.fs.UnixPath.normalizeAndCheck(UnixPath.java:77)
	 at sun.nio.fs.UnixPath.<init>(UnixPath.java:71)
	 at sun.nio.fs.UnixFileSystem.getPath(UnixFileSystem.java:281)
	 at io.zeroaicy.jrtfs2.SystemImage.open(SystemImage.java:37)
	 at io.zeroaicy.jrtfs2.JrtFileSystem.<init>(JrtFileSystem.java:77)
	 at io.zeroaicy.jrtfs2.JrtFileSystemProvider.getTheFileSystem(JrtFileSystemProvider.java:223)
	 at io.zeroaicy.jrtfs2.JrtFileSystemProvider.getFileSystem(JrtFileSystemProvider.java:238)
	 at org.eclipse.jdt.internal.compiler.tool.JrtFileSystem.initialize(JrtFileSystem.java:74)
	 at org.eclipse.jdt.internal.compiler.tool.JrtFileSystem.<init>(JrtFileSystem.java:58)
	 at org.eclipse.jdt.internal.compiler.tool.EclipseFileManager.initialize(EclipseFileManager.java:121)
	 at org.eclipse.jdt.internal.compiler.tool.EclipseFileManager.<init>(EclipseFileManager.java:103)
	 at org.eclipse.jdt.internal.compiler.apt.util.EclipseFileManager.<init>(EclipseFileManager.java:30)
	 at org.eclipse.jdt.internal.compiler.apt.dispatch.BatchProcessingEnvImpl.<init>(BatchProcessingEnvImpl.java:86)
	 at org.eclipse.jdt.internal.compiler.apt.dispatch.BatchAnnotationProcessorManager.configure(BatchAnnotationProcessorManager.java:79)
	 at org.eclipse.jdt.internal.compiler.batch.Main.initializeAnnotationProcessorManager(Main.java:4653)
	 at org.eclipse.jdt.internal.compiler.batch.Main.performCompilation(Main.java:4769)
	 at org.eclipse.jdt.internal.compiler.batch.Main.compile(Main.java:1802)
	 at CompileJavaxTools.main(CompileJavaxTools.java:115)
	 at java.lang.reflect.Method.invoke(Native Method)
	 at com.aide.ui.build.java.RunJavaActivity$a.run(SourceFile:1)
	 at java.lang.Thread.run(Thread.java:1012)
	*/
	EcjCompilerImpl compilerImpl;
	
	
	
}
