/**
 * Created on Jun 19, 2025.
 * 
 * @author bdhal
 */
package org.egov.finance.report.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;
import org.egov.finance.report.entity.AppConfigValues;
import org.egov.finance.report.entity.BudgetControlType;
import org.egov.finance.report.entity.BudgetDetail;
import org.egov.finance.report.entity.BudgetGroup;
import org.egov.finance.report.entity.CChartOfAccounts;
import org.egov.finance.report.entity.CGeneralLedger;
import org.egov.finance.report.entity.CVoucherHeader;
import org.egov.finance.report.entity.Department;
import org.egov.finance.report.entity.EgBilldetails;
import org.egov.finance.report.entity.EgBillregister;
import org.egov.finance.report.entity.EgBillregistermis;
import org.egov.finance.report.entity.FinancialYear;
import org.egov.finance.report.entity.Fund;
import org.egov.finance.report.entity.Vouchermis;
import org.egov.finance.report.exception.ApplicationRuntimeException;
import org.egov.finance.report.exception.ReportServiceException;
import org.egov.finance.report.model.BudgetReportEntry;
import org.egov.finance.report.repository.BudgetGroupRepository;
import org.egov.finance.report.repository.BudgetRepository;
import org.egov.finance.report.util.BudgetReportQueryHelper;
import org.egov.finance.report.util.CommonUtils;
import org.egov.finance.report.util.ReportConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

@Service
public class BudgetService {

	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	CommonUtils commonUtils;

	@Autowired
	BudgetReportQueryHelper budgetReportQueryHelper;

	@Autowired
	MasterCommonService masterCommonService;

	@Autowired
	BudgetRepository budgetRepository;

	@Autowired
	BudgetGroupRepository budgetGroupRepository;

	@Autowired
	LedgerService ledgerService;

	public List<Object> getBudgetDetailsForBill(final EgBillregister bill) {
		final List<Object> list = new ArrayList<Object>();
		if (bill != null && bill.getEgBillregistermis() != null
				&& bill.getEgBillregistermis().getBudgetaryAppnumber() != null
				&& !"".equalsIgnoreCase(bill.getEgBillregistermis().getBudgetaryAppnumber())) {
			final FinancialYear financialYear = masterCommonService.getFinancialYearByDate(bill.getBilldate());
			for (final EgBilldetails detail : bill.getEgBilldetailes()) {
				final CChartOfAccounts coa = masterCommonService.getCChartOfAccountsById(detail.getGlcodeid());
				if (isBudgetCheckNeeded(coa)) {
					final BudgetReportEntry budgetReportEntry = new BudgetReportEntry();
				
					String department = Optional.ofNullable(bill.getEgBillregistermis())
					        .map(EgBillregistermis::getDepartmentcode)
					        .map(masterCommonService::getDepartmenByCode)
					        .map(Department::getName)
					        .orElse("");
					budgetReportEntry.setDepartmentName(department);
					Optional.ofNullable(bill.getEgBillregistermis()).map(EgBillregistermis::getFund).map(Fund::getName)
							.ifPresent(budgetReportEntry::setFundName);

					populateDataForBill(bill, financialYear, detail, budgetReportEntry, coa);
					list.add(budgetReportEntry);
				}
			}
		}
		return list;
	}

