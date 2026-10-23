package org.upyog.dashboard.mdms.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Model representing MDMS search criteria.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MdmsCriteria {

    @JsonProperty("tenantId")
    private String tenantId;

    @JsonProperty("moduleDetails")
    private List<ModuleDetail> moduleDetails;
}
