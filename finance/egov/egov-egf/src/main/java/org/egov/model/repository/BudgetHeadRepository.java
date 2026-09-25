package org.egov.model.repository;

import org.egov.commons.CFunction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

import org.egov.model.budget.BudgetHead;

/**
 * Spring Data JPA repository for {@link BudgetHead} entities, providing standard
 * CRUD operations and custom query methods for budget head data access.
 *
 * <p>Extends {@link JpaRepository} to inherit pagination, sorting, and basic
 * persistence operations, and defines additional methods for search and
 * function-based filtering of budget heads.</p>
 *
 * <p><b>Key Capabilities:</b></p>
 * <ul>
 *   <li>Case-insensitive partial search across budget head code and name.</li>
 *   <li>Retrieval of all budget heads mapped to a specific {@link CFunction}
 *       via the {@code function_budget_head} join table.</li>
 *   <li>Combined function-scoped search by code or name using native SQL.</li>
 * </ul>
 *
 * @see BudgetHead
 * @see CFunction
 */


@Repository
public interface BudgetHeadRepository extends JpaRepository<BudgetHead, Long> {
    
    // List<BudgetHead> findByAccountTypeIs(String accountType);

    // List<BudgetHead> findByIsActiveTrue();


    List<BudgetHead> findByCodeContainingIgnoreCaseOrNameContainingIgnoreCase(String code, String name);


    @Query(value = "SELECT bh.* FROM egf_budgethead bh " +
            "JOIN function_budget_head fbh ON bh.id = fbh.budget_head_id " +
            "WHERE fbh.function_id = :functionId " +
            "AND (LOWER(bh.code) LIKE LOWER(CONCAT('%', :query, '%')) " +
            "OR LOWER(bh.name) LIKE LOWER(CONCAT('%', :query, '%')))",
            nativeQuery = true)
    List<BudgetHead> searchBudgetHeadsByFunctionNative(
            @Param("functionId") Long functionId,
            @Param("query") String query);

    @Query(value = "SELECT bh.* FROM egf_budgethead bh " +
            "JOIN function_budget_head fbh ON bh.id = fbh.budget_head_id " +
            "WHERE fbh.function_id = :functionId ",
            nativeQuery = true)
    List<BudgetHead> getBudgetHeadByFunction(@Param("functionId") Long functionId);




}
