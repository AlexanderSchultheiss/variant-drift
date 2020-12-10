package de.hub.mse.variantdrift.refactoring;

import de.hub.mse.variantdrift.experiments.algorithms.nwm.domain.Element;
import de.hub.mse.variantdrift.experiments.algorithms.nwm.domain.Model;
import de.hub.mse.variantdrift.refactoring.targets.ExtractInterfaceTarget;
import de.hub.mse.variantdrift.refactoring.targets.MovePropertyTarget;
import de.hub.mse.variantdrift.refactoring.targets.RenameElementTarget;
import de.hub.mse.variantdrift.refactoring.targets.RenamePropertyTarget;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.*;

public class RefactoringTest {
    RandomString randomString = new RandomString();

    @Test
    void renameProperty_RefactorWithValidName() {
        LinkedList<String> properties = new LinkedList<>(Arrays.asList("prop_1", "prop_2", "cool_0",
                "n_TestElement"));
        Element testElement = new Element("-1", "TestElement", properties, "0");

        testRenameProperty(testElement, "prop_1", randomString.nextString(), true);
        testRenameProperty(testElement, "prop_2", randomString.nextString(), true);
    }

    @Test
    void renameProperty_shouldNotRefactorIfOldPropertyNotInElement() {
        LinkedList<String> properties = new LinkedList<>(Arrays.asList("pro1", "pro2", "co0",
                "n_TestElement"));
        Element testElement = new Element("-1", "TestElement", properties, "0");

        testRenameProperty(testElement, randomString.nextString(), randomString.nextString(), false);
        testRenameProperty(testElement, randomString.nextString(), randomString.nextString(), false);
        testRenameProperty(testElement, randomString.nextString(), randomString.nextString(), false);
    }

    @Test
    void renameProperty_shouldNotRefactorIfOldAndNewInElement() {
        LinkedList<String> properties = new LinkedList<>(Arrays.asList("prop_1", "prop_2", "n_TestElement"));
        Element testElement = new Element("-1", "TestElement", properties, "0");

        testRenameProperty(testElement, "prop_1", "prop_2", false);
        testRenameProperty(testElement, "prop_2", "prop_2", false);
    }

    @Test
    void renameProperty_shouldNotRefactorIfOldIsNotButNewIsInElement() {
        LinkedList<String> properties = new LinkedList<>(Arrays.asList("prop_1", "prop_2", "n_TestElement"));
        Element testElement = new Element("-1", "TestElement", properties, "0");

        testRenameProperty(testElement, "missing", "prop_1", false);
        testRenameProperty(testElement, "Not__Here", "prop_2", false);
    }

    @Test
    void renameProperty_shouldNotRefactorIfOldAndNewAreTheSame() {
        LinkedList<String> properties = new LinkedList<>(Arrays.asList("prop_1", "prop_2", "n_TestElement"));
        Element testElement = new Element("-1", "TestElement", properties, "0");

        String prop1 = randomString.nextString();
        String prop2 = randomString.nextString();
        testRenameProperty(testElement, prop1, prop1, false);
        testRenameProperty(testElement, prop2, prop2, false);
    }

    @Test
    void renameProperty_shouldNotRefactorIfNewIsEmptyOrOnlyWhitespace() {
        LinkedList<String> properties = new LinkedList<>(Arrays.asList("prop_1", "prop_2", "n_TestElement"));
        Element testElement = new Element("-1", "TestElement", properties, "0");

        testRenameProperty(testElement, "prop_1", "", false);
        testRenameProperty(testElement, "prop_2", "  ", false);
    }

    private void testRenameProperty(Element element, String oldName, String newName, boolean refactoringExpected) {
        // Save a copy of the properties before the refactoring is applied
        List<String> propertiesBefore = new LinkedList<>(element.getProperties());
        RenamePropertyTarget target = new RenamePropertyTarget(element, oldName);
        Set<Element> refactoredElements = Refactoring.renameProperty(target, newName);

        if (refactoringExpected) {
            assert refactoredElements.size() == 1;
            assert refactoredElements.contains(element);
            for (Element refactoredElement : refactoredElements) {
                assert !refactoredElement.getProperties().contains(oldName);
                assert refactoredElement.getProperties().contains(newName);
                assert refactoredElement.getProperties().size() == propertiesBefore.size();
                assert element.getProperties().size() == propertiesBefore.size();
            }
        } else {
            assert refactoredElements.size() == 0;
            assert propertiesBefore.equals(element.getProperties());
        }
    }

