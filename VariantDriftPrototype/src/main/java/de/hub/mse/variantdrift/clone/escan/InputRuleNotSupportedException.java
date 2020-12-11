package de.hub.mse.variantdrift.clone.escan;


public class InputRuleNotSupportedException extends Exception {
	String message; 
	
	public InputRuleNotSupportedException(String string) {
		message = string;
	}
	
	@Override
	public String getMessage() {
		return message;
	}

}
