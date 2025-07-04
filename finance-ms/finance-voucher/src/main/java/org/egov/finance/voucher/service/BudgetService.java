package org.egov.finance.voucher.service;

import org.egov.finance.voucher.entity.Budget;
import org.egov.finance.voucher.repository.BudgetRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BudgetService {

	private final BudgetRepository budgetRepository;

	@Autowired
	public BudgetService(BudgetRepository budgetRepository) {
		this.budgetRepository = budgetRepository;
	}

	public boolean hasApprovedReForYear(final Long financialYearId) {
		Budget budget = budgetRepository.findApprovedReBudgetForYear(financialYearId);
		return budget != null;
	}

}
