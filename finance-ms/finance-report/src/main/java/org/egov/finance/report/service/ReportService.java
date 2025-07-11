/**
 * @author bpattanayak
 * @date 30 Jun 2025
 */

package org.egov.finance.report.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.egov.finance.report.util.Constants;
import org.hibernate.HibernateException;
import org.egov.finance.report.entity.AppConfigValues;
import org.egov.finance.report.entity.CChartOfAccounts;
import org.egov.finance.report.entity.CGeneralLedger;
import org.egov.finance.report.entity.CVoucherHeader;
import org.egov.finance.report.entity.FinancialYear;
import org.egov.finance.report.entity.Fund;
import org.egov.finance.report.entity.ScheduleMapping;
import org.egov.finance.report.entity.TransactionSummary;
import org.egov.finance.report.entity.Vouchermis;
import org.egov.finance.report.model.Statement;
import org.egov.finance.report.model.StatementEntry;
import org.egov.finance.report.model.StatementResultObject;
import org.egov.finance.report.repository.AppConfigValuesRepository;
import org.egov.finance.report.repository.ChartOfAccountsRepository;
import org.egov.finance.report.repository.FinancialYearRepository;
import org.egov.finance.report.repository.FundRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Tuple;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import jakarta.persistence.criteria.Predicate;

@Service
public class ReportService {

	private static final String BS = "BS";

	@Autowired
	FinancialYearRepository financialYearRepository;

	@Autowired
	AppConfigValuesRepository appConfigValuesRepository;

	@Autowired
	FundRepository fundRepository;

	@Autowired
	ChartOfAccountsRepository chartOfAccountsRepository;
	
	@Autowired
	MasterCommonService masterCommonService;

	@PersistenceContext
	private EntityManager entityManager;

	// final static Logger LOGGER = Logger.getLogger(ReportService.class);

	public Date getFromDate(final Statement statement) {
		FinancialYear financialYear = null;

		if ("Date".equalsIgnoreCase(statement.getPeriod()) && statement.getAsOndate() != null) {
			Long financialYearId = financialYearRepository.findFinancialYearIdByDate(statement.getAsOndate());

			if (financialYearId != null) {
				financialYear = financialYearRepository.findById(financialYearId).orElse(null);
				if (financialYear != null) {
					statement.setFinancialYear(financialYear);
				}
			}
		} else {
			financialYear = statement.getFinancialYear();
		}

		return financialYear != null ? financialYear.getStartingDate() : null;
	}

	public Date getToDate(final Statement statement) {
		if ("Date".equalsIgnoreCase(statement.getPeriod()) && statement.getAsOndate() != null) {
			return statement.getAsOndate();
		}

		if ("Half Yearly".equalsIgnoreCase(statement.getPeriod())) {
			// Fetch config values safely
			List<AppConfigValues> configValues = appConfigValuesRepository
					.findByConfig_KeyNameAndConfig_Module_Name("EGF", "bs_report_half_yearly");

			if (configValues != null && !configValues.isEmpty()) {
				String configValue = configValues.get(0).getValue(); // Assuming first value is used

				if (configValue != null && statement.getFinancialYear() != null
						&& statement.getFinancialYear().getStartingDate() != null) {

					String[] halfYearComponents = configValue.split("/");

					if (halfYearComponents.length == 2) {
						try {
							int day = Integer.parseInt(halfYearComponents[0]);
							int month = Integer.parseInt(halfYearComponents[1]) - 1; // Calendar months are 0-based

							Calendar fin = Calendar.getInstance();
							fin.setTime(statement.getFinancialYear().getStartingDate());

							Calendar calendar = Calendar.getInstance();
							calendar.set(fin.get(Calendar.YEAR), month, day);
							return calendar.getTime();
						} catch (NumberFormatException e) {
							System.err.println("Invalid half-yearly config format: " + configValue);
						}
					}
				}
			}
		}

		return (statement.getFinancialYear() != null) ? statement.getFinancialYear().getEndingDate() : null;
	}

	protected String getFilterQuery(final Statement balanceSheet, Map<String, Object> params) {
		final StringBuilder query = new StringBuilder();
		if (balanceSheet.getDepartment() != null && balanceSheet.getDepartment().getId() != null
				&& balanceSheet.getDepartment().getId() != 0) {
			query.append(" and mis.departmentid=:departmentid");
			params.put("departmentid", balanceSheet.getDepartment().getId().toString());
		}
		if (balanceSheet.getFunction() != null && balanceSheet.getFunction().getId() != null
				&& balanceSheet.getFunction().getId() != 0) {
			query.append(" and g.functionid=:functionid");
			params.put("functionid", balanceSheet.getFunction().getId());
		}
		if (balanceSheet.getFund() != null && balanceSheet.getFund().getId() != null
				&& balanceSheet.getFund().getId() != 0) {
			query.append(" and v.fundid=:fundid");
			params.put("fundid", balanceSheet.getFund().getId());
		}
		return query.toString();
	}

