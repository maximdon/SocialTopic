package com.softlib.imatch.test.ticketprocessing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

import junit.framework.Assert;

import org.apache.log4j.xml.DOMConfigurator;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.softlib.imatch.ConsoleAppRuntimeInfo;
import com.softlib.imatch.RuntimeInfo;
import com.softlib.imatch.dictionary.TechnicalDictionary;
import com.softlib.imatch.dictionary.TechnicalDictionaryTerm;
import com.softlib.imatch.ticketprocessing.ExtractTechTokensByNLP;
import com.softlib.imatch.ticketprocessing.NlpBaseTicketProcessStep;
import com.softlib.imatch.ticketprocessing.TechTokenPhrase;


public class TechTokensPhraseTest {

	@BeforeClass
	public static void init()
	{
	}
	
	@Test
	public void test1() {
		TechnicalDictionary dictionary = new TechnicalDictionary();
		
		NlpBaseTicketProcessStep nlpBaseTicketProcessStep = new ExtractTechTokensByNLP();
		TechTokenPhrase p = 
			new TechTokenPhrase("Source Original",
								"Source Split",
							    "Source Wordnet Split",
							    "Source Single Split",
							    dictionary, 
							    nlpBaseTicketProcessStep);
		p.init();
		p.insert("sql", "");
		p.insert("server", "");
		p.insert("collector", "");
		p.insert("agent", "");
		p.insert("kuky", "");
		print(p);
		p.getSplit();
		
		Collection<TechnicalDictionaryTerm> terms = dictionary.termsCollection();
		print(terms);
		Assert.assertEquals(countWordInPhrase(terms),24);
		Assert.assertEquals(checkExistence(terms),true);
		Assert.assertEquals(checkRelations(terms),true);
	}
	
	@Test
	public void test2() {
		TechnicalDictionary dictionary = new TechnicalDictionary();
		
		NlpBaseTicketProcessStep nlpBaseTicketProcessStep = new ExtractTechTokensByNLP();
		TechTokenPhrase p = 
			new TechTokenPhrase("Source Original",
								"Source Split",
							    "Source Wordnet Split",
							    "Source Single Split",
							    dictionary, 
							    nlpBaseTicketProcessStep);
		p.init();
		p.insert("sql", "");
		p.insert("server", "");
		p.insert("collector", "");
		p.insert("agent", "");
		p.insert("database", "");
		print(p);
		p.getSplit();
		
		Collection<TechnicalDictionaryTerm> terms = dictionary.termsCollection();
		print(terms);
		Assert.assertEquals(countWordInPhrase(terms),28);
		Assert.assertEquals(checkExistence(terms),true);
		Assert.assertEquals(checkRelations(terms),true);
	}
	
	@Test
	public void test3() {
		TechnicalDictionary dictionary = new TechnicalDictionary();
		
		NlpBaseTicketProcessStep nlpBaseTicketProcessStep = new ExtractTechTokensByNLP();
		TechTokenPhrase p = 
			new TechTokenPhrase("Source Original",
								"Source Split",
							    "Source Wordnet Split",
							    "Source Single Split",
							    dictionary, 
							    nlpBaseTicketProcessStep);
		p.init();
		p.insert("oracle", "");
		p.insert("server", "");
		p.insert("collector", "");
		p.insert("agent", "");
		p.insert("database", "");
		print(p);
		p.getSplit();
		
		Collection<TechnicalDictionaryTerm> terms = dictionary.termsCollection();
		print(terms);
		Assert.assertEquals(countWordInPhrase(terms),28);
		Assert.assertEquals(checkExistence(terms),true);
		Assert.assertEquals(checkRelations(terms),true);
	}

	private void print(TechTokenPhrase p) {
		System.out.println("---------------------------");
		System.out.println("NLP : "+p.toString());
		System.out.println("---------------------------");
	}
	
	private void print(Collection<TechnicalDictionaryTerm> pers) {
		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		if (pers!=null)
			for ( TechnicalDictionaryTerm term : pers) {
				System.out.println(" Term("+term.getTermSource()+") = "+term.getTermText());
				if (term.getAllRelations()!=null) {
					int idx = 0;
					for (TechnicalDictionaryTerm relation : term.getAllRelations()) {
						System.out.println(" --"+idx+"--> "+relation.getTermText());
						idx++;
					}

				}

			}
		System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
	}
	
	private int countWordInPhrase(Collection<TechnicalDictionaryTerm> pers) {
		int rc =0;
		for ( TechnicalDictionaryTerm term : pers) {
			StringTokenizer st = new StringTokenizer(term.getTermText());
			while (st.hasMoreTokens()) {
				st.nextToken();
				rc++;
			}
		}
		return rc;
	}
	
	private boolean checkExistence(Collection<TechnicalDictionaryTerm> pers) {
		List<TechnicalDictionaryTerm> permClone = 
			new ArrayList<TechnicalDictionaryTerm>(pers);
		for (TechnicalDictionaryTerm per :pers ) {
			Collection<TechnicalDictionaryTerm> relations = 
				new ArrayList<TechnicalDictionaryTerm>(per.getAllRelations());
			if (relations==null)
				continue;
			for (TechnicalDictionaryTerm rel : relations) {
				boolean exist = false;
				for (TechnicalDictionaryTerm perClone :permClone ) {
					if (rel==perClone)
						exist=true;
				}
				if (!exist)
					return false;
			}
		}
		return true;
	}
	
	private boolean checkRelations(Collection<TechnicalDictionaryTerm> pers) {
		for (TechnicalDictionaryTerm per :pers ) {
			Collection<TechnicalDictionaryTerm> relations = 
				new ArrayList<TechnicalDictionaryTerm>(per.getAllRelations());
			if (relations==null)
				continue;
			for (TechnicalDictionaryTerm rel : relations) {
				System.out.println("	rel = "+rel.getTermText());
				
				Collection<TechnicalDictionaryTerm> relRelations = 
					rel.getAllRelations();
				if (relRelations==null)
					return false;
				boolean exist = false;
				for (TechnicalDictionaryTerm relRel : relRelations) {
					System.out.println("	     relRel = "+relRel);
					if (relRel ==per)
						exist=true;
				}
				if (!exist)
					return false;
			}
		}
		return true;
	}


	@Before
	public void restart()
	{
		try {
			ConsoleAppRuntimeInfo.init(null);
			DOMConfigurator.configure(RuntimeInfo.getCurrentInfo().getRealPath("/{SolutionConfigFolder}/log4j-console.xml"));
			TechnicalDictionary dictionary = (TechnicalDictionary) RuntimeInfo.getCurrentInfo().getBean("dictionary");
			dictionary.loadDictionary();
		}
		catch (Exception ex) {
			ex.printStackTrace();
			Assert.assertEquals(false,true);
		}
	}
	
	
};
