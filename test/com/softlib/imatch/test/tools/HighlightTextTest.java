package com.softlib.imatch.test.tools;

import java.util.List;

import junit.framework.Assert;

import org.apache.log4j.xml.DOMConfigurator;
import org.junit.BeforeClass;
import org.junit.Test;

import com.softlib.imatch.ConsoleAppRuntimeInfo;
import com.softlib.imatch.RuntimeInfo;
import com.softlib.imatch.common.HighlightText;
import com.softlib.imatch.common.HighlightText.Type;
import com.softlib.imatch.dictionary.TechnicalDictionary;
import com.softlib.imatch.dictionary.TechnicalDictionaryKey;
import com.softlib.imatch.dictionary.TechnicalDictionaryTerm;
import com.softlib.imatch.dictionary.TechnicalTermSource;

public class HighlightTextTest {

	private static TechnicalDictionary dictionary;
	private static TechnicalTermSource simpleSource;
	private static TechnicalTermSource complexSource;
	
	@BeforeClass
	public static void init() {
		ConsoleAppRuntimeInfo.init(null);
		DOMConfigurator.configure(RuntimeInfo.getCurrentInfo().getRealPath("/{SolutionConfigFolder}/log4j-console.xml"));			

		dictionary = new TechnicalDictionary();		
		simpleSource = dictionary.addSource("simple");
		complexSource = dictionary.addSource("complex");
	}
		
	@Test
	public void testSimpleTerm() 
	{		
		dictionary.addTerm(new TechnicalDictionaryKey("bb")		,simpleSource);
		check("bb", "{bb}");
	}	

	@Test
	public void testSimpleTermWithLeadingSpace() 
	{		
		dictionary.addTerm(new TechnicalDictionaryKey("bb")		,simpleSource);
		check(" bb", " {bb}");
	}	
	
	@Test
	public void testSimpleTermWithTrailingSpace() 
	{		
		dictionary.addTerm(new TechnicalDictionaryKey("bb")		,simpleSource);
		check("bb ", "{bb} ");
	}
	
	@Test
	public void testSimpleTermWithBothSpaces() 
	{		
		dictionary.addTerm(new TechnicalDictionaryKey("bb")		,simpleSource);
		check(" bb ", " {bb} ");
	}	

	@Test
	public void testSimpleTermWithSpecialSign1() 
	{		
		dictionary.addTerm(new TechnicalDictionaryKey("bb")		,simpleSource);
		check(" >bb ", " >{bb} ");
	}	
	
	@Test
	public void testSimpleTermWithSpecialSign2() 
	{		
		dictionary.addTerm(new TechnicalDictionaryKey("bb")		,simpleSource);
		check("bb<>", "{bb}<>");
	}	

	@Test
	public void testSimpleTermWithSpecialSign3() 
	{		
		dictionary.addTerm(new TechnicalDictionaryKey("bb")		,simpleSource);
		check(" ?bb:", " ?{bb}:");
	}	

	@Test
	public void testSimpleTermWithSpecialSign4() 
	{		
		dictionary.addTerm(new TechnicalDictionaryKey("bb")		,simpleSource);
		check(" <bb> ", " <{bb}> ");
	}	

	@Test
	public void testVersionTerm() 
	{		
		dictionary.addTerm(new TechnicalDictionaryKey("ie 8")		,simpleSource);
		check("ie 8", "{ie 8}");
	}	

	@Test
	public void testVersionTermWithV() 
	{		
		dictionary.addTerm(new TechnicalDictionaryKey("ie 8")		,simpleSource);
		check("ie v 8", "{ie} v {8}");
	}	
	
	@Test
	public void testVersionTermWithVZero() 
	{		
		dictionary.addTerm(new TechnicalDictionaryKey("ie 8")		,simpleSource);
		check("ie v 8.0", "{ie} v {8.0}");		
	}	

	@Test
	public void testVersionTermWithVer() 
	{		
		dictionary.addTerm(new TechnicalDictionaryKey("ie 8")		,simpleSource);
		check("ie ver 8", "{ie} ver {8}");
	}	

	@Test
	public void testVersionTermWithVerZero() 
	{		
		dictionary.addTerm(new TechnicalDictionaryKey("ie 8")		,simpleSource);
		check("ie ver 8.0.0", "{ie} ver {8.0.0}");		
	}	

