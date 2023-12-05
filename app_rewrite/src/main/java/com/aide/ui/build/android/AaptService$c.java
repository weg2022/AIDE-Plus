package com.aide.ui.build.android;

import abcd.cy;
import abcd.dy;
import abcd.ey;
import abcd.fy;
import abcd.gy;
import abcd.hy;
import abcd.iy;
import abcd.wf;
import abcd.xf;
import android.os.Build;
import com.aide.common.AppLog;
import com.aide.common.StreamUtilities;
import com.aide.ui.build.android.AaptService;
import com.aide.ui.project.AndroidProjectSupport;
import com.aide.ui.util.FileSystem;
import io.github.zeroaicy.aide.aapt2.Aapt2Task;
import io.github.zeroaicy.aide.preference.ZeroAicySetting;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

@cy(clazz = -3735878600652745935L, container = -3735878600652745935L, user = true)
class AaptService$c {
    @fy
    private static boolean U2;
    @gy
    private static boolean a8;
    @dy(field = -931472428625758656L)
    private final Map<String, List<String>> DW;
    @dy(field = -807651976752566091L)
    private final Map<String, String> EQ;
    @dy(field = 1477571350506341892L)
    private final String FH;
	
	//androidJar File
    @dy(field = 1796935697685499452L)
    private final String Hw;
	
    @dy(field = -1781805026001355768L)
    private final List<String> J0;
	
    @dy(field = 2783851783522730945L)
    private final Map<String, String> J8;
	
    @dy(field = -2012524741532688485L)
    @hy
    final AaptService Mr;
    @dy(field = 5554888034137921137L)
    private final Map<String, String> QX;
    @dy(field = 3448893266485857335L)
    private final List<String> VH;
    @dy(field = -310830878746832448L)
    private final Map<String, String> Ws;
    @dy(field = 6523447474889641440L)
    private boolean XL;
    @dy(field = -3009879880065517368L)
    private final String Zo;
    @dy(field = -3340755254465993728L)
    private boolean aM;
	
	//resource.ap_
    @dy(field = -1883344472830919455L)
    private final String gn;
    @dy(field = 3597169466312011425L)
    private boolean j3;
	
    @dy(field = 626131991615361075L)
    private final String j6;
    @dy(field = 380638801485211317L)
    private final Map<String, List<String>> tp;
    @dy(field = -571588385592964453L)
    private final Map<String, String> u7;
    @dy(field = 3461840776536088011L)
    private final String v5;
    @dy(field = 5599908852964372480L)
    private final List<String> we;

    static {
        iy.Zo(AaptService$c.class);
    }

    @ey(method = -3030626465330413663L)
    public AaptService$c(AaptService aaptService, String str, String str2, String str3, Map<String, List<String>> map, List<String> list, List<String> list2, String androidJarFilePath, String str5, List<String> list3, String resourceAp_FilePath, Map<String, String> genPackageNameMap, Map<String, String> map3, Map<String, String> map4, Map<String, String> map5, Map<String, List<String>> map6, Map<String, String> map7, boolean z, boolean z2, boolean z3) {
        if (U2) {
            iy.QX(-1829447386727788164L, (Object) null, new Object[]{aaptService, str, str2, str3, map, list, list2, androidJarFilePath, str5, list3, resourceAp_FilePath, genPackageNameMap, map3, map4, map5, map6, map7, new Boolean(z), new Boolean(z2), new Boolean(z3)});
        }
		this.Mr = aaptService;
        this.j6 = str2;
        this.DW = map;
        this.v5 = str3;
        this.FH = str;
        this.we = list;
        this.J0 = list2;
        this.Hw = androidJarFilePath;
        this.Zo = str5;
        this.VH = list3;
        this.gn = resourceAp_FilePath;
		
		//genDir -> packageName，但只有子项目，子项目的子项目没有
        this.EQ = genPackageNameMap;
		
        this.u7 = map3;
        this.J8 = map5;
        this.tp = map6;
        this.XL = z;
        this.aM = z2;
        this.j3 = z3;
        this.QX = new HashMap<String, String>(map4);
        this.Ws = new HashMap<>();
        for (Map.Entry<String, String> entry : map7.entrySet()) {
            this.Ws.put(entry.getValue(), entry.getKey());
        }
    }

    @ey(method = 1224465869535983408L)
    public static String DW(AaptService$c aaptService$c) {
        return aaptService$c.j6;
    }

