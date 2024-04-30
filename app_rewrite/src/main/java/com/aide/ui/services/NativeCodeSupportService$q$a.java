//
// Decompiled by Jadx - 645ms
//
package com.aide.ui.services;

import com.aide.ui.services.NativeCodeSupportService;

class NativeCodeSupportService$q$a implements Runnable {
    final boolean WB;
    final NativeCodeSupportService$q mb;
    public NativeCodeSupportService$q$a(NativeCodeSupportService$q qVar, boolean z) {
        this.mb = qVar;
        this.WB = z;
	}

    @Override
    public void run() {
        try {
            NativeCodeSupportService.FH(this.mb.v5);
            if (this.WB) {
                NativeCodeSupportService$q.j6(this.mb).run();
            }
        } catch (Throwable th) {
           throw new Error(th);
        }
    }
}