	public void populateCurrentYearAmountPerFund(final Statement statement, final List<Fund> fundList,
			final String filterQuery, final Date toDate, final Date fromDate, final String scheduleReportType,
			Map<String, Object> params) {
		final Statement assets = new Statement();
		final Statement liabilities = new Statement();
		final BigDecimal divisor = statement.getDivisor();
		final List<StatementResultObject> allGlCodes = getAllGlCodesFor(scheduleReportType);
		final List<StatementResultObject> results = getTransactionAmount(toDate, fromDate, "'L','A'", "BS", params);
		// if (LOGGER.isDebugEnabled())
		// LOGGER.debug("row.getGlCode()--row.getFundId()--row.getAmount()--row.getBudgetAmount()");
		for (final StatementResultObject queryObject : allGlCodes) {
			if (queryObject.getGlCode() == null)
				queryObject.setGlCode("");
			final List<StatementResultObject> rows = getRowWithGlCode(results, queryObject.getGlCode());

			if (rows.isEmpty()) {
				if (queryObject.isLiability())
					liabilities.add(new StatementEntry(queryObject.getGlCode(), queryObject.getScheduleName(),
							queryObject.getScheduleNumber(), scheduleReportType, BigDecimal.ZERO, BigDecimal.ZERO, null,
							false));
				else
					assets.add(new StatementEntry(queryObject.getGlCode(), queryObject.getScheduleName(),
							queryObject.getScheduleNumber(), scheduleReportType, BigDecimal.ZERO, BigDecimal.ZERO, null,
							false));
			} else
				for (final StatementResultObject row : rows) {
					// if (LOGGER.isDebugEnabled())
					// LOGGER.debug(row.getGlCode() + "--" + row.getFundId() + "--" +
					// row.getAmount() + "--"
					// + row.getBudgetAmount());
					if (row.isLiability())
						row.negateAmount();
					if (liabilities.containsBalanceSheetEntry(row.getGlCode())
							|| assets.containsBalanceSheetEntry(row.getGlCode())) {
						if (row.isLiability())
							addFundAmount(fundList, liabilities, divisor, row);
						else
							addFundAmount(fundList, assets, divisor, row);
					} else {
						final StatementEntry balanceSheetEntry = new StatementEntry();
						if (row.getAmount() != null && row.getFundId() != null)
							balanceSheetEntry.getFundWiseAmount().put(getFundNameForId(row.getFundId()),
									divideAndRound(row.getAmount(), divisor));
						if (queryObject.getGlCode() != null) {
							balanceSheetEntry.setGlCode(queryObject.getGlCode());
							balanceSheetEntry.setAccountName(queryObject.getScheduleName());
							balanceSheetEntry.setScheduleNo(queryObject.getScheduleNumber());
						}

						if (row.isLiability())
							liabilities.add(balanceSheetEntry);
						else
							assets.add(balanceSheetEntry);
					}
				}
		}
		addRowsToStatement(statement, assets, liabilities);
	}

	public void populatePreviousYearTotals(final Statement balanceSheet, final String filterQuery, final Date toDate,
			final Date fromDate, final String reportSubType, final String coaType, Map<String, Object> params) {
		final boolean newbalanceSheet = balanceSheet.size() > 2 ? false : true;
		final BigDecimal divisor = balanceSheet.getDivisor();
		final Statement assets = new Statement();
		final Statement liabilities = new Statement();
		Date formattedToDate;
		final Calendar cal = Calendar.getInstance();

		if ("Yearly".equalsIgnoreCase(balanceSheet.getPeriod())) {
			cal.setTime(fromDate);
			cal.add(Calendar.DATE, -1);
			formattedToDate = cal.getTime();
		} else
			formattedToDate = getPreviousYearFor(toDate);
		final List<StatementResultObject> results = getTransactionAmount(formattedToDate, getPreviousYearFor(fromDate),
				coaType, reportSubType, params);
		for (final StatementResultObject row : results)
			if (balanceSheet.containsBalanceSheetEntry(row.getGlCode())) {
				for (int index = 0; index < balanceSheet.size(); index++)
					if (balanceSheet.get(index).getGlCode() != null
							&& row.getGlCode().equals(balanceSheet.get(index).getGlCode())) {
						if (row.isLiability())
							row.negateAmount();
						BigDecimal prevYrTotal = balanceSheet.get(index).getPreviousYearTotal();
						prevYrTotal = prevYrTotal == null ? BigDecimal.ZERO : prevYrTotal;
						balanceSheet.get(index)
								.setPreviousYearTotal(prevYrTotal.add(divideAndRound(row.getAmount(), divisor)));
					}
			} else {
				if (row.isLiability())
					row.negateAmount();
				final StatementEntry balanceSheetEntry = new StatementEntry();
				if (row.getAmount() != null && row.getFundId() != null) {
					balanceSheetEntry.setPreviousYearTotal(divideAndRound(row.getAmount(), divisor));
					balanceSheetEntry.setCurrentYearTotal(BigDecimal.ZERO);
				}
				if (row.getGlCode() != null)
					balanceSheetEntry.setGlCode(row.getGlCode());
				if (row.isLiability())
					liabilities.add(balanceSheetEntry);
				else
					assets.add(balanceSheetEntry);
			}
		if (newbalanceSheet)
			addRowsToStatement(balanceSheet, assets, liabilities);
	}

