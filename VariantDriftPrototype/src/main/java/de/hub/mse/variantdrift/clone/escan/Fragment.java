package de.hub.mse.variantdrift.clone.escan;

import de.hub.mse.variantdrift.clone.models.GenericEdge;
import de.hub.mse.variantdrift.clone.models.GenericGraph;
import de.hub.mse.variantdrift.clone.models.GenericNode;
import de.hub.mse.variantdrift.clone.util.EReferenceInstance;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;

import java.util.*;

/**
 * fragment from the ModelCd-eScan article
 *
 */
public class Fragment {
	private final List<GenericEdge> orderedCapsuleEdges;
	private final GenericGraph model;
	private final DirectedGraph<GenericNode, GenericEdge> graph;
	private final String label;

	private GenericEdge lastNotDisconnectingCapsuleEdge;

	@Override
	public int hashCode() {
		int res = model.getLabel().hashCode();
		res = res + label.hashCode();
		return res;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Fragment fragment)) {
			return false;
		}
		if (this.model != fragment.model) {
			return false;
		}
		return fragment.orderedCapsuleEdges.equals(orderedCapsuleEdges);
	}

	/**
	 * Checks if this Fragment and the given one are isomorphic to each other,
	 * respectively if the canonical labels are equal
	 * 
	 * @param f
	 *            the Fragment this Fragment should be compared to
	 * @return n <code>true</code> if the Fragments are isomorphic to each other
	 *         <code>false</code> else
	 */
	public boolean isIsomorph(Fragment f) {
		return label.equals(f.getLabel());
	}

	@Override
	public String toString() {
		return model.getLabel() + "\n Label: " + label;
	}

	/**
	 * 
	 * @return the source- and target-Nodes of the Edges of this Fragment
	 */
	public List<GenericNode> getNodes() {
		List<GenericNode> nodes = new ArrayList<>();
		for (GenericEdge genericEdge : orderedCapsuleEdges) {
			if (!nodes.contains(graph.getEdgeSource(genericEdge)))
				nodes.add(graph.getEdgeSource(genericEdge));
			if (!nodes.contains(graph.getEdgeTarget(genericEdge)))
				nodes.add(graph.getEdgeTarget(genericEdge));
		}
		return nodes;
	}

	/**
	 * 
	 * @return all the CapsuleEdges this Fragment exist of
	 */
	public List<GenericEdge> getCapsuleEdges() {
		return orderedCapsuleEdges;
	}

	/**
	 * The size of a Fragment is the number of the edges of this Fragment.
	 * 
	 * @return the size of this Fragment
	 * 
	 */
	public int size() {
		return orderedCapsuleEdges.size();
	}

	/**
	 * The edges of the computation graph are CapsuleEdges, CapsuleEdges capsule
	 * either an original edge or an attribute.
	 * 
	 * @return all original edges (that is Henhsin-edges) of this Fragment
	 */
	public Set<EReferenceInstance> getOriginalEdges() {
		Set<EReferenceInstance> res = new HashSet<>();
		for (GenericEdge e : orderedCapsuleEdges) {
				res.add(e.getOriginalReference());
		}
		return res;
	}


	/**
	 * 
	 * @return the rule this Fragment belongs to
	 */
	public GenericGraph getModel() {
		return model;
	}

	/**
	 * 
	 * @return the whole computation graph this Fragment belongs to
	 */
	public DirectedGraph<GenericNode, GenericEdge> getGraph() {
		return graph;
	}

	public Fragment(Set<GenericEdge> genericEdges, GenericGraph model,
                    DirectedGraph<GenericNode, GenericEdge> graph) {
		this.model = model;
		this.graph = graph;
		DirectedGraph<GenericNode, GenericEdge> fragmentGraph = getFragmentAsGraph(
				genericEdges, graph);
		Map<String, List<GenericEdge>> labelToOrderedCapsuleEdges = CanonicalLabelForFragmentCreator
				.getCanonicalLabel(fragmentGraph);
		this.label = labelToOrderedCapsuleEdges.keySet().iterator().next();
		this.orderedCapsuleEdges = labelToOrderedCapsuleEdges.get(label);

	}

	/**
	 * 
	 * @param biggerFragment
	 *            the Fragment that maybe contains this Fragment.
	 * @return <code>true</code> if this Fragments is a part of the
	 *         biggerFragment <code>false</code> else
	 */
	public boolean isSubFragment(Fragment biggerFragment) {
		for (GenericEdge e : orderedCapsuleEdges) {
			if (!biggerFragment.orderedCapsuleEdges.contains(e)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Two Fragments are NodeOverlapping if they contains a least one identical
	 * Node.
	 * 
	 * @param f
	 *            the other Node
	 * @return <code>true</code> if the Fragments have at least one identical
	 *         Node <code>false</code> else
	 */
	public boolean isNodeOverlapping(Fragment f) {
		if (this.model != f.getModel()) {
			return false;
		}

		Set<GenericNode> nodesThis = new HashSet<>();
		Set<GenericNode> nodesF = new HashSet<>();
		DirectedGraph<GenericNode, GenericEdge> graphF = f.getGraph();

		for (GenericEdge e : orderedCapsuleEdges) {
			nodesThis.add(graph.getEdgeSource(e));
			nodesThis.add(graph.getEdgeTarget(e));
		}

		for (GenericEdge e : f.orderedCapsuleEdges) {
			nodesF.add(graphF.getEdgeSource(e));
			nodesF.add(graphF.getEdgeTarget(e));
		}

		for (GenericNode  n : nodesThis) {
			if (nodesF.contains(n)) {
				return true;
			}
		}
		return false;
	}

	public boolean isGeneratingParent(Fragment ckp1) {
		if (ckp1.getModel() != model) {
			return false;
		}
		// determine the edge the fragments differ from each other
		GenericEdge additionalEdge = null;
		boolean foundAdditionalEdge = false;
		for (GenericEdge e : ckp1.orderedCapsuleEdges) {
			if (!orderedCapsuleEdges.contains(e)) {
				if (foundAdditionalEdge) {
					return false;
				}
				additionalEdge = e;
				foundAdditionalEdge = true;
			}
		}

		return additionalEdge == ckp1.lastNotDisconnectingCapsuleEdge();
	}

	private GenericEdge lastNotDisconnectingCapsuleEdge() {
		if (lastNotDisconnectingCapsuleEdge != null) {
			return lastNotDisconnectingCapsuleEdge;
		}

		for (GenericEdge testEdge : orderedCapsuleEdges) {
			if (!(isTheOnlyConnectingCapsuleEdge(testEdge))) {
				lastNotDisconnectingCapsuleEdge = testEdge;
				return lastNotDisconnectingCapsuleEdge;
			}
		}

		return null;
	}

	/**
	 * if e is the only connecting edge, the remaining fragments is not
	 * completely connected
	 *
	 */
	private boolean isTheOnlyConnectingCapsuleEdge(GenericEdge e) {
		Set<GenericEdge> genericEdgesWithoutE = new HashSet<>();
		for (GenericEdge edge : orderedCapsuleEdges) {
			if (edge != e) {
				genericEdgesWithoutE.add(edge);
			}
		}
		return !(isConnected(genericEdgesWithoutE, graph));
	}

	/**
	 * 
	 * starts with an arbitrary CapsuleEdge, put this CapsuleEdge to the
	 * successfulTestedCapsuleEdges and its start and destination node to nodes
	 * 
	 * searches in all remaining CapsuleEdge for a CapsuleEdge which start or
	 * destination Nodes are already in nodes found no such edge -->
	 * notConnected --> retrun false found one -- > start search again
	 * 
	 * no remaining CapsuleEdges --> isConnected --> retrun true
	 */

	public static boolean isConnected(Set<GenericEdge> testCapsuleEdgeSet,
			DirectedGraph<GenericNode, GenericEdge> graph) {
		if (testCapsuleEdgeSet.size() == 1) {
			return true;
		}

		Set<GenericNode> nodes = new HashSet<>();
		Set<GenericEdge> successfulTestedCapsuleEdges = new HashSet<>();

		Iterator<GenericEdge> testgenericEdgesetIterator = testCapsuleEdgeSet
				.iterator();
		if (testgenericEdgesetIterator.hasNext()) {
			GenericEdge e = testgenericEdgesetIterator.next();
			successfulTestedCapsuleEdges.add(e);
			nodes.add(graph.getEdgeSource(e));
			nodes.add(graph.getEdgeTarget(e));
		}

		boolean foundSuccessfulTestedCapsuleEdges;

		while (testCapsuleEdgeSet.size() != successfulTestedCapsuleEdges.size()) {
			foundSuccessfulTestedCapsuleEdges = false;

			for (GenericEdge e : testCapsuleEdgeSet) {
				if (!successfulTestedCapsuleEdges.contains(e)) {
					GenericNode source = graph.getEdgeSource(e);
					GenericNode target = graph.getEdgeTarget(e);
					if (nodes.contains(source) || nodes.contains(target)) {
						nodes.add(source);
						nodes.add(target);
						successfulTestedCapsuleEdges.add(e);
						foundSuccessfulTestedCapsuleEdges = true;
					}
				}
			}
			if (!foundSuccessfulTestedCapsuleEdges) {
				return false;
			}
		}
		return true;
	}

	public String getLabel() {
		return label;
	}

	/**
	 * 
	 * @param onlyThisCapsuleEdges
	 *            the CapsuelEdges this fragment should be extended with (a
	 *            Fragment could only be extended with a CapsuleEdge, to which
	 *            its corresponding graph is connected to)
	 * @return the set of all fragments, which are created when this fragments
	 *         is extended with one of the CapsuleEdges from
	 *         onlyThisCapsuleEdges
	 */
	public Set<Fragment> extensOp(Set<GenericEdge> onlyThisCapsuleEdges) {
		Set<Fragment> res = new HashSet<>();

		for (GenericEdge genericEdge : onlyThisCapsuleEdges) {
			if (!orderedCapsuleEdges.contains(genericEdge)) {
				if ((graph.containsEdge(genericEdge))
						&& (isConnectedTo(genericEdge, orderedCapsuleEdges,
								graph))) {
					Set<GenericEdge> tempCapsuleEdges = new HashSet<>(orderedCapsuleEdges);
					tempCapsuleEdges.add(genericEdge);
					Fragment temp = new Fragment(tempCapsuleEdges, model, graph);
					res.add(temp);

				}
			}
		}
		return res;
	}

	/**
	 * @return a fragment that is created, when this fragment would be extended
	 *         with genericEdge
	 */
	public Fragment extensOp(GenericEdge genericEdge) {

		if (!orderedCapsuleEdges.contains(genericEdge)) {
			if ((graph.containsEdge(genericEdge))
					&& (isConnectedTo(genericEdge, orderedCapsuleEdges, graph))) {
				Set<GenericEdge> tempCapsuleEdges = new HashSet<>(orderedCapsuleEdges);
				tempCapsuleEdges.add(genericEdge);

				return new Fragment(tempCapsuleEdges, model, graph);
			}
		}
		return null;
	}

	private boolean isConnectedTo(GenericEdge e,
                                  List<GenericEdge> listCapsuleEdges,
                                  DirectedGraph<GenericNode, GenericEdge> graph) {
		
			if (e.getOriginalReference().model != model.getModel()) {
				return false;
			}
		

		Set<GenericNode> nodes = new HashSet<>();
		for (GenericEdge edge : listCapsuleEdges) {
			nodes.add(graph.getEdgeSource(edge));
			nodes.add(graph.getEdgeTarget(edge));
		}
		if (nodes.contains(graph.getEdgeSource(e))) {
			return true;
		}
		return nodes.contains(graph.getEdgeTarget(e));
	}

	private DirectedGraph<GenericNode, GenericEdge> getFragmentAsGraph(
			Set<GenericEdge> genericEdges,
			DirectedGraph<GenericNode, GenericEdge> graph) {
		DirectedGraph<GenericNode, GenericEdge> fragmentGraph
				= new DefaultDirectedGraph<>(
				GenericEdge.class);

		for (GenericEdge genericEdge : genericEdges) {
			GenericNode source = graph.getEdgeSource(genericEdge);
			GenericNode target = graph.getEdgeTarget(genericEdge);
			fragmentGraph.addVertex(source);
			fragmentGraph.addVertex(target);
			fragmentGraph.addEdge(source, target, genericEdge);
		}

		return fragmentGraph;
	}

}
