package org.egov.garbageservice.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.User;
import org.egov.garbageservice.config.GarbageServiceConfig;
import org.egov.garbageservice.contract.bill.Demand;
import org.egov.garbageservice.contract.bill.DemandDetail;
import org.egov.garbageservice.model.GarbageAccount;
import org.egov.garbageservice.model.SchedulerLog;
import org.egov.garbageservice.producer.Producer;
import org.egov.garbageservice.repository.DemandRepository;
import org.egov.garbageservice.util.MdmsUtil;
import org.egov.garbageservice.util.ServiceConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DemandService {

    @Autowired
    private GarbageCalculationService calculationService;

    @Autowired
    @Qualifier("billDemandRepository")
    private org.egov.garbageservice.contract.bill.DemandRepository billDemandRepository;

    @Autowired
    private DemandRepository demandRepository;

    @Autowired
    private Producer producer;

    @Autowired
    private GarbageServiceConfig config;

    @Autowired
    private MdmsUtil mdmsUtil;

    public void generateDemand(RequestInfo requestInfo,
                               GarbageAccount garbageAccount,
                               LocalDate billingDate) {

        LocalDate periodFrom = billingDate.withDayOfMonth(1);
        LocalDate periodTo = billingDate.withDayOfMonth(billingDate.lengthOfMonth());

        List<Demand> existingDemands =
                demandRepository.searchAllDemands(
                        requestInfo,
                        garbageAccount.getTenantId(),
                        garbageAccount.getGrbgApplicationNumber(),
                        config.getBusinessService());

        boolean alreadyGenerated =
                existingDemands.stream()
                        .anyMatch(d ->
                                d.getTaxPeriodFrom().equals(convertToTimestamp(periodFrom))
                                        && d.getTaxPeriodTo().equals(convertToTimestamp(periodTo)));

        if (alreadyGenerated) {
            log.info(
                    "Demand already exists for garbage account {}, period {} to {}",
                    garbageAccount.getGrbgApplicationNumber(),
                    periodFrom,
                    periodTo);
            return;
        }

        BigDecimal currentAmount =
                calculationService.calculateAmount(
                        garbageAccount,
                        periodFrom,
                        periodTo);

        log.info(
                "Generating demand for garbage account {}, period {} to {}, amount {}",
                garbageAccount.getGrbgApplicationNumber(),
                periodFrom,
                periodTo,
                currentAmount
        );

        List<Demand> unpaidDemands =
                demandRepository.searchDemand(
                        requestInfo,
                        garbageAccount.getTenantId(),
                        garbageAccount.getGrbgApplicationNumber(),
                        config.getBusinessService());

        BigDecimal rentalFeeAmount;
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

            rentalFeeAmount =
                    previousUnpaid.add(currentAmount);

            penaltyAmount =
                    previousUnpaid
                            .multiply(config.getPenaltyRate())
                            .setScale(2, RoundingMode.HALF_UP);

            finalAmount =
                    rentalFeeAmount.add(penaltyAmount);

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
            rentalFeeAmount = currentAmount;
            finalAmount = currentAmount;
        }

        BigDecimal rebateRate = mdmsUtil.getRebateRate(requestInfo, garbageAccount.getTenantId(), garbageAccount.getGrbgCollectionUnits().get(0).getSpecialCategory());
        BigDecimal rebateAmount = rentalFeeAmount.multiply(rebateRate).setScale(2, RoundingMode.HALF_UP);
        finalAmount = finalAmount.subtract(rebateAmount);

        User payer = User.builder()
                .name(garbageAccount.getName())
                .emailId(garbageAccount.getEmailId())
                .mobileNumber(garbageAccount.getMobileNumber())
                .tenantId(garbageAccount.getTenantId())
                .build();

        List<DemandDetail> demandDetails =
                new LinkedList<>();

        demandDetails.add(
                DemandDetail.builder()
                        .taxHeadMasterCode(ServiceConstants.GRBG_TAX_HEAD_CODE)
                        .taxAmount(rentalFeeAmount)
                        .collectionAmount(BigDecimal.ZERO)
                        .tenantId(garbageAccount.getTenantId())
                        .build()
        );

        if (penaltyAmount.compareTo(BigDecimal.ZERO) > 0) {

            demandDetails.add(
                    DemandDetail.builder()
                            .taxHeadMasterCode(ServiceConstants.GRBG_PENALTY_FEE)
                            .taxAmount(penaltyAmount)
                            .collectionAmount(BigDecimal.ZERO)
                            .tenantId(garbageAccount.getTenantId())
                            .build()
            );
        }

        if (rebateAmount.compareTo(BigDecimal.ZERO) > 0) {
            demandDetails.add(
                    DemandDetail.builder()
                            .taxHeadMasterCode(ServiceConstants.GRBG_REBATE_FEE)
                            .taxAmount(rebateAmount.negate())
                            .collectionAmount(BigDecimal.ZERO)
                            .tenantId(garbageAccount.getTenantId())
                            .build()
            );
        }

        Demand demand = Demand.builder()
                .consumerCode(garbageAccount.getGrbgApplicationNumber())
                .demandDetails(demandDetails)
                .payer(payer)
                .tenantId(garbageAccount.getTenantId())
                .taxPeriodFrom(convertToTimestamp(periodFrom))
                .taxPeriodTo(convertToTimestamp(periodTo))
                .consumerType(config.getModuleName())
                .businessService(config.getBusinessService())
                .build();

        if (!demandsToUpdate.isEmpty()) {
            billDemandRepository.updateDemand(
                    requestInfo,
                    demandsToUpdate);
        }

        billDemandRepository.saveDemand(
                requestInfo,
                Collections.singletonList(demand));

        String userUuid =
                requestInfo.getUserInfo() != null
                        ? requestInfo.getUserInfo().getUuid()
                        : ServiceConstants.STATUS_SYSTEM;

        long now = System.currentTimeMillis();

        SchedulerLog schedulerLog =
                SchedulerLog.builder()
                        .id(UUID.randomUUID().toString())
                        .garbageAccountId(String.valueOf(garbageAccount.getId()))
                        .tenantId(garbageAccount.getTenantId())
                        .billingDate(billingDate)
                        .billingPeriodFrom(convertToTimestamp(periodFrom))
                        .billingPeriodTo(convertToTimestamp(periodTo))
                        .amount(finalAmount)
                        .penaltyAmount(penaltyAmount)
                        .paymentType(ServiceConstants.PAYMENT_TYPE_FULL)
                        .status(ServiceConstants.STATUS_PENDING)
                        .createdBy(userUuid)
                        .createdTime(now)
                        .lastModifiedBy(userUuid)
                        .lastModifiedTime(now)
                        .build();

        producer.push(
                config.getSchedulerLogTopic(),
                Map.of("schedulerLog", schedulerLog));

        log.info(
                "Demand generated successfully for garbage account {}",
                garbageAccount.getGrbgApplicationNumber());
    }

    public void addPenaltyTaxHead(Demand demand, String tenantId, BigDecimal penalty) {
        demand.getDemandDetails().add(DemandDetail.builder()
                .taxHeadMasterCode(ServiceConstants.GRBG_PENALTY_FEE)
                .taxAmount(penalty)
                .collectionAmount(BigDecimal.ZERO)
                .tenantId(tenantId)
                .build());
    }

    public void updateDemand(RequestInfo requestInfo, List<Demand> demands) {
        billDemandRepository.updateDemand(requestInfo, demands);
    }

    public List<Demand> searchDemand(String tenantId, Set<String> consumerCodes, RequestInfo requestInfo, String businessService) {
        return demandRepository.searchDemand(requestInfo, tenantId, consumerCodes.iterator().next(), businessService);
    }

    public void cancelDemand(String tenantId, Set<String> demandIds, RequestInfo requestInfo, String businessService) {
    }

    private Long convertToTimestamp(LocalDate date) {
        return date.atStartOfDay().atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli();
    }
}