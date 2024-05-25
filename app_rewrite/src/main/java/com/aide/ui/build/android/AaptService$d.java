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
import io.github.zeroaicy.util.Log;

class AaptService$d extends FutureTask<AaptService$b> {


    final AaptService aaptService;
	
    public AaptService$d(AaptService aaptService, AaptService$a aVar) {
        super(aVar);
		this.aaptService = aaptService;
    }

    @Override
    protected void done() {
		if (isCancelled()) {
			return;
		}
		try {
			AaptService$b aaptService$b = get();
			if (aaptService$b.FH == null) {
				//Log.e(this.toString(), aaptService$b.toString(), new Throwable());
				AaptService.j6(this.aaptService, aaptService$b.j6);
				
				
			} else {
				AaptService.DW(this.aaptService, aaptService$b.FH);
			}
		}
		catch (InterruptedException unused) {
			AaptService.FH(this.aaptService);
		}
		catch (ExecutionException e) {
			AaptService.Hw(this.aaptService, e.getCause());
		}
    }
}

