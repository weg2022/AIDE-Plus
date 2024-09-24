package com.aide.ui.services;

import android.app.Activity;
import androidx.annotation.Keep;

import com.aide.ui.util.BuildGradle;
import io.github.zeroaicy.aide.ui.services.DownloadMavenLibraries;
import java.util.List;

/**
 * 替换maven下载实现
 */
// NativeCodeSupportService$q -> DownloadService$DownloadMavenLibraries
@androidx.annotation.Keep
public class DownloadService$DownloadMavenLibraries extends io.github.zeroaicy.aide.ui.services.DownloadMavenLibraries {

	public DownloadService$DownloadMavenLibraries(
		com.aide.ui.services.DownloadService downloadService, 
		android.app.Activity activity, 
		java.util.List<com.aide.ui.util.BuildGradle.MavenDependency> deps, 
		java.util.List<com.aide.ui.util.BuildGradle.RemoteRepository> remoteRepositorys, 
		java.lang.Runnable completeCallback) {
			
		// 调用
        super(downloadService, activity, deps, remoteRepositorys, completeCallback);
    }   
}

