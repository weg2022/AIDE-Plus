

//
// Decompiled by Jadx - 982ms
//
package com.aide.common;

import abcd.cy;
import abcd.dy;
import abcd.ey;
import abcd.fy;
import abcd.gy;
import abcd.hy;
import abcd.i2;
import abcd.iy;
import abcd.th;
import android.os.Build;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.BaseInputConnection;
import android.view.inputmethod.CompletionInfo;
import android.view.inputmethod.CorrectionInfo;
import android.view.inputmethod.ExtractedText;
import android.view.inputmethod.ExtractedTextRequest;
import com.aide.common.KeyStrokeDetector;
import com.aide.common.KeyStrokeDetector$a;
import com.aide.ui.App;
import com.aide.ui.MainActivity;
import com.aide.ui.util.FileSpan;
import com.aide.ui.views.editor.OEditor;
import java.io.StringReader;
import io.github.zeroaicy.util.*;



public class KeyStrokeDetector$a extends BaseInputConnection {
    public static final String TAG = "KeyStrokeDetector$a";

	/*
	 KeyStrokeEditText[EdittextView] 
	 || CodeEditText$EditorView[OEditor[OConsole[View]]]
	 || CompletionListView[CustomKeysListView[ListView]]
	 */
	//只有自己引用，可以改名(DW)
    final View editorTextView;

	final OEditor oEditor;

    //只有自己引用，可以改名(FH)
    final KeyStrokeDetector keyStrokeDetector;
	//只有自己引用，可以改名(j6)
    final KeyStrokeDetector.KeyStrokeHandler KeyStrokeHandler;

	public KeyStrokeDetector$a(KeyStrokeDetector keyStrokeDetector, View view, boolean fullEditor, KeyStrokeDetector.KeyStrokeHandler keyStrokeHandler, View editorTextView) {
        super(view, fullEditor);
        this.keyStrokeDetector = keyStrokeDetector;
        this.KeyStrokeHandler = keyStrokeHandler;
		//view2与view就是一个
        this.editorTextView = editorTextView;

		if (this.editorTextView instanceof OEditor) {
			oEditor = (OEditor)this.editorTextView;
		} else {
			oEditor = null;
		}
    }

	/*修改*/
	@Override
	public ExtractedText getExtractedText(ExtractedTextRequest extractedTextRequest, int i) {
		ExtractedText extractedText = new ExtractedText();
		extractedText.selectionStart = 1;
		extractedText.text = "Test";
		extractedText.startOffset = 0;
		int flags = J0() ? ExtractedText.FLAG_SELECTING : ExtractedText.FLAG_SINGLE_LINE;
		extractedText.flags = flags;
		extractedText.selectionEnd = flags;
		
		return extractedText;
	}

	public boolean J0() {
		return true;
	}

	@Override
	public boolean performContextMenuAction(int id) {
        switch (id) {
			case android.R.id.selectAll:
				LogD("全选--start");
                MainActivity rN = App.rN();
                if (rN == null) return true;
                rN.w9();
                FileSpan currentFileSpan = App.rN().sh().getCurrentFileSpan();
                com.aide.ui.App.we().QX(currentFileSpan.j6, currentFileSpan.DW, currentFileSpan.FH, currentFileSpan.Hw, currentFileSpan.v5);
				LogD("全选--end");
                return true;
			case android.R.id.cut:
                if (this.editorTextView instanceof OEditor) {
                    LogD("剪切--start");
					((OEditor)this.editorTextView).b();
					LogD("剪切--end");
                }
                return true;
			case android.R.id.copy:
                if (this.editorTextView instanceof OEditor) {
                    LogD("剪切--start");
					((OEditor)this.editorTextView).vJ();
                    ((OEditor)this.editorTextView).setSelectionVisibility(false);
					LogD("剪切--end");
				}
                return true;
			case android.R.id.paste:
                //com.aide.ui.App.rN().sh().jw();
                if (this.editorTextView instanceof OEditor) {
                    ((OEditor)this.editorTextView).tj();
                }
				return true;

		}
		return super.performContextMenuAction(id);
	}

    private void DW(CharSequence commitText, boolean z, View view) {
		if (KeyStrokeDetector.gn(this.keyStrokeDetector) == null) {
			KeyStrokeDetector.u7(this.keyStrokeDetector, KeyCharacterMap.load(0));
		}
		for (int i = 0; i < commitText.length(); i++) {
			char charAt = commitText.charAt(i);
			if (!z) {
				if (KeyStrokeDetector.v5(this.keyStrokeDetector) 
					|| KeyStrokeDetector.Zo(this.keyStrokeDetector)) {
					charAt = Character.toUpperCase(charAt);
				} else {
					charAt = Character.toLowerCase(charAt);
				}
			}
			KeyEvent[] events = KeyStrokeDetector.gn(this.keyStrokeDetector).getEvents(new char[]{charAt});
			if (events != null) {
				for (KeyEvent keyEvent : events) {
					sendKeyEvent(keyEvent);
				}
			}
		}
    }

    private KeyEvent FH(KeyEvent keyEvent) {
		return new KeyEvent(keyEvent.getDownTime(), keyEvent.getEventTime(), keyEvent.getAction(), keyEvent.getKeyCode(), keyEvent.getRepeatCount(), keyEvent.getMetaState(), keyEvent.getDeviceId(), keyEvent.getScanCode(), keyEvent.getFlags() | 4 | 2);
    }

