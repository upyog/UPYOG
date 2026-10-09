package org.upyog.dashboard.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import lombok.Getter;

/**
 * Extractor-specific properties extending {@link DashboardProperties} to
 * provide centralized access to MDMS client and extractor batch configuration.
 */
@Getter
@Component
@Primary
public class DashboardExtractorProperties extends DashboardProperties {

    // eGov MDMS service settings
    @Value("${egov.mdms.host}")
    private String mdmsHost;

    @Value("${egov.mdms.search.endpoint}")
    private String mdmsSearchEndpoint;

    // Daily ingestion batch size for HTTP API payloads and database inserts
    @Value("${dashboard-data.ingestion.batch-size}")
    private int ingestionBatchSize;

    // Maximum number of ULBs grouped together in a single SQL extraction query
    @Value("${dashboard-data.extractor.tenant-batch-size}")
    private int tenantBatchSize;

    // Dedicated legacy batch size for bulk DB historical extraction & Excel
    // generation
    @Value("${dashboard-data.legacy.batch-size}")
    private int legacyBatchSize;

    // Toggle to retain generated legacy Excel files on disk
    @Value("${dashboard-data.legacy.keep-excel-file}")
    private boolean legacyKeepExcelFile;
}
