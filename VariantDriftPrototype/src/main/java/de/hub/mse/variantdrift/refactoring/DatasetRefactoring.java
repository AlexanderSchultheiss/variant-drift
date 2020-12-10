package de.hub.mse.variantdrift.refactoring;

import de.hub.mse.variantdrift.experiments.algorithms.nwm.domain.Element;
import de.hub.mse.variantdrift.experiments.algorithms.nwm.domain.Model;
import de.hub.mse.variantdrift.refactoring.targets.RenameElementTarget;
import de.hub.mse.variantdrift.refactoring.targets.RenamePropertyTarget;
import de.hub.mse.variantdrift.refactoring.targets.ExtractInterfaceTarget;
import de.hub.mse.variantdrift.refactoring.targets.MovePropertyTarget;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;
import java.util.*;

public class DatasetRefactoring {
    private static final Random random = new SecureRandom();
    // Only change this if you want to load models from a different directory
    private static final String baseDatasetDir = "./../data/experimental_subjects";
    // This directory is created in the working directory in order to store the refactored models
    private static final String baseTargetDir = "./../data/refactored_subjects";

    // Number of models that are generated for one specific number of refactorings
    private static final int numberOfVariants = 30;

    // Minimum number of refactorings that are applied
    private static final int refactoringStartCount = 0;
    // Upper bound of the number of refactorings that are applied
    private static final int refactoringEndCount = 401;
    // Step size for number of refactorings
    private static final int refactoringStep = 10;
    // Determines how the models are selected for refactoring
    private static final ERefactoringPolicy policy = ERefactoringPolicy.ALL_MODELS_RANDOMLY;
    // Whether the known distribution of refactorings should be applied or not, array for running true and false if wanted
    // In our paper, we only consider using the distribution (true) according to Vassallo et.al
    // (https://doi.org/10.1016/j.scico.2019.05.002)
    private static final boolean[] knownDistributionSetup = new boolean[]{true};
    // The list of refactoring operations that might be applied
    private static final List<List<ERefactoringOperation>> refactoringSetups = Arrays.asList(
            // Setup-A: Structural refactorings and rename refactorings
            Arrays.asList(
                    ERefactoringOperation.RENAME_PROPERTY,
                    ERefactoringOperation.RENAME_ELEMENT,
                    ERefactoringOperation.MOVE_PROPERTY,
                    ERefactoringOperation.EXTRACT_INTERFACE_WITH_COPY,
                    ERefactoringOperation.EXTRACT_INTERFACE_WITH_MOVE
            ),
            // Setup-B: Only structural refactorings
            Arrays.asList(
                    ERefactoringOperation.MOVE_PROPERTY,
                    ERefactoringOperation.EXTRACT_INTERFACE_WITH_COPY,
                    ERefactoringOperation.EXTRACT_INTERFACE_WITH_MOVE
            )
    );
    // Array of setup names, make sure there are more names than setups above
    private static final String[] setupNames = new String[]{
            "A", "B"
    };

    // List of the datasets
    // You can comment out lines of the datasets which you do not want to run, or add new datasets (beware of commas)
    private static final List<String> datasets = Arrays.asList(
            "ppu",
            "bcms",
            "Apogames"
    );

    // Refactor the experimental subjects and save all generated variants.
    public static void main(String... args) {
        // for each dataset
        for (String dataset : datasets) {
            String datasetFile = baseDatasetDir + "/" + dataset + ".csv";
            int setupID = 0;
            System.out.println("Refactoring " + dataset);
            for (var listOfRefactorings : refactoringSetups) {
                System.out.println("Generating variants for setup " + setupNames[setupID] + "...");
                for (int numberOfRefactorings = refactoringStartCount;
                     numberOfRefactorings < refactoringEndCount;
                     numberOfRefactorings += refactoringStep) {
                    for (boolean useKnownDistribution : knownDistributionSetup) {
                        for (int i = 0; i < numberOfVariants; i++) {
                            // Here, we use the Model class of Rubin and Chechik
                            ArrayList<Model> models = Model.readModelsFile(datasetFile);
                            List<ERefactoringOperation> appliedRefactorings =
                                    DatasetRefactoring.applyRandomRefactoringToDataset(models, listOfRefactorings,
                                            numberOfRefactorings, useKnownDistribution, policy);
                            var targetFile = new StringBuilder(baseTargetDir + "/" + "setup_" + setupNames[setupID]
                                    + "/" + dataset);
                            if (useKnownDistribution) {
                                targetFile.append("_Known_");
                            } else {
                                targetFile.append("_Random_");
                            }
                            targetFile.append(numberOfRefactorings);
                            targetFile.append("_");
                            targetFile.append(i);
                            targetFile.append(".csv");

                            // Create a header line that holds all refactoring information
                            StringBuilder header = new StringBuilder(
                                    String.format("RefactoringMeta;Policy:%s;UseKnown:%b;AppliedRefactorings:",
                                            policy, useKnownDistribution));
                            for (var refactoring : appliedRefactorings) {
                                header.append(refactoring);
                                header.append(",");
                            }
                            header.deleteCharAt(header.length() - 1);
                            saveDataset(targetFile.toString(), models, header.toString());
                            System.out.printf("%s: Generated %d of %d variants with %d refactorings.\r",
                                    dataset,
                                    i+1,
                                    numberOfVariants,
                                    numberOfRefactorings);
                        }
                    }
                    System.out.println();
                }
                System.out.println("Finished generation of variants for setup " + setupNames[setupID] + ".");
                System.out.println("---");
                setupID++;
            }
            System.out.println("Finished generation of all variants for " + dataset + "\n");
            System.out.println();
        }
    }

