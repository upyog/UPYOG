package org.upyog.adapter.loader.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.upyog.adapter.model.RetryAttempt;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.upyog.adapter.common.constants.KafkaTopics;
import org.upyog.adapter.entity.DailyIngestionData;
import org.upyog.adapter.loader.Loader;
import org.upyog.adapter.model.DashboardData;
import org.upyog.adapter.model.DashboardPayload;
import org.upyog.adapter.model.IngestionResult;
import org.upyog.adapter.model.NationalDashboardIngestRequest;
import org.upyog.adapter.model.RequestInfo;
import org.upyog.adapter.model.UserInfo;
import org.upyog.adapter.producer.AdapterProducer;
import org.upyog.adapter.service.OAuthTokenService;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import lombok.extern.slf4j.Slf4j;

/**
 * HTTP-based implementation of {@link Loader} that sends transformed module
 * data to the National Dashboard ingest endpoint and asynchronously persists
 * the outcome via Kafka.
 *
 * <h3>Responsibilities</h3>
 * <ol>
 * <li>Obtain a valid OAuth token from {@link OAuthTokenService} and embed it in
 * the outbound {@link NationalDashboardIngestRequest}.</li>
 * <li>POST the request to the URL configured under
 * {@code national.dashboard.ingest.url}.</li>
 * <li>Return an {@link IngestionResult} that describes the outcome (status,
 * response body, or failure reason).</li>
 * <li>Regardless of success or failure, publish a {@link DailyIngestionData}
 * record to the {@code save-adapter-ingestion-detail} Kafka topic so the DIGIT
 * persister can write the audit row to the {@code ingestion_detail} database
 * table.</li>
 * </ol>
 *
 * <h3>Error handling</h3>
 * <ul>
 * <li>Any exception thrown during the HTTP call is caught, logged, and
 * reflected as a {@code FAILURE} status in the returned
 * {@link IngestionResult}.</li>
 * <li>Kafka publish failures inside {@link #pushIngestionRecord} are also
 * caught and only logged — they must never propagate and break the main
 * ingestion flow.</li>
 * </ul>
 *
 * <h3>Threading</h3> This component is a singleton Spring bean.
 * {@link RestTemplate} is assumed to be thread-safe (standard Spring
 * configuration).
 *
 * @see Loader
 * @see DailyIngestionData
 * @see AdapterProducer
 * @see KafkaTopics#SAVE_INGESTION_DETAIL
 */
/**
 * Class representing the HttpLoader class.
 * 
 * <p>Contributes to the core Property Tax metrics ingestion pipeline.
 */
@Slf4j
@Component
public class HttpLoader implements Loader {

	/**
	 * HTTP client used to POST the ingest request to the national dashboard.
	 * Injected as a Spring-managed singleton; thread-safe by default.
	 */
	@Autowired
	private RestTemplate restTemplate;

	/**
	 * Service that provides a valid OAuth2 bearer token and the corresponding
	 * {@link UserInfo} object required by the national dashboard endpoint.
	 */
	@Autowired
	private OAuthTokenService oAuthTokenService;

	/**
	 * Kafka producer used to publish {@link DailyIngestionData} audit records after
	 * each ingestion attempt.
	 */
	@Autowired
	private AdapterProducer producer;

	/**
	 * Fully-qualified URL of the national dashboard ingest endpoint. Resolved from
	 * the {@code national.dashboard.ingest.url} property in
	 * {@code application.properties}. Declared {@code public} to allow test
	 * overrides without reflection.
	 */
	@Value("${national.dashboard.ingest.url}")
	public String dashboardIngestUrl;

	@Value("${adapter.retry.max-attempts:3}")
	private int maxAttempts;

	@Value("${adapter.retry.base-delay-ms:1000}")
	private long baseDelayMs;

	@Value("${adapter.retry.max-delay-ms:5000}")
	private long maxDelayMs;

