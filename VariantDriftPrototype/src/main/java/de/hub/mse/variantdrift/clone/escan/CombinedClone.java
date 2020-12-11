package de.hub.mse.variantdrift.clone.escan;

import de.hub.mse.variantdrift.clone.models.GenericEdge;
import de.hub.mse.variantdrift.clone.models.GenericGraph;
import de.hub.mse.variantdrift.clone.models.GenericNode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CombinedClone {
    Map<GenericGraph, GenericGraph> cloneGraphs;

    private CombinedClone(Map<GenericGraph, GenericGraph> cloneGraphs) {
        this.cloneGraphs = cloneGraphs;
    }

    public static Map<GenericGraph, GenericGraph> fromEScanResult(CloneGroupDetectionResultAsCloneMatrix result) {
        Map<GenericGraph, GenericGraph> cloneGraphs = new HashMap<>();
        for (var cloneGroup : result.getCloneGroups()) {
            // Retrieve the nodes
            var nodeMatrix = cloneGroup.getNodeMatrix();
            // Retrieve the edges
            var edgeMatrix = cloneGroup.getEdgeMatrix();

            if (nodeMatrix.size() != edgeMatrix.size()) {
                // My assumption about the result format is wrong in this case
                throw new RuntimeException("Assumption wrong!");
            }

            // Add the nodes to their corresponding clone graphs
            for (List<GenericNode> nodeList : nodeMatrix) {
                for (GenericNode node : nodeList) {
                    var model = node.getModel();
                    GenericGraph cloneGraph;
                    if (cloneGraphs.containsKey(model)) {
                        cloneGraph = cloneGraphs.get(model);
                    } else {
                        cloneGraph = new GenericGraph(model.getLabel());
                        cloneGraphs.put(model, cloneGraph);
                    }
                    cloneGraph.getNodes().add(node);
                }
            }

            // Add the edges to their corresponding clone graphs
            for (List<GenericEdge> edgeList : edgeMatrix) {
                for (GenericEdge edge : edgeList) {
                    var model = edge.getModel();
                    GenericGraph cloneGraph;
                    if (cloneGraphs.containsKey(model)) {
                        cloneGraph = cloneGraphs.get(model);
                    } else {
                        cloneGraph = new GenericGraph(model.getLabel());
                        cloneGraphs.put(model, cloneGraph);
                    }
                    cloneGraph.getEdges().add(edge);
                }
            }
        }
        return cloneGraphs;
    }
}
