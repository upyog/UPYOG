package org.egov.egf.web.controller.budget;

import org.apache.log4j.Logger;
import org.egov.commons.CFinancialYear;
import org.egov.commons.CFunction;
import org.egov.commons.Scheme;
import org.egov.commons.service.CFinancialYearService;
import org.egov.commons.service.FunctionService;
import org.egov.egf.form.BudgetForm;
import org.egov.eis.web.contract.WorkflowContainer;
import org.egov.eis.web.controller.workflow.GenericWorkFlowController;
import org.egov.infra.admin.master.entity.User;
import org.egov.infra.microservice.models.EmployeeInfo;
import org.egov.infra.microservice.utils.MicroserviceUtils;
import org.egov.infra.security.utils.SecurityUtils;
import org.egov.masters.model.AccountEntity;
import org.egov.model.budget.*;
import org.egov.model.service.BudgetHeadService;
import org.egov.model.service.BudgetItemService;
import org.egov.model.service.BudgetRegisterWorkflowService;
import org.egov.model.service.FunctionBudgetHeadService;
import org.egov.services.masters.SchemeService;
import org.egov.utils.BudgetAccountType;
import org.egov.utils.FinancialConstants;
import org.hibernate.validator.constraints.SafeHtml;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;



/**
 * Controller responsible for managing function-wise budget entry,
 * maintenance, and reporting operations.
 *
 * <p>
 * This controller provides functionality for creating, updating,
 * viewing, and consolidating budget information associated with
 * budget registers and functions.
 * </p>
 *
 * <h3>Primary Responsibilities</h3>
 * <ul>
 *     <li>Create function-wise budgets against a budget register.</li>
 *     <li>Validate budget register, function, budget head, and scheme information.</li>
 *     <li>Manage opening balance, budget entries, and closing balance calculations.</li>
 *     <li>Support budget editing and update operations.</li>
 *     <li>Display function-wise and consolidated budget views.</li>
 *     <li>Prevent duplicate budget creation for a function within a budget register.</li>
 * </ul>
 *
 * <h3>Workflow</h3>
 * <ol>
 *     <li>Select Budget Register.</li>
 *     <li>Select Function.</li>
 *     <li>Enter Opening Balance.</li>
 *     <li>Capture Revenue and Capital Budget Items.</li>
 *     <li>Automatically calculate Closing Balance.</li>
 *     <li>View or Edit submitted budget information.</li>
 * </ol>
 *
 * <h3>Key Features</h3>
 * <ul>
 *     <li>Function-wise budget preparation and maintenance.</li>
 *     <li>Automatic budget code generation.</li>
 *     <li>Grouping of budget items by account type and category.</li>
 *     <li>Opening and closing balance management.</li>
 *     <li>Scheme-based budget head support.</li>
 *     <li>Consolidated budget summary generation.</li>
 *     <li>Financial year validation and budget register integration.</li>
 * </ul>
 *
 * <h3>Supported Views</h3>
 * <ul>
 *     <li><code>budgetitem-new</code> - Function selection screen.</li>
 *     <li><code>functionwisebudget-form</code> - Budget entry form.</li>
 *     <li><code>budgetitem-view</code> - Budget detail view.</li>
 *     <li><code>budgetitem-edit</code> - Budget edit form.</li>
 *     <li><code>budgetitem-function</code> - Function-wise budget listing.</li>
 *     <li><code>budgetitemcomplete-view</code> - Consolidated budget report.</li>
 * </ul>
 *
 * <h3>Business Rules</h3>
 * <ul>
 *     <li>A function can have only one budget within a budget register.</li>
 *     <li>Opening balance is mandatory before budget entry.</li>
 *     <li>Closing balance is automatically derived from opening balance
 *         and budget totals.</li>
 *     <li>Budget items are grouped under Revenue and Capital categories.</li>
 *     <li>Only valid budget heads and schemes are permitted.</li>
 * </ul>
 *
 * @see BudgetItem
 * @see BudgetForm
 * @see BudgetRegister
 * @see BudgetItemService
 * @see BudgetRegisterWorkflowService
 */

@Controller
@RequestMapping("/budget")
public class BudgetItemController {
	private static final String BUDGET_ITEM_NEW = "budgetitem-new";
	private static final String BUDGET_ITEM = "budgetItem";
	private static final String BUDGET_FORM = "budgetitem-form";
	private static final String BUDGET_ITEM_VIEW = "budgetitem-view";
	private static final String BUDGET_ITEM_EDIT = "budgetitem-edit";
	private static final String BUDGET_FUNCTION = "budgetitem-function";
	private static final String BUDGET_COMPLETE_VIEW = "budgetitemcomplete-view";

