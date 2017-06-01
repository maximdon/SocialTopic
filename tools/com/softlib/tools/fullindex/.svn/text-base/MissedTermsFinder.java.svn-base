package com.softlib.tools.fullindex;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocsCollector;
import org.apache.lucene.search.BooleanClause.Occur;

import com.softlib.imatch.ConsoleAppRuntimeInfo;
import com.softlib.imatch.MatcherException;
import com.softlib.imatch.RuntimeInfo;
import com.softlib.imatch.dictionary.TechnicalDictionary;
import com.softlib.imatch.dictionary.TechnicalDictionaryTerm;
import com.softlib.imatch.matcher.DBTicketFieldsNames;
import com.softlib.imatch.matcher.ITicketFieldsNames;
import com.softlib.imatch.matcher.lucene.customscore.IQueryTester;
import com.softlib.imatch.ticketprocessing.IProcessedTicket.MatchMode;

public class MissedTermsFinder 
{
	private static boolean initialized = false;
	private static Logger log = Logger.getLogger(Indexer.class);
	private static MissedTermsFinder theIndexer;
	static TechnicalDictionary dict;
	static IQueryTester searcher;
	
	public static MissedTermsFinder createFixer() {
		if (!initialized) {
			ConsoleAppRuntimeInfo.init(null);
			DOMConfigurator.configure(RuntimeInfo.getCurrentInfo().getRealPath("/{SolutionConfigFolder}/log4j-console.xml"));
			theIndexer = new MissedTermsFinder();
			dict = (TechnicalDictionary)RuntimeInfo.getCurrentInfo().getBean("dictionary");
			dict.loadDictionary();
			searcher = (IQueryTester)RuntimeInfo.getCurrentInfo().getBean("lucene.searcher");
			initialized = true;
		}
		return theIndexer;
	}
	
	private MissedTermsFinder() {
	}
	
	public void findMissedTerms() throws IOException, ParseException, MatcherException {
		List<Integer> splitSources = Arrays.asList(new Integer[] {17, 20, 15}); 
		for(TechnicalDictionaryTerm term : dict.termsCollection()) {
			BooleanQuery query = new BooleanQuery();
			
			ITicketFieldsNames fieldsNamesConfig = new DBTicketFieldsNames();
			Set<String> fieldsNames = fieldsNamesConfig.getAllFields(MatchMode.all);

			for (String fieldName : fieldsNames) {
				BooleanQuery fieldQuery = new BooleanQuery();
				fieldQuery.add(new BooleanClause(new TermQuery(new Term(fieldName, term.getTermStemmedText())), Occur.MUST));
				query.add(fieldQuery, Occur.SHOULD);				
			}
			
			TopDocsCollector collector = searcher.test(query);
			if(collector.topDocs().totalHits == 0) {
				if(splitSources.contains(term.getTermSource().getSourceId()))
					log.info("Missed term from split source: " + term);
				else
					log.error("Missed term from real source: " + term);
			}
		}
	}
}
