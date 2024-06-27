//
// Decompiled by Jadx - 772ms
//
package com.aide.ui.build.android;

import abcd.cy;
import abcd.dy;
import abcd.ey;
import abcd.fy;
import abcd.gy;
import abcd.hy;
import abcd.iy;
import com.aide.engine.SyntaxError;
import com.aide.ui.build.android.a;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

@cy(clazz = -645956312330239873L, container = -645956312330239873L, user = true)
class a$c extends FutureTask<Map<String, List<SyntaxError>>> {

    @gy
    private static boolean jw;

    @fy
    private static boolean mb;

    @dy(field = 2046316356870119936L)
    @hy
    final a WB;

    static {
        iy.Zo(a$c.class);
    }

    /* JADX WARN: 'super' call moved to the top of the method (can break code semantics) */
    @ey(method = -7470700694817727821L)
    public a$c(a aVar, a$a aVar2) {
        super(aVar2);
        try {
            if (mb) {
                iy.EQ(3885362531017041375L, (Object) null, aVar, aVar2);
            }
            this.WB = aVar;
        } catch (Throwable th) {
            if (jw) {
                iy.Mr(th, 3885362531017041375L, (Object) null, aVar, aVar2);
            }
            if( th instanceof Error) throw (Error) th;
			throw new Error(th);
        }
    }

    @Override
    @ey(method = 1931302453843594495L)
    protected void done() {
        try {
            if (mb) {
                iy.gn(-1259753714771511360L, this);
            }
            if (isCancelled()) {
                return;
            }
            try {
                Map<String, List<SyntaxError>> map = get();
                if (map == null) {
                    a.j6(this.WB);
                } else {
                    a.DW(this.WB, map);
                }
            } catch (InterruptedException unused) {
                a.FH(this.WB);
            } catch (ExecutionException e) {
                a.Hw(this.WB, e.getCause());
            }
        } catch (Throwable th) {
            if (jw) {
                iy.aM(th, -1259753714771511360L, this);
            }
            if( th instanceof Error) throw (Error) th;             throw new Error(th);
        }
    }
}

