package de.hub.mse.variantsync.variantdrift.refactoring.targets;

import de.hub.mse.variantsync.variantdrift.experiments.algorithms.nwm.domain.Element;
import de.hub.mse.variantsync.variantdrift.experiments.algorithms.nwm.domain.Model;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

public class ExtractInterfaceTargetTest {
    private Model model;

    @BeforeEach
    void initEmptyModel() {
        this.model = new Model("TestModel");
    }

    @Test
    void findAllTargets_ForTwoElementsWithOneCommonProperty_FindOne() {
        LinkedList<String> propertiesA = new LinkedList<>(Arrays.asList("prop1", "prop2", "n_elementA"));
        LinkedList<String> propertiesB = new LinkedList<>(Arrays.asList("prop1", "prop4", "n_elementB"));
        LinkedList<String> propertiesC = new LinkedList<>(Arrays.asList("prop5", "prop6", "n_elementC"));
        Element elementA = new Element("0", "ElementA", propertiesA, "TestModel");
        Element elementB = new Element("1", "ElementB", propertiesB, "TestModel");
        Element elementC = new Element("2", "ElementC", propertiesC, "TestModel");

        model.addElement(elementA);
        model.addElement(elementB);
        model.addElement(elementC);

        Set<ExtractInterfaceTarget> foundTargets = ExtractInterfaceTarget.findAllTargets(model);
        assert foundTargets.size() == 1;
        for(ExtractInterfaceTarget target : foundTargets) {
            assert target.sourceElements.size() == 2;
            assert target.sourceElements.contains(elementA);
            assert target.sourceElements.contains(elementB);
            assert target.getCommonProperties().size() == 1;
            assert target.getCommonProperties().contains("prop1");
        }
    }

    @Test
    void findAllTargets_ForOneElementOnly_NoneFound() {
        LinkedList<String> propertiesA = new LinkedList<>(Arrays.asList("prop1", "prop2", "n_elementA"));
        Element elementA = new Element("0", "ElementA", propertiesA, "TestModel");

        model.addElement(elementA);

        Set<ExtractInterfaceTarget> foundTargets = ExtractInterfaceTarget.findAllTargets(model);
        assert foundTargets.isEmpty();
    }

    @Test
    void findAllTargets_ForThreeElementsWithoutCommonProperty_FindNone() {
        LinkedList<String> propertiesA = new LinkedList<>(Arrays.asList("prop1", "prop2", "n_elementA"));
        LinkedList<String> propertiesB = new LinkedList<>(Arrays.asList("prop3", "prop4", "n_elementA"));
        LinkedList<String> propertiesC = new LinkedList<>(Arrays.asList("prop5", "prop6", "n_elementC"));
        Element elementA = new Element("0", "ElementA", propertiesA, "TestModel");
        Element elementB = new Element("1", "ElementA", propertiesB, "TestModel");
        Element elementC = new Element("2", "ElementC", propertiesC, "TestModel");

        model.addElement(elementA);
        model.addElement(elementB);
        model.addElement(elementC);

        Set<ExtractInterfaceTarget> foundTargets = ExtractInterfaceTarget.findAllTargets(model);
        assert foundTargets.isEmpty();
    }

    @Test
    void findAllTargets_ForThreeElementsWithOneCommonProperty_FindOne() {
        LinkedList<String> propertiesA = new LinkedList<>(Arrays.asList("prop1", "prop2", "n_elementA"));
        LinkedList<String> propertiesB = new LinkedList<>(Arrays.asList("prop1", "prop4", "n_elementB"));
        LinkedList<String> propertiesC = new LinkedList<>(Arrays.asList("prop1", "prop6", "n_elementC"));
        Element elementA = new Element("0", "ElementA", propertiesA, "TestModel");
        Element elementB = new Element("1", "ElementB", propertiesB, "TestModel");
        Element elementC = new Element("2", "ElementC", propertiesC, "TestModel");

        model.addElement(elementA);
        model.addElement(elementB);
        model.addElement(elementC);

        Set<ExtractInterfaceTarget> foundTargets = ExtractInterfaceTarget.findAllTargets(model);
        assert foundTargets.size() == 1;
        for(ExtractInterfaceTarget target : foundTargets) {
            assert target.sourceElements.size() == 3;
            assert target.sourceElements.contains(elementA);
            assert target.sourceElements.contains(elementB);
            assert target.sourceElements.contains(elementC);
            assert target.getCommonProperties().size() == 1;
            assert target.getCommonProperties().contains("prop1");
        }
    }

