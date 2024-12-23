package org.egov.asset.calculator.repository;

public interface CustomDepreciationRepository {
    void executeBulkDepreciationProcedure(String tenantId);
    void executeSingleAndLegacyDataBulkDepreciationCalProcedure(String tenantId, String assetId);
}