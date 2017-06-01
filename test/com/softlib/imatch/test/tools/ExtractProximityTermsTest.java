package com.softlib.imatch.test.tools;



import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.xml.DOMConfigurator;

import com.softlib.imatch.ConsoleAppRuntimeInfo;
import com.softlib.imatch.RuntimeInfo;
import com.softlib.imatch.common.configuration.IConfigurationResourceLoader;
import com.softlib.imatch.dictionary.TechnicalDictionary;
import com.softlib.imatch.dictionary.TechnicalDictionaryKey;
import com.softlib.imatch.dictionary.TechnicalDictionaryTerm;
import com.softlib.imatch.dictionary.TechnicalTermSource;
import com.softlib.imatch.proximity.ExtractProximityTerms;
import com.softlib.imatch.proximity.ProximityData;

public class ExtractProximityTermsTest {

	static private TechnicalTermSource patternSource;
	static private TechnicalDictionary container;

	static private void printContainer(String title) {
		List<TechnicalDictionaryTerm> proximity = new ArrayList<TechnicalDictionaryTerm>();
		System.out.println("=========================" + title);
		for (TechnicalDictionaryTerm term : container.termsCollection()) { 
			if (term.getTermSource().getsourceName().contains("Prox")) 
				proximity.add(term);
			else
				System.out.println("term=> "+term.getTermText());
		}
		System.out.println("-------------------------" + title);
		for (TechnicalDictionaryTerm term : proximity)
			System.out.println("term=> "+term.getTermText());
		System.out.println("=========================" + title);
	}

	static private void print(ProximityData proximityData,String sentence) {
		System.out.println("~~~~~~Proximity~~~~~~~~~~~~~~~~~~~~~~");
		System.out.println("[S] "+sentence);
		System.out.println("[P] ["+proximityData.getText()+"] ("+proximityData.getProximitySource().getsourceName()+")");
		System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
	}
	
	static private void init() {
		ConsoleAppRuntimeInfo.init(null);
		DOMConfigurator.configure(RuntimeInfo.getCurrentInfo().getRealPath("/{SolutionConfigFolder}/log4j-console.xml"));			
		IConfigurationResourceLoader loader = (IConfigurationResourceLoader)RuntimeInfo.getCurrentInfo().getBean("xmlConfigurationResourceLoader");
		container = new TechnicalDictionary(loader);
		initContainer();
		patternSource = container.addSource("Prox SoftLib-SoftLib");
		patternSource.setSourceBoost(5);
		patternSource.setSourceId(35);
		patternSource = container.addSource("Prox SoftLib-NLP");
		patternSource.setSourceBoost(5);
		patternSource.setSourceId(36);
		patternSource = container.addSource("Prox SoftLib-Version");
		patternSource.setSourceBoost(5);
		patternSource.setSourceId(37);
		patternSource = container.addSource("Prox SoftLib-NLP Split");
		patternSource.setSourceBoost(5);
		patternSource.setSourceId(38);


	}
	
	public static void main(String[] args) {
		init();
		
		printContainer("Init");

		List<String> sentences = getStrings();
		
		ExtractProximityTerms extractProximity = new ExtractProximityTerms();
		extractProximity.init(container);
		
		for (String sentence : sentences) {
			List<ProximityData> result = extractProximity.process(sentence);
			for (ProximityData proximityData : result) {
				print(proximityData,sentence);
				TechnicalDictionaryKey termKey = new TechnicalDictionaryKey(proximityData.getText());
				TechnicalDictionaryTerm term = container.addTerm(termKey);
				if (term!=null) {
					TechnicalTermSource newSource = container.addSource(proximityData.getProximitySource().getsourceName());
					term.setTermSource(newSource);
					container.addDocFreq(term.getTermKey());
				}
			}
		}
		
		printContainer("End");
				
	}
	
	static private TechnicalDictionaryTerm addTerm(String text,TechnicalTermSource source) {
		TechnicalDictionaryTerm term = 
			container.addTerm(new TechnicalDictionaryKey(text));
		term.setTermSource(source);
		return term;
	}

	static public void initContainer() {

		TechnicalTermSource source = container.addSource("SoftLibTerms");
		source.setSourceBoost(5);
		source.setSourceId(101);
		
		addTerm("import file",source);
		addTerm("number",source);
		addTerm("system",source);
		addTerm("upgrade system",source);

	}

	static private List<String> getStrings() {
		List<String> strings = new ArrayList<String>();

//		strings.add("import file 1 2 3 upgrade system");

		strings.add("do import file wasn't all number in the upgrade system");
		
		return strings;
	}

};
