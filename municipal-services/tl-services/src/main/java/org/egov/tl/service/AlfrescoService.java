package org.egov.tl.service;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;

import org.egov.common.contract.request.RequestInfo;
import org.egov.tl.config.TLConfiguration;
import org.egov.tl.repository.ServiceRequestRepository;
import org.egov.tl.web.models.contract.Alfresco.DMSResponse;
import org.egov.tl.web.models.contract.Alfresco.DmsRequest;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class AlfrescoService {


	@Autowired
    private TLConfiguration config;

	@Autowired
    private ObjectMapper mapper;

	@Autowired
    private RestTemplate restTemplate;
    
	@Autowired
    private ServiceRequestRepository serviceRequestRepository;
	
	public DMSResponse uploadAttachment(DmsRequest dmsRequest, RequestInfo requestInfo) throws IOException {
		
		
		upload(dmsRequest, null);
		
			
        
//		Object result = serviceRequestRepository.fetchResult(new StringBuilder(config.getAlfrescoHost().concat(config.getAlfrescoUploadEndPoint())), dmsRequest);
//		DMSResponse response = mapper.convertValue(result,DMSResponse.class);
		
		return null;
	}
	
	
	
	
	
	
	
	
	
	public String upload(DmsRequest dmsRequest, RequestInfo requestInfo) {

		// ..............NEED to REARRANGE..........................   //
		String documentReferenceId = "";

		StringBuilder urlBuilder = new StringBuilder(config.getAlfrescoHost());
		urlBuilder.append(config.getAlfrescoUploadEndPoint());
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);
		try {
			// Prepare the request body
			MultiValueMap<String, Object> requestBody = new LinkedMultiValueMap<>();


			requestBody.add("id", dmsRequest.getId());
			requestBody.add("file", dmsRequest.getFile());
			requestBody.add("userId", dmsRequest.getUserId());
			requestBody.add("objectId", dmsRequest.getObjectId());
			requestBody.add("description", dmsRequest.getDescription());
			requestBody.add("type", dmsRequest.getType());
			requestBody.add("objectName", dmsRequest.getObjectName());
			requestBody.add("comments", dmsRequest.getComments());
			requestBody.add("status", dmsRequest.getStatus());

			ResponseEntity<LinkedHashMap> responseEntity = restTemplate.postForEntity(urlBuilder.toString(),
					requestBody, LinkedHashMap.class);

			LinkedHashMap storageResponse = responseEntity.getBody();
			List<LinkedHashMap<String, Object>> storageDocumentDetails = (List<LinkedHashMap<String, Object>>) storageResponse
					.get("files");
			if (storageDocumentDetails != null && !storageDocumentDetails.isEmpty()) {
				LinkedHashMap<String, Object> firstStorageDetail = storageDocumentDetails.get(0);
				documentReferenceId = (String) firstStorageDetail.get("fileStoreId");
			}
			return documentReferenceId;

		} catch (Exception e) {
			log.error("Error ocurred while upload file.", e);
			throw new CustomException("UPLOAD_FILE_FAILED","Error ocurred while upload file.");
		}
	}

	
	
}
