package abcd;


/*
 author : 罪慾
 date : 2024/10/2 22:46
 description : QQ3115093767
 */

import com.aide.codemodel.api.ClassType;
import com.aide.codemodel.api.FileEntry;
import com.aide.codemodel.api.Member;
import com.aide.codemodel.api.Model;
import com.aide.codemodel.api.SyntaxTree;
import com.aide.codemodel.api.collections.ListOf;
import com.aide.codemodel.api.collections.MapOfInt;
import com.aide.codemodel.language.xml.XmlCodeModel;
import io.github.zeroaicy.util.Log;
import com.aide.codemodel.api.collections.SetOf;
import com.aide.codemodel.api.Type;
import androidx.annotation.Keep;
import com.aide.common.AppLog;
import java.util.ArrayList;
import java.util.List;
import io.github.zeroaicy.aide.ui.services.ThreadPoolService;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ExecutionException;
import java.io.Reader;
import com.aide.codemodel.api.util.SyntaxTreeUtils;

public class v7{

    private static final List<ClassType> viewClassTypes = new ArrayList<>();

    private ListOf<Member> FH;

    private Model model;


    public v7(Model model){
        this.model = model;

		// 没用entitySpace没有类
		//initViewClassTypes();

    }

	private void initViewClassTypes(){

		if ( ! viewClassTypes.isEmpty() ){
			return;
		}

		AppLog.d("initViewClassTypes()");
		//this.model.codeCompleterCallback.j6();

		//(syntaxTree.getFile(), syntaxTree.getLanguage()).DEFAULT_ITERATOR;
		MapOfInt<ClassType>.Iterator default_Iterator = model.entitySpace.XG().DEFAULT_ITERATOR;
		// init
		default_Iterator.DW();

		ClassType view = this.model.entitySpace.getRootNamespace().OW(this.model.identifierSpace.get("android")).OW(this.model.identifierSpace.get("view")).ca().gn(this.model.identifierSpace.get("View"));
		ClassType viewGroup = this.model.entitySpace.getRootNamespace().OW(this.model.identifierSpace.get("android")).OW(this.model.identifierSpace.get("view")).ca().gn(this.model.identifierSpace.get("ViewGroup"));

		long loadSyntaxStartTime = System.currentTimeMillis();
		// 只写操作
		int classTypesSize = 0;

		while ( default_Iterator.hasMoreElements() ){
			classTypesSize++;
			ClassType classType = default_Iterator.nextValue();
			if ( isViewClassType(classType, view, viewGroup) ){
				this.viewClassTypes.add(classType);
			}
		}
		AppLog.d("y7", "init 遍历并loadSyntax %s个 耗时 %s 秒", classTypesSize, (System.currentTimeMillis() - loadSyntaxStartTime) / 1000f);		
	}


    private boolean v5(FileEntry fileEntry){
        return fileEntry.getParentDirectory().getFullNameString().startsWith("layout");
    }

    private String getParentName(SyntaxTree syntaxTree){
        return syntaxTree.getFile().getParentDirectory().getFullNameString();
    }

    public void DW(SyntaxTree syntaxTree, int i, int i2){
        // DW 补全资源
        String fileName = getParentName(syntaxTree);
        if ( fileName.startsWith("layout") ){

        }
    }



	@Keep
	public void J8(SyntaxTree syntaxTree, int i, int i2){

	}

