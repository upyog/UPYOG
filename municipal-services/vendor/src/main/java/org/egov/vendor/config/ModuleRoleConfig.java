package org.egov.vendor.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONArray;
import org.egov.common.contract.request.RequestInfo;
import org.egov.mdms.model.MdmsResponse;
import org.egov.mdms.model.ModuleDetail;
import org.egov.tracer.model.CustomException;
import org.egov.vendor.util.VendorConstants;
import org.egov.vendor.util.VendorUtil;
import org.egov.vendor.web.model.user.ModuleRoleMapping;
import org.egov.vendor.web.model.user.ModuleRoleMapping.MappingType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.util.*;
import java.util.stream.Collectors;

@Getter
@Setter
@Configuration
@Slf4j
public class ModuleRoleConfig {

    @Autowired
    private VendorUtil util;

    @Autowired
    private ObjectMapper mapper;

    private Map<ModuleRoleMapping, ModuleRoleMapping> moduleRoleMap;

    /**
     * Retrieves the role mapping for a specified module.
     *
     * @param requestInfo        request info object
     * @param moduleRoleMappingKey Module name and type
     * @return Corresponding role mapping
     * @throws CustomException If role mapping is not found
     */
    public ModuleRoleMapping getRoleMappingForModule(RequestInfo requestInfo, String tenantId, ModuleRoleMapping moduleRoleMappingKey) {
        log.info("moduleRoleMappingKey : {}", moduleRoleMappingKey);
        if (moduleRoleMap == null) {
            loadVendorRoleMappingMasterData(requestInfo, tenantId);
        }

        ModuleRoleMapping roleMapping = moduleRoleMap.get(moduleRoleMappingKey);
        if (roleMapping == null) {
        	roleMapping = moduleRoleMap.get(ModuleRoleMapping.builder().type(MappingType.VENDOR).moduleName("COMMON").build());
        }
        return roleMapping;
    }

    /**
     * Loads master data for module role mappings.
     *
     * @param requestInfo  request info object
     * @throws CustomException If required data is missing
     */
    private void loadVendorRoleMappingMasterData(RequestInfo requestInfo, String tenantId) {

        List<ModuleDetail> moduleDetailList = util.getModuleRoleMappingRequest();
        Object mdmsData = util.mDMSCall(requestInfo, tenantId, moduleDetailList);
        MdmsResponse mdmsResponse = mapper.convertValue(mdmsData, MdmsResponse.class);

        if (mdmsResponse.getMdmsRes().get(VendorConstants.VENDOR_MODULE) == null) {
            throw new CustomException("VENDOR_MASTER_DATA_NOT_AVAILABLE",
                    "Vendor master data is missing in the response.");
        }

        List<ModuleRoleMapping> moduleRoleMappings = new ArrayList<>();

        // Load Vendor Role Mappings
        loadRoleMappings(mdmsResponse, VendorConstants.MODULE_VENDOR_ROLE_MAPPING, moduleRoleMappings,  "MODULE_VENDOR_ROLE_MAPPING", ModuleRoleMapping.MappingType.VENDOR);

        // Load Driver Role Mappings
        loadRoleMappings(mdmsResponse, VendorConstants.MODULE_DRIVER_ROLE_MAPPING, moduleRoleMappings, "MODULE_DRIVER_ROLE_MAPPING", ModuleRoleMapping.MappingType.DRIVER);

        // Handle potential duplicate keys using a merge function
        moduleRoleMap = moduleRoleMappings.stream()
                .collect(Collectors.toMap(
                        mapping -> mapping,
                        mapping -> mapping,
                        (existing, replacement) -> existing // Handle duplicate keys by keeping the first one
                ));
        log.info("Module role mapping map after loading from mdms : {}", moduleRoleMap);
    }

    /**
     * Loads role mappings from the MDMS response.
     *
     * @param mdmsResponse       MDMS response object
     * @param mappingKey         Key for retrieving mappings
     * @param moduleRoleMappings List to store the retrieved mappings
     * @param logIdentifier      Identifier for logging
     * @param mappingType        type of mapping vendor, driver
     */
    private void loadRoleMappings(MdmsResponse mdmsResponse, String mappingKey, List<ModuleRoleMapping> moduleRoleMappings, String logIdentifier, ModuleRoleMapping.MappingType mappingType) {
        JSONArray jsonArray = mdmsResponse.getMdmsRes().get(VendorConstants.VENDOR_MODULE).get(mappingKey);

        if (jsonArray == null) {
            throw new CustomException(mappingKey + "_ERROR",
                    logIdentifier + " master data not available.");
        }

        try {
            List<ModuleRoleMapping> roleMappings = mapper.readValue(jsonArray.toJSONString(),
                    mapper.getTypeFactory().constructCollectionType(List.class, ModuleRoleMapping.class));
            roleMappings.forEach(moduleRoleMapping ->  moduleRoleMapping.setType(mappingType));
            log.info("Loaded roleMappings added mapping type {}: ",  roleMappings);
            moduleRoleMappings.addAll(roleMappings);
        } catch (JsonProcessingException e) {
            log.error("Error while parsing {}: {}", logIdentifier, e.getMessage(), e);
        }
    }
}
