package com.softlib.imatch.ticketprocessing;


import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("negativeSentimentConfig")
public class NegativeSentimentsConfig {
	
	@XStreamImplicit(itemFieldName="sentimentPattern")
	private List<String> negativeSentiments;

	public List<String>getNegativeSentiments()
	{
		return negativeSentiments;
	}
};
