
package org.egov.finance.report.entity;

import java.math.BigDecimal;

import org.egov.finance.report.customannotation.SafeHtml;
import org.egov.finance.report.util.FinancialConstants;
import org.egov.finance.report.util.RegExConstants;
import org.egov.finance.report.validation.OptionalPattern;
import org.egov.finance.report.validation.Unique;
import org.hibernate.validator.constraints.Length;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "TDS")
@SequenceGenerator(name = Recovery.SEQ_RECOVERY, sequenceName = Recovery.SEQ_RECOVERY, allocationSize = 1)
@Unique(id = "id", tableName = "TDS", fields = { "type" }, columnName = { "type" }, enableDfltMsg = true)
public class Recovery extends AuditDetailswithoutVersion {

	private static final long serialVersionUID = 6136656142691290863L;
	public static final String SEQ_RECOVERY = "SEQ_TDS";

	@Id
	@GeneratedValue(generator = SEQ_RECOVERY, strategy = GenerationType.SEQUENCE)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "glcodeid")
	private CChartOfAccounts chartofaccounts;

	@Length(max = 20)
	@SafeHtml
	@NotNull
	private String type;

	private Boolean isactive;

	private BigDecimal rate;

	@Length(max = 100)
	@SafeHtml
	@NotNull
	private String remitted;

	@Length(max = 200)
	@SafeHtml
	private String description;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "partytypeid")
	private EgPartytype egPartytype;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "bankid")
	private Bank bank;

	private BigDecimal caplimit;

	@Length(max = 50)
	@SafeHtml
	@NotNull
	private String recoveryName;

	@Length(max = 50)
	@SafeHtml
	private String calculationType;

	@SafeHtml
	@Length(min = 11, max = 11, message = "Maximum of 11 Characters allowed for IFSC Code")
	@OptionalPattern(regex = RegExConstants.ALPHANUMERIC, message = "Special Characters are not allowed in IFSC Code")
	private String ifscCode;

	@Length(max = 32)
	@SafeHtml
	@OptionalPattern(regex = FinancialConstants.numericwithoutspecialchar, message = "Special Characters are not allowed in accountNumber")
	private String accountNumber;

	@NotNull
	@Column(name = "recovery_mode")
	private Character recoveryMode;

	@Column(name = "remittance_mode")
	private Character remittanceMode;

	@Transient
	private Boolean bankLoan;


}