    /**
     * Refactor the given set of models randomly, according to the provided parameters.
     *
     * @param models               The models that are to be refactored.
     * @param enabledRefactorings  A list of variants specifying which refactoring operations are to be applied.
     * @param numberOfRefactorings The number of refactorings that are to be applied.
     * @param useKnownProbability  Whether a predetermined refactoring probability according to Vassallo et al.
     *                             (https://doi.org/10.1016/j.scico.2019.05.002) is to be used, or a uniform distribution
     *                             of the given refactorings.
     * @param policy               Policy of how the refactorings are to be distributed over the input models.
     * @return The list of refactoring operations that have been applied.
     */
    public static List<ERefactoringOperation> applyRandomRefactoringToDataset(List<Model> models,
                                                                              List<ERefactoringOperation> enabledRefactorings,
                                                                              int numberOfRefactorings,
                                                                              boolean useKnownProbability,
                                                                              ERefactoringPolicy policy) {
        List<ERefactoringOperation> appliedRefactorings = new ArrayList<>();
        Model selectedModel;
        switch (policy) {
            case ONE_RANDOM_MODEL:
                selectedModel = models.get(random.nextInt(models.size()));
                appliedRefactorings = applyRandomRefactoringToModel(selectedModel, enabledRefactorings, numberOfRefactorings, useKnownProbability);
                break;
            case ALL_MODELS_RANDOMLY:
                Map<Model, Integer> numberOfRefactoringsPerModel = new HashMap<>();
                // Randomly select a model for each refactoring that is to be applied
                for (int i = 0; i < numberOfRefactorings; i++) {
                    selectedModel = models.get(random.nextInt(models.size()));
                    if (numberOfRefactoringsPerModel.containsKey(selectedModel)) {
                        numberOfRefactoringsPerModel.put(selectedModel, numberOfRefactoringsPerModel.get(selectedModel) + 1);
                    } else {
                        numberOfRefactoringsPerModel.put(selectedModel, 1);
                    }
                }
                // Apply the refactorings
                for (Model model : numberOfRefactoringsPerModel.keySet()) {
                    appliedRefactorings.addAll(applyRandomRefactoringToModel(model, enabledRefactorings,
                            numberOfRefactoringsPerModel.get(model), useKnownProbability));
                }
                break;
            case ALL_MODELS_EQUALLY:
                for (Model model : models) {
                    appliedRefactorings.addAll(applyRandomRefactoringToModel(model, enabledRefactorings,
                            numberOfRefactorings, useKnownProbability));
                }
                break;
            default:
                throw new UnsupportedOperationException();
        }
        return appliedRefactorings;
    }

