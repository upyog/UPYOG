package org.upyog.dashboard.loader.impl;

import org.apache.commons.lang3.StringUtils;
import org.upyog.dashboard.client.DashboardFeignClient;
import org.upyog.dashboard.config.DashboardProperties;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.upyog.dashboard.entity.DailyIngestionData;
import org.upyog.dashboard.loader.DashboardDataLoader;
import org.upyog.dashboard.model.DashboardData;
import org.upyog.dashboard.model.DashboardPayload;
import org.upyog.dashboard.model.IngestionResult;
import org.upyog.dashboard.model.NationalDashboardIngestRequest;
import org.upyog.dashboard.model.RequestInfo;
import org.upyog.dashboard.model.RetryAttempt;
import org.upyog.dashboard.model.UserInfo;
import org.upyog.dashboard.producer.DashboardProducer;
import org.upyog.dashboard.service.AuditService;
import org.upyog.dashboard.service.OAuthTokenService;
import org.upyog.dashboard.util.RetryUtil;
import org.upyog.dashboard.util.CommonUtils;

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
 * record to the {@code save-dashboard-ingestion-detail} Kafka topic so the * persister can write the audit row to the {@code ingestion_detail} database
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
 * @see DashboardProducer
 */
/**
 * Class representing the DashboardDataLoaderImpl class.
 * 
 * <p>Contributes to the core Property Tax metrics ingestion pipeline.
 */
@Slf4j
@Component("httpDataLoader")
public class DashboardDataLoaderImpl implements DashboardDataLoader {

	/**
	 * HTTP client used to POST the ingest request to the national dashboard.
	 * Injected as a Spring-managed singleton; thread-safe by default.
	 */
	@Autowired
	private DashboardFeignClient dashboardFeignClient;

	/**
	 * Service that provides a valid OAuth2 bearer token and the corresponding
	 * {@link UserInfo} object required by the national dashboard endpoint.
	 */
	@Autowired
	private AuditService auditService;

	@Autowired
	private OAuthTokenService oAuthTokenService;

	@Autowired
	private DashboardProperties dashboardProperties;

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
	 *             the {@code Data} dataList should contain at least one
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
	public IngestionResult load(DashboardPayload dashboardPayload) {
		String requestPayloadJson = "";
		String responsePayloadJson = "";
		String currentIngestionStatus = "FAILURE";
		String failureReasonMessage = null;
		List<RetryAttempt> retryAttemptHistory = new ArrayList<>();

		int payloadDataSize = (dashboardPayload.getData() != null) ? dashboardPayload.getData().size() : 0;
		DashboardData firstDashboardDataElement = (payloadDataSize > 0) ? dashboardPayload.getData().get(0) : null;
		String ingestionDateString = firstDashboardDataElement != null ? firstDashboardDataElement.getDate() : null;

		int retryAttemptCount = 0;
		while (true) {
			retryAttemptCount++;
			try {
				NationalDashboardIngestRequest dashboardIngestRequest = buildRequest(dashboardPayload);

				requestPayloadJson = objectMapper.writeValueAsString(dashboardIngestRequest);
				log.info("DashboardDataLoaderImpl | attempt {} request payload: {}", retryAttemptCount, requestPayloadJson);

				log.info("DashboardDataLoaderImpl | attempt {} posting to: {}", retryAttemptCount, dashboardProperties.getDashboardIngestUrl());

				responsePayloadJson = dashboardFeignClient.ingestMetrics(
						java.net.URI.create(dashboardProperties.getDashboardIngestUrl()),
						requestPayloadJson
				);
				log.debug("DashboardDataLoaderImpl | Response of ingest API : {}", responsePayloadJson);
				currentIngestionStatus = "SUCCESS";

				retryAttemptHistory.add(RetryAttempt.builder()
						.attemptNumber(retryAttemptCount)
						.status("SUCCESS")
						.timestamp(CommonUtils.getCurrentEpochMillis())
						.build());

				IngestionResult ingestionResult = IngestionResult.builder()
						.ingestionStatus(currentIngestionStatus)
						.responseData(responsePayloadJson)
						.date(ingestionDateString)
						.moduleName(firstDashboardDataElement != null ? firstDashboardDataElement.getModule() : null)
						.ingestedAt(CommonUtils.getCurrentEpochMillis())
						.retryHistory(retryAttemptHistory)
						.build();

				pushIngestionRecord(dashboardPayload, requestPayloadJson, responsePayloadJson, currentIngestionStatus);

				return ingestionResult;

			} catch (Exception exception) {
				log.error("DashboardDataLoaderImpl | attempt {} ingestion failed", retryAttemptCount, exception);

				String responseOrErrorMessage = exception.getMessage();
				if (exception instanceof feign.FeignException feignException) {
					String feignExceptionContent = feignException.contentUTF8();
					if (StringUtils.isNotBlank(feignExceptionContent)) {
						responseOrErrorMessage = feignExceptionContent;
					}
				}
				failureReasonMessage = exception.getMessage();

				retryAttemptHistory.add(RetryAttempt.builder()
						.attemptNumber(retryAttemptCount)
						.status("FAILURE")
						.failureReason(failureReasonMessage)
						.timestamp(CommonUtils.getCurrentEpochMillis())
						.build());

				if (retryAttemptCount >= dashboardProperties.getIngestMaxAttempts() || !dashboardProperties.isIngestRetryEnabled()) {
					log.error("DashboardDataLoaderImpl | Ingestion attempts failed. Retry enabled: {}", dashboardProperties.isIngestRetryEnabled());
					pushIngestionRecord(dashboardPayload, requestPayloadJson, responseOrErrorMessage, currentIngestionStatus);
					return IngestionResult.builder()
							.ingestionStatus(currentIngestionStatus)
							.failureReason(failureReasonMessage)
							.date(ingestionDateString)
							.moduleName(firstDashboardDataElement != null ? firstDashboardDataElement.getModule() : null)
							.ingestedAt(CommonUtils.getCurrentEpochMillis())
							.retryHistory(retryAttemptHistory)
							.build();
				}

				long retryBackoffDurationMs = RetryUtil.calculateBackoffWithJitter(retryAttemptCount, dashboardProperties.getIngestBaseDelayMs(), dashboardProperties.getIngestMaxDelayMs());
				log.warn("DashboardDataLoaderImpl | Attempt {}/{} failed. Retrying in {} ms. Error: {}", 
						retryAttemptCount, dashboardProperties.getIngestMaxAttempts(), retryBackoffDurationMs, failureReasonMessage);
				try {
					Thread.sleep(retryBackoffDurationMs);
				} catch (InterruptedException interruptedException) {
					Thread.currentThread().interrupt();
					log.error("DashboardDataLoaderImpl | Ingestion retry loop interrupted", interruptedException);
					pushIngestionRecord(dashboardPayload, requestPayloadJson, responseOrErrorMessage, currentIngestionStatus);
					return IngestionResult.builder()
							.ingestionStatus(currentIngestionStatus)
							.failureReason("Interrupted: " + interruptedException.getMessage())
							.date(ingestionDateString)
							.moduleName(firstDashboardDataElement != null ? firstDashboardDataElement.getModule() : null)
							.ingestedAt(CommonUtils.getCurrentEpochMillis())
							.retryHistory(retryAttemptHistory)
							.build();
				}
			}
		}
	}



