package org.upyog.dx.upyogdata.pfms.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.upyog.dx.upyogdata.pfms.entity.PfmsDataEntity;
import org.upyog.dx.upyogdata.pfms.entity.PfmsTransactionAdditionalDetails;
import org.upyog.dx.upyogdata.pfms.entity.PfmsTransactionEntity;
import org.upyog.dx.upyogdata.pfms.enums.Status;
import org.upyog.dx.upyogdata.pfms.models.PFMSCreateTransactionKafkaMessage;
import org.upyog.dx.upyogdata.pfms.models.PFMSCreateTransactionRequest;
import org.upyog.dx.upyogdata.pfms.models.PFMSData;
import org.upyog.dx.upyogdata.pfms.models.PFMSTransaction;
import org.upyog.dx.upyogdata.util.DxUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Maps the public PFMS API contract to internal persistence/Kafka entity models.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class PFMSTransactionTransformer {

    /**
     * Builds the full Kafka envelope from an inbound create request.
     */
    public PFMSCreateTransactionKafkaMessage toKafkaMessage(PFMSCreateTransactionRequest request) {
        List<PfmsDataEntity> dataEntities = request.getData().stream()
                .map(this::toDataEntity)
                .collect(Collectors.toList());
        log.debug("PFMS API mapped to Kafka entities | dataBatches={}", dataEntities.size());
        return new PFMSCreateTransactionKafkaMessage(request.getRequestInfo(), dataEntities);
    }

    private PfmsDataEntity toDataEntity(PFMSData data) {
        String correlationId = DxUtils.getRandomUUID();
        List<PfmsTransactionEntity> transactions = data.getTransactions().stream()
                .map(transaction -> toEntity(transaction, correlationId))
                .collect(Collectors.toList());

        return PfmsDataEntity.builder()
                .id(correlationId)
                .state(data.getState())
                .ward(data.getWard())
                .module(data.getModule())
                .date(data.getDate())
                .ulb(data.getUlb())
                .targetDestination(data.getTargetDestination())
                .transactions(transactions)
                .build();
    }

    /**
     * Maps one API transaction to a persistence entity with internally assigned metadata.
     */
    public PfmsTransactionEntity toEntity(PFMSTransaction transaction, String correlationId) {
        return PfmsTransactionEntity.builder()
                .id(DxUtils.getRandomUUID())
                .correlationId(correlationId)
                .ulbCodeOrPFMSAgencyCode(transaction.getUlbCodeOrPFMSAgencyCode())
                .voucherNumber(transaction.getVoucherNumber())
                .voucherDate(transaction.getVoucherDate())
                .voucherType(transaction.getVoucherType())
                .financialYear(transaction.getFinancialYear())
                .additionalDetails(toAdditionalDetails(transaction))
                .status(Status.INITIATED)
                .ingestionDate(null)
                .build();
    }

    /**
     * Copies non-flat API fields into the JSONB {@code additionaldetails} object.
     */
    public PfmsTransactionAdditionalDetails toAdditionalDetails(PFMSTransaction transaction) {
        return PfmsTransactionAdditionalDetails.builder()
                .accountHeadCode(transaction.getAccountHeadCode())
                .functionCode(transaction.getFunctionCode())
                .schemeCode(transaction.getSchemeCode())
                .debitAmount(transaction.getDebitAmount())
                .creditAmount(transaction.getCreditAmount())
                .narrationOrDescription(transaction.getNarrationOrDescription())
                .voucherStatus(transaction.getVoucherStatus())
                .ulbBankAccountNumber(transaction.getUlbBankAccountNumber())
                .ulbIFSCCode(transaction.getUlbIFSCCode())
                .instrumentReference(transaction.getInstrumentReference())
                .modeOfTransaction(transaction.getModeOfTransaction())
                .beneficiaryOrPayeeName(transaction.getBeneficiaryOrPayeeName())
                .beneficiaryAccountNumber(transaction.getBeneficiaryAccountNumber())
                .beneficiaryIFSCCode(transaction.getBeneficiaryIFSCCode())
                .beneficiaryType(transaction.getBeneficiaryType())
                .challanNumber(transaction.getChallanNumber())
                .fromAccount(transaction.getFromAccount())
                .toAccount(transaction.getToAccount())
                .contraNature(transaction.getContraNature())
                .transferInstructionReference(transaction.getTransferInstructionReference())
                .referenceVoucherNumber(transaction.getReferenceVoucherNumber())
                .adjustmentType(transaction.getAdjustmentType())
                .relatedAssetOrLiabilityCode(transaction.getRelatedAssetOrLiabilityCode())
                .debtorOrCreditorReference(transaction.getDebtorOrCreditorReference())
                .basisOfAdjustment(transaction.getBasisOfAdjustment())
                .periodCovered(transaction.getPeriodCovered())
                .build();
    }
}
