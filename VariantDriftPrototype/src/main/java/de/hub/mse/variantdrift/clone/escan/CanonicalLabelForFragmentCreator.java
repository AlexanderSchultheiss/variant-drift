package de.hub.mse.variantdrift.clone.escan;

import de.uni_marburg.fb12.swt.cloneDetection.atl.escan.CapsuleEdge;
import de.uni_marburg.fb12.swt.cloneDetection.atl.escan.LabelCreator;
import de.uni_marburg.fb12.swt.cloneDetection.atl.escan.Permutation;
import org.eclipse.emf.ecore.EObject;
import org.jgrapht.DirectedGraph;

import java.util.*;

public class CanonicalLabelForFragmentCreator {

	/**
	 * System.getProperty("line.separator");
	 */
	public static final String SEPARATOR = LabelCreator.getSeparator();

	private static Map<String, List<CapsuleEdge>> getCanonicalLabels(
			DirectedGraph<EObject, CapsuleEdge> fragmentGraph) {
		Map<String, List<CapsuleEdge>> labelsToOrderedCapsuleEdges 
				= new HashMap<String, List<CapsuleEdge>>();

		List<List<EObject>> nodeSequences = getEObjectSequences(fragmentGraph);

		if (nodeSequences.isEmpty()) {
			System.out
					.println("CanonicalLabelForFragmenCreator -"
							+ " getCanonicalLabels: empty nodeSequence");
		}
		for (List<EObject> nodeSequence : nodeSequences) {
			List<CapsuleEdge> orderedCapsuleEdges = getOrderedCapsuleEdges(
					fragmentGraph, nodeSequence);
			labelsToOrderedCapsuleEdges.put(
					getCanonicalLabel(fragmentGraph, nodeSequence,
							orderedCapsuleEdges), orderedCapsuleEdges);
		}

		return labelsToOrderedCapsuleEdges;
	}

	/**
	 * in case there are more than one possible label, the smallest one is
	 * chosen (occurs when a perfect refining of the nodeSequenzes is not found)
	 * 
	 * @param f
	 * @param ruleGraphMap
	 * @return
	 */

	public static Map<String, List<CapsuleEdge>> getCanonicalLabel(
			DirectedGraph<EObject, CapsuleEdge> fragmentGraph) {
		Map<String, List<CapsuleEdge>> res = new HashMap<String, List<CapsuleEdge>>();
		Map<String, List<CapsuleEdge>> labelsToOrderedCapsuleEdges 
						= getCanonicalLabels(fragmentGraph);

		Set<String> labelsSet = labelsToOrderedCapsuleEdges.keySet();
		List<String> labels = new LinkedList<String>();
		labels.addAll(labelsSet);
		Collections.sort(labels);

		String label = labels.get(0);
		res.put(label, labelsToOrderedCapsuleEdges.get(label));
		return res;
	}

	private static String getCanonicalLabel(
			DirectedGraph<EObject, CapsuleEdge> fragmentGraph,
			List<EObject> orderedEObjects, List<CapsuleEdge> orderedCapsuleEdges) {
		StringBuilder stringBuilder = new StringBuilder();
		for (CapsuleEdge capsuleLink : orderedCapsuleEdges) {
			stringBuilder
					.append(LabelCreator.getModelCdEdgeLabel(capsuleLink,
							fragmentGraph, orderedEObjects.indexOf(fragmentGraph
									.getEdgeSource(capsuleLink)), orderedEObjects
									.indexOf(fragmentGraph
											.getEdgeTarget(capsuleLink))));
		}
		return stringBuilder.toString();
	}

