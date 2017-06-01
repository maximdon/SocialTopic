package com.softlib.imatch.test.ticketprocessing;

import java.util.Dictionary;

import junit.framework.Assert;

import org.apache.log4j.xml.DOMConfigurator;
import org.junit.BeforeClass;
import org.junit.Test;

import com.softlib.imatch.ConsoleAppRuntimeInfo;
import com.softlib.imatch.ITicket;
import com.softlib.imatch.InMemoryTicket;
import com.softlib.imatch.RuntimeInfo;
import com.softlib.imatch.WebAppRuntimeInfo;
import com.softlib.imatch.common.configuration.IConfigurationResourceLoader;
import com.softlib.imatch.dictionary.ITechnicalDictionary;
import com.softlib.imatch.dictionary.TechnicalDictionary;
import com.softlib.imatch.dictionary.TechnicalDictionaryKey;
import com.softlib.imatch.dictionary.TechnicalDictionaryTerm;
import com.softlib.imatch.matcher.CandidateScore;
import com.softlib.imatch.ticketprocessing.ProcessedTicket;
import com.softlib.imatch.ticketprocessing.RematchScoreCalculator;
import com.softlib.imatch.ticketprocessing.ScoreCalculator;
import com.softlib.imatch.ticketprocessing.TitleScoreCalculator;
import com.softlib.tools.fullindex.FictiveServletCtxt;

public class ScoreCalculatorTest 
{
	private static TechnicalDictionary dictionary = null;
	
	@BeforeClass
	public static void init()
	{
		WebAppRuntimeInfo.init(new FictiveServletCtxt());
		DOMConfigurator.configure(RuntimeInfo.getCurrentInfo().getRealPath("/{SolutionConfigFolder}/log4j-console.xml"));
		dictionary = (TechnicalDictionary)RuntimeInfo.getCurrentInfo().getBean("dictionary");
		dictionary.loadDictionary();
	}
	
	@Test
	public void testTitleScore()
	{
		IConfigurationResourceLoader loader = (IConfigurationResourceLoader)RuntimeInfo.getCurrentInfo().getBean("xmlConfigurationResourceLoader");
		TitleScoreCalculator titleCalc = new TitleScoreCalculator(loader);
		titleCalc.contextInitialized();
		ITicket originalTicket = new InMemoryTicket("cases", "Term1 Term2 Term3 Term4 Term5", "Test");
		ProcessedTicket sourceTicket = new ProcessedTicket(originalTicket, titleCalc);
		ProcessedTicket candidateTicket = new ProcessedTicket(originalTicket, titleCalc);
		sourceTicket.startSession("Title", "1", "NLP NNP");
		TechnicalDictionaryTerm term1 = dictionary.addTermByUser(new TechnicalDictionaryKey("Term1"));
		term1.setTermSource(dictionary.getSource(10));
		TechnicalDictionaryTerm term2 = dictionary.addTermByUser(new TechnicalDictionaryKey("Term2"));
		term2.setTermSource(dictionary.getSource(10));
		TechnicalDictionaryTerm term3 = dictionary.addTermByUser(new TechnicalDictionaryKey("Term3"));
		term3.setTermSource(dictionary.getSource(10));
		TechnicalDictionaryTerm term4 = dictionary.addTermByUser(new TechnicalDictionaryKey("Term4"));
		term4.setTermSource(dictionary.getSource(10));
		TechnicalDictionaryTerm term5 = dictionary.addTermByUser(new TechnicalDictionaryKey("Term5"));
		term5.setTermSource(dictionary.getSource(10));		
		sourceTicket.addTerm(term1.getTermKey());
		sourceTicket.addTerm(term2.getTermKey());
		sourceTicket.addTerm(term3.getTermKey());
		sourceTicket.addTerm(term4.getTermKey());
		sourceTicket.addTerm(term5.getTermKey());
		sourceTicket.endSession(0, null, false);
		candidateTicket.startSession("Title", "1", "NLP NNP");
		candidateTicket.addTerm(term1.getTermKey());
		candidateTicket.addTerm(term2.getTermKey());
		candidateTicket.addTerm(term3.getTermKey());
		candidateTicket.addTerm(term4.getTermKey());
		candidateTicket.endSession(0, null, false);		
		CandidateScore titleScore = titleCalc.calculateScore(sourceTicket, candidateTicket);	
		ITicket originalTicket2 = new InMemoryTicket("cases", "Test", "Term1 Term2 Term3 Term4 Term5");
		ProcessedTicket candidateTicket2 = new ProcessedTicket(originalTicket2, titleCalc);
		candidateTicket2.startSession("Body", "1", "NLP NNP");
		candidateTicket2.addTerm(term1.getTermKey());
		candidateTicket2.addTerm(term2.getTermKey());
		candidateTicket2.addTerm(term3.getTermKey());
		candidateTicket2.addTerm(term4.getTermKey());
		candidateTicket2.endSession(0, null, false);		
		CandidateScore regularScore = titleCalc.calculateScore(sourceTicket, candidateTicket2);		
		Assert.assertTrue(titleScore.getScore() > regularScore.getScore());
		Assert.assertEquals(1.2, titleScore.getScore() / regularScore.getScore());
	}
	
