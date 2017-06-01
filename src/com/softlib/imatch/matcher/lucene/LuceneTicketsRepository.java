package com.softlib.imatch.matcher.lucene;

import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;

import com.softlib.imatch.ITicket;
import com.softlib.imatch.MatcherException;
import com.softlib.imatch.RuntimeInfo;
import com.softlib.imatch.common.LogUtils;
import com.softlib.imatch.common.configuration.IConfigurationResourceLoader;
import com.softlib.imatch.dictionary.TechnicalDictionaryTerm;
import com.softlib.imatch.matcher.MatchCandidate;
import com.softlib.imatch.matcher.lucene.customscore.LuceneWordFactory;
import com.softlib.imatch.ticketprocessing.IProcessedTicket;

//TODO destroy repository
public class LuceneTicketsRepository extends LuceneReadonlyTicketsRepository 
{
	protected static final Logger log = Logger.getLogger(LuceneSearcher.class);
	private LuceneIndexer indexer;
	private LuceneWordIndexer wordIndexer;
	
	public LuceneTicketsRepository(IConfigurationResourceLoader loader)
	{
		super(loader);
	}
	
	private LuceneIndexer getLuceneIndexer() {
		synchronized (this) {
		if(indexer == null)
			indexer = (LuceneIndexer) RuntimeInfo.getCurrentInfo().getBean("lucene.indexer");
		}
		return indexer;
	}

	private LuceneWordIndexer getWordIndexer() {
		synchronized (this) {
			if(wordIndexer == null)								
				wordIndexer = (LuceneWordIndexer) RuntimeInfo.getCurrentInfo().getBean(LuceneWordFactory.indexerName);
		}
		return wordIndexer;
	}
	
	private LuceneSearcher getLuceneSearcher() {
		synchronized (this) {
			if(indexer == null)
				indexer = (LuceneIndexer) RuntimeInfo.getCurrentInfo().getBean("lucene.indexer");
			if(searcher == null)
				searcher = (LuceneSearcher) RuntimeInfo.getCurrentInfo().getBean("lucene.searcher");
		}
		return searcher;
	}

	private LuceneWordSearcher getLuceneWordSearcher() {
		synchronized (this) {
			if(wordSearcher == null)
				wordSearcher = (LuceneWordSearcher) RuntimeInfo.getCurrentInfo().getBean(LuceneWordFactory.searcherName);
		}
		return wordSearcher;
	}

	public boolean isExist(TechnicalDictionaryTerm term) throws MatcherException {
		List<Document> documents = 
			getLuceneSearcher().searchTerm(term.getTermStemmedText());
		return documents.size() > 0;
	}
	
	public void remove(TechnicalDictionaryTerm term) throws MatcherException {
		List<Document> documents = 
			getLuceneSearcher().searchTerm(term.getTermStemmedText());
		if (documents==null)
			return;
		getLuceneIndexer().startBatch();
		for (Document doc : documents ) 
			getLuceneIndexer().removeTerm(term,doc);
		getLuceneIndexer().endBatch();
	}

	public Collection<MatchCandidate> add(TechnicalDictionaryTerm term) throws MatcherException {
		if(getLuceneWordSearcher().isDisabled())
			//The index is disabled nothing to do
			return null;
		Collection<MatchCandidate> matchCandidates = find(term.getTermStemmedText());
		if (matchCandidates==null)
			return null;
		matchCandidates.addAll(find(term.getTermText()));		
		getLuceneIndexer().startBatch();
		for ( MatchCandidate matchCandidate : matchCandidates) {
			String ticketId = matchCandidate.getCandidateData().getId();
			String objectId = matchCandidate.getCandidateData().getOriginObjectId();
			Document document = getLuceneSearcher().searchId(ticketId,objectId);
			if (document==null) {
				LogUtils.warn(log, "document %s not found, skipped", ticketId);
				continue;
			}
			getLuceneIndexer().addTerm(term,document);
		}	
		getLuceneIndexer().endBatch();
		getLuceneSearcher().reloadIndex();
		return matchCandidates;
	}
	
	public void add(IProcessedTicket ticket) throws MatcherException {
		getLuceneIndexer().index(ticket);
		getWordIndexer().index(ticket);
	}

	public void update(IProcessedTicket ticket) throws MatcherException {
		//In update process even if the document was not deleted from the index we will try to reindex it
		try {
			getLuceneIndexer().remove(ticket.getId());
		}
		catch(MatcherException e) {
			LogUtils.warn(log, "Unable to delete document %s due to %s", ticket, e.getMessage());
		}
		getLuceneIndexer().index(ticket);
		try {
			getWordIndexer().remove(ticket.getId());
		}
		catch(MatcherException e) {
			LogUtils.warn(log, "Unable to delete document %s due to %s", ticket, e.getMessage());
		}
		getWordIndexer().index(ticket);
	}

	public void remove(IProcessedTicket ticket) throws MatcherException {
		getLuceneIndexer().remove(ticket.getId());
		ITicket orgTicket = ticket.getOriginalTicket();
		getWordIndexer().remove(orgTicket.getId());
	}

	public void startBatchUpdate() throws MatcherException {
		getLuceneIndexer().startBatch();
		getWordIndexer().startBatch();
	}

	public void endBatchUpdate() throws MatcherException {
		getLuceneIndexer().endBatch();
		getWordIndexer().endBatch();
		getLuceneSearcher().reloadIndex();
		getLuceneWordSearcher().reloadIndex();
	}
	
	public void flush() throws MatcherException {
		synchronized (this) {
			if(indexer == null)
				indexer = (LuceneIndexer) RuntimeInfo.getCurrentInfo().getBean("lucene.indexer");
		}
		indexer.flush();
		wordIndexer.flush();
	}
};
