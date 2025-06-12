package org.upyog.tp.service;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import org.egov.common.contract.request.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.upyog.tp.config.TreePruningConfiguration;
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


    @Autowired
    private CalculationService calculationService;

    @Autowired
    private DemandRepository demandRepository;

    @Autowired
    private TreePruningConfiguration config;



    /**
     * Create demand by bringing Tree Pruning price from mdms
     *
     * @param treePruningRequest
     * @return
     */

    public List<Demand> createDemand(TreePruningBookingRequest treePruningRequest) {
        if (treePruningRequest == null) {
            throw new IllegalArgumentException("Tree Pruning Booking Request is Empty");
        }

        String tenantId = treePruningRequest.getTreePruningBookingDetail().getTenantId();
        log.info("Creating demand upon approve action for booking no : {}", treePruningRequest.getTreePruningBookingDetail().getBookingNo());
        TreePruningBookingDetail treePruningBookingDetail = treePruningRequest.getTreePruningBookingDetail();
        String consumerCode = treePruningBookingDetail.getBookingNo();
        CalculationType calculationType = calculationService.calculateFee(treePruningRequest.getRequestInfo(), tenantId);
        log.info("Final amount payable after calculation : " + calculationType.getAmount());
        User owner = buildUser(treePruningBookingDetail.getApplicantDetail(), tenantId);
        List<DemandDetail> demandDetails = buildDemandDetails(calculationType.getAmount(), calculationType.getFeeType(),tenantId);
        Demand demand = buildDemand(tenantId, consumerCode, owner, demandDetails);
        log.info("Final demand generation object" + demand.toString());
        return demandRepository.saveDemand(treePruningRequest.getRequestInfo(), Collections.singletonList(demand));
    }

    private User buildUser(ApplicantDetail applicantDetail, String tenantId) {
        return User.builder().name(applicantDetail.getName()).emailId(applicantDetail.getEmailId())
                .mobileNumber(applicantDetail.getMobileNumber()).tenantId(tenantId).build();
    }

    private List<DemandDetail> buildDemandDetails(BigDecimal amountPayable, String feeType, String tenantId) {

        return Collections.singletonList(DemandDetail.builder().collectionAmount(BigDecimal.ZERO)
                .taxAmount(amountPayable).taxHeadMasterCode(feeType)
                .tenantId(tenantId).build());
    }

    private Demand buildDemand(String tenantId, String consumerCode, User owner, List<DemandDetail> demandDetails) {
        return Demand.builder().consumerCode(consumerCode).demandDetails(demandDetails).payer(owner).tenantId(tenantId)
                .taxPeriodFrom(TreePruningUtil.getCurrentTimestamp()).taxPeriodTo(TreePruningUtil.getFinancialYearEnd())
                .consumerType(config.getTreePruningBusinessService())
                .businessService(config.getTpModuleName()).additionalDetails(null).build();
    }



}
