package com.softlib.imatch.ticketprocessing;

import com.softlib.imatch.common.configuration.IConfigurationResourceLoader;
import com.softlib.imatch.dictionary.TechnicalDictionaryTerm;

public class TitleScoreCalculator extends ScoreCalculator 
{	
	public TitleScoreCalculator(IConfigurationResourceLoader loader)
	{
		super(loader);
	}

	@Override
	protected float getItemBoost(TechnicalDictionaryTerm term,
							     IProcessedTicket firstTicket, IProcessedTicket secondTicket) {
		float initialBoost = firstTicket.getItemBoost(term);
		float titleScore = 0;
			if (initialBoost != 0.0) {
				boolean isRematchMode = isRematchMode(firstTicket, secondTicket);
				if(!isRematchMode && firstTicket.getTitleTerms().contains(term)) {
					//In match mode, the term got the boost even if we have it only in the source ticket
					titleScore = 3;
					if(secondTicket.getTitleTerms().contains(term))
						titleScore = 5;
				}
				if(isRematchMode)
					//In rematch mode, even if the source ticket doesn't contain term, title boost should be applied on the candidate
					if(secondTicket.getTitleTerms().contains(term))
						titleScore = 5;					
			}
			if(!firstTicket.equals(secondTicket))
				//When calculating max score, the score explanation should not be created
				secondTicket.getScoreExplanation().addScore(term.getTermText(), "Title", titleScore * titleWeight, "");
		return initialBoost += titleScore * titleWeight;
	}
}
