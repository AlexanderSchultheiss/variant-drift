package de.hub.mse.variantdrift.clone.escan;

import de.hub.mse.variantdrift.clone.models.GenericEdge;
import org.eclipse.emf.ecore.resource.Resource;

import java.util.*;

public class MyCloneMatrix {
    private List<List<GenericEdge>> edgeMatrix = new LinkedList<>();

    public MyCloneMatrix() {
    }

    public MyCloneMatrix(List<List<GenericEdge>> edgeMatrix) {
        this.edgeMatrix = edgeMatrix;
    }

    private int getNumberOfEdgeColumns() {
        return this.edgeMatrix.size() != 0 ? this.edgeMatrix.get(0).size() : 0;
    }


    public CloneGroupMapping getAsCloneGroupMapping() {
        Map<GenericEdge, Map<Resource, GenericEdge>> edgeMappings = new HashMap<>();

        int column;
        HashMap columnAttributeMapping;
        Iterator var5;
        for(column = 0; column < this.getNumberOfEdgeColumns(); ++column) {
            columnAttributeMapping = new HashMap<>();
            var5 = this.edgeMatrix.iterator();

            GenericEdge edge;
            for(var row : this.edgeMatrix) {
                edge = (GenericEdge)row.get(column);
                columnAttributeMapping.put(edge.getGraph().getResource(), edge);
            }

            for(var row : this.edgeMatrix) {
                edge = (GenericEdge)row.get(column);
                edgeMappings.put(edge, columnAttributeMapping);
            }
        }

        return new CloneGroupMapping(this.getResourceSet(), edgeMappings, null);
    }

    public List<Resource> getResourceList() {
        List<Resource> allResources = new LinkedList();
        if (this.edgeMatrix.size() == 0) {
            return allResources;
        } else {
            Iterator var2;
            List row;
            if (((List)this.edgeMatrix.get(0)).size() == 0) {
                var2 = this.attributeMatrix.iterator();

                while(var2.hasNext()) {
                    row = (List)var2.next();
                    if (!row.isEmpty()) {
                        allResources.add(((Attribute)row.get(0)).getGraph().getResource());
                    }
                }

                return allResources;
            } else {
                var2 = this.edgeMatrix.iterator();

                while(var2.hasNext()) {
                    row = (List)var2.next();
                    allResources.add(((GenericEdge)row.get(0)).getGraph().getResource());
                }

                return allResources;
            }
        }
    }

    public Set<Resource> getResourceSet() {
        List<Resource> allResources = this.getResourceList();
        Set<Resource> res = new HashSet();
        res.addAll(allResources);
        return res;
    }

    public Set<GenericEdge> getAllEdges() {
        Set<GenericEdge> allEdges = new HashSet();
        Iterator var2 = this.edgeMatrix.iterator();

        while(var2.hasNext()) {
            List<GenericEdge> row = (List)var2.next();
            allEdges.addAll(row);
        }

        return allEdges;
    }

    public Set<GenericNode> getAllGenericNodes() {
        Set<GenericNode> allGenericNodes = new HashSet();
        Iterator var2 = this.getAllEdges().iterator();

        while(var2.hasNext()) {
            GenericEdge e = (GenericEdge)var2.next();
            allGenericNodes.add(e.getSource().getActionGenericNode());
            allGenericNodes.add(e.getTarget().getActionGenericNode());
        }

        return allGenericNodes;
    }

    public Set<Attribute> getAllAttributes() {
        Set<Attribute> allAttributes = new HashSet();
        Iterator var2 = this.attributeMatrix.iterator();

        while(var2.hasNext()) {
            List<Attribute> row = (List)var2.next();
            allAttributes.addAll(row);
        }

        return allAttributes;
    }

    public List<List<GenericEdge>> getEdgeMatrix() {
        return this.edgeMatrix;
    }

    public List<List<Attribute>> getAttributeMatrix() {
        return this.attributeMatrix;
    }

    public int getNumberOfCommonGenericNodes() {
        return this.getAllGenericNodes().size() / this.getResourceList().size();
    }

    public int getNumberOfCommonEdges() {
        return this.edgeMatrix.size() != 0 ? ((List)this.edgeMatrix.get(0)).size() : 0;
    }

    public int getNumberOfCommonAttributes() {
        return this.attributeMatrix.size() != 0 ? ((List)this.attributeMatrix.get(0)).size() : 0;
    }

    public int hashCode() {
        int res = 0;
        if (this.edgeMatrix != null) {
            res += this.edgeMatrix.hashCode();
        }

        if (this.attributeMatrix != null) {
            res += this.attributeMatrix.hashCode();
        }

        return res;
    }

    public boolean equals(Object o) {
        CloneMatrix c = (CloneMatrix)o;
        if (!this.edgeMatrix.equals(c.edgeMatrix)) {
            return false;
        } else {
            return this.attributeMatrix.equals(c.attributeMatrix);
        }
    }

    public String toString() {
        String res = "";
        res = res + "\nCloneMatrix - edgeMatrix: \n" + this.toStringEdgeMatrix(this.edgeMatrix);
        res = res + "\nCloneMatrix - attributeMatrix: \n" + this.toStringAttributeMatrix(this.attributeMatrix) + "\n\n";
        return res;
    }

    private String toStringEdgeMatrix(List<List<GenericEdge>> edgeMatrix) {
        String res = "";

        for(Iterator var3 = edgeMatrix.iterator(); var3.hasNext(); res = res + "\n") {
            List<Edge> row = (List)var3.next();

            GenericEdge edge;
            for(Iterator var5 = row.iterator(); var5.hasNext(); res = res + edge.toString() + "\t*\t") {
                edge = (GenericEdge)var5.next();
            }
        }

        return res;
    }

    private String toStringAttributeMatrix(List<List<Attribute>> attributeMatrix) {
        String res = "";

        for(Iterator var3 = attributeMatrix.iterator(); var3.hasNext(); res = res + "\n") {
            List<Attribute> row = (List)var3.next();

            Attribute attribute;
            for(Iterator var5 = row.iterator(); var5.hasNext(); res = res + attribute.toString() + "\t*\t") {
                attribute = (Attribute)var5.next();
            }
        }

        return res;
    }

    public int getSize() {
        return this.getNumberOfCommonGenericNodes() + this.getNumberOfCommonAttributes() + this.getNumberOfCommonEdges();
    }
}
