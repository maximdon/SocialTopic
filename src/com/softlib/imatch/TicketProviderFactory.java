package com.softlib.imatch;

import java.util.HashMap;
import java.util.Map;

import com.softlib.imatch.common.configuration.IConfigurationObject;
import com.softlib.imatch.common.configuration.IConfigurationResource;
import com.softlib.imatch.common.configuration.IConfigurationResourceLoader;
import com.softlib.imatch.connectors.EmptyTicketProvider;
import com.softlib.imatch.connectors.file.convert.ConvertFileTicketProvider;
import com.softlib.imatch.connectors.file.disk.FileTicketProvider;
import com.softlib.imatch.dbintegration.DBTicketProvider;
import com.softlib.imatch.matcher.TicketingSystemIntegrationConfig;

/**
 * Special factory to return configured ticketing system integration provider.
 * There is only one such object, so it's loaded once and used forever
 * @author Maxim Donde
 *
 */
public class TicketProviderFactory implements ICustomFactory, IContextInitializationListener {
	private IConfigurationResourceLoader loader;
	private Map<String, ITicketProvider> providers;
	private IConfigurationObject config;
	private final static String DB_INTEGRATION_TYPE = "database";
	private final static String FILE_INTEGRATION_TYPE = "file";
	private final static String CONVERT_FILE_INTEGRATION_TYPE = "convertFile";
	private final static String EMPTY_INTEGRATION_TYPE = "_empty_";
	
	public TicketProviderFactory(IConfigurationResourceLoader loader) {
		this.loader = loader;		
		RuntimeInfo.getCurrentInfo().registerCustomFactory(this);
		RuntimeInfo.getCurrentInfo().registerContextInitializationListener(this);
		providers = new HashMap<String, ITicketProvider>();
	}
	
	public Object getBean(String beanName) {
		ITicketProvider provider = providers.get(beanName);
		if(provider == null) {
			String configurationType = (String) config.getProperty(beanName, "integrationType");
			if(configurationType.equals(DB_INTEGRATION_TYPE)) 
				provider = new DBTicketProvider(beanName, createTicketingSystemConfig(beanName));			
			else if (configurationType.equals(FILE_INTEGRATION_TYPE))
				provider = new FileTicketProvider(beanName, createTicketingSystemConfig(beanName));
			else if (configurationType.equals(CONVERT_FILE_INTEGRATION_TYPE))
				provider = new ConvertFileTicketProvider(beanName, createTicketingSystemConfig(beanName));
			else if (configurationType.equals(EMPTY_INTEGRATION_TYPE))
				provider = new EmptyTicketProvider(beanName, createTicketingSystemConfig(beanName));
			providers.put(beanName, provider);
		}
		return provider;
	}
	
	String getIntegrationType(String objectId) {
		return (String) config.getProperty(objectId, "integrationType");
	}
	
	private TicketingSystemIntegrationConfig createTicketingSystemConfig(String objectId) {
		TicketingSystemIntegrationConfig ticketingSystemConfig = (TicketingSystemIntegrationConfig)config.getUnderlinedObject(objectId);		
		return ticketingSystemConfig;
	}

	public String getNamespace() {		
		return "ticketProvider";
	}

	public void contextInitialized() {
		IConfigurationResource resource = loader.loadResource("xml:///{SolutionConfigFolder}/matcher.xml;//ticketingSystemIntegration");
		config = resource.getConfigurationObject(TicketingSystemIntegrationConfig.class);
	}

	public static String getProviderId(ITicket ticket) {
		return getProviderId(ticket.getOriginObjectId());
	}

	public static String getProviderId(String objectId) {		
		return "ticketProvider." + objectId;
	}
	
}
