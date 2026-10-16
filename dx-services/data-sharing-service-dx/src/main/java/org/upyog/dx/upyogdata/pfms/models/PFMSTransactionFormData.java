package org.upyog.dx.upyogdata.pfms.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Represents the form data sent to the PFMS data push API.
 * Field names are in PascalCase as required by the PFMS API.
 * Use the from() method to build this from a PFMSTransaction object.
 */
@Getter
@Setter
@Builder
public class PFMSTransactionFormData {

    @JsonProperty("ULBCodeOrPFMSAgencyCode")
    private String ulbCodeOrPfmsAgencyCode;

    @JsonProperty("VoucherNumber")
    private String voucherNumber;

    @JsonProperty("VoucherDate")
    private String voucherDate;

    @JsonProperty("VoucherType")
    private String voucherType;

    @JsonProperty("FinancialYear")
    private String financialYear;

    @JsonProperty("AccountHeadCode")
    private String accountHeadCode;

    @JsonProperty("FunctionCode")
    private String functionCode;

    @JsonProperty("SchemeCode")
    private String schemeCode;

    @JsonProperty("DebitAmount")
    private String debitAmount;

    @JsonProperty("CreditAmount")
    private String creditAmount;

    @JsonProperty("NarrationOrDescription")
    private String narrationOrDescription;

    @JsonProperty("VoucherStatus")
    private String voucherStatus;

    @JsonProperty("ULBBankAccountNumber")
    private String ulbBankAccountNumber;

    @JsonProperty("ULBIFSCCode")
    private String ulbIfscCode;

    @JsonProperty("InstrumentReference")
    private String instrumentReference;

    @JsonProperty("ModeOfTransaction")
    private String modeOfTransaction;

    @JsonProperty("BeneficiaryOrPayeeName")
    private String beneficiaryOrPayeeName;

    @JsonProperty("BeneficiaryAccountNumber")
    private String beneficiaryAccountNumber;

    @JsonProperty("BeneficiaryIFSCCode")
    private String beneficiaryIfscCode;

    @JsonProperty("BeneficiaryType")
    private String beneficiaryType;

    @JsonProperty("ChallanNumber")
    private String challanNumber;

    @JsonProperty("FromAccount")
    private String fromAccount;

    @JsonProperty("ToAccount")
    private String toAccount;

    @JsonProperty("ContraNature")
    private String contraNature;

    @JsonProperty("TransferInstructionReference")
    private String transferInstructionReference;

    @JsonProperty("ReferenceVoucherNumber")
    private String referenceVoucherNumber;

    @JsonProperty("AdjustmentType")
    private String adjustmentType;

    @JsonProperty("RelatedAssetOrLiabilityCode")
    private String relatedAssetOrLiabilityCode;

    @JsonProperty("DebtorOrCreditorReference")
    private String debtorOrCreditorReference;

    @JsonProperty("BasisOfAdjustment")
    private String basisOfAdjustment;

    @JsonProperty("PeriodCovered")
    private String periodCovered;

    @JsonProperty("ClientIP")
    private String clientIp;

    private static final DateTimeFormatter PFMS_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy/MM/dd");

    /**
     * Builds a PFMSTransactionFormData from a PFMSTransaction and the client IP.
     * Formats the voucher date as yyyy/MM/dd as expected by PFMS.
     *
     * @param t        the transaction to convert
     * @param clientIp the IP address of this service, required by PFMS
     * @return ready-to-send form data object
     */
    public static PFMSTransactionFormData from(PFMSTransaction t, String clientIp) {
        return PFMSTransactionFormData.builder()
                .ulbCodeOrPfmsAgencyCode(t.getUlbCodeOrPFMSAgencyCode())
                .voucherNumber(t.getVoucherNumber())
                .voucherDate(t.getVoucherDate().format(PFMS_DATE_FORMAT))
                .voucherType(t.getVoucherType())
                .financialYear(t.getFinancialYear())
                .accountHeadCode(t.getAccountHeadCode() == null ? "" : String.join("|", t.getAccountHeadCode()))
                .functionCode(t.getFunctionCode() == null ? "" : String.join("|", t.getFunctionCode()))
                .schemeCode(t.getSchemeCode() == null ? "" : String.join("|", t.getSchemeCode()))
                .debitAmount(t.getDebitAmount() == null ? "" : t.getDebitAmount().toString())
                .creditAmount(t.getCreditAmount() == null ? "" : t.getCreditAmount().toString())
                .narrationOrDescription(t.getNarrationOrDescription())
                .voucherStatus(t.getVoucherStatus())
                .ulbBankAccountNumber(t.getUlbBankAccountNumber())
                .ulbIfscCode(t.getUlbIFSCCode())
                .instrumentReference(t.getInstrumentReference())
                .modeOfTransaction(t.getModeOfTransaction())
                .beneficiaryOrPayeeName(t.getBeneficiaryOrPayeeName())
                .beneficiaryAccountNumber(t.getBeneficiaryAccountNumber())
                .beneficiaryIfscCode(t.getBeneficiaryIFSCCode())
                .beneficiaryType(t.getBeneficiaryType())
                .challanNumber(t.getChallanNumber())
                .fromAccount(t.getFromAccount())
                .toAccount(t.getToAccount())
                .contraNature(t.getContraNature())
                .transferInstructionReference(t.getTransferInstructionReference())
                .referenceVoucherNumber(t.getReferenceVoucherNumber())
                .adjustmentType(t.getAdjustmentType())
                .relatedAssetOrLiabilityCode(t.getRelatedAssetOrLiabilityCode())
                .debtorOrCreditorReference(t.getDebtorOrCreditorReference())
                .basisOfAdjustment(t.getBasisOfAdjustment())
                .periodCovered(t.getPeriodCovered())
                .clientIp(clientIp)
                .build();
    }
}
