//
// Decompiled by Jadx - 529ms
//
package com.aide.ui.browsers;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.aide.common.ListAdapterBase;
import com.aide.ui.browsers.FileBrowser;
import com.aide.ui.rewrite.R;

class FileBrowser$g extends ListAdapterBase<FileBrowser$h> {

    final FileBrowser Hw;

    private FileBrowser$g(FileBrowser fileBrowser) {
		this.Hw = fileBrowser;
    }

    public View getView(int i, View converView, ViewGroup viewGroup) {
		final ViewHolder holder;
		if (converView == null) {
			converView = LayoutInflater.from(this.Hw.getContext()).inflate(R.layout.filebrowser_entry, viewGroup, false);
			holder = new ViewHolder(converView);
		}else{
			holder = (FileBrowser$g.ViewHolder) converView.getTag();
		}
		holder.updateData(j6(i));
		return converView;
    }

    FileBrowser$g(FileBrowser fileBrowser, FileBrowser.a aVar) {
        this(fileBrowser);
    }
	
	public static class ViewHolder {
		View converView;
		TextView filebrowserEntryName;
		ImageView filebrowserEntryFileImage;
		public ViewHolder(View converView) {
			this.converView = converView;
			converView.setTag(this);
			filebrowserEntryName = converView.findViewById(R.id.filebrowserEntryName);
			filebrowserEntryFileImage = converView.findViewById(R.id.filebrowserEntryFileImage);
		}
		public void updateData(FileBrowser$h hVar) {
			
			filebrowserEntryName.setText(hVar.j6);
			filebrowserEntryFileImage.setImageResource(hVar.v5);
		}
	}
}

