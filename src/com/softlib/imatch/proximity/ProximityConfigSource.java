package com.softlib.imatch.proximity;

import com.softlib.imatch.dictionary.TechnicalDictionary;
import com.softlib.imatch.dictionary.TechnicalTermSource;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

@XStreamAlias("sourceGroup")
public class ProximityConfigSource 
{
	@XStreamAsAttribute
	@XStreamAlias("name")
	private String sourceName;
	@XStreamOmitField
	private TechnicalTermSource source;
	
	public TechnicalTermSource getSource()
	{
		return source;
	}

	void init(TechnicalDictionary dictionary) {
		source = dictionary.getSource(sourceName);
	}
}
