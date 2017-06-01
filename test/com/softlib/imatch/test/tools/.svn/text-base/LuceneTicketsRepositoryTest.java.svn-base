package com.softlib.imatch.test.tools;


import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
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
import com.softlib.imatch.common.configuration.XMLConfigurationResourceLoader;
import com.softlib.imatch.dictionary.TechnicalDictionary;
import com.softlib.imatch.dictionary.TechnicalDictionaryKey;
import com.softlib.imatch.dictionary.TechnicalDictionaryTerm;
import com.softlib.imatch.dictionary.TechnicalTermSource;
import com.softlib.imatch.matcher.MatchCandidate;
import com.softlib.imatch.matcher.lucene.LuceneTicketsRepository;
import com.softlib.imatch.ticketprocessing.ProcessedTicket;

public class LuceneTicketsRepositoryTest {
	
	
	@BeforeClass
	public static void init()
	{
	}

	@Before
	public void restart()
	{
	}
		
	private List<String> getTerms() {
		List<String> rc = new ArrayList<String>();
		rc.add("attunity");
		rc.add("advise");
		rc.add("install");
		rc.add("confirm");
		return rc;
	}

	private ProcessedTicket getProcTicket7010(ITicket ticket) {
		final String id = "7010";
		List<String> terms = new ArrayList<String>();
		terms.add(getTerms().get(0));
		terms.add(getTerms().get(1));
		ProcessedTicket rc = 
			new ProcessedTicket(ticket, ProcessedTicket.getDefaultCalculator()) {
			public  String getId() {
				return id;
			}
		};
		rc.startSession("TestField",id,"TestStep");
		for (String str : terms) 
			rc.addTerm(new TechnicalDictionaryKey(str));
		rc.endSession(0,null,false);
		return rc;
	}
	
	private ProcessedTicket getProcTicket7011(ITicket ticket) {
		final String id = "7011";
		List<String> terms = new ArrayList<String>();
		terms.add(getTerms().get(2));
		terms.add(getTerms().get(3));
		ProcessedTicket rc = 
			new ProcessedTicket(ticket, ProcessedTicket.getDefaultCalculator()) {
				public  String getId() {
					return id;
				}
			};
			rc.startSession("TestField",id,"TestStep");
			for (String str : terms) 
				rc.addTerm(new TechnicalDictionaryKey(str));
			rc.endSession(0,null,false);
			return rc;
	}

	private void addTerm(TechnicalDictionary dictionary,String condStr) {
		TechnicalDictionaryKey termKey =
			new TechnicalDictionaryKey(condStr);
		TechnicalDictionaryTerm newTerm = 
			dictionary.addTerm(termKey);
		TechnicalTermSource source = 
			dictionary.addSource("Split");
		newTerm.setTermSource(source);

	}
	
	@Test
	public void testTermIndex() {
		try {
			ConsoleAppRuntimeInfo.init(null);
			DOMConfigurator.configure(RuntimeInfo.getCurrentInfo().getRealPath("/{SolutionConfigFolder}/log4j-console.xml"));
			TechnicalDictionary dictionary = (TechnicalDictionary) RuntimeInfo.getCurrentInfo().getBean("dictionary");
			dictionary.loadDictionary();
			String objectId = "cases";
			ITicketProvider provider = (ITicketProvider) RuntimeInfo.getCurrentInfo().getBean(TicketProviderFactory.getProviderId(objectId));
	
			ITicket ticket7010 = provider.getForDisplay("7010");
			ProcessedTicket processedTicket = getProcTicket7010(ticket7010);
			System.out.println("ticket id="+processedTicket.getId());
			
			ITicket ticket7011 = provider.getForDisplay("7010");
			ProcessedTicket matchTicket = getProcTicket7011(ticket7011);
			matchTicket.getOriginalTicket().getId();
			System.out.println("matchTicket id="+matchTicket.getId());

			
			XMLConfigurationResourceLoader loader = new XMLConfigurationResourceLoader();
			LuceneTicketsRepository repository = new LuceneTicketsRepository(loader);
			
			repository.startBatchUpdate();
			repository.add(processedTicket);
			repository.endBatchUpdate();
			
			List<String> terms = getTerms();
			for (String str : terms) {
				if (dictionary.addTerm(new TechnicalDictionaryKey(str))==null)
					System.out.println(" Can't Add Term : "+str);
			}
			
			printCandidates(repository.find(matchTicket),"Match Ticket");
			
			addTerm(dictionary,terms.get(2));
			addTerm(dictionary,terms.get(3));
			
			printCandidates(repository.find(matchTicket),"Match Ticket");
		
			System.out.println(" ================");
			
			printCandidates(repository.find(terms.get(0)),"Word "+terms.get(0));
			
			String condStr = "patches";
			TechnicalDictionaryKey termKey =
				new TechnicalDictionaryKey(condStr);
			TechnicalDictionaryTerm newTerm = 
				dictionary.addTerm(termKey);
			TechnicalTermSource source = 
				dictionary.addSource("Split");
			newTerm.setTermSource(source);

			printCandidates(repository.find(newTerm.getTermText()),"Word Exist "+condStr);
		
			repository.remove(newTerm);
		
			printCandidates(repository.find(newTerm.getTermText()),"Word Removed "+condStr);
			
			repository.add(newTerm);
			
			printCandidates(repository.find(newTerm.getTermText()),"Word Added "+condStr);

			System.out.println("processedTicket terms after [Add] :");
			Iterator<TechnicalDictionaryTerm> iter =
				processedTicket.getDictionary().termsIterator();
			while (iter.hasNext()) {
				String termStr = iter.next().getTermText();
				System.out.println("   term="+termStr);
			}
			
			System.out.println("============================");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void printCandidates(Collection<MatchCandidate> candidates,String title) {
		System.out.println("--------------------------------------------");
		System.out.println(title+" Candidates :");
		System.out.println("==========================");
		if (candidates==null || candidates.size()==0) 
			System.out.println("No Candidates");
		else 
			for ( MatchCandidate candidate : candidates) 
				System.out.println("Candidate = "+candidate);
		System.out.println("--------------------------------------------");
	}
	
	@Test
	public void testWordIndex() {
		try {
			if (1==1)
				return;
			ConsoleAppRuntimeInfo.init(null);
			DOMConfigurator.configure(RuntimeInfo.getCurrentInfo().getRealPath("/{SolutionConfigFolder}/log4j-console.xml"));
			TechnicalDictionary dictionary = (TechnicalDictionary) RuntimeInfo.getCurrentInfo().getBean("dictionary");
			dictionary.loadDictionary();
			String objectId = "cases";
			ITicketProvider provider = (ITicketProvider) RuntimeInfo.getCurrentInfo().getBean(TicketProviderFactory.getProviderId(objectId));
			ITicket ticket = provider.getForDisplay("7010");

			XMLConfigurationResourceLoader loader = new XMLConfigurationResourceLoader();
			LuceneTicketsRepository repository = new LuceneTicketsRepository(loader);
			ProcessedTicket processedTicket = new ProcessedTicket(ticket, ProcessedTicket.getDefaultCalculator());

			repository.startBatchUpdate();
			repository.add(processedTicket);
			repository.endBatchUpdate();
			
			Collection<MatchCandidate> candidates = repository.find("confirm");
			if (candidates==null) {
				System.out.println("No Candidates");
			}
			else {
				for ( MatchCandidate candidate : candidates) {
					System.out.println("Candidate = "+candidate);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}


};
