package io.github.zeroaicy.aide.services;

import com.android.tools.r8.ArchiveClassFileProvider;
import com.android.tools.r8.D8;
import com.android.tools.r8.D8Command;
import com.android.tools.r8.OutputMode;
import com.android.tools.r8.origin.Origin;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class D8Wrapper{
	public void dexing(String inputJarFilePath, int minSdk, boolean file_per_class_file, boolean intermediate, String user_androidjar, List<String> dependencyLibs, String outPath) throws Exception{
		OutputMode outputMode;
		if ( file_per_class_file ){
			outputMode = OutputMode.DexFilePerClassFile;
		}
		else{
			outputMode = OutputMode.DexIndexed;
		}

		D8Command.Builder builder = D8Command.builder();
		builder.setMinApiLevel(minSdk)
			.setIntermediate(intermediate)
			.addLibraryFiles(Paths.get(user_androidjar))
			.setOutput(Paths.get(outPath), outputMode);

		if ( dependencyLibs != null ){
			for ( String librarie : dependencyLibs ){
				builder.addClasspathResourceProvider(new ArchiveClassFileProvider(Paths.get(librarie)));
			}
		}
		Set<String> classNameSet = new HashSet<>();
		ZipInputStream zipFileInput = null;

		try{
			zipFileInput = new ZipInputStream(new FileInputStream(inputJarFilePath));
			ZipEntry originalZipEntry;
			while ( (originalZipEntry = zipFileInput.getNextEntry()) != null ){
				String name = originalZipEntry.getName();
				if( !name.endsWith(".dex")
				   || !name.endsWith(".class")){
					continue;
				}
				if( name.endsWith("module-info.class") && name.startsWith("META-INF/")){
					//META-INF/.*module-info.class
					continue;
				}
				if ( classNameSet.contains(name) ){
					continue;
				}
				if ( originalZipEntry.isDirectory() ){
					//文件夹不用加
					continue;
				}
				classNameSet.add(name);
				ByteArrayOutputStream byteArrayOutput = new ByteArrayOutputStream();
				streamTransfer(zipFileInput, byteArrayOutput);
				byte[] data = byteArrayOutput.toByteArray();
				byteArrayOutput.close();

				if ( name.endsWith(".dex") ){
					builder.addDexProgramData(data, Origin.root());
				}else if(name.endsWith(".class")){
					builder.addClassProgramData(data, Origin.root());
				}
			}
		}
		finally{
			if ( zipFileInput != null ) 
				zipFileInput.close();
		}
		D8.run(builder.build());
	}

	private void streamTransfer(ZipInputStream zipFileInput, ByteArrayOutputStream byteArrayOutput){
		// TODO: Implement this method
	}
}
