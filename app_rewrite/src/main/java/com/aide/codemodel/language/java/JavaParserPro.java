package com.aide.codemodel.language.java;


import com.aide.codemodel.api.EntitySpace;
import com.aide.codemodel.api.ErrorTable;
import com.aide.codemodel.api.IdentifierSpace;
import io.github.zeroaicy.util.Log;
import com.aide.codemodel.api.util.SyntaxTreeUtils;
import com.aide.codemodel.api.FileEntry;
import com.aide.codemodel.api.SyntaxTree;
import com.aide.codemodel.api.SyntaxTreeStyles;
import io.github.zeroaicy.util.reflect.ReflectPie;
import io.github.zeroaicy.util.reflect.ReflectPieException;
import com.aide.codemodel.api.Parser;
import com.aide.codemodel.api.Parser.a;

public class JavaParserPro extends JavaParser {

	ReflectPie that = ReflectPie.on(this);
	public ReflectPie call(String name) throws ReflectPieException {
        return call(name, new Object[0]);
    }
	public ReflectPie call(String name, Object... args) throws ReflectPieException {
        return that.call(name, args);		
	}

	public JavaParserPro(IdentifierSpace identifierSpace, ErrorTable errorTable, EntitySpace entitySpace, JavaSyntax javaSyntax, boolean p4) {
		super(identifierSpace, errorTable, entitySpace, javaSyntax, p4);

	}

	/*
	 * 参数类型可以省略，如果需要省略，每个参数的类型都要省略。
	 * 参数的小括号里面只有一个参数，那么小括号可以省略
	 * 如果方法体当中只有一句代码，那么大括号可以省略
	 * 如果方法体中只有一条语句，其是return语句，那么大括号可以省略，且去掉return关键字
	 */
	// 此方法处理的是 IDENTIFIER -> 样式
	public boolean parserLambdaExpression() throws Parser.a {

		/* LambdaExpression
		 *  	IDENTIFIER || PARAMETERS parameters
		 *      -
		 *      >
		 *      body || expression || statement
		 *      
		 *      
		 *
		 */
		// 基本语法: (parameters) "->" expression 或 (parameters) "->" { statements; }
		// (parameters) 可以是 () || (int x, int y) || ( x ) || x

		// javac lambdaExpression 文法
		// Arguments可以为空 
		// "(" Arguments ")" "->" ( Expression || Block )
		// || Ident "->" ( Expression || Block )

		// 判断是否是 lambdaExpression
		// true -> lambdaExpressionOrStatement
		// ELLIPSIS: ...
		// (xxx) -> 
		int analyzeParens = analyzeParens();

		if (analyzeParens != EXPLICIT_LAMBDA 
			&& analyzeParens != IMPLICIT_LAMBDA) {

			// IDENTIFIER -> 
			int lookahead = this.tp - 1;
			if (peekTokenOr(lookahead, /* IDENTIFIER */ 1,  /* enum */ 107)
				&& peekToken(lookahead + 1, /* - */ 27, /* > */ 48)) {
				analyzeParens = ONLY_IMPLICIT_LAMBDA;
			}else{
				// 非Lambda
				System.out.println("analyzeParens2: " + analyzeParens);
				return false;									
			}
		}

		System.out.println("analyzeParens: " + analyzeParens);
		Log.printlnStack(1, 10);
		
		// 解析 PARAMETERS

		// lambdaExpression
		// currentNodeOffset

		// u7: currentSyntaxTag we: syntaxTags tp: currentOffset

		int lastNodeOffset = this.j3;
		try {
			// PARAMETERS
			try {
				System.out.println("parserLambdaParameters 开始解析 : " + analyzeParens);
				System.out.println("parserLambdaExpression开始时 tag: " + this.u7);
				parserLambdaParameters(analyzeParens);
				
				System.out.println("parserLambdaParameters时 tag: " + this.u7);
				
			}
			catch (Throwable e) {
				that.call("g3");
				Log.d("解析器", "解析Lambda参数列表", e);
			}

			// ARROW: -> 
			
			if( this.u7 == /* - */ 27){
				we(); // -
			}else{
				that.call("g3");
				J0(27); // -
				throw new Parser.a();
			}
			
			if( this.u7 == /* > */ 48){
				we(); // >
			}else{
				that.call("g3");
				J0(48); // >
				throw new Parser.a();
			}
			
			if (this.u7 == 8) {
				//解析body
				// LBRACE : {
				// lambdaStatement
				call("nw"); // 解析右侧块

			} else {
				// 解析 expression
				call("Qq"); // 解析右侧表达式
			}

			QX(247, 4);
			
			System.out.println("打印 LAMBDA_EXPRESSION");
			SyntaxTreeUtils.printNode(this.gn, this.aM[j3]);

			return true;
		}
		catch (Throwable e) {
			Log.d("解析器", "解析Lambda", e);
			that.call("g3");
			// currentNodeOffset
			this.j3 = lastNodeOffset;

			throw new Parser.a();
		}				

	}

