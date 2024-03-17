//
// Decompiled by Jadx - 755ms
//
package com.aide.common;

import android.content.Context;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputConnection;

public class KeyStrokeDetector2 extends KeyStrokeDetector {


    private boolean rightAlt;

    private boolean EQ;

    private boolean leftShift;

    private boolean rightShift;

    private Context J0;

    private boolean VH;

    private boolean rightCtrl;

    private boolean gn;

    private boolean leftAlt;

    private int tp;

    private boolean u7;

    private boolean leftCtrl;

    private KeyCharacterMap we;


    public KeyStrokeDetector2(Context context) {
		super(context);

		this.J0 = context;
		boolean z = true;
		if (context.getResources().getConfiguration().keyboard != 1) {
			z = false;
		}
		this.EQ = z;
		we("new KeyStrokeDetector2() - isSoftKeyboard: " + this.EQ);

    }

    static int DW(KeyStrokeDetector2 KeyStrokeDetector2) {
        return KeyStrokeDetector2.tp;
    }

    static int FH(KeyStrokeDetector2 KeyStrokeDetector2, int i) {
        KeyStrokeDetector2.tp = i;
        return i;
    }

    static boolean Hw(KeyStrokeDetector2 KeyStrokeDetector2) {
        return KeyStrokeDetector2.EQ;
    }

    private void J0(String str, int i, KeyEvent keyEvent) {
		StringBuilder sb = new StringBuilder();
		sb.append(str);
		sb.append(" ");
		sb.append(i);
		sb.append("  ");
		sb.append(keyEvent.getFlags());
		sb.append(keyEvent.isAltPressed() ? " alt" : "");
		sb.append(keyEvent.isShiftPressed() ? " shift" : "");
		sb.append(" ");
		sb.append(isLeftCtrlPressed(keyEvent.getMetaState()) ? " ctrl" : "");
		we(sb.toString());
    }

    private void J8(KeyStroke keyStroke) {
		we("onKeyStroke " + keyStroke.toString());
	}

    private KeyStroke Mr(int keyCode, KeyEvent keyEvent) {
		if (keyCode == 0 || keyCode == 3 || keyCode == 4 || keyCode == 113 || keyCode == 114) {
			return null;
		}
		switch (keyCode) {
			case KeyEvent.KEYCODE_ALT_LEFT:
			case KeyEvent.KEYCODE_ALT_RIGHT:
			case KeyEvent.KEYCODE_SHIFT_LEFT:
			case KeyEvent.KEYCODE_SHIFT_RIGHT:
				return null;
				
			default:
				boolean isShiftPressed = this.leftShift | this.rightShift | keyEvent.isShiftPressed();
				
				boolean isCtrlPressed = this.leftCtrl | this.rightCtrl | isLeftCtrlPressed(keyEvent.getMetaState());
				
				boolean isAltPressed = this.leftAlt | this.rightAlt | keyEvent.isAltPressed();

				int unicodeChar = keyEvent.getUnicodeChar();

				char unicodeChar2 = (unicodeChar == 0 || Character.isISOControl(unicodeChar))
					? (char) 65535 : (char) unicodeChar;
				return new KeyStroke(keyCode, unicodeChar2, isShiftPressed, isCtrlPressed, isAltPressed);
		}
    }

    private void QX(int keyCode, boolean isSoftKeyBoard) {
        we("onMetaKeysUp " + keyCode);

        this.leftAlt &= keyCode != KeyEvent.KEYCODE_ALT_LEFT; /*57*/
        this.rightAlt &= keyCode != KeyEvent.KEYCODE_ALT_RIGHT; /*58*/

        this.leftShift &= keyCode != KeyEvent.KEYCODE_SHIFT_LEFT; /*59*/
        this.rightShift &= keyCode != KeyEvent.KEYCODE_SHIFT_RIGHT; /*60*/

        this.u7 &= keyCode != KeyEvent.KEYCODE_SHIFT_LEFT || isSoftKeyBoard;
        this.gn &= keyCode != KeyEvent.KEYCODE_SHIFT_RIGHT || isSoftKeyBoard;

        this.VH &= keyCode != KeyEvent.KEYCODE_UNKNOWN;

        this.leftCtrl &= keyCode != KeyEvent.KEYCODE_CTRL_LEFT;
        this.rightCtrl &= keyCode != KeyEvent.KEYCODE_CTRL_RIGHT;
    }

    static KeyStroke VH(KeyStrokeDetector2 KeyStrokeDetector2, char c) {
        return KeyStrokeDetector2.j3(c);
    }

