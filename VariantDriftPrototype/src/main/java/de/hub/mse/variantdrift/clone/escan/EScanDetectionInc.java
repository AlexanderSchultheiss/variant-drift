package de.hub.mse.variantdrift.clone.escan;

import aatl.MatchedRule;
import de.uni_marburg.fb12.swt.cloneDetection.atl.escan.CapsuleEdge;
import de.uni_marburg.fb12.swt.cloneDetection.atl.escan.CloneMatrixCreator;
import de.uni_marburg.fb12.swt.cloneDetection.atl.escan.EScanDetection;
import de.uni_marburg.fb12.swt.cloneDetection.atl.escan.Fragment;
import org.eclipse.emf.ecore.EObject;
import org.jgrapht.DirectedGraph;

import java.util.*;

public class EScanDetectionInc extends EScanDetection {

	/**
	 * for CombineDetection
	 */
	private final boolean ignoreIsGeneratingParent;

	public EScanDetectionInc(List<MatchedRule> rules) {
		super(rules);
		ignoreIsGeneratingParent = false;
	}

	public EScanDetectionInc(
			Map<MatchedRule, DirectedGraph<EObject, de.uni_marburg.fb12.swt.cloneDetection.atl.escan.CapsuleEdge>> ruleGraphMap,
			List<MatchedRule> ruleList) {
		super(ruleGraphMap, ruleList);
		ignoreIsGeneratingParent = false;
	}

	public EScanDetectionInc(
			Map<MatchedRule, DirectedGraph<EObject, de.uni_marburg.fb12.swt.cloneDetection.atl.escan.CapsuleEdge>> ruleGraphMap,
			List<MatchedRule> ruleList, 
			boolean ignoreIsGeneratingParent) {
		super(ruleGraphMap, ruleList);
		this.ignoreIsGeneratingParent = ignoreIsGeneratingParent;
	}

	@Override
	public void detectCloneGroups() {
		int itemizedUpTo = getMaxSizeOfClones() + 1; // +1 just to be on the
														// safe side
		detectCloneGroups(itemizedUpTo);
	}

	protected int getMaxSizeOfClones() {
		int max = 0;
		for (DirectedGraph<EObject, de.uni_marburg.fb12.swt.cloneDetection.atl.escan.CapsuleEdge> graph : ruleGraphMap.values()) {
			if (graph.edgeSet().size() > max) {
				max = graph.edgeSet().size();
			}
		}
		return max;
	}

	/**
	 * 
	 * @param itemizedUpTo
	 *            the Layer up to with lattice is build up, step by step,
	 *            respectively layer by layer itemizedUpTo < 0 --> normal eScan
	 *            Algortihmn
	 */
	public void detectCloneGroups(int itemizedUpTo) {
		final int START_LAYER = 1;
		List<Set<de.uni_marburg.fb12.swt.cloneDetection.atl.escan.Fragment>> lattice = new LinkedList<Set<de.uni_marburg.fb12.swt.cloneDetection.atl.escan.Fragment>>();
		detectCloneGroups(START_LAYER, itemizedUpTo, lattice);

	}

	public void detectCloneGroups(int startLayer, List<Set<de.uni_marburg.fb12.swt.cloneDetection.atl.escan.Fragment>> lattice) {
		int itemizedUpTo = getMaxSizeOfClones() + 1; // +1 just to be on the
														// safe side
		detectCloneGroups(startLayer, itemizedUpTo, lattice);
	}

