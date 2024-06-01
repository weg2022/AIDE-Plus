package io.github.zeroaicy.aide.utils;

import abcd.*;

import com.aide.common.KeyStroke;
import com.aide.common.MessageBox;
import com.aide.ui.util.FileSystem;
import com.aide.ui.rewrite.R;
import com.aide.common.ValueRunnable;
import java.io.File;
import com.aide.ui.ServiceContainer;

public class FilesRenameMenu extends zb {

		public FilesRenameMenu() {}

		public boolean DW(boolean z) {
			String path = ServiceContainer.getFileBrowserService().FH();
			
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
			final String FH = ServiceContainer.getFileBrowserService().FH();
			MessageBox.XL(ServiceContainer.getMainActivity(), R.string.dialog_rename_title, R.string.dialog_rename_message, FileSystem.getName(FH), new ValueRunnable<String>(){
					@Override
					public void j6(String t){
						ServiceContainer.getMainActivity().w9();
						ServiceContainer.getEngineService().SI(FH, t);
					}
				});
			return true;
		}

		public KeyStroke v5() {
			return new KeyStroke(46, false, true, false);
	}
	
}
