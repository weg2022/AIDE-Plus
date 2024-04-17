package io.github.zeroaicy.aide.shizuku;
import rikka.shizuku.ShizukuProvider;
import android.content.Context;
import android.content.pm.ProviderInfo;

public class ZeroAicyShizukuProvider extends ShizukuProvider {
	public ZeroAicyShizukuProvider(){
		ZeroAicyShizukuProvider.enableMultiProcessSupport(true);
	}
	@Override
	public void attachInfo(Context context, ProviderInfo info) {
		super.attachInfo(context, info);
		//初始化Shizuku库
		ShizukuUtil.initialized(context);

	}


}
