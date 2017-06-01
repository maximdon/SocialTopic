package com.softlib.imatch.test.nlp;

import org.apache.log4j.xml.DOMConfigurator;
import org.junit.BeforeClass;
import org.junit.Test;

import junit.framework.Assert;

import com.softlib.imatch.ConsoleAppRuntimeInfo;
import com.softlib.imatch.RuntimeInfo;
import com.softlib.imatch.nlp.NLP;

public class NlpTest 
{
	@BeforeClass
	public static void init()
	{
		try {
			ConsoleAppRuntimeInfo.init(null);
			DOMConfigurator.configure(RuntimeInfo.getCurrentInfo().getRealPath("/{SolutionConfigFolder}/log4j-console.xml"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testSentenceDetector1()
	{
		String text = "I go home";
		NLP nlp = new NLP();
		String[] sentences = nlp.extractSentences(text);
		Assert.assertEquals(1, sentences.length);
	}
	
	@Test
	public void testSentenceDetector2()
	{
		String text = "I go home. Why I'm doing that?";
		NLP nlp = new NLP();
		String[] sentences = nlp.extractSentences(text);
		Assert.assertEquals(2, sentences.length);
	}
	
	@Test
	public void testSentenceDetector3()
	{
		String text = "Why I'm going home? I should stay there. Or not?";
		NLP nlp = new NLP();
		String[] sentences = nlp.extractSentences(text);
		Assert.assertEquals(3, sentences.length);
	}
	
	@Test
	public void testSentenceDetectorMultiLine()
	{
		String text = "Why I'm going home\nI should stay there\nShould I stay or should I go? That a question";
		NLP nlp = new NLP();
		String[] sentences = nlp.extractSentences(text);
		Assert.assertEquals(4, sentences.length);
	}
}