    @Test
    void renameElement_validNameWithOneElementModel_RefactorOne() {
        LinkedList<String> properties = new LinkedList<>(Arrays.asList("prop_1", "prop_2", "n_TestElement"));
        Element element = new Element("-1", "TestElement", properties, "0");

        Model model = new Model("0");
        model.addElement(element);

        testRenameElement(element, model, randomString.nextString(), true);
    }

    @Test
    void renameElement_emptyOrWhitespaceOnlyName_NoRefactoring() {
        LinkedList<String> properties = new LinkedList<>(Arrays.asList("prop_1", "prop_2", "n_TestElement"));
        Element element = new Element("-1", "TestElement", properties, "0");

        Model model = new Model("0");
        model.addElement(element);

        testRenameElement(element, model, "", false);
        testRenameElement(element, model, " ", false);
    }

    @Test
    void renameElement_nameEqualToOldName_NoRefactoring() {
        LinkedList<String> properties = new LinkedList<>(Arrays.asList("prop_1", "prop_2", "n_TestElement"));
        Element element = new Element("-1", "TestElement", properties, "0");

        Model model = new Model("0");
        model.addElement(element);

        testRenameElement(element, model, element.getLabel(), false);
    }

    @Test
    void renameElement_validNameWithSeveralUnrelatedElements_RefactorOne() {
        LinkedList<String> properties = new LinkedList<>(Arrays.asList("prop_1", "prop_2", "n_TestElement"));
        Element element = new Element("-1", "TestElement", properties, "0");

        LinkedList<String> props1 = new LinkedList<>(Arrays.asList("prop_1", "prop_2", "n_UnrelatedA"));
        Element unrelatedElementA = new Element("-1", "UnrelatedA", props1, "0");
        LinkedList<String> props2 = new LinkedList<>(Arrays.asList("prop_1", "prop_2", "n_UnrelatedB"));
        Element unrelatedElementB = new Element("-1", "UnrelatedB", props2, "0");

        Model model = new Model("0");
        model.addElement(element);
        model.addElement(unrelatedElementA);
        model.addElement(unrelatedElementB);

        Set<Element> refactoredElements = testRenameElement(element, model, randomString.nextString(), true);
        // Assert that only the renamed element was refactored
        assert refactoredElements.size() == 1;
        assert refactoredElements.contains(element);
    }

    @Test
    void renameElement_validNameWithOnlyRelatedElements_RefactorAll() {
        LinkedList<String> properties = new LinkedList<>(Arrays.asList("prop_1", "prop_2", "n_TestElement"));
        Element element = new Element("-1", "TestElement", properties, "0");

        LinkedList<String> props1 = new LinkedList<>(Arrays.asList("prop_1", "findTestElement", "n_RelatedA"));
        Element relatedElementA = new Element("-1", "RelatedA", props1, "0");
        LinkedList<String> props2 = new LinkedList<>(Arrays.asList("prop_1", "testElement", "n_RelatedB"));
        Element relatedElementB = new Element("-1", "RelatedB", props2, "0");

        Model model = new Model("0");
        model.addElement(element);
        model.addElement(relatedElementA);
        model.addElement(relatedElementB);

        String newName = randomString.nextString();
        Set<Element> refactoredElements = testRenameElement(element, model, newName, true);
        // Assert that only the renamed element was refactored
        assert refactoredElements.size() == 3;
        assert refactoredElements.contains(element);
        assert relatedElementA.getProperties().contains("find" + newName);
        assert relatedElementB.getProperties().get(1).toLowerCase().equals(newName.toLowerCase());
    }

