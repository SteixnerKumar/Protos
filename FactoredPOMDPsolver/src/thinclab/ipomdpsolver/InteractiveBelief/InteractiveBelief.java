/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.ipomdpsolver.InteractiveBelief;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Vector;

import org.apache.commons.lang3.ArrayUtils;

import thinclab.exceptions.VariableNotFoundException;
import thinclab.exceptions.ZeroProbabilityObsException;
import thinclab.ipomdpsolver.IPOMDP;
import thinclab.symbolicperseus.DD;
import thinclab.symbolicperseus.Global;
import thinclab.symbolicperseus.MySet;
import thinclab.symbolicperseus.OP;
import thinclab.symbolicperseus.POMDP;
import thinclab.symbolicperseus.Belief.Belief;

/*
 * @author adityas
 *
 */
public class InteractiveBelief extends Belief {
	/*
	 * Represents a single interactive belief
	 * 
	 * This may be a little inefficient especially during belief updates. During the actual
	 * computation, the actual belief vector will have to be accessed through the object 
	 * attributes.
	 */
	
	// -------------------------------------------------------------------------------------
	
	public static DD staticL1BeliefUpdate(
			IPOMDP ipomdp,
			DD startBelief, 
			String actName, 
			String[] observations) throws ZeroProbabilityObsException, VariableNotFoundException {
		/*
		 * Performs a static L1 IPOMDP belief update given action and observations
		 * 
		 * Performs a query over the following DBN:
		 *      .---.                    .----.
		 *      |M_j|----------.-------->|M_j'|<----------------------.
		 *      '---'          |_______. '----'                       |
		 *      .---.                  '-->.---.______________.       |	
		 *      | S |--------------------->| S'|--,           |       |
		 *      '---'                      '---'  |           |       |
		 *                                        V           V       | 		 
		 *                                      .---.       .---.     |
		 *                                      |Oi'|       |Oj'|_____|
		 *                                      '---'       '---'
		 * 	 P_hat(S', M_j' | Oi'=o) = 
		 * 			Sigma[S] (P(S)) x Sigma[M_j] (P(M_j) x P(S', S, M_j) x P(Oi'=o, S', M_j))
		 * 		 		x	Sigma[Oj'] (P(Oj', S', M_j) x P(M_j', M_j, Oj')) 
		 */
		
		/*
		 * First reduce Oi based on observations
		 */
		int[] obsVals = new int[ipomdp.Omega.size() - ipomdp.OmegaJNames.size()];
		
		for (int o = 0; o < obsVals.length; o++) {
			int val = ipomdp.findObservationByName(o, observations[o]) + 1;
			
			if (val < 0) 
				throw new VariableNotFoundException(
						"Obs Variable " + ipomdp.Omega.get(o).name
						+ " does not take value " + observations[o]);
			
			else obsVals[o] = val;
		}
		
		/* Restrict Oi */
		DD[] restrictedOi = 
				OP.restrictN(
						ipomdp.currentOi.get(actName), 
						IPOMDP.stackArray(
								ipomdp.obsIVarPrimeIndices, obsVals));
		
		/* Collect f1 = P(S, Mj) x P(S', S, Mj) */
		DD[] f1 = ArrayUtils.add(ipomdp.currentTi.get(actName), startBelief);
		
		/* Collect f2 = f1 x P(Oi'=o, S', Mj) */
		DD[] f2 = ArrayUtils.addAll(f1, restrictedOi);
		
		/* Collect f3 = P(Oj', S', Mj) x P(Mj', Mj, Oj') */
		DD[] f3 = ArrayUtils.add(ipomdp.currentOj, ipomdp.currentMjTfn);
		
		/* Collect f4 = f2 x f3 */
		DD[] f4 = ArrayUtils.addAll(f2, f3);
		
		/* Multiply and sum out */
		DD nextBelief = 
				OP.addMultVarElim(
						f4, 
						ArrayUtils.addAll(
								ipomdp.stateVarIndices, 
								ipomdp.obsJVarPrimeIndices));
		
		/* Shift indices */
		nextBelief = OP.primeVars(nextBelief, -(ipomdp.S.size() + ipomdp.Omega.size()));
		
		/* compute normalization factor */
		DD norm = OP.addMultVarElim(nextBelief, ipomdp.stateVarIndices);
		
		if (norm.getVal() < 1e-8) 
			throw new ZeroProbabilityObsException(
					"Observation " + Arrays.toString(observations) 
					+ " not possible at belief " + startBelief);
		
		return OP.div(nextBelief, norm); 
	}
	
	public static DD getL1BeliefUpdateNorm(
			IPOMDP ipomdp,
			DD startBelief, 
			String actName) throws ZeroProbabilityObsException, VariableNotFoundException {
		/*
		 * Because the dpBackUp implementation by Hoey needs it.
		 */
		
		/* Collect f1 = P(S, Mj) x P(S', S, Mj) */
		DD[] f1 = ArrayUtils.add(ipomdp.currentTi.get(actName), startBelief);
		
		/* Collect f2 = f1 x P(Oi', S', Mj) */
		DD[] f2 = ArrayUtils.addAll(f1, ipomdp.currentOi.get(actName));
		
		/* Collect f3 = P(Oj', S', Mj) x P(Mj', Mj, Oj') */
		DD[] f3 = ArrayUtils.add(ipomdp.currentOj, ipomdp.currentMjTfn);
		
		/* Collect f4 = f2 x f3 */
		DD[] f4 = ArrayUtils.addAll(f2, f3);
		
		/* Multiply and sum out */
		DD norm = 
				OP.addMultVarElim(
						f4, 
						ArrayUtils.addAll(
							ArrayUtils.addAll(
								ipomdp.stateVarIndices, 
								ipomdp.obsJVarPrimeIndices),
							ipomdp.stateVarPrimeIndices));
		
		return norm;
	}
	
