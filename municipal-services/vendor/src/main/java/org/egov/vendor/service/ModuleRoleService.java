package org.egov.vendor.service;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.RequestInfo;
import org.egov.vendor.config.ModuleRoleConfig;
import org.egov.vendor.util.ModuleNameEnum;
import org.egov.vendor.util.VendorUtil;
import org.egov.vendor.web.model.user.ModuleRoleMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ModuleRoleService {

    private final ModuleRoleConfig moduleRoleConfig;

    @Autowired
    VendorUtil vendorUtil;

    @Autowired
    public ModuleRoleService(ModuleRoleConfig moduleRoleConfig) {
        this.moduleRoleConfig = moduleRoleConfig;
    }

    public ModuleRoleMapping getModuleRoleMapping(RequestType request, ModuleRoleMapping.MappingType mappingType) {
        ModuleRoleRequestWrapper wrapper = new ModuleRoleRequestWrapper(request, vendorUtil);

        log.info("Processing ModuleRoleMapping for {} with Module Name: {}",
                request.getClass().getSimpleName(), wrapper.getModuleName());

        return getModuleRoleMapping(wrapper.getRequestInfo(), wrapper.getTenantId(),
                wrapper.getModuleName(), mappingType);
    }

    /**
     * Retrieves the ModuleRoleMapping based on request type.
     */
    private ModuleRoleMapping getModuleRoleMapping(RequestInfo requestInfo, String tenantId,
                                                   String moduleName, ModuleRoleMapping.MappingType mappingType) {
        ModuleRoleMapping roleMappingKey = ModuleRoleMapping.builder()
                .moduleName(moduleName)
                .type(mappingType)
                .build();

        return moduleRoleConfig.getRoleMappingForModule(requestInfo, tenantId, roleMappingKey);
    }



}


@Getter
@Slf4j
class ModuleRoleRequestWrapper {
    private final RequestInfo requestInfo;
    private final String tenantId;
    private final String moduleName;

    public ModuleRoleRequestWrapper(RequestType request, VendorUtil vendorUtil) {
        this.requestInfo = request.getRequestInfo();
        this.tenantId = request.getTenantId();
        this.moduleName = request.getModuleNameOrDefault(vendorUtil);
    }

    private static ModuleNameEnum getSafeModuleNameEnum(String moduleName) {
        try {
            return ModuleNameEnum.valueOf(moduleName);
        } catch (IllegalArgumentException e) {
            log.warn("Invalid module name '{}' provided. Defaulting to FSM.", moduleName);
            return ModuleNameEnum.FSM;
        }
    }
}