	/**
	 * 
	 * @param startLayer
	 *            the layer of lattice from which the algorithm should start
	 * @param itemizedUpTo
	 *            the Layer up to with lattice is build up, step by step,
	 *            respectively layer by layer itemizedUpTo < 0 --> normal eScan
	 *            Algortihmn
	 * @param lattice
	 *            the data structure eScan works with
	 */
	private void detectCloneGroups(int startLayer, int itemizedUpTo,
			List<Set<de.uni_marburg.fb12.swt.cloneDetection.atl.escan.Fragment>> lattice) {
		long startZeit = System.currentTimeMillis();
		if (DEBUG) System.out.println(startDetectCloneGroups("EScanDetectionArticleInc"));
		boolean latticeComplete = false;
		Set<de.uni_marburg.fb12.swt.cloneDetection.atl.escan.Fragment> all1Fragments = getL1Fragment();
		final int START_LAYER = startLayer; // <-- Article way of counting
		final int MAX_LAYER = -1;

		for (int i = START_LAYER; i < itemizedUpTo; i++) {
			eScanBuildLatticeAndPrintToConsole(lattice, all1Fragments, i,
					(i + 1));
			if (lattice.size() < (i)) {
				latticeComplete = true;
				break;
			}
		}

		Set<Set<de.uni_marburg.fb12.swt.cloneDetection.atl.escan.Fragment>> cloneGroups;
		if ((latticeComplete) || (itemizedUpTo == lattice.size())) {
			cloneGroups = eScanGroupAndFilterLattice(lattice);
		} else {
			if (itemizedUpTo < 0) {
				itemizedUpTo = START_LAYER;
			}
			eScanBuildLatticeAndPrintToConsole(lattice, all1Fragments,
					itemizedUpTo, MAX_LAYER);
			cloneGroups = eScanGroupAndFilterLattice(lattice);
		}

		if (DEBUG) System.out.println(startConversion());

		resultAsCloneMatrix = CloneMatrixCreator
				.convertEScanResult(cloneGroups);

		if (DEBUG) System.out.println(endDetectCloneGroups("EScanDetectionArticleInc",
				startZeit));
	}

	/**
	 * line 2 and 3 of eScan PseudoCode from the article
	 * 
	 * @param lattice
	 *            the data structure eScan works with, the first layer will be
	 *            added (the Fragments of size 1) (which have a leat one clone,
	 *            since lattice only contains cloned fragments)
	 * @param all1Fragments
	 *            (all possible Fragments of size 1)
	 * @param startLayer
	 *            the layer of lattice from which the algorithm should start
	 * @param maxLayer
	 *            the Layer up to with lattice is build up (article way of
	 *            counting), if maxLayer < 0 lattice will be build up completely
	 */
	protected void eScanBuildLatticeAndPrintToConsole(
            List<Set<de.uni_marburg.fb12.swt.cloneDetection.atl.escan.Fragment>> lattice, Set<de.uni_marburg.fb12.swt.cloneDetection.atl.escan.Fragment> all1Fragments,
            int startLayer, int maxLayer) {

		if (DEBUG) System.out.println();
		if (DEBUG) System.out.println("eScan from: " + startLayer + " to " + maxLayer);
		lattice = eScanBuildLattice(lattice, all1Fragments, startLayer,
				maxLayer);

		Date date = new Date();
		if (DEBUG) System.out.println();
		if (DEBUG) System.out.println("lattice done - lattice.size: " + lattice.size()
				+ ": " + date.toString());
		printLattice(lattice);

	}

	/**
	 * modified line 2 and 3 of eScan PseudoCode from the article
	 * 
	 * @param lattice
	 *            the data structure eScan works with, the first layer will be
	 *            added (the Fragments of size 1) (which have a leat one clone,
	 *            since lattice only contains cloned fragments)
	 * @param all1Fragments
	 *            (all possible Fragments of size 1)
	 * @param startLayer
	 *            the layer of lattice from which the algorithm should start
	 * @param maxLayer
	 *            the Layer up to with lattice is build up (article way of
	 *            counting), if maxLayer < 0 lattice will be build up completely
	 * @return build up lattice
	 */
	private List<Set<de.uni_marburg.fb12.swt.cloneDetection.atl.escan.Fragment>> eScanBuildLattice(List<Set<de.uni_marburg.fb12.swt.cloneDetection.atl.escan.Fragment>> lattice,
                                                                                                   Set<de.uni_marburg.fb12.swt.cloneDetection.atl.escan.Fragment> all1Fragments, int startLayer, int maxLayer) {

		Set<de.uni_marburg.fb12.swt.cloneDetection.atl.escan.Fragment> layer1 = clones1(all1Fragments);
		Set<de.uni_marburg.fb12.swt.cloneDetection.atl.escan.CapsuleEdge> edgesLayer1 = extractEdges(layer1);

		if (DEBUG) System.out.println("edgesLayer1.size(): " + edgesLayer1.size());

		if (startLayer == 1) {
			if (DEBUG) System.out.println("layer1.size(): " + layer1.size());
			lattice.add(layer1);
		}

		if (lattice.size() < startLayer || lattice.size() == 1) {

		} else {
			while (((lattice.size() > startLayer - 1))
					&& ((lattice.get(startLayer - 1).size() == 0))) {
				startLayer++;
			}
			// line 3
			for (de.uni_marburg.fb12.swt.cloneDetection.atl.escan.Fragment f1 : lattice.get(startLayer - 1)) {
				discover(f1, clones(f1, lattice.get(startLayer - 1)), lattice,
						edgesLayer1, startLayer, maxLayer);
			}
		}
		return lattice;
	}

