/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import thinclab.legacy.Config;
import thinclab.legacy.DD;
import thinclab.legacy.DDleaf;
import thinclab.legacy.DDnode;
import thinclab.legacy.Global;
import thinclab.utils.Tuple;

/*
 * @author adityas
 *
 */
public class DDOP {

	/*
	 * I have been forced to not use functional patterns here to manage the
	 * performance tradeoff. This is the critical section of the library on which
	 * everything is built. Even a slight increase in execution time has drastic
	 * consequences. Also, I absolutely hate java for this.
	 */

	private static final Logger LOGGER = LogManager.getLogger(DDOP.class);

	// --------------------------------------------------------------------------------------------
	// Arith ops

	public static DD mult(DD dd1, DD dd2) {

		// dd1 precedes dd2
		if (dd1.getVar() > dd2.getVar()) {

			if (dd2.getVar() == 0 && dd2.getVal() == 0)
				return dd2;
			else if (dd2.getVar() == 0 && dd2.getVal() == 1 && dd2.getConfig() == null)
				return dd1;

			var _dds = Tuple.of(dd1, dd2);
			DD storedResult = (DD) Global.multCache.get(_dds);
			if (storedResult != null)
				return storedResult;

			DD children[];
			children = new DD[dd1.getChildren().length];
			for (int i = 0; i < dd1.getChildren().length; i++) {

				children[i] = DDOP.mult(dd1.getChildren()[i], dd2);
			}
			DD result = DDnode.getDD(dd1.getVar(), children);
			Global.multCache.put(_dds, result);
			return result;
		}

		// dd2 precedes dd1 {
		else if (dd2.getVar() > dd1.getVar()) {

			if (dd1.getVar() == 0 && dd1.getVal() == 0)
				return dd1;
			else if (dd1.getVar() == 0 && dd1.getVal() == 0 && dd1.getConfig() == null)
				return dd2;

			var _dds = Tuple.of(dd1, dd2);
			DD storedResult = (DD) Global.multCache.get(_dds);
			if (storedResult != null)
				return storedResult;

			DD children[];
			children = new DD[dd2.getChildren().length];
			for (int i = 0; i < dd2.getChildren().length; i++) {

				children[i] = DDOP.mult(dd2.getChildren()[i], dd1);
			}
			DD result = DDnode.getDD(dd2.getVar(), children);
			Global.multCache.put(_dds, result);
			return result;
		}

		// dd2 and dd1 have same root var
		else if (dd1.getVar() > 0) {

			var _dds = Tuple.of(dd1, dd2);
			DD storedResult = (DD) Global.multCache.get(_dds);
			if (storedResult != null)
				return storedResult;

			DD children[];
			children = new DD[dd1.getChildren().length];
			for (int i = 0; i < dd1.getChildren().length; i++) {

				children[i] = DDOP.mult(dd1.getChildren()[i], dd2.getChildren()[i]);
			}
			DD result = DDnode.getDD(dd1.getVar(), children);
			Global.multCache.put(_dds, result);
			return result;
		}

		// dd1 and dd2 are leaves
		else {

			float newVal = dd1.getVal() * dd2.getVal();
			int[][] newConfig = Config.merge(dd1.getConfig(), dd2.getConfig());
			return DDleaf.getDD(newVal, newConfig);
		}
	}

	public static DD div(DD dd1, DD dd2) {

		return DDOP.mult(dd1, DDOP.inv(dd2));
	}

	public static DD mult(List<DD> dds) {

		DD ddProd = DD.one;
		for (int i = 0; i < dds.size(); i++) {

			ddProd = DDOP.mult(ddProd, dds.get(i));
		}
		return ddProd;
	}

