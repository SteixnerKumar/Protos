/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.representations.modelrepresentations;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

import thinclab.ddinterface.DDMaker;
import thinclab.ddinterface.DDTree;
import thinclab.ddinterface.DDTreeLeaf;
import thinclab.legacy.DD;
import thinclab.legacy.OP;
import thinclab.legacy.StateVar;
import thinclab.representations.StructuredTree;
import thinclab.solvers.BaseSolver;

/*
 * @author adityas
 *
 */
public class FactoredMj extends StructuredTree implements Serializable, LowerLevelModel {
	
	/*
	 * Defines factored Mj (Bj x Thetaj) for L1 models
	 */
	
	/* Store references to different solved frames */
	private List<BaseSolver> lowerFrames = new ArrayList<BaseSolver>();
	private int H = -1;
	private int T = 0;
	
	/* for leaves to expand next */
	private List<Integer> nextStepRoots = new ArrayList<Integer>();
	
	private List<String> allLowerLevelActions = new ArrayList<String>();
	
	private static final long serialVersionUID = 89199883532398250L;
	
	public static final Logger LOGGER = Logger.getLogger(FactoredMj.class);
	
	// -----------------------------------------------------------------------------------------
	
	public FactoredMj(List<BaseSolver> solvers, int lookAhead) {
		
		this.lowerFrames.addAll(solvers);
		this.H = lookAhead;
		
		/* populate observations list */
		this.observations = new ArrayList<List<String>>();
		this.observations.addAll(this.lowerFrames.get(0).f.getAllPossibleObservations());
		
		/* populate actions list */
		this.allLowerLevelActions.clear();
		this.allLowerLevelActions.addAll(this.lowerFrames.get(0).f.getActions());
		
		/* initialize starting nodes */
		for (BaseSolver frame : this.lowerFrames) {
			
			/* make initial node */
			frame.f.setGlobals();
			
			for (DD init: frame.f.getInitialBeliefs()) {
				
				/* make new node */
				BeliefNode node = new BeliefNode(this.lowerFrames, init);
				int id = this.populateInternalMaps(node);
				
				if (!nextStepRoots.contains(id)) nextStepRoots.add(id);
			}
			
		}
		
		LOGGER.debug("Factored MJ initialized");

		this.buildTree();
		LOGGER.debug("Factored MJ has " + this.idToNodeMap.size() + " models");
	}
	
	// -----------------------------------------------------------------------------------------
	
	public void buildTree() {
		/*
		 * Builds the full OnlinePolicyTree upto maxT
		 */
		
		this.nextStepRoots.forEach(r -> this.idToNodeMap.get(r).setStartNode());
		
		List<Integer> prevNodes = new ArrayList<Integer>();
		prevNodes.addAll(this.nextStepRoots);
		
		/*
		 * if one step look ahead, don't add the next leaves to models
		 * but store them for computing the next step
		 */
		if (this.H == 1) {
			this.nextStepRoots.clear();
			this.nextStepRoots.addAll(this.expandFromPreviousNodes(prevNodes));
		}
		
		else {
			for (int t = 1; t < this.H; t++) {
				
				List<Integer> nextNodes = this.expandFromPreviousNodes(prevNodes);
				prevNodes = nextNodes;
				
				if (t == 1) {
					this.nextStepRoots.clear();
					this.nextStepRoots.addAll(prevNodes);
				}
			}
		}
	}
	
	public List<Integer> expandFromPreviousNodes(List<Integer> previousNodes) {
		/*
		 * Compute the next PolicyNode from the list of previous PolicyNodes
		 */
		
		List<Integer> nextLevelLeaves = new ArrayList<Integer>();
		
		/* For each previous Node */
		for (int parentId : previousNodes) {
			
			BeliefNode node = (BeliefNode) this.idToNodeMap.get(parentId);
			
			/* For all combinations */
			for (List<String> obs : this.observations) {
				
				for (String action : this.allLowerLevelActions) {
					this.expandFromPreviousNode(node, action, obs, nextLevelLeaves);
				} /* for all actions */
			} /* for all observations */
		} /* for all parents */
		
		return new ArrayList<Integer>(nextLevelLeaves);
	}
	
