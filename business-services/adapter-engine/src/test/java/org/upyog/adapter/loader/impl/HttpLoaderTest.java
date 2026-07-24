package org.upyog.adapter.loader.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.upyog.adapter.common.constants.KafkaTopics;
import org.upyog.adapter.config.AdapterProperties;
import org.upyog.adapter.model.DashboardData;
import org.upyog.adapter.model.DashboardPayload;
import org.upyog.adapter.model.IngestionResult;
import org.upyog.adapter.model.UserInfo;
import org.upyog.adapter.producer.AdapterProducer;
import org.upyog.adapter.service.OAuthTokenService;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link HttpLoader}.
 */
@ExtendWith(MockitoExtension.class)
class HttpLoaderTest {

    @Mock
    private org.upyog.adapter.client.DashboardFeignClient dashboardFeignClient;

    @Mock
    private OAuthTokenService oAuthTokenService;

    @Mock
    private AdapterProducer producer;

    @Mock
    private AdapterProperties adapterProperties;

    private final Gson gson = new Gson();

    private ObjectMapper objectMapper;

    private HttpLoader loader;

    @BeforeEach
    void setUp() throws Exception {
        objectMapper = new ObjectMapper();
        loader = new HttpLoader();
        setField(loader, "dashboardFeignClient", dashboardFeignClient);
        setField(loader, "oAuthTokenService", oAuthTokenService);
        setField(loader, "producer", producer);
        setField(loader, "gson", gson);
        setField(loader, "objectMapper", objectMapper);
        setField(loader, "adapterProperties", adapterProperties);

        lenient().when(adapterProperties.getDashboardIngestUrl()).thenReturn("http://localhost:8080/national-dashboard/metric/_ingest");
        lenient().when(adapterProperties.getIngestMaxAttempts()).thenReturn(3);
        lenient().when(adapterProperties.getIngestBaseDelayMs()).thenReturn(1L);
        lenient().when(adapterProperties.getIngestMaxDelayMs()).thenReturn(2L);
    }

    @Test
    @DisplayName("Load with valid payload returns SUCCESS")
    void load_withValidPayload_returnsSuccess() throws Exception {
        when(oAuthTokenService.getToken()).thenReturn("test-token");
        when(oAuthTokenService.getUserInfo()).thenReturn(new UserInfo());

        String responseBody = "{\"status\": \"ok\"}";
        when(dashboardFeignClient.ingestMetrics(
                any(java.net.URI.class),
                anyString()))
                .thenReturn(responseBody);

        DashboardPayload payload = createValidPayload();
        IngestionResult result = loader.load(payload);

        assertThat(result.getIngestionStatus()).isEqualTo("SUCCESS");
        assertThat(result.getResponseData()).isEqualTo("{\"status\": \"ok\"}");
        assertThat(result.getFailureReason()).isNull();
        assertThat(result.getIngestedAt()).isGreaterThan(0);

        verify(producer).push(eq(KafkaTopics.SAVE_INGESTION_DETAIL), any(Map.class));
    }

    @Test
    @DisplayName("Load when HTTP call fails returns FAILURE")
    void load_whenHttpCallFails_returnsFailure() {
        when(oAuthTokenService.getToken()).thenReturn("test-token");
        when(oAuthTokenService.getUserInfo()).thenReturn(new UserInfo());

        when(dashboardFeignClient.ingestMetrics(
                any(java.net.URI.class),
                anyString()))
                .thenThrow(new RuntimeException("Connection refused"));

        DashboardPayload payload = createValidPayload();
        IngestionResult result = loader.load(payload);

        assertThat(result.getIngestionStatus()).isEqualTo("FAILURE");
        assertThat(result.getFailureReason()).isEqualTo("Connection refused");
        verify(producer).push(anyString(), any(Map.class));
    }

    @Test
    @DisplayName("Kafka producer failure does not break main flow")
    void kafkaFailure_doesNotBreakMainFlow() throws Exception {
        when(oAuthTokenService.getToken()).thenReturn("test-token");
        when(oAuthTokenService.getUserInfo()).thenReturn(new UserInfo());

        when(dashboardFeignClient.ingestMetrics(
                any(java.net.URI.class),
                anyString()))
                .thenReturn("{\"status\": \"ok\"}");

        doThrow(new RuntimeException("Kafka unavailable"))
                .when(producer).push(anyString(), any());

        DashboardPayload payload = createValidPayload();
        IngestionResult result = loader.load(payload);

        assertThat(result.getIngestionStatus()).isEqualTo("SUCCESS");
    }

