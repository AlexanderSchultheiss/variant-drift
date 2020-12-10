package de.hub.mse.variantdrift.clone.escan;

import de.uni_marburg.fb12.swt.cloneDetection.cloneDetection.CloneGroupDetectionResultAsCloneMatrix;
import de.uni_marburg.fb12.swt.cloneDetection.cloneDetection.CloneMatrix;
import de.uni_marburg.fb12.swt.cloneDetection.henshinToIntegrated.CapsuleEdge;
import de.uni_marburg.fb12.swt.cloneDetection.henshinToIntegrated.ExtendedIntegratedGraphConverter;
import org.eclipse.emf.henshin.model.Node;
import org.eclipse.emf.henshin.model.Rule;
import org.jgrapht.DirectedGraph;

import java.util.*;

public abstract class MyCloneDetection extends MyAbstractCloneGroupDetector {
    protected Set<CloneMatrix> resultAsCloneMatrix;
    protected final Set<Rule> ruleSet;
    protected final Map<Rule, DirectedGraph<Node, CapsuleEdge>> ruleGraphMap;
    protected final List<Rule> ruleList;
    protected final boolean considerAttributeNodes;

    public MyCloneDetection(Collection<Rule> rules) {
        super(rules);
        this.ruleSet = this.getRuleSet(rules);
        this.ruleGraphMap = this.getRuleGraphMap(this.ruleSet);
        this.ruleList = this.getRuleList(this.ruleSet);
        this.considerAttributeNodes = true;
        this.resultAsCloneMatrix = new HashSet<>();
        this.result = null;
    }

    public abstract void detectCloneGroups();

    public MyCloneDetection(Map<Rule, DirectedGraph<Node, CapsuleEdge>> ruleGraphMap, List<Rule> ruleList, boolean considerAttributeNodes) {
        super(ruleGraphMap.keySet());
        this.ruleSet = ruleGraphMap.keySet();
        this.ruleGraphMap = ruleGraphMap;
        this.ruleList = ruleList;
        this.considerAttributeNodes = considerAttributeNodes;
        this.resultAsCloneMatrix = new HashSet<>();
    }

    public Set<CloneMatrix> getResultAsCloneMatrix() {
        return this.resultAsCloneMatrix;
    }

    protected int getNumberOfTermsForCloneDetective() {
        return 3;
    }

    protected Set<Rule> getRuleSet(Collection<Rule> rules) {
        return new HashSet<>(rules);
    }

    private Map<Rule, DirectedGraph<Node, CapsuleEdge>> getRuleGraphMap(Set<Rule> ruleSet) {
        ExtendedIntegratedGraphConverter graphConverter = new ExtendedIntegratedGraphConverter();
        return graphConverter.getRuleToGraphWithAttributesMap(ruleSet);
    }

    protected List<Rule> getRuleList(Set<Rule> ruleSet) {
        return new LinkedList<>(ruleSet);
    }

    public CloneGroupDetectionResultAsCloneMatrix getResultAsCloneMatrixOrderedByNumberOfCommonElements() {
        List<CloneMatrix> orderedResult = new ArrayList<>(this.resultAsCloneMatrix);
        Comparator<CloneMatrix> comp = (arg0, arg1) -> arg1.getNumberOfCommonEdges() - arg0.getNumberOfCommonEdges();
        orderedResult.sort(comp);
        return new CloneGroupDetectionResultAsCloneMatrix(orderedResult);
    }

    public String startDetectCloneGroups(String name) {
        Date dateStartDetect = new Date();
        return "\n" + name + " - start " + dateStartDetect.toString() + "\n";
    }

    public String startConversionTempResult(String nameFirst) {
        Date date = new Date();
        return "\n" + nameFirst + " end - start Conversion tempResult" + date.toString() + "\n";
    }

    public String moveOnToSecondAlgortihm(String nameSecond) {
        Date date = new Date();
        return "\nConversion tempResult done - start " + nameSecond + " " + date.toString() + "\n";
    }

    public String startGroupingResults() {
        Date date = new Date();
        return "\nstart ModelCd-eScan group and filter steps " + date.toString() + "\n";
    }

    public String startConversion() {
        Date dateStartConversion = new Date();
        return "\nCloneDetection itself done - start Conversion to CloneGroupMapping " + dateStartConversion.toString() + "\n";
    }

    public String endDetectCloneGroups(String name, long startTime) {
        long end = System.currentTimeMillis();
        Date dateEndDetect = new Date();
        String res = name + " - end " + dateEndDetect.toString() + "\n";
        res = res + this.getTime(startTime, end);
        res = res + "------------------------------------------------------------\n";
        return res;
    }

    private String getTime(long start, long end) {
        long sec = (end - start) / 1000L;
        if (sec < 60L) {
            return "Running time: " + sec + " sec.\n";
        } else {
            int min = (int)(sec / 60L);
            sec %= 60L;
            String res = "Running time: " + min + " min. " + sec + " sec.\n";
            return res;
        }
    }
}
