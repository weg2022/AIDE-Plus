package io.github.zeroaicy.aide.preference;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import com.aide.ui.rewrite.R;

public class ZeroAicySettingsFragment extends PreferenceFragment {

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		//添加ZeroAicy扩展设置
        addPreferencesFromResource(R.xml.preferences_setting_zeroaicy);
        
    }
}