    /**
     * Refactor the given set of models randomly, according to the provided parameters.
     *
     * @param model                The model that is to be refactored.
     * @param enabledRefactorings  A list of variants specifying which refactoring operations are to be applied.
     * @param numberOfRefactorings The number of refactorings that are to be applied.
     * @param useKnownProbability  Whether a predetermined refactoring probability according to Vassallo et al.
     *                             (https://doi.org/10.1016/j.scico.2019.05.002) is to be used, or a uniform distribution
     *                             of the given refactorings.
     * @return The list of refactoring operations that have been applied.
     */
    public static List<ERefactoringOperation> applyRandomRefactoringToModel(Model model,
                                                                            List<ERefactoringOperation> enabledRefactorings,
                                                                            int numberOfRefactorings,
                                                                            boolean useKnownProbability) {
        // Get all possible refactoring targets for the model
        var renamePropertyTargets = RenamePropertyTarget.findAllTargets(model);
        var renameElementTargets = RenameElementTarget.findAllTargets(model);
        var movePropertyTargets = MovePropertyTarget.findAllTargets(model);
        var extractInterfaceTargets = ExtractInterfaceTarget.findAllTargets(model);

        // Initialize the set of available refactorings based on the found targets
        List<ERefactoringOperation> availableRefactorings = new ArrayList<>(enabledRefactorings);
        if (renamePropertyTargets.isEmpty()) availableRefactorings.remove(ERefactoringOperation.RENAME_PROPERTY);
        if (renameElementTargets.isEmpty()) availableRefactorings.remove(ERefactoringOperation.RENAME_ELEMENT);
        if (movePropertyTargets.isEmpty()) availableRefactorings.remove(ERefactoringOperation.MOVE_PROPERTY);
        if (extractInterfaceTargets.isEmpty()) {
            availableRefactorings.remove(ERefactoringOperation.EXTRACT_INTERFACE_WITH_MOVE);
            availableRefactorings.remove(ERefactoringOperation.EXTRACT_INTERFACE_WITH_COPY);
        }

        // We want to store all refactorings that have been applied
        List<ERefactoringOperation> appliedRefactorings = new ArrayList<>();
        while (numberOfRefactorings > 0 && availableRefactorings.size() > 0) {
            // Get the next type of refactoring that is to be applied
            ERefactoringOperation refactoringOperation = getRandomRefactoring(useKnownProbability, availableRefactorings);
            Set<Element> refactoredElements = switch (refactoringOperation) {
                case RENAME_PROPERTY -> applyRenameProperty(renamePropertyTargets);
                case RENAME_ELEMENT -> applyRenameElement(renameElementTargets);
                case MOVE_PROPERTY -> applyMoveProperty(movePropertyTargets);
                case EXTRACT_INTERFACE_WITH_MOVE -> applyExtractInterface(extractInterfaceTargets, true);
                case EXTRACT_INTERFACE_WITH_COPY -> applyExtractInterface(extractInterfaceTargets, false);
            };
            // Randomly select a refactoring target to which the refactoring is applied to and apply the refactoring
            if (refactoredElements != null && refactoredElements.size() > 0) {
                appliedRefactorings.add(refactoringOperation);
                numberOfRefactorings--;
            } else if (renamePropertyTargets.isEmpty()
                    && renameElementTargets.isEmpty()
                    && movePropertyTargets.isEmpty()
                    && extractInterfaceTargets.isEmpty()) {
                // If there are no refactoring targets left, we stop the refactoring
                return appliedRefactorings;
            } else {
                if (renamePropertyTargets.isEmpty())
                    availableRefactorings.remove(ERefactoringOperation.RENAME_PROPERTY);
                if (renameElementTargets.isEmpty()) availableRefactorings.remove(ERefactoringOperation.RENAME_ELEMENT);
                if (movePropertyTargets.isEmpty()) availableRefactorings.remove(ERefactoringOperation.MOVE_PROPERTY);
                if (extractInterfaceTargets.isEmpty()) {
                    availableRefactorings.remove(ERefactoringOperation.EXTRACT_INTERFACE_WITH_COPY);
                    availableRefactorings.remove(ERefactoringOperation.EXTRACT_INTERFACE_WITH_MOVE);
                }
            }
        }

        return appliedRefactorings;
    }

    private static Set<Element> applyRenameProperty(Set<RenamePropertyTarget> targets) {
        var target = extractRandomTarget(targets);
        if (target != null) {
            String propertyName = NameProvider.getRandomSiblingName(target.property);
            return Refactoring.renameProperty(target, propertyName);
        }
        return null;
    }

    private static Set<Element> applyRenameElement(Set<RenameElementTarget> targets) {
        var target = extractRandomTarget(targets);
        if (target != null) {
            String elementName = NameProvider.getRandomSiblingName(target.element.getLabel());
            return Refactoring.renameElement(target, elementName);
        }
        return null;
    }

    private static Set<Element> applyMoveProperty(Set<MovePropertyTarget> targets) {
        var target = extractRandomTarget(targets);
        if (target != null) {
            // We want to remove all targets from the target set that overlap with the used target
            targets.removeIf(target::overlapsWith);
            return Refactoring.moveProperty(target);
        }
        return null;
    }

