package org.egov.garbageservice.service;

import lombok.extern.slf4j.Slf4j;
import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.User;
import org.egov.garbageservice.config.GarbageServiceConfig;
import org.egov.garbageservice.web.models.bill.Demand;
import org.egov.garbageservice.web.models.bill.DemandDetail;
import org.egov.garbageservice.web.models.AmountCalculationResult;
import org.egov.garbageservice.web.models.GarbageAccount;
import org.egov.garbageservice.web.models.GarbageAccountRequest;
import org.egov.garbageservice.web.models.SchedulerLog;
import org.egov.garbageservice.kafka.Producer;
import org.egov.garbageservice.repository.DemandRepository;
import org.egov.garbageservice.repository.GarbageAccountRepository;
import org.egov.garbageservice.util.GrbgUtils;
import org.egov.garbageservice.util.MdmsUtil;
import org.egov.garbageservice.util.ServiceConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

@Service
@Slf4j
public class DemandService {

    @Autowired
    private GarbageCalculationService calculationService;

    @Autowired
    @Qualifier("billDemandRepository")
    private org.egov.garbageservice.web.models.bill.DemandRepository billDemandRepository;

    @Autowired
    private DemandRepository demandRepository;

    @Autowired
    private Producer producer;

    @Autowired
    private GarbageServiceConfig config;

    @Autowired
    private MdmsUtil mdmsUtil;

    @Autowired
    private GarbageAccountRepository garbageAccountRepository;

    /**
     * Generates a monthly billing demand for the specified garbage account.
     *
     * <p>The demand generation process follows these steps:
     * <ol>
     *   <li>Calculates the billing period for the current month.</li>
     *   <li>Checks if a demand already exists for the given period to prevent duplicates.</li>
     *   <li>Calculates the current month's fee, including applicable rebates, via {@link GarbageCalculationService}.</li>
     *   <li>Retrieves any previous unpaid demands to calculate cumulative rental fees and penalties.</li>
     *   <li>Assembles the {@link DemandDetail} elements (tax, penalty, and rebate).</li>
     *   <li>Saves the new demand and optionally updates the history of unpaid demands.</li>
     *   <li>Logs the generation event to Kafka for asynchronous tracking.</li>
     * </ol>
     *
     * @param requestInfo    the contextual information for the API request
     * @param garbageAccount the garbage account for which the demand is being generated
     * @param billingDate    the date for which the billing cycle applies
     */

