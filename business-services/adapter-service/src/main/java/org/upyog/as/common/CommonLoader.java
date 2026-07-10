package org.upyog.as.common;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.upyog.as.core.IngestionResult;
import org.upyog.as.core.loader.Loader;
import org.upyog.as.model.payload.ModuleData;
import org.upyog.as.model.payload.NationalDashboardIngestRequest;
import org.upyog.as.model.payload.RequestInfo;
import org.upyog.as.model.payload.UserInfo;
import org.upyog.as.service.OAuthTokenService;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

/**
 * Sends transformed module data to the national dashboard ingest endpoint.
 */
@Component
public class CommonLoader implements Loader {

	@Autowired
	private RestTemplate restTemplate;
	@Autowired
	private OAuthTokenService oAuthTokenService;

	@Value("${national.dashboard.ingest.url}")
	public String dashboardIngestUrl;

	@Autowired
	private Gson gson;

	@Autowired
	private ObjectMapper objectMapper;

	/**
	 * Sends the provided module payload to the configured ingest endpoint.
	 *
	 * @param data the transformed module data to ingest
	 * @return the ingestion outcome with response details
	 */
	@Override
	public IngestionResult load(ModuleData data) {

		try {
			NationalDashboardIngestRequest payload = buildRequest(data);

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_JSON);

			System.out.println("payload : " + gson.toJson(payload));

			System.out.println("payload (Jackson - what's ACTUALLY sent): " + objectMapper.writeValueAsString(payload));

			HttpEntity<NationalDashboardIngestRequest> requestEntity = new HttpEntity<>(payload, headers);

			System.out.println("dashboardIngestUrl : " + dashboardIngestUrl);
			ResponseEntity<String> response = restTemplate.postForEntity(dashboardIngestUrl, requestEntity,
					String.class);

			return IngestionResult.builder().ingestionStatus("SUCCESS").responseData(response.getBody())
					.ingestedAt(System.currentTimeMillis()).build();

		} catch (Exception e) {
			e.printStackTrace();
			return IngestionResult.builder().ingestionStatus("FAILURE").failureReason(e.getMessage())
					.ingestedAt(System.currentTimeMillis()).build();
		}
	}

/**
	 * Builds the request payload for a single module record.
	 *
	 * @param data the module data to package for ingestion
	 * @return the fully populated ingest request
	 */
	private NationalDashboardIngestRequest buildRequest(ModuleData data) {
		String token = oAuthTokenService.getToken();
		UserInfo info = oAuthTokenService.getUserInfo();

		System.out.println("UserInfo: " + (info != null ? gson.toJson(info) : "null"));

		RequestInfo requestInfo = RequestInfo.builder().apiId("Rainmaker").authToken(token).userInfo(info)
				.msgId(System.currentTimeMillis() + "|en_IN").build();

		data.setWard("Block 4");
		data.setRegion("TEST");
		data.setState("PG");

		return NationalDashboardIngestRequest.builder().RequestInfo(requestInfo).Data(List.of(data)).build();
	}
}