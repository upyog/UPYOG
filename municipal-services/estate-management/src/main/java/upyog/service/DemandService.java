package upyog.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import upyog.config.EstateConfiguration;
import upyog.config.ServiceConstants;
import upyog.repository.DemandRepository;
import upyog.repository.EstateRepository;
import upyog.util.BillingPeriodUtil;
import upyog.util.MdmsUtil;
import upyog.web.models.*;
import upyog.web.models.billing.BillingUser;
import upyog.web.models.billing.Demand;
import upyog.web.models.billing.DemandDetail;

import java.util.Locale;
import java.util.UUID;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DemandService {

    @Autowired
    private EstateConfiguration config;

    @Autowired
    private CalculationService calculationService;

    @Autowired
    private DemandRepository demandRepository;

    @Autowired
    private EstateRepository estateRepository;

    @Autowired
    private MdmsUtil mdmsUtil;



    /**
     * Generates a rent demand for the specified billing date based on the
     * allotment billing cycle, outstanding dues and applicable penalty.
     *
     * @param requestInfo request information
     * @param allotment allotment for which demand is being generated
     * @param billingDate billing execution date
     * @param penaltyRate penalty percentage configured in MDMS
     */
    public void generateDemand(RequestInfo requestInfo,
                               Allotment allotment,
                               LocalDate billingDate,
                               BigDecimal penaltyRate) {

        BillingCycle billingCycle =
                BillingCycle.valueOf(
                        allotment.getBillingCycle().toUpperCase(Locale.ROOT));

        BillingPeriod billingPeriod =
                BillingPeriodUtil.getBillingPeriod(
                        billingDate,
                        billingCycle,
                        allotment.getAgreementEndDate());

        LocalDate periodFrom = billingPeriod.getPeriodFrom();
        LocalDate periodTo = billingPeriod.getPeriodTo();

        List<Demand> existingDemands =
                demandRepository.searchAllDemands(
                        requestInfo,
                        allotment.getTenantId(),
                        allotment.getAssetNo(),
                        config.getBusinessServiceName());

        boolean alreadyGenerated =
                existingDemands.stream()
                        .anyMatch(d ->
                                d.getTaxPeriodFrom().equals(convertToTimestamp(periodFrom))
                                        && d.getTaxPeriodTo().equals(convertToTimestamp(periodTo)));

        if (alreadyGenerated) {

            log.info(
                    "Demand already exists for allotment {}, period {} to {}",
                    allotment.getAllotmentId(),
                    periodFrom,
                    periodTo);

            return;
        }

        BigDecimal currentAmount =
                calculationService.calculateAmount(
                        allotment,
                        periodFrom,
                        periodTo);

        boolean partialCycle =
                allotment.getAgreementEndDate() != null
                        && allotment.getAgreementEndDate().equals(periodTo);

        log.info(
                "Generating demand for allotment {}, period {} to {}, amount {}",
                allotment.getAllotmentId(),
                periodFrom,
                periodTo,
                currentAmount
        );

        List<Demand> unpaidDemands =
                demandRepository.searchDemand(
                        requestInfo,
                        allotment.getTenantId(),
                        allotment.getAssetNo(),
                        config.getBusinessServiceName());

        BigDecimal bookingFeeAmount;
        BigDecimal penaltyAmount = BigDecimal.ZERO;
        BigDecimal finalAmount;

        List<Demand> demandsToUpdate =
                Collections.emptyList();

        if (!unpaidDemands.isEmpty()) {

            BigDecimal previousUnpaid =
                    unpaidDemands.stream()
                            .flatMap(d -> d.getDemandDetails().stream())
                            .map(DemandDetail::getTaxAmount)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

            bookingFeeAmount =
                    previousUnpaid.add(currentAmount);

            penaltyAmount =
                    previousUnpaid
                            .multiply(penaltyRate)
                            .setScale(2, RoundingMode.HALF_UP);

            finalAmount =
                    bookingFeeAmount.add(penaltyAmount);

            unpaidDemands.forEach(d ->
                    d.getDemandDetails().forEach(dd ->
                            dd.setTaxAmount(BigDecimal.ZERO)));

            demandsToUpdate = unpaidDemands;

            log.info(
                    "Penalty applied. Previous unpaid={}, current={}, penalty={}, total={}",
                    previousUnpaid,
                    currentAmount,
                    penaltyAmount,
                    finalAmount
            );

        } else {

            bookingFeeAmount = currentAmount;
            finalAmount = currentAmount;
        }

        BillingUser payer = BillingUser.builder()
                .name(allotment.getAlloteeName())
                .emailId(allotment.getEmailId())
                .mobileNumber(allotment.getMobileNo())
                .tenantId(allotment.getTenantId())
                .build();

        List<DemandDetail> demandDetails =
                new LinkedList<>();

        demandDetails.add(
                DemandDetail.builder()
                        .taxHeadMasterCode(ServiceConstants.EST_BOOKING_FEE)
                        .taxAmount(bookingFeeAmount)
                        .collectionAmount(BigDecimal.ZERO)
                        .tenantId(allotment.getTenantId())
                        .build()
        );

        if (penaltyAmount.compareTo(BigDecimal.ZERO) > 0) {

            demandDetails.add(
                    DemandDetail.builder()
                            .taxHeadMasterCode(ServiceConstants.EST_PENALTY_FEE)
                            .taxAmount(penaltyAmount)
                            .collectionAmount(BigDecimal.ZERO)
                            .tenantId(allotment.getTenantId())
                            .build()
            );
        }

        Demand demand = Demand.builder()
                .consumerCode(allotment.getAssetNo())
                .demandDetails(demandDetails)
                .payer(payer)
                .tenantId(allotment.getTenantId())
                .taxPeriodFrom(convertToTimestamp(periodFrom))
                .taxPeriodTo(convertToTimestamp(periodTo))
                .consumerType(config.getModuleName())
                .businessService(config.getBusinessServiceName())
                .build();

        if (!demandsToUpdate.isEmpty()) {
            demandRepository.updateDemand(
                    requestInfo,
                    demandsToUpdate);
        }

        demandRepository.saveDemand(
                requestInfo,
                List.of(demand));

        String userUuid =
                requestInfo.getUserInfo() != null
                        ? requestInfo.getUserInfo().getUuid()
                        : ServiceConstants.STATUS_SYSTEM;

        long now = System.currentTimeMillis();

        SchedulerLog schedulerLog =
                SchedulerLog.builder()
                        .id(UUID.randomUUID().toString())
                        .allotmentId(allotment.getAllotmentId())
                        .tenantId(allotment.getTenantId())
                        .billingDate(billingDate)
                        .billingPeriodFrom(convertToTimestamp(periodFrom))
                        .billingPeriodTo(convertToTimestamp(periodTo))
                        .amount(finalAmount)
                        .penaltyAmount(penaltyAmount)
                        .paymentType(
                                partialCycle
                                        ? ServiceConstants.PAYMENT_TYPE_PARTIAL
                                        : ServiceConstants.PAYMENT_TYPE_FULL)
                        .status(ServiceConstants.STATUS_PENDING)
                        .createdBy(userUuid)
                        .createdTime(now)
                        .lastModifiedBy(userUuid)
                        .lastModifiedTime(now)
                        .build();

        estateRepository.save(
                config.getSchedulerLogTopic(),
                Map.of("schedulerLog", schedulerLog));

        log.info(
                "Demand generated successfully for allotment {}",
                allotment.getAllotmentId());

    }

    /**
     * Creates an initial demand for the allotment based on the configured
     * billing cycle and agreement period.
     *
     * @param allotmentRequest allotment request details
     * @param generateDemand flag indicating whether demand should be persisted
     * @return generated demand details
     */
    public List<Demand> createDemand(AllotmentRequest allotmentRequest, boolean generateDemand) {

        String tenantId = allotmentRequest.getAllotments().get(0).getTenantId();
        String consumerCode = allotmentRequest.getAllotments().get(0).getAssetNo();
        Allotment allotment = allotmentRequest.getAllotments().get(0);

        User user = allotmentRequest.getRequestInfo().getUserInfo();
        log.info("user-details: {}", user);

        BillingUser owner = BillingUser.builder()
                .name(allotment.getAlloteeName())
                .emailId(allotment.getEmailId())
                .mobileNumber(allotment.getMobileNo())
                .tenantId(tenantId)
                .build();

        BillingCycle billingCycle =
                BillingCycle.valueOf(
                        allotment.getBillingCycle().toUpperCase(Locale.ROOT));

        BillingPeriod billingPeriod =
                BillingPeriodUtil.getBillingPeriod(
                        allotment.getAgreementStartDate(),
                        billingCycle,
                        allotment.getAgreementEndDate());

        LocalDate periodFrom =
                billingPeriod.getPeriodFrom();

        LocalDate periodTo =
                billingPeriod.getPeriodTo();

        BigDecimal amount =
                calculationService.calculateAmount(
                        allotment,
                        periodFrom,
                        periodTo);

        List<DemandDetail> demandDetails = calculationService.calculateDemand(amount, allotment);

        Demand demand = Demand.builder()
                .consumerCode(consumerCode)
                .demandDetails(demandDetails)
                .payer(owner)
                .tenantId(tenantId)
                .taxPeriodFrom(convertToTimestamp(periodFrom))
                .taxPeriodTo(convertToTimestamp(periodTo))
                .consumerType(config.getModuleName())
                .businessService(config.getBusinessServiceName())
                .build();

        List<Demand> demands = new ArrayList<>();
        demands.add(demand);

        if (!generateDemand) {

            BigDecimal totalAmount = demandDetails.stream()
                    .map(DemandDetail::getTaxAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            demand.setAdditionalDetails(totalAmount);

            return demands;
        }

        log.info(
                "Sending call to billing service for generating demand for allotment id: {}",
                consumerCode);

        return demandRepository.saveDemand(
                allotmentRequest.getRequestInfo(),
                demands);
    }

    private Long convertToTimestamp(LocalDate date) {
        return date.atStartOfDay().atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

}
