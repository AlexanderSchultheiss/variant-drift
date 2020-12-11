package de.hub.mse.variantdrift.clone.escan;

import aatl.Module;
import aatl.*;
import de.uni_marburg.fb12.swt.cloneDetection.atl.CloneMetricResults;
import de.uni_marburg.fb12.swt.cloneDetection.atl.Link;
import org.eclipse.emf.ecore.EObject;

import java.util.*;

public class CloneMatrix {
	private List<List<EObject>> nodeMatrix = new LinkedList<List<EObject>>();
	private List<List<Link>> edgeMatrix = new LinkedList<List<Link>>();

	public CloneMatrix() {
	}

	/**
	 * The order of the rows of edgeMatrix and attributeMatrix have to be
	 * according each other, means the k�ths row of both matrices have to belong
	 * to the same clone
	 * 
	 * @param rules
	 * @param edgeMatrix
	 * @param attributeMatrix
	 */
	public CloneMatrix(List<List<Link>> edgeMatrix, List<List<EObject>> nodeMatrix) {
		this.edgeMatrix = edgeMatrix;
		this.nodeMatrix = nodeMatrix;
	}

	/**
	 * 
	 * @return the rules of the clonematrices as a List
	 */
	public List<MatchedRule> getRuleList() {
		List<MatchedRule> allRules = new LinkedList<MatchedRule>();
		if (edgeMatrix.size() == 0)
			return allRules;
		if (edgeMatrix.get(0).size() != 0) {
			for (List<Link> row : edgeMatrix) {
				allRules.add(row.get(0).getRule());
			}
			return allRules;
		}
		return allRules;
	}

	/**
	 * 
	 * @return all Edges of this CloneMatrix
	 */
	public Set<Link> getAllEdges() {
		Set<Link> allEdges = new HashSet<Link>();
		for (List<Link> row : edgeMatrix) {
			allEdges.addAll(row);
		}
		return allEdges;
	}

	public Set<EObject> getAllNodes() {
		Set<EObject> allNodes = new HashSet<EObject>();

		for (Link e : getAllEdges()) {
			allNodes.add(e.getSource());
			allNodes.add(e.getTarget());
		}

		return allNodes;
	}

	public List<List<Link>> getEdgeMatrix() {
		return edgeMatrix;
	}

	public int getNumberOfCommonNodes() {
		return getAllNodes().size() / getRuleList().size();
	}

	/**
	 * 
	 * @return the number of cloned Edges (since this is Edges and not
	 *         CapsuleEdges, this means the former CapsuleEdges, which capsuled
	 *         Attributes are not inculded)
	 */
	public int getNumberOfCommonLinks() {
		if (edgeMatrix.size() != 0) {
			return edgeMatrix.get(0).size();
		}
		return 0;
	}

	@Override
	public int hashCode() {
		int res = 0;
		if (edgeMatrix != null) {
			res = res + edgeMatrix.hashCode();
		}
		return res;
	}

	@Override
	public boolean equals(Object o) {
		CloneMatrix c = (CloneMatrix) o;
		if (!this.edgeMatrix.equals(c.edgeMatrix)) {
			return false;
		}
		return true;
	}

	// @Override
	// public String toString() {
	// String res = "";
	// res = res + "\n" + "CloneMatrix - edgeMatrix: " + "\n"
	// + toStringEdgeMatrix(edgeMatrix);
	// return res;
	// }
	//

	public String toString() {
		StringBuilder sb = new StringBuilder();
		// sb.append(super.toString());
		sb.append("CG [Size ");
		sb.append(getSize());
		sb.append(", rules: ");
		sb.append(getRuleList().size());
		// sb.append(", modules: ");
		// sb.append(getInvolvedModules().size());
		sb.append(". Rules: ");
		Iterator<MatchedRule> it = getRuleList().iterator();
		while (it.hasNext()) {
			MatchedRule r = it.next();
			sb.append(((Module) r.eContainer()).getName());
			sb.append("::");
			sb.append(r.getName());
			if (it.hasNext())
				sb.append(", ");
		}
		sb.append("]");
		return sb.toString();
	}

	private List<Module> getInvolvedModules() {
		List<Module> result = new ArrayList<Module>();
		for (MatchedRule rule : getRuleList())
			if (!result.contains(rule.eContainer()))
				result.add((Module) rule.eContainer());
		return result;
	}

	public String getInvolvedNodesString() {
		StringBuilder result = new StringBuilder();
		Iterator<EObject> it = nodeMatrix.get(0).iterator();
		while (it.hasNext()) {
			EObject n = it.next();
			result.append(n);
			if (it.hasNext())
				result.append(", ");
		}
		return result.toString();
	}

	private String toStringEdgeMatrix(List<List<Link>> edgeMatrix) {
		String res = "";
		for (List<Link> row : edgeMatrix) {
			for (Link edge : row) {
				res = res + edge.toString() + "\t" + "*" + "\t";

			}
			res = res + "\n";
		}
		return res;
	}

	public int getSize() {
		return toMetrics().getSize();
	}

	public CloneMetricResults toMetrics() {
		if (metricResult == null) {
			int numberOfModules = getInvolvedModules().size();
			int numberOfRules = getRuleList().size();
			int numberOfInElements = 0;
			int numberOfFilters = 0;
			int numberOfVariables = 0;
			int numberOfOutElements = 0;
			int numberOfBindings = 0;

			if (nodeMatrix.isEmpty()) {
				metricResult = new CloneMetricResults(-1, -1, -1, -1, -1, -1, -1);
			} else {
				List<EObject> firstEntry = nodeMatrix.get(0);
				for (EObject o : firstEntry) {
					if (o instanceof InPatternElement)
						numberOfInElements++;
					else if (o instanceof OutPatternElement)
						numberOfOutElements++;
					else if (o instanceof Filter)
						numberOfFilters++;
					else if (o instanceof Variable)
						numberOfVariables++;
					else if (o instanceof Binding)
						numberOfBindings++;
				}
			}

			metricResult = new CloneMetricResults(numberOfModules, numberOfRules, numberOfFilters, numberOfVariables, numberOfInElements,
					numberOfOutElements, numberOfBindings);
		}
		return metricResult;
	}

	private CloneMetricResults metricResult = null;
}
