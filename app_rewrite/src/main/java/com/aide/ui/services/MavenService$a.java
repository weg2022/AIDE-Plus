//
// Decompiled by Jadx - 1587ms
//
package com.aide.ui.services;

import abcd.cy;
import abcd.dy;
import abcd.ey;
import abcd.fy;
import abcd.gy;
import abcd.hy;
import abcd.iy;
import com.aide.ui.util.FileSystem;
import java.io.IOException;

@cy(clazz = -1020255049470345640L, container = 81765335761816225L, user = true)
class MavenService$a implements Runnable {
    @hy
    final MavenService WB;

    static {
        iy.Zo(MavenService$a.class);
    }

    @ey(method = 4658198147752472505L)
    public MavenService$a(MavenService mavenService) {
        this.WB = mavenService;
    }

    @Override
    @ey(method = 2387352738165188657L)
    public void run() {
        try {
            try {
                MavenService.j6(this.WB).clear();
                FileSystem.VH(MavenService.DW());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Throwable th) {
			throw new Error(th);
        }
    }
}

