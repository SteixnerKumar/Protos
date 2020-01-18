package thinclab.representations.policyrepresentations;

import java.io.Serializable;

import thinclab.ddinterface.DDTree;
import thinclab.decisionprocesses.DecisionProcess;
import thinclab.legacy.DD;
import thinclab.solvers.BaseSolver;

public class PolicyNode implements Serializable {
	
	private static final long serialVersionUID = -5523013082898050215L;
	
	int alphaId=-1;
	int actId = 1;
	
	public String actName = "";
	public String sBelief = "";
	public DD belief;
	public DDTree beliefDDTree;

	public int id = -1;
	public int H = -1;

	public boolean startNode = false;
	public BaseSolver S;
	
	// ------------------------------------------------------------------------------------
	/*
	 * Constructors
	 */
	
	public PolicyNode() {
		
	}
	
	public PolicyNode(BaseSolver S, DD belief) {
		/*
		 * For using policynodes with new StructuredTree API 
		 */
		
		this.S = S;
		this.belief = belief;
		this.sBelief = this.S.f.getBeliefString(belief);
		this.beliefDDTree = this.belief.toDDTree();
		this.actName = this.S.getActionForBelief(belief);
	}
	
	public PolicyNode(int id, int timeStep, String sBelief, String action) {
		/*
		 * Constructor for using PolicyNode objects as place holders
		 * 
		 * Currently being used with BeliefGraph backend in OpponentModel objects
		 */
		this.id = id;
		this.H = timeStep;
		this.sBelief = sBelief;
		this.actName = action;
	}
	
	// ------------------------------------------------------------------------------------
	
	public void setId(int id) {
		/*
		 * Setter for id
		 */
		this.id = id;
	}
	
	public void setStartNode() {
		/*
		 * Marks the node as a start node
		 */
		this.startNode = true;
	}
	
	public BaseSolver getSolver() {
		return this.S;
	}
	
	public DecisionProcess getFramework() {
		return this.getSolver().f;
	}
	
	public DD getBelief() {
		return belief;
	}
	
	public DDTree getBeliefAsDDTree() {
		return this.beliefDDTree;
	}
	
	// -------------------------------------------------------------------------------------

	@Override
	public String toString() {
		return "PolicyNode \t [ID = " + this.id
				+ " \t level=" + this.H 
				+ " \t action=" + this.actName
				+ " \t belief=" + this.sBelief + "]\r\n";
	}

}
