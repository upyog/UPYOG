package org.egov.finance.report.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.egov.finance.report.customannotation.SafeHtml;
import org.hibernate.validator.constraints.Length;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Table(name = "generalledger")
@SequenceGenerator(name = CGeneralLedger.SEQ_GENERALLEDGER, sequenceName = CGeneralLedger.SEQ_GENERALLEDGER, allocationSize = 1)
@Data
public class CGeneralLedger implements Serializable {

	public static final String SEQ_GENERALLEDGER = "seq_generalledger";
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(generator = SEQ_GENERALLEDGER, strategy = GenerationType.SEQUENCE)
	private Long id;

	@NotNull
	private Integer voucherlineId;

	@Temporal(TemporalType.DATE)
	private Date effectiveDate;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "glcodeid")
	private CChartOfAccounts glcodeId;

	@NotNull
	@SafeHtml
	@Length(max = 50)
	private String glcode;

	@NotNull
	@Column(name = "debitamount", precision = 19, scale = 2)
	private BigDecimal debitAmount;
	
	@NotNull
	@Column(name = "creditamount", precision = 19, scale = 2)
	private BigDecimal creditAmount;

	@SafeHtml
	private String description;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "voucherheaderid")
	private CVoucherHeader voucherHeaderId;

	private Integer functionId;

	@OneToMany(mappedBy = "generalLedgerId", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private Set<CGeneralLedgerDetail> generalLedgerDetails = new HashSet<>();

	@Transient
	private Boolean isSubLedger;
}