	public BigDecimal getBudgetedAmtForYear(Map<String, Object> paramMap) {
		Map<String, Object> params = new HashMap<>();
		String deptCode = commonUtils.toStr(paramMap.get(ReportConstants.DEPTID));
		Long functionid = commonUtils.toLong(paramMap.get(ReportConstants.FUNCTIONID));
		Integer functionaryid = commonUtils.toInt(paramMap.get(ReportConstants.FUNCTIONARYID));
		Integer schemeid = commonUtils.toInt(paramMap.get(ReportConstants.SCHEMEID));
		Integer subschemeid = commonUtils.toInt(paramMap.get(ReportConstants.SUBSCHEMEID));
		Long boundaryid = commonUtils.toLong(paramMap.get(ReportConstants.BOUNDARYID));
		Integer fundid = commonUtils.toInt(paramMap.get(ReportConstants.FUNDID));
		Long financialyearid = commonUtils.toLong(paramMap.get("financialyearid"));
		List<BudgetGroup> budgetHeadList = (List<BudgetGroup>) paramMap.get("BUDGETHEADID");

		if (financialyearid == null)
			throw new ReportServiceException(Map.of("financialyearid", "Financial Year id is required"));

		if (budgetHeadList == null || budgetHeadList.isEmpty())
			throw new ReportServiceException(Map.of("budgetHeadList", "Budget Head list cannot be empty"));

		String queryStr = budgetReportQueryHelper.prepareQueryForBudget(deptCode, functionid, functionaryid, schemeid,
				subschemeid, boundaryid != null ? boundaryid.intValue() : null, fundid, params);

		queryStr += " and bd.budget.financialYear.id = :financialyearid";
		params.put("financialyearid", financialyearid);

		queryStr += " and bd.budgetGroup in :budgetHeadList";
		params.put("budgetHeadList", budgetHeadList);

		String finalQuery = "SELECT bd FROM BudgetDetail bd WHERE bd.budget.isbere = :isbere" + queryStr;
		params.put("isbere",
				budgetRepository.findApprovedReBudgetsByFinancialYear(financialyearid).isEmpty() ? "BE" : "RE");

		TypedQuery<BudgetDetail> typedQuery = entityManager.createQuery(finalQuery, BudgetDetail.class);
		params.forEach(typedQuery::setParameter);

		List<BudgetDetail> result = typedQuery.getResultList();

		if (result == null || result.isEmpty()) {
			throw new ReportServiceException(Map.of("budget", "No matching budget found for given parameters"));
		}

		return getApprovedAmt(result);
	}

	public void populateBudgetAppNumber(final EgBillregister bill, final BudgetReportEntry budgetReportEntry) {
		String budgetAppNumber = null;

		if (bill.getEgBillregistermis() != null) {
			budgetAppNumber = bill.getEgBillregistermis().getBudgetaryAppnumber();

			if (StringUtils.isBlank(budgetAppNumber) && bill.getEgBillregistermis().getVoucherHeader() != null
					&& bill.getEgBillregistermis().getVoucherHeader().getVouchermis() != null) {
				budgetAppNumber = bill.getEgBillregistermis().getVoucherHeader().getVouchermis()
						.getBudgetaryAppnumber();
			}
		}

		if (StringUtils.isNotBlank(budgetAppNumber)) {
			budgetReportEntry.setBudgetApprNumber(budgetAppNumber);
		}
	}

