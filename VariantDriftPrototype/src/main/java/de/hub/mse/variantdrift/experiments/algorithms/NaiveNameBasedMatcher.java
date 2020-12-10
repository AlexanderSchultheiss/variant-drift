package de.hub.mse.variantdrift.experiments.algorithms;

import de.hub.mse.variantdrift.experiments.algorithms.nwm.alg.AlgoBase;
import de.hub.mse.variantdrift.experiments.algorithms.nwm.domain.Element;
import de.hub.mse.variantdrift.experiments.algorithms.nwm.domain.Model;
import de.hub.mse.variantdrift.experiments.algorithms.nwm.domain.Tuple;

import java.util.*;

/**
 * Matches models purely based on the names of the elements.
 */
public class NaiveNameBasedMatcher extends AlgoBase {
    private final ArrayList<Model> models;

    public NaiveNameBasedMatcher(ArrayList<Model> models) {
        super("Name Based Matcher");
        this.models = models;
    }

    @Override
    protected ArrayList<Tuple> doRun() {
        Map<String, List<Element>> elementsByName = new HashMap<>();
        // Collect lists of elements that have the same name
        for (var model : models) {
            for (var element : model.getElements()) {
                List<Element> elementList;
                String elementName = element.getLabel();
                if (elementsByName.containsKey(elementName)) {
                    elementList = elementsByName.get(elementName);
                } else {
                    elementList = new ArrayList<>();
                    elementsByName.put(elementName, elementList);
                }
                elementList.add(element);
            }
        }
        // Form valid tuple from the element lists, if there is more than one element with a specific name in the same
        // model, a new tuple is formed. Elements from other models are added to the first model, except if there is
        // already an element from the first model in the tuple
        ArrayList<Tuple> resultTuples = new ArrayList<>();
        for (var elementList : elementsByName.values()) {
            Set<Tuple> tupleSetNew = new HashSet<>();
            for (var element : elementList) {
                Set<Tuple> tupleSetOld = tupleSetNew;
                tupleSetNew = new HashSet<>();
                Tuple nextTuple = new Tuple(element);
                boolean didMerge = false;
                // Check whether the single element tuple can be merged with one of the existing tuples
                for (var tupleInSet : tupleSetOld) {
                    if (tupleInSet.haveCommonModelWith(nextTuple)) {
                        tupleSetNew.add(tupleInSet);
                    } else {
                        var combinedElements = new ArrayList<>(tupleInSet.getElements());
                        combinedElements.addAll(nextTuple.getElements());
                        tupleSetNew.add(new Tuple(combinedElements));
                        didMerge = true;
                        break;
                    }
                }
                // Add the single element tuple to the set if it was not merged with an existing tuple
                if (!didMerge) {
                    tupleSetNew.add(nextTuple);
                }
            }
            // Collect the tuples created from the element with a common name
            resultTuples.addAll(tupleSetNew);
        }
        return resultTuples;
    }

    @Override
    public ArrayList<Model> getModels() {
        return models;
    }
}
