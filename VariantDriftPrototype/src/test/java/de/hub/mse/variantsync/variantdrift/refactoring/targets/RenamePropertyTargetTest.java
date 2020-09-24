package de.hub.mse.variantsync.variantdrift.refactoring.targets;

import de.hub.mse.variantsync.variantdrift.experiments.algorithms.nwm.domain.Element;
import de.hub.mse.variantsync.variantdrift.experiments.algorithms.nwm.domain.Model;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

public class RenamePropertyTargetTest {
    private Model model;

    @BeforeEach
    void initEmptyModel() {
        this.model = new Model("TestModel");
    }

    @Test
    void findAllTargets_ForSingleElementAndSingleValidProperty_AllFound() {
        LinkedList<String> properties = new LinkedList<>(Arrays.asList("prop1", "n_testElement"));
        Element element = new Element("0", "TestElement", properties, "TestModel");

        model.addElement(element);

        Set<RenamePropertyTarget> foundTargets = RenamePropertyTarget.findAllTargets(model);
        assert foundTargets.size() == 1;
        for (var target : foundTargets) {
            assert target.element == element;
            assert target.property.equals("prop1");
        }
    }

    @Test
    void findAllTargets_ForSingleElementAndSeveralProperties_AllFound() {
        LinkedList<String> properties = new LinkedList<>(Arrays.asList("prop1", "prop2", "bla", "n_testElement"));
        Element element = new Element("0", "TestElement", properties, "TestModel");

        model.addElement(element);

        Set<RenamePropertyTarget> foundTargets = RenamePropertyTarget.findAllTargets(model);
        assert foundTargets.size() == 3;
        List<String> targetProperties = new LinkedList<>();
        for (var target : foundTargets) {
            assert target.element == element;
            targetProperties.add(target.property);
        }
        // Check whether all valid properties have been found
        for (String property : properties) {
            assert property.startsWith("n_") || targetProperties.contains(property);
        }
    }

    @Test
    void findAllTargets_ForSeveralElementAndSeveralProperties_AllFound() {
        LinkedList<String> propertiesA = new LinkedList<>(Arrays.asList("prop1", "prop2", "n_elementA"));
        LinkedList<String> propertiesB = new LinkedList<>(Arrays.asList("prop3", "prop4", "n_elementB"));
        LinkedList<String> propertiesC = new LinkedList<>(Arrays.asList("prop1", "prop3", "n_elementC"));
        Element elementA = new Element("0", "ElementA", propertiesA, "TestModel");
        Element elementB = new Element("1", "ElementB", propertiesB, "TestModel");
        Element elementC = new Element("2", "ElementC", propertiesC, "TestModel");

        model.addElement(elementA);
        model.addElement(elementB);
        model.addElement(elementC);

        Set<RenamePropertyTarget> foundTargets = RenamePropertyTarget.findAllTargets(model);
        assert foundTargets.size() == 6;
        Set<Element> targetElements = new HashSet<>();
        Set<String> targetProperties = new HashSet<>();
        for (var target : foundTargets) {
            targetElements.add(target.element);
            targetProperties.add(target.property);
        }
        // Three unique elements
        assert targetElements.size() == 3;
        // Four unique properties
        assert targetProperties.size() == 4;

        assert targetElements.contains(elementA);
        assert targetElements.contains(elementB);
        assert targetElements.contains(elementC);
        assert targetProperties.contains("prop1");
        assert targetProperties.contains("prop2");
        assert targetProperties.contains("prop3");
        assert targetProperties.contains("prop4");
    }

    @Test
    void findAllTargets_ForSeveralElementAndOnlyNameProperties_NoneFound() {
        LinkedList<String> propertiesA = new LinkedList<>(Collections.singletonList("n_elementA"));
        LinkedList<String> propertiesB = new LinkedList<>(Collections.singletonList("n_elementB"));
        LinkedList<String> propertiesC = new LinkedList<>(Collections.singletonList("n_elementC"));
        Element elementA = new Element("0", "ElementA", propertiesA, "TestModel");
        Element elementB = new Element("1", "ElementB", propertiesB, "TestModel");
        Element elementC = new Element("2", "ElementC", propertiesC, "TestModel");

        model.addElement(elementA);
        model.addElement(elementB);
        model.addElement(elementC);

        Set<RenamePropertyTarget> foundTargets = RenamePropertyTarget.findAllTargets(model);
        assert foundTargets.isEmpty();
    }

    @Test
    void equals_TargetsWithSameElementAndSameProperty_true() {
        LinkedList<String> propertiesA = new LinkedList<>(Arrays.asList("prop1", "prop2", "n_elementA"));
        Element elementA = new Element("0", "ElementA", propertiesA, "TestModel");

        RenamePropertyTarget targetOne = new RenamePropertyTarget(elementA, "prop1");
        RenamePropertyTarget targetTwo = new RenamePropertyTarget(elementA, "prop1");

        assert targetOne.equals(targetTwo);
        assert targetOne.hashCode() == targetTwo.hashCode();
    }

    @Test
    void equals_TargetsWithSimilarElementsFromDifferentModels_false() {
        LinkedList<String> propertiesA = new LinkedList<>(Arrays.asList("prop1", "prop2", "n_elementA"));
        Element elementA = new Element("0", "ElementA", propertiesA, "FirstModel");
        Element elementB = new Element("0", "ElementA", propertiesA, "SecondModel");

        RenamePropertyTarget targetOne = new RenamePropertyTarget(elementA, "prop1");
        RenamePropertyTarget targetTwo = new RenamePropertyTarget(elementB, "prop1");

        assert !targetOne.equals(targetTwo);
        assert targetOne.hashCode() != targetTwo.hashCode();
    }

    @Test
    void equals_TargetsWithDifferentElementAndSameProperty_false() {
        LinkedList<String> propertiesA = new LinkedList<>(Arrays.asList("prop1", "prop2", "n_elementA"));
        LinkedList<String> propertiesB = new LinkedList<>(Arrays.asList("prop1", "n_elementA"));
        Element elementA = new Element("0", "ElementA", propertiesA, "TestModel");
        Element elementB = new Element("0", "ElementB", propertiesB, "TestModel");

        RenamePropertyTarget targetOne = new RenamePropertyTarget(elementA, "prop1");
        RenamePropertyTarget targetTwo = new RenamePropertyTarget(elementB, "prop1");

        assert !targetOne.equals(targetTwo);
        assert targetOne.hashCode() != targetTwo.hashCode();
    }

    @Test
    void equals_TargetsWithSameElementAndDifferentProperty_false() {
        LinkedList<String> propertiesA = new LinkedList<>(Arrays.asList("prop1", "prop2", "n_elementA"));
        Element elementA = new Element("0", "ElementA", propertiesA, "TestModel");

        RenamePropertyTarget targetOne = new RenamePropertyTarget(elementA, "prop1");
        RenamePropertyTarget targetTwo = new RenamePropertyTarget(elementA, "prop2");

        assert !targetOne.equals(targetTwo);
        assert targetOne.hashCode() != targetTwo.hashCode();
    }

}
