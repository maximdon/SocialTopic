package com.softlib.imatch.matcher;

import java.util.Collection;
import java.util.List;

import org.apache.lucene.document.Document;

import com.softlib.imatch.MatcherException;
import com.softlib.imatch.dictionary.TechnicalDictionaryTerm;
import com.softlib.imatch.ticketprocessing.IProcessedTicket;

//TODO the big refactoring: the repository classes and interfaces should be:
//ITicketsRepository, DBTicketsRepository implements ITicketsRepository
//IIndexedTicketsRepository : ITicketsRepository, BaseIndexedTicketsRepository(ITicketsRepository) implements IIndexedTicketsRepository
//LuceneIndexedTicketsRepository : BaseIndexedTicketsRepository

public interface ITicketsRepository {
	//TODO do we still need match candidate with the repository?
	Collection<MatchCandidate> find(IProcessedTicket ticket) throws MatcherException;
	
    Collection<MatchCandidate> find(String word) throws MatcherException;

	void add(IProcessedTicket ticket) throws MatcherException;
	
    Collection<MatchCandidate> add(TechnicalDictionaryTerm term) throws MatcherException;

	void remove(IProcessedTicket ticket) throws MatcherException;
	
	void remove(TechnicalDictionaryTerm term) throws MatcherException;
	
	void update(IProcessedTicket ticket) throws MatcherException;
	
	void startBatchUpdate() throws MatcherException;
	
	void endBatchUpdate() throws MatcherException;

	void flush() throws MatcherException;
}
