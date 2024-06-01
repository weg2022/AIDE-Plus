//
// Decompiled by Jadx - 671ms
//
package com.aide.ui.browsers;

import abcd.tf;
import com.aide.ui.AppFileIcons;
import com.aide.ui.rewrite.R;

class FileBrowser$h {
    public boolean DW;
    public String FH;
    public tf Hw;
	
    final FileBrowser Zo;
    public String j6;
    public int v5;

    public FileBrowser$h(FileBrowser fileBrowser, String str, String str2, boolean z) {
		this.Zo = fileBrowser;
		this.FH = str;
		this.j6 = str2;
		this.DW = z;
		if (z) {
			this.v5 = AppFileIcons.j6(str);
		} else if (DW()) {
			this.v5 = R.drawable.folder_open;
		} else if (FileBrowser.tp(str2)) {
			this.v5 = R.drawable.folder_hidden;
		} else {
			this.v5 = R.drawable.folder;
		}
    }

    public boolean DW() {
		if (!this.DW && this.j6.equals("..")) {
				return true;
		}
		return false;
    }

    public boolean j6() {
		
		if (!this.DW) {
			if (!DW()) {
				return true;
			}
		}
		return false;
    }

    public FileBrowser$h(FileBrowser fileBrowser, tf tfVar) {
		this.Zo = fileBrowser;
		this.Hw = tfVar;
		this.v5 = tfVar.VH();
		int j6 = tfVar.j6();
		if (j6 != 0) {
			this.j6 = fileBrowser.getContext().getResources().getString(j6);
		}
    }
}

