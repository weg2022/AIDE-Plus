package io.github.zeroaicy.aide.ui.preferences;


import abcd.cy;
import abcd.ey;
import abcd.fy;
import abcd.gy;
import abcd.iy;
import android.app.Activity;
import android.content.Context;
import android.preference.CheckBoxPreference;
import android.util.AttributeSet;
import com.aide.ui.ServiceContainer;

@cy(clazz = 5327021182957118005L, container = 5327021182957118005L, user = true)
public class PremiumCheckBoxPreference extends CheckBoxPreference {
    public PremiumCheckBoxPreference(Context context) {
        super(context);
    }

    @Override
    protected void onClick() {
		if (ServiceContainer.getLicenseService().gn((Activity) getContext(), getKey())) {
			super.onClick();
		}
    }

    public PremiumCheckBoxPreference(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public PremiumCheckBoxPreference(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }
}

