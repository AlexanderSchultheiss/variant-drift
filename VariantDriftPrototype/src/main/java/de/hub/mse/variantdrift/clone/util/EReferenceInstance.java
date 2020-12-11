package de.hub.mse.variantdrift.clone.util;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.Resource;

import java.util.Objects;

public class EReferenceInstance {
    public final Resource model;
    public final EReference type;
    public final EObject source;
    public final EObject target;

    public EReferenceInstance(Resource model, EReference type, EObject source, EObject target) {
        this.model = model;
        this.type = type;
        this.source = source;
        this.target = target;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EReferenceInstance that = (EReferenceInstance) o;
        return type.equals(that.type) && source.equals(that.source) && target.equals(that.target);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, source, target);
    }
}