    @Test
    void renameElement_validNameWithMixedElements_RefactorSome() {
        LinkedList<String> properties = new LinkedList<>(Arrays.asList("prop_1", "prop_2", "n_TestElement"));
        Element element = new Element("-1", "TestElement", properties, "0");

        LinkedList<String> props1 = new LinkedList<>(Arrays.asList("prop_1", "findTestElement", "n_RelatedA"));
        Element relatedElementA = new Element("-1", "RelatedA", props1, "0");
        LinkedList<String> props2 = new LinkedList<>(Arrays.asList("prop_1", "prop_2", "n_UnrelatedB"));
        Element unrelatedElementB = new Element("-1", "UnrelatedB", props2, "0");

        Model model = new Model("0");
        model.addElement(element);
        model.addElement(relatedElementA);
        model.addElement(unrelatedElementB);

        String newName = randomString.nextString();
        Set<Element> refactoredElements = testRenameElement(element, model, newName, true);
        // Assert that only the renamed element was refactored
        assert refactoredElements.size() == 2;
        assert refactoredElements.contains(element);
        assert refactoredElements.contains(relatedElementA);
        assert !refactoredElements.contains(unrelatedElementB);
        assert relatedElementA.getProperties().contains("find" + newName);
        assert unrelatedElementB.getProperties().equals(new LinkedList<>(Arrays.asList("prop_1", "prop_2", "n_UnrelatedB")));
    }

    @Test
    void renameElement_validNameButWrongModel_Exception() {
        LinkedList<String> properties = new LinkedList<>(Arrays.asList("prop_1", "prop_2", "n_TestElement"));
        Element element = new Element("-1", "TestElement", properties, "1");

        LinkedList<String> props1 = new LinkedList<>(Arrays.asList("prop_1", "findTestElement", "n_RelatedA"));
        Element relatedElementA = new Element("-1", "RelatedA", props1, "0");

        Model model = new Model("0");
        model.addElement(relatedElementA);


        Assertions.assertThrows(IllegalArgumentException.class,
                () -> testRenameElement(element, model, element.getLabel(), false));

    }

