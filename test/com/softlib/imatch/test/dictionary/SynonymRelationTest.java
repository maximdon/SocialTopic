package com.softlib.imatch.test.dictionary;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.apache.log4j.xml.DOMConfigurator;
import org.hibernate.Session;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.softlib.imatch.ConsoleAppRuntimeInfo;
import com.softlib.imatch.RuntimeInfo;
import com.softlib.imatch.common.HibernateUtils;
import com.softlib.imatch.common.SessionMode;
import com.softlib.imatch.dictionary.SynonymsRelation;
import com.softlib.imatch.dictionary.TechnicalDictionary;
import com.softlib.imatch.dictionary.TechnicalDictionaryKey;
import com.softlib.imatch.dictionary.TechnicalDictionaryTerm;
import com.softlib.imatch.dictionary.Wordnet;
import com.softlib.tools.dictionaryparsers.TechTokensParser;

public class SynonymRelationTest {
	private static TechnicalDictionary dictionary;
	private List<TechnicalDictionaryTerm> savedTerms = new ArrayList<TechnicalDictionaryTerm>();
	@BeforeClass
	public static void init()
	{
		ConsoleAppRuntimeInfo.init(null);
		DOMConfigurator.configure(RuntimeInfo.getCurrentInfo().getRealPath("/{SolutionConfigFolder}/log4j-console.xml"));
		RuntimeInfo.getCurrentInfo().getBean("dictionary");
		dictionary = new TechnicalDictionary();
		Wordnet.getInstance();
	}

	@Before
	public void restart()
	{
		dictionary = new TechnicalDictionary();
	}
	
	@After
	public void cleanup()
	{
		for(TechnicalDictionaryTerm term : savedTerms) {
			dictionary.deleteTerm(term);
		}
	}
	
	@Test
	public void testRelateWithContainingNoContaining()
	{
		TechnicalDictionaryKey term1Key = new TechnicalDictionaryKey("term1");
		TechnicalDictionaryTerm term1 = dictionary.addTerm(term1Key);
		TechnicalDictionaryKey term2Key = new TechnicalDictionaryKey("term2");
		TechnicalDictionaryTerm term2 = dictionary.addTerm(term2Key);
		SynonymsRelation rel = new SynonymsRelation();
		try {
			rel.relateWithContaining(term1, term2, "Test");
			Assert.assertEquals(1, term1.getRelations().size());
			Assert.assertEquals(1, term2.getRelations().size());
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		} 	
	}
	
	@Test
	public void testRelateWithContainingSimpleContaining()
	{
		TechnicalDictionaryKey term1Key = new TechnicalDictionaryKey("term1");
		TechnicalDictionaryTerm term1 = dictionary.addTerm(term1Key);
		dictionary.saveTerm(term1);
		savedTerms.add(term1);
		TechnicalDictionaryKey term2Key = new TechnicalDictionaryKey("term2");
		TechnicalDictionaryTerm term2 = dictionary.addTerm(term2Key);
		dictionary.saveTerm(term2);
		savedTerms.add(term2);
		TechnicalDictionaryKey term1ContainingKey = new TechnicalDictionaryKey("term1 installation");
		TechnicalDictionaryTerm term1Containing = dictionary.addTerm(term1ContainingKey);
		dictionary.saveTerm(term1Containing);
		savedTerms.add(term1Containing);
		TechnicalDictionaryKey term2ContainingKey = new TechnicalDictionaryKey("term2 installation");
		TechnicalDictionaryTerm term2Containing = dictionary.addTerm(term2ContainingKey);
		dictionary.saveTerm(term2Containing);
		savedTerms.add(term2Containing);
		SynonymsRelation rel = new SynonymsRelation();
		try {
			rel.relateWithContaining(term1, term2, "Test");
			Assert.assertEquals(1, term1.getRelations().size());
			Assert.assertEquals(1, term2.getRelations().size());
			Assert.assertEquals(1, term1Containing.getRelations().size());
			Assert.assertEquals(1, term2Containing.getRelations().size());
		} catch (Exception e) {
			Assert.fail(e.getMessage());
		} 	
	}
}
