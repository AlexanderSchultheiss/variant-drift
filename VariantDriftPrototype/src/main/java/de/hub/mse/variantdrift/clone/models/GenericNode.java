package de.hub.mse.variantdrift.clone.models;

import org.conqat.engine.model_clones.model.INode;

import java.util.Objects;

public class GenericNode implements INode {
    String label;
    GenericGraph model;

    public GenericNode(String label) {
        this.label = Objects.requireNonNull(label);
    }

    @Override
    public int getWeight() {
        return 2;
    }

    @Override
    public String getEquivalenceClassLabel() {
        return label;
    }

    @Override
    public String toString() {return label;}

    public GenericGraph getModel() {
        return model;
    }

    public void setModel(GenericGraph model) {
        this.model = model;
    }
}
