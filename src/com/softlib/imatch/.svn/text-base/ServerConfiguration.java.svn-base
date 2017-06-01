package com.softlib.imatch;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

@XStreamAlias("config")
public class ServerConfiguration 
{
	@XStreamAsAttribute
	private String version;
	@XStreamAsAttribute
	private boolean debugMode;
	@XStreamAsAttribute
	private String solution;

	private String solutionsBaseFolder;
	private String serverUrl;
	private String iSolveRootFolder;
	
	public void setVersion(String version) {
		this.version = version;
	}
	public String getVersion() {
		return version;
	}
	public void setSolution(String solution) {
		this.solution = solution;
	}
	public String getSolution() {
		return solution;
	}
	public void setServerUrl(String serverUrl) {
		this.serverUrl = serverUrl;
	}
	public String getServerUrl() {
		return serverUrl;
	}
	public void setiSolveRootFolder(String iSolveRootFolder) {
		this.iSolveRootFolder = iSolveRootFolder;
	}
	public String getiSolveRootFolder() {
		return iSolveRootFolder;
	}
	public void setDebugMode(boolean debugMode) {
		this.debugMode = debugMode;
	}
	public boolean isDebugMode() {
		return debugMode;
	}
	
	public String getSolutionsBaseFolder() {
		return solutionsBaseFolder;
	}
}
