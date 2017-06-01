package com.softlib.imatch.dictionary;

public class TechnicalDictionaryFrequency {
	private int leftMargin;
	private int rightMargin; 
	private float leftMarginPercent;
	private float rightMarginPercent;
	private int	totalNumTickets;
	
	public TechnicalDictionaryFrequency(int leftMargin, int rightMargin, float leftMarginPercent, float rightMarginPercent) {
		this.leftMargin = leftMargin;
		this.rightMargin = rightMargin;
		this.leftMarginPercent = leftMarginPercent;
		this.rightMarginPercent = rightMarginPercent;
	}
	
	public TechnicalDictionaryFrequency() {
		this(2,1000000,(float)0.1,(float)0.9);
	}
	
	public void setTotalNumTickets(int totalNumTickets) {
		this.totalNumTickets = totalNumTickets;
	}
	
	public boolean isLow(Integer frequency) {
		if ( frequency <= 1)
			return true;
    //DrorB:frequency count totalNumTickets
//		if((frequency < leftMargin) || 
//		   (frequency > rightMargin) ||
//		   (1.0 * frequency < leftMarginPercent * totalNumTickets) || 
//		   (1.0 * frequency > rightMarginPercent * totalNumTickets) ) 
//			return true;
		return false;
	}
	
	public String add2WhereString(String string,String freqStr) { 
		return (string.equals("") ? "" : string + " and ")  +
		        freqStr + ">" +  Integer.toString(1);
	}

}
