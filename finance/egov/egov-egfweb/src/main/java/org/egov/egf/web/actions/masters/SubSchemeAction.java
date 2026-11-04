/*
 *    eGov  SmartCity eGovernance suite aims to improve the internal efficiency,transparency,
 *    accountability and the service delivery of the government  organizations.
 *
 *     Copyright (C) 2017  eGovernments Foundation
 *
 *     The updated version of eGov suite of products as by eGovernments Foundation
 *     is available at http://www.egovernments.org
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program. If not, see http://www.gnu.org/licenses/ or
 *     http://www.gnu.org/licenses/gpl.html .
 *
 *     In addition to the terms of the GPL license to be adhered to in using this
 *     program, the following additional terms are to be complied with:
 *
 *         1) All versions of this program, verbatim or modified must carry this
 *            Legal Notice.
 *            Further, all user interfaces, including but not limited to citizen facing interfaces,
 *            Urban Local Bodies interfaces, dashboards, mobile applications, of the program and any
 *            derived works should carry eGovernments Foundation logo on the top right corner.
 *
 *            For the logo, please refer http://egovernments.org/html/logo/egov_logo.png.
 *            For any further queries on attribution, including queries on brand guidelines,
 *            please contact contact@egovernments.org
 *
 *         2) Any misrepresentation of the origin of the material is prohibited. It
 *            is required that all modified versions of this material be marked in
 *            reasonable ways as different from the original version.
 *
 *         3) This license does not grant any rights to any user of the program
 *            with regards to rights under trademark law for use of the trade names
 *            or trademarks of eGovernments Foundation.
 *
 *   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 *
 */
package org.egov.egf.web.actions.masters;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import jakarta.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.apache.struts2.interceptor.validation.SkipValidation;
import org.egov.commons.Scheme;
import org.egov.commons.SubScheme;
import org.egov.infra.config.core.ApplicationThreadLocals;
import org.egov.infra.validation.exception.ValidationError;
import org.egov.infra.validation.exception.ValidationException;
import org.egov.infra.web.struts.actions.BaseFormAction;
import org.egov.infra.web.struts.annotation.ValidationErrorPage;
import org.egov.infstr.utils.EgovMasterDataCaching;
import org.egov.services.masters.SubSchemeService;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;

import org.apache.struts2.validator.annotations.RequiredFieldValidator;
import org.apache.struts2.validator.annotations.Validations;
import org.springframework.beans.factory.annotation.Qualifier;

@SuppressWarnings("deprecation")
@ParentPackage("egov")

/**
 * Java 17 / Struts 7 / Spring 6 LTS Migration Notice:
 * Added explicit @Result mapping for result "edit" -> "subScheme-new.jsp".
 * In legacy Struts 2.3, the Convention Plugin automatically guessed method-name results like "edit" or "input".
 * Struts 7 enforces strict result declaration; unmapped results trigger Struts Dispatcher errors
 * ("No result defined for action SubSchemeAction and result edit") which fall through to Spring MVC as HTTP 405 (Method Not Supported).
 */
@Results({ @Result(name = BaseFormAction.NEW, location = "subScheme-new.jsp"),
		@Result(name = "edit", location = "subScheme-new.jsp"),
		@Result(name = SubSchemeAction.SEARCH, location = "subScheme-search.jsp"),
		@Result(name = SchemeAction.UNIQUECHECKFIELD, location = "subScheme-fieldUniqueCheck.jsp"),
		@Result(name = SubSchemeAction.VIEW, location = "subScheme-view.jsp") })
