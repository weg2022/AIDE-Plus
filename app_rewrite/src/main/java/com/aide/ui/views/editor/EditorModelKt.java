package com.aide.ui.views.editor;
import java.util.Vector;
import io.github.zeroaicy.aide.ui.AIDEEditor.AIDEEditorModel;

public class EditorModelKt {
	public static Vector<TextBuffer> getTextBuffers(EditorModel editorModel) {
		return EditorModel.ko(editorModel);
	}
}
