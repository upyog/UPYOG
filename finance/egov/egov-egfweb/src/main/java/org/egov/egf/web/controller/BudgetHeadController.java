package org.egov.egf.web.controller;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import javax.validation.Valid;

import org.egov.model.budget.BudgetHead;
import org.egov.model.budget.FunctionBudgetHead;
import org.egov.model.service.BudgetHeadService;
import org.egov.model.service.FunctionBudgetHeadService;
import org.egov.utils.BudgetAccountType;
import org.hibernate.validator.constraints.SafeHtml;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Controller responsible for managing Budget Head master data
 * and function-wise budget head mappings.
 *
 * <p>
 * This controller provides functionality for creating, viewing,
 * searching, and retrieving budget heads used in the budgeting module.
 * It also supports function-specific budget head mapping and
 * autocomplete services for budget entry screens.
 * </p>
 *
 * <h3>Primary Responsibilities</h3>
 * <ul>
 *     <li>Create new budget heads.</li>
 *     <li>Display budget head master data.</li>
 *     <li>Provide budget head search and autocomplete services.</li>
 *     <li>Retrieve budget heads mapped to specific functions.</li>
 *     <li>Display function-wise budget head mappings.</li>
 * </ul>
 *
 * <h3>Supported Endpoints</h3>
 * <ul>
 *     <li><b>GET/POST /budgethead/new</b> - Display budget head creation form.</li>
 *     <li><b>POST /budgethead/create</b> - Create a new budget head.</li>
 *     <li><b>POST /budgethead/view</b> - View all budget heads.</li>
 *     <li><b>GET /budgethead/ajaxBudgetHead</b> - Search budget heads by code or name.</li>
 *     <li><b>GET /budgethead/ajaxBudgetHead/{functionId}</b> -
 *         Search budget heads mapped to a function.</li>
 *     <li><b>POST /budgethead/function-wise-view</b> -
 *         View function-wise budget head mappings.</li>
 * </ul>
 *
 * <h3>Key Features</h3>
 * <ul>
 *     <li>Budget head creation with account type classification.</li>
 *     <li>Support for Revenue and Capital budget account types.</li>
 *     <li>Function-wise budget head assignment and retrieval.</li>
 *     <li>AJAX-based autocomplete and search functionality.</li>
 * </ul>
 *
 * @see BudgetHead
 * @see FunctionBudgetHead
 * @see BudgetHeadService
 * @see FunctionBudgetHeadService
 */

@Controller
@RequestMapping("/budgethead")
public class BudgetHeadController {
	private static final String BUDGETHEAD_NEW = "budgethead-new";
	private static final String BUDGET_HEAD = "budgetHead";
	private static final String BUDGET_HEAD_VIEW = "budgethead-view";
	private static final String BUDGETHEAD_FUNCTION_VIEW = "budgethead-function-view";
	
	@Autowired
	private BudgetHeadService budgetHeadService;
	@Autowired
	private MessageSource messageSource;

	@Autowired
	private FunctionBudgetHeadService functionBudgetHeadService;

	private void prepareNewForm(final Model model) {
		model.addAttribute("budgetAccountTypes", Arrays.asList(BudgetAccountType.values()));
	}

	@RequestMapping(value = "/new", method = { RequestMethod.GET, RequestMethod.POST })
	public String newForm(final Model model) {
		prepareNewForm(model);
		model.addAttribute(BUDGET_HEAD, new BudgetHead());
//		budgetHeadService.getBudgetHeadList(model);
		return BUDGETHEAD_NEW;
	}

	@PostMapping(value = "/create")
	public String create(@Valid @ModelAttribute final BudgetHead budgetHead, final BindingResult errors,
			final RedirectAttributes redirectAttrs, final Model model) {

		budgetHeadService.create(budgetHead);
		redirectAttrs.addFlashAttribute("message",
				messageSource.getMessage("msg.budgetGroup.success", null, Locale.ENGLISH));
		return "redirect:/budgethead/new";
	}

	@PostMapping(value = "/view")
	public String view(final Model model) {
		budgetHeadService.getBudgetHeadList(model);

		return BUDGET_HEAD_VIEW;
	}


	@GetMapping(value = "/ajaxBudgetHead", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public List<BudgetHead> findBudgetHead(@RequestParam @SafeHtml final String query) {
		final List<BudgetHead> budgetHeads = budgetHeadService.findBudgetHeadByNameOrCode(query);
		return budgetHeads;
	}

	@GetMapping(value = "/ajaxBudgetHead/{functionId}", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public List<BudgetHead> findFunctionBudgetHeads(@PathVariable("functionId") Long functionId, @RequestParam @SafeHtml final String query) {
		final List<BudgetHead> budgetHeads = budgetHeadService.searchBudgetHeadsByFunctionNative(functionId, query);
		return budgetHeads;
	}

	@PostMapping(value = "/function-wise-view")
	public String functionwiseview(final Model model) {
		final List<FunctionBudgetHead> functionBudgetHead = functionBudgetHeadService.findAll();
		model.addAttribute("functionBudgetHead", functionBudgetHead);
		return BUDGETHEAD_FUNCTION_VIEW;
	}

}