	@Test
	public void testBasicScore()
	{
		IConfigurationResourceLoader loader = (IConfigurationResourceLoader)RuntimeInfo.getCurrentInfo().getBean("xmlConfigurationResourceLoader");
		ScoreCalculator calc = new ScoreCalculator(loader);
		ITicket originalTicket = new InMemoryTicket("cases", "1", "Test");
		ProcessedTicket sourceTicket = new ProcessedTicket(originalTicket, calc);
		ProcessedTicket candidateTicket = new ProcessedTicket(originalTicket, calc);
		sourceTicket.startSession("Title", "1", "NLP NNP");
		TechnicalDictionaryTerm term1 = dictionary.addTermByUser(new TechnicalDictionaryKey("Term1"));
		term1.setTermSource(dictionary.getSource(10));
		TechnicalDictionaryTerm term2 = dictionary.addTermByUser(new TechnicalDictionaryKey("Term2"));
		term2.setTermSource(dictionary.getSource(10));
		TechnicalDictionaryTerm term3 = dictionary.addTermByUser(new TechnicalDictionaryKey("Term3"));
		term3.setTermSource(dictionary.getSource(10));
		TechnicalDictionaryTerm term4 = dictionary.addTermByUser(new TechnicalDictionaryKey("Term4"));
		term4.setTermSource(dictionary.getSource(10));
		TechnicalDictionaryTerm term5 = dictionary.addTermByUser(new TechnicalDictionaryKey("Term5"));
		term5.setTermSource(dictionary.getSource(10));		
		sourceTicket.addTerm(term1.getTermKey());
		sourceTicket.addTerm(term2.getTermKey());
		sourceTicket.addTerm(term3.getTermKey());
		sourceTicket.addTerm(term4.getTermKey());
		sourceTicket.addTerm(term5.getTermKey());
		sourceTicket.endSession(0, null, false);
		candidateTicket.startSession("Title", "1", "NLP NNP");
		candidateTicket.addTerm(term1.getTermKey());
		candidateTicket.addTerm(term2.getTermKey());
		candidateTicket.addTerm(term3.getTermKey());
		candidateTicket.addTerm(term4.getTermKey());
		candidateTicket.endSession(0, null, false);		
		CandidateScore titleScore = calc.calculateScore(sourceTicket, candidateTicket);
		Assert.assertEquals((float)0.8, titleScore.getScore());
	}
	
