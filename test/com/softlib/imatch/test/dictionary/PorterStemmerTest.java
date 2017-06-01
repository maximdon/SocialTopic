package com.softlib.imatch.test.dictionary;

import java.util.Collection;
import java.util.Iterator;

import junit.framework.Assert;

import org.apache.log4j.xml.DOMConfigurator;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.softlib.imatch.ConsoleAppRuntimeInfo;
import com.softlib.imatch.RuntimeInfo;
import com.softlib.imatch.dictionary.PorterStemmer;
import com.softlib.imatch.dictionary.TechnicalDictionary;
import com.softlib.imatch.dictionary.TechnicalDictionaryTerm;

public class PorterStemmerTest {

	private static PorterStemmer stemmer;

	@BeforeClass
	public static void init()
	{
		ConsoleAppRuntimeInfo.init(null);
		DOMConfigurator.configure(RuntimeInfo.getCurrentInfo().getRealPath("/{SolutionConfigFolder}/log4j-console.xml"));
		try {
			RuntimeInfo.getCurrentInfo().getBean("_no_name");
		}
		catch(Exception e){}
	}

	public PorterStemmerTest()
	{
		stemmer = new PorterStemmer();
	}	

	@Test
	public void testStemAbbriviation() {
		String abbreviation = "odbc";
		String stemmedForm = PorterStemmer.stem(abbreviation);
		Assert.assertEquals(abbreviation, stemmedForm);
	}
	@Test
	public void testStemPlural() {
		String plural = "procedures";
		String stemmedForm = PorterStemmer.stem(plural);
		Assert.assertEquals("procedur", stemmedForm);
	}
	
	@Test
	public void testStemMultiToken() {
		String plural = "stored procedures";
		String stemmedForm = PorterStemmer.stem(plural);
		Assert.assertEquals("store procedur", stemmedForm);
	}
	@Test
	public void testStemMultiTokenWithAbbreviation() {
		String plural = "odbc installations";
		String stemmedForm = PorterStemmer.stem(plural);
		Assert.assertEquals("odbc instal", stemmedForm);
	}
	
	@Test
	public void testStemMultiTokenWithAbbreviation2() {
		String plural = "installations odbc";
		String stemmedForm = PorterStemmer.stem(plural);
		Assert.assertEquals("instal odbc", stemmedForm);
	}
	
	@Test
	public void testStemMultiTokenWithName() {
		String plural = "i3 configuration";
		String stemmedForm = PorterStemmer.stem(plural);
		Assert.assertEquals("i3 configur", stemmedForm);
	}
	
	@Test
	public void testStemMultiTokenWithName2() {
		String plural = "i3 servers configuration";
		String stemmedForm = PorterStemmer.stem(plural);
		Assert.assertEquals("i3 server configur", stemmedForm);
	}
	
	@Test
	public void testStemMultiTokenWithSpecialChars() {
		String plural = "+ i3 ** servers configuration /";
		String stemmedForm = PorterStemmer.stem(plural);
		Assert.assertEquals("+ i3 ** server configur /", stemmedForm);
	}
	
	@Test
	public void testStemmerInitialization() {
		String word1 = "installations";
		String word2 = "configurations";
		String word1Stem = PorterStemmer.stem(word1);
		String word2Stem = PorterStemmer.stem(word2);
		Assert.assertEquals("instal", word1Stem);
		Assert.assertEquals("configur", word2Stem);
	}
	@Test
	public void testStemmingExceptions() {
		String word1Stem = PorterStemmer.stem("weblogic");
		Assert.assertEquals("weblogic", word1Stem);
	}
}