	private static final String STATE_TYPE = "stateType";

	private static final Logger LOGGER = Logger.getLogger(BudgetItemController.class);

	@Autowired
    private SchemeService schemeService;

	@Autowired
	private FunctionService functionService;

	@Autowired
	private BudgetItemService budgetItemService;

	@Autowired
	private CFinancialYearService financialYearService;

	@Autowired
	private BudgetHeadService budgetHeadService;

	@Autowired
	private FunctionBudgetHeadService functionBudgetHeadService;

	@Autowired
	private BudgetRegisterWorkflowService budgetRegisterWorkflowService;

	@Autowired
	private MicroserviceUtils microServiceUtil;

	@Autowired
	private SecurityUtils securityUtils;

	@RequestMapping(value = "/new/{budgetRegisterId}", method = { RequestMethod.GET, RequestMethod.POST })
	public String newForm(final Model model, @PathVariable("budgetRegisterId") Long budgetRegisterId) {
		// model.addAttribute(BUDGET_ITEM, new BudgetItem());
		prepareIfBudgetCanInput(model);
		model.addAttribute("function", new CFunction());

		BudgetRegister budgetRegister = budgetRegisterWorkflowService.findOne(budgetRegisterId);

		if (budgetRegister == null) {
			model.addAttribute("error", "Selected Budget register not available or invalid.");
		}

		model.addAttribute("budgetRegisterId", budgetRegisterId);
		return BUDGET_ITEM_NEW;
	}

	private void prepareIfBudgetCanInput(Model model) {
		addFinancialYears(model);
	}

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


	@RequestMapping(value = "/form", method = { RequestMethod.POST })
	public String budgetForm(@ModelAttribute("id") Long id, @ModelAttribute("budgetRegisterId") Long budgetRegisterId,
			final Model model, RedirectAttributes redirectAttributes) {

		Map<String, CFinancialYear> financialYears = addFinancialYears(model);

		if (financialYears == null || financialYears.size() < 2) {
			return "budget/new/" + budgetRegisterId;
		}

		CFunction function = functionService.findOne(id);

		BudgetRegister budgetRegister = budgetRegisterWorkflowService.findOne(budgetRegisterId);

		if (budgetRegister == null) {
			redirectAttributes.addAttribute("error", "Selected Budget register not available or invalid.");
			return "redirect:/budget/new/" + budgetRegisterId;
		}

		model.addAttribute("budgetRegisterId", budgetRegisterId);

		Boolean budgetAlreadyEntered = checkIfBudgetAlreadyEntered(function, budgetRegister.getCurrentFinancialYear(), budgetRegister);

		if (Boolean.TRUE.equals(budgetAlreadyEntered)) {
			redirectAttributes.addFlashAttribute("error", "Budget already entered for the selected function.");
			return "redirect:/budget/new/" + budgetRegisterId;
		}

		List<Scheme> schemes = schemeService.getBySchemeCode();

		model.addAttribute("schemes", schemes);

		model.addAttribute("function", function);

		BudgetForm budgetForm = new BudgetForm();
		budgetForm.setItems(new ArrayList<>());
		budgetForm.getItems().add(new BudgetItem());

		model.addAttribute("budgetForm", budgetForm);

		addFinancialYears(model);

		return BUDGET_FORM;
	}

	private Boolean checkIfBudgetAlreadyEntered(CFunction function, CFinancialYear currentFy,
			BudgetRegister budgetRegister) {
//		final CFinancialYear currentFy = financialYears.get("currentFy");
		// Boolean budgetExists =
		// budgetItemService.checkIfBudgetExistsForFunctionAndFinancialYear(function,
		// currentFy);
		Boolean budgetExists = budgetItemService
				.checkIfBudgetExistsForFunctionAndFinancialYearAndBudgetRegister(function, currentFy, budgetRegister);
		return budgetExists;
	}




