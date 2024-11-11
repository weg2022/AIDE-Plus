package org.eclipse.jdt.internal.compiler.batch;
import java.util.List;

public class ClasspathUtils
{
	public static void setAnnotationPaths( ClasspathJar classpathJar, List<String> annotationPaths){
		classpathJar.annotationPaths = annotationPaths;
	}
	public static void setAnnotationPaths( ClasspathJrt classpathJrt, List<String> annotationPaths){
		classpathJrt.annotationPaths = annotationPaths;
	}
	
}
