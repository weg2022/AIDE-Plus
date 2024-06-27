//
// Decompiled by Jadx - 1315ms
//
package com.aide.ui.build.android;

import abcd.cy;
import abcd.dy;
import abcd.ey;
import abcd.fy;
import abcd.gy;
import abcd.iy;
import android.os.Build;
import com.aide.common.AppLog;
import com.aide.engine.SyntaxError;
import com.aide.ui.ServiceContainer;
import com.aide.ui.project.AndroidProjectSupport;
import com.aide.ui.services.AssetInstallationService;
import com.aide.ui.util.FileSystem;
import io.github.zeroaicy.aide.extend.ZeroAicyExtensionInterface;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;

@cy(clazz = -390754962848522880L, container = -390754962848522880L, user = true)
public class a {

    @dy(field = 239202569234476859L)
    private static boolean Hw;

    @gy
    private static boolean Zo;

    @fy
    private static boolean v5;

    @dy(field = -1439319343464242515L)
    private a$c DW;

    @dy(field = 2429444071915123500L)
    private f FH;

    @dy(field = 2646723890928786381L)
    private final ExecutorService executorService;

    static {
        iy.Zo(a.class);
    }

    @ey(method = 2143289394818954145L)
    public a() {
        try {
            if (v5) {
                iy.gn(1319298152456984128L, (Object) null);
            }
            this.executorService = ZeroAicyExtensionInterface.getProjectExecutorService();
        } catch (Throwable th) {
            if (Zo) {
                iy.aM(th, 1319298152456984128L, (Object) null);
            }
            if( th instanceof Error) throw (Error) th;
            throw new Error(th);
        }
    }

    @ey(method = -7598314424750600748L)
    static void DW(a aVar, Map map) {
        aVar.Ws(map);
    }

    @ey(method = -7836100668456117327L)
    private Map<String, List<SyntaxError>> EQ(String str, String str2) {
        int i;
        try {
            if (v5) {
                iy.EQ(1447649034253373063L, this, str, str2);
            }
            HashMap hashMap = new HashMap();
            for (String str3 : str2.split("\n")) {
                String trim = str3.trim();
                if (trim.length() > 0) {
                    try {
                        int indexOf = trim.indexOf(58);
                        if (indexOf > 0) {
                            String substring = trim.substring(0, indexOf);
                            if (FileSystem.KD(substring)) {
                                int i2 = indexOf + 1;
                                int indexOf2 = trim.indexOf(58, i2);
                                if (indexOf2 < 0) {
                                    indexOf2 = trim.indexOf(32, i2);
                                }
                                if (indexOf2 > 0) {
                                    try {
                                        i = Integer.parseInt(trim.substring(i2, indexOf2));
                                    } catch (NumberFormatException unused) {
                                        i = 1;
                                    }
                                    String trim2 = trim.substring(indexOf2 + 1, trim.length()).trim();
                                    while (trim2.toLowerCase().startsWith("error:")) {
                                        trim2 = trim2.substring(6, trim2.length()).trim();
                                    }
                                    SyntaxError gn = gn("aapt", i, trim2);
                                    if (!hashMap.containsKey(substring)) {
                                        hashMap.put(substring, new ArrayList());
                                    }
                                    ((List) hashMap.get(substring)).add(gn);
                                }
                            }
                        }
                    } catch (Exception e) {
                        AppLog.v5(e);
                    }
                    if (!hashMap.containsKey(str)) {
                        hashMap.put(str, new ArrayList());
                    }
                    ((List) hashMap.get(str)).add(gn("aapt", 1, trim));
                }
            }
            return hashMap;
        } catch (Throwable th) {
            if (Zo) {
                iy.Mr(th, 1447649034253373063L, this, str, str2);
            }
            if( th instanceof Error) throw (Error) th;
            throw new Error(th);
        }
    }

    @ey(method = 637887118825791720L)
    static void FH(a aVar) {
        aVar.J8();
    }

    @ey(method = 304434648875156499L)
    static void Hw(a aVar, Throwable th) {
        aVar.J0(th);
    }

    @ey(method = -1818287024386762077L)
    private void J0(Throwable th) {
        try {
            if (v5) {
                iy.tp(-3032127779516392095L, this, th);
            }
            AppLog.v5(th);
            if (this.FH != null) {
                this.FH.g3();
            }
        } catch (Throwable th2) {
            if (Zo) {
                iy.j3(th2, -3032127779516392095L, this, th);
            }
			if( th2 instanceof Error) throw (Error) th2;
            throw new Error(th2);
        }
    }

    @ey(method = -2893096954932841107L)
    private void J8() {
        try {
            if (v5) {
                iy.gn(2548200924147779975L, this);
            }
            if (this.FH != null) {
                this.FH.J0();
            }
        } catch (Throwable th) {
            if (Zo) {
                iy.aM(th, 2548200924147779975L, this);
            }
            if( th instanceof Error) throw (Error) th;
            throw new Error(th);
        }
    }

    @ey(method = -1613577850507724712L)
    static boolean VH(boolean z) {
        Hw = z;
        return z;
    }

    @ey(method = 2291577116539024620L)
    private void Ws(Map<String, List<SyntaxError>> map) {
        try {
            if (v5) {
                iy.tp(-6041084236410510680L, this, map);
            }
            if (this.FH != null) {
                this.FH.Mz(map);
            }
        } catch (Throwable th) {
            if (Zo) {
                iy.j3(th, -6041084236410510680L, this, map);
            }
            if( th instanceof Error) throw (Error) th;
            throw new Error(th);
        }
    }