	// -----------------------------------------------------------------------------------------
	
	public DD getPAjGivenMjThetaj(DDMaker ddMaker) {
		/*
		 * Constructs P(Aj | Mj, Thetaj)
		 */
		
		List<String[]> triples = new ArrayList<String[]>();
		
		for (int id: this.idToNodeMap.keySet()) {
			
			BeliefNode node = (BeliefNode) this.idToNodeMap.get(id); 
			
			for (String frameName: node.getFrames().keySet()) {
				
				List<String> edge = new ArrayList<String>();
				
				/* add belief */
				edge.add("b_" + id);
				
				/* add frame */
				edge.add(frameName);
				
				/* add optimal action */
				edge.add(node.getOptimalActions().get(frameName));
				
				/* prob 1 for optimal action */
				edge.add("1.0");
				
				/* record the triple as a DD row */
				triples.add(edge.stream().toArray(String[]::new));
			}
		}
		
		DDTree PAjGivenMjThetaj = 
				ddMaker.getDDTreeFromSequence(
						new String[] {"M_j", "Theta_j", "A_j"},
						triples.stream().toArray(String[][]::new));
		
		return OP.reorder(PAjGivenMjThetaj.toDD());
	}
	
	public DD getPMjPGivenMjThetajOjPAj(DDMaker ddMaker, List<String> OjNames) {
		
		/*
		 * Makes P(Bj' | Bj, Aj, Thetaj, Oj')
		 */
		
		List<String[]> PBjPGivenBjAjThetajOjPRows = new ArrayList<String[]>();
		
		for (int id: this.edgeMap.keySet()) {
			
			HashMap<List<String>, Integer> row = this.edgeMap.get(id);
			
			for (List<String> condition: row.keySet()) {
				List<String> triple = new ArrayList<String>();
				
				/* add root */
				triple.add("b_" + id);
				
				/* add condition */
				triple.addAll(condition);
				
				/* add leaf */
				triple.add("b_" + row.get(condition));
				
				/* add 1.0 prob */
				triple.add("1.0");
				
				/* record row*/
				PBjPGivenBjAjThetajOjPRows.add(triple.stream().toArray(String[]::new));
			}
		}
		
		List<String> varSequence = new ArrayList<String>();
		varSequence.add("M_j");
		varSequence.add("Theta_j");
		varSequence.add("A_j");
		varSequence.addAll(OjNames.stream().map(s -> s + "'").collect(Collectors.toList()));
		varSequence.add("M_j'");
		
		DDTree PBjPGivenBjAjThetajOjPDDTree = 
				ddMaker.getDDTreeFromSequence(
						varSequence.stream().toArray(String[]::new), 
						PBjPGivenBjAjThetajOjPRows.stream().toArray(String[][]::new));
		
		/* add 1.0 probability to loop to self for terminal nodes */
		for (String child: PBjPGivenBjAjThetajOjPDDTree.children.keySet()) {
			
			if (!this.edgeMap.containsKey(Integer.parseInt(child.split("_")[1]))) {
				
				try {
					
					DDTree terminal = ddMaker.getDDTreeFromSequence(new String[] {"M_j'"});
					terminal.setDDAt(child, new DDTreeLeaf(1.0));
					
					PBjPGivenBjAjThetajOjPDDTree.setDDAt(child, terminal);
				} 
				
				catch (Exception e) {
					LOGGER.error("While looping " + child + " to self");
					e.printStackTrace();
					System.exit(-1);
				}
			}
		}
		
		return OP.reorder(PBjPGivenBjAjThetajOjPDDTree.toDD());
	}
	
