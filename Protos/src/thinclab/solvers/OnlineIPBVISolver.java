/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.solvers;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

import thinclab.belief.BeliefRegionExpansionStrategy;
import thinclab.decisionprocesses.DecisionProcess;
import thinclab.decisionprocesses.IPOMDP;
import thinclab.exceptions.VariableNotFoundException;
import thinclab.exceptions.ZeroProbabilityObsException;
import thinclab.legacy.AlphaVector;
import thinclab.legacy.DD;
import thinclab.legacy.Global;
import thinclab.legacy.OP;

/*
 * @author adityas
 *
 */
public class OnlineIPBVISolver extends OnlineSolver {

	/*
	 * Implements interactive point based value iteration for solving IPOMDPs
	 */

	private static final long serialVersionUID = 4278938278257692053L;

	/* Variables to hold point based values */
	double[][] currentPointBasedValues;
	double[][] newPointBasedValues;
	double bestImprovement;
	double worstDecline;

	/* Variables to hold AlphaVectors */
	DD[] alphaVectors;
	AlphaVector[] newAlphaVectors;
	int numNewAlphaVectors;

	/* policy holders */
	int[] policy;
	double[] policyvalue;
	boolean[] uniquePolicy;

	/* Store local reference to the IPOMDP */
	IPOMDP ipomdp;

	/* IPBVI hyper params */
	int maxRounds;
	int dpBackups;

	private static final Logger logger = Logger.getLogger(OnlineIPBVISolver.class);

	// -----------------------------------------------------------------------------------------

	public OnlineIPBVISolver(
			IPOMDP ipomdp, 
			BeliefRegionExpansionStrategy b, 
			int maxRounds, 
			int dpBackups) {

		/* Call super */
		super(ipomdp, b);

		/* get rounds and dp backups */
		this.ipomdp = ipomdp;
		this.maxRounds = maxRounds;
		this.dpBackups = dpBackups;

		/* initialize unique policy */
		this.uniquePolicy = new boolean[this.ipomdp.getActions().size()];
	}

	// -------------------------------------------------------------------------------------------
	
	@Override
	public void setFramework(DecisionProcess ipomdp) {
		/*
		 * Replaces the IPOMDP reference with given arg
		 */
		this.ipomdp = (IPOMDP) ipomdp;
		super.setFramework(ipomdp);
	}

	@Override
	public void solveForBeliefs(List<DD> beliefs) {
		/*
		 * Use online IPBVI to get solution for given beliefs
		 */

		logger.debug("Solving for " + beliefs.size() + " belief points.");

		DD[][] factoredBeliefRegion = this.f.factorBeliefRegion(beliefs);

		/* Make a default alphaVectors as rewards to start with */
		this.alphaVectors = 
				this.ipomdp.currentRi.values().stream()
					.map(a -> OP.reorder(a))
					.collect(Collectors.toList())
					.toArray(new DD[this.ipomdp.currentRi.size()]);

		/* try running IPBVI */
		try {

			for (int r = 0; r < this.maxRounds; r++) {

				this.IPBVI(100, r * this.dpBackups, this.dpBackups, factoredBeliefRegion, beliefs);

			}
			
			this.currentPointBasedValues = null;
			this.newPointBasedValues = null;
		}

		catch (Exception e) {
			logger.error("While running solver: " + e.getMessage());
			e.printStackTrace();
			System.exit(-1);
		}
	}

	@Override
	public void nextStep(String action, List<String> obs) throws ZeroProbabilityObsException {

		try {
			/*
			 * Transition the IPOMDP to a new belief by taking the specified action and
			 * observing obs.
			 */
			this.ipomdp.step(
					this.ipomdp.getCurrentBelief(), 
					action, obs.toArray(new String[obs.size()]));

			/* Reset the search to new initial beliefs */
			this.expansionStrategy.resetToNewInitialBelief();
		}

		catch (VariableNotFoundException e) {
			logger.error("While taking action " 
					+ action + " and observing " 
					+ obs + " got error: " + e.getMessage());
			System.exit(-1);
		}
	}

	public void computeMaxMinImprovement(DD[][] beliefRegion) {

		double imp;
		this.bestImprovement = Double.NEGATIVE_INFINITY;
		this.worstDecline = Double.POSITIVE_INFINITY;

		for (int j = 0; j < beliefRegion.length; j++) {
			/*
			 * find biggest improvement at this belief point
			 */
			imp = OP.max(this.newPointBasedValues[j], this.numNewAlphaVectors)
					- OP.max(this.currentPointBasedValues[j]);

			if (imp > this.bestImprovement)
				this.bestImprovement = imp;
			if (imp < this.worstDecline)
				this.worstDecline = imp;
		}
	}
	
	@Override
	public boolean hasSolution() {
		
		return (this.alphaVectors != null);
	}

	@Override
	public String getActionAtCurrentBelief() {

		return this.getActionForBelief(this.ipomdp.getCurrentBelief());
	}
	
	@Override
	public String getActionForBelief(DD belief) {
		/*
		 * Return best action at given belief
		 */
		return DecisionProcess.getActionFromPolicy(
				this.f, belief, this.alphaVectors, this.policy);
	}

	// --------------------------------------------------------------------------------------------

