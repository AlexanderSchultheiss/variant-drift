package de.hub.mse.variantsync.variantdrift.experiments.algorithms.nwm.alg.merge;

import de.hub.mse.variantsync.variantdrift.experiments.algorithms.nwm.common.N_WAY;

public class MergeDescriptor {

	public N_WAY.ALG_POLICY algPolicy;
	public boolean asc;
	public N_WAY.ORDER_BY orderBy;

	public MergeDescriptor(N_WAY.ALG_POLICY algPolicy, boolean asc, N_WAY.ORDER_BY orderBy) {
		this.algPolicy = algPolicy;
		this.asc = asc;
		this.orderBy = orderBy;
	}
	
	public static MergeDescriptor EMPTY = new MergeDescriptor(N_WAY.ALG_POLICY.PAIR_WISE, true, N_WAY.ORDER_BY.MODEL_ID);

}
