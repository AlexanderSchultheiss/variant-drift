package de.hub.mse.variantdrift.clone.escan;

import de.hub.mse.variantdrift.clone.models.GenericEdge;
import de.hub.mse.variantdrift.clone.models.GenericGraph;
import de.hub.mse.variantdrift.clone.models.GenericNode;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;

import java.util.*;


/**
 * The top-level CloneDetection class.
 */
public abstract class CloneDetection extends AbstractCloneGroupDetector {
    protected boolean DEBUG = false;


    /**
     * The result of the CloneDetection.
     */
    protected Set<CloneMatrix> resultAsCloneMatrix = null;

    /**
     * Rule to computation-Graph
     */
    protected Map<GenericGraph, DirectedGraph<GenericNode, GenericEdge>> modelGraphMap = null;

    public CloneDetection(List<GenericGraph> rules) {
        super(rules);
        initialize(rules);
    }

    public CloneDetection(
            Map<GenericGraph, DirectedGraph<GenericNode, GenericEdge>> modelGraphMap,
            List<GenericGraph> rules) {
        super(rules);
        this.modelGraphMap = modelGraphMap;
        resultAsCloneMatrix = new HashSet<>();
    }

    private static Map<GenericGraph, DirectedGraph<GenericNode, GenericEdge>> getModelToJGraphMap(
            Collection<GenericGraph> models) {
        Map<GenericGraph, DirectedGraph<GenericNode, GenericEdge>> result = new HashMap<>();

        for (GenericGraph model : models) {
            DirectedGraph<GenericNode, GenericEdge> graph = new DefaultDirectedGraph<>(GenericEdge.class);
            // Add all the nodes
            model.getNodes().stream().map(n -> (GenericNode) n).forEach(graph::addVertex);

            // Add all the edges
            model.getEdges().stream().map(e -> (GenericEdge) e).forEach(e -> {
                graph.addEdge((GenericNode) e.getSourceNode(), (GenericNode) e.getTargetNode(), e);
            });
        }
        return result;
    }

    private void initialize(List<GenericGraph> rules) {
        modelGraphMap = getRuleGraphMap(rules);
        resultAsCloneMatrix = new HashSet<>();
        this.result = null;
    }

    public abstract void detectCloneGroups();

    public Set<CloneMatrix> getResultAsCloneMatrix() {
        return resultAsCloneMatrix;
    }

    /**
     * In CloneDetective and CloneDetectiveTupel: getSimilarityFunctionValue
     * specifies how many terms are used
     *
     * @return
     */
    protected int getNumberOfTermsForCloneDetective() {
        // no special reason
        return 3;
    }

    /**
     * Building up the computation graphs to each rule.
     * <p>
     * (It�s very important, especially for the combine algorithm, that the
     * RuleGraphMap isn�t created a second time. (Due to the fact that the
     * Attributes are converted into Nodes, and this Nodes are new created, the
     * nodes of a new generated graph are equal to the Nodes of the former
     * graph, but not identical, and JGraph tests for identity, means a new
     * computation of ruleGraphMap could lead to a lot of errors.)
     *
     * @return rule to rule-computation-graph
     */
    private Map<GenericGraph, DirectedGraph<GenericNode, GenericEdge>> getRuleGraphMap(
            List<GenericGraph> modelSet) {
        return getModelToJGraphMap(modelSet);
    }

    /**
     * from org.eclipse.emf.henshin.variability.ui.clonedetector.
     * AbstractCloneGroupDetector
     *
     * @return
     */
    public CloneGroupDetectionResultAsCloneMatrix
    getResultAsCloneMatrixOrderedByNumberOfCommonElements() {
        List<CloneMatrix> orderedResult = new ArrayList<CloneMatrix>(resultAsCloneMatrix);
        Comparator<CloneMatrix> comp = (arg0, arg1) -> arg1.getNumberOfCommonGenericEdges()
                - arg0.getNumberOfCommonGenericEdges();
        orderedResult.sort(comp);
        return new CloneGroupDetectionResultAsCloneMatrix(orderedResult);
    }

    /**
     * to ensure an uniform output
     *
     * @param name the name of the CloneDetection
     * @return an output String
     */
    public String startDetectCloneGroups(String name) {
        Date dateStartDetect = new Date();
        return ("\n" + name + " - start " + dateStartDetect.toString() + "\n");
    }

    /**
     * for combine algorithm to ensure an uniform output
     *
     * @param nameFirst the name of the first CloneDetection
     * @return an output String
     */
    public String startConversionTempResult(String nameFirst) {
        Date date = new Date();
        return ("\n" + nameFirst + " end - start Conversion tempResult"
                + date.toString() + "\n");
    }

    /**
     * for combine algorithm to ensure an uniform output
     *
     * @param nameSecond the name of the second CloneDetection
     * @return an output String
     */
    public String moveOnToSecondAlgortihm(String nameSecond) {
        Date date = new Date();
        return ("\n" + "Conversion tempResult done - start " + nameSecond + " "
                + date.toString() + "\n");
    }

    /**
     * for combine algorithm CloneDetective with eScans group- and filter-steps
     * to ensure an uniform output
     *
     * @return an output String
     */
    public String startGroupingResults() {
        Date date = new Date();
        return ("\n" + "start ModelCd-eScan group and filter steps "
                + date.toString() + "\n");
    }

    /**
     * to ensure an uniform output
     *
     * @return an output String
     */
    public String startConversion() {
        Date dateStartConversion = new Date();
        return ("\n"
                + "CloneDetection itself done - start Conversion to CloneGroupMapping "
                + dateStartConversion.toString() + "\n");
    }

    /**
     * to ensure an uniform output
     *
     * @param name      the name of the CloneDetection
     * @param startTime Time of start
     * @return an output String
     */
    public String endDetectCloneGroups(String name, long startTime) {
        long end = System.currentTimeMillis();
        String res;
        Date dateEndDetect = new Date();
        res = (name + " - end " + dateEndDetect.toString() + "\n");
        res = res + getTime(startTime, end);
        res = res
                + "------------------------------------------------------------"
                + "\n";
        return res;
    }

    /**
     * to ensure an uniform output convert the time difference to minutes and
     * seconds
     *
     * @param start start time
     * @param end   end time
     * @return Time description
     */
    private String getTime(long start, long end) {
        long sec = (end - start) / 1000;
        if (sec < 60) {
            return ("Running time: " + sec + " sec." + "\n");
        }
        int min = (int) (sec / 60);
        sec = sec % 60;

        return ("Running time: " + min + " min. " + sec + " sec." + "\n");
    }

}
