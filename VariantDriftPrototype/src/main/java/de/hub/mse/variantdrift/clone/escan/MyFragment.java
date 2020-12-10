package de.hub.mse.variantdrift.clone.escan;

import de.hub.mse.variantdrift.clone.models.GenericEdge;
import de.hub.mse.variantdrift.clone.models.GenericNode;
import org.eclipse.emf.ecore.resource.Resource;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;

import java.util.*;

public class MyFragment {
    private final List<GenericEdge> orderedGenericEdges;
    private final Resource model;
    private final DirectedGraph<GenericNode, GenericEdge> graph;
    private final String label;
    private GenericEdge lastNotDisconnectingGenericEdge;

    public int hashCode() {
        int res = this.model.getURI().hashCode();
        res += this.label.hashCode();
        return res;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof MyFragment fragment) {
            if (this.model != fragment.model) {
                return false;
            } else {
                return fragment.orderedGenericEdges.equals(this.orderedGenericEdges);
            }
        } else {
            return false;
        }
    }

    public boolean isIsomorph(MyFragment f) {
        return this.label.equals(f.getLabel());
    }

    public String toString() {
        String var10000 = this.model.getURI().toString();
        return var10000 + "\n Label: " + this.label;
    }

    public Set<GenericNode> getGenericNodes() {
        Set<GenericNode> nodes = new HashSet<>();

        for (GenericEdge capsuleEdge : this.orderedGenericEdges) {
            nodes.add(this.graph.getEdgeSource(capsuleEdge));
            nodes.add(this.graph.getEdgeTarget(capsuleEdge));
        }

        return nodes;
    }

    public Set<GenericNode> getOriginalGenericNodes() {
        Set<GenericNode> nodes = new HashSet<>();

        for (GenericEdge capsuleEdge : this.orderedGenericEdges) {
                nodes.add(this.graph.getEdgeSource(capsuleEdge));
                nodes.add(this.graph.getEdgeTarget(capsuleEdge));
        }

        return nodes;
    }

    public List<GenericEdge> getGenericEdges() {
        return this.orderedGenericEdges;
    }

    public int size() {
        return this.orderedGenericEdges.size();
    }

