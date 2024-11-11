package io.github.zeroaicy;

/*
import android.app.Application;
import android.content.Context;
import androidx.startup.Initializer;
import blockcanary.BlockCanary;
import blockcanary.BlockCanaryConfig;
import io.github.zeroaicy.util.Log;
import java.util.Collections;
import java.util.List;

public class BlockCanaryStartupInitializer implements Initializer<BlockCanaryStartupInitializer> {
    public BlockCanaryStartupInitializer create(Context context) {
        //Intrinsics.checkNotNullParameter(context, "context");
        Context applicationContext = context.getApplicationContext();
        if (applicationContext == null) {
            throw new NullPointerException("null cannot be cast to non-null type android.app.Application");
        }
        Application application = (Application) applicationContext;
		initBlockCanary(application);
		Log.d("BlockCanaryStartupInitializer", "自定义BlockCanary");
        return this;
    }

	private static void initBlockCanary(Application application) {
		BlockCanaryConfig blockCanaryConfig = BlockCanaryConfig
			.newBuilder()
			// 区块阈值时间
			.blockThresholdTime(1500)
			// 区块最大阈值时间
			.blockMaxThresholdTime(40_000)
			// 堆栈采样间隔
			.stackSampleInterval(20)
			// 最大堆栈样本缓存计数
			.maxStackSampleCacheCount(100_000)
			//
			.maxCacheBlockingFiles(10_000)
			.build();

		BlockCanary blockCanary = BlockCanary.INSTANCE;
		//Intrinsics.checkNotNullExpressionValue(blockCanaryConfig, "blockCanaryConfig");
		blockCanary.install(application, blockCanaryConfig);
	}


    public List<Class<? extends Initializer<?>>> dependencies() {
        return Collections.emptyList();
	}
}
/*/
public class BlockCanaryStartupInitializer{
	public static String msg2 = ZeroAicyAIDEApplication.TAG;
	
}
//*/

