//
// Decompiled by Jadx - 812ms
//
package com.aide.ui.build.android;

import abcd.cy;
import abcd.dy;
import abcd.ey;
import abcd.fy;
import abcd.gy;
import abcd.hy;
import abcd.iy;
import com.aide.ui.build.android.AaptService;
import java.util.List;
import java.util.concurrent.Callable;

@cy(clazz = -7263059374909668792L, container = -7263059374909668792L, user = true)
class AaptService$a implements Callable<AaptService$b> {

    @fy
    private static boolean FH;

    @gy
    private static boolean Hw;

    @dy(field = 2651228717558677305L)
    @hy
    final AaptService DW;

    @dy(field = 2409792912754388595L)
    private List<AaptService$c> j6;

    static {
        iy.Zo(AaptService$a.class);
    }

    @ey(method = 6449555599739685485L)
    public AaptService$a(AaptService aaptService, List<AaptService$c> list) {
        try {
            if (FH) {
                iy.EQ(-2251170295023562080L, (Object) null, aaptService, list);
            }
            this.DW = aaptService;
            this.j6 = list;
        } catch (Throwable th) {
            if (Hw) {
                iy.Mr(th, -2251170295023562080L, (Object) null, aaptService, list);
            }
            throw new Error(th);
        }
    }

    @Override
    @ey(method = 2696294693953240200L)
    public AaptService$b call() {
        try {
            if (FH) {
                iy.gn(-1868388295617561415L, this);
            }
            boolean z = false;
            for (AaptService$c cVar : this.j6) {
                AaptService$b we = cVar.we();
                z |= we.j6;
                if (we.DW != null) {
                    we.FH = AaptService.v5(this.DW, AaptService$c.DW(cVar), AaptService$c.FH(cVar), we.DW);
                    return we;
                }
            }
            return new AaptService$b(z);
        } catch (Throwable th) {
            if (Hw) {
                iy.aM(th, -1868388295617561415L, this);
            }
            throw new Error(th);
        }
    }
}

