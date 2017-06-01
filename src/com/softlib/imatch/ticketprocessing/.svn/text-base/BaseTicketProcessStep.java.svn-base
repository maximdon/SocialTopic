package com.softlib.imatch.ticketprocessing;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.softlib.imatch.ITicket;
import com.softlib.imatch.RuntimeInfo;
import com.softlib.imatch.common.LogUtils;
import com.softlib.imatch.common.Pair;
import com.softlib.imatch.common.StringUtils;
import com.softlib.imatch.common.TracerFile;
import com.softlib.imatch.common.TracerFileLast;
import com.softlib.imatch.dbintegration.DBUtils;
import com.softlib.imatch.density.DensityData;
import com.softlib.imatch.dictionary.ITechnicalTermsContainer;
import com.softlib.imatch.dictionary.SynonymsRelation;
import com.softlib.imatch.dictionary.TechnicalDictionaryKey;
import com.softlib.imatch.dictionary.TechnicalDictionaryTerm;
import com.softlib.imatch.dictionary.VendorList;
import com.softlib.imatch.dictionary.Wordnet;

public abstract class BaseTicketProcessStep implements ITicketProcessStep {
	protected BaseTicketProcessStep parentStep;
	private static Logger log = Logger.getLogger(BaseTicketProcessStep.class);	
	
	public BaseTicketProcessStep() {
		super();
	}

	public BaseTicketProcessStep getParentStep()
	{
		return this.parentStep;
	}
	
	public void setParentStep(BaseTicketProcessStep parentStep)
	{
		this.parentStep = parentStep;
	}
	
	protected TechnicalDictionaryTerm createTerm(String termText, ITechnicalTermsContainer container) {
		return container.addTerm(new TechnicalDictionaryKey(termText));
	}
	
	protected TechnicalDictionaryTerm addTerm(String termText, ITechnicalTermsContainer container) {
		TechnicalDictionaryTerm newTerm = createTerm(termText,container);
		return newTerm;
	}
	
	protected static List<String> toStringList(List<TechnicalDictionaryTerm> terms)
	{
		ArrayList<String> result = new ArrayList<String>(terms.size());
		for(TechnicalDictionaryTerm term : terms)
			result.add(term.getTermStemmedText());
		return result;
	}
	
	protected boolean isNotInWordnet(String termText) {
		if(termText.contains(" "))
			//No need to check wordnet for phrases
			return true;
		//In WebApp mode new temp session should be created for Wordnet since the main session without Wordnet db attached.
		if(Wordnet.getInstance().containsWord(termText)) {			
			return false;
		}
		return true;
	}
	
	protected boolean isEligable(String termText) {
		return RuntimeInfo.getCurrentInfo().isWebAppMode() || (!Wordnet.getInstance().isStopWord(termText) && !isVendor(termText) && StringUtils.containsAtLeastOneLetter(termText));
	}
	
	protected boolean isVendor(String termText) {
		return VendorList.getInstance().contains(termText.trim());
	}
	
	public void end() {
	}

	protected String getData(String fieldName,ITicket ticket,StepContext context) {
		String cleanText = context.getCleanText();
		if(cleanText != null)
			return cleanText;
		String rc = DBUtils.fieldToString(ticket.getField(fieldName));
		if (StringUtils.isEmpty(rc))
			return "";
		return rc;
	}	
};