    @Test
    void findAllTargets_ForThreeElementsWithSeveralCommonProperties_FindOne() {
        LinkedList<String> propertiesA = new LinkedList<>(Arrays.asList("prop1", "prop2", "n_elementA"));
        LinkedList<String> propertiesB = new LinkedList<>(Arrays.asList("prop1", "prop2", "n_elementB"));
        LinkedList<String> propertiesC = new LinkedList<>(Arrays.asList("prop1", "prop2", "n_elementC"));
        Element elementA = new Element("0", "ElementA", propertiesA, "TestModel");
        Element elementB = new Element("1", "ElementB", propertiesB, "TestModel");
        Element elementC = new Element("2", "ElementC", propertiesC, "TestModel");

        model.addElement(elementA);
        model.addElement(elementB);
        model.addElement(elementC);

        Set<ExtractInterfaceTarget> foundTargets = ExtractInterfaceTarget.findAllTargets(model);
        assert foundTargets.size() == 1;
        for(ExtractInterfaceTarget target : foundTargets) {
            assert target.sourceElements.size() == 3;
            assert target.sourceElements.contains(elementA);
            assert target.sourceElements.contains(elementB);
            assert target.sourceElements.contains(elementC);
            assert target.getCommonProperties().size() == 2;
            assert target.getCommonProperties().contains("prop1");
            assert target.getCommonProperties().contains("prop2");
        }
    }

    @Test
    void findAllTargets_ForSeveralElementsWithPartlyCommonProperties_FindSeveral() {
        LinkedList<String> propertiesA = new LinkedList<>(Arrays.asList("prop1", "prop2", "n_elementA"));
        LinkedList<String> propertiesB = new LinkedList<>(Arrays.asList("prop1", "prop2", "n_elementB"));
        LinkedList<String> propertiesC = new LinkedList<>(Arrays.asList("prop5", "prop6", "n_elementC"));
        LinkedList<String> propertiesD = new LinkedList<>(Arrays.asList("prop5", "prop7", "n_elementD"));
        LinkedList<String> propertiesE = new LinkedList<>(Arrays.asList("prop5", "prop6", "prop1", "n_elementE"));
        Element elementA = new Element("0", "ElementA", propertiesA, "TestModel");
        Element elementB = new Element("1", "ElementB", propertiesB, "TestModel");
        Element elementC = new Element("2", "ElementC", propertiesC, "TestModel");
        Element elementD = new Element("3", "ElementD", propertiesD, "TestModel");
        Element elementE = new Element("4", "ElementE", propertiesE, "TestModel");

        model.addElement(elementA);
        model.addElement(elementB);
        model.addElement(elementC);
        model.addElement(elementD);
        model.addElement(elementE);

        Set<ExtractInterfaceTarget> foundTargets = ExtractInterfaceTarget.findAllTargets(model);
        assert foundTargets.size() == 4;
        var expected1 = new ExtractInterfaceTarget(new HashSet<>(Arrays.asList(elementA, elementB)), null);
        var expected2 = new ExtractInterfaceTarget(new HashSet<>(Arrays.asList(elementA, elementB, elementE)), null);
        var expected3 = new ExtractInterfaceTarget(new HashSet<>(Arrays.asList(elementC, elementD, elementE)), null);
        var expected4 = new ExtractInterfaceTarget(new HashSet<>(Arrays.asList(elementC, elementE)), null);

        assert foundTargets.contains(expected1);
        assert foundTargets.contains(expected2);
        assert foundTargets.contains(expected3);
        assert foundTargets.contains(expected4);
    }

    @Test
    void equals_SameSourceElements_true() {
        LinkedList<String> propertiesA = new LinkedList<>(Arrays.asList("prop1", "prop2", "n_elementA"));
        LinkedList<String> propertiesB = new LinkedList<>(Arrays.asList("prop1", "prop2", "n_elementB"));
        LinkedList<String> propertiesC = new LinkedList<>(Arrays.asList("prop1", "prop6", "n_elementC"));
        Element elementA = new Element("0", "ElementA", propertiesA, "TestModel");
        Element elementB = new Element("1", "ElementB", propertiesB, "TestModel");
        Element elementC = new Element("2", "ElementC", propertiesC, "TestModel");

        var firstTarget = new ExtractInterfaceTarget(new HashSet<>(Arrays.asList(elementA, elementB, elementC)), null);
        var secondTarget = new ExtractInterfaceTarget(new HashSet<>(Arrays.asList(elementA, elementB, elementC)), null);

        assert firstTarget.equals(secondTarget);
    }

    @Test
    void equals_DifferentSourceElements_false() {
        LinkedList<String> propertiesA = new LinkedList<>(Arrays.asList("prop1", "prop2", "n_elementA"));
        LinkedList<String> propertiesB = new LinkedList<>(Arrays.asList("prop1", "prop2", "n_elementB"));
        LinkedList<String> propertiesC = new LinkedList<>(Arrays.asList("prop1", "prop6", "n_elementC"));
        Element elementA = new Element("0", "ElementA", propertiesA, "TestModel");
        Element elementB = new Element("1", "ElementB", propertiesB, "TestModel");
        Element elementC = new Element("2", "ElementC", propertiesC, "TestModel");

        var firstTarget = new ExtractInterfaceTarget(new HashSet<>(Arrays.asList(elementA, elementB, elementC)), null);
        var secondTarget = new ExtractInterfaceTarget(new HashSet<>(Arrays.asList(elementA, elementB)), null);

        assert !firstTarget.equals(secondTarget);
    }

