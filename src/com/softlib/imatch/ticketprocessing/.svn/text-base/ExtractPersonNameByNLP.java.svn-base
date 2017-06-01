package com.softlib.imatch.ticketprocessing;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;

import com.softlib.imatch.ITicket;
import com.softlib.imatch.MatcherException;
import com.softlib.imatch.common.LogUtils;
import com.softlib.imatch.common.StringUtils;
import com.softlib.imatch.dictionary.ITechnicalTermsContainer;
import com.softlib.imatch.dictionary.TechnicalDictionaryTerm;
import com.softlib.imatch.dictionary.TechnicalTermSource;
import com.softlib.imatch.dictionary.VendorList;

public class ExtractPersonNameByNLP extends NlpBaseTicketProcessStep {

	static private final int MAX_NAME_LENGTH = 12;
	private List<String> extractedTerms;

	private static Logger log = Logger.getLogger(ExtractPersonNameByNLP.class);
	
	public ExtractPersonNameByNLP() {
		LogUtils.debug(log, "Extract Person Name by NLP step constructor");
	}

	public void run(String fieldName,ITicket ticket,ITechnicalTermsContainer container, StepContext context) throws MatcherException {

		container.startSession(fieldName,context.getTempTicketId(), getStepName());

		String nlpData = context.getNlpData();
		if (nlpData==null) {
			nlpData = getData(fieldName,ticket,context);
			if (StringUtils.isEmpty(nlpData))
				return;
			context.setNlpData(nlpData);
		}
		
		String[] sentences = context.getSentences();
		if (sentences==null) {
			sentences = extractSentences(context, nlpData);
			context.setSentences(sentences);
		}

    	TechnicalTermSource source = container.addSource(getStepName());
    	source.setAssociationRank(100);

    	extractedTerms = new ArrayList<String>();
    	
		for (String sentence : sentences) {
	
			String sent = sentence.replaceAll("\n", " ");
			String text = sent.toLowerCase();
			
			for (String name : checkPattern(text, "hi ", ','))
				addNameTerm(name,source,container);
			for (String name : checkPattern(text, "hello ", ','))
				addNameTerm(name,source,container);
			for (String name : checkPattern(text, "dear ", ','))
				addNameTerm(name,source,container);

			for (String name : checkPattern(text, "thanks,", ' '))
				addNameTerm(name,source,container);
			for (String name : checkPattern(text, "regards,", ' '))
				addNameTerm(name,source,container);
			for (String name : checkPattern(text, "thanks,", '.'))
				addNameTerm(name,source,container);
			for (String name : checkPattern(text, "regards,", '.'))
				addNameTerm(name,source,container);
			
			for (String name : checkPattern(text, "my name ", '.'))
				addNameTerm(name,source,container);
			for (String name : checkPattern(text, "my name is ", '.'))
				addNameTerm(name,source,container);

			for (String name : getEmailPatternNames("From: ",sentence))
				addNameTerm(name,source,container);
			for (String name : getEmailPatternNames("To: ",sentence))
				addNameTerm(name,source,container);
			for (String name : getEmailPatternNames("Cc: ",sentence))
				addNameTerm(name,source,container);

		}
		
		container.endSession((float)1.0, null, true);
		LogUtils.debug(log, "Extract #%s# {%s} Terms:%s", ticket.getId(),fieldName,extractedTerms);
	}

    static private List<String> checkPattern(String text,String pattern,char endChar) {		
		List<String> rc = new ArrayList<String>();
 		
		int startIdx = text.indexOf(pattern);
		if (startIdx<0)
			return rc;
	    text = text.substring(startIdx+pattern.length());
		
		int charIdx = text.indexOf(endChar);
		if (charIdx < 0)
			return rc;
		if (charIdx > (MAX_NAME_LENGTH+pattern.length()) )
			return rc;
		
		String name = text.substring(0,charIdx);
		
        int charIdx2 = text.indexOf(endChar, charIdx+1);
        if (charIdx2>0 && charIdx2<=MAX_NAME_LENGTH) {
        	String name2 = text.substring(charIdx+1,charIdx2);
        	name = name + " " + name2;
        }
			
        rc.addAll(splitName(name));
		
        return rc;
	}
		
