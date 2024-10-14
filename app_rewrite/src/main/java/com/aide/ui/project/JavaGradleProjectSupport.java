package com.aide.ui.project;

import androidx.annotation.Keep;
import com.aide.common.ValueRunnable;
import com.aide.engine.EngineSolution;
import com.aide.ui.ServiceContainer;
import com.aide.ui.project.internal.GradleTools;
import com.aide.ui.services.ProjectSupport;
import com.aide.ui.services.TemplateService;
import com.aide.ui.util.BuildGradle;
import com.aide.ui.util.FileSystem;
import io.github.zeroaicy.aide.extend.ZeroAicyExtensionInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import com.aide.ui.util.BuildGradleExt;
import com.aide.ui.util.BuildGradle.Dependency;
import java.util.Iterator;

/**
 * Java项目使用Gradle作为依赖管理
 * 项目根目录包含 build.gradle [插件使用java]
 * 且src/main目录下，没有AndroidManifest.xml
 * 必须优先AndroidProjectSupport判断*
 */
public class JavaGradleProjectSupport implements ProjectSupport {
	/**
	 * 用于判断是否是Java项目
	 * 
	 */
	private static boolean isJavaGradleProject(String projectPath) {
		// 必须有 src build.gradle 
		// 后期添加对 apply plugin的判断 用BuildGradle解析并缓存，不用每次都解析
		return GradleTools.isGradleProject(projectPath) 
			&& !GradleTools.isAndroidProject(projectPath)
			&& !GradleTools.isAndroidProject(projectPath + "/src/main") ;
	}



	@Override
	public void buildProject(boolean p) {
		// TODO: Implement this method
	}

	@Override
	public int getOpenProjectNameStringId(String string) {
		// TODO: Implement this method
		return 0;
	}

	@Override
	public String getProjectAttributeHtmlString() {
		return null;
	}






	/**
	 * subProjectMap key 项目路径 value 项目依赖
	 */
	@Override
	public void init(String projectPath, Map<String, List<String>> subProjectMap, List<String> projectPaths) {
		// 建立项目与其依赖项

		// 清除maven解析缓存
		ServiceContainer.getMavenService().resetDepMap();
		// 预先解析一遍依赖 防止边检查边解析出现问题
		preResolving(projectPath);

		// 递归解析依赖
		resolvingChildProject(projectPath, new HashSet<String>());
		//添加主项目[一般来说就一个，除非是wearApp那种]
		projectPaths.add(projectPath);

		//
		pN(projectPath, subProjectMap, projectPaths);
	}

	/**
	 * 用于填充projectPath的依赖，且递归填充子依赖的依赖
	 * 感觉subProjectMap只是 [项目 | 子项目 | aar] 与其依赖的映射
	 */
	private void pN(String projectPath, Map<String, List<String>> subProjectMap, List<String> projectPaths) {
		if (subProjectMap.containsKey(projectPath)) {
			return;
		}

		subProjectMap.put(projectPath, new ArrayList<String>());

		// 填充 projectPath 的依赖
		fillSubProjectDependency(projectPath, subProjectMap);

		// 遍历 projectPath的依赖并递归自己
		for (String dep : subProjectMap.get(projectPath)) {
			pN(dep, subProjectMap, projectPaths);
		}
	}

