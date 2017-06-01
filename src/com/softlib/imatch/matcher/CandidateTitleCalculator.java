package com.softlib.imatch.matcher;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import com.softlib.imatch.ITicket;
import com.softlib.imatch.MatcherException;
import com.softlib.imatch.common.LogUtils;
import com.softlib.imatch.common.StringUtils;
import com.softlib.imatch.dbintegration.DBTicket;
import com.softlib.imatch.ticketprocessing.UrlParserStep;
import com.softlib.imatch.ticketprocessing.IProcessedTicket.MatchMode;

public class CandidateTitleCalculator implements ISingleCandidateProcessor 
{
	private static final Logger log = Logger.getLogger(CandidateTitleCalculator.class);
	private List<ITitleBuilder> builders = new ArrayList<ITitleBuilder>();
	
	public CandidateTitleCalculator()
	{
		builders.add(new LinksTitleBuilder());
		builders.add(new TitleTitleBuilder());
		builders.add(new FileTitleBuilder());
	}
	public void processCandidate(MatchCandidate candidate) throws MatcherException 
	{
		if(candidate == null || candidate.getCandidateData() == null || candidate.getCandidateData().getId() == null)
			return;
		String method = "";
		String title = null;
		for(ITitleBuilder builder : builders) {
			title = builder.buildTitle(candidate.getCandidateData());
			if(!StringUtils.isEmpty(title)) {
				method = builder.getMethod();
				break;
			}
		}
		DBTicket newTicket = new DBTicket(candidate.getOriginObjectId());
		newTicket.setId(candidate.getCandidateData().getId());
		for(String fieldName : candidate.getCandidateData().getFieldsConfig().getAllFields(MatchMode.all))
			newTicket.setField(fieldName, candidate.getCandidateData().getField(fieldName));
		newTicket.setField("Title", title);
		LogUtils.info(log, "For candidate %s the title was set to %s by %s", candidate.getCandidateData().getId(), title, method);
		candidate.setCandidateData(newTicket);
	}
};

interface ITitleBuilder
{
	String buildTitle(ITicket ticket);
	String getMethod();
}

class TitleTitleBuilder implements ITitleBuilder
{
	private int MAX_TITLE_LENGTH = 100;
	public String buildTitle(ITicket ticket) {
		if(ticket.getTitle().length() > MAX_TITLE_LENGTH)
			return null;
		return ticket.getTitle();
	}
	@Override
	public String getMethod() {
		return "Title";
	}
}

class FileTitleBuilder implements ITitleBuilder
{
	public String buildTitle(ITicket ticket) {
		String url = (String)ticket.getField("Url");
		if(StringUtils.isEmpty(url))
			return null;
		try {
			java.net.URL urlObj = new URL(url);
			String fileName = urlObj.getPath();
			if(fileName.contains("/"))
				fileName = fileName.substring(fileName.lastIndexOf('/') + 1);
			if(fileName.contains("."))
				fileName = fileName.substring(0, fileName.indexOf('.'));
			fileName = fileName.replaceAll(UrlParserStep.URL_SPECIAL_CHARACTERS_REGEX, " ");
			fileName = fileName.replace("%20", " ");
			return fileName;
		} catch (MalformedURLException e) {
			return null;
		}
	}

	@Override
	public String getMethod() {
		return "File";
	}
}

class LinksTitleBuilder implements ITitleBuilder
{
	private static List<String> stopWords = Arrays.asList(new String[] {"click", "here", "more", "reading" });
	private static int MAX_LINK_LENGTH = 75;
	private static int MIN_LINK_LENGTH = 5;
	public String buildTitle(ITicket ticket) {
		String linksStr = (String) ticket.getField("Links");		
		if(StringUtils.isEmpty(linksStr))
			return null;
		List<String> links = Arrays.asList(linksStr.split("aaa\\."));
		String title = null;
		for(int i = 0; i < links.size(); ++i) {
			String link = links.get(i);
			link = link.trim();
			if(link.length() < MIN_LINK_LENGTH || link.length() > MAX_LINK_LENGTH)
				continue;		
			else {
				boolean validLink = true;
				for(String stopWord : stopWords) {
					if(link.contains(stopWord)) {
						validLink = false;
						break;
					}
				}
				if(validLink) {
					if(title == null || title.length() < link.length())
						title = link;				
				}
			}
		}
		return title;
	}
	@Override
	public String getMethod() {
		return "Links";
	}
}