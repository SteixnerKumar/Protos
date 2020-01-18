/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.representations.modelrepresentations;

import java.io.Serializable;
import java.util.List;

import org.apache.log4j.Logger;

import thinclab.ddinterface.DDMaker;
import thinclab.ddinterface.DDTree;
import thinclab.legacy.DD;
import thinclab.legacy.StateVar;
import thinclab.solvers.BaseSolver;

/*
 * @author adityas
 *
 */
public class FactoredMj implements Serializable, LowerLevelModel {
	
	/*
	 * Defines factored Mj (Bj x Thetaj) for L1 models
	 */
	
	/* Store references to different solved frames */
	private List<BaseSolver> lowerFrames;
	
	private static final long serialVersionUID = 89199883532398250L;
	
	public static final Logger LOGGER = Logger.getLogger(FactoredMj.class);
	
	// -----------------------------------------------------------------------------------------
	
	public FactoredMj(List<BaseSolver> solvers, int lookAhead) {
		
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
