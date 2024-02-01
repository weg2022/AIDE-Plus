package com.aide.ui;

//
// Decompiled by Jadx - 508ms
//

import abcd.cy;
import abcd.ey;
import abcd.fy;
import abcd.gy;
import abcd.iy;
import android.app.ActivityManager;
import android.os.Process;
import android.support.multidex.MultiDexApplication;
import com.aide.ui.marketing.b;
import com.aide.ui.rewrite.R;
import io.github.zeroaicy.util.ContextUtil;

public class AIDEApplication extends MultiDexApplication{
	public void onCreate() {
        super.onCreate();
		App.lp("com.aide.ui");
		if (ContextUtil.isMainProcess()) {
			if (App.P8.equals("com.aide.ui")) {
				b.j6(this, 0xa9bd, R.drawable.ic_launcher, "New version of AIDE installed", "Check out what's new!", MainActivity.Mz(this));
			} else if (App.P8.equals("com.aide.web")) {
				b.j6(this, 0xa9bd, R.drawable.ic_launcher_web, "New version of AIDE Web installed", "Check out what's new!", MainActivity.Mz(this));
			}
		}
    }
}

