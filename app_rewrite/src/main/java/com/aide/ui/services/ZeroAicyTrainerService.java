package com.aide.ui.services;
import io.github.zeroaicy.aide.ui.services.ExecutorsService;
import java.util.Collections;
import java.util.List;
import io.github.zeroaicy.util.Log;

public class ZeroAicyTrainerService extends abcd.mf {

	private static final String TAG = "ZeroAicyTrainerService";
	static{
		//
		ZeroAicyTrainerService singleton = getSingleton();
		singleton.sG();
		singleton.sG();
	}

	static ZeroAicyTrainerService mTrainerService;
	public static ZeroAicyTrainerService getSingleton() {
		if (mTrainerService == null) {
			mTrainerService = new ZeroAicyTrainerService();
		}
		return mTrainerService;
	}

	@Override
	public List<com.aide.ui.trainer.c.d> J8() {
		if (this.inited) {
			return super.J8();
		}
		return Collections.emptyList();
	}


	// 异步导致 XL() 无可用 this.tp
	private boolean inited = false;
	private boolean initing = false;

	@Override
	public void sG() {
		//Log.d(TAG, "sG", this.inited, this.initing, new Throwable());

		if (this.inited) {
			return;
		}

		if (this.initing) {
			return;
		}
		
		if (!this.initing) {

			ExecutorsService executorsService = ExecutorsService.getExecutorsService();
			executorsService.submit(new Runnable(){
					@Override
					public void run() {
						ZeroAicyTrainerService.this.super_sG();
						ZeroAicyTrainerService.this.initing = false;
					}
				});
		}

		this.initing = true;

	}

	private synchronized void super_sG() {
		if (!this.inited) {
			super.sG();
			this.inited = true;
			this.initing = false;
		}
	}

	@Override
	public void aq() {

		while (this.initing && !this.inited) {
			try {
				Thread.sleep(10);
			}
			catch (InterruptedException e) {}
		}
		super.aq();
	}

	public void super_aq() {
		ExecutorsService executorsService = ExecutorsService.getExecutorsService();
		executorsService.submit(new Runnable(){
				@Override
				public void run() {
					super_aq();	
				}
			});
		super.aq();
	}

}
