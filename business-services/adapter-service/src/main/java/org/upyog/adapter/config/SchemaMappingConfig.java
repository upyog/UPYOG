package org.upyog.adapter.config;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.upyog.adapter.common.constants.Module;

import lombok.Data;

/**
 * Configuration bean binding multi-module SQL query templates and state-enabled modules from 
 * {@code schema-mapping.yml}.
 * 
 * <p>Enables zero-code database query customization per state deployment.
 */
@Data
@Component
@ConfigurationProperties(prefix = "extractor")
public class SchemaMappingConfig {

    /**
     * List of business modules enabled for extraction in the current state deployment.
     */
    private List<Module> enabledModules = new ArrayList<>();

    /**
     * Map of dynamic SQL query configurations indexed by module.
     */
    private Map<Module, ModuleQueries> mappings = new EnumMap<>(Module.class);

    /**
     * Gets the query configuration for a specific business module.
     * 
     * @param module the module enum key
     * @return the {@link ModuleQueries} config or {@code null} if not configured
     */
    public ModuleQueries getQueriesForModule(Module module) {
        return mappings.get(module);
    }

    /**
     * Nested configuration class holding SQL query templates for a specific module.
     */
    @Data
    public static class ModuleQueries {
        /**
         * SQL query retrieving scalar metrics and JSON aggregation subqueries in a single database call.
         */
        private String combinedMetricsQuery;

        /**
         * SQL query retrieving daily collection and tax head account detail records.
         */
        private String collectionMetricsQuery;
    }
}
