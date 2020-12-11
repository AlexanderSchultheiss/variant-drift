package de.hub.mse.variantdrift.clone.escan;

import aatl.MatchedRule;
import de.uni_marburg.fb12.swt.cloneDetection.atl.Link;
import de.uni_marburg.fb12.swt.cloneDetection.atl.escan.CapsuleEdge;
import de.uni_marburg.fb12.swt.cloneDetection.atl.escan.CloneMatrix;
import de.uni_marburg.fb12.swt.cloneDetection.atl.escan.Fragment;
import org.eclipse.emf.ecore.EObject;

import java.util.*;


/**
 * 
 * Provides conversion to CloneGroupMappings for all (temp-) results of the
 * CloneDetections.
 * 
 * In some special cases this includes addingAttributes.
 */
public class CloneMatrixCreator {



	private static List<List<CapsuleEdge>> getCapsuleEdgeMatrix(
			Set<Fragment> cloneGroup) {
		List<List<CapsuleEdge>> capsuleEdgeMatrix = new LinkedList<List<CapsuleEdge>>();

		for (Fragment fragment : cloneGroup) {
			List<CapsuleEdge> capsuleEdges = fragment.getCapsuleEdges();
			capsuleEdgeMatrix.add(capsuleEdges);
		}

		return capsuleEdgeMatrix;
	}


	public static Set<de.uni_marburg.fb12.swt.cloneDetection.atl.escan.CloneMatrix> convertEScanResult(
			Set<Set<Fragment>> setOfSetsOfSameCanonicalLabeledNonOverlappingFragments) {
		Set<de.uni_marburg.fb12.swt.cloneDetection.atl.escan.CloneMatrix> res = new HashSet<de.uni_marburg.fb12.swt.cloneDetection.atl.escan.CloneMatrix>();

		for (Set<Fragment> fragmentsCloneGroup 
				: setOfSetsOfSameCanonicalLabeledNonOverlappingFragments) {
			if (fragmentsCloneGroup.size() > 1) {
				res.add(CloneMatrixCreator.convert(fragmentsCloneGroup));
			}
		}
		return res;
	}

