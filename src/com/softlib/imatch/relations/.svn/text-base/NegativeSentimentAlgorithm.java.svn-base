package com.softlib.imatch.relations;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import com.softlib.imatch.dictionary.ITechnicalDictionary;
import com.softlib.imatch.dictionary.PorterStemmer;
import com.softlib.imatch.dictionary.TechnicalDictionaryTerm;
import com.softlib.imatch.ticketprocessing.NegativeSentiment;

public class NegativeSentimentAlgorithm implements IRelationAlgorithm {

	private static Logger log = Logger.getLogger(NegativeSentimentAlgorithm.class);

	private final static int NegativeSentimentRelationAlg = 32;

	private List<NegativeSentimentRelationObject> negativeSentimentRelations = new ArrayList<NegativeSentimentRelationObject>();
	
	public NegativeSentimentAlgorithm() {
		super();		
	}
	
	public boolean relate(RelationAlgorithmContext context, TechnicalDictionaryTerm term, String[] termParts) 
	{
		int supportedRelationsAlgs = term.getTermSource().getSupportedRelationsAlgs();
		if((supportedRelationsAlgs & NegativeSentimentRelationAlg) == NegativeSentimentRelationAlg)
		{
			NegativeSentiment sentiment = NegativeSentiment.getFromTerm(term);
			if(sentiment != null) {
				String sentimentSubjectStr = sentiment.getSubject();
				NegativeSentimentSubject sentimentSubject = new NegativeSentimentSubject(sentimentSubjectStr, context.dictionary);
				NegativeSentimentRelationObject sentimentRelationObject = new NegativeSentimentRelationObject(sentimentSubject, null);
				int idx = negativeSentimentRelations.indexOf(sentimentRelationObject);
				if(idx > -1)
					sentimentRelationObject = negativeSentimentRelations.get(idx);
				else
					negativeSentimentRelations.add(sentimentRelationObject);
				sentimentRelationObject.terms.add(term);
			}
		}
		return false;
	}
	
	public void finish(RelationAlgorithmContext context) 
	{
		context.relation.enableTraceForRule("Negative Sentiment");
		for(NegativeSentimentRelationObject sentimentRelationObject : negativeSentimentRelations) {
			List<TechnicalDictionaryTerm> negativeSentimentsForSubject = sentimentRelationObject.terms;
			if(negativeSentimentsForSubject.size() == 0)
				return;			
			TechnicalDictionaryTerm firstTerm = negativeSentimentsForSubject.get(0);
			for(TechnicalDictionaryTerm otherTerm : negativeSentimentsForSubject)
				context.relation.relate(firstTerm, otherTerm, "Negative Sentiment");
		}
	}
	
	private class NegativeSentimentRelationObject
	{
		NegativeSentimentSubject subject;
		List<TechnicalDictionaryTerm> terms;

		public NegativeSentimentRelationObject(NegativeSentimentSubject subject, List<TechnicalDictionaryTerm> terms) 
		{
			this.subject = subject;
			this.terms = terms;
			if(this.terms == null)
				this.terms = new ArrayList<TechnicalDictionaryTerm>();
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			NegativeSentimentRelationObject other = (NegativeSentimentRelationObject) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (subject == null) {
				if (other.subject != null)
					return false;
			} else if (!subject.equals(other.subject))
				return false;
			return true;
		}

		private NegativeSentimentAlgorithm getOuterType() {
			return NegativeSentimentAlgorithm.this;
		}
	}
	
	private class NegativeSentimentSubject
	{
		private String subjectStr;
		private ITechnicalDictionary dictionary;
		
		public NegativeSentimentSubject(String sentimentSubjectStr, ITechnicalDictionary dictionary) 
		{
			subjectStr = PorterStemmer.stem(sentimentSubjectStr);
			this.dictionary = dictionary;
		}
		
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			NegativeSentimentSubject other = (NegativeSentimentSubject) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (subjectStr == null) {
				if (other.subjectStr != null)
					return false;
			} 
			else if (subjectStr.equals(other.subjectStr))
				return true;
			String[] words = subjectStr.split(" ");
			if(words.length == 1)
				return false;
			TechnicalDictionaryTerm term1 = dictionary.get(words[0]);
			TechnicalDictionaryTerm term2 = dictionary.get(words[1]);
			Set<String> term1Synonyms = new HashSet<String>();
			term1Synonyms.add(words[0]);
			Set<String> term2Synonyms = new HashSet<String>();
			if(term1 != null)
				for(TechnicalDictionaryTerm relatedTerm : term1.getRelations())
					term1Synonyms.add(relatedTerm.getTermStemmedText());
			term2Synonyms.add(words[1]);
			if(term2 != null)
				for(TechnicalDictionaryTerm relatedTerm : term2.getRelations())
					term2Synonyms.add(relatedTerm.getTermStemmedText());
			for(String term1Synonym : term1Synonyms)
				for(String term2Synonym : term2Synonyms) {
					String synonymSubject = term1Synonym + " " + term2Synonym;
					if(synonymSubject.equals(other.subjectStr))
						return true;
					//Try to swap words
					String synonymSubjectRevert = term2Synonym + " " + term1Synonym;
					if(synonymSubjectRevert.equals(other.subjectStr))
						return true;
				}
			return false;
		}
		private NegativeSentimentAlgorithm getOuterType() {
			return NegativeSentimentAlgorithm.this;
		}		
	}
}
