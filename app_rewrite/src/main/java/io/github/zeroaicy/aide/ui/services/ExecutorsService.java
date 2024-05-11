package io.github.zeroaicy.aide.ui.services;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.Callable;
import java.util.Collections;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.List;
import java.util.Collection;
import io.github.zeroaicy.util.Log;
import io.github.zeroaicy.aide.ui.services.ExecutorsService.Group;
import android.os.Looper;

public class ExecutorsService {
	static Thread uiThread = Looper.getMainLooper().getThread();

	private static final boolean isDebug = !true;

	public static boolean isUiThread() {
		return uiThread == Thread.currentThread();
	}
	public static ExecutorsService mExecutorsService;
	public static ExecutorsService getExecutorsService() {
		if (mExecutorsService == null) {
			mExecutorsService = new ExecutorsService();
		}
		return mExecutorsService;
	}
	//ThreadPoolExecutor mThreadPoolExecutor;

	// invokeAll阻塞调用处线程
	// 暂时用这种
	private ExecutorService service = Executors.newFixedThreadPool(16);

	public Future<?> submit(Runnable runnable) {

		// 在子线程中，直接运行
		if (!isUiThread()) {
			try {
				runnable.run();				
			}
			catch (Throwable e) {
				e.printStackTrace();
			}
			return null;
		}

		if (isDebug) {
			StackTraceElement[] stackTraces = Thread.currentThread().getStackTrace();
			String stackTrace = null;
			if (stackTraces != null && stackTraces.length > 3) {
				stackTrace = "来自"  + stackTraces[3];
			}
			ExecutorsService.Group group = new Group(runnable);
			group.setStackTrace(stackTrace)
				.setStackTraces(stackTraces);
			Log.d("ExecutorsService", group.getFlag(), "添加任务", stackTrace);

			return service.submit(group);
		}

		return service.submit(runnable);
	}

	public static class Group implements Runnable {
		public Runnable runnable;
		String stackTrace;
		StackTraceElement[] stackTraces;
		public Group(Runnable runnable) {
			this.runnable = runnable;
		}

		public void setStackTraces(StackTraceElement[] stackTraces) {
			this.stackTraces = stackTraces;
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
				e.printStackTrace();
			}

			now = System.currentTimeMillis() - now;
			Log.d("ExecutorsService", getFlag(), "耗时 ", (now) + "ms", stackTrace);

		}

		private String getFlag() {
			return "Group: " + System.identityHashCode(this);
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
