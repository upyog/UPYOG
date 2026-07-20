package org.egov.model.service;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.egov.commons.CFinancialYear;
import org.egov.commons.CFunction;
import org.egov.commons.Scheme;
import org.egov.commons.dao.EgwStatusHibernateDAO;
import org.egov.commons.dao.SchemeHibernateDAO;
import org.egov.commons.repository.FunctionRepository;
import org.egov.commons.service.CFinancialYearService;
import org.egov.egf.form.BudgetForm;
import org.egov.model.budget.Budget;
import org.egov.model.budget.BudgetHead;
import org.egov.model.budget.BudgetItem;
import org.egov.model.budget.BudgetRegister;
import org.egov.model.repository.BudgetItemRepository;
import org.egov.utils.BudgetAccountType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;


/**
 * BudgetItemService provides business logic for BudgetItem entity operations.
 * Handles budget item creation, validation, calculation, and complete budget form processing.
 *
 * Key Features:
 * - Create, update, and validate budget items with comprehensive field validation
 * - Calculate opening and closing balances based on revenue and expenditure items
 * - Compute totals for revenue (RR, CR) and expenditure (RE, CE) budget types
 * - Handle scheme-based budget validations for applicable budget heads
 * - Process complete budget input forms with automatic balance calculations
 * - Group and filter budget items by budget group, function, and financial year
 * - Support for marking items as not applicable to exclude from calculations
 * - Retrieve distinct functions that have associated budget items
 * - Update existing budget items while handling new row insertions
 *
 * @see BudgetItem
 * @see BudgetItemRepository
 */
@Service
public class BudgetItemService {

    private final static Logger LOGGER = LoggerFactory.getLogger(BudgetItemService.class);

    private final BudgetItemRepository budgetItemRepository;

    private final FunctionRepository functionRepository;

    @Autowired
    private CFinancialYearService financialYearService;

    @Autowired
    private BudgetHeadService budgetHeadService;

    @Autowired
    private BudgetRegisterWorkflowService budgetRegisterWorkflowService;

    @Autowired
    private EgwStatusHibernateDAO egwStatusDAO;

    @Autowired
    private SchemeHibernateDAO schemeHibernateDAO;

    @Autowired
    public BudgetItemService(final BudgetItemRepository budgetItemRepository,
            final FunctionRepository functionRepository) {
        this.budgetItemRepository = budgetItemRepository;
        this.functionRepository = functionRepository;
    }

    public BudgetItem create(BudgetItem item) {
        return budgetItemRepository.save(item);
    }

    @Transactional
    public BudgetItem update(BudgetItem item) {
        return budgetItemRepository.save(item);
    }


    /**
     * Validates the existence of the specified budget register.
     *
     * @param budgetRegisterId identifier of the budget register
     * @param resultBinder binding result used for validation errors
     * @return the corresponding budget register if found, otherwise null
     */

    public BudgetRegister validateBudgetRegister(Long budgetRegisterId,final BindingResult resultBinder) {
        BudgetRegister mBudgetRegister = budgetRegisterWorkflowService.findOne(budgetRegisterId);
        if (mBudgetRegister == null) {
            resultBinder.reject("budgetRegister", "The selected budget register is not available or invalid.");
        }
        return mBudgetRegister;
    }

    /**
     * Validates the existence of the specified function.
     *
     * @param functionId identifier of the function
     * @param bindingResult binding result used for validation errors
     * @return the corresponding function if found, otherwise null
     */

    public CFunction validateFunction(Long functionId, final  BindingResult bindingResult) {
        CFunction function = functionRepository.findOne(functionId);
        if (function == null) {
            bindingResult.reject("function", "* The selected function is not available or invalid.");
        }
        return function;
    }

    /**
     * Validates opening balance details and populates mandatory
     * budget-related attributes before persistence.
     *
     * @param openingBalance opening balance budget item
     * @param bindingResult binding result used to capture validation errors
     * @param function associated function
     * @param budgetRegister associated budget register
     */

    public void validateOpeningBudget(BudgetItem openingBalance, final BindingResult bindingResult, CFunction function, BudgetRegister budgetRegister) {
        if (!openingBalance.isValuesFilled()) {
            bindingResult.reject("opening", "* Fill all values of opening balance.");
            if (openingBalance.getCurrentEstimate() == null) {
                bindingResult.rejectValue("opening.currentEstimate",  "opening.currentEstimate", "Budget estimate is invalid.");
            }
            if (openingBalance.getCurrentActual() == null) {
                bindingResult.rejectValue("opening.currentActual",  "opening.currentActual", "Budget estimate actual is invalid.");
            }
            if (openingBalance.getCurrentRevisedEstimate() == null) {
                bindingResult.rejectValue("opening.currentRevisedEstimate",  "opening.currentRevisedEstimate", "Budget revised estimate is invalid.");
            }
            if (openingBalance.getNextEstimate() == null) {
                bindingResult.rejectValue("opening.nextEstimate",  "opening.nextEstimate", "Next estimate is invalid.");
            }
        } else {
            openingBalance.setBudgetGroup("Opening_Balance");
            openingBalance.setFunction(function);
            openingBalance.setFinancialYear(budgetRegister.getFinancialYear());
            openingBalance.setCurrentFinancialYear(budgetRegister.getCurrentFinancialYear());
            openingBalance.setBudgetRegister(budgetRegister);
        }
    }

