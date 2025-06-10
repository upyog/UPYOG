package org.egov.finance.master.model;

import java.math.BigDecimal;
import java.util.Date;

import org.egov.finance.master.customannotation.SafeHtml;
import org.egov.finance.master.util.MasterConstants;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder.Default;
import lombok.Data;

/**
 * SubSchemeModel.java
 * 
 * @author bpattanayak
 * @date 10 Jun 2025
 * @version 1.0
 */
@Data
public class SubSchemeModel {
	
	private Long id;

	//private Scheme scheme;
	
	@Min(value = 2)
	@Max(value = 50)
	@NotBlank(message = "Code can not be blank")
	@SafeHtml(message = MasterConstants.INVALID_TEXT_CONTAINS_HTML_TAGS_MSG)
	private String code;
	
	@Min(value = 2)
	@Max(value = 50)
	@NotBlank(message = "Name can not be blank")
	@SafeHtml(message = MasterConstants.INVALID_TEXT_CONTAINS_HTML_TAGS_MSG)
	private String name;

	@NotBlank(message = "Validform can not be blank")
	@SafeHtml(message = MasterConstants.INVALID_TEXT_CONTAINS_HTML_TAGS_MSG)
	private Date validfrom;

	@NotBlank(message = "Validto can not be blank")
	@SafeHtml(message = MasterConstants.INVALID_TEXT_CONTAINS_HTML_TAGS_MSG)
	private Date validto;

	
	private Boolean isactive;

	private String department;
	private BigDecimal initialEstimateAmount;
	private String councilLoanProposalNumber;
	private String councilAdminSanctionNumber;
	private String govtLoanProposalNumber;
	private String govtAdminSanctionNumber;
	private Date councilLoanProposalDate;
	private Date councilAdminSanctionDate;
	private Date govtLoanProposalDate;
	private Date govtAdminSanctionDate;

}
