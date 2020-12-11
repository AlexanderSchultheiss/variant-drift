package de.hub.mse.variantdrift.clone;

import de.hub.mse.variantdrift.clone.conqat.MyConqatBasedCloneDetector;
import de.hub.mse.variantdrift.clone.escan.CombinedClone;
import de.hub.mse.variantdrift.clone.escan.EScanDetectionOriginal;
import de.hub.mse.variantdrift.clone.escan.Fragment;
import de.hub.mse.variantdrift.clone.util.*;
import de.hub.mse.variantdrift.clone.models.GenericGraph;
import org.conqat.engine.model_clones.model.IDirectedEdge;
import org.conqat.engine.model_clones.model.INode;
import org.eclipse.emf.ecore.resource.Resource;
import org.jgrapht.DirectedGraph;
import org.jgrapht.alg.BronKerboschCliqueFinder;
import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.graph.DefaultEdge;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import static de.hub.mse.variantdrift.clone.demo.Demo.loadModel;

public class PlayingAround {
    static int max_number_of_models_to_consider = 2;

    public static void main(String... args) throws IOException {
        File dir = new File("models/ppu");
        foo(dir);
    }

    private static void foo(File inFolder) throws IOException {
        List<String[]> allClassRecords = new ArrayList<>();

        File[] modelFiles = inFolder.listFiles();
        assert modelFiles != null;
        List<Resource> models = new LinkedList<>();
        int i = 0;
        for (File file : modelFiles) {
            if (i < max_number_of_models_to_consider) {
                System.out.println(file.getName());
                Resource model = loadModel(file.getAbsolutePath());
                models.add(model);
                i++;
            } else {
                break;
            }
        }

        List<GenericGraph> modelGraphs = new LinkedList<>();
        List<NodeMapping> nodeMappings = new LinkedList<>();
        List<EdgeMapping> edgeMappings = new LinkedList<>();
        for (var model : models) {
            EMF2GenericGraph.ParseResult parseResult = EMF2GenericGraph.transform(model, model.toString());
            modelGraphs.add(parseResult.graph);
            nodeMappings.add(parseResult.nodeMapping);
            edgeMappings.add(parseResult.edgeMapping);
        }

//        GenericGraph filteredCombinedGraph = (GenericGraph) modelGraph;
        var combinedGraph = GenericGraph.fromGraphs("Combined Graph", modelGraphs);
        GenericGraph filteredCombinedGraph = combinedGraph.simulateSmallerGraph();

        // Conqat
        System.out.println("Starting Conqat...");
        var cloneDetector = new MyConqatBasedCloneDetector(filteredCombinedGraph);
        cloneDetector.detectCloneGroups();
        var resultConqat = cloneDetector.getResultOrderedBySize();
        System.out.println("Conqat finished.");
        System.out.println();

        // EScan
        List<GenericGraph> filteredGraphs = modelGraphs.stream().map(GenericGraph::simulateSmallerGraph).collect(Collectors.toList());

        System.out.println("Starting EScan...");
        var escan = new EScanDetectionOriginal(modelGraphs);
        escan.detectCloneGroups();
        System.out.println("done.");
//        GraphViewer.viewGraph(new GenericGraphToJGraph().transform(filteredGraphs.get(0)), "Model 1");
//        GraphViewer.viewGraph(new GenericGraphToJGraph().transform(filteredGraphs.get(1)), "Model 2");
        var resultEScan = escan.getResultAsCloneMatrixOrderedByNumberOfCommonElements();
        System.out.println("EScan finished.");
        var combinedClone = CombinedClone.fromEScanResult(resultEScan);

        // Show the combined clones...
        GraphViewer.viewGraph(new GenericGraphToJGraph().transform(combinedClone.get(modelGraphs.get(0))), "Model 1");
        GraphViewer.viewGraph(new GenericGraphToJGraph().transform(combinedClone.get(modelGraphs.get(1))), "Model 2");

        var oneClone = combinedClone.get(modelGraphs.get(0));
        var jgraphClone = new GenericGraphToJGraph().transform(oneClone);
        var inspectorConnect = new ConnectivityInspector<>((DirectedGraph<INode, IDirectedEdge>) jgraphClone);
        var connectedComponents = inspectorConnect.connectedSets();
        System.out.println("EScan found a clone comprising " + oneClone.getNodes().size() + " nodes and " + oneClone.getEdges().size() + " edges.");
        System.out.println("The clone contains " + connectedComponents.size() + " cliques.");
    }
}
