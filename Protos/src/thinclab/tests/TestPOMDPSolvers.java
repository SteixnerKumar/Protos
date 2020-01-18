/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.tests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashSet;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import thinclab.belief.FullBeliefExpansion;
import thinclab.belief.SSGABeliefExpansion;
import thinclab.decisionprocesses.POMDP;
import thinclab.exceptions.ZeroProbabilityObsException;
import thinclab.legacy.DD;
import thinclab.legacy.OP;
import thinclab.simulations.StochasticSimulation;
import thinclab.solvers.OfflinePBVISolver;
import thinclab.solvers.OfflineSymbolicPerseus;
import thinclab.utils.CustomConfigurationFactory;

/*
 * @author adityas
 *
 */
class TestPOMDPSolvers {

	public String tigerDom = "/home/adityas/git/repository/Protos/domains/tiger.95.SPUDD.txt";
//	public String attDom = "/home/adityas/git/repository/Protos/domains/attacker_l0.txt";
	
	private static Logger LOGGER;
	
	@BeforeEach
	void setUp() throws Exception {
		CustomConfigurationFactory.initializeLogging();
		LOGGER = Logger.getLogger(TestPOMDPSolvers.class);
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void testPOMDPInitFromParser() throws ZeroProbabilityObsException {
		LOGGER.info("Running testPOMDPInitFromParser()");
		
		POMDP p1 = new POMDP(this.tigerDom);
//		POMDP p2 = new POMDP(this.attDom);
		
		assertTrue(p1.S.size() >= 1);
		assertTrue(p1.Omega.size() >= 1);
		assertTrue(p1.A.size() >= 1);
		assertNotNull(p1.R);
		assertEquals(p1.S.size(), p1.stateVars.length);
		assertEquals(p1.Omega.size(), p1.obsVars.length);
		assertEquals(p1.A.size(), p1.actions.length);
		
		LOGGER.debug("Initial Belief: " + p1.getInitialBeliefs().get(0).toDDTree());
		LOGGER.debug("Action is: " + p1.getActions().get(0));
		LOGGER.debug("Obs: " + p1.getAllPossibleObservations().get(0));
		
		DD b1 = 
				p1.beliefUpdate( 
						p1.getInitialBeliefs().get(0), 
						p1.getActions().get(0), 
						p1.getAllPossibleObservations().get(0).stream().toArray(String[]::new));
		
		LOGGER.debug(p1.toMap(b1));
	}
	
	@Test
	void testOfflinePBVISolverForPOMDP() throws ZeroProbabilityObsException {
		LOGGER.info("Running testOfflinePBVISolverForPOMDP()");
		
		POMDP p1 = new POMDP(this.tigerDom);

		FullBeliefExpansion fb = new FullBeliefExpansion(p1, 3);
		
		OfflinePBVISolver solver = new OfflinePBVISolver(p1, fb, 3, 100);
		solver.solve();
		
		DD initial = p1.getInitialBeliefs().get(0);
		
		assertTrue(
				solver.getActionForBelief(
						p1.getInitialBeliefs().get(0)).contentEquals("listen"));
		
		DD nextBelief = 
				p1.beliefUpdate( 
						initial, 
						"listen", 
						new String[] {"growl-left"});
		
		assertTrue(
				solver.getActionForBelief(nextBelief).contentEquals("listen"));
		
		nextBelief = 
				p1.beliefUpdate( 
						nextBelief, 
						"listen", 
						new String[] {"growl-left"});
		
		assertTrue(
				solver.getActionForBelief(nextBelief).contentEquals("open-right"));
		
		nextBelief = 
				p1.beliefUpdate( 
						nextBelief, 
						"open-right", 
						new String[] {"growl-left"});
		
		assertTrue(
				solver.getActionForBelief(nextBelief).contentEquals("listen"));
	}
	
	@Test
	void testOfflineSymbolicPerseusSolverForPOMDP() throws ZeroProbabilityObsException {
		LOGGER.info("Running testOfflineSymbolicPerseusSolverForPOMDP()");
		
		POMDP p1 = new POMDP(this.tigerDom);
		SSGABeliefExpansion be = new SSGABeliefExpansion(p1, 100, 1);
		OfflineSymbolicPerseus solver = new OfflineSymbolicPerseus(p1, be, 15, 100);
		
		solver.solve();
		
		DD initial = p1.getInitialBeliefs().get(0);
		
		assertTrue(
				solver.getActionForBelief(
						p1.getInitialBeliefs().get(0)).contentEquals("listen"));
		
		DD nextBelief = 
				p1.beliefUpdate( 
						initial, 
						"listen", 
						new String[] {"growl-left"});
		
		assertTrue(
				solver.getActionForBelief(nextBelief).contentEquals("listen"));
		
		nextBelief = 
				p1.beliefUpdate( 
						nextBelief, 
						"listen", 
						new String[] {"growl-left"});
		
		assertTrue(
				solver.getActionForBelief(nextBelief).contentEquals("open-right"));
		
		nextBelief = 
				p1.beliefUpdate( 
						nextBelief, 
						"open-right", 
						new String[] {"growl-left"});
		
		assertTrue(
				solver.getActionForBelief(nextBelief).contentEquals("listen"));
	}
	
	@Test
	void testOfflineSymbolicPerseusSolverWithFullExpansionForPOMDP() 
			throws ZeroProbabilityObsException {
		LOGGER.info("Running testOfflineSymbolicPerseusSolverWithFullExpansionForPOMDP()");
		
		POMDP p1 = new POMDP(this.tigerDom);
		FullBeliefExpansion fb = new FullBeliefExpansion(p1, 2);
		OfflineSymbolicPerseus solver = new OfflineSymbolicPerseus(p1, fb, 4, 100);
		
		solver.solve();
		
		DD initial = p1.getInitialBeliefs().get(0);
		
		assertTrue(
				solver.getActionForBelief(
						p1.getInitialBeliefs().get(0)).contentEquals("listen"));
		
		DD nextBelief = 
				p1.beliefUpdate( 
						initial, 
						"listen", 
						new String[] {"growl-left"});
		
		assertTrue(
				solver.getActionForBelief(nextBelief).contentEquals("listen"));
		
		nextBelief = 
				p1.beliefUpdate( 
						nextBelief, 
						"listen", 
						new String[] {"growl-left"});
		
		assertTrue(
				solver.getActionForBelief(nextBelief).contentEquals("open-right"));
		
		nextBelief = 
				p1.beliefUpdate( 
						nextBelief, 
						"open-right", 
						new String[] {"growl-left"});
		
		assertTrue(
				solver.getActionForBelief(nextBelief).contentEquals("listen"));
	}
	
	@Test
	void testSim() {
		LOGGER.info("Testing Stochastic sim");
		
		POMDP p1 = new POMDP(this.tigerDom);
		FullBeliefExpansion fb = new FullBeliefExpansion(p1, 2);
		OfflineSymbolicPerseus solver = new OfflineSymbolicPerseus(p1, fb, 4, 100);
		solver.solve();
		
		StochasticSimulation ss = new StochasticSimulation(solver, 10);
		ss.runSimulation();
		
		LOGGER.debug(ss.getDotString());
	}
	
	@Test
	void testStuff() {
		
		LOGGER.info("Testing stuff");
		
		POMDP p1 = new POMDP(this.tigerDom);
		
		String action = p1.getActions().get(0);
		int act = p1.getActions().indexOf(action);
		
		LOGGER.debug("Trying for action: " + action);
		
		HashSet<DD> F = new HashSet<DD>();
		F.add(p1.getRewardFunctionForAction(action));
		
		for (int i = 0; i < 10; i++) {
			
			for (DD f: F) {
				
				for (List<String> obs : p1.getAllPossibleObservations()) {
					
					DD[] dds = new DD[0];
					
					dds = ArrayUtils.addAll(dds, p1.actions[act].transFn);
					dds = ArrayUtils.addAll(dds, p1.actions[act].obsFn);
					dds = ArrayUtils.add(dds, f);
					
					DD Tf = OP.multN(dds);
					
					LOGGER.debug("TF is " + Tf.toDDTree());
				}
			}
		}
	}
}
