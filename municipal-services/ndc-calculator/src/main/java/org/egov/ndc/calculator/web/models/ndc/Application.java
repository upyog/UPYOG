package org.egov.ndc.calculator.web.models.ndc;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.egov.ndc.calculator.web.models.workflow.ProcessInstance;
import org.egov.ndc.calculator.web.models.workflow.Workflow;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Application {

    @JsonProperty("uuid")
    private String uuid;

    @JsonProperty("tenantId")
    private String tenantId;

    @JsonProperty("owners")
    private List<OwnerInfo> owners;

    @JsonProperty("NdcDetails")
    private List<NdcDetailsRequest> ndcDetails;

    @JsonProperty("Documents")
    private List<DocumentRequest> documents;

    @JsonProperty("auditDetails")
    private AuditDetails auditDetails;

    @JsonProperty("applicationStatus")
    private String applicationStatus;

    @JsonProperty("active")
    private Boolean active;

    @JsonProperty("action")
    private String action;

    @JsonProperty("reason")
    private String reason;

    @JsonProperty("processInstance")
    private ProcessInstance processInstance;

    @JsonProperty("workflow")
    private Workflow workflow = null;

    public void addOwner(OwnerInfo owner) {
        if (owners == null) owners = new ArrayList<>();
        owners.add(owner);
    }

    public void addDetail(NdcDetailsRequest detail) {
        if (ndcDetails == null) ndcDetails = new ArrayList<>();
        ndcDetails.add(detail);
    }

    public void addDocument(DocumentRequest document) {
        if (documents == null) documents = new ArrayList<>();
        documents.add(document);
    }
}