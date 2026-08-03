package org.egov.garbageservice.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.*;
import org.egov.tracer.annotations.CustomSafeHtml;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@Data
@Builder(toBuilder = true)
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
/**
 * Core domain model for a garbage user charge account and its application lifecycle.
 * Links property, applicant details, workflow action, nested application/commercial data, bills, and child accounts.
 * Persisted and returned by GarbageAccountService on create, update, search, and status transitions.
 */
@EqualsAndHashCode(exclude = {"id", "uuid", "garbageId", "propertyId", "isOnlyWorkflowCall", "workflowAction", "workflowComment", "grbgApplication", "grbgCommercialDetails", "auditDetails", "garbageBills", "childGarbageAccounts"})
public class GarbageAccount {

    private Long id;

    @CustomSafeHtml
    private String uuid;

    @CustomSafeHtml
    private String tenantId;

    private Long garbageId;

    @CustomSafeHtml
    private String propertyId;

    @CustomSafeHtml
    private String type;

    @CustomSafeHtml
    private String name;

    @CustomSafeHtml
    private String mobileNumber;

    @CustomSafeHtml
    private String gender;

    @CustomSafeHtml
    private String emailId;

    private Boolean isOwner;

    @CustomSafeHtml
    private String userUuid;

    @CustomSafeHtml
    private String created_by;

    @CustomSafeHtml
    private String declarationUuid;

    @CustomSafeHtml
    private String workflowAction;

    @CustomSafeHtml
    private String workflowComment;

    @Builder.Default
    private Boolean isOnlyWorkflowCall = false;

    @CustomSafeHtml
    private String status;

    // new payload field — maps to status after enrichment
    @CustomSafeHtml
    private String applicationStatus;

    private GrbgApplication grbgApplication;

    @CustomSafeHtml
    private String grbgApplicationNumber;

    private GrbgOldDetails grbgOldDetails;


    private List<GrbgDocument> documents = new ArrayList<>();

    private GarbageSpecification garbageSpecification;

    private PropertyLocation propertyLocation;

    private WorkflowRequest workflow;

    // applicant person details from the new payload; persisted into additionalDetail during enrichment
    private List<ApplicantDetail> applicantDetails = new ArrayList<>();

    private AuditDetails auditDetails;


    private List<GrbgCollectionUnit> grbgCollectionUnits = new ArrayList<>();

    private List<GrbgAddress> addresses = new ArrayList<>();

    private JsonNode additionalDetail = null;

    private List<GarbageAccount> childGarbageAccounts = new ArrayList<>();

    @CustomSafeHtml
    private String parentAccount;

    private Boolean isActive = false;

    private Long subAccountCount;

    private Long approvalDate;

    @CustomSafeHtml
    private String businessService;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private LocalDate dueDate;

    @CustomSafeHtml
    private String channel;
}