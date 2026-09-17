package org.egov.loadgenerator.generator;

import java.util.Map;

/**
 * Defines the contract for all module-specific payload generators used by the
 * Load Generator framework.
 *
 * <p>Each supported module (such as Property Tax, Trade License, Water,
 * Sewerage, etc.) must provide an implementation of this interface.
 * Implementations are automatically discovered and registered by Spring,
 * eliminating the need for manual module mapping or conditional logic.
 *
 * <h3>Responsibilities</h3>
 * <ul>
 *   <li>Provide the module identifier.</li>
 *   <li>Expose Create, Search, and Update API endpoints.</li>
 *   <li>Generate module-specific request payloads for load testing.</li>
 * </ul>
 *
 * <h3>Implementation Notes</h3>
 * <p>Modules that do not support Search or Update operations may rely on
 * the default implementations, which throw
 * {@link UnsupportedOperationException}.
 */
public interface ModuleGenerator {

    /**
     * Returns the Update API endpoint for the module.
     *
     * <p>The default implementation indicates that the module does not
     * support update operations.
     *
     * @return the Update API endpoint
     * @throws UnsupportedOperationException if update is not supported
     */
    default String getUpdateApiUrl() {
        throw new UnsupportedOperationException("Update API is not implemented");
    }

    /**
     * Returns the Search API endpoint for the module.
     *
     * <p>The default implementation indicates that the module does not
     * support search operations.
     *
     * @return the Search API endpoint
     * @throws UnsupportedOperationException if search is not supported
     */
    default String getSearchApiUrl() {
        throw new UnsupportedOperationException("Search API is not implemented");
    }

    /**
     * Returns the unique module identifier.
     *
     * @return the module name
     */
    String getModuleName();

    /**
     * Returns the Create API endpoint for the module.
     *
     * @return the Create API endpoint
     */
    String getCreateApiUrl();

    /**
     * Builds a module-specific request payload for load testing.
     *
     * @param tenantId the target tenant identifier
     * @param index the sequential request number
     * @return the generated request payload
     */
    Object buildPayload(String tenantId, int index);
}