    private void j6(CharSequence text, boolean z, KeyStrokeDetector.KeyStrokeHandler bVar) {
        for (int index = 0; index < text.length(); index++) {
			char c = text.charAt(index);
			if (!z) {
				//是否是快捷键，否则小写
				if (KeyStrokeDetector.v5(this.keyStrokeDetector) 
					|| KeyStrokeDetector.Zo(this.keyStrokeDetector)) {
					c = Character.toUpperCase(c);
				} else {
					c = Character.toLowerCase(c);
				}
			}

			if (bVar != null) {
				bVar.j6(KeyStrokeDetector.VH(this.keyStrokeDetector, c));
			}
		}
    }

    @Override
    public boolean beginBatchEdit() {
		LogD("beginBatchEdit");
		return super.beginBatchEdit();
    }

    @Override
    public boolean commitCompletion(CompletionInfo completionInfo) {
        LogD("commitCompletion");
		return super.commitCompletion(completionInfo);
    }

    @Override
    public boolean commitCorrection(CorrectionInfo correctionInfo) {
		LogD("commitCorrection");
		return super.commitCorrection(correctionInfo);
    }

    @Override
    public boolean commitText(CharSequence text, int newCursorPosition) {
		String commitText;
		if (text instanceof String) {
			commitText = (String)text;
		} else {
			commitText = text.toString();
		}

		LogD("commitText: ['" + text + "']");

		if (Build.VERSION.SDK_INT >= 17) {
			for (int i2 = 0; i2 < KeyStrokeDetector.DW(this.keyStrokeDetector); i2++) {
				this.KeyStrokeHandler.j6(new KeyStroke('C', false, false, false));
			}
		} else if (KeyStrokeDetector.DW(this.keyStrokeDetector) > 0 && text.length() == 1 && text.charAt(0) == ' ') {
			KeyStrokeDetector.FH(this.keyStrokeDetector, 0);
			return true;
		}
		KeyStrokeDetector.FH(this.keyStrokeDetector, 0);

		if ("\n".equals(commitText)) {
			DW(text, KeyStrokeDetector.Hw(this.keyStrokeDetector), this.editorTextView);
		} else if (oEditor != null 
				   && commitText.indexOf('\n') >= 0) {
			int newLineNumber = 0;
			for (int offset = 0; offset < commitText.length(); offset++) {
				if (commitText.charAt(offset) == '\n') {
					newLineNumber++;
				}
			}
			int caretLine = oEditor.getCaretLine();
			int endLineNumber = newLineNumber + caretLine;
			//粘贴
			oEditor.getEditorModel().ys(oEditor.getCaretColumn(), oEditor.getCaretLine(), oEditor.jw(), oEditor.getTabSize(), new StringReader(commitText), this);
			//更新行信息
			oEditor.eN(caretLine, endLineNumber);				
			return true;
		} else {
			//不是OEditor或
			j6(text, KeyStrokeDetector.Hw(this.keyStrokeDetector), this.KeyStrokeHandler);
		}
		return true;
    }

    @Override
    public boolean deleteSurroundingText(int i, int i2) {
		LogD("deleteSurroundingText " + i + " " + i2);

		KeyStrokeDetector.FH(this.keyStrokeDetector, 0);
		for (int index = 0; index < i; index++) {
			this.KeyStrokeHandler.j6(new KeyStroke('C', false, false, false));
		}
		return super.deleteSurroundingText(i, i2);
    }

    @Override
    public boolean endBatchEdit() {
		LogD("endBatchEdit");
		return super.endBatchEdit();
    }

    @Override
    public boolean finishComposingText() {
		LogD("finishComposingText");
		return super.finishComposingText();
    }

    @Override
    public CharSequence getTextBeforeCursor(int i, int i2) {
		if (AndroidHelper.U2(KeyStrokeDetector.tp(this.keyStrokeDetector))) {
			return super.getTextBeforeCursor(i, i2);
		}
		int min = Math.min(i, 1024);
		StringBuilder sb = new StringBuilder(min);
		for (int i3 = 0; i3 < min; i3++) {
			sb.append(' ');
		}
		return sb;
    }

    @Override
    public boolean performEditorAction(int i) {
		LogD("performEditorAction" + i);
		return super.performEditorAction(i);
    }

    @Override
    public boolean sendKeyEvent(KeyEvent keyEvent) {
		LogD("sendKeyEvent " + keyEvent.getKeyCode());
		KeyStrokeDetector.FH(this.keyStrokeDetector, 0);
		return super.sendKeyEvent(FH(keyEvent));
    }

    @Override
    public boolean setComposingText(CharSequence text, int newCursorPosition) {
		// 实际KeyStrokeDetector->we[空方法]
		LogD("setComposingText '" + ((Object) text) + "'");

		for (int i2 = 0; i2 < KeyStrokeDetector.DW(this.keyStrokeDetector); i2++) {
			this.KeyStrokeHandler.j6(new KeyStroke('C', false, false, false));
		}
		KeyStrokeDetector.FH(this.keyStrokeDetector, text.length());

		j6(text, KeyStrokeDetector.Hw(this.keyStrokeDetector), this.KeyStrokeHandler);

		return true;
	}

	// 实际KeyStrokeDetector->we[空方法]
	public void LogD(String charSequence) {
		//KeyStrokeDetector.j6(keyStrokeDetector, charSequence);
		//Log.d(TAG, charSequence);
	}
}