	public List<StatementResultObject> getAllGlCodesFor(String scheduleReportType) {
		try {
			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<Tuple> cq = cb.createTupleQuery();
			Root<CChartOfAccounts> coa = cq.from(CChartOfAccounts.class);
			Join<CChartOfAccounts, ScheduleMapping> schedule = coa.join("schedule");

			cq.multiselect(coa.get("majorCode").alias("glCode"), schedule.get("schedule").alias("scheduleNumber"),
					schedule.get("scheduleName").alias("scheduleName"), coa.get("type").alias("type")).distinct(true)
					.where(cb.and(cb.equal(coa.get("classification"), 2),
							cb.equal(schedule.get("reportType"), scheduleReportType)))
					.orderBy(cb.asc(coa.get("majorCode")));

			List<Tuple> results = entityManager.createQuery(cq).getResultList();

			return results.stream().map(tuple -> {
				StatementResultObject obj = new StatementResultObject();
				obj.setGlCode(tuple.get("glCode", String.class));
				obj.setScheduleNumber(tuple.get("scheduleNumber", String.class));
				obj.setScheduleName(tuple.get("scheduleName", String.class));
				obj.setType(tuple.get("type", Character.class));
				return obj;
			}).collect(Collectors.toList());

		} catch (Exception e) {
			return Collections.emptyList();
		}
	}

	public List<StatementResultObject> getTransactionAmount(Date toDate, Date fromDate, String coaType,
			String subReportType, Map<String, Object> params) {

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Tuple> cq = cb.createTupleQuery();

		Root<CGeneralLedger> gl = cq.from(CGeneralLedger.class);
		Join<CGeneralLedger, CChartOfAccounts> coa = gl.join("glcodeId");
		Join<CGeneralLedger, CVoucherHeader> vh = gl.join("voucherHeaderId");
		Join<CVoucherHeader, Vouchermis> mis = vh.join("vouchermis");

		// Subquery for GL code prefix match
		Subquery<String> glCodeSubquery = cq.subquery(String.class);
		Root<CChartOfAccounts> coa2 = glCodeSubquery.from(CChartOfAccounts.class);
		Join<CChartOfAccounts, ScheduleMapping> schedule = coa2.join("schedule");

		glCodeSubquery.select(coa2.get("glcode")).distinct(true).where(
				cb.and(cb.equal(coa2.get("classification"), 2), cb.equal(schedule.get("reportType"), subReportType)));

		// Expression for amount calculation
		Expression<BigDecimal> amountExpr = cb.diff(cb.sum(gl.get("debitAmount")), cb.sum(gl.get("creditAmount")));

		cq.multiselect(coa.get("majorCode").alias("glCode"), vh.get("fundId").get("id").alias("fundId"),
				coa.get("type").alias("type"), amountExpr.alias("amount"));

		// Prepare predicates
		List<Predicate> predicates = new ArrayList<>();

		List<String> coaTypes = getCoaTypes(coaType);
		String voucherStatusToExclude = masterCommonService.getConfigValuesByModuleAndKey("EGF", "statusexcludeReport").get(0).getValue();
		List<String> excludedStatuses = getStatuses(voucherStatusToExclude);

		int minorCodeLength = 4; // Replace with actual value or pass as parameter

		predicates.add(coa.get("type").in(coaTypes));
		predicates.add(cb.not(vh.get("status").in(excludedStatuses)));
		predicates.add(cb.lessThanOrEqualTo(vh.get("voucherDate"), toDate));
		predicates.add(cb.greaterThanOrEqualTo(vh.get("voucherDate"), fromDate));
		predicates.add(cb.substring(coa.get("glcode"), 0, minorCodeLength).in(glCodeSubquery));

		// Dynamic filters from params (equivalent to filterQuery)
		if (params.containsKey("departmentid")) {
			predicates.add(cb.equal(mis.get("department").get("id"), params.get("departmentid")));
		}
		if (params.containsKey("functionid")) {
			predicates.add(cb.equal(gl.get("function").get("id"), params.get("functionid")));
		}
		if (params.containsKey("fundid")) {
			predicates.add(cb.equal(vh.get("fund").get("id"), params.get("fundid")));
		}

		cq.where(cb.and(predicates.toArray(new Predicate[0])));
		cq.groupBy(coa.get("majorCode"), vh.get("fundId").get("id"), coa.get("type"));
		cq.orderBy(cb.asc(coa.get("majorCode")));

		List<Tuple> results = entityManager.createQuery(cq).getResultList();

		return results.stream().map(tuple -> {
			StatementResultObject obj = new StatementResultObject();
			obj.setGlCode(tuple.get("glCode", String.class));
			obj.setFundId(tuple.get("fundId", Long.class));
			obj.setType(tuple.get("type", Character.class));
			obj.setAmount(tuple.get("amount", BigDecimal.class));
			return obj;
		}).collect(Collectors.toList());
	}

