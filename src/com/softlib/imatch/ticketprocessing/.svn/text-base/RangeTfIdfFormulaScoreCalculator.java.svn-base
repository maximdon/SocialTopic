package com.softlib.imatch.ticketprocessing;

import com.softlib.imatch.MatcherException;
import com.softlib.imatch.RuntimeInfo;
import com.softlib.imatch.common.configuration.IConfigurationResourceLoader;
import com.softlib.imatch.dictionary.TechnicalDictionaryTerm;
import com.softlib.imatch.score.ScoreExplanation;

public class RangeTfIdfFormulaScoreCalculator extends TitleScoreCalculator
{

	public RangeTfIdfFormulaScoreCalculator(IConfigurationResourceLoader loader) {
		super(loader);
	}

	@Override
	protected float calculateTermScore(TechnicalDictionaryTerm term, float itemBoost, float df, float tf, ScoreExplanation scoreExplanation) {
		float termScore = tf* df * itemBoost;

		if(scoreExplanation != null) {
			scoreExplanation.addScore(term.getTermText(), "Source+Length", df * itemBoost, "");
			scoreExplanation.addScore(term.getTermText(), "TF", df * tf, "tf: " + tf);
			scoreExplanation.addScore(term.getTermText(), "total", termScore, "");
		}
		
		return termScore;
	}

	@Override
	protected float getTfScore(int tf, int tf2) {
		float tfScore = tfMatrix(tf);
		if(tfScore > 1)
			tfScore *= tfMatrix(tf2);
		return tfScore;
	}

	@Override
	protected float getDfScore(int df) {
		float result = -1;
		int numDocs;
		double percentage;
		try {
			synchronized (this) {
				if(docFreqProvider == null)
				   docFreqProvider = (IDocFreqProvider) RuntimeInfo.getCurrentInfo().getBean("lucene.searcher");
			}
			numDocs = docFreqProvider.getNumDocs();
			percentage = df * 1.0 / numDocs;
		} 
		catch (MatcherException e) {
			throw new RuntimeException(e);
		}
		
		result = dfMatrix(percentage);		
		return result;
	}

	protected float dfMatrix(double percentage) {
		float result;
			 if (percentage <= 0.002) result = (float)1.00;
		else if (percentage <= 0.01 ) result = (float)0.90;
		else if (percentage <= 0.02 ) result = (float)0.75;
		else if (percentage <= 0.04 ) result = (float)0.50;
		else                          result = (float)0.20;
		return result;
	}
	
	protected float tfMatrix(int tf) {
		float tfScore;
		
		     if (tf == 1) tfScore = (float)1.0;
		else if (tf == 2) tfScore = (float)1.1;
		else if (tf == 3) tfScore = (float)1.2;
		else if (tf <  7) tfScore = (float)1.5;
		else if (tf < 10) tfScore = (float)1.0;
		else if (tf < 15) tfScore = (float)0.9;
		else              tfScore = (float)0.7;
		
		return tfScore;
	}
	
};