public class SubSchemeAction extends BaseFormAction {
	private static final String SUB_SCHEME_LIST = "subSchemeList";
	/*
	 * LTS Migration Fix (Hibernate 6 Upgrade):
	 * Changed field name from camelCase 'isActive=true' to lowercase 'isactive=true' in FUND_QUERY HQL.
	 * The Fund entity maps database column 'isactive' to Java property 'isactive'.
	 */
	private static final String FUND_QUERY = "from Fund where isactive=true order by name";
	private static final String FUND_LIST = "fundList";
	private static final String DUPLICATE_SUBSCHEME = "duplicate.subscheme";
	private static final String AN_ERROR_OCCURED_CONTACT_ADMINISTRATOR = "An error occured contact Administrator";
	private static final String DEPARTMENT_LIST = "departmentList";
	private static final String SCHEME_LIST = "schemeList";
	private static final long serialVersionUID = -3712472100095261379L;
	private SubScheme subScheme = new SubScheme();
	private boolean isactive = false;
	private boolean clearValues = false;
	private long fundId;
	private static final String REQUIRED = "required";
	private Integer schemeId;
	private Integer subSchemeId;
	private List<SubScheme> subSchemeList;
	public static final String SEARCH = "search";
	public static final String VIEW = "view";
	private String showMode;
	@Autowired
	@Qualifier("subSchemeService")
	private transient SubSchemeService subSchemeService;
	public static final String UNIQUECHECKFIELD = "fieldUniqueCheck";
	private boolean uniqueCode = false;
	private String code;
	private String name;
	@Autowired
	private transient EgovMasterDataCaching egovMasterDataCaching;

	/**
	 * Java 17 / Hibernate 6 LTS Migration Fix:
	 * Added 'left join fetch s.scheme sch left join fetch sch.fund f' to eagerly load associated Scheme and Fund entities.
	 * Prevents uninitialized Hibernate 6 lazy proxy issues when rendering %{subScheme.scheme.name} on view and edit forms.
	 */
	@Override
	public Object getModel() {
		resolveIdsFromRequest();
		if (subSchemeId != null && subSchemeId != -1
				&& (subScheme.getId() == null || !subSchemeId.equals(subScheme.getId())))
			subScheme = (SubScheme) persistenceService.find(
					"from SubScheme s left join fetch s.scheme sch left join fetch sch.fund f where s.id=?",
					subSchemeId);
		if (schemeId != null && schemeId != -1)
			subScheme.setScheme((Scheme) persistenceService.find("from Scheme s left join fetch s.fund f where s.id=?",
					schemeId));
		return subScheme;
	}

	@Override
	public void prepare() {
		super.prepare();
		setupDropdownDataExcluding();
		dropdownData.put(SCHEME_LIST, persistenceService.findAllBy("from Scheme where isactive=true order by name"));
		dropdownData.put(DEPARTMENT_LIST, egovMasterDataCaching.get("egi-department"));
	}

	/**
	 * Java 17 / Struts 7 LTS Migration Fix:
	 * Sets default isactive = true when initializing a new SubScheme form.
	 * Ensures the Active checkbox is checked by default on the Create SubScheme UI page.
	 */
	@SkipValidation
	@Action(value = "/masters/subScheme-newForm")
	public String newForm() {
		showMode = "new";
		isactive = true;
		if (subScheme != null)
			subScheme.setIsactive(Boolean.TRUE);
		return NEW;
	}

