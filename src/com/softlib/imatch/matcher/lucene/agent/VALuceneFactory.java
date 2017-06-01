package com.softlib.imatch.matcher.lucene.agent;

import java.util.HashMap;
import java.util.Map;

import com.softlib.imatch.MatcherException;
import com.softlib.imatch.RuntimeInfo;
import com.softlib.imatch.common.configuration.IConfigurationObject;
import com.softlib.imatch.common.configuration.IConfigurationResource;
import com.softlib.imatch.common.configuration.IConfigurationResourceLoader;
import com.softlib.imatch.matcher.lucene.ILuceneCustomFactory;
import com.softlib.imatch.matcher.lucene.LuceneIndexer;
import com.softlib.imatch.matcher.lucene.LuceneQueryBuilder;
import com.softlib.imatch.matcher.lucene.LuceneSearcher;
import com.softlib.imatch.matcher.lucene.customscore.*;

public class VALuceneFactory implements ILuceneCustomFactory {
	private LuceneIndexer indexer;
	private Map<String, LuceneSearcher> searchers = new HashMap<String, LuceneSearcher>();
	private LuceneQueryBuilder queryBuilder;
	private IConfigurationObject config;
	
	public VALuceneFactory()
	{
		RuntimeInfo.getCurrentInfo().registerCustomFactory(this);
	}
	
	public void setConfiguration(IConfigurationObject config)
	{
		this.config = config;
	}

	public Object getBean(String beanName) {
		if(beanName.equals("indexer")) {
			synchronized (this) {
				if (indexer == null)
					try {
						indexer = new LuceneIndexer(config);
					} catch (MatcherException e) {
						throw new RuntimeException(e.getMessage());
					}
			}
			return indexer;
		}
		else if(beanName.equals("searcher")) {
			return getSearcherForSolution();
		}
		else if(beanName.equals("queryBuilder")) {
			synchronized (beanName) {
				if (queryBuilder == null)
					queryBuilder = new CustomScoreQueryBuilder(config, (IQueryTester) getSearcherForSolution());
			}
			return queryBuilder;
		}

		return null;
	}

	private LuceneSearcher getSearcherForSolution() {
		LuceneSearcher searcher = null;
		synchronized (searchers) {
			searcher = searchers.get(RuntimeInfo.getCurrentInfo().getSolutionName());
			if (searcher == null)
			{
				if(RuntimeInfo.getCurrentInfo().isWebAppMode())
					searcher = (LuceneSearcher)RuntimeInfo.getCurrentInfo().getBean("MatchProcessor");
				else {
					IConfigurationResourceLoader loader = (IConfigurationResourceLoader) RuntimeInfo.getCurrentInfo().getBean("xmlConfigurationResourceLoader");
					searcher = new LuceneVirtualAgentSearcher(loader);
				}
				searchers.put(RuntimeInfo.getCurrentInfo().getSolutionName(), searcher);
			}
		}
		return searcher;
	}

	public String getNamespace() {
		return "lucene";
	}

};
