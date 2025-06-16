package org.egov.finance.voucher.model;

import java.util.Date;

import org.egov.finance.voucher.customannotation.SafeHtml;
import org.egov.finance.voucher.util.BankAccountType;
import org.egov.finance.voucher.util.CommonsConstants;
import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BankaccountModel {
	private Long id;

	private Long bankbranchId;

	private Long glcodeId;

	private Long fundId;

	@SafeHtml
	@NotNull(message = "Account number cannot be null")
	@Length(max = 20)
	//@OptionalPattern(regex = CommonsConstants.numericwithoutspecialchar, message = "Special Characters are not allowed in Accountnumber")
	private String accountnumber;

	@SafeHtml
	@NotNull(message = "Account type is required")
	private String accounttype;

	@SafeHtml
	private String narration;

	@NotNull
	private Boolean isactive;

	@SafeHtml
	private String payTo;

	@NotNull(message = "Bank account type is mandatory")
	private BankAccountType type;

	private Long chequeformatId;

	private Long createdBy;
	private Date createdDate;
	private Long lastModifiedBy;
	private Date lastModifiedDate;

}
