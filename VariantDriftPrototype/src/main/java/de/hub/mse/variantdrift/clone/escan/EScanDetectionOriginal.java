package de.hub.mse.variantdrift.clone.escan;

import aatl.MatchedRule;
import aatl.Module;
import de.uni_marburg.fb12.swt.cloneDetection.atl.escan.CapsuleEdge;
import de.uni_marburg.fb12.swt.cloneDetection.atl.escan.CloneMatrixCreator;
import de.uni_marburg.fb12.swt.cloneDetection.atl.escan.EScanDetection;
import de.uni_marburg.fb12.swt.cloneDetection.atl.escan.Fragment;
import org.eclipse.emf.ecore.EObject;
import org.jgrapht.DirectedGraph;

import java.util.*;

public class EScanDetectionOriginal extends EScanDetection {
	public EScanDetectionOriginal(List<MatchedRule> rules) {
		super(rules);
	}
	
	public EScanDetectionOriginal(Set<Module> modules) {
		super(modules);
	}

	public EScanDetectionOriginal(
			Map<MatchedRule, DirectedGraph<EObject, de.uni_marburg.fb12.swt.cloneDetection.atl.escan.CapsuleEdge>> ruleGraphMap,
			List<MatchedRule> ruleList) {
		super(ruleGraphMap, ruleList);
	}

	@Override
	public void detectCloneGroups() {
		long startZeit = System.currentTimeMillis();
		if (DEBUG) System.out
				.println(startDetectCloneGroups("EScanDetectionArticleOriginal"));

		Set<Set<de.uni_marburg.fb12.swt.cloneDetection.atl.escan.Fragment>> cloneGroups = runEScan();

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
	private Set<Set<de.uni_marburg.fb12.swt.cloneDetection.atl.escan.Fragment>> runEScan() {
		List<Set<de.uni_marburg.fb12.swt.cloneDetection.atl.escan.Fragment>> lattice = new LinkedList<Set<de.uni_marburg.fb12.swt.cloneDetection.atl.escan.Fragment>>();
		Set<de.uni_marburg.fb12.swt.cloneDetection.atl.escan.Fragment> all1Fragments = getL1Fragment();
		Set<de.uni_marburg.fb12.swt.cloneDetection.atl.escan.Fragment> layer1 = clones1(all1Fragments);
		Set<de.uni_marburg.fb12.swt.cloneDetection.atl.escan.CapsuleEdge> edgesLayer1 = extractEdges(layer1);

		lattice.add(layer1);
		int startLayer = 1;

		// line 3
		for (de.uni_marburg.fb12.swt.cloneDetection.atl.escan.Fragment f1 : lattice.get(startLayer - 1)) {
			discover(f1, clones(f1, lattice.get(startLayer - 1)), lattice,
					edgesLayer1, startLayer);
		}
		Set<Set<de.uni_marburg.fb12.swt.cloneDetection.atl.escan.Fragment>> articleFilteredCG = eScanGroupAndFilterLattice(lattice);
		return articleFilteredCG;
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
	private void discover(de.uni_marburg.fb12.swt.cloneDetection.atl.escan.Fragment f, Collection<de.uni_marburg.fb12.swt.cloneDetection.atl.escan.Fragment> fClones,
                          List<Set<de.uni_marburg.fb12.swt.cloneDetection.atl.escan.Fragment>> lattice, Set<CapsuleEdge> edgesLayer1,
                          int kFromArticle) {

		if (fClones.size() <= 1) {
			System.out
					.println("EScanDetectionArticleOriginal - discover - fClones ???????");
		}

		// in the article lattice starts with 1, but a List starts with index 0,
		// so
		// a decreased version, k, of kFromArticle will be used instead
		int k = kFromArticle - 1;

		Set<de.uni_marburg.fb12.swt.cloneDetection.atl.escan.Fragment> candidateSetCkp1 = new HashSet<de.uni_marburg.fb12.swt.cloneDetection.atl.escan.Fragment>();
		// line 9 + 10
		for (de.uni_marburg.fb12.swt.cloneDetection.atl.escan.Fragment g : fClones) {
			Set<de.uni_marburg.fb12.swt.cloneDetection.atl.escan.Fragment> extensOp = g.extensOp(edgesLayer1);
			candidateSetCkp1.addAll(extensOp);
		}

		Set<de.uni_marburg.fb12.swt.cloneDetection.atl.escan.Fragment> findClones;
		// line 11
		for (de.uni_marburg.fb12.swt.cloneDetection.atl.escan.Fragment ckp1 : candidateSetCkp1) {
			// line 12
			if (f.isGeneratingParent(ckp1)) {
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
					discover(ckp1, findClones, lattice, edgesLayer1,
							kFromArticleNext);
				}
			}
		}
	}
}
