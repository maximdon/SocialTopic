package com.softlib.imatch.matcher.lucene;

import java.util.HashSet;
import java.util.List;
import com.softlib.imatch.common.Pair;


import com.softlib.imatch.dictionary.TechnicalDictionaryTerm;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CachingTokenFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.TermPositionVector;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.highlight.Fragmenter;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.search.highlight.SimpleSpanFragmenter;
//import org.apache.lucene.search.highlight.SpanScorer;
import org.apache.lucene.search.highlight.TokenSources;
import org.apache.lucene.util.Version;
import org.apache.lucene.analysis.en.EnglishAnalyzer;


public class LuceneHighlighter {
	
	private static final StandardAnalyzer analyzer = 
			new StandardAnalyzer(Version.LUCENE_36, new HashSet<String>());
	
	private static final EnglishAnalyzer  engAnalyzer = new EnglishAnalyzer(Version.LUCENE_36);
	
	private static int fragmentSize = 65;
	private static int fragmentsCount = 2;
	
	protected static final Logger log = Logger.getLogger(LuceneHighlighter.class);
	
	public LuceneHighlighter()
	{
	
	}
	
	public static int getFragmentSize() {
		return fragmentSize;
	}

	public static void setFragmentSize(int fragmentSize) {
		LuceneHighlighter.fragmentSize = fragmentSize;
	}

	public static int getFragmentsCount() {
		return fragmentsCount;
	}

	public static void setFragmentsCount(int fragmentsCount) {
		LuceneHighlighter.fragmentsCount = fragmentsCount;
	}

	public static String GetLuceneTermRepresentation(TechnicalDictionaryTerm term)
	{
		String text = term.getTermStemmedText();
		if (text.contains("..."))
		{
			text = text.replace("...", " ");
			text = "\"" + text + "\"~10";
		}
		else if (text.contains(" "))
			text = "\"" + text + "\"";
		return text;
	}
	
	public static String GetSnippet( List< Pair<TechnicalDictionaryTerm,Float> > listOfTerms,String text) throws IOException
	{
		String queryString = "";
		for (Pair<TechnicalDictionaryTerm, Float> pair : listOfTerms) 
		{
			String termText = GetLuceneTermRepresentation(pair.getLeft());
			termText = termText + "^" + String.valueOf(pair.getRight());
			queryString = queryString + " " + termText;
		}
		Query query = null;
	    QueryParser qp = new QueryParser(Version.LUCENE_36, "text", analyzer);
	    try
	    {
	    	query = qp.parse(queryString);
	    }
	    catch (Exception e)
	    {
	    	
	    }

		TokenStream stream = TokenSources.getTokenStream("text", text, engAnalyzer);
		QueryScorer scorer = new QueryScorer(query, "text");
		//,
          //       new CachingTokenFilter(stream));
		 Fragmenter fragmenter = new SimpleSpanFragmenter(scorer, fragmentSize);
		
		 SimpleHTMLFormatter formatter = new SimpleHTMLFormatter();
		 Highlighter highlighter = new Highlighter(formatter,scorer);
		 highlighter.setTextFragmenter(fragmenter);
		 highlighter.setMaxDocCharsToAnalyze(Integer.MAX_VALUE);
		       
		 try
		 {
			 String res = highlighter.getBestFragments(stream, text, fragmentsCount,"...");
			 if (res.length() == 0)
			 {
				 // get to next whitespace
				 int pos = fragmentsCount*fragmentSize;
				 while (pos > 0 && !Character.isWhitespace(text.charAt(pos--)));
				 if (pos > 0)
					 return text.substring(0,pos);
				 else
					 return text.substring(0,fragmentsCount*fragmentSize);
			 }
			 else
			 {
				 if (res.length() > fragmentsCount*(fragmentSize+ 20))
				 {
					 // get to next whitespace
					 int pos = fragmentsCount*fragmentSize;
					 while (pos > 0 && !Character.isWhitespace(res.charAt(pos--)));
					 if (pos > 0)
						 return res.substring(0,pos);
					 else
						 return res.substring(0,fragmentsCount*fragmentSize);
				 }
				 else
					 return  res;
			 }
		 }
		 catch (Exception e)
		 {
			 return text;
		 }

	}

}
