package com.aide.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import com.aide.ui.rewrite.R;
import io.github.zeroaicy.aide.preference.ZeroAicySetting;
import android.os.Build;
import android.view.View;
import androidx.core.view.WindowCompat;
import android.view.Window;
import androidx.core.view.WindowInsetsControllerCompat;

public class ThemedActionbarActivity extends Activity {


    public static void onCreate2( ThemedActionbarActivity ThemedActionbarActivity, Bundle bundle ) {
		ThemedActionbarActivity.onCreate2(bundle);
	}

    protected void onCreate2( Bundle bundle ) {

		enableFollowSystem(false);
		super.onCreate(bundle);
		boolean isLightTheme = ZeroAicySetting.isLightTheme();
		if ( isLightTheme ) {
			setTheme(R.style.ActivityActionbarThemeLight);
		} else {
			setTheme(R.style.ActivityActionbarThemeDark);
		}
		Window window = getWindow();
		//window.setStatusBarColor(colorScheme.background.toArgb());
		WindowInsetsControllerCompat insetsController = WindowCompat.getInsetsController(window, window.getDecorView());
		insetsController.setAppearanceLightStatusBars(isLightTheme);
		
    }

    @Override
    protected void onCreate( Bundle bundle ) {

		enableFollowSystem(false);

		super.onCreate(bundle);

		if ( ZeroAicySetting.isLightTheme() ) {
			setTheme(R.style.ActivityActionbarThemeLight);
		} else {
			setTheme(R.style.ActivityActionbarThemeDark);
		}
    }

	@Override
	protected void onResume( ) {
		super.onResume();
		enableFollowSystem(true);
	}

	@Override
	public void onConfigurationChanged( Configuration configuration ) {
		super.onConfigurationChanged(configuration);
		enableFollowSystem(true);
	}

	private void enableFollowSystem( boolean recreate ) {

		if ( ZeroAicySetting.enableFollowSystem() ) {

			if ( ZeroAicySetting.isNightMode(this) ) {
				if ( ZeroAicySetting.isLightTheme() ) {
					//修改主题为暗主题
					ZeroAicySetting.setLightTheme(false);
					if ( recreate ) recreate();
				}
			} else {
				if ( !ZeroAicySetting.isLightTheme() ) {
					//修改主题为亮主题
					ZeroAicySetting.setLightTheme(true);
					if ( recreate ) recreate();
				}
			}
		}
	}
}

