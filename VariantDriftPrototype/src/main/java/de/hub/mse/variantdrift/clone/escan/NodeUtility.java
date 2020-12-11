package de.hub.mse.variantdrift.clone.escan;

import aatl.MatchedRule;
import de.uni_marburg.fb12.swt.cloneDetection.atl.escan.CapsuleEdge;
import de.uni_marburg.fb12.swt.cloneDetection.atl.escan.LabelCreator;
import org.eclipse.emf.ecore.EObject;
import org.jgrapht.DirectedGraph;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 
 * Since there are normal nodes and attribute nodes, some utility methods are
 * needed to tell them apart.
 * 
 *
 */
public class NodeUtility {

	public static DirectedGraph<EObject, CapsuleEdge> getGraph(EObject node,
                                                               Map<MatchedRule, DirectedGraph<EObject, CapsuleEdge>> ruleGraphMap) {
		MatchedRule rule = getMatchedRule(node, ruleGraphMap);
		return ruleGraphMap.get(rule);
	}

	/**
	 * 
	 * @param node
	 * @param ruleGraphMap
	 * @return the MatchedRule this node belongs to
	 */
	public static MatchedRule getMatchedRule(EObject node,
			Map<MatchedRule, DirectedGraph<EObject, CapsuleEdge>> ruleGraphMap) {
		// else node is AttributeEObject
		String ruleNames = "";
		Set<MatchedRule> rules = new HashSet<MatchedRule>();
		for (MatchedRule rule : ruleGraphMap.keySet()) {
			DirectedGraph<EObject, CapsuleEdge> graph = ruleGraphMap.get(rule);
			if (graph.containsVertex(node)) {
				rules.add(rule);
			} else {
				ruleNames = ruleNames + " " + rule.getName();
			}
		}
		if (rules.size() != 1) {
			//System.out.println("rules.size()!=1 " + node.toString());
			//System.out.println("EObjectUtility-getMatchedRule: nicht in: " + ruleNames);
		}
		return rules.iterator().next();
	}


	/**
	 * 
	 * @param node
	 *            the EObject of which the adjacent nodes are returned
	 * @param graph
	 *            he graph the node belongs to (has to be the computation- or
	 *            the fragment-graph, not the original-/henshin Graph)
	 * @param considerAttributeEObjects
	 *            true - all adjacentEObjects will be returned false - only the
	 *            nonAttributeEObjects will be returned
	 * @return the adjacent EObjects of node in the graph, undefined if node is not
	 *         a Vertex of graph
	 */
	public static Set<EObject> getAdjacentEObjects(EObject node,
			DirectedGraph<EObject, CapsuleEdge> graph,
			boolean considerAttributeEObjects) {
		Set<EObject> adjacentEObjects = getAdjacentEObjects(node, graph);

			return adjacentEObjects; 
	}

	/**
	 * Returns all nodes that have identical labels, uses LabelCreator.
	 * 
	 * @param graph
	 *            has to be the computation- or the fragment-graph, not the
	 *            original-/henshin Graph
	 * @return all EObjects of the graph that have the same Label
	 */
	public static Set<EObject> getDuplikateEObjects(
			DirectedGraph<EObject, CapsuleEdge> graph) {
		Set<EObject> res = new HashSet<EObject>();
		for (EObject node : graph.vertexSet()) {
			for (EObject n : graph.vertexSet()) {
				if (node != n) {
					if (LabelCreator.getModelCdNodeLabel(node, graph).equals(
							LabelCreator.getModelCdNodeLabel(n, graph))) {
						res.add(node);
						res.add(n);
					}
				}
			}
		}
		return res;
	}

	/**
	 * Returns all CapsuleEdges that have identical labels and whose source- and
	 * target-nodes have identical labels, uses LabelCreator.
	 * 
	 * @param graph
	 *            has to be the computation- or the fragment-graph, not the
	 *            original-/henshin Graph
	 * @return all EObjects of the graph that have identical labels and whose
	 *         source- and target-nodes have identical labels.
	 */
	public static Set<CapsuleEdge> getDuplikateCapsuleEdges(
			DirectedGraph<EObject, CapsuleEdge> graph) {
		Set<CapsuleEdge> res = new HashSet<CapsuleEdge>();
		for (CapsuleEdge capsuleEdge : graph.edgeSet()) {
			for (CapsuleEdge ce : graph.edgeSet()) {
				if (capsuleEdge != ce) {
					if (LabelCreator.getSimpleModelCdEdgeLabel(capsuleEdge,
							graph).equals(
							LabelCreator.getSimpleModelCdEdgeLabel(ce, graph))) {
						res.add(capsuleEdge);
						res.add(ce);
					}
				}
			}
		}
		return res;
	}

	/**
	 * 
	 * @param node
	 *            the EObject of which the adjacent nodes are returned
	 * @param graph
	 *            he graph the node belongs to (has to be the computation- or
	 *            the fragment-graph, not the original-/henshin Graph)
	 * @return the adjacent EObjects of node in the graph, undefined if node is not
	 *         a Vertex of graph
	 */
	public static Set<EObject> getAdjacentEObjects(EObject node,
			DirectedGraph<EObject, CapsuleEdge> graph) {
		Set<EObject> adjacentEObjects = new HashSet<EObject>();
		Set<CapsuleEdge> edges = new HashSet<CapsuleEdge>();
		edges.addAll(graph.incomingEdgesOf(node));
		edges.addAll(graph.outgoingEdgesOf(node));
		for (CapsuleEdge e : edges) {
			EObject source = graph.getEdgeSource(e);
			EObject target = graph.getEdgeTarget(e);
			if (node != source) {
				adjacentEObjects.add(source);
			}
			if (node != target) {
				adjacentEObjects.add(target);
			}
			if ((node != source) && (node != target)) {
				System.out.println("Fehler in EObjectUtility.getAdjacentEObjects");
			}
		}
		return adjacentEObjects;
	}
}