    /**
     * Validates all submitted budget items, performs budget head and scheme
     * verification, and populates mandatory fields required for persistence.
     *
     * @param budgetItems list of budget items to validate
     * @param bindingResult binding result used to capture validation errors
     * @param function associated function
     * @param budgetRegister associated budget register
     */

    public void validateBudgetItems(List<BudgetItem> budgetItems, final BindingResult bindingResult, CFunction function, BudgetRegister budgetRegister) {

        for (int i = 0; i < budgetItems.size(); i++) {
            BudgetItem item = budgetItems.get(i);

            item.setFunction(function);
            item.setFinancialYear(budgetRegister.getFinancialYear());
            item.setCurrentFinancialYear(budgetRegister.getCurrentFinancialYear());
            item.setBudgetRegister(budgetRegister);

            BudgetHead budgetHead = null;

            if (null != item.getBudgetHead().getId()) {
                budgetHead =  budgetHeadService.findById(item.getBudgetHead().getId());

            }

            if (budgetHead != null) {
                item.setBudgetGroup(budgetHead.getAccountTypeLabel());
                item.setBudgetHead(budgetHead);
            }



            if (Boolean.TRUE.equals(item.getNotApplicable())) {
                item.setScheme(null);
                continue;
            }


            // Numeric validations
            if (item.getCurrentEstimate() == null) {
                bindingResult.rejectValue("items[" + i + "].currentEstimate",
                        "budget.currentEstimate.invalid",
                        "Budget estimate is invalid.");
            }
            if (item.getCurrentActual() == null) {
                bindingResult.rejectValue("items[" + i + "].currentActual",
                        "budget.currentActual.invalid",
                        "Budget actual is invalid.");
            }
            if (item.getCurrentRevisedEstimate() == null) {
                bindingResult.rejectValue("items[" + i + "].currentRevisedEstimate",
                        "budget.currentRevisedEstimate.invalid",
                        "Budget revised estimate is invalid.");
            }
            if (item.getNextEstimate() == null) {
                bindingResult.rejectValue("items[" + i + "].nextEstimate",
                        "budget.nextEstimate.invalid",
                        "Next estimate is invalid.");
            }

            // Validate Budget Head
            if (item.getBudgetHead() == null || item.getBudgetHead().getId() == null) {
                bindingResult.rejectValue("items[" + i + "].budgetHead.id",
                        "budgetHead.id.invalid",
                        "Select a budget head.");
            }

//            BudgetHead head = budgetHeadService.findById(item.getBudgetHead().getId());
            if (budgetHead == null) {
                LOGGER.info("budget head is null on " + item.getBudgetHead().getId());
                bindingResult.rejectValue("items[" + i + "].budgetHead.id",
                        "budgetHead.id.invalid",
                        "Select a valid budget head.");
            }

            if (budgetHead != null) {
                item.setBudgetGroup(budgetHead.getAccountTypeLabel());
                item.setBudgetHead(budgetHead);
            }


            // If program = yes → scheme is required
            if ("yes".equalsIgnoreCase(budgetHead.getProgram())) {

                if (item.getScheme() == null || item.getScheme().getId() == null) {
                    bindingResult.rejectValue("items[" + i + "].scheme.id",
                            "scheme.id.required",
                            "Scheme is required.");
                    continue;
                }

                Scheme scheme = schemeHibernateDAO.getCurrentSession().get(Scheme.class,
                        item.getScheme().getId());
                if (scheme == null) {
                    bindingResult.rejectValue("items[" + i + "].scheme.id",
                            "scheme.id.invalid",
                            "Select a valid scheme.");
                } else {
                    item.setScheme(scheme);
                }

            } else {
                item.setScheme(null);
            }
        }


    }

    /**
     * Saves a complete budget input form including opening balance,
     * budget line items, and automatically calculated closing balance.
     *
     * <p>
     * Revenue and expenditure totals are calculated based on account
     * type codes and used to derive the closing balance values.
     * </p>
     *
     * @param form submitted budget form
     * @param budgetRegister target budget register
     * @param function associated function
     * @throws Exception if an error occurs while saving budget data
     */