    @ey(method = 1032727064344733680L)
    private AaptService$b EQ() {
        try {
            if (U2) {
                iy.gn(-562180946974429500L, this);
            }
            if (this.we.size() > 0 || this.J0.size() > 0) {
                String str = this.QX.get(this.Zo);
                String str2 = this.Ws.get(str);
                String str3 = this.J8.get(this.Zo);
                int size = this.we.size();
                String[] strArr = new String[size];
                for (int i = 0; i < this.we.size(); i++) {
                    strArr[i] = this.QX.get(this.we.get(i));
                }
                int size2 = this.J0.size();
                String[] strArr2 = new String[size2];
                for (int i2 = 0; i2 < this.J0.size(); i2++) {
                    strArr2[i2] = this.J0.get(i2);
                }
                this.QX.put(this.Zo, str3);
                this.Ws.put(str3, str2);
                ArrayList<File> arrayList = new ArrayList<>();
                arrayList.add(new File(str));
                for (int i3 = 0; i3 < size; i3++) {
                    arrayList.add(new File(strArr[i3]));
                }
                for (int i4 = 0; i4 < size2; i4++) {
                    arrayList.add(new File(strArr2[i4]));
                }
                if (new File(str3).exists() && u7(arrayList, Collections.singletonList(new File(str3)))) {
                    AppLog.DW("Omitting merge " + str3);
                }
                AppLog.DW("Merging " + str3);
                String j6 = l.j6(AaptService.Zo(this.Mr), str3, str, strArr2, strArr);
                if (j6 != null) {
                    return new AaptService$b(j6);
                }
            }
            return new AaptService$b(false);
        } catch (Throwable th) {
            if (a8) {
                iy.aM(th, -562180946974429500L, this);
            }
            throw new Error(th);
        }
    }

    @ey(method = -1180674979090545141L)
    public static Map FH(AaptService$c aaptService$c) {
        return aaptService$c.Ws;
    }

    @ey(method = 2668790741732965889L)
    private void Hw(File file, String str, List<File> list) {
        try {
            if (U2) {
                iy.we(2660087692555738225L, this, file, str, list);
            }
            File[] listFiles = file.listFiles();
            if (listFiles == null) {
                return;
            }
            for (File file2 : listFiles) {
                if (file2.isDirectory()) {
                    Hw(file2, str, list);
                }
				else if (str == null || str.equals(file2.getName())) {
                    list.add(file2);
                }
            }
        } catch (Throwable th) {
            if (a8) {
                iy.U2(th, 2660087692555738225L, this, file, str, list);
            }
            throw new Error(th);
        }
    }

    @ey(method = -3464081095762918397L)
    private AaptService$b J0() {
        try {
            if (U2) {
                iy.gn(545654521477259511L, this);
            }
            for (Map.Entry<String, List<String>> entry : this.tp.entrySet()) {
                if (!Thread.interrupted()) {
                    String key = entry.getKey();
                    List<String> value = entry.getValue();
                    if (!key.equals(this.Zo)) {
                        AaptService$b J8 = J8(key, value);
                        if (J8.DW != null) {
                            return J8;
                        }
                    }
                }
				else {
                    throw new InterruptedException();
                }
            }
            if (this.XL) {
                AaptService$b J82 = J8(this.Zo, this.tp.get(this.Zo));
                if (J82.DW != null) {
                    return J82;
                }
            }
            return new AaptService$b(false);
        } catch (Throwable th) {
            if (a8) {
                iy.aM(th, 545654521477259511L, this);
            }
            throw new Error(th);
        }
    }

