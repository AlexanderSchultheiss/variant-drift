package de.hub.mse.variantsync.variantdrift.clone.demo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

import de.hub.mse.variantsync.variantdrift.clone.models.GenericGraph;
import de.hub.mse.variantsync.variantdrift.clone.util.EMF2GenericGraph;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceImpl;
import org.eclipse.uml2.uml.UMLPackage;

public class Demo {

	static {
		EcorePackage.eINSTANCE.eClass();
		UMLPackage.eINSTANCE.eClass();
	}
	
	public static void main(String[] args) throws FileNotFoundException, IOException {
		Resource model = loadModel("C:\\develop\\work\\VariantDrift\\VariantDriftEMSE\\VariantDriftPrototype\\models\\ppu\\Scen00.uml");
		
		EMF2GenericGraph emf2graph = new EMF2GenericGraph();
		GenericGraph graph = emf2graph.transform(model);
	
		System.out.println(graph);
	}

	private static Resource loadModel(String fileName) throws FileNotFoundException, IOException {
		XMIResourceImpl resource = new XMIResourceImpl();
		File source = new File(fileName);
		resource.load(new FileInputStream(source), new HashMap<Object, Object>());
		
		return resource;
	}
}
