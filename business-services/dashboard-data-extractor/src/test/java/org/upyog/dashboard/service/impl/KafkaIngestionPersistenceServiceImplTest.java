package org.upyog.dashboard.service.impl;

import org.upyog.dashboard.constants.DashboardExtractorConstants;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

import java.time.LocalDate;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.upyog.dashboard.config.DashboardProperties;
import org.junit.jupiter.api.BeforeEach;
import static org.mockito.Mockito.when;
import org.upyog.dashboard.producer.DashboardProducer;

@ExtendWith(MockitoExtension.class)
class KafkaIngestionPersistenceServiceImplTest {

    @Mock
    private DashboardProducer producer;

    @Mock
    private DashboardProperties dashboardProperties;

    @InjectMocks
    private KafkaIngestionPersistenceServiceImpl service;

    @BeforeEach
    void setUp() {
        org.mockito.Mockito.lenient().when(dashboardProperties.getSaveIngestionDetailTopic()).thenReturn("save-dashboard-ingestion-detail");
        org.mockito.Mockito.lenient().when(dashboardProperties.getSaveLegacyIngestionDetailTopic()).thenReturn("save-dashboard-data-module-ingestion-detail");
        org.mockito.Mockito.lenient().when(dashboardProperties.getUpdateLegacyIngestionDetailTopic()).thenReturn("update-dashboard-data-module-ingestion-detail");
        org.mockito.Mockito.lenient().when(dashboardProperties.getSaveAdapterErrorLogTopic()).thenReturn("save-dashboard-data-error-log");
        org.mockito.Mockito.lenient().when(dashboardProperties.getUpdateAdapterModuleSummaryTopic()).thenReturn("update-dashboard-module-summary");
    }

    @Test
    @DisplayName("saveOrUpdateLastSuccessfulDate pushes to Kafka topic")
    void saveOrUpdateLastSuccessfulDate_pushesToKafka() {
        LocalDate targetDate = LocalDate.of(2026, 7, 1);
        service.saveOrUpdateLastSuccessfulDate("pg", "PT", targetDate);

        verify(producer).push(
                eq("update-dashboard-module-summary"),
                any(Map.class)
        );
    }

    @Test
    @DisplayName("saveOrUpdateLastAttemptedDate pushes to Kafka topic")
    void saveOrUpdateLastAttemptedDate_pushesToKafka() {
        LocalDate targetDate = LocalDate.of(2026, 7, 1);
        service.saveOrUpdateLastAttemptedDate("pg", "PT", targetDate);

        verify(producer).push(
                eq("update-dashboard-module-summary"),
                any(Map.class)
        );
    }

    @Test
    @DisplayName("createLegacyJob pushes to Kafka topic")
    void createLegacyJob_pushesToKafka() {
        LocalDate targetDate = LocalDate.of(2026, 7, 1);
        service.createLegacyJob("job-123", "pg", "PT", targetDate, targetDate, targetDate);

        verify(producer).push(
                eq("save-dashboard-data-module-ingestion-detail"),
                any(Map.class)
        );
    }

    @Test
    @DisplayName("updateLegacyJobStatus pushes to Kafka topic")
    void updateLegacyJobStatus_pushesToKafka() {
        service.updateLegacyJobStatus("job123", DashboardExtractorConstants.STATUS_SUCCESS, "{}", "{}");

        verify(producer).push(
                eq("update-dashboard-data-module-ingestion-detail"),
                any(Map.class)
        );
    }
}
