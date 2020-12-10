package de.hub.mse.variantdrift.clone.escan;

import de.uni_marburg.fb12.swt.cloneDetection.cloneDetection.CloneMatrixCreator;
import de.uni_marburg.fb12.swt.cloneDetection.henshinToIntegrated.CapsuleEdge;
import de.uni_marburg.fb12.swt.cloneDetection.modelCd.Fragment;
import org.eclipse.emf.henshin.model.Node;
import org.eclipse.emf.henshin.model.Rule;
import org.jgrapht.DirectedGraph;

import java.util.*;

public class MyEScanDetectionArticle extends MyEScanDetection {
    public MyEScanDetectionArticle(Collection<Rule> rules) {
        super(rules);
    }

    public MyEScanDetectionArticle(Map<Rule, DirectedGraph<Node, CapsuleEdge>> ruleGraphMap, List<Rule> ruleList, boolean considerAttributeNodes) {
        super(ruleGraphMap, ruleList, considerAttributeNodes);
    }

    public void detectCloneGroups() {
        long startZeit = System.currentTimeMillis();
        Set<Set<Fragment>> cloneGroups = this.runEScan();
        this.resultAsCloneMatrix = CloneMatrixCreator.convertEScanResult(cloneGroups);
    }

    private Set<Set<Fragment>> runEScan() {
        List<Set<Fragment>> lattice = new LinkedList();
        Set<Fragment> all1Fragments = this.getL1Fragment();
        Set<Fragment> layer1 = this.clones1(all1Fragments);
        Set<CapsuleEdge> edgesLayer1 = this.extractEdges(layer1);
        lattice.add(layer1);
        int startLayer = 1;
        Iterator var6 = ((Set)lattice.get(startLayer - 1)).iterator();

        while(var6.hasNext()) {
            Fragment f1 = (Fragment)var6.next();
            this.discover(f1, this.clones(f1, (Set)lattice.get(startLayer - 1)), lattice, edgesLayer1, startLayer);
        }

        Set<Set<Fragment>> articleFilteredCG = this.eScanGroupAndFilterLattice(lattice);
        return articleFilteredCG;
    }

    private void discover(Fragment f, Collection<Fragment> fClones, List<Set<Fragment>> lattice, Set<CapsuleEdge> edgesLayer1, int kFromArticle) {
        if (fClones.size() <= 1) {
            System.out.println("EScanDetectionArticleOriginal - discover - fClones ???????");
        }

        int k = kFromArticle - 1;
        Set<Fragment> candidateSetCkp1 = new HashSet();
        Iterator var8 = fClones.iterator();

        while(var8.hasNext()) {
            Fragment g = (Fragment)var8.next();
            Set<Fragment> extensOp = g.extensOp(edgesLayer1);
            candidateSetCkp1.addAll(extensOp);
        }

        Iterator var13 = candidateSetCkp1.iterator();

        while(var13.hasNext()) {
            Fragment ckp1 = (Fragment)var13.next();
            if (f.isGeneratingParent(ckp1)) {
                Set<Fragment> findClones = this.clones(ckp1, candidateSetCkp1);
                if (findClones.size() > 1) {
                    if (lattice.size() <= k + 1) {
                        Set<Fragment> newLayer = new HashSet();
                        lattice.add(newLayer);
                    }

                    ((Set)lattice.get(k + 1)).addAll(findClones);
                    int kFromArticleNext = kFromArticle + 1;
                    this.discover(ckp1, findClones, lattice, edgesLayer1, kFromArticleNext);
                }
            }
        }

    }
}