	public List<String> getStatuses(final String status) {
		return Arrays.stream(status.split(",")).map(String::trim).collect(Collectors.toList());
	}

	public List<String> getCoaTypes(final String coaType) {
		return Arrays.stream(coaType.split(",")).map(String::trim).collect(Collectors.toList());
	}

	public List<StatementResultObject> getRowWithGlCode(final List<StatementResultObject> results,
			final String glCode) {

		return results.stream().filter(
				obj -> glCode.equalsIgnoreCase(obj.getGlCode()) && obj.getAmount().compareTo(BigDecimal.ZERO) != 0)
				.collect(Collectors.toList());
	}

	public void addFundAmount(final List<Fund> fundList, final Statement type, final BigDecimal divisor,
			final StatementResultObject row) {
		final BigDecimal amount = divideAndRound(row.getAmount(), divisor);
		final String fundName = getFundNameForId(row.getFundId());

		type.getEntries().stream()
				.filter(entry -> entry.getGlCode() != null && entry.getGlCode().equals(row.getGlCode()))
				.forEach(entry -> entry.getFundWiseAmount().put(fundName, amount));
	}

	public BigDecimal divideAndRound(BigDecimal value, final BigDecimal divisor) {
		return value.divide(divisor, 2, RoundingMode.HALF_UP);
	}

	public String getFundNameForId(final Long id) {
		return fundRepository.findById(id).map(Fund::getName).orElse("");
	}

	protected void addRowsToStatement(final Statement balanceSheet, final Statement assets,
			final Statement liabilities) {
		if (liabilities.size() > 0) {
			balanceSheet.add(new StatementEntry(null, Constants.LIABILITIES, "", null, null, null, null, true));
			balanceSheet.addAll(liabilities);
			balanceSheet.add(new StatementEntry(null, Constants.TOTAL_LIABILITIES, "", null, null, null, null, true));
		}
		if (assets.size() > 0) {
			balanceSheet.add(new StatementEntry(null, Constants.ASSETS, "", null, null, null, null, true));
			balanceSheet.addAll(assets);
			balanceSheet.add(new StatementEntry(null, Constants.TOTAL_ASSETS, "", null, null, null, null, true));
		}
	}