	/**
	 * Java 17 / Spring 6 / JPA 3 LTS Migration Fix:
	 * 1. Synchronizes active checkbox state from form submission to entity.
	 * 2. Removed subSchemeService.getSession().flush() call to comply with JPA 3 / Spring 6 transaction rules
	 *    (prevents jakarta.persistence.TransactionRequiredException).
	 */
	@Validations(requiredFields = { @RequiredFieldValidator(fieldName = "schemeId", message = "", key = REQUIRED),
			@RequiredFieldValidator(fieldName = "code", message = "", key = REQUIRED),
			@RequiredFieldValidator(fieldName = "name", message = "", key = REQUIRED),
			@RequiredFieldValidator(fieldName = "validfrom", message = "", key = REQUIRED),
			@RequiredFieldValidator(fieldName = "validto", message = "", key = REQUIRED) })
	@ValidationErrorPage(value = NEW)
	@Action(value = "/masters/subScheme-create")
	public String save() {
		applySubmittedFormFields();
		applySubmittedIsActive();
		subScheme.setCreatedDate(new Date());
		subScheme.setCreatedBy(ApplicationThreadLocals.getUserId());
		subScheme.setLastmodifieddate(new Date());

		try {
			subSchemeService.persist(subScheme);
			// JPA 3 / Spring 6 LTS Migration Fix: Removed subSchemeService.getSession().flush() call outside @Transactional
			// Prevents jakarta.persistence.TransactionRequiredException: No EntityManager with actual transaction available
		} catch (final ValidationException e) {
			throw new ValidationException(Arrays.asList(new ValidationError(AN_ERROR_OCCURED_CONTACT_ADMINISTRATOR,
					AN_ERROR_OCCURED_CONTACT_ADMINISTRATOR)));
		} catch (final ConstraintViolationException e) {
			addActionError(getText(DUPLICATE_SUBSCHEME));
			return NEW;
		}
		clearValues = true;
		addActionMessage(getText("subscheme.saved.successfully"));
		showMode = "new";
		EgovMasterDataCaching.removeFromCache(" egi-subscheme");
		return VIEW;
	}

	/**
	 * Java 17 / Spring 6 LTS Migration Fix:
	 * Added @Validations and @ValidationErrorPage(value = NEW) annotations to subScheme-edit action.
	 * Also added result 'edit' mapping to @Results to prevent Struts Dispatcher error
	 * ("No result defined for action SubSchemeAction and result edit") and HTTP 405 Request Method Not Supported error.
	 */
	@Validations(requiredFields = { @RequiredFieldValidator(fieldName = "schemeId", message = "", key = REQUIRED),
			@RequiredFieldValidator(fieldName = "code", message = "", key = REQUIRED),
			@RequiredFieldValidator(fieldName = "name", message = "", key = REQUIRED),
			@RequiredFieldValidator(fieldName = "validfrom", message = "", key = REQUIRED),
			@RequiredFieldValidator(fieldName = "validto", message = "", key = REQUIRED) })
	@ValidationErrorPage(value = NEW)
	@Action(value = "/masters/subScheme-edit")
	public String editSubScheme() {
		applySubmittedFormFields();
		applySubmittedIsActive();
		subScheme.setLastModifiedBy(ApplicationThreadLocals.getUserId());
		subScheme.setLastmodifieddate(new Date());
		try {
			subSchemeService.persist(subScheme);
			// JPA 3 / Spring 6 LTS Migration Fix: Removed subSchemeService.getSession().flush() call outside @Transactional
			// Prevents jakarta.persistence.TransactionRequiredException: No EntityManager with actual transaction available
		} catch (final ValidationException e) {
			throw new ValidationException(Arrays.asList(new ValidationError(AN_ERROR_OCCURED_CONTACT_ADMINISTRATOR,
					AN_ERROR_OCCURED_CONTACT_ADMINISTRATOR)));
		} catch (final ConstraintViolationException e) {
			throw new ValidationException(Arrays.asList(new ValidationError(DUPLICATE_SUBSCHEME, DUPLICATE_SUBSCHEME)));
		}
		clearValues = true;
		addActionMessage(getText("subscheme.modified.successfully"));
		showMode = "";
		EgovMasterDataCaching.removeFromCache("egi-subscheme");
		return VIEW;
	}

	@SkipValidation
	@Action(value = "/masters/subScheme-beforeEdit")
	public String beforeEdit() {
		/*
		 * LTS Migration Fix (Java 17 Primitive Unboxing Fix):
		 * Used Boolean.TRUE.equals(...) to evaluate subScheme.getIsactive().
		 * In Java 17, evaluating if (subScheme.getIsactive()) when the wrapper Boolean is null throws 
		 * java.lang.NullPointerException (Cannot invoke "java.lang.Boolean.booleanValue()").
		 */
		if (subScheme != null && Boolean.TRUE.equals(subScheme.getIsactive()))
			isactive = true;
		return NEW;
	}

