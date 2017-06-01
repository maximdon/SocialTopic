package com.softlib.imatch.matcher;

import com.softlib.imatch.common.configuration.ConfigurationException;
import com.softlib.imatch.enums.SecurityType;
import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("security")
public class SecurityConfig 
{
	public final static String securityTypeStr = "securityType";
	
	public final static String loginPinCodeStr = "loginPinCode";
	public final static String loginExtraStr = "loginExtra";
	public final static String pinCodeStr = "pinCode";
	public final static String rememberMeTimeoutInDaysStr = "rememberMeTimeoutInDays";
	public final static String checkStepStr = "checkStep";

	private int rememberMeTimeoutInDays = 365;
	private String securityType = SecurityType.NONE.getName();
	private SecurityType serverSecurity = SecurityType.NONE;
	private boolean loginPinCode;
	private String pinCode;
	private boolean loginExtra;
	private String checkStep;
	

	public void setSecurityType(String securityType) {
		this.securityType = securityType;
		SecurityType st = null;
		try {
			st = SecurityType.valueOf(securityType.toUpperCase());
		}
		catch(Exception e){}
		if(st == null)
			throw new ConfigurationException("Invalid security type specified, valid values are: NONE, SECURED");
		this.setServerSecurity(st);
	}

	public String getSecurityType() {
		return securityType;
	}
	
	public SecurityType getServerSecurity() {
		return serverSecurity;
	}

	public void setServerSecurity(SecurityType serverSecurity) {
		this.serverSecurity = serverSecurity;
	}
	
	public boolean isLoginPinCode() {
		return loginPinCode;
	}

	public void setLoginPinCode(boolean loginPinCode) {
		this.loginPinCode = loginPinCode;
	}

	public void setPinCode(String pinCode) {
		this.pinCode = pinCode;
	}

	public String getPinCode() {
		return pinCode;
	}

	public boolean isLoginExtra() {
		return loginExtra;
	}

	public void setLoginExtra(boolean loginExtra) {
		this.loginExtra = loginExtra;
	}

	public void setRememberMeTimeoutInDays(int rememberMeTimeoutInDays) {
		this.rememberMeTimeoutInDays = rememberMeTimeoutInDays;
	}

	public int getRememberMeTimeoutInDays() {
		return rememberMeTimeoutInDays;
	}	


	public String getCheckStep() {
		return checkStep;
	}
	
	public void setCheckStep(String checkStep) {
		this.checkStep = checkStep;
	}


};