	public Date getPreviousYearFor(final Date date) {
		LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

		LocalDate previousYearDate = localDate.minusYears(1);

		return Date.from(previousYearDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
	}

	protected String getTransactionQuery(final Statement balanceSheet, Map<String, Object> params) {
		final StringBuilder query = new StringBuilder();
		if (balanceSheet.getDepartment() != null && balanceSheet.getDepartment().getId() != null
				&& balanceSheet.getDepartment().getId() != 0) {
			query.append(" and ts.departmentid=:tsDepartmentid");
			params.put("tsDepartmentid", balanceSheet.getDepartment().getId().toString());
		}
		if (balanceSheet.getFunction() != null && balanceSheet.getFunction().getId() != null
				&& balanceSheet.getFunction().getId() != 0) {
			query.append(" and ts.functionid=:tsFunctionid");
			params.put("tsFunctionid", balanceSheet.getFunction().getId());
		}
		if (balanceSheet.getFund() != null && balanceSheet.getFund().getId() != null
				&& balanceSheet.getFund().getId() != 0) {
			query.append(" and ts.fundid=:tsFundid");
			params.put("tsFundid", balanceSheet.getFund().getId());
		}
		return query.toString();
	}

	public void addCurrentOpeningBalancePerFund(final Statement balanceSheet, final List<Fund> fundList,
			final String transactionQuery, Map<String, Object> params) {
		final BigDecimal divisor = balanceSheet.getDivisor();

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
		Root<TransactionSummary> ts = cq.from(TransactionSummary.class);
		Join<TransactionSummary, CChartOfAccounts> coa = ts.join("glcodeid");

		cq.multiselect(cb.diff(cb.sum(ts.get("openingdebitbalance")), cb.sum(ts.get("openingcreditbalance"))),
				ts.get("fund"), coa.get("majorCode"), coa.get("type"));

		List<Predicate> predicates = new ArrayList<>();
		predicates.add(cb.equal(ts.get("financialyear").get("id"), balanceSheet.getFinancialYear().getId()));

		cq.where(cb.and(predicates.toArray(new Predicate[0])));
		cq.groupBy(ts.get("fund"), coa.get("majorCode"), coa.get("type"));

		List<Object[]> results = entityManager.createQuery(cq).getResultList();

		results.stream().filter(obj -> obj[0] != null && obj[1] != null).forEach(obj -> {
			BigDecimal total = (BigDecimal) obj[0];
			Long fundId = (Long) obj[1];
			String glCode = (String) obj[2];
			String coaType = (String) obj[3];

			if ("L".equals(coaType)) {
				total = total.multiply(BigDecimal.valueOf(-1));
			}

			BigDecimal amount = divideAndRound(total, divisor);
			String fundName = getFundNameForId(fundId);

			balanceSheet.getEntries().stream().filter(entry -> glCode.equals(entry.getGlCode()))
					.forEach(entry -> entry.getFundWiseAmount().merge(fundName, amount, BigDecimal::add));
		});
	}

	public void addOpeningBalancePrevYear(final Statement balanceSheet, final String transactionQuery,
			final Date fromDate, Map<String, Object> params) {
		try {
			final BigDecimal divisor = balanceSheet.getDivisor();
			final FinancialYear prevFinancialYr = getPreviousFinancialYearByDate(fromDate);
			final Integer prevFinancialYearId = Integer.valueOf(prevFinancialYr.getId().toString());

			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);

			Root<TransactionSummary> ts = cq.from(TransactionSummary.class);
			Join<TransactionSummary, CChartOfAccounts> coa = ts.join("glcodeid");

			Expression<BigDecimal> netOpeningBalance = cb.diff(cb.sum(ts.get("openingdebitbalance")),
					cb.sum(ts.get("openingcreditbalance")));

			cq.multiselect(netOpeningBalance, coa.get("majorCode"), coa.get("type"));

			List<Predicate> predicates = new ArrayList<>();
			predicates.add(cb.equal(ts.get("financialyear"), prevFinancialYearId));

// Add more predicates based on `transactionQuery` and `params` if needed

			cq.where(cb.and(predicates.toArray(new Predicate[0])));
			cq.groupBy(coa.get("majorCode"), coa.get("type"));

			List<Object[]> results = entityManager.createQuery(cq).getResultList();

			results.stream().filter(obj -> obj[0] != null && obj[1] != null).forEach(obj -> {
				BigDecimal total = (BigDecimal) obj[0];
				String glCode = obj[1].toString();
				String coaType = obj[2].toString();

				if ("L".equals(coaType)) {
					total = total.multiply(BigDecimal.valueOf(-1));
				}

				BigDecimal amount = divideAndRound(total, divisor);

				balanceSheet.getEntries().stream().filter(entry -> glCode.equals(entry.getGlCode())).forEach(entry -> {
					BigDecimal prevTotal = entry.getPreviousYearTotal();
					entry.setPreviousYearTotal((prevTotal == null ? BigDecimal.ZERO : prevTotal).add(amount));
				});
			});

		} catch (final HibernateException exp) {
// Consider logging the exception
		}
	}

	public FinancialYear getPreviousFinancialYearByDate(Date date) {
	    
	    return financialYearRepository.findByDateWithinRange(date)
	        .orElseThrow(() -> new EntityNotFoundException("Previous financial year not found for date: " + date));
	}


	public String getGlcodeForPurposeCode(final Integer purposeId) {
		return chartOfAccountsRepository.findMajorCodeByPurposeId(purposeId).orElse("");
	}

	public void addExcessIEForCurrentYear(final Statement balanceSheet, final List<Fund> fundList,
			final String glCodeForExcessIE, final String filterQuery, final Date toDate, final Date fromDate,
			Map<String, Object> params) {

		final BigDecimal divisor = balanceSheet.getDivisor();
		final List<String> excludedStatuses = getStatuses(masterCommonService.getConfigValuesByModuleAndKey("EGF", "statusexcludeReport").get(0).getValue());

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);

		Root<CGeneralLedger> gl = cq.from(CGeneralLedger.class);
		Join<CGeneralLedger, CVoucherHeader> vh = gl.join("voucherHeaderId");
		Join<CGeneralLedger, CChartOfAccounts> coa = gl.join("glcodeId");

		List<Predicate> predicates = new ArrayList<>();
		predicates.add(cb.between(vh.get("voucherDate"), fromDate, toDate));
		predicates.add(cb.not(vh.get("status").in(excludedStatuses)));
		predicates.add(coa.get("type").in("I", "E"));

// Optional department filter
		if (balanceSheet.getDepartment() != null && !"null".equals(balanceSheet.getDepartment().getCode())) {
			Root<Vouchermis> mis = cq.from(Vouchermis.class);
			predicates.add(cb.equal(mis.get("voucherHeader"), vh));
			predicates.add(cb.equal(mis.get("departmentCode"), balanceSheet.getDepartment().getCode()));
		}

