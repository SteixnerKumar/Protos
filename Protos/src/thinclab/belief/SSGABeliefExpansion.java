/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.belief;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.log4j.Logger;

import cern.colt.Arrays;
import thinclab.decisionprocesses.POMDP;
import thinclab.exceptions.ZeroProbabilityObsException;
import thinclab.legacy.DD;
import thinclab.legacy.Global;
import thinclab.legacy.OP;

/*
 * @author adityas
 *
 */
public class SSGABeliefExpansion extends BeliefRegionExpansionStrategy {
	
	/*
	 * Runs an SSGA belief expansion starting from the initial belief of the given POMDP
	 */
	
	private static final long serialVersionUID = 4990909984057211665L;
	
	/* reference to the recent policy of the solver */
	public DD[] alphaVectors;
	public int[] policy;
	
	/* reference to the POMDP */
	public POMDP p;
	
	/* All possible combinations of observations */
	public List<List<String>> allPossibleObs;
	
	/* Currently explored beliefs */
	private List<DD> initialBeliefs;
	private HashSet<DD> exploredBeliefs;
	
	/* number of iterations of SSGA during each expansion */
	private int nIterations;
	
	private static final Logger logger = Logger.getLogger(SSGABeliefExpansion.class);
	
	// ----------------------------------------------------------------------------------------
	
	public SSGABeliefExpansion(POMDP p, int maxDepth, int iterations) {
		/*
		 * Set properties and initialize super
		 */
		
		super(maxDepth);
		
		this.setFramework(p);
		
		this.p = p;
		this.allPossibleObs = this.p.getAllPossibleObservations();
		this.nIterations = iterations;
		
		/* add initial beliefs from the POMDP */
		this.initialBeliefs = new ArrayList<DD>();
		this.initialBeliefs.addAll(this.p.getInitialBeliefs());
		
		this.exploredBeliefs = new HashSet<DD>();
		this.exploredBeliefs.addAll(this.p.getInitialBeliefs());
		
		logger.debug("SSGA expansion search initialized");
	}
	
	// ----------------------------------------------------------------------------------------
	
	public void setRecentPolicy(DD[] aVectors, int[] policy) {
		/*
		 * Updates the references to the solver's most recent policy
		 */
		this.alphaVectors = aVectors;
		this.policy = policy;
	}
	
	@Override
	public void expand() {
		/*
		 * Run SSGA expansion for nIterations
		 */
		
		logger.debug("Starting " + this.nIterations 
				+ " expansions till depth " + this.getHBound() 
				+ " from " + this.initialBeliefs.size() + " belief points.");
		
		/* Create multinomial for sampling actions */
		double[] explore = new double[2];
		explore[0] = 0.6;
		explore[1] = 0.4;
		
		/* for iterations */
		for (int n = 0; n < this.nIterations; n++) {
		
			/* Start traversal from initial beliefs */
			for (DD belief : this.initialBeliefs) {
				
				for (int i=0; i < this.getHBound(); i++) {
					
					/* Initialize linked list to store new beliefs */
					int usePolicy = OP.sampleMultinomial(explore);
					
					/* action sampling */
					int act;
					
					if (usePolicy == 0) 
						act = 
							p.getActions().indexOf(
									POMDP.getActionFromPolicy(
											p, belief, this.alphaVectors, this.policy));
					
					else act = Global.random.nextInt(this.f.getActions().size());
						
					DD obsDist = this.f.getObsDist(belief, this.f.getActions().get(act));

					int[][] obsConfig = OP.sampleMultinomial(obsDist, p.primeObsIndices);
					
					/* Get next belief */
					try {

						DD nextBelief = ((BeliefOps) p.bOPs).beliefUpdate(belief,
							p.getActions().get(act), 
							obsConfig);
						
						/* Add belief point if it doesn't already exist */
						if (!this.exploredBeliefs.contains(nextBelief))
							this.exploredBeliefs.add(nextBelief);
						
						belief = nextBelief;
					} 
					
					catch (ZeroProbabilityObsException e) {
					
						continue;
					}
					
				} /* for horizon */
				
			} /* for leaf */
			
		} /* for iterations */
	
		logger.debug("Total beliefs explored are " + this.exploredBeliefs.size());
	}

	@Override
	public List<DD> getBeliefPoints() {
		/*
		 * Return list of currently explored unique belief points
		 */
		return new ArrayList<DD>(this.exploredBeliefs);
	}
	
	@Override
	public void resetToNewInitialBelief() {
		/*
		 * Clear currently explored beliefs and populate everything again starting from
		 * the initial beliefs from the framework
		 */
		
		/* clear all previous beliefs */
		this.exploredBeliefs.clear();
		this.initialBeliefs.clear();
		
		/* get new initial beliefs from the framework */
		this.initialBeliefs.addAll(this.f.getInitialBeliefs());
		this.exploredBeliefs.addAll(this.initialBeliefs);
	}

	@Override
	public void clearMem() {
		/*
		 * Clear all beliefs
		 */
		this.exploredBeliefs.clear();
		this.initialBeliefs.clear();
	}

}