    @Transactional(rollbackFor = Exception.class)
    public void saveBudgetInputForm(BudgetForm form, BudgetRegister budgetRegister, CFunction function) throws Exception {


        // save opening budget
        BudgetItem openingBalance = form.getOpening();

        LOGGER.info("opening balance id: " + form.getOpening().getId());
        LOGGER.info("Closing balance id: " + form.getClosing().getId());

        budgetItemRepository.save(form.getOpening());




        // calculate totals

        // Revenue
        BigDecimal BudgetEstimateRevenue = BigDecimal.ZERO;
        BigDecimal ActualRevenue = BigDecimal.ZERO;
        BigDecimal RevisedEstimateRevenue = BigDecimal.ZERO;
        BigDecimal nextBudgetEstimateRevenue = BigDecimal.ZERO;

        // Expenditure
        BigDecimal BudgetEstimateExpenditure = BigDecimal.ZERO;
        BigDecimal ActualExpenditure = BigDecimal.ZERO;
        BigDecimal RevisedEstimateExpenditure = BigDecimal.ZERO;
        BigDecimal nextBudgetEstimateExpenditure = BigDecimal.ZERO;

        if (form.getItems() != null && !form.getItems().isEmpty()) {

                for (BudgetItem item : form.getItems()) {

                    final String code = item.getBudgetHead().getAccountTypeCode();
                    if (code == null)
                        continue;

                    switch (code) {
                        case "RR":
                        case "CR":
                            BudgetEstimateRevenue = BudgetEstimateRevenue.add(item.getSafeCurrentEstimate());
                            ActualRevenue = ActualRevenue.add(item.getSafeCurrentActual());
                            RevisedEstimateRevenue = RevisedEstimateRevenue.add(item.getSafeCurrentRevisedEstimate());
                            nextBudgetEstimateRevenue = nextBudgetEstimateRevenue.add(item.getSafeNextEstimate());
                            break;
                        case "RE":
                        case "CE":
                            BudgetEstimateExpenditure = BudgetEstimateExpenditure.add(item.getSafeCurrentEstimate());
                            ActualExpenditure = ActualExpenditure.add(item.getSafeCurrentActual());
                            RevisedEstimateExpenditure = RevisedEstimateExpenditure
                                    .add(item.getSafeCurrentRevisedEstimate());
                            nextBudgetEstimateExpenditure = nextBudgetEstimateExpenditure.add(item.getSafeNextEstimate());
                            break;
                        default:
                            break;
                    }
                }
        }

        BigDecimal totalBudgetEstimate = BudgetEstimateRevenue.subtract(BudgetEstimateExpenditure);
        BigDecimal totalActual = ActualRevenue.subtract(ActualExpenditure);
        BigDecimal totalRevisedEstimate = RevisedEstimateRevenue.subtract(RevisedEstimateExpenditure);
        BigDecimal totalNextBudgetEstimate = nextBudgetEstimateRevenue.subtract(nextBudgetEstimateExpenditure);


        BudgetItem closingBalance = new BudgetItem();

        if (null != form.getClosing() &&  null != form.getClosing().getId()) {
            closingBalance = form.getClosing();
        }


        closingBalance.setFunction(function);
        closingBalance.setFinancialYear(budgetRegister.getFinancialYear());
        closingBalance.setCurrentFinancialYear(budgetRegister.getCurrentFinancialYear());
        closingBalance.setBudgetGroup("Closing_Balance");
        closingBalance.setCurrentEstimate(openingBalance.getSafeCurrentEstimate().add(totalBudgetEstimate));
        closingBalance.setCurrentActual(openingBalance.getSafeCurrentActual().add(totalActual));
        closingBalance
                .setCurrentRevisedEstimate(openingBalance.getSafeCurrentRevisedEstimate().add(totalRevisedEstimate));
        closingBalance.setNextEstimate(openingBalance.getSafeNextEstimate().add(totalNextBudgetEstimate));

        closingBalance.setBudgetRegister(budgetRegister);

        budgetItemRepository.save(closingBalance);


        // save all budget items
        budgetItemRepository.save(form.getItems());
    }

    /**
     * Retrieves budget items grouped by budget group for the specified
     * account types, function, and budget register.
     *
     * @param types list of budget groups
     * @param function function for which budget items are required
     * @param budgetRegister budget register reference
     * @return budget items grouped by budget group
     */

    public Map<String, List<BudgetItem>> getBudgetItemsByTypesFunctionFyBudgetRegister(
            List<String> types, CFunction function, BudgetRegister budgetRegister) {

        List<BudgetItem> items = budgetItemRepository
                .findByBudgetGroupInAndFunctionAndBudgetRegister(types, function, budgetRegister);


        return items.stream()
                .collect(Collectors.groupingBy(BudgetItem::getBudgetGroup));
    }


