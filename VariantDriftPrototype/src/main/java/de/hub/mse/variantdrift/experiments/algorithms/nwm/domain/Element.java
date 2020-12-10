package de.hub.mse.variantdrift.experiments.algorithms.nwm.domain;

import java.util.*;

import de.hub.mse.variantdrift.experiments.algorithms.nwm.common.AlgoUtil;
import org.apache.poi.ss.usermodel.Row;

public class Element {
	private LinkedList<String> properties;
	private String label;
	private String UUID;
	
	private ArrayList<Element> basedUponElements;
	private Tuple containingTuple;
	
	private String modelId;
	
	private final static String alphabet = "abcdefghijklmnopqrstuvwxyz1234";
	
	private String identifyingLabel;
	private static int ID = 0;
	
	private int id = ID++;
	private boolean isRaw;
	
	private static Random random = new Random(System.currentTimeMillis()+1165);
	
	public Element(String id){
		modelId = id;
		properties = new LinkedList<>();
		basedUponElements = new ArrayList<Element>();
		if(! modelId.equals(AlgoUtil.NO_MODEL_ID)){
			basedUponElements.add(this);
			containingTuple = new Tuple();
			containingTuple.addElement(this);
		}
	}
	
	public Element(Tuple t){
		this(AlgoUtil.NO_MODEL_ID);
		label = "";
		for(Element e:t.getRealElements()){
			properties.addAll(e.getProperties());
			basedUponElements.add(e);
			label = label + e.getLabel()+"+";
		}
		containingTuple = t;
		// TODO: Figure out what is up with this piece of code
		if(AlgoUtil.COMPUTE_RESULTS_CLASSICALLY){
			label =label+ properties.toString().replace(" ", "");
		}
		else
			label = "{"+t.toString()+"}";

	}
	
	public Element(int l, Model m, int commonVacabularyMin, int diffVacabularyMin){
		this(m.getId());
		
	   
	    for (int i = 0; i < l; i++) {
	        this.addProperty(pickRandomProperty(commonVacabularyMin, diffVacabularyMin));
	    }
	    label = properties.toString().replace(" ", "");
	}
	
	public Element(String UUID, String lbl, String props, String mId){ // used when read from file
		this(mId);
		this.UUID = UUID;
		String[]pr = props.split(";");
		for(int i=0;i<pr.length;i++){
			String property = pr[i];
			if(property.startsWith("\""))
				property = property.substring(1);
			if(property.endsWith("\""))
				property = property.substring(0,property.length()-1);
			if (property.trim().isEmpty()) {
				continue;
			}
			// We do not want the properties to be lower case, because then properties might be mapped that should not
			// this.addProperty(property.toLowerCase());
			this.addProperty(property);
		}

		label = lbl;
	}

	public Element(String UUID, String lbl, LinkedList<String> properties, String modelId) {
		this(modelId);
		this.UUID = UUID;
		this.label = lbl;
		this.properties = properties;
		this.modelId = modelId;
	}
	
	public void writeElementToRow(Row r){
		r.createCell(0).setCellValue(modelId);
		r.createCell(1).setCellValue(getLabel());
		r.createCell(2).setCellValue(getPropertiesAsString(";"));
	}
	
	//julia here
	private String pickRandomProperty(int commonVacabulary, int diffVacabulary){
		int whichAlphabet = random.nextInt(2);
		if(whichAlphabet == 0){
			// int n = alphabet.length();
			return ""+random.nextInt(commonVacabulary);//alphabet.charAt(random.nextInt(n));
		}
		else{
			return  ""+(100+random.nextInt(diffVacabulary));
		}
	}
	
	private String getPropertiesAsString(String sep) {
		List<String> props = getProperties();
		StringBuilder sb = new StringBuilder();
		for(String prop:props){
			sb.append(prop);
			sb.append(sep);
		}
		sb.deleteCharAt(sb.length()-1);
		return sb.toString();
	}

	public String getIdentifyingLabel(){
		if(identifyingLabel == null){
			ArrayList<Element> elems = getBasedUponElements();
			ArrayList<String> fusedProps = new ArrayList<String>();
			for(Element e:elems){
				fusedProps.addAll(e.getProperties());
			}
			Collections.sort(fusedProps);
			identifyingLabel = fusedProps.toString();
		}
		return identifyingLabel;
	}
	
	public int getId(){
		return id;
	}
	
	public Tuple getContaingTuple(){
		return containingTuple;
	}
	
	public String getLabel(){
		return label;
	}

	public String getUUID() {
		return UUID;
	}
	
	public String getPrintLabel(){
		return label.replaceAll(",", " ");
	}
	
	public ArrayList<Element> getBasedUponElements(){
		if(AlgoUtil.COMPUTE_RESULTS_CLASSICALLY){
			ArrayList<Element> retVal =  new ArrayList<Element>();
			retVal.add(this);
			return retVal;
			
		}
		return basedUponElements;
	}
	
	public ArrayList<Element> getConstructingElements(){
		return basedUponElements;
	}
	
	public String getModelId(){
		return modelId;
	}
	
	public void setModelId(String mId){
		if(modelId == AlgoUtil.NO_MODEL_ID)
			modelId = mId;
	}

	public void setLabel(String label) {
		this.label = label;
	}
	
//	@Override
//	public boolean equals(Object o) {
//		if (o instanceof Element){
//			Element e = (Element)o;
//			
//			return e.getModelId() == modelId && properties.equals(((Element)o).getProperties());
//		}
//		return false;
//	}
	
	public List<String> getProperties(){
		return this.properties;
	}

	public void setProperties(LinkedList<String> properties) {
		this.properties = properties;
	}

	public void addProperty(String p){
		properties.add(p);
	}
	
	public int getSize(){
		return properties.size();
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(label);//toPrint());
		return sb.append("<").append(modelId).append(">").toString();
	}
	
	public String toPrint(){
		StringBuilder sb = new StringBuilder();
		for (String s : properties) {
			sb.append(s);
			sb.append(";");
		}
		sb.deleteCharAt(sb.length()-1);
		return sb.toString();
	}

	public void setAsRaw() {
		this.isRaw = true;
	}
	
	public boolean isRaw(){
		return this.isRaw;
	}
	
	
}