    @ey(method = -917848650515497287L)
    private AaptService$b J8(String str, List<String> list) {
        try {
            if (U2) {
                try {
                    iy.EQ(-1076563014087588483L, this, str, list);
                } catch (Throwable th) {
                    if (a8) {
                        iy.Mr(th, -1076563014087588483L, this, str, list);
                    }
                    throw new Error(th);
                }
            }
            String str2 = this.QX.get(str);
            ArrayList<File> arrayList = new ArrayList<>();
            for (String str3 : list) {
                Hw(new File(str3), null, arrayList);
            }
            if (arrayList.isEmpty()) {
                return new AaptService$b(false);
            }
            if (new File(str2).exists()) {
                arrayList.add(new File(str2));
            }
            ArrayList<File> arrayList2 = new ArrayList<>();
            Hw(new File(str), "R.java", arrayList2);
            if (!this.aM && !arrayList2.isEmpty() && u7(arrayList, arrayList2)) {
                AppLog.DW("Omitting aapt call to regenerate R.java in " + str + " (is uptodate)");
                return new AaptService$b(false);
            }
            ArrayList<String> arrayList3 = new ArrayList<>();
            if (str.equals(this.Zo)) {
                arrayList3.addAll(Arrays.asList(new String[]{this.FH, "package", "--auto-add-overlay", "-m", "-J", str, "-M", str2, "-I", this.Hw, "--no-version-vectors"}));
            }
			else {
                arrayList3.addAll(Arrays.asList(new String[]{this.FH, "package", "--non-constant-id", "--auto-add-overlay", "-m", "-J", str, "-M", str2, "-I", this.Hw, "--no-version-vectors"}));
            }
            for (String str4 : list) {
                if (new File(str4).exists()) {
                    arrayList3.addAll(Arrays.asList(new String[]{"-S", str4}));
                }
            }
            if (str.equals(this.Zo)) {
                String gn = gn();
                if (gn.length() != 0) {
                    arrayList3.add("--extra-packages");
                    arrayList3.add(gn.toString());
                }
            }
            tp(arrayList3);
            long currentTimeMillis = System.currentTimeMillis();
            wf j6 = xf.j6(arrayList3, (String) null, null, true, (OutputStream) null, (byte[]) null);
            AppLog.DW("aapt call elapsed " + (System.currentTimeMillis() - currentTimeMillis));
            if (j6.DW() == 0) {
                for (File file : arrayList2) {
                    if (file.lastModified() < currentTimeMillis) {
                        FileSystem.aj(file.getPath());
                    }
                }
                return new AaptService$b(false);
            }
            return new AaptService$b(VH(j6.j6(), j6.DW()));
        } catch (Throwable th) {
			throw new Error(th);
        }
    }

    @ey(method = 3682399595367627425L)
    private AaptService$b QX() {
        try {
            if (U2) {
                iy.gn(2087452825608805401L, this);
            }
            if (!Thread.interrupted()) {
                List<String> list = this.tp.get(this.Zo);
                String str = this.QX.get(this.Zo);
                ArrayList<File> arrayList = new ArrayList<>();
                ArrayList<File> arrayList2 = new ArrayList<>();
                for (String str2 : list) {
                    Hw(new File(str2), null, arrayList2);
                }
                if (new File(str).exists()) {
                    arrayList2.add(new File(str));
                }
                for (String str3 : this.VH) {
                    if (new File(str3).exists()) {
                        Hw(new File(str3), null, arrayList2);
                    }
                }
                arrayList2.add(new File(this.Hw));
                Hw(new File(this.Zo), "R.java", arrayList);
                arrayList.add(new File(this.gn));
                if (!this.aM && !arrayList.isEmpty() && new File(this.gn).exists() && u7(arrayList2, arrayList)) {
                    AppLog.DW("Omitting aapt package call (is uptodate)");
                    return new AaptService$b(false);
                }
                ArrayList<String> arrayList3 = new ArrayList<>();
                if (this.j3) {
                    arrayList3.addAll(Arrays.asList(new String[]{this.FH, "package", "-f", "--no-crunch", "--auto-add-overlay", "-I", this.Hw, "-F", this.gn}));
                }
				else {
                    arrayList3.addAll(Arrays.asList(new String[]{this.FH, "package", "-f", "--no-crunch", "--auto-add-overlay", "--debug-mode", "-I", this.Hw, "-F", this.gn}));
                }
                for (String str4 : this.VH) {
                    if (new File(str4).exists()) {
                        arrayList3.addAll(Arrays.asList(new String[]{"-A", str4}));
                    }
                }
                arrayList3.addAll(Arrays.asList(new String[]{"-M", str}));
                for (String str5 : list) {
                    if (new File(str5).exists()) {
                        String str6 = this.u7.get(str5);
                        new File(str6).mkdirs();
                        arrayList3.addAll(Arrays.asList(new String[]{"-S", str6, "-S", str5}));
                    }
                }
                arrayList3.addAll(Arrays.asList(new String[]{"-m", "-J", this.Zo, "--no-version-vectors"}));
                String gn = gn();
                if (gn.length() != 0) {
                    arrayList3.add("--extra-packages");
                    arrayList3.add(gn.toString());
                }
                tp(arrayList3);
                long currentTimeMillis = System.currentTimeMillis();
                wf j6 = xf.j6(arrayList3, (String) null, null, true, (OutputStream) null, (byte[]) null);
                AppLog.DW("aapt call elapsed " + (System.currentTimeMillis() - currentTimeMillis));
                if (j6.DW() == 0) {
                    for (File file : arrayList) {
                        if (file.lastModified() < currentTimeMillis) {
                            FileSystem.aj(file.getPath());
                        }
                    }
                    return new AaptService$b(true);
                }
                return new AaptService$b(VH(j6.j6(), j6.DW()));
            }
            throw new InterruptedException();
        } catch (Throwable th) {
            if (a8) {
                iy.aM(th, 2087452825608805401L, this);
            }
            throw new Error(th);
        }
    }

