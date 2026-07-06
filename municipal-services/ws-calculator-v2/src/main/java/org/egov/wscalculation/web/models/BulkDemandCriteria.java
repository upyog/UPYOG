package org.egov.wscalculation.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Validated
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class BulkDemandCriteria {

    @JsonProperty("tenantIds")
    private List<String> tenantIds;

    @JsonProperty("offset")
    private Long offset;

    @JsonProperty("limit")
    private Long limit;

    @JsonProperty("consumerCode")
    private String consumerCode;

    @JsonProperty("tenantId")
    private String tenantId;

    @JsonProperty("locality")
    private String locality;
}
