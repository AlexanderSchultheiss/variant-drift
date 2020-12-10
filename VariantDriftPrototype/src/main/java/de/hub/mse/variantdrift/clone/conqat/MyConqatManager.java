package de.hub.mse.variantdrift.clone.conqat;

import aatl.MatchedRule;
import aatl.Module;
import de.hub.mse.variantdrift.clone.models.GenericGraph;
import de.hub.mse.variantdrift.clone.models.GenericNode;
import de.uni_marburg.fb12.swt.cloneDetection.atl.Link;
import org.conqat.engine.core.logging.ELogLevel;
import org.conqat.engine.core.logging.IConQATLogger;
import org.conqat.engine.model_clones.detection.ModelCloneReporterMock;
import org.conqat.engine.model_clones.detection.clustering.CloneClusterer;
import org.conqat.engine.model_clones.detection.pairs.PairDetector;
import org.conqat.engine.model_clones.detection.util.AugmentedModelGraph;
import org.conqat.engine.model_clones.model.IDirectedEdge;
import org.conqat.engine.model_clones.model.INode;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.uml2.uml.Package;

import java.util.*;

public class MyConqatManager {
    private static final int SETTINGS_MIN_CLONE_SIZE = 3;
    private static final int SETTINGS_MIN_CLONE_WEIGHT = 1;
    private static final int SETTINGS_MIN_FREQ = 1;
    private final GenericGraph modelGraph;
    private ModelCloneReporterMock resultReporter;

    public MyConqatManager(GenericGraph modelGraph) {
        this.modelGraph = modelGraph;
    }

    public void doCloneDetection() {
        try {
            this.resultReporter = this.runDetection();
        } catch (Exception var2) {
            var2.printStackTrace();
        }

    }

    protected ModelCloneReporterMock runDetection() throws Exception {
        int minCloneSize = 3;
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

    public ModelCloneReporterMock getResultReporter() {
        return this.resultReporter;
    }

    public List<MatchedRule> getInvolvedObjects(ModelCloneReporterMock.ModelClone clone) {
        List<MatchedRule> result = new ArrayList();
        Iterator var3 = clone.nodes.iterator();

        while (var3.hasNext()) {
            List<INode> nodeList = (List) var3.next();
            Iterator var5 = nodeList.iterator();

            while (var5.hasNext()) {
                INode node = (INode) var5.next();
//                EObject container = this.aatl2conqatMap.get(node);
                EObject container = null;

                while (!(container instanceof MatchedRule) && container != null) {
                    container = container.eContainer();
                    if (container instanceof MatchedRule && !result.contains(container)) {
                        result.add((MatchedRule) container);
                    }
                }
            }
        }

        return result;
    }

    public List<MatchedRule> getInvolvedMatchedRules(ModelCloneReporterMock.ModelClone clone) {
        List<MatchedRule> result = new ArrayList();
        Iterator var3 = clone.nodes.iterator();

        while (var3.hasNext()) {
            List<INode> nodeList = (List) var3.next();
            Iterator var5 = nodeList.iterator();

            while (var5.hasNext()) {
                INode node = (INode) var5.next();
//                EObject container = this.aatl2conqatMap.get(node);
                EObject container = null;

                while (!(container instanceof MatchedRule) && container != null) {
                    container = container.eContainer();
                    if (container instanceof MatchedRule && !result.contains(container)) {
                        result.add((MatchedRule) container);
                    }
                }
            }
        }

        return result;
    }

    public List<Module> getInvolvedModules(ModelCloneReporterMock.ModelClone clone) {
        List<Module> result = new ArrayList();
        Iterator var3 = clone.nodes.iterator();

        while (var3.hasNext()) {
            List<INode> nodeList = (List) var3.next();
            Iterator var5 = nodeList.iterator();

            while (var5.hasNext()) {
                GenericNode node = (GenericNode) var5.next();
                EObject container = null;

                while (!(container instanceof Package) && container != null) {
                    container = container.eContainer();
                    if (container instanceof Module && !result.contains(container)) {
                        result.add((Module) container);
                    }
                }
            }
        }

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

    public Map<EObject, Set<EObject>> createNodeMappings(ModelCloneReporterMock.ModelClone clone) {
        Map<EObject, Set<EObject>> result = new HashMap();
        List<INode> firstCloneInstance = (List) clone.nodes.iterator().next();
        int numberOfNodesInClone = firstCloneInstance.size();
        List<Set<EObject>> indexedList = new ArrayList(numberOfNodesInClone);

        for (int i = 0; i < numberOfNodesInClone; ++i) {
            indexedList.add(new HashSet());
        }

        Iterator var11 = clone.nodes.iterator();

        while (var11.hasNext()) {
            List<INode> nodes = (List) var11.next();

            for (int i = 0; i < numberOfNodesInClone; ++i) {
                Set<EObject> group = (Set) indexedList.get(i);
//                EObject o = this.aatl2conqatMap.get((INode)nodes.get(i));
                EObject o = null;
                group.add(o);
                result.put(o, group);
            }
        }

        return result;
    }

    public Map<Link, Set<Link>> createLinkMappings(ModelCloneReporterMock.ModelClone clone) {
        Map<Link, Set<Link>> result = new HashMap();
        List<IDirectedEdge> firstCloneInstance = (List) clone.edges.iterator().next();
        int numberOfEdgesInClone = firstCloneInstance.size();
        List<Set<Link>> indexedList = new ArrayList(numberOfEdgesInClone);

        for (int i = 0; i < numberOfEdgesInClone; ++i) {
            indexedList.add(new HashSet());
        }

        Iterator var11 = clone.edges.iterator();

        while (var11.hasNext()) {
            List<IDirectedEdge> edges = (List) var11.next();

            for (int i = 0; i < numberOfEdgesInClone; ++i) {
                Set<Link> group = (Set) indexedList.get(i);
//                Link l = this.aatl2conqatMap.get((IDirectedEdge)edges.get(i));
                Link l = null;
                group.add(l);
                result.put(l, group);
            }
        }

        return result;
    }
}
