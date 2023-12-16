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
			String FH = App.Ws().FH();
			
			if (FH != null) {
				if( new File(FH).exists()){
					return true;
				}
				if (!FileSystem.sh(FH)) {
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
			final String FH = App.Ws().FH();
			MessageBox.XL(App.rN(), R.string.dialog_rename_title, R.string.dialog_rename_message, FileSystem.er(FH), new ValueRunnable<String>(){
					@Override
					public void j6(String t){
						App.rN().w9();
						App.we().SI(FH, t);
					}
				});
			return true;
		}

		public KeyStroke v5() {
			return new KeyStroke(46, false, true, false);
	}
	
}