	public BigDecimal getBillAmountForBudgetCheck(final Map<String, Object> paramMap) {
		String deptCode = (String) paramMap.get(ReportConstants.DEPTID);
		Long functionid = (Long) paramMap.get(ReportConstants.FUNCTIONID);
		Integer functionaryid = (Integer) paramMap.get(ReportConstants.FUNCTIONARYID);
		Integer schemeid = (Integer) paramMap.get(ReportConstants.SCHEMEID);
		Integer subschemeid = (Integer) paramMap.get(ReportConstants.SUBSCHEMEID);
		Long boundaryid = (Long) paramMap.get(ReportConstants.BOUNDARYID);
		Integer fundid = (Integer) paramMap.get(ReportConstants.FUNDID);
		Long glcodeid = (Long) paramMap.get(ReportConstants.GLCODEID);
		Date asondate = (Date) paramMap.get(ReportConstants.ASONDATE);
		Date fromdate = (Date) paramMap.get("fromdate");

		if (asondate == null)
			throw new ReportServiceException(Map.of("CHECKDATE", "As On Date is null"));

		final FinancialYear finyear = masterCommonService.getFinancialYearByDate(asondate);
		if (finyear == null)
			throw new ReportServiceException(Map.of("CHECKDATE", "Financial year is not defined for this date"));

		fromdate = finyear.getStartingDate();
		paramMap.put("financialyearid", finyear.getId());
		paramMap.put("fromdate", fromdate);
		paramMap.put(ReportConstants.ASONDATE, finyear.getEndingDate());

		final List<AppConfigValues> budgetGrouplist = masterCommonService
				.getConfigValuesByModuleAndKey(ReportConstants.EGF, ReportConstants.BUDGETARY_CHECK_GROUPBY_VALUES);
		if (budgetGrouplist.isEmpty())
			throw new ReportServiceException(
					Map.of("CHECKDATE", "budgetaryCheck_groupby_values is not defined in AppConfig"));

		final AppConfigValues appConfigValues = budgetGrouplist.get(0);
		final String[] values = StringUtils.split(appConfigValues.getValue(), ",");

		final StringBuilder jpqlCondition = new StringBuilder();
		final Map<String, Object> queryParams = new HashMap<>();

		for (final String value : values) {
			switch (value.trim()) {
			case "department" -> budgetReportQueryHelper.validateAndAppend(jpqlCondition, "Department", deptCode,
					"bmis.departmentcode", "deptCode", queryParams, Function.identity());
			case "function" -> budgetReportQueryHelper.validateAndAppend(jpqlCondition, "Function", functionid,
					"bd.functionid", "functionid", queryParams, Function.identity());
			case "functionary" -> budgetReportQueryHelper.validateAndAppend(jpqlCondition, "Functionary", functionaryid,
					"bmis.functionaryid.id", "functionaryid", queryParams, Function.identity());
			case "fund" -> budgetReportQueryHelper.validateAndAppend(jpqlCondition, "Fund", fundid, "bmis.fund.id",
					"fundid", queryParams, Function.identity());
			case "scheme" -> budgetReportQueryHelper.validateAndAppend(jpqlCondition, "Scheme", schemeid,
					"bmis.scheme.id", "schemeid", queryParams, Function.identity());
			case "subscheme" -> budgetReportQueryHelper.validateAndAppend(jpqlCondition, "Subscheme", subschemeid,
					"bmis.subScheme.id", "subschemeid", queryParams, Function.identity());
			case "boundary" -> budgetReportQueryHelper.validateAndAppend(jpqlCondition, "Boundary", boundaryid,
					"bmis.fieldid.id", "boundaryid", queryParams, Function.identity());
			default -> throw new ReportServiceException(
					Map.of("CHECKDATE", "Unsupported budgetaryCheck_groupby_values value: " + value));
			}
		}

		jpqlCondition.append(" and br.billdate <= :asondate and br.billdate >= :fromdate and bd.glcodeid = :glcodeid");
		queryParams.put("asondate", asondate);
		queryParams.put("fromdate", fromdate);
		queryParams.put("glcodeid", glcodeid);

		final String finalQuery = "select sum(coalesce(bd.debitamount,0) - coalesce(bd.creditamount,0)) "
				+ "from EgBillregister br, EgBilldetails bd, EgBillregistermis bmis "
				+ "where br.id = bd.egBillregister.id and br.id = bmis.egBillregister.id "
				+ "and (bmis.budgetCheckReq is null or bmis.budgetCheckReq = true) "
				+ "and bmis.voucherHeader is null and upper(br.status.description) not in ('CANCELLED') "
				+ jpqlCondition;

		TypedQuery<BigDecimal> typedQuery = entityManager.createQuery(finalQuery, BigDecimal.class);
		queryParams.forEach(typedQuery::setParameter);

		BigDecimal billAmount = typedQuery.getSingleResult();
		BigDecimal cancelledBillAmount = getBillAmountWhereCancelledVouchers(jpqlCondition.toString(), fromdate,
				asondate, queryParams);

		if ((billAmount == null || billAmount.compareTo(BigDecimal.ZERO) == 0)
				&& (cancelledBillAmount == null || cancelledBillAmount.compareTo(BigDecimal.ZERO) == 0)) {
			throw new ReportServiceException(Map.of("budget", "No matching budget found for given parameters"));
		}

		return (billAmount != null ? billAmount : BigDecimal.ZERO).add(cancelledBillAmount);
	}