	@SuppressWarnings("unused")
	public void IPBVI(
			int maxAlpha, int firstStep, int nSteps, DD[][] beliefRegion, List<DD> beliefs)
			throws ZeroProbabilityObsException, VariableNotFoundException {

		double bellmanErr;
		double[] onezero = { 0 };
		double steptolerance = 0.0;

		int maxAlphaSetSize = maxAlpha;

		bellmanErr = 20 * this.ipomdp.tolerance;

		this.currentPointBasedValues = 
				OP.factoredExpectationSparseNoMem(beliefRegion, this.alphaVectors);

		DD[] primedV;
		double maxAbsVal = 0;

		for (int stepId = firstStep; stepId < firstStep + nSteps; stepId++) {

			steptolerance = ipomdp.tolerance;

			primedV = new DD[this.alphaVectors.length];

			for (int i = 0; i < this.alphaVectors.length; i++) {
				primedV[i] = 
						OP.primeVars(this.alphaVectors[i], ipomdp.S.size() + ipomdp.Omega.size());
			}

			maxAbsVal = 
					Math.max(
							OP.maxabs(
									IPOMDP.concatenateArray(
											OP.maxAllN(this.alphaVectors), 
											OP.minAllN(this.alphaVectors))), 
							1e-10);

			int nDpBackups = 0;

			/*
			 * could be one more than the maximum number at most
			 */
			this.newAlphaVectors = new AlphaVector[maxAlphaSetSize + 1];
			this.newPointBasedValues = new double[beliefRegion.length][maxAlphaSetSize + 1];
			this.numNewAlphaVectors = 0;

			AlphaVector newVector;

			double[] newValues;
			double improvement;

			/*
			 * we allow the number of new alpha vectors to get one bigger than the maximum
			 * allowed size, since we may be able to cull more than one alpha vector when
			 * trimming, bringing us back below the cutoff
			 */

			for (int i = 0; i < beliefRegion.length; i++) {
				Global.newHashtables();

				/* dpBackup */
				newVector = 
						AlphaVector.dpBackup(
								this.ipomdp, 
								beliefs.get(i), 
								beliefRegion[i],
								primedV, 
								maxAbsVal, 
								this.alphaVectors.length);

				newVector.alphaVector = OP.approximate(newVector.alphaVector,
						bellmanErr * (1 - this.ipomdp.discFact) / 2.0, onezero);
				newVector.setWitness(i);

				nDpBackups = nDpBackups + 1;

				/* merge and trim */
				newValues = OP.factoredExpectationSparseNoMem(beliefRegion, newVector.alphaVector);

				if (this.numNewAlphaVectors < 1)
					improvement = Double.POSITIVE_INFINITY;

				else
					improvement = 
						OP.max(
								OP.sub(
										newValues, 
										OP.getMax(
												this.newPointBasedValues, 
												this.numNewAlphaVectors)));

				if (improvement > ipomdp.tolerance) {
					for (int belid = 0; belid < beliefRegion.length; belid++)
						this.newPointBasedValues[belid][this.numNewAlphaVectors] = newValues[belid];

					this.newAlphaVectors[this.numNewAlphaVectors] = newVector;
					this.numNewAlphaVectors++;
				}
			}

			computeMaxMinImprovement(beliefRegion);

			/* save data and copy over new to old */
			this.alphaVectors = new DD[this.numNewAlphaVectors];
			this.currentPointBasedValues = 
					new double[this.newPointBasedValues.length][this.numNewAlphaVectors];

			this.policy = new int[this.numNewAlphaVectors];
			this.policyvalue = new double[this.numNewAlphaVectors];

			for (int j = 0; j < ipomdp.Ai.size(); j++)
				this.uniquePolicy[j] = false;

			for (int j = 0; j < this.numNewAlphaVectors; j++) {

				this.alphaVectors[j] = this.newAlphaVectors[j].alphaVector;
				this.policy[j] = this.newAlphaVectors[j].actId;
				this.policyvalue[j] = this.newAlphaVectors[j].value;
				this.uniquePolicy[this.policy[j]] = true;
			}

			for (int j = 0; j < beliefRegion.length; j++) {
				System.arraycopy(
						this.newPointBasedValues[j], 
						0, this.currentPointBasedValues[j], 
						0, this.numNewAlphaVectors);
			}

			bellmanErr = Math.min(10, Math.max(this.bestImprovement, -this.worstDecline));
			float errorVar = this.getErrorVariance((float) bellmanErr);
			
			logger.info("I: " + stepId 
					+ "\tB ERROR: " + String.format(Locale.US, "%.03f", bellmanErr) 
					+ "\tUSED/TOTAL BELIEFS: " + nDpBackups 
					+ "/" + beliefRegion.length 
					+ "\tA VECTORS: " + this.alphaVectors.length);

			if (stepId % 100 < 1)
				continue;

			if (bellmanErr < 0.01) {
				logger.warn("BELLMAN ERROR LESS THAN 0.01. COVERGENCE! SOFTWARE VERSION 7.0... "
						+ "LOOKING AT LIFE THROUGH THE EYES OF A TIRED HEART.");
				break;
			}

			if (stepId > 20 && errorVar < 0.0001) {
				logger.warn("DECLARING APPROXIMATE CONVERGENCE AT ERROR: " + bellmanErr
						+ " BECAUSE OF LOW ERROR VARIANCE " + errorVar);
				break;
			}

		}

	}

}
