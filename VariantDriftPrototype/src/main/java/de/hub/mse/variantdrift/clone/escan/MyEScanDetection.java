package de.hub.mse.variantdrift.clone.escan;

import de.hub.mse.variantdrift.clone.models.GenericEdge;
import de.hub.mse.variantdrift.clone.models.GenericNode;
import org.eclipse.emf.ecore.resource.Resource;
import org.jgrapht.DirectedGraph;
import org.jgrapht.alg.BronKerboschCliqueFinder;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.Pseudograph;

import java.util.*;

public abstract class MyEScanDetection extends MyCloneDetection {
    public MyEScanDetection(Collection<Resource> rules) {
        super(rules);
    }

    public MyEScanDetection(Map<Resource, DirectedGraph<GenericNode, GenericEdge>> ruleGraphMap, List<Resource> ruleList, boolean considerAttributeGenericNodes) {
        super(ruleGraphMap, ruleList, considerAttributeGenericNodes);
    }

    public Set<Set<MyFragment>> eScanGroupAndFilterLattice(List<Set<MyFragment>> lattice) {
        List<Collection<Set<MyFragment>>> groupedLayerLatticeStep1 = this.groupMyFragmentsInToCloneGroupsStep1(lattice);
        List<Set<Set<MyFragment>>> groupedLayerLattice = this.groupMyFragmentsInToCloneGroupsStep2(groupedLayerLatticeStep1);
        return this.eScanFilterLattice(groupedLayerLattice);
    }

    protected Set<GenericEdge> extractEdges(Set<MyFragment> layer1) {
        Set<GenericEdge> res = new HashSet<>();

        for (MyFragment fragment : layer1) {
            res.addAll(fragment.getGenericEdges());
        }

        return res;
    }

    private List<Collection<Set<MyFragment>>> groupMyFragmentsInToCloneGroupsStep1(List<Set<MyFragment>> lattice) {
        List<Collection<Set<MyFragment>>> groupedLayerLattice = new LinkedList<>();

        for (Set<MyFragment> fragments : lattice) {
            groupedLayerLattice.add(this.groupMyFragmentsInToCloneGroupsStep1(fragments));
        }

        return groupedLayerLattice;
    }

    private Collection<Set<MyFragment>> groupMyFragmentsInToCloneGroupsStep1(Set<MyFragment> layer) {
        return this.doGroupingStep1(layer);
    }

    private List<Set<Set<MyFragment>>> groupMyFragmentsInToCloneGroupsStep2(List<Collection<Set<MyFragment>>> groupedLayerLatticeStep1) {
        List<Set<Set<MyFragment>>> groupedLayerLattice = new LinkedList<>();

        for (Collection<Set<MyFragment>> sets : groupedLayerLatticeStep1) {
            Set<Set<MyFragment>> groupedLayer = this.groupMyFragmentsInToCloneGroupsStep2(sets);
            if (groupedLayer.size() >= 1) {
                groupedLayerLattice.add(groupedLayer);
            }
        }

        return groupedLayerLattice;
    }

    private Set<Set<MyFragment>> groupMyFragmentsInToCloneGroupsStep2(Collection<Set<MyFragment>> layer) {
        Set<Set<MyFragment>> groupedLayer = new HashSet<>();

        for (Set<MyFragment> fragments : layer) {
            Collection<Set<MyFragment>> setOfSetOfSameCanonicalLabeledNonOverlappingMyFragments = this.doGroupingStep2(fragments);

            for (Set<MyFragment> setOfSetOfSameCanonicalLabeledNonOverlappingMyFragment : setOfSetOfSameCanonicalLabeledNonOverlappingMyFragments) {
                if (setOfSetOfSameCanonicalLabeledNonOverlappingMyFragment.size() > 1) {
                    groupedLayer.add(setOfSetOfSameCanonicalLabeledNonOverlappingMyFragment);
                }
            }
        }

        return groupedLayer;
    }

    private Collection<Set<MyFragment>> doGroupingStep1(Set<MyFragment> layer) {
        Map<String, Set<MyFragment>> labelsAndMyFragments = new HashMap<>();

        for (MyFragment f : layer) {
            String label = f.getLabel();
            if (labelsAndMyFragments.containsKey(label)) {
                labelsAndMyFragments.get(label).add(f);
            } else {
                Set<MyFragment> fragments = new HashSet<>();
                fragments.add(f);
                labelsAndMyFragments.put(label, fragments);
            }
        }

        return labelsAndMyFragments.values();
    }

    private Collection<Set<MyFragment>> doGroupingStep2(Set<MyFragment> setOfSameCanonicalLabeledMyFragments) {
        return this.doCliqueCoverGroupping(setOfSameCanonicalLabeledMyFragments);
    }

    private Collection<Set<MyFragment>> doCliqueCoverGroupping(Set<MyFragment> setOfSameCanonicalLabeledMyFragments) {
        Pseudograph<MyFragment, DefaultEdge> graph = this.getCliqueCoverGraph(setOfSameCanonicalLabeledMyFragments);
        BronKerboschCliqueFinder<MyFragment, DefaultEdge> bronKerboschCliqueFinder = new BronKerboschCliqueFinder<>(graph);
        return bronKerboschCliqueFinder.getAllMaximalCliques();
    }

