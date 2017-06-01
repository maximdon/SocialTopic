package com.softlib.imatch.test.ticketprocessing;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import junit.framework.Assert;

import org.apache.log4j.xml.DOMConfigurator;
import org.junit.BeforeClass;
import org.junit.Test;

import com.softlib.imatch.ConsoleAppRuntimeInfo;
import com.softlib.imatch.InMemoryTicket;
import com.softlib.imatch.MatcherException;
import com.softlib.imatch.RuntimeInfo;
import com.softlib.imatch.common.HighlightText;
import com.softlib.imatch.common.HighlightText.Type;
import com.softlib.imatch.density.DensityData;
import com.softlib.imatch.dictionary.TechnicalDictionary;
import com.softlib.imatch.dictionary.TechnicalDictionaryKey;
import com.softlib.imatch.dictionary.TechnicalDictionaryTerm;
import com.softlib.imatch.dictionary.TechnicalTermSource;
import com.softlib.imatch.proximity.ProximityData;
import com.softlib.imatch.ticketprocessing.IProcessedTicket;
import com.softlib.imatch.ticketprocessing.ITicketProcessor;
import com.softlib.imatch.ticketprocessing.ProcessedField;
import com.softlib.imatch.ticketprocessing.TicketProcessor;
import com.softlib.imatch.ticketprocessing.IProcessedTicket.MatchMode;

public class ProcessedTicketTermsTest {

	static private TechnicalDictionary dictionary;
	
	@BeforeClass
	public static void init() {
		ConsoleAppRuntimeInfo.init(null);
		DOMConfigurator.configure(RuntimeInfo.getCurrentInfo().getRealPath("/{SolutionConfigFolder}/log4j-console.xml"));
		dictionary = (TechnicalDictionary) RuntimeInfo.getCurrentInfo().getBean("dictionary");
		dictionary.loadDictionary();
		
		TechnicalTermSource source =  dictionary.addSource("SoftlibTerms");
		source.setSourceBoost(1);
		source.setSourceId(101);

	}

	private void printTerms(List<TechnicalDictionaryTerm> result,List<TechnicalDictionaryTerm> terms,String title) {
		if (terms==null || terms.isEmpty())
			return;
		for ( TechnicalDictionaryTerm term : terms ) {
			System.out.println("       Term"+title+" = " + term.getTermText()+
					"("+term.getTermSource().getsourceName()+")");
			result.add(term);
		}
		
	}
	
	private String highlight(ProcessedField processedField,String text) {
		HighlightText highlightText = new HighlightText(text,"["+HighlightText.TITLE+"]-<",">-");

		highlightText.highlight(processedField.getTerms(),Type.Active);
		highlightText.highlight(processedField.getOneFreqTerms(),Type.One);
		highlightText.highlight(processedField.getZeroFreqTerms(),Type.Zero);

		return highlightText.getHighlightText();
	}
	
	private void checkProcessedTicket(String testName,
							          List<TechnicalDictionaryTerm> expect,
									  IProcessedTicket ticket) {
		List<TechnicalDictionaryTerm> result = new ArrayList<TechnicalDictionaryTerm>();
		System.out.println("-----------------------------------------");
		System.out.println(" printProcessedTicket : ("+testName+")");
		System.out.println("-----------------------------------------");
		for (ProcessedField field : ticket.getData().values()) {
			String fieldName = field.getFieldName();
			String fieldText = ticket.getOriginalTicket().getField(fieldName).toString();
			if (fieldText==null || fieldText.isEmpty())
				continue;
			fieldText = highlight(field, fieldText);
			System.out.println(" ### "+fieldName+":"+fieldText);
			printTerms(result,field.getTerms(),"(>1)");
			printTerms(result,field.getOneFreqTerms(),"(=1)");
			printTerms(result,field.getZeroFreqTerms(),"(=0)");
		}
		
		
		Collections.sort(result, Collections.reverseOrder());
		Collections.sort(expect, Collections.reverseOrder());

		System.out.println(" Result="+result);
		System.out.println(" Expect="+expect);
		
		if (!result.equals(expect))
			Assert.fail("result in not as excpected");
		System.out.println("-----------------------------------------");
	}
	
	private TechnicalDictionaryTerm addTerm(String text,String sourceName) {
		return addTerm(text,sourceName,3);
	}
	
	private TechnicalDictionaryTerm addTerm(String text,String sourceName,int frequancy) {
		TechnicalDictionaryKey key = new TechnicalDictionaryKey(text);
		TechnicalDictionaryTerm term = dictionary.addTerm(key);
		TechnicalTermSource source =  dictionary.addSource(sourceName);
		term.setTermSource(source);
		for (int f=0;f<frequancy;f++)
			term.addFrequency();
		return term;
	}
	
	private InMemoryTicket getTicket(String text) {
		return new InMemoryTicket("","",text);
	}
	
