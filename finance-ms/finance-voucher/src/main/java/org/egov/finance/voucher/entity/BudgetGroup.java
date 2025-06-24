package org.egov.finance.voucher.entity;

import org.egov.finance.voucher.customannotation.SafeHtml;
import org.egov.finance.voucher.enumeration.BudgetAccountType;
import org.egov.finance.voucher.enumeration.BudgetingType;
import org.hibernate.validator.constraints.Length;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "EGF_BUDGETGROUP")

@Data
public class BudgetGroup extends AuditDetailswithVersion {

	public static final String SEQ_BUDGETGROUP = "SEQ_EGF_BUDGETGROUP";
	private static final long serialVersionUID = 8907540544512153346L;
	@SequenceGenerator(name = SEQ_BUDGETGROUP, sequenceName = SEQ_BUDGETGROUP, allocationSize = 1)
	@Id
	@GeneratedValue(generator = SEQ_BUDGETGROUP, strategy = GenerationType.SEQUENCE)
	private Long id;
	@SafeHtml
	// @Required(message = "Name should not be empty")
	@Length(max = 250)
	private String name;

	@SafeHtml
	@Length(max = 250, message = "Max 250 characters are allowed for description")
	private String description;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "majorcode")
	private CChartOfAccounts majorCode;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "maxcode")
	private CChartOfAccounts maxCode;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "mincode")
	private CChartOfAccounts minCode;

	@Enumerated(value = EnumType.STRING)
	private BudgetAccountType accountType;

	@Enumerated(value = EnumType.STRING)
	private BudgetingType budgetingType;
	private Boolean isActive;

}
