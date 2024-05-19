package com.aide.ui.project;

import abcd.c0;
import abcd.fe;
import abcd.iy;
import com.aide.common.ValueRunnable;
import com.aide.engine.EngineSolution;
import com.aide.ui.App;
import com.aide.ui.build.BuildServiceCollect;
import com.aide.ui.project.internal.MakeJavaEngineSolution;
import com.aide.ui.services.ProjectSupport;
import com.aide.ui.services.TemplateService;
import com.aide.ui.trainer.c;
import com.aide.ui.util.ClassPath;
import com.aide.ui.util.FileSystem;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Collections;


public class JavaGradleProjectSupport extends JavaProjectSupport2 implements ProjectSupport {
	
	@Override
	public void DW(boolean p) {
		
	}
	int h;
	/**
	 * 是否必须Premium版才可用
	 * 这个功能免费😂
	 */
	// isPremium
	@Override
	public boolean EQ() {
		
		return false;
	}

	@Override
	public boolean FH(String string) {
		
		return false;
	}

	@Override
	public boolean Hw() {
		
		return false;
	}

	@Override
	public void J0(String string) {
		
	}

	@Override
	public boolean J8() {
		
		return false;
	}

	@Override
	public int KD(String string) {
		
		return 0;
	}

	@Override
	public void Mr() {
		
	}

	@Override
	public void P8(String string, String string1) {
		
	}

	@Override
	public String QX() {
		
		return null;
	}

	@Override
	public void SI(String string, ValueRunnable<String> valueRunnable) {
		
	}


	/**
	 * openProject
	 */
	// openProject
	@Override
	public void U2(String projectPath, Map<String, List<String>> subProjectMap, List<String> projectPaths) {
		openProject(projectPath, subProjectMap, projectPaths);
	}

	private void openProject(String projectPath, Map<String, List<String>> subProjectMap, List<String> projectPaths) {
		super.U2(projectPath, subProjectMap, projectPaths);
	}

	/**
	 * 模板
	 */
	 
	@Override
	public TemplateService.TemplateGroup[] VH() {
		return new TemplateService.TemplateGroup[0];
	}

	/**
	 * 返回EngineSolution
	 */
	@Override
	public EngineSolution Ws() {
		return makeEngineSolution();
	}
	private EngineSolution makeEngineSolution() {
		return null;
	}

	@Override
	public List<com.aide.ui.trainer.c.c> XL() {
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
	public boolean aM(String string) {
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
	public boolean er(String projectPath) {
		return isSupport(projectPath);
	}

	private boolean isSupport(String projectPath) {
		return false;
	}

	@Override
	public boolean gW() {
		return false;
	}

	/*
	 * 可以支持，渠道包
	 */
	@Override
	public List<String> getProductFlavors(String path) {
		return null;
	}

	@Override
	public void gn() {

	}

	@Override
	public boolean j3(String string) {
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

	@Override
	public List<String> ro(String string) {
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
	public String yS() {
		return null;
	}

}
