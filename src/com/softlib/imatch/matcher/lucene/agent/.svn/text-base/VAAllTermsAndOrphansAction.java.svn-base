package com.softlib.imatch.matcher.lucene.agent;

import java.util.HashMap;
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
//import org.apache.taglibs.standard.lang.jstl.OrOperator;

import com.softlib.imatch.MatcherException;
import com.softlib.imatch.common.LogUtils;
import com.softlib.imatch.common.configuration.IConfigurationObject;
import com.softlib.imatch.common.configuration.IConfigurationResourceLoader;
import com.softlib.imatch.dictionary.TechnicalDictionaryTerm;
import com.softlib.imatch.matcher.DBTicketFieldsNames;
import com.softlib.imatch.matcher.ICandidatesSubprocessor;
import com.softlib.imatch.matcher.ITicketFieldsNames;
import com.softlib.imatch.matcher.lucene.LuceneIndexer;
import com.softlib.imatch.matcher.lucene.LuceneQueryBuilder;
import com.softlib.imatch.matcher.lucene.customscore.IQueryTester;
import com.softlib.imatch.ticketprocessing.IProcessedTicket;
import com.softlib.imatch.ticketprocessing.ITicketMatchAction;
import com.softlib.imatch.ticketprocessing.ProcessedTicket;
import com.softlib.imatch.ticketprocessing.RematchScoreCalculator;
import com.softlib.imatch.ticketprocessing.IProcessedTicket.MatchMode;

class VAAllTermsAndOrphansAction extends VABaseAction implements ITicketMatchAction {
	
	protected Logger log = Logger.getLogger(VAAllTermsAndOrphansAction.class);
//	protected StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_CURRENT, new HashSet<String>());
	
	private Map<String,QueryParser> parserByFieldName = 
			new HashMap<String,QueryParser>();

	
	public VAAllTermsAndOrphansAction(IConfigurationResourceLoader loader,Boolean orphansConcatMode,Boolean termsConcatMode,Boolean useiMatchScore) {
		super(loader,orphansConcatMode,termsConcatMode,useiMatchScore);
	}
	
	
	public Query buildQuery(IProcessedTicket processedTicket) {
		
		LogUtils.debug(log,"running buildQuery for %s",this.getClass().getSimpleName());
		LogUtils.debug(log, "OrphansQueryMode: %s TermsQueryMode: %s", orphansQueryConcatMode==Operator.AND,termsQueryConcatMode==Operator.AND);
		BooleanQuery rc = new BooleanQuery();
		
		/*MatchMode matchMode = MatchMode.match;
		
		List<TechnicalDictionaryTerm> termsList = processedTicket.getSortedTerms();
		
		ITicketFieldsNames fieldsNamesConfig = new DBTicketFieldsNames();
		Set<String> fieldsNames = fieldsNamesConfig.getAllFields(matchMode);

		BooleanQuery tempQuery = new BooleanQuery();
		for (String fieldName : fieldsNames) {
			BooleanQuery fieldQuery = buildFieldQuery(fieldName,termsList,matchMode);
			if(!fieldQuery.clauses().isEmpty())
				//tempQuery.add(fieldQuery, Occur.SHOULD);	
				tempQuery.add(fieldQuery, termsQueryConcatMode);
		}

		if (tempQuery != null)
			rc.add(tempQuery, Occur.MUST);*/
		
		Query termsQurey = buildTermsQuery(processedTicket);
		if (termsQurey != null)
			rc.add(termsQurey, Occur.MUST);
		
		// now build orphans query
		Query orphansQuery = buildOrphansQuery(processedTicket.getOrphanWords());
		if (orphansQuery != null)
			rc.add(orphansQuery, Occur.MUST);
		
		return rc;
	}
	
	public Boolean  useiMatchScorer()
	{
		return useiMatchScore;
	}
};
