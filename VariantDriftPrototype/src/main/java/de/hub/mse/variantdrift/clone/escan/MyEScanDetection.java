package de.hub.mse.variantdrift.clone.escan;

import de.uni_marburg.fb12.swt.cloneDetection.henshinToIntegrated.CapsuleEdge;
import de.uni_marburg.fb12.swt.cloneDetection.modelCd.Fragment;
import org.eclipse.emf.henshin.model.Node;
import org.eclipse.emf.henshin.model.Rule;
import org.jgrapht.DirectedGraph;
import org.jgrapht.alg.BronKerboschCliqueFinder;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.Pseudograph;

import java.util.*;

public abstract class MyEScanDetection extends MyCloneDetection {
    public MyEScanDetection(Collection<Rule> rules) {
        super(rules);
    }

    public MyEScanDetection(Map<Rule, DirectedGraph<Node, CapsuleEdge>> ruleGraphMap, List<Rule> ruleList, boolean considerAttributeNodes) {
        super(ruleGraphMap, ruleList, considerAttributeNodes);
    }

    public Set<Set<Fragment>> eScanGroupAndFilterLattice(List<Set<Fragment>> lattice) {
        List<Collection<Set<Fragment>>> groupedLayerLatticeStep1 = this.groupFragmentsInToCloneGroupsStep1(lattice);
        List<Set<Set<Fragment>>> groupedLayerLattice = this.groupFragmentsInToCloneGroupsStep2(groupedLayerLatticeStep1);
        return this.eScanFilterLattice(groupedLayerLattice);
    }

    protected Set<CapsuleEdge> extractEdges(Set<Fragment> layer1) {
        Set<CapsuleEdge> res = new HashSet<>();

        for (Fragment fragment : layer1) {
            res.addAll(fragment.getCapsuleEdges());
        }

        return res;
    }

    private List<Collection<Set<Fragment>>> groupFragmentsInToCloneGroupsStep1(List<Set<Fragment>> lattice) {
        List<Collection<Set<Fragment>>> groupedLayerLattice = new LinkedList<>();

        for (Set<Fragment> fragments : lattice) {
            groupedLayerLattice.add(this.groupFragmentsInToCloneGroupsStep1(fragments));
        }

        return groupedLayerLattice;
    }

    private Collection<Set<Fragment>> groupFragmentsInToCloneGroupsStep1(Set<Fragment> layer) {
        return this.doGroupingStep1(layer);
    }

    private List<Set<Set<Fragment>>> groupFragmentsInToCloneGroupsStep2(List<Collection<Set<Fragment>>> groupedLayerLatticeStep1) {
        List<Set<Set<Fragment>>> groupedLayerLattice = new LinkedList<>();

        for (Collection<Set<Fragment>> sets : groupedLayerLatticeStep1) {
            Set<Set<Fragment>> groupedLayer = this.groupFragmentsInToCloneGroupsStep2(sets);
            if (groupedLayer.size() >= 1) {
                groupedLayerLattice.add(groupedLayer);
            }
        }

        return groupedLayerLattice;
    }

    private Set<Set<Fragment>> groupFragmentsInToCloneGroupsStep2(Collection<Set<Fragment>> layer) {
        Set<Set<Fragment>> groupedLayer = new HashSet<>();

        for (Set<Fragment> fragments : layer) {
            Collection<Set<Fragment>> setOfSetOfSameCanonicalLabeledNonOverlappingFragments = this.doGroupingStep2(fragments);

            for (Set<Fragment> setOfSetOfSameCanonicalLabeledNonOverlappingFragment : setOfSetOfSameCanonicalLabeledNonOverlappingFragments) {
                if (setOfSetOfSameCanonicalLabeledNonOverlappingFragment.size() > 1) {
                    groupedLayer.add(setOfSetOfSameCanonicalLabeledNonOverlappingFragment);
                }
            }
        }

        return groupedLayer;
    }

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

    private Collection<Set<Fragment>> doGroupingStep2(Set<Fragment> setOfSameCanonicalLabeledFragments) {
        return this.doCliqueCoverGroupping(setOfSameCanonicalLabeledFragments);
    }

    private Collection<Set<Fragment>> doCliqueCoverGroupping(Set<Fragment> setOfSameCanonicalLabeledFragments) {
        Pseudograph<Fragment, DefaultEdge> graph = this.getCliqueCoverGraph(setOfSameCanonicalLabeledFragments);
        BronKerboschCliqueFinder<Fragment, DefaultEdge> bronKerboschCliqueFinder = new BronKerboschCliqueFinder<>(graph);
        return bronKerboschCliqueFinder.getAllMaximalCliques();
    }

