/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.policy;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import thinclab.legacy.DD;
import thinclab.utils.Tuple;

/*
 * @author adityas
 *
 */
public class AlphaVectorPolicy<B extends DD> implements Policy<B> {

	public List<Tuple<Integer, B>> aVecs;

	public AlphaVectorPolicy(List<Tuple<Integer, B>> alphaVectors) {

		this.aVecs = alphaVectors;
	}

	@Override
	public int getBestActionIndex(B belief) {

		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getBestAction(B belief) {

		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public String toString() {
		return aVecs.toString();
	}

	public static <B extends DD> AlphaVectorPolicy<B> fromR(List<B> R) {

		return new AlphaVectorPolicy<B>(
				IntStream.range(0, R.size()).mapToObj(i -> Tuple.of(i, R.get(i))).collect(Collectors.toList()));
	}

}