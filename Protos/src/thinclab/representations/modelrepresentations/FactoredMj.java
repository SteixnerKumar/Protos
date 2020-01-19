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
import java.util.List;

import org.apache.log4j.Logger;

import thinclab.ddinterface.DDMaker;
import thinclab.ddinterface.DDTree;
import thinclab.legacy.DD;
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
				BeliefNode node = new BeliefNode(frame, init);
				int id = this.populateInternalMaps(node, frame);
				
				if (!nextStepRoots.contains(id)) nextStepRoots.add(id);
			}
			
		}
	}
	
	// -----------------------------------------------------------------------------------------
	
	public void buildTree() {
		/*
		 * Builds the full OnlinePolicyTree upto maxT
		 */
		
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

	@Override
	public StateVar getOpponentModelStateVar(int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DD getAjGivenMj(DDMaker ddMaker, List<String> Aj) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DD getPMjPGivenMjOjPAj(DDMaker ddMaker, List<String> Aj, List<String> OjNames) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public DDTree getMjInitBelief(DDMaker ddMaker, DDTree prior) {
		// TODO Auto-generated method stub
		return null;
	}

}