	public static DD inv(DD dd) {

		// dd is a leaf
		if (dd.getVar() == 0)
			return DDleaf.getDD(1 / dd.getVal(), dd.getConfig());

		// dd is a node
		else {

			DD children[];
			children = new DD[dd.getChildren().length];
			for (int i = 0; i < dd.getChildren().length; i++) {

				children[i] = DDOP.inv(dd.getChildren()[i]);
			}
			return DDnode.getDD(dd.getVar(), children);
		}
	}

	public static DD add(DD dd1, DD dd2) {

		// dd1 precedes dd2
		if (dd1.getVar() > dd2.getVar()) {

			if (dd2.getVar() == 0 && dd2.getVal() == 0 && dd2.getConfig() == null)
				return dd1;

			var _dds = Tuple.of(dd1, dd2);
			DD storedResult = (DD) Global.addCache.get(_dds);
			if (storedResult != null)
				return storedResult;

			DD children[];
			children = new DD[dd1.getChildren().length];
			for (int i = 0; i < dd1.getChildren().length; i++) {

				children[i] = DDOP.add(dd1.getChildren()[i], dd2);
			}
			DD result = DDnode.getDD(dd1.getVar(), children);
			Global.addCache.put(_dds, result);
			return result;
		}

		// dd2 precedes dd1 {
		else if (dd2.getVar() > dd1.getVar()) {

			if (dd1.getVar() == 0 && dd1.getVal() == 0 && dd1.getConfig() == null)
				return dd2;

			var _dds = Tuple.of(dd1, dd2);
			DD storedResult = (DD) Global.addCache.get(_dds);
			if (storedResult != null)
				return storedResult;

			DD children[];
			children = new DD[dd2.getChildren().length];
			for (int i = 0; i < dd2.getChildren().length; i++) {

				children[i] = DDOP.add(dd2.getChildren()[i], dd1);
			}
			DD result = DDnode.getDD(dd2.getVar(), children);
			Global.addCache.put(_dds, result);
			return result;
		}

		// dd2 and dd1 have same root var
		else if (dd1.getVar() > 0) {

			var _dds = Tuple.of(dd1, dd2);
			DD storedResult = (DD) Global.addCache.get(_dds);
			if (storedResult != null)
				return storedResult;

			DD children[];
			children = new DD[dd1.getChildren().length];
			for (int i = 0; i < dd1.getChildren().length; i++) {

				children[i] = DDOP.add(dd1.getChildren()[i], dd2.getChildren()[i]);
			}
			DD result = DDnode.getDD(dd1.getVar(), children);
			Global.addCache.put(_dds, result);
			return result;
		}

		// dd1 and dd2 are leaves
		else {

			float newVal = dd1.getVal() + dd2.getVal();
			int[][] newConfig = Config.merge(dd1.getConfig(), dd2.getConfig());
			return DDleaf.getDD(newVal, newConfig);
		}
	}

	public static DD sub(DD dd1, DD dd2) {

		return DDOP.add(dd1, DDOP.neg(dd2));
	}

	public static DD add(List<DD> dds) {

		DD ddSum = DD.zero;

		for (int i = 0; i < dds.size(); i++) {

			ddSum = DDOP.add(ddSum, dds.get(i));
		}

		return ddSum;
	}

	public static DD abs(DD dd) {

		// dd is a leaf
		if (dd.getVar() == 0) {

			if (dd.getVal() >= 0)
				return dd;
			else
				return DDleaf.getDD(-dd.getVal(), dd.getConfig());
		}

		// dd is a node
		else {

			DD children[];
			children = new DD[dd.getChildren().length];
			for (int i = 0; i < dd.getChildren().length; i++) {

				children[i] = DDOP.abs(dd.getChildren()[i]);
			}
			return DDnode.getDD(dd.getVar(), children);
		}
	}

	public static DD neg(DD dd) {

		// dd is a leaf
		if (dd.getVar() == 0)
			return DDleaf.getDD(-dd.getVal(), dd.getConfig());

		// dd is a node
		else {

			DD children[];
			children = new DD[dd.getChildren().length];
			for (int i = 0; i < dd.getChildren().length; i++) {

				children[i] = DDOP.neg(dd.getChildren()[i]);
			}
			return DDnode.getDD(dd.getVar(), children);
		}
	}

