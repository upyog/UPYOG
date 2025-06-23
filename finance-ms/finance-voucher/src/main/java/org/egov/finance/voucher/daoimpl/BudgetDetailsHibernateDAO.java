package org.egov.finance.voucher.daoimpl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.egov.finance.voucher.dao.BudgetDetailsDAO;
import org.egov.finance.voucher.entity.AppConfigValues;
import org.egov.finance.voucher.entity.BudgetControlType;
import org.egov.finance.voucher.entity.BudgetDetail;
import org.egov.finance.voucher.entity.BudgetGroup;
import org.egov.finance.voucher.entity.CChartOfAccounts;
import org.egov.finance.voucher.entity.CVoucherHeader;
import org.egov.finance.voucher.entity.EgBillregister;
import org.egov.finance.voucher.entity.FinancialYear;
import org.egov.finance.voucher.exception.ValidationError;
import org.egov.finance.voucher.exception.ValidationException;
import org.egov.finance.voucher.repository.BanNumberGenerator;
import org.egov.finance.voucher.repository.BudgetGroupRepository;
import org.egov.finance.voucher.repository.ChartOfAccountsRepository;
import org.egov.finance.voucher.repository.EgBillregisterRepository;
import org.egov.finance.voucher.repository.FinancialYearRepository;
import org.egov.finance.voucher.service.AppConfigValueService;
import org.egov.finance.voucher.service.AutonumberServiceBeanResolver;
import org.egov.finance.voucher.service.BudgetControlTypeService;
import org.egov.finance.voucher.service.BudgetService;
import org.egov.finance.voucher.util.Constants;
import org.egov.finance.voucher.util.FinancialConstants;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Repository
@Transactional(readOnly = true)
public class BudgetDetailsHibernateDAO implements BudgetDetailsDAO {

	private static final String EGF = "EGF";
	private static final String BUDGETHEADID = "budgetheadid";
	private static final String GLCODEID = "glcodeid";
	private static final String BUDGETARY_CHECK_GROUPBY_VALUES = "budgetaryCheck_groupby_values";
	private Session session;
	private static final Logger LOGGER = LoggerFactory.getLogger(BudgetDetailsHibernateDAO.class);
	private static final String EMPTY_STRING = "";

	@Autowired
	private BudgetControlTypeService budgetCheckConfigService;

	@Autowired
	private AppConfigValueService appConfigValuesService;

	@Autowired
	ChartOfAccountsRepository chartOfAccountsRepository;

	@Autowired
	private BudgetGroupRepository budgetGroupRepository;

	@Autowired
	FinancialYearRepository financialYearRepository;

	@Autowired
	private BudgetService budgetService;

	@Autowired
	private AutonumberServiceBeanResolver beanResolver;

	@Autowired
	private EgBillregisterRepository egBillregisterRepository;

	@Override
	public boolean budgetaryCheck(Map<String, Object> paramMap) throws ValidationException {
		String cashbasedbudgetType = EMPTY_STRING, txnType = EMPTY_STRING;
		BigDecimal debitAmt = null;
		BigDecimal creditAmt = null;
		BigDecimal txnAmt = null;

		try {

			String budgetCheckConfig = budgetCheckConfigService.getConfigValue();

			if (budgetCheckConfig.equals("NONE"))
				return true;

			if (paramMap.get("mis.budgetcheckreq") != null
					&& ((Boolean) paramMap.get("mis.budgetcheckreq")).equals(false))
				return true;

			if (paramMap.get("debitAmt") != null)
				debitAmt = (BigDecimal) paramMap.get("debitAmt");
			if (paramMap.get("creditAmt") != null)
				creditAmt = (BigDecimal) paramMap.get("creditAmt");

			if (debitAmt == null && creditAmt == null)
				throw new ValidationException(EMPTY_STRING, "Both Debit and Credit amount is null");

			if (debitAmt != null && debitAmt.compareTo(BigDecimal.ZERO) == 0 && creditAmt != null
					&& creditAmt.compareTo(BigDecimal.ZERO) == 0)
				throw new ValidationException(EMPTY_STRING, "Both Debit and Credit amount is zero");

			if (debitAmt != null && debitAmt.compareTo(BigDecimal.ZERO) > 0 && creditAmt != null
					&& creditAmt.compareTo(BigDecimal.ZERO) > 0)
				throw new ValidationException(EMPTY_STRING, "Both Debit and Credit amount is greater than zero");

			// get the type of budget from appconfig .
			List<AppConfigValues> list;
			list = appConfigValuesService.getConfigValuesByModuleAndKey(EGF, "budgetaryCheck_budgettype_cashbased");
			if (list.isEmpty())
				throw new ValidationException(EMPTY_STRING,
						"budgetaryCheck_budgettype_cashbased is not defined in AppConfig");

			cashbasedbudgetType = list.get(0).getValue();
			if (cashbasedbudgetType.equalsIgnoreCase("Y")) // cash based budget
			{
				if (LOGGER.isDebugEnabled())
					LOGGER.debug("cashbasedbudgetType==" + cashbasedbudgetType);
			} else // Accural based budgeting
			{
				if (debitAmt != null && debitAmt.compareTo(BigDecimal.ZERO) > 0) {
					txnType = "debit";
					txnAmt = debitAmt;
				} else {
					txnType = "credit";
					txnAmt = creditAmt;
				}
				paramMap.put("txnAmt", txnAmt);
				paramMap.put("txnType", txnType);
				return checkCondition(paramMap);
			}
		} catch (final ValidationException v) {
			throw new ValidationException(EMPTY_STRING, v.getMessage());
		} /*
			 * catch (final Exception e) { throw new ValidationException(EMPTY_STRING,
			 * e.getMessage()); }
			 */
		return true;
	}

