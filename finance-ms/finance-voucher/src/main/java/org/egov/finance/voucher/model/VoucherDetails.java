package org.egov.finance.voucher.model;

import java.math.BigDecimal;

import org.egov.finance.voucher.customannotation.SafeHtml;
import org.egov.finance.voucher.entity.AccountDetailType;
import org.egov.finance.voucher.entity.CChartOfAccounts;

import lombok.Data;

@Data
public class VoucherDetails {

	private Long functionIdDetail;
	private String functionDetail;
	private Long glcodeIdDetail;
	@SafeHtml
	private String glcodeDetail;
	@SafeHtml
	private String accounthead;
	private BigDecimal debitAmountDetail = BigDecimal.ZERO;
	private BigDecimal creditAmountDetail = BigDecimal.ZERO;

	private CChartOfAccounts glcode;
	private AccountDetailType detailType;
	@SafeHtml
	private String detailTypeName;
	private Integer detailKeyId;
	@SafeHtml
	private String detailKey;
	@SafeHtml
	private String detailCode;
	@SafeHtml
	private String detailName;
	private BigDecimal amount = BigDecimal.ZERO;
	@SafeHtml
	private String subledgerCode;
	@SafeHtml
	private String isSubledger;

}