    @ey(method = 2580572055336340287L)
    private String VH(byte[] bArr, int i) {
        try {
            if (U2) {
                iy.EQ(5722637118515521607L, this, bArr, new Integer(i));
            }
			String FH = StreamUtilities.FH(new InputStreamReader(new ByteArrayInputStream(bArr)));

            String trim = FH.trim();
            if (trim.length() == 0) {
                return "aapt exited with code " + i;
            }
			else if (i != 1) {
                return trim + "\naapt exited with code " + i;
            }
			else {
                return trim;
            }
        } catch (Throwable th) {
            if (a8) {
                iy.Mr(th, 5722637118515521607L, this, bArr, new Integer(i));
            }
            throw new Error(th);
        }
    }

    @ey(method = 2636827223428796640L)
    private AaptService$b Ws() {
        try {
            if (U2) {
                iy.gn(4641341701031625616L, this);
            }
            for (Map.Entry<String, String> entry : this.u7.entrySet()) {
                if (!Thread.interrupted()) {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    if (new File(key).exists()) {
                        ArrayList<File> arrayList = new ArrayList<>();
                        Hw(new File(key), null, arrayList);
                        ArrayList<File> arrayList2 = new ArrayList<>();
                        Hw(new File(value), null, arrayList2);
                        if (u7(arrayList, arrayList2)) {
                            AppLog.DW("Omitting aapt crunch call (is uptodate)");
                        }
						else {
                            List<String> asList = Arrays.asList(new String[]{this.FH, "crunch", "-S", key, "-C", value, "--no-version-vectors"});
                            tp(asList);
                            long currentTimeMillis = System.currentTimeMillis();
                            wf j6 = xf.j6(asList, (String) null, null, true, (OutputStream) null, (byte[]) null);
                            AppLog.DW("aapt call elapsed " + (System.currentTimeMillis() - currentTimeMillis));
                            if (j6.DW() != 0) {
                                return new AaptService$b(VH(j6.j6(), j6.DW()));
                            }
                        }
                    }
                }
				else {
                    throw new InterruptedException();
                }
            }
            return new AaptService$b(false);
        } catch (Throwable th) {
            if (a8) {
                iy.aM(th, 4641341701031625616L, this);
            }
            throw new Error(th);
        }
    }

    @ey(method = 3024365811395366079L)
    private void Zo() {
        try {
            if (U2) {
                iy.gn(7026278626584899355L, this);
            }
            for (Map.Entry<String, String> entry : this.EQ.entrySet()) {
                String value = entry.getValue();
                File file = new File(entry.getKey(), value.replace('.', File.separatorChar));
                if (!file.exists() && !file.mkdirs()) {
                    throw new IOException("Could not create directory " + file);
                }
                File file2 = new File(file, "BuildConfig.java");
                if (!this.XL || !file2.exists()) {
                    c.j6(file2, value, this.j3);
                }
            }
        } catch (Throwable th) {
            if (a8) {
                iy.aM(th, 7026278626584899355L, this);
            }
            throw new Error(th);
        }
    }

    @ey(method = -3669174501772761539L)
    private String gn() {
        try {
            if (U2) {
                iy.gn(5107049041690597561L, this);
            }
            HashSet<String> hashSet = new HashSet<>();
            for (Map.Entry<String, String> entry : this.EQ.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                if (!value.equals(this.EQ.get(this.Zo)) && !hashSet.contains(value) && new File(new File(key, value.replace('.', File.separatorChar)), "R.java").exists()) {
                    hashSet.add(value);
                }
            }
            if (FileSystem.er(FileSystem.gW(this.QX.get(this.Zo))).equals("AIDEExp")) {
                hashSet.add("com.aide.ui");
            }
            StringBuilder sb = new StringBuilder();
            for (String str : hashSet) {
                if (sb.length() != 0) {
                    sb.append(':');
                }
                sb.append(str);
            }
            return sb.toString();
        } catch (Throwable th) {
            if (a8) {
                iy.aM(th, 5107049041690597561L, this);
            }
            throw new Error(th);
        }
    }

