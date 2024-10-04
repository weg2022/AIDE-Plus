package abcd;
import com.aide.codemodel.api.*;
import com.aide.codemodel.language.xml.*;
import com.aide.codemodel.api.collections.*;
import io.github.zeroaicy.util.*;

public class v7 {

	private Model model;

	private ListOf<ClassType> mClassType;

    private ListOf<Member> mMember;

	public v7(Model model) {
		this.model = model;
	}


	public void DW(SyntaxTree syntaxTree, int i, int i2) {
		//补全资源属性的地方

	}

	public void FH(SyntaxTree syntaxTree, int i, int i2) {
		// 提示View的class地方
		if (this.mClassType == null) {
			this.mClassType = new ListOf<>(this.model.entitySpace);
			Namespace OW = this.model.entitySpace.getRootNamespace().OW(this.model.identifierSpace.get("android")).OW(this.model.identifierSpace.get("widget"));
			ClassType gn = this.model.entitySpace.getRootNamespace().OW(this.model.identifierSpace.get("android")).OW(this.model.identifierSpace.get("view")).ca().gn(this.model.identifierSpace.get("View"));
			/*MapOfInt.Iterator VH = OW.ca().VH();
			VH.DW();
			while (VH.hasMoreElements()) {
				ClassType nextValue = (ClassType) VH.nextValue();
				if (gn == null || nextValue.Ej(gn)) {
					this.mClassType.j6(nextValue);
				}
			}
			*/
		}
		for (int i3 = 0; i3 < this.mClassType.EQ(); i3++) {
			Log.d("模型代码", this.mClassType.v5(i3));
			this.model.codeCompleterCallback.Mr(this.mClassType.v5(i3));
		}
		//this.model.codeCompleterCallback.Mr(this.mClassType.v5(0));
	}

	public boolean Hw(FileEntry fileEntry) {
		return fileEntry.getCodeModel() instanceof XmlCodeModel;
	}

	public void j6(SyntaxTree syntaxTree, int i) {
		//补全属性的地方
		int identifier = syntaxTree.getIdentifier(syntaxTree.getChildNode(syntaxTree.getChildNode(i, 0), 2));

		if (identifier == this.model.identifierSpace.get("layout_width") && identifier == this.model.identifierSpace.get("layout_height")) {
			this.model.codeCompleterCallback.Zo("");
		}
	}
}
