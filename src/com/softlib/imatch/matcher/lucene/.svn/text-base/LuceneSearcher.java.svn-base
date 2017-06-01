package com.softlib.imatch.matcher.lucene;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.Similarity;
import org.apache.lucene.search.TopDocsCollector;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import com.softlib.imatch.MatcherException;
import com.softlib.imatch.RuntimeInfo;
import com.softlib.imatch.common.LogUtils;
import com.softlib.imatch.common.configuration.IConfigurationObject;
import com.softlib.imatch.matcher.DBTicketFieldsNames;
import com.softlib.imatch.matcher.ITicketFieldsNames;
import com.softlib.imatch.matcher.MatchCandidate;
import com.softlib.imatch.ticketprocessing.IDocFreqProvider;
import com.softlib.imatch.ticketprocessing.IProcessedTicket;
import com.softlib.imatch.ticketprocessing.IProcessedTicket.MatchMode;

/**
 * This class is responsible for running Lucene query on Lucene index.
 * Since the index changed periodically (by background process) we need to reload the underlined lucene searcher every period of time
 * @author Maxim Donde
 *
 */
public abstract class LuceneSearcher implements IDocFreqProvider
{
	private final static int MAX_RESULTS = 200;

	protected IndexSearcher searcher;
	protected IConfigurationObject configuration;
	protected int indexInterval;
	protected LuceneQueryBuilder queryBuilder;
	
	protected static final Logger log = Logger.getLogger(LuceneSearcher.class);

	protected StandardAnalyzer analyzer = 
		new StandardAnalyzer(Version.LUCENE_CURRENT, new HashSet<String>());

	private int numDocs;

	public LuceneSearcher(IConfigurationObject configuration) {
		if(configuration == null)
			return;
		this.configuration = configuration;
		initializeIndexSearcher();
	}

	public Document searchId(String ticketId,String objectId) throws  MatcherException {
	   	String id = 
	   		String.format("+%s:%s +%s:%s", 
	   					  LuceneIndexer.TICKET_ID_FIELD,ticketId, 
	   					  LuceneIndexer.TICKET_OBJECT_ID_FIELD,objectId);
	   	Query query = null;
        QueryParser queryParser = 
			new QueryParser(Version.LUCENE_CURRENT, LuceneWordIndexer.TICKET_ID_FIELD, analyzer);
        try {
			query = queryParser.parse(id);
		} catch (ParseException e) {
			throw new MatcherException("Match failed due Parser error "+e.getMessage(), e);
		}
        
		TopDocsCollector collector = TopScoreDocCollector.create(MAX_RESULTS, true);
		try {
			searcher.search(query, collector);
		} catch (IOException e) {
			throw new MatcherException("Match failed due Searcer error "+e.getMessage(), e);
		}

		if (collector.getTotalHits() > 0) {
			LogUtils.debug(log, "For query %s total %d documents found", query.toString(), collector.getTotalHits());
			for (ScoreDoc scoreDoc : collector.topDocs().scoreDocs) {
				Document hitDoc = null;
				try {
					hitDoc = searcher.doc(scoreDoc.doc);
					if (hitDoc!=null)
						return hitDoc;
				} catch (CorruptIndexException e) {
					throw new MatcherException("Match failed due to internal Lucene error "+ e.getMessage(), e);
				} catch (IOException e) {
					throw new MatcherException("Match failed due to internal Lucene error "+ e.getMessage(), e);
				}
			}
		}
		return null;

	}
	
	private BooleanQuery buildTermQuery(String termText) {
		BooleanQuery rc = new BooleanQuery();
		synchronized (this) {
			if(queryBuilder == null)
				queryBuilder = (LuceneQueryBuilder) RuntimeInfo.getCurrentInfo().getBean("lucene.queryBuilder");
		}
		
		ITicketFieldsNames fieldsNamesConfig = new DBTicketFieldsNames();
		Set<String> fieldsNames = fieldsNamesConfig.getAllFields(MatchMode.all);

		for (String fieldName : fieldsNames) {
			Query termQuery = queryBuilder.buildTermQuery(termText, fieldName);
			rc.add(termQuery, Occur.SHOULD);
		}
		return rc;
	}

	public List<Document> searchTerm(String term) throws  MatcherException {
		Query query = buildTermQuery(term);
		
		TopDocsCollector collector = TopScoreDocCollector.create(MAX_RESULTS, true);
		try {
			searcher.search(query, collector);
		} catch (IOException e) {
			throw new MatcherException("Match failed due Searcer error "+e.getMessage(), e);
		}

		List<Document> rc = new ArrayList<Document>();
		if (collector.getTotalHits() > 0) {
			LogUtils.debug(log, "For query %s total %d documents found", query.toString(), collector.getTotalHits());
			for (ScoreDoc scoreDoc : collector.topDocs().scoreDocs) {
				Document hitDoc = null;
				try {
					hitDoc = searcher.doc(scoreDoc.doc);
					if (hitDoc!=null)
						rc.add(hitDoc);
				} catch (CorruptIndexException e) {
					throw new MatcherException("Match failed due to internal Lucene error "+ e.getMessage(), e);
				} catch (IOException e) {
					throw new MatcherException("Match failed due to internal Lucene error "+ e.getMessage(), e);
				}
			}
		}
		return rc;

	}

	public List<String> search(String text) throws MatcherException {
		if (searcher == null)
			reloadIndex();
		if (searcher == null)
			throw new MatcherException("Lucene search is disabled until next index");
		synchronized (this) {
			if(queryBuilder == null)
				queryBuilder = (LuceneQueryBuilder) RuntimeInfo.getCurrentInfo().getBean("lucene.queryBuilder");
		}
		return searchIsolve(text);
}

	public Collection<MatchCandidate> search(IProcessedTicket ticket)
			throws MatcherException {
		if (searcher == null)
			reloadIndex();
		if (searcher == null)
			throw new MatcherException("Lucene search is disabled until next index");
		synchronized (this) {
			if(queryBuilder == null)
				queryBuilder = (LuceneQueryBuilder) RuntimeInfo.getCurrentInfo().getBean("lucene.queryBuilder");
		}
		return searchInternal(ticket);
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
					return;
				}
				LogUtils.debug(log, "Lucene Searcher reloaded successfully");
			} catch (Exception e) {
				throw new MatcherException("Unable to reload index, the Lucene index is corrupted", e);
			}
		}
	}

	public void shutdown() {
		try {
			if(searcher != null)
				searcher.close();
			searcher = null;
		} catch (IOException e) {
			LogUtils.error(log,	"Unable to shutdown searcher, reason %s", e.getMessage());
			searcher = null;
		}
	}
	
	public int getNumDocs() throws MatcherException {
		if(numDocs == -1)
		   numDocs = searcher.getIndexReader().numDocs();
		return numDocs;
	}
	
	public int getDf(String item) throws MatcherException {
		return 0;
	}
	
	protected abstract Collection<MatchCandidate> searchInternal(IProcessedTicket ticket) throws MatcherException;
	protected abstract List<String> searchIsolve(String text) throws MatcherException;
	protected abstract Similarity getSimilarity();
	
	protected void initializeIndexSearcher() {
		try {
			String indexFilesLocation = (String)configuration.getCommonProperty("indexFilesLocation"); 
			searcher = new IndexSearcher(FSDirectory.open(new File(indexFilesLocation)), true);
		} catch (Exception e) {
			LogUtils.error(log,	"Unable to initialize searcher the search will be disabled until next index, reason %s", e.getMessage());
			searcher = null;
		}
	}
}
