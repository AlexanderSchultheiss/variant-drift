package de.hub.mse.variantdrift.clone.examples;

import aatl.MatchedRule;
import aatl.Module;
import de.uni_marburg.fb12.swt.cloneDetection.atl.conqat.CloneGroup;
import de.uni_marburg.fb12.swt.cloneDetection.atl.conqat.CloneGroupDetectionResult;

import java.util.*;
import java.util.stream.Collectors;

public abstract class AbstractCloneGroupDetector {
    protected List<MatchedRule> rules;
    protected Set<CloneGroup> result;

    @SuppressWarnings("unused")
    private AbstractCloneGroupDetector() {
    }

    public AbstractCloneGroupDetector(List<MatchedRule> rules) {
        this.rules = rules;
    }

    public AbstractCloneGroupDetector(Module module) {
        rules = module.getElements().stream().filter(p -> p instanceof MatchedRule).map(p -> (MatchedRule) p)
                .collect(Collectors.toList());
    }

    public AbstractCloneGroupDetector(Set<Module> modules) {
        rules = modules.stream().flatMap(m -> m.getElements().stream()).filter(p -> p instanceof MatchedRule).map(p -> (MatchedRule) p)
                .collect(Collectors.toList());
    }

    public CloneGroupDetectionResult getResultOrderedBySize() {
        List<CloneGroup> orderedResult = new ArrayList<CloneGroup>();
        orderedResult.addAll(result);
        Comparator<CloneGroup> comp = new Comparator<CloneGroup>() {
            @Override
            public int compare(CloneGroup arg0, CloneGroup arg1) {
                return arg1.getSize() - arg0.getSize();
            }
        };
        Collections.sort(orderedResult, comp);
        return new CloneGroupDetectionResult(orderedResult);
    }

    public abstract void detectCloneGroups();
}
