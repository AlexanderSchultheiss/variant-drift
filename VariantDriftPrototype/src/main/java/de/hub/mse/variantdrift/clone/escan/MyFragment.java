package de.hub.mse.variantdrift.clone.escan;

import de.uni_marburg.fb12.swt.cloneDetection.henshinToIntegrated.CapsuleEdge;
import de.uni_marburg.fb12.swt.cloneDetection.modelCdCanonicalLabel.CanonicalLabelForFragmentCreator;
import org.eclipse.emf.henshin.model.Attribute;
import org.eclipse.emf.henshin.model.Edge;
import org.eclipse.emf.henshin.model.Node;
import org.eclipse.emf.henshin.model.Rule;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;

import java.util.*;

public class MyFragment {
    private final List<CapsuleEdge> orderedCapsuleEdges;
    private final Rule rule;
    private final DirectedGraph<Node, CapsuleEdge> graph;
    private final String label;
    private CapsuleEdge lastNotDisconnectingCapsuleEdge;

    public int hashCode() {
        int res = this.rule.getName().hashCode();
        res += this.label.hashCode();
        return res;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof MyFragment fragment) {
            if (this.rule != fragment.rule) {
                return false;
            } else {
                return fragment.orderedCapsuleEdges.equals(this.orderedCapsuleEdges);
            }
        } else {
            return false;
        }
    }

    public boolean isIsomorph(MyFragment f) {
        return this.label.equals(f.getLabel());
    }

    public String toString() {
        String var10000 = this.rule.getName();
        return var10000 + "\n Label: " + this.label;
    }

    public Set<Node> getNodes() {
        Set<Node> nodes = new HashSet<>();

        for (CapsuleEdge capsuleEdge : this.orderedCapsuleEdges) {
            nodes.add(this.graph.getEdgeSource(capsuleEdge));
            nodes.add(this.graph.getEdgeTarget(capsuleEdge));
        }

        return nodes;
    }

    public Set<Node> getOriginalNodes() {
        Set<Node> nodes = new HashSet<>();

        for (CapsuleEdge capsuleEdge : this.orderedCapsuleEdges) {
            if (!capsuleEdge.isAttributeEdge()) {
                nodes.add(this.graph.getEdgeSource(capsuleEdge));
                nodes.add(this.graph.getEdgeTarget(capsuleEdge));
            } else {
                nodes.add(this.graph.getEdgeSource(capsuleEdge));
            }
        }

        return nodes;
    }

    public List<CapsuleEdge> getCapsuleEdges() {
        return this.orderedCapsuleEdges;
    }

    public int size() {
        return this.orderedCapsuleEdges.size();
    }

    public Set<Edge> getOriginalEdges() {
        Set<Edge> res = new HashSet<>();

        for (CapsuleEdge e : this.orderedCapsuleEdges) {
            if (!e.isAttributeEdge()) {
                res.add(e.getOriginalEdge());
            }
        }

        return res;
    }

    public Set<Attribute> getAttributes() {
        Set<Attribute> res = new HashSet<>();

        for (CapsuleEdge e : this.orderedCapsuleEdges) {
            if (e.isAttributeEdge()) {
                res.add(e.getAttribute());
            }
        }

        return res;
    }

    public Rule getRule() {
        return this.rule;
    }

    public DirectedGraph<Node, CapsuleEdge> getGraph() {
        return this.graph;
    }

    public MyFragment(Set<CapsuleEdge> capsuleEdges, Rule rule, DirectedGraph<Node, CapsuleEdge> graph) {
        this.rule = rule;
        this.graph = graph;
        DirectedGraph<Node, CapsuleEdge> fragmentGraph = this.getMyFragmentAsGraph(capsuleEdges, graph);
        Map<String, List<CapsuleEdge>> labelToOrderedCapsuleEdges = CanonicalLabelForFragmentCreator.getCanonicalLabel(fragmentGraph);
        this.label = labelToOrderedCapsuleEdges.keySet().iterator().next();
        this.orderedCapsuleEdges = labelToOrderedCapsuleEdges.get(this.label);
    }

