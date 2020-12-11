package de.hub.mse.variantdrift.clone.escan;

import de.hub.mse.variantdrift.clone.models.GenericGraph;
import de.uni_marburg.fb12.swt.cloneDetection.atl.conqat.CloneGroup;
import de.uni_marburg.fb12.swt.cloneDetection.atl.conqat.CloneGroupDetectionResult;

import java.util.*;

public abstract class AbstractCloneGroupDetector {
	protected List<GenericGraph> models;
	protected Set<CloneGroup> result;

	public AbstractCloneGroupDetector(List<GenericGraph> models) {
		this.models = models;
	}

	public CloneGroupDetectionResult getResultOrderedBySize() {
		List<CloneGroup> orderedResult = new ArrayList<>(result);
		Comparator<CloneGroup> comp = (arg0, arg1) -> arg1.getSize() - arg0.getSize();
		orderedResult.sort(comp);
		return new CloneGroupDetectionResult(orderedResult);
	}

	public abstract void detectCloneGroups();
}
