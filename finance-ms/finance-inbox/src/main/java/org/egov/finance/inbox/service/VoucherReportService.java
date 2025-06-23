/**
 * Created on Jun 17, 2025.
 * 
 * @author bdhal
 */
package org.egov.finance.inbox.service;

import java.io.IOException;
import java.io.InputStream;
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
import org.egov.finance.inbox.entity.AppConfigValues;
import org.egov.finance.inbox.entity.BudgetDetail;
import org.egov.finance.inbox.entity.BudgetGroup;
import org.egov.finance.inbox.entity.CChartOfAccounts;
import org.egov.finance.inbox.entity.CGeneralLedger;
import org.egov.finance.inbox.entity.CVoucherHeader;
import org.egov.finance.inbox.entity.Department;
import org.egov.finance.inbox.entity.EgBillSubType;
import org.egov.finance.inbox.entity.EgBilldetails;
import org.egov.finance.inbox.entity.EgBillregister;
import org.egov.finance.inbox.entity.EgBillregistermis;
import org.egov.finance.inbox.entity.FinancialYear;
import org.egov.finance.inbox.entity.Function;
import org.egov.finance.inbox.entity.Fund;
import org.egov.finance.inbox.entity.Vouchermis;
import org.egov.finance.inbox.exception.ApplicationRuntimeException;
import org.egov.finance.inbox.exception.ReportServiceException;
import org.egov.finance.inbox.model.BudgetReportEntry;
import org.egov.finance.inbox.model.VoucherReportModel;
import org.egov.finance.inbox.model.WorkFlowHistoryItem;
import org.egov.finance.inbox.model.request.VoucherPrintRequest;
import org.egov.finance.inbox.repository.BudgetGroupRepository;
import org.egov.finance.inbox.repository.CVoucherHeaderRepository;
import org.egov.finance.inbox.repository.EgBillregistermisRepository;
import org.egov.finance.inbox.repository.FunctionRepository;
import org.egov.finance.inbox.repository.FundRepository;
import org.egov.finance.inbox.util.BudgetReportQueryHelper;
import org.egov.finance.inbox.util.CommonUtils;
import org.egov.finance.inbox.util.ReportConstants;
import org.egov.finance.inbox.workflow.entity.State;
import org.egov.finance.inbox.workflow.entity.StateHistory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.JRException;

@Service
@Slf4j
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
	
	@Autowired
	ReportBuilder reportBuilder;
	
	@Autowired
	LedgerService ledgerService;

	public Resource voucherForReport(VoucherPrintRequest request) {
		List<Object> voucherList = new ArrayList<>();
		 Map<String, Object> paramMap = new HashMap<>();
		Map<String, String> errorMap = new HashMap<>();

		CVoucherHeader voucher = Optional.ofNullable(masterCommonService.getVoucherById(request.getVoucher().getId()))
				.orElseThrow(() -> {
					errorMap.put(ReportConstants.INVALID_ID_PASSED, ReportConstants.INVALID_ID_PASSED_MESSAGE);
					return new ReportServiceException(errorMap);
				});
		voucherList = generateVoucherReportList(voucher,voucherList);
		paramMap = populateParamMap(paramMap, voucher);
		String jasperpath = "/reports/templates/journalVoucherReport_new.jasper";
		try (InputStream pdfStream = reportBuilder.exportPdf(jasperpath, paramMap, voucherList)) {
	        return new InputStreamResource(pdfStream);
	    } catch (IOException | JRException e) {
	        log.error("Failed to generate voucher report: {}", e.getMessage(), e);
	        errorMap.put("REPORT_GENERATION_FAILED", "Unable to generate report.");
	        throw new ReportServiceException(errorMap);
	    }
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

	private Map<String, Object>  populateParamMap(Map<String, Object> paramMap, CVoucherHeader voucher) {
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
		//will be changed
		cityName.append("TEST");
		paramMap.put("cityName", cityName.toString());
		paramMap.put("voucherName", billType.toUpperCase().concat(" JOURNAL VOUCHER"));

		paramMap.put("budgetAppropriationDetailJasper",
				reportHelper.getClass().getResourceAsStream("/reports/templates/budgetAppropriationDetail.jasper"));
		if (billRegistermis != null && billRegistermis.getBudgetaryAppnumber() != null
				&& !"".equalsIgnoreCase(billRegistermis.getBudgetaryAppnumber()))
			 paramMap.put("budgetDetail",budgetService.getBudgetDetailsForBill(billRegistermis.getEgBillregister()));
			 else if (voucher != null && voucher.getVouchermis().getBudgetaryAppnumber() != null && !"".equalsIgnoreCase(voucher.getVouchermis().getBudgetaryAppnumber()))
			 paramMap.put("budgetDetail", budgetService.getBudgetDetailsForVoucher(voucher));
			else
			 paramMap.put("budgetDetail", new ArrayList<>());
			System.out.println("paramMap-------------------------" + paramMap);
			
			System.out.println(paramMap);
		 return paramMap;
	}
	
	
	private List<Object> generateVoucherReportList(CVoucherHeader voucher, List<Object> voucherList) {
        if (voucher != null) {
            for (final CGeneralLedger vd : voucher.getGeneralLedger())
                if (BigDecimal.ZERO.compareTo(BigDecimal.valueOf(vd.getCreditAmount().doubleValue())) == 0) {
                	VoucherReportModel v =  new VoucherReportModel();
	            	v.setGeneralLedger(ledgerService.getLedgersByVoucherIdAndGlcodeAndLineId(voucher.getId(),
	            					vd.getGlcode(), vd.getVoucherlineId()));
	            	voucherList.add(v);
                }

            for (final CGeneralLedger vd : voucher.getGeneralLedger())
                if (BigDecimal.ZERO.compareTo(BigDecimal.valueOf(vd.getDebitAmount().doubleValue())) == 0) {
                	VoucherReportModel v =  new VoucherReportModel();
	            	v.setGeneralLedger(ledgerService.getLedgersByVoucherIdAndGlcodeAndLineId(voucher.getId(),
	            					vd.getGlcode(), vd.getVoucherlineId()));
	            	voucherList.add(v);
                }
        }
        return voucherList;
    }

	
	


}