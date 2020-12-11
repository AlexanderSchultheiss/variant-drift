package de.hub.mse.variantdrift.clone.escan;

import de.hub.mse.variantdrift.clone.models.GenericEdge;
import de.hub.mse.variantdrift.clone.models.GenericNode;
import org.jgrapht.DirectedGraph;

import java.util.*;

public class CanonicalLabelForFragmentCreator {

    /**
     * System.getProperty("line.separator");
     */
    public static final String SEPARATOR = LabelCreator.getSeparator();

    private static Map<String, List<GenericEdge>> getCanonicalLabels(
            DirectedGraph<GenericNode, GenericEdge> fragmentGraph) {
        Map<String, List<GenericEdge>> labelsToOrderedGenericEdges
                = new HashMap<>();

        List<List<GenericNode>> nodeSequences = getGenericNodeSequences(fragmentGraph);

        if (nodeSequences.isEmpty()) {
            System.out
                    .println("CanonicalLabelForFragmenCreator -"
                            + " getCanonicalLabels: empty nodeSequence");
        }
        for (List<GenericNode> nodeSequence : nodeSequences) {
            List<GenericEdge> orderedGenericEdges = getOrderedGenericEdges(
                    fragmentGraph, nodeSequence);
            labelsToOrderedGenericEdges.put(
                    getCanonicalLabel(fragmentGraph, nodeSequence,
                            orderedGenericEdges), orderedGenericEdges);
        }

        return labelsToOrderedGenericEdges;
    }

    /**
     * in case there are more than one possible label, the smallest one is
     * chosen (occurs when a perfect refining of the nodeSequenzes is not found)
     */

    public static Map<String, List<GenericEdge>> getCanonicalLabel(
            DirectedGraph<GenericNode, GenericEdge> fragmentGraph) {
        Map<String, List<GenericEdge>> res = new HashMap<>();
        Map<String, List<GenericEdge>> labelsToOrderedGenericEdges
                = getCanonicalLabels(fragmentGraph);

        Set<String> labelsSet = labelsToOrderedGenericEdges.keySet();
        List<String> labels = new LinkedList<>(labelsSet);
        Collections.sort(labels);

        String label = labels.get(0);
        res.put(label, labelsToOrderedGenericEdges.get(label));
        return res;
    }

    private static String getCanonicalLabel(
            DirectedGraph<GenericNode, GenericEdge> fragmentGraph,
            List<GenericNode> orderedGenericNodes, List<GenericEdge> orderedGenericEdges) {
        StringBuilder stringBuilder = new StringBuilder();
        for (GenericEdge capsuleLink : orderedGenericEdges) {
            stringBuilder
                    .append(LabelCreator.getModelCdEdgeLabel(capsuleLink, fragmentGraph,
                            orderedGenericNodes.indexOf(fragmentGraph.getEdgeSource(capsuleLink)),
                            orderedGenericNodes.indexOf(fragmentGraph.getEdgeTarget(capsuleLink))));
        }
        return stringBuilder.toString();
    }

    private static List<GenericEdge> getOrderedGenericEdges(
            DirectedGraph<GenericNode, GenericEdge> fragmentGraph,
            List<GenericNode> orderedGenericNodes) {
        List<GenericEdge> orderedGenericEdges = new LinkedList<>();
        for (GenericNode node : orderedGenericNodes) {
            Set<GenericNode> nodesIn = new HashSet<>();
            Set<GenericEdge> ceIn = fragmentGraph.incomingEdgesOf(node);
            for (GenericEdge ce : ceIn) {
                GenericNode source = fragmentGraph.getEdgeSource(ce);
                nodesIn.add(source);
            }

            // put nodesIn in order
            List<Integer> positions = new LinkedList<>();
            for (GenericNode nIn : nodesIn) {
                int position = orderedGenericNodes.indexOf(nIn);
                if (position >= 0) {
                    positions.add(orderedGenericNodes.indexOf(nIn));
                }
            }
            Collections.sort(positions);

            for (Integer position : positions) {
                GenericNode n = orderedGenericNodes.get(position);
                orderedGenericEdges.add(fragmentGraph.getEdge(n, node));
            }

        }
        return orderedGenericEdges;
    }

