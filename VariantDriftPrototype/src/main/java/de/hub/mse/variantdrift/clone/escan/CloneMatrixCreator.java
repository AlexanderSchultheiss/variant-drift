package de.hub.mse.variantdrift.clone.escan;

import de.hub.mse.variantdrift.clone.models.GenericEdge;
import de.hub.mse.variantdrift.clone.models.GenericGraph;
import de.hub.mse.variantdrift.clone.models.GenericNode;

import java.util.*;


/**
 * Provides conversion to CloneGroupMappings for all (temp-) results of the
 * CloneDetections.
 * <p>
 * In some special cases this includes addingAttributes.
 */
public class CloneMatrixCreator {

    private static List<List<GenericEdge>> getGenericEdgeMatrix(
            Set<Fragment> cloneGroup) {
        List<List<GenericEdge>> capsuleEdgeMatrix = new LinkedList<>();

        for (Fragment fragment : cloneGroup) {
            List<GenericEdge> capsuleEdges = fragment.getGenericEdges();
            capsuleEdgeMatrix.add(capsuleEdges);
        }

        return capsuleEdgeMatrix;
    }

    public static Set<CloneMatrix> convertEScanResult(
            Set<Set<Fragment>> setOfSetsOfSameCanonicalLabeledNonOverlappingFragments) {
        Set<CloneMatrix> res = new HashSet<>();

        for (Set<Fragment> fragmentsCloneGroup
                : setOfSetsOfSameCanonicalLabeledNonOverlappingFragments) {
            if (fragmentsCloneGroup.size() > 1) {
                res.add(CloneMatrixCreator.convert(fragmentsCloneGroup));
            }
        }
        return res;
    }

    private static CloneMatrix convert(Set<Fragment> cloneGroup) {

        Set<GenericGraph> rules = new HashSet<>();
        for (Fragment f : cloneGroup) {
            rules.add(f.getModel());
        }

        List<List<GenericEdge>> edgeMatrix = new LinkedList<>();
        List<List<GenericNode>> nodeMatrix = new LinkedList<>();

        for (Fragment fragment : cloneGroup) {
            List<GenericEdge> capsuleEdges = fragment.getGenericEdges();
            List<GenericEdge> originalEdges = new LinkedList<>(capsuleEdges);
            edgeMatrix.add(originalEdges);

            List<GenericNode> originalNodes = new ArrayList<>(fragment.getNodes());
            nodeMatrix.add(originalNodes);
        }

        return new CloneMatrix(edgeMatrix, nodeMatrix);
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
//		Set<GenericGraph> rules = new HashSet<GenericGraph>();
//		for (Fragment f : cloneGroupFragment) {
//			rules.add(f.getRule());
//		}
//
//		// Die Klassenfelder von CloneGroup
//		Map<Edge, Map<GenericGraph, Edge>> edgeMappings = new HashMap<Edge, Map<GenericGraph, Edge>>();
//		Map<Attribute, Map<GenericGraph, Attribute>> attributeMappings
//		= new HashMap<Attribute, Map<GenericGraph, Attribute>>();
//
//		for (Fragment f : cloneGroupFragment) {
//			for (GenericEdge capsuleEdge : f.getGenericEdges()) {
//				Map<GenericGraph, Edge> tempMapEdge = new HashMap<GenericGraph, Edge>();
//				Map<GenericGraph, Attribute> tempMapAttribute = new HashMap<GenericGraph, Attribute>();
//
//				if (capsuleEdge.isAttributeEdge()) {
//					tempMapAttribute.put(f.getRule(),
//							capsuleEdge.getAttribute());
//					for (Fragment f2 : cloneGroupFragment) {
//						if (!(f2 == f)) {
//							for (GenericEdge capsuleEdge2 : f2
//									.getGenericEdges()) {
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
//							for (GenericEdge capsuleEdge2 : f2
//									.getGenericEdges()) {
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
//			Map<GenericGraph, DirectedGraph<Node, GenericEdge>> ruleGraphMap) {
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
//			Map<GenericGraph, DirectedGraph<Node, GenericEdge>> ruleGraphMap) {
//		Set<GenericGraph> rules = new HashSet<GenericGraph>();
//		GenericGraph rule1 = NodeUtility.getRule(clonePair.getNodePairs().iterator()
//				.next().getNode1(), ruleGraphMap);
//		GenericGraph rule2 = NodeUtility.getRule(clonePair.getNodePairs().iterator()
//				.next().getNode2(), ruleGraphMap);
//		rules.add(rule1);
//		rules.add(rule2);
//
//		List<List<Edge>> edgeMatrix = new LinkedList<List<Edge>>();
//		List<List<Attribute>> attributeMatrix = new LinkedList<List<Attribute>>();
//
//		List<Edge> originalEdges1 = new LinkedList<Edge>();
//		List<Attribute> attributes1 = new LinkedList<Attribute>();
//		for (GenericEdge capsuleEdge : clonePair.getGenericEdges1()) {
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
//		for (GenericEdge capsuleEdge : clonePair.getGenericEdges2()) {
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
//			Map<GenericGraph, DirectedGraph<Node, GenericEdge>> ruleGraphMap) {
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
//			Map<GenericGraph, DirectedGraph<Node, GenericEdge>> ruleGraphMap) {
//
//		List<GenericGraph> ruleList = new LinkedList<GenericGraph>();
//		Set<GenericGraph> rules = new HashSet<GenericGraph>();
//		NodeTupel nodeTupel = cloneTupel.getNodeTupels().iterator().next();
//		for (Node node : nodeTupel.getNodeTupelAsNodeList()) {
//			GenericGraph rule = NodeUtility.getRule(node, ruleGraphMap);
//			rules.add(rule);
//			ruleList.add(rule);
//		}
//
//		List<List<Edge>> edgeMatrix = new LinkedList<List<Edge>>();
//		List<List<Attribute>> attributeMatrix = new LinkedList<List<Attribute>>();
//
//		for (List<GenericEdge> capsuleEdges : cloneTupel.getGenericEdges()) {
//			List<Edge> originalEdges1 = new LinkedList<Edge>();
//			List<Attribute> attributes1 = new LinkedList<Attribute>();
//			for (GenericEdge capsuleEdge : capsuleEdges) {
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