	// init codeCompleter
	@Keep
    public void FH(SyntaxTree syntaxTree, int i, int i2){
		long time = System.currentTimeMillis();

        // 提示节点
        String fileName = getParentName(syntaxTree);

        Log.d("fileName", syntaxTree.getFile().getFullNameString());

        if ( syntaxTree.getFile().getFullNameString().equals("AndroidManifest.xml") ){

        }else if ( fileName.startsWith("layout") ){

			// 重置代码提示
			this.model.codeCompleterCallback.j6();

			if ( this.viewClassTypes.isEmpty() ){
				initViewClassTypes();
			}

			for ( int index = 0; index < this.viewClassTypes.size(); index++ ){
				ClassType viewClassType = viewClassTypes.get(index);
				//j6.codeCompleterCallback.Ws(data);//Java的导包方式
				//j6.codeCompleterCallback.Zo(data.getFullyQualifiedNameString()); // 粗
				//j6.codeCompleterCallback.DW(data.getFullyQualifiedNameString()); // 细

				//member.getIdentifierString().equals("id") ? "android:id=\"@+id/" + (this.model.identifierSpace.getString(syntaxTree.getFile().getNameIdentifier()) + syntaxTree.getIdentifierString(i2)) + "|\"" : "android:" + member.getIdentifierString() + "=\"|\"");
				/// 显示 ClassType简称 填充 全称
				String fullyQualifiedNameString = viewClassType.getFullyQualifiedNameString();
				if ( fullyQualifiedNameString.startsWith("android.widget.") ){
					fullyQualifiedNameString = viewClassType.gG();
				}
				this.model.codeCompleterCallback.aM(viewClassType, fullyQualifiedNameString);
            }

        }else if ( fileName.startsWith("xml") ){

        }else if ( fileName.startsWith("values") ){

        }else if ( fileName.startsWith("menu") ){

        }else if ( fileName.startsWith("anim") ){

        }else if ( fileName.startsWith("color") ){

        }else if ( fileName.startsWith("font") ){

        }else if ( fileName.startsWith("animator") ){

        }else if ( fileName.startsWith("transition") ){

        }else if ( fileName.startsWith("interpolator") ){

        }else if ( fileName.startsWith("mipmap") ){



		}else if ( fileName.startsWith("navigation") ){

        }else if ( fileName.startsWith("drawable") ){

        }

		AppLog.d("y7", "FH 耗时 %s 秒", (System.currentTimeMillis() - time) / 1000);
    }

	// 
	public static class FilterTask implements Callable<List<ClassType>>{
		// 只读
		final List<ClassType> classTypes;
		// 开始遍历
		final int start, end;

		final ClassType view, viewGroup;

		public FilterTask(List<ClassType> classTypes, int start, int end, ClassType view, ClassType viewGroup){
			this.classTypes = classTypes;
			this.start = start;
			this.end = end;
			this.view = view;
			this.viewGroup = viewGroup;
		}

		@Override
		public List<ClassType> call() throws Exception{
			List<ClassType> copy = new ArrayList<>(end - start);
			for ( int i = this.start; i < end; i++ ){
				ClassType classType = classTypes.get(i);
				//synchronized(ClassType.class){
				SetOf<Type> allSuperTypes = classType.getAllSuperTypes();

				if ( !allSuperTypes.contains(view) 
					&& !allSuperTypes.contains(viewGroup) ){
					continue;
				}
				copy.add(classType);
				//}
			}
			return copy;
		}
	}


	private void init(SyntaxTree syntaxTree){

		/*if ( v7.allClass != null ){
		 this.DW = v7.allClass;
		 return;
		 }*/
		long time = System.currentTimeMillis();

		if ( this.viewClassTypes == null ){

			AppLog.d("初始化 xml classType");

			this.model.codeCompleterCallback.j6();


			MapOfInt<ClassType>.Iterator maps = model.entitySpace.getAllClasstypes(syntaxTree.getFile(), syntaxTree.getLanguage()).DEFAULT_ITERATOR;
			// init
			maps.DW();

			ClassType view = this.model.entitySpace.getRootNamespace().OW(this.model.identifierSpace.get("android")).OW(this.model.identifierSpace.get("view")).ca().gn(this.model.identifierSpace.get("View"));
			ClassType viewGroup = this.model.entitySpace.getRootNamespace().OW(this.model.identifierSpace.get("android")).OW(this.model.identifierSpace.get("view")).ca().gn(this.model.identifierSpace.get("ViewGroup"));

			long loadSyntaxStartTime = System.currentTimeMillis();
			// 初始化全部集合
			//final List<ClassType> classTypes = new ArrayList<>();
			// 只写操作
			int classTypesSize = 0; //classTypes.size();

			while ( maps.hasMoreElements() ){
				classTypesSize++;
				ClassType classType = maps.nextValue();
				// 加载一下父类
				// 主要耗时操作
				//classType.isDelegateType();
				//classTypes.add(classType);
				if ( isViewClassType(classType, view, viewGroup) ){
					this.viewClassTypes.add(classType);
				}
			}
			AppLog.d("y7", "init 遍历并loadSyntax %s个 耗时 %s 秒", classTypesSize, (System.currentTimeMillis() - loadSyntaxStartTime) / 1000f);

			//loadSyntaxs(classTypes);

			// method(classTypes, view, viewGroup);

		}
		AppLog.d("y7", "init 耗时 %s 秒", (System.currentTimeMillis() - time) / 1000f);
	}

