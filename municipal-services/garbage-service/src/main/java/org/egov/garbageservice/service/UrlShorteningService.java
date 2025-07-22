package org.egov.garbageservice.service;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.egov.garbageservice.util.GrbgConstants;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UrlShorteningService {
	
	@Autowired
	private RestTemplate restTemplate;
	
	@Autowired
	private GrbgConstants grbgConfig;
	
	
	public String getShortUrl(String Url,Boolean returnQueryParam) {
		String url = grbgConfig.getUrlShortningHost() +grbgConfig.getUrlShortningContextPath()+grbgConfig.getUrlShortenEndpoint();

		// 1. Create headers
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		// 2. Create request body
		Map<String, String> body = new HashMap<>();
		body.put("url", Url);

		// 3. Combine headers and body
		HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

		// 4. Execute POST request
		ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

		// 5. Get response (assuming the short URL is returned in the response body)
		String responseBody = response.getBody();
		
		String uri = null;
		if(returnQueryParam)
			uri = getUriParam(responseBody);
		else
			uri = responseBody;
		
//		System.out.println("Shortened URL: " + responseBody+" / "+ uri);

		return uri;

	}
	
	private String getUriParam(String uri) {
		 if (uri == null || !uri.contains("/egov-url-shortening/")) {
	            throw new IllegalArgumentException("Invalid short URL format.");
	        }

	        // Extract the ID (last segment)
	        String[] parts = uri.split("/egov-url-shortening/");
	        if (parts.length != 2 || parts[1].isEmpty()) {
	            throw new IllegalArgumentException("Unable to extract short ID from URL.");
	        }

	        String id = parts[1];
	        return parts[0] + "/egov-url-shortening?id=" + id;
//		return null;
	}

}
