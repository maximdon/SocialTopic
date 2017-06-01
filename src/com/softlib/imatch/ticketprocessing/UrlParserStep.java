package com.softlib.imatch.ticketprocessing;

import java.net.MalformedURLException;
import java.net.URL;
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
import com.softlib.imatch.common.StringUtils;
import com.softlib.imatch.dictionary.ITechnicalTermsContainer;
import com.softlib.imatch.dictionary.SynonymsRelation;
import com.softlib.imatch.dictionary.TechnicalDictionaryKey;
import com.softlib.imatch.dictionary.TechnicalDictionaryTerm;
import com.softlib.imatch.dictionary.TechnicalTermSource;

public class UrlParserStep extends BaseTicketProcessStep {

	private static Logger log = Logger.getLogger(UrlParserStep.class);
	static final String URL_FIELD_NAME = "Url"; 
	public static final String URL_SPECIAL_CHARACTERS_REGEX = "[-_+]";
	@Override
	public String getStepName() {
		return "UrlTokens";
	}

	@Override
	public void run(String fieldName, ITicket ticket,
			ITechnicalTermsContainer container, StepContext context)
			throws MatcherException {
		if(!fieldName.equals(URL_FIELD_NAME))
			//This step works for URL field only
			return;
		String data = getData(fieldName, ticket, context);
		try {
			java.net.URL url = new URL(data);
			String path = url.getPath();
			path = path.replaceAll("%20"," ");
			MatchRegexOperation op = new MatchRegexOperation("\\b([A-Z][A-Za-z0-9\\-_\\.]*[a-z]+)([A-Z]+[A-Za-z0-9\\-_\\.]*)\\b");
			List<String> urlTokens = new ArrayList<String>();
			String[] pathParts = path.split("/");
			for(String pathPart : pathParts) {
				if(StringUtils.isEmpty(pathPart))
					continue;
				if(pathPart.contains("."))
					pathPart = pathPart.substring(0, pathPart.indexOf('.'));
				pathPart = pathPart.replaceAll(URL_SPECIAL_CHARACTERS_REGEX, " ");
				List<String> partMatches = op.runWithSubGroups(pathPart);
				if(partMatches != null && partMatches.size() > 0) {
					partMatches.remove(0);
					urlTokens.add(StringUtils.join(partMatches, " "));
				}
				else
					urlTokens.add(pathPart);			
			}
			container.startSession(fieldName,context.getTempTicketId(), getStepName());
			for(String termStr : urlTokens) {
				if(RuntimeInfo.getCurrentInfo().isWebAppMode() || isEligable(termStr))
					addTerm(termStr, container);
			}
			container.endSession((float)1.0, null, true);
		} catch (MalformedURLException e) {
			//Invalid URL, skip
			return;
		}
	}
}