		cq.multiselect(cb.diff(cb.sum(gl.get("creditAmount")), cb.sum(gl.get("debitAmount"))), vh.get("fundId"));

		cq.where(cb.and(predicates.toArray(new Predicate[0])));
		cq.groupBy(vh.get("fundId"));

		List<Object[]> results = entityManager.createQuery(cq).getResultList();

// Apply results to matching StatementEntry
		balanceSheet.getEntries().stream().filter(entry -> glCodeForExcessIE.equals(entry.getGlCode()))
				.forEach(entry -> {
					for (Object[] obj : results) {
						if (obj[0] != null && obj[1] != null) {
							BigDecimal amount = divideAndRound((BigDecimal) obj[0], divisor);
							String fundName = getFundNameForId(Long.valueOf(obj[1].toString()));
							entry.getFundWiseAmount().merge(fundName, amount, BigDecimal::add);
						}
					}
				});
	}

	public void addExcessIEForPreviousYear(final Statement balanceSheet, final List<Fund> fundList,
			final String glCodeForExcessIE, final String filterQuery, final Date toDate, final Date fromDate,
			Map<String, Object> params) {

		final BigDecimal divisor = balanceSheet.getDivisor();
		BigDecimal sum = BigDecimal.ZERO;
		Date formattedToDate;

		String voucherStatusToExclude = masterCommonService.getConfigValuesByModuleAndKey("EGF", "statusexcludeReport").get(0).getValue();

		if ("Yearly".equalsIgnoreCase(balanceSheet.getPeriod())) {
			final Calendar cal = Calendar.getInstance();
			cal.setTime(fromDate);
			cal.add(Calendar.DATE, -1);
			formattedToDate = cal.getTime();
		} else {
			formattedToDate = getPreviousYearFor(toDate);
		}

		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);

		Root<CVoucherHeader> voucher = cq.from(CVoucherHeader.class);
		Join<CVoucherHeader, CGeneralLedger> ledger = voucher.join("generalLedger");
		Join<CGeneralLedger, CChartOfAccounts> coa = ledger.join("glcodeId");

		List<Predicate> predicates = new ArrayList<>();

// Status exclusion
		predicates.add(cb.not(voucher.get("status").in(getStatuses(voucherStatusToExclude))));

// Date range
		predicates.add(cb.greaterThanOrEqualTo(voucher.get("voucherDate"), getPreviousYearFor(fromDate)));
		predicates.add(cb.lessThanOrEqualTo(voucher.get("voucherDate"), formattedToDate));

// GL code type
		predicates.add(coa.get("type").in("I", "E"));

// Optional department join
		if (balanceSheet.getDepartment() != null && !"null".equals(balanceSheet.getDepartment().getCode())) {
			voucher.join("voucherMis");
// Add department-specific predicates if needed
		}

// Apply additional filters from params if needed
// You can manually add predicates based on `params` here

