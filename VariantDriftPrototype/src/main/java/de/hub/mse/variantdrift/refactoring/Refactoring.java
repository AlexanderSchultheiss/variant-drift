package de.hub.mse.variantdrift.refactoring;

import de.hub.mse.variantdrift.experiments.algorithms.nwm.domain.Element;
import de.hub.mse.variantdrift.refactoring.targets.RenameElementTarget;
import de.hub.mse.variantdrift.refactoring.targets.RenamePropertyTarget;
import de.hub.mse.variantdrift.refactoring.targets.ExtractInterfaceTarget;
import de.hub.mse.variantdrift.refactoring.targets.MovePropertyTarget;

import java.util.*;

public class Refactoring {

    /**
     * Rename an element's property that is identified by a given property name.
     *
     * @param target          The target specifying which element and property shall be refactored
     * @param newPropertyName The name of the property after refactoring
     * @return All elements that were refactored, i.e. the given element if one of its properties was renamed
     */
    public static Set<Element> renameProperty(RenamePropertyTarget target, String newPropertyName) {
        Set<Element> refactoredElements = new HashSet<>();
        List<String> properties = target.element.getProperties();
        // Validate the arguments
        if (target.property.strip().isEmpty() || newPropertyName.strip().isEmpty()) {
            return refactoredElements;
        }
        if (target.property.equals(newPropertyName)) {
            return refactoredElements;
        }
        if (properties.contains(newPropertyName)) {
            return refactoredElements;
        }
        if (newPropertyName.startsWith("n_")) {
            return refactoredElements;
        }

        if (properties.contains(target.property)) {
            properties.replaceAll((prop) -> prop.equals(target.property) ? newPropertyName : prop);
            refactoredElements.add((target.element));
        }

        return refactoredElements;
    }

    /**
     * Change the name of the class represented by the given element, and change the names of all properties that
     * contain the name of the element.
     * <p>
     * For example, if an element is renamed from "FancyClass" to "CoolClass", all properties referring to the name
     * "FancyClass" are changed as well. Therefore, a field that is named "fancyClass" is renamed to "coolClass", and a
     * method that is named "getFancyClass()" is renamed to "getCoolClass()"
     * </p>
     *
     * @param target The target specifying which element to rename and which model to consider
     * @param name   The name of the element after refactoring
     * @return The set of elements that were refactored, i.e. the renamed element and all element with renamed properties.
     */
    public static Set<Element> renameElement(RenameElementTarget target, String name) {
        Set<Element> refactoredElements = new HashSet<>();
        List<String> properties = target.element.getProperties();
        String oldName = target.element.getLabel();

        // Validate the arguments
        if (!target.model.getElements().contains(target.element)) {
            throw new IllegalArgumentException("The given element is not in the provided model!");
        }
        if (name.strip().isEmpty() || oldName.equals(name)) {
            return refactoredElements;
        }

        // Refactor the given element
        target.element.setLabel(name);
        String nameProperty = null;
        for (String property : properties) {
            if (property.startsWith("n_")) {
                nameProperty = property;
            }
        }
        if (nameProperty != null) {
            String lambdaOldNameProp = nameProperty;
            String lambdaNewNameProp = "n_" + name;
            properties.replaceAll((prop) -> prop.equals(lambdaOldNameProp) ? (lambdaNewNameProp) : prop);
            refactoredElements.add(target.element);
        }

        // Refactor all elements calling this element
        for (Element modelElement : target.model.getElements()) {
            List<String> refactoredProperties = modelElement.getProperties();
            for (String property : refactoredProperties) {
                String propertyLower = property.toLowerCase();
                String oldNameLower = oldName.toLowerCase();
                int indexOfOldName = propertyLower.indexOf(oldNameLower);

                // Replace the part of the property that contains the old name
                if (indexOfOldName != -1) {
                    // Make the first character of the new name lowercase if it becomes the first character in the property
                    if (indexOfOldName == 0) {
                        name = name.substring(0, 1).toLowerCase() + name.substring(1);
                    }
                    // Replace the old property name with the new one
                    String newProperty = property.substring(0, indexOfOldName)
                            + name
                            + property.substring(indexOfOldName + oldName.length());
                    refactoredProperties.replaceAll((prop) -> prop.equals(property) ? (newProperty) : prop);
                    refactoredElements.add(modelElement);
                }
            }
        }
        return refactoredElements;
    }

    /**
     * Move the property identified by the given name from the source to the target element.
     *
     * @param target The target specifying the source element, target element, and property to move
     * @return The elements which have been refactored, i.e., the source and the target element if the move was
     * successful
     */
    public static Set<Element> moveProperty(MovePropertyTarget target) {
        Set<Element> refactoredElements = new HashSet<>();
        List<String> sourceProperties = target.sourceElement.getProperties();
        List<String> targetProperties = target.targetElement.getProperties();

        // Validate the arguments
        if (target.property.startsWith("n_")) {
            return refactoredElements;
        }
        if (!sourceProperties.contains(target.property) || targetProperties.contains(target.property)) {
            return refactoredElements;
        }
        if (!target.sourceElement.getModelId().equals(target.targetElement.getModelId())) {
            throw new IllegalArgumentException("The source and target element come from different model!");
        }

        // Remove the property from the source element
        sourceProperties.remove(target.property);
        refactoredElements.add(target.sourceElement);

        // Add the property to the target element
        targetProperties.add(target.property);
        refactoredElements.add(target.targetElement);

        return refactoredElements;
    }

    /**
     * Create a new element, add it to the given model, and move/copy the set of common properties of a set of given
     * elements to the new element.
     *
     * @param target         The target specifying the elements whose properties are moved up to the interface and the model,
     *                       which contains the elements and to which the new element is added
     * @param interfaceName  The name of the new element that is to be created
     * @param moveProperties Flag whether the properties of the elements are move to the created interface element,
     *                       otherwise they are copied
     * @return The set of elements that have been refactored, i.e. the source elements and the newly added interface
     */
    public static Set<Element> extractInterface(ExtractInterfaceTarget target, String interfaceName, boolean moveProperties) {
        Set<Element> refactoredElements = new HashSet<>();
        // Validate the arguments
        if (target.sourceElements.size() < 2) {
            return refactoredElements;
        }
        for (Element element : target.model.getElements()) {
            if (element.getLabel().equals(interfaceName)) {
                return refactoredElements;
            }
        }
        Set<String> commonProperties = target.getCommonProperties();

        // Validate the source elements have enough properties in common
        if (commonProperties == null || commonProperties.isEmpty()) {
            return refactoredElements;
        }

        // Delete the properties from the source elements
        if (moveProperties) {
            for (Element source : target.sourceElements) {
                List<String> sourceProperties = source.getProperties();
                for (String property : commonProperties) {
                    sourceProperties.remove(property);
                    refactoredElements.add(source);
                }
            }
        }

        // Create a new element with the given name and the properties
        StringBuilder UUID = new StringBuilder();
        target.sourceElements.forEach(element -> UUID.append(element.getUUID()).append("_"));
        commonProperties.add("n_" + interfaceName);
        Element newElement = new Element(UUID.toString(), interfaceName, new LinkedList<>(commonProperties),
                target.model.getId());

        // Add the created element to the model
        target.model.addElement(newElement);
        refactoredElements.add(newElement);

        return refactoredElements;
    }

}
