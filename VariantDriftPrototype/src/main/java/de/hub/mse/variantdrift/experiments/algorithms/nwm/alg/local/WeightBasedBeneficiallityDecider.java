package de.hub.mse.variantdrift.experiments.algorithms.nwm.alg.local;

import java.math.BigDecimal;
import java.util.ArrayList;

import de.hub.mse.variantdrift.experiments.algorithms.nwm.common.AlgoUtil;
import de.hub.mse.variantdrift.experiments.algorithms.nwm.common.N_WAY;
import de.hub.mse.variantdrift.experiments.algorithms.nwm.domain.Tuple;

public class WeightBasedBeneficiallityDecider implements SwapBeneficiallityDecider{

	private boolean doSquaring;

	public WeightBasedBeneficiallityDecider(boolean doSquaring){
		this.doSquaring = doSquaring;
	}
	
	@Override
	public SwapDelta calcBeneficiallity(ArrayList<Tuple> solution,
			ArrayList<Tuple> proposedGroup, ArrayList<Tuple> neighborsInSolution) {
		BigDecimal proposedGroupWeight = calcGroupWeight(proposedGroup);
		BigDecimal neighborsWeight = calcGroupWeight(neighborsInSolution);
		BigDecimal delta = proposedGroupWeight.subtract(neighborsWeight, N_WAY.MATH_CTX);
		if(delta.compareTo(LocalSearch.MIN_DELTA) <= 0)
			return SwapDelta.NOT_USEFUL;
		return new SwapDelta(neighborsInSolution, proposedGroup,delta);
	}
	
	private BigDecimal calcGroupWeight(ArrayList<Tuple> tuples){
		BigDecimal weight = AlgoUtil.calcGroupWeight(tuples, false);
		if(doSquaring)
			return weight.multiply(weight, N_WAY.MATH_CTX);
		else
			return weight;
	}
	
}