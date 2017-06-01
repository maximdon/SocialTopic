package com.softlib.tools.dictionaryparsers;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.hibernate.Session;

import com.softlib.imatch.ITicket;
import com.softlib.imatch.RuntimeInfo;
import com.softlib.imatch.common.LogUtils;
import com.softlib.imatch.dictionary.ITechnicalDictionary;
import com.softlib.imatch.dictionary.SynonymsRelation;
import com.softlib.imatch.dictionary.TechnicalDictionaryTerm;
import com.softlib.imatch.dictionary.TechnicalTermSource;

public class WikipediaParser
{
	private TechnicalTermSource wikiSource = null; //new TechnicalTermSource("WIKIPEDIA");
	private static Logger log = Logger.getLogger(WikipediaParser.class);
	
	public WikipediaParser()
	{
//		wikiSource.setSourceBoost(5);
	}
	
	public Collection<TechnicalDictionaryTerm> parse(List<ITicket> tickets) throws IOException, SQLException
	{
		InputStreamReader is = new InputStreamReader(new FileInputStream(RuntimeInfo.getCurrentInfo().getRealPath("/WEB-INF/Wikipedia.txt")), "UTF-8");
        BufferedReader bf = new BufferedReader(is);
        StringBuffer  buff = new StringBuffer();
        String str;
        final String _UNKNOWN_SECTION = "_UNKNOWN";
        String currentSection = "";
        Pattern sectionPattern = Pattern.compile("^( )+\\[edit");
        Pattern definitionPattern = Pattern.compile("^( )+(\\*)( )(.)*($)");
        List<String> definitions = new ArrayList<String>();
        String currentDefinition = null;
        
        SynonymsRelation relation = new SynonymsRelation();
        while((str = bf.readLine()) != null) {
        	//Skip empty lines
        	if(str.trim().length() == 0)
    			continue;
        	if(currentSection.equals(_UNKNOWN_SECTION)) {
        		//Reading section title (contains 3 rows [edit, link row and section name (letter))
       			currentSection = str.trim();
       			continue;
        	}
        	Matcher sectionMatcher = sectionPattern.matcher(str);
        	if(sectionMatcher.matches()) {
        		if(currentSection.equals("Z"))
        			//All relevant section parsed. Stop
        			break;
        		currentSection = _UNKNOWN_SECTION;
        		//Skip link row
        		bf.readLine();
        		continue;
        	}
        	Matcher definitionMatcher = definitionPattern.matcher(str);
        	if(definitionMatcher.matches()) {
        		//New definition found
        		if(currentDefinition != null)
        			definitions.add(currentDefinition);
        		currentDefinition = str.replaceFirst("( )</(.)+>( )?[��-]", "�").replaceFirst("( )*\\*( )", "");
        	}
        	else {
        		//Continue current definition
        		currentDefinition += str.trim();
        	}
        	buff.append(str + "\n");		             
        }
        //TODO use Set instead of list
		List<TechnicalDictionaryTerm> parsedTerms = new ArrayList<TechnicalDictionaryTerm>();
        for(String definition : definitions) {
        	LogUtils.debug(log, "Parsing Wikipedia abbreviation " + definition);
        	String[] defParts = definition.split("�");
        	String abbreviation = defParts[0];
        	String abbreviationDef = defParts[1].trim();
        	TechnicalDictionaryTerm abbreviationTerm = null; //new TechnicalDictionaryTerm(abbreviation, abbreviationDef, wikiSource);
	    	TechnicalDictionaryTerm abbreviationDefTerm = null; //new TechnicalDictionaryTerm(abbreviationDef, abbreviation, wikiSource);
        	if(parsedTerms.contains(abbreviationTerm) || parsedTerms.contains(abbreviationDefTerm))
        		continue;
	    	relation.relate(abbreviationTerm, abbreviationDefTerm, "Wikipedia");
    		LogUtils.debug(log, "Technical term %s was discovered", abbreviationTerm);
        	parsedTerms.add(abbreviationTerm);
        	parsedTerms.add(abbreviationDefTerm);
        }
        return parsedTerms;
	}

	public void buildRelations(ITechnicalDictionary dictionary, Session session) throws IOException, SQLException {
		//Do nothing
	}

	public Collection<TechnicalTermSource> getSources() {
		ArrayList<TechnicalTermSource> sources = new ArrayList<TechnicalTermSource>();
		sources.add(wikiSource);
		return sources;
	}

	public void init() {
	}

}
