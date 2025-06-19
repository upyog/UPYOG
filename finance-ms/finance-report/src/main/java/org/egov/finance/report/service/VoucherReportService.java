/**
 * Created on Jun 17, 2025.
 * 
 * @author bdhal
 */
package org.egov.finance.report.service;

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

import org.egov.finance.report.entity.CVoucherHeader;
import org.egov.finance.report.entity.Department;
import org.egov.finance.report.entity.Fund;
import org.egov.finance.report.entity.Vouchermis;
import org.egov.finance.report.exception.ApplicationRuntimeException;
import org.egov.finance.report.exception.ReportServiceException;
import org.egov.finance.report.model.WorkFlowHistoryItem;
import org.egov.finance.report.model.request.VoucherPrintRequest;
import org.egov.finance.report.repository.CVoucherHeaderRepository;
import org.egov.finance.report.repository.FunctionRepository;
import org.egov.finance.report.repository.FundRepository;
import org.egov.finance.report.util.CommonUtils;
import org.egov.finance.report.util.ReportConstants;
import org.egov.finance.report.workflow.entity.StateHistory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
public class VoucherReportService {
	@Autowired
	JasperReportHelperService reportHelper;
	@Autowired
	FundRepository fundRepo;
	@Autowired
	FunctionRepository functionRepo;
	@Autowired
	CVoucherHeaderRepository voucherHeaderRepo;
	
	@Autowired
	CommonUtils commonUtils;
	
	@Autowired
	MasterCommonService masterCommonService;
	
	
	public Resource voucherForReport(VoucherPrintRequest request) {
		CVoucherHeader voucher =null;
		List<Object> voucherList = new ArrayList<>();
		final Map<String, Object> paramMap = new HashMap<>();
		Map<String, String> errorMap = new HashMap<>();
		voucher = voucherHeaderRepo.findById(request.getVoucher().getId()).orElseThrow(()->{
			errorMap.put(ReportConstants.INVALID_ID_PASSED, ReportConstants.INVALID_ID_PASSED_MESSAGE);
			throw new ReportServiceException(errorMap);
		});
		voucher.getGeneralLedger().forEach(x->System.out.println(x.getId()));
		populateParamMap(paramMap, voucher);
		
		return null;
	}
	
	
	private List<WorkFlowHistoryItem> loadInboxHistoryData(final CVoucherHeader voucher) throws ApplicationRuntimeException {
	    List<WorkFlowHistoryItem> inboxHistory = new ArrayList<>();
	    List<StateHistory> stateHistories = Optional.ofNullable(voucher.getStateHistory())
	                                                .orElse(Collections.emptyList());
	    Collections.reverse(stateHistories);

	    for (final StateHistory historyState : stateHistories) {
	        if (!"NEW".equalsIgnoreCase(historyState.getValue())) {
	            String pos = historyState.getSenderName() + " / " + historyState.getSenderName();
	            String nextAction = historyState.getNextAction();

	            String comment = Optional.ofNullable(historyState.getComments())
	                                     .map(commonUtils::removeSpecialCharacters)
	                                     .orElse("");

	            inboxHistory.add(new WorkFlowHistoryItem(
	                commonUtils.getFormattedDate(historyState.getCreatedDate(), "dd/MM/yyyy hh:mm a"),
	                pos,
	                nextAction,
	                historyState.getValue(),
	                comment
	            ));
	        }
	    }
	    Optional.ofNullable(voucher.getState()).ifPresent(state -> {
	        String pos = state.getSenderName() + " / " + state.getSenderName();
	        String nextAction = state.getNextAction();

	        String comment = Optional.ofNullable(state.getComments())
	                                 .map(commonUtils::removeSpecialCharacters)
	                                 .orElse("");

	        inboxHistory.add(new WorkFlowHistoryItem(
	            commonUtils.getFormattedDate(state.getCreatedDate(), "dd/MM/yyyy hh:mm a"),
	            pos,
	            nextAction,
	            state.getValue(),
	            comment
	        ));
	    });

	    return inboxHistory;
	}
	
