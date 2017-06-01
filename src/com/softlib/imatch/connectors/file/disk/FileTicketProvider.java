package com.softlib.imatch.connectors.file.disk;

import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.softlib.imatch.ITicket;
import com.softlib.imatch.ITicketRetrievedCallback;
import com.softlib.imatch.MatcherException;
import com.softlib.imatch.RuntimeInfo;
import com.softlib.imatch.common.FileUtils;
import com.softlib.imatch.common.cache.ICacheManager;
import com.softlib.imatch.common.LogUtils;
import com.softlib.imatch.common.configuration.ConfigurationException;
import com.softlib.imatch.connectors.BaseTicketProvider;
import com.softlib.imatch.connectors.file.LastModifiedFileFilter;
import com.softlib.imatch.dbintegration.DBTicket;
import com.softlib.imatch.dbintegration.DBTicketProvider;
import com.softlib.imatch.enums.MatchErrorCodes;
import com.softlib.imatch.matcher.Statement;
import com.softlib.imatch.matcher.TicketingSystemFieldMapping;
import com.softlib.imatch.matcher.TicketingSystemIntegrationConfig;

public class FileTicketProvider extends BaseTicketProvider {

	private final static Logger log = Logger.getLogger(FileTicketProvider.class);
	private final static String INITIAL_SECTION = "__INITIAL SECTION__";

	protected File rootDirAll;
	protected File rootDirSingle;
	private boolean includeSubfolders = true;
	private ICacheManager<ITicket> cache;
	private String charsetName;
	private Map<String, Pattern> sectionsPatterns = new HashMap<String, Pattern>();
	private Map<String, Integer> sectionsLineSkip = new HashMap<String, Integer>();
	private String[] sections = new String[0];
	protected String fileExtension;

	private FileSaveProcessedData saveProcessedData;


	
	public FileTicketProvider(String objectId, TicketingSystemIntegrationConfig config) {		
		super(objectId, config);
		readParams(config);
		saveProcessedData = new FileSaveProcessedData();
	}

	public void save() {
		saveProcessedData.save();
	}

	protected void readParams(TicketingSystemIntegrationConfig config) {	
		Map<String, String> connectionParams = config.getFileSettings().getParams();

		String connectionString = connectionParams.get("connectionString");
		String[] rootDirs = parseConnectionString(connectionString);
		rootDirAll = new File(rootDirs[0]);
		rootDirSingle = new File(rootDirs[1]);
		if(!rootDirAll.exists() || !rootDirAll.isDirectory())
			throw new ConfigurationException("Invalid path to all ticket files " + connectionString);
		if(!rootDirAll.canRead())
			throw new ConfigurationException("Unable to read from " + connectionString);
		if(!rootDirSingle.exists() || !rootDirSingle.isDirectory())
			throw new ConfigurationException("Invalid path to single ticket files " + connectionString);
		if(!rootDirSingle.canRead())
			throw new ConfigurationException("Unable to read from " + connectionString);

		LogUtils.info(log, "File system adapter initialized successfully, all tickets folder is set to %s, single ticket folder is set to %s" , rootDirs[0], rootDirs[1]);
	
		charsetName = connectionParams.get("charset");
		fileExtension = connectionParams.get("fileExtension");
		if(connectionParams.containsKey("includeSubfolders"))
			includeSubfolders = Boolean.parseBoolean(connectionParams.get("includeSubfolders"));
		
		String sectionsStr = connectionParams.get("sections");
		if (sectionsStr!=null) {
			sections = connectionParams.get("sections").split(",");
			sectionsPatterns.put(INITIAL_SECTION, Pattern.compile(INITIAL_SECTION));
			for(String section : sections) {
				sectionsPatterns.put(section, Pattern.compile(connectionParams.get(section), Pattern.MULTILINE));
			}
		}
		
		LogUtils.debug(log, "Each ticket file should contain all or part of the following sections:%s" , Arrays.asList(sections));

		for(String sectionLineSkip : connectionParams.keySet())
		if(sectionLineSkip.endsWith("## Line skip")) {
			String sectionName = sectionLineSkip.substring(0, sectionLineSkip.indexOf("##") - 1);
			sectionsLineSkip.put(sectionName, Integer.parseInt(connectionParams.get(sectionLineSkip)));
		}
	}
	
