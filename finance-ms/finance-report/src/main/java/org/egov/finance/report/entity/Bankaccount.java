package org.egov.finance.report.entity;

import java.util.HashSet;
import java.util.Set;

import org.egov.finance.report.customannotation.SafeHtml;
import org.egov.finance.report.util.BankAccountType;
import org.egov.finance.report.util.CommonsConstants;
import org.hibernate.validator.constraints.Length;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Table(name = "bankaccount", uniqueConstraints = @UniqueConstraint(columnNames = { "accountnumber" }))
@SequenceGenerator(name = Bankaccount.SEQ_BANKACCOUNT, sequenceName = Bankaccount.SEQ_BANKACCOUNT, allocationSize = 1)

@Data
@EqualsAndHashCode(callSuper = false)
public class Bankaccount extends AuditDetailswithVersion {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final String SEQ_BANKACCOUNT = "SEQ_BANKACCOUNT";

	@Id
	@GeneratedValue(generator = SEQ_BANKACCOUNT, strategy = GenerationType.SEQUENCE)
	private Long id;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "branchid", nullable = false)
	private Bankbranch bankbranch;

	@ManyToOne
	@JoinColumn(name = "glcodeid")
	private CChartOfAccounts chartofaccounts;

	@NotNull
	@ManyToOne
	@JoinColumn(name = "fundid")
	private Fund fund;

	@NotNull
	@Length(max = 20)
	@SafeHtml
	// @OptionalPattern(regex = CommonsConstants.numericwithoutspecialchar, message
	// = "Special Characters are not allowed in Accountnumber")
	private String accountnumber;

	@NotNull
	@SafeHtml
	private String accounttype;

	@SafeHtml
	private String narration;

	@NotNull
	private Boolean isactive;

	@SafeHtml
	private String payTo;

	@NotNull
	@Enumerated(EnumType.STRING)
	@Column(name = "type")
	private BankAccountType type;

	@OneToMany(mappedBy = "bankaccount", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private Set<EgSurrenderedCheques> egSurrenderedChequeses = new HashSet<>();

	@ManyToOne
	@JoinColumn(name = "chequeformatid")
	private ChequeFormat chequeformat;
}
