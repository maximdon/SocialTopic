/*
 * Created on 22/09/2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.softlib.imatch.exceptions;


public class SoftlibSessionExpired extends SoftlibException {
	private static final long serialVersionUID = 424636859621269675L;
	
	public SoftlibSessionExpired(String msg, String toView) {
		super(msg, toView);
	}
}
