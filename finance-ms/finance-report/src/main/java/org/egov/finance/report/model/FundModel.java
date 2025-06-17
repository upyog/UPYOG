/**
 * Created on Jun 2, 2025.
 * 
 * @author bdhal
 */
package org.egov.finance.report.model;

import java.math.BigDecimal;
import java.util.Date;

import org.egov.finance.report.customannotation.SafeHtml;
import org.egov.finance.report.util.ReportConstants;
import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FundModel {

	private Long id;

	@Length(max = 50, min = 2)
	@SafeHtml(message = ReportConstants.INVALID_TEXT_CONTAINS_HTML_TAGS_MSG)
	@NotNull
	@JsonProperty("name")
	private String name;

	@Length(max = 50, min = 2)
	@SafeHtml(message = ReportConstants.INVALID_TEXT_CONTAINS_HTML_TAGS_MSG)
	@NotNull
	private String code;

	private Character identifier;
	private BigDecimal llevel = BigDecimal.ONE;
	private Long parentId;
	private Boolean isnotleaf;
	private Boolean isactive;
	private Long createdBy;
	private Date createdDate;
	private Long lastModifiedBy;
	private Date lastModifiedDate;

}
