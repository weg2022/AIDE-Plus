//
// Decompiled by Jadx - 793ms
//
package com.aide.codemodel.language.aidl;

import abcd.cy;
import abcd.ey;
import abcd.fy;
import abcd.gy;
import abcd.iy;
import com.aide.codemodel.api.FileEntry;
import com.aide.codemodel.api.Model;
import com.aide.codemodel.api.SyntaxTree;
import com.aide.codemodel.api.SyntaxTreeStyles;
import com.aide.codemodel.api.abstraction.CodeModel;
import com.aide.codemodel.api.abstraction.Compiler;
import com.aide.codemodel.api.abstraction.Debugger;
import com.aide.codemodel.api.abstraction.Language;
import com.aide.codemodel.api.abstraction.Preprocessor;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.aide.codemodel.Highlighter;

@cy(clazz = 6949712858441021368L, container = 6949712858441021368L, user = true)
public class AidlCodeModel implements CodeModel {

    @gy
    private static boolean DW;

    @fy
    private static boolean j6;

	private Highlighter myHighlighter;

	private AidlLanguage myLanguage;

    static {
        iy.Zo(AidlCodeModel.class);
    }
	
	Model model;
	AidlLexer aidlLexer;
	
    @ey(method = 36659414205610416L)
    public AidlCodeModel(Model model) {
		
        try {
            if (j6) {
                iy.tp(-3100271096940072559L, (Object) null, model);
            }
			this.model = model;
			myLanguage = new AidlLanguage(model);
			aidlLexer = new AidlLexer();
			myHighlighter = new Highlighter(aidlLexer);
        } catch (Throwable th) {
            if (DW) {
                iy.j3(th, -3100271096940072559L, (Object) null, model);
            }
            if(th instanceof Error) throw (Error)th; else throw new Error(th);
        }
    }

    @ey(method = -3641933856937742513L)
    public void closeArchive() {
        try {
            if (j6) {
                iy.gn(4687294018032822096L, this);
            }
        } catch (Throwable th) {
            if (DW) {
                iy.aM(th, 4687294018032822096L, this);
            }
            if(th instanceof Error) throw (Error)th; else throw new Error(th);
        }
    }

    @ey(method = -2860436783176542765L)
    public void fillSyntaxTree(FileEntry fileEntry, Reader reader, Map<Language, SyntaxTreeStyles> map) {
        try {
            if (j6) {
                iy.we(-879286033351090304L, this, fileEntry, reader, map);
            }
			myHighlighter.highlight(fileEntry, reader, map.get(myLanguage));
        } catch (Throwable th) {
            if (DW) {
                iy.U2(th, -879286033351090304L, this, fileEntry, reader, map);
            }
            if(th instanceof Error) throw (Error)th; else throw new Error(th);
        }
    }

    @ey(method = 2470293890794684288L)
    public void fillSyntaxTree(FileEntry fileEntry, Reader reader, Map<Language, SyntaxTree> map, boolean z) {
        try {
            if (j6) {
                iy.J0(-420754971234526983L, this, fileEntry, reader, map, new Boolean(z));
            }
			if (map.containsKey(myLanguage)) {
				SyntaxTree syntaxTree = map.get(myLanguage);
				if (syntaxTree != null)
					syntaxTree.U2(syntaxTree.declareNode(0, true, new int[0], 0, 0, 1, 1));
			    //sa.DW(sa.j6(0, true, new int[0], 0, 0, 1, 1));
			}
        } catch (Throwable th) {
            if (DW) {
                iy.a8(th, -420754971234526983L, this, fileEntry, reader, map, new Boolean(z));
            }
            if(th instanceof Error) throw (Error)th; else throw new Error(th);
        }
    }

    @ey(method = 5849451323807206887L)
    public String[] getArchiveEntries(String str) {
        try {
            if (!j6) {
                return null;
            }
            iy.tp(-909846960641131220L, this, str);
            return null;
        } catch (Throwable th) {
            if (DW) {
                iy.j3(th, -909846960641131220L, this, str);
            }
            if(th instanceof Error) throw (Error)th; else throw new Error(th);
        }
    }

    @ey(method = 3954060409225664760L)
    public Reader getArchiveEntryReader(String str, String str2, String str3) {
        try {
            if (!j6) {
                return null;
            }
            iy.we(-2064562663960435247L, this, str, str2, str3);
            return null;
        } catch (Throwable th) {
            if (DW) {
                iy.U2(th, -2064562663960435247L, this, str, str2, str3);
            }
            if(th instanceof Error) throw (Error)th; else throw new Error(th);
        }
    }

