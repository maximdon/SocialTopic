package com.softlib.imatch.common;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.softlib.imatch.common.SimpleTokenizer.SplitMngr;
import com.softlib.imatch.proximity.ProximityData;

public class ProximityDelimeterAwareTokenizer extends SimpleTokenizer 
{
	public ProximityDelimeterAwareTokenizer(String regex) 
	{
		super(regex);
	}
	
	public ProximityDelimeterAwareTokenizer(char[] delimiters) 
	{
		super(delimiters);
	}

	public String[] split(String string) {
		SplitMngr splitMngr = new SplitMngr();
		for (String word : pattern.split(string))
			if(!word.equals(ProximityData.SEPERATOR.trim()))
				splitMngr.add(word);
		return splitMngr.split();
	}
};