    @ey(method = -2620449744395147856L)
    private void j6() {
        try {
            if (U2) {
                iy.gn(-5562902152935276984L, this);
            }
            if (!Thread.interrupted()) {
                if (Build.VERSION.SDK_INT < 29 && !AaptService.VH()) {
                    File aaptFile = new File(this.FH);
					boolean isSetExecutable = aaptFile.setReadable(true, false) 
						&& aaptFile.setExecutable(true, false);
                    if (!isSetExecutable) {
                        throw new IOException("Could not make " + this.FH + " executable - exit code ");
                    }
                    AaptService.gn(true);
                    return;
                }
                return;
            }
            throw new InterruptedException();
        } catch (Throwable th) {
            if (a8) {
                iy.aM(th, -5562902152935276984L, this);
            }
            throw new Error(th);
        }
    }

    @ey(method = 2250514188602812636L)
    private void tp(List<String> list) {
        try {
            if (U2) {
                iy.tp(1952183489897339520L, this, list);
            }
            StringBuilder sb = new StringBuilder();
            sb.append("Running aapt ");
            for (int i = 1; i < list.size(); i++) {
                sb.append('\"');
                sb.append(list.get(i));
                sb.append('\"');
                if (i != list.size() - 1) {
                    sb.append(" ");
                }
            }
            AppLog.DW(sb.toString());
        } catch (Throwable th) {
            if (a8) {
                iy.j3(th, 1952183489897339520L, this, list);
            }
            throw new Error(th);
        }
    }

    @ey(method = 191722989083538040L)
    private boolean u7(List<File> list, List<File> list2) {
        try {
            if (U2) {
                iy.EQ(-332527823694516160L, this, list, list2);
            }
            if (list.isEmpty() || !list2.isEmpty()) {
                long j = Long.MAX_VALUE;
                for (File file : list2) {
                    j = Math.min(j, file.lastModified());
                }
                long j2 = 0;
                for (File file2 : list) {
                    j2 = Math.max(j2, file2.lastModified());
                }
                return j > j2;
            }
            return false;
        } catch (Throwable th) {
            if (a8) {
                iy.Mr(th, -332527823694516160L, this, list, list2);
            }
            throw new Error(th);
        }
    }

    @ey(method = -2966187405524018889L)
    private void v5() {
        try {
            if (U2) {
                iy.gn(229105867974959927L, this);
            }
            for (Map.Entry<String, String> entry : this.u7.entrySet()) {
                if (!Thread.interrupted()) {
                    String value = entry.getValue();
                    if (new File(value).exists()) {
                        FileSystem.VH(value);
                    }
                }
				else {
                    throw new InterruptedException();
                }
            }
        } catch (Throwable th) {
            if (a8) {
                iy.aM(th, 229105867974959927L, this);
            }
            throw new Error(th);
        }
    }
	
	//被Callable调用
    @ey(method = 998598216370448001L)
    public AaptService$b we() {
        try {
			if( ZeroAicySetting.isEnableAapt2()){
				return (AaptService$b)Aapt2Task.proxyAapt(this);
			}
            if (U2) {
                iy.gn(-1983879111166566075L, this);
            }
            AndroidProjectSupport.Qq(this.DW, this.v5);
            AaptService$b EQ = EQ();
            if (EQ.DW != null) {
                return EQ;
            }
            for (String str : this.QX.keySet()) {
                if (!new File(str).exists()) {
                    new File(str).mkdirs();
                }
            }
            j6();
            File parentFile = new File(this.gn).getParentFile();
            if (!parentFile.exists()) {
                parentFile.mkdirs();
            }
            AaptService$b J0 = J0();
            if (J0.DW != null) {
                return J0;
            }
            Zo();
            if (!this.XL) {
                if (this.aM) {
                    v5();
                }
                AaptService$b Ws = Ws();
                return Ws.DW != null ? Ws : QX();
            }
            return new AaptService$b(false);
        } catch (Throwable th) {
            if (a8) {
                iy.aM(th, -1983879111166566075L, this);
            }
            throw new Error(th);
        }
    }
}

