//
// Decompiled by Jadx - 763ms
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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

class AaptService$d extends FutureTask<AaptService$b> {


    final AaptService aaptService;


    public AaptService$d(AaptService aaptService, AaptService$a aVar) {
        super(aVar);
        try {
            this.aaptService = aaptService;
        } catch (Throwable th) {
            throw new Error(th);
        }
    }

    @Override
    protected void done() {
        try {
            if (isCancelled()) {
                return;
            }
            try {
                AaptService$b bVar = get();
                if (bVar.FH == null) {
                    AaptService.j6(this.aaptService, bVar.j6);
                } else {
                    AaptService.DW(this.aaptService, bVar.FH);
                }
            } catch (InterruptedException unused) {
                AaptService.FH(this.aaptService);
            } catch (ExecutionException e) {
                AaptService.Hw(this.aaptService, e.getCause());
            }
        } catch (Throwable th) {
            throw new Error(th);
        }
    }
}

