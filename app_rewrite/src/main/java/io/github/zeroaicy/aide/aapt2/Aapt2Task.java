package io.github.zeroaicy.aide.aapt2;

import io.github.zeroaicy.util.Log;
import com.aide.ui.build.android.AaptService;
import java.io.PrintStream;
import java.io.File;
import io.github.zeroaicy.util.Log.AsyncOutputStreamHold;
import io.github.zeroaicy.util.reflect.ReflectPie;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.io.FileFilter;
import io.github.zeroaicy.util.ContextUtil;
import java.util.Arrays;
import java.security.MessageDigest;
import java.math.BigInteger;
import com.aide.ui.build.android.AaptService$ErrorResult;
import com.aide.ui.build.android.AaptService$Args;

public class Aapt2Task {

	private static final String TAG = Aapt2Task.class.getSimpleName();;
	//AaptService$c m;
	public static AaptService$ErrorResult proxyAapt(AaptService$Args aapt$c) {
		try {
			return proxyAaptZeroAicy(aapt$c);
		} catch (Throwable e) {
			Log.e(TAG, "proxyAapt出错 --> ", e);
		}
		return null;
	}
	
	private static AaptService$ErrorResult proxyAaptZeroAicy(AaptService$Args aapt$c) throws Exception {
		return Aapt2TaskFromZeroAicy.proxyAapt(aapt$c);
	}

}