	private boolean checkCondition(Map<String, Object> paramMap) {
		String txnType = null;
		String glCode = null;
		BigDecimal txnAmt = null;
		java.util.Date asondate = null;
		java.util.Date fromdate = null;

		try {
			if (paramMap.get("txnAmt") != null)
				txnAmt = (BigDecimal) paramMap.get("txnAmt");
			if (paramMap.get("txnType") != null)
				txnType = paramMap.get("txnType").toString();
			if (paramMap.get("glcode") != null)
				glCode = paramMap.get("glcode").toString();
			if (paramMap.get(Constants.ASONDATE) != null)
				asondate = (Date) paramMap.get(Constants.ASONDATE);

			if (glCode == null)
				throw new ValidationException(EMPTY_STRING, "glcode is null");
			if (txnAmt == null)
				throw new ValidationException(EMPTY_STRING, "txnAmt is null");
			if (txnType == null)
				throw new ValidationException(EMPTY_STRING, "txnType is null");
			if (asondate == null)
				throw new ValidationException(EMPTY_STRING, "As On Date is null");

			// check the account code needs budget checking

			final CChartOfAccounts coa = chartOfAccountsRepository.getByGlcode(glCode);
			if (coa.getBudgetCheckReq() != null && coa.getBudgetCheckReq()) {
				// get budgethead for the glcode
				// BudgetGroup bg = getBudgetHeadByGlcode(coa,paramMap);
				final List<BudgetGroup> budgetHeadListByGlcode = getBudgetHeadByGlcode(coa);
				if (budgetHeadListByGlcode == null || budgetHeadListByGlcode.size() == 0)
					throw new ValidationException(EMPTY_STRING,
							"Budget Check failed: Budget not defined for the given combination.");
				// get budgettinh type for first BG object
				if (!isBudgetCheckingRequiredForType(txnType,
						budgetHeadListByGlcode.get(0).getBudgetingType().toString())) {
					if (LOGGER.isDebugEnabled())
						LOGGER.debug(
								"No need to check budget for :" + glCode + " as the transaction type is " + txnType);
					return true;
				}

				paramMap.put("glcodeid", coa.getId());
				// get the financialyear from asondate

				final FinancialYear finyear = financialYearRepository.getFinancialYearByDate(asondate);
				if (finyear == null)
					throw new ValidationException(EMPTY_STRING, "Financial Year is not defined for-" + asondate);
				new SimpleDateFormat("dd-MMM-yyyy", Constants.LOCALE);
				fromdate = finyear.getStartingDate();

				paramMap.put("fromdate", fromdate);
				// Here as on date is overridden by Financialyear ending date to
				// check all budget appropriation irrespective of
				// date
				paramMap.put(Constants.ASONDATE, finyear.getEndingDate());
				paramMap.put("financialyearid", Long.valueOf(finyear.getId()));

				paramMap.put(BUDGETHEADID, budgetHeadListByGlcode);

				if (LOGGER.isDebugEnabled())
					LOGGER.debug("************ BudgetCheck Details *********************");
				// pass the list of bg to getBudgetedAmtForYear
				final BigDecimal budgetedAmt = getBudgetedAmtForYear(paramMap); // get
				// the
				// budgetedamount
				if (LOGGER.isDebugEnabled())
					LOGGER.debug(".............Budgeted Amount For the year............" + budgetedAmt);

				if (budgetCheckConfigService.getConfigValue()
						.equalsIgnoreCase(BudgetControlType.BudgetCheckOption.MANDATORY.toString()))
					if (budgetedAmt.compareTo(BigDecimal.ZERO) == 0)
						return false;

				final BigDecimal actualAmt = getActualBudgetUtilizedForBudgetaryCheck(paramMap); // get
				// actual
				// amount
				if (LOGGER.isDebugEnabled())
					LOGGER.debug(".............Voucher Actual amount............" + actualAmt);

				BigDecimal billAmt = getBillAmountForBudgetCheck(paramMap); // get
				// actual
				// amount
				if (LOGGER.isDebugEnabled())
					LOGGER.debug(".............Bill Actual amount............" + billAmt);
				EgBillregister bill = null;

				Long billId = (Long) paramMap.get("bill");
				Optional<EgBillregister> billOptional = egBillregisterRepository.findById(billId);
				if (billOptional.isPresent()) {
					bill = billOptional.get();
					if (bill.getEgBillregistermis().getBudgetaryAppnumber() != null) {
						if (LOGGER.isDebugEnabled())
							LOGGER.debug(".............Found BillId so subtracting txn amount......................"
									+ txnAmt);
						billAmt = billAmt.subtract(txnAmt);
					}
				}
				if (LOGGER.isDebugEnabled())
					LOGGER.debug(".......Recalculated Bill Actual amount............" + billAmt);
				final BigDecimal diff = budgetedAmt.subtract(actualAmt).subtract(billAmt); // get
				// bill
				// amt
				if (LOGGER.isDebugEnabled())
					LOGGER.debug(".................diff amount..........................." + diff);

				if (LOGGER.isDebugEnabled())
					LOGGER.debug("************ BudgetCheck Details End****************");
				// BigDecimal diff = budgetedAmt.subtract(actualAmt);

				if (budgetCheckConfigService.getConfigValue()
						.equalsIgnoreCase(BudgetControlType.BudgetCheckOption.MANDATORY.toString())) {
					if (txnAmt.compareTo(diff) <= 0) {

						generateBanNumber(paramMap, bill);
						return true;
					}

					else
						return false;
				}
				if (budgetCheckConfigService.getConfigValue()
						.equalsIgnoreCase(BudgetControlType.BudgetCheckOption.ANTICIPATORY.toString())) {
					generateBanNumber(paramMap, bill);
					return true;
				}
			} else // no budget check for coa
				return true;
		} catch (final ValidationException v) {
			throw new ValidationException(EMPTY_STRING, v.getMessage());
		} /*
			 * catch (final Exception e) { throw new ValidationException(EMPTY_STRING,
			 * e.getMessage()); }
			 */
		return true;
	}

