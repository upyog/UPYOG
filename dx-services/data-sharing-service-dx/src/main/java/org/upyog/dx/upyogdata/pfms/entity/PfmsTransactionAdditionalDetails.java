package org.upyog.dx.upyogdata.pfms.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

/**
 * Variable PFMS transaction attributes stored in PostgreSQL {@code additionaldetails} JSONB.
 * Populated internally from the API contract; not exposed on {@link org.upyog.dx.upyogdata.pfms.models.PFMSTransaction}.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PfmsTransactionAdditionalDetails {

    private List<String> accountHeadCode;
    private List<String> functionCode;
    private List<String> schemeCode;
    private BigDecimal debitAmount;
    private BigDecimal creditAmount;
    private String narrationOrDescription;
    private String voucherStatus;
    private String ulbBankAccountNumber;
    private String ulbIFSCCode;
    private String instrumentReference;
    private String modeOfTransaction;
    private String beneficiaryOrPayeeName;
    private String beneficiaryAccountNumber;
    private String beneficiaryIFSCCode;
    private String beneficiaryType;
    private String challanNumber;
    private String fromAccount;
    private String toAccount;
    private String contraNature;
    private String transferInstructionReference;
    private String referenceVoucherNumber;
    private String adjustmentType;
    private String relatedAssetOrLiabilityCode;
    private String debtorOrCreditorReference;
    private String basisOfAdjustment;
    private String periodCovered;
}
