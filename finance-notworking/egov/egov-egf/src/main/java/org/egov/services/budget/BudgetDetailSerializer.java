package org.egov.services.budget;

import org.egov.model.budget.BudgetDetail;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class BudgetDetailSerializer {
    
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    public static String serializeBudgetDetail(BudgetDetail budgetDetail) {
        try {
            // Serialize the BudgetDetail object to JSON
            return objectMapper.writeValueAsString(budgetDetail);
        } catch (JsonProcessingException e) {
            // Handle serialization error
            throw new RuntimeException("Error serializing BudgetDetail object to JSON", e);
        }
    }


}