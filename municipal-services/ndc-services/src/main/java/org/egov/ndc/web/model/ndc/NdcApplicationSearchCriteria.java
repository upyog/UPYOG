package org.egov.ndc.web.model.ndc;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NdcApplicationSearchCriteria {
    private List<String> uuid;
    private List<String> applicationNo;
    private String tenantId;
    private String status;
    private String mobileNumber;
    private String name;
    private String active;
    @JsonProperty("ownerids")
    private Set<String> ownerIds;
    private String userName;
    private String createdBy;
    private Integer limit;
    private Integer offset;

}