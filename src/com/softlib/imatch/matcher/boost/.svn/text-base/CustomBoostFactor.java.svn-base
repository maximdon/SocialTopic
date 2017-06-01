package com.softlib.imatch.matcher.boost;

import org.apache.log4j.Logger;

import com.softlib.imatch.common.LogUtils;
import com.softlib.imatch.matcher.Condition;
import com.softlib.imatch.matcher.MatchCandidate;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
@XStreamAlias("boostFactor")
public class CustomBoostFactor 
{
	@XStreamAsAttribute
	@XStreamAlias("name")
	private String factorName;
	@XStreamAsAttribute
	private float boost;
	private Condition condition;
	
	private static final Logger log = Logger.getLogger(CustomBoostFactor.class);
	
	public void applyBoost(MatchCandidate candidate)
	{
		if(candidate == null || candidate.getCandidateData() == null)
			return;
		
		if(condition.isTrue(candidate))
		{
			float newScore = candidate.getScore() * boost;
			LogUtils.debug(log, "Apply custom boost %s on candidate %s, new score is %f", factorName, candidate.toString(), newScore);
			candidate.setScore(newScore);
		}
	}
}
