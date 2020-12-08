package de.hub.mse.variantsync.variantdrift.clone.util;

import java.util.*;

import aatl.MatchedRule;
import de.hub.mse.variantsync.variantdrift.clone.models.GenericEdge;
import de.hub.mse.variantsync.variantdrift.clone.models.GenericGraph;
import de.hub.mse.variantsync.variantdrift.clone.models.GenericNode;
import org.conqat.engine.model_clones.model.IModelGraph;
import org.conqat.engine.model_clones.model.ModelGraphMock;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;

public class EMF2GenericGraph {

    Resource model;
    GenericGraph graph;
    NodeMapping nodeMapping;
    EdgeMapping edgeMapping;

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


    private EMF2GenericGraph(Resource model) {
        this.model = model;
        this.graph = new GenericGraph();
        nodeMapping = new NodeMapping();
        edgeMapping = new EdgeMapping();
    }

    public static ParseResult transform(Resource model) {
        var parser = new EMF2GenericGraph(model);

        parser.transformNodes();
        parser.transformEdges();

        return new ParseResult(parser.graph, parser.nodeMapping, parser.edgeMapping);
    }

    private void transformNodes() {
        for (Iterator<EObject> it = model.getAllContents(); it.hasNext(); ) {
            EObject eObject = it.next();

            GenericNode node = new GenericNode(eObject.eClass().getName());

            if (!nodeMapping.contains(eObject)) {
                nodeMapping.put(eObject, node);
                graph.getNodes().add(node);
            }
        }
    }

    private void transformEdges() {
        for (Iterator<EObject> it = model.getAllContents(); it.hasNext(); ) {
            EObject eObject = it.next();

            for (EStructuralFeature structuralFeature : eObject.eClass().getEAllStructuralFeatures()) {
                if (structuralFeature.isDerived()) {
                    continue;
                }

                if (structuralFeature instanceof EReference eReference) {
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
    }

    private void transformEdge(EReference eReference, EObject srcObject, EObject tgtObject) {
        GenericNode src = nodeMapping.get(srcObject);
        GenericNode tgt = nodeMapping.get(tgtObject);

        if (src != null && tgt != null) {
            GenericEdge edge = new GenericEdge(eReference.getName(), src, tgt);

            if (!edgeMapping.contains(eReference)) {
                edgeMapping.put(eReference, edge);
                edgeMapping.put(edge, eReference);
                graph.getEdges().add(edge);
            }
        }
    }

}
