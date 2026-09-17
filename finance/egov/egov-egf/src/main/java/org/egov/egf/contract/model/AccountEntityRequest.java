package org.egov.egf.contract.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.egov.infra.microservice.models.RequestInfo;
import org.egov.infra.validation.SanitizeHtml;

import jakarta.validation.constraints.NotNull;


/**
 * Request model representing an account entity creation or update payload.
 *
 * <p>This class is used as a deserialized JSON request body for account entity
 * operations in the eGov financial contract layer. It carries the identifying
 * and descriptive attributes of an account entity along with the standard
 * {@link RequestInfo} metadata.</p>
 *
 * <p><b>Validation:</b> All fields except {@code requestInfo} are marked
 * {@link NotNull} and {@link SafeHtml} to prevent null values and mitigate
 * XSS vulnerabilities in incoming data.</p>
 *
 * <p>Getters and setters are generated at compile time via Lombok's
 * {@link lombok.Getter} and {@link lombok.Setter} annotations.</p>
 *
 * @see RequestInfo
 */

@Getter
@Setter
public class AccountEntityRequest {

    @NotNull
    @JsonProperty("tenantId")
    @SanitizeHtml
    private String tenantId;

    @NotNull
    @JsonProperty("accountDetailType")
    @SanitizeHtml
    private Integer accountDetailType;

    @NotNull
    @JsonProperty("name")
    @SanitizeHtml
    private String name;

    @NotNull
    @JsonProperty("code")
    @SanitizeHtml
    private String code;

    @NotNull
    @JsonProperty("narration")
    @SanitizeHtml
    private String narration;

    @NotNull
    @JsonProperty("isActive")
    @SanitizeHtml
    private Boolean isActive;


    @JsonProperty("RequestInfo")
    private RequestInfo requestInfo;


    @Override
    public String toString() {
        return "AccountEntityRequest( [accountDetailType=" + accountDetailType + "], [name="+name+"],[code="+code+"],[narration="+narration+"],[isActive="+isActive+"])";
    }



}