    private Pseudograph<MyFragment, DefaultEdge> getCliqueCoverGraph(Set<MyFragment> setOfSameCanonicalLabeledMyFragments) {
        Pseudograph<MyFragment, DefaultEdge> graph = new Pseudograph<>(DefaultEdge.class);

        for(MyFragment fragment : setOfSameCanonicalLabeledMyFragments) {
            graph.addVertex(fragment);
        }

        for (MyFragment fragment : setOfSameCanonicalLabeledMyFragments) {

            for (MyFragment f : setOfSameCanonicalLabeledMyFragments) {
                if (!fragment.isGenericNodeOverlapping(f) && !graph.containsEdge(fragment, f) && !graph.containsEdge(f, fragment)) {
                    graph.addEdge(fragment, f);
                }
            }
        }

        return graph;
    }

    protected Set<MyFragment> clones(MyFragment fragment, Set<MyFragment> cloneCandidates) {
        Set<MyFragment> res = new HashSet<>();

        for (MyFragment f : cloneCandidates) {
            if (f.isIsomorph(fragment) && !f.isGenericNodeOverlapping(fragment)) {
                res.add(f);
            }
        }

        if (res.size() >= 1) {
            res.add(fragment);
        }

        return res;
    }

    protected Set<MyFragment> clones1(Set<MyFragment> cloneCandidates) {
        Set<MyFragment> res = new HashSet<>();

        for (MyFragment f : cloneCandidates) {
            Collection<MyFragment> clones = this.clones(f, cloneCandidates);
            if (clones.size() > 1) {
                res.addAll(clones);
            }
        }

        return res;
    }

    protected Set<MyFragment> getL1MyFragment() {
        Set<MyFragment> res = new HashSet<>();

        for (Resource r : this.ruleGraphMap.keySet()) {

            for (GenericEdge h : this.ruleGraphMap.get(r).edgeSet()) {
                Set<GenericEdge> c = new HashSet<>();
                c.add(h);
                MyFragment fragment = new MyFragment(c, r, this.ruleGraphMap.get(r));
                res.add(fragment);
            }
        }

        return res;
    }

//    protected Set<MyFragment> getL1MyFragmentWithoutAttributes() {
//        Set<MyFragment> res = new HashSet<>();
//
//        for (Resource r : this.ruleGraphMap.keySet()) {
//
//            for (Object o : this.ruleGraphMap.get(r).edgeSet()) {
//                GenericEdge h = (GenericEdge) o;
//                if (!h.isAttributeEdge()) {
//                    Set<GenericEdge> c = new HashSet<>();
//                    c.add(h);
//                    MyFragment fragment = new MyFragment(c, r, this.ruleGraphMap.get(r));
//                    res.add(fragment);
//                }
//            }
//        }
//
//        return res;
//    }

    protected void printLattice(List<Set<MyFragment>> lattice) {
    }

    protected Set<Set<MyFragment>> eScanFilterLattice(List<Set<Set<MyFragment>>> groupedLayerLattice) {
        if (groupedLayerLattice.isEmpty()) {
            return new HashSet<>();
        } else {
            Set<Set<MyFragment>> upperLayersUncoveredCloneGroups = groupedLayerLattice.get(groupedLayerLattice.size() - 1);
            Set<Set<MyFragment>> temp = new HashSet<>();

            for(int i = groupedLayerLattice.size() - 2; i >= 0; --i) {
                temp.addAll(this.getAllUncoveredCGFromLayer(groupedLayerLattice.get(i), upperLayersUncoveredCloneGroups));
                upperLayersUncoveredCloneGroups.addAll(temp);
                temp = new HashSet<>();
            }

            return upperLayersUncoveredCloneGroups;
        }
    }

    private Set<Set<MyFragment>> getAllUncoveredCGFromLayer(Set<Set<MyFragment>> layer, Set<Set<MyFragment>> upperLayersMyFragments) {
        Set<Set<MyFragment>> allUncoveredCGFromLayer = new HashSet<>();

        for (Set<MyFragment> fragments : layer) {
            boolean isCovered = false;

            for (Set<MyFragment> upperLayersMyFragment : upperLayersMyFragments) {
                if (this.isCovered(fragments, upperLayersMyFragment)) {
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

    private boolean isCovered(Set<MyFragment> cloneGroupFromLayer, Set<MyFragment> cloneGroupFromUpperLayers) {
        Iterator<MyFragment> var3 = cloneGroupFromLayer.iterator();

        boolean fragmentIsCovered;
        do {
            if (!var3.hasNext()) {
                return true;
            }

            MyFragment fragmentFromLayer = var3.next();
            fragmentIsCovered = false;

            for (MyFragment fragmentFromUpperLayers : cloneGroupFromUpperLayers) {
                if (fragmentFromLayer.isSubFragment(fragmentFromUpperLayers)) {
                    fragmentIsCovered = true;
                    break;
                }
            }
        } while(fragmentIsCovered);

        return false;
    }
}