    @Test
    @DisplayName("Load when OAuth fails returns FAILURE")
    void load_whenOAuthFails_returnsFailure() {
        when(oAuthTokenService.getToken()).thenThrow(new RuntimeException("OAuth failed"));

        DashboardPayload payload = createValidPayload();
        IngestionResult result = loader.load(payload);

        assertThat(result.getIngestionStatus()).isEqualTo("FAILURE");
        assertThat(result.getFailureReason()).isEqualTo("OAuth failed");
        verify(dashboardFeignClient, never()).ingestMetrics(any(), any());
    }

    @Test
    @DisplayName("Load retries on transient HTTP failures and succeeds eventually")
    void load_retrySucceedsEventually_returnsSuccessWithRetryHistory() throws Exception {
        when(oAuthTokenService.getToken()).thenReturn("test-token");
        when(oAuthTokenService.getUserInfo()).thenReturn(new UserInfo());

        // Fail first attempt, succeed on second
        when(dashboardFeignClient.ingestMetrics(any(java.net.URI.class), anyString()))
                .thenThrow(new RuntimeException("Transient Connection Timeout"))
                .thenReturn("{\"status\": \"ok\"}");

        DashboardPayload payload = createValidPayload();
        IngestionResult result = loader.load(payload);

        assertThat(result.getIngestionStatus()).isEqualTo("SUCCESS");
        assertThat(result.getResponseData()).isEqualTo("{\"status\": \"ok\"}");
        assertThat(result.getDate()).isEqualTo("2024-01-15");
        assertThat(result.getRetryHistory()).hasSize(2);
        
        assertThat(result.getRetryHistory().get(0).getAttemptNumber()).isEqualTo(1);
        assertThat(result.getRetryHistory().get(0).getStatus()).isEqualTo("FAILURE");
        assertThat(result.getRetryHistory().get(0).getFailureReason()).isEqualTo("Transient Connection Timeout");

        assertThat(result.getRetryHistory().get(1).getAttemptNumber()).isEqualTo(2);
        assertThat(result.getRetryHistory().get(1).getStatus()).isEqualTo("SUCCESS");
        assertThat(result.getRetryHistory().get(1).getFailureReason()).isNull();

        verify(dashboardFeignClient, times(2)).ingestMetrics(any(java.net.URI.class), anyString());
    }

    @Test
    @DisplayName("Load retries up to maxAttempts and returns last failure when all fail")
    void load_allAttemptsFail_returnsFailureWithRetryHistory() throws Exception {
        when(oAuthTokenService.getToken()).thenReturn("test-token");
        when(oAuthTokenService.getUserInfo()).thenReturn(new UserInfo());

        when(dashboardFeignClient.ingestMetrics(any(java.net.URI.class), anyString()))
                .thenThrow(new RuntimeException("Connection Timeout"));

        DashboardPayload payload = createValidPayload();
        IngestionResult result = loader.load(payload);

        assertThat(result.getIngestionStatus()).isEqualTo("FAILURE");
        assertThat(result.getFailureReason()).isEqualTo("Connection Timeout");
        assertThat(result.getDate()).isEqualTo("2024-01-15");
        assertThat(result.getRetryHistory()).hasSize(3);

        for (int i = 0; i < 3; i++) {
            assertThat(result.getRetryHistory().get(i).getAttemptNumber()).isEqualTo(i + 1);
            assertThat(result.getRetryHistory().get(i).getStatus()).isEqualTo("FAILURE");
            assertThat(result.getRetryHistory().get(i).getFailureReason()).isEqualTo("Connection Timeout");
        }

        verify(dashboardFeignClient, times(3)).ingestMetrics(any(java.net.URI.class), anyString());
    }

    @Test
    @DisplayName("HttpLoader implements Loader interface and has adapterProperties field")
    void loader_structure() throws Exception {
        assertThat(loader).isInstanceOf(org.upyog.adapter.loader.Loader.class);
        assertThat(HttpLoader.class.getDeclaredField("adapterProperties")).isNotNull();
    }

    private static DashboardPayload createValidPayload() {
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("assessments", 100);

        DashboardData data = DashboardData.builder()
                .module("PT")
                .ulb("pb.amritsar")
                .date("2024-01-15")
                .state("Punjab")
                .ward("ward-1")
                .region("pb")
                .metrics(metrics)
                .build();

        return DashboardPayload.builder()
                .data(Collections.singletonList(data))
                .build();
    }

    private static void setField(Object target, String fieldName, Object value) throws Exception {
        java.lang.reflect.Field field = target.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }
}