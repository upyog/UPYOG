package org.upyog.dashboard.pgr.validation.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.upyog.dashboard.common.constants.Module;
import org.upyog.dashboard.exception.ValidationException;
import org.upyog.dashboard.validator.impl.PGRValidator;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests for {@link PGRValidator}.
 */
class PGRValidatorTest {

    private final PGRValidator validator = new PGRValidator();

    @Test
    @DisplayName("getModule returns Module.PGR")
    void getModule_returnsPGR() {
        assertThat(validator.getModule()).isEqualTo(Module.PGR);
    }

    @Test
    @DisplayName("Valid metrics with all required keys passes validation")
    void validMetrics_passesValidation() {
        Map<String, Object> metrics = createValidPGRMetrics();

        assertThatCode(() -> validator.validate(metrics))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Missing slaAchievement key throws ValidationException")
    void missingSlaAchievement_throwsException() {
        Map<String, Object> metrics = createValidPGRMetrics();
        metrics.remove("slaAchievement");

        assertThatThrownBy(() -> validator.validate(metrics))
                .isInstanceOf(ValidationException.class)
                .hasMessage("slaAchievement is mandatory for PGR module.");
    }

    @Test
    @DisplayName("Missing uniqueCitizens key throws ValidationException")
    void missingUniqueCitizens_throwsException() {
        Map<String, Object> metrics = createValidPGRMetrics();
        metrics.remove("uniqueCitizens");

        assertThatThrownBy(() -> validator.validate(metrics))
                .isInstanceOf(ValidationException.class)
                .hasMessage("uniqueCitizens is mandatory for PGR module.");
    }

    @Test
    @DisplayName("Missing todaysComplaints key throws ValidationException")
    void missingTodaysComplaints_throwsException() {
        Map<String, Object> metrics = createValidPGRMetrics();
        metrics.remove("todaysComplaints");

        assertThatThrownBy(() -> validator.validate(metrics))
                .isInstanceOf(ValidationException.class)
                .hasMessage("todaysComplaints is mandatory for PGR module.");
    }

    private static Map<String, Object> createValidPGRMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("slaAchievement", List.of());
        metrics.put("completionRate", List.of());
        metrics.put("uniqueCitizens", 22);
        metrics.put("todaysComplaints", List.of());
        metrics.put("todaysReopenedComplaints", List.of());
        metrics.put("todaysOpenComplaints", List.of());
        metrics.put("todaysAssignedComplaints", List.of());
        metrics.put("averageSolutionTime", List.of());
        metrics.put("todaysRejectedComplaints", List.of());
        metrics.put("todaysReassignedComplaints", List.of());
        metrics.put("todaysReassignRequestedComplaints", List.of());
        metrics.put("todaysClosedComplaints", List.of());
        metrics.put("todaysResolvedComplaints", List.of());
        return metrics;
    }
}
