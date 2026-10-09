package org.upyog.dashboard.mdms.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Model representing a master detail inside MDMS criteria.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MasterDetail {

    @JsonProperty("name")
    private String name;

    @JsonProperty("filter")
    private String filter;
}
