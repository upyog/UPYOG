package org.egov.finance.voucher.entity;

import java.util.Date;

import org.egov.finance.voucher.customannotation.SafeHtml;
import org.egov.finance.voucher.validation.Unique;
import org.egov.finance.voucher.workflow.entity.StateAware;
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
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Table(name = "EGF_BUDGET")

@Unique(fields = "name", enableDfltMsg = true)
@Data
public class Budget extends StateAware {

	public static final String SEQ_BUDGET = "SEQ_EGF_BUDGET";
	private static final long serialVersionUID = 3592259793739732756L;
	@SequenceGenerator(name = SEQ_BUDGET, sequenceName = SEQ_BUDGET, allocationSize = 1)
	@Id
	@GeneratedValue(generator = SEQ_BUDGET, strategy = GenerationType.SEQUENCE)
	private Long id;

	@NotNull(message = "Name should not be empty")
	@Length(max = 250, message = "Max 250 characters are allowed for description")
	@SafeHtml
	private String name;

	@SafeHtml
	@Length(max = 20)
	private String isbere;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "FINANCIALYEARID")
	@NotNull
	private FinancialYear financialYear;

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "parent")
	private Budget parent;

	@Length(max = 250, message = "Max 250 characters are allowed for description")
	@SafeHtml
	private String description;

	@Column(name = "AS_ON_DATE")
	private Date asOnDate;

	private boolean isActiveBudget;

	private boolean isPrimaryBudget;

	@Length(max = 10, message = "Max 10 characters are allowed for description")
	@SafeHtml
	private String materializedPath;

	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	@JoinColumn(name = "reference_budget")
	private Budget referenceBudget;

	@Column(name = "DOCUMENT_NUMBER")
	private Long documentNumber;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "STATUS")
	private EgwStatus status;

	@Transient
	@SafeHtml
	private String searchBere;

}
