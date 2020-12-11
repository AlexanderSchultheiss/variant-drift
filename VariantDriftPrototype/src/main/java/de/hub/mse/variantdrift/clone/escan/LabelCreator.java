package de.hub.mse.variantdrift.clone.escan;

import org.eclipse.emf.ecore.EObject;
import org.jgrapht.DirectedGraph;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * Provides the basic labeling of nodes and capsuleEdges Note: the basic labels
 * are not the canonical labels
 * 
 *
 */
public class LabelCreator {


	/**
	 * Since the labels based on more that one part, this parts are separated by
	 * an Separator instead of just putting them together to avoid mistakes,
	 * like it would be in, for example: part 1: aba and part 2: c --> abac,
	 * would be the samt label as part 1: ab and part 2: ac --> abac
	 * 
	 */
	private static final String SEPARATOR = System
			.getProperty("line.separator");

	public static String getSeparator() {
		return SEPARATOR;
	}

	static Map<de.uni_marburg.fb12.swt.cloneDetection.atl.escan.CapsuleEdge, String> capsuleEdgeNameCache = new HashMap<de.uni_marburg.fb12.swt.cloneDetection.atl.escan.CapsuleEdge, String>();
	public static String getCapsuleEdgeToString(de.uni_marburg.fb12.swt.cloneDetection.atl.escan.CapsuleEdge capsuleEdge) {
		if (capsuleEdgeNameCache.containsKey(capsuleEdge))
			return capsuleEdgeNameCache.get(capsuleEdge);

		capsuleEdgeNameCache.put(capsuleEdge, capsuleEdge.getLabel());
		return capsuleEdge.getLabel();
	}


	// ** Part 3 - ModelCD - eScan
	static Map<EObject, String> eScanNodeCache = new HashMap<EObject, String>();
	/**
	 * 
	 * @param node
	 * @param graph
	 *            used for method from EObjectUtility, hence graph has to be the
	 *            computation- or the fragment-graph
	 * @return
	 */
	public static String getModelCdNodeLabel(EObject node,
			DirectedGraph<EObject, de.uni_marburg.fb12.swt.cloneDetection.atl.escan.CapsuleEdge> graph) {
		if (eScanNodeCache.containsKey(node))
			return eScanNodeCache.get(node);

			String result = null;
			if (node instanceof MatchedRule || node instanceof InPattern || node instanceof OutPattern) {
				result = node.eClass().getName();
			} else if (node instanceof Filter) {
				Filter filter = (Filter) node;
				result = "filter "+filter.getValue();
			} else if (node instanceof Variable) {
				Variable variable = (Variable) node;
				result = "var " + variable.getTypeModelName() + "!" + variable.getTypeName() + " <- " + variable.getInitExpression();
			} else if (node instanceof InPatternElement) {
				InPatternElement inPatternElement = (InPatternElement) node;
				result = "in "+inPatternElement.getVarName()+":"+inPatternElement.getTypeModelName() + "!" + inPatternElement.getTypeName();
			} else if (node instanceof OutPatternElement) {
				OutPatternElement outPatternElement = (OutPatternElement) node;
				result = "out "+outPatternElement.getVarName()+":"+outPatternElement.getTypeModelName() + "!" + outPatternElement.getTypeName();
			} else if (node instanceof Binding) {
				Binding binding = (Binding)node;
				result = "binding "+binding.getPropertyName()+ " <- " + binding.getValue();
			}
		
			eScanNodeCache.put(node, result);
			return result;
	}


	static Map<de.uni_marburg.fb12.swt.cloneDetection.atl.escan.CapsuleEdge, String> eScanSimpleEdgeCache = new HashMap<de.uni_marburg.fb12.swt.cloneDetection.atl.escan.CapsuleEdge, String>();
	public static String getSimpleCapsuleEdgeLabel(de.uni_marburg.fb12.swt.cloneDetection.atl.escan.CapsuleEdge capsuleEdge) {
		if (eScanSimpleEdgeCache.containsKey(capsuleEdge))
			return eScanSimpleEdgeCache.get(capsuleEdge);
		

		eScanSimpleEdgeCache.put(capsuleEdge, capsuleEdge.getLabel());
		return capsuleEdge.getLabel();
	}

