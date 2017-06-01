package com.softlib.imatch.test.tools;


import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.apache.log4j.xml.DOMConfigurator;
import org.junit.Test;

import com.softlib.imatch.ConsoleAppRuntimeInfo;
import com.softlib.imatch.RuntimeInfo;
import com.softlib.imatch.common.configuration.IConfigurationResourceLoader;
import com.softlib.imatch.dictionary.SynonymsRelation;
import com.softlib.imatch.dictionary.TechnicalDictionary;
import com.softlib.imatch.dictionary.TechnicalDictionaryKey;
import com.softlib.imatch.dictionary.TechnicalDictionaryTerm;
import com.softlib.imatch.dictionary.TechnicalTermSource;
import com.softlib.imatch.pattern.ExtractTechPattern;
import com.softlib.imatch.pattern.PatternData;

public class ExtractTechPatternTest {

	static private TechnicalTermSource patternSource;
	static private TechnicalDictionary container;

	static private void printContainer(String title) {
		List<TechnicalDictionaryTerm> patterns = new ArrayList<TechnicalDictionaryTerm>();
		System.out.println("=========================" + title);
		for (TechnicalDictionaryTerm term : container.termsCollection()) { 
			if (term.getTermSource().getSourceId()==patternSource.getSourceId()) 
				patterns.add(term);
			else
				System.out.println("term=> "+term.getTermText());
		}
		System.out.println("-------------------------" + title);
		for (TechnicalDictionaryTerm term : patterns)
			System.out.println("term=> "+term.getTermText());
		System.out.println("=========================" + title);
	}

	static private void print(PatternData pattern) {
		System.out.print("[P] - [");
		boolean first= true;
		for (TechnicalDictionaryTerm term : pattern.getTerms()) { 
			if (!first)
				System.out.print(" ");
			System.out.print(term.getTermText());
			if (first)
				first = false;
		}
		System.out.println("] ");
	}

	static private void print(List<PatternData> patterns,String sentence) {
		System.out.println("~~~~~~Pattern~~~~~~~~~~~~~~~~~~~~~~~~");
		System.out.println("[S] "+sentence);
		for (PatternData pattern : patterns)
			print(pattern);
		System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
	}

	private void init() {
		ConsoleAppRuntimeInfo.init(null);
		DOMConfigurator.configure(RuntimeInfo.getCurrentInfo().getRealPath("/{SolutionConfigFolder}/log4j-console.xml"));			
		IConfigurationResourceLoader loader = (IConfigurationResourceLoader)RuntimeInfo.getCurrentInfo().getBean("xmlConfigurationResourceLoader");
		container = new TechnicalDictionary(loader);
		initContainer();
		patternSource = container.addSource("Patterns5");
		patternSource.setSourceBoost(5);
		patternSource.setSourceId(24);
	}

	@Test
	public void test() {
		init();

		printContainer("Init");

		List<String> sentences = getStrings();
		ExtractTechPattern extractPattern = new ExtractTechPattern();
		extractPattern.init(container,"patterns_test.xml");

		for (String sentence : sentences) {
			List<PatternData> result = extractPattern.process(sentence,"10");
			print(result,sentence);
			for (PatternData pattern : result) {
				List<TechnicalDictionaryTerm> patternTerms = pattern.getTerms();
				TechnicalDictionaryKey termKey = new TechnicalDictionaryKey(patternTerms);
				TechnicalDictionaryTerm term = container.addTerm(termKey);
				if (term!=null) {
					TechnicalTermSource newSource = container.addSource(pattern.getSource());
					term.setTermSource(newSource);
					container.addDocFreq(term.getTermKey());
				}
			}
		}

		printContainer("End");

		container.addComplexSynonym(patternSource," ",false);

		int count = container.getAllTerms(true).size();
		Integer result = 48;
		Assert.assertEquals("Should be "+result+" terms in the container!!", result.intValue() , count);

	}

	static private TechnicalDictionaryTerm addTerm(String text,TechnicalTermSource source) {
		TechnicalDictionaryTerm term = 
			container.addTerm(new TechnicalDictionaryKey(text));
		term.setTermSource(source);
		term.addFrequency();
		term.addFrequency();
		term.addFrequency();
		return term;
	}

	static public void initContainer() {

		TechnicalTermSource source = container.addSource("Versions");
		source.setSourceBoost(5);
		source.setSourceId(1);

		addTerm("upgrade",source);

		addTerm("111",source);
		addTerm("222",source);

		addTerm("system",source);
		addTerm("table",source);

		TechnicalDictionaryTerm termXXX = addTerm("xxx",source);
		TechnicalDictionaryTerm termYYY = addTerm("yyy",source);
		SynonymsRelation relation = new SynonymsRelation();
		relation.relate(termXXX,termYYY, "Test");

	}

	static private List<String> getStrings() {
		List<String> strings = new ArrayList<String>();

		strings.add("the system was upgraded to ver 222 from ver 111");
		strings.add("we start to migrate the system from v 111 into version 222");
		strings.add("bla bla bla system was upgraded from 111 to ver 222 bla bla bla ");
		strings.add("the system was upgraded to 222 from ver 444");
		strings.add("bla bla upgrade table from to 111");
		strings.add("regard the system that was upgrading to version 222 from old v 111");
		strings.add("upgrade table system to 111 rows from a 222");

		strings.add("table is too big");
		strings.add("tables were too big");
		strings.add("is table too big");
		strings.add("table was too big");
		strings.add("table too big");
		strings.add("table error too big");
		strings.add("table not too big");
		strings.add("table is too short");
		strings.add("bla bla is table too short");
			
		// Don't find patterns
		strings.add("bla bla is table have too many errors");
		strings.add("table is fucking too short");
		strings.add("have bug to xxx too mach");
		strings.add("table is to big");
		strings.add("its was upgraded fromd ver 111"); 

		return strings;
	}

};
