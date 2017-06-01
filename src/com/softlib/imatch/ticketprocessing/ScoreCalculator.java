package com.softlib.imatch.ticketprocessing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.Map.Entry;

import org.apache.log4j.Logger;

import com.softlib.imatch.IContextInitializationListener;
import com.softlib.imatch.MatcherException;
import com.softlib.imatch.RuntimeInfo;
import com.softlib.imatch.common.LogUtils;
import com.softlib.imatch.common.TracerFile;
import com.softlib.imatch.common.TracerFileLast;
import com.softlib.imatch.common.configuration.IConfigurationObject;
import com.softlib.imatch.common.configuration.IConfigurationResource;
import com.softlib.imatch.common.configuration.IConfigurationResourceLoader;
import com.softlib.imatch.dictionary.TechnicalDictionaryKey;
import com.softlib.imatch.dictionary.TechnicalDictionaryTerm;
import com.softlib.imatch.matcher.CandidateScore;
import com.softlib.imatch.score.ScoreConfig;
import com.softlib.imatch.score.ScoreExplanation;

public class ScoreCalculator implements IScoreCalculator, IContextInitializationListener {
	
	protected static Logger log = Logger.getLogger(ScoreCalculator.class);

	protected IConfigurationResourceLoader loader;
	protected float freqWeight;
	protected float countWeight;
	protected float titleWeight;
	protected boolean useZeroFreqTerms;
	protected boolean useOneFreqTerms;
	protected IDocFreqProvider docFreqProvider;

	//private final float freqWeight = (float) 0.05;
	//private final float countWeight = (float)0.1;
	//private static long averageExecutionTime = 0;
	//private static int  numExecutions = 0;

	
	public ScoreCalculator(IConfigurationResourceLoader loader) {
		this.loader = loader;
		RuntimeInfo.getCurrentInfo().registerContextInitializationListener(this);
	}
	
	public CandidateScore calculateScore(IProcessedTicket sourceTicket, 
										 IProcessedTicket candidateTicket) {
		float directScore;
		if (isAllCommonTermsGeneric(sourceTicket,candidateTicket)) 
			directScore = 0;
		else
			directScore = calculateDirectScore(sourceTicket,candidateTicket);
		
		float oppositeScore = 0;//calculateDirectScore(secondTicket, firstTicket);
		CandidateScore score = new CandidateScore(directScore, oppositeScore);
		return score;
	}
	
	private boolean isAllCommonTermsGeneric(IProcessedTicket sourceTicket, 
	         						        IProcessedTicket candidateTicket) {
		boolean allCommonTermsGeneric = true; 
		for (TechnicalDictionaryTerm term : sourceTicket.getAllTerms(false)) {
			if (sourceTicket.getItemBoost(term)>0 && 
				candidateTicket.contains(term) && 
				!term.isGeneric() &&
				term.isEnabled() ) {
					allCommonTermsGeneric = false;
					term.isGeneric();
					break;
				}
		}
		if (allCommonTermsGeneric) 
			return true;
		return false;
	}
	
	private class Calculate {
		final private IProcessedTicket sourceTicket;
		final private IProcessedTicket candidateTicket;
		
		private boolean canBeLowFreq = false;
		private float matchScore = 0;
		private float maxScore = 0;
		private int count = 0;
		private int totalCount = 0;

		public Calculate(IProcessedTicket sourceTicket,
						 IProcessedTicket candidateTicket) {
			this.sourceTicket = sourceTicket;
			this.candidateTicket = candidateTicket;
		}
		
		public void calculateTerm(TechnicalDictionaryTerm term) {
			if (canBeLowFreq && term.isDeletedByUser())
				return;
			if (!canBeLowFreq && !term.isEnabled())
				return;
			float candidateItemBoost = getItemBoost(term, sourceTicket, candidateTicket);
			int tf = 0;
			int df = 0;
			float dfScore = 0;
			if (candidateItemBoost != 0.0) {
				tf = sourceTicket.getTermFreq(term);
				df = sourceTicket.getDocFreq(term);
				dfScore = getDfScore(df);
				if(candidateTicket.contains(term)) {
					float tfScore = getTfScore(tf, candidateTicket.getTermFreq(term));
					matchScore +=  calculateTermScore(term, candidateItemBoost, dfScore, tfScore, candidateTicket.getScoreExplanation());
					count++;
				}
				float sourceItemBoost = getItemBoost(term, sourceTicket, sourceTicket);
				maxScore += calculateTermScore(term, sourceItemBoost, dfScore, getTfScore(tf, tf), null);				
				totalCount++;
			}

		}
		
		public void setCanBeLowFreq(boolean canBeLowFreq) {
			this.canBeLowFreq = canBeLowFreq;
		}

