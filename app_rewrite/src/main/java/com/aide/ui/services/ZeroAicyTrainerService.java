package com.aide.ui.services;
import io.github.zeroaicy.aide.ui.services.ExecutorsService;
import abcd.mf;
import java.util.List;
import com.aide.ui.trainer.c.d;
import java.util.stream.Collectors;
import java.util.Collections;

public class ZeroAicyTrainerService extends abcd.mf {
	
	static ZeroAicyTrainerService mTrainerService;
	 boolean inited = false;
	public static ZeroAicyTrainerService getSingleton() {
		if( mTrainerService == null ){
			mTrainerService = new ZeroAicyTrainerService();
		}
		return mTrainerService;
	}

	@Override
	public List<com.aide.ui.trainer.c.d> J8() {
		if( inited){
			return super.J8();
		}
		return Collections.emptyList();
	}
	
	
	@Override
	public void sG() {
		ExecutorsService.getExecutorsService().submit(new Runnable(){
				@Override
				public void run() {
					super_sG();
				}
			});
	}

	private void super_sG() {
		super.sG();
		inited = true;
	}

}
