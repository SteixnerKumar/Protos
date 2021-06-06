/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab;

import java.util.List;

/*
 * @author adityas
 *
 */
public class RandomVariable {
	
	private String varName;
	private List<String> valNames;
	
	public RandomVariable(String varName, List<String> valNames) {
		this.varName = varName;
		this.valNames = valNames;
	}
	
	public String getVarName() {
		return this.varName;
	}
	
	public List<String> getValNames() {
		return this.valNames;
	}
}