	private BigDecimal getBillAmountWhereCancelledVouchers(final String conditionJpql, final Date fromdate,
			final Date asondate, Map<String, Object> queryParams) {

		final String jpql = "select sum(coalesce(bd.debitamount, 0) - coalesce(bd.creditamount, 0)) "
				+ "from EgBillregister br, EgBilldetails bd, EgBillregistermis bmis, CVoucherHeader vh "
				+ "where br.id = bd.egBillregister.id " + "and br.id = bmis.egBillregister.id "
				+ "and bmis.voucherHeader = vh.id " + "and (bmis.budgetCheckReq is null or bmis.budgetCheckReq = true) "
				+ "and upper(br.status.description) not in ('CANCELLED') " + "and vh.status = 4 " + conditionJpql;

		TypedQuery<BigDecimal> query = entityManager.createQuery(jpql, BigDecimal.class);
		queryParams.forEach(query::setParameter);

		BigDecimal result = query.getSingleResult();
		return result != null ? result : BigDecimal.ZERO;
	}

	private void populateDataForBill(final EgBillregister bill, final FinancialYear financialYear,
			final EgBilldetails detail, final BudgetReportEntry budgetReportEntry, final CChartOfAccounts coa) {
		Long functionId = Long.parseLong(detail.getFunctionid().toString());
		org.egov.finance.report.entity.Function function = masterCommonService.getFunctionById(functionId);
		final Map<String, Object> budgetDataMap = getRequiredBudgetDataForBill(bill, financialYear, function, coa);
		budgetReportEntry.setFunctionName(function.getName());
		budgetReportEntry.setGlCode(coa.getGlcode());
		budgetReportEntry.setFinancialYear(financialYear.getFinYearRange());
		BigDecimal budgetedAmtForYear = BigDecimal.ZERO;
		// try {
		budgetedAmtForYear = getBudgetedAmtForYear(budgetDataMap);
		// } catch (final ValidationException e) {
		/*
		 * throw e; }
		 */
		budgetReportEntry.setBudgetedAmtForYear(budgetedAmtForYear);
		populateBudgetAppNumber(bill, budgetReportEntry);
		final BigDecimal billAmount = getBillAmount(detail);
		BigDecimal soFarAppropriated = BigDecimal.ZERO;
		soFarAppropriated = getSoFarAppropriated(budgetDataMap, billAmount);
		budgetReportEntry.setAccountCode(coa.getGlcode());
		budgetReportEntry.setSoFarAppropriated(soFarAppropriated);
		final BigDecimal balance = budgetReportEntry.getBudgetedAmtForYear()
				.subtract(budgetReportEntry.getSoFarAppropriated());
		budgetReportEntry.setBalance(balance);
		budgetReportEntry.setCumilativeIncludingCurrentBill(soFarAppropriated.add(billAmount));
		budgetReportEntry.setCurrentBalanceAvailable(balance.subtract(billAmount));
		budgetReportEntry.setCurrentBillAmount(billAmount);
	}

	private Map<String, Object> getRequiredBudgetDataForBill(final EgBillregister cbill,
			final FinancialYear financialYear, final org.egov.finance.report.entity.Function function,
			final CChartOfAccounts coa) {
		final Map<String, Object> budgetDataMap = new HashMap<String, Object>();
		budgetDataMap.put("financialyearid", financialYear.getId());
		budgetDataMap.put(ReportConstants.DEPTID, cbill.getEgBillregistermis().getDepartmentcode());
		if (cbill.getEgBillregistermis().getFunctionaryid() != null)
			budgetDataMap.put(ReportConstants.FUNCTIONARYID, cbill.getEgBillregistermis().getFunctionaryid().getId());
		if (cbill.getEgBillregistermis().getScheme() != null)
			budgetDataMap.put(ReportConstants.SCHEMEID, cbill.getEgBillregistermis().getScheme().getId());
		if (cbill.getEgBillregistermis().getSubScheme() != null)
			budgetDataMap.put(ReportConstants.SUBSCHEMEID, cbill.getEgBillregistermis().getSubScheme().getId());
		budgetDataMap.put(ReportConstants.FUNDID, cbill.getEgBillregistermis().getFund().getId());
		budgetDataMap.put(ReportConstants.BOUNDARYID, cbill.getDivision());
		budgetDataMap.put(ReportConstants.ASONDATE, cbill.getBilldate());
		budgetDataMap.put(ReportConstants.FUNCTIONID, function.getId());
		budgetDataMap.put("fromdate", financialYear.getStartingDate());
		budgetDataMap.put("glcode", coa.getGlcode());
		budgetDataMap.put("glcodeid", coa.getId());
		budgetDataMap.put("budgetheadid", getBudgetHeadByGlcode(coa));
		return budgetDataMap;
	}

