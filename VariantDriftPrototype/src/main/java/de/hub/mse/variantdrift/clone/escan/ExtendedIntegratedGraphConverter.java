package de.hub.mse.variantdrift.clone.escan;

import aatl.Module;
import aatl.*;
import de.uni_marburg.fb12.swt.cloneDetection.atl.Link;
import de.uni_marburg.fb12.swt.cloneDetection.atl.escan.CapsuleEdge;
import de.uni_marburg.fb12.swt.cloneDetection.atl.escan.InputRuleNotSupportedException;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * extended: Attributes are transformed in additional EObjects and
 * (Capsule-)Edges
 *
 */

public class ExtendedIntegratedGraphConverter implements ModulePartVisitor {

	private DirectedGraph<EObject, CapsuleEdge> currentGraph;
	private MatchedRule currentRule;

	/**
	 * the extended Version
	 * 
	 * @param rules
	 * @return
	 */
	public Map<MatchedRule, DirectedGraph<EObject, CapsuleEdge>> getRuleToGraphWithAttributesMap(
			Collection<MatchedRule> rules) {
		Map<MatchedRule, DirectedGraph<EObject, CapsuleEdge>> result = new HashMap<MatchedRule, DirectedGraph<EObject, CapsuleEdge>>();

		for (MatchedRule rule : rules) {
			try {
				DirectedGraph<EObject, CapsuleEdge> graph = createIntegratedGraph(rule);
				result.put(rule, graph);
			} catch (InputRuleNotSupportedException e) {
				System.out.println("Skipping rule " + rule.getName() + ": " + e.getMessage());
			}

		}
		return result;
	}

	public DirectedGraph<EObject, CapsuleEdge> createIntegratedGraph(MatchedRule rule)
			throws InputRuleNotSupportedException {
		DirectedGraph<EObject, CapsuleEdge> result = new DefaultDirectedGraph<EObject, CapsuleEdge>(CapsuleEdge.class);
		currentRule = rule;
		currentGraph = result;
		rule.accept(this);
		return result;
	}

	@Override
	public void visit(Module module) {
		// Noop: This visitor starts at the rule level.
	}

	@Override
	public void visit(MatchedRule matchedRule) {
		addNodeToGraph(matchedRule);
	}
	

	@Override
	public void visit(Filter filter) {
		addNodeToGraph(filter);
		addContainmentEdgeToGraph(filter);
	}
	

	@Override
	public void visit(InPattern inPattern) {
		addNodeToGraph(inPattern);
		addContainmentEdgeToGraph(inPattern);
	}

	@Override
	public void visit(OutPattern outPattern) {
		addNodeToGraph(outPattern);
		addContainmentEdgeToGraph(outPattern);
	}

	@Override
	public void visit(Variable variable) {
		addNodeToGraph(variable);
		addContainmentEdgeToGraph(variable);
	}

	@Override
	public void visit(InPatternElement inPatternElement) {
		addNodeToGraph(inPatternElement);
		addContainmentEdgeToGraph(inPatternElement);
	}

	@Override
	public void visit(OutPatternElement outPatternElement) {
		addNodeToGraph(outPatternElement);
		addContainmentEdgeToGraph(outPatternElement);
	}

	@Override
	public void visit(Binding binding) {
		addNodeToGraph(binding);
		addContainmentEdgeToGraph(binding);
	}

	/**
	 * Adds the given node to the graph.
	 * 
	 * @param theMap
	 */
	private boolean addNodeToGraph(EObject node) {
		currentGraph.addVertex(node);
		return true;
	}

	private void addContainmentEdgeToGraph(EObject eObject) {
		EObject source = eObject.eContainer();
		EObject target = eObject;
		EReference reference = eObject.eContainmentFeature();
		addEdgeToGraph(reference, source, target);
	}

	/**
	 * Adds the given edge with given source and target nodes in the graph.
	 * 
	 * @param edge
	 * @param source
	 * @param target
	 * @param graph
	 */
	private boolean addEdgeToGraph(EReference reference, EObject source, EObject target) {
		if (currentGraph.containsVertex(source) && currentGraph.containsVertex(target)) {
			Link link = new Link(source, target, reference, currentRule);
			CapsuleEdge newEdge = new CapsuleEdge(link, reference.getName());
			currentGraph.addEdge(source, target, newEdge);
			return true;
		} else {
			return false;
		}
	}
}
