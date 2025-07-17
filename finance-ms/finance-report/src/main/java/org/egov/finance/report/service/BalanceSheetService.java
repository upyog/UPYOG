/**
 * @author bpattanayak
 * @date 30 Jun 2025
 */

package org.egov.finance.report.service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


import org.egov.finance.report.entity.AppConfigValues;
import org.egov.finance.report.entity.CVoucherHeader;
import org.egov.finance.report.entity.Department;
import org.egov.finance.report.entity.FinancialYear;
import org.egov.finance.report.entity.Fund;
import org.egov.finance.report.exception.ApplicationRuntimeException;
import org.egov.finance.report.model.Statement;
import org.egov.finance.report.repository.AppConfigValuesRepository;
import org.egov.finance.report.repository.CVoucherHeaderRepository;
import org.egov.finance.report.repository.DepartmentRepository;
import org.egov.finance.report.util.CommonUtils;
import org.egov.finance.report.util.Constants;
import org.egov.finance.report.util.FinancialConstants;
import org.egov.finance.report.util.MessageConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.egov.finance.report.entity.Function;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

@Service
public class BalanceSheetService {

	private Date todayDate;
	int minorCodeLength;
	private static final String BS = "BS";
	private static final String L = "L";
	private static final BigDecimal NEGATIVE = new BigDecimal(-1);
	private String removeEntrysWithZeroAmount = "";
	List<Character> coaType = new ArrayList<Character>();
	Statement balanceSheet = new Statement();
	private StringBuffer header = new StringBuffer();
	public static final Locale LOCALE = new Locale("en", "IN");
	public static final SimpleDateFormat DDMMYYYYFORMATS = new SimpleDateFormat("dd/MM/yyyy", LOCALE);
	
	@Autowired
	AppConfigValuesRepository appConfigValuesRepository;
	
	@Autowired
	ReportService reportService;
	
	@Autowired
	DepartmentRepository departmentRepository;
	
	@PersistenceContext
	private EntityManager entityManager;
	
	@Autowired
	CommonUtils commonUtils;
	
	@Autowired
	MessageSource messageSource;
	
	@Autowired
	MasterCommonService masterCommonService;
	

	public void populateBalanceSheet(final Statement balanceSheet) {
		try {
			final List<AppConfigValues> configValues = masterCommonService.getConfigValuesByModuleAndKey(
					FinancialConstants.MODULE_NAME_APPCONFIG,
					FinancialConstants.REMOVE_ENTRIES_WITH_ZERO_AMOUNT_IN_REPORT);

			for (final AppConfigValues appConfigVal : configValues)
				removeEntrysWithZeroAmount = appConfigVal.getValue();
		} catch (final ApplicationRuntimeException e) {
			throw new ApplicationRuntimeException(
					"Appconfig value for remove entries with zero amount in report is not defined in the system");
		}
		//minorCodeLength = Integer.valueOf(appConfigValuesRepository.findByConfig_KeyNameAndConfig_Module_Name(Constants.EGF, "coa_minorcode_length").get(0).getValue());
		minorCodeLength=Integer.valueOf(masterCommonService.getConfigValuesByModuleAndKey(Constants.EGF,"coa_minorcode_length").get(0).getValue());
		coaType.add('A');
		coaType.add('L');
		final Date fromDate = reportService.getFromDate(balanceSheet);
		final Date toDate = reportService.getToDate(balanceSheet);
		String voucherStatusToExclude = masterCommonService.getConfigValuesByModuleAndKey("EGF", "statusexcludeReport").get(0).getValue();
		final List<Fund> fundList = balanceSheet.getFundList();
		Map<String, Object> filterQueryParams = new HashMap<>();
		final String filterQuery =reportService.getFilterQuery(balanceSheet, filterQueryParams);
		reportService.populateCurrentYearAmountPerFund(balanceSheet, fundList, filterQuery, toDate, fromDate, BS, filterQueryParams);
		reportService.populatePreviousYearTotals(balanceSheet, filterQuery, toDate, fromDate, BS, "'L','A'", filterQueryParams);
		Map<String, Object> params = new HashMap<>();
		reportService.addCurrentOpeningBalancePerFund(balanceSheet, fundList, reportService.getTransactionQuery(balanceSheet, params), params);
		params = new HashMap<>();
		reportService.addOpeningBalancePrevYear(balanceSheet, reportService.getTransactionQuery(balanceSheet, params), fromDate, params);
		final String glCodeForExcessIE =reportService.getGlcodeForPurposeCode(7);// purpose is ExcessIE
		reportService.addExcessIEForCurrentYear(balanceSheet, fundList, glCodeForExcessIE, filterQuery, toDate, fromDate,
				filterQueryParams);
		reportService.addExcessIEForPreviousYear(balanceSheet, fundList, glCodeForExcessIE, filterQuery, toDate, fromDate,
				filterQueryParams);
		reportService.computeCurrentYearTotals(balanceSheet, Constants.LIABILITIES, Constants.ASSETS);
		reportService.populateSchedule(balanceSheet, BS);
		reportService.removeFundsWithNoData(balanceSheet);
		reportService.groupBySubSchedule(balanceSheet);
		reportService.computeTotalAssetsAndLiabilities(balanceSheet);
		if (removeEntrysWithZeroAmount.equalsIgnoreCase("Yes"))
			reportService.removeEntriesWithZeroAmount(balanceSheet);
	}
	
