package org.egov.finance.voucher.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.egov.finance.voucher.customannotation.SafeHtml;
import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonIgnore;

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
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "generalledger")
@SequenceGenerator(name = "seq_generalledger", sequenceName = "seq_generalledger", allocationSize = 1)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CGeneralLedger implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_generalledger")
	private Long id;

	@NotNull
	private Integer voucherlineId;

	private Date effectiveDate;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "glcodeid")
	private CChartOfAccounts glcodeId;

	@SafeHtml
	@NotNull
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

	@OneToMany(mappedBy = "generalLedgerId", cascade = CascadeType.ALL, orphanRemoval = true)
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	@JsonIgnore
	private Set<CGeneralLedgerDetail> generalLedgerDetails = new HashSet<>();

	@Transient
	private Boolean isSubLedger;
}
