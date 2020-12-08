package de.hub.mse.variantsync.variantdrift.clone.models;

import org.conqat.engine.model_clones.model.IDirectedEdge;
import org.conqat.engine.model_clones.model.IModelGraph;
import org.conqat.engine.model_clones.model.INode;

import java.util.*;

public class GenericGraph implements IModelGraph {
    public final Set<INode> nodes;
    public final Set<IDirectedEdge> edges;

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
}