	/**
	 * Gson instance used for debug-level serialization of complex objects (e.g.
	 * {@link UserInfo}) before logging. Jackson's {@link ObjectMapper} is preferred
	 * for the actual HTTP payload.
	 */
	@Autowired
	private Gson gson;

	/**
	 * Jackson object mapper used to serialize the outbound
	 * {@link NationalDashboardIngestRequest} to a JSON string both for the HTTP
	 * body and for storing in the audit record.
	 */
	@Autowired
	private ObjectMapper objectMapper;

	// =========================================================================
	// Public API
	// =========================================================================

	/**
	 * Sends the provided module payload to the National Dashboard ingest endpoint
	 * and publishes the outcome as a Kafka audit record.
	 *
	 * <p>
	 * The method executes the following steps:
	 * <ol>
	 * <li>Calls {@link #buildRequest(DashboardPayload)} to construct a fully
	 * populated {@link NationalDashboardIngestRequest} including the OAuth token
	 * and user context.</li>
	 * <li>Serializes the request to JSON via {@link ObjectMapper} so the exact
	 * bytes sent over the wire are also stored in the audit record.</li>
	 * <li>POSTs the request to {@link #dashboardIngestUrl}.</li>
	 * <li>On success sets status {@code "SUCCESS"} and stores the response
	 * body.</li>
	 * <li>On any exception sets status {@code "FAILURE"} and stores the exception
	 * message as the response.</li>
	 * <li>In both cases calls {@link #pushIngestionRecord} to asynchronously
	 * persist the audit row via Kafka.</li>
	 * </ol>
	 *
	 * @param data the transformed module data to ingest; must not be {@code null};
	 *             the {@code Data} list should contain at least one
	 *             {@link DashboardData} entry so that ULB / module / date context
	 *             can be extracted for the audit record
	 * @return an {@link IngestionResult} with:
	 *         <ul>
	 *         <li>{@code ingestionStatus} — {@code "SUCCESS"} or
	 *         {@code "FAILURE"}</li>
	 *         <li>{@code responseData} — raw response body (success path)</li>
	 *         <li>{@code failureReason} — exception message (failure path)</li>
	 *         <li>{@code ingestedAt} — epoch millis at the time of return</li>
	 *         </ul>
	 */
	@Override
	public IngestionResult load(DashboardPayload data) {
		String requestJson = "";
		String responseJson = "";
		String status = "FAILURE";
		String failureReason = null;
		List<RetryAttempt> retryHistory = new ArrayList<>();

		DashboardData first = (data.getData() != null && !data.getData().isEmpty()) ? data.getData().get(0) : null;
		String dateStr = first != null ? first.getDate() : null;

		int attempt = 0;
		while (true) {
			attempt++;
			try {
				NationalDashboardIngestRequest payload = buildRequest(data);

				HttpHeaders headers = new HttpHeaders();
				headers.setContentType(MediaType.APPLICATION_JSON);

				requestJson = objectMapper.writeValueAsString(payload);
				log.info("HttpLoader | attempt {} request payload: {}", attempt, requestJson);

				HttpEntity<String> requestEntity = new HttpEntity<>(requestJson, headers);

				log.info("HttpLoader | attempt {} posting to: {}", attempt, dashboardIngestUrl);

				ResponseEntity<String> response = restTemplate.postForEntity(dashboardIngestUrl, requestEntity,
						String.class);
				System.out.println("Response of ingest API : " + response);
				responseJson = response.getBody();
				status = "SUCCESS";

				retryHistory.add(RetryAttempt.builder()
						.attemptNumber(attempt)
						.status("SUCCESS")
						.timestamp(System.currentTimeMillis())
						.build());

				IngestionResult result = IngestionResult.builder()
						.ingestionStatus(status)
						.responseData(responseJson)
						.date(dateStr)
						.ingestedAt(System.currentTimeMillis())
						.retryHistory(retryHistory)
						.build();

				pushIngestionRecord(data, requestJson, responseJson, status);

				return result;

			} catch (Exception e) {
				log.error("HttpLoader | attempt {} ingestion failed", attempt, e);

				String responseOrError = e.getMessage();
				if (e instanceof org.springframework.web.client.HttpStatusCodeException httpEx) {
					String body = httpEx.getResponseBodyAsString();
					if (body != null && !body.isBlank()) {
						responseOrError = body;
					}
				}
				failureReason = e.getMessage();

				retryHistory.add(RetryAttempt.builder()
						.attemptNumber(attempt)
						.status("FAILURE")
						.failureReason(failureReason)
						.timestamp(System.currentTimeMillis())
						.build());

				if (attempt >= maxAttempts) {
					log.error("HttpLoader | All {} ingestion attempts failed.", maxAttempts);
					pushIngestionRecord(data, requestJson, responseOrError, status);
					return IngestionResult.builder()
							.ingestionStatus(status)
							.failureReason(failureReason)
							.date(dateStr)
							.ingestedAt(System.currentTimeMillis())
							.retryHistory(retryHistory)
							.build();
				}

				long backoff = calculateBackoffWithJitter(attempt);
				log.warn("HttpLoader | Attempt {}/{} failed. Retrying in {} ms. Error: {}", 
						attempt, maxAttempts, backoff, failureReason);
				try {
					Thread.sleep(backoff);
				} catch (InterruptedException ie) {
					Thread.currentThread().interrupt();
					log.error("HttpLoader | Ingestion retry loop interrupted", ie);
					pushIngestionRecord(data, requestJson, responseOrError, status);
					return IngestionResult.builder()
							.ingestionStatus(status)
							.failureReason("Interrupted: " + ie.getMessage())
							.date(dateStr)
							.ingestedAt(System.currentTimeMillis())
							.retryHistory(retryHistory)
							.build();
				}
			}
		}
	}

