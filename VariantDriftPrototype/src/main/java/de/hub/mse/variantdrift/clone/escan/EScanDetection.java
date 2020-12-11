package de.hub.mse.variantdrift.clone.escan;

import de.hub.mse.variantdrift.clone.models.GenericEdge;
import de.hub.mse.variantdrift.clone.models.GenericGraph;
import de.hub.mse.variantdrift.clone.models.GenericNode;
import org.jgrapht.DirectedGraph;
import org.jgrapht.alg.BronKerboschCliqueFinder;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.Pseudograph;

import java.util.*;

/**
 * 
 * contains the grouping steps (article eScan pseudocode, line 4) and the
 * filter-step-method (article eScan pseudocode, line 5) and combine them into a
 * group and filterStep Method contains also the Clones(fk)-method (article 4.1)
 * and some method to get the Fragments for the first layer of lattice and a
 * methods to extract the Edges of the first Layer to get the "extendEdges" for
 * Discover
 * 
 * Provides further a print-method for lattice.
 * 
 */
public abstract class EScanDetection extends CloneDetection {
	
	public EScanDetection(List<GenericGraph> models) {
		super(models);
	}

	public EScanDetection(Map<GenericGraph, DirectedGraph<GenericNode, GenericEdge>> ruleGraphMap, List<GenericGraph> rules) {
		super(ruleGraphMap, rules);
	}

	/**
	 * line 4 and 5 from eScan PseudoCode from the article
	 */
	public Set<Set<Fragment>> eScanGroupAndFilterLattice(List<Set<Fragment>> lattice) {
		// line 4
		List<Collection<Set<Fragment>>> groupedLayerLatticeStep1 = groupFragmentsInToCloneGroupsStep1(lattice);
		List<Set<Set<Fragment>>> groupedLayerLattice = groupFragmentsInToCloneGroupsStep2(groupedLayerLatticeStep1);

		// line 5
		// line 6
		return eScanFilterLattice(groupedLayerLattice);
	}

	protected Set<GenericEdge> extractEdges(Set<Fragment> layer1) {
		Set<GenericEdge> res = new HashSet<>();

		for (Fragment fragment : layer1) {
			res.addAll(fragment.getGenericEdges());
		}

		return res;
	}

	private List<Collection<Set<Fragment>>> groupFragmentsInToCloneGroupsStep1(List<Set<Fragment>> lattice) {
		List<Collection<Set<Fragment>>> groupedLayerLattice = new LinkedList<>();
		for (Set<Fragment> layer : lattice) {
			groupedLayerLattice.add(groupFragmentsInToCloneGroupsStep1(layer));
		}

		return groupedLayerLattice;
	}

	private Collection<Set<Fragment>> groupFragmentsInToCloneGroupsStep1(Set<Fragment> layer) {
		return doGroupingStep1(layer);
	}

	private List<Set<Set<Fragment>>> groupFragmentsInToCloneGroupsStep2(
			List<Collection<Set<Fragment>>> groupedLayerLatticeStep1) {
		List<Set<Set<Fragment>>> groupedLayerLattice = new LinkedList<>();
		for (Collection<Set<Fragment>> layer : groupedLayerLatticeStep1) {
			Set<Set<Fragment>> groupedLayer = groupFragmentsInToCloneGroupsStep2(layer);
			if (groupedLayer.size() >= 1) {
				groupedLayerLattice.add(groupedLayer);
			}
		}
		return groupedLayerLattice;
	}

	private Set<Set<Fragment>> groupFragmentsInToCloneGroupsStep2(Collection<Set<Fragment>> layer) {
		Set<Set<Fragment>> groupedLayer = new HashSet<>();

		for (Set<Fragment> setOfSameCanonicalLabeledFragments : layer) {
			Collection<Set<Fragment>> setOfSetOfSameCanonicalLabeledNonOverlappingFragments = doGroupingStep2(
					setOfSameCanonicalLabeledFragments);
			for (Set<Fragment> setOfSameCanonicalLabeledNonOverlappingFragments : setOfSetOfSameCanonicalLabeledNonOverlappingFragments) {
				if (setOfSameCanonicalLabeledNonOverlappingFragments.size() > 1) {
					groupedLayer.add(setOfSameCanonicalLabeledNonOverlappingFragments);
				}
			}
		}

		return groupedLayer;
	}

