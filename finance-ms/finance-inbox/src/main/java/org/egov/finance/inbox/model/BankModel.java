package org.egov.finance.inbox.model;

import java.util.Date;

import org.egov.finance.inbox.customannotation.SafeHtml;
import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BankModel {

	private Long id;

	@SafeHtml
	@NotNull(message = "Code cannot be null")
	@Length(max = 50)
	private String code;

	@SafeHtml
	@NotNull(message = "Name cannot be null")
	@Length(max = 100)
	private String name;

	@SafeHtml
	@Length(max = 250)
	private String narration;

	@NotNull
	private Boolean isactive;

	@SafeHtml
	@Length(max = 50)
	private String type;

	private Long createdBy;
	private Date createdDate;
	private Long lastModifiedBy;
	private Date lastModifiedDate;

}
