package de.hub.mse.variantdrift.clone.util;

import java.util.*;

import de.hub.mse.variantdrift.clone.models.GenericEdge;
import de.hub.mse.variantdrift.clone.models.GenericGraph;
import de.hub.mse.variantdrift.clone.models.GenericNode;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.Resource;

public class EMF2GenericGraph {

    Resource model;
    GenericGraph graph;
    NodeMapping nodeMapping;
    EdgeMapping edgeMapping;

    private EMF2GenericGraph(Resource model, String label) {
        this.model = model;
        this.graph = new GenericGraph(label);
        nodeMapping = new NodeMapping();
        edgeMapping = new EdgeMapping();
    }

    public static ParseResult transform(Resource model, String label) {
        var parser = new EMF2GenericGraph(model, label);

        parser.transformNodes();
        parser.transformEdges();

        return new ParseResult(parser.graph, parser.nodeMapping, parser.edgeMapping);
    }

    private void transformNodes() {
        for (Iterator<EObject> it = model.getAllContents(); it.hasNext(); ) {
            EObject eObject = it.next();
            String name = "";
            if (eObject.eClass().getEStructuralFeature("name") != null) {
                var tempName = eObject.eGet(eObject.eClass().getEStructuralFeature("name"));
                if (tempName != null) {
                    name = tempName.toString();
                }
            }
            GenericNode node = new GenericNode(eObject.eClass().getName() + name);
            node.setModel(graph);

            if (!nodeMapping.contains(eObject)) {
                nodeMapping.put(eObject, node);
                graph.getNodes().add(node);
            }
        }
    }

    private void transformEdges() {
        for (Iterator<EObject> it = model.getAllContents(); it.hasNext(); ) {
            EObject eObject = it.next();

            for (var eReference : eObject.eClass().getEAllReferences()) {
                if (eReference.isDerived()) {
                    continue;
                }

                if (eReference.isMany()) {
                    List<EObject> referencedObjects = (List<EObject>) eObject.eGet(eReference);
                    for (EObject referencedObject : referencedObjects) {
                        transformEdge(eReference, eObject, referencedObject);
                    }
                } else {
                    EObject referencedObject = (EObject) eObject.eGet(eReference);
                    transformEdge(eReference, eObject, referencedObject);
                }

            }
        }
    }

    private void transformEdge(EReference eReference, EObject srcObject, EObject tgtObject) {
        GenericNode src = nodeMapping.get(srcObject);
        GenericNode tgt = nodeMapping.get(tgtObject);

        if (src != null && tgt != null) {
            EReferenceInstance referenceInstance = new EReferenceInstance(model, eReference, srcObject, tgtObject);
            GenericEdge edge = new GenericEdge(eReference.getName(), src, tgt);
            edge.setModel(graph);
            if (!edgeMapping.contains(referenceInstance)) {
                edgeMapping.put(referenceInstance, edge);
                edgeMapping.put(edge, referenceInstance);
                graph.getEdges().add(edge);
            }
        }
    }

    public static class ParseResult {
        public GenericGraph graph;
        public NodeMapping nodeMapping;
        public EdgeMapping edgeMapping;

        public ParseResult(GenericGraph graph, NodeMapping nodeMapping, EdgeMapping edgeMapping) {
            this.graph = graph;
            this.nodeMapping = nodeMapping;
            this.edgeMapping = edgeMapping;
        }
    }

}
