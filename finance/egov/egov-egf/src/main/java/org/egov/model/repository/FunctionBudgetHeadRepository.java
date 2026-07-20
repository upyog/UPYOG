package org.egov.model.repository;

import org.egov.commons.CFunction;
import org.egov.model.budget.FunctionBudgetHead;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * Spring Data JPA repository for {@link FunctionBudgetHead} entities, providing data access
 * operations for the mapping between {@link CFunction} (departments) and {@link BudgetHead} records.
 *
 * <p>Extends {@link JpaRepository} to inherit standard CRUD and pagination operations,
 * and defines additional methods for function-scoped retrieval, incremental loading,
 * and case-insensitive function search across mapped budget heads.</p>
 *
 * <p><b>Key Capabilities:</b></p>
 * <ul>
 *   <li>Retrieves all {@link FunctionBudgetHead} mappings for a specific function or department.</li>
 *   <li>Fetches mappings with an ID greater than a given value, supporting incremental
 *       or paginated loading scenarios.</li>
 *   <li>Searches for distinct {@link CFunction} entries that have budget head mappings,
 *       filtered case-insensitively by function name or code.</li>
 * </ul>
 *
 * @see FunctionBudgetHead
 * @see CFunction
 * @see BudgetHead
 */

@Repository
public interface FunctionBudgetHeadRepository extends JpaRepository<FunctionBudgetHead, Long> {

    List<FunctionBudgetHead> findByFunctionId(Long functionId);

    List<FunctionBudgetHead> findByIdGreaterThan(Long id, Sort sort);

    @Query("select distinct fb.function " +
            " from FunctionBudgetHead fb " +
            "where ( lower(fb.function.name) like lower(concat('%', :query, '%'))" +
            "or lower(fb.function.code) like lower(concat('%', :query, '%')) )")
    List<CFunction> findDistinctFunctionsHavingBudgetHead(@Param("query") String query);

}
