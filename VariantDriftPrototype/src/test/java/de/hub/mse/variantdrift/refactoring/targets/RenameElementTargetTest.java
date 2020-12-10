package de.hub.mse.variantdrift.refactoring.targets;

import de.hub.mse.variantdrift.experiments.algorithms.nwm.domain.Element;
import de.hub.mse.variantdrift.experiments.algorithms.nwm.domain.Model;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

public class RenameElementTargetTest {
    private Model model;

    @BeforeEach
    void initEmptyModel () {
        this.model = new Model("TestModel");
    }

    @Test
    void findAllTargets_ForSingleElement_FindsAll() {
        LinkedList<String> properties = new LinkedList<>(Arrays.asList("prop1", "n_testElement"));
        Element element = new Element("0", "TestElement", properties, "TestModel");

        model.addElement(element);

        Set<RenameElementTarget> foundTargets = RenameElementTarget.findAllTargets(model);
        assert foundTargets.size() == 1;

        for(RenameElementTarget target : foundTargets) {
            assert target.element == element;
            assert target.model == model;
        }
    }

    @Test
    void findAllTargets_ForSeveralElements_FindsAll() {
        LinkedList<String> propertiesA = new LinkedList<>(Arrays.asList("prop1", "n_elementA"));
        LinkedList<String> propertiesB = new LinkedList<>(Arrays.asList("prop2", "n_elementB"));
        LinkedList<String> propertiesC = new LinkedList<>(Arrays.asList("prop3", "n_elementC"));
        Element elementA = new Element("0", "ElementA", propertiesA, "TestModel");
        Element elementB = new Element("1", "ElementB", propertiesB, "TestModel");
        Element elementC = new Element("2", "ElementC", propertiesC, "TestModel");

        model.addElement(elementA);
        model.addElement(elementB);
        model.addElement(elementC);

        Set<RenameElementTarget> foundTargets = RenameElementTarget.findAllTargets(model);
        assert foundTargets.size() == 3;

        Set<Element> targetElements = new HashSet<>();
        for(RenameElementTarget target : foundTargets) {
            targetElements.add(target.element);
            assert target.model == model;
        }
        assert targetElements.size() == 3;
    }

    @Test
    void findAllTargets_ForSeveralElementsWithSameName_FindsAll() {
        LinkedList<String> propertiesA = new LinkedList<>(Arrays.asList("prop1", "n_elementA"));
        LinkedList<String> propertiesB = new LinkedList<>(Arrays.asList("prop2", "n_elementA"));
        LinkedList<String> propertiesC = new LinkedList<>(Arrays.asList("prop3", "n_elementC"));
        Element elementA = new Element("0", "ElementA", propertiesA, "TestModel");
        Element elementB = new Element("1", "ElementA", propertiesB, "TestModel");
        Element elementC = new Element("2", "ElementC", propertiesC, "TestModel");

        model.addElement(elementA);
        model.addElement(elementB);
        model.addElement(elementC);

        Set<RenameElementTarget> foundTargets = RenameElementTarget.findAllTargets(model);
        assert foundTargets.size() == 3;

        Set<Element> targetElements = new HashSet<>();
        for(RenameElementTarget target : foundTargets) {
            targetElements.add(target.element);
            assert target.model == model;
        }
        assert targetElements.size() == 3;
    }

    @Test
    void equals_TargetsForSameElement_true () {
        LinkedList<String> propertiesA = new LinkedList<>(Arrays.asList("prop1", "n_elementA"));
        Element elementA = new Element("0", "ElementA", propertiesA, "TestModel");

        model.addElement(elementA);

        RenameElementTarget firstTarget = new RenameElementTarget(elementA, model);
        RenameElementTarget secondTarget = new RenameElementTarget(elementA, model);
        assert firstTarget.equals(secondTarget);
    }

    @Test
    void equals_TargetsForSimilarElements_false() {
        LinkedList<String> propertiesA = new LinkedList<>(Arrays.asList("prop1", "n_elementA"));
        LinkedList<String> propertiesB = new LinkedList<>(Arrays.asList("prop2", "n_elementA"));
        Element elementA = new Element("0", "ElementA", propertiesA, "TestModel");
        Element elementB = new Element("1", "ElementA", propertiesB, "TestModel");

        model.addElement(elementA);
        model.addElement(elementB);

        RenameElementTarget firstTarget = new RenameElementTarget(elementA, model);
        RenameElementTarget secondTarget = new RenameElementTarget(elementB, model);
        assert !firstTarget.equals(secondTarget);
    }
}
