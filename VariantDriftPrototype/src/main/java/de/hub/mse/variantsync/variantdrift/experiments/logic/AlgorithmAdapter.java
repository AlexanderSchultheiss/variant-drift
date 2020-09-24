package de.hub.mse.variantsync.variantdrift.experiments.logic;

import de.hub.mse.variantsync.variantdrift.experiments.ExperimentRunner;
import de.hub.mse.variantsync.variantdrift.experiments.algorithms.BoundaryEstimator;
import de.hub.mse.variantsync.variantdrift.experiments.data.ExperimentSetup;
import de.hub.mse.variantsync.variantdrift.experiments.data.MatchStatistic;
import de.hub.mse.variantsync.variantdrift.experiments.algorithms.NaiveNameBasedMatcher;
import de.hub.mse.variantsync.variantdrift.experiments.algorithms.nwm.alg.merge.ChainingOptimizingMerger;
import de.hub.mse.variantsync.variantdrift.experiments.algorithms.nwm.alg.merge.MultiModelMerger;
import de.hub.mse.variantsync.variantdrift.experiments.algorithms.nwm.common.AlgoUtil;
import de.hub.mse.variantsync.variantdrift.experiments.algorithms.nwm.domain.Element;
import de.hub.mse.variantsync.variantdrift.experiments.algorithms.nwm.domain.Tuple;
import de.hub.mse.variantsync.variantdrift.experiments.algorithms.HungarianPairwiseMatcher;
import de.hub.mse.variantsync.variantdrift.experiments.algorithms.nwm.domain.Model;
import de.hub.mse.variantsync.variantdrift.experiments.algorithms.nwm.execution.RunResult;
import de.hub.mse.variantsync.variantdrift.experiments.data.ElementTuple;
import de.hub.mse.variantsync.variantdrift.experiments.data.RElement;

import java.util.*;

public class AlgorithmAdapter implements MethodAdapter {
    private final ExperimentSetup setup;
    private final EMatcherType algorithmToApply;

    public AlgorithmAdapter(ExperimentSetup setup, EMatcherType algorithm) {
        this.setup = setup;
        this.algorithmToApply = algorithm;
    }

    public void run() {
        for (int runID = 0; runID < setup.numberOfRepeats; runID++) {
            // Here, we use the Model class of Rubin an Chechik
            ArrayList<Model> models = Model.readModelsFile(setup.datasetFile);
            List<ArrayList<Model>> chunks = ExperimentHelper.getDatasetChunks(models, setup.chunkSize);
            // Load all refactoring metadata
            String refactoringMetaData = chunks.get(0).get(0).getRefactoringInfo();

            for (ArrayList<Model> chunk : chunks) {
                int sizeOfLargestModel = getSizeOfLargestModel(chunk);
                int numberOfModels = chunk.size();
                MatchStatistic matchStatistic = new MatchStatistic(0, setup.datasetName, setup.name,
                        numberOfModels, sizeOfLargestModel, refactoringMetaData);

                AlgoUtil.COMPUTE_RESULTS_CLASSICALLY = false;

                List<Tuple> solution;
                RunResult runResult;
                ArrayList<Model> modelSubList = new ArrayList<>(chunk);

                // To the best of our knowledge, this is the prototype implementation of nwm used in the work of
                // Rubin and Chechik https://doi.org/10.1145/2491411.2491446
                // it achieves the matching weights that were presented in their publication
                switch (algorithmToApply) {
                    case NwM -> {
                        MultiModelMerger mmm = new ChainingOptimizingMerger(modelSubList);
                        mmm.run();
                        solution = mmm.getTuplesInMatch();
                        runResult = mmm.getRunResult(numberOfModels);
                    }
                    case PairwiseDesc -> {
                        HungarianPairwiseMatcher matcher = new HungarianPairwiseMatcher(modelSubList, algorithmToApply);
                        matcher.run();
                        solution = matcher.getResult();
                        runResult = matcher.getRunResult();
                    }
                    case NameBased -> {
                        NaiveNameBasedMatcher nameMatcher = new NaiveNameBasedMatcher(modelSubList);
                        nameMatcher.run();
                        solution = nameMatcher.getResult();
                        runResult = nameMatcher.getRunResult();
                    }
                    case Boundary -> {
                        BoundaryEstimator boundaryEstimator = new BoundaryEstimator(modelSubList);
                        boundaryEstimator.run();
                        solution = boundaryEstimator.getResult();
                        runResult = boundaryEstimator.getRunResult();
                    }
                    default -> throw new UnsupportedOperationException("This type has not been implemented yet!");
                }

                // We parse the matching returned by NwM to our own data format, in order to do the evaluation
                Set<ElementTuple> mergedModel = parseSolution(solution);

                if (ExperimentRunner.PRINT_MATCH) {
                    int numberOfClasses = countClasses(mergedModel);
                    System.out.println("Number of Classes: " + numberOfClasses);
                    for (ElementTuple tuple : mergedModel) {
                        System.out.println(tuple.getLongString());
                    }
                }
                // Save the results
                matchStatistic.calculateStatistics(mergedModel, getTimeInSeconds(runResult.execTime));
                System.out.println(matchStatistic);
                System.out.println();

                matchStatistic.writeAsJSON(setup.resultFile, true);
            }
        }
    }

    private int getSizeOfLargestModel(List<Model> models) {
        int size = 0;
        for (Model model : models) {
            if (model.getElements().size() > size) {
                size = model.getElements().size();
            }
        }
        return size;
    }

    private double getTimeInSeconds(double time) {
        return time / 1000;
    }

    private Set<ElementTuple> parseSolution(List<Tuple> solution) {
        Set<ElementTuple> parsedSet = new HashSet<>();

        // Create ElementTuple for all tuple in the solution
        for (Tuple tuple : solution) {
            List<RElement> nodes = new ArrayList<>();
            for (Element e : tuple.getRealElements()) {
                nodes.add(parseElement(e));
            }
            parsedSet.add(new ElementTuple(nodes.toArray(new RElement[0])));
        }

        return parsedSet;
    }

    private RElement parseElement(Element element) {
        return new RElement(element.getModelId(), element.getLabel(), element.getProperties());
    }

    private int countClasses(Set<ElementTuple> set) {
        ArrayList<RElement> classes = new ArrayList<>();
        for (ElementTuple tuple : set) {
            classes.addAll(tuple.getElements());
        }
        return classes.size();
    }

}