	@SkipValidation
	@Action(value = "/masters/subScheme-beforeSearch")
	public String beforeSearch() {
		dropdownData.put(FUND_LIST, persistenceService.findAllBy(FUND_QUERY));
		dropdownData.put(SCHEME_LIST, Collections.emptyList());
		dropdownData.put(SUB_SCHEME_LIST, Collections.emptyList());
		fundId = 0;
		return SEARCH;
	}

	@SkipValidation
	@Action(value = "/masters/subScheme-beforeSearch-edit")
	public String beforeSearchEdit() {
		dropdownData.put(FUND_LIST, persistenceService.findAllBy(FUND_QUERY));
		dropdownData.put(SCHEME_LIST, Collections.emptyList());
		dropdownData.put(SUB_SCHEME_LIST, Collections.emptyList());
		showMode = "edit";
		fundId = 0;
		return SEARCH;
	}

	/**
	 * Java 17 / Hibernate 6 LTS Migration Fix:
	 * Added 'left join fetch s.scheme sch left join fetch sch.fund f' to eagerly load Scheme and Fund entity references.
	 * Prevents uninitialized lazy proxy evaluation issues in Hibernate 6 when rendering 'scheme.fund.name'
	 * in search result tables.
	 */
	@SuppressWarnings("unchecked")
	@SkipValidation
	@Action(value = "/masters/subScheme-search")
	public String searchSubScheme() {
		final StringBuilder query = new StringBuilder(500);
		final List<Object> params = new ArrayList<>();
		query.append("From SubScheme s left join fetch s.scheme sch left join fetch sch.fund f ");
		if (fundId != 0) {
			query.append("where s.scheme.fund.id=?");
			params.add(fundId);
			if (schemeId != -1) {
				query.append(" and  s.scheme.id=?");
				params.add(schemeId);
				if (subSchemeId != null && subSchemeId != -1) {
					query.append(" and s.id=?");
					params.add(subScheme.getId());
				}
			}
		}
		loadDropDowns();
		subSchemeList = persistenceService.findAllBy(query.toString(), params.toArray());
		return SEARCH;
	}

	@SkipValidation
	@Action(value = "/masters/subScheme-viewSubScheme")
	public String viewSubScheme() {
		showMode = "view";
		return VIEW;
	}

	private void loadDropDowns() {
		dropdownData.put(FUND_LIST, persistenceService.findAllBy(FUND_QUERY));
		final StringBuilder st = new StringBuilder();

		if (fundId != 0) {
			st.append("from Scheme where isactive=true and fund.id=?");
			dropdownData.put(SCHEME_LIST, persistenceService.findAllBy(st.toString(), fundId));
			st.delete(0, st.length() - 1);
		} else
			dropdownData.put(SCHEME_LIST, Collections.emptyList());
		if (schemeId != -1)
			dropdownData.put(SUB_SCHEME_LIST,
					persistenceService.findAllBy("from SubScheme where isactive=true and scheme.id=?", schemeId));
		else
			dropdownData.put(SUB_SCHEME_LIST, Collections.emptyList());
	}

	@SkipValidation
	public boolean getCheckField() {
		SubScheme subSchemeValidate = null;
		boolean isDuplicate = false;

		if (uniqueCode) {
			if (!subScheme.getCode().equals("") && subScheme.getId() != null)
				subSchemeValidate = (SubScheme) persistenceService.find("from SubScheme where code=? and id!=?",
						subScheme.getCode().toLowerCase(), subScheme.getId());
			else if (!subScheme.getCode().equals(""))
				subSchemeValidate = (SubScheme) persistenceService.find("from SubScheme where code=?",
						subScheme.getCode().toLowerCase());
			uniqueCode = false;
		} else if (!subScheme.getName().equals("") && subScheme.getId() != null)
			subSchemeValidate = (SubScheme) persistenceService.find("from SubScheme where name=? and id!=?",
					subScheme.getName().toLowerCase(), subScheme.getId());
		else if (!subScheme.getName().equals(""))
			subSchemeValidate = (SubScheme) persistenceService.find("from SubScheme where name=?",
					subScheme.getName().toLowerCase());
		if (subSchemeValidate != null)
			isDuplicate = true;

		return isDuplicate;
	}

