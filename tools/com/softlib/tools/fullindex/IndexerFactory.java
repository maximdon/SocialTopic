package com.softlib.tools.fullindex;

import java.util.HashMap;
import java.util.Map;

import com.softlib.imatch.IContextInitializationListener;
import com.softlib.imatch.ICustomFactory;
import com.softlib.imatch.RuntimeInfo;
import com.softlib.imatch.common.configuration.IConfigurationObject;
import com.softlib.imatch.common.configuration.IConfigurationResource;
import com.softlib.imatch.common.configuration.IConfigurationResourceLoader;
import com.softlib.imatch.matcher.SearcherConfiguration;
import com.softlib.imatch.matcher.TicketingSystemIntegrationConfig;

public class IndexerFactory implements ICustomFactory, IContextInitializationListener{

	private IConfigurationResourceLoader loader;
	private Map<String, Indexer> indexers;
	private IConfigurationObject config;	

	public IndexerFactory(IConfigurationResourceLoader loader) {
		this.loader = loader;		
		this.indexers = new HashMap<String, Indexer>();
		RuntimeInfo.getCurrentInfo().registerCustomFactory(this);
		RuntimeInfo.getCurrentInfo().registerContextInitializationListener(this);
	}

	public Object getBean(String beanName) {
		Indexer indexer = indexers.get(beanName);
		if(indexer == null) {
			indexer = new Indexer(beanName, createSearcherConfig(beanName));
			indexers.put(beanName, indexer);
		}
		return indexer;
	}

	private SearcherConfiguration createSearcherConfig(String objectId)
	{
		return (SearcherConfiguration)config.getUnderlinedObject(objectId);
	}

	public String getNamespace() {
		return "indexer";
	}

	public void contextInitialized() {
		IConfigurationResource resource = loader.loadResource("xml:///{SolutionConfigFolder}/matcher.xml;//searcher");
		config = resource.getConfigurationObject(SearcherConfiguration.class);
	}
	
	public static String getIndexerId(String objectId) {		
		return "indexer." + objectId;
	}

}
