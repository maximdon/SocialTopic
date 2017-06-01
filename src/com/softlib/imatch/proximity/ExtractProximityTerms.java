package com.softlib.imatch.proximity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.softlib.imatch.RuntimeInfo;
import com.softlib.imatch.common.LogUtils;
import com.softlib.imatch.common.TracerFile;
import com.softlib.imatch.common.TracerFileLast;
import com.softlib.imatch.common.configuration.ConfigurationException;
import com.softlib.imatch.common.configuration.IConfigurationResource;
import com.softlib.imatch.common.configuration.IConfigurationResourceLoader;
import com.softlib.imatch.dictionary.FindTermsInText;
import com.softlib.imatch.dictionary.SynonymsRelation;
import com.softlib.imatch.dictionary.TechnicalDictionary;
import com.softlib.imatch.dictionary.TechnicalDictionaryKey;
import com.softlib.imatch.dictionary.TechnicalDictionaryTerm;
import com.softlib.imatch.dictionary.TechnicalTermSource;
import com.softlib.imatch.distance.TermPosition;
import com.softlib.imatch.distance.TermsByPositions;

public class ExtractProximityTerms  {

	private TechnicalDictionary dictionary;

	static private ProximityConfig proximityConfig;
	private Map<String,TracerFile> traceFileByName = new HashMap<String, TracerFile>();

	synchronized public void init(TechnicalDictionary dictionary) {
		this.dictionary = dictionary;
		if (proximityConfig==null) {
			IConfigurationResourceLoader loader = (IConfigurationResourceLoader) RuntimeInfo.getCurrentInfo().getBean("xmlConfigurationResourceLoader");
			IConfigurationResource resource = loader.loadResource("xml:///{SolutionConfigFolder}/proximity.xml;//proximityConfig");
			proximityConfig = (ProximityConfig) resource.getCustomConfiguration(ProximityConfig.class);
			proximityConfig.init(dictionary);
			for (ProximityConfigRule rule : proximityConfig.getRules()) {
				String sourceName = rule.getProxSrc().getsourceName();
				TracerFile tracerFile = 
					TracerFileLast.create(TracerFileLast.Proximity,sourceName.replaceAll(" ",""),false);
				traceFileByName.put(sourceName,tracerFile);
			}
			 List<ProximityConfigWordGroup> wordGroups = proximityConfig.getWordGroups();
			 if (wordGroups!=null) {
				 for(ProximityConfigWordGroup wordGroup : wordGroups) {
					 TechnicalTermSource source = dictionary.addSource(wordGroup.getSourceName());
					 if(source.getSourceId() < 1)
						 throw new ConfigurationException("Unrecognized source " + wordGroup.getSourceName());
					 TechnicalTermSource patternsSource = dictionary.addSource("Patterns");
					 for (String word : wordGroup.getWords()) {
						 TechnicalDictionaryTerm term = 
							 dictionary.addTerm(new TechnicalDictionaryKey(word));
						 if (term!=null) {
							 term.setCondTermSource(patternsSource, source);
							 term.setStatusDeleteByUser();
							 externTerms.add(term);
						 }
					 }
				 }
			 }
		}
	}
	
	public List<ProximityData> process(String sentence)  {
		List<ProximityData> rc = new ArrayList<ProximityData>();
		String split[] = sentence.split(" ");
		FindTermsInText findTermsInText = dictionary.getFindTermsInText();
		findTermsInText.addExternTerms(externTerms);
		TermsByPositions termsByPositions = findTermsInText.getFoundTerms(split,false);
		Map<TermPosition,TechnicalDictionaryTerm> result =
			termsByPositions.getTermsByPositions();
		ArrayList<TermPosition> sortedIndex = new ArrayList<TermPosition>(result.keySet());
		Collections.sort(sortedIndex);
		for(int i = 0; i < sortedIndex.size(); ++i) {
			TermPosition currentPosition = sortedIndex.get(i);
			String gap = " ";
			int gapSize = 0;
			boolean proxFound = false;
			int j = i + 1;
			TechnicalDictionaryTerm term1 = result.get(currentPosition);
			if(!isValidTerm(term1))
				continue;
			while(j < sortedIndex.size() && !proxFound && gapSize < proximityConfig.getMaxGap()) {
				TermPosition nextPosition = sortedIndex.get(j);
				TechnicalDictionaryTerm term2 = result.get(nextPosition);
				GapData gapData = calculateGap(currentPosition, nextPosition, split);
				gapSize = gapData.gapSize;
				gap = gapData.gapText;
				if (isValidGap(gap) && isValidTerm(term2)) 
				{
					for (ProximityConfigRule rule : proximityConfig.getRules()) {
						ProximityData proximityData = getProximity(rule, term1, term2, sentence, gap, gapSize);
						if (proximityData!=null) {
							rc.add(proximityData);
							proxFound = true;
						}
					}
				}
				j++;				
			}
		}
		return rc;
	}
	