	private String[] parseConnectionString(String connectionString) {
		String[] rootDirs = new String[2];
		String[] parts = connectionString.split(";");
		if(parts.length != 2)
			throw new ConfigurationException("Invalid connection string for file connector, please define both all tickets root and single ticket root");
		String part1 = parts[0];
		String part2 = parts[1];
		String[] part1parts = part1.split("=");
		String[] part2parts = part2.split("=");
		if(part1parts[0].equals("allTicketsFolder")) {
			rootDirs[0] = part1parts[1];
			rootDirs[1] = part2parts[1];
		}
		else {
			rootDirs[0] = part2parts[1];
			rootDirs[1] = part1parts[1];			
		}
		return rootDirs;
	}

	public ITicket get(String id) throws MatcherException {
		if(!validateTicketId(id))
			throw new MatcherException("Invalid ticket id supplied", MatchErrorCodes.InvalidId);
		GetCallback callback = new GetCallback();
		File ticketFile = new File(rootDirSingle, id + "." + fileExtension);
		if(!ticketFile.exists() || ticketFile.isDirectory() || !ticketFile.canRead()) {
			return null;
		}
		get(id, ticketFile, true, config.getSingleTicketStatement(), false, callback);
		LogUtils.debug(log, "Ticket %s retrieved successfully", id);
		return callback.getTicket();
	}

	@SuppressWarnings("unchecked")
	public ITicket getForDisplay(String id) throws MatcherException {
		Statement displayStmt = config.getDisplayTicketStatement();
		if (displayStmt == null)
			return get(id);
		synchronized (this) {
			if (cache == null)
				cache = (ICacheManager<ITicket>) RuntimeInfo.getCurrentInfo().getBean("cacheManager");
		}
		ITicket ticket = cache.get(objectId, id);
		if (ticket != null) {
			LogUtils.debug(log, "Return cached instance for ticket %s", id);
			return ticket;
		}
		else 
			LogUtils.debug(log, "Ticket %s not found in cache and need to be retrieved", id);
		GetCallback callback = new GetCallback();
		File ticketFile = FileUtils.findFile(id + "." + fileExtension, rootDirSingle);
		if(ticketFile == null || ticketFile.isDirectory() || !ticketFile.canRead()) {
			LogUtils.warn(log, "File for ticket %s was not found", id);
			return null;
		}
		get(id, ticketFile, false, displayStmt, true, callback);
		ticket = callback.getTicket();
		if(ticket != null) {
			LogUtils.debug(log, "Adding a ticket %s to the cache", id);
			cache.put(objectId, id, ticket);
		}
		return ticket;
	}
	
	protected void get(String id, File ticketFile, boolean logEnabled, Statement statement, boolean useCache, ITicketRetrievedCallback callback) throws MatcherException {
		ITicket ticket = null;
		LogUtils.debug(log,
				"Ticket %s is not found in cache and need to be retrieved", id);
		Scanner ticketFileScanner;
		try {
			ticketFileScanner = new Scanner(ticketFile, charsetName);
		} catch (FileNotFoundException e) {
			//Should never happen since we checked file existence before
			throw new RuntimeException("Unexpected error accessing ticket file");
		}
		StringBuilder currentFieldBuffer = new StringBuilder();
	    ticket = new DBTicket(objectId);
	    ((DBTicket)ticket).setField(TicketingSystemFieldMapping.ID_FIELD_DEFAULT_NAME, id);
	    String currentSection = INITIAL_SECTION;
	    String newSection = null;
	    int currentSectionIdx = -1;
	    int currentSectionLineCount = 0;
	    boolean lastSection = false;
		while(ticketFileScanner.hasNextLine()) {
			String currentLine = ticketFileScanner.nextLine();
			//TODO current limitations: No support for Title:XXX suppose new line, 
			//no support for Title:		Description:
			//				 Title1		Desc1
			//In short we suppose Each section to begin at new line and no intersection between sections
			//Another limitation: we should specify all file sections in matcher.xml even those we don't interest
			newSection = getSectionForLine(currentSection, currentLine);
			if(!lastSection && newSection != null) {
				if(statement.getStatementString().contains(currentSection)) {
					if(logEnabled)
						LogUtils.debug(log, "Adding data from section %s", currentSection);
					Object currValObj = ticket.getField(currentSection);
					String sectionVal = currentFieldBuffer.toString(); //.replace("\r\n", "");
					if (currValObj != null && Arrays.binarySearch(statement.getConcatFields(), currentSection) != -1) {
						String currVal = currValObj.toString();
						((DBTicket)ticket).setField(currentSection, concatField(currVal, sectionVal));
					} else
						((DBTicket)ticket).setField(currentSection, sectionVal);
				}
				currentSection = newSection;
				lastSection = currentSectionIdx == sections.length -1;
				currentSectionLineCount = 0;
				currentFieldBuffer.setLength(0);
				//Check if the section actually inline section of form Title:    Value. 
				//In this case we process it there. We suppose that inline sections don't have multi-line values 
				Matcher matcher = sectionsPatterns.get(currentSection).matcher(currentLine);
				//There is only find and we know it exists
				matcher.find();
				String sectionTitle = matcher.group();
				//1 for end line
				if(currentLine.length() > sectionTitle.length() + 1)
					currentFieldBuffer.append(currentLine.substring(sectionTitle.length()).trim());
			}
			else
				if(!currentSection.equals(INITIAL_SECTION)) {
					Integer currentSectionLineSkip = sectionsLineSkip.get(currentSection);
					if(currentSectionLineSkip == null || currentSectionLineSkip == -1 || currentSectionLineCount >= currentSectionLineSkip)
						currentFieldBuffer.append(currentLine).append("\r\n");
					currentSectionLineCount ++;
						
				}
		}
		if(statement.getStatementString().contains(currentSection)) {
			((DBTicket)ticket).setField(currentSection, currentFieldBuffer.toString());
			currentFieldBuffer.setLength(0);
		}
		statement.getCompleter().complete((DBTicket)ticket);
		callback.ticketRetrieved(ticket);
	}