	private long calculateBackoffWithJitter(int attempt) {
		int power = Math.min(attempt - 1, 30);
		long expDelay = baseDelayMs * (1L << power);
		if (expDelay < 0) {
			expDelay = maxDelayMs;
		}
		long currentMaxDelay = Math.min(maxDelayMs, expDelay);
		return java.util.concurrent.ThreadLocalRandom.current().nextLong(0, currentMaxDelay + 1);
	}


	/**
	 * Builds a {@link DailyIngestionData} audit record from the current call's
	 * context and publishes it to the {@code save-adapter-ingestion-detail} Kafka
	 * topic via {@link AdapterProducer}.
	 *
	 * <p>The payload is wrapped in a {@link Map} under the key {@code
	 * "dailyIngestionData"} to match the {@code basePath} configured in {@code
	 * adapter-service-persister.yml}: <pre>{@code { "dailyIngestionData": [ {
	 * ...DailyIngestionData fields... } ] } }</pre>
	 *
	 * <p>Context fields (ULB name, module name, push date) are extracted from the
	 * first element of {@link DashboardPayload#getData()}. When the list is empty
	 * or {@code null} these fields are left {@code null} in the audit record.
	 *
	 * <p>The {@code moduleDetailId} and {@code userId} fields are intentionally
	 * left {@code null}: {@code moduleDetailId} belongs to the higher-level service
	 * layer that owns the FK relationship, and {@code userId} is not available at
	 * this level when the call comes from a background scheduler. Callers that can
	 * supply these values should enrich the record before publishing, or extend
	 * this method signature.
	 *
	 * <p><strong>Any exception thrown inside this method is caught and logged so
	 * that a Kafka failure never interrupts the main ingestion flow.</strong>
	 *
	 * @param data the original {@link DashboardPayload} passed to {@link #load};
	 * used to extract context fields
	 * @param requestJson the JSON string that was sent (or attempted to be sent) to the national dashboard endpoint
	 * @param responseOrError the response body on success, or the exception message on failure
	 * @param status {@code "SUCCESS"} or {@code "FAILURE"}
	 */
	private void pushIngestionRecord(DashboardPayload data, String requestJson, String responseOrError, String status) {
		try {
			// Extract the first data item for context fields (ulb, module, date).
			// Each load() call corresponds to a single module/ulb/date combination.
			DashboardData first = (data.getData() != null && !data.getData().isEmpty()) ? data.getData().get(0) : null;

			long now = System.currentTimeMillis();

			DailyIngestionData record = DailyIngestionData.builder().moduleIngestionId(UUID.randomUUID().toString())
					// moduleDetailId is set by the calling service layer that owns the
					// module_ingestion_detail FK; leave blank here so callers can enrich it.
					.moduleDetailId(null).tenantId(first != null ? first.getUlb() : null)
					.moduleName(first != null ? first.getModule() : null)
					.pushDate(first != null ? first.getDate() : null)
					.requestData(toJsonString(requestJson)).responseData(toJsonString(responseOrError)).ingestionStatus(status).createdBy("SYSTEM")
					.createdTime(now).lastModifiedBy("SYSTEM").lastModifiedTime(now).build();

			Map<String, Object> kafkaMessage = new HashMap<>();
			kafkaMessage.put("dailyIngestionData", Collections.singletonList(record));

			producer.push(KafkaTopics.SAVE_INGESTION_DETAIL, kafkaMessage);

		} catch (Exception ex) {
			// Kafka failure must never break the main ingestion flow.
			log.error("HttpLoader | failed to push ingestion record to Kafka", ex);
		}
	}

