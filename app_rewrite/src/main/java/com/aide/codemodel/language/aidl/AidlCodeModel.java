//
// Decompiled by Jadx - 987ms
//
package com.aide.codemodel.language.aidl;

import abcd.cy;
import abcd.dy;
import abcd.ey;
import abcd.fy;
import abcd.g6;
import abcd.gy;
import abcd.iy;
import abcd.t6;
import com.aide.codemodel.api.FileEntry;
import com.aide.codemodel.api.Model;
import com.aide.codemodel.api.SyntaxTree;
import com.aide.codemodel.api.SyntaxTreeSytles;
import com.aide.codemodel.api.abstraction.CodeModel;
import com.aide.codemodel.api.abstraction.Compiler;
import com.aide.codemodel.api.abstraction.Debugger;
import com.aide.codemodel.api.abstraction.Language;
import com.aide.codemodel.api.abstraction.Preprocessor;
import com.aide.codemodel.language.java.JSharpCommentsLanguage;
import com.aide.codemodel.language.java.JavaCompiler;
import com.aide.codemodel.language.java.JavaDebugger;
import com.aide.codemodel.language.java.JavaParser;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.aide.codemodel.language.classfile.JavaBinaryLanguage;
import com.aide.codemodel.language.java.JavaSyntax;

public class AidlCodeModel implements CodeModel {

	@gy
    private static boolean EQ;

    @fy
    private static boolean tp;

    @dy(field = -4074038619139024941L)
    private final JavaBinaryLanguage DW;

    @dy(field = 6125048507907414968L)
    private final JSharpCommentsLanguage FH;

    @dy(field = -2589009784882602773L)
    private g6 VH;

    @dy(field = -1766239070954761056L)
    private JavaParser Zo;

    @dy(field = 1974644574678873456L)
    private JavaCompiler gn;

    @dy(field = 1235911872478228400L)
    private final Model j6;

    @dy(field = -3496342180201515488L)
    private JavaDebugger u7;

    @dy(field = -1749024221705949075L)
    private t6 v5;


    @ey(method = -140764856981626773L)
    public AidlCodeModel(Model model) {
        try {
            if (tp) {
                iy.tp(70129601982276585L, (Object) null, model);
            }
            this.j6 = model;
            this.DW = new JavaBinaryLanguage(model);
            this.FH = new JSharpCommentsLanguage(model, false);
            if (model != null) {
                this.gn = new JavaCompiler(model, this.DW);
                this.u7 = new JavaDebugger(model, this.DW, this);
                this.v5 = new t6(model.identifierSpace, model.Mr, false, this.DW, this.FH);
                this.Zo = new JavaParser(model.identifierSpace, model.Mr, model.entitySpace, (JavaSyntax)this.DW.getSyntax(), true);
                this.VH = new g6(model);
            }
        } catch (Throwable th) {
            if (EQ) {
                iy.j3(th, 70129601982276585L, (Object) null, model);
            }
            throw new Error(th);
        }
    }

    @ey(method = -1263038087072428472L)
    public void closeArchive() {
        
    }

    @ey(method = -794337822234581292L)
    public void fillSyntaxTree(FileEntry fileEntry, Reader reader, Map<Language, SyntaxTreeSytles> map) {
        try {
            if (tp) {
                iy.we(710567281588557536L, this, fileEntry, reader, map);
            }
            this.v5.Zo(fileEntry, reader, false, false, false, false, map.get(this.DW), map.get(this.FH));
        } catch (Throwable th) {
            if (EQ) {
                iy.U2(th, 710567281588557536L, this, fileEntry, reader, map);
            }
            throw new Error(th);
        }
    }

    @ey(method = -1350429167961427507L)
    public void fillSyntaxTree(FileEntry fileEntry, Reader reader, Map<Language, SyntaxTree> map, boolean z) {
        try {
            if (tp) {
                iy.J0(-218034071159248377L, this, fileEntry, reader, map, new Boolean(z));
            }
            SyntaxTreeSytles j6 = this.j6.U2.j6();
            SyntaxTreeSytles j62 = this.j6.U2.j6();
            this.v5.Zo(fileEntry, reader, false, false, map.containsKey(this.DW), map.containsKey(this.FH), j6, j62);
            if (map.containsKey(this.DW)) {
                this.Zo.v5(j6, fileEntry, z, map.get(this.DW));
            }
            this.j6.U2.DW(j6);
            if (map.containsKey(this.FH)) {
                this.VH.j6(j62, fileEntry, z, map.get(this.FH));
            }
            this.j6.U2.DW(j62);
        } catch (Throwable th) {
            if (EQ) {
                iy.a8(th, -218034071159248377L, this, fileEntry, reader, map, new Boolean(z));
            }
            throw new Error(th);
        }
    }

