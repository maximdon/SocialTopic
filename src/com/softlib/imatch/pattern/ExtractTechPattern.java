package com.softlib.imatch.pattern;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import com.softlib.imatch.RuntimeInfo;
import com.softlib.imatch.common.LogUtils;
import com.softlib.imatch.common.TracerFile;
import com.softlib.imatch.common.TracerFileLast;
import com.softlib.imatch.common.configuration.IConfigurationResource;
import com.softlib.imatch.common.configuration.IConfigurationResourceLoader;
import com.softlib.imatch.dictionary.PorterStemmer;
import com.softlib.imatch.dictionary.TechnicalDictionary;
import com.softlib.imatch.dictionary.TechnicalDictionaryTerm;

public class ExtractTechPattern {

	private static Logger log = Logger.getLogger(ExtractTechPattern.class);

	
	private final static int NO_INDEX = -1;

	static private TracerFile filePattern = 
		TracerFileLast.create(TracerFileLast.Pattern,"Pattern",false);
	static private TracerFile uniqFilePattern = 
		TracerFileLast.create(TracerFileLast.Pattern,"uniqPattern",false);
	static private TracerFile filePatternSentence = 
		TracerFileLast.create(TracerFileLast.Pattern,"PatternSentence",false);

	static private TracerFile filePatternMatch = 
		TracerFileLast.create(TracerFileLast.Pattern,"Match",false);
	static private TracerFile filePatternMatchAll = 
		TracerFileLast.create(TracerFileLast.Pattern,"MatchAll",false);

	static private PatternsConfig patternConfig;
	private TechnicalDictionary dictionary;
	static List<String> exists = new ArrayList<String>();
	
	private static PorterStemmer stemmer = new PorterStemmer();
	
	private int maxWordsInPart = 4;
	
	public ExtractTechPattern() {
		LogUtils.debug(log, "Extract Tech Pattern by NLP step constructor");
	}

	private String[] removeSubject(PatternsConfigPattrenPart partConfig,List<String> sequence) {
		List<String> rc = new ArrayList<String>();
		for (String word : sequence) {
			if (!isWordEqualTerm(word,partConfig))
				rc.add(word);
		}
		return rc.toArray(new String[0]);
	}

	private List<PatternPart> getPatternPart(PatternsConfigPattrenPart partConfig,
											 List<String> sequence,
											 boolean isSubjectEmptySplit) {
		List<PatternPart> rc = new ArrayList<PatternPart>();
		Collection<TechnicalDictionaryTerm> terms = 
			dictionary.findInText(removeSubject(partConfig,sequence));
		
		for (TechnicalDictionaryTerm term : partConfig.getPartPredic().getAllowTerms() ) 
			for (String word : sequence) 
				if (isWordEqualTerm(word,term,false)) 
					if (!terms.contains(term))
						terms.add(term);
		
		for (TechnicalDictionaryTerm term : terms) 
			if (partConfig.isAllow(term)) {
				PatternPart part = new PatternPart(partConfig.getAnchor(),term);
				rc.add(part);
				if (partConfig.isSubject() && isSubjectEmptySplit) {
					PatternPart subjectEmptyPart = new PatternPart(partConfig.getAnchor());
					rc.add(subjectEmptyPart);
				}
			}
		return rc;
	}

	private boolean isIndexValid(SortedSet<Integer> sortedIndex) {
		int lastIdx = NO_INDEX;
		for(Integer idx : sortedIndex ) {
			if (lastIdx != NO_INDEX) {
				int gap = idx-lastIdx;
				if (gap>maxWordsInPart || gap==0)
					return false;
			}
			lastIdx = idx;
		}
		return true;
	}

	private List<String> subSequance(List<String> sequance,int from, int to) {
		if (from<0)
			from = 0;
		List<String> rc = new ArrayList<String>();
		int index=0;
		for (String word : sequance) { 
			if (index>=from && index<to)
				rc.add(word);
			index++;
		}
		return rc;
	}

