/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.policy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import thinclab.belief.Belief;
import thinclab.belief.InteractiveBelief;
import thinclab.decisionprocesses.DecisionProcess;
import thinclab.decisionprocesses.IPOMDP;
import thinclab.decisionprocesses.POMDP;
import thinclab.legacy.DD;
import thinclab.policyhelper.PolicyNode;
import thinclab.solvers.BaseSolver;

/*
 * @author adityas
 *
 */
public class StaticBeliefTree extends StructuredTree {
	
	/*
	 * Holds a static belief tree which is expanded to max H at once. 
	 */
	
	/* reference for the framework and solver */
	DecisionProcess f;
	BaseSolver solver = null;
	
	private static final Logger logger = Logger.getLogger(StaticBeliefTree.class);
	
	// ------------------------------------------------------------------------------------
	
	public StaticBeliefTree(DecisionProcess f, int maxH) {
		
		/* set attributes */
		this.f = f;
		
		if (f instanceof IPOMDP)
			this.maxT = ((IPOMDP) this.f).mjLookAhead;
		
		else this.maxT = maxH;
		
		this.observations = this.f.getAllPossibleObservations();
		
		logger.debug("Initializing StaticBeliefTree for maxT " + this.maxT);
	}
	
	public StaticBeliefTree(BaseSolver solver, int maxH) {
		
		this(solver.f, maxH);
		this.solver = solver;
	}
	
	// -------------------------------------------------------------------------------------
	
	public List<Integer> getNextPolicyNodes(List<Integer> previousNodes, int T) {
		/*
		 * Compute the next PolicyNode from the list of previous PolicyNodes
		 */
		
		HashMap<DD, Integer> nodeMap = new HashMap<DD, Integer>();
		
		/* For each previous Node */
		for (int parentId : previousNodes) {
			
			/* For all combinations */
			for (List<String> obs : this.observations) {
				
				for (String action : this.f.getActions()) {
					
					DD belief = this.idToNodeMap.get(parentId).belief;
					
					this.makeNextBeliefNode(
							parentId, 
							belief, f, action, this.solver, obs, nodeMap, T);
			
				} /* for all actions */
			} /* for all observations */
		} /* for all parents */
		
		return new ArrayList<Integer>(nodeMap.values());
	}
	
	public void buildTree() {
		/*
		 * Builds the full OnlinePolicyTree upto maxT
		 */
		
		List<Integer> prevNodes = new ArrayList<Integer>();
		
		for (int i = 0; i < this.f.getInitialBeliefs().size(); i++) {
			prevNodes.add(i);
			
			PolicyNode node = new PolicyNode();
			node.id = i;
			node.belief = this.f.getInitialBeliefs().get(i);
			node.H = 0;
			
			if (this.f instanceof IPOMDP)
				node.sBelief = 
					InteractiveBelief.toStateMap(
							(IPOMDP) this.f, 
							node.belief).toString();
			
			else 
				node.sBelief =
					Belief.toStateMap((POMDP) this.f, node.belief).toString();
			
			if (this.solver != null)
				node.actName = this.solver.getActionForBelief(node.belief);
			
			else 
				node.actName = "";
				
			this.idToNodeMap.put(i, node);
			
			this.currentPolicyNodeCounter += 1;
		}
		
		for (int t = 0; t < this.maxT; t++) {
			
			List<Integer> nextNodes = this.getNextPolicyNodes(prevNodes, t);
			prevNodes = nextNodes;
		}
	}

}