    private static Set<Element> applyExtractInterface(Set<ExtractInterfaceTarget> targets, boolean moveProperties) {
        var target = extractRandomTarget(targets);
        if (target != null) {
            Set<String> elementNames = new HashSet<>();
            target.sourceElements.forEach(element -> elementNames.add(element.getLabel()));
            String propertyName = NameProvider.getRandomParentName(elementNames);

            // We want to remove all targets from the target set that overlap with the used target
            targets.removeIf(target::overlapsWith);
            return Refactoring.extractInterface(target, propertyName, moveProperties);
        }
        return null;
    }


    private static ERefactoringOperation getRandomRefactoring(boolean useKnownRefactoringDistribution,
                                                              List<ERefactoringOperation> availableRefactorings) {
        if (useKnownRefactoringDistribution) {
            // Numbers based on Carmine Vassallo, Giovanni Grano, Fabio Palomba, Harald C. Gall, Alberto Bacchelli,
            // A large-scale empirical exploration on refactoring activities in open source software projects,
            // Science of Computer Programming, Volume 180, 2019, https://doi.org/10.1016/j.scico.2019.05.002
            // Rename Method (Property) 713 + 3094 + 1105 = 4912 := ~36%
            // Rename Class (Element) 179 + 883 + 406 = 1468 := ~11%
            // Move Field/Method (Property) 552 + 177 + 2004 + 1107 + 844 + 747 = 5431 := ~40%
            // Extract Interface 12 + 1276 + 528 = 1816 := 13%
            // TOTAL: 4912 + 1468 + 5431 + 1816 = 13627
            int boundForRenameProperty = 4912;
            int boundForRenameElement = boundForRenameProperty + 1468;
            int boundForMoveProperty = boundForRenameElement + 5431;
            int boundForExtractInterface = boundForMoveProperty + 1816;

            while (true) {
                int number = random.nextInt(boundForExtractInterface);
                if (number < boundForRenameProperty && availableRefactorings.contains(ERefactoringOperation.RENAME_PROPERTY)) {
                    return ERefactoringOperation.RENAME_PROPERTY;
                } else if (number < boundForRenameElement && availableRefactorings.contains(ERefactoringOperation.RENAME_ELEMENT)) {
                    return ERefactoringOperation.RENAME_ELEMENT;
                } else if (number < boundForMoveProperty && availableRefactorings.contains(ERefactoringOperation.MOVE_PROPERTY)) {
                    return ERefactoringOperation.MOVE_PROPERTY;
                } else {
                    // We have a 50:50 chance of extract with move or copy
                    if (availableRefactorings.contains(ERefactoringOperation.EXTRACT_INTERFACE_WITH_COPY)) {
                        if (availableRefactorings.contains(ERefactoringOperation.EXTRACT_INTERFACE_WITH_MOVE)) {
                            return random.nextBoolean() ? ERefactoringOperation.EXTRACT_INTERFACE_WITH_MOVE : ERefactoringOperation.EXTRACT_INTERFACE_WITH_COPY;
                        }
                        return ERefactoringOperation.EXTRACT_INTERFACE_WITH_COPY;
                    } else if (availableRefactorings.contains(ERefactoringOperation.EXTRACT_INTERFACE_WITH_MOVE)) {
                        return ERefactoringOperation.EXTRACT_INTERFACE_WITH_MOVE;
                    }
                }
            }
        } else {
            // Get a completely random refactoring
            return availableRefactorings.get(random.nextInt(availableRefactorings.size()));
        }
    }

    private static void saveDataset(String pathToFile, List<Model> dataset, String header) {
        // Create all directories on the path
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

        try (PrintWriter writer = new PrintWriter(new FileOutputStream(new File(pathToFile), false))) {
            // Write the header
            writer.println(header);
            for (Model model : dataset) {
                for (Element element : model.getElements()) {
                    // 1,1,Bed,n_bed_1;bedNum_1;room_1
                    StringBuilder lineBuilder = new StringBuilder(model.getId() + "," + element.getUUID()
                            + "," + element.getLabel() + ",");
                    for (String prop : element.getProperties()) {
                        lineBuilder.append(prop);
                        lineBuilder.append(";");
                    }
                    // Remove the trailing ';'
                    lineBuilder.deleteCharAt(lineBuilder.length() - 1);
                    writer.println(lineBuilder.toString());
                }
            }
        } catch (Exception e) {
            System.err.println(pathToFile);
            throw new RuntimeException(e);
        }
    }

    private static <E> E extractRandomTarget(Set<E> someSet) {
        if (someSet.isEmpty()) {
            return null;
        }
        int randomIndex = random.nextInt(someSet.size());
        int id = 0;
        for (E e : someSet) {
            if (id == randomIndex) {
                someSet.remove(e);
                return e;
            } else {
                id++;
            }
        }
        return null;
    }
}
