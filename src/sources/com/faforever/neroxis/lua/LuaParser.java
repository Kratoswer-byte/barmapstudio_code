package com.faforever.neroxis.lua;

import com.faforever.neroxis.generator.util.serial.GeneratedMapNameEncoder;
import java.util.List;
import org.antlr.v4.runtime.FailedPredicateException;
import org.antlr.v4.runtime.NoViableAltException;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.RuntimeMetaData;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.Vocabulary;
import org.antlr.v4.runtime.VocabularyImpl;
import org.antlr.v4.runtime.atn.ATN;
import org.antlr.v4.runtime.atn.ATNDeserializer;
import org.antlr.v4.runtime.atn.ParserATNSimulator;
import org.antlr.v4.runtime.atn.PredictionContextCache;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;
import org.antlr.v4.runtime.tree.TerminalNode;

/* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/LuaParser.class */
public class LuaParser extends Parser {
    protected static final DFA[] _decisionToDFA;
    protected static final PredictionContextCache _sharedContextCache;
    public static final int SEMI = 1;
    public static final int EQ = 2;
    public static final int BREAK = 3;
    public static final int GOTO = 4;
    public static final int DO = 5;
    public static final int END = 6;
    public static final int WHILE = 7;
    public static final int REPEAT = 8;
    public static final int UNTIL = 9;
    public static final int IF = 10;
    public static final int THEN = 11;
    public static final int ELSEIF = 12;
    public static final int ELSE = 13;
    public static final int FOR = 14;
    public static final int COMMA = 15;
    public static final int IN = 16;
    public static final int FUNCTION = 17;
    public static final int LOCAL = 18;
    public static final int LT = 19;
    public static final int GT = 20;
    public static final int RETURN = 21;
    public static final int CONTINUE = 22;
    public static final int CC = 23;
    public static final int NIL = 24;
    public static final int FALSE = 25;
    public static final int TRUE = 26;
    public static final int DOT = 27;
    public static final int SQUIG = 28;
    public static final int MINUS = 29;
    public static final int POUND = 30;
    public static final int OP = 31;
    public static final int CP = 32;
    public static final int NOT = 33;
    public static final int LL = 34;
    public static final int GG = 35;
    public static final int AMP = 36;
    public static final int SS = 37;
    public static final int PER = 38;
    public static final int COL = 39;
    public static final int LE = 40;
    public static final int GE = 41;
    public static final int AND = 42;
    public static final int OR = 43;
    public static final int PLUS = 44;
    public static final int STAR = 45;
    public static final int OCU = 46;
    public static final int CCU = 47;
    public static final int OB = 48;
    public static final int CB = 49;
    public static final int EE = 50;
    public static final int DD = 51;
    public static final int PIPE = 52;
    public static final int CARET = 53;
    public static final int SLASH = 54;
    public static final int DDD = 55;
    public static final int SQEQ = 56;
    public static final int NEQ = 57;
    public static final int NAME = 58;
    public static final int NORMALSTRING = 59;
    public static final int CHARSTRING = 60;
    public static final int LONGSTRING = 61;
    public static final int INT = 62;
    public static final int HEX = 63;
    public static final int FLOAT = 64;
    public static final int HEX_FLOAT = 65;
    public static final int COMMENT = 66;
    public static final int WS = 67;
    public static final int NL = 68;
    public static final int SHEBANG = 69;
    public static final int HASH_COMMENT = 70;
    public static final int RULE_start = 0;
    public static final int RULE_block = 1;
    public static final int RULE_statement = 2;
    public static final int RULE_lastStatement = 3;
    public static final int RULE_functionName = 4;
    public static final int RULE_varList = 5;
    public static final int RULE_var = 6;
    public static final int RULE_nameList = 7;
    public static final int RULE_expressionList = 8;
    public static final int RULE_expression = 9;
    public static final int RULE_functionCall = 10;
    public static final int RULE_memberAccess = 11;
    public static final int RULE_args = 12;
    public static final int RULE_functionBody = 13;
    public static final int RULE_parameterList = 14;
    public static final int RULE_tableConstructor = 15;
    public static final int RULE_fieldList = 16;
    public static final int RULE_field = 17;
    public static final int RULE_number = 18;
    public static final int RULE_string = 19;
    public static final String[] ruleNames;
    private static final String[] _LITERAL_NAMES;
    private static final String[] _SYMBOLIC_NAMES;
    public static final Vocabulary VOCABULARY;

