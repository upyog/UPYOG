package org.egov.finance.report.entity;

/**
 * Scheme.java
 * 
 * @author mmavuluri
 * @date 09 Jun 2025
 * @version 1.0
 */
import java.math.BigDecimal;
import java.util.Date;

import org.egov.finance.report.customannotation.SafeHtml;
import org.hibernate.validator.constraints.Length;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "scheme")
@SequenceGenerator(name = Scheme.SEQ, sequenceName = Scheme.SEQ, allocationSize = 1)
@Data
public class Scheme extends AuditDetailswithoutVersion {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final String SEQ = "SEQ_Scheme";

	@Id
	@GeneratedValue(generator = SEQ, strategy = GenerationType.SEQUENCE)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY,targetEntity = Fund.class)
	@JoinColumn(name = "fundid")
	private Fund fund;

	@Length(max = 50, min = 2)
	@SafeHtml
	private String code;

	@Length(max = 100, min = 2)
	@SafeHtml
	private String name;

	private Date validfrom;
	private Date validto;
	private Boolean isactive = false;
	private String description;
	private BigDecimal sectorid;
	private BigDecimal aaes;
	private BigDecimal fieldid;

}