    public Map<String, List<BudgetItem>> getBudgetItemsByTypesFunctionAndBudgetRegisterAndApplicable(List<String> types, CFunction function, BudgetRegister budgetRegister) {
//        return budgetItemRepository.findByBudgetGroupInAndFunctionAndBudgetRegister(types, function, budgetRegister).stream().collect(Collectors.groupingBy(BudgetItem::getBudgetGroup));
        return budgetItemRepository.findByBudgetGroupInAndFunctionAndBudgetRegisterAndNotApplicableFalse(types, function, budgetRegister).stream().collect(Collectors.groupingBy(BudgetItem::getBudgetGroup));
    }

    /**
     * Retrieves only applicable budget items grouped by budget group.
     *
     * <p>
     * Budget items marked as not applicable are excluded.
     * </p>
     *
     * @param types list of budget groups
     * @param function function reference
     * @param budgetRegister budget register reference
     * @return grouped applicable budget items
     */

    public Map<String, List<BudgetItem>> getBudgetItemsByTypesFunctionAndBudgetRegister(List<String> types, CFunction function, BudgetRegister budgetRegister) {
        return budgetItemRepository.findByBudgetGroupInAndFunctionAndBudgetRegister(types, function, budgetRegister).stream().collect(Collectors.groupingBy(BudgetItem::getBudgetGroup));
//        return budgetItemRepository.findByBudgetGroupInAndFunctionAndBudgetRegister(types, function, budgetRegister).stream().collect(Collectors.groupingBy(BudgetItem::getBudgetGroup));
    }

    /**
     * Checks whether a budget already exists for the specified function,
     * financial year, and budget register.
     *
     * @param function function reference
     * @param currentFinancialYear financial year reference
     * @param budgetRegister budget register reference
     * @return true if budget exists, otherwise false
     */


    public Boolean checkIfBudgetExistsForFunctionAndFinancialYearAndBudgetRegister(CFunction function,
                                                                                   CFinancialYear currentFinancialYear, BudgetRegister budgetRegister) {
        return budgetItemRepository.existsBudgetForCurrentFYAndBudgetRegister(function.getId(), currentFinancialYear.getId(), budgetRegister.getId());
    }

    private Boolean isExpenditure(String code) {
        return code.equalsIgnoreCase("re") || code.equalsIgnoreCase("ce");
    }

    private Boolean isRevenue(String code) {
        return code.equalsIgnoreCase("rr") || code.equalsIgnoreCase("cr");
    }

    /**
     * Updates an existing budget input form including opening balance,
     * budget line items, and recalculated closing balance.
     *
     * <p>
     * Existing records are updated while newly added rows are inserted.
     * </p>
     *
     * @param form updated budget form
     * @param budgetRegister associated budget register
     */

