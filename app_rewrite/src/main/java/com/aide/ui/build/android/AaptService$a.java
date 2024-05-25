//
// Decompiled by Jadx - 812ms
//
package com.aide.ui.build.android;

import com.aide.ui.build.android.AaptService;
import java.util.List;
import java.util.concurrent.Callable;

class AaptService$a implements Callable<AaptService$b> {
    final AaptService DW;
	private AaptService$a.TaskFactory taskFactory;

	public interface TaskFactory {
		List<AaptService$c> getTasks();
	} 

    public AaptService$a(AaptService aaptService, TaskFactory taskFactory) {
        try {
            this.DW = aaptService;
            this.taskFactory = taskFactory;
        }
		catch (Throwable th) {
            throw new Error(th);
        }
    }

    @Override
    public AaptService$b call() {
        try {
			List<AaptService$c> tasks = taskFactory.getTasks();

            boolean hasError = false;
            for (AaptService$c cVar : tasks) {
                AaptService$b aaptService$b = cVar.we();
                hasError |= aaptService$b.j6;
                if (aaptService$b.DW != null) {
                    aaptService$b.FH = AaptService.v5(this.DW, AaptService$c.DW(cVar), AaptService$c.FH(cVar), aaptService$b.DW);
                    return aaptService$b;
                }
            }
            return new AaptService$b(hasError);
        }
		catch (Throwable th) {
            throw new Error(th);
        }
    }
}