	/**
	 * Builds a {@link DailyIngestionData} audit record from the current call's
	 * context and publishes it to the {@code save-dashboard-ingestion-detail} Kafka
	 * topic via {@link DashboardProducer}.
	 *
	 * <p>The payload is wrapped in a {@link Map} under the key {@code
	 * "dailyIngestionData"} to match the {@code basePath} configured in {@code
	 * adapter-service-persister.yml}: <pre>{@code { "dailyIngestionData": [ {
	 * ...DailyIngestionData fields... } ] } }</pre>
	 *
	 * <p>Context fields (ULB name, module name, push date) are extracted from the
	 * first element of {@link DashboardPayload#getData()}. When the dataList is empty
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
	 * @param dashboardPayload the original {@link DashboardPayload} passed to {@link #load};
	 * used to extract context fields
	 * @param requestPayloadJson the JSON string that was sent (or attempted to be sent) to the national dashboard endpoint
	 * @param responseOrErrorMessage the response body on success, or the exception message on failure
	 * @param currentIngestionStatus {@code "SUCCESS"} or {@code "FAILURE"}
	 */
	private void pushIngestionRecord(DashboardPayload dashboardPayload, String requestPayloadJson, String responseOrErrorMessage, String currentIngestionStatus) {
		try {
			auditService.pushIngestionRecord(dashboardPayload, requestPayloadJson, responseOrErrorMessage, currentIngestionStatus);
		} catch (Exception exception) {
			log.error("DashboardDataLoaderImpl | failed to push ingestion record to audit service", exception);
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
	 * {@link RequestInfo} and the module {@code Data} dataList from the payload.</li>
	 * </ol>
	 *
	 * @param dashboardPayload the module payload whose {@link DashboardPayload#getData()} dataList
	 *             will be included verbatim in the outbound request
	 * @return a fully populated {@link NationalDashboardIngestRequest} ready to be
	 *         serialized and sent over HTTP
	 */
	private NationalDashboardIngestRequest buildRequest(DashboardPayload dashboardPayload) {
		String oauthToken = oAuthTokenService.getToken();
		UserInfo systemUserInfo = oAuthTokenService.getUserInfo();

		log.debug("DashboardDataLoaderImpl | UserInfo: {}", systemUserInfo != null ? gson.toJson(systemUserInfo) : "null");

		RequestInfo dashboardRequestInfo = RequestInfo.builder().apiId("Rainmaker").authToken(oauthToken).userInfo(systemUserInfo)
				.msgId(CommonUtils.getCurrentEpochMillis() + "|en_IN").build();

		return NationalDashboardIngestRequest.builder().requestInfo(dashboardRequestInfo).data(dashboardPayload.getData()).build();
	}
}