	public static DD exp(DD dd) {

		// dd is a leaf
		if (dd.getVar() == 0)
			return DDleaf.getDD((float) Math.exp(dd.getVal()), dd.getConfig());

		// dd is a node
		else {

			DD children[];
			children = new DD[dd.getChildren().length];
			for (int i = 0; i < dd.getChildren().length; i++) {

				children[i] = DDOP.exp(dd.getChildren()[i]);
			}
			return DDnode.getDD(dd.getVar(), children);
		}
	}

	public static DD pow(DD dd, float pow) {

		// dd is a leaf
		if (dd.getVar() == 0)
			return DDleaf.getDD((float) Math.pow(dd.getVal(), pow), dd.getConfig());

		// dd is a node
		else {

			DD children[];
			children = new DD[dd.getChildren().length];
			for (int i = 0; i < dd.getChildren().length; i++) {

				children[i] = DDOP.pow(dd.getChildren()[i], pow);
			}
			return DDnode.getDD(dd.getVar(), children);
		}
	}

	public static List<DD> pow(List<DD> dds, float pow) {

		var newdds = new ArrayList<DD>(dds.size());
		for (int i = 0; i < dds.size(); i++)
			newdds.add(DDOP.pow(dds.get(i), pow));

		return newdds;
	}

	// --------------------------------------------------------------------------------------------
	// Add out

	public static int selectVarGreedily(List<DD> ddArray, List<Integer> vars) {

		// estimate cost of eliminating each var
		float bestSize = Float.POSITIVE_INFINITY;
		int bestVar = 0;
		for (int i = 0; i < vars.size(); i++) {

			var newVarSet = new HashSet<Integer>(5);
			float sizeEstimate = 1;
			int nAffectedDds = 0;
			for (int ddId = 0; ddId < ddArray.size(); ddId++) {

				var _ddVars = ddArray.get(ddId).getVars();
				if (_ddVars.contains(vars.get(i))) {

					newVarSet.addAll(_ddVars);
					sizeEstimate *= ddArray.get(ddId).getNumLeaves();
					nAffectedDds += 1;
				}
			}

			// # of affected DDs <= 1 or # of vars is <= 2
			if (nAffectedDds <= 1 || newVarSet.size() <= 2) {

				return vars.get(i);
			}

			// compute sizeUpperBound:
			// sizeUpperBound = min(sizeEstimate, prod(varDomSize(newScope)));
			float sizeUpperBound = 1;
			for (var _var : newVarSet) {

				sizeUpperBound *= Global.valNames.get(_var - 1).size();
				if (sizeUpperBound >= sizeEstimate)
					break;
			}

			if (sizeUpperBound < sizeEstimate)
				sizeEstimate = sizeUpperBound;

			// revise bestVar
			if (sizeUpperBound < bestSize) {

				bestSize = sizeUpperBound;
				bestVar = vars.get(i);
			}
		}

		return bestVar;
	}

