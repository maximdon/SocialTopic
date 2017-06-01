package com.softlib.imatch;

/**
 * Represents single folder in solution hierarchy 
 * @author Maxim Donde
 *
 */
public class SolutionFolder 
{
	private String folderName;
	private String folderNickname;
	
	public final static SolutionFolder configFolder = new SolutionFolder("config", "{SolutionConfigFolder}");
	public final static SolutionFolder dbFolder = new SolutionFolder("db", "{SolutionDBFolder}");
	public final static SolutionFolder jspFolder = new SolutionFolder("jsp", "{SolutionJspFolder}");
	public final static SolutionFolder regexFolder = new SolutionFolder("regex", "{RegexFolder}");
	public final static SolutionFolder dictFolder = new SolutionFolder("dictionaries", "{SolutionDictFolder}");
	public final static SolutionFolder[] solutionFolders = new SolutionFolder[] {configFolder, dbFolder, jspFolder, regexFolder, dictFolder};
	
	public SolutionFolder(String folderName, String folderNickname)
	{
		this.folderName = folderName;
		this.folderNickname = folderNickname;
	}
	
	public boolean resolveSolutionFolderPath(String path, String solutionRoot, StringBuffer resolvedPath)
	{
		if(path.contains(folderNickname))
		{
			resolvedPath.append(path.replace(folderNickname, solutionRoot + folderName));
			return true;
		}
		return false;
	}
	
}
