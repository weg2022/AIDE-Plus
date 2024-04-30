//
// Decompiled by Jadx - 850ms
//
package com.aide.engine.service;

import abcd.cy;
import abcd.dy;
import abcd.ey;
import abcd.fy;
import abcd.gy;
import abcd.hy;
import abcd.iy;
import abcd.th;
import android.content.res.AssetManager;
import com.aide.engine.service.CodeAnalysisEngineService$a;
import io.github.zeroaicy.aide.ClassReader;
import io.github.zeroaicy.util.Log;
import java.io.IOException;
import java.io.InputStream;
import com.aide.codemodel.api.abstraction.DebuggerResourceProvider;

public class CodeAnalysisEngineService$a extends DebuggerResourceProvider{
    final CodeAnalysisEngineService codeAnalysisEngineService;
	
	public CodeAnalysisEngineService$a(CodeAnalysisEngineService codeAnalysisEngineService){
        this.codeAnalysisEngineService = codeAnalysisEngineService;
    }
	
	/**
	 * 拦截调试器，动态修改调试器宿主包名
	 */
	@Override
    public InputStream getResourceInputStream(String fileName){
        try{
			AssetManager assets = this.codeAnalysisEngineService.getAssets();
			InputStream open = assets.open(fileName);

			if ( "adrt/ADRT.class".equals(fileName) ){
				open = ClassReader.modifyADRT(open);
			}
			return open;
		}
		catch (IOException e){
			Log.println(e);
			throw new Error(e);
		}
    }
	
	/**
	 * 调试器注入包名
	 */
	@Override
    public String getHostPackageName(){
		return this.codeAnalysisEngineService.getPackageName();
    }
}

