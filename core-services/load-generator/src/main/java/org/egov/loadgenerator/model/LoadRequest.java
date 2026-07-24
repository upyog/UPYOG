package org.egov.loadgenerator.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class LoadRequest {

    @JsonProperty("module")
    private String module;

    @JsonProperty("tenantId")
    private String tenantId;

    @JsonProperty("count")
    private int count;
}