	public static DD addMultVarElim(final Collection<DD> dds, final Collection<Integer> vars) {

		var _vars = new HashSet<Integer>(vars);
		var _dds = new ArrayList<DD>(dds);

		// check if any of the dds are zero
		for (int i = 0; i < _dds.size(); i++) {

			if (_dds.get(i).getVar() == 0 && _dds.get(i).getVal() == 0)
				return DD.zero;
		}

		// eliminate variables one by one
		while (!_vars.isEmpty()) {

			// eliminate deterministic variables
			boolean deterministic = true;
			while (deterministic && _vars.size() > 0) {

				deterministic = false;
				for (int ddId = 0; ddId < _dds.size(); ddId++) {

					var varIds = _dds.get(ddId).getVars();
					if (varIds.size() == 1 && _vars.containsAll(varIds)) {

						DD[] children = _dds.get(ddId).getChildren();
						int valId = -1;
						for (int childId = 0; childId < children.length; childId++) {

							float value = children[childId].getVal();
							if (value == 1 && !deterministic) {

								deterministic = true;
								valId = childId + 1;
							} else if ((value != 0 && value != 1) || (value == 1 && deterministic)) {

								deterministic = false;
								break;
							}
						}
						if (deterministic) {

							_vars.removeAll(varIds);
							_dds.remove(ddId);

							for (int i = 0; i < _dds.size(); i++) {

								if (_dds.get(i).getVars().containsAll(varIds))
									_dds.set(i, DDOP.restrict(_dds.get(i), new ArrayList<>(varIds), List.of(valId)));
							}
							break;
						}
					}
				}
			}

			if (_vars.isEmpty())
				break;

			// greedily choose var to eliminate
			int bestVar = DDOP.selectVarGreedily(_dds, new ArrayList<Integer>(_vars));

			// multiply together trees that depend on var
			DD newDd = DD.one;
			for (int ddId = 0; ddId < _dds.size(); ddId++) {

				var _dd = _dds.get(ddId);
				if (_dd.getVars().contains(bestVar)) {

					newDd = DDOP.mult(newDd, _dd);
					_dds.remove(ddId);
					ddId--;
				}
			}

			// sumout bestVar from newDd
			newDd = DDOP.addout(newDd, bestVar);
			if (newDd.getVar() == 0 && newDd.getVal() == 0)
				return DD.zero;

			// add new tree to dds
			_dds.add(newDd);

			// remove bestVar from vars
			_vars.remove(bestVar);
		}

		// multiply remaining trees and the newly added one; the resulting tree
		// is now free of any variable that appeared in vars

		var result = DDOP.mult(_dds);

		return result;
	}

	public static DD addout(DD dd, int var) {

		var cacheKey = Tuple.of(dd, var);
		if (Global.addOutCache.containsKey(cacheKey))
			return Global.addOutCache.get(cacheKey);

		HashMap<DD, DD> hashtable = new HashMap<>();
		var result = addout(dd, var, hashtable);

		Global.addOutCache.put(cacheKey, result);

		return result;
	}

	public static DD addout(DD dd, int var, HashMap<DD, DD> hashtable) {

		// it's a leaf
		if (dd.getVar() == 0) {

			return DDleaf.getDD(Global.varDomSize.get(var - 1) * dd.getVal(), dd.getConfig());
		}

		DD result = (DD) hashtable.get(dd);
		if (result != null)
			return result;

		// root is variable that must be eliminated
		if (dd.getVar() == var) {

			// have to collapse all children into a new node
			result = DDOP.add(Arrays.asList(dd.getChildren()));
		}

		// descend down the tree until 'var' is found
		else {

			DD children[];
			children = new DD[dd.getChildren().length];
			for (int i = 0; i < dd.getChildren().length; i++) {

				children[i] = DDOP.addout(dd.getChildren()[i], var);
			}
			result = DDnode.getDD(dd.getVar(), children);
		}

		// store result
		hashtable.put(dd, result);
		return result;
	}

	public static List<DD> factors(final DD dd, final List<Integer> vars) {

		var factordds = new ArrayList<DD>(vars.size());
		var _vars = new ArrayList<Integer>(vars);

		for (int i = 0; i < _vars.size(); i++) {

			var _var = _vars.remove(0);

			factordds.add(DDOP.addMultVarElim(List.of(dd), _vars));
			_vars.add(_var);
		}

		return factordds;
	}

