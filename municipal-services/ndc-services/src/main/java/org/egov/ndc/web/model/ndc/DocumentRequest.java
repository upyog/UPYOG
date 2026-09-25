package org.egov.ndc.web.model.ndc;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentRequest {
    @JsonProperty("uuid")
    private String uuid;

    @JsonProperty("applicationId")
    private String applicationId;

    @JsonProperty("documentType")
    private String documentType;

    @JsonProperty("documentAttachment")
    private String documentAttachment;

    @JsonProperty("createdby")
    private String createdby;

    @JsonProperty("lastmodifiedby")
    private String lastmodifiedby;

    @JsonProperty("createdtime")
    private Long createdtime;

    @JsonProperty("lastmodifiedtime")
    private Long lastmodifiedtime;
}