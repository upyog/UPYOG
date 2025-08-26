package org.egov.vehicle.web.model.user;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = {"moduleName", "type"})
@ToString
public class ModuleRoleMapping {
    public enum MappingType{
        DRIVER
    }
    private MappingType type;

    private String moduleName;

    private String roleCode ;

    private String roleName;

    private boolean active;
}
