package de.hub.mse.variantsync.variantdrift.clone;

import de.hub.mse.variantsync.variantdrift.clone.conqat.MyConqatBasedCloneDetector;
import de.hub.mse.variantsync.variantdrift.clone.models.GenericGraph;
import de.hub.mse.variantsync.variantdrift.clone.util.EMF2GenericGraph;
import de.hub.mse.variantsync.variantdrift.clone.util.EdgeMapping;
import de.hub.mse.variantsync.variantdrift.clone.util.NodeMapping;
import org.conqat.engine.model_clones.model.IModelGraph;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.henshin.model.staticanalysis.NodeMap;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Feature;
import org.eclipse.uml2.uml.Model;
import org.eclipse.uml2.uml.Operation;
import org.eclipse.uml2.uml.Parameter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static de.hub.mse.variantsync.variantdrift.clone.demo.Demo.loadModel;

public class PlayingAround {
    static int max_number_of_models_to_consider = 14;

    public static void main(String... args) throws IOException {
        File dir = new File("models/ppu");
        foo(dir);
    }

    private static void foo(File inFolder) throws IOException {
        List<String[]> allClassRecords = new ArrayList<String[]>();

        File[] modelFiles = inFolder.listFiles();
        assert modelFiles != null;
        List<Resource> models = new LinkedList<>();
        int i = 0;
        for (File file : modelFiles) {
            System.out.println(file.getName());
            Resource model = loadModel(file.getAbsolutePath());
            models.add(model);
            if (i > max_number_of_models_to_consider) {
                break;
            } else {
                i++;
            }
        }

        List<GenericGraph> modelGraphs = new LinkedList<>();
        List<NodeMapping> nodeMappings = new LinkedList<>();
        List<EdgeMapping> edgeMappings = new LinkedList<>();
        for (var model : models) {
            EMF2GenericGraph.ParseResult parseResult = EMF2GenericGraph.transform(model);
            modelGraphs.add(parseResult.graph);
            nodeMappings.add(parseResult.nodeMapping);
            edgeMappings.add(parseResult.edgeMapping);
        }
//        GenericGraph filteredGraph = (GenericGraph) modelGraph;
        var graph = GenericGraph.fromGraphs(modelGraphs);
        GenericGraph filteredGraph = graph.simulateSmallerGraph();

        var cloneDetector = new MyConqatBasedCloneDetector(filteredGraph);
        cloneDetector.detectCloneGroups();
        var result = cloneDetector.getResultOrderedBySize();
    }
}