	@Test
	public void testRematchScore()
	{
		IConfigurationResourceLoader loader = (IConfigurationResourceLoader)RuntimeInfo.getCurrentInfo().getBean("xmlConfigurationResourceLoader");
		RematchScoreCalculator calc = new RematchScoreCalculator(loader);
		ITicket originalTicket = new InMemoryTicket("cases", "1", "Test");
		ProcessedTicket sourceTicket = new ProcessedTicket(originalTicket, calc);
		ProcessedTicket candidateTicket = new ProcessedTicket(originalTicket, calc);
		sourceTicket.startSession("Title", "1", "NLP NNP");
		TechnicalDictionaryTerm term1 = dictionary.addTermByUser(new TechnicalDictionaryKey("Term1"));
		term1.setTermSource(dictionary.getSource(10));
		TechnicalDictionaryTerm term2 = dictionary.addTermByUser(new TechnicalDictionaryKey("Term2"));
		term2.setTermSource(dictionary.getSource(10));
		TechnicalDictionaryTerm term3 = dictionary.addTermByUser(new TechnicalDictionaryKey("Term3"));
		term3.setTermSource(dictionary.getSource(10));
		TechnicalDictionaryTerm term4 = dictionary.addTermByUser(new TechnicalDictionaryKey("Term4"));
		term4.setTermSource(dictionary.getSource(10));
		sourceTicket.addTerm(term1.getTermKey());
		sourceTicket.addTerm(term2.getTermKey());
		sourceTicket.addTerm(term3.getTermKey());
		sourceTicket.addTerm(term4.getTermKey());
		sourceTicket.endSession(0, null, false);
		candidateTicket.startSession("Title", "1", "NLP NNP");
		candidateTicket.addTerm(term1.getTermKey());
		candidateTicket.addTerm(term2.getTermKey());
		candidateTicket.addTerm(term3.getTermKey());
		candidateTicket.addTerm(term4.getTermKey());
		candidateTicket.endSession(0, null, false);		
		CandidateScore titleScore = calc.calculateScore(sourceTicket, candidateTicket);
		Assert.assertEquals((float)1.05, titleScore.getScore());		
	}

	@Test
	public void testRematchScore2()
	{
		IConfigurationResourceLoader loader = (IConfigurationResourceLoader)RuntimeInfo.getCurrentInfo().getBean("xmlConfigurationResourceLoader");
		RematchScoreCalculator calc = new RematchScoreCalculator(loader);
		ITicket originalTicket = new InMemoryTicket("cases", "1", "Test");
		ProcessedTicket sourceTicket = new ProcessedTicket(originalTicket, calc);
		ProcessedTicket candidateTicket = new ProcessedTicket(originalTicket, calc);
		sourceTicket.startSession("Title", "1", "NLP NNP");
		TechnicalDictionaryTerm term1 = dictionary.addTermByUser(new TechnicalDictionaryKey("Term1"));
		term1.setTermSource(dictionary.getSource(10));
		TechnicalDictionaryTerm term2 = dictionary.addTermByUser(new TechnicalDictionaryKey("Term2"));
		term2.setTermSource(dictionary.getSource(10));
		TechnicalDictionaryTerm term3 = dictionary.addTermByUser(new TechnicalDictionaryKey("Term3"));
		term3.setTermSource(dictionary.getSource(10));
		TechnicalDictionaryTerm term4 = dictionary.addTermByUser(new TechnicalDictionaryKey("Term4"));
		term4.setTermSource(dictionary.getSource(10));
		sourceTicket.addTerm(term1.getTermKey());
		sourceTicket.addTerm(term2.getTermKey());
		sourceTicket.addTerm(term3.getTermKey());
		sourceTicket.addTerm(term4.getTermKey());
		sourceTicket.endSession(0, null, false);
		candidateTicket.startSession("Title", "1", "NLP NNP");
		candidateTicket.addTerm(term1.getTermKey());
		candidateTicket.addTerm(term2.getTermKey());
		candidateTicket.addTerm(term3.getTermKey());
		candidateTicket.endSession(0, null, false);		
		CandidateScore titleScore = calc.calculateScore(sourceTicket, candidateTicket);
		Assert.assertEquals((float)0.7875, titleScore.getScore());		
	}
}
