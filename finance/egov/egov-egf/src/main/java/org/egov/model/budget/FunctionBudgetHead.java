package org.egov.model.budget;


import lombok.Getter;
import lombok.Setter;
import org.egov.commons.CFunction;

import javax.persistence.*;


/**
 * Entity representing the mapping between a {@link CFunction} (department or service)
 * and a {@link BudgetHead}, acting as a join table for their many-to-many relationship.
 *
 * <p>Each record defines a permitted association — declaring that a specific budget head
 * is authorised for use by a specific function. Budget items can only be created for
 * function and budget head combinations that have a corresponding {@code FunctionBudgetHead}
 * mapping.</p>
 *
 * <p><b>Key Characteristics:</b></p>
 * <ul>
 *   <li>Bridges the many-to-many relationship between {@link CFunction} and {@link BudgetHead}.</li>
 *   <li>Enforces uniqueness of each function–budget head pair via a composite unique constraint
 *       on {@code function_id} and {@code budget_head_id}, preventing duplicate mappings.</li>
 *   <li>Supports budget access control by restricting which budget heads are available
 *       to which departments or services.</li>
 *   <li>Both associations are loaded lazily to avoid unnecessary joins.</li>
 * </ul>
 *
 * <p><b>Business Purpose:</b></p>
 * <ul>
 *   <li>Only authorised budget heads may be used by a given department (function).</li>
 *   <li>Ensures proper segregation of budget allocation across departments.</li>
 *   <li>Supports structured and controlled budget planning workflows.</li>
 * </ul>
 *
 * <p><b>Examples:</b></p>
 * <ul>
 *   <li>The Engineering department is mapped to the "Road Maintenance" budget head.</li>
 *   <li>The Health department is mapped to the "Medical Supplies" budget head.</li>
 *   <li>The Education department cannot use "Road Maintenance" unless an explicit
 *       mapping record exists.</li>
 * </ul>
 *
 * <p>Mapped to the database table {@code function_budget_head}, with a composite unique
 * constraint on ({@code budget_head_id}, {@code function_id}).</p>
 *
 * @see CFunction
 * @see BudgetHead
 */

@Entity
@Table(name = "function_budget_head",
        uniqueConstraints = @UniqueConstraint(columnNames = {"budget_head_id", "function_id"}))
@Getter
@Setter
public class FunctionBudgetHead {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "function_id", nullable = false)
    private CFunction function;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "budget_head_id", nullable = false)
    private BudgetHead budgetHead;



}
