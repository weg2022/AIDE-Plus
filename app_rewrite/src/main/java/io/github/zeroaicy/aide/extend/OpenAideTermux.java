package io.github.zeroaicy.aide.extend;

import android.content.Context;
import android.content.Intent;
import com.aide.common.KeyStroke;
import com.aide.ui.App;
import com.aide.ui.rewrite.R;
import java.io.File;

public class OpenAideTermux implements abcd.fg{
	
	//@Override
	public String getName() {
		return "在当前目录打开终端";
	}

	//@Override
	public KeyStroke v5() {
		//不支持快捷方式，但是
		return new KeyStroke('\256', true, true, true);
	}
	
	//命令
	private static final String gradle_cmd_line_extra = "gradle_cmd_line_extra";
	//工作目录
	private static final String work_dir_extra = "work_dir_extra";
	
	private static final OpenAideTermux singleton = new OpenAideTermux();
	public static OpenAideTermux getSingleton() {
		return singleton;
	}

	@Override
	public int FH() {
		return R.id.filebrowserMenuOpenAideTermux;
	}

	@Override
	public boolean isEnabled() {
		String currentFilePath;
		if( App.Ws().FH() != null){
			//长按文件得到了文件
			currentFilePath = App.Ws().FH();
		}else{
			//当前编辑器中的文件
			currentFilePath = com.aide.ui.App.yS().u7();
		}
		
		if (currentFilePath == null) {
			return false;
		}
		
		File currentFile = new File(currentFilePath);
		if( currentFile.isDirectory()){
			//是文件夹直接返回
			return true;
		}
		//不是路径看看父目录是不是文件夹
		File currentFileParentFile = currentFile.getParentFile();
		if (currentFileParentFile.isDirectory()) {
			return true;
		}
		
		return false;
	}

	@Override
	public boolean run() {
		File currentFile;
		if( App.Ws().FH() != null){
			//长按文件得到了文件
			currentFile = new File(App.Ws().FH());
		}else{
			//当前编辑器中的文件
			String currentFilePath = com.aide.ui.App.yS().u7();
			if ( currentFilePath == null ) {
				return false;
			}
			currentFile = new File(currentFilePath);
		}
		//确保打开的是文件夹
		if( !currentFile.isDirectory()) {
			//不是文件夹，查看父目录是不是文件夹
			File currentFileParentFile = currentFile.getParentFile();
			if (currentFileParentFile.isDirectory()) {
				currentFile = currentFileParentFile;
			}else{
				return true;
			}
		}
		Context context = App.VH();
		Intent launchIntentForPackage = context.getPackageManager().getLaunchIntentForPackage("com.aide.termux");
		if( launchIntentForPackage == null){
			com.aide.common.MessageBox.BT(App.rN(), "运行错误", "AIDE-Termux未安装或找不到主Activity");
			return true;
		}
		launchIntentForPackage.putExtra(work_dir_extra, currentFile.getAbsolutePath());
		context.startActivity(launchIntentForPackage);
		return true;
	}

	@Override
	public boolean DW(boolean p) {
		if (isEnabled()) {
			if (!"com.aide.web".equals(App.P8)) {
				return true;
			}
		}return false;
	}
	
	
}
