package de.hub.mse.variantsync.variantdrift.experiments.algorithms;

import de.hub.mse.variantsync.variantdrift.experiments.logic.EMatcherType;
import de.hub.mse.variantsync.variantdrift.experiments.algorithms.nwm.alg.AlgoBase;
import de.hub.mse.variantsync.variantdrift.experiments.algorithms.nwm.alg.merge.HungarianMerger;
import de.hub.mse.variantsync.variantdrift.experiments.algorithms.nwm.alg.pair.PairWiseMatch;
import de.hub.mse.variantsync.variantdrift.experiments.algorithms.nwm.common.AlgoUtil;
import de.hub.mse.variantsync.variantdrift.experiments.algorithms.nwm.domain.Tuple;
import de.hub.mse.variantsync.variantdrift.experiments.algorithms.nwm.domain.Model;

import java.util.*;

/**
 * Iterative pairwise matcher using the Hungarian algorithm for matching of two models in each iteration.
 */
public class HungarianPairwiseMatcher extends AlgoBase {
    private final ArrayList<Model> models;
    private final EMatcherType sortMode;
    private int numberOfComparisons;

    public HungarianPairwiseMatcher(ArrayList<Model> models, EMatcherType sortMode) {
        super("Hungarian Pairwise Fast");
        this.models = models;
        this.sortMode = sortMode;
    }

    @Override
    protected ArrayList<Tuple> doRun() {
        // Sort models by size ascending or descending
        if (sortMode == EMatcherType.PairwiseAsc) {
            models.sort(Comparator.comparingInt(Model::size));
        } else if (sortMode == EMatcherType.PairwiseDesc) {
            models.sort((m1, m2) -> Integer.compare(m2.size(), m1.size()));
        } else {
            throw new UnsupportedOperationException("This sort mode has not been implemented yet!");
        }

        // Iterate over the sorted list of models and match them iteratively
        Model mergedModel = models.get(0);
        HungarianMerger merger = null;
        numberOfComparisons = 0;
        for (int i = 1; i < models.size(); i++) {
            numberOfComparisons += (mergedModel.size() * models.get(i).size());
            merger = new HungarianMerger(mergedModel, models.get(i), 2);
            merger.runPairing();
            mergedModel = merger.mergeMatchedModels();
        }

        boolean storedVal = AlgoUtil.COMPUTE_RESULTS_CLASSICALLY;

        ArrayList<Tuple> realMerge = Objects.requireNonNull(merger).extractMerge();
        AlgoUtil.COMPUTE_RESULTS_CLASSICALLY = false;
        if (storedVal) {
            for (Tuple t : realMerge) {
                t.recomputeSelf(this.models);
            }
        }
        ArrayList<Tuple> retVal = PairWiseMatch.filterTuplesByTreshold(realMerge, models);

        AlgoUtil.COMPUTE_RESULTS_CLASSICALLY = storedVal;
        return retVal;
    }

    public int getNumberOfComparisons() {
        return this.numberOfComparisons;
    }

    @Override
    public ArrayList<Model> getModels() {
        return null;
    }
}
