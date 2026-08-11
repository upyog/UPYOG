package org.upyog.reconciliation.extractor;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface MetricDataExtractor {

    /**
     * Fetches processed metrics data for the given tenant, module, and date.
     */
    List<Map<String, Object>> extractData(String tenantId, String moduleName, LocalDate date);

}
