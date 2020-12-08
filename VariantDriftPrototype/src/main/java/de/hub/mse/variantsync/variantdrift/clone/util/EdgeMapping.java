package de.hub.mse.variantsync.variantdrift.clone.util;

import de.hub.mse.variantsync.variantdrift.clone.models.GenericEdge;
import org.eclipse.emf.ecore.EReference;

import java.util.HashMap;
import java.util.Map;

public class EdgeMapping {
        Map<EReference, GenericEdge> eReference2Edge = new HashMap<>();
        Map<GenericEdge, EReference> edge2EReference = new HashMap<>();

        public boolean contains(EReference eReference) {
            return eReference2Edge.containsKey(eReference);
        }

        public boolean contains(GenericEdge node) {
            return edge2EReference.containsKey(node);
        }

        public void put(EReference eObject, GenericEdge node) {
            eReference2Edge.put(eObject, node);
            edge2EReference.put(node, eObject);
        }

        public void put(GenericEdge node, EReference eObject) {
            edge2EReference.put(node, eObject);
            eReference2Edge.put(eObject, node);
        }

        public EReference get(GenericEdge node) {
            return edge2EReference.get(node);
        }

        public GenericEdge get(EReference eObject) {
            return eReference2Edge.get(eObject);
        }
}
