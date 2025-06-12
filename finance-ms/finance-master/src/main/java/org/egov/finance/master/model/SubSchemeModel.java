package org.egov.finance.master.model;

import java.math.BigDecimal;
import java.util.Date;

import org.egov.finance.master.customannotation.SafeHtml;
import org.egov.finance.master.util.MasterConstants;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * SubSchemeModel.java
 * 
 * @author bpattanayak
 * @date 10 Jun 2025
 * 
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SubSchemeModel {
	
	private Long id;

	@NotNull(message = "Code can not be blank")
	private Long scheme;
	
	@NotBlank(message = "Code can not be blank")
	@SafeHtml(message = MasterConstants.INVALID_TEXT_CONTAINS_HTML_TAGS_MSG)
	private String code;
	
	@NotBlank(message = "Name can not be blank")
	@SafeHtml(message = MasterConstants.INVALID_TEXT_CONTAINS_HTML_TAGS_MSG)
	@Size(min = 3,max = 100,message = "Name should be between 100 words")
	private String name;

	@NotNull(message = "Validform can not be blank")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	private Date validfrom;


	@NotNull(message = "Validto can not be blank")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
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
	
	private Long createdBy;
	private Date createdDate;
	private Long lastModifiedBy;
	private Date lastModifiedDate;

}
