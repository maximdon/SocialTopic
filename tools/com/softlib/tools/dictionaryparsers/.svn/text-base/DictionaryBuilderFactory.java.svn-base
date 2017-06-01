package com.softlib.tools.dictionaryparsers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.softlib.imatch.IContextInitializationListener;
import com.softlib.imatch.ICustomFactory;
import com.softlib.imatch.RuntimeInfo;
import com.softlib.imatch.StageMngr;
import com.softlib.imatch.common.configuration.IConfigurationObject;
import com.softlib.imatch.common.configuration.IConfigurationResource;
import com.softlib.imatch.common.configuration.IConfigurationResourceLoader;
import com.softlib.imatch.matcher.SearcherConfiguration;

public class DictionaryBuilderFactory implements ICustomFactory, IContextInitializationListener {

	private IConfigurationResourceLoader loader;
	private Map<String, DictionaryBuilder> builders;
	private List<IDictionaryParser> extractParsers;
	private List<IDictionaryParser> postExtractParsers;
	private IConfigurationObject config;	

	public DictionaryBuilderFactory(IConfigurationResourceLoader loader) {
		this.loader = loader;
		this.builders = new HashMap<String, DictionaryBuilder>();
		RuntimeInfo.getCurrentInfo().registerCustomFactory(this);
		RuntimeInfo.getCurrentInfo().registerContextInitializationListener(this);
	}
	
	public Object getBean(String beanName) {
		DictionaryBuilder builder = builders.get(beanName);
		if(builder == null) {
			builder = new DictionaryBuilder(beanName, createSearcherConfig(beanName));
			builder.setParsers(extractParsers,StageMngr.Stage.Extract);
			builder.setParsers(postExtractParsers,StageMngr.Stage.PostExtract);
			builders.put(beanName, builder);
		}
		return builder;
	}

	private SearcherConfiguration createSearcherConfig(String objectId) {
		return (SearcherConfiguration)config.getUnderlinedObject(objectId);
	}
	
	public String getNamespace() {
		return "dictionaryBuilder";
	}
	
	public void contextInitialized() {
		IConfigurationResource resource = loader.loadResource("xml:///{SolutionConfigFolder}/matcher.xml;//searcher");
		config = resource.getConfigurationObject(SearcherConfiguration.class);
	}

	public void setExtractParsers(List<IDictionaryParser> extractParsers) {
		this.extractParsers = extractParsers;
	}

	public void setPostExtractParsers(List<IDictionaryParser> postExtractParsers) {
		this.postExtractParsers = postExtractParsers;
	}

	public static String getBuilderId(String objectId) {		
		return "dictionaryBuilder." + objectId;
	}
	
};
