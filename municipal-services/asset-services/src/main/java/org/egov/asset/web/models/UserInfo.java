package org.egov.asset.web.models;

import com.fasterxml.jackson.annotation.JsonProperty;
<<<<<<< HEAD
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.Valid;
=======
import io.swagger.annotations.ApiModel;
import lombok.*;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
>>>>>>> master-LTS
import java.util.ArrayList;
import java.util.List;

/**
 * This is acting ID token of the authenticated user on the server. Any value provided by the clients will be ignored and actual user based on authtoken will be used on the server.
 */
<<<<<<< HEAD
@Schema(description = "This is acting ID token of the authenticated user on the server. Any value provided by the clients will be ignored and actual user based on authtoken will be used on the server.")
@Validated
@jakarta.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-04-12T12:56:34.514+05:30")
=======
@ApiModel(description = "This is acting ID token of the authenticated user on the server. Any value provided by the clients will be ignored and actual user based on authtoken will be used on the server.")
@Validated
@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-04-12T12:56:34.514+05:30")
>>>>>>> master-LTS

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserInfo {
    @JsonProperty("tenantId")
    private String tenantId = null;

    @JsonProperty("uuid")
    private String uuid = null;

    @JsonProperty("userName")
    private String userName = null;

    @JsonProperty("password")
    private String password = null;

    @JsonProperty("idToken")
    private String idToken = null;

    @JsonProperty("mobile")
    private String mobile = null;

    @JsonProperty("email")
    private String email = null;

    @JsonProperty("primaryrole")
    @Valid
    private List<Role> primaryrole = new ArrayList<>();

    @JsonProperty("additionalroles")
    @Valid
    private List<TenantRole> additionalroles = null;


    public UserInfo addPrimaryroleItem(Role primaryroleItem) {
        this.primaryrole.add(primaryroleItem);
        return this;
    }

    public UserInfo addAdditionalrolesItem(TenantRole additionalrolesItem) {
        if (this.additionalroles == null) {
            this.additionalroles = new ArrayList<>();
        }
        this.additionalroles.add(additionalrolesItem);
        return this;
    }

}

