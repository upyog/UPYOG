package org.egov.finance.inbox.model;

import java.util.Date;

import org.egov.finance.inbox.customannotation.SafeHtml;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class FinancialYearModel {

	private Long id;

	@SafeHtml
	private String finYearRange;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
	private Date startingDate;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
	private Date endingDate;

	private Boolean active;

	private Boolean isActiveForPosting;

	private Boolean isClosed;

	private Long createdBy;
	private Date createdDate;
	private Long lastModifiedBy;
	private Date lastModifiedDate;

}
