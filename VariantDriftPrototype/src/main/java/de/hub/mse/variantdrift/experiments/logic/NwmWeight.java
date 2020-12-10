package de.hub.mse.variantdrift.experiments.logic;

import de.hub.mse.variantdrift.experiments.data.ElementTuple;
import de.hub.mse.variantdrift.experiments.data.RElement;

import java.util.*;

/**
 * We reimplemented the weight metric proposed by Rubin and Chechik (https://doi.org/10.1145/2491411.2491446) in order
 * to improve its runtime performance. We validated that both implementations calculate the same weight for a match
 * (minor divergence due to rounding). In any case, the final result for each algorithm is calculated using this
 * implementation.
 */
public class NwmWeight {
    protected int numberOfModels;

    public NwmWeight(int numberOfModels) {
        this.numberOfModels = numberOfModels;
    }

    public double getQualityOfMatching(Set<ElementTuple> set) {
        double weight = 0.0d;
        for (ElementTuple tuple : set) {
            weight += weightForElements(tuple.getElements());
        }
        return weight;
    }

    public double weightForElements(Collection<RElement> match) {
        HashMap<String, List<Boolean>> allDistinctProperties = new HashMap<>();
        long numerator = 0;
        for (RElement node : match) {
            for (String propertyName : node.getProperties()) {
                numerator += addProperty(allDistinctProperties, propertyName);
            }
        }

        int numberOfDistinctProperties = allDistinctProperties.size(); // |pi(t)|
        return ((double) numerator) / (numberOfDistinctProperties * numberOfModels * numberOfModels);
    }

    private static long addProperty(Map<String, List<Boolean>> properties, String property) {
        long value = 0;
        if (properties.containsKey(property)) {
            List<Boolean> flags = properties.get(property);
            flags.add(true);
            // Calculate the j^2 value of the NwMWeight
            value = flags.size() * flags.size();
            if (flags.size() > 2) {
                // Subtract (j-1)^2 because only the highest "j" should be considered
                value -= (flags.size() - 1) * (flags.size() - 1);
            }
        } else {
            List<Boolean> flags = new ArrayList<>();
            flags.add(true);
            properties.put(property, flags);
        }
        return value;
    }

}