	public static String toDotRecord(final DD dd, final List<Integer> vars) {

		var factordds = new ArrayList<DD>(vars.size());
		var _vars = new ArrayList<Integer>(vars);

		for (int i = 0; i < _vars.size(); i++) {

			var _var = _vars.remove(0);

			factordds.add(DDOP.addMultVarElim(List.of(dd), _vars));
			_vars.add(_var);
		}

		var builder = new StringBuilder();
		builder.append(" ")
				.append(String.join(" | ", factordds.stream().map(d -> d.toDot()).collect(Collectors.toList())))
				.append(" ");

		return builder.toString();
	}

	public static DD getFrameBelief(final DD b, final DD PThetajGivenMj, final int i_Mj, final List<Integer> vars) {

		int mjIndex = vars.indexOf(i_Mj);
		var _vars = new ArrayList<>(vars);

		if (mjIndex < 0) {

			LOGGER.error("While getting belief over frame, i_Mj %s is not in i_S %s", i_Mj, vars);
			System.exit(-1);
		}

		var _dontCare = _vars.remove(mjIndex);
		DD b_Mj = DDOP.addMultVarElim(List.of(b), _vars);
		DD b_Thetaj = DDOP.addMultVarElim(List.of(b_Mj, PThetajGivenMj), List.of(i_Mj));

		return b_Thetaj;
	}

	// -------------------------------------------------------------------------------------------------
	// restrict

	public static DD restrict(DD dd, final List<Integer> vars, final List<Integer> vals) {

		if (dd.getVar() == 0)
			return dd;

		int varIndex = vars.indexOf(dd.getVar());
		if (varIndex >= 0) {

			var _vars = new ArrayList<>(vars);
			var _vals = new ArrayList<>(vals);

			_vars.remove(varIndex);
			var _val = _vals.remove(varIndex);

			if (_vars.size() == 0)
				return dd.getChildren()[_val - 1];

			else
				return DDOP.restrict(dd.getChildren()[_val - 1], _vars, _vals);
		}

		DD[] children = new DD[dd.getChildren().length];
		for (int i = 0; i < children.length; i++) {

			children[i] = DDOP.restrict(dd.getChildren()[i], vars, vals);
		}

		return DDnode.getDD(dd.getVar(), children);
	}

	public static List<DD> restrict(List<DD> dds, final List<Integer> vars, final List<Integer> vals) {

		ArrayList<DD> result = new ArrayList<>(dds.size());

		for (int i = 0; i < dds.size(); i++) {

			result.add(DDOP.restrict(dds.get(i), vars, vals));
		}

		return result;
	}

	// --------------------------------------------------------------------------------------------------
	// rename / prime variables
	public static DD primeVars(DD dd, int n) {

		HashMap<DD, DD> hashtable = new HashMap<>(10);
		return DDOP.primeVars(dd, n, hashtable);
	}

	public static DD primeVars(DD dd, int n, HashMap<DD, DD> hashtable) {

		// dd is a leaf
		if (dd.getVar() == 0)
			return dd;

		// dd is a node
		else {

			DD result = (DD) hashtable.get(dd);
			if (result != null)
				return result;

			DD children[];
			children = new DD[dd.getChildren().length];
			for (int i = 0; i < dd.getChildren().length; i++) {

				children[i] = DDOP.primeVars(dd.getChildren()[i], n);
			}
			result = DDnode.getDD(dd.getVar() + n, children);
			hashtable.put(dd, result);
			return result;
		}
	}

	public static List<DD> primeVars(List<DD> dds, int n) {

		var primedDds = new ArrayList<DD>(dds.size());
		for (int i = 0; i < dds.size(); i++) {

			primedDds.add(DDOP.primeVars(dds.get(i), n));
		}
		return primedDds;
	}

	// ------------------------------------------------------------------------------------------------

