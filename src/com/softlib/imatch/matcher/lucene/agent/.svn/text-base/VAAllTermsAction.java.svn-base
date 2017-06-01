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
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.queryParser.QueryParser.Operator;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocsCollector;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.util.Version;

import com.softlib.imatch.MatcherException;
import com.softlib.imatch.common.LogUtils;
import com.softlib.imatch.common.configuration.IConfigurationObject;
import com.softlib.imatch.common.configuration.IConfigurationResourceLoader;
import com.softlib.imatch.dictionary.TechnicalDictionaryTerm;
import com.softlib.imatch.dictionary.TechnicalTermSource;
import com.softlib.imatch.matcher.DBTicketFieldsNames;
import com.softlib.imatch.matcher.ICandidatesSubprocessor;
import com.softlib.imatch.matcher.ITicketFieldsNames;
import com.softlib.imatch.matcher.lucene.LuceneQueryBuilder;
import com.softlib.imatch.matcher.lucene.customscore.IQueryTester;
import com.softlib.imatch.ticketprocessing.IProcessedTicket;
import com.softlib.imatch.ticketprocessing.ITicketMatchAction;
import com.softlib.imatch.ticketprocessing.ProcessedTicket;
import com.softlib.imatch.ticketprocessing.RematchScoreCalculator;
import com.softlib.imatch.ticketprocessing.IProcessedTicket.MatchMode;

class VAAllTermsAction extends VABaseAction implements ITicketMatchAction {
	
	protected Logger log = Logger.getLogger(VAAllTermsAction.class);
	//protected StandardAnalyzer analyzer = new StandardAnalyzer(Version.LUCENE_CURRENT, new HashSet<String>());
	
	
	public VAAllTermsAction(IConfigurationResourceLoader loader,Boolean orphansConcatMode,Boolean termsConcatMode,Boolean useiMatchScore) {
		super(loader,orphansConcatMode,termsConcatMode,useiMatchScore);
	}
	
	
	public Query buildQuery(IProcessedTicket processedTicket) {
		LogUtils.debug(log,"running buildQuery for %s",this.getClass().getSimpleName());
		LogUtils.debug(log, "OrphansQueryMode: %s TermsQueryMode: %s", orphansQueryConcatMode==Operator.AND,termsQueryConcatMode==Operator.AND);
		// do we process orphans
		if (orphansQueryConcatMode != Operator.AND)
		{
			// do we have orphans? if so, give up processing
			if (processedTicket.getOrphanWords() != null && processedTicket.getOrphanWords().size() != 0)
			{
				LogUtils.debug(log,"there are orphans and iteration requires none, so no query is built");
				return null;
			}
		}
		
		/*
		// check what kind of terms do we have, but only if no orphans
		if (processedTicket.getSortedTerms().size() < 3 && ((orphansQueryConcatMode == Operator.AND) || (orphansQueryConcatMode != Operator.AND && (processedTicket.getOrphanWords() == null || processedTicket.getOrphanWords().size() == 0))))
		{
			boolean onlyEnglishTerms = true;
			for (TechnicalDictionaryTerm term : processedTicket.getSortedTerms()) {
				if (term.getTermSource().getSourceId() != TechnicalTermSource.ENGLISH_WORDS_ID && term.getTermSource().getSourceId() != TechnicalTermSource.SOFTLIB_TERMS_ID)
				{
					onlyEnglishTerms = false;
					break;
				}
			}
			
			if (onlyEnglishTerms == true)
			{
				LogUtils.debug(log,"only English/non-important Terms. not building query for this query");
				return null;
			}
		}*/
		
		BooleanQuery rc = new BooleanQuery();
		/*MatchMode matchMode = MatchMode.match;
		
		List<TechnicalDictionaryTerm> termsList = processedTicket.getSortedTerms();
		
		ITicketFieldsNames fieldsNamesConfig = new DBTicketFieldsNames();
		Set<String> fieldsNames = fieldsNamesConfig.getAllFields(matchMode);

		BooleanQuery tempQuery = new BooleanQuery();
		for (String fieldName : fieldsNames) {
			BooleanQuery fieldQuery = buildFieldQuery(fieldName,termsList,matchMode);
			if(!fieldQuery.clauses().isEmpty())
				//rc.add(fieldQuery, Occur.SHOULD);//termsQueryConcatMode);	
				rc.add(fieldQuery, termsQueryConcatMode);
		}*/

		Query termsQurey = buildTermsQuery(processedTicket);
		if (termsQurey != null)
			rc.add(termsQurey, Occur.MUST);

		return rc;
	}

	public Boolean  useiMatchScorer()
	{
		return useiMatchScore;
	}	
};