	private Integer fitIndex(PatternsConfigPattrenPart part,Integer index,int subjectIndex) {
		if (part.isSubject())
			return index;
		if (subjectIndex==NO_INDEX)
			return NO_INDEX;
		if (index<subjectIndex)
			return NO_INDEX;
		if (part.isPreSubject()) {
			int preSubIdx = subjectIndex-maxWordsInPart;
			return (preSubIdx<0 ? 0 : preSubIdx);
		}
		return index;
	}
	
	private Integer indexOf(PatternsConfigPattrenPart part,List<String> sequance,int subjectIndex) {
		Integer idx=0;
		for (String split : sequance) {
			if (isWordEqualTerm(split,part) || part.isPreSubject()) {
				int index = fitIndex(part,idx,subjectIndex);
				if (index!=NO_INDEX)
					return index;
			}
			idx++;
		}
		return NO_INDEX;
	}

	private List<PatternData> createPattern(PatternsConfigPattren patternConfig,
								      		Map<TechnicalDictionaryTerm,List<PatternPart>> partsByAnchor) {
		boolean subjectExist = false;
		boolean partExist = false;

		List<PatternData> rc = new ArrayList<PatternData>();
		rc.add(new PatternData(patternConfig.getSource()));

		for (TechnicalDictionaryTerm term : patternConfig.getTerms()) {
			List<PatternData> lastPatterns = new ArrayList<PatternData>();
			lastPatterns.addAll(rc);
			
			List<PatternPart> partList = partsByAnchor.get(term);
			if (partList==null)
				continue;
			rc.clear();

			boolean addNew = false;
			for (PatternPart part : partList) {
				if (part!=null && !part.isEmpty()) {
					if (term.equals(patternConfig.getSubjectPart().getAnchor()))
						subjectExist = true;
					else
						partExist = true;
					
					for (PatternData lastData : lastPatterns) {
						PatternData newData = lastData.clone();
						newData.getTerms().addAll(part.getTerms());
						rc.add(newData);
						addNew = true;
					}
				}
			}
			
			if (!addNew)
				rc.addAll(lastPatterns);
		}

		if (!subjectExist)
			rc.clear();
		if (!partExist) 
			rc.clear();

		return rc;
	}
	
	private String getSentence(List<String> sequance,
							   Map<Integer,TechnicalDictionaryTerm> anchorByIndex,
							   PatternsConfigPattrenPart subjectConfigPart) {
		
		
		int subjectIdx = indexOf(subjectConfigPart,sequance,NO_INDEX);
		if (subjectIdx>maxWordsInPart)
			subjectIdx = subjectIdx - maxWordsInPart;
		else
			subjectIdx = 0;
		int maxIdx = 0;
		for (Integer idx : anchorByIndex.keySet()) {
			if (idx > maxIdx)
				maxIdx = idx;
		}
		List<String> seq = subSequance(sequance, subjectIdx, maxIdx);
		String rc = "";
		for (String word : seq)
			if (!rc.equals(""))
				rc = rc + " " +word;
			else
				rc = word;
		rc.replaceAll("\n","");
		return rc;
	}
	
	static private Map<String,Map<String,String>> sentenceByPattern = 
		new HashMap<String,Map<String,String>>();
		
