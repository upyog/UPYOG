package org.upyog.adapter.api;

import org.upyog.adapter.model.AdapterRequest;
import org.upyog.adapter.model.IngestionResult;

/**
 * Primary entry-point contract for the adapter-service pipeline.
 *
 * <p>Implementations of this interface coordinate the full ingestion lifecycle
 * for a single module request:
 * <ol>
 *   <li>Resolve the correct {@link org.upyog.adapter.transformer.ModuleTransformer}
 *       for the requested module.</li>
 *   <li>Transform the raw source data into a normalized
 *       {@link org.upyog.adapter.model.DashboardPayload}.</li>
 *   <li>Run common and module-specific validations.</li>
 *   <li>Delegate to a {@link org.upyog.adapter.loader.Loader} to push the
 *       payload to the target endpoint and record the outcome.</li>
 * </ol>
 *
 * <p>The interface is intentionally thin so that different execution strategies
 * (e.g. synchronous HTTP, batch, dry-run) can be swapped in without changing
 * callers.
 *
 * @see AdapterClientImpl
 * @see AdapterRequest
 * @see IngestionResult
 */
public interface AdapterClient {

    /**
     * Executes the full ingestion pipeline for the module and raw data
     * contained in {@code request}.
     *
     * <p>The method is synchronous: it blocks until the loader has received a
     * response from the downstream endpoint (or encountered an error) and
     * returns an {@link IngestionResult} that describes the outcome.
     *
     * @param request the ingestion request carrying the target
     *                {@link org.upyog.adapter.common.constants.Module} and the
     *                module-specific raw data object; must not be {@code null}
     * @return an {@link IngestionResult} with status {@code SUCCESS} or
     *         {@code FAILURE}, the raw response body or failure reason, and the
     *         epoch-millis timestamp of completion; never {@code null}
     * @throws org.upyog.adapter.exception.ValidationException if the transformed
     *         payload fails common or module-specific validation rules
     * @throws IllegalArgumentException if no transformer is registered for the
     *         module specified in {@code request}
     */
    IngestionResult execute(AdapterRequest request);
}
