package de.hub.mse.variantdrift.clone.escan;

import de.hub.mse.variantdrift.clone.models.GenericEdge;
import de.hub.mse.variantdrift.clone.models.GenericNode;
import org.jgrapht.DirectedGraph;

import java.util.*;

public class CanonicalLabelFragmentCreator {
    public static final String SEPARATOR = LabelCreator.getSeparator();

    public CanonicalLabelFragmentCreator() {
    }

    private static Map<String, List<GenericEdge>> getCanonicalLabels(DirectedGraph<GenericNode, GenericEdge> fragmentGraph) {
        Map<String, List<GenericEdge>> labelsToOrderedCapsuleEdges = new HashMap<>();
        List<List<GenericNode>> nodeSequences = getNodeSequences(fragmentGraph);
        if (nodeSequences.isEmpty()) {
            System.out.println("CanonicalLabelForFragmenCreator - getCanonicalLabels: empty nodeSequence");
        }

        for (List<GenericNode> sequence : nodeSequences) {
            List<GenericEdge> orderedCapsuleEdges = getOrderedCapsuleEdges(fragmentGraph, sequence);
            labelsToOrderedCapsuleEdges.put(getCanonicalLabel(fragmentGraph, sequence, orderedCapsuleEdges), orderedCapsuleEdges);
        }

        return labelsToOrderedCapsuleEdges;
    }

    public static Map<String, List<GenericEdge>> getCanonicalLabel(DirectedGraph<GenericNode, GenericEdge> fragmentGraph) {
        Map<String, List<GenericEdge>> res = new HashMap<>();
        Map<String, List<GenericEdge>> labelsToOrderedCapsuleEdges = getCanonicalLabels(fragmentGraph);
        Set<String> labelsSet = labelsToOrderedCapsuleEdges.keySet();
        List<String> labels = new LinkedList<>(labelsSet);
        Collections.sort(labels);
        String label = labels.get(0);
        res.put(label, labelsToOrderedCapsuleEdges.get(label));
        return res;
    }

    private static String getCanonicalLabel(DirectedGraph<GenericNode, GenericEdge> fragmentGraph, List<GenericNode> orderedNodes, List<GenericEdge> orderedCapsuleEdges) {
        StringBuilder stringBuilder = new StringBuilder();

        for (GenericEdge capsuleEdge : orderedCapsuleEdges) {
            stringBuilder.append(LabelCreator.getModelCdEdgeLabel(capsuleEdge, fragmentGraph, orderedNodes.indexOf(fragmentGraph.getEdgeSource(capsuleEdge)), orderedNodes.indexOf(fragmentGraph.getEdgeTarget(capsuleEdge))));
        }

        return stringBuilder.toString();
    }

    private static List<GenericEdge> getOrderedCapsuleEdges(DirectedGraph<GenericNode, GenericEdge> fragmentGraph, List<GenericNode> orderedNodes) {
        List<GenericEdge> orderedCapsuleEdges = new LinkedList<>();

        for (GenericNode node : orderedNodes) {
            Set<GenericNode> nodesIn = new HashSet<>();
            Set<GenericEdge> ceIn = fragmentGraph.incomingEdgesOf(node);

            for (GenericEdge ce : ceIn) {
                GenericNode nIn;
                nIn = fragmentGraph.getEdgeSource(ce);
                nodesIn.add(nIn);
            }

            List<Integer> positions = new LinkedList<>();

            for (GenericNode nIn : nodesIn) {
                int position = orderedNodes.indexOf(nIn);
                if (position >= 0) {
                    positions.add(orderedNodes.indexOf(nIn));
                }
            }

            Collections.sort(positions);

            for (Integer position : positions) {
                GenericNode n = orderedNodes.get(position);
                orderedCapsuleEdges.add(fragmentGraph.getEdge(n, node));
            }
        }

        return orderedCapsuleEdges;
    }

    private static List<List<GenericNode>> getNodeSequences(DirectedGraph<GenericNode, GenericEdge> fragmentGraph) {
        Map<String, Set<GenericNode>> nodeLabelsToNodes = new HashMap<>();
        boolean isPerfect = true;

        for (GenericNode node : fragmentGraph.vertexSet()) {
            String label = LabelCreator.getModelCdNodeLabel(node, fragmentGraph);
            if (nodeLabelsToNodes.containsKey(label)) {
                nodeLabelsToNodes.get(label).add(node);
                isPerfect = false;
            } else {
                HashSet<GenericNode> hashSet = new HashSet<>();
                hashSet.add(node);
                nodeLabelsToNodes.put(label, hashSet);
            }
        }

        if (isPerfect) {
            List<GenericNode> nodeSequenz = getNodeSequenzesSimpel(nodeLabelsToNodes);
            List<List<GenericNode>> res = new LinkedList<>();
            res.add(nodeSequenz);
            return res;
        } else {
            Map<String, Set<GenericNode>> extendedNodeLabelsToNodes = getRefineNodePartitioning(nodeLabelsToNodes, fragmentGraph);
            if (isPerfectPartitioning(extendedNodeLabelsToNodes, fragmentGraph)) {
                List<GenericNode> nodeSequenz = getNodeSequenzesSimpel(extendedNodeLabelsToNodes);
                List<List<GenericNode>> res = new LinkedList<>();
                res.add(nodeSequenz);
                return res;
            } else {
                return getNodeSequenzes(extendedNodeLabelsToNodes);
            }
        }
    }

