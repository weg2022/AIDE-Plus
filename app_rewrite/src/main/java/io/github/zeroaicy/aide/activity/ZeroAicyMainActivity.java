package io.github.zeroaicy.aide.activity;

import abcd.iy;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.PopupMenu;
import android.widget.Toast;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.legacy.app.ActionBarDrawerToggle;
import com.aide.common.AndroidHelper;
import com.aide.common.AppLog;
import com.aide.common.MessageBox;
import com.aide.ui.AppPreferences;
import com.aide.ui.MainActivity;
import com.aide.ui.PromoNotificationAlarmReceiver;
import com.aide.ui.ServiceContainer;
import com.aide.ui.ThemedActionbarActivity;
import com.aide.ui.activities.TrainerCourseActivity;
import com.aide.ui.firebase.FireBaseLogEvent;
import com.aide.ui.marketing.WhatsNewDialog;
import com.aide.ui.rewrite.R;
import com.aide.ui.util.FileSpan;
import com.aide.ui.util.FileSystem;
import com.aide.ui.views.SplitView;
import com.aide.ui.y;
import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.XXPermissions;
import io.github.zeroaicy.aide.extend.ZeroAicyExtensionInterface;
import io.github.zeroaicy.aide.preference.ZeroAicyPreferencesActivity;
import io.github.zeroaicy.aide.preference.ZeroAicySetting;
import io.github.zeroaicy.aide.ui.services.ThreadPoolService;
import io.github.zeroaicy.aide.ui.views.ZeroAicySplitView;
import io.github.zeroaicy.util.Log;
import io.github.zeroaicy.util.reflect.ReflectPie;
import java.io.File;
import java.lang.ref.WeakReference;
import java.util.GregorianCalendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class ZeroAicyMainActivity extends MainActivity {


	private static final String TAG_15255570984065567 = "ZeroAicyMainActivity";

	static ZeroAicyExtensionInterface zeroAicyExtensionInterface;
	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		// 隐藏Home键
		getActionBar().setDisplayShowHomeEnabled(false);

		if (enableActionDrawerLayout()) {
			setUpDrawerLayout();
		}
		
		if (!ZeroAicySetting.isWatch()) {
			// 检查并申请管理外部储存权限
			showRequestManageExternalStorage();
		}
	}

	/**
	 * 是否启用DrawerLayout
	 */
	private boolean enableActionDrawerLayout() {
		return !ServiceContainer.isTrainerMode() 
			&& ZeroAicySetting.enableActionDrawerLayout();
	}


	public void DWGAsync() {
		super.DW();
	}
	@Override
	public void DW() {
		runOnUiThread(new Runnable(){
				@Override
				public void run() {
					DWGAsync();
				}
			});
	}


	public void eUAsync() {
		super.eU();
	}
	@Override
	public void eU() {
		runOnUiThread(new Runnable(){
				@Override
				public void run() {
					eUAsync();
				}
			});
	}
	
	// 必须在主线程
	@Override
	public void Hw(final String string) {
		if( ThreadPoolService.isUiThread()){
			super.Hw(string);
			return;
		}
		runOnUiThread(new Runnable(){
				@Override
				public void run() {
					HwAsync(string);
				}
			});
	}
	public void HwAsync(String string){
		super.Hw(string);
	}
	
	// mainMasterButton 点击回调
	//*
	@Override
	public void Nh() {
		if (enableActionDrawerLayout() && mDrawerLayout != null) {
			if (mDrawerLayout.isOpen()) {
				mDrawerLayout.closeDrawer(Gravity.LEFT);
			} else {
				mDrawerLayout.openDrawer(Gravity.LEFT);
			}
		} else {
			super.Nh();			
		}
	}
	//*/


	@Override
	public void setContentView(int layoutResID) {
		// 仅替换 R.layout.main
		if (layoutResID != R.layout.main) {
			super.setContentView(layoutResID);
		} else if (enableActionDrawerLayout()) {
			super.setContentView(R.layout.main_drawer);
		} else {
			super.setContentView(R.layout.main);
		}
	}

	@Override
	public void onBackPressed() {
		if (mDrawerLayout != null && mDrawerLayout.isOpen()) {
			mDrawerLayout.close();
			return;
		}
		super.onBackPressed();
	}


	private DrawerLayout mDrawerLayout;
	private ActionBarDrawerToggle mDrawerToggle;
	io.github.zeroaicy.aide.ui.views.ZeroAicySplitView zeroAicySplitView;
	public void setUpDrawerLayout() {

		this.mDrawerLayout = findViewById(R.id.mainDrawerLayout);

		this.getActionBar().setDisplayHomeAsUpEnabled(true);

        this.mDrawerToggle =
			new ActionBarDrawerToggle(this,
									  mDrawerLayout,
									  R.drawable.ic_drawer,
									  R.string.app_name,
									  R.string.app_name);
		this.mDrawerLayout.addDrawerListener(mDrawerToggle);
        this.mDrawerToggle.syncState();

		this.mDrawerLayout.setOnTouchListener(new View.OnTouchListener(){
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					if (event.getAction() == MotionEvent.ACTION_DOWN)
						mDrawerLayout.close();
					return true;
				}
			});

		this.mDrawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener(){
				@Override
				public void onDrawerClosed(View view) {
					mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

				}
				@Override
				public void onDrawerOpened(View view) {
					mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN);
				}
			});

		SplitView splitView = br();
		if (splitView instanceof ZeroAicySplitView) {
			zeroAicySplitView = (ZeroAicySplitView) splitView;
			// closeSplit
			zeroAicySplitView.closeSplit(false);

			// SplitView事件拦截器
			zeroAicySplitView.setOnSplitInterceptListener(new ZeroAicySplitView.OnSplitInterceptListener(){
					@Override
					public boolean closeSplit(boolean animator, Runnable animatorListenerAdapterRunable) {
						return true;
					}
					@Override
					public boolean openSplit(boolean isHorizontal, boolean animator) {
						mDrawerLayout.openDrawer(Gravity.LEFT);
						return true;
					}
				});
		}
	}
	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem) {
		if (menuItem.getItemId() == android.R.id.home 
			&& this.mDrawerToggle != null 
			&& this.mDrawerToggle.onOptionsItemSelected(menuItem)) {
			Nh();
            return true;
        } else if (handleOptionsItemSelected(menuItem)) {
            return true;
        } else {
            return super.onOptionsItemSelected(menuItem);
        }

	}




	/**
	 * 显示授权请求弹窗
	 */
	public void showRequestManageExternalStorage() {
		if (XXPermissions.isGranted(this, android.Manifest.permission.MANAGE_EXTERNAL_STORAGE)) {
			return;
		}
		String app_name = getString(R.string.app_name);
		String message = new StringBuilder("为了访问您设备上的文件，您需要手动为")
			.append(app_name)
			.append("授予「所有文件访问」权限，点击确认后进入设置界面，选择「")
			.append(app_name)
			.append("」并开启授权。")
			.toString();
		new AlertDialog.Builder(ZeroAicyMainActivity.this)
			.setTitle("授权请求")
			.setMessage(message)
			.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog, int which) {
					requestManageExternalStorage();
				}
			})
			.show();
	}

	/**
	 * 申请 所有文件访问权限
	 */
	public void requestManageExternalStorage() {
		XXPermissions.with(this).
			permission(android.Manifest.permission.MANAGE_EXTERNAL_STORAGE)
			.request(new OnPermissionCallback(){
				public void onDenied(List<String> permissions, boolean doNotAskAgain) {
					showRequestManageExternalStorage();
				}
				@Override
				public void onGranted(List<String> list, boolean p) {}
			});
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void setHasEmbeddedTabs() {
		//ServiceContainer.Mz() && AndroidHelper.u7(this) <= 610.0f
		AndroidHelper.ei(this, ZeroAicySetting.enableActionBarSpinner() 
						 || (ServiceContainer.isTrainerMode() 
						 && AndroidHelper.u7(this) <= 610.0f));
		//绑定监听器
		AndroidHelper.nw(this);
	}


	//当前屏幕的高度
	public static float Zo(Context context) {
        try {
            return ((WindowManager) context.getSystemService("window")).getDefaultDisplay().getHeight() / context.getResources().getDisplayMetrics().density;
        }
		catch (Throwable t) {
			throw new RuntimeException(t);
        }
    }


	//可见屏幕的高度
	public static float Ws(Context context) {
        try {
            Activity activity = (Activity) context;
            float f = activity.getResources().getDisplayMetrics().density;
            Rect rect = new Rect();
            activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
            return (rect.bottom - rect.top) / f;
        }
		catch (Throwable e) {
			throw new RuntimeException(e);
        }
    }

	@Override
	public void FH(boolean z) {
		ServiceContainer.DW().u7(!z);
		q7();
		if (z) {
            boolean isLandscape = isLandscape();
			if (isLandscape && ((com.aide.common.AndroidHelper.Zo(this) > 800.0f || br().isHorizontal() && com.aide.common.AndroidHelper.Zo(this) >= 540.0f))) {
				return;
			}
			Ws(false);
		}
	}
	//是否横屏
	private boolean isLandscape() {
		boolean isLandscape = false;
		Configuration configuration = getResources().getConfiguration();
		if (configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			// 设备处于横屏模式下
			isLandscape = true;
		}
		return isLandscape;
	}

	@Override
	public void IS(int showPageIndex) {
		//拦截并替换设置PreferencesActivity
		ZeroAicyPreferencesActivity.DW(this, showPageIndex);
    }


	@Override
	public void qp(String str) {
		String suffixName = FileSystem.XL(str);
		String mimeTypeFromExtension = MimeTypeMap.getSingleton().getMimeTypeFromExtension(suffixName);

		if (!suffixName.equals("java") 
			&& !suffixName.equals("class") 
			&& !suffixName.equals("xml") 
			&& !suffixName.equals("svg") 
			&& mimeTypeFromExtension != null 
			&& !mimeTypeFromExtension.startsWith("text")) {

			if (Build.VERSION.SDK_INT < 24) {
				Intent intent = new Intent();
				intent.setAction("android.intent.action.VIEW");
				intent.setDataAndType(Uri.fromFile(new File(str)), mimeTypeFromExtension);
				try {
					gn(this, intent);
					startActivity(intent);
					iy.BT(this, intent);
					return;
				}
				catch (ActivityNotFoundException unused) {
					Context VH = ServiceContainer.getContext();
					Toast.makeText(VH, "No handler found for type " + mimeTypeFromExtension, 0).show();
					return;
				}
			}

			return;
		}
		if (FileSystem.ei(str)) {
			return;
		}
		aq(new FileSpan(str, 1, 1, 1, 1));
		ServiceContainer.getProjectService().VH(str);

    }

	private static void gn(Object obj, Intent intent) {
        ((MainActivity) obj).startActivity(intent);
        iy.BT(obj, intent);
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return super.onCreateOptionsMenu(menu);
	}


	//命令
	private static final String gradle_cmd_line_extra = "gradle_cmd_line_extra";
	//工作目录
	private static final String work_dir_extra = "work_dir_extra";

	private boolean handleOptionsItemSelected(MenuItem menuItem) {
		int itemId = menuItem.getItemId();
		if (itemId == R.id.mainMenuSettings) {
			startActivity(new Intent(this, ZeroAicyPreferencesActivity.class).putExtra("from_main", true));
			return true;
		}
		if (itemId == R.id.mainMenuRunGradle) {

			return handleRunGradle(menuItem);
		}
		return false;
	}

	private boolean handleRunGradle(MenuItem menuItem) {
		showGradleBuildDialog(menuItem);
		return true;
	}

	private void showGradleBuildDialog(final MenuItem runMenuItem) {
		String currentAppHome = ZeroAicySetting.getCurrentAppHome();
		final Map<CharSequence, String> itemNameMap = new LinkedHashMap<>();
		//只有是gradle项目才添加
		boolean hasGradlew = hasGradlew(currentAppHome);
		if (hasGradlew) {
			itemNameMap.putAll(ZeroAicySetting.getCommands());
		}
		boolean isCN = getResources().getConfiguration().locale.equals(Locale.CHINA);
		itemNameMap.put(isCN ? "运行终端" : "Terminal", "terminal");

		PopupMenu popupMenu = new PopupMenu(this, findViewById(R.id.mainMenuRunGradle));
		Menu menu = popupMenu.getMenu();

		for (final CharSequence itemName : itemNameMap.keySet()) {
			menu.add(itemName)
				.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener(){
					@Override
					public boolean onMenuItemClick(MenuItem _item) {
						String cmdline = itemNameMap.get(itemName);

						Intent launchIntentForPackage = getPackageManager().getLaunchIntentForPackage("com.aide.termux");
						if (launchIntentForPackage == null) {
							com.aide.common.MessageBox.BT(ServiceContainer.getMainActivity(), "运行错误", "AIDE-Termux未安装或找不到主Activity");
							return true;
						}


						String currentAppHome = ZeroAicySetting.getCurrentAppHome();
						File gradleProjectRootDir = new File(currentAppHome).getParentFile();
						launchIntentForPackage.putExtra(work_dir_extra, gradleProjectRootDir.getAbsolutePath());
						if (cmdline.contains("gradle")) {
							if (!hasGradlew(currentAppHome)) {
								com.aide.common.MessageBox.BT(ServiceContainer.getMainActivity(), "不是Gradle项目", "请保证项目目录下GradleWrapper(Gradle包装器)");
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

	private boolean hasGradlew(String currentAppHome) {
		if( TextUtils.isEmpty(currentAppHome)){
			return false;
		}
		File gradleProjectRootDir = new File(currentAppHome).getParentFile();

		File gradlewFile = new File(gradleProjectRootDir, "gradlew");
		File gradleWrapperJarFile = new File(gradleProjectRootDir, "gradle/wrapper/gradle-wrapper.jar");
		File gradleWrapperProperties = new File(gradleProjectRootDir, "gradle/wrapper/gradle-wrapper.properties");

		return gradlewFile.isFile()
			&& gradleWrapperJarFile.isFile()
			&& gradleWrapperProperties.isFile();
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		RepairBUG1(menu);
		if (!com.aide.ui.ServiceContainer.isTrainerMode()) {
			RepairBUG2(menu);
		}
		return super.onPrepareOptionsMenu(menu);
	}


	//查找Toolbar
	public static android.widget.Toolbar findToolbarByMenu(Menu mMenu) {
		try {
            if (mMenu == null) {
				//实例是 MenuItemImpl
                return null;
            }
			CopyOnWriteArrayList<WeakReference> mPresenters = ReflectPie.on(mMenu).get("mPresenters");
            for (WeakReference ref : mPresenters) {
                final Object presenter = ref.get();
                if (presenter == null) {
                    mPresenters.remove(ref);
					continue;
                }
				if (presenter.getClass().getName().contains("Toolbar$")) {
					Object unknownToolbar = ReflectPie.on(presenter).get("this$0");
					if (unknownToolbar instanceof android.widget.Toolbar) {
						return (android.widget.Toolbar)unknownToolbar;
					}
                }
            }
        }
		catch ( Throwable e) {}
		return null;
	}
	//Menu clear修复
	public static void RepairBUG1(Menu mMenu) {
        try {
            if (mMenu == null) {
                return;
            }
			android.widget.Toolbar mToolbar = findToolbarByMenu(mMenu);
			RepairCollapseActionView(mToolbar);//修复
        }
		catch ( Throwable e) {}

    }
	//mCollapseButtonView 修复
	public static void RepairBUG2(Menu mMenu) {
        try {
			final android.widget.Toolbar mToolbar = findToolbarByMenu(mMenu);
			if (mToolbar == null) {
				return;
			}
			View mCollapseButtonView = ReflectPie.on(mToolbar).get("mCollapseButtonView");
			if (mCollapseButtonView == null) {

			}
			if (mCollapseButtonView != null) {
				mCollapseButtonView.setOnClickListener(new View.OnClickListener(){
						@Override
						public void onClick(View view) {
							try {
								//修复
								RepairCollapseActionView(mToolbar);
								mToolbar.collapseActionView();
							}
							catch ( Throwable e) {}
						}
					});
			}
        }
		catch ( Throwable e) {}
	}

	//修复 collapseActionView方法
	public static void RepairCollapseActionView(android.widget.Toolbar mToolbar) {
        try {
            if (mToolbar == null) {
                return;
            }
			List<View> mHiddenViews = ReflectPie.on(mToolbar).get("mHiddenViews");
			if (mHiddenViews == null) {
				return;
			}
			for (View view : mHiddenViews) {
				ViewGroup parent = (ViewGroup) view.getParent();
				if (parent == null) {
					continue;
				}
				Log.d("RepairBUG", "移除Parent->" + view);
				parent.removeView(view);

			}
		}
		catch ( Throwable e) {}
	}



	/**
	 * 无意义了，做个参考吧
	 */
	/*
	protected void superonCreate(Bundle bundle) {
		if( true ) throw new Error("不要调用superonCreate，superonCreate仅供参考");
		ReflectPie that = ReflectPie.on(this);

		AppLog.Zo(this, "onCreate");

		if (AndroidHelper.er()) {
			tv.ouya.console.api.e.v5().Zo(this, "9b57b7e2-2fa3-44db-9131-04b76a1f491c");
		}
		ServiceContainer.sh(this);
		AndroidHelper.v5(this);

		boolean z = true;
		that.set("Gj", true); //this.Gj = true;
		that.set("e3", new Handler()); // this.e3 = new Handler();
		that.set("n5", AppPreferences.yS(this));//this.n5 = AppPreferences.yS(this);

		AndroidHelper.cn(this, ServiceContainer.U2());
		if (!FireBaseLogEvent.gn()) {
			abcd.b0 mainActivity$q = ReflectPie.onClass("com.aide.ui.MainActivity$q").create(this).get();
			FireBaseLogEvent.Zo(this, mainActivity$q);
		}

		ThemedActionbarActivity.onCreate2((ThemedActionbarActivity)(Object)this, bundle);

		if (!ServiceContainer.isTrainerMode() && !((Boolean)that.call("pN").get())) {
			getWindow().requestFeature(9);
		}
		String str = null;
		if (getIntent() != null && getIntent().getData() != null) {
			str = getIntent().getData().getPath();
		}
		ServiceContainer.cb(this, str);
		that.set("sg", ReflectPie.onClass("com.aide.ui.QuickActionMenu").create(this, 0x7f0b0005).get()); //this.sg = new QuickActionMenu(this, 0x7f0b0005);
		that.set("fY", ReflectPie.onClass("com.aide.common.KeyStrokeDetector").create(this).get()); //this.fY = new KeyStrokeDetector(this);
		AppPreferences.cb(this, this);
		that.set("eU", AppPreferences.Hw()); //this.eU = AppPreferences.Hw();
		setContentView(0x7f0a0026);// R.layout.main

		AndroidHelper.gW(this);
		if (!ServiceContainer.isTrainerMode()) {
			AndroidHelper.KD(findViewById(0x7f0800f3));
		}
		that.set("pO", ReflectPie.onClass("com.aide.ui.QuickKeysBar").create(this).get()); //this.pO = new QuickKeysBar(this);
		that.call("iW");
		com.aide.ui.m mVar = new com.aide.ui.m(this, 0x7f0800f4);
		that.set("cT", mVar); //this.cT = mVar;
		mVar.FH(ServiceContainer.isTrainerMode());

		android.view.View.OnClickListener get = ReflectPie.onClass("com.aide.ui.MainActivity$r").create(this).get();
		mVar.DW(get);

		br().setSwipeEnabled(!ServiceContainer.isTrainerMode() && AppPreferences.we());
		com.aide.ui.views.SplitView.OnSplitChangeListener get2 = ReflectPie.onClass("com.aide.ui.MainActivity$s").create(this).get();
		br().setOnSplitChangeListener(get2);
		findViewById(0x7f08011e).setOnClickListener((android.view.View.OnClickListener)ReflectPie.onClass("com.aide.ui.MainActivity$t").create(this).get());
		if (ServiceContainer.isTrainerMode()) {
			getActionBar().setDisplayHomeAsUpEnabled(true);
			getActionBar().setDisplayShowHomeEnabled(true);
		} else if ((Boolean)that.call("pN").get()) {
			getActionBar().setDisplayShowTitleEnabled(false);
			getActionBar().setNavigationMode(2);
			if (!AndroidHelper.lg(this) && ServiceContainer.ro().dx()) {
				getActionBar().setDisplayHomeAsUpEnabled(true);
			}
			getActionBar().setDisplayShowHomeEnabled(true);
		} else {
			getActionBar().setDisplayOptions(16);
			getActionBar().setDisplayShowCustomEnabled(true);
			getActionBar().setBackgroundDrawable(getResources().getDrawable(0x7f070001));
			findViewById(0x7f0800ed).setBackgroundDrawable((Drawable)ReflectPie.onClass("com.aide.ui.ActionBarNoTabs").create(this).get());
			findViewById(0x7f080121).setBackgroundDrawable((Drawable)ReflectPie.onClass("com.aide.ui.SearchBarNoTabs").create(this).get());
		}
		if (ServiceContainer.isTrainerMode()) {
			com.aide.ui.build.BuildService.BuildListener get3 = ReflectPie.onClass("com.aide.ui.MainActivity$u").create(this).get();
			ServiceContainer.Zo().FH(get3);
		}
		if (ServiceContainer.isTrainerMode() && AndroidHelper.U2(this)) {
			getActionBar().hide();
		}
		ServiceContainer.getOpenFileService().yS(str);
		if (!ServiceContainer.P8.equals("com.aide.web")) {
			g3().Ws();
			cn().Hw();
		}
		ServiceContainer.getNavigateService().VH(sh().getCurrentFileSpan());
		ServiceContainer.J0().u7(this);
		sh().setSoftKeyboardListener(this);
		setHasEmbeddedTabs();
		q7();
		that.call("U2");
		if ((Boolean)that.get("kf")) {
			FireBaseLogEvent.tp("First run after inital install");
		}
		if (getIntent() != null && getIntent().getBooleanExtra("EXTRA_SHOWN_FROM_TRAINER_NOTIFICATION", false)) {
			FireBaseLogEvent.tp("Shown from trainer notification");
		}
		if (getIntent() != null && getIntent().getBooleanExtra("EXTRA_SHOWN_FROM_PROMO_NOTIFICATION", false)) {
			FireBaseLogEvent.tp("Shown from promo notification");
		}
		if (!ServiceContainer.I() && !ServiceContainer.isTrainerMode() && ServiceContainer.getProjectService().isOpenProject()) {
			ServiceContainer.j6(false);
		}
		if (com.aide.ui.n.EQ() && !ServiceContainer.getLicenseService().QX() && !ServiceContainer.getLicenseService().Ws() && !ServiceContainer.getLicenseService().g3() && ((ServiceContainer.isTrainerMode() || !ServiceContainer.I()) && new GregorianCalendar().before(com.aide.ui.n.FH()))) {
			PromoNotificationAlarmReceiver.FH(ServiceContainer.getContext(), com.aide.ui.n.FH().getTimeInMillis(), that.call("nw", this).get(), "20% off special offer", "Special offer", "Save 20% on an annual subscription");
		} else {
			PromoNotificationAlarmReceiver.DW(ServiceContainer.getContext());
		}
		if (AndroidHelper.er() && !tv.ouya.console.api.e.v5().gn()) {
			MessageBox.SI(this, "AIDE for OUYA", "This version of AIDE is only intended to run on the OUYA. Contact info@appfour.com for details.", false, (Runnable)ReflectPie.onClass("com.aide.ui.MainActivity$v").create(this).get(), (Runnable)ReflectPie.onClass("com.aide.ui.MainActivity$a").create(this).get());
		} else if (ServiceContainer.isTrainerMode()) {
			ServiceContainer.ro().aq();
			if (getIntent() != null && getIntent().getBooleanExtra("EXTRA_SHOWN_FROM_UPDATE_TRAINER_NOTIFICATION", false)) {
				TrainerCourseActivity.rN(this);
			} else {
				y.XL(this);
			}
		} else {
			that.call("WB");
			if (getLastNonConfigurationInstance() == null) {
				z = false;
			}
			if (!z) {
				if (ServiceContainer.getOpenFileService().j3()) {
					ServiceContainer.EQ().g3(ServiceContainer.getOpenFileService().u7());
				}
				if (ServiceContainer.getProjectService().isOpenProject()) {
					ServiceContainer.EQ().ca(ServiceContainer.getProjectService().P8());
				}
				that.call("gW");
				that.call("Mr");
				if (getIntent() != null && getIntent().getExtras() != null && getIntent().getExtras().getBoolean("EXTRA_NAVIGATE_BREAKPOINT")) {
					that.call("jw");
					return;
				} else if (getIntent() != null && getIntent().getBooleanExtra("EXTRA_SHOWN_FROM_UPDATE_TRAINER_NOTIFICATION", false)) {
					TrainerCourseActivity.rN(this);
					return;
				} else if (ServiceContainer.P8.equals("com.aide.ui") && getIntent() != null && getIntent().getBooleanExtra("EXTRA_SHOWN_FROM_PROMO_NOTIFICATION", false) && !ServiceContainer.isTrainerMode()) {
					y.XL(this);
					return;
				} else if (getIntent() != null && getIntent().getBooleanExtra("EXTRA_SHOWN_FROM_GCM_NOTIFICATION", false) && getIntent().hasExtra("EXTRA_GCM_NOTIFICATION_IAP_PRODUCT_ID")) {
					FireBaseLogEvent.tp("Shown from GCM notification");
					y.EQ(this, getIntent().getStringExtra("EXTRA_GCM_NOTIFICATION_IAP_PRODUCT_ID"));
					return;
				} else if (getIntent() != null && getIntent().getBooleanExtra("EXTRA_UPGRADE_NOTIFICATION_CLICKED", false)) {
					FireBaseLogEvent.tp("Shown from upgrade notification");
					WhatsNewDialog.Hw(this, (Runnable)ReflectPie.onClass("com.aide.ui.MainActivity$b").create(this).get());
					return;
				} else {
					y.J8(this);
					return;
				}
			}
			y.u7(this);
		}
	 }
	 //*/
}
