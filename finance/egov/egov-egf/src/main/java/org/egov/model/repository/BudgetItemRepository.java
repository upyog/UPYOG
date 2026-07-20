package org.egov.model.repository;


import org.egov.commons.CFinancialYear;
import org.egov.commons.CFunction;
import org.egov.model.budget.BudgetItem;
import org.egov.model.budget.BudgetRegister;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * Spring Data JPA repository for {@link BudgetItem} entities, providing standard
 * CRUD operations and custom query methods for budget item data access.
 *
 * <p>Extends {@link JpaRepository} to inherit pagination, sorting, and basic
 * persistence operations, and defines additional methods for retrieving budget items
 * by various combinations of register, function, financial year, and budget group.</p>
 *
 * <p><b>Key Capabilities:</b></p>
 * <ul>
 *   <li>Retrieval of budget items by {@link BudgetRegister}, {@link CFunction},
 *       {@link CFinancialYear}, and budget group — individually or in combination.</li>
 *   <li>Filtering of applicable items by excluding records where
 *       {@code notApplicable} is {@code true}.</li>
 *   <li>Existence checks for budget items scoped to a function, financial year,
 *       and/or budget register — without loading full entity graphs.</li>
 *   <li>Retrieval of distinct {@link CFunction} values that have associated
 *       budget items, optionally scoped to a specific register.</li>
 *   <li>Support for filtering across multiple budget groups via {@code IN} queries.</li>
 * </ul>
 *
 * @see BudgetItem
 * @see BudgetRegister
 * @see CFunction
 * @see CFinancialYear
 */

@Repository
public interface BudgetItemRepository extends JpaRepository<BudgetItem, Long> {


    List<BudgetItem> findByBudgetRegisterId(Long registerId);


//    List<BudgetItem> findByBudgetGroupAndCurrentFinancialYearIdAndFunction(
//            String budgetGroup,
//            Long currentFinancialYearId,
//            CFunction function
//    );


    List<BudgetItem> findByBudgetGroupInAndFunctionAndCurrentFinancialYear(
            List<String> budgetGroup, CFunction function, CFinancialYear currentFinancialYear);


    List<BudgetItem> findByBudgetGroupInAndFunctionAndCurrentFinancialYearAndBudgetRegister(
            List<String> budgetGroup, CFunction function, CFinancialYear currentFinancialYear, BudgetRegister budgetRegister);

    List<BudgetItem> findByBudgetGroupInAndFunctionAndBudgetRegister(List<String> budgetGroup, CFunction function, BudgetRegister budgetRegister);

    // excluding not applicable budget items
    List<BudgetItem> findByBudgetGroupInAndFunctionAndBudgetRegisterAndNotApplicableFalse(List<String> budgetGroup, CFunction function, BudgetRegister budgetRegister);

    List<BudgetItem> findByBudgetGroupInAndBudgetRegister(List<String> budgetGroup, BudgetRegister budgetRegister);

    // exclude not applicable budget items
    List<BudgetItem> findByBudgetGroupInAndBudgetRegisterAndNotApplicableFalse(List<String> budgetGroup, BudgetRegister budgetRegister);





    List<BudgetItem> findByFunctionAndCurrentFinancialYear(CFunction function, CFinancialYear currentFinancialYear);


    @Query("select count(b) > 0 from BudgetItem b " +
            "where b.function.id = :functionId " +
            "and b.currentFinancialYear.id = :currentFinancialYearId")
    boolean existsBudgetForCurrentFY(@Param("functionId") Long functionId,
                                     @Param("currentFinancialYearId") Long currentFinancialYearId);

    @Query("select count(b) > 0 from BudgetItem b " +
            "where b.function.id = :functionId " +
            "and b.budgetRegister.id = :budgetRegisterId")
    boolean existsFunctionWiseBudget(@Param("functionId") Long functionId, @Param("budgetRegisterId") Long budgetRegisterId);


    BudgetItem findById(Long id);

    BudgetItem findByFunctionAndBudgetGroup(CFunction function, String budgetGroup);

    @Query("select count(b) > 0 from BudgetItem b " +
            "where b.function.id = :functionId " +
            "and b.currentFinancialYear.id = :currentFinancialYearId " +
            "and b.budgetRegister.id = :budgetRegisterId"
    )
    Boolean existsBudgetForCurrentFYAndBudgetRegister(@Param("functionId") Long functionId,
                                                      @Param("currentFinancialYearId") Long currentFinancialYearId,
                                                      @Param("budgetRegisterId") Long budgetRegisterId);


    @Query("SELECT DISTINCT b.function FROM BudgetItem b ORDER BY b.function.code")
    List<CFunction> findDistinctFunctionsWithBudgetItems();


    @Query("SELECT DISTINCT b.function FROM BudgetItem b WHERE b.budgetRegister.id = :budgetRegisterId ORDER BY b.function.code")
    List<CFunction> findDistinctFunctionsByBudgetRegisterWithBudgetItems(@Param("budgetRegisterId") Long budgetRegisterId);


    BudgetItem findByFunctionAndBudgetGroupAndBudgetRegister(CFunction function, String closingBalance, BudgetRegister budgetRegister);
}
