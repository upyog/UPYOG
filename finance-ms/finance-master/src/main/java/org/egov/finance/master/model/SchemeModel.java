package org.egov.finance.master.model;

/**
 * SchemeModel.java
 * 
 * @author mmavuluri
 * @date 09 Jun 2025
 * @version 1.0
 */
import java.math.BigDecimal;
import java.util.Date;

import org.egov.finance.master.customannotation.SafeHtml;
import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SchemeModel {

	private Long id;
	private Long fundId;

	@Length(max = 50, min = 2)
	@SafeHtml
	@NotBlank(message = "Code can not be blank")
	private String code;

	@Length(max = 100, min = 2)
	@SafeHtml
	@NotBlank(message = "Name can not be blank")
	private String name;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
	private Date validfrom;

	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
	private Date validto;

	private Boolean isactive;
	private String description;
	private BigDecimal sectorid;
	private BigDecimal aaes;
	private BigDecimal fieldid;
	private Long createdBy;
	private Date createdDate;
	private Long lastModifiedBy;
	private Date lastModifiedDate;
}