    @Test
    void overlapsWith_CommonSourceElementsAndProperties_true() {
        LinkedList<String> propertiesA = new LinkedList<>(Arrays.asList("prop1", "prop3", "n_elementA"));
        LinkedList<String> propertiesB = new LinkedList<>(Arrays.asList("prop1", "prop3", "n_elementB"));
        LinkedList<String> propertiesC = new LinkedList<>(Arrays.asList("prop1", "prop3", "prop4", "n_elementC"));
        LinkedList<String> propertiesD = new LinkedList<>(Arrays.asList("prop3", "prop4", "n_elementD"));
        LinkedList<String> propertiesE = new LinkedList<>(Arrays.asList("prop3", "prop4", "prop1", "n_elementE"));
        Element elementA = new Element("0", "ElementA", propertiesA, "TestModel");
        Element elementB = new Element("1", "ElementB", propertiesB, "TestModel");
        Element elementC = new Element("2", "ElementC", propertiesC, "TestModel");
        Element elementD = new Element("3", "ElementD", propertiesD, "TestModel");
        Element elementE = new Element("4", "ElementE", propertiesE, "TestModel");

        model.addElement(elementA);
        model.addElement(elementB);
        model.addElement(elementC);
        model.addElement(elementD);
        model.addElement(elementE);

        var target1 = new ExtractInterfaceTarget(new HashSet<>(Arrays.asList(elementA, elementB, elementC)), null);
        var target2 = new ExtractInterfaceTarget(new HashSet<>(Arrays.asList(elementC, elementD, elementE)), null);

        assert target1.overlapsWith(target2);
    }

    @Test
    void overlapsWith_CommonSourceElementsDifferentProperties_false() {
        LinkedList<String> propertiesA = new LinkedList<>(Arrays.asList("prop1", "prop2", "n_elementA"));
        LinkedList<String> propertiesB = new LinkedList<>(Arrays.asList("prop1", "prop2", "n_elementB"));
        LinkedList<String> propertiesC = new LinkedList<>(Arrays.asList("prop1", "prop2", "prop3", "n_elementC"));
        LinkedList<String> propertiesD = new LinkedList<>(Arrays.asList("prop3", "prop4", "n_elementD"));
        LinkedList<String> propertiesE = new LinkedList<>(Arrays.asList("prop3", "prop4", "prop1", "n_elementE"));
        Element elementA = new Element("0", "ElementA", propertiesA, "TestModel");
        Element elementB = new Element("1", "ElementB", propertiesB, "TestModel");
        Element elementC = new Element("2", "ElementC", propertiesC, "TestModel");
        Element elementD = new Element("3", "ElementD", propertiesD, "TestModel");
        Element elementE = new Element("4", "ElementE", propertiesE, "TestModel");

        model.addElement(elementA);
        model.addElement(elementB);
        model.addElement(elementC);
        model.addElement(elementD);
        model.addElement(elementE);

        var target1 = new ExtractInterfaceTarget(new HashSet<>(Arrays.asList(elementA, elementB, elementC)), null);
        var target2 = new ExtractInterfaceTarget(new HashSet<>(Arrays.asList(elementC, elementD, elementE)), null);

        assert !target1.overlapsWith(target2);
    }

    @Test
    void overlapsWith_DifferentSourceElementsButCommonProperties_true() {
        LinkedList<String> propertiesA = new LinkedList<>(Arrays.asList("prop1", "prop3", "n_elementA"));
        LinkedList<String> propertiesB = new LinkedList<>(Arrays.asList("prop1", "prop3", "n_elementB"));
        LinkedList<String> propertiesD = new LinkedList<>(Arrays.asList("prop3", "prop4", "n_elementD"));
        LinkedList<String> propertiesE = new LinkedList<>(Arrays.asList("prop3", "prop4", "prop1", "n_elementE"));
        Element elementA = new Element("0", "ElementA", propertiesA, "TestModel");
        Element elementB = new Element("1", "ElementB", propertiesB, "TestModel");
        Element elementD = new Element("3", "ElementD", propertiesD, "TestModel");
        Element elementE = new Element("4", "ElementE", propertiesE, "TestModel");

        model.addElement(elementA);
        model.addElement(elementB);
        model.addElement(elementD);
        model.addElement(elementE);

        var target1 = new ExtractInterfaceTarget(new HashSet<>(Arrays.asList(elementA, elementB)), null);
        var target2 = new ExtractInterfaceTarget(new HashSet<>(Arrays.asList(elementD, elementE)), null);

        assert !target1.overlapsWith(target2);
    }
}
