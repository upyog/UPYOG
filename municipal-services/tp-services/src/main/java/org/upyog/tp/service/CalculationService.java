package org.upyog.tp.service;
import java.math.BigDecimal;
import java.util.List;

import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.upyog.tp.constant.TreePruningConstants;
import org.upyog.tp.util.MdmsUtil;
import org.upyog.tp.util.TreePruningUtil;
import org.upyog.tp.web.models.billing.CalculationType;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@Service
public class CalculationService {

    @Autowired
    private MdmsUtil mdmsUtil;

    public CalculationType calculateFee(RequestInfo requestInfo, String tenantId) {
        List<CalculationType> calculationTypes = mdmsUtil.getCalculationType(requestInfo,TreePruningUtil.extractTenantId(tenantId),
                TreePruningConstants.MDMS_MODULE_NAME);

        if (calculationTypes.isEmpty()) {
            log.info("No calculationTypes found for Tree Pruning booking.");
            throw new CustomException("FEE_NOT_FOUND", "Fee not found for application type: ");
        }

        log.info("calculationTypes for Tree Pruning booking : {}", calculationTypes);
        BigDecimal treePruningFee = calculationTypes.get(0).getAmount();
        calculationTypes.get(0).setAmount(treePruningFee);

        return calculationTypes.get(0);
    }
}
