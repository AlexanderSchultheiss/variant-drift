package de.hub.mse.variantdrift.clone.util;

import de.hub.mse.variantdrift.clone.models.GenericEdge;
import org.conqat.engine.model_clones.model.IDirectedEdge;
import org.conqat.engine.model_clones.model.IModelGraph;
import org.conqat.engine.model_clones.model.INode;
import org.jgrapht.Graph;
import org.jgrapht.graph.DirectedPseudograph;


public class GenericGraphToJGraph {
    IModelGraph genericGraph;
    Graph<INode, IDirectedEdge> jGraph;

    public Graph<INode, IDirectedEdge> transform(IModelGraph genericGraph) {
        this.genericGraph = genericGraph;
        this.jGraph = new DirectedPseudograph<>(GenericEdge.class);

        transformNodes();
        transformEdges();

        return jGraph;
    }

    private void transformNodes() {
        for (var node : genericGraph.getNodes()) {
            jGraph.addVertex(node);
        }
    }

    private void transformEdges() {
        for (var edge : genericGraph.getEdges()) {
            if (edge.getSourceNode() == null || edge.getTargetNode() == null) {
                System.err.println("Null");
            }
            jGraph.addEdge(edge.getSourceNode(), edge.getTargetNode(), edge);
        }
    }
}
