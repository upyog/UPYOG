package org.egov.infra.mdms.service;

import lombok.Builder;
import lombok.extern.slf4j.Slf4j;
import org.egov.infra.mdms.config.ApplicationConfig;
import org.egov.infra.mdms.model.*;
import org.egov.infra.mdms.repository.SchemaDefinitionRepository;
import org.egov.infra.mdms.service.enrichment.SchemaDefinitionEnricher;
import org.egov.infra.mdms.service.validator.SchemaDefinitionValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Service
@Builder
@Slf4j
public class SchemaDefinitionService {

    private SchemaDefinitionRepository schemaDefinitionRepository;
    private ApplicationConfig applicationConfig;
    private SchemaDefinitionEnricher schemaDefinitionEnricher;
    private SchemaDefinitionValidator schemaDefinitionValidator;

    @Autowired
    public SchemaDefinitionService(SchemaDefinitionRepository schemaDefinitionRepository, ApplicationConfig applicationConfig,
                                   SchemaDefinitionEnricher schemaDefinitionEnricher, SchemaDefinitionValidator schemaDefinitionValidator){
        this.schemaDefinitionRepository = schemaDefinitionRepository;
        this.applicationConfig = applicationConfig;
        this.schemaDefinitionEnricher = schemaDefinitionEnricher;
        this.schemaDefinitionValidator = schemaDefinitionValidator;
        
    }

    /**
     * This method processes requests for schema definition creation.
     * @param schemaDefinitionRequest
     * @return
     */
    public List<SchemaDefinition> create(SchemaDefinitionRequest schemaDefinitionRequest) {

        // Set incoming tenantId as state level tenantId as schema is always created at state level
        String tenantId = schemaDefinitionRequest.getSchemaDefinition().getTenantId();

        // Keep the tenant exactly as received
        schemaDefinitionRequest.getSchemaDefinition().setTenantId(tenantId);
        // Validate schema create request
        schemaDefinitionValidator.validateCreateRequest(schemaDefinitionRequest);

        // Enrich schema create request
        schemaDefinitionEnricher.enrichCreateRequest(schemaDefinitionRequest);
        schemaDefinitionRequest.getSchemaDefinition().setOperation("CREATE");

        // Invoke repository method to emit schema creation event
        schemaDefinitionRepository.create(schemaDefinitionRequest);

        return Arrays.asList(schemaDefinitionRequest.getSchemaDefinition());
    }

    public List<SchemaDefinition> delete(SchemaDeleteRequest request) {

        String tenantId = request.getTenantId();

        SchemaDefCriteria criteria = SchemaDefCriteria.builder()
        .tenantId(tenantId)
        .codes(Arrays.asList(request.getCode()))
        .build();

List<SchemaDefinition> schemaDefinitions =
        schemaDefinitionRepository.search(criteria);

if (!schemaDefinitions.isEmpty()) {
    schemaDefinitionRepository.insertAudit(schemaDefinitions.get(0));
}

schemaDefinitionRepository.delete(tenantId, request.getCode());
        return Arrays.asList(
                SchemaDefinition.builder()
                        .tenantId(tenantId)
                        .code(request.getCode())
                        .build()
        );
    }

    /**
     * This method processes the requests for schema definition search.
     * @param schemaDefSearchRequest
     * @return
     */
    public List<SchemaDefinition> search(SchemaDefSearchRequest schemaDefSearchRequest) {

        // Set incoming tenantId as state level tenantId as schema is created at state level
        String tenantId = schemaDefSearchRequest.getSchemaDefCriteria().getTenantId();

        // Keep the tenant exactly as received
        schemaDefSearchRequest.getSchemaDefCriteria().setTenantId(tenantId);    
        // Fetch schema definitions based on the given criteria
       List<SchemaDefinition> schemaDefinitions =
                schemaDefinitionRepository.search(schemaDefSearchRequest.getSchemaDefCriteria());

        if (Boolean.TRUE.equals(schemaDefSearchRequest.getSchemaDefCriteria().getIsGetAllCodes())) {

            Map<String, SchemaDefinition> uniqueCodes = new TreeMap<>(); // ASC order

            schemaDefinitions.forEach(schemaDefinition -> {
                String moduleName = schemaDefinition.getCode().split("\\.")[0];

                if (!uniqueCodes.containsKey(moduleName)) {
                    schemaDefinition.setCode(moduleName);
                    schemaDefinition.setDefinition(null);
                    schemaDefinition.setDescription(null);
                    schemaDefinition.setAuditDetails(null);
                    schemaDefinition.setId(null);
                    schemaDefinition.setTenantId(null);
                    schemaDefinition.setIsActive(null);

                    uniqueCodes.put(moduleName, schemaDefinition);
                }
            });

            schemaDefinitions = new ArrayList<>(uniqueCodes.values());
        }
        

        return schemaDefinitions;
    }

}
