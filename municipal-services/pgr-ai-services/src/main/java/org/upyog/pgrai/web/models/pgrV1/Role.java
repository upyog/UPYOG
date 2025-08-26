package org.upyog.pgrai.web.models.pgrV1;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a role in the system.
 * This class encapsulates the details of a role, including:
 * - The name of the role.
 * - The unique code identifying the role.
 * - The tenant ID associated with the role.
 *
 * This class is part of the PGR V1 module and is used to manage
 * role-related information within the system.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role {

    @JsonProperty("name")
    private String name;
    
    @JsonProperty("code")
    private String code;
    
    @JsonProperty("tenantId")
    private String tenantId;
}
