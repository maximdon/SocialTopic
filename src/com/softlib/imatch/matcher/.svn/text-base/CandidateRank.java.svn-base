package com.softlib.imatch.matcher;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

@XStreamAlias("rank")
public class CandidateRank {
	@XStreamAsAttribute
	private String name;
	@XStreamAsAttribute
	private String icon;	
	@XStreamAsAttribute
	private int intValue;	
	private Condition condition;
	
	public CandidateRank()
	{
		
	}	
	
	public CandidateRank(String name, Condition condition, String icon) {
		super();
		this.condition = condition;
		this.name = name;
		this.icon = icon;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	public String getName() {
		return name;
	}
	public void setCondition(Condition condition) {
		this.condition = condition;
	}
	public Condition getCondition() {
		return condition;
	}
	public void setIcon(String icon) {
		this.icon = icon;
	}
	public String getIcon() {
		return icon;
	}	
	
	public boolean isTrue(MatchCandidate candidate)
	{
		return condition.isTrue(candidate);
	}
	
	@Override
	public String toString()
	{
		return name;
	}

	public int getIntValue() {
		return intValue;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		CandidateRank other = (CandidateRank) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}	
}
