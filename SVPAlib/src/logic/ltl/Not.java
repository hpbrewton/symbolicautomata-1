package logic.ltl;

import java.util.Collection;
import java.util.HashMap;

import automata.safa.BooleanExpression;
import automata.safa.BooleanExpressionFactory;
import automata.safa.SAFAInputMove;
import theory.BooleanAlgebra;

public class Not<P, S> extends LTLFormula<P, S> {

	protected LTLFormula<P, S> phi;

	public Not(LTLFormula<P, S> phi) {
		super();
		this.phi = phi;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((phi == null) ? 0 : phi.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Not))
			return false;
		@SuppressWarnings("unchecked")
		Not<P, S> other = (Not<P, S>) obj;
		if (phi == null) {
			if (other.phi != null)
				return false;
		} else if (!phi.equals(other.phi))
			return false;
		return true;
	}

	@Override
	protected <E extends BooleanExpression> void accumulateSAFAStatesTransitions(HashMap<LTLFormula<P, S>, Integer> formulaToStateId,
			HashMap<Integer, Collection<SAFAInputMove<P, S, E>>> moves,
			Collection<Integer> finalStates, BooleanAlgebra<P, S> ba,
			BooleanExpressionFactory<E> boolexpr) {

		throw new UnsupportedOperationException("At this point the formula should be in negation normal form.");
	}

	@Override
	protected boolean isFinalState() {
		throw new UnsupportedOperationException("At this point the formula should be in negation normal form.");
	}
	
	@Override
	protected LTLFormula<P, S> pushNegations(boolean isPositive, BooleanAlgebra<P, S> ba) {
		return phi.pushNegations(!isPositive,ba);	
	}
}