	private void populateParamMap( Map<String, Object> paramMap,CVoucherHeader voucher ) {
		List<WorkFlowHistoryItem> inboxHistory = new ArrayList<>();
		paramMap.put("fundName",
			    Optional.ofNullable(voucher)
			    		.map(CVoucherHeader::getFundId)
			            .map(fund -> masterCommonService.getFundById(fund.getId()))
			            .map(Fund::getName)
			            .orElse("")
			);
        paramMap.put("departmentName", Optional.ofNullable(voucher)
        		.map(CVoucherHeader::getVouchermis)
        		.map(Vouchermis::getDepartmentcode)
        		.map(code-> masterCommonService.getDepartmenByCode(code))
        		.map(Department::getCode)
        		.orElse("")
        		);
        
        paramMap.put("voucherNumber", Optional.ofNullable(voucher)
        		.map(CVoucherHeader::getVoucherNumber)
        		.orElse("")
        		);
        
        paramMap.put("voucherNumber", Optional.ofNullable(voucher)
        		.map(CVoucherHeader::getVoucherDate)
        		.map(date->commonUtils.getFormattedDate(date, null))
        		.orElse("")
        		);
        
        paramMap.put("voucherDescription", Optional.ofNullable(voucher)
        		.map(CVoucherHeader::getDescription)
        		.orElse("")
        		);
      
       Optional.ofNullable(voucher)
       .map(CVoucherHeader::getState)
       .ifPresent(x->{
    	   loadInboxHistoryData(voucher);
       });
        paramMap.put("workFlowHistory", inboxHistory);
        paramMap.put("workFlowJasper",
                reportHelper.getClass().getResourceAsStream("/reports/templates/workFlowHistoryReport.jasper"));
        
       // final HttpServletRequest request = ServletActionContext.getRequest();
       // final HttpSession session = request.getSession();
		/*
		 * final City cityWebsite = cityService.getCityByURL((String)
		 * session.getAttribute("cityurl")); String billType =
		 * billsManager.getBillTypeforVoucher(voucher); if (isBlank(billType)) billType
		 * = "General"; else if ("Works".equalsIgnoreCase(billType)) billType =
		 * "Contractor"; if ("Purchase".equalsIgnoreCase(billType)) billType =
		 * billsManager.getBillSubTypeforVoucher(voucher); EgBillregistermis
		 * billRegistermis = null; if (voucher != null) billRegistermis =
		 * (EgBillregistermis) persistenceService
		 * .find("from EgBillregistermis where voucherHeader.id=?", voucher.getId());
		 * final StringBuilder cityName = new StringBuilder(100);
		 * cityName.append(cityWebsite.getName().toUpperCase());
		 * paramMap.put("cityName", cityName.toString()); paramMap.put("voucherName",
		 * billType.toUpperCase().concat(" JOURNAL VOUCHER"));
		 * paramMap.put("budgetAppropriationDetailJasper",
		 * reportHelper.getClass().getResourceAsStream(
		 * "/reports/templates/budgetAppropriationDetail.jasper")); if (billRegistermis
		 * != null && billRegistermis.getBudgetaryAppnumber() != null &&
		 * !"".equalsIgnoreCase(billRegistermis.getBudgetaryAppnumber()))
		 * paramMap.put("budgetDetail",
		 * budgetAppropriationService.getBudgetDetailsForBill(billRegistermis.
		 * getEgBillregister())); else if (voucher != null &&
		 * voucher.getVouchermis().getBudgetaryAppnumber() != null &&
		 * !"".equalsIgnoreCase(voucher.getVouchermis().getBudgetaryAppnumber()))
		 * paramMap.put("budgetDetail",
		 * budgetAppropriationService.getBudgetDetailsForVoucher(voucher)); else
		 * paramMap.put("budgetDetail", new ArrayList<>());
		 */
        //return paramMap;
    }

	
	
	
}