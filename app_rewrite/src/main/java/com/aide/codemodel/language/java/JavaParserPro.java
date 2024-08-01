package com.aide.codemodel.language.java;


import com.aide.codemodel.api.EntitySpace;
import com.aide.codemodel.api.ErrorTable;
import com.aide.codemodel.api.IdentifierSpace;
import io.github.zeroaicy.util.Log;
import com.aide.codemodel.api.util.SyntaxTreeUtils;
import com.aide.codemodel.api.FileEntry;
import com.aide.codemodel.api.SyntaxTree;
import com.aide.codemodel.api.SyntaxTreeStyles;

public class JavaParserPro extends JavaParser {
	public JavaParserPro(IdentifierSpace identifierSpace, ErrorTable errorTable, EntitySpace entitySpace, JavaSyntax javaSyntax, boolean p4) {
		super(identifierSpace, errorTable, entitySpace, javaSyntax, p4);
	}
	
	@Override
	public void v5(SyntaxTreeStyles syntaxTreeStyles, FileEntry fileEntry, boolean p, SyntaxTree syntaxTree) {
		super.v5(syntaxTreeStyles, fileEntry, p, syntaxTree);

		SyntaxTreeUtils.printNode(syntaxTree, syntaxTree.getRootNode());
	}
	
	
	// accept
	@Override
	public void FH(int p) {
		System.out.println("Missing " + this.j6.getString(p));
		Log.printlnStack(3, 11);

		super.FH(p);
	}
	


	@Override
	public void we() {
		super.we();
		System.out.println("declareCurrentSyntaxTagNode " + this.j6.getString(this.u7));
		Log.printlnStack(3, 11);
	}
	
	/*************************[declareParentNode*****************************************/
	@Override
	public void QX(int syntaxTag, int len) {
		System.out.println("declareParentNode " + this.j6.getString(syntaxTag) + " len: " + len);
		Log.printlnStack(3, 11);
		System.out.println();
		super.QX(syntaxTag, len);
	}

	@Override
	public void j3(int syntaxTag, boolean synthetic, int len, int declarationNumber) {
		System.out.println("declareParentNode " + this.j6.getString(syntaxTag) + " synthetic: " + synthetic + " len: " + len + " declarationNumber: " + declarationNumber);
		Log.printlnStack(3, 11);
		super.j3(syntaxTag, synthetic, len, declarationNumber);
	}
	
	
	@Override
	public void XL(int syntaxTag, int previousOffset, int len) {
		System.out.println("declareParentNode " + this.j6.getString(syntaxTag) + " previousOffset: " + previousOffset + " len: " + len);
		Log.printlnStack(3, 11);
		
		super.XL(syntaxTag, previousOffset, len);
	}

	
	@Override
	public void aM(int syntaxTag, boolean synthetic, int len) {
		System.out.println("declareParentNode " + this.j6.getString(syntaxTag) + " synthetic: " + synthetic + " len: " + len);
		Log.printlnStack(3, 11);
		super.aM(syntaxTag, synthetic, len);
	}
	/*************************declareParentNode]*****************************************/
	

	@Override
	public void DW(String errorMsg) {
		try {
			String unexpectedDeclaration = "Unexpected end of declaration";
			if (unexpectedDeclaration.equals(errorMsg)) {
				System.out.println(unexpectedDeclaration);
				Log.printlnStack(3, 11);
			} else {
				System.out.println(errorMsg);
				Log.printlnStack(3, 11);
			}
		}
		catch (Throwable e) {}
		super.DW(errorMsg);
	}

}
