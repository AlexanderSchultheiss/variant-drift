package de.hub.mse.variantdrift.clone.escan;

import org.eclipse.emf.ecore.EObject;
import org.jgrapht.DirectedGraph;

import java.util.*;


/**
 * The top-level CloneDetection class.
 * 
 */
public abstract class CloneDetection extends AbstractCloneGroupDetector {
	// AbstractCloneGroupDetector:
	// protected Collection<Rule> rules;
	// protected Set<CloneGroupMapping> result;
	protected boolean DEBUG = false;
	
	
	/**
	 * The result of the CloneDetection.
	 */
	protected Set<de.uni_marburg.fb12.swt.cloneDetection.atl.escan.CloneMatrix> resultAsCloneMatrix = null;

	/**
	 * Rule to computation-Graph
	 */
	protected Map<MatchedRule, DirectedGraph<EObject, de.uni_marburg.fb12.swt.cloneDetection.atl.escan.CapsuleEdge>> ruleGraphMap = null;


	public CloneDetection(Set<Module> modules) {
		super(modules);
		initialize(rules);
	}
	
	public CloneDetection(List<MatchedRule> rules) {
		super(rules);
		initialize(rules);
	}

	private void initialize(List<MatchedRule> rules) {
		ruleGraphMap = getRuleGraphMap(rules);
		resultAsCloneMatrix = new HashSet<de.uni_marburg.fb12.swt.cloneDetection.atl.escan.CloneMatrix>();
		this.result = null;
	}

	public abstract void detectCloneGroups();

	public CloneDetection(
			Map<MatchedRule, DirectedGraph<EObject, de.uni_marburg.fb12.swt.cloneDetection.atl.escan.CapsuleEdge>> ruleGraphMap,
			List<MatchedRule> rules) {
		super(rules);
		this.ruleGraphMap = ruleGraphMap;
		resultAsCloneMatrix = new HashSet<de.uni_marburg.fb12.swt.cloneDetection.atl.escan.CloneMatrix>();
	}

	public Set<de.uni_marburg.fb12.swt.cloneDetection.atl.escan.CloneMatrix> getResultAsCloneMatrix() {
		return resultAsCloneMatrix;
	}

	/**
	 * In CloneDetective and CloneDetectiveTupel: getSimilarityFunctionValue
	 * specifies how many terms are used
	 * 
	 * @return
	 */
	protected int getNumberOfTermsForCloneDetective() {
		// no special reason
		return 3;
	}


	/**
	 * Building up the computation graphs to each rule.
	 * 
	 * (It�s very important, especially for the combine algorithm, that the
	 * RuleGraphMap isn�t created a second time. (Due to the fact that the
	 * Attributes are converted into Nodes, and this Nodes are new created, the
	 * nodes of a new generated graph are equal to the Nodes of the former
	 * graph, but not identical, and JGraph tests for identity, means a new
	 * computation of ruleGraphMap could lead to a lot of errors.)
	 *
	 * @return rule to rule-computation-graph
	 */
	private Map<MatchedRule, DirectedGraph<EObject, CapsuleEdge>> getRuleGraphMap(
			List<MatchedRule> ruleSet) {
		de.uni_marburg.fb12.swt.cloneDetection.atl.escan.ExtendedIntegratedGraphConverter graphConverter = new ExtendedIntegratedGraphConverter();
		return graphConverter.getRuleToGraphWithAttributesMap(ruleSet);
		// return graphConverter.getRuleToGraphWithOutAttributesMap(ruleSet);
	}


	/**
	 * from org.eclipse.emf.henshin.variability.ui.clonedetector.
	 * AbstractCloneGroupDetector
	 * 
	 * @return
	 */
	public de.uni_marburg.fb12.swt.cloneDetection.atl.escan.CloneGroupDetectionResultAsCloneMatrix
				getResultAsCloneMatrixOrderedByNumberOfCommonElements() {
		List<de.uni_marburg.fb12.swt.cloneDetection.atl.escan.CloneMatrix> orderedResult = new ArrayList<de.uni_marburg.fb12.swt.cloneDetection.atl.escan.CloneMatrix>();
		orderedResult.addAll(resultAsCloneMatrix);
		Comparator<de.uni_marburg.fb12.swt.cloneDetection.atl.escan.CloneMatrix> comp = new Comparator<de.uni_marburg.fb12.swt.cloneDetection.atl.escan.CloneMatrix>() {
			@Override
			public int compare(de.uni_marburg.fb12.swt.cloneDetection.atl.escan.CloneMatrix arg0, CloneMatrix arg1) {
				return arg1.getNumberOfCommonLinks()
						- arg0.getNumberOfCommonLinks();
			}
		};
		Collections.sort(orderedResult, comp);
		return new CloneGroupDetectionResultAsCloneMatrix(orderedResult);
	}

	/**
	 * to ensure an uniform output
	 * 
	 * @param name
	 *            the name of the CloneDetection
	 * @return an output String
	 */
	public String startDetectCloneGroups(String name) {
		Date dateStartDetect = new Date();
		return ("\n" + name + " - start " + dateStartDetect.toString() + "\n");
	}

	/**
	 * for combine algorithm to ensure an uniform output
	 * 
	 * @param nameFirst
	 *            the name of the first CloneDetection
	 * @return an output String
	 */
	public String startConversionTempResult(String nameFirst) {
		Date date = new Date();
		return ("\n" + nameFirst + " end - start Conversion tempResult"
				+ date.toString() + "\n");
	}

	/**
	 * for combine algorithm to ensure an uniform output
	 * 
	 * @param nameSecond
	 *            the name of the second CloneDetection
	 * @return an output String
	 */
	public String moveOnToSecondAlgortihm(String nameSecond) {
		Date date = new Date();
		return ("\n" + "Conversion tempResult done - start " + nameSecond + " "
				+ date.toString() + "\n");
	}

	/**
	 * for combine algorithm CloneDetective with eScans group- and filter-steps
	 * to ensure an uniform output
	 * 
	 * @return an output String
	 */
	public String startGroupingResults() {
		Date date = new Date();
		return ("\n" + "start ModelCd-eScan group and filter steps "
				+ date.toString() + "\n");
	}

	/**
	 * to ensure an uniform output
	 * 
	 * @return an output String
	 */
	public String startConversion() {
		Date dateStartConversion = new Date();
		return ("\n"
				+ "CloneDetection itself done - start Conversion to CloneGroupMapping "
				+ dateStartConversion.toString() + "\n");
	}

	/**
	 * to ensure an uniform output
	 * 
	 * @param name
	 *            the name of the CloneDetection
	 * @param startTime
	 * @return an output String
	 */
	public String endDetectCloneGroups(String name, long startTime) {
		long end = System.currentTimeMillis();
		String res;
		Date dateEndDetect = new Date();
		res = (name + " - end " + dateEndDetect.toString() + "\n");
		res = res + getTime(startTime, end);
		res = res
				+ "------------------------------------------------------------"
				+ "\n";
		return res;
	}

	/**
	 * to ensure an uniform output convert the time difference to minutes and
	 * seconds
	 * 
	 * @param start
	 *            start time
	 * @param end
	 *            end time
	 * @return
	 */
	private String getTime(long start, long end) {
		long sec = (end - start) / 1000;
		if (sec < 60) {
			return ("Running time: " + sec + " sec." + "\n");
		}
		int min = (int) (sec / 60);
		sec = sec % 60;
		String res = ("Running time: " + min + " min. " + sec + " sec." + "\n");

		return res;
	}

}
