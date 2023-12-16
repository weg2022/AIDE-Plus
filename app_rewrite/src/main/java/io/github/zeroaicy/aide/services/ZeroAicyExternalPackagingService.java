package io.github.zeroaicy.aide.services;
import com.aide.ui.build.packagingservice.ExternalPackagingService;
import io.github.zeroaicy.util.Log;

public class ZeroAicyExternalPackagingService extends ExternalPackagingService {
	@Override
	public void onCreate() {
		try {
			ExternalPackagingService.ExternalPackagingServiceWorker externalPackagingServiceWorker = getExternalPackagingServiceWorker();
			if (externalPackagingServiceWorker != null) {
				//释放旧的
				this.WB.we();
				//换成自己的
				this.WB = externalPackagingServiceWorker;			
			}	
		} catch (Throwable e) {
			Log.d("ExternalPackagingServiceWrapper", "替换打包实现失败", e);
		}
		super.onCreate();
	}
	
	public PackagingWorkerWrapper getExternalPackagingServiceWorker() {
		return new ZeroAicyPackagingWorker(this);
	}

}