	@Test
	public void testPortNumber() 
	{		
		dictionary.addTerm(new TechnicalDictionaryKey("port:8080")		,simpleSource);
		check(" port:8080 ", " {port:8080} ");
	}	

	@Test
	public void testTwoWordsTerm() 
	{		
		dictionary.addTerm(new TechnicalDictionaryKey("aa1 aa2")		,simpleSource);
		check("aa1 aa2", "{aa1 aa2}");
	}	

	@Test
	public void testTwoWordsTermWithDot() 
	{		
		dictionary.addTerm(new TechnicalDictionaryKey("aa1 aa2")		,simpleSource);
		check("aa1.aa2", "aa1.aa2");
	}	

	@Test
	public void testTwoWordsTermWithComma() 
	{		
		dictionary.addTerm(new TechnicalDictionaryKey("aa1 aa2")		,simpleSource);
		check("aa1 , aa2", "{aa1} , {aa2}");
	}

	@Test
	public void testTwoWordsTermWithCommaNoSpace() 
	{		
		dictionary.addTerm(new TechnicalDictionaryKey("aa1 aa2")		,simpleSource);
		check("aa1,aa2", "{aa1,aa2}");
	}
	
	@Test
	public void testTwoWordsTermWithSpecialCharacters1() 
	{		
		dictionary.addTerm(new TechnicalDictionaryKey("aa1 aa2")		,simpleSource);
		check("!aa1 aa2", "!{aa1 aa2}");
	}

	@Test
	public void testTwoWordsTermWithSpecialCharacters2() 
	{		
		dictionary.addTerm(new TechnicalDictionaryKey("aa1 aa2")		,simpleSource);
		check("aa1 aa2!", "{aa1 aa2}!");
	}
	
	@Test
	public void testTwoWordsTermWithSpecialCharactersNoSpace() 
	{		
		dictionary.addTerm(new TechnicalDictionaryKey("aa1 aa2")		,simpleSource);
		check("aa1!aa2", "{aa1!aa2}");
	}

	@Test
	public void testTwoWordsTermInBrackets() 
	{		
		dictionary.addTerm(new TechnicalDictionaryKey("aa1 aa2")		,simpleSource);
		check("[aa1 aa2]", "[{aa1 aa2}]");
	}

	@Test
	public void testThreeWords() 
	{		
		dictionary.addTerm(new TechnicalDictionaryKey("cc1 cc2 cc3")		,simpleSource);
		check("cc1 cc2 cc3", "{cc1 cc2 cc3}");
	}

	@Test
	public void testThreeWordsWithSpecialCharacters1() 
	{		
		dictionary.addTerm(new TechnicalDictionaryKey("cc1 cc2 cc3")		,simpleSource);
		check(" &cc1 $ cc2 % cc3; ", " &{cc1} $ {cc2} % {cc3}; ");
	}

	@Test
	public void testThreeWordsWithSpecialCharacters2() 
	{		
		dictionary.addTerm(new TechnicalDictionaryKey("cc1 cc2 cc3")		,simpleSource);
		check(" &cc1$cc2%cc3; ", " &{cc1$cc2%cc3}; ");
	}
	
	@Test
	public void testTwoTerms() 
	{		
		dictionary.addTerm(new TechnicalDictionaryKey("aa1 aa2")		,simpleSource);
		dictionary.addTerm(new TechnicalDictionaryKey("cc1 cc2 cc3")		,simpleSource);
		check("aa1 aa2 cc1 cc2 cc3", "{aa1 aa2} {cc1 cc2 cc3}");
	}
	
	private void check(String text,String expectedResult) {
		List<TechnicalDictionaryTerm> terms = (List<TechnicalDictionaryTerm>)dictionary.getAllTerms(true);
		HighlightText highlightText = new HighlightText(text,"{","}");
		highlightText.highlight(terms,Type.Active);
		String highlighted = highlightText.getHighlightText();
		String resultStr = (highlighted.equals(expectedResult) ? "Ok" : "Error");
		System.out.println("check :["+text+"] --> ["+highlighted+"] "+resultStr);
		Assert.assertEquals(expectedResult, highlighted);
	}
	
};
