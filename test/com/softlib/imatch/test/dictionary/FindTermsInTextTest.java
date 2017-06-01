package com.softlib.imatch.test.dictionary;

import java.util.Collection;
import java.util.HashSet;
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
import com.softlib.imatch.dictionary.FindTermsInText;
import com.softlib.imatch.dictionary.SynonymsRelation;
import com.softlib.imatch.dictionary.TechnicalDictionary;
import com.softlib.imatch.dictionary.TechnicalDictionaryKey;
import com.softlib.imatch.dictionary.TechnicalDictionaryTerm;
import com.softlib.imatch.dictionary.TechnicalTermRelation;
import com.softlib.imatch.distance.TermsByPositions;
import com.softlib.imatch.ticketprocessing.DictionaryTermsMerge;

public class FindTermsInTextTest {

	private static TechnicalDictionary dictionary;

	public FindTermsInTextTest()
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
	public void testFindTermsWithSource() {
		FindTermsInText findUserTerms = dictionary.getFindTermsInText();
		TechnicalDictionaryTerm term1 = dictionary.addTermByUser(new TechnicalDictionaryKey("test find terms"));
		term1.setTermSource(dictionary.getSource(41));
		findUserTerms.addAllowSource(dictionary.getSource(41));
		String[] text = new String[] {"test", "find", "term"};
		TermsByPositions termsByPositions = findUserTerms.getFoundTerms(text, false);
		Assert.assertEquals(1, termsByPositions.getTerms().size());
	}
	
	@Test
	public void testFindTermsWithWrongSource() {
		FindTermsInText findUserTerms = dictionary.getFindTermsInText();
		TechnicalDictionaryTerm term1 = dictionary.addTermByUser(new TechnicalDictionaryKey("test find terms"));
		term1.setTermSource(dictionary.getSource(41));
		findUserTerms.addAllowSource(dictionary.getSource(40));
		String[] text = new String[] {"test", "find", "term"};
		TermsByPositions termsByPositions = findUserTerms.getFoundTerms(text, false);
		Assert.assertEquals(0, termsByPositions.getTerms().size());
	}
	
	@Test
	public void testFindTermsWithLengthLimit() {
		FindTermsInText findUserTerms = dictionary.getFindTermsInText();
		findUserTerms.setMinLength(5);
		findUserTerms.setMaxLength(8);
		dictionary.addTermByUser(new TechnicalDictionaryKey("test find terms size five"));		
		String[] text = new String[] {"test", "find", "term", "size", "five"};
		TermsByPositions termsByPositions = findUserTerms.getFoundTerms(text, false);
		Assert.assertEquals(1, termsByPositions.getTerms().size());
	}
	
	@Test
	public void testFindTermsWithShortLengthLimit() {
		FindTermsInText findUserTerms = dictionary.getFindTermsInText();
		findUserTerms.setMinLength(5);
		findUserTerms.setMaxLength(8);
		dictionary.addTermByUser(new TechnicalDictionaryKey("test find terms"));		
		String[] text = new String[] {"test", "find", "term", "size", "four"};
		TermsByPositions termsByPositions = findUserTerms.getFoundTerms(text, false);
		Assert.assertEquals(0, termsByPositions.getTerms().size());
	}
}
