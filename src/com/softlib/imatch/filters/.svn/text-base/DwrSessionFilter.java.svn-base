package com.softlib.imatch.filters;

import java.lang.reflect.Method;

import org.directwebremoting.AjaxFilter;
import org.directwebremoting.AjaxFilterChain;
import org.directwebremoting.WebContextFactory;

import com.softlib.imatch.exceptions.SoftlibSessionExpired;

public class DwrSessionFilter implements AjaxFilter {

	@Override
	public Object doFilter(Object obj, Method method, Object[] params, AjaxFilterChain chain) throws Exception {
		//Check if session has timedout/invalidated  
        if( WebContextFactory.get().getSession( false ) == null ||  WebContextFactory.get().getSession( false ).getAttribute("userBean") == null) {  
            //Throw an exception  
            throw new SoftlibSessionExpired("", "error.jsf");  
        }  
  
        return chain.doFilter( obj, method, params );
	}
}
