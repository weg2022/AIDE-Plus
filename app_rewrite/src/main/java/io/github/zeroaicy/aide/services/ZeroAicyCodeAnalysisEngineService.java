package io.github.zeroaicy.aide.services;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.widget.Toast;
import com.aide.engine.service.CodeAnalysisEngineService;
import com.aide.ui_zeroaicy.R;
import io.github.zeroaicy.util.Log;
import android.content.IntentFilter;
import android.os.Bundle;
import moe.shizuku.api.BinderContainer;

public class ZeroAicyCodeAnalysisEngineService extends CodeAnalysisEngineService{

	private NotificationChannel notificationChannel;
	private static int id = 0x1010;
	private NotificationManager notificationManager;

	private Notification notification;
	
	
	//*
	@Override
	public void onCreate(){
		super.onCreate();
		Log.d("ZeroAicyCodeAnalysisEngineService", "onCreate");
		//setNotificationAndForeground();
		
		
		/*
		 Bundle bundle = new Bundle();
		 bundle.putParcelable("io.github.zeroaicy.aide.shizuku.ZeroAicyServer", new BinderContainer(new 
		 Intent intent = new Intent("io.github.zeroaicy.aide.shizuku.ZeroAicyServer");
		 intent.putExtras(bundle);
		 sendBroadcast(intent);

		 IntentFilter intentFilter = new IntentFilter("io.github.zeroaicy.aide.shizuku.ZeroAicyServer");
		 registerReceiver(new BroadcastReceiver(){

		 @Override
		 public void onReceive(Context context, Intent intent){

		 }
		 }, intentFilter))
		 */
	}
	
	
	@Override
	public IBinder onBind(Intent intent){
		return super.onBind(intent);
	}

	@Override
	public void onDestroy(){
		super.onDestroy();
		if ( notificationManager == null ){
			notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		}
		if ( notification != null ){
			notificationManager.cancel(id);
		}
		Toast.makeText(this, "代码分析器已关闭", 1).show();
		
		android.os.Process.killProcess(android.os.Process.myPid());
		System.exit(0);
		System.exit(-1000);
		System.exit(-2000);
		
	}

	private void setNotificationAndForeground(){
        //判断当前版本是否支持
        if ( Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ){
            String CHANNEL_ID = "CodeAnalysisService";

            notificationChannel = new NotificationChannel(CHANNEL_ID, "主服务", NotificationManager.IMPORTANCE_HIGH);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC); //设置锁屏可见 VISIBILITY_PUBLIC=可见
            //notificationChannel.enableLights(true);//设置提示灯
            //notificationChannel.setLightColor(Color.RED);//设置提示灯颜色
            //notificationChannel.setShowBadge(true);//显示logo
            //notificationChannel.setDescription("MediaService notification");//设置描述

            notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);

			notification = new Notification.Builder(this, CHANNEL_ID)
				.setAutoCancel(false)
				.setContentTitle("CodeAnalysisService")//标题
				//.setContentText("运行中……")//内容
				.setSmallIcon(R.drawable.ic_launcher)//不设置小图标通知不会显示，或将报错
				//.setLargeIcon(R.drawable.playing_dark_style)
				.build();
            startForeground(id, notification);//startForeground服务前台化，要在5秒内调用成功，否则前台化失败
        }
    }
}
