/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.spuddx_parser;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.TokenStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import thinclab.RandomVariable;
import thinclab.legacy.DD;
import thinclab.models.DBN;
import thinclab.models.Model;
import thinclab.models.POMDP;

/*
 * @author adityas
 *
 */
public class SpuddXParserWrapper {

	/*
	 * Wrapper for SPUDDX wrapper generated by ANTLR4. Implements the visitor
	 * methods for each statement.
	 */

	private String fileName;
	private SpuddXParser parser;

	private static final Logger LOGGER = LogManager.getLogger(SpuddXParserWrapper.class);

	// -------------------------------------------------------------------------

	public SpuddXParserWrapper(String fileName) {

		this.fileName = fileName;

		try {

			// Get tokens from lexer
			InputStream is = new FileInputStream(this.fileName);
			ANTLRInputStream antlrIs = new ANTLRInputStream(is);
			SpuddXLexer lexer = new SpuddXLexer(antlrIs);
			TokenStream tokens = new CommonTokenStream(lexer);

			this.parser = new SpuddXParser(tokens);

		}

		catch (Exception e) {

			LOGGER.error(String.format("Error while trying to parse %s: %s", this.fileName, e));
			System.exit(-1);
		}
	}

	public List<RandomVariable> getVariableDeclarations() {

		this.parser.reset();
		return new VariablesDeclarationVisitor().visit(this.parser.domain());
	}

	public HashMap<String, DD> getDDs() {

		this.parser.reset();
		return new DDParser(new HashMap<String, DD>()).getDDs(this.parser.domain());
	}

	public HashMap<String, Model> getModels(DDParser ddParser) {

		this.parser.reset();
		return new ModelsParser(ddParser).getModels(this.parser.domain());
	}

	public HashMap<String, Model> getModels() {

		this.parser.reset();
		var ddParser = new DDParser(new HashMap<String, DD>(10));
		var declDDs = ddParser.getDDs(this.parser.domain());
		
		LOGGER.debug(String.format("Parsed DDs, %s", declDDs));
		
		this.parser.reset();
		return new ModelsParser(ddParser).getModels(this.parser.domain());
	}

	public static HashMap<String, DBN> getDBNs(HashMap<String, Model> declModels) {

		// for all DBNs, convert <String, Model> to <String, DBN>
		var dbns = declModels.entrySet().stream().filter(e -> e.getValue() instanceof DBN)
				.collect(Collectors.toMap(e -> e.getKey(), e -> (DBN) e.getValue()));

		return (HashMap<String, DBN>) dbns;
	}

	public static HashMap<String, POMDP> getPOMDPs(HashMap<String, Model> declModels) {

		// for all POMDPs, convert <String, Model> to <String, POMDP>
		var pomdps = declModels.entrySet().stream().filter(e -> e.getValue() instanceof POMDP)
				.collect(Collectors.toMap(e -> e.getKey(), e -> (POMDP) e.getValue()));

		return (HashMap<String, POMDP>) pomdps;
	}

}
