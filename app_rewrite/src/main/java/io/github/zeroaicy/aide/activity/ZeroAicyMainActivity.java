package io.github.zeroaicy.aide.activity;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupMenu;
import android.widget.Toolbar;
import com.aide.common.AndroidHelper;
import com.aide.ui.App;
import com.aide.ui.MainActivity;
import com.aide.ui.rewrite.R;
import com.hjq.permissions.XXPermissions;
import io.github.zeroaicy.aide.extend.OpenAideTermux;
import io.github.zeroaicy.aide.preference.ZeroAicyPreferencesActivity;
import io.github.zeroaicy.aide.preference.ZeroAicySetting;
import io.github.zeroaicy.aide.shizuku.ShizukuUtil;
import io.github.zeroaicy.util.Log;
import io.github.zeroaicy.util.reflect.ReflectPie;
import java.io.File;
import java.lang.ref.WeakReference;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import com.aide.ui.util.FileSystem;
import android.os.Build;
import android.webkit.MimeTypeMap;
import android.net.Uri;
import abcd.iy;
import android.content.ActivityNotFoundException;
import android.widget.Toast;
import com.aide.ui.util.FileSpan;
public class ZeroAicyMainActivity extends MainActivity {



	@Override
	public void onCreate(Bundle bundle){
		super.onCreate(bundle);

		//初始化Shizuku库
		ShizukuUtil.initialized(this);

		XXPermissions.with(this).
			permission(android.Manifest.permission.MANAGE_EXTERNAL_STORAGE)
			.request(null);
	}

	@Override
	public void onDestroy(){
		super.onDestroy();
		//移除注册的监听器
		ShizukuUtil.removeBinderListener();
	}

	//@Override
	public void setHasEmbeddedTabs(){
		if ( ZeroAicySetting.enableActionBarSpinner() ){
			AndroidHelper.ei(this, true);			
		}
		else{
			AndroidHelper.ei(this, false);
		}
		//绑定监听器
		AndroidHelper.nw(this);
	}


	//当前屏幕的高度
	public static float Zo(Context context){
        try{
            return ((WindowManager) context.getSystemService("window")).getDefaultDisplay().getHeight() / context.getResources().getDisplayMetrics().density;
        }
		catch (Throwable t){
			throw new RuntimeException(t);
        }
    }
	//可见屏幕的高度
	public static float Ws(Context context){
        try{
            Activity activity = (Activity) context;
            float f = activity.getResources().getDisplayMetrics().density;
            Rect rect = new Rect();
            activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
            return (rect.bottom - rect.top) / f;
        }
		catch (Throwable e){
			throw new RuntimeException(e);
        }
    }
	@Override
	public void FH(boolean z){
		App.DW().u7(!z);
		q7();
		if ( z ){
            boolean isLandscape = isLandscape();
			if ( isLandscape && ((com.aide.common.AndroidHelper.Zo(this) > 800.0f || br().isHorizontal() && com.aide.common.AndroidHelper.Zo(this) >= 540.0f)) ){
				return;
			}
			Ws(false);
		}
	}
	//是否横屏
	private boolean isLandscape(){
		boolean isLandscape = false;
		Configuration configuration = getResources().getConfiguration();
		if ( configuration.orientation == Configuration.ORIENTATION_LANDSCAPE ){
			// 设备处于横屏模式下
			isLandscape = true;
		}
		return isLandscape;
	}

	/*@Override
	 public void Ws(boolean p){
	 System.out.println("Ws(方法): " + (p ? "打开" : "关闭"));
	 //Log.printlnStack();
	 super.Ws(p);
	 }*/
	@Override
	public void IS(int i){
		//拦截并替换设置PreferencesActivity
		ZeroAicyPreferencesActivity.DW(this, i);
    }

	public void qp(String str){
		String suffixName = FileSystem.XL(str);
		String mimeTypeFromExtension = MimeTypeMap.getSingleton().getMimeTypeFromExtension(suffixName);
		
		if ( !suffixName.equals("java") 
			&& !suffixName.equals("class") 
			&& !suffixName.equals("xml") 
			&& !suffixName.equals("svg") 
			&& mimeTypeFromExtension != null 
			&& !mimeTypeFromExtension.startsWith("text") ){
				
			if ( Build.VERSION.SDK_INT < 24 ){
				Intent intent = new Intent();
				intent.setAction("android.intent.action.VIEW");
				intent.setDataAndType(Uri.fromFile(new File(str)), mimeTypeFromExtension);
				try{
					gn(this, intent);
					startActivity(intent);
					iy.BT(this, intent);
					return;
				}
				catch (ActivityNotFoundException unused){
					Context VH = App.VH();
					Toast.makeText(VH, "No handler found for type " + mimeTypeFromExtension, 0).show();
					return;
				}
			}
			
			return;
		}
		if ( FileSystem.ei(str) ){
			return;
		}
		aq(new FileSpan(str, 1, 1, 1, 1));
		App.P8().VH(str);
        
    }
	private static void gn(Object obj, Intent intent){
        ((MainActivity) obj).startActivity(intent);
        iy.BT(obj, intent);
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu){
		return super.onCreateOptionsMenu(menu);
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem){
		if ( handleOptionsItemSelected(menuItem) ){
            return true;
        }
		return super.onOptionsItemSelected(menuItem);
	}



	//命令
	private static final String gradle_cmd_line_extra = "gradle_cmd_line_extra";
	//工作目录
	private static final String work_dir_extra = "work_dir_extra";

	private boolean handleOptionsItemSelected(MenuItem menuItem){
		int itemId = menuItem.getItemId();
		if ( itemId == R.id.mainMenuSettings ){
			startActivity(new Intent(this, ZeroAicyPreferencesActivity.class).putExtra("from_main", true));
			return true;
		}
		if ( itemId == R.id.mainMenuRunGradle ){

			return handleRunGradle(menuItem);
		}
		return false;
	}

