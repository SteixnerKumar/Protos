/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.representations.modelrepresentations;

import java.util.HashMap;
import java.util.HashSet;

import thinclab.decisionprocesses.DecisionProcess;
import thinclab.representations.policyrepresentations.PolicyNode;

/*
 * @author adityas
 *
 */
public class FactoredMJ {
	
	/*
	 * Factored Mj into independent parts of Mj beliefs
	 */
	
	public HashMap<String, HashSet<HashMap<String, Float>>> individualBeliefs = 
			new HashMap<String, HashSet<HashMap<String, Float>>>();
	
	public void makeFactoredMj(
			HashMap<Integer, PolicyNode> idToNodeMap, DecisionProcess DPRef) {
		
		/*
		 * Factors the joint distributions of MJ into independent components 
		 */
		
		for (PolicyNode node: idToNodeMap.values()) {
			for (String stateName: DPRef.getStateVarNames()) {
				
				if (!this.individualBeliefs.containsKey(stateName))
					this.individualBeliefs.put(stateName, new HashSet<HashMap<String, Float>>());
				
				this.individualBeliefs.get(stateName).add( 
						DPRef.toMap(node.belief).get(stateName));
			}
		}
	}

}
