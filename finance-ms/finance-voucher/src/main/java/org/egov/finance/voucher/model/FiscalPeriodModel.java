package org.egov.finance.voucher.model;

import java.util.Date;

import org.egov.finance.voucher.customannotation.SafeHtml;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FiscalPeriodModel {

	private Long id;

	@SafeHtml
	@NotBlank(message = "Name cannot be blank")
	private String name;

	private Long financialYearId;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
	private Date startingDate;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
	private Date endingDate;

	private Boolean active;
	private Boolean isActiveForPosting;
	private Boolean isClosed;

	private Long createdBy;
	private Date createdDate;
	private Long lastModifiedBy;
	private Date lastModifiedDate;

}
