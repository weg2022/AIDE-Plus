package com.aide.engine;

import abcd.cy;
import abcd.dy;
import abcd.ey;
import abcd.fy;
import abcd.gy;
import abcd.i2;
import abcd.i3;
import abcd.iy;
import abcd.th;
import android.content.Context;
import com.aide.ui.rewrite.R;
import android.graphics.Typeface;

public enum SyntaxStyleType{
    PLAIN("Plain", R.color.editor_syntax_plain_light, R.color.editor_syntax_plain, Typeface.NORMAL),
    
	KEYWORD("Keyword", R.color.editor_syntax_keyword_light, R.color.editor_syntax_keyword, Typeface.BOLD),
	
    IDENTIFIER("Identifier", R.color.editor_syntax_identifier_light, R.color.editor_syntax_identifier, Typeface.NORMAL),
	
    NAMESPACE_IDENTIFIER("Namespace/Package Identifier", R.color.editor_syntax_package_light, R.color.editor_syntax_package, Typeface.ITALIC),
	
    TYPE_IDENTIFIER("Type Identifier", R.color.editor_syntax_type_light, R.color.editor_syntax_type, Typeface.NORMAL),
	
    DELEGATE_IDENTIFIER("Delegate Identifier", R.color.editor_syntax_type_light, R.color.editor_syntax_type, Typeface.ITALIC),
    
	OPERATOR("Operator", R.color.editor_syntax_operator_light, R.color.editor_syntax_operator, Typeface.NORMAL),
    
	SEPARATOR("Separator/Punctuator", R.color.editor_syntax_separator_light, R.color.editor_syntax_separator, Typeface.NORMAL),
    
	LITERAL("Literal", R.color.editor_syntax_literal_light, R.color.editor_syntax_literal, Typeface.NORMAL),
    
	PREPROCESSOR("Preprocessor", R.color.editor_syntax_plain, R.color.editor_syntax_plain, Typeface.NORMAL),
    
	COMMENT("Comment", R.color.editor_syntax_comment_light, R.color.editor_syntax_comment, Typeface.NORMAL),
    
	DOC_COMMENT("Documentation Comment", R.color.editor_syntax_comment_light, R.color.editor_syntax_comment, Typeface.ITALIC),
    
	SCRIPT_BACKGROUND("Script Background", R.color.editor_syntax_plain, R.color.editor_syntax_plain, Typeface.NORMAL),
    
	SCRIPT("Script", R.color.editor_syntax_plain_light, R.color.editor_syntax_plain, Typeface.NORMAL),
	
	
	//扩展的
	//PARAMETER("Parameter", R.color.material_grey_100, R.color.material_grey_100, Typeface.ITALIC)
	;
	
	

    private final int darkResId;
    private final int lightResId;
    private final int typefaceStyle;

    private SyntaxStyleType(String enumName, int lightResId, int darkResId, int typefaceStyle) {
		this.lightResId = lightResId;
		this.darkResId = darkResId;
		// 粗体 斜体 
		this.typefaceStyle = typefaceStyle;
    }

    public int getColor(Context context, boolean isLight) {
		return context.getResources().getColor(isLight ? this.lightResId : this.darkResId);
	}
    public int getTypefaceStyle() {
		return this.typefaceStyle;
    }
}

