//
// Decompiled by Jadx - 573ms
//
package com.aide.ui.services;

import android.app.Activity;
import android.text.TextUtils;
import com.aide.ui.ServiceContainer;
import com.aide.ui.util.BuildGradle;
import com.aide.ui.util.MavenMetadataXml;
import com.aide.ui.util.PomXml;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import io.github.zeroaicy.aide.ui.services.DownloadMavenLibraries;
import androidx.annotation.Keep;

// NativeCodeSupportService$q -> DownloadService$DownloadMavenLibraries
@Keep
public class DownloadService$DownloadMavenLibraries extends DownloadMavenLibraries {
    public DownloadService$DownloadMavenLibraries(DownloadService downloadService, Activity activity, List<BuildGradle.MavenDependency> deps, List<BuildGradle.RemoteRepository> remoteRepositorys, Runnable completeCallback) {
        super(downloadService, activity, deps, remoteRepositorys, completeCallback);
    }   
}

