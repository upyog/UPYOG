
package org.egov.finance.inbox.entity;

import static org.egov.finance.inbox.entity.BudgetReAppropriationMisc.SEQ_BUDGETREAPPROMISC;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.egov.finance.inbox.util.InboxConstants;
import org.egov.finance.inbox.workflow.entity.StateAware;

import jakarta.persistence.CascadeType;
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

@Entity
@Table(name = "egf_reappropriation_misc")
@SequenceGenerator(name = SEQ_BUDGETREAPPROMISC, sequenceName = SEQ_BUDGETREAPPROMISC, allocationSize = 1)

	
public class BudgetReAppropriationMisc extends StateAware {
	private static final long serialVersionUID = 3462810824735494382L;
	public static final String SEQ_BUDGETREAPPROMISC = "SEQ_BUDGETREAPPROMISC";
	@Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = SEQ_BUDGETREAPPROMISC)
	private Long id;
	private String sequenceNumber;
	private String remarks;
	private Date reAppropriationDate;
	
	@ManyToOne
    @JoinColumn(name = "status")
	private EgwStatus status;
	
	@OneToMany(mappedBy = "reAppropriationMisc", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	private Set<BudgetReAppropriation> budgetReAppropriations = new HashSet<>();

	public Set<BudgetReAppropriation> getBudgetReAppropriations() {
		return budgetReAppropriations;
	}

	public void setBudgetReAppropriations(final Set<BudgetReAppropriation> budgetReAppropriations) {
		this.budgetReAppropriations = budgetReAppropriations;
	}

	
	public Long getId() {
		return id;
	}

	
	public void setId(final Long id) {
		this.id = id;
	}

	public String getSequenceNumber() {
		return sequenceNumber;
	}

	public void setSequenceNumber(final String sequenceNumber) {
		this.sequenceNumber = sequenceNumber;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(final String remarks) {
		this.remarks = remarks;
	}

	public Date getReAppropriationDate() {
		return reAppropriationDate;
	}

	public void setReAppropriationDate(final Date reAppropriationDate) {
		this.reAppropriationDate = reAppropriationDate;
	}

	@Override
	public String getStateDetails() {
		return sequenceNumber == null ? "" : sequenceNumber;
	}

	public List<BudgetReAppropriation> getNonApprovedReAppropriations() {
		final List<BudgetReAppropriation> reAppList = new ArrayList<>();
		budgetReAppropriations = budgetReAppropriations == null ? new HashSet<>() : budgetReAppropriations;
		for (final BudgetReAppropriation entry : budgetReAppropriations)
			if (!InboxConstants.END.equalsIgnoreCase(entry.getState().getValue())
					|| !"APPROVED".equalsIgnoreCase(entry.getState().getValue()))
				reAppList.add(entry);
		return reAppList;
	}

	public BudgetReAppropriation getBudgetReAppropriationWithId(final Long id) {
		for (final BudgetReAppropriation reAppropriation : budgetReAppropriations)
			if (id.equals(reAppropriation.getId()))
				return reAppropriation;
		return null;
	}

	public EgwStatus getStatus() {
		return status;
	}

	public void setStatus(EgwStatus status) {
		this.status = status;
	}

}
