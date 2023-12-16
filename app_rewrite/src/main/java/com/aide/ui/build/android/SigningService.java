package com.aide.ui.build.android;

import abcd.cy;
import abcd.dy;
import abcd.ed;
import abcd.ey;
import abcd.fy;
import abcd.gy;
import abcd.iy;
import android.app.Activity;
import com.aide.common.MessageBox;
import com.aide.common.ValueRunnable;
import com.aide.ui.App;
import com.aide.ui.AppPreferences;
import com.aide.ui.MainActivity;
import com.aide.ui.util.BuildGradle;
import com.aide.ui.util.FileSystem;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Collections;
@cy(clazz = 1054312504720486788L, container = 1054312504720486788L, user = true)
/* loaded from: /storage/emulated/0/AppProjects1/.project/AIDE+/app_rewrite/provider/SigningService.dex */
public class SigningService {
    @gy
    private static /* synthetic */ boolean DW;
    @fy
    private static /* synthetic */ boolean j6;

    @cy(clazz = -456191370199574592L, container = -456191370199574592L, user = true)
    /* loaded from: /storage/emulated/0/AppProjects1/.project/AIDE+/app_rewrite/provider/SigningService.dex */
    public interface SigningRunnable {
        @ey(method = 1826402226779581395L)
        void j6(String str, String str2, String str3, String str4);
    }

    @cy(clazz = -2971104453650556349L, container = 1054312504720486788L, user = true)
    /* loaded from: /storage/emulated/0/AppProjects1/.project/AIDE+/app_rewrite/provider/SigningService.dex */
    class a implements Runnable {
        @dy(field = -1325875345222548792L)
        final /* synthetic */ String WB;
        @dy(field = 3871165034209308000L)
        final /* synthetic */ SigningRunnable mb;
        @ey(method = -3441480203686840587L)
        a(String str, SigningRunnable signingRunnable) {
            this.WB = str;
            this.mb = signingRunnable;
        }