	private boolean handleRunGradle(MenuItem menuItem){
		showGradleBuildDialog(menuItem);
		return true;
	}

	private void showGradleBuildDialog(final MenuItem runMenuItem){
		String currentAppHome = ZeroAicySetting.getCurrentAppHome();
		final Map<CharSequence, String> itemNameMap = new LinkedHashMap<>();
		//只有是gradle项目才添加
		boolean hasGradlew = hasGradlew(currentAppHome);
		if ( hasGradlew ){
			itemNameMap.putAll(ZeroAicySetting.getCommands());
		}
		boolean isCN = getResources().getConfiguration().locale.equals(Locale.CHINA);
		itemNameMap.put(isCN ? "运行终端" : "Terminal", "terminal");

		PopupMenu popupMenu = new PopupMenu(this, findViewById(R.id.mainMenuRunGradle));
		Menu menu = popupMenu.getMenu();

		for ( final CharSequence itemName : itemNameMap.keySet() ){
			menu.add(itemName)
				.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener(){
					@Override
					public boolean onMenuItemClick(MenuItem _item){
						String cmdline = itemNameMap.get(itemName);

						Intent launchIntentForPackage = getPackageManager().getLaunchIntentForPackage("com.aide.termux");
						if ( launchIntentForPackage == null ){
							com.aide.common.MessageBox.BT(App.rN(), "运行错误", "AIDE-Termux未安装或找不到主Activity");
							return true;
						}


						String currentAppHome = ZeroAicySetting.getCurrentAppHome();
						File gradleProjectRootDir = new File(currentAppHome).getParentFile();
						launchIntentForPackage.putExtra(work_dir_extra, gradleProjectRootDir.getAbsolutePath());
						if ( cmdline.contains("gradle") ){
							if ( !hasGradlew(currentAppHome) ){
								com.aide.common.MessageBox.BT(App.rN(), "不是Gradle项目", "请保证项目目录下GradleWrapper(Gradle包装器)");
								return true;
							}
							launchIntentForPackage.putExtra(gradle_cmd_line_extra, cmdline);
						}
						startActivity(launchIntentForPackage);

						return true;
					}
				});
		}
		popupMenu.show();
	}

	private boolean hasGradlew(String currentAppHome){
		File gradleProjectRootDir = new File(currentAppHome).getParentFile();

		File gradlewFile = new File(gradleProjectRootDir, "gradlew");
		File gradleWrapperJarFile = new File(gradleProjectRootDir, "gradle/wrapper/gradle-wrapper.jar");
		File gradleWrapperProperties = new File(gradleProjectRootDir, "gradle/wrapper/gradle-wrapper.properties");

		return gradlewFile.isFile()
			&& gradleWrapperJarFile.isFile()
			&& gradleWrapperProperties.isFile();
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu){
		RepairBUG1(menu);
		if ( !com.aide.ui.App.Mz() ){
			RepairBUG1(menu);
			RepairBUG2(menu);
		}
		return super.onPrepareOptionsMenu(menu);
	}


	//查找Toolbar
	public static Toolbar findToolbarByMenu(Menu mMenu){
		try{
            if ( mMenu == null ){
				//实例是 MenuItemImpl
                return null;
            }
			CopyOnWriteArrayList<WeakReference> mPresenters = ReflectPie.on(mMenu).get("mPresenters");
            for ( WeakReference ref : mPresenters ){
                final Object presenter = ref.get();
                if ( presenter == null ){
                    mPresenters.remove(ref);
                }
				else if ( presenter.getClass().getName().contains("Toolbar$") ){
					Toolbar mToolbar = ReflectPie.on(presenter).get("this$0");
                    return mToolbar;
                }
            }
        }
		catch ( Exception e){
            e.printStackTrace(System.out);
        }
		return null;
	}
	//Menu clear修复
	public static void RepairBUG1(Menu mMenu){
        try{
            if ( mMenu == null ){
                return;
            }
			Toolbar mToolbar = findToolbarByMenu(mMenu);
			RepairCollapseActionView(mToolbar);//修复
        }
		catch ( Exception e){
            e.printStackTrace(System.out);
        }
    }
	//mCollapseButtonView 修复
	public static void RepairBUG2(Menu mMenu){
        try{
			final Toolbar mToolbar = findToolbarByMenu(mMenu);
			if ( mToolbar == null ){
				return;
			}
			View mCollapseButtonView = ReflectPie.on(mToolbar).get("mCollapseButtonView");
			if ( mCollapseButtonView != null ){
				mCollapseButtonView.setOnClickListener(new View.OnClickListener(){
						@Override
						public void onClick(View view){
							try{
								RepairCollapseActionView(mToolbar);//修复
								mToolbar.collapseActionView();
							}
							catch ( Exception e){
								e.printStackTrace(System.out);
							}
						}
					});
			}
        }
		catch ( Exception e){
            e.printStackTrace(System.out);
        }
	}

	//修复 collapseActionView方法
	public static void RepairCollapseActionView(Toolbar mToolbar){
        try{
            if ( mToolbar == null ){
                return;
            }
			List<View> mHiddenViews = ReflectPie.on(mToolbar).get("mHiddenViews");
			if ( mHiddenViews == null ){
				return;
			}
			for ( View view : mHiddenViews ){
				ViewGroup parent = (ViewGroup) view.getParent();
				if ( parent != null ){
					Log.d("RepairBUG", "移除Parent->" + view);
					parent.removeView(view);
				}
			}
		}
		catch ( Exception e){
            e.printStackTrace(System.out);
        }
	}
}