	private static de.uni_marburg.fb12.swt.cloneDetection.atl.escan.CloneMatrix convert(Set<Fragment> cloneGroup) {

		Set<MatchedRule> rules = new HashSet<MatchedRule>();
		for (Fragment f : cloneGroup) {
			rules.add(f.getRule());
		}

		List<List<Link>> edgeMatrix = new LinkedList<List<Link>>();
		List<List<EObject>> nodeMatrix = new LinkedList<List<EObject>>();

		for (Fragment fragment : cloneGroup) {
			List<Link> originalEdges = new LinkedList<Link>();
			List<CapsuleEdge> capsuleEdges = fragment.getCapsuleEdges();
			for (CapsuleEdge capsuleEdge : capsuleEdges) {
					originalEdges.add(capsuleEdge.getOriginalEdge());
			}
			edgeMatrix.add(originalEdges);

			List<EObject> originalNodes = new ArrayList<EObject>(fragment.getNodes());
			nodeMatrix.add(originalNodes);
		}

		de.uni_marburg.fb12.swt.cloneDetection.atl.escan.CloneMatrix res = new CloneMatrix(edgeMatrix, nodeMatrix);
		return res;
	}
//
//	public static Set<CloneGroupMapping> convertEScanResultOld(
//			Collection<Set<Fragment>> setOfSetsOfSameCanonicalLabeledNonOverlappingFragments) {
//		Set<CloneGroupMapping> res = new HashSet<CloneGroupMapping>();
//
//		for (Set<Fragment> fragments : setOfSetsOfSameCanonicalLabeledNonOverlappingFragments) {
//			if (fragments.size() > 1) {
//				res.add(CloneMatrixCreator.convertOld(fragments));
//			}
//		}
//		return res;
//	}
//
//	private static CloneGroupMapping convertOld(Set<Fragment> cloneGroupFragment) {
//		Set<MatchedRule> rules = new HashSet<MatchedRule>();
//		for (Fragment f : cloneGroupFragment) {
//			rules.add(f.getRule());
//		}
//
//		// Die Klassenfelder von CloneGroup
//		Map<Edge, Map<MatchedRule, Edge>> edgeMappings = new HashMap<Edge, Map<MatchedRule, Edge>>();
//		Map<Attribute, Map<MatchedRule, Attribute>> attributeMappings 
//		= new HashMap<Attribute, Map<MatchedRule, Attribute>>();
//
//		for (Fragment f : cloneGroupFragment) {
//			for (CapsuleEdge capsuleEdge : f.getCapsuleEdges()) {
//				Map<MatchedRule, Edge> tempMapEdge = new HashMap<MatchedRule, Edge>();
//				Map<MatchedRule, Attribute> tempMapAttribute = new HashMap<MatchedRule, Attribute>();
//
//				if (capsuleEdge.isAttributeEdge()) {
//					tempMapAttribute.put(f.getRule(),
//							capsuleEdge.getAttribute());
//					for (Fragment f2 : cloneGroupFragment) {
//						if (!(f2 == f)) {
//							for (CapsuleEdge capsuleEdge2 : f2
//									.getCapsuleEdges()) {
//								if (capsuleEdge2.isAttributeEdge()) {
//									if (capsuleEdge.getAttribute() == capsuleEdge2
//											.getAttribute()) {
//										tempMapAttribute.put(f2.getRule(),
//												capsuleEdge.getAttribute());
//									}
//								}
//							}
//						}
//					}
//					for (Attribute a : tempMapAttribute.values()) {
//						attributeMappings.put(a, tempMapAttribute);
//					}
//				} else {
//					tempMapEdge.put(f.getRule(), capsuleEdge.getOriginalEdge());
//					for (Fragment f2 : cloneGroupFragment) {
//						if (!(f2 == f)) {
//							for (CapsuleEdge capsuleEdge2 : f2
//									.getCapsuleEdges()) {
//								if (!capsuleEdge2.isAttributeEdge()) {
//									if (capsuleEdge.getOriginalEdge() == capsuleEdge2
//											.getOriginalEdge()) {
//										tempMapEdge.put(f2.getRule(),
//												capsuleEdge.getOriginalEdge());
//									}
//								}
//							}
//						}
//					}
//					for (Edge e : tempMapEdge.values()) {
//						edgeMappings.put(e, tempMapEdge);
//					}
//				}
//
//			}
//		}
//		return new CloneGroupMapping(rules, edgeMappings, attributeMappings);
//	}

//	// ** CloneDetective
//
//	public static Set<CloneMatrix> convertClonePairSet(
//			Collection<ClonePair> clonePairs,
//			Map<MatchedRule, DirectedGraph<Node, CapsuleEdge>> ruleGraphMap) {
//		Set<CloneMatrix> res = new HashSet<CloneMatrix>();
//		for (ClonePair clonePair : clonePairs) {
//			CloneMatrix cloneMatrix = convertClonePair(clonePair, ruleGraphMap);
//			if ((cloneMatrix.getEdgeMatrix().size() > 0)
//					|| (cloneMatrix.getAttributeMatrix().size() > 0)) {
//				res.add(cloneMatrix);
//			}
//		}
//		return res;
//	}
//
//	private static CloneMatrix convertClonePair(ClonePair clonePair,
//			Map<MatchedRule, DirectedGraph<Node, CapsuleEdge>> ruleGraphMap) {
//		Set<MatchedRule> rules = new HashSet<MatchedRule>();
//		MatchedRule rule1 = NodeUtility.getRule(clonePair.getNodePairs().iterator()
//				.next().getNode1(), ruleGraphMap);
//		MatchedRule rule2 = NodeUtility.getRule(clonePair.getNodePairs().iterator()
//				.next().getNode2(), ruleGraphMap);
//		rules.add(rule1);
//		rules.add(rule2);
//
//		List<List<Edge>> edgeMatrix = new LinkedList<List<Edge>>();
//		List<List<Attribute>> attributeMatrix = new LinkedList<List<Attribute>>();
//
//		List<Edge> originalEdges1 = new LinkedList<Edge>();
//		List<Attribute> attributes1 = new LinkedList<Attribute>();
//		for (CapsuleEdge capsuleEdge : clonePair.getCapsuleEdges1()) {
//			if (capsuleEdge.isAttributeEdge()) {
//				attributes1
//						.add(capsuleEdge.getAttribute().getActionAttribute());
//			} else {
//				originalEdges1.add(capsuleEdge.getOriginalEdge()
//						.getActionEdge());
//			}
//		}
//		attributeMatrix.add(attributes1);
//		edgeMatrix.add(originalEdges1);
//
//		List<Edge> originalEdges2 = new LinkedList<Edge>();
//		List<Attribute> attributes2 = new LinkedList<Attribute>();
//		for (CapsuleEdge capsuleEdge : clonePair.getCapsuleEdges2()) {
//			if (capsuleEdge.isAttributeEdge()) {
//				attributes2
//						.add(capsuleEdge.getAttribute().getActionAttribute());
//			} else {
//				originalEdges2.add(capsuleEdge.getOriginalEdge()
//						.getActionEdge());
//			}
//		}
//		attributeMatrix.add(attributes2);
//		edgeMatrix.add(originalEdges2);
//
//		return new CloneMatrix(edgeMatrix, attributeMatrix);
//	}
//
//	public static Set<CloneMatrix> convertCloneTupelSet(
//			Collection<CloneTupel> cloneTupels,
//			Map<MatchedRule, DirectedGraph<Node, CapsuleEdge>> ruleGraphMap) {
//		Set<CloneMatrix> res = new HashSet<CloneMatrix>();
//		for (CloneTupel cloneTupel : cloneTupels) {
//			CloneMatrix cloneMatrix = convertCloneTupel(cloneTupel,
//					ruleGraphMap);
//			if ((cloneMatrix.getEdgeMatrix().size() > 0)
//					|| (cloneMatrix.getAttributeMatrix().size() > 0)) {
//				res.add(cloneMatrix);
//			}
//		}
//		return res;
//	}
//
//	private static CloneMatrix convertCloneTupel(CloneTupel cloneTupel,
//			Map<MatchedRule, DirectedGraph<Node, CapsuleEdge>> ruleGraphMap) {
//
//		List<MatchedRule> ruleList = new LinkedList<MatchedRule>();
//		Set<MatchedRule> rules = new HashSet<MatchedRule>();
//		NodeTupel nodeTupel = cloneTupel.getNodeTupels().iterator().next();
//		for (Node node : nodeTupel.getNodeTupelAsNodeList()) {
//			MatchedRule rule = NodeUtility.getRule(node, ruleGraphMap);
//			rules.add(rule);
//			ruleList.add(rule);
//		}
//
//		List<List<Edge>> edgeMatrix = new LinkedList<List<Edge>>();
//		List<List<Attribute>> attributeMatrix = new LinkedList<List<Attribute>>();
//
//		for (List<CapsuleEdge> capsuleEdges : cloneTupel.getCapsuleEdges()) {
//			List<Edge> originalEdges1 = new LinkedList<Edge>();
//			List<Attribute> attributes1 = new LinkedList<Attribute>();
//			for (CapsuleEdge capsuleEdge : capsuleEdges) {
//				if (capsuleEdge.isAttributeEdge()) {
//					attributes1.add(capsuleEdge.getAttribute()
//							.getActionAttribute());
//				} else {
//					originalEdges1.add(capsuleEdge.getOriginalEdge()
//							.getActionEdge());
//				}
//			}
//			attributeMatrix.add(attributes1);
//			edgeMatrix.add(originalEdges1);
//		}
//
//		return new CloneMatrix(edgeMatrix, attributeMatrix);
//
//	}

}
