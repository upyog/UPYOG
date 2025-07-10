package org.egov.finance.master.model;

import java.util.Date;

import org.egov.finance.master.customannotation.SafeHtml;
import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ChartOfAccountsModel {

	private Long id;

	@NotNull
	@Length(max = 50)
	@SafeHtml
	private String glcode;

	@NotNull
	@Length(max = 150)
	@SafeHtml
	private String name;

	private Long purposeId;

	@SafeHtml
	private String desc;

	private Boolean isActiveForPosting;

	private Long parentId;

	private Long schedule;

	private Character operation;

	@NotNull
	private Character type;

	private Long classification;

	private Boolean functionReqd;

	private Boolean budgetCheckReq;

	@SafeHtml
	@Length(max = 255)
	private String majorCode;

	private Long myClass;

	private Boolean isSubLedger;

	private Long createdBy;
	private Date createdDate;
	private Long lastModifiedBy;
	private Date lastModifiedDate;

}
