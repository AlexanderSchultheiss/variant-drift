package de.hub.mse.variantsync.variantdrift.clone.util;

import de.hub.mse.variantsync.variantdrift.clone.models.GenericEdge;

import java.util.HashMap;
import java.util.Map;

public class EdgeMapping {
    Map<EReferenceInstance, GenericEdge> eReference2Edge = new HashMap<>();
    Map<GenericEdge, EReferenceInstance> edge2EReference = new HashMap<>();

    public boolean contains(EReferenceInstance referenceInstance) {
        return eReference2Edge.containsKey(referenceInstance);
    }

    public boolean contains(GenericEdge node) {
        return edge2EReference.containsKey(node);
    }

    public void put(EReferenceInstance referenceInstance, GenericEdge node) {
        eReference2Edge.put(referenceInstance, node);
        edge2EReference.put(node, referenceInstance);
    }

    public void put(GenericEdge node, EReferenceInstance referenceInstance) {
        edge2EReference.put(node, referenceInstance);
        eReference2Edge.put(referenceInstance, node);
    }

    public EReferenceInstance get(GenericEdge node) {
        return edge2EReference.get(node);
    }

    public GenericEdge get(EReferenceInstance referenceInstance) {
        return eReference2Edge.get(referenceInstance);
    }
}