	private void generateBanNumber(final Map<String, Object> paramMap, EgBillregister bill) {
		if (bill == null || bill.getEgBillregistermis().getBudgetaryAppnumber() == null
				|| "".equals(bill.getEgBillregistermis().getBudgetaryAppnumber()))
			if (paramMap.get("voucherHeader") != null && ((CVoucherHeader) paramMap.get("voucherHeader"))
					.getVouchermis().getBudgetaryAppnumber() == null) {
				if (LOGGER.isDebugEnabled())
					LOGGER.debug("Bill level budget app no not generated so generating voucher level");
				if (bill != null) {
					if (LOGGER.isDebugEnabled())
						LOGGER.debug("bill Number..........." + bill.getBillnumber());
				} else if (LOGGER.isDebugEnabled())
					LOGGER.debug("Bill not present");
				((CVoucherHeader) paramMap.get("voucherHeader")).getVouchermis()
						.setBudgetaryAppnumber(getBudgetApprNumber(paramMap));
			}
	}

	public String getBudgetApprNumber(final Map<String, Object> paramMap) {

		BanNumberGenerator b = (BanNumberGenerator) beanResolver.getAutoNumberServiceFor(BanNumberGenerator.class);
		final String budgetApprNumber = b.getNextNumber();
		return budgetApprNumber;
	}