    public void generateDemand(RequestInfo requestInfo,
                               GarbageAccount garbageAccount,
                               LocalDate billingDate) {

        LocalDate periodFrom = billingDate;
        LocalDate periodTo = billingDate.plusMonths(1).minusDays(1);

        String userUuid =
                requestInfo.getUserInfo() != null
                        ? requestInfo.getUserInfo().getUuid()
                        : ServiceConstants.STATUS_SYSTEM;

        long now = System.currentTimeMillis();

        List<Demand> existingDemands =
                demandRepository.searchAllDemands(
                        requestInfo,
                        garbageAccount.getTenantId(),
                        garbageAccount.getGrbgApplicationNumber(),
                        config.getBusinessService(),
                        false);

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

        AmountCalculationResult currentAmount =
                calculationService.calculateAmount(garbageAccount);

        log.info(
                "Generating demand for garbage account {}, period {} to {}, amount {}",
                garbageAccount.getGrbgApplicationNumber(),
                periodFrom,
                periodTo,
                currentAmount.getPayableAmount()
        );

        List<Demand> unpaidDemands =
                demandRepository.searchAllDemands(
                        requestInfo,
                        garbageAccount.getTenantId(),
                        garbageAccount.getGrbgApplicationNumber(),
                        config.getBusinessService(),
                        true);

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
                    previousUnpaid.add(currentAmount.getPayableAmount());

            BigDecimal penaltyRate = mdmsUtil.getPenaltyRate(requestInfo, garbageAccount.getTenantId());
            penaltyAmount =
                    previousUnpaid
                            .multiply(penaltyRate)
                            .setScale(2, RoundingMode.HALF_UP);

            finalAmount =
                    rentalFeeAmount.add(penaltyAmount);

            demandsToUpdate = unpaidDemands;

            log.info(
                    "Penalty applied. Previous unpaid={}, current={}, penalty={}, total={}",
                    previousUnpaid,
                    currentAmount,
                    penaltyAmount,
                    finalAmount
            );

        } else {
            finalAmount = currentAmount.getPayableAmount();
        }


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
                        .taxAmount(currentAmount.getTotalAmount().abs())
                        .collectionAmount(BigDecimal.ZERO)
                        .tenantId(garbageAccount.getTenantId())
                        .build()
        );

        if (penaltyAmount.compareTo(BigDecimal.ZERO) > 0) {

            demandDetails.add(
                    DemandDetail.builder()
                            .taxHeadMasterCode(ServiceConstants.GRBG_PENALTY_FEE)
                            .taxAmount(penaltyAmount.abs())
                            .collectionAmount(BigDecimal.ZERO)
                            .tenantId(garbageAccount.getTenantId())
                            .build()
            );
        }

        if (currentAmount.getRebateAmount().compareTo(BigDecimal.ZERO) > 0) {
            demandDetails.add(
                    DemandDetail.builder()
                            .taxHeadMasterCode(ServiceConstants.GRBG_REBATE_FEE)
                            .taxAmount(currentAmount.getRebateAmount().negate())
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

        SchedulerLog schedulerLog =
                SchedulerLog.builder()
                        .id(UUID.randomUUID().toString())
                        .garbageAccountId(garbageAccount.getGrbgApplicationNumber())
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

        // Update allotment due date and status to PENDING_FOR_PAYMENT
        try {
            garbageAccount.setDueDate(periodTo);
            garbageAccount.setStatus(ServiceConstants.STATUS_PENDING_FOR_PAYMENT);
            if (garbageAccount.getGrbgApplication() != null) {
                garbageAccount.getGrbgApplication().setStatus(ServiceConstants.STATUS_PENDING_FOR_PAYMENT);
            }
            String updaterUuid = requestInfo.getUserInfo() != null ? requestInfo.getUserInfo().getUuid() : ServiceConstants.STATUS_SYSTEM;
            if (garbageAccount.getAuditDetails() != null) {
                garbageAccount.getAuditDetails().setLastModifiedBy(updaterUuid);
                garbageAccount.getAuditDetails().setLastModifiedTime(Instant.now().toEpochMilli());
            } else {
                garbageAccount.setAuditDetails(GrbgUtils.getAuditDetails(updaterUuid, false));
            }
            GarbageAccountRequest garbageAccountRequest = new GarbageAccountRequest(requestInfo, List.of(garbageAccount), false, false);
            garbageAccountRepository.save(config.getUpdateGarbageAccountTopic(), garbageAccountRequest);
            log.info("Updated Garbage Account due date to {} and status to PENDING_FOR_PAYMENT for Garbage Account Id: {}", periodTo, garbageAccount.getGrbgApplicationNumber());
        } catch (Exception e) {
            log.error("Failed to update allotment due date and status on demand generation: {}", e.getMessage(), e);
        }

        log.info(
                "Demand generated successfully for garbage account {}",
                garbageAccount.getGrbgApplicationNumber());
    }

    /**
     * Appends a penalty tax head to an existing demand.
     *
     * @param demand   the {@link Demand} to update with the penalty
     * @param tenantId the tenant ID under which the penalty is assessed
     * @param penalty  the {@link BigDecimal} amount of the penalty
     */

    public void addPenaltyTaxHead(Demand demand, String tenantId, BigDecimal penalty) {
        demand.getDemandDetails().add(DemandDetail.builder()
                .taxHeadMasterCode(ServiceConstants.GRBG_PENALTY_FEE)
                .taxAmount(penalty)
                .collectionAmount(BigDecimal.ZERO)
                .tenantId(tenantId)
                .build());
    }

    /**
     * Persists updates to a list of existing demands using the billing repository.
     *
     * @param requestInfo the contextual information for the API request
     * @param demands     the list of {@link Demand} objects to be updated
     */

    public void updateDemand(RequestInfo requestInfo, List<Demand> demands) {
        billDemandRepository.updateDemand(requestInfo, demands);
    }

    /**
     * Cancels the specified demands for a given tenant and business service.
     *
     * @param tenantId        the tenant ID where the demands exist
     * @param demandIds       a {@link Set} of unique demand IDs to cancel
     * @param requestInfo     the contextual information for the API request
     * @param businessService the associated business service for the demands
     */

    public void cancelDemand(String tenantId, Set<String> demandIds, RequestInfo requestInfo, String businessService) {
    }

    /**
     * Converts a given {@link LocalDate} to its corresponding epoch timestamp in milliseconds.
     *
     * @param date the date to convert
     * @return the epoch timestamp in milliseconds
     */

    private Long convertToTimestamp(LocalDate date) {
        return date.atStartOfDay().atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli();
    }
}