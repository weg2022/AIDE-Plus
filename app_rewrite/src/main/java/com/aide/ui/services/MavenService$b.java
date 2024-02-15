//
// Decompiled by Jadx - 638ms
//
package com.aide.ui.services;

import abcd.cy;
import abcd.ey;
import abcd.fy;
import abcd.gy;
import abcd.iy;
import com.aide.ui.App;

class MavenService$b implements Runnable {

    public MavenService$b(MavenService mavenService) {}

    public void run() {
        try {
            App.getProjectService().CU();
        } catch (Throwable th) {
			throw new Error(th);
        }
    }
}

