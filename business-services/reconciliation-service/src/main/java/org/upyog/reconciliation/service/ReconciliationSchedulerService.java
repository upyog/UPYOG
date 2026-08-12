package org.upyog.reconciliation.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.upyog.reconciliation.extractor.DataExtractorFactory;
import org.upyog.reconciliation.extractor.MetricDataExtractor;
import org.upyog.reconciliation.model.ReconConfiguration;
import org.upyog.reconciliation.model.ReconExtractionDetail;
import org.upyog.reconciliation.repository.ReconConfigurationRepository;
import org.upyog.reconciliation.repository.ReconExtractionDetailRepository;
import org.upyog.reconciliation.producer.ReconciliationProducer;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class ReconciliationSchedulerService {

    @Autowired
    private ReconConfigurationRepository configurationRepository;

    @Autowired
    private ReconExtractionDetailRepository detailRepository;

    @Autowired
    private DataExtractorFactory extractorFactory;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${reconciliation.data-source.type}")
    private String dataSourceType;

    @Value("${reconciliation.kafka.create.topic}")
    private String saveTopic;

    @Autowired
    private ReconciliationProducer producer;

    // Runs at 10:00 AM UTC every day (which is 3:30 PM IST)
    @Scheduled(cron = "${reconciliation.job.cron}")
    @Transactional
    public void executeDailyExtraction() {
        executeExtractionForDate(LocalDate.now());
    }

    @Transactional
    public void executeExtractionForDate(LocalDate extractionDate) {
        log.info("Starting reconciliation extraction job for date: {}", extractionDate);

        List<ReconConfiguration> activeConfigs = configurationRepository.findByIsActiveTrue();

        for (ReconConfiguration config : activeConfigs) {
            log.info("Processing configuration for Client: {}, Module: {}, Tenant: {}",
                    config.getClientName(), config.getModuleName(), config.getTenantId());

            ReconExtractionDetail detail = new ReconExtractionDetail();
            detail.setConfiguration(config);
            detail.setExtractionDate(extractionDate);

            try {
                MetricDataExtractor extractor = extractorFactory.getExtractor(dataSourceType);
                List<Map<String, Object>> extractedData = extractor.extractData(config.getTenantId(), config.getModuleName(), extractionDate);

                detail.setStatus("SUCCESS");
                detail.setDataPayload(objectMapper.writeValueAsString(extractedData));

                // Update configuration last extraction date
                config.setLastExtractionDate(extractionDate);
                configurationRepository.save(config);

            } catch (JsonProcessingException e) {
                log.error("Failed to serialize extracted data to JSON", e);
                detail.setStatus("FAILURE");
                detail.setErrorMessage("JSON Serialization Error: " + e.getMessage());
            } catch (Exception e) {
                log.error("Error occurred during extraction for config ID: {}", config.getId(), e);
                detail.setStatus("FAILURE");
                detail.setErrorMessage(e.getMessage());
            }

            // Push to Kafka instead of direct DB insert
            producer.push(saveTopic, detail);
        }

        log.info("Reconciliation extraction job completed for date: {}", extractionDate);
    }
}
