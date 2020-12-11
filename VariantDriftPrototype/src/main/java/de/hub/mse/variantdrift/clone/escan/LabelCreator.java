package de.hub.mse.variantdrift.clone.escan;

import de.hub.mse.variantdrift.clone.models.GenericEdge;
import de.hub.mse.variantdrift.clone.models.GenericNode;
import org.jgrapht.DirectedGraph;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Provides the basic labeling of nodes and genericEdges Note: the basic labels
 * are not the canonical labels
 */
public class LabelCreator {


    /**
     * Since the labels based on more that one part, this parts are separated by
     * an Separator instead of just putting them together to avoid mistakes,
     * like it would be in, for example: part 1: aba and part 2: c --> abac,
     * would be the samt label as part 1: ab and part 2: ac --> abac
     */
    private static final String SEPARATOR = System
            .getProperty("line.separator");
    static Map<GenericEdge, String> genericEdgeNameCache = new HashMap<>();
    // ** Part 3 - ModelCD - eScan
    static Map<GenericNode, String> eScanNodeCache = new HashMap<>();
    static Map<GenericEdge, String> eScanSimpleEdgeCache = new HashMap<>();
    static Map<GenericEdge, String> eScanSimpleEdgeCache2 = new HashMap<>();

    public static String getSeparator() {
        return SEPARATOR;
    }

    public static String getGenericEdgeToString(GenericEdge genericEdge) {
        if (genericEdgeNameCache.containsKey(genericEdge))
            return genericEdgeNameCache.get(genericEdge);

        genericEdgeNameCache.put(genericEdge, genericEdge.getEquivalenceClassLabel());
        return genericEdge.getEquivalenceClassLabel();
    }

    /**
     */
    public static String getModelCdNodeLabel(GenericNode node) {
        if (eScanNodeCache.containsKey(node)) {
            return eScanNodeCache.get(node);
        }

        String result = node.getEquivalenceClassLabel();

        eScanNodeCache.put(node, result);
        return result;
    }

    public static String getSimpleGenericEdgeLabel(GenericEdge genericEdge) {
        if (eScanSimpleEdgeCache.containsKey(genericEdge))
            return eScanSimpleEdgeCache.get(genericEdge);


        eScanSimpleEdgeCache.put(genericEdge, genericEdge.getEquivalenceClassLabel());
        return genericEdge.getEquivalenceClassLabel();
    }

    public static String getModelCdEdgeLabel(GenericEdge h,
                                             DirectedGraph<GenericNode, GenericEdge> graph, int indexSourceNode,
                                             int indexTargetNode) {
        return getSimpleGenericEdgeLabel(h) +
                " " +
                indexSourceNode +
                " " +
                getModelCdNodeLabel(graph.getEdgeSource(h)) +
                " " +
                indexTargetNode +
                " " +
                getModelCdNodeLabel(graph.getEdgeTarget(h)) +
                SEPARATOR;
    }

    public static String getSimpleModelCdEdgeLabel(GenericEdge h,
                                                   DirectedGraph<GenericNode, GenericEdge> graph) {
        if (eScanSimpleEdgeCache2.containsKey(h))
            return eScanSimpleEdgeCache2.get(h);


        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getSimpleGenericEdgeLabel(h));
        stringBuilder.append(" ");
        stringBuilder
                .append(getModelCdNodeLabel(graph.getEdgeSource(h)));
        stringBuilder.append(" ");
        stringBuilder
                .append(getModelCdNodeLabel(graph.getEdgeTarget(h)));
        stringBuilder.append(SEPARATOR);
        eScanSimpleEdgeCache2.put(h, stringBuilder.toString());
        return stringBuilder.toString();
    }

    // ** Part 4 - CloneDetective

    public static String getCloneDetectiveNodeLabel(GenericNode node) {
        return getModelCdNodeLabel(node);
    }

    public static boolean haveEqualCloneDetectiveLabels(GenericNode node1, GenericNode node2) {
        return getCloneDetectiveNodeLabel(node1).equals(
                getCloneDetectiveNodeLabel(node2));
    }

    public static boolean haveEqualCloneDetectiveLabels(GenericNode node1, GenericNode node2,
                                                        GenericNode node3, GenericNode node4) {

        return (LabelCreator.haveEqualCloneDetectiveLabels(node1, node2))
                && (LabelCreator.haveEqualCloneDetectiveLabels(node1, node3))
                && (LabelCreator.haveEqualCloneDetectiveLabels(node1, node4));
    }

    public static boolean haveEqualCloneDetectiveLabels(List<GenericNode> nodes) {
        String label1 = getCloneDetectiveNodeLabel(nodes.get(0));
        boolean labelsAreEqual = true;
        for (GenericNode node : nodes) {
            int index = nodes.indexOf(node);
            if (!(label1.equals(getCloneDetectiveNodeLabel(node)))) {
                labelsAreEqual = false;
            }
        }
        return labelsAreEqual;
    }

    public static boolean haveEqualCloneDetectiveGenericEdgeLabels(
            GenericEdge genericEdge1, GenericEdge genericEdge2) {
        return getSimpleGenericEdgeLabel(genericEdge1).equals(
                getSimpleGenericEdgeLabel(genericEdge2));
    }

    public static boolean haveEqualCloneDetectiveGenericEdgeLabels(
            List<GenericEdge> genericEdges) {
        String label1 = getSimpleGenericEdgeLabel(genericEdges.get(0));
        boolean labelsAreEqual = true;
        for (GenericEdge genericEdge : genericEdges) {
            int index = genericEdges.indexOf(genericEdge);
            if (!(label1.equals(getSimpleGenericEdgeLabel(genericEdges
                    .get(index))))) {
                labelsAreEqual = false;
            }
        }
        return labelsAreEqual;
    }

}
