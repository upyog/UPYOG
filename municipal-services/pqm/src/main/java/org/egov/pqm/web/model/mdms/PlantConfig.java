package org.egov.pqm.web.model.mdms;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class PlantConfig {

    @JsonProperty("code")
    private String code;

    @JsonProperty("name")
    private String name;

    @JsonProperty("active")
    private boolean active;

    @JsonProperty("iotAnomalyDetectionDays")
    private int iotAnomalyDetectionDays;

    @JsonProperty("pendingTaskToDisplayWithinDays")
    private int pendingTaskToDisplayWithinDays;

    @JsonProperty("manualTestPendingEscalationDays")
    private int manualTestPendingEscalationDays;

    @JsonProperty("pendingTestsToDisplayWithinDays")
    private int pendingTestsToDisplayWithinDays;

    @JsonProperty("pendingTaskToDisplayWithinDaysInbox")
    private int pendingTaskToDisplayWithinDaysInbox;

    @JsonProperty("pendingTaskToDisplayWithinDaysForULB")
    private int pendingTaskToDisplayWithinDaysForULB;

    @JsonProperty("pendingTestsToDisplayWithinDaysInbox")
    private int pendingTestsToDisplayWithinDaysInbox;

    @JsonProperty("pendingTestsToDisplayWithinDaysForULB")
    private int pendingTestsToDisplayWithinDaysForULB;

}