	private static List<CapsuleEdge> getOrderedCapsuleEdges(
			DirectedGraph<EObject, CapsuleEdge> fragmentGraph,
			List<EObject> orderedEObjects) {
		List<CapsuleEdge> orderedCapsuleEdges = new LinkedList<CapsuleEdge>();
		for (EObject node : orderedEObjects) {
			Set<EObject> nodesIn = new HashSet<EObject>();
			Set<CapsuleEdge> ceIn = fragmentGraph.incomingEdgesOf(node);
			for (CapsuleEdge ce : ceIn) {
				EObject source = fragmentGraph.getEdgeSource(ce);
				nodesIn.add(source);
			}

			// put nodesIn in order
			List<Integer> positions = new LinkedList<Integer>();
			for (EObject nIn : nodesIn) {
				int position = orderedEObjects.indexOf(nIn);
				if (position >= 0) {
					positions.add(orderedEObjects.indexOf(nIn));
				}
			}
			Collections.sort(positions);

			for (Integer position : positions) {
				EObject n = orderedEObjects.get(position);
				orderedCapsuleEdges.add(fragmentGraph.getEdge(n, node));
			}

		}
		return orderedCapsuleEdges;
	}

	/**
	 * 
	 * @param fragmentGraph
	 * @return List of possible EObjectSequences a EObjectSequenzes is a List of EObjects
	 *         if a perfect Partitioning could be achieved the List of possible
	 *         EObjectSequences contains only one EObjectSequence
	 */

	private static List<List<EObject>> getEObjectSequences(
			DirectedGraph<EObject, CapsuleEdge> fragmentGraph) {
		// create Partitioning based on EObjectLabels
		Map<String, Set<EObject>> nodeLabelsToEObjects = new HashMap<String, Set<EObject>>();
		boolean isPerfect = true;
		for (EObject node : fragmentGraph.vertexSet()) {
			String label = LabelCreator
					.getModelCdNodeLabel(node, fragmentGraph);
			if (nodeLabelsToEObjects.containsKey(label)) {
				nodeLabelsToEObjects.get(label).add(node);
				isPerfect = false;
			} else {
				HashSet<EObject> hashSet = new HashSet<EObject>();
				hashSet.add(node);
				nodeLabelsToEObjects.put(label, hashSet);
			}
		}
		if (isPerfect) {
			List<EObject> nodeSequenz = getEObjectSequenzesSimpel(nodeLabelsToEObjects);
			List<List<EObject>> res = new LinkedList<List<EObject>>();
			res.add(nodeSequenz);
			return res;
		}

		// refine Partitioning
		Map<String, Set<EObject>> extendedEObjectLabelsToEObjects = getRefineEObjectPartitioning(
				nodeLabelsToEObjects, fragmentGraph);

		if (isPerfectPartitioning(extendedEObjectLabelsToEObjects, fragmentGraph)) {
			List<EObject> nodeSequenz = getEObjectSequenzesSimpel(extendedEObjectLabelsToEObjects);
			List<List<EObject>> res = new LinkedList<List<EObject>>();
			res.add(nodeSequenz);
			return res;
		}

		return getEObjectSequenzes(extendedEObjectLabelsToEObjects);
	}

	private static Map<String, Set<EObject>> getRefineEObjectPartitioning(
			Map<String, Set<EObject>> nodeLabelsToEObjects,
			DirectedGraph<EObject, CapsuleEdge> fragmentGraph) {
		Map<String, Set<EObject>> extendedEObjectLabelsToEObjects = new HashMap<String, Set<EObject>>();

		for (String s : nodeLabelsToEObjects.keySet()) {
			if (nodeLabelsToEObjects.get(s).size() == 1) {
				extendedEObjectLabelsToEObjects.put(s, nodeLabelsToEObjects.get(s));
			} else {
				extendedEObjectLabelsToEObjects.putAll(getRefinedEObjectSet(s,
						nodeLabelsToEObjects.get(s), fragmentGraph));
			}
		}
		return extendedEObjectLabelsToEObjects;
	}

