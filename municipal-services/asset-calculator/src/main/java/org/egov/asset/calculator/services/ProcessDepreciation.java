package org.egov.asset.calculator.services;


import lombok.extern.slf4j.Slf4j;
import org.egov.asset.calculator.repository.CustomDepreciationRepository;
import org.egov.asset.calculator.repository.DepreciationDetailRepository;
import org.egov.asset.calculator.web.models.DepreciationDetail;
import org.egov.asset.calculator.utils.CalculatorConstants;
import org.egov.tracer.model.CustomException;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@Slf4j
public class ProcessDepreciation {

    private final DepreciationDetailRepository depreciationDetailRepository;
    private final CustomDepreciationRepository customDepreciationRepository;

    public ProcessDepreciation(DepreciationDetailRepository depreciationDetailRepository,
                               CustomDepreciationRepository customDepreciationRepository) {
        this.depreciationDetailRepository = depreciationDetailRepository;
        this.customDepreciationRepository = customDepreciationRepository;
    }


    /**
     * Executes the bulk depreciation calculation at DB level.
     *
     * @param tenantId The tenant ID.
     */
    public void executeBulkDepreciationProcedure(String tenantId) {
        try {
            customDepreciationRepository.executeBulkDepreciationProcedure(tenantId);
            log.info("Procedure executeBulkDepreciationProcedure called");
        } catch (Exception e) {
            throw new CustomException(CalculatorConstants.PROCESSING_ERROR, CalculatorConstants.PROCESSING_ERROR + tenantId);
        }
    }

    /**
     * Executes the single and legacy date depreciation calculation at DB level.
     *
     * @param tenantId The tenant ID.
     * @param assetId  The ID of the asset.
     * @return Success message.
     */
    public String executeSingleAndLegacyDepreciationProcedure(String tenantId, String assetId) {
        try {
            customDepreciationRepository.executeSingleAndLegacyDataBulkDepreciationCalProcedure(tenantId, assetId);
            return CalculatorConstants.SUCCESS_MESSAGE + tenantId + ", asset: " + assetId;
        } catch (Exception e) {
            throw new CustomException(CalculatorConstants.PROCESSING_ERROR,
                    CalculatorConstants.PROCESSING_ERROR + tenantId + ", asset: " + assetId);
        }
    }

    public List<DepreciationDetail> getDepreciationDetails(String assetId) {
        return depreciationDetailRepository.findByAssetId(assetId);
    }
}