    private void addNameTerm(String text,TechnicalTermSource source,ITechnicalTermsContainer termsContainer) {

    	text = text.replaceAll(", ", " ");
    	text = text.replaceAll(",", " ");
    	text = text.replaceAll("\r", " ");
    	String tempText = " "+text.toLowerCase()+" ";
    	if (tempText.contains(" support ")|| 
    		tempText.contains(" at ")     || 
    		tempText.contains(" the ")    || 
    		tempText.contains(" this ")   || 
    	    tempText.contains(" team ")   || 
    		tempText.contains(" user ")   || 
    		tempText.contains(" users ")  || 
    		tempText.contains(" group ")  || 
    		tempText.contains(" all ")    || 
    		tempText.contains(" email "))
    		return;
    	VendorList vendorList = VendorList.getInstance();
    	if(vendorList.getTermVendor(tempText.trim()) != null)
    		return;
    	if (isNotInWordnet(text)) {
    		TechnicalDictionaryTerm term = addTerm(text,termsContainer);
    		extractedTerms.add(text);
    		term.setTermSource(source);	
    	}
    }

	static private boolean isName(String name) {
		String rc = name;
		rc = rc.replaceAll("[.=:~%?#!\\*\\+\\$]", "");
		rc = rc.replaceAll("[\\(\\)\\[\\]]", "");
		rc = rc.replaceAll("[><]", "");
		rc = rc.replaceAll("[0-9]", "");
		rc = rc.replaceAll("-", "");
		rc = rc.replaceAll("'", "");
		rc = rc.replaceAll("[\\/]", "");
		rc = rc.replaceAll("[\\\\]", "");
		rc = rc.replaceAll("\n", " ");
		return rc.equals(name);
	}
	
	static private Set<String> splitName(String name) {
		name = name.replace("'", "");
		Set<String> rc = new HashSet<String>();
		StringTokenizer st = new StringTokenizer(name," ,");
		while (st.hasMoreTokens()) {
			String splitName = st.nextToken();
			if (isName(splitName))
				rc.add(splitName);
		}
		if (!rc.contains(name) && isName(name))
			rc.add(name);

		return rc;		
	}

	static private Set<String> getEmailAddrNames(String text) {
		int idx = text.indexOf(":");
		if (idx>0) 
			text = text.substring(idx+1);
		idx = text.indexOf("@");
		if (idx<0)
			return new HashSet<String>();
		text = text.substring(0,idx);
		text = text.replace(".", " ");	
		return splitName(text);
	}
	
	//This method defined as public for unit test purposes only, don't use outside of this class
	static public Set<String> getEmailNames(String text) {
		Set<String> rc = new HashSet<String>();
		text = text.trim();
		String name = "";
		String addr = "";
		int idx = text.indexOf(" [");
		if (idx>0) {
			name = text.substring(0,idx);
			int idx2 = text.indexOf("]");
			if (idx2>idx) {
				addr = text.substring(idx+2,idx2);
				rc.addAll(getEmailAddrNames(addr));
			}
		}
		else {
			name = text;
		}
		//Check if all name parts are in form Capital Capital (first letter is capital and the rest are not)
		Set<String> nameParts = splitName(name);
		boolean validName = nameParts.size() == 0 ? false : true;
		for(String namePart : nameParts) {
			if(namePart.contains(" ") || namePart.length() == 0)
				//Check base parts only, don't check compound parts
				continue;
			validName = validName && !Character.isLowerCase(namePart.charAt(0));
			for(int c = 1; c < namePart.length(); ++c)
				validName = validName && (!Character.isLetter(namePart.charAt(c)) || Character.isLowerCase(namePart.charAt(c)));
		}		
		if(validName)
			rc.addAll(nameParts);
		else
			rc.add(name);
		return rc;
	}
	
	static private Set<String> getEmailPatternNames(String pattern,String text) {
		Set<String> rc = new HashSet<String>();
		int startIdx = text.indexOf(pattern);
		if (startIdx<0 || pattern.isEmpty())
			return rc;
	    text = text.substring(startIdx+pattern.length()-1);
	    StringTokenizer st = new StringTokenizer(text,";\n");
	    while (st.hasMoreTokens()) {
	    	String currToken = st.nextToken();
	    	rc.addAll(getEmailNames(currToken));
	    }
		return rc;
	}

	public String getStepName() {
		return "Persons Names";
	}

	
};
