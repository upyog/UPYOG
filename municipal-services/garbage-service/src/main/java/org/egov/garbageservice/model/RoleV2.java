package org.egov.garbageservice.model;

import org.egov.tracer.annotations.CustomSafeHtml;

import lombok.*;

import java.util.Date;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
/**
 * Role definition in user service v2 format with tenant scope and validity dates.
 * Attached to UserV2 for authorization when employees access garbage workflows.
 */
@EqualsAndHashCode(of = {"code", "tenantId"})
public class RoleV2 {
    private static final String CITIZEN = "CITIZEN";
    @CustomSafeHtml
    private String name;
    @CustomSafeHtml
    private String code;
    @CustomSafeHtml
    private String description;
    private Long createdBy;
    private Date createdDate;
    private Long lastModifiedBy;
    private Date lastModifiedDate;
    @CustomSafeHtml
    private String tenantId;

    public static RoleV2 getCitizenRole() {
        return RoleV2.builder().code(CITIZEN).build();
    }
}