package de.hub.mse.variantdrift.clone.conqat;

import aatl.MatchedRule;
import aatl.Module;
import de.hub.mse.variantdrift.clone.models.GenericGraph;
import de.hub.mse.variantdrift.clone.util.GenericGraphToJGraph;
import de.hub.mse.variantdrift.clone.util.GraphViewer;
import de.uni_marburg.fb12.swt.cloneDetection.atl.Link;
import de.uni_marburg.fb12.swt.cloneDetection.atl.conqat.CloneGroup;
import de.uni_marburg.fb12.swt.cloneDetection.atl.conqat.CloneGroupDetectionResult;
import org.conqat.engine.core.logging.ELogLevel;
import org.conqat.engine.core.logging.IConQATLogger;
import org.conqat.engine.model_clones.detection.ModelCloneReporterMock;
import org.conqat.engine.model_clones.detection.clustering.CloneClusterer;
import org.conqat.engine.model_clones.detection.pairs.PairDetector;
import org.conqat.engine.model_clones.detection.util.AugmentedModelGraph;
import org.eclipse.emf.ecore.EObject;

import java.util.*;

public class MyConqatBasedCloneDetector {
    private GenericGraph modelGraph;
    protected Set<CloneGroup> result;

    boolean includeRhs;
    int minSubCloneSize;

    public MyConqatBasedCloneDetector(GenericGraph modelGraph) {
        this.modelGraph = modelGraph;
    }

    public MyConqatBasedCloneDetector(GenericGraph modelGraph, int minSubCloneSize) {
        this.modelGraph = modelGraph;
        this.minSubCloneSize = minSubCloneSize;
    }

    public void detectCloneGroups() {
        MyConqatManager conqatManager = new MyConqatManager(modelGraph);
        conqatManager.doCloneDetection();
        ModelCloneReporterMock reporter = conqatManager.getResultReporter();

        result = new HashSet<>();
        for (ModelCloneReporterMock.ModelClone clone : reporter.modelClones) {
            visualizeClone(clone);
            List<Module> involvedModules = conqatManager.getInvolvedModules(clone);
            List<MatchedRule> involvedRules = conqatManager.getInvolvedMatchedRules(clone);
            Map<EObject, Set<EObject>> nodeMappings = conqatManager.createNodeMappings(clone);
            Map<Link, Set<Link>> linkMappings = conqatManager.createLinkMappings(clone);
            if (!involvedRules.isEmpty()) {
                CloneGroup newCloneGroup = new CloneGroup(involvedModules, involvedRules, nodeMappings, linkMappings);
                result.add(newCloneGroup);
            }
        }
    }

    private static void visualizeClone(ModelCloneReporterMock.ModelClone clone) {
        var nodes = clone.nodes.get(0);
        var edges = clone.edges.get(0);
        var graph = new GenericGraph("Model", new HashSet<>(nodes), new HashSet<>(edges));
        var jGraph = new GenericGraphToJGraph().transform(graph);
        GraphViewer.viewGraph(jGraph, "Clone");
    }

    protected ModelCloneReporterMock runDetection() throws Exception {
        int minCloneSize = 0;
        int minCloneWeight = 1;
        AugmentedModelGraph mag = new AugmentedModelGraph(this.modelGraph);
        ModelCloneReporterMock result = new ModelCloneReporterMock();
        IConQATLogger logger = this.createDummyLogger();
        CloneClusterer clusterer = new CloneClusterer(mag, result, logger, false);
        (new PairDetector(mag, minCloneSize, minCloneWeight, false, clusterer, logger)).execute();
        clusterer.performInclusionAnalysis();
        clusterer.performClustering();
        return result;
    }

    private IConQATLogger createDummyLogger() {
        return new IConQATLogger() {
            public void debug(Object arg0) {
            }

            public void debug(Object arg0, Throwable arg1) {
            }

            public void error(Object arg0) {
            }

            public void error(Object arg0, Throwable arg1) {
            }

            public void info(Object arg0) {
            }

            public void info(Object arg0, Throwable arg1) {
            }

            public void warn(Object arg0) {
            }

            public void warn(Object arg0, Throwable arg1) {
            }

            public ELogLevel getMinLogLevel() {
                return null;
            }

            public void log(ELogLevel arg0, Object arg1) {
            }

            public void log(ELogLevel arg0, Object arg1, Throwable arg2) {
            }
        };
    }

    public CloneGroupDetectionResult getResultOrderedBySize() {
        List<CloneGroup> orderedResult = new ArrayList();
        orderedResult.addAll(this.result);
        Comparator<CloneGroup> comp = new Comparator<CloneGroup>() {
            public int compare(CloneGroup arg0, CloneGroup arg1) {
                return arg1.getSize() - arg0.getSize();
            }
        };
        Collections.sort(orderedResult, comp);
        return new CloneGroupDetectionResult(orderedResult);
    }
}
