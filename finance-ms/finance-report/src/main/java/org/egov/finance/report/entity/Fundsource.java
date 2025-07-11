package org.egov.finance.report.entity;

import java.math.BigDecimal;
import java.util.Date;

import org.egov.finance.report.customannotation.SafeHtml;
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
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Table(name = "fundsource", uniqueConstraints = @UniqueConstraint(columnNames = { "name", "code" }))
@Data
@SequenceGenerator(name = Fundsource.SEQ_FUNDSOURCE, sequenceName = Fundsource.SEQ_FUNDSOURCE, allocationSize = 1)
public class Fundsource extends AuditDetailswithVersion {

	public static final String SEQ_FUNDSOURCE = "SEQ_FUNDSOURCE";
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(generator = SEQ_FUNDSOURCE, strategy = GenerationType.SEQUENCE)
	private Long id;

	@Length(min = 1, max = 25)
	@NotNull
	@SafeHtml
	private String code;

	@Length(min = 1, max = 25)
	@NotNull
	@SafeHtml
	private String name;

	@Length(max = 25)
	@SafeHtml
	private String type;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parentid")
	private Fundsource fundsource;

	private BigDecimal llevel;
	private Boolean isactive;
	private Boolean isnotleaf;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "financialinstid")
	private FinancingInstitution finInstId;

	@Length(max = 25)
	@Column(name = "funding_type")
	@SafeHtml
	private String fundingType;

	@Column(name = "loan_percentage")
	private Double loanPercentage;

	@Column(name = "source_amount")
	private BigDecimal sourceAmount;

	@Column(name = "rate_of_interest")
	private Double rateOfIntrest;

	@Column(name = "loan_period")
	private Double loanPeriod;

	@Column(name = "moratorium_period")
	private Double moratoriumPeriod;

	@Length(max = 25)
	@Column(name = "repayment_frequency")
	@SafeHtml
	private String repaymentFrequency;

	@Column(name = "no_of_installment")
	private Integer noOfInstallment;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "bankaccountid")
	private Bankaccount bankAccountId;

	@Length(max = 25)
	@Column(name = "govt_order")
	@SafeHtml
	private String govtOrder;

	@Column(name = "govt_date")
	private Date govtDate;

	@Length(max = 25)
	@Column(name = "dp_code_number")
	@SafeHtml
	private String dpCodeNum;

	@Length(max = 25)
	@Column(name = "dp_code_resg")
	@SafeHtml
	private String dpCodeResistration;

	@Length(max = 25)
	@Column(name = "fin_inst_letter_num")
	@SafeHtml
	private String finInstLetterNum;

	@Column(name = "fin_inst_letter_date")
	private Date finInstLetterDate;

	@Length(max = 25)
	@Column(name = "fin_inst_schm_num")
	@SafeHtml
	private String finInstSchmNum;

	@Column(name = "fin_inst_schm_date")
	private Date finInstSchmDate;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "subschemeid")
	private SubScheme subSchemeId;


}
