package org.egov.garbageservice.web.models.workflow;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.egov.common.contract.request.User;
import org.egov.garbageservice.web.models.AuditDetails;
import org.egov.tracer.annotations.CustomSafeHtml;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(of = {"id"})
@ToString
/**
 * Contract model for a single workflow process tied to a garbage application (or sub-application).
 *
 * Behavior:
 * - Identifies the instance (id, tenantId, businessService, businessId, moduleName, action).
 * - Carries current {@link State}, comment, documents, assigner/assignes, and nextActions after transition.
 * - Tracks SLA fields (stateSla, businesssServiceSla), previousStatus, rating, and escalation flag.
 * - Built in GarbageAccountService when creating/updating accounts and sent via {@link ProcessInstanceRequest}.
 * - Helper methods add documents, next actions, and assignees without duplicates where applicable.
 *
 * Notes:
 * - businessId is typically the garbage application number; parent and child accounts may each get an instance.
 * - JSON property assignes uses legacy spelling (not assignees).
 * - businesssServiceSla field name has an extra s — matches workflow API contract.
 * - Data-only model; {@link WorkflowService#callWf} posts transitions to the workflow engine.
 */
public class ProcessInstance {

    @JsonProperty("id")
    @CustomSafeHtml
    private String id = null;

    //        @NotNull
    @JsonProperty("tenantId")
    @CustomSafeHtml
    private String tenantId = null;

    //        @NotNull
    @JsonProperty("businessService")
    @CustomSafeHtml
    private String businessService = null;

    //        @NotNull
    @JsonProperty("businessId")
    @CustomSafeHtml
    private String businessId = null;

    //        @NotNull
    @JsonProperty("action")
    @CustomSafeHtml
    private String action = null;

    //        @NotNull
    @JsonProperty("moduleName")
    @CustomSafeHtml
    private String moduleName = null;

    @JsonProperty("state")
    private State state = null;

    @JsonProperty("comment")
    @CustomSafeHtml
    private String comment = null;

    @JsonProperty("documents")
//        @Valid
    private List<Document> documents = null;

    @JsonProperty("assigner")
    private User assigner = null;

    @JsonProperty("assignes")
    private List<User> assignes = null;

    @JsonProperty("nextActions")
//        @Valid
    private List<Action> nextActions = null;

    @JsonProperty("stateSla")
    private Long stateSla = 0L;

    @JsonProperty("businesssServiceSla")
    private Long businesssServiceSla = null;

    @JsonProperty("previousStatus")
    @CustomSafeHtml
    private String previousStatus = null;

    @JsonProperty("entity")
    private Object entity = null;

    @JsonProperty("auditDetails")
    private AuditDetails auditDetails = null;

    @JsonProperty("rating")
    private Integer rating = null;

    @JsonProperty("escalated")
    private Boolean escalated = false;

    @JsonProperty("role")
    @CustomSafeHtml
    private String role = null;

    /**
     * Appends a document to the process instance.
     *
     * @param documentsItem the document to append
     * @return this ProcessInstance instance for chaining
     */

    public ProcessInstance addDocumentsItem(Document documentsItem) {
        if (this.documents == null) {
            this.documents = new ArrayList<>();
        }
        if (!this.documents.contains(documentsItem))
            this.documents.add(documentsItem);

        return this;
    }

    /**
     * Appends a next available action to this state transition.
     *
     * @param nextActionsItem the action to append
     * @return this ProcessInstance instance for chaining
     */

    public ProcessInstance addNextActionsItem(Action nextActionsItem) {
        if (this.nextActions == null) {
            this.nextActions = new ArrayList<>();
        }
        this.nextActions.add(nextActionsItem);
        return this;
    }

    /**
     * Appends an assignee user to this process instance.
     *
     * @param usersItem the user to append
     * @return this ProcessInstance instance for chaining
     */

    public ProcessInstance addUsersItem(User usersItem) {
        if (this.assignes == null) {
            this.assignes = new ArrayList<>();
        }
        if (!this.assignes.contains(usersItem))
            this.assignes.add(usersItem);

        return this;
    }

}

