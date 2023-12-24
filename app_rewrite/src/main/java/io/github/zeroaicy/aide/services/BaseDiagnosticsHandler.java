package io.github.zeroaicy.aide.services;
import com.android.tools.r8.Diagnostic;
import com.android.tools.r8.DiagnosticsHandler;
import com.android.tools.r8.DiagnosticsLevel;
import io.github.zeroaicy.util.Log;
import com.android.tools.r8.origin.Origin;
import com.android.tools.r8.origin.PathOrigin;

public class BaseDiagnosticsHandler implements DiagnosticsHandler{
	public static final String TAG = "R8 minify";
	private boolean error;

	private String diagnosticMessage;
	
	public boolean hasError(){
		return false;
	}
	public String getErrorMessage(){
		return diagnosticMessage;
	}
	
	@Override
	public void error(Diagnostic error){
		this.error = true;
		this.diagnosticMessage = error.getDiagnosticMessage();
		Origin origin = error.getOrigin();
		if ( origin != null && origin instanceof PathOrigin){
			diagnosticMessage += " -> " + (PathOrigin)origin;
		}
		Log.d(TAG, diagnosticMessage);
	}
	
	@Override
	public DiagnosticsLevel modifyDiagnosticsLevel(DiagnosticsLevel level, Diagnostic diagnostic){
		return level;
	}

	@Override
	public void info(Diagnostic info){}

	@Override
	public void warning(Diagnostic warning) {}
}
