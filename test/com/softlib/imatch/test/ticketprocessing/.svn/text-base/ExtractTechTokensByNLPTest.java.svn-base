package com.softlib.imatch.test.ticketprocessing;

import java.util.List;

import org.apache.log4j.xml.DOMConfigurator;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.softlib.imatch.ConsoleAppRuntimeInfo;
import com.softlib.imatch.ITicket;
import com.softlib.imatch.ITicketProvider;
import com.softlib.imatch.RuntimeInfo;
import com.softlib.imatch.TicketProviderFactory;
import com.softlib.imatch.dictionary.ITechnicalTermsContainer;
import com.softlib.imatch.dictionary.TechnicalDictionary;
import com.softlib.imatch.ticketprocessing.ExtractTechTokensByNLP;
import com.softlib.imatch.ticketprocessing.StepContext;

public class ExtractTechTokensByNLPTest {

	@BeforeClass
	public static void init()
	{
	}

	@Test
	public void test() {
	}

	private ITechnicalTermsContainer getTermsContainer() {
		TechnicalDictionary dictionary = (TechnicalDictionary) RuntimeInfo.getCurrentInfo().getBean("dictionary");
		return dictionary;
	}

	@Before
	public void restart()
	{
		try {
			ConsoleAppRuntimeInfo.init(null);
			DOMConfigurator.configure(RuntimeInfo.getCurrentInfo().getRealPath("/{SolutionConfigFolder}/log4j-console.xml"));
			TechnicalDictionary dictionary = (TechnicalDictionary) RuntimeInfo.getCurrentInfo().getBean("dictionary");
			dictionary.loadDictionary();
			String objectId = "cases";
			ITicketProvider provider = (ITicketProvider) RuntimeInfo.getCurrentInfo().getBean(TicketProviderFactory.getProviderId(objectId));
			ITicket ticket= provider.getForDisplay("7010");
//			IProcessedTicket processedTicket = new ProcessedTicket(ticket, ProcessedTicket.getDefaultCalculator());
			ExtractTechTokensByNLP step = new ExtractTechTokensByNLP();
			step.run("TestField",ticket,getTermsContainer(), new StepContext("1"));
			System.out.println("==============================");
			System.out.println("       H i s t o r y          ");
			System.out.println("==============================");
			List<String> history = step.getPhrase().getHistory();
			if (history!=null)
				for ( String str: history)
					System.out.println(" Term = "+str);
			System.out.println("=============================="+history.size());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

};
