package com.softlib.imatch.test.relations;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.apache.log4j.xml.DOMConfigurator;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.Test;

import com.softlib.imatch.ConsoleAppRuntimeInfo;
import com.softlib.imatch.RuntimeInfo;
import com.softlib.imatch.dictionary.SynonymsRelation;
import com.softlib.imatch.dictionary.TechnicalDictionary;
import com.softlib.imatch.dictionary.TechnicalDictionaryKey;
import com.softlib.imatch.dictionary.TechnicalDictionaryTerm;
import com.softlib.imatch.relations.GeneralDelimitersAlgorithm;
import com.softlib.imatch.relations.NegativeSentimentAlgorithm;
import com.softlib.imatch.relations.RelationAlgorithmContext;

public class NegativeSentimentAlgorithmTest 
{
	private static TechnicalDictionary dictionary;
	
	private List<TechnicalDictionaryTerm> addedTerms = new ArrayList<TechnicalDictionaryTerm>();

	@BeforeClass
	public static void init()
	{
		ConsoleAppRuntimeInfo.init(null);
		DOMConfigurator.configure(RuntimeInfo.getCurrentInfo().getRealPath("/{SolutionConfigFolder}/log4j-console.xml"));
		RuntimeInfo.getCurrentInfo().getBean("dictionary");
		dictionary = new TechnicalDictionary();
		dictionary.loadDictionary();
	}
	
	@After
	public void clean()
	{
		for(TechnicalDictionaryTerm term : addedTerms) {
			term.getAllRelations().clear();
			term.getRelations().clear();
			dictionary.removeTermByUser(term.getTermKey());
		}
	}
	
	@Test
	public void testStartServer()
	{
		NegativeSentimentAlgorithm alg = new NegativeSentimentAlgorithm();
		RelationAlgorithmContext context = new RelationAlgorithmContext();
		context.dictionary = dictionary;
		context.relation = new SynonymsRelation(dictionary);
		String termText = "unable to start server";
		TechnicalDictionaryTerm term = dictionary.addTermByUser(new TechnicalDictionaryKey(termText));
		term.setTermSource(dictionary.getSource(70));
		addedTerms.add(term);
		String term2Text = "start server failed";
		TechnicalDictionaryTerm term2 = dictionary.addTermByUser(new TechnicalDictionaryKey(term2Text));
		term2.setTermSource(dictionary.getSource(70));
		addedTerms.add(term2);
		String term3Text = "error starting server";
		TechnicalDictionaryTerm term3 = dictionary.addTermByUser(new TechnicalDictionaryKey(term3Text));
		term3.setTermSource(dictionary.getSource(70));
		addedTerms.add(term3);		
		alg.relate(context, term, termText.split(" "));
		alg.relate(context, term2, term2Text.split(" "));
		alg.relate(context, term3, term3Text.split(" "));
		alg.finish(context);
		Assert.assertEquals(2, term.getRelations().size());
	}

	@Test
	public void testSwappedWords()
	{
		NegativeSentimentAlgorithm alg = new NegativeSentimentAlgorithm();
		RelationAlgorithmContext context = new RelationAlgorithmContext();
		context.dictionary = dictionary;
		context.relation = new SynonymsRelation(dictionary);
		String termText = "unable to start server";
		TechnicalDictionaryTerm term = dictionary.addTermByUser(new TechnicalDictionaryKey(termText));
		term.setTermSource(dictionary.getSource(70));
		addedTerms.add(term);
		String term2Text = "server start failed";
		TechnicalDictionaryTerm term2 = dictionary.addTermByUser(new TechnicalDictionaryKey(term2Text));
		term2.setTermSource(dictionary.getSource(70));
		addedTerms.add(term2);
		alg.relate(context, term, termText.split(" "));
		alg.relate(context, term2, term2Text.split(" "));
		alg.finish(context);
		Assert.assertEquals(1, term.getRelations().size());
	}

