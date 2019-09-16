/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.policyhelper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import thinclab.symbolicperseus.DD;
import thinclab.symbolicperseus.POMDP;
import thinclab.symbolicperseus.Belief.Belief;
import thinclab.utils.LoggerFactory;

/*
 * @author adityas
 *
 */
public class PolicyTree {
	/*
	 * Constructs the policy tree for a solved POMDP
	 * 
	 * Similar to policy graph. But in this case, it computes the policy up to a given horizon
	 */
	
	public POMDP pomdp;
	public List<List<String>> allObsCombinations = new ArrayList<List<String>>();
	
	public List<PolicyNode> roots;
	public List<PolicyNode> policyNodes;
	
	private Logger logger = LoggerFactory.getNewLogger("PolicyTree");
	
	public HashSet<DD> treeRoots = new HashSet<DD>();
	
	private int idCounter = 0;
	
	// -------------------------------------------------------------------------------------
	
	public PolicyTree(POMDP p, int horizon) {
		/*
		 * Constructor makes the policy tree for the given horizon
		 */
		this.pomdp = p;
		
		this.roots = 
				this.pomdp.getInitialBeliefsList().stream()
												  .map(b -> new PolicyNode(
														  this.pomdp, 
														  b, 
														  0))
												  .collect(Collectors.toList());
		
		this.roots.stream().forEach(n -> n.setStartNode());
		IntStream.range(0, this.roots.size())
				 .forEach(i -> 
				 	this.roots.get(i).setId(this.getNextId()));
		
		this.logger.info("Building policy tree for POMDP" + p + " starting from " + this.roots);
		
		this.expandForHorizon(horizon);
	}
	
	// --------------------------------------------------------------------------------------
	
	public List<PolicyNode> expandForSingleStep(
			List<PolicyNode> previousLeaves, 
			int currentH) {
		/*
		 * Expands the policy tree for a single time step
		 */
		this.logger.fine("Expanding from horizon " + currentH + " from nodes " + previousLeaves);
		
		List<PolicyNode> nextNodes = new ArrayList<PolicyNode>();
		
		/*
		 * Use hashmap to index new nodes. This ensures only unique nodes are added and
		 * repeating nodes at the same horizon do not exist.
		 */
		HashMap<DD, Integer> nodeIndexMap = new HashMap<DD, Integer>();
		
		for (PolicyNode policyNode : previousLeaves) {
			
			/*
			 * Update for each observation 
			 */
			List<List<String>> obs = this.pomdp.getAllObservationsList();
			for (List<String> o : obs) {
				
				DD nextBelief;
				
				try {
					nextBelief = 
							Belief.beliefUpdate(
									this.pomdp, 
									policyNode.belief, 
									policyNode.actId, 
									o.toArray(new String[o.size()]));
				}
				
				catch (Exception e) {
					continue;
				}
				
				/* unique belief. So add it to the node index and give it a new ID */
				if (!nodeIndexMap.containsKey(nextBelief))
					nodeIndexMap.put(nextBelief, new Integer(this.getNextId()));
					
				int newNodeId = nodeIndexMap.get(nextBelief);

				policyNode.nextNode.put(o, newNodeId);
			}
		}
		
		/* Add each unique node to next nodes */
		nodeIndexMap.forEach((k, v) -> {
			PolicyNode newNode = new PolicyNode(this.pomdp, k, currentH + 1);
			newNode.setId(v);
			nextNodes.add(newNode);
		});
		
		return nextNodes;
	}
	
	public void expandForHorizon(int horizons) {
		/*
		 * Expands the policy tree for given number of horizons
		 */
		this.policyNodes = new ArrayList<PolicyNode>();
		List<PolicyNode> previousLeaves = new ArrayList<PolicyNode>();
		
		previousLeaves.addAll(this.roots);
		this.policyNodes.addAll(this.roots);
		
		/*
		 * Expand the policy tree
		 */
		for (int h=0; h < horizons; h++) {
			List<PolicyNode> nextNodes = this.expandForSingleStep(previousLeaves, h);
			
			/*
			 * If this is the last time step. Loop all nodes back to themselves for
			 * any random observation
			 * 
			 * WARNING: THIS IS REALLY A HACK TO ENSURE THAT THE RESULTING DD IS NORMALISED.
			 * LOOPING LEAVES TO THEMSELVES MAY HAVE OTHER UNFORESEEN IMPLICATIONS.
			 */
			if (h == (horizons - 1)) {
				List<List<String>> dummyObs = this.pomdp.getAllObservationsList();
				for (PolicyNode nextNode : nextNodes)
					dummyObs.stream().forEach(o -> nextNode.nextNode.put(o, nextNode.id));
			}
			this.policyNodes.addAll(nextNodes);
			previousLeaves = nextNodes;
		}
	}
	
	public void shiftIndex(int start) {
		/*
		 * Offsets the indices of policy nodes by the given arg 
		 */
		for (PolicyNode node : this.policyNodes) {
			
			/* replace for node */
			node.setId(node.id + start);
			
			/* replace for children */
			for (List<String> obs : node.nextNode.keySet()) {
				node.nextNode.replace(obs, node.nextNode.get(obs) + start);
			}
		} /* for this.policyNodes */
	}
	
	private int getNextId() {
		/*
		 * Get the next unique int ID for policy Nodes.
		 */
		this.idCounter++;
		return this.idCounter;
	}
	
	public List<String> getObsVarSequence() {
		/*
		 * Returns the observation variable names in sequence
		 * 
		 * The DDMaker implementation needs a variable ordering while making DDTree objects.
		 * This method can be useful while making a DD for policy node transitions. 
		 */
		
		List<String> obsSeq = 
				Arrays.asList(this.pomdp.obsVars).stream()
					.map(v -> v.name)
					.collect(Collectors.toList());
		
		return obsSeq;
	}
	
	public void deleteRedundantDDs() {
		/*
		 * Nulls out the belief DD in each PolicyNode to save space.
		 * 
		 * Lower level belief DDs should not be used anyway after the policy tree / belief tree is
		 * constructed.
		 */
		this.logger.fine("Setting lower level beliefs to NULL");
		this.policyNodes.forEach(n -> n.belief = null);
	}
	
}
