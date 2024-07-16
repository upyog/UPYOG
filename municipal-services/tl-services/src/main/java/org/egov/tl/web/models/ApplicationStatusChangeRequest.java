package org.egov.tl.web.models;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApplicationStatusChangeRequest {

	private String applicationNumber;
    private String idProofDoc;
    private String ownerProofDoc;
    private String ownerImage;
    private String tenantId;
    private String applicationStatus;

}
