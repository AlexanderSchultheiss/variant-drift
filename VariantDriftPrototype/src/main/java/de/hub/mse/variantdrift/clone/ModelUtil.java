package de.hub.mse.variantdrift.clone;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Feature;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.resources.util.UMLResourcesUtil;

public class ModelUtil {

    public static List<Feature> getAllProperties(Class clazz) {
        List<Feature> properties = new ArrayList<Feature>();
        properties.addAll(clazz.getAttributes());
        properties.addAll(clazz.getOperations());

        return properties;
    }

    public static List<Class> getAllClasses(Model model) {
        List<Class> classes = new ArrayList<Class>();
        for (Iterator<EObject> iterator = model.eAllContents(); iterator.hasNext();) {
            EObject eObject = (EObject) iterator.next();
            if (eObject.eClass() == UMLPackage.eINSTANCE.getClass_()) {
                classes.add((Class) eObject);
            }
        }

        return classes;
    }

    public static String getXmiId(EObject eObject) {
        assert (eObject != null && eObject.eResource() instanceof XMIResource);

        String objectID = ((XMIResource) eObject.eResource()).getID(eObject);

        return objectID;
    }

    public static Model loadModel(String path) {
        ResourceSet resourceSet = new ResourceSetImpl();
        UMLResourcesUtil.init(resourceSet);
        Resource resource = resourceSet.getResource(URI.createFileURI(path), true);
        Model model = (Model) resource.getContents().get(0);

        return model;
    }

    public static void saveModel(Model model, String path) {
        System.out.println(path);
        ResourceSet resourceSet = new ResourceSetImpl();
        UMLResourcesUtil.init(resourceSet);
        Resource resource = resourceSet.createResource(URI.createFileURI(path));
        resource.getContents().add(model);

        // now save the content.
        try {
            resource.save(Collections.EMPTY_MAP);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
