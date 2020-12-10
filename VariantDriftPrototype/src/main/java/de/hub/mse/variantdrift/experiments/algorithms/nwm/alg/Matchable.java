package de.hub.mse.variantdrift.experiments.algorithms.nwm.alg;

import java.util.ArrayList;

import de.hub.mse.variantdrift.experiments.algorithms.nwm.domain.Tuple;
import de.hub.mse.variantdrift.experiments.algorithms.nwm.domain.Model;

public interface Matchable {
	 
	 ArrayList<Tuple> getTuplesInMatch();
	 ArrayList<Model> getModels();
}