	public List<BudgetGroup> getBudgetHeadByGlcode(final CChartOfAccounts coa) {
		Map<String, String> errorMap = new HashMap<>();
		String glcode = coa.getGlcode();
		List<BudgetGroup> bgList = null;
		int majorCodeLnegth = Optional
				.ofNullable(
						masterCommonService.getConfigValuesByModuleAndKey(ReportConstants.EGF, "coa_majorcode_length"))
				.filter(x -> x != null).map(list -> Integer.valueOf(list.get(0).getValue())).orElseThrow(() -> {
					errorMap.put(ReportConstants.FUNCTIONID, "coa_majorcode_length is not defined");
					throw new ReportServiceException(errorMap);
				});

		int minorCodeLength = Optional
				.ofNullable(
						masterCommonService.getConfigValuesByModuleAndKey(ReportConstants.EGF, "coa_minorcode_length"))
				.filter(x -> x != null).map(list -> Integer.valueOf(list.get(0).getValue())).orElseThrow(() -> {
					errorMap.put(ReportConstants.FUNCTIONID, "coa_majorcode_length is not defined");
					throw new ReportServiceException(errorMap);

				});
		bgList = budgetGroupRepository.findEligibleBudgetGroups(glcode);
		if (!bgList.isEmpty())
			return bgList;

		bgList = budgetGroupRepository.findEligibleBudgetGroups(glcode.substring(0, minorCodeLength));
		if (!bgList.isEmpty())
			return bgList;

		bgList = budgetGroupRepository.findByMajorCodeGlcode(glcode.substring(0, majorCodeLnegth));
		if (!bgList.isEmpty())
			return bgList;

		// need to change all the Exception
		errorMap.put(ReportConstants.FUNCTIONID, "Budget Check failed: Budget not defined for the given combination.");
		throw new ReportServiceException(errorMap);
	}

	private BigDecimal getApprovedAmt(final List<BudgetDetail> bdList) {
		BigDecimal approvedAmt = BigDecimal.ZERO;
		for (final BudgetDetail bd : bdList) {
			if (bd.getApprovedReAppropriationsTotal() != null) {
				approvedAmt = approvedAmt.add(bd.getApprovedReAppropriationsTotal());
			} else if (bd.getApprovedAmount() != null) {
				approvedAmt = approvedAmt.add(bd.getApprovedAmount());
			}
		}
		return approvedAmt;
	}

	private boolean isBudgetCheckNeeded(final CChartOfAccounts coa) {
		List<BudgetControlType> controlTypes = masterCommonService.getAllBudgetControlType();
		if (controlTypes.isEmpty()) {
			throw new ApplicationRuntimeException("Budget Check Configuration not defined");
		}
		if (controlTypes.size() > 1) {
			throw new ApplicationRuntimeException("Multiple Budget Check Configurations defined");
		}
		BudgetControlType controlType = controlTypes.get(0);
		boolean budgetCheckEnabled = !BudgetControlType.BudgetCheckOption.NONE.toString()
				.equals(controlType.getValue());
		return budgetCheckEnabled && coa != null && Boolean.TRUE.equals(coa.getBudgetCheckReq());
	}

