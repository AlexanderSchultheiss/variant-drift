//package de.hub.mse.variantdrift.clone.escan;
//
//import aatl.MatchedRule;
//import de.uni_marburg.fb12.swt.cloneDetection.atl.escan.CapsuleEdge;
//import de.uni_marburg.fb12.swt.cloneDetection.atl.escan.EScanDetection;
//import de.uni_marburg.fb12.swt.cloneDetection.atl.escan.Fragment;
//import de.uni_marburg.fb12.swt.cloneDetection.atl.escan.LabelCreator;
//import org.eclipse.emf.ecore.EObject;
//import org.jgrapht.DirectedGraph;
//
//import java.util.*;
//
//public abstract class EScanDetection3DAbstract extends EScanDetection {
//
//	public EScanDetection3DAbstract(List<MatchedRule> rules) {
//		super(rules);
//	}
//
//	public EScanDetection3DAbstract(
//			Map<MatchedRule, DirectedGraph<EObject, CapsuleEdge>> ruleGraphMap,
//			List<MatchedRule> ruleList) {
//		super(ruleGraphMap, ruleList);
//	}
//
//	public abstract void detectCloneGroups(
//			Map<Fragment, List<Set<Fragment>>> topLayer);
//
//	protected List<Map<Fragment, List<Set<Fragment>>>> buildLattice3D(
//			Map<Fragment, List<Set<Fragment>>> topLayer,
//			Map<CapsuleEdge, List<Set<CapsuleEdge>>> edgesLayer1) {
//
//		List<Map<Fragment, List<Set<Fragment>>>> lattice3D
//				= new LinkedList<Map<Fragment, List<Set<Fragment>>>>();
//		lattice3D.add(topLayer);
//
//		int layerIndex = topLayer.keySet().iterator().next().getCapsuleEdges()
//				.size();
//		//System.out.println("Fragment.size() = layerIndex = " + layerIndex);
//		if (layerIndex == 1) {
//			//System.out.println("layerIndex: " + layerIndex + " layer.size(): "
//			//		+ topLayer.size());
//		} else {
//			//System.out.println("layerIndex: " + layerIndex
//			//		+ " topLayer.size(): " + topLayer.size());
//		}
//
//		while (true) {
//			Map<Fragment, List<Set<Fragment>>> layer = buildNextLayer(
//					lattice3D.get(lattice3D.size() - 1), edgesLayer1);
//			// checkLayer(layer);
//			if (layer.size() != 0) {
//				layerIndex++;
//				Date date = new Date();
//				//System.out.println("layerIndex: " + layerIndex
//				//		+ " layer.size(): " + layer.size() + " - "
//				//		+ date.toString());
//				lattice3D.add(layer);
//			} else {
//				break;
//			}
//		}
//		return lattice3D;
//	}
//
//	private Map<Fragment, List<Set<Fragment>>> buildNextLayer(
//			Map<Fragment, List<Set<Fragment>>> layer,
//			Map<CapsuleEdge, List<Set<CapsuleEdge>>> edgesLayer1) {
//		Map<Fragment, List<Set<Fragment>>> res = new HashMap<Fragment, List<Set<Fragment>>>();
//		for (Fragment fragment : layer.keySet()) {
//			for (CapsuleEdge capsuleEdge : edgesLayer1.keySet()) {
//				Fragment fragmentExt = fragment.extensOp(capsuleEdge);
//
//				if ((fragmentExt != null)) {
//					List<Set<Fragment>> cloneCandidates = getCloneCandidates(
//							layer.get(fragment), edgesLayer1.get(capsuleEdge));
//					List<Set<Fragment>> clonesFragmentExt = reduceToClones(
//							fragmentExt, cloneCandidates);
//					if (clonesFragmentExt != null) {
//						if (!res.containsKey(fragmentExt)) {
//							res.put(fragmentExt, clonesFragmentExt);
//						} else {
//							for (int i = 0; i < clonesFragmentExt.size(); i++) {
//								res.get(fragmentExt).get(i)
//										.addAll((clonesFragmentExt).get(i));
//							}
//						}
//					}
//				}
//			}
//		}
//		return res;
//	}
//
//	/**
//	 * extend the fragments at the k-th position with the edges at the k-th
//	 * position
//	 *
//	 * @param valuesEdgesLayer1
//	 * @param ValuesLayer
//	 * @return
//	 */
//	private List<Set<Fragment>> getCloneCandidates(
//			List<Set<Fragment>> valuesLayer,
//			List<Set<CapsuleEdge>> valuesEdgesLayer1) {
//		List<Set<Fragment>> res = new LinkedList<Set<Fragment>>();
//		for (int i = 0; i < valuesLayer.size(); i++) {
//			Set<Fragment> tempRes = new HashSet<Fragment>();
//			for (Fragment fragment : valuesLayer.get(i)) {
//				tempRes.addAll(fragment.extensOp(valuesEdgesLayer1.get(i)));
//			}
//			res.add(tempRes);
//		}
//		return res;
//	}
//
//	private List<Set<Fragment>> reduceToClones(Fragment fragment,
//			List<Set<Fragment>> cloneCandidates) {
//		List<Set<Fragment>> res = new LinkedList<Set<Fragment>>();
//		boolean found = false;
//		for (Set<Fragment> candidateFragmentSet : cloneCandidates) {
//			found = false;
//			Set<Fragment> tempRes = new HashSet<Fragment>();
//			for (Fragment f : candidateFragmentSet) {
//				if (f.isIsomorph(fragment)) {
//					tempRes.add(f);
//					found = true;
//				}
//			}
//
//			if (found) {
//				res.add(tempRes);
//			} else {
//				return null;
//			}
//		}
//		return res;
//	}
//
//	protected Set<Set<Fragment>> groupAndFilterLattice3D(
//			List<Map<Fragment, List<Set<Fragment>>>> lattice3D) {
//		//System.out.println("Lattice3D erstellt");
//		List<Set<Fragment>> lattice = convertToLattice(lattice3D);
//		return eScanGroupAndFilterLattice(lattice);
//	}
//
//	private List<Set<Fragment>> convertToLattice(
//			List<Map<Fragment, List<Set<Fragment>>>> lattice3D) {
//		List<Set<Fragment>> res = new LinkedList<Set<Fragment>>();
//		for (Map<Fragment, List<Set<Fragment>>> layer : lattice3D) {
//			Set<Fragment> resLayer = converToLatticeLayer(layer);
//			res.add(resLayer);
//		}
//		return res;
//	}
//
//	private Set<Fragment> converToLatticeLayer(
//			Map<Fragment, List<Set<Fragment>>> layer) {
//		Set<Fragment> res = new HashSet<Fragment>();
//		for (Fragment keyFragment : layer.keySet()) {
//			res.add(keyFragment);
//			for (Set<Fragment> fragmentsSet : layer.get(keyFragment)) {
//				res.addAll(fragmentsSet);
//			}
//		}
//		return res;
//	}
//
//	protected Set<Set<Fragment>> groupLayerLattice3D(
//			Map<Fragment, List<Set<Fragment>>> layer) {
//		Set<Set<Fragment>> resLayer = new HashSet<Set<Fragment>>();
//		for (Fragment keyFragment : layer.keySet()) {
//			Set<Fragment> resSet = new HashSet<Fragment>();
//			resSet.add(keyFragment);
//			for (Set<Fragment> fragmentsSet : layer.get(keyFragment)) {
//				resSet.addAll(fragmentsSet);
//			}
//			resLayer.add(resSet);
//		}
//		return resLayer;
//	}
//
//	protected Map<Fragment, List<Set<Fragment>>> buildLayer1(
//			Map<CapsuleEdge, List<Set<CapsuleEdge>>> edgesLayer1) {
//		Map<Fragment, List<Set<Fragment>>> res = new HashMap<Fragment, List<Set<Fragment>>>();
//		for (CapsuleEdge capsuleEdge : edgesLayer1.keySet()) {
//
//			Set<CapsuleEdge> set = new HashSet<CapsuleEdge>();
//			set.add(capsuleEdge);
//			Fragment key = new Fragment(set, capsuleEdge.getOriginalEdge().getRule(),
//					ruleGraphMap.get(capsuleEdge.getOriginalEdge().getRule()));
//
//			List<Set<Fragment>> value = new LinkedList<Set<Fragment>>();
//			for (Set<CapsuleEdge> capsuleEdgeSet : edgesLayer1.get(capsuleEdge)) {
//				Set<Fragment> fragmentSet = new HashSet<Fragment>();
//				for (CapsuleEdge ce : capsuleEdgeSet) {
//					Set<CapsuleEdge> s = new HashSet<CapsuleEdge>();
//					s.add(ce);
//					fragmentSet.add(new Fragment(s, ce.getOriginalEdge().getRule(), ruleGraphMap
//							.get(ce.getOriginalEdge().getRule())));
//				}
//				value.add(fragmentSet);
//			}
//			res.put(key, value);
//		}
//		return res;
//	}
//
//	public Map<CapsuleEdge, List<Set<CapsuleEdge>>> getCapsuleEdgeMappingLayer1(
//			List<Set<CapsuleEdge>> graphsEdgeSetList) {
//		Map<CapsuleEdge, List<Set<CapsuleEdge>>> res
//				= new HashMap<CapsuleEdge, List<Set<CapsuleEdge>>>();
//		for (CapsuleEdge capsuleEdge : graphsEdgeSetList.get(0)) {
//			List<Set<CapsuleEdge>> value = new ArrayList<Set<CapsuleEdge>>();
//			boolean found = false;
//			for (int i = 1; i < graphsEdgeSetList.size(); i++) {
//				Set<CapsuleEdge> set = new HashSet<CapsuleEdge>();
//				value.add(set);
//				Set<CapsuleEdge> capsuleEdgeSetGraphI = graphsEdgeSetList
//						.get(i);
//				found = false;
//				for (CapsuleEdge ce : capsuleEdgeSetGraphI) {
//					if ((LabelCreator.getSimpleModelCdEdgeLabel(capsuleEdge,
//							ruleGraphMap.get(rules.get(0))))
//							.equals((LabelCreator.getSimpleModelCdEdgeLabel(ce,
//									ruleGraphMap.get(rules.get(i)))))) {
//						found = true;
//						value.get(i - 1).add(ce);
//					}
//				}
//				if (!found) {
//					break;
//				}
//			}
//
//			if (found) {
//				res.put(capsuleEdge, value);
//			}
//		}
//
//		return res;
//	}
//}
