package com.aide.codemodel.api.util;

import com.aide.codemodel.api.SyntaxTree;
import com.aide.codemodel.api.Type;
import com.aide.codemodel.api.abstraction.Syntax;
import java.util.Arrays;

public class SyntaxTreeUtils {
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
}