	public BigDecimal getBillAmountForBudgetCheck(final Map<String, Object> paramMap) {
		String deptCode = null;
		Long functionid = null;
		Integer functionaryid = null;
		Integer schemeid = null;
		Integer subschemeid = null;
		Long boundaryid = null;
		Integer fundid = null;
		Long glcodeid = null;
		Date fromdate = null;
		Date asondate = null;
		BigDecimal totalBillUtilized = new BigDecimal(0);

		final StringBuilder query = new StringBuilder();
		final Map<String, Object> queryParams = new HashMap<>();
		StringBuilder query1 = new StringBuilder();
		try {
			if (paramMap.get(Constants.DEPTID) != null)
				deptCode = paramMap.get(Constants.DEPTID).toString();
			if (paramMap.get(Constants.FUNCTIONID) != null)
				functionid = (Long) paramMap.get(Constants.FUNCTIONID);
			if (paramMap.get(Constants.FUNCTIONARYID) != null)
				functionaryid = (Integer) paramMap.get(Constants.FUNCTIONARYID);
			if (paramMap.get(Constants.SCHEMEID) != null)
				schemeid = (Integer) paramMap.get(Constants.SCHEMEID);
			if (paramMap.get(Constants.FUNDID) != null)
				fundid = (Integer) paramMap.get(Constants.FUNDID);
			if (paramMap.get(Constants.SUBSCHEMEID) != null)
				subschemeid = (Integer) paramMap.get(Constants.SUBSCHEMEID);
			if (paramMap.get(Constants.BOUNDARYID) != null)
				boundaryid = (Long) paramMap.get(Constants.BOUNDARYID);
			if (paramMap.get(GLCODEID) != null)
				glcodeid = (Long) paramMap.get(GLCODEID);
			if (paramMap.get(Constants.ASONDATE) != null)
				asondate = (java.util.Date) paramMap.get(Constants.ASONDATE);
			if (paramMap.get("fromdate") != null)
				fromdate = (java.util.Date) paramMap.get("fromdate");

			// get the financialyear from asondate
			final FinancialYear finyear = financialYearRepository.getFinancialYearByDate(asondate);
			final SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy", Constants.LOCALE);
			if (finyear == null)
				throw new ValidationException(EMPTY_STRING,
						"Financial year is not defined for this date [" + sdf.format(asondate) + "]");
			fromdate = finyear.getStartingDate();

			paramMap.put("financialyearid", Long.valueOf(finyear.getId()));
			paramMap.put("fromdate", fromdate);
			paramMap.put(Constants.ASONDATE, finyear.getEndingDate());

			if (LOGGER.isDebugEnabled())
				LOGGER.debug("deptCode=" + deptCode + ",functionid=" + functionid + ",functionaryid=" + functionaryid
						+ ",schemeid=" + schemeid + ",subschemeid=" + subschemeid + ",boundaryid=" + boundaryid
						+ ",glcodeid=" + glcodeid + ",asondate=" + asondate);

			if (asondate == null)
				throw new ValidationException(EMPTY_STRING, "As On Date is null");

			final List<AppConfigValues> budgetGrouplist = appConfigValuesService.getConfigValuesByModuleAndKey(EGF,
					BUDGETARY_CHECK_GROUPBY_VALUES);
			if (budgetGrouplist.isEmpty())
				throw new ValidationException(EMPTY_STRING,
						"budgetaryCheck_groupby_values is not defined in AppConfig");
			else {
				final AppConfigValues appConfigValues = budgetGrouplist.get(0);
				final String[] values = StringUtils.split(appConfigValues.getValue(), ",");
				for (final String value : values)
					if (value.equals("department")) {
						if (deptCode == null || deptCode == "0")
							throw new ValidationException(EMPTY_STRING, "Department is required");
						else {
							query.append(" and bmis.departmentcode=:deptCode");
							queryParams.put("deptCode", deptCode);
						}
					} else if (value.equals("function")) {
						if (functionid == null || functionid == 0)
							throw new ValidationException(EMPTY_STRING, "Function is required");
						else {
							query.append(" and bd.functionid=:functionid");
							queryParams.put("functionid", functionid);
						}
					} else if (value.equals("functionary")) {
						if (functionaryid == null || functionaryid == 0)
							throw new ValidationException(EMPTY_STRING, "Functionary is required");
						else {
							query.append(" and bmis.functionaryid.id=:functionaryid");
							queryParams.put("functionaryid", functionaryid);
						}
					} else if (value.equals("fund")) {
						if (fundid == null || fundid == 0)
							throw new ValidationException(EMPTY_STRING, "Fund is required");
						else {
							query.append(" and bmis.fund.id=:fundid");
							queryParams.put("fundid", fundid);
						}
					} else if (value.equals("scheme")) {
						if (schemeid == null || schemeid == 0)
							throw new ValidationException(EMPTY_STRING, "Scheme is required");
						else {
							query.append(" and bmis.scheme.id=:schemeid");
							queryParams.put("schemeid", schemeid);
						}
					} else if (value.equals("subscheme")) {
						if (subschemeid == null || subschemeid == 0)
							throw new ValidationException(EMPTY_STRING, "Sub scheme is required");
						else {
							query.append(" and bmis.subScheme.id=:subschemeid");
							queryParams.put("subschemeid", subschemeid);
						}
					} else if (value.equals("boundary")) {
						if (boundaryid == null || boundaryid == 0)
							throw new ValidationException(EMPTY_STRING, "Boundary is required");
						else {
							query.append(" and bmis.fieldid.id=:boundaryid");
							queryParams.put("boundaryid", boundaryid);
						}
					} else
						throw new ValidationException(EMPTY_STRING,
								"budgetaryCheck_groupby_values is not matching=" + value);
			}
			if (asondate != null) {
				query.append(" and br.billdate <=:asondate ");
				queryParams.put("asondate", asondate);
			}
			if (fromdate != null) {
				query.append(" and  br.billdate>=:fromdate ");
				queryParams.put("fromdate", fromdate);
			}

			query.append(" and bd.glcodeid=:glcodeid");
			queryParams.put("glcodeid", glcodeid);

			query1 = new StringBuilder("select sum(case when bd.debitamount is null then 0").append(
					" ELSE bd.debitamount end -case when bd.creditamount is null then 0 else bd.creditamount end)  ")
					.append(" from EgBillregister br, EgBilldetails bd, EgBillregistermis bmis  ")
					.append(" where br.id=bd.egBillregister.id and br.id=bmis.egBillregister.id")
					.append(" and (bmis.budgetCheckReq is null or bmis.budgetCheckReq=true)  and bmis.voucherHeader is null")
					.append(" and upper(br.status.description) not in ('CANCELLED') ").append(query);

			if (LOGGER.isDebugEnabled())
				LOGGER.debug("getBillAmountForBudgetCheck query============" + query1);

			final Query qry = (Query) entityManager.createQuery(query1.toString());
			queryParams.entrySet().forEach(entry -> qry.setParameter(entry.getKey(), entry.getValue()));
			Object ob = qry.getResultList().isEmpty() ? null : qry.getResultList().get(0);

			final BigDecimal billAmountWhereCancelledVouchers = getBillAmountWhereCancelledVouchers(query.toString(),
					fromdate, asondate, queryParams);

			if (LOGGER.isDebugEnabled())
				LOGGER.debug("Total Amount from all bills where vouchers are cancelled is : "
						+ billAmountWhereCancelledVouchers);

			if (ob == null)
				totalBillUtilized = billAmountWhereCancelledVouchers;
			else
				totalBillUtilized = new BigDecimal(ob.toString()).add(billAmountWhereCancelledVouchers);

			return totalBillUtilized;

		} catch (final ValidationException v) {
			LOGGER.error("Exp in getBillAmountForBudgetCheck API()===" + v.getErrors());
			throw new ValidationException(v.getErrors());
		} /*
			 * catch (final Exception e) {
			 * LOGGER.error("Exp in getBillAmountForBudgetCheck API()===" + e.getMessage());
			 * throw new ValidationException(EMPTY_STRING,
			 * "Exp in getBillAmountForBudgetCheck API()===" + e.getMessage()); }
			 */
	}