    private static Map<String, Set<GenericNode>> getRefineNodePartitioning(Map<String, Set<GenericNode>> nodeLabelsToNodes, DirectedGraph<GenericNode, GenericEdge> fragmentGraph) {
        Map<String, Set<GenericNode>> extendedNodeLabelsToNodes = new HashMap<>();

        for (String s : nodeLabelsToNodes.keySet()) {
            if ((nodeLabelsToNodes.get(s)).size() == 1) {
                extendedNodeLabelsToNodes.put(s, nodeLabelsToNodes.get(s));
            } else {
                extendedNodeLabelsToNodes.putAll(getRefinedNodeSet(s, nodeLabelsToNodes.get(s), fragmentGraph));
            }
        }

        return extendedNodeLabelsToNodes;
    }

    private static Map<String, Set<GenericNode>> getRefinedNodeSet(String label, Set<GenericNode> nodes, DirectedGraph<GenericNode, GenericEdge> fragmentGraph) {
        Map<String, Set<GenericNode>> tempRes = new HashMap<>();
        Map<GenericNode, String> nodesToTempLabel = new HashMap<>();
        boolean isPerfect = true;

        for (GenericNode node : nodes) {
            String newLabel = label + SEPARATOR + fragmentGraph.incomingEdgesOf(node).size();
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
        } else {
            Map<String, Set<GenericNode>> res = new HashMap<>();

            for (GenericNode node : nodes) {
                String var10000 = nodesToTempLabel.get(node);
                String newLabel = var10000 + SEPARATOR + fragmentGraph.outgoingEdgesOf(node).size();
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
    }

    private static boolean isPerfectPartitioning(Map<String, Set<GenericNode>> nodeLabelsToNodes, DirectedGraph<GenericNode, GenericEdge> fragmentGraph) {
        return nodeLabelsToNodes.size() == fragmentGraph.vertexSet().size();
    }

    private static List<GenericNode> getNodeSequenzesSimpel(Map<String, Set<GenericNode>> nodeLabelsToNodes) {
        List<GenericNode> res = new LinkedList<>();
        List<String> labels = new LinkedList<>(nodeLabelsToNodes.keySet());
        Collections.sort(labels);

        String label;
        for(Iterator<String> var3 = labels.iterator(); var3.hasNext(); res.add(nodeLabelsToNodes.get(label).iterator().next())) {
            label = var3.next();
            if ((nodeLabelsToNodes.get(label)).size() != 1) {
                System.out.println("CanonicalLabelForFragmenCreator - getNodeSequenzesSimpel: ????");
            }
        }

        return res;
    }

    private static List<List<GenericNode>> getNodeSequenzes(Map<String, Set<GenericNode>> nodeLabelsToNodes) {
        int positionLastDuplicate = -1;
        List<String> labels = new LinkedList<>(nodeLabelsToNodes.keySet());
        Collections.sort(labels);
        int position = -1;

        for (String label : labels) {
            ++position;
            if ((nodeLabelsToNodes.get(label)).size() != 1) {
                positionLastDuplicate = position;
            }
        }

        List<GenericNode> endTail = new LinkedList<>();

        for(int i = positionLastDuplicate + 1; i < labels.size(); ++i) {
            GenericNode insertNode = nodeLabelsToNodes.get(labels.get(i)).iterator().next();
            endTail.add(insertNode);
        }

        if (positionLastDuplicate == -1) {
            List<List<GenericNode>> res = new LinkedList<>();
            res.add(endTail);
            return res;
        } else {
            List<List<GenericNode>> tails = getAllPermutations(nodeLabelsToNodes.get(labels.get(0)));
            List<String> remainingLabels = labels.subList(1, positionLastDuplicate + 1);

            Iterator<String> var7;
            LinkedList<List<GenericNode>> newTails;
            for(var7 = remainingLabels.iterator(); var7.hasNext(); tails = newTails) {
                String label = var7.next();
                newTails = new LinkedList<>();
                List<List<GenericNode>> intermediateTails = getAllPermutations(nodeLabelsToNodes.get(label));

                for (List<GenericNode> nodes : tails) {

                    for (List<GenericNode> tail : intermediateTails) {
                        List<GenericNode> newTail = new LinkedList<>();
                        newTail.addAll(nodes);
                        newTail.addAll(tail);
                        newTails.add(newTail);
                    }
                }
            }

            for (List<GenericNode> nodes : tails) {
                nodes.addAll(endTail);
            }

            return tails;
        }
    }

    private static List<List<GenericNode>> getAllPermutations(Set<GenericNode> nodesOfSameLabel) {
        return permute(nodesOfSameLabel);
    }

    public static List<List<GenericNode>> permute(Set<GenericNode> nodes) {
        List<List<GenericNode>> listOfLists;
        if (nodes.size() == 1) {
            List<GenericNode> arrayList = new ArrayList<>();
            arrayList.add((GenericNode)nodes.iterator().next());
            listOfLists = new ArrayList<>();
            listOfLists.add(arrayList);
            return listOfLists;
        } else {
            Set<GenericNode> setOf = new HashSet<>(nodes);
            listOfLists = new ArrayList<>();

            for (GenericNode i : nodes) {
                Set<GenericNode> setOfCopied = new HashSet<>(setOf);
                setOfCopied.remove(i);
                Set<GenericNode> isttt = new HashSet<>(setOfCopied);
                List<List<GenericNode>> permute = permute(isttt);

                for (List<GenericNode> genericNodes : permute) {
                    genericNodes.add(i);
                    listOfLists.add(genericNodes);
                }
            }

            return listOfLists;
        }
    }
}
