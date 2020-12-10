package de.hub.mse.variantdrift.experiments.data;

import java.util.*;

public class ElementTuple {
    private final Set<RElement> elements;

    public ElementTuple(RElement... elements) {
        this(Arrays.asList(elements));
    }

    public ElementTuple(Collection<RElement> elements) {
        this.elements = new HashSet<>(elements);
    }

    public boolean isValid() {
        HashSet<String> modelSet = new HashSet<>();
        for (RElement element : elements) {
            if (modelSet.contains(element.getModelID())) {
                return false;
            } else {
                modelSet.add(element.getModelID());
            }
        }
        return true;
    }

    public Collection<RElement> getElements() {
        return elements;
    }

    public boolean contains(RElement element) {
        return elements.contains(element);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (RElement element : elements) {
            sb.append(element);
            sb.append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    public String getLongString() {
        StringBuilder sb = new StringBuilder();
        Set<String> properties = new HashSet<>();
        for (RElement element : elements) {
            sb.append(element);
            sb.append(",");
            properties.addAll(element.getProperties());
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append("::{{");
        for (String property : properties) {
            sb.append(property);
            sb.append(";");
        }
        sb.append("}}");
        return sb.toString();
    }

}
