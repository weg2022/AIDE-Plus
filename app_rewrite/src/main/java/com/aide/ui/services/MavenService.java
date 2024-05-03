package com.aide.ui.services;

import com.aide.ui.util.BuildGradle;
import io.github.zeroaicy.aide.ui.services.ZeroAicyMavenService;
import java.util.List;
import java.util.Map;

/**
 * 所有在此类重复声明的函数
 * 是为了底包2.2做准备
 * 一旦更新2.2底包后，直接删除这些混淆的方法
 */
// 底包对MavenService的全部引用
public class MavenService extends ZeroAicyMavenService {
	// 仅一处引用且已被替换
	public final void nw(){
		this.refreshMavenCache();
	}
	
	// refreshing maven cache
	@Override
	public final void refreshMavenCache() {
		super.refreshMavenCache();
	}
	
	// resolvingDependency
	public final void v5(BuildGradle.MavenDependency dependency){
		this.resolvingDependency(dependency);
	}
	@Override
	public final void resolvingDependency(BuildGradle.MavenDependency dependency) {
		super.resolvingDependency(dependency);
	}

	// 计算并返回dependency在maven缓存仓库中的路径
	public final String u7(BuildGradle.MavenDependency dependency){
		return this.resolveMavenDepPath(dependency);
	}
	
	@Override
	public final String resolveMavenDepPath(BuildGradle.MavenDependency dependency) {
		return super.resolveMavenDepPath(dependency);
	}

	/**
	 * 从给定的依赖路径，返回其自己及子依赖路径
	 * 递归3层
	 */
	public final List<String> J0(String depPath){
		return this.resolveFullDependencyTree(depPath);
	}

	@Override
	public final List<String> resolveFullDependencyTree(String depPath) {
		return super.resolveFullDependencyTree(depPath);
	}

	// 解析dependency并返回依赖树集合
	// 会优先从flatRepositoryPath中查找
	public final List<String> J8(Map<String, String> flatRepositoryPathMap, BuildGradle.MavenDependency dependency){
		return this.resolveFullDependencyTree(flatRepositoryPathMap, dependency);
	}
	@Override
	public final List<String> resolveFullDependencyTree(Map<String, String> flatRepositoryPathMap, BuildGradle.MavenDependency dep) {
		return super.resolveFullDependencyTree(flatRepositoryPathMap, dep);
	}
	
	// 
	public final boolean BT(Map<String, String> flatRepoPathMap, BuildGradle.MavenDependency dependency){
		return this.existsLocalMavenCache(flatRepoPathMap, dependency);
	}

	@Override
	public final boolean existsLocalMavenCache(Map<String, String> flatRepoPathMap, BuildGradle.MavenDependency dependency) {
		return super.existsLocalMavenCache(flatRepoPathMap, dependency);
	}
	
	// 重置依赖在maven缓存路径中的映射
	public final void FH(){
		this.resetDepPathMap();
	}
	@Override
	public final void resetDepPathMap() {
		super.resetDepPathMap();
	}
	
	
	// resetDepMap
	public final void ei(){
		this.resetDepMap();
	}
	//重置当前服务的依赖记录
	@Override
	public final void resetDepMap() {
		super.resetDepMap();
	}
	
	
	// MavenService2.dependencyPathMap字段
	public static Map j6(MavenService mavenService){
		return getDependencyPathMap(mavenService);
	}

	public static Map getDependencyPathMap(MavenService mavenService) {
		return ZeroAicyMavenService.getDependencyPathMap(mavenService);
	}
	
	//  getDefaulRepositoriePath
	public static String DW(){
		return getDefaulRepositoriePath();
	}
	/**
	 * 默认下载maven仓库路径
	 */
	public static String getDefaulRepositoriePath() {
        return ZeroAicyMavenService.getDefaulRepositoriePath();
    }
	
	// getMetadataUrl
	public static String rN(BuildGradle.RemoteRepository remoteRepository, BuildGradle.MavenDependency dependency) {
		return getMetadataUrl(remoteRepository, dependency);
	}
	public static String getMetadataUrl(BuildGradle.RemoteRepository remoteRepository, BuildGradle.MavenDependency dependency) {
		return ZeroAicyMavenService.getMetadataUrl(remoteRepository, dependency);
	}
	
	// getMetadataPath
	public static String lg(BuildGradle.RemoteRepository remoteRepository, BuildGradle.MavenDependency dependency) {
		return getMetadataPath(remoteRepository, dependency);
	}
	public static String getMetadataPath(BuildGradle.RemoteRepository remoteRepository, BuildGradle.MavenDependency dependency) {
		return ZeroAicyMavenService.getMetadataPath(remoteRepository, dependency);
	}
	
	// getArtifactPath
	public static String XL(BuildGradle.RemoteRepository remoteRepository, BuildGradle.MavenDependency dependency, String version, String type) {
		return getArtifactPath(remoteRepository, dependency, version, type);
	}
	public static String getArtifactPath(BuildGradle.RemoteRepository remoteRepository, BuildGradle.MavenDependency dependency, String version, String type) {
		return ZeroAicyMavenService.getArtifactPath(remoteRepository, dependency, version, type);
	}
}
