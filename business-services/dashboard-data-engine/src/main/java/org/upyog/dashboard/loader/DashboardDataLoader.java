package org.upyog.dashboard.loader;

import org.upyog.dashboard.model.DashboardPayload;
import org.upyog.dashboard.model.IngestionResult;
import org.upyog.dashboard.util.CommonUtils;

/**
 * Strategy interface for pushing a normalized {@link DashboardPayload} to a
 * downstream target (e.g. the National Dashboard ingest HTTP endpoint, AWS S3, etc.).
 *
 * <p>The engine supports multiple loader strategies selected dynamically or statically based on
 * configuration. Concrete implementations include {@code DashboardDataLoaderImpl} (HTTP REST)
 * and {@code S3DashboardDataLoaderImpl} (Excel upload to S3).
 *
 * @see org.upyog.dashboard.loader.impl.DashboardDataLoaderImpl
 * @see org.upyog.dashboard.loader.impl.S3DashboardDataLoaderImpl
 * @see DashboardPayload
 * @see IngestionResult
 */
public interface DashboardDataLoader {

    /**
     * Pushes the normalized {@code data} payload to the configured downstream
     * target and returns a result describing the outcome.
     *
     * @param data the fully validated, normalized dashboard payload to push
     * @return an {@link IngestionResult} describing the outcome
     */
    default IngestionResult load(DashboardPayload data) {
        return IngestionResult.builder()
                .ingestionStatus("SUCCESS")
                .responseData("")
                .failureReason(null)
                .ingestedAt(CommonUtils.getCurrentEpochMillis())
                .build();
    }
}
