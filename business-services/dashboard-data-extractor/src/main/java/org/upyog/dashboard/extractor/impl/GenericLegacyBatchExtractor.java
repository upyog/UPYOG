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
    public long extractInBatches(Module module, LocalDate startDate, LocalDate endDate, String tenantId, int batchSize, Consumer<List<Object>> batchConsumer) {
        log.info("Starting generic legacy batch extraction from {} to {} for module {}", startDate, endDate, module);

        ModuleExtractor<?> extractor = extractorRegistry.get(module);
        if (extractor == null) {
            throw new IllegalStateException("No registered ModuleExtractor bean found for module " + module);
        }

        long totalExtracted = 0;
        LocalDate currentDate = startDate;

        while (!currentDate.isAfter(endDate)) {
            try {
                Object extractedData = extractor.extractData(currentDate);
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
                log.error("Failed to extract legacy data for module {} on date {}", module, currentDate, exception);
            }
            currentDate = currentDate.plusDays(1);
        }

        log.info("Legacy date-wise extraction completed for module {}. Total records extracted: {}", module, totalExtracted);
        return totalExtracted;
    }
}