	@PostMapping("/create")
	@Transactional
	public String save(final Model model, @ModelAttribute @Valid BudgetForm budgetForm, final BindingResult resultBinder, @ModelAttribute("budgetRegisterId") Long budgetRegisterId, RedirectAttributes redirectAttrs,
					   final HttpServletRequest request) throws Exception {


		// validate budget register
		BudgetRegister budgetRegister = null;
		budgetRegister =  budgetItemService.validateBudgetRegister(budgetRegisterId, resultBinder);

		if (budgetRegister == null) {
			resultBinder.reject("budgetRegister", "Selected budget register is invalid or not available.");
			return "error/422";
		}

		//validate function
		CFunction function = budgetItemService.validateFunction(budgetForm.getFunctionid(), resultBinder);
		if (function == null) {
			resultBinder.reject("function", "Selected function is invalid or not available.");
			return "error/422";
		}

		// validate if already exists
		boolean alreadyExists =  budgetItemService.validateIfFunctionBudgetExists(budgetRegister, function);
		if (alreadyExists) {
			return "error/422";
		}


		// validate opening balance
		budgetItemService.validateOpeningBudget(budgetForm.getOpening(), resultBinder, function, budgetRegister);


		// validate each entries
		budgetItemService.validateBudgetItems(budgetForm.getItems(), resultBinder, function, budgetRegister);


		if (resultBinder.hasErrors()) {
			budgetItemService.populateValidationErrors(model, function, budgetRegisterId, budgetForm, budgetRegister);
			return BUDGET_FORM;
//			LOGGER.info("returning the view!");
//			return "functionwisebudget-form";
		}


		try {
			budgetItemService.saveBudgetInputForm(budgetForm, budgetRegister, function);
		} catch (Exception e) {
			e.printStackTrace();
			return "error/422";
        }


        redirectAttrs.addFlashAttribute("message", "Budget items saved successfully!");

		return "forward:/budget/view/" + budgetForm.getFunctionid() + "/" + budgetRegisterId;
	}



	@RequestMapping(value = "/newv2", method = { RequestMethod.GET, RequestMethod.POST })
	public String newFormv2(final Model model, @ModelAttribute("id") Long id, @ModelAttribute("budgetRegisterId") Long budgetRegisterId, final RedirectAttributes redirectAttributes) {


		CFunction function = functionService.findOne(id);
		model.addAttribute("function", function);

		BudgetRegister budgetRegister = budgetRegisterWorkflowService.findOne(budgetRegisterId);
		if (budgetRegister == null) {
			redirectAttributes.addAttribute("error", "Selected Budget register not available or invalid.");
			return "redirect:/budget/new/" + budgetRegisterId;
		}

		model.addAttribute("budgetRegisterId", budgetRegisterId);
		model.addAttribute("budgetRegister", budgetRegister);

		Boolean budgetAlreadyEntered = checkIfBudgetAlreadyEntered(function, budgetRegister.getCurrentFinancialYear(), budgetRegister);

		if (Boolean.TRUE.equals(budgetAlreadyEntered)) {
			redirectAttributes.addFlashAttribute("error", "Budget already entered for the selected function.");
			return "redirect:/budget/new/" + budgetRegisterId;
		}

		BudgetForm budgetForm = new BudgetForm();
		BudgetItem openingBalance = new BudgetItem();
		openingBalance.setFinancialYear(budgetRegister.getFinancialYear());
		openingBalance.setCurrentFinancialYear(budgetRegister.getCurrentFinancialYear());
		openingBalance.setFunction(function);
		openingBalance.setBudgetRegister(budgetRegister);
		openingBalance.setBudgetGroup("Opening_Balance");

		budgetForm.setOpening(openingBalance);


		List<BudgetHead> heads = budgetHeadService.getBudgetHeadsByFunction(function);

		LOGGER.info("generateBC:");
		List<BudgetItem> budgetItems = heads.stream().map(budgetHead -> {
			BudgetItem budgetItem = new BudgetItem();
			budgetItem.setBudgetHead(budgetHead);
			budgetItem.setFunction(function);
			budgetItem.setBudgetRegister(budgetRegister);
			budgetItem.setCurrentFinancialYear(budgetRegister.getCurrentFinancialYear());
			budgetItem.setFinancialYear(budgetRegister.getFinancialYear());
			LOGGER.info(budgetItem.generateBudgetCode());
			budgetItem.setBudgetCode(budgetItem.generateBudgetCode());
			budgetItem.setStateBudgetCode(budgetHead.getStateCode());

			return budgetItem;
		}).collect(Collectors.toList());


		AtomicInteger counter = new AtomicInteger(0);
		budgetItems.forEach(item -> item.setRowIndex(counter.getAndIncrement()));



		Map<BudgetAccountType, Map<String, List<BudgetItem>>> groupedItems = budgetItems.stream()
				.sorted(Comparator.comparing(item -> item.getBudgetHead().getOrder())) // sort by order
				.collect(Collectors.groupingBy(
						item -> item.getBudgetHead().getAccountType(),
						LinkedHashMap::new, // preserve AccountType order
						Collectors.groupingBy(
								itm -> itm.getBudgetHead().getCategory(),
								LinkedHashMap::new, // preserve Category order
								Collectors.toList())));

		model.addAttribute("groupedItems", groupedItems);


		budgetForm.setItems(budgetItems);
		budgetForm.setFunctionid(function.getId());
		budgetForm.setCurrentFinancialYear(budgetRegister.getCurrentFinancialYear().getId());
		budgetForm.setFinancialYear(budgetRegister.getFinancialYear().getId());

		model.addAttribute("budgetForm", budgetForm);



		return "functionwisebudget-form";
	}

