package de.hub.mse.variantsync.variantdrift.experiments.data;

import java.util.List;

public class RElement {
    private final String modelID;
    private final String name;
    private final List<String> properties;

    public RElement(String modelID, String name, List<String> properties) {
        this.modelID = modelID;
        this.name = name;
        this.properties = properties;
    }

    public String getName() {
        return this.name;
    }

    public String getModelID() {
        return modelID;
    }

    public List<String> getProperties() {
        return properties;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(modelID + ":" + name + ":(");
        for (String property : properties) {
            sb.append(property);
            sb.append(",");
        }
        // Delete the last comma
        sb.deleteCharAt(sb.length() - 1);
        sb.append(")");
        return sb.toString();
    }
}
