package de.hub.mse.variantdrift.clone.escan;

import de.uni_marburg.fb12.swt.cloneDetection.atl.escan.CloneMatrix;

import java.util.List;

/**
 * 
 * org.eclipse.emf.henshin.variability.ui.clonedetector CloneMatrix instead of
 * CloneGroupMapping
 *
 */
public class CloneGroupDetectionResultAsCloneMatrix {
	private List<CloneMatrix> cloneGroups;

	public CloneGroupDetectionResultAsCloneMatrix(List<CloneMatrix> cloneGroups) {
		this.cloneGroups = cloneGroups;
	}

	public List<CloneMatrix> getCloneGroups() {
		return cloneGroups;
	}
}
