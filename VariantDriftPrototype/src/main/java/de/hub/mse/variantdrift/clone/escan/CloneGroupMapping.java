package de.hub.mse.variantdrift.clone.escan;

import de.hub.mse.variantdrift.clone.models.GenericEdge;
import org.eclipse.emf.ecore.resource.Resource;

import java.util.Map;
import java.util.Set;

public record CloneGroupMapping(Set<Resource> rules,
                                Map<GenericEdge, Map<Resource, GenericEdge>> edgeMappings,
                                Map<Object, Map<Resource, Object>> attributeMappings) {

    public Set<Resource> getResources() {
        return this.rules;
    }

    public Map<GenericEdge, Map<Resource, GenericEdge>> getGenericEdgeMappings() {
        return this.edgeMappings;
    }

//    public Map<Object, Map<Resource, Object>> getObjectMappings() {
//        return this.attributeMappings;
//    }

    public int getNumberOfCommonGenericEdges() {
        return this.edgeMappings.keySet().size() / this.rules.size();
    }

    public int getNumberOfCommonObjects() {
        return this.attributeMappings.keySet().size() / this.rules.size();
    }

    public int hashCode() {
        return 0;
    }

    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (o instanceof CloneGroupMapping c) {
            if (!this.rules.equals(c.rules)) {
                return false;
            } else if (!this.edgeMappings.equals(c.edgeMappings)) {
                return false;
            } else {
                return this.attributeMappings.equals(c.attributeMappings);
            }
        }
        return false;
    }

    public String toString() {
        String res = "CloneGroupMapping - rules: " + this.rules.toString();
        res = res + "\nCloneGroupMapping - edges: " + this.edgeMappings.toString();
        res = res + "\nCloneGroupMapping - attributes: " + this.attributeMappings.toString() + "\n\n";
        return res;
    }
}
