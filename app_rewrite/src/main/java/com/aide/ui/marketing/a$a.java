//
// Decompiled by Jadx - 764ms
//
package com.aide.ui.marketing;

import android.content.Context;
import android.text.TextUtils;
import com.aide.common.AppLog;
import com.aide.ui.ServiceContainer;
import io.github.zeroaicy.util.ContextUtil;
import io.github.zeroaicy.util.IOUtils;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

public class a$a {
    public String DW;
    public String FH;
    public String Hw;
    public int j6;
    public String v5;


	//更新提示，
    public a$a(int ImageIconId, String appFlag, String title, String str3, String str4) {
		this.j6 = ImageIconId;

		//跟随App.P8，表示是aide还是web等
		repair(this, appFlag);

		this.FH = title;
		this.Hw = str3;
		this.v5 = str4;
    }
	/**
	 * 修复更新弹窗
	 */
	private static void repair(a$a info, String appFlag) {
		if (appFlag.equals(ServiceContainer.P8)) {
			//更新为当前包名
			info.DW = ContextUtil.getPackageName();
			AppLog.d(appFlag, info.DW);
		} else {
			info.DW = appFlag;
		}
	}
    private List<String> FH(Context context, String str) {
		try {
			InputStream input = context.getAssets().open(str);
			List<String> lines = IOUtils.readLines(input);
			return lines;
		}
		catch (IOException e) {
			return Collections.emptyList();
		}
    }

    public String DW(Context context) {
		return TextUtils.join("\n", j6(context));
    }

    public List<String> j6(Context context) {
        return FH(context, this.v5);
    }
}

