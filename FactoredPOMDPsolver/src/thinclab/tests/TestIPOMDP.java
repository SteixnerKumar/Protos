/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.tests;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.instrument.Instrumentation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import thinclab.domainMaker.L0Frame;
import thinclab.domainMaker.SPUDDHelpers.VariablesContext;
import thinclab.domainMaker.ddHelpers.DDTree;
import thinclab.exceptions.ParserException;
import thinclab.exceptions.SolverException;
import thinclab.ipomdpsolver.IPOMDP;
import thinclab.ipomdpsolver.IPOMDPParser;
import thinclab.ipomdpsolver.InteractiveStateVar;
import thinclab.ipomdpsolver.OpponentModel;
import thinclab.ipomdpsolver.InteractiveBelief.InteractiveBelief;
import thinclab.symbolicperseus.DD;
import thinclab.symbolicperseus.Global;
import thinclab.symbolicperseus.OP;
import thinclab.symbolicperseus.POMDP;
import thinclab.symbolicperseus.Belief.Belief;

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
	void testIPOMDPOiDDCreation() {
		/*
		 * Test IPOMDP solve function for L1
		 */
		System.out.println("Running testIPOMDPOjDDCreation()");
		
		IPOMDPParser parser = new IPOMDPParser(this.l1DomainFile);
		parser.parseDomain();
		
		/*
		 * Initialize IPOMDP
		 */
		IPOMDP tigerL1IPOMDP = new IPOMDP(parser, 15, 3);
		try {
			tigerL1IPOMDP.solveOpponentModels();
			
			/* 
			 * Stage and commit additional state and variables to populate global 
			 * arrays
			 */
			tigerL1IPOMDP.initializeIS();
			
			for (String Ai : tigerL1IPOMDP.currentOi.keySet()) {
				for (int s = 0; s < tigerL1IPOMDP.Omega.size() - 1; s++) {
					System.out.println("For Ai " + Ai + " and o " + tigerL1IPOMDP.Omega.get(s));
					assertTrue(
							OP.maxAll(
									OP.abs(
										OP.sub(
											DD.one, 
											OP.addMultVarElim(
												tigerL1IPOMDP.currentOi.get(Ai)[s],
												IPOMDP.getVarIndex(
														tigerL1IPOMDP.Omega.get(s).name + "'"))))) < 1e-8);
				}
			}
		}
		
		catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
			fail();
		}
		
	}
	
	@Test
	void testIPOMDPTiDDCreation() {
		/*
		 * Test IPOMDP solve function for L1
		 */
		System.out.println("Running testIPOMDPTiDDCreation()");
		
		long then = System.nanoTime();
		IPOMDPParser parser = new IPOMDPParser(this.l1DomainFile);
		parser.parseDomain();
		
		/*
		 * Initialize IPOMDP
		 */
		IPOMDP tigerL1IPOMDP = new IPOMDP(parser, 10, 3);
		try {
			
			tigerL1IPOMDP.solveOpponentModels();
			
			/* 
			 * Stage and commit additional state and variables to populate global 
			 * arrays
			 */
			tigerL1IPOMDP.initializeIS();
			
			/*
			 * These will need to be constructed at every belief update
			 */
			
			/* Make M_j transition DD */
//			tigerL1IPOMDP.makeOpponentModelTransitionDD();
			
//			HashMap<String, HashMap<String, DDTree>> Ti = tigerL1IPOMDP.makeTi();
			long now = System.nanoTime();

			System.out.println("Exec time: " + (now - then)/10000000 + " millisec.");
			System.out.println("State Sequence " + tigerL1IPOMDP.S);
			System.out.println("Ti " + tigerL1IPOMDP.currentTi);
			for (String Ai : tigerL1IPOMDP.currentTi.keySet()) {
				for (int s = 0; s < tigerL1IPOMDP.S.size() - 1; s++) {
					System.out.println("For Ai " + Ai + " and s " + tigerL1IPOMDP.S.get(s));
					assertTrue(
							OP.maxAll(
									OP.abs(
										OP.sub(
											DD.one, 
											OP.addMultVarElim(
												tigerL1IPOMDP.currentTi.get(Ai)[s],
												IPOMDP.getVarIndex(
														tigerL1IPOMDP.S.get(s).name + "'"))))) < 1e-8);
				}
			}
		}
		
		catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
			fail();
		}
		
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
			tigerL1IPOMDP.initializeFromParsers(parser);
			tigerL1IPOMDP.setMjDepth(10);
			tigerL1IPOMDP.setMjLookAhead(2);
			tigerL1IPOMDP.solveOpponentModels();
			
			tigerL1IPOMDP.initializeIS();
			
			assertEquals(
					tigerL1IPOMDP.S.size(), 
					tigerL1IPOMDP.lowerLevelFrames.get(0).S.size() + 1);
		} 
		
		catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	void testIPOMDPObsVarCreation() throws SolverException {
		System.out.println("Running testIPOMDPObsVarCreation()");
		
		IPOMDPParser parser = new IPOMDPParser(this.l1DomainFile);
		parser.parseDomain();
		
		/*
		 * Initialize IPOMDP
		 */
		IPOMDP tigerL1IPOMDP = new IPOMDP(parser, 10, 2);
		tigerL1IPOMDP.solveOpponentModels();
		
		assertEquals(tigerL1IPOMDP.Omega.size(),
					2 + 
					(tigerL1IPOMDP.lowerLevelFrames.size() * 
					tigerL1IPOMDP.lowerLevelFrames.get(0).nObsVars));
		
	}
	
	@Test
	void testIPOMDPOjExtraction() {
		System.out.println("Running testIPOMDPOjExtraction()");
		
		IPOMDPParser parser = new IPOMDPParser(this.l1DomainFile);
		parser.parseDomain();
		
		/*
		 * Initialize IPOMDP
		 */
		IPOMDP tigerL1IPOMDP = new IPOMDP(parser, 15, 3);
		try {
			tigerL1IPOMDP.solveOpponentModels();
			tigerL1IPOMDP.initializeIS();
			
			for (int s = 0; s < tigerL1IPOMDP.OmegaJNames.size() - 1; s++) {
				System.out.println("For and OmegaJ " + tigerL1IPOMDP.OmegaJNames.get(s));
				assertTrue(
						OP.maxAll(
								OP.abs(
									OP.sub(
										DD.one, 
										OP.addMultVarElim(
											tigerL1IPOMDP.currentOj[s],
											IPOMDP.getVarIndex(
													tigerL1IPOMDP.OmegaJNames.get(s) + "'"))))) < 1e-8);
			}
		} 
		
		catch (Exception e) {
			System.err.println(e.getMessage());
//			e.printStackTrace();
			fail();
		}
		
		System.out.println();
	}
	
	@Test
	void testIPOMDPMjDDCreation() {
		System.out.println("Running testIPOMDPMjDDCreation()");
		
		IPOMDPParser parser = new IPOMDPParser(this.l1DomainFile);
		parser.parseDomain();
		
		/*
		 * Initialize IPOMDP
		 */
		IPOMDP tigerL1IPOMDP = new IPOMDP(parser, 15, 3);
		try {
			tigerL1IPOMDP.solveOpponentModels();
			tigerL1IPOMDP.initializeIS();

			assertTrue(
					OP.maxAll(
							OP.abs(
								OP.sub(
									DD.one, 
									OP.addMultVarElim(
										tigerL1IPOMDP.currentMjTfn,
										IPOMDP.getVarIndex("M_j'"))))) < 1e-8);
		} 
		
		catch (Exception e) {
			System.err.println(e.getMessage());
			fail();
		}
		
	}
	
	@Test
	void testIPOMDPISInitBelief() {
		System.out.println("Running testIPOMDPISInitBelief()");
		
		IPOMDPParser parser = new IPOMDPParser(this.l1DomainFile);
		parser.parseDomain();
		
		/*
		 * Initialize IPOMDP
		 */
		IPOMDP tigerL1IPOMDP = new IPOMDP(parser, 15, 3);
		try {
			tigerL1IPOMDP.solveOpponentModels();
			tigerL1IPOMDP.initializeIS();

//			System.out.println(tigerL1IPOMDP.lookAheadRootInitBelief);
//			System.out.println(
//					OP.addMultVarElim(tigerL1IPOMDP.lookAheadRootInitBelief, new int[] {2}));
		} 
		
		catch (Exception e) {
			System.err.println(e.getMessage());
			fail();
		}
		
	}
	
	@Test
	void testIPOMDPInitRForLATree() {
		System.out.println("Running testIPOMDPInitRforLATree()");
		
		IPOMDPParser parser = new IPOMDPParser(this.l1DomainFile);
		parser.parseDomain();
		
		/*
		 * Initialize IPOMDP
		 */
		IPOMDP tigerL1IPOMDP = new IPOMDP(parser, 15, 3);
		try {
			tigerL1IPOMDP.solveOpponentModels();
			tigerL1IPOMDP.initializeIS();
			
			System.out.println(tigerL1IPOMDP.currentRi);
		} 
		
		catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
			fail();
		}
		
	}
	
	@Test
	void testIPOMDPstepping() {
		System.out.println("Running testIPOMDPstepping()");
		
		IPOMDPParser parser = new IPOMDPParser(this.l1DomainFile);
		parser.parseDomain();
		
		/*
		 * Initialize IPOMDP
		 */
		IPOMDP tigerL1IPOMDP = new IPOMDP(parser, 15, 3);
		try {
			tigerL1IPOMDP.solveOpponentModels();
			tigerL1IPOMDP.initializeIS();
			
			HashSet<String> beliefNodes = tigerL1IPOMDP.oppModel.currentRoots;
			System.out.println(beliefNodes);
			
			System.out.println(
					InteractiveBelief.toStateMap(
							tigerL1IPOMDP, 
							tigerL1IPOMDP.lookAheadRootInitBeliefs.get(0)));
			
			tigerL1IPOMDP.step(
					tigerL1IPOMDP.lookAheadRootInitBeliefs.get(0), 
					"listen", 
					new String[] {"growl-right", "silence"});
			
			tigerL1IPOMDP.step(
					tigerL1IPOMDP.lookAheadRootInitBeliefs.get(0), 
					"listen", 
					new String[] {"growl-right", "silence"});
			
			HashSet<String> beliefNodesNow = tigerL1IPOMDP.oppModel.currentRoots;
			System.out.println(beliefNodesNow);
			System.out.println(
					InteractiveBelief.toStateMap(
							tigerL1IPOMDP, 
							tigerL1IPOMDP.lookAheadRootInitBeliefs.get(0)));
		} 
		
		catch (Exception e) {
			System.err.println(e.getMessage());
			e.printStackTrace();
			fail();
		}
		
	}
}
