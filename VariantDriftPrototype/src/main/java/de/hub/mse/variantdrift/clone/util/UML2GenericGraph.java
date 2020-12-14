package de.hub.mse.variantdrift.clone.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.conqat.engine.model_clones.model.IDirectedEdge;
import org.conqat.engine.model_clones.model.INode;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.uml2.uml.Association;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Generalization;
import org.eclipse.uml2.uml.Property;

import de.hub.mse.variantdrift.clone.models.GenericEdge;
import de.hub.mse.variantdrift.clone.models.GenericNode;



public class UML2GenericGraph extends EMF2GenericGraph {

	public UML2GenericGraph(Resource model, String label) {
		super(model, label);
	}

	@Override
	protected void doPostProcessing() {
		abstractFromAssociations();
		abstractFromGeneralizations();
	}

	private void abstractFromAssociations() {
		for (Iterator<EObject> it = model.getAllContents(); it.hasNext();) {
			EObject eObject = it.next();
			if (eObject.eClass().getName().equals("Association")) {
				Association a = (Association) eObject;
				List<Property> memberEnds = a.getMemberEnds();

				assert (memberEnds.size() == 2);
				assert (memberEnds.get(0).getType() instanceof org.eclipse.uml2.uml.Class);
				assert (memberEnds.get(1).getType() instanceof org.eclipse.uml2.uml.Class);

				Class c0 = (Class) memberEnds.get(0).getType();
				Class c1 = (Class) memberEnds.get(1).getType();

				GenericNode n0 = nodeMapping.eObject2Node.get(c0);
				GenericNode n1 = nodeMapping.eObject2Node.get(c1);
				GenericNode aNode = nodeMapping.eObject2Node.get(a);

				assert (n0 != null);
				assert (n1 != null);
				assert (aNode != null);

				// Add "synthetic" edge that represents association and its ends
				if (c0.getOwnedElements().contains(memberEnds.get(0))) {
					GenericEdge edge = new GenericEdge("association", n0, n1);
					graph.getEdges().add(edge);
				}
				if (c0.getOwnedElements().contains(memberEnds.get(1))) {
					GenericEdge edge = new GenericEdge("association", n0, n1);
					graph.getEdges().add(edge);
				}
				if (c1.getOwnedElements().contains(memberEnds.get(0))) {
					GenericEdge edge = new GenericEdge("association", n1, n0);
					graph.getEdges().add(edge);
				}
				if (c1.getOwnedElements().contains(memberEnds.get(1))) {
					GenericEdge edge = new GenericEdge("association", n1, n0);
					graph.getEdges().add(edge);
				}

				// Remove association and ends (including incident edges)
				Property p1 = memberEnds.get(0);
				Property p2 = memberEnds.get(1);

				GenericNode np1 = nodeMapping.eObject2Node.get(p1);
				GenericNode np2 = nodeMapping.eObject2Node.get(p2);

				assert (np1 != null);
				assert (np2 != null);

				removeNode(aNode);
				removeNode(np1);
				removeNode(np2);
			}
		}
	}
	
	private void abstractFromGeneralizations() {
		for (Iterator<EObject> it = model.getAllContents(); it.hasNext();) {
			EObject eObject = it.next();
			if (eObject.eClass().getName().equals("Generalization")) {
				Generalization g = (Generalization) eObject;
				
				assert(g.getGeneral() != null);
				assert(g.getSpecific() != null);
				assert (g.getGeneral() instanceof org.eclipse.uml2.uml.Class);
				assert (g.getSpecific() instanceof org.eclipse.uml2.uml.Class);
				
				Class c0 = (Class) g.getGeneral();
				Class c1 = (Class) g.getSpecific();

				GenericNode n0 = nodeMapping.eObject2Node.get(c0);
				GenericNode n1 = nodeMapping.eObject2Node.get(c1);
				GenericNode gNode = nodeMapping.eObject2Node.get(g);

				assert (n0 != null);
				assert (n1 != null);
				assert (gNode != null);

				// Add "synthetic" edge that represents generalization
				GenericEdge edge = new GenericEdge("generalization", n1, n0);
				graph.getEdges().add(edge);
				
				// Remove generalization (including incident edges)
				removeNode(gNode);
			}
		}
	}
	
	private void removeNode(GenericNode node) {
		// remove node and node mappings
		for (Iterator<INode> iterator = graph.getNodes().iterator(); iterator.hasNext();) {
			INode n = iterator.next();
			if (n == node) {
				iterator.remove();

				nodeMapping.node2eObject.remove(node);

				List<EObject> del = new ArrayList<EObject>();
				for (Iterator<EObject> iterator2 = nodeMapping.eObject2Node.keySet().iterator(); iterator2.hasNext();) {
					EObject o = iterator2.next();
					if (nodeMapping.eObject2Node.get(0) == node) {
						del.add(o);
					}
				}
				for (EObject eObject : del) {
					nodeMapping.eObject2Node.remove(eObject);
				}
			}
		}

		// remove incident edges and the respective edge mappings
		for (Iterator<IDirectedEdge> iterator = graph.getEdges().iterator(); iterator.hasNext();) {
			IDirectedEdge edge = iterator.next();
			if (edge.getSourceNode() == node || edge.getTargetNode() == node) {
				iterator.remove();

				edgeMapping.edge2EReference.remove(edge);

				List<EReferenceInstance> del = new ArrayList<EReferenceInstance>();
				for (Iterator<EReferenceInstance> iterator2 = edgeMapping.eReference2Edge.keySet().iterator(); iterator2.hasNext();) {
					EReferenceInstance r = iterator2.next();
					if (edgeMapping.eReference2Edge.get(r) == edge) {
						del.add(r);
					}
				}
				for (EReferenceInstance eReferenceInstance : del) {
					edgeMapping.eReference2Edge.remove(eReferenceInstance);
				}
			}
		}

	}

}
