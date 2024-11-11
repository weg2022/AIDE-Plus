package io.github.zeroaicy.compiler.ecj.util;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;
import javax.tools.StandardLocation;
import org.eclipse.jdt.internal.compiler.batch.FileSystem;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileConstants;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jdt.internal.compiler.tool.JrtFileSystem;

/**
 * Implementation of the Standard Java File Manager
 */
public class EclipseFileManager extends org.eclipse.jdt.internal.compiler.tool.EclipseFileManager {
	
	
	JrtFileSystem jrtSystem;
	File jrtHome;
	boolean isOnJvm9 = false;
	
	public final Charset charset;
	public EclipseFileManager( Locale locale, Charset charset ) {
		super(locale, charset);
		this.charset = charset;
	}

	public Charset getCharset( ) {
		return this.charset;
	}

	protected void initialize( File javahome ) throws IOException {
		this.setLocation(StandardLocation.PLATFORM_CLASS_PATH, getDefaultBootclasspath());

		Iterable<? extends File> defaultClasspath = getDefaultClasspath();
		this.setLocation(StandardLocation.CLASS_PATH, defaultClasspath);
		// No annotation module path by default
		this.setLocation(StandardLocation.ANNOTATION_PROCESSOR_PATH, defaultClasspath);
	}
	Iterable<? extends File> getDefaultBootclasspath( ) {
		List<File> files = new ArrayList<>();
		String javaversion = System.getProperty("java.version");//$NON-NLS-1$
		if ( javaversion.length() > 3 )
			javaversion = javaversion.substring(0, 3);
		long jdkLevel = CompilerOptions.versionToJdkLevel(javaversion);
		if ( jdkLevel < ClassFileConstants.JDK1_6 ) {
			// wrong jdk - 1.6 or above is required
			return null;
		}

		for ( FileSystem.Classpath classpath : org.eclipse.jdt.internal.compiler.util.Util.collectFilesNames() ) {
			files.add(new File(classpath.getPath()));
		}
		return files;
	}
	Iterable<? extends File> getDefaultClasspath( ) {
		// default classpath
		ArrayList<File> files = new ArrayList<>();
		String classProp = System.getProperty("java.class.path"); //$NON-NLS-1$
		if ( ( classProp == null ) || ( classProp.length() == 0 ) ) {
			return null;
		} else {
			StringTokenizer tokenizer = new StringTokenizer(classProp, File.pathSeparator);
			String token;
			while ( tokenizer.hasMoreTokens() ) {
				token = tokenizer.nextToken();
				File file = new File(token);
				if ( file.exists() ) {
					files.add(file);
				}
			}
		}
		return files;
	}
	@Override
	public Iterable<? extends File> getLocation( Location location ) {
		/* XXX there are strange differences regarding module name handling with super class
		 if (location instanceof LocationWrapper) {
		 return getFiles(((LocationWrapper) location).getPaths());
		 }
		 LocationWrapper loc = this.locationHandler.getLocation(location, ""); //$NON-NLS-1$
		 if (loc == null) {
		 return null;
		 }
		 return getFiles(loc.getPaths());
		 */
		return super.getLocation(location);
	}

	@Override
	public boolean hasLocation( Location location ) {
		/* XXX there are strange differences regarding module name handling with super class
		 try {
		 return getLocationForModule(location, "") != null; //$NON-NLS-1$
		 } catch (IOException e) {
		 // nothing to do
		 }
		 return false;
		 */
		return super.hasLocation(location);
	}

	@Override
	public void setLocation( Location location, Iterable<? extends File> files ) throws IOException {
		/* XXX there are strange differences regarding module name handling with super class
		 if (location.isOutputLocation() && files != null) {
		 // output location
		 int count = 0;
		 for (Iterator<? extends File> iterator = files.iterator(); iterator.hasNext(); ) {
		 iterator.next();
		 count++;
		 }
		 if (count != 1) {
		 throw new IllegalArgumentException("output location can only have one path");//$NON-NLS-1$
		 }
		 }
		 this.locationHandler.setLocation(location, "", getPaths(files)); //$NON-NLS-1$
		 */
		super.setLocation(location, files);
	}

	@Override
	public void setLocationForModule( Location location, String moduleName, Collection<? extends Path> paths ) throws IOException {
		/* XXX there are strange differences regarding module name handling with super class
		 validateModuleLocation(location, moduleName);
		 this.locationHandler.setLocation(location, moduleName, paths);
		 if (location == StandardLocation.MODULE_SOURCE_PATH) {
		 LocationWrapper wrapper = this.locationHandler.getLocation(StandardLocation.CLASS_OUTPUT, moduleName);
		 if (wrapper == null) {
		 wrapper = this.locationHandler.getLocation(StandardLocation.CLASS_OUTPUT, ""); //$NON-NLS-1$
		 if (wrapper != null) {
		 Iterator<? extends Path> iterator = wrapper.getPaths().iterator();
		 if (iterator.hasNext()) {
		 // Per module output location is always a singleton list
		 Path path = iterator.next().resolve(moduleName);
		 this.locationHandler.setLocation(StandardLocation.CLASS_OUTPUT, moduleName, Collections.singletonList(path));
		 }
		 }
		 }
		 }
		 */
		super.setLocationForModule(location, moduleName, paths);
	}
}
