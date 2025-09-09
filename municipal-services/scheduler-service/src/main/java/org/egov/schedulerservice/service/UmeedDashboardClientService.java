package org.egov.schedulerservice.service;

import org.egov.schedulerservice.request.UmeedDashboardRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UmeedDashboardClientService {

	@Value("${umeed.dashboard.api.url}")
	private String dashboardUrl;

	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	private ObjectMapper objectMapper;

	public String sendMetrics(UmeedDashboardRequest dashboardRequest) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);

			HttpEntity<UmeedDashboardRequest> requestEntity = new HttpEntity<>(dashboardRequest, headers);

			String responseBody = restTemplate.postForObject(dashboardUrl, requestEntity, String.class);

			if (responseBody != null) {
				JsonNode root = objectMapper.readTree(responseBody);

				if (root.has("Errors")) {
					JsonNode error = root.get("Errors").get(0);
					log.error("Error Code: " + error.get("code").asText());
					log.error("Error Message: " + error.get("message").asText());
					return ("Error Message: " + error.get("message").asText());
				} else if (root.has("responseHash")) {
					log.info("Ingest Success, Hash: " + root.get("responseHash"));
					return ("Ingest Success, Hash: " + root.get("responseHash"));
				} else {
					log.error("Unexpected response: " + responseBody);
					return ("Unexpected response: " + responseBody);
				}
			} else {
				log.error("Empty response from server");
				return ("Empty response from server");
			}

		} catch (Exception e) {
//			e.printStackTrace();
			log.error("Error sending metrics: " + e.getMessage());
			return ("Error sending metrics: " + e.getMessage());
		}
	}

}
