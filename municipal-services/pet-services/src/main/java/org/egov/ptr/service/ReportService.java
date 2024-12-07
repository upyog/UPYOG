package org.egov.ptr.service;

import org.egov.ptr.repository.ServiceRequestRepository;
import org.egov.ptr.util.PTRConstants;
import org.egov.ptr.web.contracts.PDFRequest;
import org.egov.ptr.web.contracts.PetPhotoRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Base64;


import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class ReportService {

	@Autowired
    private PTRConstants config;

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
	
	public String getPetPhoto(PetPhotoRequest petPhotoRequest) {
	    HttpHeaders headers = new HttpHeaders();
	    headers.set("Content-Type", "application/json");

	    try {
	        // Create the HTTP entity with headers and request body
	        HttpEntity<PetPhotoRequest> requestEntity = new HttpEntity<>(petPhotoRequest, headers);

	        // Make the REST call to get binary data
	        ResponseEntity<byte[]> responseEntity = restTemplate.exchange(
	                config.getAlfrescoHost().concat(config.getAlfrescoGetPetPhotoEndPoint()),
	                HttpMethod.POST,
	                requestEntity,
	                byte[].class
	        );

	        // Extract the response body (binary data)
	        byte[] binaryData = responseEntity.getBody();
	        if (binaryData != null) {
	            // Convert binary data to Base64 string and return
	            return Base64.getEncoder().encodeToString(binaryData);
	        } else {
	            throw new RuntimeException("Empty response body");
	        }
	    } catch (Exception e) {
	        // Log the error
	        e.printStackTrace();
	        // Return a default error message or an empty Base64 string
	        return "Error: Unable to fetch pet photo. " + e.getMessage();
	    }
	}

	
//	public String sampleBytePhoto() {
//		  HttpHeaders headers = new HttpHeaders();
//	        headers.set("Content-Type", "application/json");
//		 String requestBody = "{\r\n" +
//	                "    \"RequestInfo\": {\r\n" +
//	                "        \"authToken\": \"717791b3-8e75-46ed-9194-5630ecc786f6\"\r\n" +
//	                "    },\r\n" +
//	                "    \"serviceName\": \"PTR\",\r\n" +
//	                "    \"objectId\": \"25e00c25-3a46-470b-83f2-f6235db80ec0\",\r\n" +
//	                "    \"documentId\": \"9\",\r\n" +
//	                "    \"documentType\": \"CITIZ\"\r\n" +
//	                "}";
//
//	        // Create HttpEntity
//	        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody, headers);
//	        try {
//	            // Make the REST call and get response as byte[]
//	            ResponseEntity<byte[]> response = restTemplate.exchange(
//	                    "https://citizenseva.hp.gov.in/hpud-dms-service/dms/fetchServicebyDocID",
//	                    HttpMethod.POST,
//	                    requestEntity,
//	                    byte[].class
//	            );
//
//	            // Check if response has a body
//	            byte[] binaryData = response.getBody();
//	            if (binaryData != null) {
//	                // Convert binary data to Base64
//	                return Base64.getEncoder().encodeToString(binaryData);
//	            } else {
//	                throw new RuntimeException("Empty response body");
//	            }
//
//	        } catch (Exception e) {
//	            throw new RuntimeException("Error fetching or converting to Base64: " + e.getMessage(), e);
//	        }
//	}
//	
}