	private BigDecimal getBillAmountWhereCancelledVouchers(final String query, final Date fromdate, final Date asondate,
			Map<String, Object> queryParams) {

		final StringBuilder newQuery = new StringBuilder(
				"select sum(case when bd.debitamount is null then 0 else bd.debitamount end -")
						.append(" case when bd.creditamount is null then 0 else bd.creditamount end )  ")
						.append(" from EgBillregister br, EgBilldetails bd, EgBillregistermis bmis, CVoucherHeader vh  ")
						.append(" where br.id = bd.egBillregister.id and br.id = bmis.egBillregister.id ")
						.append(" and (bmis.budgetCheckReq is null or bmis.budgetCheckReq = true) ")
						.append(" and bmis.voucherHeader = vh.id ")
						.append(" and upper(br.status.description) not in ('CANCELLED') and vh.status = 4 ")
						.append(query);

		if (LOGGER.isDebugEnabled())
			LOGGER.debug("getBillAmountWhereCancelledVouchers query============" + newQuery);

		final Query qry = (Query) entityManager.createQuery(newQuery.toString());
		queryParams.forEach(qry::setParameter);

		Object ob = qry.getResultList().isEmpty() ? null : qry.getResultList().get(0);

		return ob == null ? BigDecimal.ZERO : new BigDecimal(ob.toString());
	}

