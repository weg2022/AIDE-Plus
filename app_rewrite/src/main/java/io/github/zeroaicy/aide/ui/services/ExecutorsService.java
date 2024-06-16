package io.github.zeroaicy.aide.ui.services;

import android.os.Looper;
import io.github.zeroaicy.util.Log;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.codehaus.groovy.tools.shell.util.Logger;
import java.util.Map;
import java.util.HashMap;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

public class ExecutorsService {

	private static final boolean isDebug = true;
	
	private static final String TAG = "ExecutorsService";
	private static ThreadFactory threadFactory = new ThreadFactory(){
		private final AtomicInteger poolNumber = new AtomicInteger();
		@Override
		public Thread newThread(Runnable r) {
			//namePrefix = "pool-" + poolNumber.getAndIncrement() + "-thread-";			
			Thread thread = new Thread(r, TAG + "-pool-" + poolNumber.incrementAndGet());
			return thread;
		}
	};
	
	// 主线程
	static Thread uiThread = Looper.getMainLooper().getThread();

	public static boolean isUiThread() {
		return uiThread == Thread.currentThread();
	}
	// 默认子线程
	private static ExecutorsService defaultExecutorsService;
	
	public static ExecutorsService getExecutorsService() {
		if (defaultExecutorsService == null) {
			defaultExecutorsService = getExecutorsService("default");
		}
		return defaultExecutorsService;
	}

	private static Map<String, ExecutorsService> executorsNameMap = new HashMap<>();
	public static ExecutorsService getExecutorsService(String executorsName) {
		ExecutorsService temp = executorsNameMap.get(executorsName);
		if (temp == null) {
			temp = new ExecutorsService();
			executorsNameMap.put(executorsName, temp);
		}
		return temp;
	}

	// invokeAll阻塞调用处线程
	// 暂时用这种
	private ExecutorService service = Executors.newFixedThreadPool(1, threadFactory);
	
	/**
	 * 保证一组调用都在子线程中
	 */
	public Future<?> submit(Runnable runnable) {
		ExecutorsService.Group group = new Group(runnable);
		if (isDebug) {
			StackTraceElement[] stackTraces = Thread.currentThread().getStackTrace();
			String stackTrace = null;
			if (stackTraces != null && stackTraces.length > 3) {
				stackTrace = "来自"  + stackTraces[3];
			}
			group.setStackTrace(stackTrace)
				.setStackTraces(stackTraces);
		}
		
		// 还在子线程中，直接运行
		if (!isUiThread()) {
			group.run();			
			return null;
		}
		return service.submit(group);
	}

	public static class Group implements Runnable {
		public Runnable runnable;
		String stackTrace;
		StackTraceElement[] stackTraces;
		public Group(Runnable runnable) {
			this.runnable = runnable;
		}

		public Group setStackTraces(StackTraceElement[] stackTraces) {
			this.stackTraces = stackTraces;
			return this;
		}

		public Group setStackTrace(String stackTrace) {
			this.stackTrace = stackTrace;
			return this;
		}
		public String getStackTrace() {
			return stackTrace;
		}

		@Override
		public void run() {
			long now = System.currentTimeMillis();
			try {
				this.runnable.run();
			}
			catch (Throwable e) {
				Log.e("异步错误", this.stackTrace, e);
			}
			now = System.currentTimeMillis() - now;
			//Log.d("ExecutorsService", "耗时 ", (now) + "ms");
		}
	}


	public <T> T runTask(Callable<T> callable) {
		try {
			return service.submit(callable).get();
		}
		catch (ExecutionException | InterruptedException e) {
			return null;
		}
	}

	public <T extends Object> List<Future<T>> invokeAll(Collection<Callable<T>> tasks) {
		try {
			return service.invokeAll(tasks);
		}
		catch (InterruptedException e) {
			return null;
		}
	}

	// obj 组标记，用于区分任务是否是一个调用链[组]

}