    @Test
    void renameElement_validNameButEmptyModel_Exception() {
        LinkedList<String> properties = new LinkedList<>(Arrays.asList("prop_1", "prop_2", "n_TestElement"));
        Element element = new Element("-1", "TestElement", properties, "1");

        Model model = new Model("0");

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> testRenameElement(element, model, element.getLabel(), false));
    }

    private Set<Element> testRenameElement(Element element, Model model, String name, boolean refactoringExpected) {
        // Save a copy of the properties before the refactoring is applied
        Map<Element, List<String>> propertiesBefore = new HashMap<>();
        for (Element ele : model.getElements()) {
            propertiesBefore.put(ele, new LinkedList<>(ele.getProperties()));
        }
        String oldName = element.getLabel();
        String oldNameProperty = null;
        for (String prop : element.getProperties()) {
            if (prop.startsWith("n_")) {
                oldNameProperty = prop;
            }
        }

        // Apply the refactoring
        RenameElementTarget target = new RenameElementTarget(element, model);
        Set<Element> refactoredElements = Refactoring.renameElement(target, name);

        if (refactoringExpected) {
            assert refactoredElements.size() > 0;
            assert refactoredElements.contains(element);
            for (Element ele : model.getElements()) {
                // Make sure that the number of properties of each element in the model has not changed
                assert propertiesBefore.get(ele).size() == ele.getProperties().size();
            }
            // Assert that the name change was applied correctly to the element
            assert !element.getProperties().contains(oldNameProperty);
            assert element.getProperties().contains("n_" + name);
            assert element.getLabel().equals(name);
        } else {
            // Assert that no name change was applied
            assert refactoredElements.size() == 0;
            assert element.getLabel().equals(oldName);
            assert element.getProperties().contains(oldNameProperty);
            // Make sure that no properties of other elements changed
            for (Element ele : model.getElements()) {
                List<String> props = propertiesBefore.get(ele);
                assert props.equals(ele.getProperties());
            }
        }
        // Assert the properties of the refactored elements based on the test case in more detail
        return refactoredElements;
    }

    @Test
    void moveProperty_validMove_RefactorSourceAndTarget() {
        LinkedList<String> propsOfSource = new LinkedList<>(Arrays.asList("prop_1", "prop_2", "n_SourceElement"));
        Element source = new Element("-1", "SourceElement", propsOfSource, "0");

        LinkedList<String> propsOfTarget = new LinkedList<>(Arrays.asList("prop_1", "n_TargetElement"));
        Element target = new Element("-1", "TargetElement", propsOfTarget, "0");

        String propertyName = "prop_2";

        testMoveProperty(source, target, propertyName, true);
    }

    @Test
    void moveProperty_validPropertyInSourceButNamePropertyInTarget_NoRefactoring() {
        LinkedList<String> propsOfSource = new LinkedList<>(Arrays.asList("prop_1", "prop_2", "n_SourceElement",
                "n_TargetElement"));
        Element source = new Element("-1", "SourceElement", propsOfSource, "0");

        LinkedList<String> propsOfTarget = new LinkedList<>(Arrays.asList("prop_1", "n_TargetElement"));
        Element target = new Element("-1", "TargetElement", propsOfTarget, "0");

        String propertyName = "n_TargetElement";

        testMoveProperty(source, target, propertyName, false);
    }

    @Test
    void moveProperty_validPropertyInSourceButAlreadyExistingInTarget_NoRefactoring() {
        LinkedList<String> propsOfSource = new LinkedList<>(Arrays.asList("prop_1", "prop_2", "n_SourceElement"));
        Element source = new Element("-1", "SourceElement", propsOfSource, "0");

        LinkedList<String> propsOfTarget = new LinkedList<>(Arrays.asList("prop_1", "n_TargetElement"));
        Element target = new Element("-1", "TargetElement", propsOfTarget, "0");

        String propertyName = "prop_1";

        testMoveProperty(source, target, propertyName, false);
    }

    @Test
    void moveProperty_namePropertyInSourceAndNotExistingInTarget_NoRefactoring() {
        LinkedList<String> propsOfSource = new LinkedList<>(Arrays.asList("prop_1", "prop_2", "n_SourceElement"));
        Element source = new Element("-1", "SourceElement", propsOfSource, "0");

        LinkedList<String> propsOfTarget = new LinkedList<>(Arrays.asList("prop_1", "n_TargetElement"));
        Element target = new Element("-1", "TargetElement", propsOfTarget, "0");

        String propertyName = "n_SourceElement";

        testMoveProperty(source, target, propertyName, false);
    }

    @Test
    void moveProperty_notExistingInSourceAndNotExistingInTarget_NoRefactoring() {
        LinkedList<String> propsOfSource = new LinkedList<>(Arrays.asList("prop_1", "prop_2", "n_SourceElement"));
        Element source = new Element("-1", "SourceElement", propsOfSource, "0");

        LinkedList<String> propsOfTarget = new LinkedList<>(Arrays.asList("prop_1", "n_TargetElement"));
        Element target = new Element("-1", "TargetElement", propsOfTarget, "0");

        String propertyName = "not_existing";

        testMoveProperty(source, target, propertyName, false);
    }

    @Test
    void moveProperty_validPropertyButModelsOfElementsDiffer_Exception() {
        LinkedList<String> propsOfSource = new LinkedList<>(Arrays.asList("prop_1", "prop_2", "n_SourceElement"));
        Element source = new Element("-1", "SourceElement", propsOfSource, "0");

        LinkedList<String> propsOfTarget = new LinkedList<>(Arrays.asList("prop_1", "n_TargetElement"));
        Element target = new Element("-1", "TargetElement", propsOfTarget, "1");

        String propertyName = "prop_2";

        Assertions.assertThrows(IllegalArgumentException.class,
                () -> testMoveProperty(source, target, propertyName, true));
    }

    private void testMoveProperty(Element source, Element target, String propertyName, boolean refactoringExpected) {
        List<String> sourcePropertiesBefore = new LinkedList<>(source.getProperties());
        List<String> targetPropertiesBefore = new LinkedList<>(target.getProperties());

        MovePropertyTarget refactoringTarget = new MovePropertyTarget(source, target, propertyName);
        Set<Element> refactoredElements = Refactoring.moveProperty(refactoringTarget);

        if (refactoringExpected) {
            assert refactoredElements.size() == 2;
            assert refactoredElements.contains(source);
            assert refactoredElements.contains(target);
            assert !source.getProperties().contains(propertyName);
            assert target.getProperties().contains(propertyName);
            assert (source.getProperties().size() + 1) == sourcePropertiesBefore.size();
            assert (target.getProperties().size() - 1) == targetPropertiesBefore.size();
        } else {
            assert refactoredElements.isEmpty();
            assert source.getProperties().equals(sourcePropertiesBefore);
            assert target.getProperties().equals(targetPropertiesBefore);
        }
    }

    @Test
    void extractInterface_validArgumentsWithOnlyOneCommonPropertyAndMove_ElementsRefactored() {
        LinkedList<String> props1 = new LinkedList<>(Arrays.asList("prop_1", "prop_2", "n_eleA"));
        Element elementA = new Element("-1", "EleA", props1, "0");

        LinkedList<String> props2 = new LinkedList<>(Arrays.asList("prop_1", "prop_3", "n_eleB"));
        Element elementB = new Element("-1", "EleB", props2, "0");

        LinkedList<String> props3 = new LinkedList<>(Arrays.asList("prop_1", "prop_4", "n_eleC"));
        Element elementC = new Element("-1", "EleC", props3, "0");

        Model model = new Model("0");
        model.addElement(elementA);
        model.addElement(elementB);
        model.addElement(elementC);

        Set<Element> elements = new HashSet<>(model.getElements());
        List<String> refactoredProperties = new LinkedList<>();
        refactoredProperties.add("prop_1");
        testExtractInterface(elements, model, "FancyInterface",
                true, true, refactoredProperties);
    }

    @Test
    void extractInterface_validArgumentsWithOnlyOneCommonPropertyAndCopy_OnlyNewInterface() {
        LinkedList<String> props1 = new LinkedList<>(Arrays.asList("prop_1", "prop_2", "n_eleA"));
        Element elementA = new Element("-1", "EleA", props1, "0");

        LinkedList<String> props2 = new LinkedList<>(Arrays.asList("prop_1", "prop_3", "n_eleB"));
        Element elementB = new Element("-1", "EleB", props2, "0");

        LinkedList<String> props3 = new LinkedList<>(Arrays.asList("prop_1", "prop_4", "n_eleC"));
        Element elementC = new Element("-1", "EleC", props3, "0");

        Model model = new Model("0");
        model.addElement(elementA);
        model.addElement(elementB);
        model.addElement(elementC);

        Set<Element> elements = new HashSet<>(model.getElements());
        List<String> refactoredProperties = new LinkedList<>();
        refactoredProperties.add("prop_1");
        testExtractInterface(elements, model, "FancyInterface",
                false, true, refactoredProperties);
    }

    @Test
    void extractInterface_validArgumentsWithSeveralCommonPropertiesAndMove_ElementsRefactored() {
        LinkedList<String> props1 = new LinkedList<>(Arrays.asList("prop_1", "prop_2", "other", "n_eleA"));
        Element elementA = new Element("-1", "EleA", props1, "0");

        LinkedList<String> props2 = new LinkedList<>(Arrays.asList("prop_1", "prop_2", "prop", "n_eleB"));
        Element elementB = new Element("-1", "EleB", props2, "0");

        LinkedList<String> props3 = new LinkedList<>(Arrays.asList("prop_1", "prop_2", "other", "n_eleC"));
        Element elementC = new Element("-1", "EleC", props3, "0");

        Model model = new Model("0");
        model.addElement(elementA);
        model.addElement(elementB);
        model.addElement(elementC);

        Set<Element> elements = new HashSet<>(model.getElements());
        List<String> refactoredProperties = new LinkedList<>();
        refactoredProperties.add("prop_1");
        refactoredProperties.add("prop_2");
        testExtractInterface(elements, model, "FancyInterface",
                true, true, refactoredProperties);
    }

    @Test
    void extractInterface_validArgumentsWithSeveralCommonPropertiesAndCopy_OnlyNewInterface() {
        LinkedList<String> props1 = new LinkedList<>(Arrays.asList("prop_1", "prop_2", "other", "n_eleA"));
        Element elementA = new Element("-1", "EleA", props1, "0");

        LinkedList<String> props2 = new LinkedList<>(Arrays.asList("prop_1", "prop_2", "prop", "n_eleB"));
        Element elementB = new Element("-1", "EleB", props2, "0");

        LinkedList<String> props3 = new LinkedList<>(Arrays.asList("prop_1", "prop_2", "other", "n_eleC"));
        Element elementC = new Element("-1", "EleC", props3, "0");

        Model model = new Model("0");
        model.addElement(elementA);
        model.addElement(elementB);
        model.addElement(elementC);

        Set<Element> elements = new HashSet<>(model.getElements());
        List<String> refactoredProperties = new LinkedList<>();
        refactoredProperties.add("prop_1");
        refactoredProperties.add("prop_2");
        testExtractInterface(elements, model, "FancyInterface",
                false, true, refactoredProperties);
    }

    @Test
    void extractInterface_nameOfInterfaceAlreadyExists_NoRefactoring() {
        LinkedList<String> props1 = new LinkedList<>(Arrays.asList("prop_1", "prop_2", "n_eleA"));
        Element elementA = new Element("-1", "EleA", props1, "0");

        LinkedList<String> props2 = new LinkedList<>(Arrays.asList("prop_1", "prop_3", "n_eleB"));
        Element elementB = new Element("-1", "EleB", props2, "0");

        LinkedList<String> props3 = new LinkedList<>(Arrays.asList("prop_1", "prop_4", "n_fancyInterface"));
        Element elementC = new Element("-1", "FancyInterface", props3, "0");

        Model model = new Model("0");
        model.addElement(elementA);
        model.addElement(elementB);
        model.addElement(elementC);

        Set<Element> elements = new HashSet<>();
        elements.add(elementA);
        elements.add(elementB);
        List<String> refactoredProperties = new LinkedList<>();
        testExtractInterface(elements, model, "FancyInterface",
                true, false, refactoredProperties);
    }

    @Test
    void extractInterface_modelDoesNotContainAllSourceElements_Exception() {
        LinkedList<String> props1 = new LinkedList<>(Arrays.asList("prop_1", "prop_2", "n_eleA"));
        Element elementA = new Element("-1", "EleA", props1, "0");

        LinkedList<String> props2 = new LinkedList<>(Arrays.asList("prop_1", "prop_3", "n_eleB"));
        Element elementB = new Element("-1", "EleB", props2, "1");

        LinkedList<String> props3 = new LinkedList<>(Arrays.asList("prop_1", "prop_4", "n_eleC"));
        Element elementC = new Element("-1", "EleC", props3, "0");

        Model model = new Model("0");
        model.addElement(elementA);
        model.addElement(elementC);

        Set<Element> elements = new HashSet<>();
        elements.add(elementA);
        elements.add(elementB);
        elements.add(elementC);

        List<String> refactoredProperties = new LinkedList<>();
        refactoredProperties.add("prop_1");
        Assertions.assertThrows(IllegalArgumentException.class, () -> testExtractInterface(elements, model,
                "FancyInterface", false, false, refactoredProperties));
    }

    @Test
    void extractInterface_notAllSourceElementsShareProperties_NoRefactoring() {
        LinkedList<String> props1 = new LinkedList<>(Arrays.asList("prop_1", "prop_2", "n_eleA"));
        Element elementA = new Element("-1", "EleA", props1, "0");

        LinkedList<String> props2 = new LinkedList<>(Arrays.asList("prop_1", "prop_3", "n_eleB"));
        Element elementB = new Element("-1", "EleB", props2, "0");

        LinkedList<String> props3 = new LinkedList<>(Arrays.asList("prop_3", "prop_4", "n_eleC"));
        Element elementC = new Element("-1", "EleC", props3, "0");

        Model model = new Model("0");
        model.addElement(elementA);
        model.addElement(elementB);
        model.addElement(elementC);

        Set<Element> elements = new HashSet<>(model.getElements());
        List<String> refactoredProperties = new LinkedList<>();
        refactoredProperties.add("prop_1");
        testExtractInterface(elements, model, "FancyInterface",
                true, false, refactoredProperties);
    }

    @Test
    void extractInterface_onlyOneSourceElementGiven_IllegalArgument() {
        LinkedList<String> props1 = new LinkedList<>(Arrays.asList("prop_1", "prop_2", "n_eleA"));
        Element elementA = new Element("-1", "EleA", props1, "0");

        LinkedList<String> props2 = new LinkedList<>(Arrays.asList("prop_1", "prop_3", "n_eleB"));
        Element elementB = new Element("-1", "EleB", props2, "0");

        LinkedList<String> props3 = new LinkedList<>(Arrays.asList("prop_1", "prop_4", "n_eleC"));
        Element elementC = new Element("-1", "EleC", props3, "0");

        Model model = new Model("0");
        model.addElement(elementA);
        model.addElement(elementB);
        model.addElement(elementC);

        Set<Element> elements = new HashSet<>();
        elements.add(elementA);
        List<String> refactoredProperties = new LinkedList<>();
        refactoredProperties.add("prop_1");
        Assertions.assertThrows(IllegalArgumentException.class, () -> testExtractInterface(elements, model, "FancyInterface",
                false, false, refactoredProperties));
    }

    @Test
    void extractInterface_noSourceElementGiven_IllegalArgument() {
        LinkedList<String> props1 = new LinkedList<>(Arrays.asList("prop_1", "prop_2", "n_eleA"));
        Element elementA = new Element("-1", "EleA", props1, "0");

        LinkedList<String> props2 = new LinkedList<>(Arrays.asList("prop_1", "prop_3", "n_eleB"));
        Element elementB = new Element("-1", "EleB", props2, "0");

        LinkedList<String> props3 = new LinkedList<>(Arrays.asList("prop_1", "prop_4", "n_eleC"));
        Element elementC = new Element("-1", "EleC", props3, "0");

        Model model = new Model("0");
        model.addElement(elementA);
        model.addElement(elementB);
        model.addElement(elementC);

        Set<Element> elements = new HashSet<>();
        List<String> refactoredProperties = new LinkedList<>();
        refactoredProperties.add("prop_1");
        Assertions.assertThrows(IllegalArgumentException.class, () -> testExtractInterface(elements, model, "CoolInterface",
                true, false, refactoredProperties));
    }

    private void testExtractInterface(Set<Element> sourceElements, Model model, String interfaceName,
                                      boolean moveProperties, boolean refactoringExpected,
                                      List<String> refactoredProperties) {
        Map<Element, List<String>> propertiesBefore = new HashMap<>();
        // Save the properties from before the refactoring
        for (Element element : model.getElements()) {
            propertiesBefore.put(element, new LinkedList<>(element.getProperties()));
        }
        // Save the elements in the model from before the refactoring
        List<Element> elementsInModelBefore = new ArrayList<>(model.getElements());

        // Apply the refactoring
        ExtractInterfaceTarget target = new ExtractInterfaceTarget(sourceElements, model);
        Set<Element> refactoredElements = Refactoring.extractInterface(target, interfaceName, moveProperties);

        if (refactoringExpected) {
            assert model.size() == elementsInModelBefore.size() + 1;
            for (Element refactored : refactoredElements) {
                if (!sourceElements.contains(refactored)) {
                    // Assert that the new interface has been created correctly and that it is in the model
                    assert model.getElements().contains(refactored);
                    assert refactored.getModelId().equals(model.getId());
                    assert refactored.getLabel().equals(interfaceName);
                    assert refactored.getProperties()
                            .subList(1, refactored.getProperties().size())
                            .equals(refactoredProperties);
                }
            }
            if (moveProperties) {
                assert refactoredElements.size() == sourceElements.size() + 1;
                for (Element source : sourceElements) {
                    // Assert that all source elements have been refactored accordingly
                    assert refactoredElements.contains(source);
                    assert source.getProperties().size() == propertiesBefore.get(source).size() - refactoredProperties.size();
                    for (String prop : refactoredProperties) {
                        assert !source.getProperties().contains(prop);
                    }
                }
            } else {
                assert refactoredElements.size() == 1;
                for (Element source : sourceElements) {
                    // Assert that properties have only been copied, not moved
                    assert !refactoredElements.contains(source);
                    assert source.getProperties().equals(propertiesBefore.get(source));
                }
            }
        } else {
            // Assert that nothing changed
            assert refactoredElements.isEmpty();
            assert elementsInModelBefore.equals(model.getElements());
            for (Element element : model.getElements()) {
                assert propertiesBefore.get(element).equals(element.getProperties());
            }
        }
    }
}
