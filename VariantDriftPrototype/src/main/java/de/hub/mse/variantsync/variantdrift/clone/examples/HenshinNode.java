package de.hub.mse.variantsync.variantdrift.clone.examples;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.henshin.model.Action;
import org.eclipse.emf.henshin.variability.mergein.normalize.HenshinGraph;
import org.eclipse.emf.henshin.variability.mergein.normalize.HenshinGraphElement;

public class HenshinNode extends HenshinGraphElement {
    EObject type;
    Action action;
    String ruleName;

    public HenshinNode(HenshinGraph graph, EObject type, Action action, String ruleName) {
        super(graph);
        this.type = type;
        this.action = action;
        this.ruleName = ruleName;
    }

    public EObject getType() {
        return this.type;
    }

    public void setType(EObject type) {
        this.type = type;
    }

    public Action getAction() {
        return this.action;
    }

    public void setActionType(Action action) {
        this.action = action;
    }

    public String getRuleName() {
        return this.ruleName;
    }

    public void setRuleName(String rule) {
        this.ruleName = rule;
    }
}
