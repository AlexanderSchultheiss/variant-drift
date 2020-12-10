package de.hub.mse.variantdrift.clone.escan;

import de.hub.mse.variantdrift.clone.models.GenericEdge;
import de.hub.mse.variantdrift.clone.models.GenericNode;
import de.uni_marburg.fb12.swt.cloneDetection.cloneDetection.NodeUtility;
import org.eclipse.emf.ecore.EReference;
import org.jgrapht.DirectedGraph;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class LabelCreator {
    private static final String SEPARATOR = System.getProperty("line.separator");
//    static Map<Attribute, String> attributeNodeNameCache = new HashMap();
    static Map<GenericEdge, String> capsuleEdgeNameCache = new HashMap();
//    static Map<Attribute, String> attributeNameCache = new HashMap();
    static Map<GenericNode, String> eScanNodeCache = new HashMap();
    static Map<GenericEdge, String> eScanSimpleEdgeCache = new HashMap();
    static Map<GenericEdge, String> eScanSimpleEdgeCache2 = new HashMap();

    public LabelCreator() {
    }

    public static String getSeparator() {
        return SEPARATOR;
    }

//    public static String getGenericEdgeToString(GenericEdge capsuleEdge) {
//        if (capsuleEdgeNameCache.containsKey(capsuleEdge)) {
//            return (String)capsuleEdgeNameCache.get(capsuleEdge);
//        } else {
//            String result = null;
//            if (capsuleEdge.isAttributeEdge()) {
//                String var10000 = capsuleEdge.getRule().getName();
//                result = var10000 + " - " + getAttributeLabel(capsuleEdge.getAttribute());
//            } else {
//                StringBuilder stringBuilder = new StringBuilder();
//                Action action = capsuleEdge.getAction();
//                EReference type = capsuleEdge.getType();
//                if (action != null) {
//                    if (action.toString().equals("preserve")) {
//                        stringBuilder.append("p ");
//                    }
//
//                    if (action.toString().equals("delete")) {
//                        stringBuilder.append("d ");
//                    }
//
//                    if (action.toString().equals("create")) {
//                        stringBuilder.append("c ");
//                    }
//
//                    if (action.toString().equals("forbid")) {
//                        stringBuilder.append("f ");
//                    }
//
//                    if (action.toString().equals("require")) {
//                        stringBuilder.append("r ");
//                    }
//                }
//
//                if (type != null) {
//                    stringBuilder.append(type.getName());
//                }
//
//                result = stringBuilder.toString();
//            }
//
//            capsuleEdgeNameCache.put(capsuleEdge, result);
//            return result;
//        }
//    }

//    public static String getAttributeLabel(Attribute attribute) {
//        if (attributeNameCache.containsKey(attribute)) {
//            return (String)attributeNameCache.get(attribute);
//        } else {
//            StringBuilder stringBuilder = new StringBuilder();
//            stringBuilder.append("a ");
//            stringBuilder.append(attribute.getType().getName());
//            stringBuilder.append(attribute.getValue());
//            attributeNameCache.put(attribute, stringBuilder.toString());
//            return stringBuilder.toString();
//        }
//    }

    public static String getModelCdNodeLabel(GenericNode node, DirectedGraph<GenericNode, GenericEdge> graph) {
        if (eScanNodeCache.containsKey(node)) {
            return (String)eScanNodeCache.get(node);
        } else {
            String result = node.getEquivalenceClassLabel();

                eScanNodeCache.put(node, result);
                return result;
        }
    }

    public static String getSimpleGenericEdgeLabel(GenericEdge capsuleEdge) {
        if (eScanSimpleEdgeCache.containsKey(capsuleEdge)) {
            return (String)eScanSimpleEdgeCache.get(capsuleEdge);
        } else {
            String result = capsuleEdge.getEquivalenceClassLabel();

            eScanSimpleEdgeCache.put(capsuleEdge, result);
            return result;
        }
    }

    public static String getModelCdEdgeLabel(GenericEdge h, DirectedGraph<GenericNode, GenericEdge> graph, int indexSourceNode, int indexTargetNode) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getSimpleGenericEdgeLabel(h));
        stringBuilder.append(" ");
        stringBuilder.append(indexSourceNode);
        stringBuilder.append(" ");
        stringBuilder.append(getModelCdNodeLabel((GenericNode)graph.getEdgeSource(h), graph));
        stringBuilder.append(" ");
        stringBuilder.append(indexTargetNode);
        stringBuilder.append(" ");
        stringBuilder.append(getModelCdNodeLabel((GenericNode)graph.getEdgeTarget(h), graph));
        stringBuilder.append(SEPARATOR);
        return stringBuilder.toString();
    }

    public static String getSimpleModelCdEdgeLabel(GenericEdge h, DirectedGraph<GenericNode, GenericEdge> graph) {
        if (eScanSimpleEdgeCache2.containsKey(h)) {
            return (String)eScanSimpleEdgeCache2.get(h);
        } else {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(getSimpleGenericEdgeLabel(h));
            stringBuilder.append(" ");
            stringBuilder.append(getModelCdNodeLabel((GenericNode)graph.getEdgeSource(h), graph));
            stringBuilder.append(" ");
            stringBuilder.append(getModelCdNodeLabel((GenericNode)graph.getEdgeTarget(h), graph));
            stringBuilder.append(SEPARATOR);
            eScanSimpleEdgeCache2.put(h, stringBuilder.toString());
            return stringBuilder.toString();
        }
    }

    public static String getCloneDetectiveNodeLabel(GenericNode node, DirectedGraph<GenericNode, GenericEdge> graph) {
        return getModelCdNodeLabel(node, graph);
    }

    public static boolean haveEqualCloneDetectiveLabels(GenericNode node1, GenericNode node2, DirectedGraph<GenericNode, GenericEdge> graph1, DirectedGraph<GenericNode, GenericEdge> graph2) {
        return getCloneDetectiveNodeLabel(node1, graph1).equals(getCloneDetectiveNodeLabel(node2, graph2));
    }