	private String toJsonString(String input) {
		if (input == null || input.isBlank()) {
			return "{}";
		}
		try {
			com.fasterxml.jackson.databind.JsonNode node = objectMapper.readTree(input);
			if (node != null && (node.isObject() || node.isArray())) {
				return input;
			}
		} catch (Exception ignored) {
		}
		try {
			return objectMapper.writeValueAsString(Map.of("error", input));
		} catch (Exception ex) {
			return "{\"error\":\"" + input.replace("\"", "\\\"").replace("\n", " ") + "\"}";
		}
	}

	/**
	 * Constructs the {@link NationalDashboardIngestRequest} that will be POSTed to
	 * the national dashboard endpoint.
	 *
	 * <p>
	 * The method performs the following steps:
	 * <ol>
	 * <li>Fetches a fresh OAuth2 bearer token from
	 * {@link OAuthTokenService#getToken()}.</li>
	 * <li>Fetches the corresponding {@link UserInfo} from
	 * {@link OAuthTokenService#getUserInfo()}.</li>
	 * <li>Builds a {@link RequestInfo} with the standard Rainmaker API ID, the
	 * bearer token, the user object, and a timestamp-based message ID in the format
	 * {@code "<epochMillis>|en_IN"}.</li>
	 * <li>Returns a {@link NationalDashboardIngestRequest} containing the
	 * {@link RequestInfo} and the module {@code Data} list from the payload.</li>
	 * </ol>
	 *
	 * @param data the module payload whose {@link DashboardPayload#getData()} list
	 *             will be included verbatim in the outbound request
	 * @return a fully populated {@link NationalDashboardIngestRequest} ready to be
	 *         serialized and sent over HTTP
	 */
	private NationalDashboardIngestRequest buildRequest(DashboardPayload data) {
		String token = oAuthTokenService.getToken();
		UserInfo info = oAuthTokenService.getUserInfo();

		log.debug("HttpLoader | UserInfo: {}", info != null ? gson.toJson(info) : "null");

		RequestInfo requestInfo = RequestInfo.builder().apiId("Rainmaker").authToken(token).userInfo(info)
				.msgId(System.currentTimeMillis() + "|en_IN").build();

		return NationalDashboardIngestRequest.builder().requestInfo(requestInfo).data(data.getData()).build();
	}
}
