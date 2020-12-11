package de.hub.mse.variantdrift.clone.models;

import de.hub.mse.variantdrift.clone.util.EReferenceInstance;
import org.conqat.engine.model_clones.model.IDirectedEdge;
import org.conqat.engine.model_clones.model.INode;
import org.eclipse.emf.henshin.variability.mergein.normalize.HenshinEdge;
import org.eclipse.uml2.uml.internal.impl.AssociationImpl;

import java.util.Objects;

public class GenericEdge implements IDirectedEdge {
    private final String label;
    private final INode sourceNode;
    private final INode targetNode;
    private GenericGraph model;

    public GenericEdge(String label, INode sourceNode, INode targetNode) {
        this.label = Objects.requireNonNull(label);
        this.sourceNode = Objects.requireNonNull(sourceNode);
        this.targetNode = Objects.requireNonNull(targetNode);
    }

    @Override
    public INode getSourceNode() {
        return sourceNode;
    }

    @Override
    public INode getTargetNode() {
        return targetNode;
    }

    @Override
    public String getEquivalenceClassLabel() {
        return label;
    }

    @Override
    public String toString() {return sourceNode.toString() + " --" + label + "--> " + targetNode.toString();}

    public GenericEdge getOriginalEdge() {
        return this;
    }

    public GenericGraph getModel() {
        return model;
    }

    public void setModel(GenericGraph model) {
        this.model = model;
    }
}
