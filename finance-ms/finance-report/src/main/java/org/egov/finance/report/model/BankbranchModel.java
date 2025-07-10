package org.egov.finance.report.model;

import java.util.Date;

import org.egov.finance.report.customannotation.SafeHtml;
import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BankbranchModel {

	private Integer id;
	private Integer bankId;

	@SafeHtml
	@NotNull
	@Length(max = 50)
	private String branchcode;

	@SafeHtml
	@NotNull
	@Length(max = 50)
	private String branchname;

	@SafeHtml
	@NotNull
	@Length(max = 50)
	private String branchaddress1;

	@SafeHtml
	@Length(max = 50)
	private String branchaddress2;

	@SafeHtml
	@Length(max = 50)
	private String branchcity;

	@SafeHtml
	@Length(max = 50)
	private String branchstate;

	@SafeHtml
	@Length(max = 50)
	private String branchpin;

	@SafeHtml
	@Length(max = 15)
	private String branchphone;

	@SafeHtml
	@Length(max = 15)
	private String branchfax;

	@SafeHtml
	@Length(max = 50)
	private String contactperson;

	@NotNull
	private Boolean isactive;

	@SafeHtml
	@Length(max = 250)
	private String narration;

	@SafeHtml
	@Length(max = 50)
	private String branchMICR;

	private Long createdBy;
	private Date createdDate;
	private Long lastModifiedBy;
	private Date lastModifiedDate;

}
