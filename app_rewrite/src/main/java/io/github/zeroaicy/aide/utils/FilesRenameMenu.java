package io.github.zeroaicy.aide.utils;

import abcd.*;

import com.aide.common.KeyStroke;
import com.aide.common.MessageBox;
import com.aide.ui.App;
import com.aide.ui.util.FileSystem;
import com.aide.ui.rewrite.R;
import com.aide.common.ValueRunnable;
import java.io.File;

public class FilesRenameMenu extends zb {

		public FilesRenameMenu() {}

		public boolean DW(boolean z) {
			String path = App.getFileBrowserService().FH();
			
			if (path != null) {
				if( new File(path).exists()){
					return true;
				}
				if (!FileSystem.sh(path)) {
					return true;
				}
			}
			return false;
		}

		public int FH() {
			return R.id.filebrowserMenuRename;
		}

		public String getName() {
			return "Files - Rename";
		}

		public boolean isEnabled() {
			return DW(false);
		}

		public boolean run() {
			final String FH = App.getFileBrowserService().FH();
			MessageBox.XL(App.getMainActivity(), R.string.dialog_rename_title, R.string.dialog_rename_message, FileSystem.getName(FH), new ValueRunnable<String>(){
					@Override
					public void j6(String t){
						App.getMainActivity().w9();
						App.we().SI(FH, t);
					}
				});
			return true;
		}

		public KeyStroke v5() {
			return new KeyStroke(46, false, true, false);
	}
	
}
