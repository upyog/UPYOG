package org.upyog.adapter.validator;

import java.util.Map;

import org.upyog.adapter.common.constants.Module;

/**
 * Strategy interface for module-specific metric validation.
 *
 * <p>Implementations of this interface validate the {@code metrics} dataMap of a
 * {@link org.upyog.adapter.model.DashboardData} record for a specific DIGIT module,
 * ensuring that all KPIs required by that module are present before the payload
 * is sent to the National Dashboard endpoint.
 *
 * <p>Module validators complement {@link CommonValidator}: the common validator
 * checks structural fields (module, state, ULB, etc.) while module validators
 * check the contents of the metrics dataMap, which differs per module.
 *
 * <h3>Registration</h3>
 * Implementations must:
 * <ol>
 *   <li>Be annotated with {@code @Component} so Spring discovers them at startup.</li>
 *   <li>Return a unique, non-{@code null} {@link Module} value from
 *       {@link #getModule()} so {@link ValidatorRegistry} can build the
 *       module-to-validator dataMap without conflicts.</li>
 * </ol>
 *
 * <h3>Adding a new module validator</h3>
 * <ol>
 *   <li>Create a class in {@code org.upyog.adapter.<module>.validation.impl}
 *       implementing {@code ModuleValidator}.</li>
 *   <li>Annotate it with {@code @Component}.</li>
 *   <li>Implement {@link #getModule()} to return the corresponding
 *       {@link Module} constant.</li>
 *   <li>Implement {@link #validate(Map)} to throw
 *       {@link org.upyog.adapter.exception.ValidationException} for any missing
 *       or invalid metric key.</li>
 * </ol>
 *
 * @see ValidatorRegistry
 * @see CommonValidator
 * @see org.upyog.adapter.pt.validation.impl.PTValidator
 */
public interface ModuleValidator {

    /**
     * Returns the {@link Module} constant that identifies which module this
     * validator handles.
     *
     * <p>Used as the registration key in {@link ValidatorRegistry}.  Each
     * implementation must return a distinct value; duplicate module registrations
     * cause the later one to silently overwrite the earlier one.
     *
     * @return the module this validator covers; never {@code null}
     */
    Module getModule();

    /**
     * Validates the module-specific metrics dataMap extracted from a
     * {@link org.upyog.adapter.model.DashboardData} record.
     *
     * <p>Implementations should iterate over all metrics keys that are mandatory
     * for their module and throw a {@link org.upyog.adapter.exception.ValidationException}
     * for the first missing key (fail-fast) or accumulate errors and throw once
     * (fail-all), depending on the desired UX.
     *
     * <p>This method is guaranteed by the pipeline to be called only after
     * {@link CommonValidator#validate} has confirmed that {@code metrics} is
     * non-null.
     *
     * @param metrics the metrics dataMap from
     *                {@link org.upyog.adapter.model.DashboardData#getMetrics()};
     *                never {@code null} at this point in the pipeline
     * @throws org.upyog.adapter.exception.ValidationException if any mandatory
     *         module-specific metric key is absent or invalid
     */
    void validate(Map<String, Object> metrics);
}
