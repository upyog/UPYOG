package org.egov.applyworkflow.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.egov.applyworkflow.web.model.BusinessService;
import org.egov.applyworkflow.repository.EgMdmsDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EgMdmsDataService {

    private final EgMdmsDataRepository repository;
    private final ObjectMapper objectMapper;

    @Autowired
    public EgMdmsDataService(EgMdmsDataRepository repository, ObjectMapper objectMapper) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    public BusinessService getData(String tenantId, String uniqueIdentifier) {
        // Fetch JSON from the database
        String jsonData = repository.findDataByUniqueIdentifier(tenantId, uniqueIdentifier);

        // Handle null data
        if (jsonData == null) {
            throw new RuntimeException("No data found for tenantId: " + tenantId + " and uniqueIdentifier: " + uniqueIdentifier);
        }

        try {
            // Map JSON string to BusinessService object
            return objectMapper.readValue(jsonData, BusinessService.class);
        } catch (Exception e) {
            throw new RuntimeException("Error mapping JSON to BusinessService", e);
        }
    }
}