	public BigDecimal getActualBudgetUtilizedForBudgetaryCheck(final Map<String, Object> paramMap) {
		String deptCode = null;
		Long functionid = null;
		Integer functionaryid = null;
		Integer schemeid = null;
		Integer subschemeid = null;
		Long boundaryid = null;
		Integer fundid = null;
		final Long budgetheadid = null;
		Date fromdate = null;
		Date asondate = null;

		final StringBuilder query = new StringBuilder();
		final Map<String, Object> params = new HashMap<>();
		String select = EMPTY_STRING;
		try {
			if (paramMap.get(Constants.DEPTID) != null)
				deptCode = paramMap.get(Constants.DEPTID).toString();
			if (paramMap.get(Constants.FUNCTIONID) != null)
				functionid = (Long) paramMap.get(Constants.FUNCTIONID);
			if (paramMap.get(Constants.FUNCTIONARYID) != null)
				functionaryid = (Integer) paramMap.get(Constants.FUNCTIONARYID);
			if (paramMap.get(Constants.SCHEMEID) != null)
				schemeid = (Integer) paramMap.get(Constants.SCHEMEID);
			if (paramMap.get(Constants.FUNDID) != null)
				fundid = (Integer) paramMap.get(Constants.FUNDID);
			if (paramMap.get(Constants.SUBSCHEMEID) != null)
				subschemeid = (Integer) paramMap.get(Constants.SUBSCHEMEID);
			if (paramMap.get(Constants.BOUNDARYID) != null)
				boundaryid = (Long) paramMap.get(Constants.BOUNDARYID);
			/*
			 * if(paramMap.get(BUDGETHEADID)!=null) budgetheadid =
			 * (Long)paramMap.get(BUDGETHEADID);
			 */
			if (paramMap.get(Constants.ASONDATE) != null)
				asondate = (java.util.Date) paramMap.get(Constants.ASONDATE);

			if (LOGGER.isDebugEnabled())
				LOGGER.debug("deptCode=" + deptCode + ",functionid=" + functionid + ",functionaryid=" + functionaryid
						+ ",schemeid=" + schemeid + ",subschemeid=" + subschemeid + ",boundaryid=" + boundaryid
						+ ",budgetheadid=" + budgetheadid + ",asondate=" + asondate);

			if (asondate == null)
				throw new ValidationException(EMPTY_STRING, "As On Date is null");

			final SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yyyy", Constants.LOCALE);

			final FinancialYear finyear = financialYearRepository.getFinancialYearByDate(asondate);
			if (finyear == null)
				throw new ValidationException(EMPTY_STRING,
						"Financial year is not fefined for this date [" + sdf.format(asondate) + "]");
			fromdate = finyear.getStartingDate();

			final List<AppConfigValues> budgetGrouplist = appConfigValuesService.getConfigValuesByModuleAndKey(EGF,
					BUDGETARY_CHECK_GROUPBY_VALUES);
			if (budgetGrouplist.isEmpty())
				throw new ValidationException(EMPTY_STRING,
						"budgetaryCheck_groupby_values is not defined in AppConfig");
			else {
				final AppConfigValues appConfigValues = budgetGrouplist.get(0);
				final String[] values = StringUtils.split(appConfigValues.getValue(), ",");
				for (final String value : values)
					if (value.equals("department")) {
						query.append(" and vmis.departmentcode=:deptCode");
						params.put("deptCode", deptCode);
					} else if (value.equals("function")) {
						query.append(" and gl.functionId=:functionid");
						params.put("functionid", functionid);
					} else if (value.equals("functionary")) {
						query.append(" and vmis.functionary=:functionaryid");
						params.put("functionaryid", functionaryid);
					} else if (value.equals("fund")) {
						query.append(" and vh.fundId=:fundid");
						params.put("fundid", fundid);
					} else if (value.equals("scheme")) {
						query.append(" and vmis.schemeid=:schemeid");
						params.put("schemeid", schemeid);
					} else if (value.equals("subscheme")) {
						query.append(" and vmis.subschemeid=:subschemeid");
						params.put("subschemeid", subschemeid);
					} else if (value.equals("boundary")) {
						query.append(" and vmis.divisionid=:boundaryid");
						params.put("boundaryid", boundaryid);
					} else
						throw new ValidationException(EMPTY_STRING,
								"budgetaryCheck_groupby_values is not matching=" + value);
			}

			String glcode = EMPTY_STRING;
			if (paramMap.get("glcode") != null)
				glcode = paramMap.get("glcode").toString();
			if (EMPTY_STRING.equals(glcode))
				throw new ValidationException(EMPTY_STRING, "Glcode is null");

			query.append(" and gl.glcode=:glcode");
			params.put("glcode", glcode);

			final CChartOfAccounts coa = chartOfAccountsRepository.getByGlcode(glcode);
			if (coa == null)
				throw new ValidationException(EMPTY_STRING, "Chartofaccounts is null for this glcode:" + glcode);

			if ("I".equalsIgnoreCase(coa.getType().toString()) || "L".equalsIgnoreCase(coa.getType().toString()))
				select = " SELECT SUM(gl.creditAmount)-SUM(gl.debitAmount) ";
			else
				select = " SELECT SUM(gl.debitAmount)-SUM(gl.creditAmount) ";

			final List<AppConfigValues> list = appConfigValuesService.getConfigValuesByModuleAndKey(EGF,
					"exclude_status_forbudget_actual");
			if (list.isEmpty())
				throw new ValidationException(EMPTY_STRING,
						"exclude_status_forbudget_actual is not defined in AppConfig");

			list.get(0).getValue();

			final StringBuilder queryString = new StringBuilder(select)
					.append(" FROM CGeneralLedger gl,CVoucherHeader vh,Vouchermis vmis where  ")
					.append(" vh.id = gl.voucherHeaderId.id AND vh.id=vmis.voucherheaderid")
					.append(" and (vmis.budgetCheckReq=null or vmis.budgetCheckReq=true) and vh.status !=4 ")
					.append("and vh.voucherDate>=:fromdate and vh.voucherDate <=:asondate ").append(query);
			params.put("fromdate", fromdate);
			params.put("asondate", asondate);

			if (LOGGER.isDebugEnabled())
				LOGGER.debug("loadActualBudget query============" + query);
			final Query qry = (Query) entityManager.createQuery(queryString.toString());
			params.entrySet().forEach(entry -> qry.setParameter(entry.getKey(), entry.getValue()));
			final Object ob = qry.getResultList().isEmpty() ? null : qry.getResultList().get(0);
			if (ob == null)
				return BigDecimal.ZERO;
			else
				return new BigDecimal(ob.toString());
		} catch (final ValidationException v) {
			LOGGER.error("Exp in getActualBudgetUtilizedForBudgetaryCheck API()===" + v.getErrors());
			throw new ValidationException(EMPTY_STRING,
					"Exp in getActualBudgetUtilizedForBudgetaryCheck API()===" + v.getMessage());
		} /*
			 * catch (final Exception e) { LOGGER.
			 * error("Exp in getActualBudgetUtilizedForBudgetaryCheck API()===" +
			 * e.getMessage()); throw new ValidationException(EMPTY_STRING,
			 * "Exp in getActualBudgetUtilizedForBudgetaryCheck API()===" + e.getMessage());
			 * }
			 */
	}

