package com.softlib.imatch.matcher.lucene.customscore;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocsCollector;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.util.Version;

import com.softlib.imatch.MatcherException;
import com.softlib.imatch.common.LogUtils;
import com.softlib.imatch.common.configuration.IConfigurationObject;
import com.softlib.imatch.dictionary.TechnicalDictionaryTerm;
import com.softlib.imatch.matcher.DBTicketFieldsNames;
import com.softlib.imatch.matcher.ITicketFieldsNames;
import com.softlib.imatch.matcher.lucene.LuceneQueryBuilder;
import com.softlib.imatch.ticketprocessing.IProcessedTicket;
import com.softlib.imatch.ticketprocessing.RematchScoreCalculator;
import com.softlib.imatch.ticketprocessing.IProcessedTicket.MatchMode;

public class CustomScoreQueryBuilder extends LuceneQueryBuilder {
	
	private IQueryTester tester;
	
	private Map<String,QueryParser> parserByFieldName = 
		new HashMap<String,QueryParser>();

	public CustomScoreQueryBuilder(IConfigurationObject config, IQueryTester tester) {
		super(config);
		this.tester = tester;
	}
	
	protected Query buildQuery(IProcessedTicket processedTicket, BooleanQuery filterQuery) {
		BooleanQuery rc = new BooleanQuery();
		
		MatchMode matchMode = MatchMode.match;
		if (processedTicket.getScoreCalculator() instanceof RematchScoreCalculator)
			matchMode = MatchMode.rematch;
		
		List<TechnicalDictionaryTerm> termsList = processedTicket.getSortedTerms();
		Set<TechnicalDictionaryTerm> mustTerms = new HashSet<TechnicalDictionaryTerm>();
		int numTerms = 1;
		
		if (matchMode == MatchMode.rematch) {
			ListIterator<TechnicalDictionaryTerm> iter = termsList.listIterator();
			while(iter.hasNext()) {
				TechnicalDictionaryTerm currTerm = iter.next();
				if (processedTicket.isMustTerm(currTerm))
					mustTerms.add(currTerm);
				if(processedTicket.getItemBoost(currTerm) == 0)
					iter.remove();
			}
			numTerms = termsList.size();
		}
		//Note, minNumCandidates also serves as maximumFrequencyForInitialFilter 
		//(i.e. maximum frequency for term to be eligible for initial filtering)
		int minNumCandidates = (Integer)config.getProperty(processedTicket.getOriginObjectId(), "minimumNumCandidates");
		while (numTerms <= termsList.size()) {			
			List<TechnicalDictionaryTerm> subTerms = termsList.subList(0, numTerms);
			
			ITicketFieldsNames fieldsNamesConfig = new DBTicketFieldsNames();
			Set<String> fieldsNames = fieldsNamesConfig.getAllFields(matchMode);

			BooleanQuery tempQuery = new BooleanQuery();
			for (String fieldName : fieldsNames) {
				BooleanQuery fieldQuery = buildFieldQuery(fieldName,subTerms,mustTerms, matchMode, minNumCandidates);
				if(!fieldQuery.clauses().isEmpty())
					tempQuery.add(fieldQuery, Occur.SHOULD);	
			}
			int totalHits = 0;
			if(!tempQuery.clauses().isEmpty()) {
				BooleanQuery testQuery = new BooleanQuery();
				if(filterQuery != null)
					testQuery.add(filterQuery, Occur.MUST);
				testQuery.add(tempQuery, Occur.MUST);
				totalHits = testQueryTotalHits(testQuery);
			}
			if (totalHits < 0)
				return null;

			if(totalHits > minNumCandidates || 
			   numTerms >= termsList.size() || 
			   processedTicket.getItemBoost(termsList.get(numTerms))==0) { 
				if(tempQuery.clauses().isEmpty()) {
					//We went through all terms and the query remains empty, general filter based on all terms
					for (String fieldName : fieldsNames) {
						BooleanQuery fieldQuery = buildFieldQuery(fieldName,subTerms,mustTerms, MatchMode.rematch, minNumCandidates);
						tempQuery.add(fieldQuery, Occur.SHOULD);	
					}
				}
				rc = tempQuery;
				break;
			}
			
			numTerms++;
		}
		
		if(filterQuery == null) { //TODO check again, filterQuery should not be null
			filterQuery = addFilterFields(processedTicket);
			BooleanQuery resultQuery = new BooleanQuery();
			resultQuery.add(filterQuery, Occur.MUST);
			resultQuery.add(rc, Occur.MUST);
			return resultQuery;
		}
		else
			return rc;
	}
	
	protected BooleanQuery buildFieldQuery(String fieldName,
										List<TechnicalDictionaryTerm> subTerms,
										Set<TechnicalDictionaryTerm> mustTerms,
										MatchMode mode,
										int maximumFrequencyForInitialFilter) {
		BooleanQuery rc = new BooleanQuery();
		for(TechnicalDictionaryTerm term : subTerms) {
			Query termQuery = buildTermQuery(term.getTermStemmedText(),fieldName);
			if(termQuery == null)
				continue;
			if(term != null && (mode == MatchMode.rematch || term.getFrequency() < maximumFrequencyForInitialFilter)) {
				for(TechnicalDictionaryTerm termSynonym : term.getRelations()) {
					Query termSynonymQuery = buildTermQuery(termSynonym.getTermStemmedText(),fieldName);
					if(termSynonymQuery != null)
						rc.add(termSynonymQuery, Occur.SHOULD);						
				}
				if (mustTerms.contains(term))
					rc.add(termQuery, Occur.MUST);				
				else
					rc.add(termQuery, Occur.SHOULD);		
			}				
		}
		return rc;
	}
	
	public Query buildTermQuery(String termText,String fieldName) {
		
		QueryParser queryParser = parserByFieldName.get(fieldName);
		if (queryParser==null) {
			queryParser = new QueryParser(Version.LUCENE_CURRENT,fieldName,analyzer);
			parserByFieldName.put(fieldName, queryParser);
		}
		if(termText.contains(" "))
			termText = "\"" + termText + "\"";
		Query termQuery = null;
		try {			
			termQuery = queryParser.parse(termText);
		} catch (ParseException e) {
			LogUtils.error(log, "Unable to parse term %s", termText);
		}
		return termQuery;
	}

	private int testQueryTotalHits(BooleanQuery testQuery) {
		TopDocsCollector collector;
		try {
			collector = tester.test(testQuery);
		} catch (MatcherException e) {
			LogUtils.error(log, "Unable to test query %s", testQuery);
			return -1;
		}
		return collector.getTotalHits();
	}
	
	
	
};
