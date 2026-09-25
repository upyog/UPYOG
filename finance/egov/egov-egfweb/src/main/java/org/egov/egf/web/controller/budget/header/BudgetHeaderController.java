package org.egov.egf.web.controller.budget.header;


import org.apache.log4j.Logger;
import org.egov.commons.CFinancialYear;
import org.egov.commons.service.CFinancialYearService;
import org.egov.model.budget.header.BudgetHeader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * BudgetHeaderController handles web requests for budget header management.
 *
 * Primary Responsibilities:
 * - Display forms for creating new budget headers
 * - Auto-generate budget header names based on financial year
 * - Validate and prepare financial year data (current and next FY)
 * - Provide model attributes for budget header creation views
 *
 * Request Mappings:
 * - GET/POST /budget/header/new - Displays new budget header creation form with pre-populated financial year data
 *
 * Key Features:
 * - Automatic budget name generation: Budget_{NextFYRange} format
 * - Financial year validation and error handling
 * - Calculates next financial year from current FY end date
 * - Supports both GET and POST for form display
 *
 * Dependencies:
 * - CFinancialYearService: For fetching current and next financial years
 *
 * View Resolution:
 * - Returns "budgetheader-new" view for budget header creation form
 */


@Controller
@RequestMapping("/budget/header")
public class BudgetHeaderController {

    private static final String BUDGET_HEADER_NEW = "budgetheader-new";

    private static final Logger LOGGER = Logger.getLogger(BudgetHeaderController.class);


    @Autowired
    private CFinancialYearService financialYearService;


    /**
     * Displays the budget header creation form.
     *
     * <p>
     * Retrieves the current and next financial years, generates a default
     * budget header name using the next financial year's range, and populates
     * the model with a new {@link BudgetHeader} instance.
     * </p>
     *
     * <p>
     * Example generated name:
     * <pre>
     * Budget_2026-27
     * </pre>
     * </p>
     *
     * @param model Spring MVC model used to populate view attributes
     * @return the budget header creation view name
     */

    @RequestMapping(value = "/new", method = { RequestMethod.GET, RequestMethod.POST })
    public String newForm(final Model model) {

       Map<String, CFinancialYear> financialYearMap = addFinancialYears(model);
       String name = "Budget_" + financialYearMap.get("nextFy").getFinYearRange();
       BudgetHeader budgetHeader = new BudgetHeader();
       budgetHeader.setName(name);
        model.addAttribute("budgetHeader", budgetHeader);
        return BUDGET_HEADER_NEW;
    }


    /**
     * Retrieves the current and next financial years and adds them to the model.
     *
     * <p>
     * The next financial year is determined by adding one day to the
     * current financial year's ending date and resolving the corresponding
     * financial year.
     * </p>
     *
     * <p>
     * The following model attributes are populated:
     * <ul>
     *     <li>currentFy - Current financial year</li>
     *     <li>nextFy - Next financial year</li>
     * </ul>
     * </p>
     *
     * <p>
     * If either financial year cannot be resolved, an error message is added
     * to the model and {@code null} is returned.
     * </p>
     *
     * @param model Spring MVC model used to populate financial year attributes
     * @return map containing current and next financial years with keys
     *         {@code currentFy} and {@code nextFy}, or {@code null} if unavailable
     */

    private Map<String, CFinancialYear> addFinancialYears(Model model) {
        CFinancialYear financialYear = financialYearService.getCurrentFinancialYear();
        ArrayList<String> errors = new ArrayList<>();

        if (financialYear == null) {
            model.addAttribute("errors", "Financial year not found !");
            return null;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(financialYear.getEndingDate());
        calendar.add(Calendar.DATE, 1);
        CFinancialYear nextFinancialYear = financialYearService.getFinancialYearByDate(calendar.getTime());

        if (nextFinancialYear == null) {
            model.addAttribute("errors", "Financial year not found !");
            return null;
        }

        model.addAttribute("currentFy", financialYear);
        model.addAttribute("nextFy", nextFinancialYear);

        Map<String, CFinancialYear> financialYearMap = new HashMap<>();
        financialYearMap.put("currentFy", financialYear);
        financialYearMap.put("nextFy", nextFinancialYear);

        return financialYearMap;
    }

}
