//
// Decompiled by Jadx - 812ms
//
package com.aide.ui.build.android;

import com.aide.ui.build.android.AaptService;
import java.util.List;
import java.util.concurrent.Callable;

class AaptService$a implements Callable<AaptService$ErrorResult> {
    final AaptService DW;
	private AaptService$a.TaskFactory taskFactory;

	public interface TaskFactory {
		List<AaptService$Args> getTasks();
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
    public AaptService$ErrorResult call() {
        try {
			List<AaptService$Args> tasks = taskFactory.getTasks();

            boolean hasError = false;
            for (AaptService$Args cVar : tasks) {
                AaptService$ErrorResult aaptService$b = cVar.we();
                hasError |= aaptService$b.hasError;
                if (aaptService$b.errorInfo != null) {
                    aaptService$b.syntaxErrorsMap = AaptService.v5(this.DW, AaptService$Args.DW(cVar), AaptService$Args.FH(cVar), aaptService$b.errorInfo);
                    return aaptService$b;
                }
            }
            return new AaptService$ErrorResult(hasError);
        }
		catch (Throwable th) {
            throw new Error(th);
        }
    }
}