	/**
	 * 处理一个项目的子项目
	 */
	public void resolvingChildProject(String projectPath, Set<String> resolvedProjects) {
		// AIDE是 resolvedProjects.contains(resolvedProjects)
		// 这应该是防止循环依赖的
		if (resolvedProjects.contains(projectPath)) {
			return;
		}

		resolvedProjects.add(projectPath);

		if (isJavaGradleProject(projectPath)) {
			// 
			List<Dependency> projectDependencies = getProjectDependencies(projectPath);
			for (BuildGradle.Dependency dependency : projectDependencies) {
				if (dependency instanceof BuildGradle.MavenDependency) {
					// 解析maven依赖
					ServiceContainer.getMavenService().resolvingMavenDependency((BuildGradle.MavenDependency)dependency);
				}

			}
			for (BuildGradle.Dependency dependency : projectDependencies) {
				if (dependency instanceof BuildGradle.ProjectDependency) {
					BuildGradle.ProjectDependency projectDependency = (BuildGradle.ProjectDependency)dependency;
					// 计算依赖的项目目录
					String settingsGradlePath = GradleTools.getSettingsGradlePath(projectPath);

					BuildGradleExt settingsGradleBuildGradleExt = buildGradleExt.getConfiguration(settingsGradlePath);
					String projectDependencyPath = projectDependency.getProjectDependencyPath(projectPath, settingsGradleBuildGradleExt);
					if (FileSystem.isDirectory(projectDependencyPath)) {
						resolvingChildProject(projectDependencyPath, resolvedProjects);
					}
				}
			}
		}

	}
	static BuildGradle buildGradle = ZeroAicyExtensionInterface.getBuildGradle();
	static BuildGradleExt buildGradleExt = new BuildGradleExt();

	public static List<BuildGradle.Dependency> getProjectDependencies(String projectPath) {
		String buildGradlePath = GradleTools.getBuildGradlePath(projectPath);
		if (!FileSystem.isFileAndNotZip(buildGradlePath)) {
			return Collections.emptyList();
		}
		BuildGradle projectBuildGradle = buildGradle.getConfiguration(buildGradlePath);

		// 获得上一级目录的build.gradle
		String lastBuildGradlePath = GradleTools.getLastBuildGradlePath(projectPath);

		if (FileSystem.isFileAndNotZip(lastBuildGradlePath)) {
			// 添加全局依赖
			BuildGradle lastBuildGradle = buildGradle.getConfiguration(lastBuildGradlePath);
			if (lastBuildGradle.subProjectsDependencies.size() > 0 
				|| lastBuildGradle.allProjectsDependencies.size() > 0) {

				List<BuildGradle.Dependency> dependencies = new ArrayList<>();
				for (BuildGradle.Dependency dependency : lastBuildGradle.subProjectsDependencies) {
					if (dependency instanceof BuildGradle.MavenDependency) {
						dependencies.add(dependency);
					}
				}
				for (BuildGradle.Dependency dependency2 : lastBuildGradle.allProjectsDependencies) {
					if (dependency2 instanceof BuildGradle.MavenDependency) {
						dependencies.add(dependency2);
					}
				}
				dependencies.addAll(projectBuildGradle.dependencies);
				return dependencies;

			}
		}
		return projectBuildGradle.dependencies;
	}

	private static void fillSubProjectDependency(String projectPath, Map<String, List<String>> subProjectMap) {

		List<String> dependencys = subProjectMap.get(projectPath);

		if (GradleTools.isAarEexplodedPath(projectPath)) {
			// Java项目不支持aar依赖
			// 应该报错的
			return;
		}

		if (isJavaGradleProject(projectPath)) {


			List<Dependency> projectDependencies = getProjectDependencies(projectPath);
			for (Dependency dependency : projectDependencies) {
				if (dependency instanceof BuildGradle.MavenDependency) {
					BuildGradle.MavenDependency mavenDependency = ( BuildGradle.MavenDependency)dependency;
					for (String libFilePath : ServiceContainer.getMavenService().resolveFullDependencyTree(null, mavenDependency)) {
						if (!dependencys.contains(libFilePath)
							&& !projectPath.equals(libFilePath)
							&& libFilePath.endsWith(".jar")) {
							// 添加依赖
							dependencys.add(libFilePath);
						}
					}
					continue;
				}
				// 处理子项目的依赖
				if (dependency instanceof BuildGradle.ProjectDependency) {
					BuildGradle.ProjectDependency projectDependency = (BuildGradle.ProjectDependency)dependency;

					String settingsGradlePath = GradleTools.getSettingsGradlePath(projectPath);
					String projectDependencyPath = projectDependency.getProjectDependencyPath(projectPath, buildGradleExt.getConfiguration(settingsGradlePath));
					if (FileSystem.isDirectory(projectDependencyPath) 
						&& !dependencys.contains(projectDependencyPath)) {
						// 将子子项目路径添加到集合
						dependencys.add(projectDependencyPath);
					}
				}
			}
		}
	}
	

