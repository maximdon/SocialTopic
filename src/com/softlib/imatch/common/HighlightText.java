package com.softlib.imatch.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.NavigableSet;
import java.util.Set;
import java.util.TreeSet;

import com.softlib.imatch.dictionary.TechnicalDictionaryTerm;
import com.softlib.imatch.dictionary.TechnicalTermSource;
import com.softlib.imatch.proximity.ProximityData;

public class HighlightText {

	static final public String TITLE = "#TITLE#";
	static final public String SUFFIX = "#SUFFIX#";
	static final public String CLASS = "#CLASS#";
	static final public String TERM_ID = "#TERM_ID#";
	static final public String TERM_TEXT = "#TERM_TEXT#";

	static final private String AFTER_TEMPLATE = "</span>";
	
	static final private String BEFORE_TEMPLATE = 
		"<span"+
			getTag("title",TITLE+SUFFIX)+
			getTag("term_id",TERM_ID)+
			getTag("term_text",TERM_TEXT)+
			getTag("softlibTerm","highlight") +
			getTag("local","false") +
			getTag("class","highlight"+CLASS+"Term") +
		">";
		
	final private String beforeTemplate;
	final private String afterTemplate;

	final private String text;
	final private CharTokenizer tokenizer;
	final private StringBuilder sb;
	
	public enum Type {
		Active,
		One,
		Zero
	}
	
	public HighlightText(String text) {
		this(text,BEFORE_TEMPLATE,AFTER_TEMPLATE);
	}

	public HighlightText(String text,String beforeTemplate,String afterTemplate) {
		this.beforeTemplate = beforeTemplate;
		this.afterTemplate = afterTemplate;
		this.text = text==null?"":text;
		sb = new StringBuilder(this.text);
		tokenizer = new CharTokenizer();
		tokenizer.process(this.text);
	}

	static private String getTag(String name,String value) {
		return " "+name+"="+"\""+ value +"\"";
	}
	
	private String getClass(Pair<TechnicalDictionaryTerm,Type> data) {
		Type type = data.getRight();
		if (type==Type.One || type==Type.Zero)
			return type.name();
			
		TechnicalDictionaryTerm term = data.getLeft();
		String srcName = term.getTermSource().getsourceName();
		
		if (srcName.equals("Density")) 
			return "Density";
		else if (srcName.startsWith("Prox "))
			return "Proximity";
		else if (srcName.startsWith("Patterns") && !srcName.endsWith("Patterns") )
			return "Pattern";
		else if (srcName.startsWith("PP ")) 
			return "PP";

		return "Simple";
	}
	
	private Pair<String,String> getPair(Pair<TechnicalDictionaryTerm,Type> data) {
		TechnicalDictionaryTerm term = data.getLeft();
 		
		TechnicalTermSource source = term.getTermSource();
		String sourceName = (source==null?"":source.getsourceName());
		
		String classs = getClass(data);
		
		Type type = data.getRight();
		String suffix = "";
		if (type==Type.One)
			suffix = "(Frequency=1)";
		else if (type==Type.Zero)
			suffix = "(Frequency=0)";

		String before = beforeTemplate;
		before = before.replace(TITLE,sourceName);
		before = before.replace(SUFFIX,suffix);
		before = before.replace(CLASS,classs);
		
		String termText = term.getTermText();
		if (SourceMngr.isSource(sourceName, SourceMngr.Type.Compound))
			termText = termText.replaceAll(ProximityData.SEPERATOR," ... ");	
		before = before.replace(TERM_TEXT,termText);

		if (sourceName.equals(SourceMngr.UserDefinedLocal))// || sourceName.equals(SourceMngr.UserDefined))
			before = before.replace(TERM_ID,String.valueOf(termText.hashCode()));
		else if (type!=Type.Active)
			before = before.replace(TERM_ID,"0");
		else
			before = before.replace(TERM_ID,String.valueOf(term.getTermId()));
			
			
		String after = afterTemplate;
		return new Pair<String,String>(before,after);
	}
		
	private void insert(StringBuilder sb,String text,int pos) {
		if (sb.length()<=pos)
			sb.append(text);
		else
			sb.insert(pos,text);
	}

	private void replace(String before,String after,TextPosition textPosition) {
		insert(sb,after,textPosition.getEnd());
		insert(sb,before,textPosition.getStart());
	}

	private void highlight(Pair<TechnicalDictionaryTerm,Type> data,
						   TextPosition textPosition) {
		Pair<String,String> pair = getPair(data);
		String before = pair.getLeft();
		String after =  pair.getRight();
		replace(before,after,textPosition);
	}
	
	public void highlight(List<TechnicalDictionaryTerm> terms,Type type) {
		List<TechnicalDictionaryTerm> first = new ArrayList<TechnicalDictionaryTerm>();
		List<TechnicalDictionaryTerm> other = new ArrayList<TechnicalDictionaryTerm>();
		for (TechnicalDictionaryTerm term : terms) {
			if (!first.contains(term))
				first.add(term);
			else
				other.add(term);
		}
		doHighlight(first,type);
		doHighlight(other,type);
	}

	private void doHighlight(List<TechnicalDictionaryTerm> terms,Type type) {
		if (terms==null || terms.size()==0)
			return;
		
		List<TechnicalDictionaryTerm> complexTerms = new ArrayList<TechnicalDictionaryTerm>();
		List<TechnicalDictionaryTerm> simpleTerms = new ArrayList<TechnicalDictionaryTerm>();
			
		for (TechnicalDictionaryTerm term : terms ) {
			if (SourceMngr.isSource(term.getTermSource(),SourceMngr.Type.Complex))
				complexTerms.add(term);
			else
				simpleTerms.add(term);
		}

		Comparator<TechnicalDictionaryTerm> comparator = new Comparator<TechnicalDictionaryTerm>() {
			public int compare(TechnicalDictionaryTerm term1, TechnicalDictionaryTerm term2) {
				String[] split1 =
					((TechnicalDictionaryTerm)term1).getTermText().split(" ");
				String[] split2 =
					((TechnicalDictionaryTerm)term2).getTermText().split(" ");
				return split2.length - split1.length;
			}
		};

		Collections.sort(simpleTerms,comparator);
		Collections.sort(complexTerms,comparator);
		
		for (TechnicalDictionaryTerm term : complexTerms )
			tokenizer.findTerm(term,false,type);
		
		for (TechnicalDictionaryTerm term : simpleTerms )
			tokenizer.findTerm(term,true,type);
	}
	
	public String getHighlightText() {
		Map<TextPosition,Pair<TechnicalDictionaryTerm,Type>> results = tokenizer.getTermsByPosition();
		
		TreeSet<TextPosition> sortedPositions = new TreeSet<TextPosition>(results.keySet());
		NavigableSet<TextPosition> reverceOrder = sortedPositions.descendingSet();

		for (TextPosition textPosition : reverceOrder) {
			Pair<TechnicalDictionaryTerm,Type> data = results.get(textPosition);
			highlight(data,textPosition);
		}
		return sb.toString();
	}
	
	public Map<TechnicalDictionaryTerm, Set<TextPosition>> getPositionsByTerm() {
		return tokenizer.getPositionsByTerm();
	}


};
