//package de.hub.mse.variantsync.variantdrift.clone.examples;
//
//
//import org.conqat.engine.model_clones.model.IDirectedEdge;
//import org.conqat.engine.model_clones.model.IModelGraph;
//import org.conqat.engine.model_clones.model.INode;
//import org.conqat.engine.model_clones.model.ModelGraphMock;
//import org.eclipse.emf.henshin.variability.mergein.conqat.ConqatAttributeNode;
//import org.eclipse.emf.henshin.variability.mergein.conqat.ConqatEdge;
//import org.eclipse.emf.henshin.variability.mergein.conqat.ConqatNode;
//import org.eclipse.emf.henshin.variability.mergein.conqat.HenshinToConqatGraphElementMap;
//import org.eclipse.emf.henshin.variability.mergein.normalize.HenshinAttributeNode;
//import org.eclipse.emf.henshin.variability.mergein.normalize.HenshinEdge;
//import org.eclipse.emf.henshin.variability.mergein.normalize.HenshinGraph;
//import org.eclipse.emf.henshin.variability.mergein.normalize.HenshinNode;
//
//import java.util.Iterator;
//import java.util.List;
//
//public class HenshinToConqatGraphConverter {
//    HenshinToConqatGraphElementMap map;
//    List<HenshinGraph> henshinGraphs;
//
//    public HenshinToConqatGraphConverter(List<HenshinGraph> henshinGraphs, HenshinToConqatGraphElementMap map) {
//        this.henshinGraphs = henshinGraphs;
//        this.map = map;
//    }
//
//    public IModelGraph createConqatGraph() {
//        IModelGraph resultGraph = new ModelGraphMock();
//        Iterator var2 = this.henshinGraphs.iterator();
//
//        while(var2.hasNext()) {
//            HenshinGraph henshinGraph = (HenshinGraph)var2.next();
//            IModelGraph tempGraph = this.createConqatGraph(henshinGraph);
//            resultGraph.getNodes().addAll(tempGraph.getNodes());
//            resultGraph.getEdges().addAll(tempGraph.getEdges());
//        }
//
//        return resultGraph;
//    }
//
//    public IModelGraph createConqatGraph(HenshinGraph henshinGraph) {
//        IModelGraph graph = new ModelGraphMock();
//        Iterator var3 = henshinGraph.vertexSet().iterator();
//
//        while(var3.hasNext()) {
//            HenshinNode node = (HenshinNode)var3.next();
//            this.addNodeToGraph(node, graph);
//        }
//
//        var3 = henshinGraph.edgeSet().iterator();
//
//        while(var3.hasNext()) {
//            HenshinEdge edge = (HenshinEdge)var3.next();
//            HenshinNode source = (HenshinNode)henshinGraph.getEdgeSource(edge);
//            HenshinNode target = (HenshinNode)henshinGraph.getEdgeTarget(edge);
//            this.addEdgeToGraph(edge, source, target, graph);
//        }
//
//        return graph;
//    }
//
//    private boolean addNodeToGraph(HenshinNode node, IModelGraph graph) {
//        Object newNode;
//        if (node instanceof HenshinAttributeNode) {
//            newNode = new ConqatAttributeNode((HenshinAttributeNode)node);
//        } else {
//            newNode = new ConqatNode(node);
//        }
//
//        graph.getNodes().add(newNode);
//        this.map.put(node, (INode)newNode);
//        return true;
//    }
//
//    private boolean addEdgeToGraph(HenshinEdge edge, HenshinNode source, HenshinNode target, IModelGraph graph) {
//        INode sourceNode = this.map.get(source);
//        INode targetNode = this.map.get(target);
//        if (sourceNode != null && targetNode != null) {
//            IDirectedEdge newEdge = new ConqatEdge(edge, sourceNode, targetNode);
//            graph.getEdges().add(newEdge);
//            this.map.put(edge, newEdge);
//            return true;
//        } else {
//            return false;
//        }
//    }
//}