	private BigDecimal getBudgetedAmtForYear(Map<String, Object> paramMap) {
		String deptCode = null;
		Long functionid = null;
		Integer functionaryid = null;
		Integer schemeid = null;
		Integer subschemeid = null;
		Long boundaryid = null;
		Integer fundid = null;
		List<BudgetGroup> budgetHeadList = null;
		Long financialyearid = null;

		final StringBuilder query = new StringBuilder();
		final Map<String, Object> params = new HashMap<>();
		try {
			if (paramMap.get(Constants.DEPTID) != null)
				deptCode = paramMap.get(Constants.DEPTID).toString();
			if (paramMap.get(Constants.FUNCTIONID) != null)
				functionid = (Long) paramMap.get(Constants.FUNCTIONID);
			if (paramMap.get(Constants.FUNCTIONARYID) != null)
				functionaryid = (Integer) paramMap.get(Constants.FUNCTIONARYID);
			if (paramMap.get(Constants.SCHEMEID) != null)
				schemeid = (Integer) paramMap.get(Constants.SCHEMEID);
			if (paramMap.get(Constants.SUBSCHEMEID) != null)
				subschemeid = (Integer) paramMap.get(Constants.SUBSCHEMEID);
			if (paramMap.get(Constants.FUNDID) != null)
				fundid = (Integer) paramMap.get(Constants.FUNDID);
			if (paramMap.get(Constants.BOUNDARYID) != null)
				boundaryid = (Long) paramMap.get(Constants.BOUNDARYID);
			if (paramMap.get(BUDGETHEADID) != null)
				budgetHeadList = (List) paramMap.get(BUDGETHEADID);
			if (paramMap.get("financialyearid") != null)
				financialyearid = (Long) paramMap.get("financialyearid");

			if (LOGGER.isDebugEnabled())
				LOGGER.debug("deptCode " + deptCode + ",functionid " + functionid + ",functionaryid " + functionaryid
						+ ",schemeid " + schemeid + ",subschemeid " + subschemeid + ",boundaryid " + boundaryid
						+ ",budgetheadids " + budgetHeadList + ",financialyearid " + financialyearid);

			query.append(prepareQuery(deptCode, functionid, functionaryid, schemeid, subschemeid,
					boundaryid != null ? boundaryid.intValue() : null, fundid, params));

			// handle the list

			if (financialyearid == null)
				throw new ValidationException(EMPTY_STRING, "Financial Year id is null");
			query.append(" and bd.budget.financialYear=:financialyearid");
			params.put("financialyearid", financialyearid);
			if (budgetHeadList == null || budgetHeadList.size() == 0)
				throw new ValidationException(EMPTY_STRING, "Budget head id is null or empty");
			query.append(" and bd.budgetGroup in ( :budgetHeadList )");
			params.put("budgetHeadList", budgetHeadList);

			// check any RE is available for the passed parameters.if RE is not
			// exist, take BE's Approved Amount
			String finalquery;
			if (budgetService.hasApprovedReForYear(financialyearid))
				finalquery = " from BudgetDetail bd where bd.budget.isbere='RE' " + query;
			else
				finalquery = " from BudgetDetail bd where bd.budget.isbere='BE' " + query;

			if (LOGGER.isDebugEnabled())
				LOGGER.debug("Final query=" + finalquery);
			final Query hibQuery = getCurrentSession().createQuery(finalquery);
			params.entrySet().forEach(entry -> hibQuery.setParameter(entry.getKey(), entry.getValue()));
			final List<BudgetDetail> bdList = hibQuery.list();
			if (bdList == null || bdList.size() == 0)
				// return BigDecimal.ZERO;
				throw new ValidationException(
						new ValidationError("Budget Check failed: Budget not defined for the given combination.",
								"Budget Check failed: Budget not defined for the given combination."));
			else
				return getApprovedAmt(bdList);
		} catch (final ValidationException v) {
			LOGGER.error("Exp in getBudgetedAmtForYear==" + v.getErrors());
			throw new ValidationException(EMPTY_STRING, "Exp in getBudgetedAmtForYear==" + v.getMessage());

		} /*
			 * catch (final Exception e) { LOGGER.error("Exp in getBudgetedAmtForYear==" +
			 * e.getMessage()); throw new ValidationException(EMPTY_STRING,
			 * "Exp in getBudgetedAmtForYear==" + e.getMessage()); }
			 */
	}

	private BigDecimal getApprovedAmt(final List<BudgetDetail> bdList) {
		BigDecimal approvedAmt = BigDecimal.ZERO;
		for (final BudgetDetail bd : bdList) {
			if (bd.getApprovedAmount() != null)
				approvedAmt = approvedAmt.add(bd.getApprovedAmount());
			approvedAmt = approvedAmt.add(bd.getApprovedReAppropriationsTotal());
		}
		return approvedAmt;
	}

	@PersistenceContext
	private EntityManager entityManager;

	public Session getCurrentSession() {
		return entityManager.unwrap(Session.class);
	}

