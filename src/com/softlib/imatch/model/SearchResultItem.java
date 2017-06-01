package com.softlib.imatch.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.softlib.imatch.ITicket;
import com.softlib.imatch.RuntimeInfo;
import com.softlib.imatch.common.HighlightText;
import com.softlib.imatch.common.LogUtils;
import com.softlib.imatch.common.SourceMngr;
import com.softlib.imatch.common.HighlightText.Type;
import com.softlib.imatch.common.configuration.IConfigurationObject;
import com.softlib.imatch.dictionary.TechnicalDictionaryKey;
import com.softlib.imatch.dictionary.TechnicalDictionaryTerm;
import com.softlib.imatch.enums.HighlightType;
import com.softlib.imatch.logging.Audit;
import com.softlib.imatch.matcher.DBTicketFieldsNames;
import com.softlib.imatch.matcher.ITicketFieldsNames;
import com.softlib.imatch.matcher.MatchCandidate;
import com.softlib.imatch.ticketprocessing.IProcessedTicket;
import com.softlib.imatch.ticketprocessing.ProcessedField;

public class SearchResultItem {
	private final static Logger log = Logger.getLogger(RuntimeInfo.class);

	private MatchCandidate matchCandidate;
	private String id;
	private String groupID;
	private String detailsURL;
	private String title;
	private String highlightedTitle;
	private String preview;
	private HashMap<String, String> fieldValues;
	
	private ArrayList<TechnicalDictionaryTerm> terms;
	private ArrayList<TechnicalDictionaryTerm> oneFreqTerms;
	
	private Map<TechnicalDictionaryKey, Float> boostFactors;
	private HighlightType highlightType;

	private boolean isHighlightZeroFreqTerms;
	
	public void setMatchCandidate(MatchCandidate matchCandidate) {
		this.matchCandidate = matchCandidate;
	}
	
	public MatchCandidate getMatchCandidate() {
		return matchCandidate;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}
	
	public void setGroupID(String groupID) {
		this.groupID = groupID;
	}
	
	public String getGroupID() {
		return groupID;
	}

	public void setDetailsURL(String detailsURL) {
		this.detailsURL = detailsURL;
	}

