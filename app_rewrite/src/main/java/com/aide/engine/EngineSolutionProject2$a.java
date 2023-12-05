//
// Decompiled by Jadx - 2544ms
//
package com.aide.engine;

import android.os.Parcel;
import android.os.Parcelable;


public class EngineSolutionProject2$a implements Parcelable.Creator<EngineSolutionProject> {
	public EngineSolutionProject[] DW(int size) {
		return new EngineSolutionProject[size];
    }

    @Override
    public EngineSolutionProject createFromParcel(Parcel source) {
		return j6(source);
    }

    public EngineSolutionProject j6(Parcel source) {
        return new EngineSolutionProject(source);
    }

    @Override
    public EngineSolutionProject[] newArray(int size) {
        return DW(size);
    }
}

