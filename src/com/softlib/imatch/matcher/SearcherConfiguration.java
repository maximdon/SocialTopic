package com.softlib.imatch.matcher;

import java.util.ArrayList;
import java.util.Collection;

import com.softlib.imatch.RuntimeInfo;
import com.softlib.imatch.common.configuration.ConfigurationValidationException;
import com.softlib.imatch.common.configuration.IValidatableConfiguration;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.annotations.XStreamImplicitCollection;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

//TODO refactor SearcherConfiguration -> TicketsRepositoryConfiguration
@XStreamAlias("searcher")
public class SearcherConfiguration implements IValidatableConfiguration
{
	@XStreamAsAttribute
	@XStreamAlias("type")
	private String searcherType;
	private String indexFilesLocation;
	private int indexIntervalSeconds;
	private int extractIntervalSeconds;
	private int numThreadsInPool = 2;
	private int indexBufferSize = 100;
	private int extractBufferSize = 300;
	private int minimumNumCandidates = 100;
	private int maximumNumCandidates = 400;
	private int indexInitialIntervalSeconds = 0;
	private int extractInitialIntervalSeconds = 0;
	@XStreamConverter(SearcherConfigurationConverter.class)
	@XStreamAlias("indexSpecialFields")
	private ArrayList<String>filterFields;
	
	public void setSearcherType(String searcherType) {
		this.searcherType = searcherType;
	}
	public String getSearcherType() {
		return searcherType;
	}
	public void setIndexFilesLocation(String indexFilesLocation) {
		this.indexFilesLocation = indexFilesLocation;
	}
	public String getIndexFilesLocation() {
		if(indexFilesLocation == null)
			return null;
		if(indexFilesLocation.indexOf('{') > -1)
			return RuntimeInfo.getCurrentInfo().getRealPath(indexFilesLocation);
		else
			return indexFilesLocation;
	}
	public void setIndexIntervalSeconds(int indexIntervalSeconds) {
		this.indexIntervalSeconds = indexIntervalSeconds;
	}
	public int getIndexIntervalSeconds() {
		return indexIntervalSeconds;
	}
	public void setExtractIntervalSeconds(int extractIntervalSeconds) {
		this.extractIntervalSeconds = extractIntervalSeconds;
	}
	public int getExtractIntervalSeconds() {
		return extractIntervalSeconds;
	}
	public void setNumThreadsInPool(int numTheadsInPool) {
		this.numThreadsInPool = numThreadsInPool;
	}
	public int getNumThreadsInPool() {
		return numThreadsInPool;
	}
	public void setIndexBufferSize(int bufferSize) {
		this.indexBufferSize = bufferSize;
	}
	public int getIndexBufferSize() {
		return indexBufferSize;
	}
	public void setExtractBufferSize(int bufferSize) {
		this.extractBufferSize = bufferSize;
	}
	public int getExtractBufferSize() {
		return extractBufferSize;
	}
	public Collection<String> getFilterFields() {
		return filterFields;
	}
	public void setMinimumNumCandidates(int minimumNumCandidates) {
		this.minimumNumCandidates = minimumNumCandidates;
	}
	public int getMinimumNumCandidates() {
		return minimumNumCandidates;
	}
	public void setMaximumNumCandidates(int maximumNumCandidates) {
		this.maximumNumCandidates = maximumNumCandidates;
	}
	public int getMaximumNumCandidates() {
		return maximumNumCandidates;
	}
	public int getIndexInitialInterval() {
		return indexInitialIntervalSeconds;
	}
	public int getExtractInitialInterval() {
		return extractInitialIntervalSeconds;
	}
	@Override
	public void validate() throws ConfigurationValidationException {
		if(numThreadsInPool < 1)
			throw new ConfigurationValidationException("At least one pool thread required");
		if(indexIntervalSeconds < 1800)
			throw new ConfigurationValidationException("Index interval should be at least 1800 seconds (30 minutes)");
		if(extractIntervalSeconds < 1800)
			throw new ConfigurationValidationException("Extract interval should be at least 1800 seconds (30 minutes)");
		if(indexBufferSize < 10)
			throw new ConfigurationValidationException("Index buffer should be at least 10");
		if(extractBufferSize < 10)
			throw new ConfigurationValidationException("Index buffer should be at least 10");		
	}
}
