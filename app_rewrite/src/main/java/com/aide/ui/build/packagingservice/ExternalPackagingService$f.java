package com.aide.ui.build.packagingservice;

//
// Decompiled by Jadx - 534ms
//

import android.text.TextUtils;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipOutputStream;
import java.util.List;
import java.util.HashMap;
import java.util.Set;
import java.io.ByteArrayOutputStream;
import android.content.pm.PackageInfo;
import io.github.zeroaicy.util.Log;

//用于可以添加dex zip包
public class ExternalPackagingService$f extends ZipOutputStream{

	public HashSet<String> WB;
	public ExternalPackagingService$f(OutputStream outputStream){
		super(outputStream);
		this.WB = new HashSet<>();
	}

	//查询是否已添加
	public boolean j6(String str){
		return this.WB.contains(str);
	}

	@Override
	public void putNextEntry(ZipEntry zipEntry) throws IOException{
		//dex count重定向
		zipEntry = processClassesCount(zipEntry);

		String zipEntryName = zipEntry.getName();

		if ( !this.WB.contains(zipEntryName) ){

			if ( "resources.arsc".equals(zipEntryName) ){
				Log.d("resources.arsc是否未压缩", zipEntry.getMethod() ==  ZipOutputStream.STORED);
			}
			/*
			//AIDE设置了跟随ap_文件
			if ( "resources.arsc".equals(zipEntryName) ){
				//android:targetSdkVersion 30
				//resources.arsc不能被压缩
				zipEntry.setMethod(ZipOutputStream.STORED);
			}
			*/
			
			if ( zipEntryName.startsWith("/lib/") && zipEntryName.endsWith(".so") ){
				//android:targetSdkVersion 30
				//android:extractNativeLibs时不能被压缩
				zipEntry.setMethod(ZipOutputStream.STORED);
			}


			this.WB.add(zipEntryName);
			super.putNextEntry(zipEntry);
		}else if ( !zipEntry.isDirectory() ){
			throw new ZipException("Entry already exists: " + zipEntry.getName());
		}
	}

	//依次更新
	protected int classesCountDex = 1;
	protected ZipEntry processClassesCount(ZipEntry zipEntry){
		String zipEntryFileName = zipEntry.getName();

		//重命名根目录下以classes开头的dex文件
		if ( !zipEntry.isDirectory() 
			&& !zipEntryFileName.contains("/") 
			&& zipEntryFileName.startsWith("classes") 
			&& zipEntryFileName.endsWith(".dex") ){

			String oldCount = zipEntryFileName.substring("classes".length(), zipEntryFileName.length() - ".dex".length());

			//标记是否需要添加
			boolean isFilterZipEntryName = false;
			try{
				if ( !TextUtils.isEmpty(oldCount) ){
					//classes.dex会被重命名，依此判断是否已添加过
					//负值classes.dex 一般在_resource.jar中出现
					isFilterZipEntryName = Integer.valueOf(oldCount) < 0;
				}
			}
			catch (NumberFormatException e){
				isFilterZipEntryName = true;
			}
			String dexEntryName = classesCountDex > 1 ? String.format("classes%d.dex", classesCountDex) : "classes.dex";

			while ( this.j6(dexEntryName) && classesCountDex < this.WB.size() + 1 ){
				classesCountDex++;
				dexEntryName = String.format("classes%d.dex", classesCountDex);
			}
			zipEntry = new ZipEntry(dexEntryName);

			if ( isFilterZipEntryName ){
				//资源内classesDex为负值序列
				this.WB.add(zipEntryFileName);
			}
		}
		return zipEntry;
	}
}