	@Test
	public void test_Patterns() {
		addTerm("upgrade","SoftlibTerms");
		addTerm("upgrade from 22.222 to 11.111","Patterns5",1);
		List<TechnicalDictionaryTerm> expect = new ArrayList<TechnicalDictionaryTerm>();
		expect.add(addTerm("upgrade dbsystem from 22.222 to 11.111","Patterns5"));
		expect.add(addTerm("DBSystem","NLP NNP Tokens"));
		expect.add(addTerm("11.111","Versions"));
		expect.add(addTerm("22.222","Versions"));
		expect.add(addTerm("11.111 0 22.222 0 dbsystem","Density"));
		
		InMemoryTicket ticket = 
			getTicket("after we try to upgrade DBSystem to the 11.111 versoin from 22.222 it failed");
		ITicketProcessor ticketProcessor = (ITicketProcessor) RuntimeInfo.getCurrentInfo().getBean("ticketProcessor");
		try {
			IProcessedTicket processedTicket = ticketProcessor.processTicket(TicketProcessor.StepsMode.Match,
																			 ticket,MatchMode.all,null,false);
			checkProcessedTicket("Patterns",expect,processedTicket);		
		} catch (MatcherException e) {
			Assert.fail(e.getMessage());
		}
	}	

	@Test
	public void test_LowFreq() {
		List<TechnicalDictionaryTerm> expect = new ArrayList<TechnicalDictionaryTerm>();
		addTerm("upgrade","SoftlibTerms");
		expect.add(addTerm("ora-111","errorCodes",0));
		expect.add(addTerm("ora-1435","errorCodes",1));
		expect.add(addTerm("tables","NLP NNP Tokens"));
		InMemoryTicket ticket = 
			getTicket("upgrade table from ora-111 to ora-1435 xxx.");
		ITicketProcessor ticketProcessor = (ITicketProcessor) RuntimeInfo.getCurrentInfo().getBean("ticketProcessor");
		try {
			IProcessedTicket processedTicket = ticketProcessor.processTicket(TicketProcessor.StepsMode.Match,
					 														 ticket,MatchMode.all,null,true);
			checkProcessedTicket("LowFreq",expect,processedTicket);		
		} catch (MatcherException e) {
			Assert.fail(e.getMessage());
		}
	}	

	@Test
	public void test_Proximity() {
		List<TechnicalDictionaryTerm> expect = new ArrayList<TechnicalDictionaryTerm>();
		expect.add(addTerm("aaa1","SoftlibTerms"));
		expect.add(addTerm("bbb1","SoftlibTerms"));
		expect.add(addTerm("ccc1","SoftlibTerms"));
		expect.add(addTerm("aaa1"+ProximityData.SEPERATOR+"bbb1","Prox"));
		expect.add(addTerm("bbb1"+ProximityData.SEPERATOR+"ccc1","Prox"));
		InMemoryTicket ticket = 
			getTicket("xxx aaa1 xxx bbb1 xxx ccc1");
		ITicketProcessor ticketProcessor = (ITicketProcessor) RuntimeInfo.getCurrentInfo().getBean("ticketProcessor");
		try {
			IProcessedTicket processedTicket = ticketProcessor.processTicket(TicketProcessor.StepsMode.Match,
					 														 ticket,MatchMode.all,null,true);
			checkProcessedTicket("Proximity",expect,processedTicket);		
		} catch (MatcherException e) {
			Assert.fail(e.getMessage());
		}
	}	

	@Test
	public void test_Density() {
		addTerm("upgrade","SoftlibTerms");
		List<TechnicalDictionaryTerm> expect = new ArrayList<TechnicalDictionaryTerm>();
		expect.add(addTerm("DBSystem","NLP NNP Tokens"));
		expect.add(addTerm("33.333","Versions"));
		expect.add(addTerm("44.444","Versions"));
		expect.add(addTerm("33.333"+DensityData.SEPERATOR+"44.444"+DensityData.SEPERATOR+"dbsystem","Density"));
		
		InMemoryTicket ticket = 
			getTicket("after we try to upgrade DBSystem to the 33.333 versoin from 44.444 it failed");
		ITicketProcessor ticketProcessor = (ITicketProcessor) RuntimeInfo.getCurrentInfo().getBean("ticketProcessor");
		try {
			IProcessedTicket processedTicket = ticketProcessor.processTicket(TicketProcessor.StepsMode.Match,
					 														 ticket,MatchMode.all,null,false);
			checkProcessedTicket("Density",expect,processedTicket);		
		} catch (MatcherException e) {
			Assert.fail(e.getMessage());
		}
	}	

	@Test
	public void test_PP() {
		List<TechnicalDictionaryTerm> expect = new ArrayList<TechnicalDictionaryTerm>();
		expect.add(addTerm("file","SoftlibTerms"));
		expect.add(addTerm("permissions","SoftlibTerms"));
		expect.add(addTerm("granting","SoftlibTerms"));
		expect.add(addTerm("granting permissions","PP TermTerm Tokens"));
		
		InMemoryTicket ticket = 
			getTicket("the operator recover the file by granting the permissions.");
		ITicketProcessor ticketProcessor = (ITicketProcessor) RuntimeInfo.getCurrentInfo().getBean("ticketProcessor");
		try {
			IProcessedTicket processedTicket = ticketProcessor.processTicket(TicketProcessor.StepsMode.Match,
					 														 ticket,MatchMode.all,null,false);
			checkProcessedTicket("PP",expect,processedTicket);		
		} catch (MatcherException e) {
			Assert.fail(e.getMessage());
		}
	}	

};
