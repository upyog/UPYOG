/**
 * Created on Jun 17, 2025.
 * 
 * @author bdhal
 */
package org.egov.finance.report.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;

import org.apache.commons.lang3.StringUtils;
import org.egov.finance.report.entity.AppConfigValues;
import org.egov.finance.report.entity.BudgetDetail;
import org.egov.finance.report.entity.BudgetGroup;
import org.egov.finance.report.entity.CChartOfAccounts;
import org.egov.finance.report.entity.CVoucherHeader;
import org.egov.finance.report.entity.Department;
import org.egov.finance.report.entity.EgBillSubType;
import org.egov.finance.report.entity.EgBilldetails;
import org.egov.finance.report.entity.EgBillregister;
import org.egov.finance.report.entity.EgBillregistermis;
import org.egov.finance.report.entity.FinancialYear;
import org.egov.finance.report.entity.Function;
import org.egov.finance.report.entity.Fund;
import org.egov.finance.report.entity.Vouchermis;
import org.egov.finance.report.exception.ApplicationRuntimeException;
import org.egov.finance.report.exception.ReportServiceException;
import org.egov.finance.report.model.BudgetReportEntry;
import org.egov.finance.report.model.WorkFlowHistoryItem;
import org.egov.finance.report.model.request.VoucherPrintRequest;
import org.egov.finance.report.repository.BudgetGroupRepository;
import org.egov.finance.report.repository.CVoucherHeaderRepository;
import org.egov.finance.report.repository.EgBillregistermisRepository;
import org.egov.finance.report.repository.FunctionRepository;
import org.egov.finance.report.repository.FundRepository;
import org.egov.finance.report.util.CommonUtils;
import org.egov.finance.report.util.ReportConstants;
import org.egov.finance.report.util.BudgetReportQueryHelper;
import org.egov.finance.report.workflow.entity.State;
import org.egov.finance.report.workflow.entity.StateHistory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Service
public class VoucherReportService {
	private static final String EGF = "EGF";

	@Autowired
	JasperReportHelperService reportHelper;

	@Autowired
	CommonUtils commonUtils;
	@Autowired
	MasterCommonService masterCommonService;

	@Autowired
	CVoucherHeaderRepository voucherHeaderRepo;

	@Autowired
	BudgetGroupRepository budgetGroupRepository;

	@Autowired
	BudgetReportQueryHelper budgetReportQueryHelper;
	
	@Autowired
	BudgetService budgetService;

	public Resource voucherForReport(VoucherPrintRequest request) {
		List<Object> voucherList = new ArrayList<>();
		final Map<String, Object> paramMap = new HashMap<>();
		Map<String, String> errorMap = new HashMap<>();

		CVoucherHeader voucher = Optional.ofNullable(masterCommonService.getVoucherById(request.getVoucher().getId()))
				.orElseThrow(() -> {
					errorMap.put(ReportConstants.INVALID_ID_PASSED, ReportConstants.INVALID_ID_PASSED_MESSAGE);
					return new ReportServiceException(errorMap);
				});
		voucher.getGeneralLedger().forEach(x -> System.out.println(x.getId()));
		populateParamMap(paramMap, voucher);
		return null;
	}

	private List<WorkFlowHistoryItem> loadInboxHistoryData(final CVoucherHeader voucher)
			throws ApplicationRuntimeException {
		List<WorkFlowHistoryItem> inboxHistory = new ArrayList<>();
		List<StateHistory> stateHistories = Optional.ofNullable(voucher.getStateHistory())
				.orElse(Collections.emptyList());
		Collections.reverse(stateHistories);

		for (final StateHistory historyState : stateHistories) {
			if (!"NEW".equalsIgnoreCase(historyState.getValue())) {
				String pos = historyState.getSenderName() + " / " + historyState.getSenderName();
				String nextAction = historyState.getNextAction();

				String comment = Optional.ofNullable(historyState.getComments())
						.map(commonUtils::removeSpecialCharacters).orElse("");

				inboxHistory.add(new WorkFlowHistoryItem(
						commonUtils.getFormattedDate(historyState.getCreatedDate(), "dd/MM/yyyy hh:mm a"), pos,
						nextAction, historyState.getValue(), comment));
			}
		}
		Optional.ofNullable(voucher.getState()).ifPresent(state -> {
			String pos = state.getSenderName() + " / " + state.getSenderName();
			String nextAction = state.getNextAction();

			String comment = Optional.ofNullable(state.getComments()).map(commonUtils::removeSpecialCharacters)
					.orElse("");

			inboxHistory.add(
					new WorkFlowHistoryItem(commonUtils.getFormattedDate(state.getCreatedDate(), "dd/MM/yyyy hh:mm a"),
							pos, nextAction, state.getValue(), comment));
		});

		return inboxHistory;
	}

