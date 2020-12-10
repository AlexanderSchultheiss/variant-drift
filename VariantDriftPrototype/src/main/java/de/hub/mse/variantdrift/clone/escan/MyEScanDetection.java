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
        Set<Set<Fragment>> articleFilteredCG = this.eScanFilterLattice(groupedLayerLattice);
        return articleFilteredCG;
    }

    protected Set<CapsuleEdge> extractEdges(Set<Fragment> layer1) {
        Set<CapsuleEdge> res = new HashSet();
        Iterator var3 = layer1.iterator();

        while(var3.hasNext()) {
            Fragment fragment = (Fragment)var3.next();
            res.addAll(fragment.getCapsuleEdges());
        }

        return res;
    }

    private List<Collection<Set<Fragment>>> groupFragmentsInToCloneGroupsStep1(List<Set<Fragment>> lattice) {
        List<Collection<Set<Fragment>>> groupedLayerLattice = new LinkedList();
        Iterator var3 = lattice.iterator();

        while(var3.hasNext()) {
            Set<Fragment> layer = (Set)var3.next();
            groupedLayerLattice.add(this.groupFragmentsInToCloneGroupsStep1(layer));
        }

        return groupedLayerLattice;
    }

    private Collection<Set<Fragment>> groupFragmentsInToCloneGroupsStep1(Set<Fragment> layer) {
        Collection<Set<Fragment>> collectionOfSetsOfSameCanonicalLabeledFragments = this.doGroupingStep1(layer);
        return collectionOfSetsOfSameCanonicalLabeledFragments;
    }

    private List<Set<Set<Fragment>>> groupFragmentsInToCloneGroupsStep2(List<Collection<Set<Fragment>>> groupedLayerLatticeStep1) {
        List<Set<Set<Fragment>>> groupedLayerLattice = new LinkedList();
        Iterator var3 = groupedLayerLatticeStep1.iterator();

        while(var3.hasNext()) {
            Collection<Set<Fragment>> layer = (Collection)var3.next();
            Set<Set<Fragment>> groupedLayer = this.groupFragmentsInToCloneGroupsStep2(layer);
            if (groupedLayer.size() >= 1) {
                groupedLayerLattice.add(groupedLayer);
            }
        }

        return groupedLayerLattice;
    }

    private Set<Set<Fragment>> groupFragmentsInToCloneGroupsStep2(Collection<Set<Fragment>> layer) {
        Set<Set<Fragment>> groupedLayer = new HashSet();
        Iterator var3 = layer.iterator();

        while(var3.hasNext()) {
            Set<Fragment> setOfSameCanonicalLabeledFragments = (Set)var3.next();
            Collection<Set<Fragment>> setOfSetOfSameCanonicalLabeledNonOverlappingFragments = this.doGroupingStep2(setOfSameCanonicalLabeledFragments);
            Iterator var6 = setOfSetOfSameCanonicalLabeledNonOverlappingFragments.iterator();

            while(var6.hasNext()) {
                Set<Fragment> setOfSameCanonicalLabeledNonOverlappingFragments = (Set)var6.next();
                if (setOfSameCanonicalLabeledNonOverlappingFragments.size() > 1) {
                    groupedLayer.add(setOfSameCanonicalLabeledNonOverlappingFragments);
                }
            }
        }

        return groupedLayer;
    }

    private Collection<Set<Fragment>> doGroupingStep1(Set<Fragment> layer) {
        Map<String, Set<Fragment>> labelsAndFragments = new HashMap();
        Iterator var3 = layer.iterator();

        while(var3.hasNext()) {
            Fragment f = (Fragment)var3.next();
            String label = f.getLabel();
            if (labelsAndFragments.keySet().contains(label)) {
                ((Set)labelsAndFragments.get(label)).add(f);
            } else {
                Set<Fragment> fragments = new HashSet();
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
        BronKerboschCliqueFinder<Fragment, DefaultEdge> bronKerboschCliqueFinder = new BronKerboschCliqueFinder(graph);
        Collection<Set<Fragment>> res = bronKerboschCliqueFinder.getAllMaximalCliques();
        return res;
    }

    private Pseudograph<Fragment, DefaultEdge> getCliqueCoverGraph(Set<Fragment> setOfSameCanonicalLabeledFragments) {
        Pseudograph<Fragment, DefaultEdge> graph = new Pseudograph(DefaultEdge.class);
        Iterator var3 = setOfSameCanonicalLabeledFragments.iterator();

        Fragment fragment;
        while(var3.hasNext()) {
            fragment = (Fragment)var3.next();
            graph.addVertex(fragment);
        }

        var3 = setOfSameCanonicalLabeledFragments.iterator();

        while(var3.hasNext()) {
            fragment = (Fragment)var3.next();
            Iterator var5 = setOfSameCanonicalLabeledFragments.iterator();

            while(var5.hasNext()) {
                Fragment f = (Fragment)var5.next();
                if (!fragment.isNodeOverlapping(f) && !graph.containsEdge(fragment, f) && !graph.containsEdge(f, fragment)) {
                    graph.addEdge(fragment, f);
                }
            }
        }

        return graph;
    }

    protected Set<Fragment> clones(Fragment fragment, Set<Fragment> cloneCandidates) {
        Set<Fragment> res = new HashSet();
        Iterator var4 = cloneCandidates.iterator();

        while(var4.hasNext()) {
            Fragment f = (Fragment)var4.next();
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
        Set<Fragment> res = new HashSet();
        Iterator var3 = cloneCandidates.iterator();

        while(var3.hasNext()) {
            Fragment f = (Fragment)var3.next();
            Collection<Fragment> clones = this.clones(f, cloneCandidates);
            if (clones.size() > 1) {
                res.addAll(clones);
            }
        }

        return res;
    }

    protected Set<Fragment> getL1Fragment() {
        Set<Fragment> res = new HashSet();
        Iterator var2 = this.ruleGraphMap.keySet().iterator();

        while(var2.hasNext()) {
            Rule r = (Rule)var2.next();
            Iterator var4 = ((DirectedGraph)this.ruleGraphMap.get(r)).edgeSet().iterator();

            while(var4.hasNext()) {
                CapsuleEdge h = (CapsuleEdge)var4.next();
                Set<CapsuleEdge> c = new HashSet();
                c.add(h);
                Fragment fragment = new Fragment(c, r, (DirectedGraph)this.ruleGraphMap.get(r));
                res.add(fragment);
            }
        }

        return res;
    }

    protected Set<Fragment> getL1FragmentWithoutAttributes() {
        Set<Fragment> res = new HashSet();
        Iterator var2 = this.ruleGraphMap.keySet().iterator();

        while(var2.hasNext()) {
            Rule r = (Rule)var2.next();
            Iterator var4 = ((DirectedGraph)this.ruleGraphMap.get(r)).edgeSet().iterator();

            while(var4.hasNext()) {
                CapsuleEdge h = (CapsuleEdge)var4.next();
                if (!h.isAttributeEdge()) {
                    Set<CapsuleEdge> c = new HashSet();
                    c.add(h);
                    Fragment fragment = new Fragment(c, r, (DirectedGraph)this.ruleGraphMap.get(r));
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
            return new HashSet();
        } else {
            Set<Set<Fragment>> upperLayersUncoveredCloneGroups = (Set)groupedLayerLattice.get(groupedLayerLattice.size() - 1);
            Set<Set<Fragment>> temp = new HashSet();

            for(int i = groupedLayerLattice.size() - 2; i >= 0; --i) {
                temp.addAll(this.getAllUncoveredCGFromLayer((Set)groupedLayerLattice.get(i), upperLayersUncoveredCloneGroups));
                upperLayersUncoveredCloneGroups.addAll(temp);
                temp = new HashSet();
            }

            return upperLayersUncoveredCloneGroups;
        }
    }

    private Set<Set<Fragment>> getAllUncoveredCGFromLayer(Set<Set<Fragment>> layer, Set<Set<Fragment>> upperLayersFragments) {
        Set<Set<Fragment>> allUncoveredCGFromLayer = new HashSet();
        Iterator var4 = layer.iterator();

        while(var4.hasNext()) {
            Set<Fragment> cloneGroupFromLayer = (Set)var4.next();
            boolean isCovered = false;
            Iterator var7 = upperLayersFragments.iterator();

            while(var7.hasNext()) {
                Set<Fragment> cloneGroupFromUpperLayers = (Set)var7.next();
                if (this.isCovered(cloneGroupFromLayer, cloneGroupFromUpperLayers)) {
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
        Iterator var3 = cloneGroupFromLayer.iterator();

        boolean fragmentIsCovered;
        do {
            if (!var3.hasNext()) {
                return true;
            }

            Fragment fragmentFromLayer = (Fragment)var3.next();
            fragmentIsCovered = false;
            Iterator var6 = cloneGroupFromUpperLayers.iterator();

            while(var6.hasNext()) {
                Fragment fragmentFromUpperLayers = (Fragment)var6.next();
                if (fragmentFromLayer.isSubFragment(fragmentFromUpperLayers)) {
                    fragmentIsCovered = true;
                    break;
                }
            }
        } while(fragmentIsCovered);

        return false;
    }
}
