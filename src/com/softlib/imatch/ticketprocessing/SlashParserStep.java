package com.softlib.imatch.ticketprocessing;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import bsh.StringUtil;

import com.softlib.imatch.ITicket;
import com.softlib.imatch.MatcherException;
import com.softlib.imatch.RuntimeInfo;
import com.softlib.imatch.common.LogUtils;
import com.softlib.imatch.common.Pair;
import com.softlib.imatch.common.StringUtils;
import com.softlib.imatch.dictionary.ITechnicalTermsContainer;
import com.softlib.imatch.dictionary.SynonymsRelation;
import com.softlib.imatch.dictionary.TechnicalDictionaryKey;
import com.softlib.imatch.dictionary.TechnicalDictionaryTerm;
import com.softlib.imatch.dictionary.TechnicalTermSource;

public class SlashParserStep extends BaseTicketProcessStep {

	private static Logger log = Logger.getLogger(SlashParserStep.class);
	
	@Override
	public String getStepName() {
		return "SlashParser";
	}

	@Override
	public void run(String fieldName, ITicket ticket,
			ITechnicalTermsContainer termsContainer, StepContext context)
			throws MatcherException {
		if(fieldName.equals(UrlParserStep.URL_FIELD_NAME))
			return;
		String data = getData(fieldName, ticket, context);
		Map<String, Integer>slashPositions = runRegex(data);
		List<Pair<String, String>> stepData = new ArrayList<Pair<String, String>>();
		List<String> extractedTerms = new ArrayList<String>();
		for(String wordWithSlash : slashPositions.keySet()) {
			String[] words = wordWithSlash.split("/");
			if(words.length != 2 || StringUtils.isEmpty(words[0].trim()) || StringUtils.isEmpty(words[1].trim()))
				continue;
			stepData.add(new Pair<String, String>(words[0], words[1]));
			extractedTerms.add(words[0]);
		}
		termsContainer.startSession(fieldName,context.getTempTicketId(), getStepName());
		SynonymsRelation relation = new SynonymsRelation();
		for(Pair<String, String> slashPair : stepData) {
			TechnicalDictionaryTerm term1 = null;
			TechnicalDictionaryTerm term2 = null;
			if(RuntimeInfo.getCurrentInfo().isWebAppMode() || isEligable(slashPair.getLeft()))
				term1 = addTerm(slashPair.getLeft(), termsContainer);
			if(RuntimeInfo.getCurrentInfo().isWebAppMode() || isEligable(slashPair.getRight()))
				term2 =addTerm(slashPair.getRight(), termsContainer);
			if(term1 != null && term2 != null)
				relation.relate(term1, term2, "Slash rule");
		}
		termsContainer.endSession((float)1.0, null, true);
		LogUtils.debug(log, "Extract #%s# {%s} Terms:%s", ticket.getId(),fieldName,extractedTerms);
		//Remove slash from the clean data
		data = data.replace('/', ' ');
		context.setCleanText(data);
		context.setNlpData(data);
	}
	
	private Map<String, Integer> runRegex(String data) {
		MatchRegexOperation regexOp = new MatchRegexOperation("[\\w_\\-\\.]+?/[\\w_\\-\\.]+");
		Map<String, Integer> result = regexOp.runWithPositions(data);
		return result;
	}
}