	@RequestMapping(value = "/view/{functionId}/{budgetRegisterId}", method = { RequestMethod.GET, RequestMethod.POST })
	public String view(final Model model, @PathVariable Long functionId,
			@PathVariable("budgetRegisterId") Long budgetRegisterId, RedirectAttributes redirectAttributes)
			throws Exception {

		final CFunction function = functionService.findOne(functionId);

		if (function == null) {
			throw new Exception("Selected function is invalid!");
		}

		model.addAttribute("function", function);


		BudgetRegister budgetRegister = budgetRegisterWorkflowService.findOne(budgetRegisterId);

		if (budgetRegister == null) {
			redirectAttributes.addAttribute("error", "Selected Budget register not available or invalid.");
			return "redirect:/budget/new";
		}

		model.addAttribute("currentFy", budgetRegister.getCurrentFinancialYear());
		model.addAttribute("nextFy", budgetRegister.getFinancialYear());

		model.addAttribute("budgetRegisterId", budgetRegisterId);

		List<String> types = Arrays.asList("Opening_Balance", "Closing_Balance", "Revenue_Budget", "Capital_Budget");
		Map<String, List<BudgetItem>> grouped = budgetItemService.getBudgetItemsByTypesFunctionAndBudgetRegisterAndApplicable(types, function,
				 budgetRegister);

		final List<BudgetItem> oBal = grouped.getOrDefault("Opening_Balance", Collections.emptyList());
		final List<BudgetItem> cBal = grouped.getOrDefault("Closing_Balance", Collections.emptyList());
		final List<BudgetItem> rb = grouped.getOrDefault("Revenue_Budget", Collections.emptyList());
		final List<BudgetItem> cb = grouped.getOrDefault("Capital_Budget", Collections.emptyList());

		LOGGER.info("Opening Balance");
		oBal.forEach(budgetItem -> {
			LOGGER.info(budgetItem.getBudgetGroup());
		});

		model.addAttribute("opening_balance", oBal);
		model.addAttribute("closing_balance", cBal);

		// grouping for revenue budget
		Map<BudgetAccountType, Map<String, List<BudgetItem>>> groupedRB = rb.stream().collect(Collectors.groupingBy(
				item -> item.getBudgetHead().getAccountType(),
				Collectors.groupingBy(
						itm -> itm.getBudgetHead().getCategory())));

		model.addAttribute("grouped_rb", groupedRB);

		// grouping for capital budget
		Map<BudgetAccountType, Map<String, List<BudgetItem>>> groupedCB = cb.stream().collect(Collectors.groupingBy(
				item -> item.getBudgetHead().getAccountType(),
				Collectors.groupingBy(
						itm -> itm.getBudgetHead().getCategory())));

		model.addAttribute("grouped_cb", groupedCB);

		Map<String, BudgetTotals> rbTotals = new LinkedHashMap<>();

		for (Map.Entry<BudgetAccountType, Map<String, List<BudgetItem>>> acct : groupedRB.entrySet()) {
			for (Map.Entry<String, List<BudgetItem>> cat : acct.getValue().entrySet()) {
				rbTotals.put(cat.getKey(), computeTotals(cat.getValue()));
			}
		}

		model.addAttribute("rbTotals", rbTotals);

		Map<String, BudgetTotals> cbTotals = new LinkedHashMap<>();

		for (Map.Entry<BudgetAccountType, Map<String, List<BudgetItem>>> acct : groupedCB.entrySet()) {
			for (Map.Entry<String, List<BudgetItem>> cat : acct.getValue().entrySet()) {
				cbTotals.put(cat.getKey(), computeTotals(cat.getValue()));
			}
		}

		model.addAttribute("cbTotals", cbTotals);

		return BUDGET_ITEM_VIEW;
	}

