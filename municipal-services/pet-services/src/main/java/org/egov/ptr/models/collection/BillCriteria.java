package org.egov.ptr.models.collection;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BillCriteria {

    @JsonProperty("tenantId")
    private String tenantId;

    @JsonProperty("consumerCode")
    private String consumerCode;

    @JsonProperty("businessService")
    private String businessService;
}