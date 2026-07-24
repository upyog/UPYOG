package org.upyog.adapter.validator.impl;

import java.util.Map;

import org.springframework.stereotype.Component;
import org.upyog.adapter.common.constants.Module;
import org.upyog.adapter.exception.ValidationException;
import org.upyog.adapter.validator.ModuleValidator;

/**
 * Public Grievance Redressal (PGR) module-specific implementation of {@link ModuleValidator}.
 *
 * <p>Validates that all mandatory PGR metric keys are present in the metrics map.
 */
@Component
public class PGRValidator implements ModuleValidator {

    @Override
    public Module getModule() {
        return Module.PGR;
    }

    @Override
    public void validate(Map<String, Object> metrics) {
        validateRequired(metrics, "slaAchievement");
        validateRequired(metrics, "completionRate");
        validateRequired(metrics, "uniqueCitizens");
        validateRequired(metrics, "todaysComplaints");
        validateRequired(metrics, "todaysReopenedComplaints");
        validateRequired(metrics, "todaysOpenComplaints");
        validateRequired(metrics, "todaysAssignedComplaints");
        validateRequired(metrics, "averageSolutionTime");
        validateRequired(metrics, "todaysRejectedComplaints");
        validateRequired(metrics, "todaysReassignedComplaints");
        validateRequired(metrics, "todaysReassignRequestedComplaints");
        validateRequired(metrics, "todaysClosedComplaints");
        validateRequired(metrics, "todaysResolvedComplaints");
    }

    private void validateRequired(Map<String, Object> metrics, String key) {
        if (!metrics.containsKey(key)) {
            throw new ValidationException(key + " is mandatory for PGR module.");
        }
    }
}
