package com.softlib.tools.dictionaryapi;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.softlib.imatch.common.HighlightText;
import com.softlib.imatch.common.TextPosition;
import com.softlib.imatch.common.HighlightText.Type;
import com.softlib.imatch.dictionary.TechnicalDictionaryTerm;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

public class DictAPIResponseBody {

	@XStreamImplicit(itemFieldName="Term")
	private List<DictAPIResponseBodyTerm> bodyTerms;

	public List<DictAPIResponseBodyTerm> getBodyTerms() {
		return bodyTerms;
	}

	public void setBodyTerms(List<DictAPIResponseBodyTerm> bodyTerms) {
		this.bodyTerms = bodyTerms;
	}

	private String getSubString(String text,Set<TextPosition> positions) {
		if (positions==null)
			return "";
		
		int startPos = -1;
		int endPos = -1;
		
		for (TextPosition position : positions) {
			if ((startPos==-1) || position.getStart() < startPos)
				startPos = position.getStart();
			if ((endPos==-1) || position.getEnd() > endPos)
				endPos = position.getEnd();
		}
		
		return text.substring(startPos,endPos);
	}
	
	public DictAPIResponseBody(String text,List<DictAPIResponseDataTerm> termDatas) {

		HighlightText highlightText = new HighlightText(text);
		List<TechnicalDictionaryTerm> terms = new ArrayList<TechnicalDictionaryTerm>();
		for (DictAPIResponseDataTerm termData : termDatas) {
			terms.add(termData.getTerm());
		}
		
		highlightText.highlight(terms,Type.Active);
		
		Map<TechnicalDictionaryTerm, Set<TextPosition>> positionsByTerm = 
			highlightText.getPositionsByTerm();
		
		bodyTerms = new ArrayList<DictAPIResponseBodyTerm>();
		for (DictAPIResponseDataTerm termData : termDatas) {
			String originalText = getSubString(text,positionsByTerm.get(termData.getTerm()));
			bodyTerms.add(new DictAPIResponseBodyTerm(termData,originalText));
		}
	}
	
};
