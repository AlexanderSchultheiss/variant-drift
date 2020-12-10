package de.hub.mse.variantdrift.clone.escan;

import java.util.List;

public class CloneGroupDetectionResult {
    List<CloneGroupMapping> cloneGroups;

    public CloneGroupDetectionResult(List<CloneGroupMapping> cloneGroups) {
        this.cloneGroups = cloneGroups;
    }

    public List<CloneGroupMapping> getCloneGroups() {
        return this.cloneGroups;
    }
}
