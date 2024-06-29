package com.aide.codemodel;
import io.github.zeroaicy.util.Log;
import com.aide.codemodel.api.FileEntry;
import java.io.Reader;
import com.aide.codemodel.api.SyntaxTreeStyles;
import java.io.IOException;

public class Highlighter {
    private static final String LOG_TAG = "Highlighter";
    private final JFlexLexer lexer;
    
    public Highlighter(JFlexLexer lexer) {
        this.lexer = lexer;
    }
    
    public void highlight(FileEntry da, Reader reader, SyntaxTreeStyles wa) {
        Log.i(LOG_TAG, "highlight: "+lexer.getClass().getName());
        wa.DW(); // j6() -> DW()
        try {
            lexer.yyreset(reader);
            lexer.yybegin(lexer.getDefaultState());
            int style = lexer.yylex();
            int startLine = lexer.getLine() + 1;
            int startColumn = lexer.getColumn() +1;
            while (true) {
                int nextStyle = lexer.yylex();
                int line = lexer.getLine() + 1;
                int column = lexer.getColumn() + 1;
                wa.j6(style, 0, startLine, startColumn, line, column);
                style = nextStyle;
                startLine = line;
                startColumn = column;
                if (nextStyle == -1) break;
                wa.j6(0, 0, startLine, startColumn, line, column);
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "highlight: "+lexer.getClass().getName(), e);
        } finally {
            try {
                lexer.yyclose();
            } catch (IOException ignored) {
            
            }
        }
    }
    
}
