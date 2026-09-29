package com.faforever.neroxis.lua;

import org.antlr.v4.runtime.ANTLRErrorListener;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.RecognitionException;
import org.jspecify.annotations.NullMarked;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/LuaLexerBase.class */
@NullMarked
public abstract class LuaLexerBase extends Lexer {
    private int start_line;
    private int start_col;

    protected LuaLexerBase(CharStream input) {
        super(input);
    }

    protected void HandleComment() {
        int sep;
        this.start_line = getLine();
        this.start_col = getCharPositionInLine() - 2;
        CharStream cs = this._input;
        if (cs.LA(1) == 91 && (sep = skip_sep(cs)) >= 2) {
            read_long_string(cs, sep);
            return;
        }
        while (cs.LA(1) != 10 && cs.LA(1) != -1) {
            cs.consume();
        }
    }

    private void read_long_string(CharStream cs, int sep) {
        boolean done = false;
        cs.consume();
        do {
            int c = cs.LA(1);
            switch (c) {
                case -1:
                    done = true;
                    ANTLRErrorListener listener = getErrorListenerDispatch();
                    listener.syntaxError(this, (Object) null, this.start_line, this.start_col, "unfinished long comment", (RecognitionException) null);
                    break;
                case 93:
                    if (skip_sep(cs) == sep) {
                        cs.consume();
                        done = true;
                    }
                    break;
                default:
                    if (cs.LA(1) == -1) {
                        done = true;
                    } else {
                        cs.consume();
                    }
                    break;
            }
        } while (!done);
    }

    private int skip_sep(CharStream cs) {
        int count;
        int count2 = 0;
        int s = cs.LA(1);
        cs.consume();
        while (cs.LA(1) == 61) {
            cs.consume();
            count2++;
        }
        if (cs.LA(1) == s) {
            count = count2 + 2;
        } else if (count2 == 0) {
            count = 1;
        } else {
            count = 0;
        }
        return count;
    }

    public boolean IsLine1Col0() {
        CharStream cs = this._input;
        return cs.index() == 1;
    }
}
