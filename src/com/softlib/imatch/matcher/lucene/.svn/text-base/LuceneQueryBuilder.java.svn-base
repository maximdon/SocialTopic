package com.softlib.imatch.matcher.lucene;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.util.Version;

import com.softlib.imatch.ITicket;
import com.softlib.imatch.InMemoryTicket;
import com.softlib.imatch.common.LogUtils;
import com.softlib.imatch.common.configuration.IConfigurationObject;
import com.softlib.imatch.dictionary.TechnicalDictionaryTerm;
import com.softlib.imatch.ticketprocessing.IProcessedTicket;
import com.softlib.imatch.ticketprocessing.IScoreCalculator;
import com.softlib.imatch.ticketprocessing.IProcessedTicket.MatchMode;

/**
 * This class is responsible for building Lucene query based on ticket information
 * @author Maxim Donde
 *
 */
public abstract class LuceneQueryBuilder {
	protected IConfigurationObject config;
	protected Logger log = Logger.getLogger(LuceneQueryBuilder.class);
	protected StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_CURRENT, new HashSet<String>());
	private QueryParser parser = new QueryParser(Version.LUCENE_CURRENT, "", analyzer);

	public LuceneQueryBuilder(IConfigurationObject config) {
		this.config = config;
	}

	public Query buildQuery(IProcessedTicket ticket) {
		BooleanQuery luceneQuery = new BooleanQuery();
		BooleanQuery filterQuery = addFilterFields(ticket);
		BooleanQuery fieldQuery = new BooleanQuery();
		
		IScoreCalculator scoreCalculator = ticket.getScoreCalculator();
		scoreCalculator.sortTerms(ticket);
		fieldQuery.add(buildQuery(ticket, filterQuery), Occur.MUST);
				
		luceneQuery.add(filterQuery, Occur.MUST);
		luceneQuery.add(fieldQuery, Occur.MUST);
		LogUtils.debug(log, "For ticket %s the Lucene query to run is %s", ticket, luceneQuery);
		return luceneQuery;
	}

	//Adds filter fields to the query, these fields must appear in every Lucene search result
	@SuppressWarnings("unchecked")
	protected BooleanQuery addFilterFields(IProcessedTicket ticket) {
		BooleanQuery filterQuery = new BooleanQuery();
		if(ticket.getId() != null && !ticket.getId().equals(InMemoryTicket.DEFAULT_ID)) {
			//Search for existing ticket, we need to exclude this ticket from the results.
			//Note, if id == null, this ticket is not saved yet so nothing to exclude
			Query idQuery;
			try {
				synchronized (parser) {
					idQuery = parser.parse(String.format("%s:%s AND %s:%s", LuceneIndexer.TICKET_ID_FIELD, ticket.getId(), LuceneIndexer.TICKET_OBJECT_ID_FIELD, ticket.getOriginObjectId()));					
				}
			} catch (ParseException e) {
				throw new RuntimeException("Unexpected error occured: " + e.getMessage());
			}
			filterQuery.add(idQuery, Occur.MUST_NOT);
		}
		//Patch, Lucene doesn't support query like -ticket_id:4. 
		//For this reason we add synthetic field which has value 0 for all documents and doesn't change the score
		filterQuery.add(new TermQuery(new Term(LuceneIndexer.DUPLICATE_TICKET_ID_FIELD, "0")), Occur.MUST);

		List<String> filterFields = (List<String>) config.getProperty(ticket.getOriginObjectId(), "filterFields");
		for(String filterFieldName : filterFields) {
			ITicket originalTicket = ticket.getOriginalTicket();
			Object filterFieldValue;
			if(originalTicket == null || (filterFieldValue = originalTicket.getField(filterFieldName)) == null)
				continue;
			//filterQuery.add(new TermQuery(new Term(filterFieldName, filterFieldValue.toString())), Occur.MUST);
			Query filterFieldQuery = null;
			try {
				filterFieldQuery = parser.parse(String.format("%s:\"%s\"", filterFieldName, filterFieldValue.toString()));
			}
			catch(Exception e) {
				continue;
			}
			filterQuery.add(filterFieldQuery, Occur.MUST);
		}
		return filterQuery;
	}
	
	protected abstract Query buildQuery(IProcessedTicket processedTicket, BooleanQuery filterQuery);

	protected abstract BooleanQuery buildFieldQuery(String fieldName,
												 List<TechnicalDictionaryTerm> subTerms,
												 Set<TechnicalDictionaryTerm> mustTerms,
												 MatchMode mode,
												 int maximumFrequencyForInitialFilter);

	public abstract Query buildTermQuery(String termText,String fieldName);

};
