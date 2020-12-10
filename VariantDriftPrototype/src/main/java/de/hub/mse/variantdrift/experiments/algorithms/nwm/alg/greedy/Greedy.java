package de.hub.mse.variantdrift.experiments.algorithms.nwm.alg.greedy;

import java.util.ArrayList;
import java.util.HashSet;

import de.hub.mse.variantdrift.experiments.algorithms.nwm.alg.AlgoBase;
import de.hub.mse.variantdrift.experiments.algorithms.nwm.common.AlgoUtil;
import de.hub.mse.variantdrift.experiments.algorithms.nwm.domain.Element;
import de.hub.mse.variantdrift.experiments.algorithms.nwm.domain.Tuple;
import de.hub.mse.variantdrift.experiments.algorithms.nwm.domain.Model;

public class Greedy extends AlgoBase {
	
	private ArrayList<Tuple> tuples;
	private ArrayList<Model> models;
	private long tupleSetupTime;
	
	public Greedy(String name, ArrayList<Model> mdls) {
		super(name);
		models = mdls;
	}
	
	protected void setupTuples(){
		long startTime = System.currentTimeMillis();
		if(tuples != null)
			return;
		tuples = AlgoUtil.generateAllTuples(models, false);
		long endTime = System.currentTimeMillis();
		tupleSetupTime = endTime - startTime;

	}
	
	public ArrayList<Model> getModels(){
		return models;
	}
	
	public long getTupleSetupTime(){
		return tupleSetupTime;
	}

	@Override
	protected ArrayList<Tuple> doRun() {
		AlgoUtil.sortTuples(tuples);
		HashSet<Element> collectedElements = new HashSet<Element>();
		ArrayList<Tuple> resultTuples = new ArrayList<Tuple>();
		for(int i=0; i< tuples.size();i++){
			Tuple t = tuples.get(i);
			if(elementsOfTupleFoundInCollectedSet(t, collectedElements))
				continue;
			for(Element elem:t.getRealElements()){
				collectedElements.add(elem);
			}
			resultTuples.add(t);
		}
		return resultTuples;
	}

	private boolean elementsOfTupleFoundInCollectedSet(Tuple t, HashSet<Element> collectedElements) {
		for(Element e : t.getRealElements()){
			if(collectedElements.contains(e))
				return true;
		}
		return false;
	}
	

	
	

}
