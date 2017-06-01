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
import com.softlib.imatch.dictionary.ITechnicalTermsContainer;
import com.softlib.imatch.dictionary.TechnicalDictionary;
import com.softlib.imatch.dictionary.TechnicalDictionaryKey;
import com.softlib.imatch.dictionary.TechnicalDictionaryTerm;
import com.softlib.imatch.dictionary.TechnicalTermSource;
import com.softlib.imatch.proximity.ExtractProximityTerms;
import com.softlib.imatch.proximity.ProximityData;

public class ExtractProximityTermsByNLP extends NlpBaseTicketProcessStep {

	static private Logger log = Logger.getLogger(ExtractProximityTermsByNLP.class);
	static private Object lock = new Object();
	
	private TechnicalDictionary dictionary;
	private ExtractProximityTerms extractProximity;	
	
	static private TracerFile matchFile = 
		TracerFileLast.create(TracerFileLast.Proximity,"Match",false);
	
	static private SentencesHistory history =  new SentencesHistory(matchFile,true);

	
	public ExtractProximityTermsByNLP() {
		LogUtils.debug(log, "Extract Proximity Terms By NLP step constructor");
		extractProximity = new ExtractProximityTerms();
	}
	
	public void end() {
		history.print();
	}
		
	public void run(String fieldName,ITicket ticket,ITechnicalTermsContainer container, StepContext context) throws MatcherException {

		List<String> extractedTerms = new ArrayList<String>();
		synchronized (lock) {
			if (dictionary == null)  {
				dictionary = (TechnicalDictionary) RuntimeInfo.getCurrentInfo().getBean("dictionary");			
				extractProximity.init(dictionary);
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
			List<ProximityData> result = extractProximity.process(sentence);

			for (ProximityData proximity : result) {
				TechnicalDictionaryKey termKey = new TechnicalDictionaryKey(proximity.getText());
				TechnicalDictionaryTerm term = container.addTerm(termKey);
				if (term!=null) {
					extractedTerms.add(term.getTermText() + "(" + proximity.getRuleName() + ")");
					TechnicalTermSource source = container.addSource(getStepName());
					term.setCondTermSource(source,proximity.getProximitySource());
					for (TechnicalDictionaryTerm proxTerm : proximity.getTerms()) 
						container.reduceTermFreq(proxTerm);
					history.put(proximity.getText(),sentence);
				}
			}
		}
		container.endSession((float)1.0, null, true);
		LogUtils.debug(log, "Extract #%s# {%s} Terms:%s", ticket.getId(),fieldName,extractedTerms);
	}

	public String getStepName() {
		return "Proximity";
	}

	
};
