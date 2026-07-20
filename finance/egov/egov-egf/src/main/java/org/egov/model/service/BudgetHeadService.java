package org.egov.model.service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.struts2.util.SortIteratorFilter;
import org.egov.commons.CFinancialYear;
import org.egov.commons.CFunction;
import org.egov.model.budget.BudgetHead;
import org.egov.model.repository.BudgetHeadRepository;
import org.egov.utils.BudgetAccountType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * Service class responsible for managing {@link BudgetHead} entities and
 * providing business operations related to budget head creation, retrieval,
 * search, and classification.
 *
 * <p>
 * This service acts as an intermediary between the controller layer and
 * {@link BudgetHeadRepository}, encapsulating business logic associated with
 * budget heads.
 * </p>
 *
 * <p>
 * Responsibilities include:
 * <ul>
 *     <li>Creating budget heads with corresponding account type codes.</li>
 *     <li>Retrieving budget heads grouped by account type.</li>
 *     <li>Fetching active budget heads in display order.</li>
 *     <li>Searching budget heads by code or name.</li>
 *     <li>Retrieving budget heads associated with a specific function.</li>
 * </ul>
 * </p>
 *
 * @see BudgetHead
 * @see BudgetHeadRepository
 */

@Service
@Transactional(readOnly = true)
public class BudgetHeadService {

    private final BudgetHeadRepository budgetHeadRepository;

    @Autowired
    @Qualifier("parentMessageSource")
    private MessageSource messageSource;

    @PersistenceContext
    private EntityManager entityManager;


    @Autowired
    public BudgetHeadService(final BudgetHeadRepository budgetHeadRepository) {
        this.budgetHeadRepository = budgetHeadRepository;
    }

    /**
     * Creates and persists a new {@link BudgetHead}.
     *
     * <p>
     * Based on the selected {@link BudgetAccountType}, a corresponding
     * account type code is automatically assigned:
     * </p>
     *
     * <ul>
     *     <li>REVENUE_RECEIPTS → RR</li>
     *     <li>REVENUE_EXPENDITURE → RE</li>
     *     <li>CAPITAL_RECEIPTS → CR</li>
     *     <li>CAPITAL_EXPENDITURE → CE</li>
     * </ul>
     *
     * @param budgetHead budget head entity to be created
     * @return persisted BudgetHead entity
     */

    @Transactional
    public BudgetHead create(final BudgetHead budgetHead) {
        if (budgetHead.getAccountType() != null) {
            switch (budgetHead.getAccountType()) {
                case REVENUE_RECEIPTS:
                    budgetHead.setAccountTypeCode("RR");
                    break;
                case REVENUE_EXPENDITURE:
                    budgetHead.setAccountTypeCode("RE");
                    break;
                case CAPITAL_RECEIPTS:
                    budgetHead.setAccountTypeCode("CR");
                    break;
                case CAPITAL_EXPENDITURE:
                    budgetHead.setAccountTypeCode("CE");
                    break;
                default:
                    budgetHead.setAccountTypeCode(null); // or throw exception
            }
        }
        return budgetHeadRepository.save(budgetHead);
    }

    /**
     * Retrieves all budget heads, groups them by account type, and
     * populates the provided model with categorized budget head lists.
     *
     * <p>
     * The following model attributes are added:
     * </p>
     * <ul>
     *     <li>rr - Revenue Receipts</li>
     *     <li>re - Revenue Expenditure</li>
     *     <li>cr - Capital Receipts</li>
     *     <li>ce - Capital Expenditure</li>
     * </ul>
     *
     * @param model Spring MVC model to populate with grouped budget heads
     */

    public void getBudgetHeadList(Model model) {
        List<BudgetHead> budgetHeads =  budgetHeadRepository.findAll();

        Map<BudgetAccountType, List<BudgetHead>> grouped = budgetHeads.stream()
                .collect(Collectors.groupingBy(BudgetHead::getAccountType));


        List<BudgetHead> revenueReceipts = grouped.get(BudgetAccountType.REVENUE_RECEIPTS);
        List<BudgetHead> revenueExpenditure = grouped.get(BudgetAccountType.REVENUE_EXPENDITURE);
        List<BudgetHead> capitalReceipts = grouped.get(BudgetAccountType.CAPITAL_RECEIPTS);
        List<BudgetHead> capitalExpenditure = grouped.get(BudgetAccountType.CAPITAL_EXPENDITURE);

        model.addAttribute("rr", revenueReceipts);
        model.addAttribute("re", revenueExpenditure);
        model.addAttribute("cr", capitalReceipts);
        model.addAttribute("ce", capitalExpenditure);

    }

    public List<BudgetHead> getActiveBudgetHeads() {
        List<BudgetHead> budgetHeads = budgetHeadRepository.findAll(new Sort(Sort.Direction.ASC, "order"));
        return budgetHeads;
    }


    public List<BudgetHead> findBudgetHeadByNameOrCode(final String query) {
        return budgetHeadRepository.findByCodeContainingIgnoreCaseOrNameContainingIgnoreCase(query, query);
    }



    public List<BudgetHead> searchBudgetHeadsByFunctionNative(final Long functionId, final String query) {
        return budgetHeadRepository.searchBudgetHeadsByFunctionNative(functionId, query);
    }

    public List<BudgetHead> getBudgetHeadsByFunction(CFunction function) {
        return budgetHeadRepository.getBudgetHeadByFunction(function.getId());
    }

    public BudgetHead findById(final  Long budgetHeadId) {
        return budgetHeadRepository.findOne(budgetHeadId);
    }


    public void getBudgetByFunctionAndFy(Long id, CFinancialYear currentFy) {

    }
}
