package de.hub.mse.variantsync.variantdrift.experiments;

import de.hub.mse.variantsync.variantdrift.experiments.data.ExperimentSetup;
import de.hub.mse.variantsync.variantdrift.experiments.logic.MethodAdapter;
import de.hub.mse.variantsync.variantdrift.experiments.logic.EMatcherType;
import de.hub.mse.variantsync.variantdrift.experiments.logic.AlgorithmAdapter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;

public class ExperimentRunner {
    // Only change this if you want to load models from a different directory
    private static final String baseDatasetDir = "./../data/refactored_subjects";
    // This directory is created in the working directory in order to store all experimental results there
    private static final String baseResultsDir = "./../data/experimental_results";

    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // Configuration of experiments starts from here
    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

    // Flags that determine which algorithms should be run
    private static final boolean shouldRunNwM = true;
    private static final boolean shouldRunPairwise = true;
    private static final boolean shouldRunNaiveNameBased = true;
    private static final boolean shouldRunBoundaryEstimation = true;

    // Extra-Verbose mode, prints the tuples of each matching
    public static boolean PRINT_MATCH = false;
    // Number of times each setup is executed, note that there are already 30 variants per specific number of
    // refactorings of a experimental subject.
    private static final int numberOfRepeats = 1;

    // List of the "smaller" datasets
    // You can comment out lines of the datasets which you do not want to run (beware of commas)
    private static final List<String> enabledDatasets = Arrays.asList(
            "ppu"
            ,"bcms"
            ,"Apogames"
    );

    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    // No configuration to be done below this line
    // +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
    public static void main(String... args) {
        String runIDRestriction = null;
        if (args.length == 1) {
            runIDRestriction = "_" + args[0];
        } else if (args.length > 1) {
            throw new RuntimeException("Too many arguments");
        }

        File folder = new File(baseDatasetDir);
        File[] listOfSetups = folder.listFiles();
        List<String> subsets = new ArrayList<>();
        if (listOfSetups != null) {
            for (File setup : listOfSetups) {
                File[] listOfFiles = setup.listFiles();
                if (listOfFiles != null) {
                    for (File subsetFile : listOfFiles) {
                        String name = subsetFile.getName().replace(".csv", "");
                        subsets.add(setup.getName() + "/" + name);
                    }
                }
            }
        }

        List<String> dataset_files = new ArrayList<>(subsets);

        // Filter out all datasets that are disabled
        List<String> tempDatasets = new ArrayList<>();
        for (String dataset : dataset_files) {
            for (String name : enabledDatasets) {
                if (dataset.contains(name)) {
                    if (runIDRestriction == null) {
                        tempDatasets.add(dataset);
                    } else {
                        // In case there is a restriction to the run id for a specific number of refactorings, we only
                        // add this file if it matches the restriction. This can be used for running specific files
                        if (dataset.endsWith(runIDRestriction)) {
                            tempDatasets.add(dataset);
                        }
                    }
                }
            }
        }
        dataset_files = tempDatasets;

        for (String pathToDataset : dataset_files) {
            System.out.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++" +
                    "+++++++++++++++++++++++++++++++++++");
            String resultsDir = baseResultsDir;
            String datasetDir = baseDatasetDir;

            int chunkSize = Integer.MAX_VALUE;

            ExperimentSetup nwmSetup = new ExperimentSetup("NwM", numberOfRepeats,
                    resultsDir, datasetDir, pathToDataset, chunkSize);

            ExperimentSetup pairwiseSetup = new ExperimentSetup("PairwiseDesc", numberOfRepeats,
                    resultsDir, datasetDir, pathToDataset, chunkSize);

            ExperimentSetup naiveNameBasedSetup = new ExperimentSetup("NameBased", numberOfRepeats,
                    resultsDir, datasetDir, pathToDataset, chunkSize);

            ExperimentSetup boundaryEstimationSetup = new ExperimentSetup("BoundaryEstimation", numberOfRepeats,
                    resultsDir, datasetDir, pathToDataset, chunkSize);


            // Rubin NwM
            if (shouldRunNwM) {
                runExperiment(new AlgorithmAdapter(nwmSetup, EMatcherType.NwM),
                        nwmSetup.name,
                        pathToDataset);
            }

            if (shouldRunPairwise) {
                runExperiment(new AlgorithmAdapter(pairwiseSetup, EMatcherType.PairwiseDesc),
                        pairwiseSetup.name,
                        pathToDataset);
            }

            // Run Naive Name Based
            if (shouldRunNaiveNameBased) {
                runExperiment(new AlgorithmAdapter(naiveNameBasedSetup, EMatcherType.NameBased),
                        naiveNameBasedSetup.name,
                        pathToDataset);
            }
            // Run boundary estimation
            if (shouldRunBoundaryEstimation) {
                runExperiment(new AlgorithmAdapter(boundaryEstimationSetup, EMatcherType.Boundary),
                        boundaryEstimationSetup.name,
                        pathToDataset);
            }

        }
    }

    private static void runExperiment(MethodAdapter adapter, String name, String dataset) {
        try {
            System.out.println("Running " + name + " on " + dataset + "...");
            adapter.run();
        } catch (Error | Exception error) {
            LocalDateTime localDateTime = LocalDateTime.now();
            String errorText = "+++++++++++++++++++++++\n"
                    + localDateTime.toString()
                    + ": ERROR for " + name + " on " + dataset + "\n"
                    + error
                    + "\n+++++++++++++++++++++++\n";

            File errorLogFile = Paths.get(baseResultsDir, "ERRORLOG.txt").toFile();
            try (FileWriter fw = new FileWriter(errorLogFile, true)) {
                fw.write(errorText);
                fw.write("\n");
            } catch (IOException e) {
                System.err.println("WARNING: Not possible to write to ERRORLOG!\n" + e);
            }

            System.err.println("ERROR for " + name + " on " + dataset + "\n" + error);
            error.printStackTrace();
        }
        System.out.println("----------------------------------------------------------------------------------------------------------------");
        System.out.println("----------------------------------------------------------------------------------------------------------------");
    }

}
