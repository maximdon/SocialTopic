package com.softlib.imatch.test.dictionary;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import org.apache.log4j.xml.DOMConfigurator;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.softlib.imatch.ConsoleAppRuntimeInfo;
import com.softlib.imatch.RuntimeInfo;
import com.softlib.imatch.dictionary.SynonymsRelation;
import com.softlib.imatch.dictionary.TechnicalDictionary;
import com.softlib.imatch.dictionary.TechnicalDictionaryKey;
import com.softlib.imatch.dictionary.TechnicalDictionaryTerm;
import com.softlib.imatch.dictionary.TechnicalTermRelation;
import com.softlib.imatch.ticketprocessing.DictionaryTermsMerge;

public class DictionaryTermTest {

	private static TechnicalDictionary dictionary;

	public DictionaryTermTest()
	{
	}
	
	@BeforeClass
	public static void init()
	{
		ConsoleAppRuntimeInfo.init(null);
		DOMConfigurator.configure(RuntimeInfo.getCurrentInfo().getRealPath("/{SolutionConfigFolder}/log4j-console.xml"));
		RuntimeInfo info = RuntimeInfo.getCurrentInfo();
		info.startThread();
		dictionary = (TechnicalDictionary) info.getBean("dictionary");
		dictionary.loadDictionary();
		info.finishThread();
	}

	@Test
	public void testHasWordOneWord()
	{
		TechnicalDictionaryTerm term = dictionary.addTermByUser(new TechnicalDictionaryKey("test"));
		boolean result = term.hasWord("test");
		Assert.assertTrue(result);
	}
	
	@Test
	public void testHasWordOneWordStemming()
	{
		TechnicalDictionaryTerm term = dictionary.addTermByUser(new TechnicalDictionaryKey("test"));
		boolean result = term.hasWord("testing");
		Assert.assertTrue(result);
	}
	
	@Test
	public void testHasWordOneWordStemming2()
	{
		TechnicalDictionaryTerm term = dictionary.addTermByUser(new TechnicalDictionaryKey("tested"));
		boolean result = term.hasWord("testing");
		Assert.assertTrue(result);
	}
	
	@Test
	public void testHasWordTwoWordsStemming()
	{
		TechnicalDictionaryTerm term = dictionary.addTermByUser(new TechnicalDictionaryKey("terms tested"));
		boolean result = term.hasWord("testing");
		result = result & term.hasWord("term");
		Assert.assertTrue(result);
	}
}
