package digit.web.models;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.*;
import org.egov.common.contract.models.AuditDetails;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Validated
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class SchemaDefinition {

    @JsonProperty("id")
    @Size(min = 2, max = 128)
    private String id = null;

    @JsonProperty("tenantId")
    @NotNull
    @Size(min = 2, max = 128)
    private String tenantId = null;

    @JsonProperty("code")
    @NotNull
    @Size(min = 2, max = 128)
    private String code = null;

    @JsonProperty("description")

    @Size(min = 2, max = 512)
    private String description = null;

    @JsonProperty("definition")
    @NotNull
    private JsonNode definition = null;

    @JsonProperty("isActive")

    private Boolean isActive = true;

    @JsonProperty("auditDetails")
    @Valid
    private AuditDetails auditDetails = null;

}