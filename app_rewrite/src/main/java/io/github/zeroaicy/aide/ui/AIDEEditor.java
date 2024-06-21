package io.github.zeroaicy.aide.ui;

import android.content.Context;
import android.util.AttributeSet;
import com.aide.engine.OpenFile;
import com.aide.ui.ServiceContainer;
import com.aide.ui.services.OpenFileService;
import com.aide.ui.util.FileSystem;
import com.aide.ui.views.editor.EditorModel;
import com.aide.ui.views.editor.EditorModelKt;
import com.aide.ui.views.editor.TextBuffer;
import io.github.zeroaicy.aide.ui.services.ExecutorsService;
import java.io.IOException;
import java.io.Reader;
import java.util.Vector;
import android.graphics.Canvas;
import io.github.zeroaicy.util.IOUtils;
import com.aide.ui.services.EngineService;
import com.aide.ui.views.CodeEditText;
import com.aide.ui.views.editor.OConsole;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import io.github.zeroaicy.util.Log;
import java.util.concurrent.atomic.AtomicBoolean;

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
	
	
	/**
	 * 文件路径，也有可能是jar里的class
	 */
	@Override
	protected OpenFileService.OpenFileModel Z1(final String filePath) {
		final AIDEEditorModel editorModel = new AIDEEditorModel(filePath);
		// 先返回，内容异步塞入
		return editorModel;
	}


	public class AIDEEditorModel extends com.aide.ui.AIDEEditor.t {

		private static final String TAG = "AIDEEditorModel";
		
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
			fh();
			Vector<TextBuffer> textBuffers = EditorModelKt.getTextBuffers(this);
			textBuffers.addElement(new TextBuffer("异步加载中....".toCharArray()));

			// 异步
			ExecutorsService.getExecutorsService().submit(
				new Runnable(){
					@Override
					public void run() {
						init(FileSystem.Mz(AIDEEditorModel.this.filePath));
					}
				});

		}

		@Override
		public void j6() {
			// 异步
			ExecutorsService.getExecutorsService().submit(
				new Runnable(){
					@Override
					public void run() {
						j6Async();
					}
				});
		}
		public void j6Async() {
			synchronized (this) {
				super.j6();
			}			
		}
		// OpenFileModel 由代码分析进程通过aidl调用
		// 通过阻塞防止代码分析进程获取的是空内容
		// 虽然可以主动通知 代码分析进程，
		// 但是代码分析进程可能阻塞，比如分析代码中，不能够及时
		// 而调用此方法也在子线程以及aidl线程不会阻塞Ui线程
		@Override
		public void J0(OpenFile openFile) {
			if (initing.get()) {
				try {
					synchronized (this.lock) {
						//等待
						this.lock.wait();
					}
				}
				catch (Throwable e) {
					Log.d(TAG, "等待异步加载初始化错误", e);				}
			}
			super.J0(openFile);
		}


		// 为了等待

		private final Object lock = new Object();

		private final AtomicBoolean initing = new AtomicBoolean(true);

		public void init(Reader reader) {
			try {
				// 读取
				initReader(reader);
			}
			catch (Throwable e) {
				Log.d(TAG, "异步加载初始化", e);
			}
		}

		private void initReader(Reader reader) {
			synchronized (this) {
				// k1()
				this.cb = com.aide.engine.service.CodeModelFactory.findCodeModel(filePath, ServiceContainer.Hw());

				Vector<TextBuffer> textBuffers = EditorModelKt.getTextBuffers(this);
				// 重置
				textBuffers.clear();
				char[] bufferPool = new char[0x8000];

				com.aide.ui.views.editor.v.j6(reader, new EditorModel.a(new StringBuffer(), false, getTabSize(), false), bufferPool);
				IOUtils.close(reader);

				// 没有内容
				if (textBuffers.size() == 0) {
					textBuffers.addElement(new TextBuffer());
				}
				textBuffers.trimToSize();
				
				this.initing.set(false);
				synchronized (this.lock) {
					// 通知代码分析进程
					this.lock.notifyAll();
					this.lock.notifyAll();
				}

				final CodeEditText.EditorView oEditorView = getOEditorView();
				oEditorView.indexingLayoutTask.DW();
				oEditorView.invalidateLayoutTask.DW();

				EngineService engineService = ServiceContainer.getEngineService();
				engineService.ef();
				engineService.ei();
			}
		}
	}
}
