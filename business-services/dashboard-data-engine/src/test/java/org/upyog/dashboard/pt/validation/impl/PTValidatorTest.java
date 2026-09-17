package org.upyog.dashboard.pt.validation.impl;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.upyog.dashboard.common.constants.Module;
import org.upyog.dashboard.exception.ValidationException;
import org.upyog.dashboard.validator.impl.PTValidator;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThatCode;

/**
 * Unit tests for {@link PTValidator}.
 */
class PTValidatorTest {

    private final PTValidator validator = new PTValidator();

    @Test
    @DisplayName("getModule returns Module.PT")
    void getModule_returnsPT() {
        assertThat(validator.getModule()).isEqualTo(Module.PT);
    }

    @Test
    @DisplayName("Valid metrics with all required keys passes validation")
    void validMetrics_passesValidation() {
        Map<String, Object> metrics = createValidPTMetrics();

        assertThatCode(() -> validator.validate(metrics))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Missing assessments key throws ValidationException")
    void missingAssessments_throwsException() {
        Map<String, Object> metrics = createValidPTMetrics();
        metrics.remove("assessments");

        assertThatThrownBy(() -> validator.validate(metrics))
                .isInstanceOf(ValidationException.class)
                .hasMessage("assessments is mandatory for PT module.");
    }

    @Test
    @DisplayName("Missing todaysTotalApplications throws ValidationException")
    void missingTodaysTotalApplications_throwsException() {
        Map<String, Object> metrics = createValidPTMetrics();
        metrics.remove("todaysTotalApplications");

        assertThatThrownBy(() -> validator.validate(metrics))
                .isInstanceOf(ValidationException.class)
                .hasMessage("todaysTotalApplications is mandatory for PT module.");
    }

    @Test
    @DisplayName("Missing todaysClosedApplications throws ValidationException")
    void missingTodaysClosedApplications_throwsException() {
        Map<String, Object> metrics = createValidPTMetrics();
        metrics.remove("todaysClosedApplications");

        assertThatThrownBy(() -> validator.validate(metrics))
                .isInstanceOf(ValidationException.class)
                .hasMessage("todaysClosedApplications is mandatory for PT module.");
    }

    @Test
    @DisplayName("Missing todaysApprovedApplications throws ValidationException")
    void missingTodaysApprovedApplications_throwsException() {
        Map<String, Object> metrics = createValidPTMetrics();
        metrics.remove("todaysApprovedApplications");

        assertThatThrownBy(() -> validator.validate(metrics))
                .isInstanceOf(ValidationException.class)
                .hasMessage("todaysApprovedApplications is mandatory for PT module.");
    }

    @Test
    @DisplayName("Missing todaysApprovedApplicationsWithinSLA throws ValidationException")
    void missingTodaysApprovedApplicationsWithinSLA_throwsException() {
        Map<String, Object> metrics = createValidPTMetrics();
        metrics.remove("todaysApprovedApplicationsWithinSLA");

        assertThatThrownBy(() -> validator.validate(metrics))
                .isInstanceOf(ValidationException.class)
                .hasMessage("todaysApprovedApplicationsWithinSLA is mandatory for PT module.");
    }

    @Test
    @DisplayName("Missing avgDaysForApplicationApproval throws ValidationException")
    void missingAvgDaysForApplicationApproval_throwsException() {
        Map<String, Object> metrics = createValidPTMetrics();
        metrics.remove("avgDaysForApplicationApproval");

        assertThatThrownBy(() -> validator.validate(metrics))
                .isInstanceOf(ValidationException.class)
                .hasMessage("avgDaysForApplicationApproval is mandatory for PT module.");
    }

    @Test
    @DisplayName("Missing noOfPropertiesPaidToday throws ValidationException")
    void missingNoOfPropertiesPaidToday_throwsException() {
        Map<String, Object> metrics = createValidPTMetrics();
        metrics.remove("noOfPropertiesPaidToday");

        assertThatThrownBy(() -> validator.validate(metrics))
                .isInstanceOf(ValidationException.class)
                .hasMessage("noOfPropertiesPaidToday is mandatory for PT module.");
    }

    @Test
    @DisplayName("Missing propertiesRegistered throws ValidationException")
    void missingPropertiesRegistered_throwsException() {
        Map<String, Object> metrics = createValidPTMetrics();
        metrics.remove("propertiesRegistered");

        assertThatThrownBy(() -> validator.validate(metrics))
                .isInstanceOf(ValidationException.class)
                .hasMessage("propertiesRegistered is mandatory for PT module.");
    }

    @Test
    @DisplayName("Missing assessedProperties throws ValidationException")
    void missingAssessedProperties_throwsException() {
        Map<String, Object> metrics = createValidPTMetrics();
        metrics.remove("assessedProperties");

        assertThatThrownBy(() -> validator.validate(metrics))
                .isInstanceOf(ValidationException.class)
                .hasMessage("assessedProperties is mandatory for PT module.");
    }

    @Test
    @DisplayName("Missing transactions throws ValidationException")
    void missingTransactions_throwsException() {
        Map<String, Object> metrics = createValidPTMetrics();
        metrics.remove("transactions");

        assertThatThrownBy(() -> validator.validate(metrics))
                .isInstanceOf(ValidationException.class)
                .hasMessage("transactions is mandatory for PT module.");
    }

    @Test
    @DisplayName("Missing propertyTax throws ValidationException")
    void missingPropertyTax_throwsException() {
        Map<String, Object> metrics = createValidPTMetrics();
        metrics.remove("propertyTax");

        assertThatThrownBy(() -> validator.validate(metrics))
                .isInstanceOf(ValidationException.class)
                .hasMessage("propertyTax is mandatory for PT module.");
    }

    @Test
    @DisplayName("Missing cess throws ValidationException")
    void missingCess_throwsException() {
        Map<String, Object> metrics = createValidPTMetrics();
        metrics.remove("cess");

        assertThatThrownBy(() -> validator.validate(metrics))
                .isInstanceOf(ValidationException.class)
                .hasMessage("cess is mandatory for PT module.");
    }

    @Test
    @DisplayName("Missing rebate throws ValidationException")
    void missingRebate_throwsException() {
        Map<String, Object> metrics = createValidPTMetrics();
        metrics.remove("rebate");

        assertThatThrownBy(() -> validator.validate(metrics))
                .isInstanceOf(ValidationException.class)
                .hasMessage("rebate is mandatory for PT module.");
    }

    @Test
    @DisplayName("Missing penalty throws ValidationException")
    void missingPenalty_throwsException() {
        Map<String, Object> metrics = createValidPTMetrics();
        metrics.remove("penalty");

        assertThatThrownBy(() -> validator.validate(metrics))
                .isInstanceOf(ValidationException.class)
                .hasMessage("penalty is mandatory for PT module.");
    }

    @Test
    @DisplayName("Missing interest throws ValidationException")
    void missingInterest_throwsException() {
        Map<String, Object> metrics = createValidPTMetrics();
        metrics.remove("interest");

        assertThatThrownBy(() -> validator.validate(metrics))
                .isInstanceOf(ValidationException.class)
                .hasMessage("interest is mandatory for PT module.");
    }

    @Test
    @DisplayName("Missing todaysCollection throws ValidationException")
    void missingTodaysCollection_throwsException() {
        Map<String, Object> metrics = createValidPTMetrics();
        metrics.remove("todaysCollection");

        assertThatThrownBy(() -> validator.validate(metrics))
                .isInstanceOf(ValidationException.class)
                .hasMessage("todaysCollection is mandatory for PT module.");
    }

    @Test
    @DisplayName("Key with null value is considered present")
    void keyWithNullValue_isConsideredPresent() {
        Map<String, Object> metrics = createValidPTMetrics();
        metrics.put("assessments", null);

        // Should not throw because containsKey returns true even for null values
        assertThatCode(() -> validator.validate(metrics))
                .doesNotThrowAnyException();
    }

    private static Map<String, Object> createValidPTMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        metrics.put("assessments", 100);
        metrics.put("todaysTotalApplications", 50);
        metrics.put("todaysClosedApplications", 30);
        metrics.put("todaysApprovedApplications", 25);
        metrics.put("todaysApprovedApplicationsWithinSLA", 20);
        metrics.put("avgDaysForApplicationApproval", 3.5);
        metrics.put("noOfPropertiesPaidToday", 40);
        metrics.put("propertiesRegistered", 500);
        metrics.put("assessedProperties", 450);
        metrics.put("transactions", 200);
        metrics.put("propertyTax", 100000.0);
        metrics.put("cess", 5000.0);
        metrics.put("rebate", 2000.0);
        metrics.put("penalty", 1000.0);
        metrics.put("interest", 500.0);
        metrics.put("todaysCollection", 15000.0);
        return metrics;
    }
}