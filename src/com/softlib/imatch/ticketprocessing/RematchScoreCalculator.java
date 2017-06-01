package com.softlib.imatch.ticketprocessing;

import java.util.ArrayList;
import java.util.List;

import com.softlib.imatch.RuntimeInfo;
import com.softlib.imatch.common.configuration.IConfigurationResourceLoader;
import com.softlib.imatch.dictionary.TechnicalDictionaryTerm;
import com.softlib.imatch.matcher.CandidateScore;

public class RematchScoreCalculator extends TitleScoreCalculator 
{
	static IScoreCalculator scoreCalculator = (IScoreCalculator) RuntimeInfo.getCurrentInfo().getBean("scoreCalculator");
	
	public RematchScoreCalculator(IConfigurationResourceLoader loader)
	{
		super(loader);
		contextInitialized();
	}

	@Override
	protected float calculateDirectScore(IProcessedTicket firstTicket, IProcessedTicket secondTicket) {
		CandidateScore candidateScore;
		float directScore;
		int count = 0;
		int totalCount = 0;

		List<TechnicalDictionaryTerm> allTerms = new ArrayList<TechnicalDictionaryTerm>(firstTicket.getAllTerms(false));
		if(useOneFreqTerms)
			allTerms.addAll(firstTicket.getOneFreqTerms());
		if(useZeroFreqTerms)
			allTerms.addAll(firstTicket.getZeroFreqTerms());
		for (TechnicalDictionaryTerm term : allTerms) {
			if(!term.isEnabled())
				continue;
			float itemBoost = firstTicket.getItemBoost(term);
			if (itemBoost != 0.0) {
				if(secondTicket.contains(term)) {
					count ++;
				}				
				totalCount ++;
			}
		}

		if(totalCount > 3)
		{
			candidateScore = scoreCalculator.calculateScore(firstTicket, secondTicket);
			return candidateScore.getScore();
		}
		else
		{
			candidateScore = scoreCalculator.calculateScore(firstTicket, secondTicket);
			//We want the main part of score to come from count/totalCount 
			//and difference between different candidates with the same count be less significant part between 0.01 and 0.05
			directScore = (float) ((count/(float)totalCount) + candidateScore.getScore() * 0.05);
			secondTicket.getScoreExplanation().addScore("No term, just count", "count score", count/(float)totalCount, "");
			secondTicket.getScoreExplanation().addScore("No term, just count", "total", directScore, "");
		}
		
		//Date endDate = new Date();
		//long currentExec = (endDate.getTime() - startDate.getTime());
		//long total = averageExecutionTime * numExecutions + currentExec;
		//numExecutions ++;
		//averageExecutionTime = total / numExecutions;
		//LogUtils.debug(log, "Calculate direct score for %s took %d, average is %d", secondTicket.getId(), currentExec, averageExecutionTime);
		return directScore;
	}

	@Override
	public void contextInitialized() {
		super.contextInitialized();
		countWeight = 0;
	}

	
	/*
	public Collection<String> sortTerms(ProcessedField field) {
		List<String> result = new ArrayList<String>();
		
		for(String term : field.getUniqueData()) {
			result.add(term);
		}
		
		return result;
	}
	*/
}