    private void Ws(int keyCode, boolean isSoftKeyBoard) {
		we("onMetaKeysDown " + keyCode);

		this.leftAlt |= keyCode == KeyEvent.KEYCODE_ALT_LEFT;
		this.rightAlt |= keyCode == KeyEvent.KEYCODE_ALT_RIGHT;

		this.leftShift |= keyCode == KeyEvent.KEYCODE_SHIFT_LEFT;
		this.rightShift |= keyCode == KeyEvent.KEYCODE_SHIFT_RIGHT;
		
		// 非软键盘 shift
		this.u7 |= keyCode == KeyEvent.KEYCODE_SHIFT_LEFT && !isSoftKeyBoard;
		this.gn |= keyCode == KeyEvent.KEYCODE_SHIFT_RIGHT && !isSoftKeyBoard;

		this.VH |= keyCode == KeyEvent.KEYCODE_UNKNOWN;

		this.leftCtrl |= keyCode == KeyEvent.KEYCODE_CTRL_LEFT;

		this.rightCtrl |= keyCode == KeyEvent.KEYCODE_CTRL_RIGHT;

    }
	
	/**
	 * 通过 KeyEvent::getMetaState判断 LEFT CTRL 
	 */
    private boolean isLeftCtrlPressed(int i) {
		// 此掩码用于检查是否按下了CTRL元键之一
		// 此掩码用于检查是否按下了左CTRL元键。
		return (i & (KeyEvent.META_CTRL_ON | KeyEvent.META_CTRL_LEFT_ON)) != 0;
    }

    static boolean Zo(KeyStrokeDetector2 KeyStrokeDetector2) {
        return KeyStrokeDetector2.gn;
    }

    static KeyCharacterMap gn(KeyStrokeDetector2 KeyStrokeDetector2) {
        return KeyStrokeDetector2.we;
    }

    private KeyStroke j3(char c) {
		return new KeyStroke(-1, c, this.u7 | this.gn | Character.isUpperCase(c), false, false);
    }

    static void j6(KeyStrokeDetector2 KeyStrokeDetector2, String str) {
        KeyStrokeDetector2.we(str);
    }

    static Context tp(KeyStrokeDetector2 KeyStrokeDetector2) {
        return KeyStrokeDetector2.J0;
    }

    static KeyCharacterMap u7(KeyStrokeDetector2 KeyStrokeDetector2, KeyCharacterMap keyCharacterMap) {
        KeyStrokeDetector2.we = keyCharacterMap;
        return keyCharacterMap;
    }

    static boolean v5(KeyStrokeDetector2 KeyStrokeDetector2) {
        return KeyStrokeDetector2.u7;
    }

	/**
	 * log
	 */
    private void we(String str) {

    }

    public InputConnection EQ(View view, KeyStrokeHandler keyStrokeHandler) {
		return new KeyStrokeDetector$a(this, view, true, keyStrokeHandler, view);
    }

    public void U2() {
		this.tp = 0;
    }

    public void a8(int i, KeyEvent keyEvent) {
		Ws(i, (keyEvent.getFlags() & KeyEvent.FLAG_SOFT_KEYBOARD) != 0);
    }

    public boolean aM() {
		if (!this.leftCtrl) {
			if (!this.rightCtrl) {
				return false;
			}
		}
		return true;
    }

    public boolean er(int keyCode, KeyEvent keyEvent, KeyStrokeHandler keyStrokeHandler) {
		J0("onKeyDown", keyCode, keyEvent);
		int keyCode2 = keyCode == KeyEvent.KEYCODE_SEARCH ? KeyEvent.KEYCODE_ALT_LEFT : keyCode;

		Ws(keyCode2, (keyEvent.getFlags() & KeyEvent.FLAG_SOFT_KEYBOARD) != 0);

		KeyStroke Mr = Mr(keyCode2, keyEvent);

		if (Mr == null 
			|| !keyStrokeHandler.j6(Mr)) {
			//
			return keyCode == KeyEvent.KEYCODE_SEARCH;
		}
		// 打印 log
		J8(Mr);

		return true;
	}

    public void lg(int i, KeyEvent keyEvent) {
        QX(i, (keyEvent.getFlags() & KeyEvent.FLAG_SOFT_KEYBOARD) != 0);

    }

    public void rN(Context context) {
		boolean z = true;
		if (context.getResources().getConfiguration().keyboard != 1) {
			z = false;
		}
		this.EQ = z;
		we("KeyStrokeDetector2.onConfigChange() - isSoftKeyboard: " + this.EQ);
		this.we = null;
    }

    public boolean yS(int i, KeyEvent keyEvent, KeyStrokeHandler keyStrokeHandler) {
		J0("onKeyUp", i, keyEvent);
		int i2 = i == 84 ? 57 : i;
		QX(i2, (keyEvent.getFlags() & KeyEvent.FLAG_SOFT_KEYBOARD) != 0);
		return i == 84;

    }

	public interface KeyStrokeHandler extends KeyStrokeDetector.KeyStrokeHandler {
		public boolean j6(KeyStroke keyStroke);
	}

}