	private void parserLambdaParameters(int analyzeParens) throws Parser.a {
		if (analyzeParens == EXPLICIT_LAMBDA) {
			
			that.call("WB");
			
		} else if (analyzeParens == ONLY_IMPLICIT_LAMBDA) {
			we(); // IDENTIFIER
			QX(191, 1); // PARAMETER
			QX(190, 1); // PARAMETERS
		} else {
			// 就必须自己实现了
			int childCount = 1;
			we(); // (
			
			// 解析PARAMETER 特殊的PARAMETER 仅有 IDENTIFIER
			while ( this.u7 == /* IDENTIFIER */ 1 ){
				we(); // IDENTIFIER
				QX(191, 1); // PARAMETER
				childCount++;
				
				// 处理 ,
				if (this.u7 == /* , */ 15) {
					we();
					childCount++;
				}else if (this.u7 == /* ) */ 13) {
					// ) 结束
					we(); // )
					childCount++;	
					break;
				}
				else{
					// 缺少 )
					J0(13); // )
					break;
				}
			}
			
			
			QX(190, childCount); // PARAMETERS

		}
	}

	public boolean peekToken(int index, int tag) {
		return this.we.length > index + 1
			&& this.we[index + 1] == tag;
	}

	public boolean peekToken(int index, int tag1, int tag2) {
		return this.we.length > index + 2
			&& this.we[index + 1] == tag1
			&& this.we[index + 2] == tag2;
	}
	public boolean peekTokenOr(int index, int tag1, int tag2) {
		return this.we.length > index + 2
			&& (this.we[index + 1] == tag1
			|| this.we[index + 1] == tag2);
	}

	private int UN_LAMBDA = 0;
	private int EXPLICIT_LAMBDA = 1;
	private int IMPLICIT_LAMBDA = 2;
	private int ONLY_IMPLICIT_LAMBDA = 3;

