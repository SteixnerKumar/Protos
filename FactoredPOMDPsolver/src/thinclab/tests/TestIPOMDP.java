/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.tests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import thinclab.domainMaker.L0Frame;
import thinclab.domainMaker.SPUDDHelpers.VariablesContext;
import thinclab.exceptions.ParserException;
import thinclab.exceptions.SolverException;
import thinclab.ipomdpsolver.IPOMDP;
import thinclab.ipomdpsolver.IPOMDPParser;
import thinclab.ipomdpsolver.InteractiveStateVar;
import thinclab.ipomdpsolver.OpponentModel;
import thinclab.symbolicperseus.POMDP;

/*
 * @author adityas
 *
 */
class TestIPOMDP {

	public String l1DomainFile;
	
	@BeforeEach
	void setUp() throws Exception {
		this.l1DomainFile = "/home/adityas/git/repository/FactoredPOMDPsolver/src/tiger.L1.txt";
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void testIPOMDPL1FrameInit() {
		/*
		 * Test POMDP creation from L0Frame
		 */
		System.out.println("Running testIPOMDPL1FrameInit()");
		
		IPOMDPParser parser = new IPOMDPParser(this.l1DomainFile);
		parser.parseDomain();
		
		/*
		 * Initialize IPOMDP
		 */
		IPOMDP tigerL1IPOMDP = new IPOMDP();
		try {
			tigerL1IPOMDP.initializeFromParsers(parser);
		} 
		
		catch (ParserException e) {
			System.err.println(e.getMessage());
			fail();
		}
		
		assertEquals(tigerL1IPOMDP.lowerLevelFrames.size(), parser.childFrames.size());
	}
	
	@Test
	void testIPOMDPL1FrameSolve() {
		/*
		 * Test IPOMDP solve function for L1
		 */
		System.out.println("Running testIPOMDPL1FrameSolve()");
		
		IPOMDPParser parser = new IPOMDPParser(this.l1DomainFile);
		parser.parseDomain();
		
		/*
		 * Initialize IPOMDP
		 */
		IPOMDP tigerL1IPOMDP = new IPOMDP();
		try {
			tigerL1IPOMDP.initializeFromParsers(parser);
		} 
		
		catch (ParserException e) {
			System.err.println(e.getMessage());
			fail();
		}
		
		tigerL1IPOMDP.solveIPBVI(10, 100);
	}
	
	@Test
	void testIPOMDPISCreation() {
		System.out.println("Running testIPOMDPISCreation()");
		
		IPOMDPParser parser = new IPOMDPParser(this.l1DomainFile);
		parser.parseDomain();
		
		/*
		 * Initialize IPOMDP
		 */
		IPOMDP tigerL1IPOMDP = new IPOMDP();
		try {
			
			HashSet<OpponentModel> oppModelSet = new HashSet<OpponentModel>();
			
			tigerL1IPOMDP.initializeFromParsers(parser);
			List<OpponentModel> oppModels = tigerL1IPOMDP.getOpponentModels();
			oppModelSet.addAll(oppModels);
			List<InteractiveStateVar> ISVars = 
					tigerL1IPOMDP.makeInteractiveStateSpace(oppModelSet);
			
			assertEquals(ISVars.size(), (oppModels.size() * tigerL1IPOMDP.nStateVars));
		} 
		
		catch (Exception e) {
			System.err.println(e.getMessage());
			fail();
		}
	}

}
