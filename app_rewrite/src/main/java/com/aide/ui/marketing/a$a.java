//
// Decompiled by Jadx - 764ms
//
package com.aide.ui.marketing;

import android.content.Context;
import android.text.TextUtils;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import com.aide.ui.App;
import io.github.zeroaicy.util.*;

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
		if( appFlag.equals(App.P8)){
			//更新为当前包名
			this.DW = ContextUtil.getPackageName();
			Log.d(appFlag, DW);
		}else{
			this.DW = appFlag;
		}
		
		this.FH = title;
		this.Hw = str3;
		this.v5 = str4;
    }
    private List<String> FH(Context context, String str) {
		ArrayList<String> arrayList = new ArrayList<>();
		try {
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(context.getAssets().open(str)));
			while (true) {
				String readLine = bufferedReader.readLine();
				if (readLine == null) {
					break;
				}
				arrayList.add(readLine);
			}
			bufferedReader.close();
		} catch (IOException unused) {
			
		}
		return arrayList;
    }

    public String DW(Context context) {
		return TextUtils.join("\n", j6(context));
    }
    public List<String> j6(Context context) {
        return FH(context, this.v5);
    }
}

