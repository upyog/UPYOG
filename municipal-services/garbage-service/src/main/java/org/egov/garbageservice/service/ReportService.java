package org.egov.garbageservice.service;

import org.egov.garbageservice.util.GrbgConstants;
import org.egov.garbageservice.model.contract.PDFRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

//import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ReportService {

	@Autowired
    private GrbgConstants config;

//	@Autowired
//    private ObjectMapper mapper;
    
	@Autowired
	private RestTemplate restTemplate;

	
	public Resource createNoSavePDF(PDFRequest pdfRequest) {
		
//		Object result = serviceRequestRepository.fetchResult(new StringBuilder(config.getReportHost().concat(config.getReportCreateEndPoint())), pdfRequest);
//		Resource resource = mapper.convertValue(result,Resource.class);
		
		

		HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        HttpEntity<PDFRequest> requestEntity = new HttpEntity<>(pdfRequest, headers);

        ResponseEntity<Resource> responseEntity = restTemplate.exchange(
        		config.getReportHost().concat(config.getReportCreateEndPoint()),
            HttpMethod.POST,
            requestEntity,
            Resource.class
        );
		
		
		return responseEntity.getBody();
	}
	
}
