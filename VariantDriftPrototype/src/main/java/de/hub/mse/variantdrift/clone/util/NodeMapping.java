package de.hub.mse.variantdrift.clone.util;

import de.hub.mse.variantdrift.clone.models.GenericNode;
import org.eclipse.emf.ecore.EObject;

import java.util.HashMap;
import java.util.Map;

public class NodeMapping {
    Map<EObject, GenericNode> eObject2Node = new HashMap<>();
    Map<GenericNode, EObject> node2eObject = new HashMap<>();

    public boolean contains(EObject eObject) {
        return eObject2Node.containsKey(eObject);
    }

    public boolean contains(GenericNode node) {
        return node2eObject.containsKey(node);
    }

    public void put(EObject eObject, GenericNode node) {
        eObject2Node.put(eObject, node);
        node2eObject.put(node, eObject);
    }

    public void put(GenericNode node, EObject eObject) {
        node2eObject.put(node, eObject);
        eObject2Node.put(eObject, node);
    }

    public EObject get(GenericNode node) {
        return node2eObject.get(node);
    }

    public GenericNode get(EObject eObject) {
        return eObject2Node.get(eObject);
    }
}