	@SkipValidation
	@Action(value = "/masters/subScheme-codeUniqueCheck")
	public String codeUniqueCheck() {
		uniqueCode = true;
		return UNIQUECHECKFIELD;
	}

	@SkipValidation
	@Action(value = "/masters/subScheme-nameUniqueCheck")
	public String nameUniqueCheck() {
		return UNIQUECHECKFIELD;
	}

	public void setFundId(final long fundId) {
		this.fundId = fundId;
	}

	public long getFundId() {
		return fundId;
	}

	public void setSchemeId(final Integer schemeId) {
		this.schemeId = schemeId;
	}

	public Integer getSchemeId() {
		return schemeId;
	}

	public void setSubSchemeList(final List<SubScheme> subSchemeList) {
		this.subSchemeList = subSchemeList;
	}

	public List<SubScheme> getSubSchemeList() {
		return subSchemeList;
	}

	public void setShowMode(final String showMode) {
		this.showMode = showMode;
	}

	public String getShowMode() {
		return showMode;
	}

	public SubSchemeService getSubSchemeService() {
		return subSchemeService;
	}

	public void setSubSchemeService(final SubSchemeService subSchemeService) {
		this.subSchemeService = subSchemeService;
	}

	public void setSubScheme(final SubScheme subScheme) {
		this.subScheme = subScheme;
	}

	public SubScheme getSubScheme() {
		return subScheme;
	}

	/**
	 * Java 17 / Struts 7 LTS Migration Fix (Form Parameter & Checkbox Binding):
	 *
	 * PROBLEM:
	 * When creating or modifying a SubScheme via subScheme-create.action / subScheme-edit.action, checking the
	 * 'Active' checkbox on subScheme-new.jsp resulted in 'isactive' being saved as false (No) in the database.
	 *
	 * ROOT CAUSE:
	 * 1. The action previously only defined 'public boolean isIsactive()'.
	 * 2. In Struts 7 / OGNL 3.3+ (Spring 6 LTS stack), JavaBeans Specification v1.01 introspection is strictly enforced.
	 *    For a primitive boolean field 'isactive', the specification expects getter 'getIsactive()' or 'isactive()'.
	 * 3. Double-prefix method 'isIsactive()' (is + Isactive) was mapped by Struts Introspector to property name 'Isactive' (capital I).
	 * 4. When the HTTP POST request submitted parameter 'isactive=true' (lowercase i), Struts ParameterInterceptor searched for
	 *    property 'isactive'. Due to missing getIsactive(), PropertyDescriptor lookup failed, causing Struts to silently
	 *    skip calling setIsactive(boolean). Thus, 'isactive' remained false (default primitive value).
	 *
	 * SOLUTION:
	 * Added standard getter 'getIsactive()' matching 'setIsactive(boolean)'.
	 * Struts 7 now successfully binds 'isactive' from form submit, setting subScheme.isactive = true in DB.
	 *
	 * @return boolean true if active, false otherwise
	 */
	public boolean getIsactive() {
		return isactive;
	}

	public boolean isIsactive() {
		return isactive;
	}

	public void setIsactive(boolean isactive) {
		this.isactive = isactive;
	}