	public String getDetailsURL() {
		return detailsURL;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitle() {
		return title;
	}

	public void setHighlightedTitle(String highlightedTitle) {
		this.highlightedTitle = highlightedTitle;
	}

	public String getHighlightedTitle() {
		return highlightedTitle;
	}

	public void setPreview(String preview) {
		this.preview = preview;
	}

	public String getPreview() {
		return preview;
	}

	public HashMap<String, String> getFieldValues() {
		return fieldValues;
	}

	public void setFieldValues(HashMap<String, String> fieldValues) {
		this.fieldValues = fieldValues;
	}
	
	public ArrayList<TechnicalDictionaryTerm> getTerms() {
		ArrayList<TechnicalDictionaryTerm> rc = new ArrayList<TechnicalDictionaryTerm>(terms);
		rc.addAll(oneFreqTerms);
		return rc;
	}

	public void setTerms(ArrayList<TechnicalDictionaryTerm> terms) {
		this.terms = terms;
	}

	public void setBoostFactors(Map<TechnicalDictionaryKey, Float> boostFactors) {
		this.boostFactors = boostFactors;
	}
	
	public Map<TechnicalDictionaryKey, Float> getBoostFactors() {
		return boostFactors;
	}
	
	public SearchResultItem(MatchCandidate matchCandidate, Map<String, String> columns, 
							IConfigurationObject config, HighlightType highlightType) {
		
		this.matchCandidate = matchCandidate;
		this.highlightType = highlightType;
		
		fieldValues = new HashMap<String, String>();
		detailsURL = replacePlaceHolders((String)config.getProperty(matchCandidate.getOriginObjectId(), "detailsURL"), "(?<=\\{).*?(?=})", matchCandidate.getCandidateData());
		Boolean tmpHighlightZeroFreqTerms = (Boolean)config.getProperty(matchCandidate.getOriginObjectId(), "highlightZeroTerms");
		if(tmpHighlightZeroFreqTerms == null)
			isHighlightZeroFreqTerms = true;
		else
			isHighlightZeroFreqTerms = tmpHighlightZeroFreqTerms;
		groupID = matchCandidate.getOriginObjectId();
		
		if (!highlightType.equals(HighlightType.MATCH)) {
			boostFactors = matchCandidate.getProcessedTicket().getBoostFactors();
		}
		
		ITicket candidateData = matchCandidate.getCandidateData();
		id = candidateData.getId();
		
		if (candidateData!=null && matchCandidate.isDataExist()) {
			// At first do highlight only on origin ticket all other tickets will have highlight on demand
			this.preview = buildPreview(this.highlightType.equals(HighlightType.EXTRACTED));
		}
		else
			LogUtils.error(log, "Could not get ticket body for preview");
		
		
		List<TechnicalDictionaryTerm> highlightTerms = 
			getHighlightTerms(matchCandidate.getProcessedTicket().getTitleTerms());
		
		title = candidateData.getTitle();
		HighlightText highlightText = new HighlightText(title);
		highlightText.highlight(highlightTerms, Type.Active);
		highlightedTitle = highlightText.getHighlightText();
		
		for (Map.Entry<String, String> column: columns.entrySet()) {
			try {
				String key = column.getKey();
				Object fieldVal = candidateData.getField(key);
				if (fieldVal != null)
					this.fieldValues.put(key, fieldVal.toString());
			} catch (Exception e) {
				LogUtils.error(log, "Error when setting field values - %s", e.getMessage());
			}	
		}
	}
	
	private String fieldText(Object field,boolean highlight) {
		String rc = field == null ? "" : field.toString();
		rc = rc.replaceAll("<.*?>", "");
		if (highlight && !this.highlightType.equals(HighlightType.MATCH))
			rc = rc.replaceAll("\\n", "<br/>");
		return rc;
	}
	
	private List<TechnicalDictionaryTerm> getHighlightTerms(List<TechnicalDictionaryTerm> candidateTerms) {
		List<TechnicalDictionaryTerm> rc = new ArrayList<TechnicalDictionaryTerm>();
		for (TechnicalDictionaryTerm term : candidateTerms) {
			if (terms.contains(term))
				rc.add(term);
			else {
				for (TechnicalDictionaryTerm relation : term.getRelations()) {
					if (terms.contains(relation)) {
						rc.add(relation);
						if (!rc.contains(term))
							rc.add(term);
					}
				}
			}
		}
		return rc;
	}
	
	public List<TechnicalDictionaryTerm> getUserDefineTerms(List<TechnicalDictionaryTerm> terms) {
		List<TechnicalDictionaryTerm> rc = new ArrayList<TechnicalDictionaryTerm>();
		for (TechnicalDictionaryTerm term : terms ) {
			if (term.getTermSource().getsourceName().equals(SourceMngr.UserDefinedLocal))
				rc.add(term);
		}
		return rc;
	}
	
	public String buildPreview(boolean highlight) { 
		String rc = "";
		
		IProcessedTicket processedTicket  = matchCandidate.getProcessedTicket();
		IProcessedTicket sourceTicket = matchCandidate.getSourceProcessedTicket();
		
		terms = new ArrayList<TechnicalDictionaryTerm>(sourceTicket.getSortedTerms());
		oneFreqTerms = new ArrayList<TechnicalDictionaryTerm>(sourceTicket.getOneFreqTerms());
		
		ITicketFieldsNames fieldsNamesConfig = new DBTicketFieldsNames();
		fieldsNamesConfig.setObjectId(processedTicket.getOriginObjectId());
		Set<String> bodyFieldsNames = fieldsNamesConfig.getBodyFields(sourceTicket.getMatchMode());

		List<String> emptyTitles = new ArrayList<String>();
		
		for (String fieldName : bodyFieldsNames) {
			Object field = matchCandidate.getCandidateData().getField(fieldName);
			String fieldText = fieldText(field,highlight);
			String fieldTitle = "<div class=\"previewTitle\">" + fieldName + "</div>";
			if (fieldText.isEmpty()) {
				emptyTitles.add(fieldTitle);
				continue;
			}
			fieldText = prepareTextForHTML(fieldText);
			
			if (highlight) {
				ProcessedField processedField = processedTicket.getField(fieldName);
				if (processedField==null)
					rc += fieldTitle + fieldText;
				else {
					List<TechnicalDictionaryTerm> highlightTerms = 
						getHighlightTerms(processedField.getTerms());
					highlightTerms.addAll(getUserDefineTerms(terms));
					
					HighlightText highlightText = new HighlightText(fieldText);
					highlightText.highlight(highlightTerms,Type.Active);
					if (processedTicket.isSourceTicket() && isHighlightZeroFreqTerms) {
		 				highlightText.highlight(processedField.getOneFreqTerms(),Type.One);
		 				highlightText.highlight(processedField.getZeroFreqTerms(),Type.Zero);
					}
					String highlightFieldText = highlightText.getHighlightText();
					rc += fieldTitle + highlightFieldText;
					
				}
			}
			else
				rc += fieldTitle + fieldText;
			
			rc += "<br/>";
		}

//		for (String emptyTitle : emptyTitles)
//			rc += emptyTitle;
		
		this.preview = rc;
		return rc;
	}
	
	public String prepareTextForHTML(String body) {
		try {
			Pattern hasHTML = Pattern.compile("<.*?>");
			Pattern hasNewLines = Pattern.compile("\n");
			Matcher hasHtmlMatcher = hasHTML.matcher(body);
			Matcher hasNewLinesMatcher = hasNewLines.matcher(body);
			
			if (hasHtmlMatcher.find(0) && hasNewLinesMatcher.find(0))
			{
				body = body.replaceAll("\n","");
				body = body.replaceAll("<br/?>|<p>","\n");
				body = body.replaceAll("</p>","");
			}
			else if (hasHtmlMatcher.find(0))
			{
				body = body.replaceAll("<br/?>|<p>","\n");
				body = body.replaceAll("</p>","");
			}
			
			body = body.replaceAll("<.*?>", "");
			
			//if (highlight && !this.highlightType.equals(HighlightType.MATCH))
			body = body.replaceAll("\\n", "<br/>");
		} catch (Exception e) {
			Audit.error(String.format("Error - prepareTextForHTML - %s", e.getMessage()), this.getClass());
		}
		
		return body;
	}

	public static String replacePlaceHolders(String str, String regex, ITicket ticket) {
		String newStr = str;

		try {
			//TODO prepare regex pattern only once
			Pattern regexPattern = Pattern.compile(regex);
			Matcher matcher = regexPattern.matcher(str);

			while (matcher.find()) {
				try {
					String matchGroup = matcher.group();
					Object field = ticket.getField(matchGroup); 
					
					if (field!=null) {
						String fieldStr = Matcher.quoteReplacement(field.toString());
						newStr = newStr.replaceAll("\\{"+matchGroup+"\\}", fieldStr);
					}
				} 
				catch (UnsupportedOperationException e) {
					LogUtils.error(log, e.getMessage());
				}
			}
		} catch (Exception e) {
			LogUtils.error(log, "Error when replacing place holders in the url: %s\n error msg: %s", str, e.getMessage());
		}
		
		return newStr;
	}
	
};
