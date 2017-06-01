package com.softlib.imatch.distance;

import com.softlib.imatch.dictionary.TechnicalDictionaryTerm;

public class TermPosition implements Comparable<TermPosition> {
	//TODO this is very ugly, sometimes (like TermsByPositions.find) the start and end indices are char positions
	//While in other cases (like FindTermsInText.getFoundTerms) the start and end indices are word positions (in this case startPosition equals to endPosition)
	final private Integer start;
	final private Integer end;
	final boolean oneSize;
	
	private TechnicalDictionaryTerm term;
	
	public Integer getStart() {
		return start;
	}

	public Integer getEnd() {
		return end;
	}

	public TermPosition(Integer start,Integer end) {
		this.start = start;
		this.end = end;
		if (end==start)
			oneSize=true;
		else
			oneSize=false;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((start == null) ? 0 : start.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TermPosition other = (TermPosition) obj;
		if (start == null) {
			if (other.start != null)
				return false;
		} else if (!start.equals(other.start))
			return false;
		return true;
	}

	public int compareTo(TermPosition o) {
		return start.compareTo(o.start);
	}
	
	public String toString() {
		String rc = "{";
		if (oneSize)
			rc+= start.toString();
		else	
			rc = rc + start.toString() + "-" + end.toString();
		rc += "}";
		return rc;
	}

	public String getTermSourceName() {
		String rc;
		try {
			rc = term.getTermSource().getsourceName();
		}
		catch (Exception e) {
			rc = "Error";
		}
		return rc;
	}
	
	public TechnicalDictionaryTerm getTerm() {
		return term;
	}

	public void setTerm(TechnicalDictionaryTerm term) {
		this.term = term;
	}
	
	
};
