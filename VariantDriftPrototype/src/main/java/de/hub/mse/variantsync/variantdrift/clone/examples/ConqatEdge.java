package de.hub.mse.variantsync.variantdrift.clone.examples;
import org.conqat.engine.model_clones.model.IDirectedEdge;
import org.conqat.engine.model_clones.model.INode;
import org.eclipse.emf.henshin.variability.mergein.normalize.HenshinEdge;

public class ConqatEdge implements IDirectedEdge {
    private INode sourceNode;
    private INode targetNode;
    private String label;

    public ConqatEdge(HenshinEdge edge, INode sourceNode, INode targetNode) {
        this.sourceNode = sourceNode;
        this.targetNode = targetNode;
        this.label = this.computeLabel(edge);
    }

    private String computeLabel(HenshinEdge edge) {
        StringBuilder sb = new StringBuilder();
        sb.append(edge.getType().getName());
        sb.append(' ');
        sb.append(edge.getActionType().name());
        sb.append(' ');
        sb.append(edge.getRuleName());
        return sb.toString();
    }

    public String getEquivalenceClassLabel() {
        return this.label;
    }

    public INode getSourceNode() {
        return this.sourceNode;
    }

    public INode getTargetNode() {
        return this.targetNode;
    }
}

