package com.softlib.imatch.test.tools;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.xml.DOMConfigurator;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.softlib.imatch.ConsoleAppRuntimeInfo;
import com.softlib.imatch.ITicket;
import com.softlib.imatch.ITicketProvider;
import com.softlib.imatch.InMemoryTicket;
import com.softlib.imatch.RuntimeInfo;
import com.softlib.imatch.StageMngr;
import com.softlib.imatch.TicketProviderFactory;
import com.softlib.imatch.StageMngr.Stage;
import com.softlib.imatch.common.HibernateUtils;
import com.softlib.imatch.dictionary.TechnicalDictionary;
import com.softlib.imatch.ticketprocessing.ExtractTechTokensByNLP;
import com.softlib.imatch.ticketprocessing.ITicketProcessStep;
import com.softlib.tools.dictionaryparsers.TechTokensParser;

public class TechTokensParserTest2 {
	private static TechnicalDictionary dictionary;
	
	@BeforeClass
	public static void init() {
		ConsoleAppRuntimeInfo.init(null);
		DOMConfigurator.configure(RuntimeInfo.getCurrentInfo().getRealPath("/{SolutionConfigFolder}/log4j-console.xml"));
		dictionary = (TechnicalDictionary) RuntimeInfo.getCurrentInfo().getBean("dictionary");
	}

	@Before
	public void restart() {
		dictionary.loadDictionary();
	}
	
	private ITicket getTestTicket() {
		ITicket rc = 
			new InMemoryTicket("1",
							   "upgrade explorer from 7.0 to 8.0",
							   "we have upgrade the explorer from version 7.0 to ver 8.0 and it work");
		return rc;
	}
	
	ITicket getRealTicket() {
		ITicket rc;
		try {
		String objectId = "cases";			
		ITicketProvider provider = (ITicketProvider) RuntimeInfo.getCurrentInfo().getBean(TicketProviderFactory.getProviderId(objectId));
		rc = provider.getForDisplay("7010");
		}
		catch (Exception e) {
			return getTestTicket();
		}
		return rc;
	}
	
	@Test
	public void ExtractTechTokensByNLP() {
		try {

			ITicket ticket = getTestTicket();
			
			TechTokensParser parser = new TechTokensParser(0, 0, 0, 0, false, 0);
			ExtractTechTokensByNLP step = new ExtractTechTokensByNLP();
			List<ITicketProcessStep> steps = new ArrayList<ITicketProcessStep>();
			steps.add(step);
			parser.setSteps(steps);

			StageMngr.instance().setStage(Stage.Extract);
			parser.parse(ticket);
					
			System.out.println("==============================");
			System.out.println("       H i s t o r y          ");
			System.out.println("==============================");
			List<String> history = step.getPhrase().getHistory();
			if (history!=null)
				for ( String str: history)
					System.out.println(" Term = "+str);
			System.out.println("============================== ["+history.size()+"]");

		} catch (Exception e) {
			e.printStackTrace();
		}
	};
	

};
