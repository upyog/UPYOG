package org.egov.finance.inbox.model;

import java.math.BigDecimal;
import java.util.Date;

import org.egov.finance.inbox.customannotation.SafeHtml;
import org.egov.finance.inbox.util.InboxConstants;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

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

	@NotNull(message = "Scheme can not be blank")
	@JsonProperty("schemeId")
	private Long scheme;
	
	@NotBlank(message = "Code can not be blank")
	@SafeHtml(message = InboxConstants.INVALID_TEXT_CONTAINS_HTML_TAGS_MSG)
	private String code;
	
	@NotBlank(message = "Name can not be blank")
	@SafeHtml(message = InboxConstants.INVALID_TEXT_CONTAINS_HTML_TAGS_MSG)
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