	private void populateParamMap(Map<String, Object> paramMap, CVoucherHeader voucher) {
		List<WorkFlowHistoryItem> inboxHistory = new ArrayList<>();
		paramMap.put("fundName", Optional.ofNullable(voucher).map(CVoucherHeader::getFundId)
				.map(fund -> masterCommonService.getFundById(fund.getId())).map(Fund::getName).orElse(""));
		paramMap.put("departmentName",
				Optional.ofNullable(voucher).map(CVoucherHeader::getVouchermis).map(Vouchermis::getDepartmentcode)
						.map(code -> masterCommonService.getDepartmenByCode(code)).map(Department::getCode).orElse(""));

		paramMap.put("voucherNumber", Optional.ofNullable(voucher).map(CVoucherHeader::getVoucherNumber).orElse(""));

		paramMap.put("voucherDate", Optional.ofNullable(voucher).map(CVoucherHeader::getVoucherDate)
				.map(date -> commonUtils.getFormattedDate(date, null)).orElse(""));

		paramMap.put("voucherDescription", Optional.ofNullable(voucher).map(CVoucherHeader::getDescription).orElse(""));

		Optional<State> optionalState = Optional.ofNullable(voucher).map(CVoucherHeader::getState);
		if (optionalState.isPresent()) {
			inboxHistory = loadInboxHistoryData(voucher);
		}
		paramMap.put("workFlowHistory", inboxHistory);
		paramMap.put("workFlowJasper",
				reportHelper.getClass().getResourceAsStream("/reports/templates/workFlowHistoryReport.jasper"));

		// final HttpServletRequest request = ServletActionContext.getRequest();
		// final HttpSession session = request.getSession();

		// final City cityWebsite = cityService.getCityByURL((String)
		// session.getAttribute("cityurl"));
		String billType = Optional.ofNullable(voucher).map(CVoucherHeader::getId)
				.map(id -> masterCommonService.getEgBillRegisterByVoucherId(id)).map(EgBillregister::getExpendituretype)
				.orElse(null);

		if (ObjectUtils.isEmpty(billType))
			billType = "General";
		else if ("Works".equalsIgnoreCase(billType))
			billType = "Contractor";
		if ("Purchase".equalsIgnoreCase(billType))
			billType = Optional.ofNullable(voucher).map(CVoucherHeader::getId)
					.map(id -> masterCommonService.getEgBillRegisterByVoucherId(id))
					.map(EgBillregister::getEgBillregistermis).map(EgBillregistermis::getEgBillSubType)
					.map(EgBillSubType::getExpenditureType).orElse(null);

		EgBillregistermis billRegistermis = Optional.ofNullable(voucher).map(CVoucherHeader::getId)
				.map(id -> masterCommonService.getEgBillregistermisByVoucherId(id)).orElse(null);

		final StringBuilder cityName = new StringBuilder(100);
		cityName.append("TEST");
		paramMap.put("cityName", cityName.toString());
		paramMap.put("voucherName", billType.toUpperCase().concat(" JOURNAL VOUCHER"));

		System.out.println("paramMap-------------------------" + paramMap);
		paramMap.put("budgetAppropriationDetailJasper",
				reportHelper.getClass().getResourceAsStream("/reports/templates/budgetAppropriationDetail.jasper"));
		if (billRegistermis != null && billRegistermis.getBudgetaryAppnumber() != null
				&& !"".equalsIgnoreCase(billRegistermis.getBudgetaryAppnumber()))
			// paramMap.put("budgetDetail",budgetAppropriationService.getBudgetDetailsForBill(billRegistermis.getEgBillregister()));
			// else if (voucher != null && voucher.getVouchermis().getBudgetaryAppnumber()
			// != null
			// && !"".equalsIgnoreCase(voucher.getVouchermis().getBudgetaryAppnumber()))
			// paramMap.put("budgetDetail",
			// budgetAppropriationService.getBudgetDetailsForVoucher(voucher));
			// else
			// paramMap.put("budgetDetail", new ArrayList<>());
			System.out.println("paramMap-------------------------" + paramMap);

		// return paramMap;
	}

	
	private boolean isBudgetCheckNeeded(final CChartOfAccounts coa) {
		boolean checkReq = false;
		if (!budgetControlTypeService.getConfigValue().equals(BudgetControlType.BudgetCheckOption.NONE.toString()))
			if (null != coa && null != coa.getBudgetCheckReq() && coa.getBudgetCheckReq())
				checkReq = true;
		return checkReq;
	}

	private void populateDataForBill(final EgBillregister bill, final FinancialYear financialYear,
			final EgBilldetails detail, final BudgetReportEntry budgetReportEntry, final CChartOfAccounts coa) {
		final Function function = getFunction(detail);
		final Map<String, Object> budgetDataMap = getRequiredBudgetDataForBill(bill, financialYear, function, coa);
		budgetReportEntry.setFunctionName(function.getName());
		budgetReportEntry.setGlCode(coa.getGlcode());
		budgetReportEntry.setFinancialYear(financialYear.getFinYearRange());
		BigDecimal budgetedAmtForYear = BigDecimal.ZERO;
		try {
			budgetedAmtForYear = budgetService.getBudgetedAmtForYear(budgetDataMap);
		} catch (final ValidationException e) {
			throw e;
		}
		budgetReportEntry.setBudgetedAmtForYear(budgetedAmtForYear);
		budgetService.populateBudgetAppNumber(bill, budgetReportEntry);
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
			final FinancialYear financialYear, final Function function, final CChartOfAccounts coa) {
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
				.ofNullable(masterCommonService.getConfigValuesByModuleAndKey(EGF, "coa_majorcode_length"))
				.filter(x -> x != null).map(list -> Integer.valueOf(list.get(0).getValue())).orElseThrow(() -> {
					errorMap.put(ReportConstants.FUNCTIONID, "coa_majorcode_length is not defined");
					throw new ReportServiceException(errorMap);
				});

		int minorCodeLength = Optional
				.ofNullable(masterCommonService.getConfigValuesByModuleAndKey(EGF, "coa_minorcode_length"))
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
	
	


}