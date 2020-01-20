/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.belief;

import java.util.Arrays;
import java.util.HashMap;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.log4j.Logger;

import thinclab.decisionprocesses.FIPOMDP;
import thinclab.decisionprocesses.IPOMDP;
import thinclab.exceptions.ZeroProbabilityObsException;
import thinclab.legacy.DD;
import thinclab.legacy.Global;
import thinclab.legacy.OP;

/*
 * @author adityas
 *
 */
public class FIBeliefOps extends IBeliefOps {
	
	/*
	 * Defines belief operations for fully factored I-POMDPs
	 */

	private static final long serialVersionUID = 439964215831344094L;
	private static final Logger LOGGER = Logger.getLogger(IBeliefOps.class);
	
	// --------------------------------------------------------------------------------------

	public FIBeliefOps(FIPOMDP fIpomdp) {
		super(fIpomdp);
	}
	
	// --------------------------------------------------------------------------------------
	
	@Override
	public DD beliefUpdate(
			DD belief, String action, String[] observations) throws ZeroProbabilityObsException {
		
		/*
		 * Level 1 belief update
		 * 
		 * P(S, Mj, Thetaj| Oi'=o) = 
		 * 		norm x Sumout[S, Mj, Thetaj, Aj] 
		 * 					f(S, Mj, Thetaj) x f(Aj, Mj, Thetaj)  
		 * 					x f(S', S, Aj) x f(Thetaj', Thetaj) x f(Oi'=o, S', Aj)
		 * 			 x Sumout[Oj'] 
		 * 					f(Oj', Aj, Thetaj, S') x f(Mj', Mj, Aj, Thetaj, Oj') 
		 */
		
		FIPOMDP DPRef = (FIPOMDP) this.getIPOMDP();
		
		/* First reduce Oi based on observations */
		int[] obsVals = 
				new int[DPRef.Omega.size() - DPRef.OmegaJNames.size()];
		
		for (int o = 0; o < obsVals.length; o++) {
			int val = DPRef.findObservationByName(o, observations[o]) + 1;
			
			if (val < 0) {
				LOGGER.error(
						"Obs Variable " + DPRef.Omega.get(o).name
						+ " does not take value " + observations[o]);
				System.exit(-1);
			}
			
			else obsVals[o] = val;
		}
		
		/* Restrict Oi */
		DD[] restrictedOi = 
				OP.restrictN(
						DPRef.currentOi.get(action), 
						IPOMDP.stackArray(
								DPRef.obsIVarPrimeIndices, obsVals));
		
		/* Collect f1 = P(S, Mj, Thetaj)  */
		DD f1 = belief;

		/*
		 * Collect f2 = 
		 * 		P(Aj| Mj, Thetaj) x P(Thetaj'| Thetaj) 
		 * 			x P(Oi'=o, S', Aj) 
		 * 			x P (S', Aj, S)
		 */
		DD[] f2 = 
				ArrayUtils.addAll(
						ArrayUtils.addAll(
								DPRef.currentTi.get(action), 
								new DD[] {
										DPRef.currentPAjGivenMjThetaj, 
										DPRef.currentThetajPGivenThetaj}), 
						restrictedOi);
		
		/* Get TAU */
		DD tau = DPRef.currentTau;
		
		/* Perform the sum out */
		DD nextBelief = 
				OP.addMultVarElim(
						ArrayUtils.add(
								ArrayUtils.addAll(f2, f1), 
								tau),
						DPRef.stateVarIndices);
		
		/* Shift indices */
		nextBelief = OP.primeVars(nextBelief, -(DPRef.S.size() + DPRef.Omega.size()));
		
		/* compute normalization factor */
		DD norm = 
				OP.addMultVarElim(
						nextBelief, 
						ArrayUtils.subarray(
								DPRef.stateVarIndices, 
								0, 
								DPRef.AjVarStartPosition));
		
		if (norm.getVal() < 1e-8) 
			throw new ZeroProbabilityObsException(
					"Observation " + Arrays.toString(observations) 
					+ " not possible at belief " + belief);
		
		return OP.div(nextBelief, norm); 
	}
	
	@Override
	public HashMap<String, HashMap<String, Float>> toMap(DD belief) {
		/*
		 * Makes a hashmap of belief state and values and returns it
		 */
		FIPOMDP DPRef = (FIPOMDP) this.getIPOMDP();
		
		HashMap<String, HashMap<String, Float>> beliefs = 
				new HashMap<String, HashMap<String, Float>>();
		
		/* Factor the belief state into individual variables */
		DD[] fbs = new DD[DPRef.AjVarStartPosition];
		for (int varId = 0; varId < fbs.length; varId++) {
			
			fbs[varId] = OP.addMultVarElim(belief,
					ArrayUtils.remove(
							ArrayUtils.subarray(
									DPRef.stateVarIndices, 
									0, 
									DPRef.AjVarStartPosition), varId));
			
			/* Make state variable name */
			String name = DPRef.S.get(varId).name;
			
			/* Get respective belief for the variable */
			DD[] varChildren = fbs[varId].getChildren();
			HashMap<String, Float> childVals = new HashMap<String, Float>();
			
			if (varChildren == null) {
				for (int i=0; i < DPRef.stateVars[varId].arity; i++) {
					childVals.put(Global.valNames[varId][i], new Float(fbs[varId].getVal()));
				}
			}
			
			else {
				for (int i=0; i < DPRef.stateVars[varId].arity; i++) {
					if (varChildren[i].getVal() == 0.0)
						continue;
					
					childVals.put(Global.valNames[varId][i], new Float(varChildren[i].getVal()));
				}
			}
			
			beliefs.put(name, childVals);
		}
		
		return beliefs;
	}

}
