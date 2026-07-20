package org.egov.model.budget;

import java.math.BigDecimal;


/**
 * Data Transfer Object (DTO) for aggregating and displaying consolidated budget totals
 * across multiple {@link BudgetItem} records.
 *
 * <p>Provides a read-only summary of the four core budget figures — current estimate,
 * actual expenditure, revised estimate, and next year estimate — for use in reports,
 * dashboards, and summary displays.</p>
 *
 * <p><b>Typical Use Cases:</b></p>
 * <ul>
 *   <li>Generating budget summary and consolidated reports.</li>
 *   <li>Displaying department-wise or function-wise budget totals.</li>
 *   <li>Comparing budget estimates against actuals across financial years.</li>
 *   <li>Populating dashboard widgets with aggregated budget information.</li>
 * </ul>
 *
 * <p><b>Design Notes:</b></p>
 * <ul>
 *   <li>This class is not a JPA entity and is not persisted to the database.</li>
 *   <li>All four fields are initialised to {@link BigDecimal#ZERO} as defaults,
 *       and set via constructor — there are no setters, making instances
 *       effectively immutable after construction.</li>
 *   <li>Callers should pass {@link BigDecimal#ZERO} explicitly rather than
 *       {@code null} to avoid overwriting the safe defaults.</li>
 * </ul>
 *
 * @see BudgetItem
 */

public class BudgetTotals {
    private BigDecimal estimate = BigDecimal.ZERO;
    private BigDecimal actual = BigDecimal.ZERO;
    private BigDecimal revised = BigDecimal.ZERO;
    private BigDecimal next = BigDecimal.ZERO;

    public BudgetTotals(BigDecimal estimate, BigDecimal actual, BigDecimal revised, BigDecimal next) {
        this.estimate = estimate;
        this.actual = actual;
        this.revised = revised;
        this.next = next;
    }

    public BigDecimal getEstimate() { return estimate; }
    public BigDecimal getActual() { return actual; }
    public BigDecimal getRevised() { return revised; }
    public BigDecimal getNext() { return next; }
}
