package de.hub.mse.variantsync.variantdrift.refactoring.targets;

import de.hub.mse.variantsync.variantdrift.experiments.algorithms.nwm.domain.Element;
import de.hub.mse.variantsync.variantdrift.experiments.algorithms.nwm.domain.Model;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * An instance of this class represents a potential target for a rename property refactoring. A target for this
 * operation consists of exactly one element and one property.
 */
public class RenamePropertyTarget {
    public final Element element;
    public final String property;

    public RenamePropertyTarget(Element element, String property) {
        this.element = element;
        this.property = property;
    }

    /**
     * Return a set with all targets for the rename property refactoring, targets that can be found in the given model.
     * <p>
     * A target comprises exactly one element and one property, the property that can be renamed. This refactoring is
     * in principle applicable to all elements in the model, that have at least one property which is not the element's
     * name.
     *
     * @param model The model for which potential targets are to be identified
     * @return A set of RenamePropertyTarget instances, where each instance represents a potential target
     */
    public static Set<RenamePropertyTarget> findAllTargets(Model model) {
        Set<RenamePropertyTarget> possibleTargets = new HashSet<>();

        for (Element element : model.getElements()) {
            // Create one target for each property in an element that is not the name property
            for (String property : element.getProperties()) {
                if (!property.startsWith("n_")) {
                    possibleTargets.add(new RenamePropertyTarget(element, property));
                }
            }
        }
        return possibleTargets;
    }

    @Override
    public int hashCode() {
        return Objects.hash(element, property);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj instanceof RenamePropertyTarget) {
            RenamePropertyTarget other = (RenamePropertyTarget) obj;
            return this.element == other.element && this.property.equals(other.property);
        } else {
            return false;
        }
    }

}
