package org.egov.filestore.repository;

import java.util.HashMap;
import java.util.Map;

import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.api.SessionFactory;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.enums.BindingType;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@Order(2)
public class AlfrescoClientFacade implements ApplicationRunner{
	
	@Value("${ALFRESCO_USER}")
	private String ALFRESCO_USER;
	@Value("${ALFRESCO_PASSWORD}")
	private String ALFRESCO_PASSWORD;
	@Value("${ALFRESCO_URL}")
	private String ALFRESCO_URL;
	
	@Value("${isAlfrescoEnabled}")
	private Boolean isAlfrescoEnabled;
	
	private static Session session;
	
	@Override
	public void run(ApplicationArguments arg0) throws Exception {
		if(isAlfrescoEnabled)
			initializeAlfrescoClient();		
	}
	
	public void initializeAlfrescoClient() {
		SessionFactory factory = null;
		Map<String, String> parameter = new HashMap<String, String>();
		Session currentSession = null;
		try {
			factory = SessionFactoryImpl.newInstance();
			parameter.put(SessionParameter.USER, ALFRESCO_USER);
			parameter.put(SessionParameter.PASSWORD, ALFRESCO_PASSWORD);
			parameter.put(SessionParameter.ATOMPUB_URL, ALFRESCO_URL);
			parameter.put(SessionParameter.BINDING_TYPE, BindingType.ATOMPUB.value());
			currentSession = factory.getRepositories(parameter).get(0).createSession();
		} catch (Exception e) {
			throw new CustomException("WG_WF_CLIENT_INITIALIZE_ERROR",e.getMessage());
		}
		 session = currentSession;
		
	}
	
	public Session getAlfrescoClient() {
		return session;
	}
	
	
}
