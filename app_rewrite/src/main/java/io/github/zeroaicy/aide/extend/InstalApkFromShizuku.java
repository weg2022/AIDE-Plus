package io.github.zeroaicy.aide.extend;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import io.github.zeroaicy.aide.shizuku.ShizukuUtil;
import com.aide.ui.App;
import android.widget.Toast;
import com.aide.ui.project.AndroidProjectSupport;

public class InstalApkFromShizuku implements Runnable {

	private String appPath;

	public InstalApkFromShizuku(String appPath) {
		this.appPath = appPath;
	}

	@Override
	public void run() {
		//在主线程中
		final String instalApkError = ShizukuUtil.instalApk(appPath);
		Context context = App.getContext();
		if (TextUtils.isEmpty(instalApkError)) {
			//成功安装，启动应用
			String gW = App.getProjectService().getCurrentAppHome();
			String packageName = AndroidProjectSupport.kQ(gW, App.getProjectService().getBuildVariant());
			PackageManager packageManager = context.getPackageManager();
			Intent launchIntentForPackage = packageManager.getLaunchIntentForPackage(packageName);

			if (launchIntentForPackage != null) {
				context.startActivity(launchIntentForPackage);
			}
			else {
				com.aide.common.MessageBox.BT(App.getMainActivity(), "运行错误", "应用程序已成功安装，但找不到主活动");					
			}
		}
		else {
			
			//安装失败
			com.aide.common.MessageBox.BT(App.getMainActivity(), "安装失败", instalApkError);
		}
	}


}