		public float getDirectScore() {
			if(maxScore == 0)
				return 0;
			else
				return (float) ((matchScore * (1 - countWeight) + count * countWeight) / (maxScore * (1 - countWeight) + totalCount * countWeight));
		}
	};
	
	protected float calculateDirectScore(IProcessedTicket sourceTicket, 
								         IProcessedTicket candidateTicket) {
		Calculate calculate = new Calculate(sourceTicket,candidateTicket);
		
		for (TechnicalDictionaryTerm term : sourceTicket.getAllTerms(false))
			calculate.calculateTerm(term);
		calculate.setCanBeLowFreq(true);
		if(useOneFreqTerms)
			for (TechnicalDictionaryTerm term : sourceTicket.getOneFreqTerms())
				calculate.calculateTerm(term);
		if(useZeroFreqTerms)
			for (TechnicalDictionaryTerm term : sourceTicket.getZeroFreqTerms())
				calculate.calculateTerm(term);
	
		return calculate.getDirectScore();
	}


	public List<TechnicalDictionaryTerm> sortTerms(IProcessedTicket processedTicket) {
		//TODO reuse the code from calculateDirectScore 
		SortedMap<Float, Set<TechnicalDictionaryTerm>> sortedTerms = new TreeMap<Float, Set<TechnicalDictionaryTerm>>(Collections.reverseOrder());
		//Note: sorted terms don't contain low frequency terms
		for (TechnicalDictionaryTerm term : processedTicket.getAllTerms(true)) {
			float matchScore = 0;
			float itemBoost = processedTicket.getItemBoost(term);			
			int tf = 0;
			int df = 0;
			float tfScore = 0;
			float dfScore = 0;
			if(itemBoost != 0) {
				tf = processedTicket.getTermFreq(term);
				df = processedTicket.getDocFreq(term);
				tfScore = getTfScore(tf, tf);
				dfScore = getDfScore(df);
			}
			matchScore += calculateTermScore(term, itemBoost, dfScore, tfScore, null) ;
			Set<TechnicalDictionaryTerm> termsByScore = sortedTerms.get(matchScore);
			if(termsByScore == null) {
				termsByScore = new HashSet<TechnicalDictionaryTerm>();
				sortedTerms.put(matchScore, termsByScore);
			}
			termsByScore.add(term);
		}

		String id = processedTicket.getOriginalTicket().getId();
		TracerFile tracerFile = 
			TracerFileLast.create(TracerFileLast.MostTerms,id,true);

		tracerFile.write("\n### TicketId = "+processedTicket.getOriginalTicket().getId());

		tracerFile.write("\n### Sort Terms : ");
		tracerFile.write("----+------+----+----+------+------+-------------+----+--------------------");
		tracerFile.write(" Idx| Score| DF | TF | Freq | Boost| Term Source : Id | Term Stemmed Text  ");
		tracerFile.write("----+------+----+----+------+------+-------------+----+--------------------");
		int idx=0;

		List<TechnicalDictionaryTerm> result = new ArrayList<TechnicalDictionaryTerm>();
		Map<TechnicalDictionaryKey, Float> boostFactors = new HashMap<TechnicalDictionaryKey, Float>();
		
		StringBuilder termsList = new StringBuilder();
		for(Entry<Float, Set<TechnicalDictionaryTerm>> sortedTerm : sortedTerms.entrySet()) {
			for(TechnicalDictionaryTerm term : sortedTerm.getValue()) {
				result.add(term);
				boostFactors.put(term.getTermKey(), sortedTerm.getKey());
				if(log.isInfoEnabled())
					termsList.append(term).append("(").append(sortedTerm.getKey()).append("),");
				if (tracerFile.isActive()) {
					Float score = sortedTerm.getKey();
					printTerm(tracerFile, processedTicket, term, score, idx);
					idx++;
				}
			}
		}

		Collection<TechnicalDictionaryTerm> lowFreqTerms = 
			processedTicket.getOneFreqTerms();

		if (!lowFreqTerms.isEmpty()) {
			tracerFile.write("--Low Frequency-+----+------+------+-------------+----+--------------------");
			idx=0;
			for (TechnicalDictionaryTerm term : lowFreqTerms) {
				printTerm(tracerFile, processedTicket, term, Float.MIN_VALUE , idx);

			}
		}
		tracerFile.write("----+------+----+----+------+------+-------------+----+--------------------");

		LogUtils.info(log, "For ticket %s, the list of sorted terms is:%s", processedTicket.getId(), termsList);
		String zeroFreqNote = "";
		if(!useZeroFreqTerms)
			zeroFreqNote = "(note zero frequency terms currently not taken into score calcution)";
		LogUtils.info(log, "For ticket %s, the list of zero frequency terms %s is:%s", processedTicket.getId(), zeroFreqNote, processedTicket.getZeroFreqTerms());			
		String oneFreqNote = "";
		if(!useOneFreqTerms)
			oneFreqNote = "(note one frequency terms currently not taken into score calcution)";
		LogUtils.info(log, "For ticket %s, the list of one frequency terms %s is:%s", processedTicket.getId(), oneFreqNote, processedTicket.getOneFreqTerms());		
		processedTicket.setSortedTerms(result);
		processedTicket.setBoostFactors(boostFactors);
		
		return result;
	}
	
