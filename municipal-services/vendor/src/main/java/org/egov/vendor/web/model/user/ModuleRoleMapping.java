package org.egov.vendor.web.model.user;

import lombok.*;
import org.egov.vendor.util.ModuleNameEnum;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = {"moduleName", "type"})
public class ModuleRoleMapping {
    public enum MappingType{
        VENDOR, VEHICLE, DRIVER
    }
    private MappingType type;

    private ModuleNameEnum moduleName;

    private String roleCode ;

    private String roleName;

    private boolean active;
}
