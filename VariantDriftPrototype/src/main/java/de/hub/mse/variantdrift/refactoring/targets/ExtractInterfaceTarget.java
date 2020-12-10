package de.hub.mse.variantdrift.refactoring.targets;

import de.hub.mse.variantdrift.experiments.algorithms.nwm.domain.Element;
import de.hub.mse.variantdrift.experiments.algorithms.nwm.domain.Model;

import java.util.*;

/**
 * An instance of this class represents a potential target for an extract interface refactoring. A target for this
 * operation consists of at least two elements that have common properties from which the interface is to be extracted.
 */
public class ExtractInterfaceTarget {
    public final Set<Element> sourceElements;
    public final Model model;

    public ExtractInterfaceTarget(Set<Element> sourceElements, Model model) {
        if (sourceElements.size() < 2) {
            throw new IllegalArgumentException("At least two elements expected");
        }
        this.sourceElements = sourceElements;
        this.model = model;
    }

    /**
     * Return a set with all targets for the extract interface refactoring, targets that can be found in the given model.
     * <p>
     * A target comprises at least two elements from which the interface is to be extracted. It is possible that a
     * specific element appears in more than one target.
     *
     * @param model The model for which potential targets are to be identified
     * @return A set of ExtractInterfaceTarget instances, where each instance represents a potential target
     */
    public static Set<ExtractInterfaceTarget> findAllTargets(Model model) {
        // Possible targets are all sets of elements that have a common set of properties
        // It is possible that an element occurs in more than one set
        Set<ExtractInterfaceTarget> targets = new HashSet<>();

        // First, for each property, we find the set of elements that have the property
        Map<String, Set<Element>> mapOfPropertiesToElementSets = new HashMap<>();
        for (Element element : model.getElements()) {
            for (String property : element.getProperties()) {
                if (property.startsWith("n_")) {
                    // We filter out name properties
                    continue;
                }
                Set<Element> elementsWithProperty;
                if (mapOfPropertiesToElementSets.containsKey(property)) {
                    elementsWithProperty = mapOfPropertiesToElementSets.get(property);
                } else {
                    elementsWithProperty = new HashSet<>();
                    mapOfPropertiesToElementSets.put(property, elementsWithProperty);
                }
                elementsWithProperty.add(element);
            }
        }

        // Then, we iterate over all gathered sets of elements and add them as target. Duplicate targets are identified
        // by the elements they contain, therefore we automatically account for elements sharing several properties
        for (Set<Element> elementsWithProperty : mapOfPropertiesToElementSets.values()) {
            // Each target has to comprise at least two elements
            if (elementsWithProperty.size() > 1) {
                targets.add(new ExtractInterfaceTarget(elementsWithProperty, model));
            }
        }

        return targets;
    }

    /**
     * Return true if this target overlaps with the given target. Two targets overlap if they both target at least
     * one common element and at least one common property in that element. Otherwise, return false.
     *
     * @param other The target for which an overlap should be detected
     * @return true, if this target overlaps with the given target, false otherwise
     */
    public boolean overlapsWith(ExtractInterfaceTarget other) {
        if (this.equals(other)) {
            return true;
        }
        // We have to check whether the set of elements and the set of common properties overlap
        boolean elementsOverlap = false;
        for (Element elementOfThis : this.sourceElements) {
            if (other.sourceElements.contains(elementOfThis)) {
                // The sets of elements overlap, now we have to check the properties
                elementsOverlap = true;
                break;
            }
        }
        if (elementsOverlap) {
            for (String commonPropertyOfThis : this.getCommonProperties()) {
                if (other.getCommonProperties().contains(commonPropertyOfThis)) {
                    // If at least one property overlaps as well, the refactoring targets overlap
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Identify and return the set of properties that are common among the elements of this target.
     *
     * @return A set of String instances representing common properties.
     */
    public Set<String> getCommonProperties() {
        // Find all common properties
        Set<String> properties = null;
        String modelID = null;
        for (Element element : this.sourceElements) {
            if (modelID == null) {
                modelID = element.getModelId();
            }
            // Validate that the source element is in the model
            if (!element.getModelId().equals(modelID)) {
                throw new IllegalArgumentException("One of the source elements is not from the same model!");
            }
            if (properties == null) {
                properties = new HashSet<>(element.getProperties());
            } else {
                properties.retainAll(element.getProperties());
            }
        }
        return properties;
    }

    @Override
    public int hashCode() {
        return Objects.hash(sourceElements);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj instanceof ExtractInterfaceTarget) {
            ExtractInterfaceTarget other = (ExtractInterfaceTarget) obj;
            return this.sourceElements.equals(other.sourceElements);
        } else {
            return false;
        }
    }
}