	private void preResolving(String projectPath) {

	}

	/**
	 * 是否必须Premium版才可用
	 * 这个功能免费😂
	 */
	/**
	 * 此ProjectSupport是否付费版才能使用
	 */
	@Override
	public boolean isPremium() {
		return false;
	}


	/**
	 * 查询依赖是否包含文件路径
	 */
	@Override
	public boolean containJarLib(String path) {

		return false;
	}
	/**
	 * 添加依赖
	 * 需要向build.gradle添加
	 */
	@Override
	public void addJarLib(String string) {

	}


	/**
	 * 验证此 ProjectSupport需要的资源有无需要下载
	 * 比如 C/Cpp项目的Ndk
	 * Gradle项目的maven依赖
	 */
	@Override
	public boolean verifyResourcesDownload() {

		return false;
	}

	@Override
	public boolean J8() {

		return false;
	}

	@Override
	public void Mr() {

	}


	@Override
	public void P8(String string, String string1) {

	}


	@Override
	public void SI(String string, ValueRunnable<String> valueRunnable) {

	}

	/**
	 * 模板
	 */
	@Override
	public TemplateService.TemplateGroup[] getTemplateGroups() {
		return new TemplateService.TemplateGroup[0];
	}

	/**
	 * 返回EngineSolution
	 * 创建EngineSolution，用于代码分析进程处理依赖
	 */
	@Keep
	public EngineSolution makeEngineSolution() {
		// 向代码分析进程填充项目信息
		return null;
	}

	/**
	 * 教程
	 */
	@Override
	public List<com.aide.ui.trainer.Course.File> getTrainerCourses() {
		return Collections.emptyList();
	}

	@Override
	public boolean Zo(String string) {
		return false;
	}

	@Override
	public boolean a8(String string) {
		return false;
	}

	@Override
	public boolean isInCurrentProjectDirectory(String string) {
		return false;
	}


	@Override
	public void cn(List<String> list, boolean p) {
	}

	@Override
	public void ei(String string) {

	}

	/**
	 * 安卓项目-判断依据 项目目录 src文件夹存在 build.gradle存在
	 * 或者 AndroidManifest.xml存在
	 * Java项目-判断依据 项目目录 .classpath文件存在
	 *
	 * 是否是支持此项目
	 * 此项目不支持渠道包 ？
	 * 
	 */
	@Override
	public boolean isSupport(String projectPath) {
		return isJavaGradleProject(projectPath);
	}


	@Override
	public boolean gW() {
		return false;
	}

	/*
	 * 渠道包
	 */
	@Override
	public List<String> getProductFlavors(String path) {
		return null;
	}

	@Override
	public void gn() {

	}

	@Override
	public boolean isVersionSupport(String string) {
		return false;
	}

	@Override
	public void j6() {
	}

	@Override
	public boolean lg() {
		return false;
	}

	@Override
	public void nw(String string) {
	}

	@Override
	public int rN(String string) {
		return 0;
	}

	// 添加到项目建议
	@Override
	public List<String> getAddToProjectAdvise(String string) {
		return null;
	}

	@Override
	public String sh(String string) {
		return null;
	}

	@Override
	public String tp(String string) {
		return null;
	}

	@Override
	public boolean u7(String string) {
		return false;
	}

	@Override
	public String v5(String string) {
		return null;
	}

	@Override
	public boolean vy(String string) {
		return false;
	}

	@Override
	public int we(String string) {
		return 0;
	}

	@Override
	public String getProjectPackageName() {
		return null;
	}

}