	private boolean shouldSkip(String type, List<BudgetItem> items) {
		return type == null
				|| items == null
				|| items.isEmpty()
				|| "Opening_Balance".equals(type)
				|| "Closing_Balance".equals(type);
	}

	private BudgetTotals computeTotals(List<BudgetItem> items) {

		BigDecimal est = items.stream()
				.map(BudgetItem::getCurrentEstimate)
				.filter(Objects::nonNull)
				.reduce(BigDecimal.ZERO, BigDecimal::add);

		BigDecimal act = items.stream()
				.map(BudgetItem::getCurrentActual)
				.filter(Objects::nonNull)
				.reduce(BigDecimal.ZERO, BigDecimal::add);

		BigDecimal rev = items.stream()
				.map(BudgetItem::getCurrentRevisedEstimate)
				.filter(Objects::nonNull)
				.reduce(BigDecimal.ZERO, BigDecimal::add);

		BigDecimal nxt = items.stream()
				.map(BudgetItem::getNextEstimate)
				.filter(Objects::nonNull)
				.reduce(BigDecimal.ZERO, BigDecimal::add);

		return new BudgetTotals(est, act, rev, nxt);
	}

	@RequestMapping(value = "/edit/{functionId}/{budgetRegisterId}", method = { RequestMethod.GET, RequestMethod.POST })
	public String edit(@PathVariable Long functionId, @PathVariable("budgetRegisterId") Long budgetRegisterId,
			Model model, RedirectAttributes redirectAttributes) throws Exception {

		final CFunction function = functionService.findOne(functionId);

		if (function == null) {
			throw new Exception("Selected function is invalid!");
		}

		model.addAttribute("function", function);

		BudgetRegister budgetRegister = budgetRegisterWorkflowService.findOne(budgetRegisterId);

		if (budgetRegister == null) {
			redirectAttributes.addAttribute("error", "Selected Budget register not available or invalid.");
			return "redirect:/budget/new";
		}

		model.addAttribute("budgetRegisterId", budgetRegisterId);
		model.addAttribute("budgetRegister", budgetRegister);

		final CFinancialYear currentFy = financialYearService.getCurrentFinancialYear();

		if (currentFy == null) {
			throw new Exception("Financial year is invalid !");
		}

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(currentFy.getEndingDate());
		calendar.add(Calendar.DATE, 1);
		CFinancialYear nextFy = financialYearService.getFinancialYearByDate(calendar.getTime());

		if (nextFy == null) {
			throw new Exception("Invalid financial year ! for " + calendar.getTime());
		}

		model.addAttribute("currentFy", currentFy);
		model.addAttribute("nextFy", nextFy);

		// model.addAttribute("budgetForm", new BudgetForm());

		List<String> types = Arrays.asList("Opening_Balance", "Closing_Balance", "Revenue_Budget", "Capital_Budget");
		Map<String, List<BudgetItem>> grouped = budgetItemService.getBudgetItemsByTypesFunctionFyBudgetRegister(types, function, budgetRegister);

		// final List<BudgetItem> oBal = grouped.getOrDefault("Opening_Balance",
		// Collections.emptyList());
		// final List<BudgetItem> cBal = grouped.getOrDefault("Closing_Balance",
		// Collections.emptyList());

		// model.addAttribute("opening_balance", oBal.get(0));
		// model.addAttribute("closing_balance", cBal);

		List<BudgetItem> oBal = grouped.getOrDefault("Opening_Balance", Collections.emptyList());
		// BudgetItem first = oBal.isEmpty() ? new BudgetItem() : oBal.get(0);

		// model.addAttribute("opening_balance", oBal);

		List<BudgetItem> revenue = grouped.getOrDefault("Revenue_Budget", Collections.emptyList());
		List<BudgetItem> capital = grouped.getOrDefault("Capital_Budget", Collections.emptyList());

		// merge both lists into one
		List<BudgetItem> allBudget = new ArrayList<>();
		allBudget.addAll(revenue);
		allBudget.addAll(capital);

		// model.addAttribute("opening_balance", oBal);
		// model.addAttribute("closing_balance", cBal);
		model.addAttribute("all_budget_items", allBudget);

		List<Scheme> schemes = schemeService.getBySchemeCode();

		model.addAttribute("schemes", schemes);

		BudgetForm form = new BudgetForm();
		form.setOpening(oBal.get(0)); // <-- FIX
		form.setItems(allBudget); // <-- items also must be set
		form.setFunctionid(function.getId());
		form.setCurrentFinancialYear(currentFy.getId());
		form.setFinancialYear(nextFy.getId());

		model.addAttribute("budgetForm", form);

		model.addAttribute("function", function);
		model.addAttribute("currentFy", currentFy);
		model.addAttribute("nextFy", nextFy);

		System.out.println("Opening in GET = " + form.getOpening().getId());

		return BUDGET_ITEM_EDIT;
	}

