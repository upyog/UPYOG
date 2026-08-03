package org.egov.model.repository;

import org.egov.commons.CFinancialYear;
import org.egov.commons.EgwStatus;
import org.egov.model.budget.BudgetRegister;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigInteger;
import java.util.List;


/**
 * Spring Data JPA repository for {@link BudgetRegister} entities, providing data access
 * operations for budget register retrieval and workflow management.
 *
 * <p>Extends {@link JpaRepository} to inherit standard CRUD and pagination operations,
 * and defines additional methods for sequence generation, financial-year-scoped lookups,
 * status-based filtering, and register number search.</p>
 *
 * <p><b>Key Capabilities:</b></p>
 * <ul>
 *   <li>Generates unique register sequence numbers directly from the database sequence
 *       {@code seq_eg_budgetregister}.</li>
 *   <li>Retrieves budget registers scoped to a specific combination of authoring and
 *       target {@link CFinancialYear}.</li>
 *   <li>Filters budget registers by workflow {@link EgwStatus} (e.g. Created, Submitted,
 *       Approved, Rejected) within a financial year scope.</li>
 *   <li>Retrieves the most recently created register for a given financial year
 *       combination.</li>
 *   <li>Looks up a specific register by its unique register number.</li>
 * </ul>
 *
 * @see BudgetRegister
 * @see CFinancialYear
 * @see EgwStatus
 */


public interface BudgetRegisterWorkflowRepository extends JpaRepository<BudgetRegister, Long>  {

    @Query(value = "SELECT nextval('seq_eg_budgetregister')", nativeQuery = true)
    Long getNextBudgetRegisterSequence();


    List<BudgetRegister> findByCurrentFinancialYearAndFinancialYear(CFinancialYear currentFinancialYear, CFinancialYear financialYear);


    List<BudgetRegister> findByCurrentFinancialYearAndFinancialYearAndStatus(CFinancialYear currentFinancialYear, CFinancialYear financialYear, EgwStatus status);


    BudgetRegister findTopByCurrentFinancialYearAndFinancialYearOrderByIdDesc(CFinancialYear currentFinancialYear, CFinancialYear financialYear);


    BudgetRegister findByBudgetRegisterNumber(String budgetRegisterNumber);


}