	public static String getModelCdEdgeLabel(de.uni_marburg.fb12.swt.cloneDetection.atl.escan.CapsuleEdge h,
                                             DirectedGraph<EObject, de.uni_marburg.fb12.swt.cloneDetection.atl.escan.CapsuleEdge> graph, int indexSourceNode,
                                             int indexTargetNode) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(getSimpleCapsuleEdgeLabel(h));
		stringBuilder.append(" ");
		stringBuilder.append(indexSourceNode);
		stringBuilder.append(" ");
		stringBuilder
				.append(getModelCdNodeLabel(graph.getEdgeSource(h), graph));
		stringBuilder.append(" ");
		stringBuilder.append(indexTargetNode);
		stringBuilder.append(" ");
		stringBuilder
				.append(getModelCdNodeLabel(graph.getEdgeTarget(h), graph));
		stringBuilder.append(SEPARATOR);
		return stringBuilder.toString();
	}

	static Map<de.uni_marburg.fb12.swt.cloneDetection.atl.escan.CapsuleEdge, String> eScanSimpleEdgeCache2 = new HashMap<de.uni_marburg.fb12.swt.cloneDetection.atl.escan.CapsuleEdge, String>();
	public static String getSimpleModelCdEdgeLabel(de.uni_marburg.fb12.swt.cloneDetection.atl.escan.CapsuleEdge h,
                                                   DirectedGraph<EObject, de.uni_marburg.fb12.swt.cloneDetection.atl.escan.CapsuleEdge> graph) {
		if (eScanSimpleEdgeCache2.containsKey(h))
			return eScanSimpleEdgeCache2.get(h);
		
		
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(getSimpleCapsuleEdgeLabel(h));
		stringBuilder.append(" ");
		stringBuilder
				.append(getModelCdNodeLabel(graph.getEdgeSource(h), graph));
		stringBuilder.append(" ");
		stringBuilder
				.append(getModelCdNodeLabel(graph.getEdgeTarget(h), graph));
		stringBuilder.append(SEPARATOR);
		eScanSimpleEdgeCache2.put(h, stringBuilder.toString());
		return stringBuilder.toString();
	}

	// ** Part 4 - CloneDetective

	public static String getCloneDetectiveNodeLabel(EObject node,
			DirectedGraph<EObject, de.uni_marburg.fb12.swt.cloneDetection.atl.escan.CapsuleEdge> graph) {
		return getModelCdNodeLabel(node, graph);
	}

	public static boolean haveEqualCloneDetectiveLabels(EObject node1, EObject node2,
			DirectedGraph<EObject, de.uni_marburg.fb12.swt.cloneDetection.atl.escan.CapsuleEdge> graph1,
			DirectedGraph<EObject, de.uni_marburg.fb12.swt.cloneDetection.atl.escan.CapsuleEdge> graph2) {
		return getCloneDetectiveNodeLabel(node1, graph1).equals(
				getCloneDetectiveNodeLabel(node2, graph2));
	}

	public static boolean haveEqualCloneDetectiveLabels(EObject node1, EObject node2,
			EObject node3, EObject node4, DirectedGraph<EObject, de.uni_marburg.fb12.swt.cloneDetection.atl.escan.CapsuleEdge> graph1,
			DirectedGraph<EObject, de.uni_marburg.fb12.swt.cloneDetection.atl.escan.CapsuleEdge> graph2,
			DirectedGraph<EObject, de.uni_marburg.fb12.swt.cloneDetection.atl.escan.CapsuleEdge> graph3,
			DirectedGraph<EObject, de.uni_marburg.fb12.swt.cloneDetection.atl.escan.CapsuleEdge> graph4) {

		if ((LabelCreator.haveEqualCloneDetectiveLabels(node1, node2, graph1,
				graph2))
				&& (LabelCreator.haveEqualCloneDetectiveLabels(node1, node3,
						graph1, graph3))
				&& (LabelCreator.haveEqualCloneDetectiveLabels(node1, node4,
						graph1, graph4))) {
			return true;
		}
		return false;
	}

	public static boolean haveEqualCloneDetectiveLabels(List<EObject> nodes,
			List<DirectedGraph<EObject, de.uni_marburg.fb12.swt.cloneDetection.atl.escan.CapsuleEdge>> graphs) {
		String label1 = getCloneDetectiveNodeLabel(nodes.get(0), graphs.get(0));
		boolean labelsAreEqual = true;
		for (EObject node : nodes) {
			int index = nodes.indexOf(node);
			if (!(label1.equals(getCloneDetectiveNodeLabel(node,
					graphs.get(index))))) {
				labelsAreEqual = false;
			}
		}
		return labelsAreEqual;
	}

	public static boolean haveEqualCloneDetectiveCapsuleEdgeLabels(
            de.uni_marburg.fb12.swt.cloneDetection.atl.escan.CapsuleEdge capsuleEdge1, de.uni_marburg.fb12.swt.cloneDetection.atl.escan.CapsuleEdge capsuleEdge2) {
		return getSimpleCapsuleEdgeLabel(capsuleEdge1).equals(
				getSimpleCapsuleEdgeLabel(capsuleEdge2));
	}

	public static boolean haveEqualCloneDetectiveCapsuleEdgeLabels(
			List<de.uni_marburg.fb12.swt.cloneDetection.atl.escan.CapsuleEdge> capsuleEdges) {
		String label1 = getSimpleCapsuleEdgeLabel(capsuleEdges.get(0));
		boolean labelsAreEqual = true;
		for (CapsuleEdge capsuleEdge : capsuleEdges) {
			int index = capsuleEdges.indexOf(capsuleEdge);
			if (!(label1.equals(getSimpleCapsuleEdgeLabel(capsuleEdges
					.get(index))))) {
				labelsAreEqual = false;
			}
		}
		return labelsAreEqual;
	}

}