	private void method(List<ClassType> classTypes, ClassType view, ClassType viewGroup){
		long  findThisiewClassTypes = System.currentTimeMillis();
		for ( int i = 0, size = classTypes.size(); i < size; i++ ){
			ClassType classType = classTypes.get(i);
			if ( isViewClassType(classType, view, viewGroup) ){
				this.viewClassTypes.add(classType);
			}
		}
		AppLog.d("y7", "init 遍历并发现 %s个 共耗时 %s 秒", this.viewClassTypes.size(), (System.currentTimeMillis() - findThisiewClassTypes) / 1000f);
	}

	private boolean isViewClassType(ClassType classType, ClassType view, ClassType viewGroup){
		SetOf<Type> allSuperTypes = classType.getAllSuperTypes();
		if ( !allSuperTypes.contains(view) 
			&& !allSuperTypes.contains(viewGroup) ){
			return false;
		}
		return true;
	}

	private void loadSyntaxs(final List<ClassType> classTypes){
		int classTypesSize = classTypes.size();
		AppLog.d("y7", "遍历%s个classType", classTypesSize);
		// 加载 getAllSuperTypes

		// 分成10组任务
		int taskSize = 10;
		int groupSize = classTypesSize / 10;

		if ( groupSize == 0 ){
			groupSize = classTypesSize;
		}
		List<Callable<Void>> tasks = new ArrayList<>();
		for ( int groupIndex = 0; groupIndex < taskSize; groupIndex++ ){

			final int start = groupIndex * groupSize;
			final int end = groupIndex == 
				// 最后一个
				taskSize - 1 ? 
				classTypesSize : 
				(groupIndex + 1) * groupSize;

			tasks.add(new Callable<Void>(){
					@Override
					public Void call() throws Exception{
						for ( int i = start; i < end; i++ ){
							ClassType classType = classTypes.get(i);
							//synchronized ( classType ){
							try{
								classType.isDelegateType();
							}
							catch (Throwable e){
								AppLog.e(String.format("loadSyntax类 %s", classType.getFullyQualifiedNameString()), e);
							}
							//}
						}
						return null;
					}
				});

		}

		try{	
			ThreadPoolService defaultThreadPoolService = ThreadPoolService.getDefaultThreadPoolService();
			for ( Future<Void> future : defaultThreadPoolService.invokeAll(tasks) ){
				try{
					future.get();
				}
				catch (Throwable e){
					e.printStackTrace();
				}
			}
		}
		catch (InterruptedException e){
			e.printStackTrace();
		}
	}

	@Keep
    public boolean Hw(FileEntry fileEntry){

        return fileEntry.getCodeModel() instanceof XmlCodeModel;
    }


