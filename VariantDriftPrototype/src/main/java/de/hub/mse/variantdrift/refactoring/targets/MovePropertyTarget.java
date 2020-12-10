package de.hub.mse.variantdrift.refactoring.targets;

import de.hub.mse.variantdrift.experiments.algorithms.nwm.domain.Element;
import de.hub.mse.variantdrift.experiments.algorithms.nwm.domain.Model;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * An instance of this class represents a potential target for a move property refactoring. A target for this
 * operation consists of exactly two elements: a source and a target for the move operation. As well as a property
 * that is to be moved from source to target.
 */
public class MovePropertyTarget {
    public final Element sourceElement;
    public final Element targetElement;
    public final String property;

    public MovePropertyTarget(Element sourceElement, Element targetElement, String property) {
        this.sourceElement = sourceElement;
        this.targetElement = targetElement;
        this.property = property;
    }

    /**
     * Return a set with all targets for the move property refactoring, targets that can be found in the given model.
     * <p>
     * A target comprises at exactly two elements and one property. A source element that contains the property before
     * the move is applied and a target element to which the property is to be moved to.
     *
     * @param model The model for which potential targets are to be identified
     * @return A set of MovePropertyTarget instances, where each instance represents a potential target
     */
    public static Set<MovePropertyTarget> findAllTargets(Model model) {
        Set<MovePropertyTarget> targets = new HashSet<>();
        // Possible targets are all pairs of two elements for which the source has a property that is not in the target
        for (Element sourceElement : model.getElements()) {
            for (Element targetElement : model.getElements()) {
                if (sourceElement != targetElement) {
                    // We have one target for each movable property
                    for (String property : sourceElement.getProperties()) {
                        // We never move the name property
                        if (!property.startsWith("n_")) {
                            // We only want to move properties that are not in the target
                            if (!targetElement.getProperties().contains(property)) {
                                targets.add(new MovePropertyTarget(sourceElement, targetElement, property));
                            }
                        }
                    }
                }
            }
        }
        return targets;
    }

    /**
     * Return true if this target overlaps with the given target. Two targets overlap if they have the same source or
     * same target elements, and move the same property
     *
     * @param other The target for which an overlap should be detected
     * @return true, if this target overlaps with the given target, false otherwise
     */
    public boolean overlapsWith(MovePropertyTarget other) {
        // Two move targets overlap if they have the same source or same target elements, and move the same property
        if (this.property.equals(other.property)) {
            return this.sourceElement == other.sourceElement || this.targetElement == other.targetElement;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(sourceElement, targetElement, property);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj instanceof MovePropertyTarget) {
            MovePropertyTarget other = (MovePropertyTarget) obj;
            return this.sourceElement == other.sourceElement && this.targetElement == other.targetElement && this.property.equals(other.property);
        } else {
            return false;
        }
    }
}
