package de.hub.mse.variantdrift.experiments.data;

import java.nio.file.Paths;

public class ExperimentSetup {
    public final String name;
    public final int numberOfRepeats;
    public final String datasetName;
    public final String resultFile;
    public final String mergeResultFile;
    public final String datasetFile;
    public final int chunkSize;

    public ExperimentSetup(String name, int numberOfRepeats,
                           String resultDir, String datasetDir,
                           String pathToDataset, int chunkSize) {
        this.name = name;
        this.numberOfRepeats = numberOfRepeats;
        var path_parts = pathToDataset.split("/");
        String setupName = path_parts[0];
        this.datasetName = path_parts[1];

        // Adjust name for ArgoUML subsets
        String[] parts = datasetName.split("_");
        String tempName = parts[0] + "_" + parts[1] + "_" + parts[2];
        this.resultFile = Paths.get(resultDir, setupName, name, name + "_" + tempName + "_stats.json").toString();
        this.mergeResultFile = Paths.get(resultDir, name, name + "_" + datasetName + "_model.csv").toString();
        this.datasetFile = Paths.get(datasetDir, setupName, datasetName + ".csv").toString();
        this.chunkSize = chunkSize;
    }

}
