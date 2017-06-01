package com.softlib.tools.fullindex;

import java.io.File;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Set;

import javax.servlet.RequestDispatcher;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;

public class FictiveServletCtxt implements ServletContext {
	
	private String rootDir;
	public FictiveServletCtxt() {	
		this(null);
	}
	
	public FictiveServletCtxt(String rootDir) {
		this.rootDir = rootDir;
	}
	public Object getAttribute(String name) {
		return null;
	}

	public Enumeration getAttributeNames() {
		return null;
	}

	public ServletContext getContext(String uripath) {
		return null;
	}

	public String getInitParameter(String name) {
		return null;
	}

	public Enumeration getInitParameterNames() {
		return null;
	}

	public int getMajorVersion() {
		return 0;
	}

	public String getMimeType(String file) {
		return null;
	}

	public int getMinorVersion() {
		return 0;
	}

	public RequestDispatcher getNamedDispatcher(String name) {
		return null;
	}

	public String getRealPath(String path) {
		String currentDir;
		if(rootDir != null)
			currentDir = rootDir;
		else
			currentDir = System.getProperty("user.dir");
		File f = new File(currentDir, "WebRoot");
		if(f.exists())
			return currentDir + "/WebRoot" + path;
		else
			return currentDir + path;
	}

	public RequestDispatcher getRequestDispatcher(String path) {
		return null;
	}

	public URL getResource(String path) throws MalformedURLException {
		return null;
	}

	public InputStream getResourceAsStream(String path) {
		return null;
	}

	public Set getResourcePaths(String path) {
		return null;
	}

	public String getServerInfo() {
		return null;
	}

	public Servlet getServlet(String name) throws ServletException {
		return null;
	}

	public String getServletContextName() {
		return null;
	}

	public Enumeration getServletNames() {
		return null;
	}

	public Enumeration getServlets() {
		return null;
	}

	public void log(String msg) {
	}

	public void log(Exception exception, String msg) {
	}

	public void log(String message, Throwable throwable) {
	}

	public void removeAttribute(String name) {
	}

	public void setAttribute(String name, Object object) {
	}

	public String getContextPath() {
		// TODO Auto-generated method stub
		return null;
	}
}