	@RequestMapping(value = "/editv2/{functionId}/{budgetRegisterId}", method = { RequestMethod.GET, RequestMethod.POST })
	public String editv2(@PathVariable Long functionId, @PathVariable("budgetRegisterId") Long budgetRegisterId,
					   Model model, RedirectAttributes redirectAttributes) throws Exception {


		BudgetRegister budgetRegister = budgetRegisterWorkflowService.findOne(budgetRegisterId);
		if (budgetRegister == null) {
			return "error/422";
		}
		model.addAttribute("budgetRegister", budgetRegister);
		model.addAttribute("budgetRegisterId", budgetRegister.getId());


		CFunction function = functionService.findOne(functionId);
		if (function == null) {
			return "error/422";
		}
		model.addAttribute("function", function);

		BudgetForm budgetForm = new BudgetForm();
		budgetForm.setFunctionid(function.getId());
		budgetForm.setFinancialYear(budgetRegister.getFinancialYear().getId());
		budgetForm.setCurrentFinancialYear(budgetRegister.getCurrentFinancialYear().getId());


		List<String> types = Arrays.asList("Opening_Balance", "Closing_Balance", "Revenue_Budget", "Capital_Budget");
		Map<String, List<BudgetItem>> grouped = budgetItemService.getBudgetItemsByTypesFunctionAndBudgetRegister(types, function,
				budgetRegister);

		final List<BudgetItem> oBal = grouped.getOrDefault("Opening_Balance", Collections.emptyList());

		BudgetItem openingBalance = oBal.get(0);
		openingBalance.setFunction(function);
		openingBalance.setBudgetRegister(budgetRegister);
		openingBalance.setCurrentFinancialYear(budgetRegister.getCurrentFinancialYear());
		openingBalance.setFinancialYear(budgetRegister.getFinancialYear());

		budgetForm.setOpening(openingBalance);

		// closing balance
		final List<BudgetItem> cBal = grouped.getOrDefault("Closing_Balance", Collections.emptyList());
		BudgetItem closingBalance = cBal.get(0);
		openingBalance.setFunction(function);
		openingBalance.setBudgetRegister(budgetRegister);
		openingBalance.setCurrentFinancialYear(budgetRegister.getCurrentFinancialYear());
		openingBalance.setFinancialYear(budgetRegister.getFinancialYear());

		budgetForm.setClosing(closingBalance);


		List<BudgetItem> normalBudgetItems = new ArrayList<>();
		normalBudgetItems.addAll(grouped.getOrDefault("Revenue_Budget", Collections.emptyList()));
		normalBudgetItems.addAll(grouped.getOrDefault("Capital_Budget", Collections.emptyList()));

		AtomicInteger counter = new AtomicInteger(0);
		normalBudgetItems.forEach(item -> item.setRowIndex(counter.getAndIncrement()));


		budgetForm.setItems(normalBudgetItems);

		budgetItemService.populateForEdit(model, function, budgetRegister.getId(), budgetForm, budgetRegister);


		return "functionwisebudget-edit";
	}

	@PostMapping("/update/{budgetRegisterId}")
	public String update(Model model, @ModelAttribute @Valid BudgetForm budgetForm, final BindingResult resultBinder, @PathVariable("budgetRegisterId") Long budgetRegisterId,
			RedirectAttributes redirectAttrs) {

		// validate budget register
		BudgetRegister budgetRegister = null;
		budgetRegister =  budgetItemService.validateBudgetRegister(budgetRegisterId, resultBinder);

		//validate function
		CFunction function = budgetItemService.validateFunction(budgetForm.getFunctionid(), resultBinder);

		// validate opening balance
		budgetItemService.validateOpeningBudget(budgetForm.getOpening(), resultBinder, function, budgetRegister);


		// validate each entries
		budgetItemService.validateBudgetItems(budgetForm.getItems(), resultBinder, function, budgetRegister);

		if (resultBinder.hasErrors()) {
			budgetItemService.populateValidationErrors(model, function, budgetRegisterId, budgetForm, budgetRegister);
			return BUDGET_ITEM_EDIT;
		}


		try {

			budgetItemService.updateBudgetInputForm(budgetForm, budgetRegister); // inside service: save opening, items,

//			budgetItemService.saveAndUpdateBudgetInputForm(budgetForm, budgetRegister, function);								// closing
			redirectAttrs.addFlashAttribute("message", "Budget items updated successfully!");

		} catch (Exception e) {
			e.printStackTrace();
			return "error/422";
		}

		return "redirect:/budget/view/" + budgetForm.getFunctionid() + "/" + budgetRegisterId;
	}