	private BigDecimal getSoFarAppropriated(final Map<String, Object> budgetDataMap, final BigDecimal billAmount) {
		BigDecimal actualAmtFromVoucher = Optional.ofNullable(getActualBudgetUtilizedForBudgetaryCheck(budgetDataMap))
				.orElse(BigDecimal.ZERO);

		BigDecimal actualAmtFromBill = Optional.ofNullable(getBillAmountForBudgetCheck(budgetDataMap))
				.orElse(BigDecimal.ZERO);

		return actualAmtFromVoucher.add(actualAmtFromBill)
				.subtract(Optional.ofNullable(billAmount).orElse(BigDecimal.ZERO));
	}

	private BigDecimal getBillAmount(final EgBilldetails billDetail) {
		return Optional.ofNullable(billDetail.getDebitamount()).filter(debit -> debit.compareTo(BigDecimal.ZERO) != 0)
				.orElse(Optional.ofNullable(billDetail.getCreditamount()).orElse(BigDecimal.ZERO));
	}

	public BigDecimal getActualBudgetUtilizedForBudgetaryCheck(final Map<String, Object> paramMap) {
		Date asondate = (Date) paramMap.get(ReportConstants.ASONDATE);
		if (asondate == null)
			throw new ReportServiceException(Map.of("CHECKDATE", "As On Date is null"));

		FinancialYear finyear = masterCommonService.getFinancialYearByDate(asondate);
		if (finyear == null)
			throw new ReportServiceException(Map.of("CHECKDATE", "Financial year is not defined for this date"));

		Date fromdate = finyear.getStartingDate();
		paramMap.put("financialyearid", finyear.getId());
		paramMap.put("fromdate", fromdate);
		paramMap.put(ReportConstants.ASONDATE, finyear.getEndingDate());

		List<AppConfigValues> budgetGrouplist = masterCommonService.getConfigValuesByModuleAndKey(ReportConstants.EGF,
				ReportConstants.BUDGETARY_CHECK_GROUPBY_VALUES);
		if (budgetGrouplist.isEmpty())
			throw new ReportServiceException(
					Map.of("CONFIG", "budgetaryCheck_groupby_values is not defined in AppConfig"));

		String[] values = StringUtils.split(budgetGrouplist.get(0).getValue(), ",");
		StringBuilder query = new StringBuilder();
		Map<String, Object> params = new HashMap<>();

		for (String value : values) {
			switch (value.trim()) {
			case "department" -> budgetReportQueryHelper.validateAndAppend(query, "Department",
					paramMap.get(ReportConstants.DEPTID), "vmis.departmentcode", "deptCode", params, Object::toString);
			case "function" ->
				budgetReportQueryHelper.validateAndAppend(query, "Function", paramMap.get(ReportConstants.FUNCTIONID),
						"gl.functionId", "functionid", params, Function.identity());
			case "functionary" -> budgetReportQueryHelper.validateAndAppend(query, "Functionary",
					paramMap.get(ReportConstants.FUNCTIONARYID), "vmis.functionary", "functionaryid", params,
					Function.identity());
			case "fund" -> budgetReportQueryHelper.validateAndAppend(query, "Fund",
					paramMap.get(ReportConstants.FUNDID), "vh.fundId", "fundid", params, Function.identity());
			case "scheme" -> budgetReportQueryHelper.validateAndAppend(query, "Scheme",
					paramMap.get(ReportConstants.SCHEMEID), "vmis.schemeid", "schemeid", params, Function.identity());
			case "subscheme" ->
				budgetReportQueryHelper.validateAndAppend(query, "Subscheme", paramMap.get(ReportConstants.SUBSCHEMEID),
						"vmis.subschemeid", "subschemeid", params, Function.identity());
			case "boundary" ->
				budgetReportQueryHelper.validateAndAppend(query, "Boundary", paramMap.get(ReportConstants.BOUNDARYID),
						"vmis.divisionid", "boundaryid", params, Function.identity());
			default -> throw new ReportServiceException(
					Map.of("CONFIG", "Unsupported budgetaryCheck_groupby_values value: " + value));
			}
		}

		String glcode = (String) paramMap.get("glcode");
		if (StringUtils.isBlank(glcode))
			throw new ReportServiceException(Map.of("GLCODE", "Glcode is null"));

		query.append(" and gl.glcode=:glcode");
		params.put("glcode", glcode);

		CChartOfAccounts coa = masterCommonService.getCChartOfAccountsByGlCode(glcode);
		if (coa == null)
			throw new ReportServiceException(Map.of("GLCODE", "ChartOfAccounts not found for glcode: " + glcode));

		String select = ("I".equalsIgnoreCase(coa.getType().toString())
				|| "L".equalsIgnoreCase(coa.getType().toString()))
						? "SELECT SUM(gl.creditAmount) - SUM(gl.debitAmount) "
						: "SELECT SUM(gl.debitAmount) - SUM(gl.creditAmount) ";

		List<AppConfigValues> statusConfig = masterCommonService.getConfigValuesByModuleAndKey(ReportConstants.EGF,
				"exclude_status_forbudget_actual");
		if (statusConfig.isEmpty())
			throw new ReportServiceException(
					Map.of("CONFIG", "exclude_status_forbudget_actual is not defined in AppConfig"));

		StringBuilder queryString = new StringBuilder(select)
				.append(" FROM CGeneralLedger gl, CVoucherHeader vh, Vouchermis vmis ")
				.append(" WHERE vh.id = gl.voucherHeaderId.id AND vh.id = vmis.voucherheaderid ")
				.append(" AND (vmis.budgetCheckReq IS NULL OR vmis.budgetCheckReq = true) ")
				.append(" AND vh.status != 4 ")
				.append(" AND vh.voucherDate >= :fromdate AND vh.voucherDate <= :asondate ").append(query);

		params.put("fromdate", fromdate);
		params.put("asondate", asondate);

		TypedQuery<BigDecimal> typedQuery = entityManager.createQuery(queryString.toString(), BigDecimal.class);
		params.forEach(typedQuery::setParameter);

		BigDecimal result = typedQuery.getSingleResult();
		return result != null ? result : BigDecimal.ZERO;
	}