    @Deprecated
    public static final String[] tokenNames;
    public static final String _serializedATN = "\u0004\u0001FƟ\u0002\u0000\u0007\u0000\u0002\u0001\u0007\u0001\u0002\u0002\u0007\u0002\u0002\u0003\u0007\u0003\u0002\u0004\u0007\u0004\u0002\u0005\u0007\u0005\u0002\u0006\u0007\u0006\u0002\u0007\u0007\u0007\u0002\b\u0007\b\u0002\t\u0007\t\u0002\n\u0007\n\u0002\u000b\u0007\u000b\u0002\f\u0007\f\u0002\r\u0007\r\u0002\u000e\u0007\u000e\u0002\u000f\u0007\u000f\u0002\u0010\u0007\u0010\u0002\u0011\u0007\u0011\u0002\u0012\u0007\u0012\u0002\u0013\u0007\u0013\u0001\u0000\u0001\u0000\u0001\u0000\u0001\u0001\u0001\u0001\u0003\u0001.\b\u0001\u0005\u00010\b\u0001\n\u0001\f\u00013\t\u0001\u0001\u0001\u0001\u0001\u0003\u00017\b\u0001\u0003\u00019\b\u0001\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0005\u0002X\b\u0002\n\u0002\f\u0002[\t\u0002\u0001\u0002\u0001\u0002\u0003\u0002_\b\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0003\u0002k\b\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0001\u0002\u0003\u0002\u0085\b\u0002\u0003\u0002\u0087\b\u0002\u0001\u0003\u0001\u0003\u0003\u0003\u008b\b\u0003\u0001\u0003\u0001\u0003\u0003\u0003\u008f\b\u0003\u0001\u0004\u0001\u0004\u0001\u0004\u0005\u0004\u0094\b\u0004\n\u0004\f\u0004\u0097\t\u0004\u0001\u0004\u0001\u0004\u0003\u0004\u009b\b\u0004\u0001\u0005\u0001\u0005\u0001\u0005\u0005\u0005 \b\u0005\n\u0005\f\u0005£\t\u0005\u0001\u0006\u0001\u0006\u0005\u0006§\b\u0006\n\u0006\f\u0006ª\t\u0006\u0001\u0006\u0001\u0006\u0004\u0006®\b\u0006\u000b\u0006\f\u0006¯\u0001\u0006\u0001\u0006\u0001\u0006\u0001\u0006\u0004\u0006¶\b\u0006\u000b\u0006\f\u0006·\u0003\u0006º\b\u0006\u0001\u0007\u0001\u0007\u0001\u0007\u0005\u0007¿\b\u0007\n\u0007\f\u0007Â\t\u0007\u0001\b\u0001\b\u0001\b\u0005\bÇ\b\b\n\b\f\bÊ\t\b\u0001\b\u0001\b\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0004\tÚ\b\t\u000b\t\f\tÛ\u0001\t\u0001\t\u0001\t\u0004\tá\b\t\u000b\t\f\tâ\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0004\tí\b\t\u000b\t\f\tî\u0001\t\u0001\t\u0001\t\u0003\tô\b\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0001\t\u0005\tĎ\b\t\n\t\f\tđ\t\t\u0001\n\u0001\n\u0001\n\u0005\nĖ\b\n\n\n\f\nę\t\n\u0001\n\u0001\n\u0001\n\u0005\nĞ\b\n\n\n\f\nġ\t\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0005\nĪ\b\n\n\n\f\nĭ\t\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0001\n\u0005\nĵ\b\n\n\n\f\nĸ\t\n\u0001\n\u0001\n\u0001\n\u0001\n\u0003\nľ\b\n\u0001\n\u0001\n\u0005\nł\b\n\n\n\f\nŅ\t\n\u0001\n\u0001\n\u0001\n\u0005\nŊ\b\n\n\n\f\nō\t\n\u0001\n\u0001\n\u0001\n\u0005\nŒ\b\n\n\n\f\nŕ\t\n\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0001\u000b\u0003\u000bŝ\b\u000b\u0001\f\u0001\f\u0003\fš\b\f\u0001\f\u0001\f\u0001\f\u0003\fŦ\b\f\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\r\u0001\u000e\u0001\u000e\u0001\u000e\u0003\u000eű\b\u000e\u0001\u000e\u0001\u000e\u0003\u000eŵ\b\u000e\u0001\u000f\u0001\u000f\u0003\u000fŹ\b\u000f\u0001\u000f\u0001\u000f\u0001\u0010\u0001\u0010\u0001\u0010\u0005\u0010ƀ\b\u0010\n\u0010\f\u0010ƃ\t\u0010\u0001\u0010\u0003\u0010Ɔ\b\u0010\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0001\u0011\u0003\u0011ƒ\b\u0011\u0001\u0012\u0001\u0012\u0001\u0012\u0001\u0012\u0003\u0012Ƙ\b\u0012\u0001\u0013\u0001\u0013\u0001\u0013\u0003\u0013Ɲ\b\u0013\u0001\u0013\u0000\u0002\u0012\u0014\u0014\u0000\u0002\u0004\u0006\b\n\f\u000e\u0010\u0012\u0014\u0016\u0018\u001a\u001c\u001e \"$&\u0000\u0006\u0002\u0000\u001c\u001e!!\u0003\u0000%&--66\u0002\u0000\u001d\u001d,,\u0004\u0000\u0013\u0014()2289\u0003\u0000\u001c\u001c\"$44\u0002\u0000\u0001\u0001\u000f\u000fǞ\u0000(\u0001\u0000\u0000\u0000\u00021\u0001\u0000\u0000\u0000\u0004\u0086\u0001\u0000\u0000\u0000\u0006\u008e\u0001\u0000\u0000\u0000\b\u0090\u0001\u0000\u0000\u0000\n\u009c\u0001\u0000\u0000\u0000\f¹\u0001\u0000\u0000\u0000\u000e»\u0001\u0000\u0000\u0000\u0010È\u0001\u0000\u0000\u0000\u0012ó\u0001\u0000\u0000\u0000\u0014Ľ\u0001\u0000\u0000\u0000\u0016Ŝ\u0001\u0000\u0000\u0000\u0018ť\u0001\u0000\u0000\u0000\u001aŧ\u0001\u0000\u0000\u0000\u001cŴ\u0001\u0000\u0000\u0000\u001eŶ\u0001\u0000\u0000\u0000 ż\u0001\u0000\u0000\u0000\"Ƒ\u0001\u0000\u0000\u0000$Ɨ\u0001\u0000\u0000\u0000&Ɯ\u0001\u0000\u0000\u0000()\u0003\u0002\u0001\u0000)*\u0005\u0000\u0000\u0001*\u0001\u0001\u0000\u0000\u0000+-\u0003\u0004\u0002\u0000,.\u0005\u0001\u0000\u0000-,\u0001\u0000\u0000\u0000-.\u0001\u0000\u0000\u0000.0\u0001\u0000\u0000\u0000/+\u0001\u0000\u0000\u000003\u0001\u0000\u0000\u00001/\u0001\u0000\u0000\u000012\u0001\u0000\u0000\u000028\u0001\u0000\u0000\u000031\u0001\u0000\u0000\u000046\u0003\u0006\u0003\u000057\u0005\u0001\u0000\u000065\u0001\u0000\u0000\u000067\u0001\u0000\u0000\u000079\u0001\u0000\u0000\u000084\u0001\u0000\u0000\u000089\u0001\u0000\u0000\u00009\u0003\u0001\u0000\u0000\u0000:;\u0003\n\u0005\u0000;<\u0005\u0002\u0000\u0000<=\u0003\u0010\b\u0000=\u0087\u0001\u0000\u0000\u0000>\u0087\u0003\u0014\n\u0000?@\u0005\u0005\u0000\u0000@A\u0003\u0002\u0001\u0000AB\u0005\u0006\u0000\u0000B\u0087\u0001\u0000\u0000\u0000CD\u0005\u0007\u0000\u0000DE\u0003\u0012\t\u0000EF\u0005\u0005\u0000\u0000FG\u0003\u0002\u0001\u0000GH\u0005\u0006\u0000\u0000H\u0087\u0001\u0000\u0000\u0000IJ\u0005\b\u0000\u0000JK\u0003\u0002\u0001\u0000KL\u0005\t\u0000\u0000LM\u0003\u0012\t\u0000M\u0087\u0001\u0000\u0000\u0000NO\u0005\n\u0000\u0000OP\u0003\u0012\t\u0000PQ\u0005\u000b\u0000\u0000QY\u0003\u0002\u0001\u0000RS\u0005\f\u0000\u0000ST\u0003\u0012\t\u0000TU\u0005\u000b\u0000\u0000UV\u0003\u0002\u0001\u0000VX\u0001\u0000\u0000\u0000WR\u0001\u0000\u0000\u0000X[\u0001\u0000\u0000\u0000YW\u0001\u0000\u0000\u0000YZ\u0001\u0000\u0000\u0000Z^\u0001\u0000\u0000\u0000[Y\u0001\u0000\u0000\u0000\\]\u0005\r\u0000\u0000]_\u0003\u0002\u0001\u0000^\\\u0001\u0000\u0000\u0000^_\u0001\u0000\u0000\u0000_`\u0001\u0000\u0000\u0000`a\u0005\u0006\u0000\u0000a\u0087\u0001\u0000\u0000\u0000bc\u0005\u000e\u0000\u0000cd\u0005:\u0000\u0000de\u0005\u0002\u0000\u0000ef\u0003\u0012\t\u0000fg\u0005\u000f\u0000\u0000gj\u0003\u0012\t\u0000hi\u0005\u000f\u0000\u0000ik\u0003\u0012\t\u0000jh\u0001\u0000\u0000\u0000jk\u0001\u0000\u0000\u0000kl\u0001\u0000\u0000\u0000lm\u0005\u0005\u0000\u0000mn\u0003\u0002\u0001\u0000no\u0005\u0006\u0000\u0000o\u0087\u0001\u0000\u0000\u0000pq\u0005\u000e\u0000\u0000qr\u0003\u000e\u0007\u0000rs\u0005\u0010\u0000\u0000st\u0003\u0010\b\u0000tu\u0005\u0005\u0000\u0000uv\u0003\u0002\u0001\u0000vw\u0005\u0006\u0000\u0000w\u0087\u0001\u0000\u0000\u0000xy\u0005\u0011\u0000\u0000yz\u0003\b\u0004\u0000z{\u0003\u001a\r\u0000{\u0087\u0001\u0000\u0000\u0000|}\u0005\u0012\u0000\u0000}~\u0005\u0011\u0000\u0000~\u007f\u0005:\u0000\u0000\u007f\u0087\u0003\u001a\r\u0000\u0080\u0081\u0005\u0012\u0000\u0000\u0081\u0084\u0003\u000e\u0007\u0000\u0082\u0083\u0005\u0002\u0000\u0000\u0083\u0085\u0003\u0010\b\u0000\u0084\u0082\u0001\u0000\u0000\u0000\u0084\u0085\u0001\u0000\u0000\u0000\u0085\u0087\u0001\u0000\u0000\u0000\u0086:\u0001\u0000\u0000\u0000\u0086>\u0001\u0000\u0000\u0000\u0086?\u0001\u0000\u0000\u0000\u0086C\u0001\u0000\u0000\u0000\u0086I\u0001\u0000\u0000\u0000\u0086N\u0001\u0000\u0000\u0000\u0086b\u0001\u0000\u0000\u0000\u0086p\u0001\u0000\u0000\u0000\u0086x\u0001\u0000\u0000\u0000\u0086|\u0001\u0000\u0000\u0000\u0086\u0080\u0001\u0000\u0000\u0000\u0087\u0005\u0001\u0000\u0000\u0000\u0088\u008a\u0005\u0015\u0000\u0000\u0089\u008b\u0003\u0010\b\u0000\u008a\u0089\u0001\u0000\u0000\u0000\u008a\u008b\u0001\u0000\u0000\u0000\u008b\u008f\u0001\u0000\u0000\u0000\u008c\u008f\u0005\u0003\u0000\u0000\u008d\u008f\u0005\u0016\u0000\u0000\u008e\u0088\u0001\u0000\u0000\u0000\u008e\u008c\u0001\u0000\u0000\u0000\u008e\u008d\u0001\u0000\u0000\u0000\u008f\u0007\u0001\u0000\u0000\u0000\u0090\u0095\u0005:\u0000\u0000\u0091\u0092\u0005\u001b\u0000\u0000\u0092\u0094\u0005:\u0000\u0000\u0093\u0091\u0001\u0000\u0000\u0000\u0094\u0097\u0001\u0000\u0000\u0000\u0095\u0093\u0001\u0000\u0000\u0000\u0095\u0096\u0001\u0000\u0000\u0000\u0096\u009a\u0001\u0000\u0000\u0000\u0097\u0095\u0001\u0000\u0000\u0000\u0098\u0099\u0005'\u0000\u0000\u0099\u009b\u0005:\u0000\u0000\u009a\u0098\u0001\u0000\u0000\u0000\u009a\u009b\u0001\u0000\u0000\u0000\u009b\t\u0001\u0000\u0000\u0000\u009c¡\u0003\f\u0006\u0000\u009d\u009e\u0005\u000f\u0000\u0000\u009e \u0003\f\u0006\u0000\u009f\u009d\u0001\u0000\u0000\u0000 £\u0001\u0000\u0000\u0000¡\u009f\u0001\u0000\u0000\u0000¡¢\u0001\u0000\u0000\u0000¢\u000b\u0001\u0000\u0000\u0000£¡\u0001\u0000\u0000\u0000¤¨\u0005:\u0000\u0000¥§\u0003\u0016\u000b\u0000¦¥\u0001\u0000\u0000\u0000§ª\u0001\u0000\u0000\u0000¨¦\u0001\u0000\u0000\u0000¨©\u0001\u0000\u0000\u0000©º\u0001\u0000\u0000\u0000ª¨\u0001\u0000\u0000\u0000«\u00ad\u0003\u0014\n\u0000¬®\u0003\u0016\u000b\u0000\u00ad¬\u0001\u0000\u0000\u0000®¯\u0001\u0000\u0000\u0000¯\u00ad\u0001\u0000\u0000\u0000¯°\u0001\u0000\u0000\u0000°º\u0001\u0000\u0000\u0000±²\u0005\u001f\u0000\u0000²³\u0003\u0012\t\u0000³µ\u0005 \u0000\u0000´¶\u0003\u0016\u000b\u0000µ´\u0001\u0000\u0000\u0000¶·\u0001\u0000\u0000\u0000·µ\u0001\u0000\u0000\u0000·¸\u0001\u0000\u0000\u0000¸º\u0001\u0000\u0000\u0000¹¤\u0001\u0000\u0000\u0000¹«\u0001\u0000\u0000\u0000¹±\u0001\u0000\u0000\u0000º\r\u0001\u0000\u0000\u0000»À\u0005:\u0000\u0000¼½\u0005\u000f\u0000\u0000½¿\u0005:\u0000\u0000¾¼\u0001\u0000\u0000\u0000¿Â\u0001\u0000\u0000\u0000À¾\u0001\u0000\u0000\u0000ÀÁ\u0001\u0000\u0000\u0000Á\u000f\u0001\u0000\u0000\u0000ÂÀ\u0001\u0000\u0000\u0000ÃÄ\u0003\u0012\t\u0000ÄÅ\u0005\u000f\u0000\u0000ÅÇ\u0001\u0000\u0000\u0000ÆÃ\u0001\u0000\u0000\u0000ÇÊ\u0001\u0000\u0000\u0000ÈÆ\u0001\u0000\u0000\u0000ÈÉ\u0001\u0000\u0000\u0000ÉË\u0001\u0000\u0000\u0000ÊÈ\u0001\u0000\u0000\u0000ËÌ\u0003\u0012\t\u0000Ì\u0011\u0001\u0000\u0000\u0000ÍÎ\u0006\t\uffff\uffff\u0000Îô\u0005\u0018\u0000\u0000Ïô\u0005\u0019\u0000\u0000Ðô\u0005\u001a\u0000\u0000Ñô\u0003$\u0012\u0000Òô\u0003&\u0013\u0000Óô\u00057\u0000\u0000ÔÕ\u0005\u0011\u0000\u0000Õô\u0003\u001a\r\u0000Öô\u0005:\u0000\u0000×Ù\u0005:\u0000\u0000ØÚ\u0003\u0016\u000b\u0000ÙØ\u0001\u0000\u0000\u0000ÚÛ\u0001\u0000\u0000\u0000ÛÙ\u0001\u0000\u0000\u0000ÛÜ\u0001\u0000\u0000\u0000Üô\u0001\u0000\u0000\u0000Ýô\u0003\u0014\n\u0000Þà\u0003\u0014\n\u0000ßá\u0003\u0016\u000b\u0000àß\u0001\u0000\u0000\u0000áâ\u0001\u0000\u0000\u0000âà\u0001\u0000\u0000\u0000âã\u0001\u0000\u0000\u0000ãô\u0001\u0000\u0000\u0000äå\u0005\u001f\u0000\u0000åæ\u0003\u0012\t\u0000æç\u0005 \u0000\u0000çô\u0001\u0000\u0000\u0000èé\u0005\u001f\u0000\u0000éê\u0003\u0012\t\u0000êì\u0005 \u0000\u0000ëí\u0003\u0016\u000b\u0000ìë\u0001\u0000\u0000\u0000íî\u0001\u0000\u0000\u0000îì\u0001\u0000\u0000\u0000îï\u0001\u0000\u0000\u0000ïô\u0001\u0000\u0000\u0000ðô\u0003\u001e\u000f\u0000ñò\u0007\u0000\u0000\u0000òô\u0003\u0012\t\bóÍ\u0001\u0000\u0000\u0000óÏ\u0001\u0000\u0000\u0000óÐ\u0001\u0000\u0000\u0000óÑ\u0001\u0000\u0000\u0000óÒ\u0001\u0000\u0000\u0000óÓ\u0001\u0000\u0000\u0000óÔ\u0001\u0000\u0000\u0000óÖ\u0001\u0000\u0000\u0000ó×\u0001\u0000\u0000\u0000óÝ\u0001\u0000\u0000\u0000óÞ\u0001\u0000\u0000\u0000óä\u0001\u0000\u0000\u0000óè\u0001\u0000\u0000\u0000óð\u0001\u0000\u0000\u0000óñ\u0001\u0000\u0000\u0000ôď\u0001\u0000\u0000\u0000õö\n\t\u0000\u0000ö÷\u00055\u0000\u0000÷Ď\u0003\u0012\t\tøù\n\u0007\u0000\u0000ùú\u0007\u0001\u0000\u0000úĎ\u0003\u0012\t\bûü\n\u0006\u0000\u0000üý\u0007\u0002\u0000\u0000ýĎ\u0003\u0012\t\u0007þÿ\n\u0005\u0000\u0000ÿĀ\u00053\u0000\u0000ĀĎ\u0003\u0012\t\u0005āĂ\n\u0004\u0000\u0000Ăă\u0007\u0003\u0000\u0000ăĎ\u0003\u0012\t\u0005Ąą\n\u0003\u0000\u0000ąĆ\u0005*\u0000\u0000ĆĎ\u0003\u0012\t\u0004ćĈ\n\u0002\u0000\u0000Ĉĉ\u0005+\u0000\u0000ĉĎ\u0003\u0012\t\u0003Ċċ\n\u0001\u0000\u0000ċČ\u0007\u0004\u0000\u0000ČĎ\u0003\u0012\t\u0002čõ\u0001\u0000\u0000\u0000čø\u0001\u0000\u0000\u0000čû\u0001\u0000\u0000\u0000čþ\u0001\u0000\u0000\u0000čā\u0001\u0000\u0000\u0000čĄ\u0001\u0000\u0000\u0000čć\u0001\u0000\u0000\u0000čĊ\u0001\u0000\u0000\u0000Ďđ\u0001\u0000\u0000\u0000ďč\u0001\u0000\u0000\u0000ďĐ\u0001\u0000\u0000\u0000Đ\u0013\u0001\u0000\u0000\u0000đď\u0001\u0000\u0000\u0000Ēē\u0006\n\uffff\uffff\u0000ēė\u0005:\u0000\u0000ĔĖ\u0003\u0016\u000b\u0000ĕĔ\u0001\u0000\u0000\u0000Ėę\u0001\u0000\u0000\u0000ėĕ\u0001\u0000\u0000\u0000ėĘ\u0001\u0000\u0000\u0000ĘĚ\u0001\u0000\u0000\u0000ęė\u0001\u0000\u0000\u0000Ěľ\u0003\u0018\f\u0000ěğ\u0005:\u0000\u0000ĜĞ\u0003\u0016\u000b\u0000ĝĜ\u0001\u0000\u0000\u0000Ğġ\u0001\u0000\u0000\u0000ğĝ\u0001\u0000\u0000\u0000ğĠ\u0001\u0000\u0000\u0000ĠĢ\u0001\u0000\u0000\u0000ġğ\u0001\u0000\u0000\u0000Ģģ\u0005'\u0000\u0000ģĤ\u0005:\u0000\u0000Ĥľ\u0003\u0018\f\u0000ĥĦ\u0005\u001f\u0000\u0000Ħħ\u0003\u0012\t\u0000ħī\u0005 \u0000\u0000ĨĪ\u0003\u0016\u000b\u0000ĩĨ\u0001\u0000\u0000\u0000Īĭ\u0001\u0000\u0000\u0000īĩ\u0001\u0000\u0000\u0000īĬ\u0001\u0000\u0000\u0000ĬĮ\u0001\u0000\u0000\u0000ĭī\u0001\u0000\u0000\u0000Įį\u0003\u0018\f\u0000įľ\u0001\u0000\u0000\u0000İı\u0005\u001f\u0000\u0000ıĲ\u0003\u0012\t\u0000ĲĶ\u0005 \u0000\u0000ĳĵ\u0003\u0016\u000b\u0000Ĵĳ\u0001\u0000\u0000\u0000ĵĸ\u0001\u0000\u0000\u0000ĶĴ\u0001\u0000\u0000\u0000Ķķ\u0001\u0000\u0000\u0000ķĹ\u0001\u0000\u0000\u0000ĸĶ\u0001\u0000\u0000\u0000Ĺĺ\u0005'\u0000\u0000ĺĻ\u0005:\u0000\u0000Ļļ\u0003\u0018\f\u0000ļľ\u0001\u0000\u0000\u0000ĽĒ\u0001\u0000\u0000\u0000Ľě\u0001\u0000\u0000\u0000Ľĥ\u0001\u0000\u0000\u0000Ľİ\u0001\u0000\u0000\u0000ľœ\u0001\u0000\u0000\u0000ĿŃ\n\u0002\u0000\u0000ŀł\u0003\u0016\u000b\u0000Łŀ\u0001\u0000\u0000\u0000łŅ\u0001\u0000\u0000\u0000ŃŁ\u0001\u0000\u0000\u0000Ńń\u0001\u0000\u0000\u0000ńņ\u0001\u0000\u0000\u0000ŅŃ\u0001\u0000\u0000\u0000ņŒ\u0003\u0018\f\u0000Ňŋ\n\u0001\u0000\u0000ňŊ\u0003\u0016\u000b\u0000ŉň\u0001\u0000\u0000\u0000Ŋō\u0001\u0000\u0000\u0000ŋŉ\u0001\u0000\u0000\u0000ŋŌ\u0001\u0000\u0000\u0000ŌŎ\u0001\u0000\u0000\u0000ōŋ\u0001\u0000\u0000\u0000Ŏŏ\u0005'\u0000\u0000ŏŐ\u0005:\u0000\u0000ŐŒ\u0003\u0018\f\u0000őĿ\u0001\u0000\u0000\u0000őŇ\u0001\u0000\u0000\u0000Œŕ\u0001\u0000\u0000\u0000œő\u0001\u0000\u0000\u0000œŔ\u0001\u0000\u0000\u0000Ŕ\u0015\u0001\u0000\u0000\u0000ŕœ\u0001\u0000\u0000\u0000Ŗŗ\u00050\u0000\u0000ŗŘ\u0003\u0012\t\u0000Řř\u00051\u0000\u0000řŝ\u0001\u0000\u0000\u0000Śś\u0005\u001b\u0000\u0000śŝ\u0005:\u0000\u0000ŜŖ\u0001\u0000\u0000\u0000ŜŚ\u0001\u0000\u0000\u0000ŝ\u0017\u0001\u0000\u0000\u0000ŞŠ\u0005\u001f\u0000\u0000şš\u0003\u0010\b\u0000Šş\u0001\u0000\u0000\u0000Šš\u0001\u0000\u0000\u0000šŢ\u0001\u0000\u0000\u0000ŢŦ\u0005 \u0000\u0000ţŦ\u0003\u001e\u000f\u0000ŤŦ\u0003&\u0013\u0000ťŞ\u0001\u0000\u0000\u0000ťţ\u0001\u0000\u0000\u0000ťŤ\u0001\u0000\u0000\u0000Ŧ\u0019\u0001\u0000\u0000\u0000ŧŨ\u0005\u001f\u0000\u0000Ũũ\u0003\u001c\u000e\u0000ũŪ\u0005 \u0000\u0000Ūū\u0003\u0002\u0001\u0000ūŬ\u0005\u0006\u0000\u0000Ŭ\u001b\u0001\u0000\u0000\u0000ŭŰ\u0003\u000e\u0007\u0000Ůů\u0005\u000f\u0000\u0000ůű\u00057\u0000\u0000ŰŮ\u0001\u0000\u0000\u0000Űű\u0001\u0000\u0000\u0000űŵ\u0001\u0000\u0000\u0000Ųŵ\u00057\u0000\u0000ųŵ\u0001\u0000\u0000\u0000Ŵŭ\u0001\u0000\u0000\u0000ŴŲ\u0001\u0000\u0000\u0000Ŵų\u0001\u0000\u0000\u0000ŵ\u001d\u0001\u0000\u0000\u0000ŶŸ\u0005.\u0000\u0000ŷŹ\u0003 \u0010\u0000Ÿŷ\u0001\u0000\u0000\u0000ŸŹ\u0001\u0000\u0000\u0000Źź\u0001\u0000\u0000\u0000źŻ\u0005/\u0000\u0000Ż\u001f\u0001\u0000\u0000\u0000żƁ\u0003\"\u0011\u0000Žž\u0007\u0005\u0000\u0000žƀ\u0003\"\u0011\u0000ſŽ\u0001\u0000\u0000\u0000ƀƃ\u0001\u0000\u0000\u0000Ɓſ\u0001\u0000\u0000\u0000ƁƂ\u0001\u0000\u0000\u0000Ƃƅ\u0001\u0000\u0000\u0000ƃƁ\u0001\u0000\u0000\u0000ƄƆ\u0007\u0005\u0000\u0000ƅƄ\u0001\u0000\u0000\u0000ƅƆ\u0001\u0000\u0000\u0000Ɔ!\u0001\u0000\u0000\u0000Ƈƈ\u00050\u0000\u0000ƈƉ\u0003\u0012\t\u0000ƉƊ\u00051\u0000\u0000ƊƋ\u0005\u0002\u0000\u0000Ƌƌ\u0003\u0012\t\u0000ƌƒ\u0001\u0000\u0000\u0000ƍƎ\u0005:\u0000\u0000ƎƏ\u0005\u0002\u0000\u0000Əƒ\u0003\u0012\t\u0000Ɛƒ\u0003\u0012\t\u0000ƑƇ\u0001\u0000\u0000\u0000Ƒƍ\u0001\u0000\u0000\u0000ƑƐ\u0001\u0000\u0000\u0000ƒ#\u0001\u0000\u0000\u0000ƓƘ\u0005>\u0000\u0000ƔƘ\u0005?\u0000\u0000ƕƘ\u0005@\u0000\u0000ƖƘ\u0005A\u0000\u0000ƗƓ\u0001\u0000\u0000\u0000ƗƔ\u0001\u0000\u0000\u0000Ɨƕ\u0001\u0000\u0000\u0000ƗƖ\u0001\u0000\u0000\u0000Ƙ%\u0001\u0000\u0000\u0000ƙƝ\u0005;\u0000\u0000ƚƝ\u0005<\u0000\u0000ƛƝ\u0005=\u0000\u0000Ɯƙ\u0001\u0000\u0000\u0000Ɯƚ\u0001\u0000\u0000\u0000Ɯƛ\u0001\u0000\u0000\u0000Ɲ'\u0001\u0000\u0000\u0000.-168Y^j\u0084\u0086\u008a\u008e\u0095\u009a¡¨¯·¹ÀÈÛâîóčďėğīĶĽŃŋőœŜŠťŰŴŸƁƅƑƗƜ";
    public static final ATN _ATN;