	@Test
	public void testSynonyms()
	{
		NegativeSentimentAlgorithm alg = new NegativeSentimentAlgorithm();
		RelationAlgorithmContext context = new RelationAlgorithmContext();
		context.dictionary = dictionary;
		context.relation = new SynonymsRelation(dictionary);
		String termText = "unable to start server";
		TechnicalDictionaryTerm term = dictionary.addTermByUser(new TechnicalDictionaryKey(termText));
		term.setTermSource(dictionary.getSource(70));
		addedTerms.add(term);
		String term2Text = "launch server failed";
		TechnicalDictionaryTerm term2 = dictionary.addTermByUser(new TechnicalDictionaryKey(term2Text));
		addedTerms.add(term2);
		term2.setTermSource(dictionary.getSource(70));
		TechnicalDictionaryTerm term3 = dictionary.addTermByUser(new TechnicalDictionaryKey("start"));
		addedTerms.add(term3);
		TechnicalDictionaryTerm term4 = dictionary.addTermByUser(new TechnicalDictionaryKey("launch"));
		addedTerms.add(term4);
		SynonymsRelation relation = new SynonymsRelation();
		relation.relate(term3, term4, "Test");
		alg.relate(context, term, termText.split(" "));
		alg.relate(context, term2, term2Text.split(" "));
		alg.finish(context);
		Assert.assertEquals(1, term.getRelations().size());
	}

	@Test
	public void testSwappedSynonyms()
	{
		NegativeSentimentAlgorithm alg = new NegativeSentimentAlgorithm();
		RelationAlgorithmContext context = new RelationAlgorithmContext();
		context.dictionary = dictionary;
		context.relation = new SynonymsRelation(dictionary);
		String termText = "unable to start server";
		TechnicalDictionaryTerm term = dictionary.addTermByUser(new TechnicalDictionaryKey(termText));
		term.setTermSource(dictionary.getSource(70));
		addedTerms.add(term);
		String term2Text = "machine launch failed";
		TechnicalDictionaryTerm term2 = dictionary.addTermByUser(new TechnicalDictionaryKey(term2Text));
		addedTerms.add(term2);
		term2.setTermSource(dictionary.getSource(70));
		TechnicalDictionaryTerm term3 = dictionary.addTermByUser(new TechnicalDictionaryKey("start"));
		addedTerms.add(term3);
		TechnicalDictionaryTerm term4 = dictionary.addTermByUser(new TechnicalDictionaryKey("launch"));
		addedTerms.add(term4);
		TechnicalDictionaryTerm term5 = dictionary.addTermByUser(new TechnicalDictionaryKey("machine"));
		addedTerms.add(term5);
		TechnicalDictionaryTerm term6 = dictionary.addTermByUser(new TechnicalDictionaryKey("server"));
		addedTerms.add(term6);
		SynonymsRelation relation = new SynonymsRelation();
		relation.relate(term3, term4, "Test");
		relation.relate(term5, term6, "Test");
		alg.relate(context, term, termText.split(" "));
		alg.relate(context, term2, term2Text.split(" "));
		alg.finish(context);
		Assert.assertEquals(1, term.getRelations().size());
	}

	@Test
	public void testInvalidSource()
	{
		NegativeSentimentAlgorithm alg = new NegativeSentimentAlgorithm();
		RelationAlgorithmContext context = new RelationAlgorithmContext();
		context.dictionary = dictionary;
		context.relation = new SynonymsRelation(dictionary);
		String termText = "unable to start server";
		TechnicalDictionaryTerm term = dictionary.addTermByUser(new TechnicalDictionaryKey(termText));
		term.setTermSource(dictionary.getSource(10));
		addedTerms.add(term);
		String term2Text = "start server failed";
		TechnicalDictionaryTerm term2 = dictionary.addTermByUser(new TechnicalDictionaryKey(term2Text));
		term2.setTermSource(dictionary.getSource(10));
		addedTerms.add(term2);
		String term3Text = "error starting server";
		TechnicalDictionaryTerm term3 = dictionary.addTermByUser(new TechnicalDictionaryKey(term3Text));
		term3.setTermSource(dictionary.getSource(10));
		addedTerms.add(term3);
		alg.relate(context, term, termText.split(" "));
		alg.relate(context, term2, term2Text.split(" "));
		alg.relate(context, term3, term3Text.split(" "));
		alg.finish(context);
		Assert.assertEquals(0, term.getRelations().size());		
	}
}
