package com.softlib.imatch.matcher.boost;

import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("customBoostFactors")
public class CustomBoostFactorsConfig {
	@XStreamImplicit(itemFieldName="boostFactor")
	private List<CustomBoostFactor> factors;

	public void setFactors(List<CustomBoostFactor> factors) {
		this.factors = factors;
	}

	public List<CustomBoostFactor> getFactors() {
		return factors;
	}
	
}