// Select sum and group by
		Expression<BigDecimal> credit = ledger.get("creditAmount");
		Expression<BigDecimal> debit = ledger.get("debitAmount");
		Expression<Long> fundId = voucher.get("fundId");
		Expression<Long> functionId = ledger.get("functionId");

		cq.multiselect(cb.diff(cb.sum(credit), cb.sum(debit)), fundId);
		cq.where(predicates.toArray(new Predicate[0]));
		cq.groupBy(fundId, functionId);

		List<Object[]> results = entityManager.createQuery(cq).getResultList();

		for (Object[] obj : results) {
			sum = sum.add((BigDecimal) obj[0]);
		}

		for (int index = 0; index < balanceSheet.size(); index++) {
			if (balanceSheet.get(index).getGlCode() != null
					&& glCodeForExcessIE.equals(balanceSheet.get(index).getGlCode())) {

				BigDecimal prevYrTotal = balanceSheet.get(index).getPreviousYearTotal();
				prevYrTotal = prevYrTotal == null ? BigDecimal.ZERO : prevYrTotal;
				balanceSheet.get(index).setPreviousYearTotal(prevYrTotal.add(divideAndRound(sum, divisor)));
			}
		}
	}

	protected void computeCurrentYearTotals(final Statement statement, final String type1, final String type2) {
	    for (final StatementEntry entry : statement.getEntries()) {
	        if (type1.equals(entry.getAccountName()) || type2.equals(entry.getAccountName()) || entry.isDisplayBold()) {
	            continue;
	        }

	        BigDecimal currentYearTotal = BigDecimal.ZERO;
	        Map<String, BigDecimal> fundWiseAmount = entry.getFundWiseAmount();

	        if (fundWiseAmount != null) {
	            for (Map.Entry<String, BigDecimal> fundEntry : fundWiseAmount.entrySet()) {
	                currentYearTotal = currentYearTotal.add(fundEntry.getValue());
	            }
	        }

	        entry.setCurrentYearTotal(currentYearTotal);
	    }
	}


	protected void populateSchedule(final Statement statement, final String reportSubType) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);

		Root<CChartOfAccounts> coa = cq.from(CChartOfAccounts.class);
		Join<CChartOfAccounts, ScheduleMapping> schedule = coa.join("schedule"); // adjust field name if needed

		cq.multiselect(coa.get("majorCode"), schedule.get("scheduleName"), schedule.get("schedule"));

		List<Predicate> predicates = new ArrayList<>();
		predicates.add(cb.equal(schedule.get("reportType"), reportSubType));
		predicates.add(coa.get("type").in("A", "L"));

		cq.where(cb.and(predicates.toArray(new Predicate[0])));
		cq.groupBy(coa.get("majorCode"), schedule.get("scheduleName"), schedule.get("schedule"));
		cq.orderBy(cb.asc(coa.get("majorCode")));

		List<Object[]> results = entityManager.createQuery(cq).getResultList();

		for (Object[] obj : results) {
			String majorCode = obj[0] != null ? obj[0].toString() : "";
			String scheduleName = obj[1] != null ? obj[1].toString() : "";
			String scheduleNo = obj[2] != null ? obj[2].toString() : "";

			for (StatementEntry entry : statement.getEntries()) {
				if (entry.getGlCode() != null && entry.getGlCode().equals(majorCode)) {
					entry.setAccountName(scheduleName);
					entry.setScheduleNo(scheduleNo);
				}
			}
		}
	}

	void removeFundsWithNoData(final Statement statement) {
		final Map<String, Boolean> fundToBeRemoved = new HashMap<>();

		// Mark all funds for potential removal
		for (final Fund fund : statement.getFundList()) {
			fundToBeRemoved.put(fund.getName(), Boolean.TRUE);
		}

		// Check each entry for fund data
		for (final StatementEntry entry : statement.getEntries()) {
		    Map<String, BigDecimal> fundWiseAmount = entry.getFundWiseAmount();
		    if (fundWiseAmount != null) {
		        for (final String fundName : fundWiseAmount.keySet()) {
		            fundToBeRemoved.put(fundName, Boolean.FALSE);
		        }
		    }
		}


		// Remove funds with no data
		final Iterator<Fund> iterator = statement.getFundList().iterator();
		while (iterator.hasNext()) {
			final Fund fund = iterator.next();
			if (Boolean.TRUE.equals(fundToBeRemoved.get(fund.getName()))) {
				iterator.remove();
			}
		}
	}

	void groupBySubSchedule(final Statement balanceSheet) {
		List<StatementEntry> groupedEntries = new LinkedList<>();
		Map<String, String> scheduleNumberToNameMap = getSubSchedule(BS);
		Set<String> processedSchedules = new HashSet<>();

		BigDecimal previousYearTotal = BigDecimal.ZERO;
		BigDecimal currentYearTotal = BigDecimal.ZERO;
		Map<String, BigDecimal> fundWiseTotals = new HashMap<>();

		boolean lastEntryWasHeader = true;

		for (StatementEntry entry : balanceSheet.getEntries()) {
			String scheduleName = scheduleNumberToNameMap.get(entry.getScheduleNo());

			// Start a new group if this schedule hasn't been processed yet
			if (!processedSchedules.contains(scheduleName)) {
				if (!lastEntryWasHeader) {
					groupedEntries.add(createTotalEntry(previousYearTotal, currentYearTotal, fundWiseTotals));
					fundWiseTotals = new HashMap<>();
				}

				addTotalRowToPreviousGroup(groupedEntries, scheduleNumberToNameMap, entry);
				previousYearTotal = BigDecimal.ZERO;
				currentYearTotal = BigDecimal.ZERO;
				processedSchedules.add(scheduleName);
			}

			// Special case: TOTAL_LIABILITIES row triggers a total row
			if (Constants.TOTAL_LIABILITIES.equalsIgnoreCase(entry.getAccountName())) {
				groupedEntries.add(createTotalEntry(previousYearTotal, currentYearTotal, fundWiseTotals));
				fundWiseTotals = new HashMap<>();
			}

			groupedEntries.add(entry);
			addFundAmount(entry, fundWiseTotals);

			previousYearTotal = previousYearTotal.add(zeroOrValue(entry.getPreviousYearTotal()));
			currentYearTotal = currentYearTotal.add(zeroOrValue(entry.getCurrentYearTotal()));

			lastEntryWasHeader = entry.getGlCode() == null;

			if (Constants.TOTAL_LIABILITIES.equalsIgnoreCase(entry.getAccountName())) {
				previousYearTotal = BigDecimal.ZERO;
				currentYearTotal = BigDecimal.ZERO;
			}
		}

		// Add final total row
		groupedEntries.add(groupedEntries.size() - 1,
				createTotalEntry(previousYearTotal, currentYearTotal, fundWiseTotals));
		balanceSheet.setEntries(groupedEntries);
	}

	protected Map<String, String> getSubSchedule(final String subReportType) {
		Map<String, String> scheduleNumberToName = new HashMap<>();

		String sql = "SELECT s.schedule, sub.subschedulename " + "FROM egf_subschedule sub, schedulemapping s "
				+ "WHERE sub.reporttype = :reporttype AND sub.SUBSCHNAME = s.REPSUBTYPE";

		@SuppressWarnings("unchecked")
		List<Object[]> rows = entityManager.createNativeQuery(sql).setParameter("reporttype", subReportType)
				.getResultList();

		for (Object[] row : rows) {
			String scheduleNumber = row[0] != null ? row[0].toString() : "";
			String subScheduleName = row[1] != null ? row[1].toString() : "";
			scheduleNumberToName.put(scheduleNumber, subScheduleName);
		}

		return scheduleNumberToName;
	}

	private StatementEntry createTotalEntry(BigDecimal previousTotal, BigDecimal currentTotal,
			Map<String, BigDecimal> fundTotals) {
		StatementEntry totalEntry = new StatementEntry(null, Constants.TOTAL, "", null, previousTotal, currentTotal,
				fundTotals, true);
		totalEntry.setFundWiseAmount(fundTotals);
		return totalEntry;
	}

	void addTotalRowToPreviousGroup(final List<StatementEntry> list, final Map<String, String> schedueNumberToNameMap,
			final StatementEntry entry) {
		list.add(new StatementEntry("", schedueNumberToNameMap.get(entry.getScheduleNo()), "", null, null, null, null,
				true));
	}

	protected BigDecimal zeroOrValue(final BigDecimal value) {
		return value == null ? BigDecimal.ZERO : value;
	}

	void addFundAmount(final StatementEntry entry, final Map<String, BigDecimal> fundTotals) {
	    Map<String, BigDecimal> fundWiseAmount = entry.getFundWiseAmount();
	    if (fundWiseAmount != null) {
	        for (Map.Entry<String, BigDecimal> row : fundWiseAmount.entrySet()) {
	            String fundName = row.getKey();
	            BigDecimal amount = row.getValue();

	            fundTotals.merge(fundName, amount, BigDecimal::add);
	        }
	    }
	}


	void computeTotalAssetsAndLiabilities(final Statement balanceSheet) {
		BigDecimal currentYearTotal = BigDecimal.ZERO;
		BigDecimal previousYearTotal = BigDecimal.ZERO;

		for (StatementEntry entry : balanceSheet.getEntries()) {
			String accountName = entry.getAccountName();

			if (accountName == null || Constants.TOTAL.equalsIgnoreCase(accountName)
					|| Constants.LIABILITIES.equalsIgnoreCase(accountName)
					|| Constants.ASSETS.equalsIgnoreCase(accountName)) {
				continue;
			}

			if (Constants.TOTAL_LIABILITIES.equalsIgnoreCase(accountName)
					|| Constants.TOTAL_ASSETS.equalsIgnoreCase(accountName)) {

				entry.setCurrentYearTotal(currentYearTotal);
				entry.setPreviousYearTotal(previousYearTotal);

				currentYearTotal = BigDecimal.ZERO;
				previousYearTotal = BigDecimal.ZERO;
			} else {
				if (entry.getCurrentYearTotal() != null) {
					currentYearTotal = currentYearTotal.add(entry.getCurrentYearTotal());
				}
				if (entry.getPreviousYearTotal() != null) {
					previousYearTotal = previousYearTotal.add(entry.getPreviousYearTotal());
				}
			}
		}
	}

	void removeEntriesWithZeroAmount(final Statement balanceSheet) {
		List<StatementEntry> filteredEntries = new LinkedList<>();

		for (StatementEntry entry : balanceSheet.getEntries()) {
			String glCode = entry.getGlCode();

			// Keep entries with null or empty GL code
			if (glCode == null || glCode.isEmpty()) {
				filteredEntries.add(entry);
				continue;
			}

			Map<String, BigDecimal> fundWiseAmount = entry.getFundWiseAmount();

			// Keep entries with no fund-wise data
			if (fundWiseAmount == null || fundWiseAmount.isEmpty()) {
				filteredEntries.add(entry);
				continue;
			}

			boolean hasNonZeroAmount = fundWiseAmount.entrySet().stream()
					.anyMatch(e -> e.getValue().compareTo(BigDecimal.ZERO) != 0 || (entry.getPreviousYearTotal() != null
							&& entry.getPreviousYearTotal().compareTo(BigDecimal.ZERO) != 0));

			if (hasNonZeroAmount) {
				filteredEntries.add(entry);
			}
		}

		balanceSheet.setEntries(filteredEntries);
	}
	
}