    /**
     * @return List of possible GenericNodeSequences a GenericNodeSequenzes is a List of GenericNodes
     * if a perfect Partitioning could be achieved the List of possible
     * GenericNodeSequences contains only one GenericNodeSequence
     */

    private static List<List<GenericNode>> getGenericNodeSequences(
            DirectedGraph<GenericNode, GenericEdge> fragmentGraph) {
        // create Partitioning based on GenericNodeLabels
        Map<String, Set<GenericNode>> nodeLabelsToGenericNodes = new HashMap<>();
        boolean isPerfect = true;
        for (GenericNode node : fragmentGraph.vertexSet()) {
            String label = LabelCreator
                    .getModelCdNodeLabel(node);
            if (nodeLabelsToGenericNodes.containsKey(label)) {
                nodeLabelsToGenericNodes.get(label).add(node);
                isPerfect = false;
            } else {
                HashSet<GenericNode> hashSet = new HashSet<>();
                hashSet.add(node);
                nodeLabelsToGenericNodes.put(label, hashSet);
            }
        }
        if (isPerfect) {
            List<GenericNode> nodeSequenz = getGenericNodeSequenzesSimpel(nodeLabelsToGenericNodes);
            List<List<GenericNode>> res = new LinkedList<>();
            res.add(nodeSequenz);
            return res;
        }

        // refine Partitioning
        Map<String, Set<GenericNode>> extendedGenericNodeLabelsToGenericNodes = getRefineGenericNodePartitioning(
                nodeLabelsToGenericNodes, fragmentGraph);

        if (isPerfectPartitioning(extendedGenericNodeLabelsToGenericNodes, fragmentGraph)) {
            List<GenericNode> nodeSequenz = getGenericNodeSequenzesSimpel(extendedGenericNodeLabelsToGenericNodes);
            List<List<GenericNode>> res = new LinkedList<>();
            res.add(nodeSequenz);
            return res;
        }

        return getGenericNodeSequenzes(extendedGenericNodeLabelsToGenericNodes);
    }

    private static Map<String, Set<GenericNode>> getRefineGenericNodePartitioning(
            Map<String, Set<GenericNode>> nodeLabelsToGenericNodes,
            DirectedGraph<GenericNode, GenericEdge> fragmentGraph) {
        Map<String, Set<GenericNode>> extendedGenericNodeLabelsToGenericNodes = new HashMap<>();

        for (String s : nodeLabelsToGenericNodes.keySet()) {
            if (nodeLabelsToGenericNodes.get(s).size() == 1) {
                extendedGenericNodeLabelsToGenericNodes.put(s, nodeLabelsToGenericNodes.get(s));
            } else {
                extendedGenericNodeLabelsToGenericNodes.putAll(getRefinedGenericNodeSet(s,
                        nodeLabelsToGenericNodes.get(s), fragmentGraph));
            }
        }
        return extendedGenericNodeLabelsToGenericNodes;
    }

