package com.softlib.imatch.connectors.file.convert;

import java.io.File;

import org.apache.log4j.Logger;

import com.softlib.imatch.ITicket;
import com.softlib.imatch.ITicketRetrievedCallback;
import com.softlib.imatch.MatcherException;
import com.softlib.imatch.RuntimeInfo;
import com.softlib.imatch.common.FileUtils;
import com.softlib.imatch.common.cache.ICacheManager;
import com.softlib.imatch.common.LogUtils;
import com.softlib.imatch.connectors.file.disk.FileTicketProvider;
import com.softlib.imatch.dbintegration.DBTicket;
import com.softlib.imatch.matcher.Statement;
import com.softlib.imatch.matcher.TicketingSystemIntegrationConfig;
import com.softlib.imatch.ticketprocessing.IProcessedTicket.MatchMode;

public class ConvertFileTicketProvider extends FileTicketProvider {
	
	private final static Logger log = Logger.getLogger(ConvertFileTicketProvider.class);

	private ConvertFileMngr convertFileMngr;	

	protected File rootFile;
	
	public ConvertFileTicketProvider(String objectId,TicketingSystemIntegrationConfig config) {
		super(objectId, config);
	}

	public ITicket getForDisplay(String id) throws MatcherException {
		Statement displayStmt = config.getDisplayTicketStatement();
		if (displayStmt == null)
			return get(id);
		GetCallback callback = new GetCallback();
		File ticketFile = new File(id);
		if(ticketFile == null || ticketFile.isDirectory() || !ticketFile.canRead()) {
			LogUtils.warn(log, "File for ticket %s was not found", id);
			return null;
		}
		convertFileMngr.set(rootDirSingle);
		File convertFile = convertFileMngr.getConvert(ticketFile, config.getFileSettings().getParams().get("fileExtension"));
		get(id, convertFile, false, displayStmt, true, callback);
		ITicket ticket = callback.getTicket();
		return ticket;
	}

	protected String getTicketId(File ticketFile) {
		return ticketFile.getAbsolutePath();
	}

	protected void setRootDir(File rootFile) {
		super.setRootDir(rootFile);
		this.rootFile = rootFile;
		convertFileMngr.set(rootFile);
	}
	
	protected File getModifyTicketFile(File ticketFile,String ticketId) {
		return convertFileMngr.getConvert(ticketFile,fileExtension);
	}

	protected void get(String id, File ticketFile, boolean logEnabled, Statement statement, boolean useCache, ITicketRetrievedCallback callback) throws MatcherException {
		if (logEnabled)
			LogUtils.debug(log,
				"Ticket %s is not found in cache and need to be retrieved", id);
		
		String body = FileUtils.getContents(ticketFile);
		
		DBTicket dbTicket = new DBTicket(objectId);
		String titleFieldName = dbTicket.getFieldsConfig().getTitleFields().iterator().next();
		String bodyFieldName = dbTicket.getFieldsConfig().getBodyFields(MatchMode.all).iterator().next();
		String idFieldName = dbTicket.getFieldsConfig().getIdField();
		dbTicket.setField(idFieldName,id);
		dbTicket.setField("ShortName", id.substring(id.lastIndexOf('\\') + 1));
		dbTicket.setField(titleFieldName, id);
		dbTicket.setField(bodyFieldName, body);
		
		callback.ticketRetrieved(dbTicket);
	}

	protected void readParams(TicketingSystemIntegrationConfig config) {
		super.readParams(config);
		convertFileMngr = new ConvertFileMngr(config.getConvertSettings());
	}	

	
};
