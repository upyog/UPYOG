package org.upyog.dashboard.model;
import java.util.List;

import org.upyog.dashboard.common.constants.Module;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.EqualsAndHashCode;
import lombok.Setter;

/**
 * Encapsulates a single ingestion request passed into the adapter-service pipeline.
 *
 * <p>An {@code DashboardRequest} is the top-level input object consumed by
 * {@link org.upyog.dashboard.api.DashboardClient#execute}.  It carries two pieces
 * of information:
 * <ol>
 *   <li>Which module the data belongs to ({@link #module}).</li>
 *   <li>The raw, module-specific source data ({@link #rawData}) that will be
 *       passed to the corresponding
 *       {@link org.upyog.dashboard.transformer.ModuleTransformer}.</li>
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
 * DashboardRequest request = DashboardRequest.builder()
 *     .module(Module.PT)
 *     .rawData(ptRawData)   // PTRawData instance
 *     .build();
 *
 * IngestionResult result = dashboardClient.execute(request);
 * }</pre>
 *
 * @see org.upyog.dashboard.api.DashboardClient
 * @see org.upyog.dashboard.common.constants.Module
 * @see org.upyog.dashboard.transformer.ModuleTransformer
 */
/**
 * Class representing the DashboardRequest class.
 * 
 * <p>Contributes to the core Property Tax metrics ingestion pipeline.
 */
@Builder
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class DashboardRequest {

    /**
     * The module whose data this request carries.
     *
     * <p>Used as a key to look up the correct
     * {@link org.upyog.dashboard.transformer.ModuleTransformer} in
     * {@link org.upyog.dashboard.registry.TransformerRegistry} and (when enabled)
     * the correct {@link org.upyog.dashboard.validator.ModuleValidator} in
     * {@link org.upyog.dashboard.validator.ValidatorRegistry}.
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
     * {@link org.upyog.dashboard.transformer.ModuleTransformer#transform(Object)},
     * which performs the type cast.  Providing a mismatched type will result in
     * a {@link ClassCastException} inside the transformer.
     */
    private Object rawData;
}
