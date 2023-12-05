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

public class a$a {
    public String DW;
    public String FH;
    public String Hw;
    public int j6;
    public String v5;

    public a$a(int ImageIconId, String packageName, String title, String str3, String str4) {
		this.j6 = ImageIconId;
		this.DW = App.P8;
		
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

