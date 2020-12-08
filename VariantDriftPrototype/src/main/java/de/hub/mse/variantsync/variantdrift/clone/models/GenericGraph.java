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
    Map<EObject, GenericNode> eObject2Node = null;
    Map<GenericNode, EObject> node2eObject = null;
    Map<EReference, GenericEdge> eReference2Edge = null;
    Map<GenericEdge, EReference> edge2eReference = null;

    public GenericGraph() {
        this.nodes = new HashSet<>();
        this.edges = new HashSet<>();
    }

    public GenericGraph(Set<INode> nodes, Set<IDirectedEdge> edges) {
        this.nodes = Objects.requireNonNull(nodes);
        this.edges = Objects.requireNonNull(edges);
    }

    public void setEObject2Node(Map<EObject, GenericNode> eObject2Node) {
        this.eObject2Node = eObject2Node;
    }

    public void setNode2eObject(Map<GenericNode, EObject> node2eObject) {
        this.node2eObject = node2eObject;
    }

    public void seteReference2Edge(Map<EReference, GenericEdge> eReference2Edge) {
        this.eReference2Edge = eReference2Edge;
    }

    public void setEdge2eReference(Map<GenericEdge, EReference> edge2eReference) {
        this.edge2eReference = edge2eReference;
    }

    public EObject getEObject(GenericNode node) {
        return this.node2eObject.get(node);
    }

    public INode getGenericNode(EObject eObject) {
        return this.eObject2Node.get(eObject);
    }

    public EReference getEReference(GenericEdge edge) {
        return this.edge2eReference.get(edge);
    }

    public GenericEdge getGenericEdge(EReference reference) {
        return this.eReference2Edge.get(reference);
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
        GenericGraph graph = new GenericGraph(nodes, edges);
        Map<EObject, GenericNode> eObject2Node = new HashMap<>();
        Map<GenericNode, EObject> node2eObject = new HashMap<>();
        Map<EReference, GenericEdge> eReference2Edge = new HashMap<>();
        Map<GenericEdge, EReference> edge2eReference = new HashMap<>();

        for (var node : nodes) {
            GenericNode genericNode = (GenericNode) node;
            var eObject = this.getEObject(genericNode);
            eObject2Node.put(eObject, genericNode);
            node2eObject.put(genericNode, eObject);
        }

        for (var edge: edges) {
            var genericEdge = (GenericEdge) edge;
            var eReference = this.getEReference(genericEdge);
            eReference2Edge.put(eReference, genericEdge);
            edge2eReference.put(genericEdge, eReference);
        }
        graph.setNode2eObject(node2eObject);
        graph.setEObject2Node(eObject2Node);
        graph.seteReference2Edge(eReference2Edge);
        graph.setEdge2eReference(edge2eReference);
        return graph;
    }
}
