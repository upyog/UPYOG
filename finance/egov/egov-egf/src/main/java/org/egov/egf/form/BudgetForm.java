package org.egov.egf.form;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import org.egov.model.budget.BudgetItem;

/**
 * Form model representing the budget input data for a specific function and financial year.
 *
 * <p>This class is used to capture and transfer budget data submitted via the UI,
 * including opening and closing balance rows and a variable number of
 * revenue or capital {@link BudgetItem} rows.</p>
 *
 * <p>Getters and setters are generated at compile time via Lombok's
 * {@link lombok.Getter} and {@link lombok.Setter} annotations.</p>
 *
 * <p><b>Note:</b> The {@code items} list is initialised to an empty {@link ArrayList}
 * in the constructor to ensure it is never {@code null}.</p>
 *
 * @see BudgetItem
 */

@Getter
@Setter
public class BudgetForm {

    private Long functionid;             // Selected Function ID

    private BudgetItem opening = new BudgetItem();          // Opening Balance row

    private BudgetItem closing = new BudgetItem();          // Opening Balance row

    private List<BudgetItem> items;      // Revenue/Capital rows (multiple)

    private Long financialYear;

    private Long currentFinancialYear;

    private Long stateBudgetCode;

    public BudgetForm() {
        // Ensure the list is never null
        this.items = new ArrayList<>();
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Function Id: ").append(functionid);
//        stringBuilder.append("\n Opening Balance: ").append(opening.toString());
//        stringBuilder.append("\n Closing Balance: ").append(closing.toString());
        stringBuilder.append("\n\n");

//        items.forEach(budgetItem -> {
//            stringBuilder.append("\n ").append(budgetItem.toString());
//        });

        return stringBuilder.toString();

    }

}