	@RequestMapping(value = "/functionwise/{budgetRegisterId}", method = { RequestMethod.GET, RequestMethod.POST })
	public String functionView(final Model model, @PathVariable("budgetRegisterId") Long budgetRegisterId,
			RedirectAttributes redirectAttributes) {

		BudgetRegister budgetRegister = budgetRegisterWorkflowService.findOne(budgetRegisterId);

		if (budgetRegister == null) {
			model.addAttribute("error", "Selected Budget register not available or invalid.");
			redirectAttributes.addAttribute("error", "Selected Budget register not available or invalid.");
			return "";
		}

		model.addAttribute("budgetRegisterId", budgetRegisterId);
		model.addAttribute("budgetRegister", budgetRegister);

		// List<CFunction> budgetFunction = budgetItemService.functionListWithBudget();

		List<CFunction> budgetFunction = budgetItemService.functionsHavingBudgetOfBudgetRegister(budgetRegister);

		model.addAttribute("budgetFunction", budgetFunction);

		User currentUser = securityUtils.getCurrentUser();

		List<EmployeeInfo> emplist = microServiceUtil.getEmployee(currentUser.getId(), null, null, null);

		LOGGER.info("emplist: " + emplist.size());

		String[] allowedStatus = new String[] { "reverted", "REVERTED", "NEW", "new" };

		if (Arrays.asList(allowedStatus).contains(budgetRegister.getStatus().getCode().toLowerCase())) {
			if (emplist != null && !emplist.isEmpty()) {
				String designation = emplist.get(0).getAssignments().get(0).getDesignation();
				LOGGER.info("emp-des: " + designation);
				String[] desigs = new String[] { "Financial Management Officer", "FMO", "Accounts Officer", "AO" };
				if (Arrays.asList(desigs).contains(designation)) {
					model.addAttribute("allowCreate", true);
				}
			}
		}

		return BUDGET_FUNCTION;

	}

