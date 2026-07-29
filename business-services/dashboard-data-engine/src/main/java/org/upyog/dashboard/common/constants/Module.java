package org.upyog.dashboard.common.constants;

/**
 * Enumeration of modules that the adapter-service currently supports.
 *
 * <p>Each constant in this enum acts as a registry key used in two places:
 * <ol>
 *   <li>{@link org.upyog.dashboard.registry.TransformerRegistry} — maps a
 *       {@code Module} value to the concrete
 *       {@link org.upyog.dashboard.transformer.ModuleTransformer} that knows how to
 *       transform raw data for that module into a
 *       {@link org.upyog.dashboard.model.DashboardPayload}.</li>
 *   <li>{@link org.upyog.dashboard.validator.ValidatorRegistry} — maps a
 *       {@code Module} value to the concrete
 *       {@link org.upyog.dashboard.validator.ModuleValidator} that enforces
 *       module-specific metric rules.</li>
 * </ol>
 *
 * <h3>Adding a new module</h3>
 * <ol>
 *   <li>Add a new constant here (e.g. {@code TL}, {@code FSM}).</li>
 *   <li>Create a {@code ModuleTransformer} implementation that returns the new
 *       constant from {@code getModule()}.</li>
 *   <li>Optionally create a {@code ModuleValidator} implementation for
 *       module-specific metric validation.</li>
 * </ol>
 *
 * @see org.upyog.dashboard.registry.TransformerRegistry
 * @see org.upyog.dashboard.validator.ValidatorRegistry
 */
public enum Module {

    /**
     * Property Tax module.
     *
     * <p>Handled by:
     * <ul>
     *   <li>Transformer: {@link org.upyog.dashboard.pt.transformer.PTTransformer}</li>
     *   <li>Validator:   {@link org.upyog.dashboard.pt.validation.impl.PTValidator}</li>
     * </ul>
     *
     * <p>Expected metrics keys include: {@code assessments}, {@code propertyTax},
     * {@code cess}, {@code rebate}, {@code penalty}, {@code interest},
     * {@code transactions}, {@code todaysCollection}, and several application
     * count/SLA fields.
     */
    PT,

    /**
     * Public Grievance Redressal module.
     *
     * <p>Handled by:
     * <ul>
     *   <li>Extractor:   {@link org.upyog.dashboard.pgr.extractor.PgrModuleExtractor}</li>
     *   <li>Transformer: {@link org.upyog.dashboard.pgr.transformer.PGRTransformer}</li>
     *   <li>Validator:   {@link org.upyog.dashboard.pgr.validation.impl.PGRValidator}</li>
     * </ul>
     */
    PGR
}
