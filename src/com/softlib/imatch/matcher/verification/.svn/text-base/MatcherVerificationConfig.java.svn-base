package com.softlib.imatch.matcher.verification;

import java.util.List;

import com.softlib.imatch.matcher.verification.VerifyTestInfo;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("matcherVerification")
public class MatcherVerificationConfig {

	@XStreamImplicit(itemFieldName="test")
	private List<VerifyTestInfo> tests;

	public void setTests(List<VerifyTestInfo> tests) {
		this.tests = tests;
	}

	public List<VerifyTestInfo> getTests() {
		return tests;
	}
	
	
}
