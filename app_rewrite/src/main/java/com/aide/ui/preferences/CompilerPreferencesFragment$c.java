package com.aide.ui.preferences;

import abcd.cy;
import abcd.ey;
import abcd.fy;
import abcd.gy;
import abcd.iy;
import android.preference.Preference;
import com.aide.ui.App;
import com.aide.ui.browsers.ErrorBrowser;
import android.app.AlertDialog;
import com.aide.ui.rewrite.R;
import android.content.DialogInterface;
import com.aide.ui.services.MavenService;

/**
 * 增量maven仓库刷新，二级确认弹窗
 */
class CompilerPreferencesFragment$c implements Preference.OnPreferenceClickListener {
    public CompilerPreferencesFragment$c(CompilerPreferencesFragment compilerPreferencesFragment) {}
	// refresh_maven_repository
    @Override
    public boolean onPreferenceClick(Preference preference) {
        try {
			AlertDialog.Builder builder = new AlertDialog.Builder(preference.getContext());
            builder.setTitle(R.string.refresh_maven_repository_tips);
            builder.setMessage(R.string.refresh_maven_repository_tips_message);
            builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// nw() -> refreshMavenCache
						App.getMavenService().nw();
					}
				});
            builder.setNegativeButton(android.R.string.cancel, (DialogInterface.OnClickListener) null);
            builder.show();
            return true;
        } catch (Throwable th) {
            throw new Error(th);
        }
    }
}