//    public static boolean haveEqualCloneDetectiveLabels(GenericNode node1, GenericNode node2, GenericNode node3, GenericNode node4, DirectedGraph<GenericNode, GenericEdge> graph1, DirectedGraph<GenericNode, GenericEdge> graph2, DirectedGraph<Node, GenericEdge> graph3, DirectedGraph<Node, GenericEdge> graph4) {
//        return haveEqualCloneDetectiveLabels(node1, node2, graph1, graph2) && haveEqualCloneDetectiveLabels(node1, node3, graph1, graph3) && haveEqualCloneDetectiveLabels(node1, node4, graph1, graph4);
//    }

    public static boolean haveEqualCloneDetectiveLabels(List<GenericNode> nodes, List<DirectedGraph<GenericNode, GenericEdge>> graphs) {
        String label1 = getCloneDetectiveNodeLabel((GenericNode)nodes.get(0), (DirectedGraph)graphs.get(0));
        boolean labelsAreEqual = true;
        Iterator var4 = nodes.iterator();

        while(var4.hasNext()) {
            GenericNode node = (GenericNode)var4.next();
            int index = nodes.indexOf(node);
            if (!label1.equals(getCloneDetectiveNodeLabel(node, (DirectedGraph)graphs.get(index)))) {
                labelsAreEqual = false;
            }
        }

        return labelsAreEqual;
    }

    public static boolean haveEqualCloneDetectiveGenericEdgeLabels(GenericEdge capsuleEdge1, GenericEdge capsuleEdge2) {
        return getSimpleGenericEdgeLabel(capsuleEdge1).equals(getSimpleGenericEdgeLabel(capsuleEdge2));
    }

    public static boolean haveEqualCloneDetectiveGenericEdgeLabels(List<GenericEdge> capsuleEdges) {
        String label1 = getSimpleGenericEdgeLabel((GenericEdge)capsuleEdges.get(0));
        boolean labelsAreEqual = true;
        Iterator var3 = capsuleEdges.iterator();

        while(var3.hasNext()) {
            GenericEdge capsuleEdge = (GenericEdge)var3.next();
            int index = capsuleEdges.indexOf(capsuleEdge);
            if (!label1.equals(getSimpleGenericEdgeLabel((GenericEdge)capsuleEdges.get(index)))) {
                labelsAreEqual = false;
            }
        }

        return labelsAreEqual;
    }
}
