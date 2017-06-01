package com.softlib.imatch.test.relations;

import junit.framework.Assert;

import org.apache.log4j.xml.DOMConfigurator;
import org.junit.BeforeClass;
import org.junit.Test;

import com.softlib.imatch.ConsoleAppRuntimeInfo;
import com.softlib.imatch.RuntimeInfo;
import com.softlib.imatch.dictionary.SynonymsRelation;
import com.softlib.imatch.dictionary.TechnicalDictionary;
import com.softlib.imatch.dictionary.TechnicalDictionaryKey;
import com.softlib.imatch.dictionary.TechnicalDictionaryTerm;
import com.softlib.imatch.dictionary.Wordnet;
import com.softlib.imatch.relations.GeneralDelimitersAlgorithm;
import com.softlib.imatch.relations.RelationAlgorithmContext;

public class GeneralDelimetersAlgorithmTest 
{
	private static TechnicalDictionary dictionary;

	@BeforeClass
	public static void init()
	{
		ConsoleAppRuntimeInfo.init(null);
		DOMConfigurator.configure(RuntimeInfo.getCurrentInfo().getRealPath("/{SolutionConfigFolder}/log4j-console.xml"));
		RuntimeInfo.getCurrentInfo().getBean("dictionary");
		dictionary = new TechnicalDictionary();
		dictionary.loadDictionary();
	}
	
	@Test
	public void testDB400()
	{
		GeneralDelimitersAlgorithm alg = new GeneralDelimitersAlgorithm();
		RelationAlgorithmContext context = new RelationAlgorithmContext();
		context.dictionary = dictionary;
		context.relation = new SynonymsRelation(dictionary);
		String termText = "db 400";
		TechnicalDictionaryTerm term = dictionary.addTermByUser(new TechnicalDictionaryKey(termText));
		term.setTermSource(dictionary.getSource(10));
		TechnicalDictionaryTerm term2 = dictionary.addTermByUser(new TechnicalDictionaryKey("db400"));
		term2.setTermSource(dictionary.getSource(10));
		TechnicalDictionaryTerm term3 = dictionary.addTermByUser(new TechnicalDictionaryKey("db-400"));
		term3.setTermSource(dictionary.getSource(10));
		alg.relate(context, term, termText.split(" "));
		Assert.assertEquals(2, term.getRelations().size());
	}
	
	@Test
	public void testRBC()
	{
		GeneralDelimitersAlgorithm alg = new GeneralDelimitersAlgorithm();
		RelationAlgorithmContext context = new RelationAlgorithmContext();
		context.dictionary = dictionary;
		context.relation = new SynonymsRelation(dictionary);
		String termText = "r b c";
		TechnicalDictionaryTerm term = dictionary.addTermByUser(new TechnicalDictionaryKey(termText));
		term.setTermSource(dictionary.getSource(10));
		TechnicalDictionaryTerm term2 = dictionary.addTermByUser(new TechnicalDictionaryKey("rbc"));
		term2.setTermSource(dictionary.getSource(10));
		TechnicalDictionaryTerm term3 = dictionary.addTermByUser(new TechnicalDictionaryKey("rb-c"));
		term3.setTermSource(dictionary.getSource(10));
		TechnicalDictionaryTerm term4 = dictionary.addTermByUser(new TechnicalDictionaryKey("r-b-c"));
		term4.setTermSource(dictionary.getSource(10));
		alg.relate(context, term, termText.split(" "));
		Assert.assertEquals(3, term.getRelations().size());
	}
	
	@Test
	public void testDB400Install()
	{
		GeneralDelimitersAlgorithm alg = new GeneralDelimitersAlgorithm();
		RelationAlgorithmContext context = new RelationAlgorithmContext();
		context.dictionary = dictionary;
		context.relation = new SynonymsRelation(dictionary);
		String termText = "db 400 installation";
		TechnicalDictionaryTerm term = dictionary.addTermByUser(new TechnicalDictionaryKey(termText));
		term.setTermSource(dictionary.getSource(10));
		TechnicalDictionaryTerm term2 = dictionary.addTermByUser(new TechnicalDictionaryKey("db400 installation"));
		term2.setTermSource(dictionary.getSource(10));
		TechnicalDictionaryTerm term3 = dictionary.addTermByUser(new TechnicalDictionaryKey("db-400 installation"));
		term3.setTermSource(dictionary.getSource(10));
		alg.relate(context, term, termText.split(" "));
		Assert.assertEquals(2, term.getRelations().size());
	}
}
