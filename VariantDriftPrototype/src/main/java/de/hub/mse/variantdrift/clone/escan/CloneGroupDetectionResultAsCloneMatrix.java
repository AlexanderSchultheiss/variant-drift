package de.hub.mse.variantdrift.clone.escan;

import java.util.List;

/**
 * org.eclipse.emf.henshin.variability.ui.clonedetector CloneMatrix instead of
 * CloneGroupMapping
 */
public class CloneGroupDetectionResultAsCloneMatrix {
    private final List<CloneMatrix> cloneGroups;

    public CloneGroupDetectionResultAsCloneMatrix(List<CloneMatrix> cloneGroups) {
        this.cloneGroups = cloneGroups;
    }

    public List<CloneMatrix> getCloneGroups() {
        return cloneGroups;
    }
}
