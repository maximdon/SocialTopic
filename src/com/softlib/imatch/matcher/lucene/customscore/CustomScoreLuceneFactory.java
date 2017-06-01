package com.softlib.imatch.matcher.lucene.customscore;

import com.softlib.imatch.MatcherException;
import com.softlib.imatch.RuntimeInfo;
import com.softlib.imatch.common.configuration.IConfigurationObject;
import com.softlib.imatch.matcher.lucene.ILuceneCustomFactory;
import com.softlib.imatch.matcher.lucene.LuceneIndexer;
import com.softlib.imatch.matcher.lucene.LuceneQueryBuilder;
import com.softlib.imatch.matcher.lucene.LuceneSearcher;

public class CustomScoreLuceneFactory implements ILuceneCustomFactory {
	private LuceneIndexer indexer;
	private LuceneSearcher searcher;
	private LuceneQueryBuilder queryBuilder;

	private IConfigurationObject config;
	
	public CustomScoreLuceneFactory()
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
			synchronized (beanName) {
				if (searcher == null)
					searcher = new LuceneCustomScoreSearcher(config);
			}
			return searcher;
		}
		else if(beanName.equals("queryBuilder")) {
			synchronized (beanName) {
				if (queryBuilder == null)
					queryBuilder = new CustomScoreQueryBuilder(config, (IQueryTester) searcher);
			}
			return queryBuilder;
		}

		return null;
	}

	public String getNamespace() {
		return "lucene";
	}

};
