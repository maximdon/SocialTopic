/*
 * Created on 22/09/2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.softlib.imatch.exceptions;


public class SoftlibException extends RuntimeException
{
	private static final long serialVersionUID = 3717943988114428956L;

	private String msg;
	private String toView;
	
	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getMsg() {
		return msg;
	}
	
	public void setToView(String toView) {
		this.toView = toView;
	}

	public String getToView() {
		return toView;
	}

	public SoftlibException(String msg, String toView) {
		this.msg = msg;
		this.toView = toView;
	}

	@Override
	public String getMessage() {
		// TODO Auto-generated method stub
		return getMsg();
	}

}