	/**
	 * 
	 * group Fragments with the same canonical label into a Set
	 *
	 * 
	 */
	private Collection<Set<Fragment>> doGroupingStep1(Set<Fragment> layer) {
		Map<String, Set<Fragment>> labelsAndFragments = new HashMap<>();

		for (Fragment f : layer) {
			String label = f.getLabel();
			if (labelsAndFragments.containsKey(label)) {
				labelsAndFragments.get(label).add(f);
			} else {
				Set<Fragment> fragments = new HashSet<>();
				fragments.add(f);
				labelsAndFragments.put(label, fragments);
			}
		}

		return labelsAndFragments.values();
	}

	/**
	 * ensures the non-overlapping Condition
	 *
	 */
	private Collection<Set<Fragment>> doGroupingStep2(Set<Fragment> setOfSameCanonicalLabeledFragments) {
		return doCliqueCoverGroupping(setOfSameCanonicalLabeledFragments);
	}

	private Collection<Set<Fragment>> doCliqueCoverGroupping(Set<Fragment> setOfSameCanonicalLabeledFragments) {
		Pseudograph<Fragment, DefaultEdge> graph = getCliqueCoverGraph(setOfSameCanonicalLabeledFragments);
		BronKerboschCliqueFinder<Fragment, DefaultEdge> bronKerboschCliqueFinder = new BronKerboschCliqueFinder<>(
				graph);
		return bronKerboschCliqueFinder.getAllMaximalCliques();
	}

	private Pseudograph<Fragment, DefaultEdge> getCliqueCoverGraph(Set<Fragment> setOfSameCanonicalLabeledFragments) {
		Pseudograph<Fragment, DefaultEdge> graph = new Pseudograph<>(DefaultEdge.class);
		for (Fragment f : setOfSameCanonicalLabeledFragments) {
			graph.addVertex(f);
		}
		for (Fragment fragment : setOfSameCanonicalLabeledFragments) {
			for (Fragment f : setOfSameCanonicalLabeledFragments) {
				if (!fragment.isNodeOverlapping(f)) {
					if (!((graph.containsEdge(fragment, f) || graph.containsEdge(f, fragment)))) {
						graph.addEdge(fragment, f);
					}
				}
			}
		}
		return graph;
	}

	/**
	 * 
	 * @param fragment
	 *            - the cloneCandidates-Set are searched for clones of this
	 *            Fragment
	 * @return a set containing fragment and all of its clones in
	 *         cloneCandidates
	 */

	protected Set<Fragment> clones(Fragment fragment, Set<Fragment> cloneCandidates) {
		Set<Fragment> res = new HashSet<>();

		for (Fragment f : cloneCandidates) {
			if (f.isIsomorph(fragment)) {
				if (!f.isNodeOverlapping(fragment)) {
					res.add(f);
				}
			}
		}

		if (res.size() >= 1) {
			// clones have to be non-overlapping, so fragment could not be a
			// clone of itself,
			// but if a clone is found, fragment should be contained in the
			// result,
			// so it is added, if res.size() >= 1
			res.add(fragment);
		}
		return res;
	}

	/**
	 *
	 * @return all clones from cloneCandidates
	 */

	protected Set<Fragment> clones1(Set<Fragment> cloneCandidates) {
		Set<Fragment> res = new HashSet<>(); // LinkedList<Fragment>();

		for (Fragment f : cloneCandidates) {
			Collection<Fragment> clones = clones(f, cloneCandidates);
			if (clones.size() > 1) {
				res.addAll(clones);
			}
		}
		return res;
	}

