package com.softlib.imatch.matcher.lucene.agent;

import java.util.HashMap;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.queryParser.QueryParser.Operator;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocsCollector;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.util.Version;
import org.apache.lucene.analysis.Analyzer;

import com.softlib.imatch.common.LogUtils;
import com.softlib.imatch.common.configuration.IConfigurationObject;
import com.softlib.imatch.common.configuration.IConfigurationResourceLoader;
import com.softlib.imatch.dictionary.TechnicalDictionaryTerm;
import com.softlib.imatch.matcher.DBTicketFieldsNames;
import com.softlib.imatch.matcher.ITicketFieldsNames;
import com.softlib.imatch.matcher.lucene.LuceneIndexer;
import com.softlib.imatch.ticketprocessing.IProcessedTicket;
import com.softlib.imatch.ticketprocessing.ProcessedTicket;
import com.softlib.imatch.ticketprocessing.IProcessedTicket.MatchMode;

class VABaseAction {
	
	protected Operator	termsQueryConcatMode;
	protected Operator	orphansQueryConcatMode;
	protected Boolean   useiMatchScore;
	protected IConfigurationResourceLoader loader;
	
	protected Logger log = null;//Logger.getLogger(LuceneQueryBuilder.class);
	protected Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_CURRENT, new HashSet<String>());
	
	private Map<String,QueryParser> parserByFieldName = 
			new HashMap<String,QueryParser>();

	
	public VABaseAction(IConfigurationResourceLoader loader,Boolean orphansConcatMode,Boolean termsConcatMode,Boolean useiMatchScore) {
		this.loader = loader;
		if (orphansConcatMode == true)
			orphansQueryConcatMode = Operator.AND;
		else
			orphansQueryConcatMode = Operator.OR;

		if (termsConcatMode == true)
			termsQueryConcatMode = Operator.AND;
		else
			termsQueryConcatMode = Operator.OR;
		
		this.useiMatchScore = useiMatchScore;

	}
	
	public Boolean continueWithProcess(Set<ProcessedTicket> candidates)
	{
		if (candidates.size() > 0)// && candidates.size() <= 3)
			return false;
		return true;
	}
	
	
	Query buildOrphansQuery(List<String> orphanWords)
	{
		if (orphanWords.size() == 0)
			return new BooleanQuery();
		
			// all words together in multi-field search
		String queryWords = "";
		
		for (String word : orphanWords) {
			queryWords = queryWords + " " + word;
		}
		
		String[] fields = {LuceneIndexer.TICKET_TITLE_FIELD,LuceneIndexer.TICKET_CONTENT_FIELD, LuceneIndexer.TICKET_URL_FIELD, LuceneIndexer.TICKET_LINKS_FIELD};
		Map<String,Float> boosts = new HashMap<String,Float>();
		boosts.put(LuceneIndexer.TICKET_URL_FIELD, 2.0f);
		boosts.put(LuceneIndexer.TICKET_LINKS_FIELD, 2.0f);
		boosts.put(LuceneIndexer.TICKET_TITLE_FIELD, 2.0f);
		
		MultiFieldQueryParser mfq = new MultiFieldQueryParser(Version.LUCENE_CURRENT,fields,analyzer,boosts);
		mfq.setDefaultOperator(orphansQueryConcatMode);
		mfq.setLowercaseExpandedTerms(false);
		
		Query q = null;
		try
		{
			q = mfq.parse(queryWords);
		}
		catch(ParseException e)
		{
			LogUtils.error(log,"Error parsing query string: %s",queryWords);
			// return empty query
			return new BooleanQuery();
		}
		return q;

	}
	
	public Query buildTermsQuery(IProcessedTicket processedTicket) {
		
		if (processedTicket.getSortedTerms().size() == 0)
			return new BooleanQuery();
		
		// all words together in multi-field search
		String queryWords = "";
		
		for (TechnicalDictionaryTerm term : processedTicket.getSortedTerms()) {
			String word = term.getTermStemmedText();
			if (word.contains(" "))
				queryWords = queryWords + " \"" + word + "\"";
			else
				queryWords = queryWords + " " + word;
			
			queryWords = queryWords + "^" + String.valueOf(term.getTermSource().getSourceBoost()); 
		}
		
		ITicketFieldsNames fieldsNamesConfig = new DBTicketFieldsNames();
		Set<String> fieldsNames = fieldsNamesConfig.getAllFields(MatchMode.all);
		String[] fields = new String[fieldsNames.size()];// = new ArrayList<String>();//{LuceneIndexer.TICKET_TITLE_FIELD,LuceneIndexer.TICKET_CONTENT_FIELD};
		int i = 0;
		for (String fieldName : fieldsNames) {
			fields[i++] = fieldName;
		}
		Map<String,Float> boosts = new HashMap<String,Float>();
		
		boosts.put("Url", fieldsNamesConfig.getFieldBoost("Url"));
		boosts.put("Title", fieldsNamesConfig.getFieldBoost("Title"));
		boosts.put("Links", fieldsNamesConfig.getFieldBoost("Links"));
		MultiFieldQueryParser mfq = new MultiFieldQueryParser(Version.LUCENE_CURRENT,fields,analyzer,boosts);
		mfq.setDefaultOperator(termsQueryConcatMode);
		mfq.setLowercaseExpandedTerms(false);
		
		Query q = null;
		try
		{
			q = mfq.parse(queryWords);
		}
		catch(ParseException e)
		{
			LogUtils.error(log,"Error parsing query string: %s",queryWords);
			return null;
		}
		return q;		
		
		/*
		BooleanQuery rc = new BooleanQuery();
		
		MatchMode matchMode = MatchMode.match;
		
		List<TechnicalDictionaryTerm> termsList = processedTicket.getSortedTerms();
		int numTerms = 1;
		
		//int minNumCandidates = (Integer)loader.getProperty(processedTicket.getOriginObjectId(), "minimumNumCandidates");
			

		ITicketFieldsNames fieldsNamesConfig = new DBTicketFieldsNames();
		Set<String> fieldsNames = fieldsNamesConfig.getAllFields(matchMode);

		BooleanQuery tempQuery = new BooleanQuery();
		for (String fieldName : fieldsNames) {
			BooleanQuery fieldQuery = buildFieldQuery(fieldName,termsList,matchMode);
			if(!fieldQuery.clauses().isEmpty())
				tempQuery.add(fieldQuery, termsQueryConcatMode);	
		}

		if (tempQuery != null)
			rc.add(tempQuery, Occur.MUST);
		
		return rc;*/
	}
	
	protected BooleanQuery buildFieldQuery(String fieldName,
										List<TechnicalDictionaryTerm> subTerms,
										MatchMode mode
										) {
		BooleanQuery rc = new BooleanQuery();
		for(TechnicalDictionaryTerm term : subTerms) {
			Query termQuery = buildTermQuery(term.getTermStemmedText(),fieldName);
			if(termQuery == null)
				continue;
			if(term != null) {
				for(TechnicalDictionaryTerm termSynonym : term.getRelations()) {
					Query termSynonymQuery = buildTermQuery(termSynonym.getTermStemmedText(),fieldName);
					if(termSynonymQuery != null)
						rc.add(termSynonymQuery, Occur.SHOULD);						
				}
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

	
};
