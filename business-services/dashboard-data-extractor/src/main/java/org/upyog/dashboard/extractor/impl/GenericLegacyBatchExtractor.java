package org.upyog.dashboard.extractor.impl;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.springframework.stereotype.Component;
import org.upyog.dashboard.common.constants.Module;
import org.upyog.dashboard.extractor.LegacyBatchExtractor;
import org.upyog.dashboard.extractor.ModuleExtractor;
import org.upyog.dashboard.registry.ExtractorRegistry;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Generic implementation of {@link LegacyBatchExtractor} that dynamically queries
 * the registered {@link ModuleExtractor} per date and streams records into the consumer.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class GenericLegacyBatchExtractor implements LegacyBatchExtractor {

    private final ExtractorRegistry extractorRegistry;

    /** {@inheritDoc} */
    @Override
    public long extractInBatches(Module module, LocalDate startDate, LocalDate endDate, List<String> tenantIds, int batchSize, Consumer<List<Object>> batchConsumer) {
        log.info("Starting generic legacy batch extraction from {} to {} for module {} across {} tenants",
                startDate, endDate, module, tenantIds != null ? tenantIds.size() : 0);

        ModuleExtractor<?> extractor = extractorRegistry.get(module);
        if (extractor == null) {
            throw new IllegalStateException("No registered ModuleExtractor bean found for module " + module);
        }

        if (tenantIds == null || tenantIds.isEmpty()) {
            log.warn("No tenant IDs provided for legacy batch extraction of module {}", module);
            return 0;
        }

        int chunkLimit = (batchSize > 0) ? batchSize : 50;
        long totalExtracted = 0;
        LocalDate currentDate = startDate;

        while (!currentDate.isAfter(endDate)) {
            for (int offset = 0; offset < tenantIds.size(); offset += chunkLimit) {
                List<String> tenantBatch = tenantIds.subList(offset, Math.min(offset + chunkLimit, tenantIds.size()));
                try {
                    Object extractedData = extractor.extractData(tenantBatch, currentDate);
                    if (extractedData != null) {
                        if (extractedData instanceof List<?> list) {
                            if (!list.isEmpty()) {
                                totalExtracted += list.size();
                                batchConsumer.accept(new ArrayList<>(list));
                            }
                        } else {
                            totalExtracted++;
                            batchConsumer.accept(List.of(extractedData));
                        }
                    }
                } catch (Exception exception) {
                    log.error("Failed to extract legacy data for module {} on date {} for tenants {}", module, currentDate, tenantBatch, exception);
                }
            }
            currentDate = currentDate.plusDays(1);
        }

        log.info("Legacy date-wise extraction completed for module {}. Total records extracted: {}", module, totalExtracted);
        return totalExtracted;
    }

    /** {@inheritDoc} */
    @Override
    public long extractInBatches(Module module, LocalDate startDate, LocalDate endDate, String tenantId, int batchSize, Consumer<List<Object>> batchConsumer) {
        return extractInBatches(module, startDate, endDate, tenantId != null ? List.of(tenantId) : List.of(), batchSize, batchConsumer);
    }
}
