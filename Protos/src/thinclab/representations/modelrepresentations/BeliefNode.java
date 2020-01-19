/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.representations.modelrepresentations;

import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import thinclab.legacy.DD;
import thinclab.representations.policyrepresentations.PolicyNode;
import thinclab.solvers.BaseSolver;

/*
 * @author adityas
 *
 */
public class BeliefNode extends PolicyNode {
	
	/*
	 * Represents a single Bj which may belong to multiple frames
	 */
	
	private HashMap<String, String> actions = new HashMap<String, String>();
	private HashMap<String, BaseSolver> solvedFrames = new HashMap<String, BaseSolver>();
	
	private static final long serialVersionUID = -472828835095723833L;
	private static final Logger LOGGER = Logger.getLogger(BeliefNode.class);
	
	// -----------------------------------------------------------------------------------------
	
	public BeliefNode(BaseSolver solvedFrame, DD belief) {
		
		solvedFrame.f.setGlobals();
		this.solvedFrames.put("theta/" + solvedFrame.f.frameID, solvedFrame);
		this.actions.put(
				"theta/" + solvedFrame.f.frameID, 
				solvedFrame.getActionForBelief(belief));
		
		this.belief = belief;
		this.beliefDDTree = belief.toDDTree();
		this.sBelief = solvedFrame.f.getBeliefString(this.belief);
	}
	
	public BeliefNode(List<BaseSolver> solvedFrames, DD belief) {
		/*
		 * For the belief node to be valid in multiple frames, the DD representing the
		 * belief has to be valid in all the frames
		 */

		/* store frames and optimal actions */
		for (BaseSolver frame: solvedFrames) {
			frame.f.setGlobals();
			
			/* store frame ref */
			this.solvedFrames.put("theta/" + frame.f.frameID, frame);
			
			this.actions.put("theta/" + frame.f.frameID, frame.getActionForBelief(belief));
			
			if (this.sBelief.length() < 1)
				this.sBelief = frame.f.getBeliefString(belief);
		}
		
		/* store belief DDTree */
		this.belief = belief;
		this.beliefDDTree = belief.toDDTree();
	}
	
	// -----------------------------------------------------------------------------------------
	
	public void setId(int id) {
		this.id = id;
	}
	
	public void addFrame(BaseSolver frame) {
		
		if (this.solvedFrames.containsKey("theta/" + frame.f.frameID)) {
			LOGGER.warn("Belief Node already contains frame ref for " + frame.f.frameID);
			LOGGER.warn("Replacing older ref. This shouldn't be happening.");
		}
		
		frame.f.setGlobals();
		this.solvedFrames.put("theta/" + frame.f.frameID, frame);
		this.actions.put("theta/" + frame.f.frameID, frame.getActionForBelief(this.belief));
	}
	
	// -----------------------------------------------------------------------------------------
	
	public HashMap<String, BaseSolver> getFrames() {
		return this.solvedFrames;
	}
	
	public HashMap<String, String> getOptimalActions() {
		return this.actions;
	}
	
	public boolean isInFrame(String frame) {
		return this.solvedFrames.containsKey(frame);
	}
}