	private int analyzeParens() {

        outer: for (int lookahead = this.tp, size = this.we.length ; lookahead < size; lookahead++) {

			int tag = this.we[lookahead];

			// isTypeIdentifier
			if (this.j6.isTypeIdentifier(tag)) {
				if (peekToken(lookahead, /* ) */ 13)) {
					//Type, ')' -> cast
					return UN_LAMBDA;
				} else if (peekTokenOr(lookahead, /* IDENTIFIER */ 1,  /* enum */ 107)) {
					//Type, Identifier/'_'/'assert'/'enum' -> explicit lambda
					return EXPLICIT_LAMBDA;
				}
			}

			switch (tag) {
				case 1: // IDENTIFIER
				case 105: // assert
				case 107: // enum
					if (peekTokenOr(lookahead, /* IDENTIFIER */ 1,  /* enum */ 107)) {
                        // Identifier, Identifier/'_'/'assert'/'enum' -> explicit lambda
                        return EXPLICIT_LAMBDA;
                    } else if (peekToken(lookahead, /* ) */ 13)
							   && peekToken(lookahead + 1, /* - */ 27, /* > */ 48)) {
                        // Identifier, ')' '->' -> implicit lambda
                        return IMPLICIT_LAMBDA;
                    }
                    break;
				case 10:	// LBRACKET: [
					if (peekToken(lookahead, /* ] */ 11)
						&& peekTokenOr(lookahead + 1, /* IDENTIFIER */ 1,  /* enum */ 107)) {
                        // '[', ']', Identifier/'_'/'assert'/'enum' -> explicit lambda
                        return EXPLICIT_LAMBDA;
                    } else if (peekToken(lookahead, /* ] */ 11, /* ) */ 13) ||
							   peekToken(lookahead, /* ] */ 11, /* & */ 41)) {
                        // '[', ']', ')' -> cast
                        // '[', ']', '&' -> cast (intersection type)
                        return UN_LAMBDA;
                    } else if (peekToken(lookahead,  /* ] */ 11)) {
                        //consume the ']' and skip
                        lookahead++;
                        break;
                    } else {
                        return UN_LAMBDA;
                    }

				case 12:	// LPAREN: (
					if (lookahead != this.tp) {
                        // '(' in a non-starting position -> parens
                        return UN_LAMBDA;
                    } else if (peekToken(lookahead, 13)) {
                        // '(', ')' -> explicit lambda
                        return EXPLICIT_LAMBDA;
                    }

					break;
				case 13:	// RPAREN: )
					// 是 CAST 还是 PARENS并不重要
					return UN_LAMBDA;
				case 16: // COMMA: .
					break;
				case 25:// QUES: ?
                    break;
				case 75: // final
                case 108: // ...
					return EXPLICIT_LAMBDA;
                case 115: // @
					lookahead += 1; //skip '@'
                    while (peekToken(lookahead, /* @ */ 115)) {
                        lookahead += 2;
                    }
                    if (peekToken(lookahead, /* ( */ 12)) {
                        lookahead++;
                        //skip annotation values
                        int nesting = 0;
                        for (; ; lookahead++) {
							if (lookahead + 1 < size) {
								return UN_LAMBDA;
							}

							int tk2 = this.we[lookahead];
							switch (tk2) {
                                case 12:	// LPAREN: (
                                    nesting++;
                                    break;
                                case 13:	// RPAREN: )
                                    nesting--;
                                    if (nesting == 0) {
                                        continue outer;
                                    }
									break;
                            }
                        }
                    }
					break;
			}
		}
		return UN_LAMBDA;
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
		 // Log.printlnStack(5, 18);

		super.FH(p);
	}



	@Override
	public void we() {
		System.out.println("declareCurrentSyntaxTagNode " + this.j6.getString(this.u7));
		super.we();

	}

	/*************************[declareParentNode*****************************************/
	/*
	 @Override
	 public void QX(int syntaxTag, int len) {
	 System.out.println("declareParentNode " + this.j6.getString(syntaxTag) + " len: " + len);
	  // Log.printlnStack(5, 18);
	 System.out.println();
	 super.QX(syntaxTag, len);
	 }//*/

	@Override
	public void j3(int syntaxTag, boolean synthetic, int len, int declarationNumber) {
		System.out.println("declareParentNode1 " + this.j6.getString(syntaxTag) + " synthetic: " + synthetic + " len: " + len + " declarationNumber: " + declarationNumber);
		 // Log.printlnStack(5, 18);
		super.j3(syntaxTag, synthetic, len, declarationNumber);
	}


	@Override
	public void XL(int syntaxTag, int previousOffset, int len) {
		System.out.println("declareParentNode2 " + this.j6.getString(syntaxTag) + " previousOffset: " + previousOffset + " len: " + len);
		 // Log.printlnStack(5, 18);

		super.XL(syntaxTag, previousOffset, len);
	}


	@Override
	public void aM(int syntaxTag, boolean synthetic, int len) {
		System.out.println("declareParentNode3 " + this.j6.getString(syntaxTag) + " synthetic: " + synthetic + " len: " + len);
		 // Log.printlnStack(5, 18);
		super.aM(syntaxTag, synthetic, len);
	}
	/*************************declareParentNode]*****************************************/


	@Override
	public void DW(String errorMsg) {
		try {
			String unexpectedDeclaration = "Unexpected end of declaration";
			if (unexpectedDeclaration.equals(errorMsg)) {
				System.out.println(unexpectedDeclaration);
				 // Log.printlnStack(5, 18);
			} else {
				System.out.println(errorMsg);
				 // Log.printlnStack(5, 18);
			}
		}
		catch (Throwable e) {}
		super.DW(errorMsg);
	}

}
