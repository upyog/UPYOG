package org.upyog.dashboard.extractor;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.upyog.dashboard.common.constants.Module;

public interface LegacyBatchExtractor {

    /**
     * Identifies the business module targeted by this batch extractor.
     *
     * @return the target {@link org.upyog.dashboard.common.constants.Module} enum constant
     */
    Module getModule();

    /**
     * Extracts legacy records in memory-safe batches and streams them to the consumer.
     *
     * @param startDate start date of the extraction window
     * @param endDate end date of the extraction window
     * @param tenantId target state tenant ID
     * @param batchSize limit of records fetched per batch
     * @param batchConsumer consumer receiving each processed chunk of records
     * @return total count of records extracted across all batches
     */
    long extractInBatches(LocalDate startDate, LocalDate endDate, String tenantId, int batchSize, Consumer<List<Object>> batchConsumer);
}
