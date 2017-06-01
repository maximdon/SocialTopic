package com.softlib.imatch.matcher.lucene;

import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.NotImplementedException;

import com.softlib.imatch.IContextInitializationListener;
import com.softlib.imatch.MatcherException;
import com.softlib.imatch.RuntimeInfo;
import com.softlib.imatch.common.configuration.IConfigurationObject;
import com.softlib.imatch.common.configuration.IConfigurationResource;
import com.softlib.imatch.common.configuration.IConfigurationResourceLoader;
import com.softlib.imatch.dictionary.TechnicalDictionaryTerm;
import com.softlib.imatch.matcher.ITicketsRepository;
import com.softlib.imatch.matcher.MatchCandidate;
import com.softlib.imatch.matcher.SearcherConfiguration;
import com.softlib.imatch.matcher.lucene.customscore.LuceneWordFactory;
import com.softlib.imatch.ticketprocessing.IProcessedTicket;

public class LuceneReadonlyTicketsRepository implements ITicketsRepository, IContextInitializationListener 
{
	protected IConfigurationObject configuration;
	protected LuceneSearcher searcher;
	protected LuceneWordSearcher wordSearcher;
	private IConfigurationResourceLoader loader;
	
	public LuceneReadonlyTicketsRepository(IConfigurationResourceLoader loader)
	{
		this.loader = loader;
		//TODO bug this class is registered twice
		RuntimeInfo.getCurrentInfo().registerContextInitializationListener(this);
	}

	public Collection<MatchCandidate> find(IProcessedTicket ticket) throws MatcherException {
		searcher = (LuceneSearcher) RuntimeInfo.getCurrentInfo().getBean("lucene.searcher");
		return searcher.search(ticket);
	}

	public Collection<MatchCandidate> find(String word) throws MatcherException {
		synchronized (this) {
			if(wordSearcher == null)
				wordSearcher = (LuceneWordSearcher) RuntimeInfo.getCurrentInfo().getBean(LuceneWordFactory.searcherName);
		}
		return wordSearcher.search(word);
	}
	
	public List<String> findIsolve(String text) throws MatcherException {
		synchronized (this) {
			if(searcher == null)
				searcher = (LuceneSearcher) RuntimeInfo.getCurrentInfo().getBean("lucene.searcher");
		}
		return searcher.searchIsolve(text);
	}

	public void add(IProcessedTicket ticket) throws MatcherException {
		throw new NotImplementedException();
	}

	public void update(IProcessedTicket ticket) throws MatcherException {
		throw new NotImplementedException();
	}

	public void remove(IProcessedTicket ticket) throws MatcherException {
		throw new NotImplementedException();
	}

	public void startBatchUpdate() throws MatcherException {
		throw new NotImplementedException();
	}

	public void endBatchUpdate() throws MatcherException {
		throw new NotImplementedException();
	}
	
	public Collection<MatchCandidate> add(TechnicalDictionaryTerm term) throws MatcherException {
		throw new NotImplementedException();		
	}

	public void flush() throws MatcherException {
		throw new NotImplementedException();		
	}

	public void remove(TechnicalDictionaryTerm term) throws MatcherException {
		throw new NotImplementedException();	
	}

	public void contextInitialized() {
		IConfigurationResource resource = loader
				.loadResource("xml:///{SolutionConfigFolder}/matcher.xml;//searcher");
		configuration = resource.getConfigurationObject(SearcherConfiguration.class);
		ILuceneCustomFactory customFactory = (ILuceneCustomFactory) RuntimeInfo.getCurrentInfo().getBean("repositoryFactory");
		customFactory.setConfiguration(configuration);
		ILuceneCustomFactory customWordFactory = (ILuceneCustomFactory) RuntimeInfo.getCurrentInfo().getBean("repositoryWordFactory");
		customWordFactory.setConfiguration(configuration);
	}

}
