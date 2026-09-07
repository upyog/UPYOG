package org.upyog.dashboard.validator.impl;

import java.util.Map;

import org.springframework.stereotype.Component;
import org.upyog.dashboard.common.constants.Module;
import org.upyog.dashboard.exception.ValidationException;
import org.upyog.dashboard.pt.constants.PTMetricConstants;
import org.upyog.dashboard.validator.ModuleValidator;

/**
 * Property Tax (PT) module-specific implementation of {@link ModuleValidator}.
 *
 * <p>Validates that all mandatory PT metric keys are present in the metrics dataMap
 * of a transformed {@link org.upyog.dashboard.model.DashboardData} record.
 * This validator runs <em>after</em> the common cross-module validation performed
 * by {@link org.upyog.dashboard.validator.CommonValidator}, providing an additional
 * layer of PT-specific checks.
 *
 * <h3>Mandatory PT metrics</h3>
 * The following keys must all be present in the metrics dataMap:
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
 *   <li>{@code cess}Component of tax</li>
 *   <li>{@code rebate} — rebate applied</li>
 *   <li>{@code penalty} — penalty charged</li>
 *   <li>{@code interest} — interest charged</li>
 *   <li>{@code todaysCollection} — total collection for today</li>
 * </ul>
 *
 * <h3>Registration</h3>
 * Annotated with {@code @Component} so Spring discovers it at startup and
 * {@link org.upyog.dashboard.validator.ValidatorRegistry} automatically registers
 * it under the {@link Module#PT} key.
 *
 * @see ModuleValidator
 * @see org.upyog.dashboard.validator.ValidatorRegistry
 * @see org.upyog.dashboard.validator.CommonValidator
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
     * <p>Used by {@link org.upyog.dashboard.validator.ValidatorRegistry} to build
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
     * validation for subsequent keys.  Callers that want a full dataList of missing
     * fields should consider collecting errors rather than failing fast.
     *
     * @param metrics the metrics dataMap from
     *                {@link org.upyog.dashboard.model.DashboardData#getMetrics()};
     *                must not be {@code null} (enforced by
     *                {@link org.upyog.dashboard.validator.CommonValidator})
     * @throws ValidationException if any mandatory PT metric key is absent from
     *                             {@code metrics}
     */
    @Override
    public void validate(Map<String, Object> metrics) {

        validateRequired(metrics, PTMetricConstants.ASSESSMENTS);
        validateRequired(metrics, PTMetricConstants.TODAYS_TOTAL_APPLICATIONS);
        validateRequired(metrics, PTMetricConstants.TODAYS_CLOSED_APPLICATIONS);
        validateRequired(metrics, PTMetricConstants.TODAYS_APPROVED_APPLICATIONS);
        validateRequired(metrics, PTMetricConstants.TODAYS_APPROVED_APPLICATIONS_WITHIN_SLA);
        validateRequired(metrics, PTMetricConstants.AVG_DAYS_FOR_APPLICATION_APPROVAL);
        validateRequired(metrics, PTMetricConstants.NO_OF_PROPERTIES_PAID_TODAY);
        validateRequired(metrics, PTMetricConstants.PROPERTIES_REGISTERED);
        validateRequired(metrics, PTMetricConstants.ASSESSED_PROPERTIES);
        validateRequired(metrics, PTMetricConstants.TRANSACTIONS);
        validateRequired(metrics, PTMetricConstants.PROPERTY_TAX);
        validateRequired(metrics, PTMetricConstants.CESS);
        validateRequired(metrics, PTMetricConstants.REBATE);
        validateRequired(metrics, PTMetricConstants.PENALTY);
        validateRequired(metrics, PTMetricConstants.INTEREST);
        validateRequired(metrics, PTMetricConstants.TODAYS_COLLECTION);
    }

    /**
     * Asserts that {@code key} is present in the {@code metrics} dataMap.
     *
     * <p>Uses {@link Map#containsKey(Object)} so that a key mapped to a
     * {@code null} value is considered present.  If the key is absent a
     * {@link ValidationException} is thrown with a message in the format:
     * {@code "<key> is mandatory for PT module."}.
     *
     * @param metrics the dataMap to inspect; must not be {@code null}
     * @param key     the metric key to verify; must not be {@code null}
     * @throws ValidationException if {@code metrics} does not contain {@code key}
     */
    private void validateRequired(Map<String, Object> metrics, String key) {
        if (!metrics.containsKey(key)) {
            throw new ValidationException(key + " is mandatory for PT module.");
        }
    }
}
