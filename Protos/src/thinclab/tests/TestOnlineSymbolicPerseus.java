/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import thinclab.belief.FullInteractiveBeliefExpansion;
import thinclab.frameworks.IPOMDP;
import thinclab.frameworks.POMDP;
import thinclab.ipomdpsolver.IPOMDPParser;
import thinclab.solvers.OnlineInteractiveSymbolicPerseus;
import thinclab.solvers.OnlineSolver;

/*
 * @author adityas
 *
 */
class TestOnlineSymbolicPerseus {

	public String l1DomainFile;
	public String tigerDom;
	
	@BeforeEach
	void setUp() throws Exception {
		this.l1DomainFile = "/home/adityas/git/repository/FactoredPOMDPsolver/src/tiger.L1.txt";
		this.tigerDom = "/home/adityas/git/repository/FactoredPOMDPsolver/src/tiger.95.SPUDD.txt";
//		this.tigerDom = "/home/adityas/git/repository/FactoredPOMDPsolver/src/attacker_l0.txt";
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void testFHLASolverUtiliyComputation() {
		System.out.println("Running testFHLASolverUtiliyComputation()");
		
		IPOMDPParser parser = new IPOMDPParser(this.l1DomainFile);
		parser.parseDomain();
		
		/*
		 * Initialize IPOMDP
		 */
		IPOMDP tigerL1IPOMDP = new IPOMDP(parser, 15, 3);
		
		FullInteractiveBeliefExpansion fb = 
				new FullInteractiveBeliefExpansion(
						tigerL1IPOMDP);
		
		OnlineInteractiveSymbolicPerseus solver = 
				new OnlineInteractiveSymbolicPerseus(
						tigerL1IPOMDP, 
						fb, 
						1, 
						100);
		
		solver.solveCurrentStep();
		
		solver.nextStep(
				solver.getBestActionAtCurrentBelief(), 
				tigerL1IPOMDP.obsCombinations.get(2));
		
		solver.solveCurrentStep();
		
		solver.nextStep(
				solver.getBestActionAtCurrentBelief(), 
				tigerL1IPOMDP.obsCombinations.get(2));
		
		solver.solveCurrentStep();
		
		solver.nextStep(
				solver.getBestActionAtCurrentBelief(), 
				tigerL1IPOMDP.obsCombinations.get(2));
	}
	
//	@Test
//	void testPOMDPSolvePBVI() {
//		System.out.println("Running testPOMDPSolvePBVI()");
//		
//		POMDP p1 = new POMDP(this.tigerDom);
////		POMDP p2 = new POMDP(this.attDom);
//		
//		assertNull(p1.alphaVectors);
////		assertNull(p2.alphaVectors);
//		
//		p1.solvePBVI(1, 6);
////		p2.solvePBVI(15, 100);
//		
//		assertNotNull(p1.alphaVectors);
////		assertNotNull(p2.alphaVectors);
//	}

}