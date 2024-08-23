package com.aide.ui.build.android;

import com.aide.engine.SyntaxError;
import java.util.List;
import java.util.Map;

public class AaptService$ErrorResult {
	// error info
    public String errorInfo;
    public Map<String, List<SyntaxError>> syntaxErrorsMap;
	// hasError
    public boolean hasError;
	
    public AaptService$ErrorResult(String str) {
		this.errorInfo = str;
	}
    public AaptService$ErrorResult(boolean z) {
		this.hasError = z;
    }
}

