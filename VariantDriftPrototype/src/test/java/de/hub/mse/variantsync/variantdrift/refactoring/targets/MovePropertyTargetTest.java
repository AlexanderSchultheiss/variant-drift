package de.hub.mse.variantsync.variantdrift.refactoring.targets;

import de.hub.mse.variantsync.variantdrift.experiments.algorithms.nwm.domain.Element;
import de.hub.mse.variantsync.variantdrift.experiments.algorithms.nwm.domain.Model;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

public class MovePropertyTargetTest {
    private Model model;

    @BeforeEach
    void initEmptyModel() {
        this.model = new Model("TestModel");
    }

    @Test
    void findAllTargets_ForTwoElementsWithOnlyMovableProperties_FindAll() {
        LinkedList<String> propertiesA = new LinkedList<>(Arrays.asList("prop1", "prop2", "n_elementA"));
        LinkedList<String> propertiesB = new LinkedList<>(Arrays.asList("prop3", "prop4", "n_elementB"));
        Element elementA = new Element("0", "ElementA", propertiesA, "TestModel");
        Element elementB = new Element("1", "ElementB", propertiesB, "TestModel");

        model.addElement(elementA);
        model.addElement(elementB);

        Set<MovePropertyTarget> foundTargets = MovePropertyTarget.findAllTargets(model);
        assert foundTargets.size() == 4;

        for(var target : foundTargets) {
            if (target.sourceElement == elementA) {
                assert target.targetElement == elementB;
                assert target.property.equals("prop1") || target.property.equals("prop2");
            } else if(target.sourceElement == elementB) {
                assert target.targetElement == elementA;
                assert target.property.equals("prop3") || target.property.equals("prop4");
            } else {
                throw new AssertionError();
            }
        }
    }

    @Test
    void findAllTargets_ForTwoElementsWithSomeMovableProperties_FindSome() {
        LinkedList<String> propertiesA = new LinkedList<>(Arrays.asList("prop1", "prop2", "n_elementA"));
        LinkedList<String> propertiesB = new LinkedList<>(Arrays.asList("prop2", "prop4", "n_elementB"));
        Element elementA = new Element("0", "ElementA", propertiesA, "TestModel");
        Element elementB = new Element("1", "ElementB", propertiesB, "TestModel");

        model.addElement(elementA);
        model.addElement(elementB);

        Set<MovePropertyTarget> foundTargets = MovePropertyTarget.findAllTargets(model);
        assert foundTargets.size() == 2;

        for(var target : foundTargets) {
            if (target.sourceElement == elementA) {
                assert target.targetElement == elementB;
                assert target.property.equals("prop1");
            } else if(target.sourceElement == elementB) {
                assert target.targetElement == elementA;
                assert target.property.equals("prop4");
            } else {
                throw new AssertionError();
            }
        }
    }

    @Test
    void findAllTargets_ForThreeElementsWithSomeMovableProperties_FindSome() {
        LinkedList<String> propertiesA = new LinkedList<>(Arrays.asList("prop1", "prop2", "n_elementA"));
        LinkedList<String> propertiesB = new LinkedList<>(Arrays.asList("prop3", "prop4", "n_elementB"));
        LinkedList<String> propertiesC = new LinkedList<>(Arrays.asList("prop1", "prop3", "n_elementC"));
        Element elementA = new Element("0", "ElementA", propertiesA, "TestModel");
        Element elementB = new Element("1", "ElementB", propertiesB, "TestModel");
        Element elementC = new Element("2", "ElementC", propertiesC, "TestModel");

        model.addElement(elementA);
        model.addElement(elementB);
        model.addElement(elementC);

        Set<MovePropertyTarget> foundTargets = MovePropertyTarget.findAllTargets(model);
        assert foundTargets.size() == 8;

        for(var target : foundTargets) {
            if (target.sourceElement == elementA) {
                if (target.targetElement == elementB) {
                    assert target.property.equals("prop1") || target.property.equals("prop2");
                } else if(target.targetElement == elementC) {
                    assert target.property.equals("prop2");
                } else {
                    throw new AssertionError();
                }
            } else if(target.sourceElement == elementB) {
                if(target.targetElement == elementA) {
                    assert target.property.equals("prop3") || target.property.equals("prop4");
                } else if(target.targetElement == elementC) {
                    assert target.property.equals("prop4");
                } else {
                    throw new AssertionError();
                }
            } else if(target.sourceElement == elementC){
                if (target.targetElement == elementA) {
                    assert target.property.equals("prop3");
                } else if (target.targetElement == elementB) {
                    assert target.property.equals("prop1");
                } else {
                    throw new AssertionError();
                }
            } else {
                throw new AssertionError();
            }
        }
    }

