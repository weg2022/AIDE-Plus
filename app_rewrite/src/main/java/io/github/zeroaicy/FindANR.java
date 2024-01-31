package io.github.zeroaicy;

import android.os.*;

import java.util.Map;
import io.github.zeroaicy.util.Log;

public class FindANR{

	public static void setStop(boolean stop){
		FindANR.stop = stop;
	}

	public static boolean isStop(){
		return stop;
	}

	private static Handler startCheckThread(){
		
		HandlerThread mHandlerThread = new HandlerThread("Check ANR");
		mHandlerThread.start();


		final Handler mCheckAnrHandler = new Handler(mHandlerThread.getLooper()){
			@Override
			public void handleMessage(Message msg){
				switch ( msg.what ){
					case MAIN_THREAD_MESSAGE:
						//优先FIND_ANR触发，移除
						removeMessages(FIND_ANR);
						//重置FIND_ANR时间，一旦主进程阻塞
						//则无法及时移除并重置FIND_ANR
						sendEmptyMessageDelayed(FIND_ANR, 5500);
						break;
					case FIND_ANR:
						//接收到FIND_ANR消息
						Log.println("找到ANR");

						for ( Map.Entry<Thread, StackTraceElement[]> entry: Thread.getAllStackTraces().entrySet() ){
							Thread key = entry.getKey();
							String name = key.getName();
							if ( name.startsWith("binder") || name.equals("main") ){
								Log.println();
								Log.println("线程名称: " + name);
								StackTraceElement[] value = entry.getValue();
								if(value == null || value.length == 0){
									Log.println("线程堆栈:为空");
									continue;
								}
								
								Log.println("new 堆栈");
								for (StackTraceElement stack : key.getStackTrace()) {
									Log.println(stack.toString());
								}
								Log.println();
							}
						}
						//FindANR.setStop(true);
						break;

				}

			}
		};
		return mCheckAnrHandler;
	}

	static final int MAIN_THREAD_MESSAGE = 0x10;
	static final int FIND_ANR = 0x20;

	private static boolean running = false;

	private static boolean stop = false;


	public static void startSendMsg(){

		if ( running ){ 
			return;
		}
		running = true;
		
		
		final Handler mMainHandler = new Handler(Looper.getMainLooper());

		final Handler mCheckAnrHandler = startCheckThread();
		if ( mCheckAnrHandler == null ){
			Log.println("mCheckAnrHandler == null");
			return;
		}

	
		Runnable mRunnable = new Runnable() {
			@Override
			public void run(){
				// 向子线程发送消息
				mCheckAnrHandler.sendEmptyMessage(MAIN_THREAD_MESSAGE);
				
				//保证只有一个this
				mMainHandler.removeCallbacks(this);
				
				if ( !isStop() ){
					mMainHandler.postDelayed(this, 5000);
				}
			}
		};
		
		mCheckAnrHandler.sendEmptyMessage(MAIN_THREAD_MESSAGE);
		
		mMainHandler.postDelayed(mRunnable, 5000);
	}
}
