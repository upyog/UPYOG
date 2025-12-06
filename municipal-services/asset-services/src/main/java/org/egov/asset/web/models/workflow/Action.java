package org.egov.asset.web.models.workflow;

import com.fasterxml.jackson.annotation.JsonProperty;
<<<<<<< HEAD
import io.swagger.v3.oas.annotations.media.Schema;
=======
import io.swagger.annotations.ApiModel;
>>>>>>> master-LTS
import lombok.*;
import org.egov.asset.web.models.AuditDetails;
import org.springframework.validation.annotation.Validated;

<<<<<<< HEAD
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
=======
import javax.validation.Valid;
import javax.validation.constraints.Size;
>>>>>>> master-LTS
import java.util.ArrayList;
import java.util.List;

/**
 * A Object holds the basic data for a Building Plan
 */
<<<<<<< HEAD
@Schema(description = "A Object holds the basic data for a Building Plan")
@Validated
@jakarta.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2018-12-04T11:26:25.532+05:30")
=======
@ApiModel(description = "A Object holds the basic data for a Building Plan")
@Validated
@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2018-12-04T11:26:25.532+05:30")
>>>>>>> master-LTS

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
//@EqualsAndHashCode(of = {"tenantId","currentState","action"})
@EqualsAndHashCode
public class Action {

    @Size(max = 256)
    @JsonProperty("uuid")
    private String uuid;

    @Size(max = 256)
    @JsonProperty("tenantId")
    private String tenantId;

    @Size(max = 256)
    @JsonProperty("currentState")
    private String currentState;

    @Size(max = 256)
    @JsonProperty("action")
    private String action;

    @Size(max = 256)
    @JsonProperty("nextState")
    private String nextState;

    @Size(max = 1024)
    @JsonProperty("roles")
    @Valid
    private List<String> roles;

    private AuditDetails auditDetails;


    public Action addRolesItem(String rolesItem) {
        if (this.roles == null) {
            this.roles = new ArrayList<>();
        }
        this.roles.add(rolesItem);
        return this;
    }

}

