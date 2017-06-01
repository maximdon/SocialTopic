package com.softlib.imatch.test.tools;


import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.xml.DOMConfigurator;

import com.softlib.imatch.ConsoleAppRuntimeInfo;
import com.softlib.imatch.RuntimeInfo;
import com.softlib.imatch.common.configuration.IConfigurationResourceLoader;
import com.softlib.imatch.density.DensityData;
import com.softlib.imatch.density.ExtractDensityTerms;
import com.softlib.imatch.dictionary.TechnicalDictionary;
import com.softlib.imatch.dictionary.TechnicalDictionaryKey;
import com.softlib.imatch.dictionary.TechnicalDictionaryTerm;
import com.softlib.imatch.dictionary.TechnicalTermSource;

public class ExtractDensityTermsTest {

	static private TechnicalTermSource densitySource;
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

	static private void print(DensityData data) {
		System.out.println("~~~~~~Proximity~~~~~~~~~~~~~~~~~~~~~~");
		System.out.println("[P] "+data.getText());
		System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
	}
	
	static private void init() {
		ConsoleAppRuntimeInfo.init(null);
		DOMConfigurator.configure(RuntimeInfo.getCurrentInfo().getRealPath("/{SolutionConfigFolder}/log4j-console.xml"));			
		IConfigurationResourceLoader loader = (IConfigurationResourceLoader)RuntimeInfo.getCurrentInfo().getBean("xmlConfigurationResourceLoader");
		container = new TechnicalDictionary(loader);
		initContainer();
		densitySource = container.addSource("Density");
		densitySource.setSourceBoost(5);
		densitySource.setSourceId(45);
	}
	
	public static void main(String[] args) {
		init();
		
		printContainer("Init");

		List<String> sentences = getStrings();
		
		ExtractDensityTerms extractDensity = new ExtractDensityTerms();
		extractDensity.init(container);
		
		for (String sentence : sentences) {
			List<DensityData> result = extractDensity.process(sentence);
			for (DensityData densityData : result) {
				print(densityData);
				TechnicalDictionaryKey termKey = new TechnicalDictionaryKey(densityData.getText());
				TechnicalDictionaryTerm term = container.addTerm(termKey);
				if (term!=null) {
					TechnicalTermSource newSource = container.addSource(densitySource.getsourceName());
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
		
		addTerm("aaa",source);
		addTerm("bbb",source);
		addTerm("ccc",source);
		addTerm("ddd",source);
		addTerm("eee",source);
		addTerm("fff",source);
		addTerm("ggg",source);

	}

	static private List<String> getStrings() {
		List<String> strings = new ArrayList<String>();

		strings.add("xxx aaa xxx bbb xxx xxx ccc xxx ddd xxx eee xxx fff");
		
		return strings;
	}

};