	public DDTree getInitBelief(DDMaker ddMaker, DDTree prior) {
		/*
		 * Constructs an initial belief DDTree based on the current roots
		 */
		
		LOGGER.debug("Making initial belief");
		DDTree beliefMj = ddMaker.getDDTreeFromSequence(new String[] {"M_j"});
		
		if (prior == null) {
			
			int mjCount = 0;
			
			mjCount +=
				this.idToNodeMap.values().stream()
					.filter(n -> n.startNode)
					.map(i -> i.id)
					.collect(Collectors.toList()).size();
				
			List<Integer> roots = 
					this.idToNodeMap.values().stream()
						.filter(n -> n.startNode)
						.map(i -> i.id)
						.collect(Collectors.toList());
			
			/* Uniform distribution over all current roots */
			for (int node : roots) {
				
				try {
					beliefMj.setValueAt(
							"b_" + node, 
							(1.0 / mjCount));
				} 
				
				catch (Exception e) {
					LOGGER.error("While making initial Mj: " + e.getMessage());
					e.printStackTrace();
				}
			}
			
		}
		
		/* else use previous belief values */
		else {
			
			for (Entry<String, DDTree> entry : prior.children.entrySet()) {
				
				DDTree child = entry.getValue();
				
				/* add all non leaf vars */
				if (!child.varName.contentEquals("LeafVar"))
					beliefMj.addChild(entry.getKey(), child);
				
				else if (child.varName.contentEquals("LeafVar") 
						&& ((DDTreeLeaf) child).val != 0.0)
					beliefMj.addChild(entry.getKey(), child);
			}
			
		}
		
		LOGGER.debug("Made initial belief");
		return beliefMj;
	}
	
	public void step(HashSet<String> nonZeroMj) {
		/*
		 * Moves to the next time step
		 */
		
		this.nextStepRoots = 
				this.nextStepRoots.stream()
					.filter(r -> nonZeroMj.contains("b_" + r))
					.collect(Collectors.toList());
		
		this.T += 1;
		LOGGER.info("Mj currently tracking time step " + this.T);
		
		this.buildTree();
	}
	
	public String getOptimalActionAtNode(String node, String frame) {
		/*
		 * Returns j's optimal action at the belief point at node
		 */
		
		return ((BeliefNode) this.idToNodeMap.get(
						FactoredMj.getBeliefIDFromModel(node))).getOptimalActions().get(frame);
	}
	
	public static int getBeliefIDFromModel(String beliefLabel) {
		/*
		 * Splits the model label to find out frameID
		 */
		return Integer.valueOf(beliefLabel.split("_")[1]);
	}
	
	public String getBeliefTextAtNode(String node) {
		/*
		 * Returns j's beliefs at node
		 * 
		 * Note that this method only returns the string representation and not the actual
		 * usable belief
		 */
		
		return this.idToNodeMap.get(FactoredMj.getBeliefIDFromModel(node)).sBelief;
	}
	
	// -----------------------------------------------------------------------------------------

	@Override
	public StateVar getOpponentModelStateVar(int index) {
		/*
		 * Collect all possible models from lower level MJs and accumulate into a
		 * variable
		 */

		List<String> nodeNamesList = new ArrayList<String>();

		nodeNamesList.addAll(this.idToNodeMap.keySet().stream()
				.map(i -> "b_" + i).collect(Collectors.toList()));

		String[] nodeNames = nodeNamesList.toArray(new String[this.idToNodeMap.size()]);

		return new StateVar("M_j", index, nodeNames);
	}

	@Override
	public DD getAjGivenMj(DDMaker ddMaker, List<String> Aj) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DD getPMjPGivenMjOjPAj(DDMaker ddMaker, List<String> Aj, List<String> OjNames) {
		
		/*
		 * Makes P(Bj' | Bj, Aj, Thetaj, Oj')
		 */
		
		
		return null;
	}

	@Override
	public DDTree getMjInitBelief(DDMaker ddMaker, DDTree prior) {
		// TODO Auto-generated method stub
		return null;
	}

}
