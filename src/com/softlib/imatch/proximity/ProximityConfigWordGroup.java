package com.softlib.imatch.proximity;

import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("wordGroup")
public class ProximityConfigWordGroup 
{
	@XStreamAsAttribute
	@XStreamAlias("source")
	private String sourceName;
	@XStreamImplicit(itemFieldName="word")
	private List<String> words;

	public String getSourceName()
	{
		return sourceName;
	}
	
	public List<String> getWords()
	{
		return words;
	}
}
