package com.aide.ui.services;
import io.github.zeroaicy.aide.ui.services.ExecutorsService;
import java.util.Collections;
import java.util.List;
import io.github.zeroaicy.util.Log;
import com.aide.ui.trainer.c.d;
import com.aide.ui.trainer.c.j;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.Future;
import java.util.concurrent.Callable;
import android.os.Handler;
import android.os.Looper;

public class ZeroAicyTrainerService extends abcd.mf {

	private static final String TAG = "ZeroAicyTrainerService";
	
	static abcd.mf mTrainerService;
	public static abcd.mf getSingleton() {
		if (mTrainerService == null) {
			// 异步总是有问题
			mTrainerService = new abcd.mf();
		}
		return mTrainerService;
	}
	private AtomicBoolean inited = new AtomicBoolean(false);
	private AtomicBoolean initing = new AtomicBoolean(false);
	private final Handler mainHandler = new Handler(Looper.getMainLooper());
	/***********************************************************************/
	public void hz(){
		mainHandler.postDelayed(new a(), 1000L);
	}
	
	ExecutorsService executorsService = ExecutorsService.getExecutorsService();
	@Override
	public void aq() {
		executorsService.submit(new Runnable(){
				@Override
				public void run() {
					aqAsync();
				}
			});

	}
	public void aqAsync() {
		super.aq();
	}

	@Override
	public List<com.aide.ui.trainer.c.d> J8() {
		if (this.inited.get()) {
			return super.J8();
		}
		// 这个一般是TrainerCourseActivity调用所以阻塞一下没事
		List<com.aide.ui.trainer.c.d> submit = executorsService.runTask(new Callable<List<com.aide.ui.trainer.c.d>>(){
				@Override
				public List<com.aide.ui.trainer.c.d> call() {
					return J8Async();
				}
			});
		if (submit != null) {
			return submit;
		}
		return Collections.emptyList();
	}

	public List<com.aide.ui.trainer.c.d> J8Async() {
		return super.J8();
	}

	// init方法 一个实例执行一次就行
	@Override
	public void sG() {
		// 已初始化或正在初始化
		if (this.inited.get() || this.initing.get()) {
			return;
		}
		// 初始化中
		this.initing.set(true);
		// 异步
		executorsService.submit(new Runnable(){
				@Override
				public void run() {
					sGAsync();
				}
			});

	}
	public void sGAsync() {
		super.sG();
		ZeroAicyTrainerService.this.initing.set(false);
		ZeroAicyTrainerService.this.inited.set(true);

	}

	@Override
	public com.aide.ui.trainer.c.d SI() {
		if (!this.inited.get() || this.initing.get()) {
			return null;
		}
		return super.SI();
	}

	@Override
	public com.aide.ui.trainer.c.d XL(final String string) {
		if (!this.inited.get() || this.initing.get()) {
			com.aide.ui.trainer.c.d submit = executorsService.runTask(new Callable<com.aide.ui.trainer.c.d>(){
					@Override
					public com.aide.ui.trainer.c.d call() {
						return XLAsync(string);
					}
				});
			if( submit != null ){
				return submit;
			}
			return null;
		}
		return super.XL(string);
	}
	public com.aide.ui.trainer.c.d XLAsync(String string) {
		return super.XL(string);
	}
	@Override
	public String aM() {
		if (!this.inited.get() || this.initing.get()) {
			String submit = executorsService.runTask(new Callable<String>(){
					@Override
					public String call() {
						return aMAsync();
					}
				});
			return submit;
		}
		return super.aM();
	}
	public String aMAsync() {
		return super.aM();
	}
	@Override
	public void kQ(final String string) {
		if (!this.inited.get() || this.initing.get()) {
			executorsService.runTask(new Callable<Void>(){
					@Override
					public Void call() {
						kQAsync(string);
						return null;
					}
				});
			return;
		}

		super.kQ(string);
	}
	public void kQAsync(String string) {
		super.kQ(string);
	}

	@Override
	public void FN(final com.aide.ui.trainer.c.j j, final boolean p) {
		if (!this.inited.get() || this.initing.get()) {
			executorsService.runTask(new Callable<Void>(){
					@Override
					public Void call() {
						FNAsync(j, p);
						return null;
					}
				});
			return;
		}
		super.FN(j, p);
	}

	public void FNAsync(com.aide.ui.trainer.c.j j, boolean p) {
		super.FN(j, p);
	}
}
