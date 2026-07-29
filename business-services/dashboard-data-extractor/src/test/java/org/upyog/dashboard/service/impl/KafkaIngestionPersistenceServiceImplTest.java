package org.upyog.dashboard.service.impl;

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
import org.upyog.dashboard.common.constants.KafkaTopics;
import org.upyog.dashboard.producer.DashboardProducer;

@ExtendWith(MockitoExtension.class)
class KafkaIngestionPersistenceServiceImplTest {

    @Mock
    private DashboardProducer producer;

    @InjectMocks
    private KafkaIngestionPersistenceServiceImpl service;

    @Test
    @DisplayName("saveOrUpdateLastSuccessfulDate pushes to Kafka topic")
    void saveOrUpdateLastSuccessfulDate_pushesToKafka() {
        LocalDate targetDate = LocalDate.of(2026, 7, 1);
        service.saveOrUpdateLastSuccessfulDate("pg", "PT", targetDate);

        verify(producer).push(
                eq(KafkaTopics.UPDATE_ADAPTER_MODULE_SUMMARY),
                any(Map.class)
        );
    }

    @Test
    @DisplayName("saveOrUpdateLastAttemptedDate pushes to Kafka topic")
    void saveOrUpdateLastAttemptedDate_pushesToKafka() {
        LocalDate targetDate = LocalDate.of(2026, 7, 1);
        service.saveOrUpdateLastAttemptedDate("pg", "PT", targetDate);

        verify(producer).push(
                eq(KafkaTopics.UPDATE_ADAPTER_MODULE_SUMMARY),
                any(Map.class)
        );
    }

    @Test
    @DisplayName("createLegacyJob pushes to Kafka topic")
    void createLegacyJob_pushesToKafka() {
        LocalDate targetDate = LocalDate.of(2026, 7, 1);
        service.createLegacyJob("pg", "PT", targetDate);

        verify(producer).push(
                eq(KafkaTopics.SAVE_LEGACY_INGESTION_DETAIL),
                any(Map.class)
        );
    }

    @Test
    @DisplayName("updateLegacyJobStatus pushes to Kafka topic")
    void updateLegacyJobStatus_pushesToKafka() {
        service.updateLegacyJobStatus("job123", "SUCCESS", "{}", "{}");

        verify(producer).push(
                eq(KafkaTopics.UPDATE_LEGACY_INGESTION_DETAIL),
                any(Map.class)
        );
    }
}
