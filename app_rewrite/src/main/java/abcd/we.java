package abcd;

import android.content.SharedPreferences;
import com.aide.ui.App;
import com.aide.ui.util.FileSystem;
// we -> FileBrowserService
public class we {
    private String DW;
	private we$a j6;
	
    public String DW() {
		//多用户也用主内置储存
		return "/storage/emulated/0" + "/AppProjects";
    }

    public String FH() {
		ed.Zo(null,0);
		
		return this.DW;
	}

    public void Hw(String str) {
		SharedPreferences.Editor edit = App.getContext().getSharedPreferences("FileBrowserService", 0).edit();
		edit.putString("CurrentDir", str);
		edit.commit();
		v5();
    }

    public void VH(String str) {
		this.DW = str;
    }

    public void Zo(we$a aVar) {
		this.j6 = aVar;
    }

    public String j6() {
		String string = App.getContext().getSharedPreferences("FileBrowserService", 0).getString("CurrentDir", null);
		if (string == null || !FileSystem.SI(string)) {
			String DW = DW();
			FileSystem.g3(DW);
			return DW;
		}
		return string;
		
    }

    @ey(method = 910690012032294000L)
    public void v5() {
		if (this.j6 != null) {
			this.j6.j6();
		}
    }
}
