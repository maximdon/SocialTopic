package com.softlib.imatch.matcher;

import com.softlib.imatch.common.configuration.ConfigurationObject;
import com.softlib.imatch.common.configuration.ConfigurationValidationException;
import com.softlib.imatch.common.configuration.IParameterizableConfiguration;
import com.softlib.imatch.common.configuration.IValidatableConfiguration;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamConverter;

@XStreamAlias("ticketingSystemIntegration")
public class TicketingSystemIntegrationConfig implements IValidatableConfiguration, IParameterizableConfiguration
{	
	@XStreamAsAttribute
	@XStreamAlias("type")
	private String integrationType;

	@XStreamConverter(TicketingSystemSettings.class)
	private TicketingSystemSettings dbSettings = new TicketingSystemSettings();
	@XStreamConverter(TicketingSystemSettings.class)
	private TicketingSystemSettings fileSettings = new TicketingSystemSettings();
	@XStreamConverter(TicketingSystemSettings.class)
	private TicketingSystemSettings convertSettings = new TicketingSystemSettings();
	@XStreamConverter(TicketingSystemSettings.class)
	private TicketingSystemSettings azureBlobsSettings = new TicketingSystemSettings();
	
	@XStreamAsAttribute
	private String ticketIdPattern; 
	@XStreamConverter(Statement.class)
	private Statement singleTicketStatement = new Statement();
	@XStreamConverter(Statement.class)
	private Statement displayTicketStatement = new Statement();
	@XStreamConverter(Statement.class)
	private Statement changedTicketsStatement = new Statement();
	@XStreamConverter(Statement.class)
	private Statement allTicketsStatement = new Statement();
	@XStreamConverter(TicketingSystemFieldMapping.class)	
	private TicketingSystemFieldMapping fieldMapping = new TicketingSystemFieldMapping();

	@XStreamConverter(FieldBoostMapping.class)	
	private FieldBoostMapping boostMapping = new FieldBoostMapping();

	
	@XStreamConverter(TicketingSystemBodyFields.class)	
	private TicketingSystemBodyFields bodyFields = new TicketingSystemBodyFields();
	@XStreamConverter(Statement.class)
	private Statement allTicketsCountStatement = new Statement();	

	public void setIntegrationType(String integrationType) {
		this.integrationType = integrationType;
	}
	public String getIntegrationType() {
		return integrationType;
	}
	public void setSingleTicketStatement(Statement singleTicketStatement) {
		this.singleTicketStatement = singleTicketStatement;
	}
	
	public Statement getSingleTicketStatement() {
		return singleTicketStatement;
	}	
	
	public void setDisplayTicketStatement(Statement displayTicketStatement) {
		this.displayTicketStatement = displayTicketStatement;
	}

	public Statement getDisplayTicketStatement() {
		return displayTicketStatement;
	}

	public TicketingSystemFieldMapping getFieldMapping()
	{
		return fieldMapping;
	}
	
	public void setFieldMapping(TicketingSystemFieldMapping mapping)
	{
		fieldMapping = mapping;
	}
	
	public TicketingSystemBodyFields getBodyFields()
	{
		return bodyFields;
	}
	
	public void setBodyFields(TicketingSystemBodyFields bodyFields)
	{
		this.bodyFields = bodyFields;
	}

	public void setChangedTicketsStatement(Statement changedTicketsStatement) {
		this.changedTicketsStatement = changedTicketsStatement;
	}
	public Statement getChangedTicketsStatement() {
		return changedTicketsStatement;
	}
	public void setAllTicketsStatement(Statement allTicketsStatement) {
		this.allTicketsStatement = allTicketsStatement;
	}
	public Statement getAllTicketsStatement() {
		return allTicketsStatement;
	}
	public void setAllTicketsCountStatement(Statement allTicketsCountStatement) {
		this.allTicketsCountStatement = allTicketsCountStatement;
	}
	public Statement getAllTicketsCountStatement() {
		return allTicketsCountStatement;
	}
	
	public TicketingSystemSettings getDbSettings() {
		return dbSettings;
	}
	public void setDbSettings(TicketingSystemSettings dbSettings) {
		this.dbSettings = dbSettings;
	}
	public TicketingSystemSettings getFileSettings() {
		return fileSettings;
	}
	public void setFileSettings(TicketingSystemSettings fileSettings) {
		this.fileSettings = fileSettings;
	}
	public TicketingSystemSettings getConvertSettings() {
		return convertSettings;
	}
	public void setConvertSettings(TicketingSystemSettings convertSettings) {
		this.convertSettings = convertSettings;
	}

	public void setAzureBlobsSettings(TicketingSystemSettings azureBlobsSettings) {
		this.azureBlobsSettings = azureBlobsSettings;
	}
	public TicketingSystemSettings getAzureBlobsSettings() {
		return azureBlobsSettings;
	}

	public FieldBoostMapping getBoostMapping(){
		if (boostMapping == null)
			boostMapping = new FieldBoostMapping();
		
		return boostMapping;
	}
	public void setBoostMapping(FieldBoostMapping mapping)
	{
		this.boostMapping = mapping;
	}
	
	public void setTicketIdPattern(String idPattern) {
		this.ticketIdPattern = idPattern;
	}
	
	public String getTicketIdPattern() {
		return ticketIdPattern;
	}
	@Override
	public void validate() throws ConfigurationValidationException {
		if(bodyFields.isEmpty())
			throw new ConfigurationValidationException("At least one body field required");
		if(singleTicketStatement == null || displayTicketStatement == null || changedTicketsStatement == null || allTicketsStatement == null)
			throw new ConfigurationValidationException("One of ticket statements contain ',' which is forbidden character. Use _COMMA_ instead");
		if(dbSettings != null && dbSettings.isInitialized() && dbSettings.getConnectionString() == null)
			throw new ConfigurationValidationException("connectionString param is missing for db configuration");
		if(fileSettings != null && fileSettings.isInitialized() && fileSettings.getConnectionString() == null)
			throw new ConfigurationValidationException("connectionString param is missing for file configuration");
		if(azureBlobsSettings != null && azureBlobsSettings.isInitialized() && azureBlobsSettings.getConnectionString() == null)
			throw new ConfigurationValidationException("connectionString param is missing for azure blob configuration");
	}
	@Override
	public void replacePlaceHolders() {
		if(azureBlobsSettings == null)
			return;
		String connectionString = ConfigurationObject.replacePlaceHolders(azureBlobsSettings.getConnectionString());
		azureBlobsSettings.setConnectionString(connectionString);
	}
}