    @Test
    void findAllTargets_ForOneElementWithMovableProperties_FindNone() {
        LinkedList<String> propertiesA = new LinkedList<>(Arrays.asList("prop1", "prop2", "n_elementA"));
        Element elementA = new Element("0", "ElementA", propertiesA, "TestModel");

        model.addElement(elementA);

        Set<MovePropertyTarget> foundTargets = MovePropertyTarget.findAllTargets(model);
        assert foundTargets.isEmpty();
    }

    @Test
    void findAllTargets_ForTwoElementsWithoutMovableProperties_FindNone() {
        LinkedList<String> propertiesA = new LinkedList<>(Arrays.asList("prop1", "prop2", "n_elementA"));
        LinkedList<String> propertiesB = new LinkedList<>(Arrays.asList("prop2", "prop1", "n_elementB"));
        Element elementA = new Element("0", "ElementA", propertiesA, "TestModel");
        Element elementB = new Element("1", "ElementB", propertiesB, "TestModel");

        model.addElement(elementA);
        model.addElement(elementB);

        Set<MovePropertyTarget> foundTargets = MovePropertyTarget.findAllTargets(model);
        assert foundTargets.isEmpty();
    }

    @Test
    void equals_AllFieldsAreTheSame_true() {
        LinkedList<String> propertiesA = new LinkedList<>(Arrays.asList("prop1", "prop2", "n_elementA"));
        LinkedList<String> propertiesB = new LinkedList<>(Arrays.asList("prop3", "prop4", "n_elementB"));
        Element elementA = new Element("0", "ElementA", propertiesA, "TestModel");
        Element elementB = new Element("1", "ElementB", propertiesB, "TestModel");

        MovePropertyTarget firstTarget = new MovePropertyTarget(elementA, elementB, "prop1");
        MovePropertyTarget secondTarget = new MovePropertyTarget(elementA, elementB, "prop1");

        assert firstTarget.equals(secondTarget);
        assert firstTarget.hashCode() == secondTarget.hashCode();
    }

    @Test
    void equals_SameSourceAndTargetDifferentProperty_false() {
        LinkedList<String> propertiesA = new LinkedList<>(Arrays.asList("prop1", "prop2", "n_elementA"));
        LinkedList<String> propertiesB = new LinkedList<>(Arrays.asList("prop3", "prop4", "n_elementB"));
        Element elementA = new Element("0", "ElementA", propertiesA, "TestModel");
        Element elementB = new Element("1", "ElementB", propertiesB, "TestModel");

        MovePropertyTarget firstTarget = new MovePropertyTarget(elementA, elementB, "prop1");
        MovePropertyTarget secondTarget = new MovePropertyTarget(elementA, elementB, "prop2");

        assert !firstTarget.equals(secondTarget);
        assert firstTarget.hashCode() != secondTarget.hashCode();
    }

