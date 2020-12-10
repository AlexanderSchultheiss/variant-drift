package de.hub.mse.variantdrift.clone.models;

import org.conqat.engine.model_clones.model.INode;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.uml2.uml.internal.impl.ClassImpl;
import org.eclipse.uml2.uml.internal.impl.EnumerationImpl;
import org.eclipse.uml2.uml.internal.impl.PackageImpl;

import java.util.Objects;

public class GenericNode implements INode {
    String label;

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
}
