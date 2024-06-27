//
// Decompiled by Jadx - 646ms
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
import java.util.concurrent.Callable;

@cy(clazz = 6522210001655193240L, container = 6522210001655193240L, user = true)
class a$a implements Callable<Map<String, List<SyntaxError>>> {

    @fy
    private static boolean FH;

    @gy
    private static boolean Hw;

    @dy(field = 3008899555223405220L)
    @hy
    final a DW;

    @dy(field = -22998293770523960L)
    private List<a$b> j6;

    static {
        iy.Zo(a$a.class);
    }
	public interface TaskFactory {
		List<a$b> getTasks();
	} 
	
	private a$a.TaskFactory taskFactory;
	
    @ey(method = 4957435647640520224L)
    public a$a(a aVar, TaskFactory taskFactory) {
        try {
            if (FH) {
                iy.EQ(-1214405530551007235L, (Object) null, aVar, taskFactory);
            }
            this.DW = aVar;
			this.taskFactory = taskFactory;
        } catch (Throwable th) {
            if (Hw) {
                iy.Mr(th, -1214405530551007235L, (Object) null, aVar, taskFactory);
            }
            if( th instanceof Error) throw (Error) th;
            throw new Error(th);
        }
    }

    @Override
    @ey(method = -2245564065867158435L)
    public Map<String, List<SyntaxError>> call() {
        try {
            if (FH) {
                iy.gn(182277424535641056L, this);
            }
			this.j6 = this.taskFactory.getTasks();
            for (a$b bVar : this.j6) {
                String gn = bVar.gn();
                if (gn != null) {
                    return a.v5(this.DW, a$b.DW(bVar), gn);
                }
            }
            return null;
        } catch (Throwable th) {
            if (Hw) {
                iy.aM(th, 182277424535641056L, this);
            }
            if( th instanceof Error) throw (Error) th;
            throw new Error(th);
        }
    }
}

