package com.softlib.imatch.matcher.lucene;

import com.softlib.imatch.ICustomFactory;
import com.softlib.imatch.MatcherException;
import com.softlib.imatch.common.configuration.IConfigurationObject;
import com.softlib.imatch.matcher.SearcherConfiguration;

public interface ILuceneCustomFactory extends ICustomFactory {
	void setConfiguration(IConfigurationObject config); 
}