	/*
	 * LTS Migration Fix (Struts 7) — Modify Sub Scheme department
	 *
	 * What was the issue?
	 *   Modify Sub Scheme showed "Sub scheme Modified successfully", but Department
	 *   (and other model-only fields) stayed unchanged when the record was viewed
	 *   again. The dropdown posts name="department" (department code). Persist still
	 *   wrote the entity loaded from DB, whose department was null / old.
	 *
	 * Why do we need this change?
	 *   Struts 7 does not reliably bind ModelDriven form params onto SubScheme.
	 *   getModel() also reloads by subSchemeId, which wipes any values that did
	 *   bind. The action has name/code fields, so those can land on the action
	 *   instead of the entity. Department has no action field, so it was dropped.
	 *
	 * How we solved this?
	 *   Read schemeId, subSchemeId, department, name, code, dates and the other
	 *   form fields from the HTTP request and copy them onto the loaded SubScheme
	 *   before persist. Empty department ("0" / blank) is stored as null.
	 *
	 * What did we solve?
	 *   Changing Department (for example to Street Lights) now persists and shows
	 *   on view / next edit. Create and modify both keep the submitted values.
	 */
	private void applySubmittedFormFields() {
		resolveIdsFromRequest();
		if (subSchemeId != null && subSchemeId != -1
				&& (subScheme.getId() == null || !subSchemeId.equals(subScheme.getId()))) {
			final SubScheme loaded = (SubScheme) persistenceService.find(
					"from SubScheme s left join fetch s.scheme sch left join fetch sch.fund f where s.id=?",
					subSchemeId);
			if (loaded != null)
				subScheme = loaded;
		}
		final HttpServletRequest request = ServletActionContext.getRequest();
		if (request == null)
			return;

		final String submittedName = firstNonBlank(request.getParameter("name"), name);
		if (submittedName != null)
			subScheme.setName(submittedName);
		final String submittedCode = firstNonBlank(request.getParameter("code"), code);
		if (submittedCode != null)
			subScheme.setCode(submittedCode);

		final String departmentParam = request.getParameter("department");
		if (departmentParam != null) {
			final String trimmed = departmentParam.trim();
			if (trimmed.isEmpty() || "0".equals(trimmed) || "-1".equals(trimmed))
				subScheme.setDepartment(null);
			else
				subScheme.setDepartment(trimmed);
		}

		final String estimate = request.getParameter("initialEstimateAmount");
		if (estimate != null) {
			final String trimmed = estimate.trim();
			if (trimmed.isEmpty())
				subScheme.setInitialEstimateAmount(null);
			else {
				try {
					subScheme.setInitialEstimateAmount(new BigDecimal(trimmed));
				} catch (final NumberFormatException e) {
					// leave existing amount; client-side validate() already rejects non-numeric input
				}
			}
		}

		applySubmittedString(request, "councilLoanProposalNumber");
		applySubmittedString(request, "councilAdminSanctionNumber");
		applySubmittedString(request, "govtLoanProposalNumber");
		applySubmittedString(request, "govtAdminSanctionNumber");

		final Date validFrom = parseSubmittedDate(request.getParameter("validfrom"));
		if (validFrom != null)
			subScheme.setValidfrom(validFrom);
		final Date validTo = parseSubmittedDate(request.getParameter("validto"));
		if (validTo != null)
			subScheme.setValidto(validTo);
		subScheme.setCouncilLoanProposalDate(
				parseSubmittedDateAllowClear(request.getParameter("councilLoanProposalDate"),
						subScheme.getCouncilLoanProposalDate()));
		subScheme.setCouncilAdminSanctionDate(
				parseSubmittedDateAllowClear(request.getParameter("councilAdminSanctionDate"),
						subScheme.getCouncilAdminSanctionDate()));
		subScheme.setGovtLoanProposalDate(
				parseSubmittedDateAllowClear(request.getParameter("govtLoanProposalDate"),
						subScheme.getGovtLoanProposalDate()));
		subScheme.setGovtAdminSanctionDate(
				parseSubmittedDateAllowClear(request.getParameter("govtAdminSanctionDate"),
						subScheme.getGovtAdminSanctionDate()));

		if (schemeId != null && schemeId != -1)
			subScheme.setScheme((Scheme) persistenceService.find(
					"from Scheme s left join fetch s.fund f where s.id=?", schemeId));
	}

