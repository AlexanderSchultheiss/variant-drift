package de.hub.mse.variantdrift.clone.escan;

import de.hub.mse.variantdrift.clone.models.GenericEdge;
import de.hub.mse.variantdrift.clone.models.GenericNode;
import de.uni_marburg.fb12.swt.cloneDetection.atl.escan.CloneMatrix;
import org.eclipse.emf.ecore.resource.Resource;
import org.jgrapht.DirectedGraph;

import java.util.*;

public class MyEScanDetectionArticle extends MyEScanDetection {
    public MyEScanDetectionArticle(Collection<Resource> rules) {
        super(rules);
    }

    public MyEScanDetectionArticle(Map<Resource, DirectedGraph<GenericNode, GenericEdge>> ruleGraphMap, List<Resource> ruleList, boolean considerAttributeNodes) {
        super(ruleGraphMap, ruleList, considerAttributeNodes);
    }

    public void detectCloneGroups() {
        Set<Set<MyFragment>> cloneGroups = this.runEScan();
        this.resultAsCloneMatrix = convertEScanResult(cloneGroups);
    }

    public static Set<CloneMatrix> convertEScanResult(Set<Set<MyFragment>> setOfSetsOfSameCanonicalLabeledNonOverlappingFragments) {
        Set<CloneMatrix> res = new HashSet<>();

        for (Set<MyFragment> setOfSetsOfSameCanonicalLabeledNonOverlappingFragment : setOfSetsOfSameCanonicalLabeledNonOverlappingFragments) {
            if (setOfSetsOfSameCanonicalLabeledNonOverlappingFragment.size() > 1) {
                res.add(convert(setOfSetsOfSameCanonicalLabeledNonOverlappingFragment));
            }
        }

        return res;
    }

    private static CloneMatrix convert(Set<MyFragment> cloneGroup) {
        Set<Resource> rules = new HashSet<>();

        for (MyFragment myFragment : cloneGroup) {
            rules.add(myFragment.getResource());
        }

        List<List<GenericEdge>> edgeMatrix = new LinkedList<>();

        for (MyFragment myFragment : cloneGroup) {
            List<GenericEdge> originalEdges = new LinkedList<>();
            List<GenericEdge> capsuleEdges = myFragment.getGenericEdges();

            for (GenericEdge capsuleEdge : capsuleEdges) {
                originalEdges.add(capsuleEdge.getOriginalEdge().getActionEdge());
            }

            attributeMatrix.add(attributes);
            edgeMatrix.add(originalEdges);
        }

        CloneMatrix res = new CloneMatrix(edgeMatrix, attributeMatrix);
        return res;
    }

    private Set<Set<MyFragment>> runEScan() {
        List<Set<MyFragment>> lattice = new LinkedList<>();
        Set<MyFragment> all1Fragments = this.getL1Fragment();
        Set<MyFragment> layer1 = this.clones1(all1Fragments);
        Set<GenericEdge> edgesLayer1 = this.extractEdges(layer1);
        lattice.add(layer1);
        int startLayer = 0;

        for (Object o : lattice.get(startLayer)) {
            MyFragment f1 = (MyFragment) o;
            this.discover(f1, this.clones(f1, lattice.get(startLayer)), lattice, edgesLayer1, startLayer);
        }

        return this.eScanGroupAndFilterLattice(lattice);
    }

    private void discover(MyFragment f, Collection<MyFragment> fClones, List<Set<MyFragment>> lattice, Set<GenericEdge> edgesLayer1, int kFromArticle) {
        if (fClones.size() <= 1) {
            System.out.println("EScanDetectionArticleOriginal - discover - fClones ???????");
        }

        int k = kFromArticle - 1;
        Set<MyFragment> candidateSetCkp1 = new HashSet<>();

        for (MyFragment g : fClones) {
            Set<MyFragment> extensOp = g.extensOp(edgesLayer1);
            candidateSetCkp1.addAll(extensOp);
        }

        for (MyFragment ckp1 : candidateSetCkp1) {
            if (f.isGeneratingParent(ckp1)) {
                Set<MyFragment> findClones = this.clones(ckp1, candidateSetCkp1);
                if (findClones.size() > 1) {
                    if (lattice.size() <= k + 1) {
                        Set<MyFragment> newLayer = new HashSet<>();
                        lattice.add(newLayer);
                    }

                    (lattice.get(k + 1)).addAll(findClones);
                    int kFromArticleNext = kFromArticle + 1;
                    this.discover(ckp1, findClones, lattice, edgesLayer1, kFromArticleNext);
                }
            }
        }

    }
}
