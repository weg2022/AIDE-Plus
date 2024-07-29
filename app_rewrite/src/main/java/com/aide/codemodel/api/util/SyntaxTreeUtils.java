package com.aide.codemodel.api.util;

import com.aide.codemodel.api.SyntaxTree;
import com.aide.codemodel.api.Type;
import com.aide.codemodel.api.abstraction.Syntax;
import java.util.Arrays;

public class SyntaxTreeUtils {

	public static boolean isFields(SyntaxTree syntaxTree, int varRootNode) {
		return syntaxTree.getSyntaxTag(varRootNode) == 126;
	}
	/**
	 * 打印节点起始行列
	 */
	public static void printlnNodeAttr(SyntaxTree syntaxTree, String nodeName, int varNode) {
		System.out.print(nodeName);

		System.out.print(" StartLine " + syntaxTree.getStartLine(varNode));
		System.out.print(" StartColumn " + syntaxTree.getStartColumn(varNode));
		System.out.print(" EndLine " + syntaxTree.getEndLine(varNode));
		System.out.print(" EndColumn " + syntaxTree.getEndColumn(varNode));
		System.out.println();
	}

	public static String getIndent(int indent) {
        char[] tab = new char[indent];
        Arrays.fill(tab, '\t');
        return new String(tab);
    }
	/**
	 * 递归打印 AIDE语法树
	 */
	public static void printNode(SyntaxTree syntaxTree, int node, int indent) {
		Syntax syntax = syntaxTree.getLanguage().getSyntax();
		// 计算缩进
		String indentString = getIndent(indent);

		int syntaxTag = syntaxTree.getSyntaxTag(node);
		// 打印缩进
		System.out.print(indentString);
		System.out.print(syntax.getString(syntaxTag));
		System.out.printf(" [id: %s]", node);

		if (syntaxTree.hasAttrType(node)) {
			Type attrType = syntaxTree.getAttrType(node);
			System.out.printf(" [attr-type: %s]", attrType);	
		}

		String nodeIdentifierString = syntaxTree.getIdentifierString(node);
		if (nodeIdentifierString != null && nodeIdentifierString.length() != 0) {
			System.out.printf(" [Identifier: %s]", nodeIdentifierString);
		}
		// syntaxTree.sh(node)[getDeclareAttrReferenceKind] declareAttrReferenceKind
		try {
			if (syntaxTree.isIdentifierNode(node)) {
				int attrReferenceKind = syntaxTree.sh(node);
				if (attrReferenceKind != 0) {
					System.out.printf(" [attrReferenceKind: %s]", attrReferenceKind);
				}
			}			
		}
		catch (Throwable e) {

		}


		System.out.println();
		System.out.println();

		int childCount = syntaxTree.getChildCount(node);
		for (int index = 0; index < childCount; index++) {
			int childNode = syntaxTree.getChildNode(node, index);
			if (childNode != -1) {
				printNode(syntaxTree, childNode, indent + 1);
			}
		}
	}
	/**
	 * 判断type name是否是var
	 */
	public static boolean isVarNode(SyntaxTree syntaxTree, int varParentNode) {
		// varParentNode[TYPE_NAME]
		if (syntaxTree.getChildCount(varParentNode) < 1) {
			return false;
		}
		int varNode = syntaxTree.getChildNode(varParentNode, 0);
		if (!syntaxTree.isIdentifierNode(varNode)) {
			return false;
		}
		// 是var Node
		String varNodeIdentifierString = syntaxTree.getIdentifierString(varNode);
		if ("var".equals(varNodeIdentifierString)) {
			return true;
		}
		return false;
	}


	/**
	 * 非abstract方法[接口]
	 */
	public static boolean isNoInterfaceAbstractMethod(SyntaxTree syntaxTree, int nodeIndex) {

		for (int i = 0, childCount = syntaxTree.getChildCount(nodeIndex); i < childCount; i++) {
			int syntaxTag = syntaxTree.getSyntaxTag(syntaxTree.getChildNode(nodeIndex, i));
			// abstract
			if (syntaxTag == 95) {
				return false;
			}
			// default || static
			if (syntaxTag == 90
				|| syntaxTag == 86) {
				return true;
			}
		}
		return false;
	}

	private int EQ(int syntaxTag) {
		// 5511177 [ 1 4 12 13 19 21 23 ]
		int newValue;  
		switch (syntaxTag) {  
				// final	
			case 75: 
				// 896 = 0x380 = 1 << 7 | 1 << 8 | 1 << 9  
				newValue = 896; // 0x380, 1 << 7 | 1 << 8 | 1 << 9  
				break;
				// static
			case 86: 
				// 64 = 0x40 = 1 << 6  
				newValue = 64; // 0x40, 1 << 6  
				break;
				// synchronized
			case 104: 
				// 2048 = 0x800 = 1 << 11  
				newValue = 2048; // 0x800, 1 << 11  
				break;
				// @
			case 115: 
				// 536870912 = 0x20000000 = 1 << 29  
				newValue = 0x20000000; // 1 << 29  
				break;
				// native
			case 83: 
				// 524288 = 0x80000 = 1 << 19  
				newValue = 524288; // 0x80000, 1 << 19  
				break;
				// public
			case 84: 
				newValue = 1; // 1 << 0  
				break;
				// private
			case 94: 
				newValue = 4; // 1 << 2  
				break;
				// abstract
			case 95: 
				// 16384 = 0x4000 = 1 << 14  
				newValue = 16384; // 0x4000, 1 << 14  
				break;
				// strictfp
			case 97: 
				// 8192 = 0x2000 = 1 << 13  
				newValue = 8192; // 0x2000, 1 << 13  
				break;
				// volatile
			case 98: 
				// 1024 = 0x400 = 1 << 10  
				newValue = 1024; // 0x400, 1 << 10  
				break;
				// protected
			case 100: 
				newValue = 8; // 1 << 3  
				break;
				// transient
			case 101: 
				// 4096 = 0x1000 = 1 << 12  
				newValue = 4096; // 0x1000, 1 << 12  
				break;
			default: 
				newValue = 0;  
				break;
		}
		// 如果需要，可以在这里将 newValue 赋值给 syntaxTag 或者其他变量  
		syntaxTag = newValue;

        return syntaxTag;
    }
}
