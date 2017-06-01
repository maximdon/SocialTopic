package com.softlib.imatch.matcher.lucene.customscore;


import org.apache.commons.configuration.ConfigurationException;

import com.softlib.imatch.MatcherException;
import com.softlib.imatch.RuntimeInfo;
import com.softlib.imatch.common.configuration.IConfigurationObject;
import com.softlib.imatch.matcher.SearcherConfiguration;
import com.softlib.imatch.matcher.lucene.ILuceneCustomFactory;
import com.softlib.imatch.matcher.lucene.LuceneWordIndexer;
import com.softlib.imatch.matcher.lucene.LuceneWordSearcher;

public class LuceneWordFactory implements ILuceneCustomFactory {
	
	static public String nameSpace = "word_lucene";
	static private String indexerStr = "indexer";
	static private String searcherStr = "searcher";	
	static public String indexerName = nameSpace + "." + "indexer";
	static public String searcherName = nameSpace + "." + "searcher";
	
	private LuceneWordIndexer indexer;
	private LuceneWordSearcher searcher;

	private IConfigurationObject config;
	
	public LuceneWordFactory()
	{
		RuntimeInfo.getCurrentInfo().registerCustomFactory(this);
	}
	
	public void setConfiguration(IConfigurationObject config)
	{
		this.config = config;
	}

	public Object getBean(String beanName)  {
		if(beanName.equals(indexerStr)) {
			synchronized (this) {
				if (indexer == null)
					try {
						indexer = new LuceneWordIndexer(config);
					} catch (MatcherException e) {
						throw new RuntimeException(e.getMessage());
					}
			}
			return indexer;
		}
		else if(beanName.equals(searcherStr)) {
			synchronized (beanName) {
				if (searcher == null)
					searcher = new LuceneWordSearcher(config);
			}
			return searcher;
		}

		return null;
	}

	public String getNamespace() {
		return nameSpace;
	}

};
