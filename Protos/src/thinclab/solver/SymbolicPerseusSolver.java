/*
 *	THINC Lab at UGA | Cyber Deception Group
 *
 *	Author: Aditya Shinde
 * 
 *	email: shinde.aditya386@gmail.com
 */
package thinclab.solver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import thinclab.legacy.DD;
import thinclab.legacy.DDleaf;
import thinclab.legacy.Global;
import thinclab.legacy.OP;
import thinclab.model_ops.belief_exploration.ExplorationStrategy;
import thinclab.models.BeliefBasedModel;
import thinclab.models.POSeqDecMakingModel;
import thinclab.models.datastructures.ReachabilityGraph;
import thinclab.policy.AlphaVectorPolicy;
import thinclab.utils.Tuple;
import thinclab.utils.Tuple3;

/*
 * @author adityas
 *
 */
public class SymbolicPerseusSolver<M extends POSeqDecMakingModel<DD> & PBVISolvable & BeliefBasedModel<DD>>
		implements PointBasedSolver<M, AlphaVectorPolicy> {

	public ReachabilityGraph RG;
	public ExplorationStrategy<M, ReachabilityGraph> ES;
	private int usedBeliefs = 0;

	private static final Logger LOGGER = LogManager.getLogger(SymbolicPerseusSolver.class);

	public SymbolicPerseusSolver(ReachabilityGraph RG, ExplorationStrategy<M, ReachabilityGraph> ES) {

		this.RG = RG;
		this.ES = ES;
	}

	protected Tuple<Integer, DD> Gab(final M m, final DD b, List<DD> primedAVecs) {

		// compute in parallel if there are multiple \alpha vectors and dimensionality
		// is huge
		var aStream = IntStream.range(0, m.A().size());
		if (primedAVecs.size() > 2 && (m.i_S().length + m.i_Om().length) > 5)
			aStream = aStream.parallel();

		// For each action and observation pair (a, o), and each \alpha(s'), compute the
		// dot product for \Gamma_{a, o} with b and get the best \alpha. Sum across
		// observations
		var Gao = aStream.mapToObj(_a -> m.Gaoi(b, _a, primedAVecs)).collect(Collectors.toList());
		var Ga = Arrays.stream(m.R()).map(r -> new Tuple<>(OP.dotProduct(b, r, m.i_S()), r))
				.collect(Collectors.toList());

		// construct \alpha vector for best Ga + Gao
		int bestA = -1;
		float bestVal = Float.NEGATIVE_INFINITY;
		DD bestAlpha = DDleaf.getDD(Float.NaN);

		for (int i = 0; i < m.A().size(); i++) {

			var val = Ga.get(i)._0() + Gao.get(i)._0();
			if (val > bestVal) {

				bestA = i;
				bestVal = val;
				bestAlpha = OP.add(Ga.get(i)._1(), Gao.get(i)._1());
			}
		}

		return new Tuple<>(bestA, bestAlpha);
	}

	protected Tuple<Integer, DD> backup(final M m, final DD b, List<DD> aVecs) {

		var primedAVecs = aVecs.stream().map(a -> OP.primeVars(a, Global.NUM_VARS / 2)).collect(Collectors.toList());
		return Gab(m, b, primedAVecs);
	}

	/*
	 * Perform backups and get the next value function for a given explored belief
	 * region
	 */
	protected AlphaVectorPolicy solveForB(final M m, List<DD> B, AlphaVectorPolicy Vn) {

		List<Tuple<Integer, DD>> newVn = new ArrayList<>(10);
		this.usedBeliefs = 0;

		while (B.size() > 0) {

			DD b = B.remove(Global.random.nextInt(B.size()));
			var newAlpha = backup(m, b, Vn.aVecs.stream().map(a -> a._1()).collect(Collectors.toList()));

			// Construct V_{n+1}(b)
			var Vnb = Vn.aVecs.stream()
					.map(a -> new Tuple3<Float, Integer, DD>(OP.dotProduct(b, a._1(), m.i_S()), a._0(), a._1()))
					.reduce(new Tuple3<>(Float.NEGATIVE_INFINITY, -1, DDleaf.zero),
							(v1, v2) -> v1._0() >= v2._0() ? v1 : v2);

			var newAlphab = OP.dotProduct(b, newAlpha._1(), m.i_S());

			// If new \alpha.b > Vn(b) add it to new V
			if (newAlphab > Vnb._0())
				newVn.add(newAlpha);

			else
				newVn.add(new Tuple<>(Vnb._1(), Vnb._2()));

			B = B.stream().filter(_b -> OP.value_b(Vn.aVecs, _b, m.i_S()) > OP.value_b(newVn, _b, m.i_S()))
					.collect(Collectors.toList());

			this.usedBeliefs++;
		}

		return new AlphaVectorPolicy(newVn);
	}

	@Override
	public AlphaVectorPolicy solve(List<DD> bs, final M m, int H) {

		// h = 0 value function
		var Vn = AlphaVectorPolicy.fromR(m.R());
		bs.forEach(RG::addNode);

		for (int i = 0; i < H; i++) {

			// expand belief region
			RG = ES.expandRG(m, RG);
			var B = new ArrayList<DD>(RG.getAllNodes());
			long then = System.nanoTime();

			// new value function after backups
			var Vn_p = solveForB(m, B, Vn);

			float backupT = (System.nanoTime() - then) / 1000000.0f;
			float bellmanError = B.stream()
					.map(_b -> Math.abs(OP.value_b(Vn.aVecs, _b, m.i_S()) - OP.value_b(Vn_p.aVecs, _b, m.i_S())))
					.reduce(Float.NEGATIVE_INFINITY, (v1, v2) -> v1 > v2 ? v1 : v2);

			// prepare for next iter
			Vn.aVecs.clear();
			Vn.aVecs.addAll(Vn_p.aVecs);

			LOGGER.info(
					String.format("iter: %s | bell err: %.5f | time: %.3f msec | num vectors: %s | beliefs used: %s/%s",
							i, bellmanError, backupT, Vn_p.aVecs.size(), this.usedBeliefs, B.size()));

			if (bellmanError < 0.001 && i > 5) {

				LOGGER.info(String.format("Declaring solution at Bellman error %s and iteration %s", bellmanError, i));
				break;
			}

		}

		return Vn;
	}

}
