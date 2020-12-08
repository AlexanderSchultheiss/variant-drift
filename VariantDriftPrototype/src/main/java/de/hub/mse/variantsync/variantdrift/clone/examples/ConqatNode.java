package de.hub.mse.variantsync.variantdrift.clone.examples;


import org.conqat.engine.model_clones.model.INode;
import org.eclipse.emf.ecore.ENamedElement;
import org.eclipse.emf.henshin.variability.mergein.normalize.HenshinNode;

public class ConqatNode implements INode {
    String label;

    public ConqatNode(HenshinNode node) {
        this.label = this.computeLabel(node);
    }

    protected String computeLabel(HenshinNode node) {
        StringBuilder sb = new StringBuilder();
        if (node.getType() instanceof ENamedElement) {
            sb.append(((ENamedElement)node.getType()).getName());
            sb.append(' ');
        }

        sb.append(node.getAction());
        sb.append(' ');
        sb.append(node.getRuleName());
        return sb.toString();
    }

    public String getEquivalenceClassLabel() {
        return this.label;
    }

    public int getWeight() {
        return 2;
    }
}