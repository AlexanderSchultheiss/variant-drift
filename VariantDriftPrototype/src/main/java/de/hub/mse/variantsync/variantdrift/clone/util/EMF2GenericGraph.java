package de.hub.mse.variantsync.variantdrift.clone.util;

import java.util.*;

import aatl.MatchedRule;
import de.hub.mse.variantsync.variantdrift.clone.models.GenericEdge;
import de.hub.mse.variantsync.variantdrift.clone.models.GenericGraph;
import de.hub.mse.variantsync.variantdrift.clone.models.GenericNode;
import org.conqat.engine.model_clones.model.IModelGraph;
import org.conqat.engine.model_clones.model.ModelGraphMock;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.Resource;

public class EMF2GenericGraph {

	Resource model;
	GenericGraph graph;

	Map<EObject, GenericNode> eObject2Node;
	Map<GenericNode, EObject> node2eObject;

	public IModelGraph transform(Resource model) {
		this.model = model;
		this.graph = new GenericGraph();

		this.eObject2Node = new HashMap<>();
		this.node2eObject = new HashMap<>();

		transformNodes();
		transformEdges();

		return graph;
	}

	public IModelGraph transform(Collection<Resource> models) {
		IModelGraph resultGraph = new GenericGraph();
		for (var model : models) {
			IModelGraph tempGraph = this.transform(model);
			resultGraph.getNodes().addAll(tempGraph.getNodes());
			resultGraph.getEdges().addAll(tempGraph.getEdges());
		}

		return resultGraph;
	}

	private void transformNodes() {
		for (Iterator<EObject> it = model.getAllContents(); it.hasNext();) {
			EObject eObject = it.next();

			GenericNode node = new GenericNode(eObject.eClass().getName());

			graph.getNodes().add(node);
			eObject2Node.put(eObject, node);
			node2eObject.put(node, eObject);
		}
	}

	private void transformEdges() {
		for (Iterator<EObject> it = model.getAllContents(); it.hasNext();) {
			EObject eObject = it.next();

			for (EReference eReference : eObject.eClass().getEAllReferences()) {
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
		GenericNode src = eObject2Node.get(srcObject);
		GenericNode tgt = eObject2Node.get(tgtObject);

		if (src != null && tgt != null) {
			GenericEdge edge = new GenericEdge(eReference.getName(), src, tgt);
			graph.getEdges().add(edge);
		}
	}

}
