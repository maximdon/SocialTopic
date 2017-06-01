package com.softlib.imatch.common;

public class TextPosition implements Comparable<TextPosition> {
	
	final private Integer start;
	final private Integer end;
	final private String text;

	public TextPosition(String text,Integer start) {
		this.text = text;
		this.start = start;
		this.end = start+text.length();
	}

	public boolean isNear(TextPosition other) {
		if (end+1==other.start)
			return true;
		if (other.end+1==start)
			return true;
		return false;
	}
	
	public Integer getStart() {
		return start;
	}

	public Integer getEnd() {
		return end;
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
		TextPosition other = (TextPosition) obj;
		if (start == null) {
			if (other.start != null)
				return false;
		} else if (!start.equals(other.start))
			return false;
		return true;
	}

	public int compareTo(TextPosition o) {
		return start.compareTo(o.start);
	}
	
	public String toString() {
		String rc = "{";
		rc = rc + start.toString() + "-" + end.toString();
		rc += "}";
		return rc;
	}
	
	public String getText() {
		return text;
	}
	
};
