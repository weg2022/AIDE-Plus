package io.github.zeroaicy.aide.ui;

import android.content.Context;
import android.util.AttributeSet;
import com.aide.engine.OpenFile;
import com.aide.engine.SyntaxStyleType;
import com.aide.ui.ServiceContainer;
import com.aide.ui.services.EngineService;
import com.aide.ui.services.OpenFileService;
import com.aide.ui.util.FileSystem;
import com.aide.ui.views.CodeEditText;
import com.aide.ui.views.editor.EditorModel;
import com.aide.ui.views.editor.EditorModelKt;
import com.aide.ui.views.editor.TextBuffer;
import com.aide.ui.views.editor.TextStyle;
import io.github.zeroaicy.aide.ui.services.ThreadPoolService;
import io.github.zeroaicy.util.IOUtils;
import io.github.zeroaicy.util.Log;
import java.io.Reader;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;
import android.graphics.Typeface;
import com.aide.ui.AppPreferences;
import com.aide.common.AndroidHelper;
import com.aide.ui.views.editor.Color;
import com.aide.ui.rewrite.R;
import io.github.zeroaicy.aide.highlight.ColorKind;
import android.graphics.Canvas;
import com.aide.common.AppLog;

public class AIDEEditor extends com.aide.ui.AIDEEditor {
	
	public class EditorView extends CodeEditText.EditorView {

		private boolean jn = true;
		private AIDEEditor ee = AIDEEditor.this;

		public EditorView(Context context) {
			super(context);
		}

		@Override
		protected void onDraw(Canvas canvas) {
			super.onDraw(canvas);
		}

		@Override
		public void initColors() {
			super.initColors();

			if (this.ee == null) {
                return;
            }
			boolean isLight = this.ee.isLightTheme();
			// is Material主题
			if (AndroidHelper.isMaterialTheme(getContext())) {
                //this.selectionColor = new Color(getResources().getColor(isLight ? R.color.editor_selection_material_light : R.color.editor_selection_material));
				this.selectionColor = new Color(io.github.zeroaicy.aide.highlight.ColorKind.EDITOR_SELECTION.getColor(getContext(), isLight));
            } else {
                this.selectionColor = new Color(getResources().getColor(isLight ? R.color.editor_selection_light : R.color.editor_selection));
            }

			// 背景颜色
            //this.graphicsColor = new Color(getResources().getColor(isLight ? R.color.editor_background_light : R.color.editor_background));
			this.graphicsColor = new Color(io.github.zeroaicy.aide.highlight.ColorKind.EDITOR_BACKGROUND.getColor(getContext(), isLight));

			// 光标所在行背景色
			this.Za = this.jn ? new Color(getResources().getColor(isLight ? R.color.editor_caret_line_light : R.color.editor_caret_line)): null;

			this.stepbarColor = this.jn ? new Color(getResources().getColor(isLight ? R.color.editor_stepping_bar_light : R.color.editor_stepping_bar)) : null;

            this.Pa = new Color(getResources().getColor(isLight ? R.color.editor_caret_light : R.color.editor_caret));

            this.separatorColor = new Color(getResources().getColor(isLight ? R.color.editor_separator_light : R.color.editor_separator));
            this.hyperlinkColor = new Color(getResources().getColor(isLight ? R.color.editor_hyperlink_light : R.color.editor_hyperlink));

            this.Bx = new Color(getResources().getColor(isLight ? R.color.editor_diff_inserted_light : R.color.editor_diff_inserted));
            this.Jm = new Color(getResources().getColor(isLight ? R.color.editor_diff_deleted_light : R.color.editor_diff_deleted));
            this.An = new Color(getResources().getColor(isLight ? R.color.editor_line_number_light : R.color.editor_line_number));

		}

		@Override
		public void setShowCaretLine(boolean showCaretLine) {
			this.jn = showCaretLine;
			super.setShowCaretLine(showCaretLine);
		}


	}
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
	protected void createEditorView() {
		//super.createEditorView();
		removeAllViews();
		addView(new AIDEEditor.EditorView(getContext()));
	}
	
	
	@Override
	protected com.aide.ui.views.CodeEditText.EditorView getOEditorView() {
		return super.getOEditorView();
	}


