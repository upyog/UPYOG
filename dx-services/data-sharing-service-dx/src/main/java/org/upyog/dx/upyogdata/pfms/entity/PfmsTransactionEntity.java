package org.upyog.dx.upyogdata.pfms.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.upyog.dx.upyogdata.pfms.enums.Status;
import org.upyog.dx.upyogdata.pfms.models.PFMSTransaction;
import org.upyog.dx.upyogdata.pfms.models.PFMSTransactionFormData;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Internal persistence and Kafka model for a PFMS voucher transaction.
 * Flat columns map to {@code ug_pfms_transactions}; extended fields live in {@link PfmsTransactionAdditionalDetails}.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PfmsTransactionEntity {

    private String id;
    private String correlationId;
    private String ulbCodeOrPFMSAgencyCode;
    private String voucherNumber;

    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate voucherDate;

    private String voucherType;
    private String financialYear;
    private PfmsTransactionAdditionalDetails additionalDetails;
    private Status status;
    private LocalDateTime ingestionDate;

    /**
     * Builds a temporary API-shaped view for {@link org.upyog.dx.upyogdata.pfms.models.PFMSTransactionFormData}.
     */
    public PFMSTransaction toPfmsPushView() {
        PFMSTransaction pfmsTransaction = new PFMSTransaction();
        pfmsTransaction.setId(id);
        pfmsTransaction.setCorrelationId(correlationId);
        pfmsTransaction.setUlbCodeOrPFMSAgencyCode(ulbCodeOrPFMSAgencyCode);
        pfmsTransaction.setVoucherNumber(voucherNumber);
        pfmsTransaction.setVoucherDate(voucherDate);
        pfmsTransaction.setVoucherType(voucherType);
        pfmsTransaction.setFinancialYear(financialYear);
        pfmsTransaction.setStatus(status);
        pfmsTransaction.setIngestionDate(ingestionDate);

        if (additionalDetails != null) {
            pfmsTransaction.setAccountHeadCode(additionalDetails.getAccountHeadCode());
            pfmsTransaction.setFunctionCode(additionalDetails.getFunctionCode());
            pfmsTransaction.setSchemeCode(additionalDetails.getSchemeCode());
            pfmsTransaction.setDebitAmount(additionalDetails.getDebitAmount());
            pfmsTransaction.setCreditAmount(additionalDetails.getCreditAmount());
            pfmsTransaction.setNarrationOrDescription(additionalDetails.getNarrationOrDescription());
            pfmsTransaction.setVoucherStatus(additionalDetails.getVoucherStatus());
            pfmsTransaction.setUlbBankAccountNumber(additionalDetails.getUlbBankAccountNumber());
            pfmsTransaction.setUlbIFSCCode(additionalDetails.getUlbIFSCCode());
            pfmsTransaction.setInstrumentReference(additionalDetails.getInstrumentReference());
            pfmsTransaction.setModeOfTransaction(additionalDetails.getModeOfTransaction());
            pfmsTransaction.setBeneficiaryOrPayeeName(additionalDetails.getBeneficiaryOrPayeeName());
            pfmsTransaction.setBeneficiaryAccountNumber(additionalDetails.getBeneficiaryAccountNumber());
            pfmsTransaction.setBeneficiaryIFSCCode(additionalDetails.getBeneficiaryIFSCCode());
            pfmsTransaction.setBeneficiaryType(additionalDetails.getBeneficiaryType());
            pfmsTransaction.setChallanNumber(additionalDetails.getChallanNumber());
            pfmsTransaction.setFromAccount(additionalDetails.getFromAccount());
            pfmsTransaction.setToAccount(additionalDetails.getToAccount());
            pfmsTransaction.setContraNature(additionalDetails.getContraNature());
            pfmsTransaction.setTransferInstructionReference(additionalDetails.getTransferInstructionReference());
            pfmsTransaction.setReferenceVoucherNumber(additionalDetails.getReferenceVoucherNumber());
            pfmsTransaction.setAdjustmentType(additionalDetails.getAdjustmentType());
            pfmsTransaction.setRelatedAssetOrLiabilityCode(additionalDetails.getRelatedAssetOrLiabilityCode());
            pfmsTransaction.setDebtorOrCreditorReference(additionalDetails.getDebtorOrCreditorReference());
            pfmsTransaction.setBasisOfAdjustment(additionalDetails.getBasisOfAdjustment());
            pfmsTransaction.setPeriodCovered(additionalDetails.getPeriodCovered());
        }
        return pfmsTransaction;
    }

    /**
     * Converts this entity to PFMS outbound form fields (does not merge unknown JSONB keys).
     */
    public PFMSTransactionFormData toFormData(String clientIp) {
        return PFMSTransactionFormData.from(toPfmsPushView(), clientIp);
    }
}
