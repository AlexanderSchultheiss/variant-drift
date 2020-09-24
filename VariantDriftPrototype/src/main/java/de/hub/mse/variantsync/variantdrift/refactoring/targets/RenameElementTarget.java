package de.hub.mse.variantsync.variantdrift.refactoring.targets;

import de.hub.mse.variantsync.variantdrift.experiments.algorithms.nwm.domain.Element;
import de.hub.mse.variantsync.variantdrift.experiments.algorithms.nwm.domain.Model;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * An instance of this class represents a potential target for a rename element refactoring. A target for this
 * operation consists of exactly one element, the element which is to be renamed.
 */
public class RenameElementTarget {
    public final Element element;
    public final Model model;

    public RenameElementTarget(Element element, Model model) {
        this.element = element;
        this.model = model;
    }

    /**
     * Return a set with all targets for the rename element refactoring, targets that can be found in the given model.
     * <p>
     * A target comprises exactly one element that can be renamed. This refactoring is in principle applicable to all
     * elements in the model
     *
     * @param model The model for which potential targets are to be identified
     * @return A set of RenameElementTarget instances, where each instance represents a potential target
     */
    public static Set<RenameElementTarget> findAllTargets(Model model) {
        Set<RenameElementTarget> possibleTargets = new HashSet<>();

        // This refactoring is in principle applicable to all elements in the model
        for (Element element : model.getElements()) {
            possibleTargets.add(new RenameElementTarget(element, model));
        }
        return possibleTargets;
    }

    @Override
    public int hashCode() {
        return Objects.hash(element);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj instanceof RenameElementTarget) {
            RenameElementTarget other = (RenameElementTarget) obj;
            return this.element == other.element;
        } else {
            return false;
        }
    }
}
