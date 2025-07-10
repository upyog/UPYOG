package org.egov.finance.inbox.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.egov.finance.inbox.customannotation.SafeHtml;
import org.egov.finance.inbox.entity.AccountDetail;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

@Data
public class VoucherModel {

	private Long id;

	@SafeHtml
	private String name;

	@SafeHtml
	private String type;

	@SafeHtml
	private String voucherNumber;

	@SafeHtml
	private String description;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
	private Date voucherDate;

	private Long fundId;
	private Long functionId;
	private Long fiscalPeriodId;
	private Long statusId;
	private Long originalVhId;
	private Long refVhId;

	@SafeHtml
	private String cgvn;
	private Long moduleId;

	@SafeHtml
	private String department;

	@SafeHtml
	private String source;

	private Long schemeId;
	private Long subSchemeId;
	private Long functionaryId;
	private Long fundsourceId;

	private List<AccountDetail> ledgers = new ArrayList<>();

	@SafeHtml
	private String tenantId;

	@SafeHtml
	private String serviceName;

	@SafeHtml
	private String referenceDocument;

	private Long createdBy;
	private Date createdDate;
	private Long lastModifiedBy;
	private Date lastModifiedDate;

}