	@RequestMapping(value = "/complete/{budgetRegisterId}/view", method = { RequestMethod.GET, RequestMethod.POST })
	public String completeBudgetView(final Model model, @PathVariable("budgetRegisterId") Long budgetRegisterId,
			RedirectAttributes redirectAttributes) {

		BudgetRegister budgetRegister = budgetRegisterWorkflowService.findOne(budgetRegisterId);

		if (budgetRegister == null) {
			redirectAttributes.addAttribute("error", "Selected Budget register not available or invalid.");
			return "redirect:/budget/new";
		}

		model.addAttribute("budgetRegisterId", budgetRegisterId);
		model.addAttribute("budgetRegister", budgetRegister);

		addFinancialYears(model);

		List<String> types = Arrays.asList("Opening_Balance", "Closing_Balance", "Revenue_Budget", "Capital_Budget");
		Map<String, List<BudgetItem>> grouped = budgetItemService.getBudgetItemsByTypesAndBudgetRegister(types,
				budgetRegister);

		final List<BudgetItem> oBal = grouped.getOrDefault("Opening_Balance", Collections.emptyList());
		final List<BudgetItem> cBal = grouped.getOrDefault("Closing_Balance", Collections.emptyList());
		final List<BudgetItem> rb = grouped.getOrDefault("Revenue_Budget", Collections.emptyList());
		final List<BudgetItem> cb = grouped.getOrDefault("Capital_Budget", Collections.emptyList());

		BigDecimal openingCurrentEstimate = BigDecimal.ZERO;
		BigDecimal openingActual = BigDecimal.ZERO;
		BigDecimal openingRevised = BigDecimal.ZERO;
		BigDecimal openingNext = BigDecimal.ZERO;

		for (BudgetItem ob : oBal) {
			openingCurrentEstimate = openingCurrentEstimate.add(ob.getCurrentEstimate());
			openingActual = openingActual.add(ob.getCurrentActual());
			openingRevised = openingRevised.add(ob.getCurrentRevisedEstimate());
			openingNext = openingNext.add(ob.getNextEstimate());
		}

		BudgetItem openingBalance = new BudgetItem();
		openingBalance.setCurrentEstimate(openingCurrentEstimate);
		openingBalance.setCurrentActual(openingActual);
		openingBalance.setCurrentRevisedEstimate(openingRevised);
		openingBalance.setNextEstimate(openingNext);

		LOGGER.info("ce: " + openingBalance.getCurrentEstimate() + ", ca: " + openingBalance.getCurrentActual()
				+ ", cr: " + openingBalance.getCurrentRevisedEstimate() + ", ne: " + openingBalance.getNextEstimate());

		// closing
		BigDecimal closingCurrentEstimate = BigDecimal.ZERO;
		BigDecimal closingActual = BigDecimal.ZERO;
		BigDecimal closingRevised = BigDecimal.ZERO;
		BigDecimal closingNext = BigDecimal.ZERO;

		for (BudgetItem cbalance : cBal) {
			closingCurrentEstimate = closingCurrentEstimate.add(cbalance.getCurrentEstimate());
			closingActual = closingActual.add(cbalance.getCurrentActual());
			closingRevised = closingRevised.add(cbalance.getCurrentRevisedEstimate());
			closingNext = closingNext.add(cbalance.getNextEstimate());
		}

		BudgetItem closingBalance = new BudgetItem();
		closingBalance.setCurrentEstimate(closingCurrentEstimate);
		closingBalance.setCurrentActual(closingActual);
		closingBalance.setCurrentRevisedEstimate(closingRevised);
		closingBalance.setNextEstimate(closingNext);

		model.addAttribute("opening_balance", openingBalance);
		model.addAttribute("closing_balance", closingBalance);

		// grouping for revenue budget

		// Map<BudgetAccountType, Map<String, List<BudgetItem>>> groupedRB =
		// rb.stream().collect(Collectors.groupingBy(
		// item -> item.getBudgetHead().getAccountType(),
		// Collectors.groupingBy(
		// itm -> itm.getBudgetHead().getCategory())));

		Map<BudgetAccountType, Map<String, List<BudgetItem>>> groupedRB = rb.stream()
				.sorted(Comparator.comparing(item -> item.getBudgetHead().getOrder())) // this works
				.collect(Collectors.groupingBy(
						item -> item.getBudgetHead().getAccountType(),
						LinkedHashMap::new, // << preserve order of account types
						Collectors.groupingBy(
								itm -> itm.getBudgetHead().getCategory(),
								LinkedHashMap::new, // << preserve order of categories
								Collectors.toList())));

		model.addAttribute("grouped_rb", groupedRB);

		// grouping for capital budget
		// Map<BudgetAccountType, Map<String, List<BudgetItem>>> groupedCB =
		// cb.stream().collect(Collectors.groupingBy(
		// item -> item.getBudgetHead().getAccountType(),
		// Collectors.groupingBy(
		// itm -> itm.getBudgetHead().getCategory())));

		Map<BudgetAccountType, Map<String, List<BudgetItem>>> groupedCB = cb.stream()
				.sorted(Comparator.comparing(item -> item.getBudgetHead().getOrder())) // sort by order
				.collect(Collectors.groupingBy(
						item -> item.getBudgetHead().getAccountType(),
						LinkedHashMap::new, // preserve AccountType order
						Collectors.groupingBy(
								itm -> itm.getBudgetHead().getCategory(),
								LinkedHashMap::new, // preserve Category order
								Collectors.toList())));

		model.addAttribute("grouped_cb", groupedCB);

		Map<String, BudgetTotals> rbTotals = new LinkedHashMap<>();

		for (Map.Entry<BudgetAccountType, Map<String, List<BudgetItem>>> acct : groupedRB.entrySet()) {
			for (Map.Entry<String, List<BudgetItem>> cat : acct.getValue().entrySet()) {
				rbTotals.put(cat.getKey(), computeTotals(cat.getValue()));
			}
		}

		model.addAttribute("rbTotals", rbTotals);

		Map<String, BudgetTotals> cbTotals = new LinkedHashMap<>();

		for (Map.Entry<BudgetAccountType, Map<String, List<BudgetItem>>> acct : groupedCB.entrySet()) {
			for (Map.Entry<String, List<BudgetItem>> cat : acct.getValue().entrySet()) {
				cbTotals.put(cat.getKey(), computeTotals(cat.getValue()));
			}
		}

		model.addAttribute("cbTotals", cbTotals);

		return BUDGET_COMPLETE_VIEW;
	}





}
