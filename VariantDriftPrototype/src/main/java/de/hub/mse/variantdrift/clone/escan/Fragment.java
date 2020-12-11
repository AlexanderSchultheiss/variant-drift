package de.hub.mse.variantdrift.clone.escan;

import aatl.MatchedRule;
import de.uni_marburg.fb12.swt.cloneDetection.atl.Link;
import de.uni_marburg.fb12.swt.cloneDetection.atl.escan.CanonicalLabelForFragmentCreator;
import de.uni_marburg.fb12.swt.cloneDetection.atl.escan.CapsuleEdge;
import org.eclipse.emf.ecore.EObject;
import org.jgrapht.DirectedGraph;
import org.jgrapht.graph.DefaultDirectedGraph;

import java.util.*;

/**
 * fragment from the ModelCd-eScan article
 *
 */
public class Fragment {
	private final List<de.uni_marburg.fb12.swt.cloneDetection.atl.escan.CapsuleEdge> orderedCapsuleEdges;
	private final MatchedRule rule;
	private final DirectedGraph<EObject, de.uni_marburg.fb12.swt.cloneDetection.atl.escan.CapsuleEdge> graph;
	private String label;

	private de.uni_marburg.fb12.swt.cloneDetection.atl.escan.CapsuleEdge lastNotDisconnectingCapsuleEdge;

	@Override
	public int hashCode() {
		int res = rule.getName().hashCode();
		res = res + label.hashCode();
		return res;
	}

	@Override
	public boolean equals(Object o) {
		Fragment fragment = (Fragment) o;

		if (this.rule != fragment.rule) {
			return false;
		}
		if (!fragment.orderedCapsuleEdges.equals(orderedCapsuleEdges)) {
			return false;
		}

		return true;
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
		String res = rule.getName() + "\n Label: " + label;
		return res;
	}

	/**
	 * 
	 * @return the source- and target-Nodes of the Edges of this Fragment
	 */
	public List<EObject> getNodes() {
		List<EObject> nodes = new ArrayList<EObject>();
		for (de.uni_marburg.fb12.swt.cloneDetection.atl.escan.CapsuleEdge capsuleEdge : orderedCapsuleEdges) {
			if (!nodes.contains(graph.getEdgeSource(capsuleEdge)))
				nodes.add(graph.getEdgeSource(capsuleEdge));
			if (!nodes.contains(graph.getEdgeTarget(capsuleEdge)))
				nodes.add(graph.getEdgeTarget(capsuleEdge));
		}
		return nodes;
	}

