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
import thinclab.DDOP;
import thinclab.legacy.DD;
import thinclab.utils.Tuple;

/*
 * @author adityas
 *
 */
public class AlphaVectorPolicy implements Policy<DD> {

	public List<Tuple<Integer, DD>> aVecs;

	public AlphaVectorPolicy(List<Tuple<Integer, DD>> alphaVectors) {

		this.aVecs = alphaVectors;
	}

	@Override
	public int getBestActionIndex(DD belief, List<Integer> S) {

		int i = DDOP.bestAlphaIndex(aVecs, belief, S);
		return aVecs.get(i)._0();
	}

	@Override
	public String toString() {
		return aVecs.toString();
	}

	public static AlphaVectorPolicy fromR(List<DD> R) {

		return new AlphaVectorPolicy(
				IntStream.range(0, R.size()).mapToObj(i -> Tuple.of(i, R.get(i))).collect(Collectors.toList()));
	}

}
