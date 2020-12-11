package de.hub.mse.variantdrift.clone.escan;

import de.hub.mse.variantdrift.clone.models.GenericEdge;
import de.hub.mse.variantdrift.clone.models.GenericGraph;
import de.hub.mse.variantdrift.clone.models.GenericNode;
import de.uni_marburg.fb12.swt.cloneDetection.atl.CloneMetricResults;

import java.util.*;

public class CloneMatrix {
    private List<List<GenericNode>> nodeMatrix = new LinkedList<>();
    private List<List<GenericEdge>> edgeMatrix = new LinkedList<>();
    private CloneMetricResults metricResult = null;

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
        String res = "";
        for (List<GenericEdge> row : edgeMatrix) {
            for (GenericEdge edge : row) {
                res = res + edge.toString() + "\t" + "*" + "\t";

            }
            res = res + "\n";
        }
        return res;
    }

    public int getSize() {
        return toMetrics().getSize();
    }

    public CloneMetricResults toMetrics() {
        if (metricResult == null) {
            int numberOfModules = 0;
            int numberOfRules = getRuleList().size();
            int numberOfInElements = 0;
            int numberOfFilters = 0;
            int numberOfVariables = 0;
            int numberOfOutElements = 0;
            int numberOfBindings = 0;

            if (nodeMatrix.isEmpty()) {
                metricResult = new CloneMetricResults(-1, -1, -1, -1, -1, -1, -1);
            } else {
                List<GenericNode> firstEntry = nodeMatrix.get(0);
//				for (GenericNode o : firstEntry) {
//					if (o instanceof InPatternElement)
//						numberOfInElements++;
//					else if (o instanceof OutPatternElement)
//						numberOfOutElements++;
//					else if (o instanceof Filter)
//						numberOfFilters++;
//					else if (o instanceof Variable)
//						numberOfVariables++;
//					else if (o instanceof Binding)
//						numberOfBindings++;
//				}
            }

            metricResult = new CloneMetricResults(numberOfModules, numberOfRules, numberOfFilters, numberOfVariables, numberOfInElements,
                    numberOfOutElements, numberOfBindings);
        }
        return metricResult;
    }
}
