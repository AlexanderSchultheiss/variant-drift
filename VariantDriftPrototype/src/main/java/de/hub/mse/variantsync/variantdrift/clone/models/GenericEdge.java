package de.hub.mse.variantsync.variantdrift.clone.models;

import org.conqat.engine.model_clones.model.IDirectedEdge;
import org.conqat.engine.model_clones.model.INode;
import org.eclipse.emf.henshin.variability.mergein.normalize.HenshinEdge;
import org.eclipse.uml2.uml.internal.impl.AssociationImpl;

import java.util.Objects;

public class GenericEdge implements IDirectedEdge {
    private final String label;
    private final INode sourceNode;
    private final INode targetNode;

    public GenericEdge(String label, INode sourceNode, INode targetNode) {
        this.label = Objects.requireNonNull(label);
        this.sourceNode = Objects.requireNonNull(sourceNode);
        this.targetNode = Objects.requireNonNull(targetNode);
    }

    @Override
    public INode getSourceNode() {
        return null;
    }

    @Override
    public INode getTargetNode() {
        return null;
    }

    @Override
    public String getEquivalenceClassLabel() {
        return null;
    }

    @Override
    public String toString() {return sourceNode.toString() + " --" + label + "--> " + targetNode.toString();}
}
