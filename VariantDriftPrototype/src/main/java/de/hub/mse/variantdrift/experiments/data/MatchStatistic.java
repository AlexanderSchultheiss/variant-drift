package de.hub.mse.variantdrift.experiments.data;

import com.google.gson.Gson;
import de.hub.mse.variantdrift.experiments.logic.NwmWeight;
import de.hub.mse.variantdrift.refactoring.ERefactoringOperation;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class MatchStatistic {
    // Basic information
    private final int runID;
    private final String method;
    private final String dataset;

    // Result information
    private double runtime;
    private double weight;
    private final int numberOfModels;
    private final int sizeOfLargestModel;
    private final int numberOfRefactorings;
    private final boolean useKnownRefactoringDistribution;
    private final String policy;
    private int numberOfElements;
    private int numberOfTuples;
    private int numberOfMatches;
    private int numberOfRenameProperty;
    private int numberOfRenameElement;
    private int numberOfMoveProperty;
    private int numberOfExtractInterfaceCopy;
    private int numberOfExtractInterfaceMove;


    public MatchStatistic(int runID, String dataset, String method, int numberOfModels, int sizeOfLargestModel,
                          String refactoringMetaData) {
        this.runID = runID;
        this.dataset = dataset;
        this.method = method;
        this.numberOfModels = numberOfModels;
        this.sizeOfLargestModel = sizeOfLargestModel;

        final String RENAME_PROPERTY = ERefactoringOperation.RENAME_PROPERTY.name();
        final String RENAME_ELEMENT = ERefactoringOperation.RENAME_ELEMENT.name();
        final String MOVE_PROPERTY = ERefactoringOperation.MOVE_PROPERTY.name();
        final String EXTRACT_INTERFACE_WITH_COPY = ERefactoringOperation.EXTRACT_INTERFACE_WITH_COPY.name();
        final String EXTRACT_INTERFACE_WITH_MOVE = ERefactoringOperation.EXTRACT_INTERFACE_WITH_MOVE.name();

        if (refactoringMetaData != null) {
            String[] dataParts = refactoringMetaData.split(";");
            // "RefactoringMeta;Policy:%s;UseKnown:%b;AppliedRefactorings:"
            this.policy = dataParts[1].split(":")[1];
            this.useKnownRefactoringDistribution = Boolean.parseBoolean(dataParts[2].split(":")[1]);
            var tempRefactorings = dataParts[3].split(":");
            String[] appliedRefactorings;
            if (tempRefactorings.length > 1) {
                appliedRefactorings = tempRefactorings[1].split(",");
            } else {
                appliedRefactorings = new String[0];
            }
            this.numberOfRefactorings = appliedRefactorings.length;
            for (String refactoring : appliedRefactorings) {
                if (refactoring.equals(RENAME_PROPERTY)) {
                    this.numberOfRenameProperty++;
                } else if (refactoring.equals(RENAME_ELEMENT)) {
                    this.numberOfRenameElement++;
                } else if (refactoring.equals(MOVE_PROPERTY)) {
                    this.numberOfMoveProperty++;
                } else if (refactoring.equals(EXTRACT_INTERFACE_WITH_COPY)) {
                    this.numberOfExtractInterfaceCopy++;
                } else if (refactoring.equals(EXTRACT_INTERFACE_WITH_MOVE)) {
                    this.numberOfExtractInterfaceMove++;
                } else {
                    throw new IllegalArgumentException("Not Implemented case!");
                }
            }
        } else {
            this.policy = "";
            this.numberOfRefactorings = 0;
            this.useKnownRefactoringDistribution = false;
        }
    }

    public void calculateStatistics(Set<ElementTuple> tuples, double runtime) {
        this.numberOfElements = countElements(tuples);
        this.numberOfTuples = tuples.size();

        this.runtime = runtime;
        NwmWeight weightMetric = new NwmWeight(this.numberOfModels);
        this.weight = weightMetric.getQualityOfMatching(tuples);
        // A match is represented by a tuple with more than one element, we want to count how many there are in order
        // to calculate the average weight of a match tuple
        this.numberOfMatches = tuples.stream().mapToInt(tuple -> tuple.getElements().size() > 1 ? 1 : 0).sum();
    }

    private int countElements(Set<ElementTuple> set) {
        ArrayList<RElement> elements = new ArrayList<>();
        for (ElementTuple tuple : set) {
            elements.addAll(tuple.getElements());
        }
        return elements.size();
    }

    public void writeAsJSON(String pathToFile, boolean append) {
        Path path = Paths.get(pathToFile);
        for (int i = 1; i < path.getNameCount(); i++) {
            File f;
            if (path.getRoot() != null) {
                f = Paths.get(path.getRoot().toString(), path.subpath(0, i).toString()).toFile();
            } else {
                f = Paths.get(path.subpath(0, i).toString()).toFile();
            }
            if (!f.exists()) {
                f.mkdir();
            }
        }

        Gson gson = new Gson();
        try (PrintWriter writer = new PrintWriter(new FileOutputStream(new File(pathToFile), append))) {
            String json = gson.toJson(this);
            writer.println(json);
        } catch (Exception e) {
            System.err.println(this.toString());
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        return "Dataset: " + this.dataset + "\n" +
                String.format(Locale.ENGLISH,
                        "#%d\t" +
                                "%s  \t--  \t" +
                                "weight: %.4f  \t--  \t" +
                                "runtimeTotal: %.5f\t",
                        runID,
                        method,
                        weight,
                        runtime)
                + "\n"
                + "Number of Models " +
                numberOfModels +
                "  \t--  \tNumber of Elements: " +
                numberOfElements +
                "  \t--  \tNumber of Tuples: " +
                numberOfTuples +
                "  \t--  \tNumber of Matches: " +
                numberOfMatches +
                "  \t--  \tNumber of Elements in Largest Model: " +
                sizeOfLargestModel +
                "\n" +
                "Refactoring Policy: " + policy +
                "  \t-- \tUse Known Refactoring Distribution: " +
                useKnownRefactoringDistribution +
                "  \t-- \tTotal Number of Refactorings: " +
                numberOfRefactorings +
                "\n" +
                "Rename Property: " + numberOfRenameProperty +
                "  \t-- \t#Rename Element: " + numberOfRenameElement +
                "  \t-- \t#Move Property: " + numberOfMoveProperty +
                "  \t-- \t#Extract Interface (Move): " + numberOfExtractInterfaceMove +
                "  \t-- \t#Extract Interface (Copy): " + numberOfExtractInterfaceCopy +
                "\n";
    }

}