	protected void setRelatedEntitiesOn() {
	    setTodayDate(new Date());

	    if (balanceSheet.getFinancialYear() != null && balanceSheet.getFinancialYear().getId() != null) {
	        FinancialYear fy = entityManager.find(FinancialYear.class, balanceSheet.getFinancialYear().getId());
	        balanceSheet.setFinancialYear(fy);
	    }

	    if (balanceSheet.getDepartment() != null &&
	        balanceSheet.getDepartment().getCode() != null &&
	        !"null".equalsIgnoreCase(balanceSheet.getDepartment().getCode()) &&
	        !balanceSheet.getDepartment().getCode().isEmpty()) {

	        Department dept = commonUtils.getDepartmentByCode(balanceSheet.getDepartment().getCode());
	        balanceSheet.setDepartment(dept);
	        header.append(" in ").append(dept.getName());
	    } else {
	        balanceSheet.setDepartment(null);
	    }

	    if (balanceSheet.getFund() != null && balanceSheet.getFund().getId() != null && balanceSheet.getFund().getId() != 0) {
	        Fund fund = entityManager.find(Fund.class, balanceSheet.getFund().getId());
	        balanceSheet.setFund(fund);
	        header.append(" for ").append(fund.getName());
	    }

	    if (balanceSheet.getFunction() != null && balanceSheet.getFunction().getId() != null && balanceSheet.getFunction().getId() != 0) {
	        Function function = entityManager.find(Function.class, balanceSheet.getFunction().getId());
	        balanceSheet.setFunction(function);
	        header.append(" for ").append(function.getName());
	    }

	    if (balanceSheet.getAsOndate() != null) {
	        header.append(" as on ").append(DDMMYYYYFORMATS.format(balanceSheet.getAsOndate()));
	    }
	}
	
	public void setTodayDate(final Date todayDate) {
        this.todayDate = todayDate;
    }
	
	public List<Fund> getFunds() {
	    CriteriaBuilder cb = entityManager.getCriteriaBuilder();

	    // Step 1: Get distinct fund IDs from CVoucherHeader
	    CriteriaQuery<Long> fundIdQuery = cb.createQuery(Long.class);
	    Root<CVoucherHeader> voucherRoot = fundIdQuery.from(CVoucherHeader.class);
	    fundIdQuery.select(voucherRoot.get("fundId").get("id")).distinct(true);

	    List<Long> fundIds = entityManager.createQuery(fundIdQuery).getResultList();

	    // Step 2: Fetch Fund entities by those IDs
	    if (!fundIds.isEmpty()) {
	        CriteriaQuery<Fund> fundQuery = cb.createQuery(Fund.class);
	        Root<Fund> fundRoot = fundQuery.from(Fund.class);
	        fundQuery.select(fundRoot).where(fundRoot.get("id").in(fundIds));

	        return entityManager.createQuery(fundQuery).getResultList();
	    }

	    return new ArrayList<>();
	}
	
	public String getCurrentYearToDate(Statement balanceSheet) {
        final SimpleDateFormat formatter = Constants.DDMMYYYYFORMAT1;
        return formatter.format(reportService.getToDate(balanceSheet));
    }
	
	public String getPreviousYearToDate() {
		final SimpleDateFormat formatter = Constants.DDMMYYYYFORMAT1;
        return formatter.format(reportService.getPreviousYearFor(todayDate));
    }
	
	public String getMessage(String message) {
        return messageSource.getMessage(message, null, LOCALE);
    }
	

}
