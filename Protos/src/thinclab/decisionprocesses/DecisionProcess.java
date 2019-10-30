/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.decisionprocesses;

import java.util.Arrays;
import java.util.List;

import thinclab.exceptions.VariableNotFoundException;
import thinclab.legacy.DD;
import thinclab.legacy.Global;
import thinclab.legacy.OP;

/*
 * @author adityas
 *
 */
public abstract class DecisionProcess {
	/*
	 * Defines the basic skeleton for a POMDP or IPOMDP object
	 */
	
	// --------------------------------------------------------------------------------
	
	public abstract List<String> getActions();
	public abstract List<List<String>> getAllPossibleObservations();
	public abstract List<String> getObsVarNames();
	public abstract List<String> getStateVarNames();
	public abstract List<DD> getInitialBeliefs();
	public abstract DD getCurrentBelief();
	public abstract int[] getStateVarIndices();
	public abstract int[] getObsVarIndices();
	public abstract void setGlobals();
	public abstract String getType();
	public abstract String getBeliefString(DD belief);
	
	// ---------------------------------------------------------------------------------
	
	public static String getActionFromPolicy(
			DecisionProcess f, DD belief, DD[] alphaVectors, int[] policy) {
		
		/*
		 * Compute the dot product of each alpha vector with the belief and
		 * return the action represented by the max alpha vector 
		 */
		
		double bestVal = Double.NEGATIVE_INFINITY;
		double val;
		int bestAlphaId = 0;
		
		double[] values = new double[alphaVectors.length];
		for (int alphaId = 0; alphaId < alphaVectors.length; alphaId++) {
			
			val = OP.dotProduct(belief, alphaVectors[alphaId], f.getStateVarIndices());
			values[alphaId] = val;
			
			if (val >= bestVal) {
				bestVal = val;
				bestAlphaId = alphaId;
			}
		}
		
		String bestAction = f.getActions().get(policy[bestAlphaId]); 
		
		return bestAction;
	}
	
	// --------------------------------------------------------------------------------
	
	public static int getVarIndex(String varName) throws VariableNotFoundException {
		/*
		 * Gets the global varIndex for variable varName
		 */
		if (varName.contains("'")) 
			varName = varName.substring(0, varName.length() - 1) + "_P";
		
		int varIndex = Arrays.asList(Global.varNames).indexOf(varName) + 1;
		
		if (varIndex == -1)
			throw new VariableNotFoundException("Var " + varName + " does not exist");
		
		return varIndex;
	}
	
	public static String getVarName(int varIndex) throws VariableNotFoundException {
		/*
		 * Gets the Global varName for the varIndex
		 */
		if (varIndex > Global.varNames.length) 
			throw new VariableNotFoundException("Can't find var for index " + varIndex);
		
		/* sub 1 from index to compensate for Matlab-like indexing in Globals */
		String varName = Global.varNames[varIndex - 1];
		
		if (varName.length() > 2 && varName.substring(varName.length() - 2).contains("_P"))
			return varName.substring(0, varName.length() - 2) + "'";
		
		else return varName;
	}
}