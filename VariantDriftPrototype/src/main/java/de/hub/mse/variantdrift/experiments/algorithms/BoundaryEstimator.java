package de.hub.mse.variantdrift.experiments.algorithms;

import de.hub.mse.variantdrift.experiments.algorithms.nwm.alg.AlgoBase;
import de.hub.mse.variantdrift.experiments.algorithms.nwm.domain.Element;
import de.hub.mse.variantdrift.experiments.algorithms.nwm.domain.Model;
import de.hub.mse.variantdrift.experiments.algorithms.nwm.domain.Tuple;

import java.util.*;

/**
 * This class is responsible for calculating the boundary of a matching weight for a given set of models.
 */
public class BoundaryEstimator extends AlgoBase {
    private final ArrayList<Model> models;

    public BoundaryEstimator(ArrayList<Model> models) {
        super("Boundary Matcher");
        this.models = models;
    }

    @Override
    protected ArrayList<Tuple> doRun() {
        ArrayList<Tuple> result = new ArrayList<>();
        Map<String, Set<Element>> propertyToElementsMap = getPropertyToElementsMap(models);
        Set<Element> allElements = new HashSet<>();
        models.forEach(m -> allElements.addAll(m.getElements()));
        // For each element in the dataset, we want to find the tuple with the highest weight
        for (var element : allElements) {
            // First, we iterate over all properties of the element and collect all potential match candidates
            Set<Element> potentialCandidates = new HashSet<>();
            for (var property : element.getProperties()) {
                potentialCandidates.addAll(propertyToElementsMap.get(property));
            }
            // Then we group the candidates by model
            Map<String, Set<Element>> modelToCandidateMap = new HashMap<>();
            for (var candidate : potentialCandidates) {
                String modelID = candidate.getModelId();
                Set<Element> modelSpecificCandidates;
                if (modelToCandidateMap.containsKey(modelID)) {
                    modelSpecificCandidates = modelToCandidateMap.get(modelID);
                } else {
                    modelSpecificCandidates = new HashSet<>();
                    modelToCandidateMap.put(modelID, modelSpecificCandidates);
                }
                modelSpecificCandidates.add(candidate);
            }
            // Next, we select the best fitting candidate from each model
            Set<Element> matchCandidates = new HashSet<>();
            for (Set<Element> modelSpecificCandidates : modelToCandidateMap.values()) {
                Element bestCandidate = null;
                double bestWeight = 0.0d;
                for (var candidate : modelSpecificCandidates) {
                    Tuple tuple = new Tuple();
                    tuple.addElement(element);
                    tuple.addElement(candidate);
                    double weight = tuple.calcWeight(models).doubleValue();
                    if (bestWeight < weight) {
                        bestWeight = weight;
                        bestCandidate = candidate;
                    }
                }
                matchCandidates.add(bestCandidate);
            }
            // Now we check all possible tuples that can be formed and only take the best one
            Tuple bestTupleForElement = getBestTuple(element, matchCandidates);
            result.add(bestTupleForElement);
        }
        // Filter out duplicate tuple ... we removed filtering after it showed to cause side effects for refactored models
        // where suddenly a broader variety of unique tuple appears
        // Set<Tuple> filteredSet = new HashSet<>(result);
        // return new ArrayList<>(filteredSet);
        return result;
    }

    public Tuple getBestTuple(Element target, Set<Element> elements) {
        List<Tuple> pairs = getElementPairs(target, elements);
        // Sort descending by weight
        pairs.sort((o1, o2) -> Double.compare(o2.getWeight().doubleValue(), o1.getWeight().doubleValue()));
        double bestWeight = 0.0d;
        Tuple bestTuple = new Tuple();
        bestTuple.addElement(target);
        for (Tuple tuple : pairs) {
            Tuple potentiallyBetterTuple = new Tuple();
            potentiallyBetterTuple.addElements(bestTuple.getElements());
            Element nextCandidate = tuple.getElements().get(1);
            if (nextCandidate == target) {
                throw new RuntimeException("Error in selection of elements");
            }
            potentiallyBetterTuple.addElement(nextCandidate);
            double nextWeight = potentiallyBetterTuple.calcWeight(models).doubleValue();
            if (nextWeight > bestWeight) {
                bestWeight = nextWeight;
                bestTuple = potentiallyBetterTuple;
            }
        }
        return bestTuple;
    }

    public List<Tuple> getElementPairs(Element target, Set<Element> candidates) {
        List<Tuple> pairs = new ArrayList<>();
        for (Element candidate : candidates) {
            if (!target.getModelId().equals(candidate.getModelId())) {
                Tuple tuple = new Tuple();
                tuple.addElement(target);
                tuple.addElement(candidate);
                tuple.setWeight(tuple.calcWeight(models));
                pairs.add(tuple);
            }
        }
        return pairs;
    }

    public static Map<String, Set<Element>> getPropertyToElementsMap(List<Model> models) {
        Map<String, Set<Element>> propertyToElementsMap = new HashMap<>();
        // Collect sets of elements that have a specific property and the set of all properties
        for (var model : models) {
            for (var element : model.getElements()) {
                for (var property : element.getProperties()) {
                    Set<Element> elementList;
                    if (propertyToElementsMap.containsKey(property)) {
                        elementList = propertyToElementsMap.get(property);
                    } else {
                        elementList = new HashSet<>();
                        propertyToElementsMap.put(property, elementList);
                    }
                    elementList.add(element);
                }
            }
        }
        return propertyToElementsMap;
    }

    @Override
    public ArrayList<Model> getModels() {
        return models;
    }
}