package de.hub.mse.variantsync.variantdrift.refactoring;

import de.hub.mse.variantsync.variantdrift.experiments.algorithms.nwm.domain.Element;
import de.hub.mse.variantsync.variantdrift.experiments.algorithms.nwm.domain.Model;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class DatasetRefactoringTest {
    private Model model;
    private List<ERefactoringOperation> enabledRefactorings = Arrays.asList(ERefactoringOperation.RENAME_PROPERTY,
            ERefactoringOperation.RENAME_ELEMENT, ERefactoringOperation.MOVE_PROPERTY,
            ERefactoringOperation.EXTRACT_INTERFACE_WITH_MOVE, ERefactoringOperation.EXTRACT_INTERFACE_WITH_COPY);

    @BeforeEach
    void initEmptyModel() {
        this.model = new Model("TestModel");
    }

    @Test
    void applyRRToModel_OneElementWithoutAdditionalProperties_OnlyRenameElementApplied() {
        LinkedList<String> propertiesA = new LinkedList<>(Collections.singletonList("n_elementA"));
        Element elementA = new Element("0", "ElementA", propertiesA, "TestModel");

        model.addElement(elementA);

        int count = 1;
        List<ERefactoringOperation> appliedRefactorings =
                DatasetRefactoring.applyRandomRefactoringToModel(model, enabledRefactorings, count, false);

        assert appliedRefactorings.size() == 1;
        assert appliedRefactorings.contains(ERefactoringOperation.RENAME_ELEMENT);
        assert !elementA.getLabel().equals("ElementA");
        assert !elementA.getProperties().get(0).equals("n_elementA");
        assert elementA.getProperties().size() == 1;
    }

    @RepeatedTest(100)
        // We repeat 100 times to increase the chance that all random permutations are tested
    void applyRRToModel_OneElementWithAdditionalProperties_RenameElementAndRenameProperty() {
        LinkedList<String> propertiesA = new LinkedList<>(Arrays.asList("n_elementA", "prop1"));
        Element elementA = new Element("0", "ElementA", propertiesA, "TestModel");

        model.addElement(elementA);

        int count = 2;
        List<ERefactoringOperation> appliedRefactorings =
                DatasetRefactoring.applyRandomRefactoringToModel(model, enabledRefactorings, count, false);

        assert appliedRefactorings.size() == 2;
        assert appliedRefactorings.contains(ERefactoringOperation.RENAME_ELEMENT);
        assert appliedRefactorings.contains(ERefactoringOperation.RENAME_PROPERTY);
        assert !elementA.getLabel().equals("ElementA");
        assert elementA.getProperties().size() == 2;
        assert !elementA.getProperties().contains("n_elementA");
        assert !elementA.getProperties().contains("prop1");
    }

    @RepeatedTest(100)
        // We repeat 100 times to increase the chance that all random permutations are tested
    void applyRRToModel_OneElementWithOneElementWithoutAdditionalProperties_Rename2ElementAndRenameProperty() {
        LinkedList<String> propertiesA = new LinkedList<>(Arrays.asList("n_elementA", "prop1"));
        LinkedList<String> propertiesB = new LinkedList<>(Collections.singletonList("n_elementB"));
        Element elementA = new Element("0", "ElementA", propertiesA, "TestModel");
        Element elementB = new Element("1", "ElementB", propertiesB, "TestModel");

        model.addElement(elementA);
        model.addElement(elementB);

        int count = 3;
        List<ERefactoringOperation> appliedRefactorings =
                DatasetRefactoring.applyRandomRefactoringToModel(model, enabledRefactorings, count, false);

        assert appliedRefactorings.size() == 3;
        assert appliedRefactorings.contains(ERefactoringOperation.RENAME_ELEMENT);
        assert !elementA.getLabel().equals("ElementA");
        assert !elementB.getLabel().equals("ElementB");
        assert !elementA.getProperties().contains("n_elementA");
        assert !elementB.getProperties().contains("n_elementB");
        if (appliedRefactorings.contains(ERefactoringOperation.RENAME_PROPERTY)) {
            assert elementA.getProperties().size() == 2;
            assert elementB.getProperties().size() == 1;
            assert !elementA.getProperties().contains("prop1");
        } else if (appliedRefactorings.contains(ERefactoringOperation.MOVE_PROPERTY)) {
            assert elementA.getProperties().size() == 1;
            assert elementB.getProperties().size() == 2;
            assert !elementA.getProperties().contains("prop1");
            assert elementB.getProperties().contains("prop1");
        } else {
            throw new AssertionError();
        }
    }

    @RepeatedTest(100)
        // We repeat 100 times to increase the chance that all random permutations are tested
    void applyRRToModel_TwoElementsWithCommonProperties_RenameElementRenamePropertyAndExtractInterface() {
        LinkedList<String> propertiesA = new LinkedList<>(Arrays.asList("n_elementA", "prop1", "prop2"));
        LinkedList<String> propertiesB = new LinkedList<>(Arrays.asList("n_elementB", "prop1", "prop2"));
        Element elementA = new Element("0", "ElementA", propertiesA, "TestModel");
        Element elementB = new Element("1", "ElementB", propertiesB, "TestModel");

        model.addElement(elementA);
        model.addElement(elementB);

        int count = 10;
        List<ERefactoringOperation> appliedRefactorings =
                DatasetRefactoring.applyRandomRefactoringToModel(model, enabledRefactorings, count, false);

        //appliedRefactorings.forEach(System.out::println);
        assert appliedRefactorings.contains(ERefactoringOperation.RENAME_ELEMENT);
        assert !appliedRefactorings.contains(ERefactoringOperation.MOVE_PROPERTY);
        assert !elementA.getLabel().equals("ElementA");
        assert !elementB.getLabel().equals("ElementB");

        if (appliedRefactorings.contains(ERefactoringOperation.EXTRACT_INTERFACE_WITH_MOVE)
                || appliedRefactorings.contains(ERefactoringOperation.EXTRACT_INTERFACE_WITH_COPY)) {
            assert model.getElements().size() == 3;
            var interfaceElement = model.getElements().get(2);
            assert interfaceElement.getProperties().contains("prop1") || interfaceElement.getProperties().contains("prop2");
        }

        switch (appliedRefactorings.size()) {
            case 3:
                assert appliedRefactorings.contains(ERefactoringOperation.EXTRACT_INTERFACE_WITH_MOVE);
                break;
            case 4:
                assert appliedRefactorings.contains(ERefactoringOperation.EXTRACT_INTERFACE_WITH_MOVE);
                assert appliedRefactorings.contains(ERefactoringOperation.RENAME_PROPERTY);
                break;
            case 5:
                assert appliedRefactorings.contains(ERefactoringOperation.EXTRACT_INTERFACE_WITH_MOVE)
                        || (!propertiesA.get(0).equals(propertiesB.get(0)) && !propertiesA.get(1).equals(propertiesB.get(1)));
                assert appliedRefactorings.contains(ERefactoringOperation.RENAME_PROPERTY);
                break;
            case 6:
                assert !appliedRefactorings.contains(ERefactoringOperation.EXTRACT_INTERFACE_WITH_MOVE);
                assert !appliedRefactorings.contains(ERefactoringOperation.EXTRACT_INTERFACE_WITH_COPY);
                assert appliedRefactorings.contains(ERefactoringOperation.RENAME_PROPERTY);
                break;
            case 7:
                assert appliedRefactorings.contains(ERefactoringOperation.EXTRACT_INTERFACE_WITH_COPY);
                assert appliedRefactorings.contains(ERefactoringOperation.RENAME_PROPERTY);
                break;
            default:
                throw new AssertionError("Unexpected number of refactorings!");
        }
    }

    @RepeatedTest(100)
        // We repeat 100 times to increase the chance that all random permutations are tested
    void applyRRToModel_TwoElementsWithCommonAndUniqueProperties_AnyRefactoringApplied() {
        LinkedList<String> propertiesA = new LinkedList<>(Arrays.asList("n_elementA", "prop1", "prop2"));
        LinkedList<String> propertiesB = new LinkedList<>(Arrays.asList("n_elementB", "prop1", "prop3"));
        Element elementA = new Element("0", "ElementA", propertiesA, "TestModel");
        Element elementB = new Element("1", "ElementB", propertiesB, "TestModel");

        model.addElement(elementA);
        model.addElement(elementB);

        int count = 10;
        List<ERefactoringOperation> appliedRefactorings =
                DatasetRefactoring.applyRandomRefactoringToModel(model, enabledRefactorings, count, false);

        assert appliedRefactorings.contains(ERefactoringOperation.RENAME_ELEMENT);
        assert !elementA.getLabel().equals("ElementA");
        assert !elementB.getLabel().equals("ElementB");
        assert appliedRefactorings.contains(ERefactoringOperation.RENAME_PROPERTY)
                || appliedRefactorings.contains(ERefactoringOperation.MOVE_PROPERTY);

        try {
            switch (appliedRefactorings.size()) {
                case 5:
                    assert appliedRefactorings.contains(ERefactoringOperation.EXTRACT_INTERFACE_WITH_MOVE);
                    assert model.getElements().size() == 3;
                    assert !propertiesA.contains("prop1") && !propertiesB.contains("prop1");
                    assert propertiesA.size() + propertiesB.size() == 4;
                    break;
                case 6:
                    assert !appliedRefactorings.contains(ERefactoringOperation.EXTRACT_INTERFACE_WITH_MOVE);
                    assert !appliedRefactorings.contains(ERefactoringOperation.EXTRACT_INTERFACE_WITH_COPY);
                    assert propertiesA.size() + propertiesB.size() == 6;
                    break;
                case 7:
                    assert appliedRefactorings.contains(ERefactoringOperation.EXTRACT_INTERFACE_WITH_COPY);
                    assert appliedRefactorings.contains(ERefactoringOperation.RENAME_PROPERTY);
                    assert propertiesA.size() + propertiesB.size() == 6;
                    break;
                default:
                    throw new AssertionError("Unexpected number of refactorings");
            }
        } catch (AssertionError e) {
            appliedRefactorings.forEach(System.out::println);
            throw e;
        }
    }

    @Test
    void applyRRToModel_NumberOfRefactoringsIsZero_NoRefactoringDone() {
        LinkedList<String> propertiesA = new LinkedList<>(Arrays.asList("n_elementA", "prop1"));
        LinkedList<String> propertiesB = new LinkedList<>(Collections.singletonList("n_elementB"));
        Element elementA = new Element("0", "ElementA", propertiesA, "TestModel");
        Element elementB = new Element("1", "ElementB", propertiesB, "TestModel");

        model.addElement(elementA);
        model.addElement(elementB);

        int count = 0;
        List<ERefactoringOperation> appliedRefactorings =
                DatasetRefactoring.applyRandomRefactoringToModel(model, enabledRefactorings, count, false);

        assert appliedRefactorings.size() == 0;
        assert propertiesA.get(0).equals("n_elementA");
        assert propertiesA.get(1).equals("prop1");
        assert propertiesB.get(0).equals("n_elementB");
        assert model.getElements().size() == 2;
    }

    @RepeatedTest(100)
    void applyRRToModel_NumberOfRefactoringsIsOne_ExactlyOneRefactoringDone() {
        LinkedList<String> propertiesA = new LinkedList<>(Arrays.asList("n_elementA", "prop1"));
        LinkedList<String> propertiesB = new LinkedList<>(Collections.singletonList("n_elementB"));
        Element elementA = new Element("0", "ElementA", propertiesA, "TestModel");
        Element elementB = new Element("1", "ElementB", propertiesB, "TestModel");

        model.addElement(elementA);
        model.addElement(elementB);

        int count = 1;
        List<ERefactoringOperation> appliedRefactorings =
                DatasetRefactoring.applyRandomRefactoringToModel(model, enabledRefactorings, count, false);

        assert appliedRefactorings.size() == 1;
        switch (appliedRefactorings.get(0)) {
            case RENAME_PROPERTY:
                assert !propertiesA.get(1).equals("prop1");
                break;
            case RENAME_ELEMENT:
                assert !(propertiesA.get(0).equals("n_elementA") && propertiesB.get(0).equals("n_elementB"));
                break;
            case MOVE_PROPERTY:
                assert propertiesA.size() == 1;
                assert propertiesB.size() == 2;
                assert propertiesB.get(1).equals("prop1");
                break;
        }
        assert model.getElements().size() == 2;
    }

    @ParameterizedTest
    @ValueSource(ints = {3, 5, 15, Integer.MAX_VALUE})
    void applyRRToModel_NumberOfRefactoringsIsTheMaximumPossibleOrHigherOne_AllPossibleDone(int count) {
        LinkedList<String> propertiesA = new LinkedList<>(Arrays.asList("n_elementA", "prop1"));
        LinkedList<String> propertiesB = new LinkedList<>(Collections.singletonList("n_elementB"));
        Element elementA = new Element("0", "ElementA", propertiesA, "TestModel");
        Element elementB = new Element("1", "ElementB", propertiesB, "TestModel");

        model.addElement(elementA);
        model.addElement(elementB);

        List<ERefactoringOperation> appliedRefactorings =
                DatasetRefactoring.applyRandomRefactoringToModel(model, enabledRefactorings, count, false);

        assert appliedRefactorings.size() == 3;
        assert appliedRefactorings.contains(ERefactoringOperation.RENAME_ELEMENT);
        if (appliedRefactorings.contains(ERefactoringOperation.RENAME_PROPERTY)) {
            assert propertiesA.size() == 2;
            assert propertiesB.size() == 1;
            assert !propertiesA.get(1).equals("prop1");
        }
        if (appliedRefactorings.contains(ERefactoringOperation.MOVE_PROPERTY)) {
            assert propertiesA.size() == 1;
            assert propertiesB.size() == 2;
            assert propertiesB.get(1).equals("prop1");
        }

        assert model.getElements().size() == 2;
    }
}
