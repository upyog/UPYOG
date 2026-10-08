package org.upyog.dashboard.mdms.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Model representing module details inside MDMS criteria.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ModuleDetail {

    @JsonProperty("moduleName")
    private String moduleName;

    @JsonProperty("masterDetails")
    private List<MasterDetail> masterDetails;
}
