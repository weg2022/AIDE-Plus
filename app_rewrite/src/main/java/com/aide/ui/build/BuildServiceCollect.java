//
// Decompiled by Jadx - 815ms
//
package com.aide.ui.build;

import com.aide.ui.build.BuildServiceCollect;
import com.aide.ui.build.android.AndroidProjectBuildService;
import com.aide.ui.build.java.JavaProjectBuildService;
import com.aide.ui.build.javascript.JavaScriptBuildService;
import com.aide.ui.build.nativeexecutable.NativeProjectBuildService;
import com.aide.ui.htmluidesigner.HtmlCodeBuildService;
import com.google.android.gms.internal.ads.cy;
import com.google.android.gms.internal.ads.iy;
import com.probelytics.annotation.ExceptionEnabled;
import com.probelytics.annotation.FieldMark;
import com.probelytics.annotation.ParametersEnabled;
import com.probelytics.annotation.TypeMark;

@TypeMark(clazz = 640679041625343920L, container = 640679041625343920L, user = true)
public class BuildServiceCollect {

    @ParametersEnabled
    private static boolean VH;
    @ExceptionEnabled
    private static boolean gn;
	
	
    @FieldMark(field = 3399072473542906440L)
    public static AndroidProjectBuildService androidProjectBuildService;

    @FieldMark(field = 2197916291524773644L)
    public static IBuildService[] buildServices;


    @FieldMark(field = 2203712763761595957L)
    public static HtmlCodeBuildService htmlCodeBuildService;

    @FieldMark(field = 812460999067921955L)
    public static JavaProjectBuildService javaProjectBuildService;

    @FieldMark(field = -770050735975788136L)
    public static JavaScriptBuildService javaScriptBuildService;

    @FieldMark(field = -5401337091685781131L)
    public static NativeProjectBuildService nativeProjectBuildService;

    static {
        try {
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
