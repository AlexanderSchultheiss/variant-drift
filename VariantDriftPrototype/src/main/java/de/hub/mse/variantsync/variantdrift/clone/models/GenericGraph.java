package de.hub.mse.variantsync.variantdrift.clone.models;

import org.conqat.engine.model_clones.model.IDirectedEdge;
import org.conqat.engine.model_clones.model.IModelGraph;
import org.conqat.engine.model_clones.model.INode;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;

import java.util.*;

public class GenericGraph implements IModelGraph {
    private final Set<INode> nodes;
    private final Set<IDirectedEdge> edges;

    public GenericGraph() {
        this.nodes = new HashSet<>();
        this.edges = new HashSet<>();
    }

    public GenericGraph(Set<INode> nodes, Set<IDirectedEdge> edges) {
        this.nodes = Objects.requireNonNull(nodes);
        this.edges = Objects.requireNonNull(edges);
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

    public static GenericGraph fromGraphs(Collection<GenericGraph> graphs) {
        Set<INode> combinedNodes = new HashSet<>();
        Set<IDirectedEdge> combinedEdges = new HashSet<>();

        graphs.forEach(g -> {
            combinedNodes.addAll(g.nodes);
            combinedEdges.addAll(g.edges);
        });
        return new GenericGraph(combinedNodes, combinedEdges);
    }

    public GenericGraph simulateSmallerGraph() {
        Set<INode> nodes = new HashSet<>();
        Set<IDirectedEdge> edges = new HashSet<>();
        for (var node : this.nodes) {
            if (node.getEquivalenceClassLabel().equals("Class")
                    || node.getEquivalenceClassLabel().equals("Association")
//                    || node.getEquivalenceClassLabel().equals("Property")
//                    || node.getEquivalenceClassLabel().equals("Parameter")
//                    || node.getEquivalenceClassLabel().equals("Operation")
                    || node.getEquivalenceClassLabel().equals("EnumerationLiteral")
                    || node.getEquivalenceClassLabel().equals("Enumeration")
                    || node.getEquivalenceClassLabel().equals("Generalization")
                    || node.getEquivalenceClassLabel().equals("Package")
            ) {
                nodes.add(node);
            }
        }
        for (var edge : this.edges) {
            if (nodes.contains(edge.getSourceNode()) && nodes.contains(edge.getTargetNode())) {
                edges.add(edge);
            }
        }
        return new GenericGraph(nodes, edges);
    }
}
