package de.hub.mse.variantdrift.experiments.algorithms.nwm.common;

import java.util.Comparator;

import de.hub.mse.variantdrift.experiments.algorithms.nwm.domain.Model;

public class ModelComparator implements Comparator<Model> {

	private boolean asc;
	
	public ModelComparator(boolean asc) {
		this.asc = asc;
	}

	@Override
	public int compare(Model m1, Model m2) {
		if(asc){
			return m1.size() - m2.size();
		}
		else
			return m2.size() - m1.size();
	}

}