	public static float dotProduct(DD dd1, DD dd2, Collection<Integer> vars) {

		if ((dd1.getVar() == 0 && dd1.getVal() == 0) || (dd2.getVar() == 0 && dd2.getVal() == 0))
			return 0;

		var _vars = new HashSet<Integer>(vars);
		var _computation = Tuple.of(dd1, dd2, _vars);

		var result = Global.dotProductCache.get(_computation);
		if (result != null)
			return result;

		// dd1 precedes dd2
		if (dd1.getVar() > dd2.getVar()) {

			_vars.remove(dd1.getVar());
			float dp = 0;
			for (int i = 0; i < dd1.getChildren().length; i++) {

				dp += DDOP.dotProduct(dd1.getChildren()[i], dd2, _vars);
			}

			Global.dotProductCache.put(_computation, dp);
			return dp;
		}

		// dd2 precedes dd1 {
		else if (dd2.getVar() > dd1.getVar()) {

			_vars.remove(dd2.getVar());
			float dp = 0;
			for (int i = 0; i < dd2.getChildren().length; i++) {

				dp += DDOP.dotProduct(dd2.getChildren()[i], dd1, _vars);
			}
			Global.dotProductCache.put(_computation, dp);
			return dp;
		}

		// dd2 and dd1 have same root var
		else if (dd1.getVar() > 0) {

			_vars.remove(dd1.getVar());
			float dp = 0;
			for (int i = 0; i < dd1.getChildren().length; i++) {

				dp += DDOP.dotProduct(dd1.getChildren()[i], dd2.getChildren()[i], _vars);
			}
			Global.dotProductCache.put(_computation, dp);
			return dp;
		}

		// dd1 and dd2 are leaves
		else {

			float _result = dd1.getVal() * dd2.getVal();
			for (var _v : _vars) {

				_result *= Global.valNames.get(_v - 1).size();
			}

			return _result;
		}
	}

	public static List<List<Float>> dotProduct(List<DD> dds1, List<DD> dds2, Collection<Integer> vars) {

		List<List<Float>> results = new ArrayList<>(dds1.size());
		for (int i = 0; i < dds1.size(); i++) {

			List<Float> _results = new ArrayList<>(dds2.size());

			for (int j = 0; j < dds2.size(); j++) {

				_results.add(DDOP.dotProduct(dds1.get(i), dds2.get(j), vars));
			}
			results.add(_results);
		}

		return results;
	}

	public static float value_b(List<Tuple<Integer, DD>> Vn, DD b, Collection<Integer> Svars) {

		float maxVal = Float.NEGATIVE_INFINITY;

		for (int i = 0; i < Vn.size(); i++) {

			float val = DDOP.dotProduct(b, Vn.get(i)._1(), Svars);

			if (val > maxVal)
				maxVal = val;
		}

		return maxVal;
	}

	public static int bestAlphaIndex(List<Tuple<Integer, DD>> Vn, DD b, Collection<Integer> Svars) {

		float maxVal = Float.NEGATIVE_INFINITY;
		int bestIndex = -1;

		for (int i = 0; i < Vn.size(); i++) {

			float val = DDOP.dotProduct(b, Vn.get(i)._1(), Svars);

			if (val > maxVal) {

				maxVal = val;
				bestIndex = i;
			}
		}

		return bestIndex;
	}

	public static <T> List<List<T>> cartesianProd(List<List<T>> a, List<List<T>> b) {

		var prod = a.stream()
				.map(x -> b.stream().map(y -> Stream.concat(x.stream(), y.stream()).collect(Collectors.toList()))
						.collect(Collectors.toList()))
				.flatMap(z -> z.stream()).collect(Collectors.toList());

		return prod;
	}

	public static <T> List<List<T>> cartesianProd(List<List<T>> sets) {

		var prod = sets.stream()
				.map(s -> s.stream().map(t -> Collections.singletonList(t)).collect(Collectors.toList()))
				.reduce((x, y) -> DDOP.cartesianProd(x, y)).orElse(new ArrayList<List<T>>(1));

		return prod;
	}

	// -----------------------------------------------------------------------------------------------------------