	private void print(TechnicalDictionaryTerm term1,
					   TechnicalDictionaryTerm term2,
					   String sentence,
					   String gap,TracerFile file) {
		if (file == null || !file.isActive())
			return;
		String conjunction = getConjunction(gap);
		if (!conjunction.isEmpty())
			conjunction = " " + conjunction;
		file.write("[S] "+sentence.toLowerCase());
		file.write("[G] {"+term1.getTermText()+"}"+gap+"{"+term2.getTermText()+"}");		
		file.write("[P] {"+term1.getTermText()+"}"+conjunction+" {"+term2.getTermText()+"}");		
	}

	private String getConjunction(String gap) {
		// TODO: this method is used for debug purposes only, check if should be enabled in actual proximity generation
		if (gap.contains(" not ") || 
			gap.contains("n't ") ||
			gap.contains(" wasnot ") ||
			gap.contains(" wont ") )
			return "not";
		if (gap.contains(" is ") || 
			gap.contains(" be ") ||
			gap.contains(" been ") ||
			gap.contains(" are ") ||
			gap.contains(" was ") ||
			gap.contains(" has ") ||
			gap.contains(" have ") ||
			gap.contains(" were ") ||
			gap.contains(" will ") )
			return "";
		if (gap.contains(" can ")) return "can";
		if (gap.contains(" cannot ") || gap.contains(" cann't ")) return "cannot";
		if (gap.contains(" for ")) return "for";
		if (gap.contains(" on ")) return "on";
		if (gap.contains(" from ")) return "from";
		if (gap.contains(" by ")) return "by";
		if (gap.contains(" with ")) return "with";
		if (gap.contains(" without ")) return "without";
		if (gap.contains(" in ") || gap.contains(" into ")) return "in";
		if (gap.contains(" to ")) return "to";

		return "";
	}
		
	private ProximityData getProximity(ProximityConfigRule rule,
					   		   		   TechnicalDictionaryTerm term1,
					   		   		   TechnicalDictionaryTerm term2,
					   		   		   String sentence,String gap,int gapSize) {
		if (gapSize > rule.getMaxGapSize())
			return null;
		
		TechnicalTermSource term1Src = term1.getTermSource();
		TechnicalTermSource term2Src = term2.getTermSource();
		
		if (rule.matchSources(term1Src, term2Src)) {
			TracerFile tracerFile = traceFileByName.get(rule.getProxSrc());
			print(term1,term2,sentence,gap,tracerFile);
			return new ProximityData(term1,getConjunction(gap),term2,rule);
		}
		return null;
	}

	private boolean isValidTerm(TechnicalDictionaryTerm term)
	{
		String text = " " + term.getTermText() + " ";
		if (text.contains(" the ")) return false;
		if (text.contains(" a ")) return false;
		if(term.getTermSource() == null) {
			return false;
		}
		String[] words = text.split(" ");
		for(String word : words)
			if(!isValidWord(word))
				return false;
		return true;
	}
	
	private boolean isValidWord(String word) {
		String clean = word.replaceAll("[,:%?!\\*\\+\\$\\\\()\\]\\[{}]", " ");
		return clean.equals(word);
	}
	
	private boolean isValidGap(String gap) {
		if (!isValidWord(gap))
			//TODO check here, why invalid word in the gap 
			return false;
		String clean = gap.replaceAll("[.]", " ");
		if (!clean.equals(gap))
			return false;
		if (gap.contains(" and "))
			return false;
		if (gap.contains(" or "))
			return false;
		if (gap.contains(" but "))
			return false;
		return true;
	}
	
	private static Set<TechnicalDictionaryTerm> externTerms = 
		new HashSet<TechnicalDictionaryTerm>();
	
	private GapData calculateGap(TermPosition startPosition, TermPosition endPosition, String[] allWords) 
	{
		int startIdx = startPosition.getEnd();
		int endIdx = endPosition.getStart();
		int gapSize;
		String gap = "";
		for(gapSize = 1; gapSize + startIdx < endIdx; gapSize++)
			gap = gap + allWords[startIdx + gapSize] + " " ;
		return new GapData(gap, gapSize - 1);
	}	

	private class GapData
	{
		public String gapText;
		public int gapSize;
		public GapData(String gap, int gapSize) {
			this.gapText = gap;
			this.gapSize = gapSize;
		}
		
	}
};
