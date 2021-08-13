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
		T__9=10, T__10=11, T__11=12, T__12=13, T__13=14, T__14=15, T__15=16, T__16=17, 
		T__17=18, T__18=19, T__19=20, T__20=21, ENV=22, DD=23, POMDP=24, IPOMDP=25, 
		DBN=26, UNIFORM=27, OP_ADD=28, OP_SUB=29, OP_MUL=30, OP_DIV=31, IDENTIFIER=32, 
		FLOAT_NUM=33, LP=34, RP=35, WS=36;
	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] ruleNames = {
		"T__0", "T__1", "T__2", "T__3", "T__4", "T__5", "T__6", "T__7", "T__8", 
		"T__9", "T__10", "T__11", "T__12", "T__13", "T__14", "T__15", "T__16", 
		"T__17", "T__18", "T__19", "T__20", "ENV", "DD", "POMDP", "IPOMDP", "DBN", 
		"UNIFORM", "OP_ADD", "OP_SUB", "OP_MUL", "OP_DIV", "IDENTIFIER", "FLOAT_NUM", 
		"LP", "RP", "WS"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'defvar'", "'defmodelvar'", "'defpbvisolv'", "'defpomdp'", "'defipomdp'", 
		"'S'", "'O'", "'A'", "'Aj'", "'Mj'", "'Thetaj'", "'dynamics'", "'b'", 
		"'R'", "'discount'", "'defdd'", "'SAME'", "'defdbn'", "'run'", "'='", 
		"'solve'", null, null, null, null, null, null, "'+'", "'-'", "'*'", "'/'", 
		null, null, "'('", "')'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, null, null, null, null, null, null, null, null, null, null, 
		null, null, null, null, null, null, null, null, null, null, "ENV", "DD", 
		"POMDP", "IPOMDP", "DBN", "UNIFORM", "OP_ADD", "OP_SUB", "OP_MUL", "OP_DIV", 
		"IDENTIFIER", "FLOAT_NUM", "LP", "RP", "WS"
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
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\2&\u012f\b\1\4\2\t"+
		"\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t \4!"+
		"\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\3\2\3\2\3\2\3\2\3\2\3\2\3\2\3\3\3\3\3\3"+
		"\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3\4\3"+
		"\4\3\4\3\4\3\4\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\6\3\6\3\6\3\6\3\6"+
		"\3\6\3\6\3\6\3\6\3\6\3\7\3\7\3\b\3\b\3\t\3\t\3\n\3\n\3\n\3\13\3\13\3\13"+
		"\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3\16"+
		"\3\16\3\17\3\17\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\20\3\21\3\21"+
		"\3\21\3\21\3\21\3\21\3\22\3\22\3\22\3\22\3\22\3\23\3\23\3\23\3\23\3\23"+
		"\3\23\3\23\3\24\3\24\3\24\3\24\3\25\3\25\3\26\3\26\3\26\3\26\3\26\3\26"+
		"\3\27\3\27\3\27\3\27\3\27\3\27\5\27\u00cb\n\27\3\30\3\30\3\30\3\30\5\30"+
		"\u00d1\n\30\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\3\31\5\31\u00dd"+
		"\n\31\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\3\32\5\32"+
		"\u00eb\n\32\3\33\3\33\3\33\3\33\3\33\3\33\5\33\u00f3\n\33\3\34\3\34\3"+
		"\34\3\34\3\34\3\34\3\34\3\34\3\34\3\34\3\34\3\34\3\34\3\34\5\34\u0103"+
		"\n\34\3\35\3\35\3\36\3\36\3\37\3\37\3 \3 \3!\5!\u010e\n!\3!\3!\7!\u0112"+
		"\n!\f!\16!\u0115\13!\3\"\7\"\u0118\n\"\f\"\16\"\u011b\13\"\3\"\5\"\u011e"+
		"\n\"\3\"\6\"\u0121\n\"\r\"\16\"\u0122\3#\3#\3$\3$\3%\6%\u012a\n%\r%\16"+
		"%\u012b\3%\3%\2\2&\3\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13\25\f\27\r"+
		"\31\16\33\17\35\20\37\21!\22#\23%\24\'\25)\26+\27-\30/\31\61\32\63\33"+
		"\65\34\67\359\36;\37= ?!A\"C#E$G%I&\3\2\b\3\2aa\4\2C\\c|\7\2))\62;C\\"+
		"aac|\3\2\62;\4\2))\60\60\5\2\13\f\17\17\"\"\u013a\2\3\3\2\2\2\2\5\3\2"+
		"\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2\2\21"+
		"\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3\2\2\2\2\33\3\2"+
		"\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2%\3\2\2\2\2\'\3"+
		"\2\2\2\2)\3\2\2\2\2+\3\2\2\2\2-\3\2\2\2\2/\3\2\2\2\2\61\3\2\2\2\2\63\3"+
		"\2\2\2\2\65\3\2\2\2\2\67\3\2\2\2\29\3\2\2\2\2;\3\2\2\2\2=\3\2\2\2\2?\3"+
		"\2\2\2\2A\3\2\2\2\2C\3\2\2\2\2E\3\2\2\2\2G\3\2\2\2\2I\3\2\2\2\3K\3\2\2"+
		"\2\5R\3\2\2\2\7^\3\2\2\2\tj\3\2\2\2\13s\3\2\2\2\r}\3\2\2\2\17\177\3\2"+
		"\2\2\21\u0081\3\2\2\2\23\u0083\3\2\2\2\25\u0086\3\2\2\2\27\u0089\3\2\2"+
		"\2\31\u0090\3\2\2\2\33\u0099\3\2\2\2\35\u009b\3\2\2\2\37\u009d\3\2\2\2"+
		"!\u00a6\3\2\2\2#\u00ac\3\2\2\2%\u00b1\3\2\2\2\'\u00b8\3\2\2\2)\u00bc\3"+
		"\2\2\2+\u00be\3\2\2\2-\u00ca\3\2\2\2/\u00d0\3\2\2\2\61\u00dc\3\2\2\2\63"+
		"\u00ea\3\2\2\2\65\u00f2\3\2\2\2\67\u0102\3\2\2\29\u0104\3\2\2\2;\u0106"+
		"\3\2\2\2=\u0108\3\2\2\2?\u010a\3\2\2\2A\u010d\3\2\2\2C\u0119\3\2\2\2E"+
		"\u0124\3\2\2\2G\u0126\3\2\2\2I\u0129\3\2\2\2KL\7f\2\2LM\7g\2\2MN\7h\2"+
		"\2NO\7x\2\2OP\7c\2\2PQ\7t\2\2Q\4\3\2\2\2RS\7f\2\2ST\7g\2\2TU\7h\2\2UV"+
		"\7o\2\2VW\7q\2\2WX\7f\2\2XY\7g\2\2YZ\7n\2\2Z[\7x\2\2[\\\7c\2\2\\]\7t\2"+
		"\2]\6\3\2\2\2^_\7f\2\2_`\7g\2\2`a\7h\2\2ab\7r\2\2bc\7d\2\2cd\7x\2\2de"+
		"\7k\2\2ef\7u\2\2fg\7q\2\2gh\7n\2\2hi\7x\2\2i\b\3\2\2\2jk\7f\2\2kl\7g\2"+
		"\2lm\7h\2\2mn\7r\2\2no\7q\2\2op\7o\2\2pq\7f\2\2qr\7r\2\2r\n\3\2\2\2st"+
		"\7f\2\2tu\7g\2\2uv\7h\2\2vw\7k\2\2wx\7r\2\2xy\7q\2\2yz\7o\2\2z{\7f\2\2"+
		"{|\7r\2\2|\f\3\2\2\2}~\7U\2\2~\16\3\2\2\2\177\u0080\7Q\2\2\u0080\20\3"+
		"\2\2\2\u0081\u0082\7C\2\2\u0082\22\3\2\2\2\u0083\u0084\7C\2\2\u0084\u0085"+
		"\7l\2\2\u0085\24\3\2\2\2\u0086\u0087\7O\2\2\u0087\u0088\7l\2\2\u0088\26"+
		"\3\2\2\2\u0089\u008a\7V\2\2\u008a\u008b\7j\2\2\u008b\u008c\7g\2\2\u008c"+
		"\u008d\7v\2\2\u008d\u008e\7c\2\2\u008e\u008f\7l\2\2\u008f\30\3\2\2\2\u0090"+
		"\u0091\7f\2\2\u0091\u0092\7{\2\2\u0092\u0093\7p\2\2\u0093\u0094\7c\2\2"+
		"\u0094\u0095\7o\2\2\u0095\u0096\7k\2\2\u0096\u0097\7e\2\2\u0097\u0098"+
		"\7u\2\2\u0098\32\3\2\2\2\u0099\u009a\7d\2\2\u009a\34\3\2\2\2\u009b\u009c"+
		"\7T\2\2\u009c\36\3\2\2\2\u009d\u009e\7f\2\2\u009e\u009f\7k\2\2\u009f\u00a0"+
		"\7u\2\2\u00a0\u00a1\7e\2\2\u00a1\u00a2\7q\2\2\u00a2\u00a3\7w\2\2\u00a3"+
		"\u00a4\7p\2\2\u00a4\u00a5\7v\2\2\u00a5 \3\2\2\2\u00a6\u00a7\7f\2\2\u00a7"+
		"\u00a8\7g\2\2\u00a8\u00a9\7h\2\2\u00a9\u00aa\7f\2\2\u00aa\u00ab\7f\2\2"+
		"\u00ab\"\3\2\2\2\u00ac\u00ad\7U\2\2\u00ad\u00ae\7C\2\2\u00ae\u00af\7O"+
		"\2\2\u00af\u00b0\7G\2\2\u00b0$\3\2\2\2\u00b1\u00b2\7f\2\2\u00b2\u00b3"+
		"\7g\2\2\u00b3\u00b4\7h\2\2\u00b4\u00b5\7f\2\2\u00b5\u00b6\7d\2\2\u00b6"+
		"\u00b7\7p\2\2\u00b7&\3\2\2\2\u00b8\u00b9\7t\2\2\u00b9\u00ba\7w\2\2\u00ba"+
		"\u00bb\7p\2\2\u00bb(\3\2\2\2\u00bc\u00bd\7?\2\2\u00bd*\3\2\2\2\u00be\u00bf"+
		"\7u\2\2\u00bf\u00c0\7q\2\2\u00c0\u00c1\7n\2\2\u00c1\u00c2\7x\2\2\u00c2"+
		"\u00c3\7g\2\2\u00c3,\3\2\2\2\u00c4\u00c5\7G\2\2\u00c5\u00c6\7P\2\2\u00c6"+
		"\u00cb\7X\2\2\u00c7\u00c8\7g\2\2\u00c8\u00c9\7p\2\2\u00c9\u00cb\7x\2\2"+
		"\u00ca\u00c4\3\2\2\2\u00ca\u00c7\3\2\2\2\u00cb.\3\2\2\2\u00cc\u00cd\7"+
		"F\2\2\u00cd\u00d1\7F\2\2\u00ce\u00cf\7f\2\2\u00cf\u00d1\7f\2\2\u00d0\u00cc"+
		"\3\2\2\2\u00d0\u00ce\3\2\2\2\u00d1\60\3\2\2\2\u00d2\u00d3\7R\2\2\u00d3"+
		"\u00d4\7Q\2\2\u00d4\u00d5\7O\2\2\u00d5\u00d6\7F\2\2\u00d6\u00dd\7R\2\2"+
		"\u00d7\u00d8\7r\2\2\u00d8\u00d9\7q\2\2\u00d9\u00da\7o\2\2\u00da\u00db"+
		"\7f\2\2\u00db\u00dd\7r\2\2\u00dc\u00d2\3\2\2\2\u00dc\u00d7\3\2\2\2\u00dd"+
		"\62\3\2\2\2\u00de\u00df\7K\2\2\u00df\u00e0\7R\2\2\u00e0\u00e1\7Q\2\2\u00e1"+
		"\u00e2\7O\2\2\u00e2\u00e3\7F\2\2\u00e3\u00eb\7R\2\2\u00e4\u00e5\7k\2\2"+
		"\u00e5\u00e6\7r\2\2\u00e6\u00e7\7q\2\2\u00e7\u00e8\7o\2\2\u00e8\u00e9"+
		"\7f\2\2\u00e9\u00eb\7r\2\2\u00ea\u00de\3\2\2\2\u00ea\u00e4\3\2\2\2\u00eb"+
		"\64\3\2\2\2\u00ec\u00ed\7F\2\2\u00ed\u00ee\7D\2\2\u00ee\u00f3\7P\2\2\u00ef"+
		"\u00f0\7f\2\2\u00f0\u00f1\7d\2\2\u00f1\u00f3\7p\2\2\u00f2\u00ec\3\2\2"+
		"\2\u00f2\u00ef\3\2\2\2\u00f3\66\3\2\2\2\u00f4\u00f5\7w\2\2\u00f5\u00f6"+
		"\7p\2\2\u00f6\u00f7\7k\2\2\u00f7\u00f8\7h\2\2\u00f8\u00f9\7q\2\2\u00f9"+
		"\u00fa\7t\2\2\u00fa\u0103\7o\2\2\u00fb\u00fc\7W\2\2\u00fc\u00fd\7P\2\2"+
		"\u00fd\u00fe\7K\2\2\u00fe\u00ff\7H\2\2\u00ff\u0100\7Q\2\2\u0100\u0101"+
		"\7T\2\2\u0101\u0103\7O\2\2\u0102\u00f4\3\2\2\2\u0102\u00fb\3\2\2\2\u0103"+
		"8\3\2\2\2\u0104\u0105\7-\2\2\u0105:\3\2\2\2\u0106\u0107\7/\2\2\u0107<"+
		"\3\2\2\2\u0108\u0109\7,\2\2\u0109>\3\2\2\2\u010a\u010b\7\61\2\2\u010b"+
		"@\3\2\2\2\u010c\u010e\t\2\2\2\u010d\u010c\3\2\2\2\u010d\u010e\3\2\2\2"+
		"\u010e\u010f\3\2\2\2\u010f\u0113\t\3\2\2\u0110\u0112\t\4\2\2\u0111\u0110"+
		"\3\2\2\2\u0112\u0115\3\2\2\2\u0113\u0111\3\2\2\2\u0113\u0114\3\2\2\2\u0114"+
		"B\3\2\2\2\u0115\u0113\3\2\2\2\u0116\u0118\t\5\2\2\u0117\u0116\3\2\2\2"+
		"\u0118\u011b\3\2\2\2\u0119\u0117\3\2\2\2\u0119\u011a\3\2\2\2\u011a\u011d"+
		"\3\2\2\2\u011b\u0119\3\2\2\2\u011c\u011e\t\6\2\2\u011d\u011c\3\2\2\2\u011d"+
		"\u011e\3\2\2\2\u011e\u0120\3\2\2\2\u011f\u0121\t\5\2\2\u0120\u011f\3\2"+
		"\2\2\u0121\u0122\3\2\2\2\u0122\u0120\3\2\2\2\u0122\u0123\3\2\2\2\u0123"+
		"D\3\2\2\2\u0124\u0125\7*\2\2\u0125F\3\2\2\2\u0126\u0127\7+\2\2\u0127H"+
		"\3\2\2\2\u0128\u012a\t\7\2\2\u0129\u0128\3\2\2\2\u012a\u012b\3\2\2\2\u012b"+
		"\u0129\3\2\2\2\u012b\u012c\3\2\2\2\u012c\u012d\3\2\2\2\u012d\u012e\b%"+
		"\2\2\u012eJ\3\2\2\2\17\2\u00ca\u00d0\u00dc\u00ea\u00f2\u0102\u010d\u0113"+
		"\u0119\u011d\u0122\u012b\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}