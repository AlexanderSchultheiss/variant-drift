package de.hub.mse.variantdrift.clone.escan;

import de.uni_marburg.fb12.swt.cloneDetection.atl.Link;
import de.uni_marburg.fb12.swt.cloneDetection.atl.escan.LabelCreator;
import org.jgrapht.graph.DefaultEdge;

/**
 * 
 * Links are
 * capsuled in CapsuleEdges
 *
 */
@SuppressWarnings("serial")
public class CapsuleEdge extends DefaultEdge {
	private Link originalEdge;
	private String label;

	private CapsuleEdge() {
		super();
	}

	@Override
	public int hashCode() {
			return originalEdge.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		CapsuleEdge e = (CapsuleEdge) o;
				return originalEdge == e.getOriginalEdge();
	}

	@Override
	public CapsuleEdge clone() {
			return new CapsuleEdge(originalEdge, label);
	}


	public CapsuleEdge(Link link, String label) {
		super();
		this.originalEdge = link;
		this.label = label;
	}



//	public MatchedRule getRule() {
//			return originalEdge.ge.getGraph().getRule();
//	}

	public Link getOriginalEdge() {
			return originalEdge;
	}

	@Override
	public String toString() {
			String res = originalEdge.getSource().toString() + " -> "
					+ LabelCreator.getCapsuleEdgeToString(this) + " -> "
					+ originalEdge.getTarget().toString() + "\t";
			return res;
	}

	public String getLabel() {
		return label;
	}
}