    @ey(method = 2040231874625047915L)
    public long getArchiveVersion(String str) {
        try {
            if (!j6) {
                return 0L;
            }
            iy.tp(-4419479403991875548L, this, str);
            return 0L;
        } catch (Throwable th) {
            if (DW) {
                iy.j3(th, -4419479403991875548L, this, str);
            }
            if(th instanceof Error) throw (Error)th; else throw new Error(th);
        }
    }

    @ey(method = -4462979187785903708L)
    public Compiler getCompiler() {
        try {
            if (!j6) {
                return null;
            }
            iy.gn(962728229604130395L, this);
            return null;
        } catch (Throwable th) {
            if (DW) {
                iy.aM(th, 962728229604130395L, this);
            }
            if(th instanceof Error) throw (Error)th; else throw new Error(th);
        }
    }

    @ey(method = -1765898278405099200L)
    public Debugger getDebugger() {
        try {
            if (!j6) {
                return null;
            }
            iy.gn(3425350175512247795L, this);
            return null;
        } catch (Throwable th) {
            if (DW) {
                iy.aM(th, 3425350175512247795L, this);
            }
            if(th instanceof Error) throw (Error)th; else throw new Error(th);
        }
    }

    @ey(method = 22993740422317037L)
    public String[] getDefaultFilePatterns() {
        try {
            if (j6) {
                iy.gn(-4915508830245677792L, this);
            }
            return new String[]{"*.aidl"};
        } catch (Throwable th) {
            if (DW) {
                iy.aM(th, -4915508830245677792L, this);
            }
            if(th instanceof Error) throw (Error)th; else throw new Error(th);
        }
    }

    @ey(method = -67344288115762064L)
    public String[] getExtendFilePatterns() {
        try {
            if (j6) {
                iy.gn(151673034510213295L, this);
            }
            return new String[0];
        } catch (Throwable th) {
            if (DW) {
                iy.aM(th, 151673034510213295L, this);
            }
            if(th instanceof Error) throw (Error)th; else throw new Error(th);
        }
    }

    @ey(method = -3657681744464385708L)
    public List<Language> getLanguages() {
        try {
            if (j6) {
                iy.gn(4654920932976428985L, this);
            }
			List<Language> list=new ArrayList<>();
			list.add(myLanguage);
			return list;
        } catch (Throwable th) {
            if (DW) {
                iy.aM(th, 4654920932976428985L, this);
            }
            if(th instanceof Error) throw (Error)th; else throw new Error(th);
        }
    }

    @ey(method = -2217810603626894101L)
    public String getName() {
        try {
            if (!j6) {
                return "AIDL";
            }
            iy.gn(1464193926131869188L, this);
            return "AIDL";
        } catch (Throwable th) {
            if (DW) {
                iy.aM(th, 1464193926131869188L, this);
            }
            if(th instanceof Error) throw (Error)th; else throw new Error(th);
        }
    }

    @ey(method = -942823976672949672L)
    public Preprocessor getPreprocessor() {
        try {
            if (!j6) {
                return null;
            }
            iy.gn(-3328238559875699201L, this);
            return null;
        } catch (Throwable th) {
            if (DW) {
                iy.aM(th, -3328238559875699201L, this);
            }
            if(th instanceof Error) throw (Error)th; else throw new Error(th);
        }
    }

    @ey(method = -1665509274194170872L)
    public boolean isSupportArchiveFile() {
        try {
            if (!j6) {
                return false;
            }
            iy.gn(2789734261390442975L, this);
            return false;
        } catch (Throwable th) {
            if (DW) {
                iy.aM(th, 2789734261390442975L, this);
            }
            if(th instanceof Error) throw (Error)th; else throw new Error(th);
        }
    }

    @ey(method = -2352837954259293108L)
    public void processVersion(FileEntry fileEntry, Language language) {
        try {
            if (j6) {
                iy.EQ(-688434963930260723L, this, fileEntry, language);
            }
        } catch (Throwable th) {
            if (DW) {
                iy.Mr(th, -688434963930260723L, this, fileEntry, language);
            }
            if(th instanceof Error) throw (Error)th; else throw new Error(th);
        }
    }

    @ey(method = 1567655813524964112L)
    public boolean u7() {
        try {
            if (!j6) {
                return true;
            }
            iy.gn(22909411606814369L, this);
            return true;
        } catch (Throwable th) {
            if (DW) {
                iy.aM(th, 22909411606814369L, this);
            }
            if(th instanceof Error) throw (Error)th; else throw new Error(th);
        }
    }

    @ey(method = -2887196389108170100L)
    public void update() {
        try {
            if (j6) {
                iy.gn(-44590475551519715L, this);
            }
        } catch (Throwable th) {
            if (DW) {
                iy.aM(th, -44590475551519715L, this);
            }
            if(th instanceof Error) throw (Error)th; else throw new Error(th);
        }
    }
}

