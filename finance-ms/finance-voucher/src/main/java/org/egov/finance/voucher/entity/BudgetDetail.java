package org.egov.finance.voucher.entity;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import org.egov.finance.voucher.model.BudgetReAppropriation;
import org.egov.finance.voucher.workflow.entity.StateAware;
import org.hibernate.validator.constraints.Length;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import lombok.Data;

@Entity
@Table(name = "EGF_BUDGETDETAIL")
@SequenceGenerator(name = BudgetDetail.SEQ_BUDGETDETAIL, sequenceName = BudgetDetail.SEQ_BUDGETDETAIL, allocationSize = 1)

@Data
public class BudgetDetail extends StateAware {

	public static final String SEQ_BUDGETDETAIL = "SEQ_EGF_BUDGETDETAIL";
	private static final long serialVersionUID = 5908792258911500512L;

	@Id
	@GeneratedValue(generator = SEQ_BUDGETDETAIL, strategy = GenerationType.SEQUENCE)
	private Long id;

	@Transient
	private Long nextYrId;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "budgetgroup")
	private BudgetGroup budgetGroup;

	@NotNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "budget")
	private Budget budget;

	private BigDecimal originalAmount = BigDecimal.ZERO;
	private BigDecimal approvedAmount = BigDecimal.ZERO;

	@Transient
	private BigDecimal nextYroriginalAmount = BigDecimal.ZERO;

	@Transient
	private BigDecimal nextYrapprovedAmount = BigDecimal.ZERO;

	private BigDecimal budgetAvailable = BigDecimal.ZERO;

	@Column(name = "anticipatory_amount")
	private BigDecimal anticipatoryAmount = BigDecimal.ZERO;

	@Column(name = "using_department")
	private String usingDepartment;

	@Column(name = "executing_department")
	private String executingDepartment;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "function")
	private Function function;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "scheme")
	private Scheme scheme;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "fund")
	private Fund fund;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "subScheme")
	private SubScheme subScheme;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "functionary")
	private Functionary functionary;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "boundary")
	private Boundary boundary;

	@Length(max = 10)
	private String materializedPath;

	@OneToMany(mappedBy = "budgetDetail", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Set<BudgetReAppropriation> budgetReAppropriations = new HashSet<>();

	@Column(name = "document_number")
	private Long documentNumber;

	@Length(max = 32)
	private String uniqueNo;

	private BigDecimal planningPercent;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "status")
	private EgwStatus status;

	@Transient
	private String comment;

	private static final Logger LOGGER = LoggerFactory.getLogger(BudgetDetail.class);

	public BigDecimal getApprovedReAppropriationsTotal() {
		BigDecimal total = BigDecimal.ZERO;
		budgetReAppropriations = budgetReAppropriations == null ? new HashSet<>() : budgetReAppropriations;
		for (final BudgetReAppropriation entry : budgetReAppropriations) {
			if (!entry.getStatus().getDescription().equalsIgnoreCase("Cancelled")) {
				if ((entry.getAdditionAmount() != null) && BigDecimal.ZERO.compareTo(entry.getAdditionAmount()) != 0)
					total = total.add(entry.getAdditionAmount());
				else
					total = total.subtract(entry.getDeductionAmount());
			}
		}
		return total;
	}

	// Getters, setters, constructors if needed
}
