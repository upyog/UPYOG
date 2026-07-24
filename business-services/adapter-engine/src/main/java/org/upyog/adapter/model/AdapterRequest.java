package org.upyog.adapter.model;

import java.util.List;

import org.upyog.adapter.common.constants.Module;

import lombok.Builder;
import lombok.Data;

/**
 * Encapsulates a single ingestion request passed into the adapter-service pipeline.
 *
 * <p>An {@code AdapterRequest} is the top-level input object consumed by
 * {@link org.upyog.adapter.api.AdapterClient#execute}.  It carries two pieces
 * of information:
 * <ol>
 *   <li>Which DIGIT module the data belongs to ({@link #module}).</li>
 *   <li>The raw, module-specific source data ({@link #rawData}) that will be
 *       passed to the corresponding
 *       {@link org.upyog.adapter.transformer.ModuleTransformer}.</li>
 * </ol>
 *
 * <h3>Type safety</h3>
 * {@code rawData} is typed as {@link Object} so that the request model stays
 * generic and decoupled from individual module data structures.  The actual
 * type is known only to the matching {@code ModuleTransformer}, which casts it
 * internally.  Callers should always pair the correct {@code rawData} type with
 * the corresponding {@code module} value to avoid {@link ClassCastException}s.
 *
 * <h3>Example</h3>
 * <pre>{@code
 * AdapterRequest request = AdapterRequest.builder()
 *     .module(Module.PT)
 *     .rawData(ptRawData)   // PTRawData instance
 *     .build();
 *
 * IngestionResult result = adapterClient.execute(request);
 * }</pre>
 *
 * @see org.upyog.adapter.api.AdapterClient
 * @see org.upyog.adapter.common.constants.Module
 * @see org.upyog.adapter.transformer.ModuleTransformer
 */
/**
 * Class representing the AdapterRequest class.
 * 
 * <p>Contributes to the core Property Tax metrics ingestion pipeline.
 */
@Data
@Builder
public class AdapterRequest {

    /**
     * The DIGIT module whose data this request carries.
     *
     * <p>Used as a key to look up the correct
     * {@link org.upyog.adapter.transformer.ModuleTransformer} in
     * {@link org.upyog.adapter.registry.TransformerRegistry} and (when enabled)
     * the correct {@link org.upyog.adapter.validator.ModuleValidator} in
     * {@link org.upyog.adapter.validator.ValidatorRegistry}.
     *
     * <p>Must not be {@code null}.
     */
    private Module module;

    /**
     * Module-specific raw input data to be transformed and ingested.
     *
     * <p>The concrete type of this field depends on the {@link #module}:
     * <ul>
     *   <li>{@link Module#PT} — expects a {@code PTDTO} or equivalent
     *       PT-specific raw data object.</li>
     * </ul>
     *
     * <p>The raw data is passed verbatim to
     * {@link org.upyog.adapter.transformer.ModuleTransformer#transform(Object)},
     * which performs the type cast.  Providing a mismatched type will result in
     * a {@link ClassCastException} inside the transformer.
     */
    private Object rawData;
}
