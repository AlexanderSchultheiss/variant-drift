//package de.hub.mse.variantdrift.clone.escan;
//
//import aatl.MatchedRule;
//import de.uni_marburg.fb12.swt.cloneDetection.atl.escan.CapsuleEdge;
//import de.uni_marburg.fb12.swt.cloneDetection.atl.escan.CloneMatrixCreator;
//import de.uni_marburg.fb12.swt.cloneDetection.atl.escan.EScanDetection3DAbstract;
//import de.uni_marburg.fb12.swt.cloneDetection.atl.escan.Fragment;
//import org.eclipse.emf.ecore.EObject;
//import org.jgrapht.DirectedGraph;
//
//import java.util.LinkedList;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//
//public class EScanDetection3D extends EScanDetection3DAbstract {
//	public EScanDetection3D(List<MatchedRule> rules) {
//		super(rules);
//	}
//
//	public EScanDetection3D(
//			Map<MatchedRule, DirectedGraph<EObject, de.uni_marburg.fb12.swt.cloneDetection.atl.escan.CapsuleEdge>> ruleGraphMap,
//			List<MatchedRule> ruleList) {
//		super(ruleGraphMap, ruleList);
//	}
//
//	@Override
//	public void detectCloneGroups() {
//		long startZeit = System.currentTimeMillis();
//		if (DEBUG) System.out
//				.println(startDetectCloneGroups("EScanDetection3DIn1Step (CloneDetection"
//						+ " directly with Attributes)"));
//		List<Set<de.uni_marburg.fb12.swt.cloneDetection.atl.escan.CapsuleEdge>> graphsEdgeSetList = getGraphsEdgeSetListWithAttributes(rules);
//		Map<de.uni_marburg.fb12.swt.cloneDetection.atl.escan.CapsuleEdge, List<Set<de.uni_marburg.fb12.swt.cloneDetection.atl.escan.CapsuleEdge>>> edgesLayer1
//				= getCapsuleEdgeMappingLayer1(graphsEdgeSetList);
//
//		if (edgesLayer1.size() == 0) {
//			resultAsCloneMatrix = null;
//		} else {
//			Map<de.uni_marburg.fb12.swt.cloneDetection.atl.escan.Fragment, List<Set<de.uni_marburg.fb12.swt.cloneDetection.atl.escan.Fragment>>> layer1 = buildLayer1(edgesLayer1);
//			List<Map<de.uni_marburg.fb12.swt.cloneDetection.atl.escan.Fragment, List<Set<de.uni_marburg.fb12.swt.cloneDetection.atl.escan.Fragment>>>> lattice3D = buildLattice3D(
//					layer1, edgesLayer1);
//
//			Set<Set<de.uni_marburg.fb12.swt.cloneDetection.atl.escan.Fragment>> cloneGroups = groupAndFilterLattice3D(lattice3D);
//			if (DEBUG) System.out.println(startConversion());
//			resultAsCloneMatrix = de.uni_marburg.fb12.swt.cloneDetection.atl.escan.CloneMatrixCreator
//					.convertEScanResult(cloneGroups);
//			if (DEBUG) System.out.println(endDetectCloneGroups("EScanDetection3DIn1Step",
//					startZeit));
//		}
//
//	}
//
//	@Override
//	public void detectCloneGroups(Map<de.uni_marburg.fb12.swt.cloneDetection.atl.escan.Fragment, List<Set<de.uni_marburg.fb12.swt.cloneDetection.atl.escan.Fragment>>> topLayer) {
//
//		List<Set<de.uni_marburg.fb12.swt.cloneDetection.atl.escan.CapsuleEdge>> graphsEdgeSetList = getGraphsEdgeSetListWithAttributes(rules);
//		Map<de.uni_marburg.fb12.swt.cloneDetection.atl.escan.CapsuleEdge, List<Set<de.uni_marburg.fb12.swt.cloneDetection.atl.escan.CapsuleEdge>>> edgesLayer1
//				= getCapsuleEdgeMappingLayer1(graphsEdgeSetList);
//
//		List<Map<de.uni_marburg.fb12.swt.cloneDetection.atl.escan.Fragment, List<Set<de.uni_marburg.fb12.swt.cloneDetection.atl.escan.Fragment>>>> lattice3D = buildLattice3D(
//				topLayer, edgesLayer1);
//
//		Set<Set<de.uni_marburg.fb12.swt.cloneDetection.atl.escan.Fragment>> cloneGroups = groupAndFilterLattice3D(lattice3D);
//		if (DEBUG) System.out.println(startConversion());
//		resultAsCloneMatrix = CloneMatrixCreator
//				.convertEScanResult(cloneGroups);
//
//	}
//
//	public Set<Set<de.uni_marburg.fb12.swt.cloneDetection.atl.escan.Fragment>> getCloneGroups() {
//		List<Set<de.uni_marburg.fb12.swt.cloneDetection.atl.escan.CapsuleEdge>> graphsEdgeSetList = getGraphsEdgeSetListWithAttributes(rules);
//		Map<de.uni_marburg.fb12.swt.cloneDetection.atl.escan.CapsuleEdge, List<Set<de.uni_marburg.fb12.swt.cloneDetection.atl.escan.CapsuleEdge>>> edgesLayer1
//				= getCapsuleEdgeMappingLayer1(graphsEdgeSetList);
//
//		if (edgesLayer1.size() == 0) {
//			return null;
//		} else {
//			Map<de.uni_marburg.fb12.swt.cloneDetection.atl.escan.Fragment, List<Set<de.uni_marburg.fb12.swt.cloneDetection.atl.escan.Fragment>>> layer1 = buildLayer1(edgesLayer1);
//			List<Map<de.uni_marburg.fb12.swt.cloneDetection.atl.escan.Fragment, List<Set<de.uni_marburg.fb12.swt.cloneDetection.atl.escan.Fragment>>>> lattice3D = buildLattice3D(
//					layer1, edgesLayer1);
//
//			Set<Set<de.uni_marburg.fb12.swt.cloneDetection.atl.escan.Fragment>> cloneGroups = groupAndFilterLattice3D(lattice3D);
//			return cloneGroups;
//		}
//	}
//
//	public Set<Set<de.uni_marburg.fb12.swt.cloneDetection.atl.escan.Fragment>> getCloneGroups(
//			List<Set<de.uni_marburg.fb12.swt.cloneDetection.atl.escan.CapsuleEdge>> graphsFragmentsSetList) {
//		Map<de.uni_marburg.fb12.swt.cloneDetection.atl.escan.CapsuleEdge, List<Set<de.uni_marburg.fb12.swt.cloneDetection.atl.escan.CapsuleEdge>>> edgesLayer1
//				= getCapsuleEdgeMappingLayer1(graphsFragmentsSetList);
//
//		if (edgesLayer1.size() == 0) {
//			return null;
//		} else {
//			Map<de.uni_marburg.fb12.swt.cloneDetection.atl.escan.Fragment, List<Set<de.uni_marburg.fb12.swt.cloneDetection.atl.escan.Fragment>>> layer1 = buildLayer1(edgesLayer1);
//			List<Map<de.uni_marburg.fb12.swt.cloneDetection.atl.escan.Fragment, List<Set<de.uni_marburg.fb12.swt.cloneDetection.atl.escan.Fragment>>>> lattice3D = buildLattice3D(
//					layer1, edgesLayer1);
//
//			Set<Set<Fragment>> cloneGroups = groupAndFilterLattice3D(lattice3D);
//			return cloneGroups;
//		}
//	}
//
//	private List<Set<de.uni_marburg.fb12.swt.cloneDetection.atl.escan.CapsuleEdge>> getGraphsEdgeSetListWithAttributes(
//			List<MatchedRule> ruleList) {
//		List<Set<de.uni_marburg.fb12.swt.cloneDetection.atl.escan.CapsuleEdge>> graphsEdgeSetList = new LinkedList<Set<CapsuleEdge>>();
//		for (MatchedRule rule : ruleList) {
//			graphsEdgeSetList.add(ruleGraphMap.get(rule).edgeSet());
//		}
//		return graphsEdgeSetList;
//	}
//
//}
