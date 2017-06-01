package com.softlib.imatch.ticketprocessing;


import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.softlib.imatch.ITicket;
import com.softlib.imatch.MatcherException;
import com.softlib.imatch.RuntimeInfo;
import com.softlib.imatch.common.LogUtils;
import com.softlib.imatch.common.SentencesHistory;
import com.softlib.imatch.common.StringUtils;
import com.softlib.imatch.common.TracerFile;
import com.softlib.imatch.common.TracerFileLast;
import com.softlib.imatch.density.DensityData;
import com.softlib.imatch.density.ExtractDensityTerms;
import com.softlib.imatch.dictionary.ITechnicalTermsContainer;
import com.softlib.imatch.dictionary.TechnicalDictionary;
import com.softlib.imatch.dictionary.TechnicalDictionaryKey;
import com.softlib.imatch.dictionary.TechnicalDictionaryTerm;

public class ExtractDensityTermsByNLP extends NlpBaseTicketProcessStep {

	static private Logger log = Logger.getLogger(ExtractDensityTermsByNLP.class);
	static private Object lock = new Object();
	
	private TechnicalDictionary dictionary;
	private ExtractDensityTerms extractDensityTerms;	
	
	static private TracerFile matchFile = 
		TracerFileLast.create(TracerFileLast.Density,"Match",false);
	
	static private SentencesHistory history =  new SentencesHistory(matchFile,false);

	
	public ExtractDensityTermsByNLP() {
		LogUtils.debug(log, "Extract Density Terms By NLP step constructor");
		extractDensityTerms = new ExtractDensityTerms();
	}
	
	public void end() {
		history.print();
	}

	public void run(String fieldName,ITicket ticket,ITechnicalTermsContainer container, StepContext context) throws MatcherException {

		List<String> extractedTerms = new ArrayList<String>();
		synchronized (lock) {
			if (dictionary == null)  {
				dictionary = (TechnicalDictionary) RuntimeInfo.getCurrentInfo().getBean("dictionary");			
				extractDensityTerms.init(dictionary);
			}			
		}

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

		for (String sentence : sentences) {
			List<DensityData> result = extractDensityTerms.process(sentence);

			for (DensityData densityData : result) {
				TechnicalDictionaryKey termKey = new TechnicalDictionaryKey(densityData.getStemmedText());
				TechnicalDictionaryTerm term = container.addTerm(termKey);
				if (term!=null) {
					extractedTerms.add(term.getTermText());
					history.put(densityData.getText(),sentence);
				}
			}
		}
		container.endSession((float)1.0, null, true);
		LogUtils.debug(log, "Extract #%s# {%s} Terms:%s", ticket.getId(),fieldName,extractedTerms);
	}

	public String getStepName() {
		return "Density";
	}

	
};
