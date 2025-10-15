package org.egov.inbox.web.model.ndc;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DocumentRequest {
    @JsonProperty("uuid")
    private String uuid;

    @JsonProperty("applicantId")
    private String applicantId;

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

    // Getters and setters
}