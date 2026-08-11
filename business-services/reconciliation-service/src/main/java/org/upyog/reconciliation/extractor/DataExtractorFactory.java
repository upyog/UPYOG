package org.upyog.reconciliation.extractor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class DataExtractorFactory {

    @Autowired
    private Map<String, MetricDataExtractor> extractorMap;

    public MetricDataExtractor getExtractor(String dataSourceType) {
        MetricDataExtractor extractor = extractorMap.get(dataSourceType);
        if (extractor == null) {
            throw new IllegalArgumentException("Unknown data source type: " + dataSourceType);
        }
        return extractor;
    }
}
