package de.hub.mse.variantdrift.clone.models;

import org.conqat.engine.model_clones.model.IDirectedEdge;
import org.conqat.engine.model_clones.model.IModelGraph;
import org.conqat.engine.model_clones.model.INode;

import java.util.*;

public class GenericGraph implements IModelGraph {
    private final Set<INode> nodes;
    private final Set<IDirectedEdge> edges;
    private final String label;

    public GenericGraph(String label) {
        this.nodes = new HashSet<>();
        this.edges = new HashSet<>();
        this.label = label;
    }

    public GenericGraph(String label, Set<INode> nodes, Set<IDirectedEdge> edges) {
        this.label = label;
        this.edges = Objects.requireNonNull(edges);
        this.nodes = Objects.requireNonNull(nodes);
    }

    @Override
    public Collection<INode> getNodes() {
        return this.nodes;
    }

    @Override
    public Collection<IDirectedEdge> getEdges() {
        return this.edges;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        this.nodes.forEach(n -> sb.append(n.toString()).append("\n"));
        this.edges.forEach(e -> sb.append(e.toString()).append("\n"));
        return sb.toString();
    }

    public static GenericGraph fromGraphs(String label, Collection<GenericGraph> graphs) {
        Set<INode> combinedNodes = new HashSet<>();
        Set<IDirectedEdge> combinedEdges = new HashSet<>();

        graphs.forEach(g -> {
            combinedNodes.addAll(g.nodes);
            combinedEdges.addAll(g.edges);
        });
        return new GenericGraph(label, combinedNodes, combinedEdges);
    }

    public GenericGraph simulateSmallerGraph() {
        Set<INode> nodes = new HashSet<>();
        Set<IDirectedEdge> edges = new HashSet<>();
        for (var node : this.nodes) {
            if (node.getEquivalenceClassLabel().contains("Class")
//                    || node.getEquivalenceClassLabel().contains("Association")
//                    || node.getEquivalenceClassLabel().contains("Property")
//                    || node.getEquivalenceClassLabel().contains("Parameter")
//                    || node.getEquivalenceClassLabel().contains("Operation")
//                    || node.getEquivalenceClassLabel().contains("EnumerationLiteral")
//                    || node.getEquivalenceClassLabel().contains("Enumeration")
//                    || node.getEquivalenceClassLabel().contains("Generalization")
//                    || node.getEquivalenceClassLabel().contains("Package")
            ) {
                nodes.add(node);
            }
        }
        for (var edge : this.edges) {
            if (nodes.contains(edge.getSourceNode()) && nodes.contains(edge.getTargetNode())) {
                edges.add(edge);
            }
        }
        return new GenericGraph(label, nodes, edges);
    }

    public String getLabel() {
        return label;
    }
}
