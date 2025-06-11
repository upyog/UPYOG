package org.upyog.tp.service;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.egov.common.contract.request.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.upyog.tp.config.TreePruningConfiguration;
import org.upyog.tp.constant.TreePruningConstants;
import org.upyog.tp.repository.DemandRepository;
import org.upyog.tp.util.TreePruningUtil;
import org.upyog.tp.web.models.ApplicantDetail;
import org.upyog.tp.web.models.Demand;
import org.upyog.tp.web.models.DemandDetail;
import org.upyog.tp.web.models.billing.CalculationType;
import org.upyog.tp.web.models.treePruning.TreePruningBookingDetail;
import org.upyog.tp.web.models.treePruning.TreePruningBookingRequest;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DemandService {


//    @Autowired
//    private CalculationService calculationService;

    @Autowired
    private DemandRepository demandRepository;

    @Autowired
    private TreePruningConfiguration config;



    /**
     * Create demand by bringing tanker price from mdms
     *
     * @param treePruningRequest
     * @return
     */

    public List<Demand> createDemand(TreePruningBookingRequest treePruningRequest) {
        if (treePruningRequest == null) {
            throw new IllegalArgumentException("TreePruning Booking Request is Empty");
        }

        String tenantId = treePruningRequest.getTreePruningBookingDetail().getTenantId();
        log.info("Creating demand upon approve action for bboking no : {}", treePruningRequest.getTreePruningBookingDetail().getBookingNo());
        TreePruningBookingDetail treePruningBookingDetail = treePruningRequest.getTreePruningBookingDetail();
        String consumerCode = treePruningBookingDetail.getBookingNo();
//        String tankerCapacityType = treePruningBookingDetail.getTankerType() + "_"
//                + treePruningBookingDetail.getWaterQuantity();

//        BigDecimal amountPayable = calculationService.calculateFee(treePruningBookingDetail.getTankerQuantity(),
//                tankerCapacityType, treePruningRequest.getRequestInfo(), tenantId);
//        BigDecimal immediateDeliveryFee = calculationService.immediateDeliveryFee(treePruningBookingDetail.getExtraCharge(),
//                treePruningRequest.getRequestInfo(), tenantId);
        BigDecimal amountPayable= BigDecimal.ZERO;
        BigDecimal immediateDeliveryFee = BigDecimal.ZERO;

        log.info("immediateDeliveryFee for tanker booking : {}", immediateDeliveryFee);
        log.info("Final amount payable after calculation : " + amountPayable);

        User owner = buildUser(treePruningBookingDetail.getApplicantDetail(), tenantId);
        List<DemandDetail> demandDetails = buildDemandDetails(amountPayable, tenantId,immediateDeliveryFee);
        Demand demand = buildDemand(tenantId, consumerCode, owner, demandDetails, amountPayable);
        log.info("Final demand generation object" + demand.toString());
        return demandRepository.saveDemand(treePruningRequest.getRequestInfo(), Collections.singletonList(demand));
    }

    private User buildUser(ApplicantDetail applicantDetail, String tenantId) {
        return User.builder().name(applicantDetail.getName()).emailId(applicantDetail.getEmailId())
                .mobileNumber(applicantDetail.getMobileNumber()).tenantId(tenantId).build();
    }

    /**
     * Builds a list of DemandDetail objects for the given payable amounts.
     *
     * Steps:
     * 1. Checks if an immediate delivery fee is applicable:
     *    - If greater than 0, adds a DemandDetail for the immediate delivery fee.
     * 2. Adds a DemandDetail for the main payable amount.
     * 3. Logs the final list of demand details.
     *
     * Parameters:
     * - amountPayable: The main amount to be paid.
     * - tenantId: The tenant ID for which the demand is created.
     * - immediateDeliveryFee: The fee for immediate delivery, if applicable.
     *
     * Returns:
     * - A list of DemandDetail objects containing tax details.
     */
    private List<DemandDetail> buildDemandDetails(BigDecimal amountPayable, String tenantId, BigDecimal immediateDeliveryFee) {
        List<DemandDetail> demandDetails = new LinkedList<>();
        demandDetails.add(DemandDetail.builder().collectionAmount(BigDecimal.ZERO)
                .taxAmount(amountPayable).taxHeadMasterCode(TreePruningConstants.REQUEST_SERVICE_TAX_MASTER_CODE)
                .tenantId(tenantId).build());
        if(immediateDeliveryFee.compareTo(BigDecimal.ZERO) > 0) {
            demandDetails.add(DemandDetail.builder().collectionAmount(BigDecimal.ZERO)
                    .taxAmount(immediateDeliveryFee).taxHeadMasterCode(TreePruningConstants.IMMEDIATE_DELIVERY_TAX_HEAD)
                    .tenantId(tenantId).build());
        }

        log.info("Final demand details: {}", demandDetails);

        return demandDetails;
    }

    private Demand buildDemand(String tenantId, String consumerCode, User owner, List<DemandDetail> demandDetails,
                               BigDecimal amountPayable) {
        return Demand.builder().consumerCode(consumerCode).demandDetails(demandDetails).payer(owner).tenantId(tenantId)
                .taxPeriodFrom(TreePruningUtil.getCurrentTimestamp()).taxPeriodTo(TreePruningUtil.getFinancialYearEnd())
                .consumerType(TreePruningConstants.WATER_TANKER_SERVICE_NAME)
                .businessService(config.getTpModuleName()).additionalDetails(null).build();
    }

}
