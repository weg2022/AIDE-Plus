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
import abcd.k4;

@cy(clazz = -146370945839940960L, container = 2006115082471780797L, user = true)
class CodeAnalysisEngineService$a extends k4 {
    @gy
    private static boolean VH;
    @fy
    private static boolean Zo;
    @dy(field = -2242509117242277440L)
    @hy
    final CodeAnalysisEngineService v5;

    static {
		iy.Zo(CodeAnalysisEngineService$a.class);
    }

    @ey(method = 3587681936204591544L)
   public CodeAnalysisEngineService$a(CodeAnalysisEngineService codeAnalysisEngineService) {
        this.v5 = codeAnalysisEngineService;
    }

    @ey(method = 555879083465091925L)
    public InputStream FH(String str) {
		
        try {
            if (Zo) {
                iy.tp(3462692199028310193L, this, str);
            }
			try {
				AssetManager assets = this.v5.getAssets();
				InputStream open = assets.open(str);
				
				if ("adrt/ADRT.class".equals(str)) {
					open = ClassReader.modifyADRT(open);
				}
				return open;
			} catch (IOException e) {
				Log.println(e);
				throw e;
			}
			
        } catch (Throwable th) {
            if (VH) {
                iy.j3(th, 3462692199028310193L, this, str);
            }
            throw new RuntimeException(th);
        }
    }

    @ey(method = -6315075546956300225L)
    public String j6() {
        try {
            if (Zo) {
                iy.gn(731669566573082785L, this);
            }
            return this.v5.getPackageName();
        } catch (Throwable th) {
            if (VH) {
                iy.aM(th, 731669566573082785L, this);
            }
            throw new RuntimeException(th);
        }
    }
}

