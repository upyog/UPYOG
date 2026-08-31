package org.upyog.dashboard.extractor.impl;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Component;
import org.upyog.dashboard.common.constants.Module;
import org.upyog.dashboard.extractor.LegacyBatchExtractor;
import org.upyog.dashboard.extractor.ModuleExtractor;
import org.upyog.dashboard.registry.ExtractorRegistry;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class GenericLegacyBatchExtractor implements LegacyBatchExtractor {

    private final ExtractorRegistry extractorRegistry;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    /** {@inheritDoc} */
    @Override
    public Module getModule() {
        return Module.PT;
    }

    /** {@inheritDoc} */
    @Override
    public long extractInBatches(LocalDate startDate, LocalDate endDate, String tenantId, int batchSize, java.util.function.Consumer<java.util.List<Object>> batchConsumer) {
        log.info("Starting legacy batch extraction from {} to {} for module {}", startDate, endDate, getModule());

        ModuleExtractor<?> extractor = extractorRegistry.get(getModule());
        if (extractor == null) {
            throw new IllegalStateException("No registered ModuleExtractor bean found for module " + getModule());
        }

        long totalExtracted = 0;
        LocalDate currentDate = startDate;

        while (!currentDate.isAfter(endDate)) {
            try {
                Object extractedData = extractor.extractData(currentDate);
                if (extractedData != null) {
                    if (extractedData instanceof java.util.List<?> list) {
                        if (!list.isEmpty()) {
                            totalExtracted += list.size();
                            batchConsumer.accept(new java.util.ArrayList<>(list));
                        }
                    } else {
                        totalExtracted++;
                        batchConsumer.accept(java.util.List.of(extractedData));
                    }
                }
            } catch (Exception exception) {
                log.error("Failed to extract legacy data for module {} on date {}", getModule(), currentDate, exception);
            }
            currentDate = currentDate.plusDays(1);
        }

        log.info("Legacy date-wise extraction completed for module {}. Total records extracted: {}", getModule(), totalExtracted);
        return totalExtracted;
    }
}
