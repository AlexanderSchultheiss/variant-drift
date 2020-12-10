package de.hub.mse.variantdrift.clone.examples;

import org.conqat.engine.model_clones.model.IDirectedEdge;
import org.conqat.engine.model_clones.model.IModelGraph;
import org.conqat.engine.model_clones.model.INode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ModelGraphMock implements IModelGraph {
    public final List<INode> nodes = new ArrayList();
    public final List<IDirectedEdge> edges = new ArrayList();

    public ModelGraphMock() {
    }

    public Collection<IDirectedEdge> getEdges() {
        return this.edges;
    }

    public Collection<INode> getNodes() {
        return this.nodes;
    }
}