package com.aide.ui.project;

import abcd.it;
import androidx.annotation.Keep;
import com.aide.common.ValueRunnable;
import com.aide.engine.EngineSolution;
import com.aide.ui.ServiceContainer;
import com.aide.ui.project.internal.GradleTools;
import com.aide.ui.services.ProjectSupport;
import com.aide.ui.services.TemplateService;
import com.aide.ui.util.BuildGradle;
import com.aide.ui.util.BuildGradleExt;
import com.aide.ui.util.FileSystem;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import io.github.zeroaicy.aide.extend.ZeroAicyExtensionInterface;
import java.util.ArrayList;

/**
 * Java项目使用Gradle作为依赖管理
 * 项目根目录包含 build.gradle [插件使用java]
 * 且src/main目录下，没有AndroidManifest.xml
 * 必须优先AndroidProjectSupport判断*
 */
public class JavaGradleProjectSupport implements ProjectSupport{
	/**
	 * 用于判断是否是Java项目
	 * 
	 */
	private static boolean isJavaGradleProject(String projectPath){
		// 必须有 src build.gradle 
		// 后期添加对 apply plugin的判断 用BuildGradle解析并缓存，不用每次都解析
		return GradleTools.isGradleProject(projectPath) 
		&& !GradleTools.isAndroidProject(projectPath)
		&& !GradleTools.isAndroidProject(projectPath + "/src/main" ) ;
	}

	
	
	@Override
	public void buildProject(boolean p){
		// TODO: Implement this method
	}

	@Override
	public int getOpenProjectNameStringId(String string){
		// TODO: Implement this method
		return 0;
	}

	@Override
	public String getProjectAttributeHtmlString(){
		// TODO: Implement this method
		return null;
	}






	/**
	 * 
	 */
	@Override
	public void init(String projectPath, Map<String, List<String>> subProjectMap, List<String> projectPaths){
		// 建立项目与其依赖项

		// 清除maven解析缓存
		ServiceContainer.getMavenService().resetDepMap();

		preResolving(projectPath);

		//添加主项目[一般来说就一个，除非是wearApp那种]

		projectPaths.add(projectPath);

	}
	
	/**
	 * 处理一个项目的子项目
	 */
	public void resolvingChildProject(String projectPath, Set<String> resolvedProjects){
		// AIDE是 resolvedProjects.contains(resolvedProjects)
		// 这应该是防止循环依赖的
		if (resolvedProjects.contains(projectPath)) {
			return;
		}
		
		resolvedProjects.add(projectPath);
		
		if( isJavaGradleProject(projectPath)){
			
		}
		
	}
	private static List<BuildGradle.Dependency> getProjectMavenDependencyList(String str) throws Throwable {
        try {
            BuildGradle configuration = ZeroAicyExtensionInterface.getBuildGradle().getConfiguration(GradleTools.getBuildGradlePath(str));
            String lastBuildGradlePath = GradleTools.getLastBuildGradlePath(str);
            if (FileSystem.isFileAndNotZip(lastBuildGradlePath)) {
                BuildGradle configuration2 = ZeroAicyExtensionInterface.getBuildGradle().getConfiguration(lastBuildGradlePath);
                if (configuration2.subProjectsDependencies.size() > 0 || configuration2.allProjectsDependencies.size() > 0) {
                    ArrayList<BuildGradle.Dependency> arrayList = new ArrayList<>();
                    for (BuildGradle.Dependency dependency : configuration2.subProjectsDependencies) {
                        if (dependency instanceof BuildGradle.MavenDependency) {
                            arrayList.add(dependency);
                        }
                    }
                    for (BuildGradle.Dependency dependency2 : configuration2.allProjectsDependencies) {
                        if (dependency2 instanceof BuildGradle.MavenDependency) {
                            arrayList.add(dependency2);
                        }
                    }
                    arrayList.addAll(ZeroAicyExtensionInterface.getFlavorDependencies(configuration));
                    return arrayList;
                }
            }
            // 实现渠道包依赖
            return ZeroAicyExtensionInterface.getFlavorDependencies(configuration);
        } catch (Throwable th) {
            throw th;
        }
    }
	
