package de.hub.mse.variantsync.variantdrift.clone.models;

import org.conqat.engine.core.logging.ELogLevel;
import org.conqat.engine.core.logging.IConQATLogger;
import org.conqat.engine.model_clones.detection.ModelCloneReporterMock;
import org.conqat.engine.model_clones.detection.clustering.CloneClusterer;
import org.conqat.engine.model_clones.detection.pairs.PairDetector;
import org.conqat.engine.model_clones.detection.util.AugmentedModelGraph;
import org.conqat.engine.model_clones.model.IModelGraph;

public class MyConqatBasedCloneDetector {
    private IModelGraph modelGraph;

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
}
