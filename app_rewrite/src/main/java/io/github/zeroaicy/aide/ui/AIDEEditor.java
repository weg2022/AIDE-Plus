package io.github.zeroaicy.aide.ui;

import android.content.Context;
import android.util.AttributeSet;
import com.aide.ui.App;
import com.aide.ui.services.OpenFileService;
import com.aide.ui.util.FileSystem;
import com.aide.ui.views.editor.EditorModel;
import com.aide.ui.views.editor.EditorModelKt;
import com.aide.ui.views.editor.TextBuffer;
import io.github.zeroaicy.aide.ui.services.ExecutorsService;
import java.io.IOException;
import java.io.Reader;
import java.util.Vector;
import com.aide.ui.views.editor.ModelListener;
import com.aide.ui.views.editor.Model;
import io.github.zeroaicy.util.Log;
import com.android.tools.r8.ir.optimize.j0;
import com.aide.engine.OpenFile;

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
		// 异步
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
		
		// OpenFileModel 由代码分析进程通过aidl调用
		// 通过阻塞防止代码分析进程获取的是空内容
		// 虽然可以主动通知 代码分析进程，
		// 但是代码分析进程可能阻塞，比如分析代码中，不能够及时
		// 而调用此方法也在子线程以及aidl线程不会阻塞Ui线程
		@Override
		public void J0(OpenFile openFile) {
			
			if( !inited){
				try {
					//等待
					this.lock.wait();
				}
				catch (InterruptedException e) {}
			}
			
			super.J0(openFile);
		}
		
		private final Object lock = new Object();
		
		private boolean inited = false;
		public void init(Reader reader) {
			// 读取
			synchronized (this) {
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
				
				/*
				int lineCount = textBuffers.size() - 1;
				TextBuffer elementAt = textBuffers.elementAt(lineCount);
				int columnCount = elementAt.J0();
				XG(0, 0, lineCount, columnCount);
				*/
				// 通知EngineService
				//App.we().ei();
				
				// 更新 consoleMaxWidth
				// yS() -> this.indexingLayoutTask -> DW() -> FH() -> x9()[OConsole]
				
				// 通知编辑器
				getOEditorView().yS(this);
				
				this.inited = true;
				// 通知已经初始化
				this.lock.notifyAll();
			}
		}
	}
}