	/**
	 * 
	 * modified Discover from the article builts up lattice (the "line" comments
	 * are refering to the article)
	 * 
	 * @param f
	 *            the first parameter from the article
	 * @param fClones
	 *            the second parameter from the article, fClones contains f and
	 *            all of f clones
	 * @param lattice
	 *            the data structure eScan works with, the first layer must
	 *            already be added (the Fragments of size 1) (which have a leat
	 *            one clone, since lattice only contains cloned fragments) (at
	 *            index 0, c.p. kFromArticle)
	 * @param edgesForExtens
	 *            the edges a fragment maybe be extended with, q.v.
	 *            Fragment.extensOp
	 * @param kFromArticle
	 *            the kth layer of lattice, in the article lattice starts with
	 *            1, but a List starts with index 0, so kFromArticle will be
	 *            decreased by 1 and Fragments of size 2 will be stored at index
	 *            1,
	 * @param maxLayer
	 *            the Layer up to with lattice is build up (article way of
	 *            counting), if maxLayer < 0 lattice will be build up completely
	 */
	private void discover(de.uni_marburg.fb12.swt.cloneDetection.atl.escan.Fragment f, Set<de.uni_marburg.fb12.swt.cloneDetection.atl.escan.Fragment> fClones,
                          List<Set<de.uni_marburg.fb12.swt.cloneDetection.atl.escan.Fragment>> lattice, Set<CapsuleEdge> edgesForExtens,
                          int kFromArticle, int maxLayer) {
		if ((maxLayer < 0) || (kFromArticle < maxLayer)) {

			if (fClones.size() <= 1) {
				System.out
						.println("EScanDetectionArticleOriginal - discover - fClones ???????");
			}

			// in the article lattice starts with 1, but a List starts with
			// index 0, so
			// a decreased version, k, of kFromArticle will be used instead
			int k = kFromArticle - 1;

			Set<de.uni_marburg.fb12.swt.cloneDetection.atl.escan.Fragment> candidateSetCkp1 = new HashSet<de.uni_marburg.fb12.swt.cloneDetection.atl.escan.Fragment>();
			// line 9 + 10
			for (de.uni_marburg.fb12.swt.cloneDetection.atl.escan.Fragment g : fClones) {
				Set<de.uni_marburg.fb12.swt.cloneDetection.atl.escan.Fragment> extensOp = g.extensOp(edgesForExtens);
				candidateSetCkp1.addAll(extensOp);
			}

			if (lattice == null) {
				System.out.println("lattice is missing");
			}

			Set<de.uni_marburg.fb12.swt.cloneDetection.atl.escan.Fragment> findClones;
			// line 11
			for (de.uni_marburg.fb12.swt.cloneDetection.atl.escan.Fragment ckp1 : candidateSetCkp1) {
				// line 12
				if (ignoreIsGeneratingParent || f.isGeneratingParent(ckp1)) {
					// line 13
					findClones = clones(ckp1, candidateSetCkp1);
					// line 14
					if ((findClones.size() > 1)) {
						if (lattice.size() <= k + 1) {
							Set<de.uni_marburg.fb12.swt.cloneDetection.atl.escan.Fragment> newLayer = new HashSet<Fragment>();
							lattice.add(newLayer);
						}
						// line 15
						lattice.get(k + 1).addAll(findClones);
						// line 16
						int kFromArticleNext = kFromArticle + 1;
						discover(ckp1, findClones, lattice, edgesForExtens,
								kFromArticleNext, maxLayer);
					}
				}
			}
		}
	}

}