    @Test
    void equals_SameSourceDifferentTargetSameProperty_false() {
        LinkedList<String> propertiesA = new LinkedList<>(Arrays.asList("prop1", "prop2", "n_elementA"));
        LinkedList<String> propertiesB = new LinkedList<>(Arrays.asList("prop3", "prop4", "n_elementB"));
        LinkedList<String> propertiesC = new LinkedList<>(Arrays.asList("prop5", "prop6", "n_elementC"));
        Element elementA = new Element("0", "ElementA", propertiesA, "TestModel");
        Element elementB = new Element("1", "ElementB", propertiesB, "TestModel");
        Element elementC = new Element("2", "ElementC", propertiesC, "TestModel");

        MovePropertyTarget firstTarget = new MovePropertyTarget(elementA, elementB, "prop1");
        MovePropertyTarget secondTarget = new MovePropertyTarget(elementA, elementC, "prop1");

        assert !firstTarget.equals(secondTarget);
        assert firstTarget.hashCode() != secondTarget.hashCode();
    }

    @Test
    void equals_DifferentSourceSameTargetSameProperty_false() {
        LinkedList<String> propertiesA = new LinkedList<>(Arrays.asList("prop1", "prop2", "n_elementA"));
        LinkedList<String> propertiesB = new LinkedList<>(Arrays.asList("prop1", "prop4", "n_elementB"));
        LinkedList<String> propertiesC = new LinkedList<>(Arrays.asList("prop5", "prop6", "n_elementC"));
        Element elementA = new Element("0", "ElementA", propertiesA, "TestModel");
        Element elementB = new Element("1", "ElementB", propertiesB, "TestModel");
        Element elementC = new Element("2", "ElementC", propertiesC, "TestModel");

        MovePropertyTarget firstTarget = new MovePropertyTarget(elementA, elementC, "prop1");
        MovePropertyTarget secondTarget = new MovePropertyTarget(elementB, elementC, "prop1");

        assert !firstTarget.equals(secondTarget);
        assert firstTarget.hashCode() != secondTarget.hashCode();
    }

    @Test
    void overlapsWith_SameSourceDifferentTargetSameProperty_true() {
        LinkedList<String> propertiesA = new LinkedList<>(Arrays.asList("prop1", "prop2", "n_elementA"));
        LinkedList<String> propertiesB = new LinkedList<>(Arrays.asList("prop3", "prop4", "n_elementB"));
        LinkedList<String> propertiesC = new LinkedList<>(Arrays.asList("prop5", "prop6", "n_elementC"));
        Element elementA = new Element("0", "ElementA", propertiesA, "TestModel");
        Element elementB = new Element("1", "ElementB", propertiesB, "TestModel");
        Element elementC = new Element("2", "ElementC", propertiesC, "TestModel");

        MovePropertyTarget firstTarget = new MovePropertyTarget(elementA, elementB, "prop1");
        MovePropertyTarget secondTarget = new MovePropertyTarget(elementA, elementC, "prop1");

        assert firstTarget.overlapsWith(secondTarget);
    }

    @Test
    void overlapsWith_DifferentSourceSameTargetSameProperty_true() {
        LinkedList<String> propertiesA = new LinkedList<>(Arrays.asList("prop1", "prop2", "n_elementA"));
        LinkedList<String> propertiesB = new LinkedList<>(Arrays.asList("prop1", "prop4", "n_elementB"));
        LinkedList<String> propertiesC = new LinkedList<>(Arrays.asList("prop5", "prop6", "n_elementC"));
        Element elementA = new Element("0", "ElementA", propertiesA, "TestModel");
        Element elementB = new Element("1", "ElementB", propertiesB, "TestModel");
        Element elementC = new Element("2", "ElementC", propertiesC, "TestModel");

        MovePropertyTarget firstTarget = new MovePropertyTarget(elementA, elementC, "prop1");
        MovePropertyTarget secondTarget = new MovePropertyTarget(elementB, elementC, "prop1");

        assert firstTarget.overlapsWith(secondTarget);
    }