	private String getSectionForLine(String currentSection, String currentLine) {
		for(String section : sections) {
			if(sectionsPatterns.get(section).matcher(currentLine).find())
				return section;
		}
		return null;
	}
	
	protected void setRootDir(File rootFile) {
	}
	
	protected String getFileName(String id) {
		return id + "." + fileExtension;
	}
	
	protected String getTicketId(File ticketFile) {
		String id;
		String fileName = ticketFile.getName();
		int lastDotIdx = fileName.lastIndexOf('.');
		
		if (lastDotIdx>=0)
			id = fileName.substring(0, fileName.lastIndexOf('.'));
		else
			id = fileName;
		return id;
	}
	
	protected File getTicketFile(File ticketFile,String ticketId) {
		if (saveProcessedData.isExist(ticketId))
			return null;
		File rc = getModifyTicketFile(ticketFile,ticketId);
		saveProcessedData.add(ticketId);
		return rc;
	}
	
	protected File getModifyTicketFile(File ticketFile,String ticketId) {
		return ticketFile;
	}
	
	public void getAllTickets(ITicketRetrievedCallback callback) throws MatcherException {
		setRootDir(rootDirAll);
		int ticketCount = doGetAllTickets(callback, null);
		LogUtils.info(log, "Total %d tickets retrieved from the file system", ticketCount);
	}
	
	protected int doGetAllTickets(ITicketRetrievedCallback callback,FileFilter filter) throws MatcherException {
		return getAllTicketsRecursive(callback, rootDirAll, filter);
	}
	
	private int getAllTicketsRecursive(ITicketRetrievedCallback callback, File dir, FileFilter filter) throws MatcherException {
		int ticketCount = 0;
		File[] allFiles;
		if(filter == null)
			allFiles = dir.listFiles();
		else
			allFiles = dir.listFiles(filter);
		LogUtils.info(log, "File adapter going to process %d tickets", allFiles.length);		

		for(File ticketFile : allFiles) {
			if(ticketFile.isDirectory()) {
				if(!includeSubfolders)
					continue;
				ticketCount += getAllTicketsRecursive(callback, ticketFile, filter);
			}
			else {
				String ticketId = getTicketId(ticketFile);
				File ticketNewFile = getTicketFile(ticketFile,ticketId);
				if (ticketNewFile!=null) {
					get(ticketId, ticketNewFile, false, config.getAllTicketsStatement(), false, callback);
					ticketCount++;
					if (ticketCount % 100 == 0)
						LogUtils.debug(log, "%d tickets retrieved", ticketCount);
				}
				else 
					LogUtils.debug(log,	"Skipping ticket %s", ticketId);
			}
		}
		return ticketCount;
	}

	public int getAllTicketsCount() throws MatcherException {
		return rootDirAll.listFiles().length;
	}

	public void getChangedTickets(long lastRunTime,ITicketRetrievedCallback callback) throws MatcherException {
		if(lastRunTime < 0) {
			//Special case, retrieve all tickets
			LogUtils.debug(log, "Retrieving all tickets");		
			getAllTickets(callback);
			return;
		}
		FileFilter filter = new LastModifiedFileFilter(lastRunTime);
		setRootDir(rootDirAll);
		int ticketCount =  doGetAllTickets(callback, filter);
		LogUtils.info(log, "Total %d tickets retrieved from the file system", ticketCount);
	}	
};