    @ey(method = 3364127788162686923L)
    static boolean Zo() {
        return Hw;
    }

    @ey(method = 609663216955893907L)
    private SyntaxError gn(String str, int i, String str2) {
        try {
            if (v5) {
                iy.we(-4919482023679335L, this, str, new Integer(i), str2);
            }
            SyntaxError syntaxError = new SyntaxError();
            syntaxError.jw = i;
            syntaxError.fY = 1;
            syntaxError.qp = i;
            syntaxError.k2 = 1000;
            syntaxError.zh = str + ": " + str2;
            return syntaxError;
        } catch (Throwable th) {
            if (Zo) {
                iy.U2(th, -4919482023679335L, this, str, new Integer(i), str2);
            }
            if( th instanceof Error) throw (Error) th;
            throw new Error(th);
        }
    }

    @ey(method = 473248448452984248L)
    static void j6(a aVar) {
        aVar.we();
    }

    @ey(method = 4174856810662674400L)
    private String tp() {
        try {
            if (v5) {
                iy.gn(36354047737288176L, this);
            }
            if (Build.VERSION.SDK_INT >= 29) {
                return ServiceContainer.getContext().getApplicationInfo().nativeLibraryDir + "/libaidl.so";
            }
            return AssetInstallationService.DW("aidl", false);
        } catch (Throwable th) {
            if (Zo) {
                iy.aM(th, 36354047737288176L, this);
            }
            if( th instanceof Error) throw (Error) th;
            throw new Error(th);
        }
    }

    @ey(method = -2638105283236111335L)
    private a$b u7(String str, List<String> list, boolean z, String str2) {
        try {
            if (v5) {
                iy.J0(956057466586821503L, this, str, list, new Boolean(z), str2);
            }
            return new a$b(this, str2, str, ServiceContainer.getAssetInstallationService().gn(), AndroidProjectSupport.hz(ServiceContainer.getProjectService().vy(str), ServiceContainer.getProjectService().getBuildVariant()), list, z);
        } catch (Throwable th) {
            if (Zo) {
                iy.a8(th, 956057466586821503L, this, str, list, new Boolean(z), str2);
            }
            if( th instanceof Error) throw (Error) th;
            throw new Error(th);
        }
    }

    @ey(method = 4158781679847656875L)
    static Map v5(a aVar, String str, String str2) {
        return aVar.EQ(str, str2);
    }

    @ey(method = -3535590781531786879L)
    private void we() {
        try {
            if (v5) {
                iy.gn(-2948145073366377573L, this);
            }
            if (this.FH != null) {
                this.FH.j6();
            }
        } catch (Throwable th) {
            if (Zo) {
                iy.aM(th, -2948145073366377573L, this);
            }
            if( th instanceof Error) throw (Error) th;
            throw new Error(th);
        }
    }

    @ey(method = -2918463703260725856L)
    public void QX(f fVar) {
        try {
            if (v5) {
                iy.tp(2262218825376155904L, this, fVar);
            }
            this.FH = fVar;
        } catch (Throwable th) {
            if (Zo) {
                iy.j3(th, 2262218825376155904L, this, fVar);
            }
            if( th instanceof Error) throw (Error) th;
            throw new Error(th);
        }
    }

    @ey(method = 32178355211172639L)
    public void XL(final String str, final boolean z, final boolean z2) {
        try {
            if (v5) {
                iy.we(2748627575776940733L, this, str, new Boolean(z), new Boolean(z2));
            }
            final String tp = tp();
            if (this.DW != null) {
                this.DW.cancel(true);
                this.DW = null;
            }
			a$a.TaskFactory taskFactory = new a$a.TaskFactory(){
				@Override
				public List<a$b> getTasks() {
					ArrayList<a$b> arrayList = new ArrayList<>();
					if (z2) {
						for (String str2 : ServiceContainer.getProjectService().yS()) {
							if (!str.equals(str2)) {
								arrayList.add(u7(str2, null, false, tp));
							}
						}
					}
					arrayList.add(u7(str, null, z, tp));
					return arrayList;
				}
			};
            
            a$c cVar = new a$c(this, new a$a(this, taskFactory));
            this.DW = cVar;
            this.executorService.execute(cVar);
        } catch (Throwable th) {
            if (Zo) {
                iy.U2(th, 2748627575776940733L, this, str, new Boolean(z), new Boolean(z2));
            }
            if( th instanceof Error) throw (Error) th;
            throw new Error(th);
        }
    }

    @ey(method = -570462002232748675L)
    public void aM(final List<String> list) {
        try {
            if (v5) {
                iy.tp(-7522701461290010201L, this, list);
            }
            final String tp = tp();
            if (this.DW != null) {
                this.DW.cancel(true);
                this.DW = null;
            }
			// 异步，如果在主线程则非常耗时
			a$a.TaskFactory taskFactory = new a$a.TaskFactory(){
				@Override
				public List<a$b> getTasks() {
					// 比较耗时，当在线程池运行
					ArrayList<a$b> arrayList = new ArrayList<>();
					for(String next : ServiceContainer.getProjectService().yS()){
						arrayList.add(u7(next, list, false, tp));
					}
					return arrayList;
				}
			};
            a$c cVar = new a$c(this, new a$a(this, taskFactory));
            this.DW = cVar;
            this.executorService.execute(cVar);
        } catch (Throwable th) {
            if (Zo) {
                iy.j3(th, -7522701461290010201L, this, list);
            }
            if( th instanceof Error) throw (Error) th;
            throw new Error(th);
        }
    }
}

