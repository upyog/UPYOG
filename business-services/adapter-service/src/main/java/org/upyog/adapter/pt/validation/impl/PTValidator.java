package org.upyog.adapter.pt.validation.impl;

import java.util.Map;

import org.springframework.stereotype.Component;
import org.upyog.adapter.common.constants.Module;
import org.upyog.adapter.exception.ValidationException;
import org.upyog.adapter.validator.ModuleValidator;

/**
 * Property Tax (PT) module-specific implementation of {@link ModuleValidator}.
 *
 * <p>Validates that all mandatory PT metric keys are present in the metrics map
 * of a transformed {@link org.upyog.adapter.model.DashboardData} record.
 * This validator runs <em>after</em> the common cross-module validation performed
 * by {@link org.upyog.adapter.validator.CommonValidator}, providing an additional
 * layer of PT-specific checks.
 *
 * <h3>Mandatory PT metrics</h3>
 * The following keys must all be present in the metrics map:
 * <ul>
 *   <li>{@code assessments} — total number of property assessments</li>
 *   <li>{@code todaysTotalApplications} — applications received today</li>
 *   <li>{@code todaysClosedApplications} — applications closed today</li>
 *   <li>{@code todaysApprovedApplications} — applications approved today</li>
 *   <li>{@code todaysApprovedApplicationsWithinSLA} — approvals within SLA today</li>
 *   <li>{@code avgDaysForApplicationApproval} — average approval turnaround</li>
 *   <li>{@code noOfPropertiesPaidToday} — number of properties with payment today</li>
 *   <li>{@code propertiesRegistered} — total registered properties</li>
 *   <li>{@code assessedProperties} — total assessed properties</li>
 *   <li>{@code transactions} — total payment transactions</li>
 *   <li>{@code propertyTax} — total property tax collected</li>
 *   <li>{@code cess} — cess component of tax</li>
 *   <li>{@code rebate} — rebate applied</li>
 *   <li>{@code penalty} — penalty charged</li>
 *   <li>{@code interest} — interest charged</li>
 *   <li>{@code todaysCollection} — total collection for today</li>
 * </ul>
 *
 * <h3>Registration</h3>
 * Annotated with {@code @Component} so Spring discovers it at startup and
 * {@link org.upyog.adapter.validator.ValidatorRegistry} automatically registers
 * it under the {@link Module#PT} key.
 *
 * @see ModuleValidator
 * @see org.upyog.adapter.validator.ValidatorRegistry
 * @see org.upyog.adapter.validator.CommonValidator
 */
/**
 * Class representing the PTValidator class.
 * 
 * <p>Contributes to the core Property Tax metrics ingestion pipeline.
 */
@Component
public class PTValidator implements ModuleValidator {

    /**
     * Returns the module constant that this validator handles.
     *
     * <p>Used by {@link org.upyog.adapter.validator.ValidatorRegistry} to build
     * the module-to-validator mapping at application startup.
     *
     * @return {@link Module#PT} — always
     */
    @Override
    public Module getModule() {
        return Module.PT;
    }

    /**
     * Validates that all mandatory PT metric keys are present in {@code metrics}.
     *
     * <p>Each key is checked with {@link #validateRequired(Map, String)}.  If any
     * key is absent a {@link ValidationException} is thrown immediately, stopping
     * validation for subsequent keys.  Callers that want a full list of missing
     * fields should consider collecting errors rather than failing fast.
     *
     * @param metrics the metrics map from
     *                {@link org.upyog.adapter.model.DashboardData#getMetrics()};
     *                must not be {@code null} (enforced by
     *                {@link org.upyog.adapter.validator.CommonValidator})
     * @throws ValidationException if any mandatory PT metric key is absent from
     *                             {@code metrics}
     */
    @Override
    public void validate(Map<String, Object> metrics) {

        validateRequired(metrics, "assessments");
        validateRequired(metrics, "todaysTotalApplications");
        validateRequired(metrics, "todaysClosedApplications");
        validateRequired(metrics, "todaysApprovedApplications");
        validateRequired(metrics, "todaysApprovedApplicationsWithinSLA");
        validateRequired(metrics, "avgDaysForApplicationApproval");
        validateRequired(metrics, "noOfPropertiesPaidToday");
        validateRequired(metrics, "propertiesRegistered");
        validateRequired(metrics, "assessedProperties");
        validateRequired(metrics, "transactions");
        validateRequired(metrics, "propertyTax");
        validateRequired(metrics, "cess");
        validateRequired(metrics, "rebate");
        validateRequired(metrics, "penalty");
        validateRequired(metrics, "interest");
        validateRequired(metrics, "todaysCollection");
    }

    /**
     * Asserts that {@code key} is present in the {@code metrics} map.
     *
     * <p>Uses {@link Map#containsKey(Object)} so that a key mapped to a
     * {@code null} value is considered present.  If the key is absent a
     * {@link ValidationException} is thrown with a message in the format:
     * {@code "<key> is mandatory for PT module."}.
     *
     * @param metrics the map to inspect; must not be {@code null}
     * @param key     the metric key to verify; must not be {@code null}
     * @throws ValidationException if {@code metrics} does not contain {@code key}
     */
    private void validateRequired(Map<String, Object> metrics, String key) {
        if (!metrics.containsKey(key)) {
            throw new ValidationException(key + " is mandatory for PT module.");
        }
    }
}
