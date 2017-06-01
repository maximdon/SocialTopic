package com.softlib.imatch.ticketprocessing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import com.softlib.imatch.ITicket;
import com.softlib.imatch.MatcherException;
import com.softlib.imatch.RuntimeInfo;
import com.softlib.imatch.common.ITokenizer;
import com.softlib.imatch.common.LogUtils;
import com.softlib.imatch.common.SimpleTokenizer;
import com.softlib.imatch.common.StringUtils;
import com.softlib.imatch.common.TracerFile;
import com.softlib.imatch.common.TracerFileLast;
import com.softlib.imatch.dictionary.ITechnicalTermsContainer;
import com.softlib.imatch.dictionary.TechnicalDictionary;
import com.softlib.imatch.dictionary.TechnicalDictionaryKey;
import com.softlib.imatch.dictionary.TechnicalDictionaryTerm;
import com.softlib.imatch.dictionary.TechnicalTermSource;
import com.softlib.imatch.nlp.NLP.Chunk;

public class ExtractPPbyNLP extends BaseMeaningExtractionStep {

	private static Logger log = Logger.getLogger(ExtractPPbyNLP.class);

	static private TracerFile ppSummary = 
		TracerFileLast.create(TracerFileLast.PP,"ppSummary",false);
	static private TracerFile ppOnline = 
		TracerFileLast.create(TracerFileLast.PP,"ppOnline",false);
	
	static ITokenizer splitter = new SimpleTokenizer(new char[] {' ', '.', '_'});

	private TechnicalDictionary dictionary;

	
	private Map<String,String> data = new HashMap<String,String>();
	private String[] tags = {"VB","NN"};
	private int index = 0;
	private StringBuilder phrase = new StringBuilder();
	private List<Chunk> lastChunks;
	private boolean vp_term;
	private boolean vp_exist;
	private boolean np_term;
	private boolean np_exist;


	public ExtractPPbyNLP() {
	}

	private void init() {
		index = 0;
		phrase.delete(0, phrase.length());
		lastChunks = new ArrayList<Chunk>();
		vp_term = false;
		np_term = false;
	}

	private void printSummary() {
		if (!ppSummary.isActive())
			return;
		SortedSet<String> patterns = new TreeSet<String>(data.keySet());
		int idx=0;
		for (String pttn : patterns) {
			ppSummary.write("[P] {"+TracerFile.getInt(idx++)+"} "+pttn);
			ppSummary.write("[S] "+data.get(pttn));
		}
	}
	
	private String toString(List<Chunk> chunks) {
		String rc = "";
		for (Chunk chunk : chunks) 
			rc = rc + chunk.toString();
		return rc.replaceAll("\n","");
	}

	public void end() {
		printSummary();
	}
	
	public void run(String fieldName,ITicket ticket,ITechnicalTermsContainer container, StepContext context) throws MatcherException {
		if (dictionary == null)
			dictionary = (TechnicalDictionary) RuntimeInfo.getCurrentInfo().getBean("dictionary");			
		List<String> extractedTerms = new ArrayList<String>();

		container.startSession(fieldName,context.getTempTicketId(), getStepName());

		String nlpData = context.getNlpData();
		if (nlpData==null) {
			nlpData = getData(fieldName,ticket,context);
			if (StringUtils.isEmpty(nlpData))
				return;
			context.setNlpData(nlpData);
		}
		
		List<Chunk> chunks = context.getChunks();
		if (chunks==null) {
			chunks = extractChunks(context,nlpData);
			context.setChunks(chunks);
		}

		init();		
		for (Chunk chunk : chunks) {
			
			switch (index) {
				case 0: {
					if (chunk.getTag().equals("PP")) {
						lastChunks.add(chunk);
						index++;
						break;
					}
					init();
					break;
				}
				case 1: {
					if (chunk.getTag().equals("VP")) {
						lastChunks.add(chunk);
						String vn = chunk.getText(tags);
						if (!vn.isEmpty()) {
							vp_exist = true;
							phrase.append(vn).append(" ");
							TechnicalDictionaryTerm term = dictionary.get(new TechnicalDictionaryKey(vn));
							if (term!=null && term.isEnabled())
								vp_term = true;
							index++;
							break;
						}
					}
					init();
					break;
				}
				case 2: {
					if (chunk.getTag().equals("NP")) {
						lastChunks.add(chunk);
						String vn = chunk.getText(tags);
						if (!vn.isEmpty()) {
							np_exist = true;
							phrase.append(vn);
							TechnicalDictionaryTerm term = dictionary.get(new TechnicalDictionaryKey(vn));
							if (term!=null && term.isEnabled())
								np_term = true;
							index++;
							break;
						}
					}
					init();
					break;
				}
				case 3: {
					if ( np_exist && vp_exist) {
						String phraseStr = phrase.toString().trim();
						boolean eligible = isEligable(phraseStr);
						if(eligible) {
							String sourceName = "PP "+(vp_term?"Term":"Word")+(np_term?"Term":"Word")+" Tokens";
							if (ppOnline.isActive()) {
								data.put(sourceName+" --> "+phraseStr, toString(lastChunks));	
								ppOnline.write("[P] {"+sourceName+"} --> "+phraseStr);
								ppOnline.write("[S] "+toString(lastChunks));
							}
							
							TechnicalDictionaryTerm term = 
								container.addTerm(new TechnicalDictionaryKey(phraseStr));
							extractedTerms.add(phraseStr);
							TechnicalTermSource source = container.addSource(getStepName());
							TechnicalTermSource newSource = container.addSource(sourceName);
							if(term != null)
								term.setCondTermSource(source,newSource);
						}
					}
					init();
					break;
				}
				default: {
					init();
					break;
				}
			
			}
		}
		
		container.endSession((float)1.0, null, true);
		LogUtils.debug(log, "Extract #%s# {%s} Terms:%s", ticket.getId(),fieldName,extractedTerms);
	}

	public String getStepName() {
		return "PP Tokens";
	}


};
