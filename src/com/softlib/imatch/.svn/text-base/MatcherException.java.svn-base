package com.softlib.imatch;

import com.softlib.imatch.enums.MatchErrorCodes;

public class MatcherException extends Exception 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 4786780353299489614L;
	private MatchErrorCodes errorCode = MatchErrorCodes.Generic;
		
	public void setErrorCode(MatchErrorCodes errorCode) {
		this.errorCode = errorCode;
	}

	public MatchErrorCodes getErrorCode() {
		return errorCode;
	}

	public MatcherException() {
		super();
	}

	public MatcherException(String message, Throwable cause) {
		super(message, cause);
	}

	public MatcherException(String message) {
		super(message);
	}
	
	public MatcherException(String message, Throwable cause, MatchErrorCodes errorCode) {
		super(message);
		this.errorCode = errorCode;
	}
	
	public MatcherException(String message, MatchErrorCodes errorCode) {
		super(message);
		this.errorCode = errorCode;
	}
}