    @Keep
	public void j6(SyntaxTree syntaxTree, int i){
        // 补全“”里面的属性
        // int identifier = syntaxTree.getIdentifier(syntaxTree.getChildNode(syntaxTree.getChildNode(i, 0), 2));

		SyntaxTreeUtils.printNode(syntaxTree, syntaxTree.getRootNode());
		int identifier = syntaxTree.getIdentifier(syntaxTree.getChildNode(syntaxTree.getChildNode(i, 0), 2));
		if ( identifier != this.model.identifierSpace.get("layout_width") && identifier != this.model.identifierSpace.get("layout_height") ){
			if ( identifier == this.model.identifierSpace.get("orientation") ){
				this.model.codeCompleterCallback.Zo("horizontal");
				this.model.codeCompleterCallback.Zo("vertical");
				return;
			}
			if ( identifier == this.model.identifierSpace.get("visibility") ){
				this.model.codeCompleterCallback.Zo("visible");
				this.model.codeCompleterCallback.Zo("invisible");
				this.model.codeCompleterCallback.Zo("gone");
				return;
			}
			if ( identifier == this.model.identifierSpace.get("textAllignment") ){
				this.model.codeCompleterCallback.Zo("inherit");
				this.model.codeCompleterCallback.Zo("gravity");
				this.model.codeCompleterCallback.Zo("textStart");
				this.model.codeCompleterCallback.Zo("textEnd");
				this.model.codeCompleterCallback.Zo("center");
				this.model.codeCompleterCallback.Zo("viewStart");
				this.model.codeCompleterCallback.Zo("viewEnd");
				return;
			}
			if ( identifier != this.model.identifierSpace.get("gravity") && identifier != this.model.identifierSpace.get("layout_gravity") && identifier != this.model.identifierSpace.get("foregroundGravity") ){
				if ( identifier != this.model.identifierSpace.get("allowSingleTap") && identifier != this.model.identifierSpace.get("layout_centerInParent") && identifier != this.model.identifierSpace.get("layout_centerHorizontal") && identifier != this.model.identifierSpace.get("layout_centerVertical") && identifier != this.model.identifierSpace.get("layout_alignParentTop") && identifier != this.model.identifierSpace.get("layout_alignParentStart") && identifier != this.model.identifierSpace.get("layout_alignParentRight") && identifier != this.model.identifierSpace.get("layout_alignParentEnd") && identifier != this.model.identifierSpace.get("layout_alignParentLeft") && identifier != this.model.identifierSpace.get("layout_alignParentBottom") && identifier != this.model.identifierSpace.get("isIndicator") && identifier != this.model.identifierSpace.get("indeterminate") && identifier != this.model.identifierSpace.get("cropToPadding") && identifier != this.model.identifierSpace.get("baselineAlignBottom") && identifier != this.model.identifierSpace.get("adjustViewBounds") && identifier != this.model.identifierSpace.get("fillViewport") && identifier != this.model.identifierSpace.get("useDefaultMargins") && identifier != this.model.identifierSpace.get("rowOrderPreserved") && identifier != this.model.identifierSpace.get("columnOrderPreserved") && identifier != this.model.identifierSpace.get("stretchColumns") && identifier != this.model.identifierSpace.get("shrinkColumns") && identifier != this.model.identifierSpace.get("measureAllChildren") && identifier != this.model.identifierSpace.get("measureWithLargestChild") && identifier != this.model.identifierSpace.get("baselineAligned") && identifier != this.model.identifierSpace.get("clipToPadding") && identifier != this.model.identifierSpace.get("clipChildren") ){
					if ( identifier != this.model.identifierSpace.get("ignoreGravity") && identifier != this.model.identifierSpace.get("handle") && identifier != this.model.identifierSpace.get("content") && identifier != this.model.identifierSpace.get("layout_alignLeft") && identifier != this.model.identifierSpace.get("layout_alignRight") && identifier != this.model.identifierSpace.get("layout_alignStart") && identifier != this.model.identifierSpace.get("layout_alignEnd") && identifier != this.model.identifierSpace.get("layout_alignTop") && identifier != this.model.identifierSpace.get("layout_alignBottom") && identifier != this.model.identifierSpace.get("layout_alignBaseline") && identifier != this.model.identifierSpace.get("layout_toStartOf") && identifier != this.model.identifierSpace.get("layout_toLeftOf") && identifier != this.model.identifierSpace.get("layout_toEndOf") && identifier != this.model.identifierSpace.get("layout_toRightOf") && identifier != this.model.identifierSpace.get("layout_above") && identifier != this.model.identifierSpace.get("layout_below") ){
						if ( identifier != this.model.identifierSpace.get("background") && identifier != this.model.identifierSpace.get("src") && identifier != this.model.identifierSpace.get("thumb") && identifier != this.model.identifierSpace.get("track") && identifier != this.model.identifierSpace.get("popupBackground") ){
							if ( identifier != this.model.identifierSpace.get("prompt") && identifier != this.model.identifierSpace.get("textOn") && identifier != this.model.identifierSpace.get("textOff") && identifier != this.model.identifierSpace.get("text") ){
								if ( identifier == this.model.identifierSpace.get("textStyle") ){
									this.model.codeCompleterCallback.Zo("normal");
									this.model.codeCompleterCallback.Zo("bold");
									this.model.codeCompleterCallback.Zo("italic");
									return;
								}
								if ( identifier == this.model.identifierSpace.get("typeface") ){
									this.model.codeCompleterCallback.Zo("normal");
									this.model.codeCompleterCallback.Zo("sans");
									this.model.codeCompleterCallback.Zo("serif");
									this.model.codeCompleterCallback.Zo("monospace");
									return;
								}
								if ( identifier == this.model.identifierSpace.get("ellipsize") ){
									this.model.codeCompleterCallback.Zo("none");
									this.model.codeCompleterCallback.Zo("start");
									this.model.codeCompleterCallback.Zo("middle");
									this.model.codeCompleterCallback.Zo("end");
									this.model.codeCompleterCallback.Zo("marquee");
									return;
								}
								if ( identifier == this.model.identifierSpace.get("inputType") ){
									this.model.codeCompleterCallback.Zo("none");
									this.model.codeCompleterCallback.Zo("text");
									this.model.codeCompleterCallback.Zo("textCapCharacters");
									this.model.codeCompleterCallback.Zo("textCapWords");
									this.model.codeCompleterCallback.Zo("textCapSentences");
									this.model.codeCompleterCallback.Zo("textAutoCorrect");
									this.model.codeCompleterCallback.Zo("textAutoComplete");
									this.model.codeCompleterCallback.Zo("textMultiLine");
									this.model.codeCompleterCallback.Zo("textImeMultiLine");
									this.model.codeCompleterCallback.Zo("textNoSuggestions");
									this.model.codeCompleterCallback.Zo("textUri");
									this.model.codeCompleterCallback.Zo("textEmailAddress");
									this.model.codeCompleterCallback.Zo("textEmailSubject");
									this.model.codeCompleterCallback.Zo("textShortMessage");
									this.model.codeCompleterCallback.Zo("textLongMessage");
									this.model.codeCompleterCallback.Zo("textPersonName");
									this.model.codeCompleterCallback.Zo("textPostalAddress");
									this.model.codeCompleterCallback.Zo("textPassword");
									this.model.codeCompleterCallback.Zo("textVisiblePassword");
									this.model.codeCompleterCallback.Zo("textWebEditText");
									this.model.codeCompleterCallback.Zo("textFilter");
									this.model.codeCompleterCallback.Zo("textPhonetic");
									this.model.codeCompleterCallback.Zo("textWebEmailAddress");
									this.model.codeCompleterCallback.Zo("textWebPassword");
									this.model.codeCompleterCallback.Zo("number");
									this.model.codeCompleterCallback.Zo("numberSigned");
									this.model.codeCompleterCallback.Zo("numberDecimal");
									this.model.codeCompleterCallback.Zo("numberPassword");
									this.model.codeCompleterCallback.Zo("phone");
									this.model.codeCompleterCallback.Zo("datetime");
									this.model.codeCompleterCallback.Zo("date");
									this.model.codeCompleterCallback.Zo("time");
									return;
								}
								return;
							}
							this.model.codeCompleterCallback.Zo("@string/");
							return;
						}
						this.model.codeCompleterCallback.Zo("@drawable/");
						return;
					}
					this.model.codeCompleterCallback.Zo("@id/");
					return;
				}
				this.model.codeCompleterCallback.Zo("true");
				this.model.codeCompleterCallback.Zo("false");
				return;
			}
			this.model.codeCompleterCallback.Zo("top");
			this.model.codeCompleterCallback.Zo("bottom");
			this.model.codeCompleterCallback.Zo("left");
			this.model.codeCompleterCallback.Zo("right");
			this.model.codeCompleterCallback.Zo("center_vertical");
			this.model.codeCompleterCallback.Zo("fill_vertical");
			this.model.codeCompleterCallback.Zo("center_horizontal");
			this.model.codeCompleterCallback.Zo("fill_horizontal");
			this.model.codeCompleterCallback.Zo("center");
			this.model.codeCompleterCallback.Zo("fill");
			this.model.codeCompleterCallback.Zo("clip_vertical");
			this.model.codeCompleterCallback.Zo("clip_horizontal");
			this.model.codeCompleterCallback.Zo("start");
			this.model.codeCompleterCallback.Zo("end");
			return;
		}
		this.model.codeCompleterCallback.Zo("fill_parent");
		this.model.codeCompleterCallback.Zo("match_parent");
		this.model.codeCompleterCallback.Zo("wrap_content");
    }
}