    @Transactional
    public void updateBudgetInputForm(BudgetForm form, BudgetRegister budgetRegister) {

        try {

            // ========================================
            // 1️⃣ UPDATE OR INSERT OPENING BALANCE
            // ========================================

            BudgetItem opening = form.getOpening();
            if (opening != null) {

                BudgetItem openingBalance = budgetItemRepository.findOne(opening.getId());

                if (openingBalance == null) {
                    throw new Exception("opening balance is null");
                }

                LOGGER.info("saving:");
                LOGGER.info("my id:" + openingBalance.getId());

                openingBalance.setCurrentEstimate(opening.getCurrentEstimate());
                openingBalance.setCurrentActual(opening.getCurrentActual());
                openingBalance.setCurrentRevisedEstimate(opening.getCurrentRevisedEstimate());
                openingBalance.setNextEstimate(opening.getNextEstimate());

                budgetItemRepository.save(openingBalance);
            }

            // ---------------------------------
            // Running totals
            // ---------------------------------
            BigDecimal BudgetEstimateRevenue = BigDecimal.ZERO;
            BigDecimal ActualRevenue = BigDecimal.ZERO;
            BigDecimal RevisedEstimateRevenue = BigDecimal.ZERO;
            BigDecimal nextBudgetEstimateRevenue = BigDecimal.ZERO;

            BigDecimal BudgetEstimateExpenditure = BigDecimal.ZERO;
            BigDecimal ActualExpenditure = BigDecimal.ZERO;
            BigDecimal RevisedEstimateExpenditure = BigDecimal.ZERO;
            BigDecimal nextBudgetEstimateExpenditure = BigDecimal.ZERO;

            // ========================================
            // 2️⃣ MULTIPLE BUDGET ITEMS (ADD / UPDATE)
            // ========================================

            // Fetch function and financial years ONCE, not for every row
            CFunction function = functionRepository.findOne(form.getFunctionid());
            if (function == null) {
                throw new Exception("The selected function not found !");
            }

            CFinancialYear financialYear = financialYearService.findOne(form.getFinancialYear());
            CFinancialYear nextFinancialYear = financialYearService.findOne(form.getCurrentFinancialYear());

            if (financialYear == null || nextFinancialYear == null) {
                throw new Exception("Financial year not found !");
            }

            for (BudgetItem item : form.getItems()) {

                if (item == null)
                    continue;

                // NOTE: Some rows may be empty – skip them safely
                if (item.getBudgetHead() == null || item.getBudgetHead().getId() == null)
                    continue;

                // --- Validate Budget Head ---
                BudgetHead bh = budgetHeadService.findById(item.getBudgetHead().getId());
                if (bh == null) {
                    throw new Exception("Invalid budget head on " + item.getBudgetGroup());
                }
                item.setBudgetHead(bh);

                // --- Validate Scheme ---
                if (item.getScheme() != null && item.getScheme().getId() != null) {

                    Scheme scheme = schemeHibernateDAO.getCurrentSession()
                            .get(Scheme.class, item.getScheme().getId());

                    if (scheme == null) {
                        throw new Exception("Invalid scheme on " + item.getBudgetGroup());
                    }

                    item.setScheme(scheme); // valid scheme
                } else {
                    item.setScheme(null); // UI cleared → destroy scheme
                }

                // --------------------------------------------------------------------
                // FIX: New row detection (VERY IMPORTANT)
                // --------------------------------------------------------------------
                if (item.getId() == null || item.getId() == 0) {
                    // ---- INSERT NEW RECORD ----
                    LOGGER.info("Inserting new record → " + item.getBudgetCode());

                    item.setFunction(function);
                    item.setFinancialYear(financialYear);
                    item.setCurrentFinancialYear(nextFinancialYear);
                    item.setBudgetRegister(budgetRegister);

                    budgetItemRepository.save(item);
                } else {

                    // ---- UPDATE EXISTING RECORD ----
                    BudgetItem budgetInput = budgetItemRepository.findOne(item.getId());

                    if (budgetInput == null) {
                        // fail-safe: if ID sent but record missing → treat as new
                        LOGGER.warn("ID sent but no record found. Creating as new.");
                        item.setId(null);
                        item.setFunction(function);
                        item.setFinancialYear(financialYear);
                        item.setCurrentFinancialYear(nextFinancialYear);
                        item.setBudgetRegister(budgetRegister);
                        budgetItemRepository.save(item);
                        continue;
                    }

                    budgetInput.setCurrentEstimate(item.getCurrentEstimate());
                    budgetInput.setCurrentActual(item.getCurrentActual());
                    budgetInput.setCurrentRevisedEstimate(item.getCurrentRevisedEstimate());
                    budgetInput.setNextEstimate(item.getNextEstimate());

                    budgetInput.setBudgetCode(item.getBudgetCode());
                    budgetInput.setBudgetGroup(item.getBudgetGroup());
                    budgetInput.setBudgetHead(item.getBudgetHead());
                    budgetInput.setStateBudgetCode(item.getStateBudgetCode());

                    // Scheme logic
                    if (item.getScheme() == null) {
                        budgetInput.setScheme(null); // destroy old scheme
                    } else {
                        budgetInput.setScheme(item.getScheme()); // keep/update scheme
                    }

                    budgetItemRepository.save(budgetInput);
                }

                // --------------------------------------------------------------------
                // Categorization
                // --------------------------------------------------------------------
                final String code = item.getBudgetHead().getAccountTypeCode();
                if (code == null)
                    continue;

                switch (code) {

                    case "RR":
                    case "CR":
                        BudgetEstimateRevenue = BudgetEstimateRevenue.add(item.getCurrentEstimate());
                        ActualRevenue = ActualRevenue.add(item.getCurrentActual());
                        RevisedEstimateRevenue = RevisedEstimateRevenue.add(item.getCurrentRevisedEstimate());
                        nextBudgetEstimateRevenue = nextBudgetEstimateRevenue.add(item.getNextEstimate());
                        break;

                    case "RE":
                    case "CE":
                        BudgetEstimateExpenditure = BudgetEstimateExpenditure.add(item.getCurrentEstimate());
                        ActualExpenditure = ActualExpenditure.add(item.getCurrentActual());
                        RevisedEstimateExpenditure = RevisedEstimateExpenditure.add(item.getCurrentRevisedEstimate());
                        nextBudgetEstimateExpenditure = nextBudgetEstimateExpenditure.add(item.getNextEstimate());
                        break;
                }
            }

            // ---------------------------------
            // Compute Final Totals
            // ---------------------------------
            BigDecimal totalBudgetEstimate = BudgetEstimateRevenue.subtract(BudgetEstimateExpenditure);
            BigDecimal totalActual = ActualRevenue.subtract(ActualExpenditure);
            BigDecimal totalRevisedEstimate = RevisedEstimateRevenue.subtract(RevisedEstimateExpenditure);
            BigDecimal totalNextBudgetEstimate = nextBudgetEstimateRevenue.subtract(nextBudgetEstimateExpenditure);

            LOGGER.info("Budget Estimate:{}, Actual:{}, Revised Estimate:{}, Next Budget Estimate:{}",
                    totalBudgetEstimate, totalActual, totalRevisedEstimate, totalNextBudgetEstimate);

            BudgetItem openingBalance = budgetItemRepository.findOne(form.getOpening().getId());

            // ---------------------------------
            // Closing Balance
            // ---------------------------------
            BudgetItem closingBalance = budgetItemRepository.findByFunctionAndBudgetGroupAndBudgetRegister(function, "Closing_Balance", budgetRegister);

            closingBalance.setCurrentEstimate(openingBalance.getCurrentEstimate().add(totalBudgetEstimate));
            closingBalance.setCurrentActual(openingBalance.getCurrentActual().add(totalActual));
            closingBalance
                    .setCurrentRevisedEstimate(openingBalance.getCurrentRevisedEstimate().add(totalRevisedEstimate));
            closingBalance.setNextEstimate(openingBalance.getNextEstimate().add(totalNextBudgetEstimate));

            budgetItemRepository.save(closingBalance);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<CFunction> functionListWithBudget() {
        List<CFunction> functions = budgetItemRepository.findDistinctFunctionsWithBudgetItems();

        return functions;
    }

    /**
     * Retrieves functions for which budget entries exist under the
     * specified budget register.
     *
     * @param budgetRegister budget register reference
     * @return list of functions having budget entries
     */

    public List<CFunction> functionsHavingBudgetOfBudgetRegister(BudgetRegister budgetRegister) {
        List<CFunction> functions = budgetItemRepository.findDistinctFunctionsByBudgetRegisterWithBudgetItems(budgetRegister.getId());

        return functions;
    }


    /**
     * Retrieves functions for which budget entries exist under the
     * specified budget register.
     *
     * @param budgetRegister budget register reference
     * @return list of functions having budget entries
     */

    public Map<String, List<BudgetItem>> getBudgetItemsByTypesAndBudgetRegister(
            List<String> types,BudgetRegister budgetRegister) {

        List<BudgetItem> items = budgetItemRepository.findByBudgetGroupInAndBudgetRegisterAndNotApplicableFalse(types, budgetRegister);

        return items.stream()
                .collect(Collectors.groupingBy(BudgetItem::getBudgetGroup));
    }


    /**
     * Checks whether budget data already exists for the specified
     * function within the given budget register.
     *
     * @param budgetRegister budget register reference
     * @param function function reference
     * @return true if budget data exists, otherwise false
     */

    public boolean validateIfFunctionBudgetExists(BudgetRegister budgetRegister, CFunction function) {
        return budgetItemRepository.existsFunctionWiseBudget(function.getId(), budgetRegister.getId());
    }

    /**
     * Repopulates model attributes required to redisplay the budget form
     * after validation failures.
     *
     * <p>
     * This method rebuilds grouped budget item structures and restores
     * all form-related data for rendering on the UI.
     * </p>
     *
     * @param model spring model
     * @param function selected function
     * @param budgetRegisterId budget register identifier
     * @param budgetForm submitted budget form
     * @param budgetRegister budget register reference
     */


    public void populateValidationErrors(Model model, CFunction function, Long budgetRegisterId, BudgetForm budgetForm, BudgetRegister budgetRegister) {
        model.addAttribute("id", function.getId());
        model.addAttribute("function", function);
        model.addAttribute("budgetRegisterId", budgetRegisterId);

        List<BudgetHead> heads = budgetHeadService.getBudgetHeadsByFunction(function);

        List<BudgetItem> budgetItems = budgetForm.getItems();


        Map<BudgetAccountType, Map<String, List<BudgetItem>>> groupedItems = budgetItems.stream()
                .peek(budgetItem -> {
                    Optional<BudgetHead> budgetHead = heads.stream()
                            .filter(bh -> bh.getId().equals(budgetItem.getBudgetHead().getId()))
                            .findFirst();

                    Scheme scheme = budgetItem.getScheme();

                    if (scheme != null && scheme.getId() != null) {

                       scheme = schemeHibernateDAO.getCurrentSession().get(Scheme.class,
                                budgetItem.getScheme().getId());
                        if (scheme != null) {
                            budgetItem.setScheme(scheme);
                        }
                    }

                    BudgetHead bh = budgetHead.get();

                    budgetItem.setBudgetGroup(bh.getAccountTypeLabel());
                    budgetItem.setBudgetHead(bh);
                    budgetItem.setFunction(function);
                    budgetItem.setBudgetRegister(budgetRegister);
                    budgetItem.setCurrentFinancialYear(budgetRegister.getCurrentFinancialYear());
                    budgetItem.setFinancialYear(budgetRegister.getFinancialYear());
                })
                .sorted(Comparator.comparing(item -> item.getBudgetHead().getOrder())) // sort by order
                .collect(Collectors.groupingBy(
                        item -> item.getBudgetHead().getAccountType(),
                        LinkedHashMap::new, // preserve AccountType order
                        Collectors.groupingBy(
                                itm -> itm.getBudgetHead().getCategory(),
                                LinkedHashMap::new, // preserve Category order
                                Collectors.toList())));

        model.addAttribute("groupedItems", groupedItems);


        model.addAttribute("budgetForm", budgetForm);
        model.addAttribute("budgetRegister", budgetRegister);
        model.addAttribute("currentFy", budgetRegister.getCurrentFinancialYear());
        model.addAttribute("nextFy", budgetRegister.getFinancialYear());

    }

    /**
     * Populates model attributes required for editing an existing budget.
     *
     * @param model spring model
     * @param function selected function
     * @param budgetRegisterId budget register identifier
     * @param budgetForm budget form containing existing values
     * @param budgetRegister budget register reference
     */

    public void populateForEdit(Model model, CFunction function, Long budgetRegisterId, BudgetForm budgetForm, BudgetRegister budgetRegister) {

        model.addAttribute("id", function.getId());
        model.addAttribute("function", function);
        model.addAttribute("budgetRegisterId", budgetRegisterId);


        List<BudgetHead> heads = budgetHeadService.getBudgetHeadsByFunction(function);

        List<BudgetItem> budgetItems = budgetForm.getItems();

        Map<BudgetAccountType, Map<String, List<BudgetItem>>> groupedItems = budgetItems.stream()
                .peek(budgetItem -> {
                    Optional<BudgetHead> budgetHead = heads.stream()
                            .filter(bh -> bh.getId().equals(budgetItem.getBudgetHead().getId()))
                            .findFirst();

                    if (!budgetHead.isPresent()) {
                        LOGGER.info("budget head is null for - " + budgetItem.getId());
                    }

                    Scheme scheme = budgetItem.getScheme();

                    if (scheme != null && scheme.getId() != null) {

                        scheme = schemeHibernateDAO.getCurrentSession().get(Scheme.class,
                                budgetItem.getScheme().getId());
                        if (scheme != null) {
                            budgetItem.setScheme(scheme);
                        }
                    }

                    BudgetHead bh = budgetHead.get();

                    budgetItem.setBudgetGroup(bh.getAccountTypeLabel());
                    budgetItem.setBudgetHead(bh);
                    budgetItem.setFunction(function);
                    budgetItem.setBudgetRegister(budgetRegister);
                    budgetItem.setCurrentFinancialYear(budgetRegister.getCurrentFinancialYear());
                    budgetItem.setFinancialYear(budgetRegister.getFinancialYear());
                })
                .sorted(Comparator.comparing(item -> item.getBudgetHead().getOrder())) // sort by order
                .collect(Collectors.groupingBy(
                        item -> item.getBudgetHead().getAccountType(),
                        LinkedHashMap::new, // preserve AccountType order
                        Collectors.groupingBy(
                                itm -> itm.getBudgetHead().getCategory(),
                                LinkedHashMap::new, // preserve Category order
                                Collectors.toList())));


        model.addAttribute("groupedItems", groupedItems);


        model.addAttribute("budgetForm", budgetForm);
        model.addAttribute("budgetRegister", budgetRegister);
        model.addAttribute("currentFy", budgetRegister.getCurrentFinancialYear());
        model.addAttribute("nextFy", budgetRegister.getFinancialYear());
    }


    @Transactional(rollbackFor = Exception.class)
    public void saveAndUpdateBudgetInputForm(BudgetForm form, BudgetRegister budgetRegister, CFunction function) throws Exception {


        // save opening budget
        BudgetItem openingBalance = form.getOpening();
        BudgetItem openingBalanceDb = budgetItemRepository.findById(openingBalance.getId());
        openingBalanceDb.setCurrentEstimate(openingBalance.getCurrentEstimate());
        openingBalanceDb.setCurrentActual(openingBalance.getCurrentActual());
        openingBalanceDb.setCurrentRevisedEstimate(openingBalance.getCurrentRevisedEstimate());
        openingBalanceDb.setNextEstimate(openingBalance.getNextEstimate());

        budgetItemRepository.save(openingBalanceDb);


        // calculate totals

        // Revenue
        BigDecimal BudgetEstimateRevenue = BigDecimal.ZERO;
        BigDecimal ActualRevenue = BigDecimal.ZERO;
        BigDecimal RevisedEstimateRevenue = BigDecimal.ZERO;
        BigDecimal nextBudgetEstimateRevenue = BigDecimal.ZERO;

        // Expenditure
        BigDecimal BudgetEstimateExpenditure = BigDecimal.ZERO;
        BigDecimal ActualExpenditure = BigDecimal.ZERO;
        BigDecimal RevisedEstimateExpenditure = BigDecimal.ZERO;
        BigDecimal nextBudgetEstimateExpenditure = BigDecimal.ZERO;

        if (form.getItems() != null && !form.getItems().isEmpty()) {

            for (BudgetItem item : form.getItems()) {

                final String code = item.getBudgetHead().getAccountTypeCode();
                if (code == null)
                    continue;

                switch (code) {
                    case "RR":
                    case "CR":
                        BudgetEstimateRevenue = BudgetEstimateRevenue.add(item.getSafeCurrentEstimate());
                        ActualRevenue = ActualRevenue.add(item.getSafeCurrentActual());
                        RevisedEstimateRevenue = RevisedEstimateRevenue.add(item.getSafeCurrentRevisedEstimate());
                        nextBudgetEstimateRevenue = nextBudgetEstimateRevenue.add(item.getSafeNextEstimate());
                        break;
                    case "RE":
                    case "CE":
                        BudgetEstimateExpenditure = BudgetEstimateExpenditure.add(item.getSafeCurrentEstimate());
                        ActualExpenditure = ActualExpenditure.add(item.getSafeCurrentActual());
                        RevisedEstimateExpenditure = RevisedEstimateExpenditure
                                .add(item.getSafeCurrentRevisedEstimate());
                        nextBudgetEstimateExpenditure = nextBudgetEstimateExpenditure.add(item.getSafeNextEstimate());
                        break;
                    default:
                        break;
                }
            }
        }

        BigDecimal totalBudgetEstimate = BudgetEstimateRevenue.subtract(BudgetEstimateExpenditure);
        BigDecimal totalActual = ActualRevenue.subtract(ActualExpenditure);
        BigDecimal totalRevisedEstimate = RevisedEstimateRevenue.subtract(RevisedEstimateExpenditure);
        BigDecimal totalNextBudgetEstimate = nextBudgetEstimateRevenue.subtract(nextBudgetEstimateExpenditure);



        BudgetItem closingBalance = form.getClosing();
        BudgetItem closingBalanceDb = budgetItemRepository.findById(closingBalance.getId());

        closingBalanceDb.setCurrentEstimate(openingBalance.getSafeCurrentEstimate().add(totalBudgetEstimate));
        closingBalanceDb.setCurrentActual(openingBalance.getSafeCurrentActual().add(totalActual));
        closingBalanceDb
                .setCurrentRevisedEstimate(openingBalance.getSafeCurrentRevisedEstimate().add(totalRevisedEstimate));
        closingBalanceDb.setNextEstimate(openingBalance.getSafeNextEstimate().add(totalNextBudgetEstimate));

        budgetItemRepository.save(closingBalanceDb);


        List<BudgetItem> budgetItems = form.getItems();
        List<BudgetItem> budgetItemsDb = budgetItemRepository.findByBudgetGroupInAndFunctionAndBudgetRegister(Arrays.asList("Revenue_Budget", "Capital_Budget"), function, budgetRegister);

        Map<Long, BudgetItem> submittedById =
                budgetItems.stream()
                        .filter(bi -> bi.getId() != null)
                        .collect(Collectors.toMap(
                                BudgetItem::getId,
                                Function.identity()
                        ));

        for (BudgetItem dbItem : budgetItemsDb) {

            BudgetItem submitted = submittedById.get(dbItem.getId());

            if (submitted == null) {
                continue;
            }

            dbItem.setCurrentEstimate(submitted.getCurrentEstimate());
            dbItem.setCurrentActual(submitted.getCurrentActual());
            dbItem.setCurrentRevisedEstimate(submitted.getCurrentRevisedEstimate());
            dbItem.setNextEstimate(submitted.getNextEstimate());

            dbItem.setScheme(submitted.getScheme());
            dbItem.setNotApplicable(submitted.getNotApplicable());
        }

        // save all budget items
        budgetItemRepository.save(budgetItemsDb);
    }

}


