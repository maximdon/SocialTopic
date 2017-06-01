package com.softlib.imatch.ticketprocessing;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections.MultiHashMap;
import org.apache.commons.collections.MultiMap;
import org.apache.commons.collections.map.MultiValueMap;
import org.apache.log4j.Logger;
import org.hibernate.Session;

import com.softlib.imatch.ITicket;
import com.softlib.imatch.MatcherException;
import com.softlib.imatch.RuntimeInfo;
import com.softlib.imatch.common.HibernateUtils;
import com.softlib.imatch.common.LogUtils;
import com.softlib.imatch.common.SessionMode;
import com.softlib.imatch.common.StringUtils;
import com.softlib.imatch.dictionary.ITechnicalTermsContainer;
import com.softlib.imatch.dictionary.SynonymsRelation;
import com.softlib.imatch.dictionary.TechnicalDictionaryKey;
import com.softlib.imatch.dictionary.TechnicalDictionaryTerm;
import com.softlib.imatch.dictionary.TechnicalTermSource;

public class CommonMisspellingsStep extends BaseTicketProcessStep 
{
	private MultiMap misspellings = new MultiValueMap();
	private MultiMap misspellingsRevert = new MultiValueMap();
	
	private Pattern appostropheePattern = Pattern.compile("\\b(\\w+)(')(s)\\b");
	private static Logger log = Logger.getLogger(CommonMisspellingsStep.class);
	
	public CommonMisspellingsStep()
	{
		loadMisspellings();
	}
	
	@Override
	public String getStepName() {		
		return "Common Misspellings";
	}

	@Override
	public void run(String fieldName, ITicket ticket,
			ITechnicalTermsContainer termsContainer, StepContext context)
			throws MatcherException {
		String cleanText = getData(fieldName,ticket,context);
		if(StringUtils.isEmpty(cleanText))
			return;
		String[] words = cleanText.split(" ");
		termsContainer.startSession(fieldName, ticket.getId(), getStepName());
		SynonymsRelation relation = new SynonymsRelation();
		for(String word : words) {
			List<String> misspelledWords = checkMisspellingWord(word);
			if(misspelledWords.size() == 0)
				misspelledWords = checkAppostrophee(word);
			for(String misspelledWord : misspelledWords) {
				TechnicalDictionaryTerm term1 = termsContainer.addTerm(new TechnicalDictionaryKey(word));
				TechnicalDictionaryTerm term2 = termsContainer.addTerm(new TechnicalDictionaryKey(misspelledWord));
				if(term1 != null && term2 != null)
					relation.relate(term1, term2, "Misspelling");
			}
		}
		termsContainer.endSession(0, null, false);
	}
	
	private void loadMisspellings()
	{
		Session session = RuntimeInfo.getCurrentInfo().getHibernate().acquireSession(SessionMode.READONLY);
		Connection connection = session.connection();
		try {
			Statement stmt = connection.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT Word, Misspelling FROM COMMON_MISSPELLINGS");
			while(rs.next()) {				
				String key = rs.getString(1);
				String value = rs.getString(2);
				misspellings.put(key, value);
				misspellingsRevert.put(value, key);
			}
			LogUtils.info(log, "%d common misspellings found", misspellings.size());
		} catch (SQLException e) {
			LogUtils.error(log, "Unable to load common misspellings, reason %s", e.getMessage());
		}		
		finally {
			RuntimeInfo.getCurrentInfo().getHibernate().releaseSession(session);
		}
	}
	
	@SuppressWarnings("unchecked")
	private List<String> checkMisspellingWord(String word)
	{
		Object tmp = null;
		List<String> result = new ArrayList<String>();
		if(misspellings.containsKey(word)) {
			tmp = misspellings.get(word);			
		}
		if(misspellingsRevert.containsKey(word)) {
			tmp = misspellingsRevert.get(word);
		}
		if(tmp instanceof String) {
			result.add((String)tmp);
		}
		else if(tmp instanceof List) {
			result.addAll((List)tmp);
		}
		return result;
	}
	
	private List<String> checkAppostrophee(String word)
	{
		Matcher m = appostropheePattern.matcher(word);
		List<String>result = new ArrayList<String>();
		String wordWithoutAppostrophee = m.replaceFirst("$1$3");
		if(!word.equals(wordWithoutAppostrophee))
			result.add(wordWithoutAppostrophee);
		String wordWithoutAppostrophee2 = m.replaceFirst("$1");
		if(!word.equals(wordWithoutAppostrophee2))
			result.add(wordWithoutAppostrophee2);
		return result;
	}
}