	/**
	 * refine nodeset based on the incoming and outgoing edges
	 * 
	 * @param label
	 * @param nodes
	 * @return
	 */
	private static Map<String, Set<EObject>> getRefinedEObjectSet(String label,
			Set<EObject> nodes, DirectedGraph<EObject, CapsuleEdge> fragmentGraph) {
		Map<String, Set<EObject>> tempRes = new HashMap<String, Set<EObject>>();
		Map<EObject, String> nodesToTempLabel = new HashMap<EObject, String>();
		// incomingLinks
		boolean isPerfect = true;
		for (EObject node : nodes) {
			String newLabel = label + SEPARATOR
					+ fragmentGraph.incomingEdgesOf(node).size();
			if (tempRes.containsKey(newLabel)) {
				tempRes.get(newLabel).add(node);
				nodesToTempLabel.put(node, newLabel);
				isPerfect = false;
			} else {
				Set<EObject> set = new HashSet<EObject>();
				set.add(node);
				tempRes.put(newLabel, set);
				nodesToTempLabel.put(node, newLabel);
			}
		}

		if (isPerfect) {
			return tempRes;
		}

		Map<String, Set<EObject>> res = new HashMap<String, Set<EObject>>();
		// outgoingLinks
		for (EObject node : nodes) {
			String newLabel = nodesToTempLabel.get(node) + SEPARATOR
					+ fragmentGraph.outgoingEdgesOf(node).size();
			if (res.containsKey(newLabel)) {
				res.get(newLabel).add(node);
			} else {
				Set<EObject> set = new HashSet<EObject>();
				set.add(node);
				res.put(newLabel, set);
			}
		}
		return res;
	}

	private static boolean isPerfectPartitioning(
			Map<String, Set<EObject>> nodeLabelsToEObjects,
			DirectedGraph<EObject, CapsuleEdge> fragmentGraph) {
		if (nodeLabelsToEObjects.size() == fragmentGraph.vertexSet().size()) {
			return true;
		} else {
			return false;
		}

	}

	private static List<EObject> getEObjectSequenzesSimpel(
			Map<String, Set<EObject>> nodeLabelsToEObjects) {
		List<EObject> res = new LinkedList<EObject>();
		List<String> labels = new LinkedList<String>();
		labels.addAll(nodeLabelsToEObjects.keySet());
		Collections.sort(labels);
		for (String label : labels) {
			if (nodeLabelsToEObjects.get(label).size() != 1) {
				System.out
						.println("CanonicalLabelForFragmenCreator - getEObjectSequenzesSimpel: ????");
			}
			res.add(nodeLabelsToEObjects.get(label).iterator().next());
		}
		return res;
	}

	private static List<List<EObject>> getEObjectSequenzes(
			Map<String, Set<EObject>> nodeLabelsToEObjects) {
		int positionLastDuplicate = -1;

		List<String> labels = new LinkedList<String>();
		labels.addAll(nodeLabelsToEObjects.keySet());
		Collections.sort(labels);
		int position = -1;
		for (String label : labels) {
			position++;
			if (nodeLabelsToEObjects.get(label).size() != 1) {
				positionLastDuplicate = position;
			}
		}

		List<EObject> endTail = new LinkedList<EObject>();
		for (int i = positionLastDuplicate + 1; i < labels.size(); i++) {
			// beyond the last duplicate there is only one EObject per set
			EObject insertEObject = nodeLabelsToEObjects.get(labels.get(i)).iterator()
					.next();
			endTail.add(insertEObject);
		}

		if (positionLastDuplicate == -1) {
			List<List<EObject>> res = new LinkedList<List<EObject>>();
			res.add(endTail);
			return res;
		}

		List<List<EObject>> tails = getAllPermutations(nodeLabelsToEObjects
				.get(labels.get(0)));

		List<String> remainingLabels = labels.subList(1,
				positionLastDuplicate + 1);

		for (String label : remainingLabels) {
			List<List<EObject>> newTails = new LinkedList<List<EObject>>();
			List<List<EObject>> intermediateTails = getAllPermutations(nodeLabelsToEObjects
					.get(label));
			for (List<EObject> tail : tails) {
				for (List<EObject> intermediateTail : intermediateTails) {
					List<EObject> newTail = new LinkedList<EObject>();
					newTail.addAll(tail);
					newTail.addAll(intermediateTail);
					newTails.add(newTail);
				}

			}
			tails = newTails;
		}

		for (List<EObject> tail : tails) {
			tail.addAll(endTail);
		}

		return tails;
	}

	private static List<List<EObject>> getAllPermutations(
			Set<EObject> nodesOfSameLabel) {
		return Permutation.permute(nodesOfSameLabel);
	}

}
