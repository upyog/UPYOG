package org.egov.garbageservice.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.egov.common.contract.request.RequestInfo;
import org.egov.garbageservice.web.models.AmountCalculationResult;
import org.egov.garbageservice.web.models.GarbageAccount;
import org.egov.garbageservice.util.MdmsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Service class responsible for calculating garbage collection fees and applicable rebates.
 *
 * <p>This service interacts with the Master Data Management Service (MDMS) to retrieve
 * dynamic fee configurations and calculates the final payable amount for a garbage account.
 */
@Service
public class GarbageCalculationService {

    @Autowired
    private MdmsService mdmsService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MdmsUtil mdmsUtil;

    /**
     * Calculates the billing amount for the given garbage account by retrieving
     * the applicable monthly fee from MDMS and applying any eligible rebate.
     *
     * <p>The calculation performs the following steps:
     * <ol>
     *   <li>Fetches the garbage fee configuration from MDMS based on the tenant ID.</li>
     *   <li>Determines the base monthly charge for the account from the MDMS response.</li>
     *   <li>Retrieves the applicable rebate rate based on the special category of the collection unit.</li>
     *   <li>Calculates the rebate amount by applying the rebate rate to the monthly amount.</li>
     *   <li>Calculates and returns the final payable amount.</li>
     * </ol>
     *
     * @param garbageAccount the garbage account for which the billing amount is calculated
     * @return an {@link AmountCalculationResult} containing the monthly amount,
     * rebate amount, and final payable amount
     */
    public AmountCalculationResult calculateAmount(GarbageAccount garbageAccount) {

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

        BigDecimal payableAmount = monthlyAmount.subtract(rebateAmount);

        return new AmountCalculationResult(
                monthlyAmount,
                rebateAmount,
                payableAmount);
    }
}