	private void resolveIdsFromRequest() {
		final HttpServletRequest request = ServletActionContext.getRequest();
		if (request == null)
			return;
		if (subSchemeId == null || subSchemeId == -1) {
			final Integer parsed = parseInteger(request.getParameter("subSchemeId"));
			if (parsed != null)
				subSchemeId = parsed;
		}
		if (schemeId == null || schemeId == -1) {
			final Integer parsed = parseInteger(request.getParameter("schemeId"));
			if (parsed != null)
				schemeId = parsed;
		}
	}

	private void applySubmittedString(final HttpServletRequest request, final String paramName) {
		final String value = request.getParameter(paramName);
		if (value == null)
			return;
		final String trimmed = value.trim();
		final String stored = trimmed.isEmpty() ? null : trimmed;
		switch (paramName) {
		case "councilLoanProposalNumber":
			subScheme.setCouncilLoanProposalNumber(stored);
			break;
		case "councilAdminSanctionNumber":
			subScheme.setCouncilAdminSanctionNumber(stored);
			break;
		case "govtLoanProposalNumber":
			subScheme.setGovtLoanProposalNumber(stored);
			break;
		case "govtAdminSanctionNumber":
			subScheme.setGovtAdminSanctionNumber(stored);
			break;
		default:
			break;
		}
	}

	private static String firstNonBlank(final String primary, final String fallback) {
		if (primary != null && !primary.trim().isEmpty())
			return primary.trim();
		if (fallback != null && !fallback.trim().isEmpty())
			return fallback.trim();
		return null;
	}

	private static Integer parseInteger(final String value) {
		if (value == null)
			return null;
		final String trimmed = value.trim();
		if (trimmed.isEmpty() || "0".equals(trimmed) || "-1".equals(trimmed))
			return null;
		try {
			return Integer.valueOf(trimmed);
		} catch (final NumberFormatException e) {
			return null;
		}
	}

	private static Date parseSubmittedDate(final String value) {
		if (value == null || value.trim().isEmpty())
			return null;
		try {
			final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			sdf.setLenient(false);
			return sdf.parse(value.trim());
		} catch (final ParseException e) {
			return null;
		}
	}

	private static Date parseSubmittedDateAllowClear(final String value, final Date existing) {
		if (value == null)
			return existing;
		if (value.trim().isEmpty())
			return null;
		final Date parsed = parseSubmittedDate(value);
		return parsed != null ? parsed : existing;
	}

	/**
	 * Struts 7 ModelDriven binds {@code isactive} onto {@code SubScheme} (the
	 * model), not this action field. {@code save()} used to copy the action
	 * default {@code false} over the entity and persist inactive. Read the
	 * submitted checkbox from the request first.
	 */
	private void applySubmittedIsActive() {
		isactive = resolveSubmittedIsActive();
		subScheme.setIsactive(isactive);
	}

	private boolean resolveSubmittedIsActive() {
		final String[] values = ServletActionContext.getRequest().getParameterValues("isactive");
		if (values != null) {
			for (final String value : values) {
				if (value == null)
					continue;
				final String trimmed = value.trim();
				if ("true".equalsIgnoreCase(trimmed) || "on".equalsIgnoreCase(trimmed)
						|| "yes".equalsIgnoreCase(trimmed) || "1".equals(trimmed))
					return true;
			}
			for (final String value : values) {
				if (value != null && "false".equalsIgnoreCase(value.trim()))
					return false;
			}
		}
		if (isactive)
			return true;
		return subScheme != null && Boolean.TRUE.equals(subScheme.getIsactive());
	}

	public void setClearValues(final boolean clearValues) {
		this.clearValues = clearValues;
	}

	public boolean isClearValues() {
		return clearValues;
	}

	public Integer getSubSchemeId() {
		return subSchemeId;
	}

	public void setSubSchemeId(final Integer subSchemeId) {
		this.subSchemeId = subSchemeId;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
