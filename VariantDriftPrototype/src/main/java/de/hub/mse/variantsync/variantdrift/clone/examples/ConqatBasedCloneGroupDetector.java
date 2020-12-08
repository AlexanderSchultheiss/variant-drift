//package de.hub.mse.variantsync.variantdrift.clone.examples;
//
//import aatl.MatchedRule;
//import aatl.Module;
//import de.uni_marburg.fb12.swt.cloneDetection.atl.AbstractCloneGroupDetector;
//import de.uni_marburg.fb12.swt.cloneDetection.atl.Link;
//import org.conqat.engine.model_clones.detection.ModelCloneReporterMock;
//import org.conqat.engine.model_clones.detection.ModelCloneReporterMock.ModelClone;
//import org.eclipse.emf.ecore.EObject;
//
//import java.util.HashSet;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//
///**
// * A clone detector for ATL rules employing the ConQAT clone detection
// * technique. For internal calculations, the ATL rule is transformed into a
// * normalized representation ({@link HenshinGraph}) and, consequently, into a
// * custom representation as required by ConQAT.
// *
// * @author strueber
// *
// */
//public class ConqatBasedCloneGroupDetector extends AbstractCloneGroupDetector {
//
//    boolean includeRhs;
//    int minSubCloneSize;
//
//    public ConqatBasedCloneGroupDetector(List<MatchedRule> rules) {
//        super(rules);
//    }
//
//    public ConqatBasedCloneGroupDetector(List<MatchedRule> rules, int minSubCloneSize) {
//        super(rules);
//        this.minSubCloneSize = minSubCloneSize;
//    }
//
//    public ConqatBasedCloneGroupDetector(Module module) {
//        super(module);
//    }
//
//    public ConqatBasedCloneGroupDetector(Set<Module> modules) {
//        super(modules);
//    }
//
//
//    @Override
//    public void detectCloneGroups() {
//        ConqatManager conquatManager = new ConqatManager(rules);
//        conquatManager.doCloneDetection();
//        ModelCloneReporterMock reporter = conquatManager.getResultReporter();
//
//        result = new HashSet<CloneGroup>();
//        for (ModelClone clone : reporter.modelClones) {
//            List<Module> involvedModules = conquatManager.getInvolvedModules(clone);
//            List<MatchedRule> involvedRules = conquatManager.getInvolvedMatchedRules(clone);
//            Map<EObject, Set<EObject>> nodeMappings = conquatManager.createNodeMappings(clone);
//            Map<Link, Set<Link>> linkMappings = conquatManager.createLinkMappings(clone);
//            if (!involvedRules.isEmpty()) {
//                CloneGroup newCloneGroup = new CloneGroup(involvedModules, involvedRules, nodeMappings, linkMappings);
//                result.add(newCloneGroup);
//            }
//        }
//    }
//
//}
