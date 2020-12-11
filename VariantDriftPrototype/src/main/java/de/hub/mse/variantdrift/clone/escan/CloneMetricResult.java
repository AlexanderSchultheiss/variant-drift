package de.hub.mse.variantdrift.clone.escan;

public class CloneMetricResult {
    private final int numberOfNodes;
    private final int numberOfEdges;

    public CloneMetricResult(int numberOfNodes, int numberOfEdges) {
        this.numberOfNodes = numberOfNodes;
        this.numberOfEdges = numberOfEdges;
    }

    public int getNumberOfNodes() {
        return numberOfNodes;
    }

    public int getNumberOfEdges() {
        return numberOfEdges;
    }

    public int getSize() {
        return numberOfNodes + numberOfEdges;
    }

    public String toString() {
        return "\t" + this.numberOfNodes + "\t" + this.numberOfEdges;
    }
}


