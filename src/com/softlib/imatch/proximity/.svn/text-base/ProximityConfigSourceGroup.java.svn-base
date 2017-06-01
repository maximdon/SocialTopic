package com.softlib.imatch.proximity;

import java.util.ArrayList;
import java.util.List;

import com.softlib.imatch.dictionary.TechnicalDictionary;
import com.softlib.imatch.dictionary.TechnicalTermSource;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("sourceGroup")
public class ProximityConfigSourceGroup {
	@XStreamAsAttribute
	@XStreamAlias("name")
	private String groupName;
	@XStreamImplicit(itemFieldName="source")
	private List<ProximityConfigSource> configSources;

	public String getGroupName()
	{
		return groupName;
	}
	
	public boolean containsSource(TechnicalTermSource source)
	{
		for(ProximityConfigSource configSource : configSources)
			if(configSource.getSource().equals(source))
				return true;
		return false;
	}

	void init(TechnicalDictionary dictionary) {
		for(ProximityConfigSource source : configSources)
			source.init(dictionary);
	}
};
