package com.softlib.imatch.matcher.lucene;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.DefaultSimilarity;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Similarity;
import org.apache.lucene.search.TopDocsCollector;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import com.softlib.imatch.InMemoryTicket;
import com.softlib.imatch.MatcherException;
import com.softlib.imatch.RuntimeInfo;
import com.softlib.imatch.common.LogUtils;
import com.softlib.imatch.common.configuration.IConfigurationObject;
import com.softlib.imatch.matcher.CandidateScore;
import com.softlib.imatch.matcher.MatchCandidate;
import com.softlib.imatch.ticketprocessing.IProcessedTicket;
import com.softlib.imatch.ticketprocessing.ProcessedTicket;

/**
 * This class is responsible for running Lucene query on Lucene index.
 * Since the index changed periodically (by background process) we need to reload the underlined lucene searcher every period of time
 * @author Maxim Donde
 *
 */
public class LuceneWordSearcher {
	private final static int MAX_RESULTS = 500;

	protected IndexSearcher searcher;
	protected IConfigurationObject configuration;
	protected LuceneQueryBuilder queryBuilder;
	private Calendar searcherInitializationTimestamp = GregorianCalendar.getInstance();

	protected static final Logger log = Logger.getLogger(LuceneWordSearcher.class);
	
	//0 indicates status Unknown, 1 - enable, -1 disable
	private int status = 0;
	
	public LuceneWordSearcher(IConfigurationObject config) {
		this.configuration = config;
		initializeIndexSearcher();
	}
	
	private Query buildTermQuery(String termText) throws MatcherException {
		synchronized (this) {
			if(queryBuilder == null)
				queryBuilder = (LuceneQueryBuilder) RuntimeInfo.getCurrentInfo().getBean("lucene.queryBuilder");
		}
		return queryBuilder.buildTermQuery(termText, LuceneWordIndexer.TICKET_BODY_FIELD);
	}

	public Collection<MatchCandidate> searchInternal(String queryString) throws  MatcherException {
		
    	Collection<MatchCandidate> rc = new HashSet<MatchCandidate>();
    	Query query = buildTermQuery(queryString);       
		TopDocsCollector collector = TopScoreDocCollector.create(MAX_RESULTS, true);
		try {
			searcher.search(query, collector);
		} catch (IOException e) {
			throw new MatcherException("Match failed due Searcer error "+e.getMessage(), e);
		}

		Set<ProcessedTicket> candidates = 
			new HashSet<ProcessedTicket>();

		if (collector.getTotalHits() > 0) {
			LogUtils.debug(log, "For query %s total %d documents found", query.toString(), collector.getTotalHits());
			for (ScoreDoc scoreDoc : collector.topDocs().scoreDocs) {
				Document hitDoc = null;
				try {
					hitDoc = searcher.doc(scoreDoc.doc);
				} catch (CorruptIndexException e) {
					throw new MatcherException("Match failed due to internal Lucene error "+ e.getMessage(), e);
				} catch (IOException e) {
					throw new MatcherException("Match failed due to internal Lucene error "+ e.getMessage(), e);
				}
				
				String objectId = hitDoc.get(LuceneWordIndexer.TICKET_OBJECT_ID_FIELD);
				String ticketId = hitDoc.get(LuceneWordIndexer.TICKET_ID_FIELD);
				String ticketBody = hitDoc.get(LuceneWordIndexer.TICKET_BODY_FIELD);
				InMemoryTicket imCandidateTicket = new InMemoryTicket(objectId, ticketId, "", ticketBody);
				
				ProcessedTicket candidateTicket = 
					new ProcessedTicket(imCandidateTicket, ProcessedTicket.getDefaultCalculator());

				candidates.add(candidateTicket);
			}
 		}
 		
		for (IProcessedTicket candidateTicket : candidates) {
			CandidateScore matchScore = new CandidateScore(0);
			MatchCandidate matchCandidate = new MatchCandidate(matchScore, candidateTicket.getOriginalTicket(), !candidateTicket.getData().isEmpty() , candidateTicket);
			rc.add(matchCandidate);
		}
		LogUtils.debug(log, "For query %s total %d candidates found", query.toString(), rc.size());

		return rc;
	}
    

	public Collection<MatchCandidate> search(String queryString)
			throws MatcherException {
		searcherInitializationTimestamp.add(Calendar.SECOND, (int) configuration.getMinValue("indexIntervalSeconds"));
		if (searcherInitializationTimestamp.before(GregorianCalendar.getInstance())	|| searcher == null)
			// The index is not fresh, reload it (probably index ran since the
			// last time...)
			reloadIndex();

		if (searcher == null)
			throw new MatcherException("Lucene search is disabled until next index");

		return searchInternal(queryString);
	}

	public void reloadIndex() throws MatcherException {
		synchronized (this) {
			try {
				if (searcher != null)
					searcher.close();
				searcher = null;
				initializeIndexSearcher();
				if (searcher == null) {
					// Reload failed, do nothing
					LogUtils.warn(log, "Reload index failed since the index files are not readable");
					status = -1;
					return;
				}
				status = 1;
				LogUtils.debug(log, "Lucene Searcher reloaded successfully");
			} catch (Exception e) {
				throw new MatcherException("Unable to reload index, the Lucene index is corrupted", e);
			}
		}
	}
	
	private void initializeIndexSearcher() {
		try {
			String fileName = 
				LuceneWordIndexer.getWordFileName((String) configuration.getCommonProperty("indexFilesLocation"));
			searcher = new IndexSearcher(FSDirectory.open(new File(fileName)), true);
			searcher.setSimilarity(getSimilarity());
			searcherInitializationTimestamp = GregorianCalendar.getInstance();
		} catch (Exception e) {
			LogUtils.error(log,	"Unable to initialize searcher the search will be disabled until next index, reason %s", e.getMessage());
			searcher = null;
		}
	}


	protected Similarity getSimilarity() {
		return new DefaultSimilarity();
	}

	public TopDocsCollector test(BooleanQuery tempQuery) throws MatcherException {
		return test(tempQuery, true);
	}

	private TopDocsCollector test(BooleanQuery tempQuery, boolean logMsg) throws MatcherException {
		TopDocsCollector collector = TopScoreDocCollector.create(10000, true);
		try {
			searcher.search(tempQuery, collector);
			if(logMsg)
				LogUtils.debug(log, "Testing query %s. This query returns %d results", tempQuery.toString(), collector.getTotalHits());
		} catch (IOException e) {
			throw new MatcherException("Match failed due to internal Lucene error " + e.getMessage(), e);
		}
		return collector;
	}
	
	boolean isDisabled() {
		synchronized (this) {
			if(status == 0) 
				tryEnable();
		}
		return status == -1;
	}

	void tryEnable() {
		try {
			reloadIndex();
		} catch (MatcherException e) {
			//Do nothing
		}
		if(searcher == null)
			status = -1;
		else
			status = 1;
	}
}
