package com.aide.ui.preferences;



/**
 * 增量maven仓库刷新，二级确认弹窗
 */
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.preference.Preference;
import com.aide.ui.ServiceContainer;
import com.aide.ui.rewrite.R;

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
						ServiceContainer.getMavenService().refresh();
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

