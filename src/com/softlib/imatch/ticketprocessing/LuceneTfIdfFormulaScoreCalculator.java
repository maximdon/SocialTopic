package com.softlib.imatch.ticketprocessing;

import com.softlib.imatch.MatcherException;
import com.softlib.imatch.RuntimeInfo;
import com.softlib.imatch.common.configuration.IConfigurationResourceLoader;
import com.softlib.imatch.dictionary.TechnicalDictionaryTerm;
import com.softlib.imatch.score.ScoreExplanation;

/**
 * This score calculator uses Lucene tf-idf formula for score calculation
 * @author Maxim Donde
 */
public class LuceneTfIdfFormulaScoreCalculator extends TitleScoreCalculator
{

	public LuceneTfIdfFormulaScoreCalculator(IConfigurationResourceLoader loader) {
		super(loader);
	}

	@Override
	protected float calculateTermScore(TechnicalDictionaryTerm term, float itemBoost, float df, float tf, ScoreExplanation scoreExplanation) {
		int numDocs = 0;
		synchronized (this) {
			if(docFreqProvider == null)
			   docFreqProvider = (IDocFreqProvider) RuntimeInfo.getCurrentInfo().getBean("lucene.searcher");
		}
		try {
			numDocs = docFreqProvider.getNumDocs();
		} catch (MatcherException e) {
			throw new RuntimeException(e);
		}

		float termScore = (float) (1.0 * Math.sqrt(tf) * Math.log10(1.0 * numDocs / df) * itemBoost);

		if(scoreExplanation != null) {
			scoreExplanation.addScore(term.getTermText(), "Source+Length", df * itemBoost, "");
			scoreExplanation.addScore(term.getTermText(), "TF", df * tf, "tf: " + tf);
			scoreExplanation.addScore(term.getTermText(), "total", termScore, "");
		}
		
		return termScore;
	}

	@Override
	protected float getTfScore(int tf, int tf2) 
	{
		return tf;
	}

	@Override
	protected float getDfScore(int df) {
		return df;
	}
}
