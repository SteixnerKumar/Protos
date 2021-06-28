// Generated from SpuddX.g4 by ANTLR 4.5
package thinclab.spuddx_parser;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class SpuddXLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.5", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, T__3=4, T__4=5, T__5=6, T__6=7, T__7=8, T__8=9, 
		DD=10, POMDP=11, DBN=12, UNIFORM=13, OP_ADD=14, OP_SUB=15, OP_MUL=16, 
		OP_DIV=17, IDENTIFIER=18, FLOAT_NUM=19, LP=20, RP=21, WS=22;
	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] ruleNames = {
		"T__0", "T__1", "T__2", "T__3", "T__4", "T__5", "T__6", "T__7", "T__8", 
		"DD", "POMDP", "DBN", "UNIFORM", "OP_ADD", "OP_SUB", "OP_MUL", "OP_DIV", 
		"IDENTIFIER", "FLOAT_NUM", "LP", "RP", "WS"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'variables'", "'S'", "'O'", "'A'", "'dynamics'", "'b'", "'R'", 
		"'discount'", "'SAME'", null, null, null, null, "'+'", "'-'", "'*'", "'/'", 
		null, null, "'('", "')'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, null, null, null, null, null, null, null, null, "DD", "POMDP", 
		"DBN", "UNIFORM", "OP_ADD", "OP_SUB", "OP_MUL", "OP_DIV", "IDENTIFIER", 
		"FLOAT_NUM", "LP", "RP", "WS"
	};
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
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
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}


	public SpuddXLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "SpuddX.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\2\30\u00af\b\1\4\2"+
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
		"\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\3\2\3\2\3\2\3"+
		"\2\3\2\3\2\3\2\3\2\3\2\3\2\3\3\3\3\3\4\3\4\3\5\3\5\3\6\3\6\3\6\3\6\3\6"+
		"\3\6\3\6\3\6\3\6\3\7\3\7\3\b\3\b\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3"+
		"\n\3\n\3\n\3\n\3\n\3\13\3\13\3\13\3\13\5\13_\n\13\3\f\3\f\3\f\3\f\3\f"+
		"\3\f\3\f\3\f\3\f\3\f\5\fk\n\f\3\r\3\r\3\r\3\r\3\r\3\r\5\rs\n\r\3\16\3"+
		"\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\5\16\u0083"+
		"\n\16\3\17\3\17\3\20\3\20\3\21\3\21\3\22\3\22\3\23\5\23\u008e\n\23\3\23"+
		"\3\23\7\23\u0092\n\23\f\23\16\23\u0095\13\23\3\24\7\24\u0098\n\24\f\24"+
		"\16\24\u009b\13\24\3\24\5\24\u009e\n\24\3\24\6\24\u00a1\n\24\r\24\16\24"+
		"\u00a2\3\25\3\25\3\26\3\26\3\27\6\27\u00aa\n\27\r\27\16\27\u00ab\3\27"+
		"\3\27\2\2\30\3\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13\25\f\27\r\31\16"+
		"\33\17\35\20\37\21!\22#\23%\24\'\25)\26+\27-\30\3\2\b\3\2aa\4\2C\\c|\7"+
		"\2))\62;C\\aac|\3\2\62;\4\2))\60\60\5\2\13\f\17\17\"\"\u00b8\2\3\3\2\2"+
		"\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3"+
		"\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2"+
		"\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2"+
		"\2\2\2\'\3\2\2\2\2)\3\2\2\2\2+\3\2\2\2\2-\3\2\2\2\3/\3\2\2\2\59\3\2\2"+
		"\2\7;\3\2\2\2\t=\3\2\2\2\13?\3\2\2\2\rH\3\2\2\2\17J\3\2\2\2\21L\3\2\2"+
		"\2\23U\3\2\2\2\25^\3\2\2\2\27j\3\2\2\2\31r\3\2\2\2\33\u0082\3\2\2\2\35"+
		"\u0084\3\2\2\2\37\u0086\3\2\2\2!\u0088\3\2\2\2#\u008a\3\2\2\2%\u008d\3"+
		"\2\2\2\'\u0099\3\2\2\2)\u00a4\3\2\2\2+\u00a6\3\2\2\2-\u00a9\3\2\2\2/\60"+
		"\7x\2\2\60\61\7c\2\2\61\62\7t\2\2\62\63\7k\2\2\63\64\7c\2\2\64\65\7d\2"+
		"\2\65\66\7n\2\2\66\67\7g\2\2\678\7u\2\28\4\3\2\2\29:\7U\2\2:\6\3\2\2\2"+
		";<\7Q\2\2<\b\3\2\2\2=>\7C\2\2>\n\3\2\2\2?@\7f\2\2@A\7{\2\2AB\7p\2\2BC"+
		"\7c\2\2CD\7o\2\2DE\7k\2\2EF\7e\2\2FG\7u\2\2G\f\3\2\2\2HI\7d\2\2I\16\3"+
		"\2\2\2JK\7T\2\2K\20\3\2\2\2LM\7f\2\2MN\7k\2\2NO\7u\2\2OP\7e\2\2PQ\7q\2"+
		"\2QR\7w\2\2RS\7p\2\2ST\7v\2\2T\22\3\2\2\2UV\7U\2\2VW\7C\2\2WX\7O\2\2X"+
		"Y\7G\2\2Y\24\3\2\2\2Z[\7F\2\2[_\7F\2\2\\]\7f\2\2]_\7f\2\2^Z\3\2\2\2^\\"+
		"\3\2\2\2_\26\3\2\2\2`a\7R\2\2ab\7Q\2\2bc\7O\2\2cd\7F\2\2dk\7R\2\2ef\7"+
		"r\2\2fg\7q\2\2gh\7o\2\2hi\7f\2\2ik\7r\2\2j`\3\2\2\2je\3\2\2\2k\30\3\2"+
		"\2\2lm\7F\2\2mn\7D\2\2ns\7P\2\2op\7f\2\2pq\7d\2\2qs\7p\2\2rl\3\2\2\2r"+
		"o\3\2\2\2s\32\3\2\2\2tu\7w\2\2uv\7p\2\2vw\7k\2\2wx\7h\2\2xy\7q\2\2yz\7"+
		"t\2\2z\u0083\7o\2\2{|\7W\2\2|}\7P\2\2}~\7K\2\2~\177\7H\2\2\177\u0080\7"+
		"Q\2\2\u0080\u0081\7T\2\2\u0081\u0083\7O\2\2\u0082t\3\2\2\2\u0082{\3\2"+
		"\2\2\u0083\34\3\2\2\2\u0084\u0085\7-\2\2\u0085\36\3\2\2\2\u0086\u0087"+
		"\7/\2\2\u0087 \3\2\2\2\u0088\u0089\7,\2\2\u0089\"\3\2\2\2\u008a\u008b"+
		"\7\61\2\2\u008b$\3\2\2\2\u008c\u008e\t\2\2\2\u008d\u008c\3\2\2\2\u008d"+
		"\u008e\3\2\2\2\u008e\u008f\3\2\2\2\u008f\u0093\t\3\2\2\u0090\u0092\t\4"+
		"\2\2\u0091\u0090\3\2\2\2\u0092\u0095\3\2\2\2\u0093\u0091\3\2\2\2\u0093"+
		"\u0094\3\2\2\2\u0094&\3\2\2\2\u0095\u0093\3\2\2\2\u0096\u0098\t\5\2\2"+
		"\u0097\u0096\3\2\2\2\u0098\u009b\3\2\2\2\u0099\u0097\3\2\2\2\u0099\u009a"+
		"\3\2\2\2\u009a\u009d\3\2\2\2\u009b\u0099\3\2\2\2\u009c\u009e\t\6\2\2\u009d"+
		"\u009c\3\2\2\2\u009d\u009e\3\2\2\2\u009e\u00a0\3\2\2\2\u009f\u00a1\t\5"+
		"\2\2\u00a0\u009f\3\2\2\2\u00a1\u00a2\3\2\2\2\u00a2\u00a0\3\2\2\2\u00a2"+
		"\u00a3\3\2\2\2\u00a3(\3\2\2\2\u00a4\u00a5\7*\2\2\u00a5*\3\2\2\2\u00a6"+
		"\u00a7\7+\2\2\u00a7,\3\2\2\2\u00a8\u00aa\t\7\2\2\u00a9\u00a8\3\2\2\2\u00aa"+
		"\u00ab\3\2\2\2\u00ab\u00a9\3\2\2\2\u00ab\u00ac\3\2\2\2\u00ac\u00ad\3\2"+
		"\2\2\u00ad\u00ae\b\27\2\2\u00ae.\3\2\2\2\r\2^jr\u0082\u008d\u0093\u0099"+
		"\u009d\u00a2\u00ab\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}