package de.hub.mse.variantdrift.clone.escan;

import org.eclipse.emf.henshin.model.Rule;
import org.eclipse.emf.henshin.variability.ui.clonedetector.CloneGroupDetectionResult;
import org.eclipse.emf.henshin.variability.ui.clonedetector.CloneGroupMapping;

import java.util.*;

public abstract class MyAbstractCloneGroupDetector {
    protected Collection<Rule> rules;
    protected Set<CloneGroupMapping> result;

    private MyAbstractCloneGroupDetector() {
    }

    public MyAbstractCloneGroupDetector(Collection<Rule> rules) {
        this.rules = rules;
    }

    public Set<CloneGroupMapping> getResult() {
        return this.result;
    }

    public CloneGroupDetectionResult getResultOrderedByNumberOfCommonElements() {
        List<CloneGroupMapping> orderedResult = new ArrayList();
        orderedResult.addAll(this.result);
        Comparator<CloneGroupMapping> comp = new Comparator<CloneGroupMapping>() {
            public int compare(CloneGroupMapping arg0, CloneGroupMapping arg1) {
                return arg1.getNumberOfCommonEdges() - arg0.getNumberOfCommonEdges();
            }
        };
        Collections.sort(orderedResult, comp);
        return new CloneGroupDetectionResult(orderedResult);
    }

    public abstract void detectCloneGroups();
}
