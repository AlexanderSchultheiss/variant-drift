package de.hub.mse.variantdrift.clone.escan;

import de.hub.mse.variantdrift.clone.models.GenericEdge;
import de.hub.mse.variantdrift.clone.models.GenericGraph;
import de.hub.mse.variantdrift.clone.models.GenericNode;
import org.jgrapht.DirectedGraph;

import java.util.*;

public class EScanDetectionOriginal extends EScanDetection {
	public EScanDetectionOriginal(List<GenericGraph> rules) {
		super(rules);
	}

	public EScanDetectionOriginal(
			Map<GenericGraph, DirectedGraph<GenericNode, GenericEdge>> ruleGraphMap,
			List<GenericGraph> ruleList) {
		super(ruleGraphMap, ruleList);
	}

	@Override
	public void detectCloneGroups() {
		long startZeit = System.currentTimeMillis();
		if (DEBUG) System.out
				.println(startDetectCloneGroups("EScanDetectionArticleOriginal"));

		Set<Set<Fragment>> cloneGroups = runEScan();

		if (DEBUG) System.out.println(startConversion());

		resultAsCloneMatrix = CloneMatrixCreator
				.convertEScanResult(cloneGroups);

		if (DEBUG) System.out.println(endDetectCloneGroups(
				"EScanDetectionArticleOriginal", startZeit));
	}

	/**
	 * 
	 * the actual eScan algorithm from the paper, the line comments correspond
	 * to the lines of the pseudocode from the paper
	 * 
	 * @return the found cloneGroups
	 */
	private Set<Set<Fragment>> runEScan() {
		List<Set<Fragment>> lattice = new LinkedList<>();
		Set<Fragment> all1Fragments = getL1Fragment();
		Set<Fragment> layer1 = clones1(all1Fragments);
		Set<GenericEdge> edgesLayer1 = extractEdges(layer1);

		lattice.add(layer1);
		int startLayer = 0;

		// line 3
		for (Fragment f1 : lattice.get(startLayer)) {
			discover(f1, clones(f1, lattice.get(startLayer)), lattice,
					edgesLayer1, startLayer);
		}
		return eScanGroupAndFilterLattice(lattice);
	}

	/**
	 * 
	 * Discover from the article builds up lattice (the "line" comments are
	 * referring to the article)
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
	 * @param edgesLayer1
	 *            the edges a fragment maybe be extended with, q.v.
	 *            Fragment.extensOp
	 * @param kFromArticle
	 *            the k-th layer of lattice, in the article lattice starts with
	 *            1, but a List starts with index 0, so kFromArticle will be
	 *            decreased by 1 and Fragments of size 2 will be stored at index
	 *            1,
	 */
	private void discover(Fragment f, Collection<Fragment> fClones,
                          List<Set<Fragment>> lattice, Set<GenericEdge> edgesLayer1,
                          int kFromArticle) {

		if (fClones.size() <= 1) {
			System.out
					.println("EScanDetectionArticleOriginal - discover - fClones ???????");
		}

		// in the article lattice starts with 1, but a List starts with index 0,
		// so
		// a decreased version, k, of kFromArticle will be used instead
		int k = kFromArticle - 1;

		Set<Fragment> candidateSetCkp1 = new HashSet<>();
		// line 9 + 10
		for (Fragment g : fClones) {
			Set<Fragment> extensOp = g.extensOp(edgesLayer1);
			candidateSetCkp1.addAll(extensOp);
		}

		Set<Fragment> findClones;
		// line 11
		for (Fragment ckp1 : candidateSetCkp1) {
			// line 12
			if (f.isGeneratingParent(ckp1)) {
				// line 13
				findClones = clones(ckp1, candidateSetCkp1);
				// line 14
				if ((findClones.size() > 1)) {
					if (lattice.size() <= k + 1) {
						Set<Fragment> newLayer = new HashSet<>();
						lattice.add(newLayer);
					}
					// line 15
					lattice.get(k + 1).addAll(findClones);
					// line 16
					int kFromArticleNext = kFromArticle + 1;
					discover(ckp1, findClones, lattice, edgesLayer1,
							kFromArticleNext);
				}
			}
		}
	}
}
