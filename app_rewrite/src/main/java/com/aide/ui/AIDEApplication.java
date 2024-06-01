package com.aide.ui;

//
// Decompiled by Jadx - 508ms
//

import androidx.multidex.MultiDexApplication;
import com.aide.ui.marketing.b;
import com.aide.ui.rewrite.R;
import io.github.zeroaicy.util.ContextUtil;

public class AIDEApplication extends MultiDexApplication{
	public void onCreate() {
        super.onCreate();
		// 共存版关键
		ServiceContainer.lp("com.aide.ui");
		
		if (ContextUtil.isMainProcess()) {
			if (ServiceContainer.P8.equals("com.aide.ui")) {
				b.j6(this, 0xa9bd, R.drawable.ic_launcher, "New version of AIDE installed", "Check out what's new!", MainActivity.Mz(this));
			} else if (ServiceContainer.P8.equals("com.aide.web")) {
				b.j6(this, 0xa9bd, R.drawable.ic_launcher_web, "New version of AIDE Web installed", "Check out what's new!", MainActivity.Mz(this));
			}
		}
    }
}

