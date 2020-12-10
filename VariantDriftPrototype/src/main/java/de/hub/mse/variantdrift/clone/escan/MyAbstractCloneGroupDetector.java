package de.hub.mse.variantdrift.clone.escan;


import org.eclipse.emf.ecore.resource.Resource;

import java.util.*;

public abstract class MyAbstractCloneGroupDetector {
    protected Collection<Resource> models;
    protected Set<CloneGroupMapping> result;

    private MyAbstractCloneGroupDetector() {
    }

    public MyAbstractCloneGroupDetector(Collection<Resource> models) {
        this.models = models;
    }

    public Set<CloneGroupMapping> getResult() {
        return this.result;
    }

    public CloneGroupDetectionResult getResultOrderedByNumberOfCommonElements() {
        List<CloneGroupMapping> orderedResult = new ArrayList<>(this.result);
        Comparator<CloneGroupMapping> comp = (arg0, arg1) -> arg1.getNumberOfCommonGenericEdges() - arg0.getNumberOfCommonGenericEdges();
        orderedResult.sort(comp);
        return new CloneGroupDetectionResult(orderedResult);
    }

    public abstract void detectCloneGroups();
}