        @Override // java.lang.Runnable
        @ey(method = -962677808293196125L)
        public void run() {
			SigningService.this.v5(this.WB, this.mb);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @cy(clazz = -2971208049586580704L, container = 1054312504720486788L, user = true)
    /* loaded from: /storage/emulated/0/AppProjects1/.project/AIDE+/app_rewrite/provider/SigningService.dex */
    public class b implements ValueRunnable<String> {
        @dy(field = 4401127368425169043L)
        final /* synthetic */ String DW;
        @dy(field = 3592669819486048336L)
        final /* synthetic */ SigningRunnable FH;
        @dy(field = -5640335584373944044L)
        final /* synthetic */ String j6;
		
        @ey(method = 17269374959139957L)
        b(String str, String str2, SigningRunnable signingRunnable) {
            this.j6 = str;
            this.DW = str2;
            this.FH = signingRunnable;
        }

        @ey(method = -8482063888119037329L)
        /* renamed from: DW */
        public void j6(String str) {
			SigningService.j6(SigningService.this, this.j6, this.DW, str, this.FH);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    @cy(clazz = -2971311645518541765L, container = 1054312504720486788L, user = true)
    /* loaded from: /storage/emulated/0/AppProjects1/.project/AIDE+/app_rewrite/provider/SigningService.dex */
    public class c implements ValueRunnable<String> {
        @dy(field = 2320447443640381865L)
        final /* synthetic */ String DW;
        @dy(field = -3408984433179110808L)
        final /* synthetic */ String FH;
        @dy(field = -3343611760975335240L)
        final /* synthetic */ SigningRunnable Hw;
        @dy(field = 1796514412372031136L)
        final /* synthetic */ String j6;
		
        @ey(method = -196320373074543L)
        c(String str, String str2, String str3, SigningRunnable signingRunnable) {
            this.j6 = str;
            this.DW = str2;
            this.FH = str3;
            this.Hw = signingRunnable;
        }

        @ey(method = -3768758292758685135L)
        /* renamed from: DW */
        public void j6(String str) {
			SigningService.DW(SigningService.this, this.j6, this.DW, this.FH, str, this.Hw);
        }
    }

    @cy(clazz = -2971415241446439532L, container = 1054312504720486788L, user = true)
    /* loaded from: /storage/emulated/0/AppProjects1/.project/AIDE+/app_rewrite/provider/SigningService.dex */
    class d implements ed.f {
        final /* synthetic */ Activity j6;

        @ey(method = -426660062506308725L)
        d(SigningService signingService, Activity activity) {
            this.j6 = activity;
        }

        @ey(method = -5662325880149501773L)
        public void j6(boolean z, String str) {
			if (z) {
				AppPreferences.I(str);
				Activity activity = this.j6;
				MessageBox.ei(activity, "Create keystore", "Keystore file " + str + " has been created and set as default.", (Runnable) null);
				return;
			}
			Activity activity2 = this.j6;
			MessageBox.BT(activity2, "Create keystore", "Failed to create keystore file " + str);
        }
    }
    @ey(method = 2173769266671334785L)
    public SigningService() {}

    @ey(method = 5110595402486911839L)
    public static void DW(SigningService signingService, String str, String str2, String str3, String str4, SigningRunnable signingRunnable) {
        signingService.FH(str, str2, str3, str4, signingRunnable);
    }

    @ey(method = -765423962236243125L)
    private void FH(String str, String str2, String str3, String str4, SigningRunnable signingRunnable) {
        try {
            try {
                JKSKeyStore jKSKeyStore = new JKSKeyStore();
                jKSKeyStore.load(new FileInputStream(str), str2.toCharArray());
                if (jKSKeyStore.getKey(str3, str4.toCharArray()) != null) {
                    signingRunnable.j6(str, str2, str3, str4);
                    return;
                }
                throw new Exception("no alias");
            } catch (Exception unused) {
                MessageBox.BT(App.rN(), "Build Error", "Invalid keystore credentials!");
            }
        } catch (Throwable th) {
			
        }
    }

    @ey(method = 4111598620218933911L)
    private void VH(String str, String str2, SigningRunnable signingRunnable) {
        try {
			JKSKeyStore jKSKeyStore = new JKSKeyStore();
			jKSKeyStore.load(new FileInputStream(str), str2.toCharArray());
			ArrayList list = Collections.list(jKSKeyStore.aliases());
			if (list.size() == 1) {
				gn(str, str2, (String) list.get(0), signingRunnable);
			} else {
				MessageBox.VH(App.rN(), "Select keystore alias", list, new b(str, str2, signingRunnable));
			}
		} catch (Exception unused) {
			MessageBox.BT(App.rN(), "Build Error", "Invalid keystore credentials!");
		}
    }

    @ey(method = -4586519056902629363L)
    private void gn(String str, String str2, String str3, SigningRunnable signingRunnable) {
		MainActivity rN = App.rN();
		MessageBox.J8(rN, (String) null, "Enter password for keystore alias '" + str3 + "':", new c(str, str2, str3, signingRunnable));
    }

    @ey(method = -56070401644590480L)
    public static void j6(SigningService signingService, String str, String str2, String str3, SigningRunnable signingRunnable) {
        signingService.gn(str, str2, str3, signingRunnable);
    }

    /* JADX WARN: Removed duplicated region for block: B:46:0x0175  */
    @abcd.ey(method = 52117267659415973L)
    /*
	 Code decompiled incorrectly, please refer to instructions dump.
	 To view partially-correct add '--show-bad-code' argument
	 */
    public boolean Hw(java.lang.String r29, java.lang.String r30, java.lang.String r31, java.lang.String r32, java.util.Date r33, java.util.Date r34, java.math.BigInteger r35, java.lang.String r36, java.lang.String r37, java.lang.String r38, java.lang.String r39, java.lang.String r40, java.lang.String r41) {
        /*
		 Method dump skipped, instructions count: 419
		 To view this dump add '--comments-level debug' option
		 */
        throw new UnsupportedOperationException("Method not decompiled: com.aide.ui.build.android.SigningService.Hw(java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.util.Date, java.util.Date, java.math.BigInteger, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String, java.lang.String):boolean");
    }

    @ey(method = 374861035471152168L)
    public void Zo(String str, BuildGradle.SigningConfig signingConfig, SigningRunnable signingRunnable) {
        if (signingConfig != null) {
			try {
				String j62 = signingConfig.j6();
				JKSKeyStore jKSKeyStore = new JKSKeyStore();
				jKSKeyStore.load(new FileInputStream(j62), signingConfig.Hw.toCharArray());
				if (jKSKeyStore.getKey(signingConfig.DW, signingConfig.FH.toCharArray()) != null) {
					signingRunnable.j6(j62, signingConfig.Hw, signingConfig.DW, signingConfig.FH);
					return;
				}
				throw new Exception("can not read keystore");
			} catch (Exception unused) {
				MessageBox.rN(App.rN(), "Build Error", "Failed to open signingConfig from build.gradle. Use alternative signing?", new a(str, signingRunnable), (Runnable) null);
				return;
			}
		}
		v5(str, signingRunnable);
		
    }

    @ey(method = 402808675949028321L)
    public void u7(Activity activity) {
		MessageBox.gW(activity, new ed(FileSystem.vy() + "/AppProjects/debug.keystore", "androiddebugkey", "android", new d(this, activity)));
		
    }

    @ey(method = 6458267827521734465L)
    public void v5(String str, SigningRunnable signingRunnable) {
		if (isPk8X509Pem(str)) {
			signingRunnable.j6(str, "", "", "");
		}
		if (str != null && str.length() > 0) {
			if (FileSystem.sG(str)) {
				try {
					JKSKeyStore jKSKeyStore = new JKSKeyStore();
					jKSKeyStore.load(new FileInputStream(str), "android".toCharArray());
					if (jKSKeyStore.getKey("androiddebugkey", "android".toCharArray()) != null) {
						signingRunnable.j6(str, "android", "androiddebugkey", "android");
						return;
					}
					throw new Exception("no androiddebugkey");
				} catch (Exception unused) {
					if (App.a8().VH(App.gn(), "custom_keystore")) {
						VH(str, "", signingRunnable);
						return;
					}
					return;
				}
			}
			MainActivity rN = App.rN();
			MessageBox.BT(rN, "Build Error", "Keystore file " + str + " does not exist!");
			return;
		}
		signingRunnable.j6("", "", "", "");
    }
	
	public static boolean isPk8X509Pem(String str) {
        return (str != null && str.endsWith(".pk8")) || str.endsWith(".x509.pem");
    }
}