	public static DD[] getCpts(
			IPOMDP ipomdp,
			DD startBelief, 
			String actName) throws ZeroProbabilityObsException, VariableNotFoundException {
		/*
		 * Because the dpBackUp implementation by Hoey needs it.
		 */
		
		/* Collect f1 = P(S, Mj) x P(S', S, Mj) */
		DD[] f1 = 
				ArrayUtils.addAll(
						ipomdp.currentTi.get(actName),
						InteractiveBelief.factorInteractiveBelief(ipomdp, startBelief));
		
		/* Collect f2 = f1 x P(Oi', S', Mj) */
		DD[] f2 = ArrayUtils.addAll(f1, ipomdp.currentOi.get(actName));
		
		/* Collect f3 = P(Oj', S', Mj) x P(Mj', Mj, Oj') */
		DD[] f3 = ArrayUtils.add(ipomdp.currentOj, ipomdp.currentMjTfn);
		
		/* Collect f4 = f2 x f3 */
		DD[] f4 = ArrayUtils.addAll(f2, OP.addMultVarElim(f3, ipomdp.obsJVarPrimeIndices));
		
		return f4;
	}
	
	// ------------------------------------------------------------------------------------------
	
	public static DD[] factorInteractiveBelief(IPOMDP ipomdp, DD belief) {
		/*
		 * Factors belief point from joint distribution to individual variables
		 * 
		 * Exactly similar to Hoey's implementation for POMDPs
		 */
		ipomdp.setGlobals();
		
		DD[] fbs = new DD[ipomdp.nStateVars];
		
		/* For each state var, summout everything else */
		for (int varId = 0; varId < ipomdp.nStateVars; varId++)
			fbs[varId] = OP.addMultVarElim(belief,
					MySet.remove(ipomdp.varIndices, varId + 1));

		return fbs;
	}
	
	public static DD[][] factorInteractiveBeliefRegion(IPOMDP ipomdp, List<DD> iBeliefRegion) {
		/*
		 * Factors the full belief region represented as a list
		 */
		DD[][] iBelRegion = new DD[iBeliefRegion.size()][ipomdp.nStateVars];
		
		/* Convert to array because accessing known elements is faster in arrays */
		DD[] iBeliefRegionArray = iBeliefRegion.toArray(new DD[iBeliefRegion.size()]);
		
		for (int i = 0; i < iBeliefRegion.size(); i++)
			iBelRegion[i] = InteractiveBelief.factorInteractiveBelief(ipomdp, iBeliefRegionArray[i]);
				
		return iBelRegion;
	}
	
	// ------------------------------------------------------------------------------------------
	
	public static HashMap<String, HashMap<String, Float>> toStateMap(IPOMDP ipomdp, DD belState) {
		/*
		 * Makes a hashmap of belief state and values and returns it
		 */
		ipomdp.setGlobals();
		
		HashMap<String, HashMap<String, Float>> beliefs = 
				new HashMap<String, HashMap<String, Float>>();
		
		/* Factor the belief state into individual variables */
		DD[] fbs = new DD[ipomdp.stateVarIndices.length];
		for (int varId = 0; varId < ipomdp.stateVarIndices.length; varId++) {
			
			fbs[varId] = OP.addMultVarElim(belState,
					MySet.remove(ipomdp.varIndices, varId + 1));
			
			/* Make state variable name */
			String name = ipomdp.varName[varId];
			
			/* Get respective belief for the variable */
			DD[] varChildren = fbs[varId].getChildren();
			HashMap<String, Float> childVals = new HashMap<String, Float>();
			
			if (varChildren == null) {
				for (int i=0; i < ipomdp.stateVars[varId].arity; i++) {
					childVals.put(Global.valNames[varId][i], new Float(fbs[varId].getVal()));
				}
			}
			
			else {
				for (int i=0; i < ipomdp.stateVars[varId].arity; i++) {
					
					if (varChildren[i].getVal() == 0.0)
						continue;
					
					childVals.put(Global.valNames[varId][i], new Float(varChildren[i].getVal()));
				}
			}
			
			beliefs.put(name, childVals);
		}
		
		return beliefs;
	}
	
	// ---------------------------------------------------------------------------------------------
	
	public static String toDot(IPOMDP ipomdp, DD belief) {
		/*
		 * Converts to a dot format node for graphviz
		 */
		
		String endl = "\r\n";
		String dotString = "[";
		
		dotString += "label=\"{BELIEF" + endl;
		
		HashMap<String, HashMap<String, Float>> stateMap = 
				InteractiveBelief.toStateMap(ipomdp, belief);
		
		for (Entry<String, HashMap<String, Float>> sEntry : stateMap.entrySet()) {
			
			dotString += "|" + sEntry.getKey();
			
			for (Entry<String, Float> entry : sEntry.getValue().entrySet())
				dotString += "|{" + entry.getKey() + "|" + entry.getValue() + "}";  
		}

		dotString += "}\" , shape=record]";
		return dotString;
	}
	
}