//    public boolean isSubMyFragment(MyFragment biggerMyFragment) {
//        if (biggerMyFragment.orderedCapsuleEdges.size() < this.orderedCapsuleEdges.size()) {
//        }
//
//        Iterator var2 = this.orderedCapsuleEdges.iterator();
//
//        CapsuleEdge e;
//        do {
//            if (!var2.hasNext()) {
//                return true;
//            }
//
//            e = (CapsuleEdge)var2.next();
//        } while(biggerMyFragment.orderedCapsuleEdges.contains(e));
//
//        return false;
//    }

    public boolean isNodeOverlapping(MyFragment f) {
        if (this.rule != f.getRule()) {
            return false;
        } else {
            Set<Node> nodesThis = new HashSet<>();
            Set<Node> nodesF = new HashSet<>();
            DirectedGraph<Node, CapsuleEdge> graphF = f.getGraph();

            for(CapsuleEdge e : this.orderedCapsuleEdges) {
                nodesThis.add(this.graph.getEdgeSource(e));
                nodesThis.add(this.graph.getEdgeTarget(e));
            }

            for (CapsuleEdge e : f.orderedCapsuleEdges) {
                nodesF.add(graphF.getEdgeSource(e));
                nodesF.add(graphF.getEdgeTarget(e));
            }

            Iterator<Node> var5 = nodesThis.iterator();

            Node n;
            do {
                if (!var5.hasNext()) {
                    return false;
                }

                n = var5.next();
            } while(!nodesF.contains(n));

            return true;
        }
    }

    public boolean isGeneratingParent(MyFragment ckp1) {
        if (ckp1.getRule() != this.rule) {
            return false;
        } else {
            CapsuleEdge additionalEdge = null;
            boolean foundAdditionalEdge = false;

            for (CapsuleEdge e : ckp1.orderedCapsuleEdges) {
                if (!this.orderedCapsuleEdges.contains(e)) {
                    if (foundAdditionalEdge) {
                        return false;
                    }

                    additionalEdge = e;
                    foundAdditionalEdge = true;
                }
            }

            return additionalEdge == ckp1.lastNotDisconnectingCapsuleEdge();
        }
    }

    private CapsuleEdge lastNotDisconnectingCapsuleEdge() {
        if (this.lastNotDisconnectingCapsuleEdge != null) {
            return this.lastNotDisconnectingCapsuleEdge;
        } else {
            Iterator<CapsuleEdge> var1 = this.orderedCapsuleEdges.iterator();

            CapsuleEdge testEdge;
            do {
                if (!var1.hasNext()) {
                    return null;
                }

                testEdge = var1.next();
            } while(this.isTheOnlyConnectingCapsuleEdge(testEdge));

            this.lastNotDisconnectingCapsuleEdge = testEdge;
            return this.lastNotDisconnectingCapsuleEdge;
        }
    }

    private boolean isTheOnlyConnectingCapsuleEdge(CapsuleEdge e) {
        Set<CapsuleEdge> capsuleEdgesWithoutE = new HashSet<>();

        for (CapsuleEdge edge : this.orderedCapsuleEdges) {
            if (edge != e) {
                capsuleEdgesWithoutE.add(edge);
            }
        }

        return !isConnected(capsuleEdgesWithoutE, this.graph);
    }

    public static boolean isConnected(Set<CapsuleEdge> testCapsuleEdgeSet, DirectedGraph<Node, CapsuleEdge> graph) {
        if (testCapsuleEdgeSet.size() == 1) {
            return true;
        } else {
            Set<Node> nodes = new HashSet<>();
            Set<CapsuleEdge> successfulTestedCapsuleEdges = new HashSet<>();
            Iterator<CapsuleEdge> testcapsuleEdgesetIterator = testCapsuleEdgeSet.iterator();
            if (testcapsuleEdgesetIterator.hasNext()) {
                CapsuleEdge e = testcapsuleEdgesetIterator.next();
                successfulTestedCapsuleEdges.add(e);
                nodes.add(graph.getEdgeSource(e));
                nodes.add(graph.getEdgeTarget(e));
            }

            boolean foundSuccessfulTestedCapsuleEdges;

            label43:
            do {
                if (testCapsuleEdgeSet.size() == successfulTestedCapsuleEdges.size()) {
                    return true;
                }

                foundSuccessfulTestedCapsuleEdges = false;
                Iterator<CapsuleEdge> var6 = testCapsuleEdgeSet.iterator();

                while(true) {
                    CapsuleEdge e;
                    Node source;
                    Node target;
                    do {
                        do {
                            if (!var6.hasNext()) {
                                continue label43;
                            }

                            e = var6.next();
                        } while(successfulTestedCapsuleEdges.contains(e));

                        source = graph.getEdgeSource(e);
                        target = graph.getEdgeTarget(e);
                    } while(!nodes.contains(source) && !nodes.contains(target));

                    nodes.add(source);
                    nodes.add(target);
                    successfulTestedCapsuleEdges.add(e);
                    foundSuccessfulTestedCapsuleEdges = true;
                }
            } while(foundSuccessfulTestedCapsuleEdges);

            return false;
        }
    }

    public String getLabel() {
        return this.label;
    }

    public Set<MyFragment> extensOp(Set<CapsuleEdge> onlyThisCapsuleEdges) {
        Set<MyFragment> res = new HashSet<>();

        for (CapsuleEdge capsuleEdge : onlyThisCapsuleEdges) {
            if (!this.orderedCapsuleEdges.contains(capsuleEdge) && this.graph.containsEdge(capsuleEdge) && this.isConnectedTo(capsuleEdge, this.orderedCapsuleEdges, this.graph)) {
                Set<CapsuleEdge> tempCapsuleEdges = new HashSet<>(this.orderedCapsuleEdges);
                tempCapsuleEdges.add(capsuleEdge);
                MyFragment temp = new MyFragment(tempCapsuleEdges, this.rule, this.graph);
                res.add(temp);
            }
        }

        return res;
    }

    public MyFragment extensOp(CapsuleEdge capsuleEdge) {
        if (!this.orderedCapsuleEdges.contains(capsuleEdge) && this.graph.containsEdge(capsuleEdge) && this.isConnectedTo(capsuleEdge, this.orderedCapsuleEdges, this.graph)) {
            Set<CapsuleEdge> tempCapsuleEdges = new HashSet<>(this.orderedCapsuleEdges);
            tempCapsuleEdges.add(capsuleEdge);
            return new MyFragment(tempCapsuleEdges, this.rule, this.graph);
        } else {
            return null;
        }
    }

    private boolean isConnectedTo(CapsuleEdge e, List<CapsuleEdge> listCapsuleEdges, DirectedGraph<Node, CapsuleEdge> graph) {
        if (!e.isAttributeEdge()) {
            if (e.getOriginalEdge().getActionEdge().getGraph().getRule() != this.rule) {
                return false;
            }
        } else if (e.getAttribute().getActionAttribute().getGraph().getRule() != this.rule) {
            return false;
        }

        Set<Node> nodes = new HashSet<>();

        for (CapsuleEdge edge : listCapsuleEdges) {
            nodes.add(graph.getEdgeSource(edge));
            nodes.add(graph.getEdgeTarget(edge));
        }

        if (nodes.contains(graph.getEdgeSource(e))) {
            return true;
        } else {
            return nodes.contains(graph.getEdgeTarget(e));
        }
    }

    private DirectedGraph<Node, CapsuleEdge> getMyFragmentAsGraph(Set<CapsuleEdge> capsuleEdges, DirectedGraph<Node, CapsuleEdge> graph) {
        DirectedGraph<Node, CapsuleEdge> fragmentGraph = new DefaultDirectedGraph<>(CapsuleEdge.class);

        for (CapsuleEdge capsuleEdge : capsuleEdges) {
            Node source = graph.getEdgeSource(capsuleEdge);
            Node target = graph.getEdgeTarget(capsuleEdge);
            fragmentGraph.addVertex(source);
            fragmentGraph.addVertex(target);
            fragmentGraph.addEdge(source, target, capsuleEdge);
        }

        return fragmentGraph;
    }
}
