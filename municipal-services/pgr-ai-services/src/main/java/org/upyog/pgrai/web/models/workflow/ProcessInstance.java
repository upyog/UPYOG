package org.upyog.pgrai.web.models.workflow;

import java.util.ArrayList;
import java.util.List;

<<<<<<< HEAD
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
=======
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
>>>>>>> master-LTS

import org.upyog.pgrai.web.models.AuditDetails;
import org.upyog.pgrai.web.models.Document;
import org.egov.common.contract.request.User;
import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonProperty;

<<<<<<< HEAD
import io.swagger.v3.oas.annotations.media.Schema;
=======
import io.swagger.annotations.ApiModel;
>>>>>>> master-LTS
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * A Object holds the basic data for a PGR workflow
 */
<<<<<<< HEAD
@Schema(description = "A Object holds the basic data for a PGR workflow")
@Validated
@jakarta.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2018-12-04T11:26:25.532+05:30")
=======
@ApiModel(description = "A Object holds the basic data for a PGR workflow")
@Validated
@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2018-12-04T11:26:25.532+05:30")
>>>>>>> master-LTS

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(of = { "id" })
@ToString
public class ProcessInstance {

    @Size(max = 64)
    @JsonProperty("id")
    private String id;

    @NotNull
    @Size(max = 128)
    @JsonProperty("tenantId")
    private String tenantId;

    @NotNull
    @Size(max = 128)
    @JsonProperty("businessService")
    private String businessService;

    @NotNull
    @Size(max = 128)
    @JsonProperty("businessId")
    private String businessId;

    @NotNull
    @Size(max = 128)
    @JsonProperty("action")
    private String action;

    @NotNull
    @Size(max = 64)
    @JsonProperty("moduleName")
    private String moduleName;

    @JsonProperty("state")
    private State state;

    private String notificationAction;

    @JsonProperty("comment")
    private String comment;

    @JsonProperty("documents")
    @Valid
    private List<Document> documents;

    @JsonProperty("assignes")
    private List<User> assignes;

    @JsonProperty("assigner")
    private User assigner = null;

    @JsonProperty("auditDetails")
    private AuditDetails auditDetails = null;

    @JsonProperty("businesssServiceSla")
    private Long businesssServiceSla = null;

    public ProcessInstance addDocumentsItem(Document documentsItem) {
        if (this.documents == null) {
            this.documents = new ArrayList<>();
        }
        if (!this.documents.contains(documentsItem))
            this.documents.add(documentsItem);

        return this;
    }

}