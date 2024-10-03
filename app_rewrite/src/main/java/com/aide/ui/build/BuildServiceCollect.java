//
// Decompiled by Jadx - 815ms
//
package com.aide.ui.build;

import abcd.cy;
import abcd.dy;
import abcd.fy;
import abcd.gy;
import abcd.iy;
import com.aide.ui.build.android.AndroidProjectBuildService;
import com.aide.ui.build.java.JavaProjectBuildService;
import com.aide.ui.build.javascript.JavaScriptBuildService;
import com.aide.ui.build.nativeexecutable.NativeProjectBuildService;
import com.aide.ui.htmluidesigner.HtmlCodeBuildService;

@cy(clazz = 640679041625343920L, container = 640679041625343920L, user = true)
public class BuildServiceCollect {

    @fy
    private static boolean VH;
    @gy
    private static boolean gn;
	
	
    @dy(field = 3399072473542906440L)
    public static AndroidProjectBuildService androidProjectBuildService;

    @dy(field = 2197916291524773644L)
    public static IBuildService[] buildServices;


    @dy(field = 2203712763761595957L)
    public static HtmlCodeBuildService htmlCodeBuildService;

    @dy(field = 812460999067921955L)
    public static JavaProjectBuildService javaProjectBuildService;

    @dy(field = -770050735975788136L)
    public static JavaScriptBuildService javaScriptBuildService;

    @dy(field = -5401337091685781131L)
    public static NativeProjectBuildService nativeProjectBuildService;

    static {
        try {
            iy.Zo(BuildServiceCollect.class);
            if (VH) {
                iy.gn(-381416819618194767L, (Object) null);
            }
            androidProjectBuildService = new AndroidProjectBuildService();
            javaProjectBuildService = new JavaProjectBuildService();
            nativeProjectBuildService = new NativeProjectBuildService();
            htmlCodeBuildService = new HtmlCodeBuildService();
            javaScriptBuildService = new JavaScriptBuildService();
            buildServices = new IBuildService[]{androidProjectBuildService, javaProjectBuildService, nativeProjectBuildService, htmlCodeBuildService, new JavaScriptBuildService()};
        } catch (Error th) {
            throw th;			
		}
		catch (Throwable th) {
			throw new Error(th);
        }
    }
    public BuildServiceCollect() {}
}