//    public Set<Edge> getOriginalEdges() {
//        Set<Edge> res = new HashSet<>();
//
//        for (GenericEdge e : this.orderedGenericEdges) {
//            if (!e.isAttributeEdge()) {
//                res.add(e.getOriginalEdge());
//            }
//        }
//
//        return res;
//    }
//
//    public Set<Attribute> getAttributes() {
//        Set<Attribute> res = new HashSet<>();
//
//        for (GenericEdge e : this.orderedGenericEdges) {
//            if (e.isAttributeEdge()) {
//                res.add(e.getAttribute());
//            }
//        }
//
//        return res;
//    }

    public Resource getResource() {
        return this.model;
    }

    public DirectedGraph<GenericNode, GenericEdge> getGraph() {
        return this.graph;
    }

    public MyFragment(Set<GenericEdge> capsuleEdges, Resource model, DirectedGraph<GenericNode, GenericEdge> graph) {
        this.model = model;
        this.graph = graph;
        DirectedGraph<GenericNode, GenericEdge> fragmentGraph = this.getMyFragmentAsGraph(capsuleEdges, graph);
        Map<String, List<GenericEdge>> labelToOrderedGenericEdges = CanonicalLabelFragmentCreator.getCanonicalLabel(fragmentGraph);
        this.label = labelToOrderedGenericEdges.keySet().iterator().next();
        this.orderedGenericEdges = labelToOrderedGenericEdges.get(this.label);
    }

    public boolean isSubFragment(MyFragment biggerMyFragment) {
        if (biggerMyFragment.orderedGenericEdges.size() < this.orderedGenericEdges.size()) {
        }

        Iterator var2 = this.orderedGenericEdges.iterator();

        GenericEdge e;
        do {
            if (!var2.hasNext()) {
                return true;
            }

            e = (GenericEdge)var2.next();
        } while(biggerMyFragment.orderedGenericEdges.contains(e));

        return false;
    }

    public boolean isGenericNodeOverlapping(MyFragment f) {
        if (this.model != f.getResource()) {
            return false;
        } else {
            Set<GenericNode> nodesThis = new HashSet<>();
            Set<GenericNode> nodesF = new HashSet<>();
            DirectedGraph<GenericNode, GenericEdge> graphF = f.getGraph();

            for(GenericEdge e : this.orderedGenericEdges) {
                nodesThis.add(this.graph.getEdgeSource(e));
                nodesThis.add(this.graph.getEdgeTarget(e));
            }

            for (GenericEdge e : f.orderedGenericEdges) {
                nodesF.add(graphF.getEdgeSource(e));
                nodesF.add(graphF.getEdgeTarget(e));
            }

            Iterator<GenericNode> var5 = nodesThis.iterator();

            GenericNode n;
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
        if (ckp1.getResource() != this.model) {
            return false;
        } else {
            GenericEdge additionalEdge = null;
            boolean foundAdditionalEdge = false;

            for (GenericEdge e : ckp1.orderedGenericEdges) {
                if (!this.orderedGenericEdges.contains(e)) {
                    if (foundAdditionalEdge) {
                        return false;
                    }

                    additionalEdge = e;
                    foundAdditionalEdge = true;
                }
            }

            return additionalEdge == ckp1.lastNotDisconnectingGenericEdge();
        }
    }

    private GenericEdge lastNotDisconnectingGenericEdge() {
        if (this.lastNotDisconnectingGenericEdge != null) {
            return this.lastNotDisconnectingGenericEdge;
        } else {
            Iterator<GenericEdge> var1 = this.orderedGenericEdges.iterator();

            GenericEdge testEdge;
            do {
                if (!var1.hasNext()) {
                    return null;
                }

                testEdge = var1.next();
            } while(this.isTheOnlyConnectingGenericEdge(testEdge));

            this.lastNotDisconnectingGenericEdge = testEdge;
            return this.lastNotDisconnectingGenericEdge;
        }
    }

    private boolean isTheOnlyConnectingGenericEdge(GenericEdge e) {
        Set<GenericEdge> capsuleEdgesWithoutE = new HashSet<>();

        for (GenericEdge edge : this.orderedGenericEdges) {
            if (edge != e) {
                capsuleEdgesWithoutE.add(edge);
            }
        }

        return !isConnected(capsuleEdgesWithoutE, this.graph);
    }

    public static boolean isConnected(Set<GenericEdge> testGenericEdgeSet, DirectedGraph<GenericNode, GenericEdge> graph) {
        if (testGenericEdgeSet.size() == 1) {
            return true;
        } else {
            Set<GenericNode> nodes = new HashSet<>();
            Set<GenericEdge> successfulTestedGenericEdges = new HashSet<>();
            Iterator<GenericEdge> testcapsuleEdgesetIterator = testGenericEdgeSet.iterator();
            if (testcapsuleEdgesetIterator.hasNext()) {
                GenericEdge e = testcapsuleEdgesetIterator.next();
                successfulTestedGenericEdges.add(e);
                nodes.add(graph.getEdgeSource(e));
                nodes.add(graph.getEdgeTarget(e));
            }

            boolean foundSuccessfulTestedGenericEdges;

            label43:
            do {
                if (testGenericEdgeSet.size() == successfulTestedGenericEdges.size()) {
                    return true;
                }

                foundSuccessfulTestedGenericEdges = false;
                Iterator<GenericEdge> var6 = testGenericEdgeSet.iterator();

                while(true) {
                    GenericEdge e;
                    GenericNode source;
                    GenericNode target;
                    do {
                        do {
                            if (!var6.hasNext()) {
                                continue label43;
                            }

                            e = var6.next();
                        } while(successfulTestedGenericEdges.contains(e));

                        source = graph.getEdgeSource(e);
                        target = graph.getEdgeTarget(e);
                    } while(!nodes.contains(source) && !nodes.contains(target));

                    nodes.add(source);
                    nodes.add(target);
                    successfulTestedGenericEdges.add(e);
                    foundSuccessfulTestedGenericEdges = true;
                }
            } while(foundSuccessfulTestedGenericEdges);

            return false;
        }
    }

    public String getLabel() {
        return this.label;
    }

    public Set<MyFragment> extensOp(Set<GenericEdge> onlyThisGenericEdges) {
        Set<MyFragment> res = new HashSet<>();

        for (GenericEdge capsuleEdge : onlyThisGenericEdges) {
            if (!this.orderedGenericEdges.contains(capsuleEdge) && this.graph.containsEdge(capsuleEdge) && this.isConnectedTo(capsuleEdge, this.orderedGenericEdges, this.graph)) {
                Set<GenericEdge> tempGenericEdges = new HashSet<>(this.orderedGenericEdges);
                tempGenericEdges.add(capsuleEdge);
                MyFragment temp = new MyFragment(tempGenericEdges, this.model, this.graph);
                res.add(temp);
            }
        }

        return res;
    }

    public MyFragment extensOp(GenericEdge capsuleEdge) {
        if (!this.orderedGenericEdges.contains(capsuleEdge) && this.graph.containsEdge(capsuleEdge) && this.isConnectedTo(capsuleEdge, this.orderedGenericEdges, this.graph)) {
            Set<GenericEdge> tempGenericEdges = new HashSet<>(this.orderedGenericEdges);
            tempGenericEdges.add(capsuleEdge);
            return new MyFragment(tempGenericEdges, this.model, this.graph);
        } else {
            return null;
        }
    }

    private boolean isConnectedTo(GenericEdge e, List<GenericEdge> listGenericEdges, DirectedGraph<GenericNode, GenericEdge> graph) {
//        if (!e.isAttributeEdge()) {
//            if (e.getOriginalEdge().getActionEdge().getGraph().getResource() != this.model) {
//                return false;
//            }
//        } else if (e.getAttribute().getActionAttribute().getGraph().getResource() != this.model) {
//            return false;
//        }

        Set<GenericNode> nodes = new HashSet<>();

        for (GenericEdge edge : listGenericEdges) {
            nodes.add(graph.getEdgeSource(edge));
            nodes.add(graph.getEdgeTarget(edge));
        }

        if (nodes.contains(graph.getEdgeSource(e))) {
            return true;
        } else {
            return nodes.contains(graph.getEdgeTarget(e));
        }
    }

    private DirectedGraph<GenericNode, GenericEdge> getMyFragmentAsGraph(Set<GenericEdge> capsuleEdges, DirectedGraph<GenericNode, GenericEdge> graph) {
        DirectedGraph<GenericNode, GenericEdge> fragmentGraph = new DefaultDirectedGraph<>(GenericEdge.class);

        for (GenericEdge capsuleEdge : capsuleEdges) {
            GenericNode source = graph.getEdgeSource(capsuleEdge);
            GenericNode target = graph.getEdgeTarget(capsuleEdge);
            fragmentGraph.addVertex(source);
            fragmentGraph.addVertex(target);
            fragmentGraph.addEdge(source, target, capsuleEdge);
        }

        return fragmentGraph;
    }
}
