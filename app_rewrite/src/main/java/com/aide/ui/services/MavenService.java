//
// Decompiled by Jadx - 994ms
//
package com.aide.ui.services;

import com.aide.common.AppLog;
import com.aide.ui.App;
import com.aide.ui.AppPreferences;
import com.aide.ui.util.BuildGradle;
import com.aide.ui.util.FileSystem;
import com.aide.ui.util.MavenDependencyVersion;
import com.aide.ui.util.MavenMetadataXml;
import com.aide.ui.util.PomXml;
import io.github.zeroaicy.aide.extend.ZeroAicyExtensionInterface;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MavenService {
    private Map<String, BuildGradle.MavenDependency> dependencyMap;
    private Map<BuildGradle.MavenDependency, String> dependencyPathMap;
    public MavenService() {
        try {
            this.dependencyPathMap = new HashMap<>();
            this.dependencyMap = new HashMap<>();
        }
		catch (Throwable th) {
            throw new Error(th);
        }
    }

    public static String DW() {
        return getDefaulRepositoriePath();
    }
	//计算缓存路径
	private String getArtifactCachePath(String repositoriePath, String groupId, String artifactId, String version, String packaging) {
        String artifactIdDir = repositoriePath + "/" + groupId.replace(".", "/") + "/" + artifactId;

        if (!FileSystem.isDirectory(artifactIdDir) 
			|| (version = getSearchVersion(artifactIdDir, version)) == null) {
            return null;
        }

        String artifactIdVersionDir = artifactIdDir + "/" + version + "/" + artifactId + "-" + version;
		if ("pom".equals(packaging)) {
			if (new File(artifactIdVersionDir + ".pom").isFile()) {
				return artifactIdVersionDir + ".pom";
			}
		}
        if (new File(artifactIdVersionDir + ".jar").isFile()) {
            return artifactIdVersionDir + ".jar";
        }
        if (new File(artifactIdVersionDir + ".aar").isDirectory()) {
            return artifactIdVersionDir + ".aar";
        }
        if (new File(artifactIdVersionDir + ".exploded.aar").isDirectory()) {
            return artifactIdVersionDir + ".exploded.aar";
        }
        if (new File(artifactIdVersionDir + ".aar").isFile()) {
            VH(artifactIdVersionDir + ".aar", artifactIdVersionDir + ".exploded.aar");
            return artifactIdVersionDir + ".exploded.aar";
        }
        return null;
    }


	//复用缓存
    private BuildGradle.MavenDependency Hw(BuildGradle.MavenDependency dpendency) {
        try {
            if (!this.dependencyMap.containsKey(dpendency.getGroupIdArtifactId())) {
                this.dependencyMap.put(dpendency.getGroupIdArtifactId(), dpendency);
            }
            return this.dependencyMap.get(dpendency.getGroupIdArtifactId());
        }
		catch (Throwable th) {
            throw new Error(th);
        }
    }

    private boolean P8(String str, String str2) {
        try {
            if (new File(str2).isDirectory()) {
                File[] listFiles = new File(str2).listFiles();
                if (listFiles != null) {
                    long lastModified = new File(str).lastModified();
                    for (File file : listFiles) {
                        if (file.isFile() && file.lastModified() < lastModified) {
                            return false;
                        }
                    }
                    return true;
                }
                return true;
            }
            return false;
        }
		catch (Throwable th) {
            throw new Error(th);
        }
    }

    private void QX(Map<String, String> map, String str, List<String> list, int depth) {
        try {
            if (list.contains(str)) {
                return;
            }
            list.add(str);
            if (depth > 0) {
                PomXml configuration = PomXml.empty.getConfiguration(getDependencyPomPath(str));
				for (BuildGradle.MavenDependency dependency : configuration.dependencyManagements) {
					// dependencyManagement只做版本控制
					BuildGradle.MavenDependency cache = Hw(dependency);
					if (dependency != cache) {
						//控制版本里的更新
						if (MavenDependencyVersion.compareTo(dependency.version, cache.version) > 0) {
							this.dependencyMap.put(dependency.getGroupIdArtifactId(), dependency);
						}

					}
				}
				String dependencyPath;
				for (BuildGradle.MavenDependency dependency : configuration.dependencies) {
                    if (!vy(dependency) && (dependencyPath = we(map, Hw(dependency))) != null) {
                        QX(map, dependencyPath, list, depth - 1);
                    }
                }
            }
        }
		catch (Throwable th) {
            throw new Error(th);
        }
    }

    private void VH(String str, String str2) {
        try {
            if (P8(str, str2)) {
                return;
            }
            try {
                FileSystem.u7(new FileInputStream(str), str2, true);
                AppLog.DW("Extracted AAR " + str);
            }
			catch (IOException e) {
                e.printStackTrace();
            }
        }
		catch (Throwable th) {
            throw new Error(th);
        }
    }

    private List<String> Ws(Map<String, String> map, String str) {
        try {
            ArrayList<String> arrayList = new ArrayList<>();
            if (str != null) {
                QX(map, str, arrayList, 3);
            }
            return arrayList;
        }
		catch (Throwable th) {
            throw new Error(th);
        }
    }

	//递归解析dependency的子依赖
    private void Zo(BuildGradle.MavenDependency dependency, int depth) {
        try {
			//从缓存仓库计算MavenDependency依赖路径[pom|jar|aar]
            String dependencyPath = we(null, dependency);
            if (dependencyPath != null) {
                String version = a8(dependencyPath);
                if (!this.dependencyMap.containsKey(dependency.getGroupIdArtifactId()) 
					|| MavenDependencyVersion.compareTo(version, this.dependencyMap.get(dependency.getGroupIdArtifactId()).version) > 0) {
                    BuildGradle.MavenDependency mavenDependency = new BuildGradle.MavenDependency(dependency, version);
					this.dependencyMap.put(dependency.getGroupIdArtifactId(), mavenDependency);
                }

                if (depth > 0) {
                    PomXml configuration = PomXml.empty.getConfiguration(getDependencyPomPath(dependencyPath));

					for (BuildGradle.MavenDependency subDependency : configuration.dependencyManagements) {
						// dependencyManagement只做版本控制
						BuildGradle.MavenDependency cache = Hw(subDependency);
						if (subDependency != cache) {
							//控制版本里的更新
							if (MavenDependencyVersion.compareTo(subDependency.version, cache.version) > 0) {
								this.dependencyMap.put(subDependency.getGroupIdArtifactId(), subDependency);
							}
						}
					}
					for (BuildGradle.MavenDependency subDependency : configuration.dependencies) {
                        if (!vy(subDependency)) {
                            Zo(subDependency, depth - 1);
                        }
                    }
                }
            }
        }
		catch (Throwable th) {
            throw new Error(th);
        }
    }
	//获取此依赖缓存路径的版本
    private static String a8(String str) {
        try {
            String[] split = str.split("/");
            return split[split.length - 2];
        }
		catch (Throwable th) {
            throw new Error(th);
        }
    }



	//从 jar|aar依赖路径返回pom路径
    private String getDependencyPomPath(String str) {
        try {
            if (str.endsWith(".exploded.aar")) {
                return str.substring(0, str.length() - 13) + ".pom";
            }
            return str.substring(0, str.length() - 4) + ".pom";
        }
		catch (Throwable th) {
            throw new Error(th);
        }
    }

    private static String getDefaulRepositoriePath() {
        String userM2Repositories = ZeroAicyExtensionInterface.getUserM2Repositories();
        if (userM2Repositories != null) {
            return userM2Repositories;
        }
        try {
            return FileSystem.yS() + "/.aide/maven";
        }
		catch (Throwable th) {
            throw new Error(th);
        }
    }

    private String getMavenDependencyPath(BuildGradle.MavenDependency dependency) {
        try {
            for (String str : getRepositoriePaths()) {
                String EQ = getArtifactCachePath(str, dependency.groupId, dependency.artifactId, dependency.version, dependency.packaging);
                if (EQ != null) {
                    return EQ;
                }
            }
            return null;
        }
		catch (Throwable th) {
            throw new Error(th);
        }
    }

    private List<String> getRepositoriePaths() {
        try {
            ArrayList<String> arrayList = new ArrayList<>();
            for (String str : AppPreferences.br().split(";")) {
                if (!str.trim().isEmpty()) {
                    arrayList.add(str.trim());
                }
            }
            arrayList.add(getDefaulRepositoriePath());
            return arrayList;
        }
		catch (Throwable th) {
            throw new Error(th);
        }
    }
	private static MavenMetadataXml mavenMetadataXml = new MavenMetadataXml();
    private String getSearchVersion(String artifactIdDir, String searchVersion) {
        try {
            String metadataPath = artifactIdDir + "/maven-metadata.xml";
			String version;
			if (!FileSystem.isFileAndNotZip(metadataPath) 
				|| (version = mavenMetadataXml.getConfiguration(metadataPath).getVersion(searchVersion)) == null) {
                try {
					//本地所有版本集合
                    ArrayList<String> versions = new ArrayList<>();
                    for (String artifactIdVersionDir : FileSystem.we(artifactIdDir)) {
                        versions.add(FileSystem.getName(artifactIdVersionDir));
                    }
                    return MavenMetadataXml.getVersion(versions, searchVersion);
                }
				catch (Exception unused) {
                    return null;
                }
            }
            return version;
        }
		catch (Throwable th) {
            throw new Error(th);
        }
    }

	/**
	 * 计算 flat仓库的aar[groupId无效]
	 */
    private String getFlatArtifactPath(String flatRepositoryPath, String groupId, String artifactId, String version) {
        try {
			//从
            String artifactPath = flatRepositoryPath + "/" + artifactId + ".aar";
            if (new File(artifactPath).exists()) {
                return artifactPath;
            }
            File[] listFiles = new File(flatRepositoryPath).listFiles();
            if (listFiles == null) {
				return null;
			}
			for (File file : listFiles) {
				String name = file.getName();

				if (name.startsWith(artifactId + "-") 
					&& name.endsWith(".aar")
				//是否匹配版本
					&& MavenMetadataXml.matchVersion(name.substring(artifactId.length() + 1, name.length() - 4), version)) {
					return file.getPath();
				}
			}
			return null;

        }
		catch (Throwable th) {
            throw new Error(th);
        }
    }




    private boolean vy(BuildGradle.MavenDependency dependency) {
        try {
            return dependency.artifactId.contains("android-all");
        }
		catch (Throwable th) {
            throw new Error(th);
        }
    }

    private String we(Map<String, String> flatRepoPathMap, BuildGradle.MavenDependency dependency) {
        try {
            if (flatRepoPathMap != null) {
                for (String flatRepositoryPath : flatRepoPathMap.keySet()) {
                    String flatArtifactPath = getFlatArtifactPath(flatRepositoryPath, dependency.groupId, dependency.artifactId, dependency.version);
					if (flatArtifactPath != null) {
                        String name = new File(flatArtifactPath).getName();
                        String flatRepoCachePath = flatRepoPathMap.get(flatRepositoryPath);
						String explodedAarPath = flatRepoCachePath + "/" + name.substring(0, name.length() - 4) + ".exploded.aar";
                        //提取aar并解压
						VH(flatArtifactPath, explodedAarPath);
                        return explodedAarPath;
                    }
                }
            }

            if (this.dependencyPathMap.containsKey(dependency)) {
                String str3 = this.dependencyPathMap.get(dependency);
                if (str3.length() == 0) {
                    return null;
                }
                return str3;
            }
            String dependencyPath = getMavenDependencyPath(dependency);
            if (dependencyPath == null) {
                this.dependencyPathMap.put(dependency, "");
            } else {
                this.dependencyPathMap.put(dependency, dependencyPath);
            }
            return dependencyPath;
        }
		catch (Throwable th) {
            throw new Error(th);
        }
    }

	// 递归查找依赖缓存，不存在时加入list
    private void yS(Map<String, String> flatRepoPathMap, BuildGradle.MavenDependency dependency, List<BuildGradle.MavenDependency> dependencyList, int depth) {
        try {
			//从缓存仓库计算MavenDependency依赖路径[pom|jar|aar]
            String dependencyPath = we(flatRepoPathMap, Hw(dependency));

			if (dependencyPath == null) {
                dependencyList.add(dependency);
			} else if (depth > 0) {
                PomXml curPomXml = PomXml.empty.getConfiguration(getDependencyPomPath(dependencyPath));
				for (BuildGradle.MavenDependency subDependency : curPomXml.dependencyManagements) {
					// dependencyManagement只做版本控制
					BuildGradle.MavenDependency cache = Hw(subDependency);
					if (subDependency != cache) {
						//控制版本里的更新
						if (MavenDependencyVersion.compareTo(subDependency.version, cache.version) > 0) {
							this.dependencyMap.put(subDependency.getGroupIdArtifactId(), subDependency);
						}

					}
				}
				for (BuildGradle.MavenDependency subDependency : curPomXml.dependencies) {
                    if (!vy(subDependency)) {
                        yS(flatRepoPathMap, subDependency, dependencyList, depth - 1);
                    }
                }
            }
        }
		catch (Throwable th) {
            throw new Error(th);
        }
    }

    public boolean BT(Map<String, String> map, BuildGradle.MavenDependency dependency) {
        try {
            return we(map, Hw(dependency)) != null;
        }
		catch (Throwable th) {
            throw new Error(th);
        }
    }



    public List<String> J0(String str) {
        try {
            return Ws(null, str);
        }
		catch (Throwable th) {
            throw new Error(th);
        }
    }

    public List<String> J8(Map<String, String> map, BuildGradle.MavenDependency dependency) {
        try {
            return Ws(map, we(map, Hw(dependency)));
        }
		catch (Throwable th) {
            throw new Error(th);
        }
    }



	// 返回 未下载的依赖
	@Deprecated
    public List<BuildGradle.MavenDependency> er(Map<String, String> flatRepoPathMap, BuildGradle.MavenDependency dependency) {
        try {
			return getNoDownloadDeps(flatRepoPathMap, dependency);
        }
		catch (Throwable th) {
            throw new Error(th);
        }
    }
	//解析MavenDependency的子依赖
    public void v5(BuildGradle.MavenDependency dependency) {
        try {
            Zo(dependency, 3);
        }
		catch (Throwable th) {
            throw new Error(th);
        }
    }
	
	// 返回 未下载的依赖
    public List<BuildGradle.MavenDependency> getNoDownloadDeps(Map<String, String> flatRepoPathMap, BuildGradle.MavenDependency dependency) {
        try {
            ArrayList<BuildGradle.MavenDependency> noDownloadDeps = new ArrayList<>();
            yS(flatRepoPathMap, dependency, noDownloadDeps, 3);

            return noDownloadDeps;
        }
		catch (Throwable th) {
            throw new Error(th);
        }
    }

    public String u7(BuildGradle.MavenDependency dependency) {
        try {
            return we(null, dependency);
        }
		catch (Throwable th) {
            throw new Error(th);
        }
    }

	/**
	 * 共有api
	 */
	// getMetadataUrl
	@Deprecated
    public static String rN(BuildGradle.RemoteRepository remoteRepository, BuildGradle.MavenDependency dependency) {
        try {
            return getMetadataUrl(remoteRepository, dependency);
        }
		catch (Throwable th) {
            throw new Error(th);
        }
    }
	public static String getMetadataUrl(BuildGradle.RemoteRepository remoteRepository, BuildGradle.MavenDependency dependency) {
        try {
            return remoteRepository.repositorieURL + ("/" + dependency.groupId.replace('.', '/') + "/" + dependency.artifactId) + "/maven-metadata.xml";
        }
		catch (Throwable th) {
            throw new Error(th);
        }
    }
	// getMetadataPath
	@Deprecated
    public static String lg(BuildGradle.RemoteRepository remoteRepository, BuildGradle.MavenDependency dependency) {
        try {
            return getMetadataPath(remoteRepository, dependency);
        }
		catch (Throwable th) {
            throw new Error(th);
        }
    }
	public static String getMetadataPath(BuildGradle.RemoteRepository remoteRepository, BuildGradle.MavenDependency dependency) {
        try {
			// getDefaulRepositoriePath应该叫默认下载maven缓存路径
            return getDefaulRepositoriePath() + ("/" + dependency.groupId.replace('.', '/') + "/" + dependency.artifactId) + "/maven-metadata.xml";
        }
		catch (Throwable th) {
            throw new Error(th);
        }
    }
	// 重置路径
	@Deprecated
    public void FH() {
        try {
            resetDepPathMap();
        }
		catch (Throwable th) {
            throw new Error(th);
        }
    }
	public void resetDepPathMap() {
        try {
            this.dependencyPathMap.clear();
        }
		catch (Throwable th) {
            throw new Error(th);
        }
    }

	@Deprecated
    public void ei() {
        try {
            resetDepMap();
        }
		catch (Throwable th) {
            throw new Error(th);
        }
    }
	public void resetDepMap() {
        try {
            this.dependencyMap.clear();
        }
		catch (Throwable th) {
            throw new Error(th);
        }
    }


	//Refreshing
    @Deprecated
    public void nw() {
        try {
			refresh();
		}
		catch (Throwable th) {
            throw new Error(th);
        }
    }
    public void refresh() {
        try {
            App.sy(App.gn(), "Refreshing...", new MavenService$a(this), new MavenService$b(this));
        }
		catch (Throwable th) {
            throw new Error(th);
        }
    }
	// getArtifactUrl
	@Deprecated
    public static String aM(BuildGradle.RemoteRepository remoteRepository, BuildGradle.MavenDependency dependency, String version, String type) {
        try {
            return getArtifactUrl(remoteRepository, dependency, version, type);
        }
		catch (Throwable th) {
            throw new Error(th);
        }
    }
	public static String getArtifactUrl(BuildGradle.RemoteRepository remoteRepository, BuildGradle.MavenDependency dependency, String version, String type) {
        try {
            return remoteRepository.repositorieURL + (("/" + dependency.groupId.replace('.', '/') + "/" + dependency.artifactId) + "/" + version + "/" + dependency.artifactId + "-" + version) + type;
        }
		catch (Throwable th) {
            throw new Error(th);
        }
    }
	// getArtifactPath
	@Deprecated
    public static String XL(BuildGradle.RemoteRepository remoteRepository, BuildGradle.MavenDependency dependency, String version, String type) {
        try {
            return getArtifactPath(remoteRepository, dependency, version, type);
        }
		catch (Throwable th) {
            throw new Error(th);
        }
    }
	public static String getArtifactPath(BuildGradle.RemoteRepository remoteRepository, BuildGradle.MavenDependency dependency, String version, String type) {
        try {
            return getDefaulRepositoriePath() + (("/" + dependency.groupId.replace('.', '/') + "/" + dependency.artifactId) + "/" + version + "/" + dependency.artifactId + "-" + version) + type;
        }
		catch (Throwable th) {
            throw new Error(th);
        }
    }


	//getDepPathMap
	@Deprecated
    public static Map<BuildGradle.MavenDependency, String> j6(MavenService mavenService) {
        return mavenService.dependencyPathMap;
    }

	//getDepPathMap
    public static Map<BuildGradle.MavenDependency, String> getDepPathMap(MavenService mavenService) {
        return mavenService.dependencyPathMap;
    }

}

