package io.github.zeroaicy.aide.ui;

import android.content.Context;
import android.util.AttributeSet;
import com.aide.ui.services.OpenFileService;
import com.aide.ui.util.FileSystem;
import com.aide.ui.views.editor.EditorModel;
import com.aide.ui.views.editor.EditorModelKt;
import com.aide.ui.views.editor.TextBuffer;
import io.github.zeroaicy.aide.ui.services.ExecutorsService;
import java.io.IOException;
import java.io.Reader;
import java.util.Vector;
import com.aide.ui.App;
import com.aide.ui.views.editor.OConsole;

public class AIDEEditor extends com.aide.ui.AIDEEditor {
	public AIDEEditor(Context context) {
		this(context, null);
	}
	public AIDEEditor(Context context, AttributeSet attributeSet) {
		this(context, attributeSet, 0);
	}

	public AIDEEditor(Context context, AttributeSet attributeSet, int defStyleAttr) {
		super(context, attributeSet, defStyleAttr);
	}


	@Override
	protected OpenFileService.OpenFileModel Z1(final String string) {
		
		final AIDEEditorModel editorModel = new AIDEEditorModel(string);
		
		ExecutorsService.getExecutorsService().submit(
			new Runnable(){
				@Override
				public void run() {
					editorModel.init(FileSystem.Mz(string));
				}
			});
		// 先返回，内容异步塞入
		return editorModel;
	}

	public class AIDEEditorModel extends com.aide.ui.AIDEEditor.t {
		public AIDEEditorModel() {
			super();
		}
		String filePath;
		public AIDEEditorModel(String filePath) {
			this();
			this.filePath = filePath;
			
            this.sh = FileSystem.lg(this.filePath);
			
			// 补全 k1参数为null的操作
			this.dx = filePath;
			this.cb = com.aide.engine.service.l.Hw(filePath, App.Hw());
			fh();
		}

		public void init(Reader reader) {
			Vector<TextBuffer> textBuffers = EditorModelKt.getTextBuffers(this);
			// 重置
			textBuffers.clear();

			com.aide.ui.views.editor.v.j6(reader, new EditorModel.a(new StringBuffer(), false, getTabSize(), false), new char[32768]);
			if (textBuffers.size() == 0) {
                textBuffers.addElement(new TextBuffer());
            }
            try {
				reader.close();
			}
			catch (IOException e) {}

			textBuffers.trimToSize();
			// 更新 consoleMaxWidth
			// yS() -> this.indexingLayoutTask -> DW() -> FH() -> x9()[OConsole]
			getOEditorView().yS(this);
		}
	}
}
