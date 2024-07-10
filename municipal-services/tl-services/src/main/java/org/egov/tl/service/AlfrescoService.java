package org.egov.tl.service;

import org.egov.tl.config.TLConfiguration;
import org.egov.tl.repository.ServiceRequestRepository;
import org.egov.tl.web.models.contract.Alfresco.DMSResponse;
import org.egov.tl.web.models.contract.Alfresco.DmsRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class AlfrescoService {


	@Autowired
    private TLConfiguration config;

	@Autowired
    private ObjectMapper mapper;
    
	@Autowired
    private ServiceRequestRepository serviceRequestRepository;
	
	public DMSResponse uploadAttachment(DmsRequest dmsRequest) {
		
		Object result = serviceRequestRepository.fetchResult(new StringBuilder(config.getAlfrescoHost().concat(config.getAlfrescoUploadEndPoint())), dmsRequest);
		DMSResponse response = mapper.convertValue(result,DMSResponse.class);
		
		return response;
	}
	
	
}