	public List<Object> getBudgetDetailsForVoucher(final CVoucherHeader voucher) {
		List<Object> budgetDetailsList = new ArrayList<>();

		if (voucher == null)
			return budgetDetailsList;

		Vouchermis vmis = voucher.getVouchermis();
		String budgetaryAppNumber = vmis != null ? vmis.getBudgetaryAppnumber() : null;

		if (StringUtils.isBlank(budgetaryAppNumber))
			return budgetDetailsList;

		FinancialYear financialYear = masterCommonService.getFinancialYearByDate(voucher.getVoucherDate());
		List<CGeneralLedger> ledgerDetails = ledgerService.getAllLedgerForvoucer(voucher.getId());
		Department department = getDepartmentForVoucher(voucher);

		for (CGeneralLedger detail : ledgerDetails) {
			if (!isBudgetCheckNeeded(detail.getGlcodeId()))
				continue;

			BudgetReportEntry entry = new BudgetReportEntry();
			populateDataForVoucher(voucher, financialYear, detail, entry);
			entry.setDepartmentName(department != null ? department.getName() : null);

			if (voucher.getFundId() != null) {
				entry.setFundName(voucher.getFundId().getName());
			}

			budgetDetailsList.add(entry);
		}

		return budgetDetailsList;
	}

	private Department getDepartmentForVoucher(final CVoucherHeader voucher) {
		if (voucher == null || voucher.getVouchermis() == null)
			return null;

		String deptId = voucher.getVouchermis().getDepartmentcode();
		return (deptId != null) ? (Department) masterCommonService.getDepartmenByCode(deptId) : null;
	}

