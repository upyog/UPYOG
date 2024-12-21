package org.egov.asset.calculator.repository;

public interface CustomDepreciationRepository {
    void executeBulkDepreciationProcedure(String tenantId);
    void executeSingleAndLegacyDateDepreciationCalProcedure(String tenantId, String assetId);
}