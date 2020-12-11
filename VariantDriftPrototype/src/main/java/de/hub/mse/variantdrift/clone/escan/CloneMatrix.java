package de.hub.mse.variantdrift.clone.escan;

import de.hub.mse.variantdrift.clone.models.GenericEdge;
import de.hub.mse.variantdrift.clone.models.GenericGraph;
import de.hub.mse.variantdrift.clone.models.GenericNode;

import java.util.*;

public class CloneMatrix {
    private List<List<GenericNode>> nodeMatrix = new LinkedList<>();
    private List<List<GenericEdge>> edgeMatrix = new LinkedList<>();
    private CloneMetricResult metricResult = null;

    public CloneMatrix() {
    }

    /**
     * The order of the rows of edgeMatrix and attributeMatrix have to be
     * according each other, means the k�ths row of both matrices have to belong
     * to the same clone
     */
    public CloneMatrix(List<List<GenericEdge>> edgeMatrix, List<List<GenericNode>> nodeMatrix) {
        this.edgeMatrix = edgeMatrix;
        this.nodeMatrix = nodeMatrix;
    }

    /**
     * @return the rules of the clonematrices as a List
     */
    public List<GenericGraph> getRuleList() {
        List<GenericGraph> allRules = new LinkedList<>();
        if (edgeMatrix.size() == 0)
            return allRules;
        if (edgeMatrix.get(0).size() != 0) {
            for (List<GenericEdge> row : edgeMatrix) {
                allRules.add(row.get(0).getModel());
            }
            return allRules;
        }
        return allRules;
    }

    /**
     * @return all Edges of this CloneMatrix
     */
    public Set<GenericEdge> getAllEdges() {
        Set<GenericEdge> allEdges = new HashSet<>();
        for (List<GenericEdge> row : edgeMatrix) {
            allEdges.addAll(row);
        }
        return allEdges;
    }

    public Set<GenericNode> getAllNodes() {
        Set<GenericNode> allNodes = new HashSet<GenericNode>();

        for (GenericEdge e : getAllEdges()) {
            allNodes.add((GenericNode) e.getSourceNode());
            allNodes.add((GenericNode) e.getTargetNode());
        }

        return allNodes;
    }

    public List<List<GenericEdge>> getEdgeMatrix() {
        return edgeMatrix;
    }

    public List<List<GenericNode>> getNodeMatrix() {
        return nodeMatrix;
    }

    public int getNumberOfCommonNodes() {
        return getAllNodes().size() / getRuleList().size();
    }

    /**
     * @return the number of cloned Edges (since this is Edges and not
     * CapsuleEdges, this means the former CapsuleEdges, which capsuled
     * Attributes are not inculded)
     */
    public int getNumberOfCommonGenericEdges() {
        if (edgeMatrix.size() != 0) {
            return edgeMatrix.get(0).size();
        }
        return 0;
    }

    @Override
    public int hashCode() {
        int res = 0;
        if (edgeMatrix != null) {
            res = res + edgeMatrix.hashCode();
        }
        return res;
    }

    // @Override
    // public String toString() {
    // String res = "";
    // res = res + "\n" + "CloneMatrix - edgeMatrix: " + "\n"
    // + toStringEdgeMatrix(edgeMatrix);
    // return res;
    // }
    //

    @Override
    public boolean equals(Object o) {
        CloneMatrix c = (CloneMatrix) o;
        if (!this.edgeMatrix.equals(c.edgeMatrix)) {
            return false;
        }
        return true;
    }

//	private List<Module> getInvolvedModules() {
//		List<Module> result = new ArrayList<Module>();
//		for (GenericGraph rule : getRuleList())
//			if (!result.contains(rule.eContainer()))
//				result.add((Module) rule.eContainer());
//		return result;
//	}

    public String toString() {
        StringBuilder sb = new StringBuilder();
        // sb.append(super.toString());
        sb.append("CG [Size ");
        sb.append(getSize());
        sb.append(", rules: ");
        sb.append(getRuleList().size());
        // sb.append(", modules: ");
        // sb.append(getInvolvedModules().size());
        sb.append(". Rules: ");
        Iterator<GenericGraph> it = getRuleList().iterator();
        while (it.hasNext()) {
            GenericGraph r = it.next();
            sb.append(r.getLabel());
            if (it.hasNext())
                sb.append(", ");
        }
        sb.append("]");
        return sb.toString();
    }

    public String getInvolvedNodesString() {
        StringBuilder result = new StringBuilder();
        Iterator<GenericNode> it = nodeMatrix.get(0).iterator();
        while (it.hasNext()) {
            GenericNode n = it.next();
            result.append(n);
            if (it.hasNext())
                result.append(", ");
        }
        return result.toString();
    }

    private String toStringEdgeMatrix(List<List<GenericEdge>> edgeMatrix) {
        StringBuilder res = new StringBuilder();
        for (List<GenericEdge> row : edgeMatrix) {
            for (GenericEdge edge : row) {
                res.append(edge.toString()).append("\t").append("*").append("\t");

            }
            res.append("\n");
        }
        return res.toString();
    }

    public int getSize() {
        return toMetrics().getSize();
    }

    public CloneMetricResult toMetrics() {
        if (metricResult == null) {
            int numberOfNodes = 0;
            int numberOfEdges = 0;

            if (nodeMatrix.isEmpty()) {
                metricResult = new CloneMetricResult(numberOfNodes, numberOfEdges);
            } else {
                List<GenericNode> firstNodeEntry = nodeMatrix.get(0);
				for (GenericNode ignored : firstNodeEntry) {
					numberOfNodes++;
				}

				List<GenericEdge> firstEdgeEntry = edgeMatrix.get(0);
				for(GenericEdge ignored : firstEdgeEntry) {
				    numberOfEdges++;
                }
            }

            metricResult = new CloneMetricResult(numberOfNodes, numberOfEdges);
        }
        return metricResult;
    }
}
