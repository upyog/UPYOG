/**
 * Created on Jun 19, 2025.
 * 
 * @author bdhal
 */
package org.egov.finance.report.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;
import org.egov.finance.report.entity.AppConfigValues;
import org.egov.finance.report.entity.BudgetDetail;
import org.egov.finance.report.entity.BudgetGroup;
import org.egov.finance.report.entity.EgBillregister;
import org.egov.finance.report.entity.FinancialYear;
import org.egov.finance.report.exception.ReportServiceException;
import org.egov.finance.report.model.BudgetReportEntry;
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

	public BigDecimal getBudgetedAmtForYear(Map<String, Object> paramMap) {
		Map<String, Object> params = new HashMap<>();
		List<AppConfigValues> configValues = masterCommonService.getConfigValuesByModuleAndKey("EGF",
				"budgetaryCheck_groupby_values");

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
				subschemeid, boundaryid != null ? boundaryid.intValue() : null, fundid, params, configValues);

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

	private BigDecimal getApprovedAmt(final List<BudgetDetail> bdList) {
		BigDecimal approvedAmt = BigDecimal.ZERO;
		for (final BudgetDetail bd : bdList) {
			BigDecimal baseAmount = bd.getApprovedAmount() != null ? bd.getApprovedAmount() : BigDecimal.ZERO;
			BigDecimal reAmount = bd.getApprovedReAppropriationsTotal() != null ? bd.getApprovedReAppropriationsTotal()
					: BigDecimal.ZERO;
			approvedAmt = approvedAmt.add(baseAmount).add(reAmount);
		}
		return approvedAmt;
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

}
