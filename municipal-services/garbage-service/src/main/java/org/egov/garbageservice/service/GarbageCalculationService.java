package org.egov.garbageservice.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.egov.common.contract.request.RequestInfo;
import org.egov.garbageservice.util.MdmsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.egov.garbageservice.model.GarbageAccount;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class GarbageCalculationService {

    @Autowired
    private MdmsService mdmsService;
    
    @Autowired
	private ObjectMapper objectMapper;

    @Autowired
    private MdmsUtil mdmsUtil;

    public BigDecimal calculateAmount(GarbageAccount garbageAccount,
                                      LocalDate periodFrom,
                                      LocalDate periodTo) {

        RequestInfo requestInfo = new RequestInfo(); // This should be properly populated
        Object mdmsResponse = mdmsService.fetchGarbageFeeFromMdms(requestInfo,
                garbageAccount.getTenantId());
        List<String> errorList = new ArrayList<>();
        ObjectNode calculationBreakdown = objectMapper.createObjectNode();
        BigDecimal monthlyAmount =
                mdmsService.fetchGarbageAmountFromMDMSResponse(
                        mdmsResponse, garbageAccount, errorList, calculationBreakdown
                );

        BigDecimal rebateRate = mdmsUtil.getRebateRate(requestInfo, garbageAccount.getTenantId(), garbageAccount.getGrbgCollectionUnits().get(0).getSpecialCategory());
        BigDecimal rebateAmount = monthlyAmount.multiply(rebateRate);

        return monthlyAmount.subtract(rebateAmount);
    }
}