	/**
	 * 
	 * @return all possible Fragments of size 1 from the computation graphs
	 */
	protected Set<Fragment> getL1Fragment() {
		return getFragments();
	}

	private Set<Fragment> getFragments() {
		Set<Fragment> res = new HashSet<>();

		for (GenericGraph r : modelGraphMap.keySet()) {
			for (GenericEdge h : modelGraphMap.get(r).edgeSet()) {
				Set<GenericEdge> c = new HashSet<>();
				c.add(h);
				Fragment fragment = new Fragment(c, r, modelGraphMap.get(r));
				res.add(fragment);
			}

		}
		return res;
	}

	/**
	 * 
	 * @return all possible Fragments of size 1 from the computation graphs that
	 *         are not based on capsuleEdges, which contains an Attribute
	 */
	protected Set<Fragment> getL1FragmentWithoutAttributes() {
		return getFragments();
	}

	protected void printLattice(List<Set<Fragment>> lattice) {
		if (DEBUG) System.out.println("Lattice: ");
		int k = 1;
		int anzahlFragmente = 0;
		for (Set<Fragment> layer : lattice) {
			if (DEBUG) System.out.println("Layer: " + k + " Number of Fragments: " + layer.size());
			anzahlFragmente = anzahlFragmente + layer.size();
			k++;
		}
		if (DEBUG) System.out.println("lattice: " + anzahlFragmente + " Fragments");
		if (DEBUG) System.out.println();
	}

	/**
	 * eScan pseudocode line 5 the filter-step, all covered CloneGroups will be
	 * removed
	 * 
	 * @param groupedLayerLattice
	 *            lattice already grouped accordingly to eScan pseudocode line 4
	 */
	protected Set<Set<Fragment>> eScanFilterLattice(List<Set<Set<Fragment>>> groupedLayerLattice) {
		if (groupedLayerLattice.isEmpty())
			return new HashSet<>();

		Set<Set<Fragment>> upperLayersUncoveredCloneGroups = groupedLayerLattice.get(groupedLayerLattice.size() - 1);
		Set<Set<Fragment>> temp = new HashSet<>();

		for (int i = (groupedLayerLattice.size() - 2); i >= 0; i--) {
			temp.addAll(getAllUncoveredCGFromLayer(groupedLayerLattice.get(i), upperLayersUncoveredCloneGroups));
			upperLayersUncoveredCloneGroups.addAll(temp);
			temp = new HashSet<>();
		}
		return upperLayersUncoveredCloneGroups;
	}

	private Set<Set<Fragment>> getAllUncoveredCGFromLayer(Set<Set<Fragment>> layer,
			Set<Set<Fragment>> upperLayersFragments) {
		Set<Set<Fragment>> allUncoveredCGFromLayer = new HashSet<>();
		for (Set<Fragment> cloneGroupFromLayer : layer) {
			boolean isCovered = false;
			for (Set<Fragment> cloneGroupFromUpperLayers : upperLayersFragments) {
				if (isCovered(cloneGroupFromLayer, cloneGroupFromUpperLayers)) {
					isCovered = true;
					break;
				}
			}
			if (!isCovered) {
				allUncoveredCGFromLayer.add(cloneGroupFromLayer);
			}
		}
		return allUncoveredCGFromLayer;
	}

	private boolean isCovered(Set<Fragment> cloneGroupFromLayer, Set<Fragment> cloneGroupFromUpperLayers) {
		for (Fragment fragmentFromLayer : cloneGroupFromLayer) {
			boolean fragmentIsCovered = false;
			for (Fragment fragmentFromUpperLayers : cloneGroupFromUpperLayers) {
				if (fragmentFromLayer.isSubFragment(fragmentFromUpperLayers)) {
					fragmentIsCovered = true;
					break;
				}
			}
			if (!fragmentIsCovered) {
				return false;
			}
		}
		return true;
	}

}
