package org.egov.vehicle.service;


import org.egov.common.contract.request.RequestInfo;
import org.egov.vehicle.config.ModuleRoleConfig;
import org.egov.vehicle.util.VehicleUtil;
import org.egov.vehicle.web.model.VehicleRequest;
import org.egov.vehicle.web.model.user.ModuleRoleMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ModuleRoleService {

    private final ModuleRoleConfig moduleRoleConfig;

    @Autowired
    VehicleUtil vehicleUtil;

    @Autowired
    public ModuleRoleService(ModuleRoleConfig moduleRoleConfig) {
        this.moduleRoleConfig = moduleRoleConfig;
    }

    public ModuleRoleMapping getModuleRoleMapping(VehicleRequest request, ModuleRoleMapping.MappingType mappingType) {
    	String moduleName = vehicleUtil.getModuleNameOrDefault(request);
        log.info("Processing ModuleRoleMapping for {} with Module Name: {}",
                request.getClass().getSimpleName(), moduleName);

        return getModuleRoleMapping(request.getRequestInfo(), request.getVehicle().getTenantId(),
        		moduleName, mappingType);
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
