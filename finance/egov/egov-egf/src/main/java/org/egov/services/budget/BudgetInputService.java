package org.egov.services.budget;


import org.egov.model.budget.BudgetItem;
import org.egov.model.repository.BudgetItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
 * Service for bulk budget data input operations.
 *
 * <p>Handles the creation of function-wise {@link BudgetItem} records by bulk saving them
 * to the repository. The class is read-only by default at the transaction level;
 * write operations are explicitly marked with {@link Transactional}.</p>
 *
 * @see BudgetItem
 * @see BudgetItemRepository
 */



@Service
@Transactional(readOnly = true)
public class BudgetInputService {

    private final BudgetItemRepository budgetItemRepository;

    private BudgetItem budgetItem;

    @Autowired
    public BudgetInputService(final BudgetItemRepository budgetItemRepository) {
        this.budgetItemRepository = budgetItemRepository;
    }


    @Transactional
    public void createFunctionWiseBudget(final List<BudgetItem> budgetItems) {

        budgetItemRepository.save(budgetItems);


    }


}
