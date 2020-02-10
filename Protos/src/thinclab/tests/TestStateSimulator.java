/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.tests;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import thinclab.belief.SSGABeliefExpansion;
import thinclab.belief.SparseFullBeliefExpansion;
import thinclab.decisionprocesses.IPOMDP;
import thinclab.decisionprocesses.POMDP;
import thinclab.parsers.IPOMDPParser;
import thinclab.simulations.StateSimulator;
import thinclab.simulations.StochasticSimulation;
import thinclab.solvers.BaseSolver;
import thinclab.solvers.OfflineSymbolicPerseus;
import thinclab.solvers.OnlineInteractiveSymbolicPerseus;
import thinclab.utils.CustomConfigurationFactory;

/*
 * @author adityas
 *
 */
class TestStateSimulator {

	public String l1DomainFile;
	public String l0DomainFile;
	
	private static Logger LOGGER;
	
	@BeforeEach
	void setUp() throws Exception {
		CustomConfigurationFactory.initializeLogging();
		LOGGER = Logger.getLogger(TestStateSimulator.class);
		this.l1DomainFile = "/home/adityas/git/repository/Protos/domains/tiger.L1multiple_new_parser.txt";
//		this.l0DomainFile = "/home/adityas/git/repository/Protos/domains/tiger.95.SPUDD.txt";
		this.l0DomainFile = "/home/adityas/UGA/THINCLab/DomainFiles/final_domains/exfil.5S.L0.domain";
//		this.l0DomainFile = "/home/adityas/git/repository/Protos/domains/coffee3po.dat";
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	void testStateSimInit() throws Exception {
		
		/* init L0 */
		POMDP pomdp = new POMDP(this.l0DomainFile);
		OfflineSymbolicPerseus S0 = 
				new OfflineSymbolicPerseus(
						pomdp, 
						new SSGABeliefExpansion(pomdp, 10, 10), 
						10, 100);
		
		S0.solve();
		
		StochasticSimulation SS = new StochasticSimulation(S0, 10);
		SS.runSimulation();
		
		LOGGER.info(SS.getJSONString());
	}

}