    @ey(method = 3316152580554248640L)
    public String[] getArchiveEntries(String str) {
        return new String[0];
    }

    @ey(method = 2872003654743124741L)
    public Reader getArchiveEntryReader(String str, String str2, String str3) {
        return null;
    }

    @ey(method = -489673516924675904L)
    public long getArchiveVersion(String str) {
        try {
            if (tp) {
                iy.tp(-5998745651620362160L, this, str);
            }
            return 0L;
        } catch (Throwable th) {
            if (EQ) {
                iy.j3(th, -5998745651620362160L, this, str);
            }
            throw new Error(th);
        }
    }

    @ey(method = 543554144713219595L)
    public Compiler getCompiler() {
        try {
            if (tp) {
                iy.gn(2902163857507914125L, this);
            }
            return this.gn;
        } catch (Throwable th) {
            if (EQ) {
                iy.aM(th, 2902163857507914125L, this);
            }
            throw new Error(th);
        }
    }

    @ey(method = 3208054804161263595L)
    public Debugger getDebugger() {
        try {
            if (tp) {
                iy.gn(-3386532466675789015L, this);
            }
            return this.u7;
        } catch (Throwable th) {
            if (EQ) {
                iy.aM(th, -3386532466675789015L, this);
            }
            throw new Error(th);
        }
    }

    @ey(method = 4604163969011308L)
    public String[] getDefaultFilePatterns() {
        try {
            if (tp) {
                iy.gn(1524412646518513528L, this);
            }
            return new String[]{"*.aidl"};
        } catch (Throwable th) {
            if (EQ) {
                iy.aM(th, 1524412646518513528L, this);
            }
            throw new Error(th);
        }
    }

    @ey(method = 35910227245426699L)
    public String[] getExtendFilePatterns() {
        try {
            if (tp) {
                iy.gn(6600921524517248785L, this);
            }
            return new String[0];
        } catch (Throwable th) {
            if (EQ) {
                iy.aM(th, 6600921524517248785L, this);
            }
            throw new Error(th);
        }
    }

    @ey(method = -1283683696029590559L)
    public List<Language> getLanguages() {
        try {
            if (tp) {
                iy.gn(3427234482816124071L, this);
            }
            ArrayList arrayList = new ArrayList();
            arrayList.add(this.DW);
            arrayList.add(this.FH);
            return arrayList;
        } catch (Throwable th) {
            if (EQ) {
                iy.aM(th, 3427234482816124071L, this);
            }
            throw new Error(th);
        }
    }

    @ey(method = -344292127604006240L)
    public String getName() {
        try {
            if (!tp) {
                return "AIDL";
            }
            iy.gn(1637614501702413600L, this);
            return "AIDL";
        } catch (Throwable th) {
            if (EQ) {
                iy.aM(th, 1637614501702413600L, this);
            }
            throw new Error(th);
        }
    }

    @ey(method = 2667161872060147275L)
    public Preprocessor getPreprocessor() {
        try {
            if (!tp) {
                return null;
            }
            iy.gn(2639016714457629289L, this);
            return null;
        } catch (Throwable th) {
            if (EQ) {
                iy.aM(th, 2639016714457629289L, this);
            }
            throw new Error(th);
        }
    }

    @ey(method = 2791350172500256731L)
    public boolean isSupportArchiveFile() {
        try {
            if (!tp) {
                return true;
            }
            iy.gn(2718291995474413845L, this);
            return true;
        } catch (Throwable th) {
            if (EQ) {
                iy.aM(th, 2718291995474413845L, this);
            }
            throw new Error(th);
        }
    }

    @ey(method = -743969176713109587L)
    public void processVersion(FileEntry fileEntry, Language language) {
        try {
            if (tp) {
                iy.EQ(1751226635276461475L, this, fileEntry, language);
            }
        } catch (Throwable th) {
            if (EQ) {
                iy.Mr(th, 1751226635276461475L, this, fileEntry, language);
            }
            throw new Error(th);
        }
    }

    @ey(method = 3926369485678524849L)
    public boolean u7() {
        try {
            if (!tp) {
                return false;
            }
            iy.gn(-1192286148093177885L, this);
            return false;
        } catch (Throwable th) {
            if (EQ) {
                iy.aM(th, -1192286148093177885L, this);
            }
            throw new Error(th);
        }
    }

    @ey(method = 944753589858639645L)
    public void update() {
        
    }
}