	private void populateDataForVoucher(final CVoucherHeader voucher, final FinancialYear financialYear,
			final CGeneralLedger detail, final BudgetReportEntry budgetReportEntry) {
		final CChartOfAccounts coa = detail.getGlcodeId();
		final org.egov.finance.report.entity.Function function = masterCommonService
				.getFunctionById(commonUtils.toLong(detail.getFunctionId()));

		final Map<String, Object> budgetDataMap = getRequiredBudgetDataForVoucher(voucher, financialYear, function,
				coa);

		budgetReportEntry.setFunctionName(function.getName());
		budgetReportEntry.setGlCode(coa.getGlcode());
		budgetReportEntry.setFinancialYear(financialYear.getFinYearRange());

		BigDecimal budgetedAmtForYear = getBudgetedAmtForYear(budgetDataMap);
		budgetReportEntry.setBudgetedAmtForYear(budgetedAmtForYear);

		populateBudgetAppNumberForVoucher(voucher, budgetReportEntry);

		final BigDecimal billAmount = getVoucherAmount(detail);
		BigDecimal soFarAppropriated = getSoFarAppropriated(budgetDataMap, billAmount);

		budgetReportEntry.setAccountCode(coa.getGlcode());
		budgetReportEntry.setSoFarAppropriated(soFarAppropriated);

		final BigDecimal balance = budgetedAmtForYear.subtract(soFarAppropriated);
		budgetReportEntry.setBalance(balance);

		budgetReportEntry.setCumilativeIncludingCurrentBill(soFarAppropriated.add(billAmount));
		budgetReportEntry.setCurrentBalanceAvailable(balance.subtract(billAmount));
		budgetReportEntry.setCurrentBillAmount(billAmount);
	}

	private Map<String, Object> getRequiredBudgetDataForVoucher(final CVoucherHeader voucher,
			final FinancialYear financialYear, final org.egov.finance.report.entity.Function function,
			final CChartOfAccounts coa) {
		final Map<String, Object> budgetDataMap = new HashMap<>();
		final Vouchermis mis = voucher.getVouchermis();

		budgetDataMap.put("financialyearid", financialYear.getId());
		budgetDataMap.put(ReportConstants.DEPTID, mis.getDepartmentcode());

		if (mis.getFunctionary() != null)
			budgetDataMap.put(ReportConstants.FUNCTIONARYID, mis.getFunctionary().getId());

		if (mis.getSchemeid() != null)
			budgetDataMap.put(ReportConstants.SCHEMEID, mis.getSchemeid().getId());

		if (mis.getSubschemeid() != null)
			budgetDataMap.put(ReportConstants.SUBSCHEMEID, mis.getSubschemeid().getId());

		if (voucher.getFundId() != null)
			budgetDataMap.put(ReportConstants.FUNDID, voucher.getFundId().getId());

		budgetDataMap.put(ReportConstants.BOUNDARYID, mis.getDivisionid());
		budgetDataMap.put(ReportConstants.ASONDATE, voucher.getVoucherDate());
		budgetDataMap.put(ReportConstants.FUNCTIONID, function.getId());
		budgetDataMap.put("fromdate", financialYear.getStartingDate());
		budgetDataMap.put("glcode", coa.getGlcode());
		budgetDataMap.put("glcodeid", coa.getId());
		budgetDataMap.put("budgetheadid", getBudgetHeadByGlcode(coa));

		return budgetDataMap;
	}
	
	private void populateBudgetAppNumberForVoucher(final CVoucherHeader voucher,
			final BudgetReportEntry budgetReportEntry) {
		if (!StringUtils.isBlank(voucher.getVouchermis().getBudgetaryAppnumber()))
			budgetReportEntry.setBudgetApprNumber(voucher.getVouchermis().getBudgetaryAppnumber());
	}
	
	private BigDecimal getVoucherAmount(final CGeneralLedger detail) {
	    if (detail.getDebitAmount() != null && detail.getDebitAmount().compareTo(BigDecimal.ZERO) != 0) {
	        return detail.getDebitAmount();
	    } else if (detail.getCreditAmount() != null) {
	        return detail.getCreditAmount();
	    } else {
	        return BigDecimal.ZERO;
	    }
	}
	

}