    /**
     * refine nodeset based on the incoming and outgoing edges
     */
    private static Map<String, Set<GenericNode>> getRefinedGenericNodeSet(String label,
                                                                          Set<GenericNode> nodes, DirectedGraph<GenericNode, GenericEdge> fragmentGraph) {
        Map<String, Set<GenericNode>> tempRes = new HashMap<>();
        Map<GenericNode, String> nodesToTempLabel = new HashMap<>();
        // incomingLinks
        boolean isPerfect = true;
        for (GenericNode node : nodes) {
            String newLabel = label + SEPARATOR
                    + fragmentGraph.incomingEdgesOf(node).size();
            if (tempRes.containsKey(newLabel)) {
                tempRes.get(newLabel).add(node);
                nodesToTempLabel.put(node, newLabel);
                isPerfect = false;
            } else {
                Set<GenericNode> set = new HashSet<>();
                set.add(node);
                tempRes.put(newLabel, set);
                nodesToTempLabel.put(node, newLabel);
            }
        }

        if (isPerfect) {
            return tempRes;
        }

        Map<String, Set<GenericNode>> res = new HashMap<>();
        // outgoingLinks
        for (GenericNode node : nodes) {
            String newLabel = nodesToTempLabel.get(node) + SEPARATOR
                    + fragmentGraph.outgoingEdgesOf(node).size();
            if (res.containsKey(newLabel)) {
                res.get(newLabel).add(node);
            } else {
                Set<GenericNode> set = new HashSet<>();
                set.add(node);
                res.put(newLabel, set);
            }
        }
        return res;
    }

    private static boolean isPerfectPartitioning(
            Map<String, Set<GenericNode>> nodeLabelsToGenericNodes,
            DirectedGraph<GenericNode, GenericEdge> fragmentGraph) {
        return nodeLabelsToGenericNodes.size() == fragmentGraph.vertexSet().size();

    }

    private static List<GenericNode> getGenericNodeSequenzesSimpel(
            Map<String, Set<GenericNode>> nodeLabelsToGenericNodes) {
        List<GenericNode> res = new LinkedList<>();
        List<String> labels = new LinkedList<>(nodeLabelsToGenericNodes.keySet());
        Collections.sort(labels);
        for (String label : labels) {
            if (nodeLabelsToGenericNodes.get(label).size() != 1) {
                System.out
                        .println("CanonicalLabelForFragmenCreator - getGenericNodeSequenzesSimpel: ????");
            }
            res.add(nodeLabelsToGenericNodes.get(label).iterator().next());
        }
        return res;
    }

    private static List<List<GenericNode>> getGenericNodeSequenzes(
            Map<String, Set<GenericNode>> nodeLabelsToGenericNodes) {
        int positionLastDuplicate = -1;

        List<String> labels = new LinkedList<>(nodeLabelsToGenericNodes.keySet());
        Collections.sort(labels);
        int position = -1;
        for (String label : labels) {
            position++;
            if (nodeLabelsToGenericNodes.get(label).size() != 1) {
                positionLastDuplicate = position;
            }
        }

        List<GenericNode> endTail = new LinkedList<>();
        for (int i = positionLastDuplicate + 1; i < labels.size(); i++) {
            // beyond the last duplicate there is only one GenericNode per set
            GenericNode insertGenericNode = nodeLabelsToGenericNodes.get(labels.get(i)).iterator()
                    .next();
            endTail.add(insertGenericNode);
        }

        if (positionLastDuplicate == -1) {
            List<List<GenericNode>> res = new LinkedList<>();
            res.add(endTail);
            return res;
        }

        List<List<GenericNode>> tails = getAllPermutations(nodeLabelsToGenericNodes
                .get(labels.get(0)));

        List<String> remainingLabels = labels.subList(1,
                positionLastDuplicate + 1);

        for (String label : remainingLabels) {
            List<List<GenericNode>> newTails = new LinkedList<>();
            List<List<GenericNode>> intermediateTails = getAllPermutations(nodeLabelsToGenericNodes
                    .get(label));
            for (List<GenericNode> tail : tails) {
                for (List<GenericNode> intermediateTail : intermediateTails) {
                    List<GenericNode> newTail = new LinkedList<>();
                    newTail.addAll(tail);
                    newTail.addAll(intermediateTail);
                    newTails.add(newTail);
                }

            }
            tails = newTails;
        }

        for (List<GenericNode> tail : tails) {
            tail.addAll(endTail);
        }

        return tails;
    }

    private static List<List<GenericNode>> getAllPermutations(
            Set<GenericNode> nodesOfSameLabel) {
        return Permutation.permute(nodesOfSameLabel);
    }

}
