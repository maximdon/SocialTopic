package com.softlib.imatch.matcher;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import com.softlib.imatch.ITicketProvider;
import com.softlib.imatch.RuntimeInfo;
import com.softlib.imatch.TicketProviderFactory;
import com.softlib.imatch.common.configuration.IConfigurationObject;
import com.softlib.imatch.common.configuration.IConfigurationResource;
import com.softlib.imatch.common.configuration.IConfigurationResourceLoader;
import com.softlib.imatch.ticketprocessing.IProcessedTicket.MatchMode;

public class DBTicketFieldsNames implements ITicketFieldsNames {

	private static IConfigurationObject config;
	private static Map<String,TicketingSystemIntegrationConfig> configByObjectId =
		new HashMap<String,TicketingSystemIntegrationConfig>();

	private String objectId;

	public DBTicketFieldsNames() {
		if (config==null) {
			IConfigurationResourceLoader loader = (IConfigurationResourceLoader)RuntimeInfo.getCurrentInfo().getBean("xmlConfigurationResourceLoader");
			IConfigurationResource resource = loader.loadResource("xml:///{SolutionConfigFolder}/matcher.xml;//searcher");
			config = resource.getConfigurationObject(SearcherConfiguration.class);
		}
	}
	
	public void setObjectId(String objectId) {
		this.objectId = objectId;
	}
	
	private TicketingSystemIntegrationConfig getConfig(String objectId) {
		TicketingSystemIntegrationConfig config = configByObjectId.get(objectId);
		if (config==null) {
			ITicketProvider ticketProvider = 
				(ITicketProvider) RuntimeInfo.getCurrentInfo().getBean(TicketProviderFactory.getProviderId(objectId));
			config = ticketProvider.getConfig();
			configByObjectId.put(objectId, config);
		}
		return config;
	}

	private Set<String> getFields(String objectId,MatchMode matchMode,boolean includeTitle,boolean includeBody) {
		Set<String> rc = new LinkedHashSet<String>();
		Set<String> objectIdList =  config.getAllObjects();
		for (String objId : objectIdList) {
			if (objectId!=null && !objId.equals(objectId))
				continue;
			TicketingSystemIntegrationConfig integrationConfig = getConfig(objId);
			if (includeTitle)
				rc.add(integrationConfig.getFieldMapping().getTitleFieldName());
			if (includeBody)
				rc.addAll(integrationConfig.getBodyFields().getBodyFields(matchMode));
		}
		return rc;
	}

	public Float  getFieldBoost(String fieldName)
	{
//		Set<String> rc = new LinkedHashSet<String>();
		Set<String> objectIdList =  config.getAllObjects();
		for (String objId : objectIdList) {
			if (objectId!=null && !objId.equals(objectId))
				continue;
			TicketingSystemIntegrationConfig integrationConfig = getConfig(objId);
			return integrationConfig.getBoostMapping().GetFieldBoost(fieldName);
		}
		
		return 1.0f;
	}
	
	public Set<String> getTitleFields() {
		return getFields(objectId,null,true,false);
	}
	
	public Set<String> getBodyFields(MatchMode matchMode) {
		return getFields(objectId,matchMode,false,true);
	}

	public Set<String> getAllFields(MatchMode matchMode) {
		return getFields(objectId,matchMode,true,true);
	}

	private String getField(String fieldName) {
		if (objectId==null)
			return null;
		Object fieldVal = getConfig(objectId).getFieldMapping().getFieldName(fieldName);
		return fieldVal != null ? fieldVal.toString() : null;
	}
	
	public String getIdField() {
		return getField(TicketingSystemFieldMapping.ID_FIELD_DEFAULT_NAME);
	}
	
	public String getStateField() {
		return getField(TicketingSystemFieldMapping.STATE_FIELD_DEFAULT_NAME);
	}
	
};