	synchronized public void printSummary() {
		if (!filePatternMatchAll.isActive())
			return;
		int pidx = 0;
		int midx = 0;
		SortedSet<String> patterns = new TreeSet<String>(sentenceByPattern.keySet());
		for (String pttn : patterns) {
			Map<String,String> sentenceById = sentenceByPattern.get(pttn);
			if (sentenceById.keySet().size()<=1)
				continue;
			filePatternMatchAll.write("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
			filePatternMatchAll.write("[P]{"+TracerFile.getInt(pidx++)+"}"+pttn);
			for (String id : sentenceById.keySet()) 
				filePatternMatchAll.write("[S"+TracerFile.getInt(midx++)+"]{"+id+"}"+sentenceById.get(id));
		}
	}
	
	synchronized private void print(String ticketId,
								    List<String> sequance,
								    PatternsConfigPattrenPart subjectConfigPart,
					   			    PatternData pattern,
					   			    Map<Integer,TechnicalDictionaryTerm> anchorByIndex) {
		if (pattern==null)
			return;
		String patternStr = pattern.getTerms().toString();
		if (!filePatternMatch.isActive())
			return;
		String sentence = getSentence(sequance, anchorByIndex, subjectConfigPart);
		Map<String,String> sentenceById = sentenceByPattern.get(patternStr);
		if (sentenceById==null) {
			sentenceById = new HashMap<String,String>();
			sentenceById.put(ticketId,sentence);
			sentenceByPattern.put(patternStr,sentenceById);
		}
		else {
			if (sentenceById.get(ticketId)!=null)
				return;
			else {
				sentenceById.put(ticketId,sentence);

				filePatternMatch.write("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
				filePatternMatch.write("[P]"+patternStr);
				int idx = 0;
				for (String id : sentenceById.keySet()) 
					filePatternMatch.write("[S"+TracerFile.getInt(idx++)+"]{"+id+"}"+sentenceById.get(id));
			}
		}
				
	}

	private boolean isEmpty(List<PatternPart> partList) {
		if (partList==null || partList.isEmpty())
			return true;
		for (PatternPart part : partList) {
			if (!part.isEmpty())
				return false;
		}
		return true;
	}
	
	private List<PatternData> createPatterns(String ticketId,PatternsConfigPattren patternConfig,List<String> sequance) {
		
		maxWordsInPart = patternConfig.getMaxWordsInPart();
		List<PatternData> rc = new ArrayList<PatternData>();

		Map<TechnicalDictionaryTerm,Integer> indexByAnchor = new HashMap<TechnicalDictionaryTerm,Integer>();
		Map<Integer,TechnicalDictionaryTerm> anchorByIndex = new HashMap<Integer,TechnicalDictionaryTerm>();
		
		int maxIndex = NO_INDEX;
		int subjectIndex = NO_INDEX;
		for (PatternsConfigPattrenPart configPart : patternConfig.getParts()) {
			TechnicalDictionaryTerm term = configPart.getAnchor();
			Integer idxTerm = indexOf(configPart,sequance,subjectIndex);
			if (configPart.isSubject()) 
				subjectIndex = idxTerm;
			if (idxTerm>maxIndex)
				maxIndex=idxTerm;
			indexByAnchor.put(term,idxTerm);
			anchorByIndex.put(idxTerm,term);
		}
		int endIndex = sequance.size() < maxIndex+maxWordsInPart ? sequance.size() : maxIndex+maxWordsInPart;
		anchorByIndex.put(endIndex,null);
		

		SortedSet<Integer> sortedIndex = new TreeSet<Integer>(anchorByIndex.keySet());

		if (!isIndexValid(sortedIndex))
			return rc;

		Map<TechnicalDictionaryTerm,List<PatternPart>> partsByAnchor = 
			new HashMap<TechnicalDictionaryTerm, List<PatternPart>>();
		int lastIdx=NO_INDEX;
		for (Integer idx : sortedIndex) {
			if (lastIdx!=NO_INDEX) {
				TechnicalDictionaryTerm anchor = anchorByIndex.get(lastIdx);
				PatternsConfigPattrenPart partConfig = patternConfig.getPartConfig(anchor);
				List<PatternPart> parts = 
					getPatternPart(partConfig,
								   subSequance(sequance,lastIdx,idx),
								   patternConfig.isSubjectEmptySplit());
				partsByAnchor.put(anchor,parts);
			}
			lastIdx = idx;
		}

		PatternsConfigPattrenPart subjectConfigPart = patternConfig.getSubjectPart();
		TechnicalDictionaryTerm subject = subjectConfigPart.getAnchor();
		List<PatternPart> subjectPart = partsByAnchor.get(subject);

		if (isEmpty(subjectPart) && patternConfig.isSubjectCanBeBehind()) {
			List<PatternPart> part =
				getPatternPart(subjectConfigPart,
							   subSequance(sequance,
									   	   indexByAnchor.get(subject)-maxWordsInPart,
									   	   indexByAnchor.get(subject)),
									   	   patternConfig.isSubjectEmptySplit());
			partsByAnchor.put(subject,part);
		}

		subjectPart = partsByAnchor.get(subject);

		if (isEmpty(subjectPart) && patternConfig.isSubjectEmptySplit()) {
			PatternPart part = new PatternPart(subject);
			partsByAnchor.put(subject,part.getList());
		}

		for (PatternData patternData : createPattern(patternConfig,partsByAnchor)) {
			rc.add(patternData);
			print(ticketId,sequance,subjectConfigPart,patternData,anchorByIndex);
		}
		
		for (PatternData pattern : rc) {
			String patternStr = pattern.getTerms().toString();

			filePattern.write(patternStr);
			if (!exists.contains(patternStr)) {
				uniqFilePattern.write(patternStr);
				exists.add(patternStr);
			}
			filePatternSentence.write("[ S ] {"+toString(sequance)+"}");
			filePatternSentence.write("[ P ] {"+patternStr+"}");
		}

		return rc;

	}

	private String toString(List<String> sequance) {
		String rc = "";
		for (String word : sequance)
			rc = rc + " " + word;
		return rc;
	}

	synchronized public void init(TechnicalDictionary dictionary,String fileName) {
		if (fileName==null)
			fileName = "patterns.xml";
		this.dictionary = dictionary;
		if (patternConfig==null) {
			IConfigurationResourceLoader loader = (IConfigurationResourceLoader) RuntimeInfo.getCurrentInfo().getBean("xmlConfigurationResourceLoader");
			IConfigurationResource resource = loader.loadResource("xml:///{SolutionConfigFolder}/"+fileName+";//patternsConfig");
			patternConfig = (PatternsConfig) resource.getCustomConfiguration(PatternsConfig.class);
			patternConfig.init(dictionary);
		}
	}

	private boolean isWordEqualTerm(String word,PatternsConfigPattrenPart part) {
		for (TechnicalDictionaryTerm term : part.getPartAnchor().getAnchors())
			if (isWordEqualTerm(word,term,part.isSubject()))
				return true;
		return false;
	}

	private boolean isWordEqualTerm(String word,TechnicalDictionaryTerm term,boolean isSubject) {
		if (!isSubject)
			return term.getTermStemmedText().equals(word);
		
		String termStemmed = PorterStemmer.fix(term.getTermStemmedText());
		if (!word.startsWith(termStemmed))
			return false;
		
		String wordStemmed = PorterStemmer.fix(stemmer.stem(word));
		return wordStemmed.equals(termStemmed);
	}
		
	public List<PatternData> process(String sentence,String ticketId) {
		
		List<String> sequance = new ArrayList<String>(Arrays.asList(sentence.split(" ")));
		Map<TechnicalDictionaryTerm,PatternsConfigPattren> configPatternByTerm = 
			patternConfig.getConfigPatternBySubject();

		List<PatternsConfigPattren> patternsConfigFounded = 
			new ArrayList<PatternsConfigPattren>();

		for (String word : sequance) {
			for (TechnicalDictionaryTerm subject : configPatternByTerm.keySet()) {
				PatternsConfigPattren configPattren = configPatternByTerm.get(subject);
				PatternsConfigPattrenPart subjectPart = configPattren.getSubjectPart();
				if (isWordEqualTerm(word,subjectPart)) {
					patternsConfigFounded.add(configPatternByTerm.get(subject));
				}					
			}
		}
		
		List<PatternData> patterns = new ArrayList<PatternData>();

		for (PatternsConfigPattren configPattren : patternsConfigFounded)
			patterns.addAll(createPatterns(ticketId,configPattren,sequance));
		
		return patterns;		
	}	


};