    static {
        RuntimeMetaData.checkVersion("4.13.2", "4.13.2");
        _sharedContextCache = new PredictionContextCache();
        ruleNames = makeRuleNames();
        _LITERAL_NAMES = makeLiteralNames();
        _SYMBOLIC_NAMES = makeSymbolicNames();
        VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);
        tokenNames = new String[_SYMBOLIC_NAMES.length];
        for (int i = 0; i < tokenNames.length; i++) {
            tokenNames[i] = VOCABULARY.getLiteralName(i);
            if (tokenNames[i] == null) {
                tokenNames[i] = VOCABULARY.getSymbolicName(i);
            }
            if (tokenNames[i] == null) {
                tokenNames[i] = "<INVALID>";
            }
        }
        _ATN = new ATNDeserializer().deserialize(_serializedATN.toCharArray());
        _decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
        for (int i2 = 0; i2 < _ATN.getNumberOfDecisions(); i2++) {
            _decisionToDFA[i2] = new DFA(_ATN.getDecisionState(i2), i2);
        }
    }

    private static String[] makeRuleNames() {
        return new String[]{"start", "block", "statement", "lastStatement", "functionName", "varList", "var", "nameList", "expressionList", "expression", "functionCall", "memberAccess", "args", "functionBody", "parameterList", "tableConstructor", "fieldList", "field", "number", "string"};
    }

    private static String[] makeLiteralNames() {
        return new String[]{null, "';'", "'='", "'break'", "'goto'", "'do'", "'end'", "'while'", "'repeat'", "'until'", "'if'", "'then'", "'elseif'", "'else'", "'for'", "','", "'in'", "'function'", "'local'", "'<'", "'>'", "'return'", "'continue'", "'::'", "'nil'", "'false'", "'true'", "'.'", "'~'", "'-'", "'#'", "'('", "')'", "'not'", "'<<'", "'>>'", "'&'", "'//'", "'%'", "':'", "'<='", "'>='", "'and'", "'or'", "'+'", "'*'", "'{'", "'}'", "'['", "']'", "'=='", "'..'", "'|'", "'^'", "'/'", "'...'", "'~='", "'!='"};
    }

    private static String[] makeSymbolicNames() {
        return new String[]{null, "SEMI", "EQ", "BREAK", "GOTO", "DO", "END", "WHILE", "REPEAT", "UNTIL", "IF", "THEN", "ELSEIF", "ELSE", "FOR", "COMMA", "IN", "FUNCTION", "LOCAL", "LT", "GT", "RETURN", "CONTINUE", "CC", "NIL", "FALSE", "TRUE", "DOT", "SQUIG", "MINUS", "POUND", "OP", "CP", "NOT", "LL", "GG", "AMP", "SS", "PER", "COL", "LE", "GE", "AND", "OR", "PLUS", "STAR", "OCU", "CCU", "OB", "CB", "EE", "DD", "PIPE", "CARET", "SLASH", "DDD", "SQEQ", "NEQ", "NAME", "NORMALSTRING", "CHARSTRING", "LONGSTRING", "INT", "HEX", "FLOAT", "HEX_FLOAT", "COMMENT", "WS", "NL", "SHEBANG", "HASH_COMMENT"};
    }

    @Deprecated
    public String[] getTokenNames() {
        return tokenNames;
    }

    public Vocabulary getVocabulary() {
        return VOCABULARY;
    }

    public String getGrammarFileName() {
        return "LuaParser.g4";
    }

    public String[] getRuleNames() {
        return ruleNames;
    }

    public String getSerializedATN() {
        return _serializedATN;
    }

    public ATN getATN() {
        return _ATN;
    }

    public LuaParser(TokenStream input) {
        super(input);
        this._interp = new ParserATNSimulator(this, _ATN, _decisionToDFA, _sharedContextCache);
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/LuaParser$StartContext.class */
    public static class StartContext extends ParserRuleContext {
        public BlockContext block() {
            return (BlockContext) getRuleContext(BlockContext.class, 0);
        }

        public TerminalNode EOF() {
            return getToken(-1, 0);
        }

        public StartContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        public int getRuleIndex() {
            return 0;
        }

        public <T> T accept(ParseTreeVisitor<? extends T> parseTreeVisitor) {
            return parseTreeVisitor instanceof LuaParserVisitor ? (T) ((LuaParserVisitor) parseTreeVisitor).visitStart(this) : (T) parseTreeVisitor.visitChildren(this);
        }
    }

    public final StartContext start() throws RecognitionException {
        StartContext _localctx = new StartContext(this._ctx, getState());
        enterRule(_localctx, 0, 0);
        try {
            enterOuterAlt(_localctx, 1);
            setState(40);
            block();
            setState(41);
            match(-1);
        } catch (RecognitionException re) {
            _localctx.exception = re;
            this._errHandler.reportError(this, re);
            this._errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/LuaParser$BlockContext.class */
    public static class BlockContext extends ParserRuleContext {
        public List<StatementContext> statement() {
            return getRuleContexts(StatementContext.class);
        }

        public StatementContext statement(int i) {
            return (StatementContext) getRuleContext(StatementContext.class, i);
        }

        public LastStatementContext lastStatement() {
            return (LastStatementContext) getRuleContext(LastStatementContext.class, 0);
        }

        public List<TerminalNode> SEMI() {
            return getTokens(1);
        }

        public TerminalNode SEMI(int i) {
            return getToken(1, i);
        }

        public BlockContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        public int getRuleIndex() {
            return 1;
        }

        public <T> T accept(ParseTreeVisitor<? extends T> parseTreeVisitor) {
            return parseTreeVisitor instanceof LuaParserVisitor ? (T) ((LuaParserVisitor) parseTreeVisitor).visitBlock(this) : (T) parseTreeVisitor.visitChildren(this);
        }
    }

    public final BlockContext block() throws RecognitionException {
        BlockContext _localctx = new BlockContext(this._ctx, getState());
        enterRule(_localctx, 2, 1);
        try {
            enterOuterAlt(_localctx, 1);
            setState(49);
            this._errHandler.sync(this);
            int _la = this._input.LA(1);
            while ((_la & (-64)) == 0 && ((1 << _la) & 288230378299606432L) != 0) {
                setState(43);
                statement();
                setState(45);
                this._errHandler.sync(this);
                int _la2 = this._input.LA(1);
                if (_la2 == 1) {
                    setState(44);
                    match(1);
                }
                setState(51);
                this._errHandler.sync(this);
                _la = this._input.LA(1);
            }
            setState(56);
            this._errHandler.sync(this);
            int _la3 = this._input.LA(1);
            if ((_la3 & (-64)) == 0 && ((1 << _la3) & 6291464) != 0) {
                setState(52);
                lastStatement();
                setState(54);
                this._errHandler.sync(this);
                int _la4 = this._input.LA(1);
                if (_la4 == 1) {
                    setState(53);
                    match(1);
                }
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            this._errHandler.reportError(this, re);
            this._errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/LuaParser$StatementContext.class */
    public static class StatementContext extends ParserRuleContext {
        public StatementContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        public int getRuleIndex() {
            return 2;
        }

        public StatementContext() {
        }

        public void copyFrom(StatementContext ctx) {
            super.copyFrom(ctx);
        }
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/LuaParser$WhileStatementContext.class */
    public static class WhileStatementContext extends StatementContext {
        public TerminalNode WHILE() {
            return getToken(7, 0);
        }

        public ExpressionContext expression() {
            return (ExpressionContext) getRuleContext(ExpressionContext.class, 0);
        }

        public TerminalNode DO() {
            return getToken(5, 0);
        }

        public BlockContext block() {
            return (BlockContext) getRuleContext(BlockContext.class, 0);
        }

        public TerminalNode END() {
            return getToken(6, 0);
        }

        public WhileStatementContext(StatementContext ctx) {
            copyFrom(ctx);
        }

        public <T> T accept(ParseTreeVisitor<? extends T> parseTreeVisitor) {
            return parseTreeVisitor instanceof LuaParserVisitor ? (T) ((LuaParserVisitor) parseTreeVisitor).visitWhileStatement(this) : (T) parseTreeVisitor.visitChildren(this);
        }
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/LuaParser$DoStatementContext.class */
    public static class DoStatementContext extends StatementContext {
        public TerminalNode DO() {
            return getToken(5, 0);
        }

        public BlockContext block() {
            return (BlockContext) getRuleContext(BlockContext.class, 0);
        }

        public TerminalNode END() {
            return getToken(6, 0);
        }

        public DoStatementContext(StatementContext ctx) {
            copyFrom(ctx);
        }

        public <T> T accept(ParseTreeVisitor<? extends T> parseTreeVisitor) {
            return parseTreeVisitor instanceof LuaParserVisitor ? (T) ((LuaParserVisitor) parseTreeVisitor).visitDoStatement(this) : (T) parseTreeVisitor.visitChildren(this);
        }
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/LuaParser$RepeatStatementContext.class */
    public static class RepeatStatementContext extends StatementContext {
        public TerminalNode REPEAT() {
            return getToken(8, 0);
        }

        public BlockContext block() {
            return (BlockContext) getRuleContext(BlockContext.class, 0);
        }

        public TerminalNode UNTIL() {
            return getToken(9, 0);
        }

        public ExpressionContext expression() {
            return (ExpressionContext) getRuleContext(ExpressionContext.class, 0);
        }

        public RepeatStatementContext(StatementContext ctx) {
            copyFrom(ctx);
        }

        public <T> T accept(ParseTreeVisitor<? extends T> parseTreeVisitor) {
            return parseTreeVisitor instanceof LuaParserVisitor ? (T) ((LuaParserVisitor) parseTreeVisitor).visitRepeatStatement(this) : (T) parseTreeVisitor.visitChildren(this);
        }
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/LuaParser$LocalAssignmentStatementContext.class */
    public static class LocalAssignmentStatementContext extends StatementContext {
        public TerminalNode LOCAL() {
            return getToken(18, 0);
        }

        public NameListContext nameList() {
            return (NameListContext) getRuleContext(NameListContext.class, 0);
        }

        public TerminalNode EQ() {
            return getToken(2, 0);
        }

        public ExpressionListContext expressionList() {
            return (ExpressionListContext) getRuleContext(ExpressionListContext.class, 0);
        }

        public LocalAssignmentStatementContext(StatementContext ctx) {
            copyFrom(ctx);
        }

        public <T> T accept(ParseTreeVisitor<? extends T> parseTreeVisitor) {
            return parseTreeVisitor instanceof LuaParserVisitor ? (T) ((LuaParserVisitor) parseTreeVisitor).visitLocalAssignmentStatement(this) : (T) parseTreeVisitor.visitChildren(this);
        }
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/LuaParser$GenericForStatementContext.class */
    public static class GenericForStatementContext extends StatementContext {
        public TerminalNode FOR() {
            return getToken(14, 0);
        }

        public NameListContext nameList() {
            return (NameListContext) getRuleContext(NameListContext.class, 0);
        }

        public TerminalNode IN() {
            return getToken(16, 0);
        }

        public ExpressionListContext expressionList() {
            return (ExpressionListContext) getRuleContext(ExpressionListContext.class, 0);
        }

        public TerminalNode DO() {
            return getToken(5, 0);
        }

        public BlockContext block() {
            return (BlockContext) getRuleContext(BlockContext.class, 0);
        }

        public TerminalNode END() {
            return getToken(6, 0);
        }

        public GenericForStatementContext(StatementContext ctx) {
            copyFrom(ctx);
        }

        public <T> T accept(ParseTreeVisitor<? extends T> parseTreeVisitor) {
            return parseTreeVisitor instanceof LuaParserVisitor ? (T) ((LuaParserVisitor) parseTreeVisitor).visitGenericForStatement(this) : (T) parseTreeVisitor.visitChildren(this);
        }
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/LuaParser$FunctionStatementContext.class */
    public static class FunctionStatementContext extends StatementContext {
        public TerminalNode FUNCTION() {
            return getToken(17, 0);
        }

        public FunctionNameContext functionName() {
            return (FunctionNameContext) getRuleContext(FunctionNameContext.class, 0);
        }

        public FunctionBodyContext functionBody() {
            return (FunctionBodyContext) getRuleContext(FunctionBodyContext.class, 0);
        }

        public FunctionStatementContext(StatementContext ctx) {
            copyFrom(ctx);
        }

        public <T> T accept(ParseTreeVisitor<? extends T> parseTreeVisitor) {
            return parseTreeVisitor instanceof LuaParserVisitor ? (T) ((LuaParserVisitor) parseTreeVisitor).visitFunctionStatement(this) : (T) parseTreeVisitor.visitChildren(this);
        }
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/LuaParser$AssignmentStatementContext.class */
    public static class AssignmentStatementContext extends StatementContext {
        public VarListContext varList() {
            return (VarListContext) getRuleContext(VarListContext.class, 0);
        }

        public TerminalNode EQ() {
            return getToken(2, 0);
        }

        public ExpressionListContext expressionList() {
            return (ExpressionListContext) getRuleContext(ExpressionListContext.class, 0);
        }

        public AssignmentStatementContext(StatementContext ctx) {
            copyFrom(ctx);
        }

        public <T> T accept(ParseTreeVisitor<? extends T> parseTreeVisitor) {
            return parseTreeVisitor instanceof LuaParserVisitor ? (T) ((LuaParserVisitor) parseTreeVisitor).visitAssignmentStatement(this) : (T) parseTreeVisitor.visitChildren(this);
        }
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/LuaParser$NumericForStatementContext.class */
    public static class NumericForStatementContext extends StatementContext {
        public ExpressionContext startValue;
        public ExpressionContext endValue;
        public ExpressionContext stepValue;

        public TerminalNode FOR() {
            return getToken(14, 0);
        }

        public TerminalNode NAME() {
            return getToken(58, 0);
        }

        public TerminalNode EQ() {
            return getToken(2, 0);
        }

        public List<TerminalNode> COMMA() {
            return getTokens(15);
        }

        public TerminalNode COMMA(int i) {
            return getToken(15, i);
        }

        public TerminalNode DO() {
            return getToken(5, 0);
        }

        public BlockContext block() {
            return (BlockContext) getRuleContext(BlockContext.class, 0);
        }

        public TerminalNode END() {
            return getToken(6, 0);
        }

        public List<ExpressionContext> expression() {
            return getRuleContexts(ExpressionContext.class);
        }

        public ExpressionContext expression(int i) {
            return (ExpressionContext) getRuleContext(ExpressionContext.class, i);
        }

        public NumericForStatementContext(StatementContext ctx) {
            copyFrom(ctx);
        }

        public <T> T accept(ParseTreeVisitor<? extends T> parseTreeVisitor) {
            return parseTreeVisitor instanceof LuaParserVisitor ? (T) ((LuaParserVisitor) parseTreeVisitor).visitNumericForStatement(this) : (T) parseTreeVisitor.visitChildren(this);
        }
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/LuaParser$LocalFunctionStatementContext.class */
    public static class LocalFunctionStatementContext extends StatementContext {
        public TerminalNode LOCAL() {
            return getToken(18, 0);
        }

        public TerminalNode FUNCTION() {
            return getToken(17, 0);
        }

        public TerminalNode NAME() {
            return getToken(58, 0);
        }

        public FunctionBodyContext functionBody() {
            return (FunctionBodyContext) getRuleContext(FunctionBodyContext.class, 0);
        }

        public LocalFunctionStatementContext(StatementContext ctx) {
            copyFrom(ctx);
        }

        public <T> T accept(ParseTreeVisitor<? extends T> parseTreeVisitor) {
            return parseTreeVisitor instanceof LuaParserVisitor ? (T) ((LuaParserVisitor) parseTreeVisitor).visitLocalFunctionStatement(this) : (T) parseTreeVisitor.visitChildren(this);
        }
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/LuaParser$FunctionCallStatementContext.class */
    public static class FunctionCallStatementContext extends StatementContext {
        public FunctionCallContext functionCall() {
            return (FunctionCallContext) getRuleContext(FunctionCallContext.class, 0);
        }

        public FunctionCallStatementContext(StatementContext ctx) {
            copyFrom(ctx);
        }

        public <T> T accept(ParseTreeVisitor<? extends T> parseTreeVisitor) {
            return parseTreeVisitor instanceof LuaParserVisitor ? (T) ((LuaParserVisitor) parseTreeVisitor).visitFunctionCallStatement(this) : (T) parseTreeVisitor.visitChildren(this);
        }
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/LuaParser$IfStatementContext.class */
    public static class IfStatementContext extends StatementContext {
        public TerminalNode IF() {
            return getToken(10, 0);
        }

        public List<ExpressionContext> expression() {
            return getRuleContexts(ExpressionContext.class);
        }

        public ExpressionContext expression(int i) {
            return (ExpressionContext) getRuleContext(ExpressionContext.class, i);
        }

        public List<TerminalNode> THEN() {
            return getTokens(11);
        }

        public TerminalNode THEN(int i) {
            return getToken(11, i);
        }

        public List<BlockContext> block() {
            return getRuleContexts(BlockContext.class);
        }

        public BlockContext block(int i) {
            return (BlockContext) getRuleContext(BlockContext.class, i);
        }

        public TerminalNode END() {
            return getToken(6, 0);
        }

        public List<TerminalNode> ELSEIF() {
            return getTokens(12);
        }

        public TerminalNode ELSEIF(int i) {
            return getToken(12, i);
        }

        public TerminalNode ELSE() {
            return getToken(13, 0);
        }

        public IfStatementContext(StatementContext ctx) {
            copyFrom(ctx);
        }

        public <T> T accept(ParseTreeVisitor<? extends T> parseTreeVisitor) {
            return parseTreeVisitor instanceof LuaParserVisitor ? (T) ((LuaParserVisitor) parseTreeVisitor).visitIfStatement(this) : (T) parseTreeVisitor.visitChildren(this);
        }
    }

    public final StatementContext statement() throws RecognitionException {
        StatementContext _localctx = new StatementContext(this._ctx, getState());
        enterRule(_localctx, 4, 2);
        try {
            setState(134);
            this._errHandler.sync(this);
            switch (getInterpreter().adaptivePredict(this._input, 8, this._ctx)) {
                case 1:
                    _localctx = new AssignmentStatementContext(_localctx);
                    enterOuterAlt(_localctx, 1);
                    setState(58);
                    varList();
                    setState(59);
                    match(2);
                    setState(60);
                    expressionList();
                    break;
                case 2:
                    _localctx = new FunctionCallStatementContext(_localctx);
                    enterOuterAlt(_localctx, 2);
                    setState(62);
                    functionCall(0);
                    break;
                case 3:
                    _localctx = new DoStatementContext(_localctx);
                    enterOuterAlt(_localctx, 3);
                    setState(63);
                    match(5);
                    setState(64);
                    block();
                    setState(65);
                    match(6);
                    break;
                case 4:
                    _localctx = new WhileStatementContext(_localctx);
                    enterOuterAlt(_localctx, 4);
                    setState(67);
                    match(7);
                    setState(68);
                    expression(0);
                    setState(69);
                    match(5);
                    setState(70);
                    block();
                    setState(71);
                    match(6);
                    break;
                case 5:
                    _localctx = new RepeatStatementContext(_localctx);
                    enterOuterAlt(_localctx, 5);
                    setState(73);
                    match(8);
                    setState(74);
                    block();
                    setState(75);
                    match(9);
                    setState(76);
                    expression(0);
                    break;
                case 6:
                    _localctx = new IfStatementContext(_localctx);
                    enterOuterAlt(_localctx, 6);
                    setState(78);
                    match(10);
                    setState(79);
                    expression(0);
                    setState(80);
                    match(11);
                    setState(81);
                    block();
                    setState(89);
                    this._errHandler.sync(this);
                    int _la = this._input.LA(1);
                    while (_la == 12) {
                        setState(82);
                        match(12);
                        setState(83);
                        expression(0);
                        setState(84);
                        match(11);
                        setState(85);
                        block();
                        setState(91);
                        this._errHandler.sync(this);
                        _la = this._input.LA(1);
                    }
                    setState(94);
                    this._errHandler.sync(this);
                    int _la2 = this._input.LA(1);
                    if (_la2 == 13) {
                        setState(92);
                        match(13);
                        setState(93);
                        block();
                    }
                    setState(96);
                    match(6);
                    break;
                case 7:
                    _localctx = new NumericForStatementContext(_localctx);
                    enterOuterAlt(_localctx, 7);
                    setState(98);
                    match(14);
                    setState(99);
                    match(58);
                    setState(100);
                    match(2);
                    setState(101);
                    ((NumericForStatementContext) _localctx).startValue = expression(0);
                    setState(102);
                    match(15);
                    setState(103);
                    ((NumericForStatementContext) _localctx).endValue = expression(0);
                    setState(106);
                    this._errHandler.sync(this);
                    int _la3 = this._input.LA(1);
                    if (_la3 == 15) {
                        setState(104);
                        match(15);
                        setState(105);
                        ((NumericForStatementContext) _localctx).stepValue = expression(0);
                    }
                    setState(108);
                    match(5);
                    setState(109);
                    block();
                    setState(110);
                    match(6);
                    break;
                case 8:
                    _localctx = new GenericForStatementContext(_localctx);
                    enterOuterAlt(_localctx, 8);
                    setState(112);
                    match(14);
                    setState(113);
                    nameList();
                    setState(114);
                    match(16);
                    setState(115);
                    expressionList();
                    setState(116);
                    match(5);
                    setState(117);
                    block();
                    setState(118);
                    match(6);
                    break;
                case 9:
                    _localctx = new FunctionStatementContext(_localctx);
                    enterOuterAlt(_localctx, 9);
                    setState(120);
                    match(17);
                    setState(121);
                    functionName();
                    setState(122);
                    functionBody();
                    break;
                case 10:
                    _localctx = new LocalFunctionStatementContext(_localctx);
                    enterOuterAlt(_localctx, 10);
                    setState(124);
                    match(18);
                    setState(125);
                    match(17);
                    setState(126);
                    match(58);
                    setState(GeneratedMapNameEncoder.NUM_BINS);
                    functionBody();
                    break;
                case 11:
                    _localctx = new LocalAssignmentStatementContext(_localctx);
                    enterOuterAlt(_localctx, 11);
                    setState(128);
                    match(18);
                    setState(129);
                    nameList();
                    setState(132);
                    this._errHandler.sync(this);
                    int _la4 = this._input.LA(1);
                    if (_la4 == 2) {
                        setState(130);
                        match(2);
                        setState(131);
                        expressionList();
                    }
                    break;
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            this._errHandler.reportError(this, re);
            this._errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/LuaParser$LastStatementContext.class */
    public static class LastStatementContext extends ParserRuleContext {
        public LastStatementContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        public int getRuleIndex() {
            return 3;
        }

        public LastStatementContext() {
        }

        public void copyFrom(LastStatementContext ctx) {
            super.copyFrom(ctx);
        }
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/LuaParser$BreakStatementContext.class */
    public static class BreakStatementContext extends LastStatementContext {
        public TerminalNode BREAK() {
            return getToken(3, 0);
        }

        public BreakStatementContext(LastStatementContext ctx) {
            copyFrom(ctx);
        }

        public <T> T accept(ParseTreeVisitor<? extends T> parseTreeVisitor) {
            return parseTreeVisitor instanceof LuaParserVisitor ? (T) ((LuaParserVisitor) parseTreeVisitor).visitBreakStatement(this) : (T) parseTreeVisitor.visitChildren(this);
        }
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/LuaParser$ContinueStatementContext.class */
    public static class ContinueStatementContext extends LastStatementContext {
        public TerminalNode CONTINUE() {
            return getToken(22, 0);
        }

        public ContinueStatementContext(LastStatementContext ctx) {
            copyFrom(ctx);
        }

        public <T> T accept(ParseTreeVisitor<? extends T> parseTreeVisitor) {
            return parseTreeVisitor instanceof LuaParserVisitor ? (T) ((LuaParserVisitor) parseTreeVisitor).visitContinueStatement(this) : (T) parseTreeVisitor.visitChildren(this);
        }
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/LuaParser$ReturnStatementContext.class */
    public static class ReturnStatementContext extends LastStatementContext {
        public TerminalNode RETURN() {
            return getToken(21, 0);
        }

        public ExpressionListContext expressionList() {
            return (ExpressionListContext) getRuleContext(ExpressionListContext.class, 0);
        }

        public ReturnStatementContext(LastStatementContext ctx) {
            copyFrom(ctx);
        }

        public <T> T accept(ParseTreeVisitor<? extends T> parseTreeVisitor) {
            return parseTreeVisitor instanceof LuaParserVisitor ? (T) ((LuaParserVisitor) parseTreeVisitor).visitReturnStatement(this) : (T) parseTreeVisitor.visitChildren(this);
        }
    }

    public final LastStatementContext lastStatement() throws RecognitionException {
        LastStatementContext _localctx = new LastStatementContext(this._ctx, getState());
        enterRule(_localctx, 6, 3);
        try {
            try {
                setState(142);
                this._errHandler.sync(this);
                switch (this._input.LA(1)) {
                    case 3:
                        _localctx = new BreakStatementContext(_localctx);
                        enterOuterAlt(_localctx, 2);
                        setState(140);
                        match(3);
                        break;
                    case 21:
                        _localctx = new ReturnStatementContext(_localctx);
                        enterOuterAlt(_localctx, 1);
                        setState(136);
                        match(21);
                        setState(138);
                        this._errHandler.sync(this);
                        int _la = this._input.LA(1);
                        if (((_la - 17) & (-64)) == 0 && ((1 << (_la - 17)) & 561026345040769L) != 0) {
                            setState(137);
                            expressionList();
                        }
                        break;
                    case 22:
                        _localctx = new ContinueStatementContext(_localctx);
                        enterOuterAlt(_localctx, 3);
                        setState(141);
                        match(22);
                        break;
                    default:
                        throw new NoViableAltException(this);
                }
                exitRule();
            } catch (RecognitionException re) {
                _localctx.exception = re;
                this._errHandler.reportError(this, re);
                this._errHandler.recover(this, re);
                exitRule();
            }
            return _localctx;
        } catch (Throwable th) {
            exitRule();
            throw th;
        }
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/LuaParser$FunctionNameContext.class */
    public static class FunctionNameContext extends ParserRuleContext {
        public List<TerminalNode> NAME() {
            return getTokens(58);
        }

        public TerminalNode NAME(int i) {
            return getToken(58, i);
        }

        public List<TerminalNode> DOT() {
            return getTokens(27);
        }

        public TerminalNode DOT(int i) {
            return getToken(27, i);
        }

        public TerminalNode COL() {
            return getToken(39, 0);
        }

        public FunctionNameContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        public int getRuleIndex() {
            return 4;
        }

        public <T> T accept(ParseTreeVisitor<? extends T> parseTreeVisitor) {
            return parseTreeVisitor instanceof LuaParserVisitor ? (T) ((LuaParserVisitor) parseTreeVisitor).visitFunctionName(this) : (T) parseTreeVisitor.visitChildren(this);
        }
    }

    public final FunctionNameContext functionName() throws RecognitionException {
        FunctionNameContext _localctx = new FunctionNameContext(this._ctx, getState());
        enterRule(_localctx, 8, 4);
        try {
            enterOuterAlt(_localctx, 1);
            setState(144);
            match(58);
            setState(149);
            this._errHandler.sync(this);
            int _la = this._input.LA(1);
            while (_la == 27) {
                setState(145);
                match(27);
                setState(146);
                match(58);
                setState(151);
                this._errHandler.sync(this);
                _la = this._input.LA(1);
            }
            setState(154);
            this._errHandler.sync(this);
            int _la2 = this._input.LA(1);
            if (_la2 == 39) {
                setState(152);
                match(39);
                setState(153);
                match(58);
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            this._errHandler.reportError(this, re);
            this._errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/LuaParser$VarListContext.class */
    public static class VarListContext extends ParserRuleContext {
        public List<VarContext> var() {
            return getRuleContexts(VarContext.class);
        }

        public VarContext var(int i) {
            return (VarContext) getRuleContext(VarContext.class, i);
        }

        public List<TerminalNode> COMMA() {
            return getTokens(15);
        }

        public TerminalNode COMMA(int i) {
            return getToken(15, i);
        }

        public VarListContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        public int getRuleIndex() {
            return 5;
        }

        public <T> T accept(ParseTreeVisitor<? extends T> parseTreeVisitor) {
            return parseTreeVisitor instanceof LuaParserVisitor ? (T) ((LuaParserVisitor) parseTreeVisitor).visitVarList(this) : (T) parseTreeVisitor.visitChildren(this);
        }
    }

    public final VarListContext varList() throws RecognitionException {
        VarListContext _localctx = new VarListContext(this._ctx, getState());
        enterRule(_localctx, 10, 5);
        try {
            enterOuterAlt(_localctx, 1);
            setState(156);
            var();
            setState(161);
            this._errHandler.sync(this);
            int _la = this._input.LA(1);
            while (_la == 15) {
                setState(157);
                match(15);
                setState(158);
                var();
                setState(163);
                this._errHandler.sync(this);
                _la = this._input.LA(1);
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            this._errHandler.reportError(this, re);
            this._errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/LuaParser$VarContext.class */
    public static class VarContext extends ParserRuleContext {
        public VarContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        public int getRuleIndex() {
            return 6;
        }

        public VarContext() {
        }

        public void copyFrom(VarContext ctx) {
            super.copyFrom(ctx);
        }
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/LuaParser$FunctionVarContext.class */
    public static class FunctionVarContext extends VarContext {
        public FunctionCallContext functionCall() {
            return (FunctionCallContext) getRuleContext(FunctionCallContext.class, 0);
        }

        public List<MemberAccessContext> memberAccess() {
            return getRuleContexts(MemberAccessContext.class);
        }

        public MemberAccessContext memberAccess(int i) {
            return (MemberAccessContext) getRuleContext(MemberAccessContext.class, i);
        }

        public FunctionVarContext(VarContext ctx) {
            copyFrom(ctx);
        }

        public <T> T accept(ParseTreeVisitor<? extends T> parseTreeVisitor) {
            return parseTreeVisitor instanceof LuaParserVisitor ? (T) ((LuaParserVisitor) parseTreeVisitor).visitFunctionVar(this) : (T) parseTreeVisitor.visitChildren(this);
        }
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/LuaParser$ExpressionVarContext.class */
    public static class ExpressionVarContext extends VarContext {
        public TerminalNode OP() {
            return getToken(31, 0);
        }

        public ExpressionContext expression() {
            return (ExpressionContext) getRuleContext(ExpressionContext.class, 0);
        }

        public TerminalNode CP() {
            return getToken(32, 0);
        }

        public List<MemberAccessContext> memberAccess() {
            return getRuleContexts(MemberAccessContext.class);
        }

        public MemberAccessContext memberAccess(int i) {
            return (MemberAccessContext) getRuleContext(MemberAccessContext.class, i);
        }

        public ExpressionVarContext(VarContext ctx) {
            copyFrom(ctx);
        }

        public <T> T accept(ParseTreeVisitor<? extends T> parseTreeVisitor) {
            return parseTreeVisitor instanceof LuaParserVisitor ? (T) ((LuaParserVisitor) parseTreeVisitor).visitExpressionVar(this) : (T) parseTreeVisitor.visitChildren(this);
        }
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/LuaParser$MemberVarContext.class */
    public static class MemberVarContext extends VarContext {
        public TerminalNode NAME() {
            return getToken(58, 0);
        }

        public List<MemberAccessContext> memberAccess() {
            return getRuleContexts(MemberAccessContext.class);
        }

        public MemberAccessContext memberAccess(int i) {
            return (MemberAccessContext) getRuleContext(MemberAccessContext.class, i);
        }

        public MemberVarContext(VarContext ctx) {
            copyFrom(ctx);
        }

        public <T> T accept(ParseTreeVisitor<? extends T> parseTreeVisitor) {
            return parseTreeVisitor instanceof LuaParserVisitor ? (T) ((LuaParserVisitor) parseTreeVisitor).visitMemberVar(this) : (T) parseTreeVisitor.visitChildren(this);
        }
    }

    public final VarContext var() throws RecognitionException {
        VarContext _localctx = new VarContext(this._ctx, getState());
        enterRule(_localctx, 12, 6);
        try {
            try {
                setState(185);
                this._errHandler.sync(this);
                switch (getInterpreter().adaptivePredict(this._input, 17, this._ctx)) {
                    case 1:
                        _localctx = new MemberVarContext(_localctx);
                        enterOuterAlt(_localctx, 1);
                        setState(164);
                        match(58);
                        setState(168);
                        this._errHandler.sync(this);
                        int _la = this._input.LA(1);
                        while (true) {
                            if (_la == 27 || _la == 48) {
                                setState(165);
                                memberAccess();
                                setState(170);
                                this._errHandler.sync(this);
                                _la = this._input.LA(1);
                            }
                            break;
                        }
                        break;
                    case 2:
                        _localctx = new FunctionVarContext(_localctx);
                        enterOuterAlt(_localctx, 2);
                        setState(171);
                        functionCall(0);
                        setState(173);
                        this._errHandler.sync(this);
                        this._input.LA(1);
                        while (true) {
                            setState(172);
                            memberAccess();
                            setState(175);
                            this._errHandler.sync(this);
                            int _la2 = this._input.LA(1);
                            if (_la2 != 27 && _la2 != 48) {
                                break;
                            }
                        }
                        break;
                    case 3:
                        _localctx = new ExpressionVarContext(_localctx);
                        enterOuterAlt(_localctx, 3);
                        setState(177);
                        match(31);
                        setState(178);
                        expression(0);
                        setState(179);
                        match(32);
                        setState(181);
                        this._errHandler.sync(this);
                        this._input.LA(1);
                        while (true) {
                            setState(180);
                            memberAccess();
                            setState(183);
                            this._errHandler.sync(this);
                            int _la3 = this._input.LA(1);
                            if (_la3 != 27 && _la3 != 48) {
                                break;
                            }
                        }
                        break;
                }
            } catch (RecognitionException re) {
                _localctx.exception = re;
                this._errHandler.reportError(this, re);
                this._errHandler.recover(this, re);
            }
            return _localctx;
        } finally {
            exitRule();
        }
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/LuaParser$NameListContext.class */
    public static class NameListContext extends ParserRuleContext {
        public List<TerminalNode> NAME() {
            return getTokens(58);
        }

        public TerminalNode NAME(int i) {
            return getToken(58, i);
        }

        public List<TerminalNode> COMMA() {
            return getTokens(15);
        }

        public TerminalNode COMMA(int i) {
            return getToken(15, i);
        }

        public NameListContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        public int getRuleIndex() {
            return 7;
        }

        public <T> T accept(ParseTreeVisitor<? extends T> parseTreeVisitor) {
            return parseTreeVisitor instanceof LuaParserVisitor ? (T) ((LuaParserVisitor) parseTreeVisitor).visitNameList(this) : (T) parseTreeVisitor.visitChildren(this);
        }
    }

    public final NameListContext nameList() throws RecognitionException {
        NameListContext _localctx = new NameListContext(this._ctx, getState());
        enterRule(_localctx, 14, 7);
        try {
            try {
                enterOuterAlt(_localctx, 1);
                setState(187);
                match(58);
                setState(192);
                this._errHandler.sync(this);
                int _alt = getInterpreter().adaptivePredict(this._input, 18, this._ctx);
                while (_alt != 2 && _alt != 0) {
                    if (_alt == 1) {
                        setState(188);
                        match(15);
                        setState(189);
                        match(58);
                    }
                    setState(194);
                    this._errHandler.sync(this);
                    _alt = getInterpreter().adaptivePredict(this._input, 18, this._ctx);
                }
                exitRule();
            } catch (RecognitionException re) {
                _localctx.exception = re;
                this._errHandler.reportError(this, re);
                this._errHandler.recover(this, re);
                exitRule();
            }
            return _localctx;
        } catch (Throwable th) {
            exitRule();
            throw th;
        }
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/LuaParser$ExpressionListContext.class */
    public static class ExpressionListContext extends ParserRuleContext {
        public List<ExpressionContext> expression() {
            return getRuleContexts(ExpressionContext.class);
        }

        public ExpressionContext expression(int i) {
            return (ExpressionContext) getRuleContext(ExpressionContext.class, i);
        }

        public List<TerminalNode> COMMA() {
            return getTokens(15);
        }

        public TerminalNode COMMA(int i) {
            return getToken(15, i);
        }

        public ExpressionListContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        public int getRuleIndex() {
            return 8;
        }

        public <T> T accept(ParseTreeVisitor<? extends T> parseTreeVisitor) {
            return parseTreeVisitor instanceof LuaParserVisitor ? (T) ((LuaParserVisitor) parseTreeVisitor).visitExpressionList(this) : (T) parseTreeVisitor.visitChildren(this);
        }
    }

    public final ExpressionListContext expressionList() throws RecognitionException {
        ExpressionListContext _localctx = new ExpressionListContext(this._ctx, getState());
        enterRule(_localctx, 16, 8);
        try {
            enterOuterAlt(_localctx, 1);
            setState(200);
            this._errHandler.sync(this);
            int _alt = getInterpreter().adaptivePredict(this._input, 19, this._ctx);
            while (_alt != 2 && _alt != 0) {
                if (_alt == 1) {
                    setState(195);
                    expression(0);
                    setState(196);
                    match(15);
                }
                setState(202);
                this._errHandler.sync(this);
                _alt = getInterpreter().adaptivePredict(this._input, 19, this._ctx);
            }
            setState(203);
            expression(0);
        } catch (RecognitionException re) {
            _localctx.exception = re;
            this._errHandler.reportError(this, re);
            this._errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/LuaParser$ExpressionContext.class */
    public static class ExpressionContext extends ParserRuleContext {
        public ExpressionContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        public int getRuleIndex() {
            return 9;
        }

        public ExpressionContext() {
        }

        public void copyFrom(ExpressionContext ctx) {
            super.copyFrom(ctx);
        }
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/LuaParser$ExpressionAccessLiteralContext.class */
    public static class ExpressionAccessLiteralContext extends ExpressionContext {
        public TerminalNode OP() {
            return getToken(31, 0);
        }

        public ExpressionContext expression() {
            return (ExpressionContext) getRuleContext(ExpressionContext.class, 0);
        }

        public TerminalNode CP() {
            return getToken(32, 0);
        }

        public List<MemberAccessContext> memberAccess() {
            return getRuleContexts(MemberAccessContext.class);
        }

        public MemberAccessContext memberAccess(int i) {
            return (MemberAccessContext) getRuleContext(MemberAccessContext.class, i);
        }

        public ExpressionAccessLiteralContext(ExpressionContext ctx) {
            copyFrom(ctx);
        }

        public <T> T accept(ParseTreeVisitor<? extends T> parseTreeVisitor) {
            return parseTreeVisitor instanceof LuaParserVisitor ? (T) ((LuaParserVisitor) parseTreeVisitor).visitExpressionAccessLiteral(this) : (T) parseTreeVisitor.visitChildren(this);
        }
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/LuaParser$VariableLiteralContext.class */
    public static class VariableLiteralContext extends ExpressionContext {
        public TerminalNode NAME() {
            return getToken(58, 0);
        }

        public VariableLiteralContext(ExpressionContext ctx) {
            copyFrom(ctx);
        }

        public <T> T accept(ParseTreeVisitor<? extends T> parseTreeVisitor) {
            return parseTreeVisitor instanceof LuaParserVisitor ? (T) ((LuaParserVisitor) parseTreeVisitor).visitVariableLiteral(this) : (T) parseTreeVisitor.visitChildren(this);
        }
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/LuaParser$ExpressionUnaryContext.class */
    public static class ExpressionUnaryContext extends ExpressionContext {
        public Token operator;

        public ExpressionContext expression() {
            return (ExpressionContext) getRuleContext(ExpressionContext.class, 0);
        }

        public TerminalNode NOT() {
            return getToken(33, 0);
        }

        public TerminalNode POUND() {
            return getToken(30, 0);
        }

        public TerminalNode MINUS() {
            return getToken(29, 0);
        }

        public TerminalNode SQUIG() {
            return getToken(28, 0);
        }

        public ExpressionUnaryContext(ExpressionContext ctx) {
            copyFrom(ctx);
        }

        public <T> T accept(ParseTreeVisitor<? extends T> parseTreeVisitor) {
            return parseTreeVisitor instanceof LuaParserVisitor ? (T) ((LuaParserVisitor) parseTreeVisitor).visitExpressionUnary(this) : (T) parseTreeVisitor.visitChildren(this);
        }
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/LuaParser$ExpressionPowerContext.class */
    public static class ExpressionPowerContext extends ExpressionContext {
        public ExpressionContext left;
        public ExpressionContext right;

        public List<ExpressionContext> expression() {
            return getRuleContexts(ExpressionContext.class);
        }

        public ExpressionContext expression(int i) {
            return (ExpressionContext) getRuleContext(ExpressionContext.class, i);
        }

        public TerminalNode CARET() {
            return getToken(53, 0);
        }

        public ExpressionPowerContext(ExpressionContext ctx) {
            copyFrom(ctx);
        }

        public <T> T accept(ParseTreeVisitor<? extends T> parseTreeVisitor) {
            return parseTreeVisitor instanceof LuaParserVisitor ? (T) ((LuaParserVisitor) parseTreeVisitor).visitExpressionPower(this) : (T) parseTreeVisitor.visitChildren(this);
        }
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/LuaParser$MemberAccessLiteralContext.class */
    public static class MemberAccessLiteralContext extends ExpressionContext {
        public TerminalNode NAME() {
            return getToken(58, 0);
        }

        public List<MemberAccessContext> memberAccess() {
            return getRuleContexts(MemberAccessContext.class);
        }

        public MemberAccessContext memberAccess(int i) {
            return (MemberAccessContext) getRuleContext(MemberAccessContext.class, i);
        }

        public MemberAccessLiteralContext(ExpressionContext ctx) {
            copyFrom(ctx);
        }

        public <T> T accept(ParseTreeVisitor<? extends T> parseTreeVisitor) {
            return parseTreeVisitor instanceof LuaParserVisitor ? (T) ((LuaParserVisitor) parseTreeVisitor).visitMemberAccessLiteral(this) : (T) parseTreeVisitor.visitChildren(this);
        }
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/LuaParser$ExpressionBitwiseContext.class */
    public static class ExpressionBitwiseContext extends ExpressionContext {
        public ExpressionContext left;
        public Token operator;
        public ExpressionContext right;

        public List<ExpressionContext> expression() {
            return getRuleContexts(ExpressionContext.class);
        }

        public ExpressionContext expression(int i) {
            return (ExpressionContext) getRuleContext(ExpressionContext.class, i);
        }

        public TerminalNode AMP() {
            return getToken(36, 0);
        }

        public TerminalNode PIPE() {
            return getToken(52, 0);
        }

        public TerminalNode SQUIG() {
            return getToken(28, 0);
        }

        public TerminalNode LL() {
            return getToken(34, 0);
        }

        public TerminalNode GG() {
            return getToken(35, 0);
        }

        public ExpressionBitwiseContext(ExpressionContext ctx) {
            copyFrom(ctx);
        }

        public <T> T accept(ParseTreeVisitor<? extends T> parseTreeVisitor) {
            return parseTreeVisitor instanceof LuaParserVisitor ? (T) ((LuaParserVisitor) parseTreeVisitor).visitExpressionBitwise(this) : (T) parseTreeVisitor.visitChildren(this);
        }
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/LuaParser$NilLiteralContext.class */
    public static class NilLiteralContext extends ExpressionContext {
        public TerminalNode NIL() {
            return getToken(24, 0);
        }

        public NilLiteralContext(ExpressionContext ctx) {
            copyFrom(ctx);
        }

        public <T> T accept(ParseTreeVisitor<? extends T> parseTreeVisitor) {
            return parseTreeVisitor instanceof LuaParserVisitor ? (T) ((LuaParserVisitor) parseTreeVisitor).visitNilLiteral(this) : (T) parseTreeVisitor.visitChildren(this);
        }
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/LuaParser$TrueLiteralContext.class */
    public static class TrueLiteralContext extends ExpressionContext {
        public TerminalNode TRUE() {
            return getToken(26, 0);
        }

        public TrueLiteralContext(ExpressionContext ctx) {
            copyFrom(ctx);
        }

        public <T> T accept(ParseTreeVisitor<? extends T> parseTreeVisitor) {
            return parseTreeVisitor instanceof LuaParserVisitor ? (T) ((LuaParserVisitor) parseTreeVisitor).visitTrueLiteral(this) : (T) parseTreeVisitor.visitChildren(this);
        }
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/LuaParser$VarargLiteralContext.class */
    public static class VarargLiteralContext extends ExpressionContext {
        public TerminalNode DDD() {
            return getToken(55, 0);
        }

        public VarargLiteralContext(ExpressionContext ctx) {
            copyFrom(ctx);
        }

        public <T> T accept(ParseTreeVisitor<? extends T> parseTreeVisitor) {
            return parseTreeVisitor instanceof LuaParserVisitor ? (T) ((LuaParserVisitor) parseTreeVisitor).visitVarargLiteral(this) : (T) parseTreeVisitor.visitChildren(this);
        }
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/LuaParser$ExpressionAdditiveContext.class */
    public static class ExpressionAdditiveContext extends ExpressionContext {
        public ExpressionContext left;
        public Token operator;
        public ExpressionContext right;

        public List<ExpressionContext> expression() {
            return getRuleContexts(ExpressionContext.class);
        }

        public ExpressionContext expression(int i) {
            return (ExpressionContext) getRuleContext(ExpressionContext.class, i);
        }

        public TerminalNode PLUS() {
            return getToken(44, 0);
        }

        public TerminalNode MINUS() {
            return getToken(29, 0);
        }

        public ExpressionAdditiveContext(ExpressionContext ctx) {
            copyFrom(ctx);
        }

        public <T> T accept(ParseTreeVisitor<? extends T> parseTreeVisitor) {
            return parseTreeVisitor instanceof LuaParserVisitor ? (T) ((LuaParserVisitor) parseTreeVisitor).visitExpressionAdditive(this) : (T) parseTreeVisitor.visitChildren(this);
        }
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/LuaParser$ParenthesizedExpressionLiteralContext.class */
    public static class ParenthesizedExpressionLiteralContext extends ExpressionContext {
        public TerminalNode OP() {
            return getToken(31, 0);
        }

        public ExpressionContext expression() {
            return (ExpressionContext) getRuleContext(ExpressionContext.class, 0);
        }

        public TerminalNode CP() {
            return getToken(32, 0);
        }

        public ParenthesizedExpressionLiteralContext(ExpressionContext ctx) {
            copyFrom(ctx);
        }

        public <T> T accept(ParseTreeVisitor<? extends T> parseTreeVisitor) {
            return parseTreeVisitor instanceof LuaParserVisitor ? (T) ((LuaParserVisitor) parseTreeVisitor).visitParenthesizedExpressionLiteral(this) : (T) parseTreeVisitor.visitChildren(this);
        }
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/LuaParser$ExpressionMultiplicativeContext.class */
    public static class ExpressionMultiplicativeContext extends ExpressionContext {
        public ExpressionContext left;
        public Token operator;
        public ExpressionContext right;

        public List<ExpressionContext> expression() {
            return getRuleContexts(ExpressionContext.class);
        }

        public ExpressionContext expression(int i) {
            return (ExpressionContext) getRuleContext(ExpressionContext.class, i);
        }

        public TerminalNode STAR() {
            return getToken(45, 0);
        }

        public TerminalNode SLASH() {
            return getToken(54, 0);
        }

        public TerminalNode PER() {
            return getToken(38, 0);
        }

        public TerminalNode SS() {
            return getToken(37, 0);
        }

        public ExpressionMultiplicativeContext(ExpressionContext ctx) {
            copyFrom(ctx);
        }

        public <T> T accept(ParseTreeVisitor<? extends T> parseTreeVisitor) {
            return parseTreeVisitor instanceof LuaParserVisitor ? (T) ((LuaParserVisitor) parseTreeVisitor).visitExpressionMultiplicative(this) : (T) parseTreeVisitor.visitChildren(this);
        }
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/LuaParser$ExpressionConcatContext.class */
    public static class ExpressionConcatContext extends ExpressionContext {
        public ExpressionContext left;
        public ExpressionContext right;

        public List<ExpressionContext> expression() {
            return getRuleContexts(ExpressionContext.class);
        }

        public ExpressionContext expression(int i) {
            return (ExpressionContext) getRuleContext(ExpressionContext.class, i);
        }

        public TerminalNode DD() {
            return getToken(51, 0);
        }

        public ExpressionConcatContext(ExpressionContext ctx) {
            copyFrom(ctx);
        }

        public <T> T accept(ParseTreeVisitor<? extends T> parseTreeVisitor) {
            return parseTreeVisitor instanceof LuaParserVisitor ? (T) ((LuaParserVisitor) parseTreeVisitor).visitExpressionConcat(this) : (T) parseTreeVisitor.visitChildren(this);
        }
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/LuaParser$TableLiteralContext.class */
    public static class TableLiteralContext extends ExpressionContext {
        public TableConstructorContext tableConstructor() {
            return (TableConstructorContext) getRuleContext(TableConstructorContext.class, 0);
        }

        public TableLiteralContext(ExpressionContext ctx) {
            copyFrom(ctx);
        }

        public <T> T accept(ParseTreeVisitor<? extends T> parseTreeVisitor) {
            return parseTreeVisitor instanceof LuaParserVisitor ? (T) ((LuaParserVisitor) parseTreeVisitor).visitTableLiteral(this) : (T) parseTreeVisitor.visitChildren(this);
        }
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/LuaParser$FalseLiteralContext.class */
    public static class FalseLiteralContext extends ExpressionContext {
        public TerminalNode FALSE() {
            return getToken(25, 0);
        }

        public FalseLiteralContext(ExpressionContext ctx) {
            copyFrom(ctx);
        }

        public <T> T accept(ParseTreeVisitor<? extends T> parseTreeVisitor) {
            return parseTreeVisitor instanceof LuaParserVisitor ? (T) ((LuaParserVisitor) parseTreeVisitor).visitFalseLiteral(this) : (T) parseTreeVisitor.visitChildren(this);
        }
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/LuaParser$StringLiteralContext.class */
    public static class StringLiteralContext extends ExpressionContext {
        public StringContext string() {
            return (StringContext) getRuleContext(StringContext.class, 0);
        }

        public StringLiteralContext(ExpressionContext ctx) {
            copyFrom(ctx);
        }

        public <T> T accept(ParseTreeVisitor<? extends T> parseTreeVisitor) {
            return parseTreeVisitor instanceof LuaParserVisitor ? (T) ((LuaParserVisitor) parseTreeVisitor).visitStringLiteral(this) : (T) parseTreeVisitor.visitChildren(this);
        }
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/LuaParser$FunctionAccessLiteralContext.class */
    public static class FunctionAccessLiteralContext extends ExpressionContext {
        public FunctionCallContext functionCall() {
            return (FunctionCallContext) getRuleContext(FunctionCallContext.class, 0);
        }

        public List<MemberAccessContext> memberAccess() {
            return getRuleContexts(MemberAccessContext.class);
        }

        public MemberAccessContext memberAccess(int i) {
            return (MemberAccessContext) getRuleContext(MemberAccessContext.class, i);
        }

        public FunctionAccessLiteralContext(ExpressionContext ctx) {
            copyFrom(ctx);
        }

        public <T> T accept(ParseTreeVisitor<? extends T> parseTreeVisitor) {
            return parseTreeVisitor instanceof LuaParserVisitor ? (T) ((LuaParserVisitor) parseTreeVisitor).visitFunctionAccessLiteral(this) : (T) parseTreeVisitor.visitChildren(this);
        }
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/LuaParser$ExpressionComparativeContext.class */
    public static class ExpressionComparativeContext extends ExpressionContext {
        public ExpressionContext left;
        public Token operator;
        public ExpressionContext right;

        public List<ExpressionContext> expression() {
            return getRuleContexts(ExpressionContext.class);
        }

        public ExpressionContext expression(int i) {
            return (ExpressionContext) getRuleContext(ExpressionContext.class, i);
        }

        public TerminalNode LT() {
            return getToken(19, 0);
        }

        public TerminalNode GT() {
            return getToken(20, 0);
        }

        public TerminalNode LE() {
            return getToken(40, 0);
        }

        public TerminalNode GE() {
            return getToken(41, 0);
        }

        public TerminalNode SQEQ() {
            return getToken(56, 0);
        }

        public TerminalNode EE() {
            return getToken(50, 0);
        }

        public TerminalNode NEQ() {
            return getToken(57, 0);
        }

        public ExpressionComparativeContext(ExpressionContext ctx) {
            copyFrom(ctx);
        }

        public <T> T accept(ParseTreeVisitor<? extends T> parseTreeVisitor) {
            return parseTreeVisitor instanceof LuaParserVisitor ? (T) ((LuaParserVisitor) parseTreeVisitor).visitExpressionComparative(this) : (T) parseTreeVisitor.visitChildren(this);
        }
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/LuaParser$ExpressionAndContext.class */
    public static class ExpressionAndContext extends ExpressionContext {
        public ExpressionContext left;
        public Token operator;
        public ExpressionContext right;

        public List<ExpressionContext> expression() {
            return getRuleContexts(ExpressionContext.class);
        }

        public ExpressionContext expression(int i) {
            return (ExpressionContext) getRuleContext(ExpressionContext.class, i);
        }

        public TerminalNode AND() {
            return getToken(42, 0);
        }

        public ExpressionAndContext(ExpressionContext ctx) {
            copyFrom(ctx);
        }

        public <T> T accept(ParseTreeVisitor<? extends T> parseTreeVisitor) {
            return parseTreeVisitor instanceof LuaParserVisitor ? (T) ((LuaParserVisitor) parseTreeVisitor).visitExpressionAnd(this) : (T) parseTreeVisitor.visitChildren(this);
        }
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/LuaParser$FunctionLiteralContext.class */
    public static class FunctionLiteralContext extends ExpressionContext {
        public TerminalNode FUNCTION() {
            return getToken(17, 0);
        }

        public FunctionBodyContext functionBody() {
            return (FunctionBodyContext) getRuleContext(FunctionBodyContext.class, 0);
        }

        public FunctionLiteralContext(ExpressionContext ctx) {
            copyFrom(ctx);
        }

        public <T> T accept(ParseTreeVisitor<? extends T> parseTreeVisitor) {
            return parseTreeVisitor instanceof LuaParserVisitor ? (T) ((LuaParserVisitor) parseTreeVisitor).visitFunctionLiteral(this) : (T) parseTreeVisitor.visitChildren(this);
        }
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/LuaParser$FunctionCallLiteralContext.class */
    public static class FunctionCallLiteralContext extends ExpressionContext {
        public FunctionCallContext functionCall() {
            return (FunctionCallContext) getRuleContext(FunctionCallContext.class, 0);
        }

        public FunctionCallLiteralContext(ExpressionContext ctx) {
            copyFrom(ctx);
        }

        public <T> T accept(ParseTreeVisitor<? extends T> parseTreeVisitor) {
            return parseTreeVisitor instanceof LuaParserVisitor ? (T) ((LuaParserVisitor) parseTreeVisitor).visitFunctionCallLiteral(this) : (T) parseTreeVisitor.visitChildren(this);
        }
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/LuaParser$ExpressionOrContext.class */
    public static class ExpressionOrContext extends ExpressionContext {
        public ExpressionContext left;
        public Token operator;
        public ExpressionContext right;

        public List<ExpressionContext> expression() {
            return getRuleContexts(ExpressionContext.class);
        }

        public ExpressionContext expression(int i) {
            return (ExpressionContext) getRuleContext(ExpressionContext.class, i);
        }

        public TerminalNode OR() {
            return getToken(43, 0);
        }

        public ExpressionOrContext(ExpressionContext ctx) {
            copyFrom(ctx);
        }

        public <T> T accept(ParseTreeVisitor<? extends T> parseTreeVisitor) {
            return parseTreeVisitor instanceof LuaParserVisitor ? (T) ((LuaParserVisitor) parseTreeVisitor).visitExpressionOr(this) : (T) parseTreeVisitor.visitChildren(this);
        }
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/LuaParser$NumberLiteralContext.class */
    public static class NumberLiteralContext extends ExpressionContext {
        public NumberContext number() {
            return (NumberContext) getRuleContext(NumberContext.class, 0);
        }

        public NumberLiteralContext(ExpressionContext ctx) {
            copyFrom(ctx);
        }

        public <T> T accept(ParseTreeVisitor<? extends T> parseTreeVisitor) {
            return parseTreeVisitor instanceof LuaParserVisitor ? (T) ((LuaParserVisitor) parseTreeVisitor).visitNumberLiteral(this) : (T) parseTreeVisitor.visitChildren(this);
        }
    }

    public final ExpressionContext expression() throws RecognitionException {
        return expression(0);
    }

    /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
    /*  JADX ERROR: JadxRuntimeException in pass: RegionMakerVisitor
        jadx.core.utils.exceptions.JadxRuntimeException: Not found exit edge by exit block: B:61:0x0514
        	at jadx.core.dex.visitors.regions.maker.LoopRegionMaker.checkLoopExits(LoopRegionMaker.java:272)
        	at jadx.core.dex.visitors.regions.maker.LoopRegionMaker.makeLoopRegion(LoopRegionMaker.java:237)
        	at jadx.core.dex.visitors.regions.maker.LoopRegionMaker.process(LoopRegionMaker.java:80)
        	at jadx.core.dex.visitors.regions.maker.RegionMaker.traverse(RegionMaker.java:92)
        	at jadx.core.dex.visitors.regions.maker.RegionMaker.makeRegion(RegionMaker.java:69)
        	at jadx.core.dex.visitors.regions.maker.SwitchRegionMaker.addCases(SwitchRegionMaker.java:127)
        	at jadx.core.dex.visitors.regions.maker.SwitchRegionMaker.process(SwitchRegionMaker.java:75)
        	at jadx.core.dex.visitors.regions.maker.RegionMaker.traverse(RegionMaker.java:115)
        	at jadx.core.dex.visitors.regions.maker.RegionMaker.makeRegion(RegionMaker.java:69)
        	at jadx.core.dex.visitors.regions.maker.RegionMaker.makeMthRegion(RegionMaker.java:49)
        	at jadx.core.dex.visitors.regions.RegionMakerVisitor.visit(RegionMakerVisitor.java:25)
        */
    private com.faforever.neroxis.lua.LuaParser.ExpressionContext expression(int r8) throws org.antlr.v4.runtime.RecognitionException {
        /*
            Method dump skipped, instruction units count: 2770
            To view this dump add '--comments-level debug' option
        */
        throw new UnsupportedOperationException("Method not decompiled: com.faforever.neroxis.lua.LuaParser.expression(int):com.faforever.neroxis.lua.LuaParser$ExpressionContext");
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/LuaParser$FunctionCallContext.class */
    public static class FunctionCallContext extends ParserRuleContext {
        public FunctionCallContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        public int getRuleIndex() {
            return 10;
        }

        public FunctionCallContext() {
        }

        public void copyFrom(FunctionCallContext ctx) {
            super.copyFrom(ctx);
        }
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/LuaParser$DirectFunctionCallContext.class */
    public static class DirectFunctionCallContext extends FunctionCallContext {
        public Token receiver;

        public ArgsContext args() {
            return (ArgsContext) getRuleContext(ArgsContext.class, 0);
        }

        public TerminalNode NAME() {
            return getToken(58, 0);
        }

        public List<MemberAccessContext> memberAccess() {
            return getRuleContexts(MemberAccessContext.class);
        }

        public MemberAccessContext memberAccess(int i) {
            return (MemberAccessContext) getRuleContext(MemberAccessContext.class, i);
        }

        public DirectFunctionCallContext(FunctionCallContext ctx) {
            copyFrom(ctx);
        }

        public <T> T accept(ParseTreeVisitor<? extends T> parseTreeVisitor) {
            return parseTreeVisitor instanceof LuaParserVisitor ? (T) ((LuaParserVisitor) parseTreeVisitor).visitDirectFunctionCall(this) : (T) parseTreeVisitor.visitChildren(this);
        }
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/LuaParser$NestedSelfCallContext.class */
    public static class NestedSelfCallContext extends FunctionCallContext {
        public Token methodName;

        public FunctionCallContext functionCall() {
            return (FunctionCallContext) getRuleContext(FunctionCallContext.class, 0);
        }

        public TerminalNode COL() {
            return getToken(39, 0);
        }

        public ArgsContext args() {
            return (ArgsContext) getRuleContext(ArgsContext.class, 0);
        }

        public TerminalNode NAME() {
            return getToken(58, 0);
        }

        public List<MemberAccessContext> memberAccess() {
            return getRuleContexts(MemberAccessContext.class);
        }

        public MemberAccessContext memberAccess(int i) {
            return (MemberAccessContext) getRuleContext(MemberAccessContext.class, i);
        }

        public NestedSelfCallContext(FunctionCallContext ctx) {
            copyFrom(ctx);
        }

        public <T> T accept(ParseTreeVisitor<? extends T> parseTreeVisitor) {
            return parseTreeVisitor instanceof LuaParserVisitor ? (T) ((LuaParserVisitor) parseTreeVisitor).visitNestedSelfCall(this) : (T) parseTreeVisitor.visitChildren(this);
        }
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/LuaParser$ExpressionFunctionCallContext.class */
    public static class ExpressionFunctionCallContext extends FunctionCallContext {
        public TerminalNode OP() {
            return getToken(31, 0);
        }

        public ExpressionContext expression() {
            return (ExpressionContext) getRuleContext(ExpressionContext.class, 0);
        }

        public TerminalNode CP() {
            return getToken(32, 0);
        }

        public ArgsContext args() {
            return (ArgsContext) getRuleContext(ArgsContext.class, 0);
        }

        public List<MemberAccessContext> memberAccess() {
            return getRuleContexts(MemberAccessContext.class);
        }

        public MemberAccessContext memberAccess(int i) {
            return (MemberAccessContext) getRuleContext(MemberAccessContext.class, i);
        }

        public ExpressionFunctionCallContext(FunctionCallContext ctx) {
            copyFrom(ctx);
        }

        public <T> T accept(ParseTreeVisitor<? extends T> parseTreeVisitor) {
            return parseTreeVisitor instanceof LuaParserVisitor ? (T) ((LuaParserVisitor) parseTreeVisitor).visitExpressionFunctionCall(this) : (T) parseTreeVisitor.visitChildren(this);
        }
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/LuaParser$ExpressionSelfCallContext.class */
    public static class ExpressionSelfCallContext extends FunctionCallContext {
        public Token methodName;

        public TerminalNode OP() {
            return getToken(31, 0);
        }

        public ExpressionContext expression() {
            return (ExpressionContext) getRuleContext(ExpressionContext.class, 0);
        }

        public TerminalNode CP() {
            return getToken(32, 0);
        }

        public TerminalNode COL() {
            return getToken(39, 0);
        }

        public ArgsContext args() {
            return (ArgsContext) getRuleContext(ArgsContext.class, 0);
        }

        public TerminalNode NAME() {
            return getToken(58, 0);
        }

        public List<MemberAccessContext> memberAccess() {
            return getRuleContexts(MemberAccessContext.class);
        }

        public MemberAccessContext memberAccess(int i) {
            return (MemberAccessContext) getRuleContext(MemberAccessContext.class, i);
        }

        public ExpressionSelfCallContext(FunctionCallContext ctx) {
            copyFrom(ctx);
        }

        public <T> T accept(ParseTreeVisitor<? extends T> parseTreeVisitor) {
            return parseTreeVisitor instanceof LuaParserVisitor ? (T) ((LuaParserVisitor) parseTreeVisitor).visitExpressionSelfCall(this) : (T) parseTreeVisitor.visitChildren(this);
        }
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/LuaParser$DirectSelfCallContext.class */
    public static class DirectSelfCallContext extends FunctionCallContext {
        public Token receiver;
        public Token methodName;

        public TerminalNode COL() {
            return getToken(39, 0);
        }

        public ArgsContext args() {
            return (ArgsContext) getRuleContext(ArgsContext.class, 0);
        }

        public List<TerminalNode> NAME() {
            return getTokens(58);
        }

        public TerminalNode NAME(int i) {
            return getToken(58, i);
        }

        public List<MemberAccessContext> memberAccess() {
            return getRuleContexts(MemberAccessContext.class);
        }

        public MemberAccessContext memberAccess(int i) {
            return (MemberAccessContext) getRuleContext(MemberAccessContext.class, i);
        }

        public DirectSelfCallContext(FunctionCallContext ctx) {
            copyFrom(ctx);
        }

        public <T> T accept(ParseTreeVisitor<? extends T> parseTreeVisitor) {
            return parseTreeVisitor instanceof LuaParserVisitor ? (T) ((LuaParserVisitor) parseTreeVisitor).visitDirectSelfCall(this) : (T) parseTreeVisitor.visitChildren(this);
        }
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/LuaParser$NestedFunctionCallContext.class */
    public static class NestedFunctionCallContext extends FunctionCallContext {
        public FunctionCallContext functionCall() {
            return (FunctionCallContext) getRuleContext(FunctionCallContext.class, 0);
        }

        public ArgsContext args() {
            return (ArgsContext) getRuleContext(ArgsContext.class, 0);
        }

        public List<MemberAccessContext> memberAccess() {
            return getRuleContexts(MemberAccessContext.class);
        }

        public MemberAccessContext memberAccess(int i) {
            return (MemberAccessContext) getRuleContext(MemberAccessContext.class, i);
        }

        public NestedFunctionCallContext(FunctionCallContext ctx) {
            copyFrom(ctx);
        }

        public <T> T accept(ParseTreeVisitor<? extends T> parseTreeVisitor) {
            return parseTreeVisitor instanceof LuaParserVisitor ? (T) ((LuaParserVisitor) parseTreeVisitor).visitNestedFunctionCall(this) : (T) parseTreeVisitor.visitChildren(this);
        }
    }

    public final FunctionCallContext functionCall() throws RecognitionException {
        return functionCall(0);
    }

    private FunctionCallContext functionCall(int _p) throws RecognitionException {
        ParserRuleContext _parentctx = this._ctx;
        int _parentState = getState();
        FunctionCallContext _localctx = new FunctionCallContext(this._ctx, _parentState);
        enterRecursionRule(_localctx, 20, 10, _p);
        try {
            try {
                enterOuterAlt(_localctx, 1);
                setState(317);
                this._errHandler.sync(this);
                switch (getInterpreter().adaptivePredict(this._input, 30, this._ctx)) {
                    case 1:
                        _localctx = new DirectFunctionCallContext(_localctx);
                        this._ctx = _localctx;
                        setState(275);
                        ((DirectFunctionCallContext) _localctx).receiver = match(58);
                        setState(279);
                        this._errHandler.sync(this);
                        int _la = this._input.LA(1);
                        while (true) {
                            if (_la == 27 || _la == 48) {
                                setState(276);
                                memberAccess();
                                setState(281);
                                this._errHandler.sync(this);
                                _la = this._input.LA(1);
                            } else {
                                setState(282);
                                args();
                            }
                            break;
                        }
                        break;
                    case 2:
                        _localctx = new DirectSelfCallContext(_localctx);
                        this._ctx = _localctx;
                        setState(283);
                        ((DirectSelfCallContext) _localctx).receiver = match(58);
                        setState(287);
                        this._errHandler.sync(this);
                        int _la2 = this._input.LA(1);
                        while (true) {
                            if (_la2 == 27 || _la2 == 48) {
                                setState(284);
                                memberAccess();
                                setState(289);
                                this._errHandler.sync(this);
                                _la2 = this._input.LA(1);
                            } else {
                                setState(290);
                                match(39);
                                setState(291);
                                ((DirectSelfCallContext) _localctx).methodName = match(58);
                                setState(292);
                                args();
                            }
                            break;
                        }
                        break;
                    case 3:
                        _localctx = new ExpressionFunctionCallContext(_localctx);
                        this._ctx = _localctx;
                        setState(293);
                        match(31);
                        setState(294);
                        expression(0);
                        setState(295);
                        match(32);
                        setState(299);
                        this._errHandler.sync(this);
                        int _la3 = this._input.LA(1);
                        while (true) {
                            if (_la3 == 27 || _la3 == 48) {
                                setState(296);
                                memberAccess();
                                setState(301);
                                this._errHandler.sync(this);
                                _la3 = this._input.LA(1);
                            } else {
                                setState(302);
                                args();
                            }
                            break;
                        }
                        break;
                    case 4:
                        _localctx = new ExpressionSelfCallContext(_localctx);
                        this._ctx = _localctx;
                        setState(304);
                        match(31);
                        setState(305);
                        expression(0);
                        setState(306);
                        match(32);
                        setState(310);
                        this._errHandler.sync(this);
                        int _la4 = this._input.LA(1);
                        while (true) {
                            if (_la4 == 27 || _la4 == 48) {
                                setState(307);
                                memberAccess();
                                setState(312);
                                this._errHandler.sync(this);
                                _la4 = this._input.LA(1);
                            } else {
                                setState(313);
                                match(39);
                                setState(314);
                                ((ExpressionSelfCallContext) _localctx).methodName = match(58);
                                setState(315);
                                args();
                            }
                            break;
                        }
                        break;
                }
                this._ctx.stop = this._input.LT(-1);
                setState(339);
                this._errHandler.sync(this);
                int _alt = getInterpreter().adaptivePredict(this._input, 34, this._ctx);
                while (_alt != 2 && _alt != 0) {
                    if (_alt == 1) {
                        if (this._parseListeners != null) {
                            triggerExitRuleEvent();
                        }
                        setState(337);
                        this._errHandler.sync(this);
                        switch (getInterpreter().adaptivePredict(this._input, 33, this._ctx)) {
                            case 1:
                                _localctx = new NestedFunctionCallContext(new FunctionCallContext(_parentctx, _parentState));
                                pushNewRecursionContext(_localctx, 20, 10);
                                setState(319);
                                if (!precpred(this._ctx, 2)) {
                                    throw new FailedPredicateException(this, "precpred(_ctx, 2)");
                                }
                                setState(323);
                                this._errHandler.sync(this);
                                int _la5 = this._input.LA(1);
                                while (true) {
                                    if (_la5 == 27 || _la5 == 48) {
                                        setState(320);
                                        memberAccess();
                                        setState(325);
                                        this._errHandler.sync(this);
                                        _la5 = this._input.LA(1);
                                    } else {
                                        setState(326);
                                        args();
                                    }
                                    break;
                                }
                                break;
                            case 2:
                                _localctx = new NestedSelfCallContext(new FunctionCallContext(_parentctx, _parentState));
                                pushNewRecursionContext(_localctx, 20, 10);
                                setState(327);
                                if (!precpred(this._ctx, 1)) {
                                    throw new FailedPredicateException(this, "precpred(_ctx, 1)");
                                }
                                setState(331);
                                this._errHandler.sync(this);
                                int _la6 = this._input.LA(1);
                                while (true) {
                                    if (_la6 == 27 || _la6 == 48) {
                                        setState(328);
                                        memberAccess();
                                        setState(333);
                                        this._errHandler.sync(this);
                                        _la6 = this._input.LA(1);
                                    } else {
                                        setState(334);
                                        match(39);
                                        setState(335);
                                        ((NestedSelfCallContext) _localctx).methodName = match(58);
                                        setState(336);
                                        args();
                                    }
                                    break;
                                }
                                break;
                        }
                    }
                    setState(341);
                    this._errHandler.sync(this);
                    _alt = getInterpreter().adaptivePredict(this._input, 34, this._ctx);
                }
                unrollRecursionContexts(_parentctx);
            } catch (RecognitionException re) {
                _localctx.exception = re;
                this._errHandler.reportError(this, re);
                this._errHandler.recover(this, re);
                unrollRecursionContexts(_parentctx);
            }
            return _localctx;
        } catch (Throwable th) {
            unrollRecursionContexts(_parentctx);
            throw th;
        }
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/LuaParser$MemberAccessContext.class */
    public static class MemberAccessContext extends ParserRuleContext {
        public MemberAccessContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        public int getRuleIndex() {
            return 11;
        }

        public MemberAccessContext() {
        }

        public void copyFrom(MemberAccessContext ctx) {
            super.copyFrom(ctx);
        }
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/LuaParser$NamedAccessContext.class */
    public static class NamedAccessContext extends MemberAccessContext {
        public TerminalNode DOT() {
            return getToken(27, 0);
        }

        public TerminalNode NAME() {
            return getToken(58, 0);
        }

        public NamedAccessContext(MemberAccessContext ctx) {
            copyFrom(ctx);
        }

        public <T> T accept(ParseTreeVisitor<? extends T> parseTreeVisitor) {
            return parseTreeVisitor instanceof LuaParserVisitor ? (T) ((LuaParserVisitor) parseTreeVisitor).visitNamedAccess(this) : (T) parseTreeVisitor.visitChildren(this);
        }
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/LuaParser$ExpressionAccessContext.class */
    public static class ExpressionAccessContext extends MemberAccessContext {
        public TerminalNode OB() {
            return getToken(48, 0);
        }

        public ExpressionContext expression() {
            return (ExpressionContext) getRuleContext(ExpressionContext.class, 0);
        }

        public TerminalNode CB() {
            return getToken(49, 0);
        }

        public ExpressionAccessContext(MemberAccessContext ctx) {
            copyFrom(ctx);
        }

        public <T> T accept(ParseTreeVisitor<? extends T> parseTreeVisitor) {
            return parseTreeVisitor instanceof LuaParserVisitor ? (T) ((LuaParserVisitor) parseTreeVisitor).visitExpressionAccess(this) : (T) parseTreeVisitor.visitChildren(this);
        }
    }

    public final MemberAccessContext memberAccess() throws RecognitionException {
        MemberAccessContext _localctx = new MemberAccessContext(this._ctx, getState());
        enterRule(_localctx, 22, 11);
        try {
            try {
                setState(348);
                this._errHandler.sync(this);
                switch (this._input.LA(1)) {
                    case 27:
                        _localctx = new NamedAccessContext(_localctx);
                        enterOuterAlt(_localctx, 2);
                        setState(346);
                        match(27);
                        setState(347);
                        match(58);
                        break;
                    case 48:
                        _localctx = new ExpressionAccessContext(_localctx);
                        enterOuterAlt(_localctx, 1);
                        setState(342);
                        match(48);
                        setState(343);
                        expression(0);
                        setState(344);
                        match(49);
                        break;
                    default:
                        throw new NoViableAltException(this);
                }
                exitRule();
            } catch (RecognitionException re) {
                _localctx.exception = re;
                this._errHandler.reportError(this, re);
                this._errHandler.recover(this, re);
                exitRule();
            }
            return _localctx;
        } catch (Throwable th) {
            exitRule();
            throw th;
        }
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/LuaParser$ArgsContext.class */
    public static class ArgsContext extends ParserRuleContext {
        public ArgsContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        public int getRuleIndex() {
            return 12;
        }

        public ArgsContext() {
        }

        public void copyFrom(ArgsContext ctx) {
            super.copyFrom(ctx);
        }
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/LuaParser$StringArgumentContext.class */
    public static class StringArgumentContext extends ArgsContext {
        public StringContext string() {
            return (StringContext) getRuleContext(StringContext.class, 0);
        }

        public StringArgumentContext(ArgsContext ctx) {
            copyFrom(ctx);
        }

        public <T> T accept(ParseTreeVisitor<? extends T> parseTreeVisitor) {
            return parseTreeVisitor instanceof LuaParserVisitor ? (T) ((LuaParserVisitor) parseTreeVisitor).visitStringArgument(this) : (T) parseTreeVisitor.visitChildren(this);
        }
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/LuaParser$TableArgumentContext.class */
    public static class TableArgumentContext extends ArgsContext {
        public TableConstructorContext tableConstructor() {
            return (TableConstructorContext) getRuleContext(TableConstructorContext.class, 0);
        }

        public TableArgumentContext(ArgsContext ctx) {
            copyFrom(ctx);
        }

        public <T> T accept(ParseTreeVisitor<? extends T> parseTreeVisitor) {
            return parseTreeVisitor instanceof LuaParserVisitor ? (T) ((LuaParserVisitor) parseTreeVisitor).visitTableArgument(this) : (T) parseTreeVisitor.visitChildren(this);
        }
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/LuaParser$ExpressionArgumentsContext.class */
    public static class ExpressionArgumentsContext extends ArgsContext {
        public TerminalNode OP() {
            return getToken(31, 0);
        }

        public TerminalNode CP() {
            return getToken(32, 0);
        }

        public ExpressionListContext expressionList() {
            return (ExpressionListContext) getRuleContext(ExpressionListContext.class, 0);
        }

        public ExpressionArgumentsContext(ArgsContext ctx) {
            copyFrom(ctx);
        }

        public <T> T accept(ParseTreeVisitor<? extends T> parseTreeVisitor) {
            return parseTreeVisitor instanceof LuaParserVisitor ? (T) ((LuaParserVisitor) parseTreeVisitor).visitExpressionArguments(this) : (T) parseTreeVisitor.visitChildren(this);
        }
    }

    public final ArgsContext args() throws RecognitionException {
        ArgsContext _localctx = new ArgsContext(this._ctx, getState());
        enterRule(_localctx, 24, 12);
        try {
            try {
                setState(357);
                this._errHandler.sync(this);
                switch (this._input.LA(1)) {
                    case 31:
                        _localctx = new ExpressionArgumentsContext(_localctx);
                        enterOuterAlt(_localctx, 1);
                        setState(350);
                        match(31);
                        setState(352);
                        this._errHandler.sync(this);
                        int _la = this._input.LA(1);
                        if (((_la - 17) & (-64)) == 0 && ((1 << (_la - 17)) & 561026345040769L) != 0) {
                            setState(351);
                            expressionList();
                        }
                        setState(354);
                        match(32);
                        break;
                    case 46:
                        _localctx = new TableArgumentContext(_localctx);
                        enterOuterAlt(_localctx, 2);
                        setState(355);
                        tableConstructor();
                        break;
                    case 59:
                    case 60:
                    case 61:
                        _localctx = new StringArgumentContext(_localctx);
                        enterOuterAlt(_localctx, 3);
                        setState(356);
                        string();
                        break;
                    default:
                        throw new NoViableAltException(this);
                }
                exitRule();
            } catch (RecognitionException re) {
                _localctx.exception = re;
                this._errHandler.reportError(this, re);
                this._errHandler.recover(this, re);
                exitRule();
            }
            return _localctx;
        } catch (Throwable th) {
            exitRule();
            throw th;
        }
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/LuaParser$FunctionBodyContext.class */
    public static class FunctionBodyContext extends ParserRuleContext {
        public TerminalNode OP() {
            return getToken(31, 0);
        }

        public ParameterListContext parameterList() {
            return (ParameterListContext) getRuleContext(ParameterListContext.class, 0);
        }

        public TerminalNode CP() {
            return getToken(32, 0);
        }

        public BlockContext block() {
            return (BlockContext) getRuleContext(BlockContext.class, 0);
        }

        public TerminalNode END() {
            return getToken(6, 0);
        }

        public FunctionBodyContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        public int getRuleIndex() {
            return 13;
        }

        public <T> T accept(ParseTreeVisitor<? extends T> parseTreeVisitor) {
            return parseTreeVisitor instanceof LuaParserVisitor ? (T) ((LuaParserVisitor) parseTreeVisitor).visitFunctionBody(this) : (T) parseTreeVisitor.visitChildren(this);
        }
    }

    public final FunctionBodyContext functionBody() throws RecognitionException {
        FunctionBodyContext _localctx = new FunctionBodyContext(this._ctx, getState());
        enterRule(_localctx, 26, 13);
        try {
            enterOuterAlt(_localctx, 1);
            setState(359);
            match(31);
            setState(360);
            parameterList();
            setState(361);
            match(32);
            setState(362);
            block();
            setState(363);
            match(6);
        } catch (RecognitionException re) {
            _localctx.exception = re;
            this._errHandler.reportError(this, re);
            this._errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/LuaParser$ParameterListContext.class */
    public static class ParameterListContext extends ParserRuleContext {
        public ParameterListContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        public int getRuleIndex() {
            return 14;
        }

        public ParameterListContext() {
        }

        public void copyFrom(ParameterListContext ctx) {
            super.copyFrom(ctx);
        }
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/LuaParser$PopulatedParListContext.class */
    public static class PopulatedParListContext extends ParameterListContext {
        public Token vararg;

        public NameListContext nameList() {
            return (NameListContext) getRuleContext(NameListContext.class, 0);
        }

        public TerminalNode COMMA() {
            return getToken(15, 0);
        }

        public TerminalNode DDD() {
            return getToken(55, 0);
        }

        public PopulatedParListContext(ParameterListContext ctx) {
            copyFrom(ctx);
        }

        public <T> T accept(ParseTreeVisitor<? extends T> parseTreeVisitor) {
            return parseTreeVisitor instanceof LuaParserVisitor ? (T) ((LuaParserVisitor) parseTreeVisitor).visitPopulatedParList(this) : (T) parseTreeVisitor.visitChildren(this);
        }
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/LuaParser$VarargParListContext.class */
    public static class VarargParListContext extends ParameterListContext {
        public TerminalNode DDD() {
            return getToken(55, 0);
        }

        public VarargParListContext(ParameterListContext ctx) {
            copyFrom(ctx);
        }

        public <T> T accept(ParseTreeVisitor<? extends T> parseTreeVisitor) {
            return parseTreeVisitor instanceof LuaParserVisitor ? (T) ((LuaParserVisitor) parseTreeVisitor).visitVarargParList(this) : (T) parseTreeVisitor.visitChildren(this);
        }
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/LuaParser$EmptyParListContext.class */
    public static class EmptyParListContext extends ParameterListContext {
        public EmptyParListContext(ParameterListContext ctx) {
            copyFrom(ctx);
        }

        public <T> T accept(ParseTreeVisitor<? extends T> parseTreeVisitor) {
            return parseTreeVisitor instanceof LuaParserVisitor ? (T) ((LuaParserVisitor) parseTreeVisitor).visitEmptyParList(this) : (T) parseTreeVisitor.visitChildren(this);
        }
    }

    public final ParameterListContext parameterList() throws RecognitionException {
        ParameterListContext _localctx = new ParameterListContext(this._ctx, getState());
        enterRule(_localctx, 28, 14);
        try {
            try {
                setState(372);
                this._errHandler.sync(this);
                switch (this._input.LA(1)) {
                    case 32:
                        _localctx = new EmptyParListContext(_localctx);
                        enterOuterAlt(_localctx, 3);
                        break;
                    case 55:
                        _localctx = new VarargParListContext(_localctx);
                        enterOuterAlt(_localctx, 2);
                        setState(370);
                        match(55);
                        break;
                    case 58:
                        _localctx = new PopulatedParListContext(_localctx);
                        enterOuterAlt(_localctx, 1);
                        setState(365);
                        nameList();
                        setState(368);
                        this._errHandler.sync(this);
                        int _la = this._input.LA(1);
                        if (_la == 15) {
                            setState(366);
                            match(15);
                            setState(367);
                            ((PopulatedParListContext) _localctx).vararg = match(55);
                        }
                        break;
                    default:
                        throw new NoViableAltException(this);
                }
                exitRule();
            } catch (RecognitionException re) {
                _localctx.exception = re;
                this._errHandler.reportError(this, re);
                this._errHandler.recover(this, re);
                exitRule();
            }
            return _localctx;
        } catch (Throwable th) {
            exitRule();
            throw th;
        }
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/LuaParser$TableConstructorContext.class */
    public static class TableConstructorContext extends ParserRuleContext {
        public TerminalNode OCU() {
            return getToken(46, 0);
        }

        public TerminalNode CCU() {
            return getToken(47, 0);
        }

        public FieldListContext fieldList() {
            return (FieldListContext) getRuleContext(FieldListContext.class, 0);
        }

        public TableConstructorContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        public int getRuleIndex() {
            return 15;
        }

        public <T> T accept(ParseTreeVisitor<? extends T> parseTreeVisitor) {
            return parseTreeVisitor instanceof LuaParserVisitor ? (T) ((LuaParserVisitor) parseTreeVisitor).visitTableConstructor(this) : (T) parseTreeVisitor.visitChildren(this);
        }
    }

    public final TableConstructorContext tableConstructor() throws RecognitionException {
        TableConstructorContext _localctx = new TableConstructorContext(this._ctx, getState());
        enterRule(_localctx, 30, 15);
        try {
            enterOuterAlt(_localctx, 1);
            setState(374);
            match(46);
            setState(376);
            this._errHandler.sync(this);
            int _la = this._input.LA(1);
            if (((_la - 17) & (-64)) == 0 && ((1 << (_la - 17)) & 561028492524417L) != 0) {
                setState(375);
                fieldList();
            }
            setState(378);
            match(47);
        } catch (RecognitionException re) {
            _localctx.exception = re;
            this._errHandler.reportError(this, re);
            this._errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/LuaParser$FieldListContext.class */
    public static class FieldListContext extends ParserRuleContext {
        public List<FieldContext> field() {
            return getRuleContexts(FieldContext.class);
        }

        public FieldContext field(int i) {
            return (FieldContext) getRuleContext(FieldContext.class, i);
        }

        public List<TerminalNode> COMMA() {
            return getTokens(15);
        }

        public TerminalNode COMMA(int i) {
            return getToken(15, i);
        }

        public List<TerminalNode> SEMI() {
            return getTokens(1);
        }

        public TerminalNode SEMI(int i) {
            return getToken(1, i);
        }

        public FieldListContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        public int getRuleIndex() {
            return 16;
        }

        public <T> T accept(ParseTreeVisitor<? extends T> parseTreeVisitor) {
            return parseTreeVisitor instanceof LuaParserVisitor ? (T) ((LuaParserVisitor) parseTreeVisitor).visitFieldList(this) : (T) parseTreeVisitor.visitChildren(this);
        }
    }

    public final FieldListContext fieldList() throws RecognitionException {
        FieldListContext _localctx = new FieldListContext(this._ctx, getState());
        enterRule(_localctx, 32, 16);
        try {
            enterOuterAlt(_localctx, 1);
            setState(380);
            field();
            setState(385);
            this._errHandler.sync(this);
            int _alt = getInterpreter().adaptivePredict(this._input, 41, this._ctx);
            while (_alt != 2 && _alt != 0) {
                if (_alt == 1) {
                    setState(381);
                    int _la = this._input.LA(1);
                    if (_la != 1 && _la != 15) {
                        this._errHandler.recoverInline(this);
                    } else {
                        if (this._input.LA(1) == -1) {
                            this.matchedEOF = true;
                        }
                        this._errHandler.reportMatch(this);
                        consume();
                    }
                    setState(382);
                    field();
                }
                setState(387);
                this._errHandler.sync(this);
                _alt = getInterpreter().adaptivePredict(this._input, 41, this._ctx);
            }
            setState(389);
            this._errHandler.sync(this);
            int _la2 = this._input.LA(1);
            if (_la2 == 1 || _la2 == 15) {
                setState(388);
                int _la3 = this._input.LA(1);
                if (_la3 != 1 && _la3 != 15) {
                    this._errHandler.recoverInline(this);
                } else {
                    if (this._input.LA(1) == -1) {
                        this.matchedEOF = true;
                    }
                    this._errHandler.reportMatch(this);
                    consume();
                }
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            this._errHandler.reportError(this, re);
            this._errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/LuaParser$FieldContext.class */
    public static class FieldContext extends ParserRuleContext {
        public FieldContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        public int getRuleIndex() {
            return 17;
        }

        public FieldContext() {
        }

        public void copyFrom(FieldContext ctx) {
            super.copyFrom(ctx);
        }
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/LuaParser$IndexFieldAssingmentContext.class */
    public static class IndexFieldAssingmentContext extends FieldContext {
        public ExpressionContext value;

        public ExpressionContext expression() {
            return (ExpressionContext) getRuleContext(ExpressionContext.class, 0);
        }

        public IndexFieldAssingmentContext(FieldContext ctx) {
            copyFrom(ctx);
        }

        public <T> T accept(ParseTreeVisitor<? extends T> parseTreeVisitor) {
            return parseTreeVisitor instanceof LuaParserVisitor ? (T) ((LuaParserVisitor) parseTreeVisitor).visitIndexFieldAssingment(this) : (T) parseTreeVisitor.visitChildren(this);
        }
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/LuaParser$ExpressionFieldAssignmentContext.class */
    public static class ExpressionFieldAssignmentContext extends FieldContext {
        public ExpressionContext key;
        public ExpressionContext value;

        public TerminalNode OB() {
            return getToken(48, 0);
        }

        public TerminalNode CB() {
            return getToken(49, 0);
        }

        public TerminalNode EQ() {
            return getToken(2, 0);
        }

        public List<ExpressionContext> expression() {
            return getRuleContexts(ExpressionContext.class);
        }

        public ExpressionContext expression(int i) {
            return (ExpressionContext) getRuleContext(ExpressionContext.class, i);
        }

        public ExpressionFieldAssignmentContext(FieldContext ctx) {
            copyFrom(ctx);
        }

        public <T> T accept(ParseTreeVisitor<? extends T> parseTreeVisitor) {
            return parseTreeVisitor instanceof LuaParserVisitor ? (T) ((LuaParserVisitor) parseTreeVisitor).visitExpressionFieldAssignment(this) : (T) parseTreeVisitor.visitChildren(this);
        }
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/LuaParser$DirectFieldAssignmentContext.class */
    public static class DirectFieldAssignmentContext extends FieldContext {
        public ExpressionContext value;

        public TerminalNode NAME() {
            return getToken(58, 0);
        }

        public TerminalNode EQ() {
            return getToken(2, 0);
        }

        public ExpressionContext expression() {
            return (ExpressionContext) getRuleContext(ExpressionContext.class, 0);
        }

        public DirectFieldAssignmentContext(FieldContext ctx) {
            copyFrom(ctx);
        }

        public <T> T accept(ParseTreeVisitor<? extends T> parseTreeVisitor) {
            return parseTreeVisitor instanceof LuaParserVisitor ? (T) ((LuaParserVisitor) parseTreeVisitor).visitDirectFieldAssignment(this) : (T) parseTreeVisitor.visitChildren(this);
        }
    }

    public final FieldContext field() throws RecognitionException {
        FieldContext _localctx = new FieldContext(this._ctx, getState());
        enterRule(_localctx, 34, 17);
        try {
            setState(401);
            this._errHandler.sync(this);
            switch (getInterpreter().adaptivePredict(this._input, 43, this._ctx)) {
                case 1:
                    _localctx = new ExpressionFieldAssignmentContext(_localctx);
                    enterOuterAlt(_localctx, 1);
                    setState(391);
                    match(48);
                    setState(392);
                    ((ExpressionFieldAssignmentContext) _localctx).key = expression(0);
                    setState(393);
                    match(49);
                    setState(394);
                    match(2);
                    setState(395);
                    ((ExpressionFieldAssignmentContext) _localctx).value = expression(0);
                    break;
                case 2:
                    _localctx = new DirectFieldAssignmentContext(_localctx);
                    enterOuterAlt(_localctx, 2);
                    setState(397);
                    match(58);
                    setState(398);
                    match(2);
                    setState(399);
                    ((DirectFieldAssignmentContext) _localctx).value = expression(0);
                    break;
                case 3:
                    _localctx = new IndexFieldAssingmentContext(_localctx);
                    enterOuterAlt(_localctx, 3);
                    setState(400);
                    ((IndexFieldAssingmentContext) _localctx).value = expression(0);
                    break;
            }
        } catch (RecognitionException re) {
            _localctx.exception = re;
            this._errHandler.reportError(this, re);
            this._errHandler.recover(this, re);
        } finally {
            exitRule();
        }
        return _localctx;
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/LuaParser$NumberContext.class */
    public static class NumberContext extends ParserRuleContext {
        public NumberContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        public int getRuleIndex() {
            return 18;
        }

        public NumberContext() {
        }

        public void copyFrom(NumberContext ctx) {
            super.copyFrom(ctx);
        }
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/LuaParser$HexNumberContext.class */
    public static class HexNumberContext extends NumberContext {
        public TerminalNode HEX() {
            return getToken(63, 0);
        }

        public HexNumberContext(NumberContext ctx) {
            copyFrom(ctx);
        }

        public <T> T accept(ParseTreeVisitor<? extends T> parseTreeVisitor) {
            return parseTreeVisitor instanceof LuaParserVisitor ? (T) ((LuaParserVisitor) parseTreeVisitor).visitHexNumber(this) : (T) parseTreeVisitor.visitChildren(this);
        }
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/LuaParser$IntNumberContext.class */
    public static class IntNumberContext extends NumberContext {
        public TerminalNode INT() {
            return getToken(62, 0);
        }

        public IntNumberContext(NumberContext ctx) {
            copyFrom(ctx);
        }

        public <T> T accept(ParseTreeVisitor<? extends T> parseTreeVisitor) {
            return parseTreeVisitor instanceof LuaParserVisitor ? (T) ((LuaParserVisitor) parseTreeVisitor).visitIntNumber(this) : (T) parseTreeVisitor.visitChildren(this);
        }
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/LuaParser$FloatNumberContext.class */
    public static class FloatNumberContext extends NumberContext {
        public TerminalNode FLOAT() {
            return getToken(64, 0);
        }

        public FloatNumberContext(NumberContext ctx) {
            copyFrom(ctx);
        }

        public <T> T accept(ParseTreeVisitor<? extends T> parseTreeVisitor) {
            return parseTreeVisitor instanceof LuaParserVisitor ? (T) ((LuaParserVisitor) parseTreeVisitor).visitFloatNumber(this) : (T) parseTreeVisitor.visitChildren(this);
        }
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/LuaParser$HexFloatNumberContext.class */
    public static class HexFloatNumberContext extends NumberContext {
        public TerminalNode HEX_FLOAT() {
            return getToken(65, 0);
        }

        public HexFloatNumberContext(NumberContext ctx) {
            copyFrom(ctx);
        }

        public <T> T accept(ParseTreeVisitor<? extends T> parseTreeVisitor) {
            return parseTreeVisitor instanceof LuaParserVisitor ? (T) ((LuaParserVisitor) parseTreeVisitor).visitHexFloatNumber(this) : (T) parseTreeVisitor.visitChildren(this);
        }
    }

    public final NumberContext number() throws RecognitionException {
        NumberContext _localctx = new NumberContext(this._ctx, getState());
        enterRule(_localctx, 36, 18);
        try {
            try {
                setState(407);
                this._errHandler.sync(this);
                switch (this._input.LA(1)) {
                    case 62:
                        _localctx = new IntNumberContext(_localctx);
                        enterOuterAlt(_localctx, 1);
                        setState(403);
                        match(62);
                        break;
                    case 63:
                        _localctx = new HexNumberContext(_localctx);
                        enterOuterAlt(_localctx, 2);
                        setState(404);
                        match(63);
                        break;
                    case 64:
                        _localctx = new FloatNumberContext(_localctx);
                        enterOuterAlt(_localctx, 3);
                        setState(405);
                        match(64);
                        break;
                    case 65:
                        _localctx = new HexFloatNumberContext(_localctx);
                        enterOuterAlt(_localctx, 4);
                        setState(406);
                        match(65);
                        break;
                    default:
                        throw new NoViableAltException(this);
                }
                exitRule();
            } catch (RecognitionException re) {
                _localctx.exception = re;
                this._errHandler.reportError(this, re);
                this._errHandler.recover(this, re);
                exitRule();
            }
            return _localctx;
        } catch (Throwable th) {
            exitRule();
            throw th;
        }
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/LuaParser$StringContext.class */
    public static class StringContext extends ParserRuleContext {
        public StringContext(ParserRuleContext parent, int invokingState) {
            super(parent, invokingState);
        }

        public int getRuleIndex() {
            return 19;
        }

        public StringContext() {
        }

        public void copyFrom(StringContext ctx) {
            super.copyFrom(ctx);
        }
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/LuaParser$CharStringContext.class */
    public static class CharStringContext extends StringContext {
        public TerminalNode CHARSTRING() {
            return getToken(60, 0);
        }

        public CharStringContext(StringContext ctx) {
            copyFrom(ctx);
        }

        public <T> T accept(ParseTreeVisitor<? extends T> parseTreeVisitor) {
            return parseTreeVisitor instanceof LuaParserVisitor ? (T) ((LuaParserVisitor) parseTreeVisitor).visitCharString(this) : (T) parseTreeVisitor.visitChildren(this);
        }
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/LuaParser$LongStringContext.class */
    public static class LongStringContext extends StringContext {
        public TerminalNode LONGSTRING() {
            return getToken(61, 0);
        }

        public LongStringContext(StringContext ctx) {
            copyFrom(ctx);
        }

        public <T> T accept(ParseTreeVisitor<? extends T> parseTreeVisitor) {
            return parseTreeVisitor instanceof LuaParserVisitor ? (T) ((LuaParserVisitor) parseTreeVisitor).visitLongString(this) : (T) parseTreeVisitor.visitChildren(this);
        }
    }

    /* JADX INFO: loaded from: app_classes.zip:com/faforever/neroxis/lua/LuaParser$NormalStringContext.class */
    public static class NormalStringContext extends StringContext {
        public TerminalNode NORMALSTRING() {
            return getToken(59, 0);
        }

        public NormalStringContext(StringContext ctx) {
            copyFrom(ctx);
        }

        public <T> T accept(ParseTreeVisitor<? extends T> parseTreeVisitor) {
            return parseTreeVisitor instanceof LuaParserVisitor ? (T) ((LuaParserVisitor) parseTreeVisitor).visitNormalString(this) : (T) parseTreeVisitor.visitChildren(this);
        }
    }

    public final StringContext string() throws RecognitionException {
        StringContext _localctx = new StringContext(this._ctx, getState());
        enterRule(_localctx, 38, 19);
        try {
            try {
                setState(412);
                this._errHandler.sync(this);
                switch (this._input.LA(1)) {
                    case 59:
                        _localctx = new NormalStringContext(_localctx);
                        enterOuterAlt(_localctx, 1);
                        setState(409);
                        match(59);
                        break;
                    case 60:
                        _localctx = new CharStringContext(_localctx);
                        enterOuterAlt(_localctx, 2);
                        setState(410);
                        match(60);
                        break;
                    case 61:
                        _localctx = new LongStringContext(_localctx);
                        enterOuterAlt(_localctx, 3);
                        setState(411);
                        match(61);
                        break;
                    default:
                        throw new NoViableAltException(this);
                }
                exitRule();
            } catch (RecognitionException re) {
                _localctx.exception = re;
                this._errHandler.reportError(this, re);
                this._errHandler.recover(this, re);
                exitRule();
            }
            return _localctx;
        } catch (Throwable th) {
            exitRule();
            throw th;
        }
    }

    public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
        switch (ruleIndex) {
            case 9:
                return expression_sempred((ExpressionContext) _localctx, predIndex);
            case 10:
                return functionCall_sempred((FunctionCallContext) _localctx, predIndex);
            default:
                return true;
        }
    }

    private boolean expression_sempred(ExpressionContext _localctx, int predIndex) {
        switch (predIndex) {
            case 0:
                return precpred(this._ctx, 9);
            case 1:
                return precpred(this._ctx, 7);
            case 2:
                return precpred(this._ctx, 6);
            case 3:
                return precpred(this._ctx, 5);
            case 4:
                return precpred(this._ctx, 4);
            case 5:
                return precpred(this._ctx, 3);
            case 6:
                return precpred(this._ctx, 2);
            case 7:
                return precpred(this._ctx, 1);
            default:
                return true;
        }
    }

    private boolean functionCall_sempred(FunctionCallContext _localctx, int predIndex) {
        switch (predIndex) {
            case 8:
                return precpred(this._ctx, 2);
            case 9:
                return precpred(this._ctx, 1);
            default:
                return true;
        }
    }
}