	private int getIndentationSize() {
		String lowerCase = getFilePath().toLowerCase();
		if (lowerCase.endsWith(".java")) {
			return AppPreferences.getJavaIndentationSize();
		}
		if (lowerCase.endsWith(".js")) {
			return AppPreferences.getJsIndentationSize();
		}

		if (lowerCase.endsWith(".c") 
			|| lowerCase.endsWith(".cpp") 
			|| lowerCase.endsWith(".h") 
			|| lowerCase.endsWith(".cc") 
			|| lowerCase.endsWith(".hh") 
			|| lowerCase.endsWith(".hpp")) {
			return AppPreferences.getCppIndentationSize();
		}

		if (lowerCase.endsWith(".xml")) {
			return AppPreferences.getXmlIndentationSize();
		}

		if (lowerCase.endsWith(".html") 
			|| lowerCase.endsWith(".htm")) {
			return AppPreferences.getHtmlIndentationSize();
		}
		if (lowerCase.endsWith(".css")) {
			return AppPreferences.getCssIndentationSize();
		}
		return getTabSize();

	}

	@Override
	public String getQuickKeys() {
		String str = "";
		int indentationSize = getIndentationSize();
		int i = 0;
		if (indentationSize % getTabSize() == 0) {
			while (i < indentationSize / getTabSize()) {
				str = str + "\t";
				i++;
			}
		} else {
			while (i < indentationSize) {
				str = str + "s";
				i++;
			}
		}
		String lowerCase = getFilePath().toLowerCase();

		if (lowerCase.endsWith(".css")) {
			return str + " { } - : . ; # % ( ) \" ' @ > = [ ] / * !";
		}
		if (lowerCase.endsWith(".xml") 
			|| lowerCase.endsWith(".html") 
			|| lowerCase.endsWith(".htm")) {
			return str + " < > / = \" : @ + ( ) ; , . | & ! [ ] { } _ -";
		}
		if (lowerCase.endsWith(".java") 
			|| lowerCase.endsWith(".js")) {
			return str + " { } ( ) ; , . = \" | & ! [ ] < > + - / * ? : _";
		}
		// 比如gradle
		return str + " { } ( ) ; , . = \" | & ! [ ] < > + - / * :";


		//return super.getQuickKeys();
	}

	/**
	 * 文件路径，也有可能是jar里的class
	 * 有很大的闪退风险
	 */
	/*
	@Override
	protected OpenFileService.OpenFileModel Z1(final String filePath) {
		// 先返回，内容异步塞入
		return new AIDEEditorModel(filePath);
	}
	//*/


	public class AIDEEditorModel extends com.aide.ui.AIDEEditor.t {

		private static final String TAG = "AIDEEditorModel";

		String filePath;
		public AIDEEditorModel(String filePath) {
			// 使用无参构造器，因为没有加载内容
			super();

			this.filePath = filePath;

			this.sh = FileSystem.lastModified(this.filePath);

			// 补全 k1参数为null的操作
			this.dx = filePath;
			fh();

			// 延迟加载内容
			Vector<TextBuffer> textBuffers = EditorModelKt.getTextBuffers(this);
			textBuffers.addElement(new TextBuffer("异步加载中....".toCharArray()));

			// 异步
			ThreadPoolService.getDefaultThreadPoolService().submit(
				new Runnable(){
					@Override
					public void run() {
						initAsync(FileSystem.readFileOrZipEntry(AIDEEditorModel.this.filePath));
					}
				});

		}

		@Override
		public void j6() {
			// 异步
			ThreadPoolService.getDefaultThreadPoolService().submit(
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
					AppLog.e(TAG, "等待异步加载初始化错误", e);				}
			}
			super.J0(openFile);
		}

		@Override
		public int getLineCount() {

			return super.getLineCount();
		}
		// 为了等待
		private final Object lock = new Object();

		private final AtomicBoolean initing = new AtomicBoolean(true);

		public void initAsync(Reader reader) {
			try {
				// 读取
				initReader(reader);
			}
			catch (Throwable e) {
				AppLog.e(TAG, "异步加载初始化", e);
			}
		}
		private void initReader(Reader reader) {
			synchronized (this) {
				// k1()
				this.cb = com.aide.engine.service.CodeModelFactory.findCodeModel(filePath, ServiceContainer.Hw());

				Vector<TextBuffer> textBuffers = EditorModelKt.getTextBuffers(this);
				// 需要对textBuffers操作，防止并发
				//synchronized (textBuffers) {
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
				}

				final CodeEditText.EditorView oEditorView = getOEditorView();
				oEditorView.invalidateLayoutTask.DW();
				oEditorView.indexingLayoutTask.DW();

				// 通知代码分析进程 内容填充完毕
				EngineService engineService = ServiceContainer.getEngineService();
				// 解除代码分析进程阻塞
				engineService.ef();
				engineService.ei();

				//}

			}
		}
	}
}