    private Pseudograph<Fragment, DefaultEdge> getCliqueCoverGraph(Set<Fragment> setOfSameCanonicalLabeledFragments) {
        Pseudograph<Fragment, DefaultEdge> graph = new Pseudograph<>(DefaultEdge.class);

        for(Fragment fragment : setOfSameCanonicalLabeledFragments) {
            graph.addVertex(fragment);
        }

        for (Fragment fragment : setOfSameCanonicalLabeledFragments) {

            for (Fragment f : setOfSameCanonicalLabeledFragments) {
                if (!fragment.isNodeOverlapping(f) && !graph.containsEdge(fragment, f) && !graph.containsEdge(f, fragment)) {
                    graph.addEdge(fragment, f);
                }
            }
        }

        return graph;
    }

    protected Set<Fragment> clones(Fragment fragment, Set<Fragment> cloneCandidates) {
        Set<Fragment> res = new HashSet<>();

        for (Fragment f : cloneCandidates) {
            if (f.isIsomorph(fragment) && !f.isNodeOverlapping(fragment)) {
                res.add(f);
            }
        }

        if (res.size() >= 1) {
            res.add(fragment);
        }

        return res;
    }

    protected Set<Fragment> clones1(Set<Fragment> cloneCandidates) {
        Set<Fragment> res = new HashSet<>();

        for (Fragment f : cloneCandidates) {
            Collection<Fragment> clones = this.clones(f, cloneCandidates);
            if (clones.size() > 1) {
                res.addAll(clones);
            }
        }

        return res;
    }

    protected Set<Fragment> getL1Fragment() {
        Set<Fragment> res = new HashSet<>();

        for (Rule r : this.ruleGraphMap.keySet()) {

            for (CapsuleEdge h : this.ruleGraphMap.get(r).edgeSet()) {
                Set<CapsuleEdge> c = new HashSet<>();
                c.add(h);
                Fragment fragment = new Fragment(c, r, this.ruleGraphMap.get(r));
                res.add(fragment);
            }
        }

        return res;
    }

    protected Set<Fragment> getL1FragmentWithoutAttributes() {
        Set<Fragment> res = new HashSet<>();

        for (Rule r : this.ruleGraphMap.keySet()) {

            for (Object o : this.ruleGraphMap.get(r).edgeSet()) {
                CapsuleEdge h = (CapsuleEdge) o;
                if (!h.isAttributeEdge()) {
                    Set<CapsuleEdge> c = new HashSet<>();
                    c.add(h);
                    Fragment fragment = new Fragment(c, r, this.ruleGraphMap.get(r));
                    res.add(fragment);
                }
            }
        }

        return res;
    }

    protected void printLattice(List<Set<Fragment>> lattice) {
    }

    protected Set<Set<Fragment>> eScanFilterLattice(List<Set<Set<Fragment>>> groupedLayerLattice) {
        if (groupedLayerLattice.isEmpty()) {
            return new HashSet<>();
        } else {
            Set<Set<Fragment>> upperLayersUncoveredCloneGroups = groupedLayerLattice.get(groupedLayerLattice.size() - 1);
            Set<Set<Fragment>> temp = new HashSet<>();

            for(int i = groupedLayerLattice.size() - 2; i >= 0; --i) {
                temp.addAll(this.getAllUncoveredCGFromLayer(groupedLayerLattice.get(i), upperLayersUncoveredCloneGroups));
                upperLayersUncoveredCloneGroups.addAll(temp);
                temp = new HashSet<>();
            }

            return upperLayersUncoveredCloneGroups;
        }
    }

    private Set<Set<Fragment>> getAllUncoveredCGFromLayer(Set<Set<Fragment>> layer, Set<Set<Fragment>> upperLayersFragments) {
        Set<Set<Fragment>> allUncoveredCGFromLayer = new HashSet<>();

        for (Set<Fragment> fragments : layer) {
            boolean isCovered = false;

            for (Set<Fragment> upperLayersFragment : upperLayersFragments) {
                if (this.isCovered(fragments, upperLayersFragment)) {
                    isCovered = true;
                    break;
                }
            }

            if (!isCovered) {
                allUncoveredCGFromLayer.add(fragments);
            }
        }

        return allUncoveredCGFromLayer;
    }

    private boolean isCovered(Set<Fragment> cloneGroupFromLayer, Set<Fragment> cloneGroupFromUpperLayers) {
        Iterator<Fragment> var3 = cloneGroupFromLayer.iterator();

        boolean fragmentIsCovered;
        do {
            if (!var3.hasNext()) {
                return true;
            }

            Fragment fragmentFromLayer = var3.next();
            fragmentIsCovered = false;

            for (Fragment fragmentFromUpperLayers : cloneGroupFromUpperLayers) {
                if (fragmentFromLayer.isSubFragment(fragmentFromUpperLayers)) {
                    fragmentIsCovered = true;
                    break;
                }
            }
        } while(fragmentIsCovered);

        return false;
    }
}
