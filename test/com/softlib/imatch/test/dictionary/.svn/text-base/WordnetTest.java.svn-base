package com.softlib.imatch.test.dictionary;

import junit.framework.Assert;

import org.apache.log4j.xml.DOMConfigurator;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.softlib.imatch.ConsoleAppRuntimeInfo;
import com.softlib.imatch.RuntimeInfo;
import com.softlib.imatch.dictionary.PorterStemmer;
import com.softlib.imatch.dictionary.Wordnet;
import com.softlib.imatch.dictionary.WordnetWord;

public class WordnetTest {

	private static Wordnet wordnet;

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

	public WordnetTest()
	{
		wordnet = Wordnet.getInstance();
	}	

	@Test
	public void testLemmatizer() {
		String word = "installations";
		String pos = "NN";
		WordnetWord wordnetWord = wordnet.findWord(word, pos);
		Assert.assertEquals("installation", wordnetWord.getLemma());
	}
	
	@Test
	@Ignore("This test currently fails")
	public void testLemmatizer2() {
		String word = "regards";
		String pos = "NN";
		WordnetWord wordnetWord = wordnet.findWord(word, pos);
		Assert.assertEquals("regard", wordnetWord.getLemma());
	}
	
	@Test
	public void testLemmatizerVerb() {
		String word = "gone";
		String pos = "VB";
		WordnetWord wordnetWord = wordnet.findWord(word, pos);
		Assert.assertEquals("go", wordnetWord.getLemma());
	}
	
	@Test
	public void testLemmatizerVerb2() {
		String word = "ran";
		String pos = "VB";
		WordnetWord wordnetWord = wordnet.findWord(word, pos);
		Assert.assertEquals("run", wordnetWord.getLemma());
	}
	
	@Test
	public void testLemmatizerVerb3() {
		String word = "running";
		String pos = "VB";
		WordnetWord wordnetWord = wordnet.findWord(word, pos);
		Assert.assertEquals("run", wordnetWord.getLemma());
	}
	
	@Test
	public void testLemmatizerVerb4() {
		String word = "took";
		String pos = "VB";
		WordnetWord wordnetWord = wordnet.findWord(word, pos);
		Assert.assertEquals("take", wordnetWord.getLemma());
	}

	@Test
	public void testLemmatizerVerb5() {
		String word = "clicking";
		String pos = "VB";
		WordnetWord wordnetWord = wordnet.findWord(word, pos);
		Assert.assertEquals("click", wordnetWord.getLemma());
	}
	
	@Test
	public void testLemmatizerData() {
		String word = "data";
		String pos = "";
		WordnetWord wordnetWord = wordnet.findWord(word, pos);
		Assert.assertEquals("data", wordnetWord.getLemma());
	}
	
	@Test
	public void testLemmatizerStatus() {
		String word = "status";
		String pos = "";
		WordnetWord wordnetWord = wordnet.findWord(word, pos);
		Assert.assertEquals("status", wordnetWord.getLemma());
	}
}