	private String prepareQuery(final String departmentCode, final Long functionid, final Integer functionaryid,
			final Integer schemeid, final Integer subschemeid, final Integer boundaryid, final Integer fundid,
			Map<String, Object> params) {
		final StringBuilder query = new StringBuilder();

		final List<AppConfigValues> list = appConfigValuesService.getConfigValuesByModuleAndKey(EGF,
				BUDGETARY_CHECK_GROUPBY_VALUES);
		if (list.isEmpty())
			throw new ValidationException(EMPTY_STRING, "budgetaryCheck_groupby_values is not defined in AppConfig");
		else {
			final AppConfigValues appConfigValues = list.get(0);
			final String[] values = StringUtils.split(appConfigValues.getValue(), ",");
			for (final String value : values)
				if (value.equals("department")) {
					if (departmentCode == null || departmentCode == "0" || departmentCode == "")
						throw new ValidationException(EMPTY_STRING, "Department is required");
					else {
						query.append(" and bd.executingDepartment=:departmentCode");
						params.put("departmentCode", departmentCode);
					}
				} else if (value.equals("function")) {
					if (functionid == null || functionid == 0)
						throw new ValidationException(EMPTY_STRING, "Function is required");
					else {
						query.append(" and bd.function=:functionid");
						params.put("functionid", functionid);
					}
				} else if ("functionary".equals(value)) {
					if (functionaryid == null || functionaryid == 0)
						throw new ValidationException(EMPTY_STRING, "Functionary is required");
					else {
						query.append(" and bd.functionary=:functionaryid");
						params.put("functionaryid", functionaryid);
					}
				} else if (value.equals("fund")) {
					if (fundid == null || fundid == 0)
						throw new ValidationException(EMPTY_STRING, "Fund is required");
					else {
						query.append(" and bd.fund=:fundid");
						params.put("fundid", fundid);
					}
				} else if (value.equals("scheme")) {
					if (schemeid == null || schemeid == 0)
						throw new ValidationException(EMPTY_STRING, "Scheme is required");
					else {
						query.append(" and bd.scheme=:schemeid");
						params.put("schemeid", schemeid);
					}
				} else if (value.equals("subscheme")) {
					if (subschemeid == null || subschemeid == 0)
						throw new ValidationException(EMPTY_STRING, "Subscheme is required");
					else {
						query.append(" and bd.subScheme=:subschemeid");
						params.put("subschemeid", subschemeid);
					}
				} else if (value.equals("boundary")) {
					if (boundaryid == null || boundaryid == 0)
						throw new ValidationException(EMPTY_STRING, "Boundary is required");
					else {
						query.append(" and bd.boundary=:boundaryId");
						params.put("boundaryId", boundaryid.longValue());
					}
				} else
					throw new ValidationException(EMPTY_STRING,
							"budgetaryCheck_groupby_values is not matching=" + value);
		}
		return " and bd.budget.status.description='Approved' and bd.status.description='Approved'  " + query;
	}

	private boolean isBudgetCheckingRequiredForType(final String txnType, final String budgetingType) {
		if (FinancialConstants.BUDGETTYPE_DEBIT.equalsIgnoreCase(budgetingType)
				&& FinancialConstants.BUDGETTYPE_DEBIT.equals(txnType))
			return true;
		else if (FinancialConstants.BUDGETTYPE_CREDIT.equalsIgnoreCase(budgetingType)
				&& FinancialConstants.BUDGETTYPE_CREDIT.equals(txnType))
			return true;
		else if (FinancialConstants.BUDGETTYPE_ALL.equalsIgnoreCase(budgetingType))
			return true;
		else
			return false;
	}

	private List<BudgetGroup> getBudgetHeadByGlcode(CChartOfAccounts coa) {
		try {
			int majorcodelength = getConfigValue("coa_majorcode_length");
			int minorcodelength = getConfigValue("coa_minorcode_length");

			String glcode = coa.getGlcode();

			List<BudgetGroup> bgList = budgetGroupRepository.findByDetailCode(glcode);
			if (!bgList.isEmpty())
				return bgList;

			String minorCode = glcode.substring(0, minorcodelength);
			bgList = budgetGroupRepository.findByMinorCode(minorCode);
			if (!bgList.isEmpty())
				return bgList;

			String majorCode = glcode.substring(0, majorcodelength);
			bgList = budgetGroupRepository.findByMajorCode(majorCode);
			if (!bgList.isEmpty())
				return bgList;

			throw new ValidationException(EMPTY_STRING,
					"Budget Check failed: Budget not defined for the given combination.");

		} catch (ValidationException v) {
			List<ValidationError> errors = new ArrayList<>();
			errors.add(new ValidationError("exp", v.getErrors().get(0).getMessage()));
			LOGGER.error("Exception in getBudgetHeadByGlcode API=" + v.getMessage());
			throw new ValidationException(errors);
		}
	}

	private int getConfigValue(String key) {
		List<AppConfigValues> appList = appConfigValuesService.getConfigValuesByModuleAndKey(EGF, key);
		if (appList.isEmpty())
			throw new ValidationException(EMPTY_STRING, key + " is not defined");
		return Integer.parseInt(appList.get(0).getValue());
	}

}
