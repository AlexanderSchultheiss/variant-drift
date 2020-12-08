package de.hub.mse.variantsync.variantdrift.clone.examples;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.henshin.model.Action.Type;
import org.eclipse.emf.henshin.variability.mergein.normalize.HenshinGraph;
import org.eclipse.emf.henshin.variability.mergein.normalize.HenshinGraphElement;

public class HenshinEdge extends HenshinGraphElement {
    private EReference type;
    private Type actionType;
    private String ruleName;

    public HenshinEdge(HenshinGraph graph, EReference type, Type actionType, String ruleName, boolean attributeEdge) {
        super(graph);
        this.type = type;
        this.actionType = actionType;
        this.ruleName = ruleName;
        if (type == null || actionType == null) {
            System.err.println("type or actionType was null");
        }

    }

    public EReference getType() {
        return this.type;
    }

    public void setType(EReference type) {
        this.type = type;
    }

    public Type getActionType() {
        return this.actionType;
    }

    public void setActionType(Type actionType) {
        this.actionType = actionType;
    }

    public String getRuleName() {
        return this.ruleName;
    }

    public void setRuleName(String ruleName) {
        this.ruleName = ruleName;
    }
}