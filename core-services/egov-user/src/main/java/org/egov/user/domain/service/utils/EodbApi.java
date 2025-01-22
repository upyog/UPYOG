package org.egov.user.domain.service.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.*;


@Service
public class EodbApi{
	
	 @Value("${eodb.host.getCAFData}")
	    private String getCAFData;
	 @Value("${eodb.tokenhost}")
	    private String tokenhost;
    @Autowired
    private RestTemplate restTemplate;
    
 
    
    public JsonNode getCAFData(String iPin) {
        // Get the token
        String token = getToken();
        if (token == null) {
            throw new RuntimeException("Failed to fetch token");
        }

        // Prepare the request body
        String requestPayload = "{\"iPin\":\"" + iPin + "\"}";

        // Create headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization",token);

        // Create the HTTP entity with headers and body
        HttpEntity<String> requestEntity = new HttpEntity<>(requestPayload, headers);

        // Use RestTemplate to call the API
        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<JsonNode> response = restTemplate.exchange(
            		getCAFData,
                    HttpMethod.POST,
                    requestEntity,
                    JsonNode.class
            );

            // Check the response status and return the data
            if (response.getStatusCode() == HttpStatus.OK) {
                return response.getBody();
            } else {
                throw new RuntimeException("Failed to fetch CAF data, status: " + response.getStatusCode());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error while fetching CAF data: " + e.getMessage(), e);
        }
    }
    
    
    
    public String getToken() {
        // Define the request payload
    	String requestPayload = "{\"IntegrationKey\": \"UAT_LG\"}";
    	
        // Create headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Create the HTTP entity with headers and body
        HttpEntity<String> requestEntity = new HttpEntity<>(requestPayload, headers);

        // Use RestTemplate to call the API
        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<JsonNode> response = restTemplate.exchange(
                tokenhost,
                HttpMethod.POST,
                requestEntity,
                JsonNode.class
            );
            // Check the response status and extract the token
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                JsonNode responseBody = response.getBody();
                if (responseBody.has("token")) {
                    return responseBody.get("token").asText();
                } else {
                    throw new RuntimeException("Token not found in response");
                }
            } else {
                throw new RuntimeException("Failed to get token, status: " + response.getStatusCode());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error while fetching token: " + e.getMessage(), e);
            
        }
    }

}
