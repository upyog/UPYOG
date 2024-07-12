package org.egov.tl.service;

import org.egov.tl.config.TLConfiguration;
import org.egov.tl.repository.ServiceRequestRepository;
import org.egov.tl.web.models.contract.PDFRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ReportService {

	@Autowired
    private TLConfiguration config;

	@Autowired
    private ObjectMapper mapper;
    
	@Autowired
    private ServiceRequestRepository serviceRequestRepository;
	
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