	/**
	 * 
	 * @return all the CapsuleEdges this Fragment exist of
	 */
	public List<de.uni_marburg.fb12.swt.cloneDetection.atl.escan.CapsuleEdge> getCapsuleEdges() {
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
	public Set<Link> getOriginalEdges() {
		Set<Link> res = new HashSet<Link>();
		for (de.uni_marburg.fb12.swt.cloneDetection.atl.escan.CapsuleEdge e : orderedCapsuleEdges) {
				res.add(e.getOriginalEdge());
		}
		return res;
	}


	/**
	 * 
	 * @return the rule this Fragment belongs to
	 */
	public MatchedRule getRule() {
		return rule;
	}

	/**
	 * 
	 * @return the whole computation graph this Fragment belongs to
	 */
	public DirectedGraph<EObject, de.uni_marburg.fb12.swt.cloneDetection.atl.escan.CapsuleEdge> getGraph() {
		return graph;
	}

	public Fragment(Set<de.uni_marburg.fb12.swt.cloneDetection.atl.escan.CapsuleEdge> capsuleEdges, MatchedRule rule,
                    DirectedGraph<EObject, de.uni_marburg.fb12.swt.cloneDetection.atl.escan.CapsuleEdge> graph) {
		this.rule = rule;
		this.graph = graph;
		DirectedGraph<EObject, de.uni_marburg.fb12.swt.cloneDetection.atl.escan.CapsuleEdge> fragmentGraph = getFragmentAsGraph(
				capsuleEdges, graph);
		Map<String, List<de.uni_marburg.fb12.swt.cloneDetection.atl.escan.CapsuleEdge>> labelToOrderedCapsuleEdges = CanonicalLabelForFragmentCreator
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
		if (biggerFragment.orderedCapsuleEdges.size() < orderedCapsuleEdges
				.size()) {
			//System.out.println("Fragment.java: biggerFragment.size(): "
			//		+ biggerFragment.orderedCapsuleEdges.size()
			//		+ " this.size() " + orderedCapsuleEdges.size());
		}
		for (de.uni_marburg.fb12.swt.cloneDetection.atl.escan.CapsuleEdge e : orderedCapsuleEdges) {
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
		if (this.rule != f.getRule()) {
			return false;
		}

		Set<EObject> nodesThis = new HashSet<EObject>();
		Set<EObject> nodesF = new HashSet<EObject>();
		DirectedGraph<EObject, de.uni_marburg.fb12.swt.cloneDetection.atl.escan.CapsuleEdge> graphF = f.getGraph();

		for (de.uni_marburg.fb12.swt.cloneDetection.atl.escan.CapsuleEdge e : orderedCapsuleEdges) {
			nodesThis.add(graph.getEdgeSource(e));
			nodesThis.add(graph.getEdgeTarget(e));
		}

		for (de.uni_marburg.fb12.swt.cloneDetection.atl.escan.CapsuleEdge e : f.orderedCapsuleEdges) {
			nodesF.add(graphF.getEdgeSource(e));
			nodesF.add(graphF.getEdgeTarget(e));
		}

		for (EObject  n : nodesThis) {
			if (nodesF.contains(n)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 
	 * @param ckp1
	 * @return
	 */
	public boolean isGeneratingParent(Fragment ckp1) {
		if (ckp1.getRule() != rule) {
			return false;
		}
		// determine the edge the fragments differ from each other
		de.uni_marburg.fb12.swt.cloneDetection.atl.escan.CapsuleEdge additionalEdge = null;
		boolean foundAdditionalEdge = false;
		for (de.uni_marburg.fb12.swt.cloneDetection.atl.escan.CapsuleEdge e : ckp1.orderedCapsuleEdges) {
			if (!orderedCapsuleEdges.contains(e)) {
				if (foundAdditionalEdge) {
					return false;
				}
				additionalEdge = e;
				foundAdditionalEdge = true;
			}
		}

		if (additionalEdge == ckp1.lastNotDisconnectingCapsuleEdge()) {
			return true;
		}
		return false;
	}

	private de.uni_marburg.fb12.swt.cloneDetection.atl.escan.CapsuleEdge lastNotDisconnectingCapsuleEdge() {
		if (lastNotDisconnectingCapsuleEdge != null) {
			return lastNotDisconnectingCapsuleEdge;
		}

		for (de.uni_marburg.fb12.swt.cloneDetection.atl.escan.CapsuleEdge testEdge : orderedCapsuleEdges) {
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
	 * @param e
	 * @return
	 */
	private boolean isTheOnlyConnectingCapsuleEdge(de.uni_marburg.fb12.swt.cloneDetection.atl.escan.CapsuleEdge e) {
		Set<de.uni_marburg.fb12.swt.cloneDetection.atl.escan.CapsuleEdge> capsuleEdgesWithoutE = new HashSet<de.uni_marburg.fb12.swt.cloneDetection.atl.escan.CapsuleEdge>();
		for (de.uni_marburg.fb12.swt.cloneDetection.atl.escan.CapsuleEdge edge : orderedCapsuleEdges) {
			if (edge != e) {
				capsuleEdgesWithoutE.add(edge);
			}
		}
		return !(isConnected(capsuleEdgesWithoutE, graph));
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
	 * 
	 * @param testcapsuleEdgeset
	 * @param graph
	 * @return
	 */

	public static boolean isConnected(Set<de.uni_marburg.fb12.swt.cloneDetection.atl.escan.CapsuleEdge> testCapsuleEdgeSet,
			DirectedGraph<EObject, de.uni_marburg.fb12.swt.cloneDetection.atl.escan.CapsuleEdge> graph) {
		if (testCapsuleEdgeSet.size() == 1) {
			return true;
		}

		Set<EObject> nodes = new HashSet<EObject>();
		Set<de.uni_marburg.fb12.swt.cloneDetection.atl.escan.CapsuleEdge> successfulTestedCapsuleEdges = new HashSet<de.uni_marburg.fb12.swt.cloneDetection.atl.escan.CapsuleEdge>();

		Iterator<de.uni_marburg.fb12.swt.cloneDetection.atl.escan.CapsuleEdge> testcapsuleEdgesetIterator = testCapsuleEdgeSet
				.iterator();
		if (testcapsuleEdgesetIterator.hasNext()) {
			de.uni_marburg.fb12.swt.cloneDetection.atl.escan.CapsuleEdge e = testcapsuleEdgesetIterator.next();
			successfulTestedCapsuleEdges.add(e);
			nodes.add(graph.getEdgeSource(e));
			nodes.add(graph.getEdgeTarget(e));
		}

		boolean foundSuccessfulTestedCapsuleEdges = false;

		while (testCapsuleEdgeSet.size() != successfulTestedCapsuleEdges.size()) {
			foundSuccessfulTestedCapsuleEdges = false;

			for (de.uni_marburg.fb12.swt.cloneDetection.atl.escan.CapsuleEdge e : testCapsuleEdgeSet) {
				if (!successfulTestedCapsuleEdges.contains(e)) {
					EObject source = graph.getEdgeSource(e);
					EObject target = graph.getEdgeTarget(e);
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
	public Set<Fragment> extensOp(Set<de.uni_marburg.fb12.swt.cloneDetection.atl.escan.CapsuleEdge> onlyThisCapsuleEdges) {
		Set<Fragment> res = new HashSet<Fragment>();

		for (de.uni_marburg.fb12.swt.cloneDetection.atl.escan.CapsuleEdge capsuleEdge : onlyThisCapsuleEdges) {
			if (!orderedCapsuleEdges.contains(capsuleEdge)) {
				if ((graph.containsEdge(capsuleEdge))
						&& (isConnectedTo(capsuleEdge, orderedCapsuleEdges,
								graph))) {
					Set<de.uni_marburg.fb12.swt.cloneDetection.atl.escan.CapsuleEdge> tempCapsuleEdges = new HashSet<de.uni_marburg.fb12.swt.cloneDetection.atl.escan.CapsuleEdge>();
					tempCapsuleEdges.addAll(orderedCapsuleEdges);
					tempCapsuleEdges.add(capsuleEdge);
					Fragment temp = new Fragment(tempCapsuleEdges, rule, graph);
					res.add(temp);

				}
			}
		}
		return res;
	}

	/**
	 * 
	 * @param capsuleEdge
	 * @return a fragment that is created, when this fragment would be extended
	 *         with capsuleEdge
	 */
	public Fragment extensOp(de.uni_marburg.fb12.swt.cloneDetection.atl.escan.CapsuleEdge capsuleEdge) {

		if (!orderedCapsuleEdges.contains(capsuleEdge)) {
			if ((graph.containsEdge(capsuleEdge))
					&& (isConnectedTo(capsuleEdge, orderedCapsuleEdges, graph))) {
				Set<de.uni_marburg.fb12.swt.cloneDetection.atl.escan.CapsuleEdge> tempCapsuleEdges = new HashSet<de.uni_marburg.fb12.swt.cloneDetection.atl.escan.CapsuleEdge>();
				tempCapsuleEdges.addAll(orderedCapsuleEdges);
				tempCapsuleEdges.add(capsuleEdge);

				Fragment res = new Fragment(tempCapsuleEdges, rule, graph);
				return res;

			}

		}
		return null;
	}

	private boolean isConnectedTo(de.uni_marburg.fb12.swt.cloneDetection.atl.escan.CapsuleEdge e,
                                  List<de.uni_marburg.fb12.swt.cloneDetection.atl.escan.CapsuleEdge> listCapsuleEdges,
                                  DirectedGraph<EObject, de.uni_marburg.fb12.swt.cloneDetection.atl.escan.CapsuleEdge> graph) {
		
			if (e.getOriginalEdge().getRule() != rule) {
				return false;
			}
		

		Set<EObject> nodes = new HashSet<EObject>();
		for (de.uni_marburg.fb12.swt.cloneDetection.atl.escan.CapsuleEdge edge : listCapsuleEdges) {
			nodes.add(graph.getEdgeSource(edge));
			nodes.add(graph.getEdgeTarget(edge));
		}
		if (nodes.contains(graph.getEdgeSource(e))) {
			return true;
		}
		if (nodes.contains(graph.getEdgeTarget(e))) {
			return true;
		}
		return false;
	}

	private DirectedGraph<EObject, de.uni_marburg.fb12.swt.cloneDetection.atl.escan.CapsuleEdge> getFragmentAsGraph(
			Set<de.uni_marburg.fb12.swt.cloneDetection.atl.escan.CapsuleEdge> capsuleEdges,
			DirectedGraph<EObject, de.uni_marburg.fb12.swt.cloneDetection.atl.escan.CapsuleEdge> graph) {
		DirectedGraph<EObject, de.uni_marburg.fb12.swt.cloneDetection.atl.escan.CapsuleEdge> fragmentGraph
				= new DefaultDirectedGraph<EObject, de.uni_marburg.fb12.swt.cloneDetection.atl.escan.CapsuleEdge>(
				de.uni_marburg.fb12.swt.cloneDetection.atl.escan.CapsuleEdge.class);

		for (CapsuleEdge capsuleEdge : capsuleEdges) {
			EObject source = graph.getEdgeSource(capsuleEdge);
			EObject target = graph.getEdgeTarget(capsuleEdge);
			fragmentGraph.addVertex(source);
			fragmentGraph.addVertex(target);
			fragmentGraph.addEdge(source, target, capsuleEdge);
		}

		return fragmentGraph;
	}

}