	/*
	
	private void ca(String str, HashSet<String> hashSet){

		hashSet.add(str);
		if ( GradleTools.isGradleProject(str) ){
			Iterator<BuildGradle.Dependency> it = getProjectMavenDependencyList(str).iterator();
			while ( it.hasNext() ){
				BuildGradle.MavenDependency mavenDependency = (BuildGradle.Dependency) it.next();
				if ( mavenDependency instanceof BuildGradle.MavenDependency ){
					ServiceContainer.getMavenService().resolvingMavenDependency(mavenDependency);
				}
			}
			Iterator<BuildGradle.Dependency> it2 = getProjectMavenDependencyList(str).iterator();
			while ( it2.hasNext() ){
				BuildGradle.ProjectDependency projectDependency = (BuildGradle.Dependency) it2.next();
				if ( projectDependency instanceof BuildGradle.ProjectDependency ){
					String projectDependencyPath = projectDependency.getProjectDependencyPath(str, new BuildGradleExt().getConfiguration(GradleTools.getSettingsGradlePath(str)));
					if ( FileSystem.isDirectory(projectDependencyPath) ){
						ca(projectDependencyPath, hashSet);
					}
				}
			}
		}
	}
	/**
	 * 提前
	 */
	private void preResolving(String projectPath){

	}

	/**
	 * 是否必须Premium版才可用
	 * 这个功能免费😂
	 */
	/**
	 * 此ProjectSupport是否付费版才能使用
	 */
	@Override
	public boolean isPremium(){
		return false;
	}


	/**
	 * 查询依赖是否包含文件路径
	 */
	@Override
	public boolean containJarLib(String path){

		return false;
	}
	/**
	 * 添加依赖
	 * 需要向build.gradle添加
	 */
	@Override
	public void addJarLib(String string){

	}


	/**
	 * 验证此 ProjectSupport需要的资源有无需要下载
	 * 比如 C/Cpp项目的Ndk
	 * Gradle项目的maven依赖
	 */
	@Override
	public boolean verifyResourcesDownload(){

		return false;
	}

	@Override
	public boolean J8(){

		return false;
	}

	@Override
	public void Mr(){

	}


	@Override
	public void P8(String string, String string1){

	}


	@Override
	public void SI(String string, ValueRunnable<String> valueRunnable){

	}

	/**
	 * 模板
	 */
	@Override
	public TemplateService.TemplateGroup[] getTemplateGroups(){
		return new TemplateService.TemplateGroup[0];
	}

	/**
	 * 返回EngineSolution
	 * 创建EngineSolution，用于代码分析进程处理依赖
	 */
	@Keep
	public EngineSolution makeEngineSolution(){
		// 向代码分析进程填充项目信息
		return null;
	}

	/**
	 * 教程
	 */
	@Override
	public List<com.aide.ui.trainer.Course.File> getTrainerCourses(){
		return Collections.emptyList();
	}

	@Override
	public boolean Zo(String string){
		return false;
	}

	@Override
	public boolean a8(String string){
		return false;
	}

	@Override
	public boolean isInCurrentProjectDirectory(String string){
		return false;
	}


	@Override
	public void cn(List<String> list, boolean p){
	}

	@Override
	public void ei(String string){

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
	public boolean isSupport(String projectPath){

		return false;
	}


	@Override
	public boolean gW(){
		return false;
	}

	/*
	 * 渠道包
	 */
	@Override
	public List<String> getProductFlavors(String path){
		return null;
	}

	@Override
	public void gn(){

	}

	@Override
	public boolean isVersionSupport(String string){
		return false;
	}

	@Override
	public void j6(){
	}

	@Override
	public boolean lg(){
		return false;
	}

	@Override
	public void nw(String string){
	}

	@Override
	public int rN(String string){
		return 0;
	}

	// 添加到项目建议
	@Override
	public List<String> getAddToProjectAdvise(String string){
		return null;
	}

	@Override
	public String sh(String string){
		return null;
	}

	@Override
	public String tp(String string){
		return null;
	}

	@Override
	public boolean u7(String string){
		return false;
	}

	@Override
	public String v5(String string){
		return null;
	}

	@Override
	public boolean vy(String string){
		return false;
	}

	@Override
	public int we(String string){
		return 0;
	}

	@Override
	public String getProjectPackageName(){
		return null;
	}

}
