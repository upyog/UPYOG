package org.upyog.employee.dasboard.web.models;

import java.util.Map;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RoleBasedDashboardResponse {
       @JsonProperty("responseInfo")
    private ResponseInfo responseInfo;

    @JsonProperty("dashboardData")
    private Map<String, EmployeeDashboardDetails> dashboardData;
}
