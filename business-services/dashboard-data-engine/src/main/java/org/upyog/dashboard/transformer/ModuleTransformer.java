package org.upyog.dashboard.transformer;

import org.upyog.dashboard.common.constants.Module;
import org.upyog.dashboard.model.DashboardPayload;

/**
 * Strategy interface for converting module-specific raw data into a normalized
 * {@link DashboardPayload} that can flow through the rest of the adapter pipeline.
 *
 * <p>Each module has its own data schema and metric derivation rules.
 * Implementations of this interface encapsulate that module-specific knowledge,
 * keeping the rest of the pipeline (validation, loading, auditing) completely
 * generic.
 *
 * <h3>Registration</h3>
 * Implementations must:
 * <ol>
 *   <li>Be annotated with {@code @Component} (or a meta-annotation thereof) so
 *       Spring discovers them at startup.</li>
 *   <li>Return a unique, non-{@code null} {@link Module} value from
 *       {@link #getModule()} so {@link org.upyog.dashboard.registry.TransformerRegistry}
 *       can build the module-to-transformer dataMap without conflicts.</li>
 * </ol>
 *
 * <h3>Type parameter</h3>
 * {@code <T>} is the raw input data type specific to the module.  For example,
 * a PT transformer would declare {@code ModuleTransformer<DashboardData>} (or a
 * richer PT-specific model once one exists).  Using a type parameter keeps the
 * interface safe while the registry uses a raw-type cast internally
 * ({@code @SuppressWarnings("unchecked")}).
 *
 * <h3>Adding a new module</h3>
 * <ol>
 *   <li>Add the module constant to {@link org.upyog.dashboard.common.constants.Module}.</li>
 *   <li>Create a class implementing {@code ModuleTransformer<YourRawDataType>}.</li>
 *   <li>Annotate it with {@code @Component}.</li>
 *   <li>Implement {@link #getModule()} to return the new constant.</li>
 *   <li>Implement {@link #transform(Object)} with the module-specific logic.</li>
 * </ol>
 *
 * @param <T> the raw input data type accepted by this transformer
 *
 * @see org.upyog.dashboard.registry.TransformerRegistry
 * @see org.upyog.dashboard.pt.transformer.PTTransformer
 * @see DashboardPayload
 */
public interface ModuleTransformer<T> {

    /**
     * Returns the {@link Module} constant that identifies which module this
     * transformer handles.
     *
     * <p>The return value is used as the key when registering this transformer
     * in {@link org.upyog.dashboard.registry.TransformerRegistry}.  Each
     * transformer must return a unique module; if two transformers return the
     * same module the latter one silently overwrites the former in the registry.
     *
     * @return the module this transformer is responsible for; never {@code null}
     */
    Module getModule();

    /**
     * Converts module-specific {@code rawData} into a normalized
     * {@link DashboardPayload} suitable for validation and ingestion.
     *
     * <p>Implementations are responsible for:
     * <ul>
     *   <li>Extracting or computing all required metric values from
     *       {@code rawData}.</li>
     *   <li>Building one or more {@link org.upyog.dashboard.model.DashboardData}
     *       records with the correct {@code module}, {@code ulb}, {@code date},
     *       {@code state}, {@code region}, {@code ward}, and {@code metrics}
     *       fields populated.</li>
     *   <li>Wrapping the records in a {@link DashboardPayload} and returning it.</li>
     * </ul>
     *
     * <p>Implementations must <strong>never return {@code null}</strong> — a
     * {@code null} return will cause a {@link NullPointerException} in the
     * downstream {@link org.upyog.dashboard.validator.CommonValidator}.
     *
     * @param rawData the module-specific source data to transform; the concrete
     *                type is determined by the type parameter {@code <T>} of
     *                this interface
     * @return a fully populated {@link DashboardPayload}; never {@code null}
     */
    DashboardPayload transform(T rawData);
}