    @Test
    void overlapsWith_DifferentSourceAndTargetSameProperty_false() {
        LinkedList<String> propertiesA = new LinkedList<>(Arrays.asList("prop1", "prop2", "n_elementA"));
        LinkedList<String> propertiesB = new LinkedList<>(Arrays.asList("prop3", "prop4", "n_elementB"));
        LinkedList<String> propertiesC = new LinkedList<>(Arrays.asList("prop1", "prop6", "n_elementC"));
        LinkedList<String> propertiesD = new LinkedList<>(Arrays.asList("prop5", "prop6", "n_elementC"));
        Element elementA = new Element("0", "ElementA", propertiesA, "TestModel");
        Element elementB = new Element("1", "ElementB", propertiesB, "TestModel");
        Element elementC = new Element("2", "ElementC", propertiesC, "TestModel");
        Element elementD = new Element("3", "ElementD", propertiesD, "TestModel");

        MovePropertyTarget firstTarget = new MovePropertyTarget(elementA, elementB, "prop1");
        MovePropertyTarget secondTarget = new MovePropertyTarget(elementC, elementD, "prop1");

        assert !firstTarget.overlapsWith(secondTarget);
    }

    @Test
    void overlapsWith_SameSourceDifferentTargetDifferentProperty_false() {
        LinkedList<String> propertiesA = new LinkedList<>(Arrays.asList("prop1", "prop2", "n_elementA"));
        LinkedList<String> propertiesB = new LinkedList<>(Arrays.asList("prop1", "prop4", "n_elementB"));
        LinkedList<String> propertiesC = new LinkedList<>(Arrays.asList("prop5", "prop6", "n_elementC"));
        Element elementA = new Element("0", "ElementA", propertiesA, "TestModel");
        Element elementB = new Element("1", "ElementB", propertiesB, "TestModel");
        Element elementC = new Element("2", "ElementC", propertiesC, "TestModel");

        MovePropertyTarget firstTarget = new MovePropertyTarget(elementA, elementB, "prop2");
        MovePropertyTarget secondTarget = new MovePropertyTarget(elementA, elementC, "prop1");

        assert !firstTarget.overlapsWith(secondTarget);
    }

    @Test
    void overlapsWith_DifferentSourceSameTargetDifferentProperty_false() {
        LinkedList<String> propertiesA = new LinkedList<>(Arrays.asList("prop1", "prop2", "n_elementA"));
        LinkedList<String> propertiesB = new LinkedList<>(Arrays.asList("prop1", "prop4", "n_elementB"));
        LinkedList<String> propertiesC = new LinkedList<>(Arrays.asList("prop5", "prop6", "n_elementC"));
        Element elementA = new Element("0", "ElementA", propertiesA, "TestModel");
        Element elementB = new Element("1", "ElementB", propertiesB, "TestModel");
        Element elementC = new Element("2", "ElementC", propertiesC, "TestModel");

        MovePropertyTarget firstTarget = new MovePropertyTarget(elementA, elementC, "prop1");
        MovePropertyTarget secondTarget = new MovePropertyTarget(elementB, elementC, "prop4");

        assert !firstTarget.overlapsWith(secondTarget);
    }

    @Test
    void overlapsWith_SameSourceAndTargetDifferentProperty_false() {
        LinkedList<String> propertiesA = new LinkedList<>(Arrays.asList("prop1", "prop2", "n_elementA"));
        LinkedList<String> propertiesB = new LinkedList<>(Arrays.asList("prop3", "prop4", "n_elementB"));
        Element elementA = new Element("0", "ElementA", propertiesA, "TestModel");
        Element elementB = new Element("1", "ElementB", propertiesB, "TestModel");

        MovePropertyTarget firstTarget = new MovePropertyTarget(elementA, elementB, "prop1");
        MovePropertyTarget secondTarget = new MovePropertyTarget(elementA, elementB, "prop2");

        assert !firstTarget.overlapsWith(secondTarget);
    }
}
