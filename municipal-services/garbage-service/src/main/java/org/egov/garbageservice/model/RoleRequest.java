package org.egov.garbageservice.model;

import lombok.*;
import org.egov.tracer.annotations.CustomSafeHtml;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
/**
 * Minimal role lookup key (code and tenantId) for user service role APIs.
 * Used when assigning or validating roles during user provisioning.
 */
@EqualsAndHashCode(of = {"code", "tenantId"})
public class RoleRequest {

    @CustomSafeHtml
    private String code;
    @CustomSafeHtml
    private String name;
    @CustomSafeHtml
    private String tenantId;

    /**
     * Constructs a new instance with the specified attributes.
     */

    public RoleRequest(RoleV2 domainRole) {
        this.code = domainRole.getCode();
        this.name = domainRole.getName();
        this.tenantId = domainRole.getTenantId();
    }

    /**
     * Converts this object into its corresponding domain representation.
     *
     * @return converted domain object
     */

    public RoleV2 toDomain() {
        return RoleV2.builder().code(code).name(name).tenantId(tenantId).build();
    }
}