	public static DD reorder(DD dd) {

		// it's a leaf
		if (dd.getVar() == 0)
			return dd;

		// it's a node
		var _vars = dd.getVars();
		int highestVar = _vars.last();
		DD[] children = new DD[Global.valNames.get(highestVar - 1).size()];
		for (int i = 0; i < children.length; i++) {

			DD restDd = DDOP.restrict(dd, List.of(highestVar), List.of((i + 1)));
			children[i] = DDOP.reorder(restDd);
		}
		return DDnode.getDD(highestVar, children);
	}

	// -----------------------------------------------------------------------------------------------------------

	public static int sampleIndex(List<Float> pdist) {

		float thesum = 0.0f;
		int i = 0;

		for (i = 0; i < pdist.size(); i++)
			thesum += pdist.get(i);

		float ssum = 0.0f;
		float r = Global.random.nextFloat();

		i = 0;

		while (ssum < r && i < pdist.size())
			ssum += pdist.get(i++) / thesum;

		return (i - 1);
	}

	public static Tuple<List<Integer>, List<Integer>> sample(List<DD> dd, List<Integer> varId) {

		var _vars = new ArrayList<>(varId);
		var _dds = new ArrayList<>(dd);
		var _varSamples = new ArrayList<Integer>(varId.size());
		var _valSamples = new ArrayList<Integer>(varId.size());

		while (!_vars.isEmpty()) {

			int v = _vars.remove(0);
			var sample = DDOP.sample(DDOP.addMultVarElim(_dds, _vars), v);
			_dds = (ArrayList<DD>) DDOP.restrict(_dds, sample._0(), sample._1());
			_varSamples.addAll(sample._0());
			_valSamples.addAll(sample._1());
		}

		return Tuple.of(_varSamples, _valSamples);
	}

	public static Tuple<List<Integer>, List<Integer>> sample(DD dd, int varId) {

		// for variables
		var _varList = new ArrayList<Integer>(1);
		_varList.add(varId);

		// for values
		var _valList = new ArrayList<Integer>(1);

		// it's a leaf
		if (dd.getVar() == 0) {

			_valList.add(Global.random.nextInt(Global.valNames.get(varId - 1).size()) + 1);
			return Tuple.of(_varList, _valList);
		}

		// it's a node
		else {

			float sum = 0;
			DD[] children = dd.getChildren();
			for (int childId = 0; childId < children.length; childId++) {

				sum += children[childId].getVal();
			}

			float randomVal = Global.random.nextFloat() * sum;
			sum = 0;
			for (int childId = 0; childId < children.length; childId++) {

				sum += children[childId].getVal();
				if (sum >= randomVal) {

					_valList.add(childId + 1);
					return Tuple.of(_varList, _valList);
				}
			}

			// return last non-zero child
			for (int childId = children.length - 1; childId >= 0; childId--) {

				if (children[childId].getVal() > 0) {

					_valList.add(childId + 1);
					return Tuple.of(_varList, _valList);
				}
			}

			// otherwise there is a bug
			LOGGER.error("Bug in sample multinomial");
			return null;
		}
	}

	// ---------------------------------------------------------------------------------------
	// max all
	public static float maxAll(DD dd) {

		HashMap<DD, Float> hashtable = new HashMap<>();
		return maxAll(dd, hashtable);
	}

	public static float maxAll(DD dd, HashMap<DD, Float> hashtable) {

		Float storedResult = (Float) hashtable.get(dd);

		if (storedResult != null)
			return storedResult.floatValue();

		// it's a leaf
		float result = Float.NEGATIVE_INFINITY;

		if (dd.getVar() == 0)
			result = dd.getVal();

		else {

			DD[] children = dd.getChildren();

			for (int i = 0; i < children.length; i++) {

				float maxVal = DDOP.maxAll(children[i], hashtable);
				if (result < maxVal)
					result = maxVal;
			}
		}

		hashtable.put(dd, Float.valueOf(result));
		return result;
	}
}
