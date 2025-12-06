package org.upyog.web.models;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;

<<<<<<< HEAD
import java.util.ArrayList;
import java.util.List;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.validation.annotation.Validated;
import org.upyog.web.models.Role;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
=======
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.ArrayList;
import java.util.List;
import org.springframework.validation.annotation.Validated;
import org.upyog.web.models.Role;

import javax.validation.Valid;
import javax.validation.constraints.*;
>>>>>>> master-LTS
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder;

/**
 * User role carries the tenant related role information for the user. A user can have multiple roles per tenant based on the need of the tenant. A user may also have multiple roles for multiple tenants.
 */
<<<<<<< HEAD
@Schema (description = "User role carries the tenant related role information for the user. A user can have multiple roles per tenant based on the need of the tenant. A user may also have multiple roles for multiple tenants.")
@Validated
@jakarta.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-12-07T15:40:06.365+05:30")
=======
@ApiModel(description = "User role carries the tenant related role information for the user. A user can have multiple roles per tenant based on the need of the tenant. A user may also have multiple roles for multiple tenants.")
@Validated
@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-12-07T15:40:06.365+05:30")
>>>>>>> master-LTS

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TenantRole   {
        @JsonProperty("tenantId")
        private String tenantId = null;

        @JsonProperty("roles")
        @Valid
        private List<Role> roles = new ArrayList<>();


        public TenantRole addRolesItem(Role rolesItem) {
        this.roles.add(rolesItem);
        return this;
        }

}