	private void printTerm(TracerFile tracerFile,
						   IProcessedTicket processedTicket,
						   TechnicalDictionaryTerm term,Float score,int idx) {
		int srcId = term.getTermSource().getSourceId();
		String srcName = term.getTermSource().getsourceName();
		float boost = term.getTermSource().getSourceBoost();
		int termFreq = processedTicket.getTermFreq(term);
		int docFreq = processedTicket.getDocFreq(term);
		tracerFile.write(
				TracerFile.getInt(idx) ,
				TracerFile.getFloat(score),
				TracerFile.getInt(docFreq),
				TracerFile.getInt(termFreq),
				TracerFile.getFloat((float)(1.0 * Math.sqrt(termFreq) * Math.log10(5000.0 / docFreq))), 
				TracerFile.getFloat(boost),
				TracerFile.getString(srcName,13),
				TracerFile.getInt(srcId),
				term.getTermText()
		);

	}
	
	protected boolean isRematchMode(IProcessedTicket firstTicket, IProcessedTicket secondTicket)
	{
		Collection<TechnicalDictionaryTerm> allTerms = new ArrayList<TechnicalDictionaryTerm>(firstTicket.getAllTerms(false));
		int totalCount = 0;
	
		if(useOneFreqTerms)
			allTerms.addAll(firstTicket.getOneFreqTerms());
		if(useZeroFreqTerms)
			allTerms.addAll(firstTicket.getZeroFreqTerms());
		for (TechnicalDictionaryTerm term : allTerms) {
			if(!term.isEnabled())
				continue;
			float itemBoost = firstTicket.getItemBoost(term);
			if (itemBoost != 0.0) {
				totalCount ++;
			}
		}
		return totalCount <= 3;
	}
	
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
		} catch (MatcherException e) {
			throw new RuntimeException(e);
		}		
		if(percentage <= 0.1)
			result = 1;
		else if(percentage <= 0.3)
			result = (float) 0.85;
		else if(percentage <= 0.5)
			result = (float) 0.7;
		else
			result = (float) 0.5;
		return result;
	}
	
	protected float getTfScore(int tf, int tf2) {
		//TODO take into account tf2
		float tfScore;
		if(tf < 3)
			tfScore = 1; //(float) (Math.min(1.0 * tf / tf2, 1.0 * tf2 / tf) * Math.log(1 + tf) / Math.log(2.0));
		else if(tf > 3 && tf < 7)
			tfScore = (float) 1.5;
		else if(tf > 7 && tf < 11) 
			tfScore = 3;
		else
			tfScore = 5;
		return tfScore * freqWeight;
	}
	
	protected float calculateTermScore(TechnicalDictionaryTerm term, float itemBoost, float df, float tf, ScoreExplanation scoreExplanation) {
		float termScore = df * (itemBoost + tf);
		if(scoreExplanation != null) {
			scoreExplanation.addScore(term.getTermText(), "Source+Length", df * itemBoost, "");
			scoreExplanation.addScore(term.getTermText(), "TF", df * tf, "tf: " + tf);
			scoreExplanation.addScore(term.getTermText(), "total", termScore, "");
		}
		return termScore;
	}
	
	protected float getItemBoost(TechnicalDictionaryTerm term, IProcessedTicket firstTicket, IProcessedTicket secondTicket) {
		return firstTicket.getItemBoost(term);
	}

	public void contextInitialized() {
		IConfigurationResource resource = loader.loadResource("xml:///{SolutionConfigFolder}/matcher.xml;//scorer");
		IConfigurationObject config = resource.getConfigurationObject(ScoreConfig.class);
		freqWeight = (Float)config.getCommonProperty("freqWeight");
		countWeight = (Float)config.getCommonProperty("countWeight");
		titleWeight = (Float)config.getCommonProperty("titleWeight");
		useZeroFreqTerms = (Boolean)config.getCommonProperty("useZeroFreqTerms");
		useOneFreqTerms = (Boolean)config.getCommonProperty("useOneFreqTerms");
	}


};
