package org.egov.finance.voucher.model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.egov.finance.voucher.entity.EgwStatus;
import org.egov.finance.voucher.workflow.entity.StateAware;

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
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.Data;

@Data
@Entity
@Table(name = "budget_reappropriation_misc") 
public class BudgetReAppropriationMisc extends StateAware implements Serializable {

	private static final long serialVersionUID = 3462810824735494382L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "sequence_number")
	private String sequenceNumber;

	private String remarks;

	@Temporal(TemporalType.DATE)
	@Column(name = "reappropriation_date")
	private Date reAppropriationDate;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "status_id")
	private EgwStatus status;

	@OneToMany(mappedBy = "reAppropriationMisc", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<BudgetReAppropriation> budgetReAppropriations = new HashSet<>();
}
