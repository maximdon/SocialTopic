package com.softlib.imatch.test.tools;


import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.xml.DOMConfigurator;

import com.softlib.imatch.ConsoleAppRuntimeInfo;
import com.softlib.imatch.RuntimeInfo;
import com.softlib.imatch.common.configuration.IConfigurationResourceLoader;
import com.softlib.imatch.density.DensityCalculation;
import com.softlib.imatch.dictionary.SynonymsRelation;
import com.softlib.imatch.dictionary.TechnicalDictionary;
import com.softlib.imatch.dictionary.TechnicalDictionaryKey;
import com.softlib.imatch.dictionary.TechnicalDictionaryTerm;
import com.softlib.imatch.dictionary.TechnicalTermSource;
import com.softlib.imatch.distance.PositionsByTerm;
import com.softlib.imatch.distance.TermPosition;
import com.softlib.imatch.distance.TermsByPositions;

public class TermsByPositionsTest {

	static private TechnicalTermSource source;
	static private TechnicalDictionary container;

	static List<TechnicalDictionaryTerm> terms;
	
	static private void init() {
		ConsoleAppRuntimeInfo.init(null);
		DOMConfigurator.configure(RuntimeInfo.getCurrentInfo().getRealPath("/{SolutionConfigFolder}/log4j-console.xml"));			
		IConfigurationResourceLoader loader = (IConfigurationResourceLoader)RuntimeInfo.getCurrentInfo().getBean("xmlConfigurationResourceLoader");
		container = new TechnicalDictionary();
		initContainer();
		source = container.addSource("SoftLibTerms");
		source.setSourceBoost(5);
		source.setSourceId(101);
	}
	
	public static void main(String[] args) {
		init();
		process();	
		addToContainer1();
		process();
		addToContainer2();
		process();
	}
	
	static private void process() {
		
		String text = "[ a1   a2    b1  b2   c1  <c2>     mm1  memory        e1 e2\n]";
		String poss = "[123456789.123456789.123456789.123456789.123456789.123456789]";
		
		System.out.println("       "+poss);
		System.out.println("text = "+text);
		
		TermsByPositions termsByPositions = new TermsByPositions();
		termsByPositions.process(terms,text);
		
		System.out.println("Look for "+terms.size()+" terms ...");

		PositionsByTerm positionsByTermRelationship = 
			termsByPositions.getPositionsByRelationship();
		for (TechnicalDictionaryTerm term : positionsByTermRelationship.getMap().keySet()) {
			List<TermPosition> positions = positionsByTermRelationship.getMap().get(term);
			System.out.println("Found Term "+term.getTermText()+" "+positions);
		}
		DensityCalculation densityCalculation = 
			new DensityCalculation(termsByPositions.getPositionsByRelationship());
		int numTerms = terms.size();
		System.out.println("Density(05) = "+densityCalculation.relationshipDensity(numTerms,05));
		System.out.println("Density(10) = "+densityCalculation.relationshipDensity(numTerms,10));
		System.out.println("Density(20) = "+densityCalculation.relationshipDensity(numTerms,20));
		System.out.println("Density(40) = "+densityCalculation.relationshipDensity(numTerms,40));				

		System.out.println(" REPLACE = "+termsByPositions.replace("{", "}",true));
	}
	
	static private TechnicalDictionaryTerm addTerm(String text,TechnicalTermSource source) {
		TechnicalDictionaryTerm term = 
			container.addTerm(new TechnicalDictionaryKey(text));
		term.setTermSource(source);
		return term;
	}

	static public void addToContainer1() {
		SynonymsRelation relation = new SynonymsRelation();
		TechnicalDictionaryTerm d1 = addTerm("memory",source);
		terms.add(d1);
		TechnicalDictionaryTerm d2 = addTerm("mm1",source);
		TechnicalDictionaryTerm d3 = addTerm("mm2",source);
		relation.relate(d1,d2,"Test");
		relation.relate(d1,d3,"Test");
	}

	static public void addToContainer2() {
		TechnicalDictionaryTerm e1 = addTerm("e1",source);
		terms.add(e1);
		TechnicalDictionaryTerm e2 = addTerm("e2",source);
		terms.add(e2);
		TechnicalDictionaryTerm e12 = addTerm("e1 e2",source);
		terms.add(e12);
	}
	
	
	static public void initContainer() {
		terms = new ArrayList<TechnicalDictionaryTerm>();
		
		SynonymsRelation relation = new SynonymsRelation();

		TechnicalDictionaryTerm a1 = addTerm("a1",source);
		terms.add(a1);
		TechnicalDictionaryTerm a2 = addTerm("a2",source);
		relation.relate(a1,a2,"Test");
		TechnicalDictionaryTerm a3 = addTerm("a3",source);
		relation.relate(a1,a3,"Test");
		
		TechnicalDictionaryTerm b1 = addTerm("b1",source);
		terms.add(b1);
		TechnicalDictionaryTerm b2 = addTerm("b2",source);
		relation.relate(b1,b2,"Test");
		TechnicalDictionaryTerm b3 = addTerm("b3",source);
		relation.relate(b1,b3,"Test");

		TechnicalDictionaryTerm c1 = addTerm("c1",source);
		terms.add(c1);
		TechnicalDictionaryTerm c2 = addTerm("c2",source);
		relation.relate(c1,c2,"Test");
		TechnicalDictionaryTerm c3 = addTerm("c3",source);
		relation.relate(c1,c3,"Test");

	}


};
