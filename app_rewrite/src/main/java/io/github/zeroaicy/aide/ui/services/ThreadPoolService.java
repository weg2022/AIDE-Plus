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
import com.aide.ui.build.android.AaptService$d;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ThreadPoolService implements ExecutorService {


	@Override
	public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
		return this.service.awaitTermination(timeout, unit);
	}

	@Override
	public <T extends Object> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks) throws InterruptedException {
		return  this.service.invokeAll(tasks);
	}
	@Override
	public <T extends Object> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws InterruptedException {
		return this.service.invokeAll(tasks, timeout, unit);
	}

	@Override
	public <T extends Object> T invokeAny(Collection<? extends Callable<T>> tasks) throws ExecutionException, InterruptedException {
		return this.service.invokeAny(tasks);
	}

	@Override
	public <T extends Object> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit) throws ExecutionException, InterruptedException, TimeoutException {
		return this.service.invokeAny(tasks, timeout, unit);
	}

	@Override
	public boolean isShutdown() {
		return  this.service.isShutdown();
	}

	@Override
	public boolean isTerminated() {
		return  this.service.isTerminated();
	}

	@Override
	public void shutdown() {
		this.service.shutdown();
	}

	@Override
	public List<Runnable> shutdownNow() {
		return  this.service.shutdownNow();
	}

	@Override
	public <T extends Object> Future<T> submit(Runnable task, T result) {
		return  this.service.submit(task, result);
	}

	@Override
	public <T extends Object> Future<T> submit(Callable<T> task) {
		return  this.service.submit(task);
	}
	/**
	 * 监听
	 */
	@Override
	public Future<?> submit(Runnable runnable) {
		ThreadPoolService.Group group = new Group(runnable);
		// 还在子线程中，直接运行
		if (!isUiThread()) {
			group.run();			
			return null;
		}
		return service.submit(group);
	}
	/**
	 * 监听
	 */
	@Override
	public void execute(Runnable command) {
		ThreadPoolService.Group group = new Group(command);
		// 还在子线程中，直接运行
		if (!isUiThread()) {
			group.run();			
			return;
		}
		service.submit(group);
	}

	public static final boolean isDebug = !true;

	public static final boolean isPrint = false;

	private static final String TAG = "ExecutorsService";
	private static final int maxThreadNumber = 8;

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
	private static Thread uiThread = Looper.getMainLooper().getThread();


	public static boolean isUiThread() {
		return uiThread == Thread.currentThread();
	}

	// 默认子线程
	private static ThreadPoolService defaultExecutorsService;

	// 默认线程为4
	public static ThreadPoolService getExecutorsService() {
		if (defaultExecutorsService == null) {
			defaultExecutorsService = getExecutorsService("default", 4);
		}
		return defaultExecutorsService;
	}

	private static Map<String, ThreadPoolService> executorsNameMap = new HashMap<>();

	/**
	 * 默认线程数为 1
	 */
	public static ThreadPoolService getThreadPoolService(String executorsName) {
		return getExecutorsService(executorsName, 1);
	}

	private static ThreadPoolService getExecutorsService(String executorsName, int threadNumber) {
		ThreadPoolService temp = executorsNameMap.get(executorsName);
		if (temp == null) {
			temp = new ThreadPoolService(threadNumber);
			executorsNameMap.put(executorsName, temp);
		}
		return temp;
	}
	// invokeAll阻塞调用处线程
	// 暂时用这种
	private final ExecutorService service;
	public ExecutorService getService() {
		return this.service;
	}
	public ThreadPoolService() {
		service = Executors.newFixedThreadPool(1, threadFactory);
	}

	public ThreadPoolService(int threadNumber) {
		if (threadNumber > maxThreadNumber) {
			threadNumber = maxThreadNumber;
		}
		service = Executors.newFixedThreadPool(threadNumber, threadFactory);
	}

	/**
	 * 使得AaptService可以使用此线程池
	 * 已免运行aapt2时
	 */
	public <V> void execute(FutureTask<V> futureTask) {
		service.execute(futureTask);
	}

	public static class Group implements Runnable {
		public Runnable runnable;
		String stackTrace;
		StackTraceElement[] stackTraces;

		public Group(Runnable runnable) {
			this.runnable = runnable;
			if (isDebug) {
				StackTraceElement[] stackTraces = Thread.currentThread().getStackTrace();
				String stackTrace = null;
				if (stackTraces != null && stackTraces.length > 4) {
					stackTrace = "来自"  + stackTraces[4];
				}
				setStackTrace(stackTrace);
				setStackTraces(stackTraces);
			}
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
			if (isDebug && isPrint) {

				Log.println("\n******************************************************");
				Log.println(stackTrace);
			}
			long now = System.currentTimeMillis();
			try {
				this.runnable.run();
			}
			catch (Throwable e) {
				Log.e("异步错误", this.stackTrace, e);
			}
			now = System.currentTimeMillis() - now;
			if (isDebug && isPrint) {
				Log.d("ExecutorsService", "耗时 ", (now) + "ms");
				Log.println(stackTraces);
				Log.println("******************************************************\n");
				
			}
			

		}
	}
}
