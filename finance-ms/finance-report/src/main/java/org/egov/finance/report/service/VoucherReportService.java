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
import java.util.regex.Matcher;

import org.egov.finance.report.entity.CVoucherHeader;
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
	
	
	public Resource voucherForReport(VoucherPrintRequest request) {
		CVoucherHeader voucher =null;
		List<Object> voucherList = new ArrayList<>();
		Map<String, String> errorMap = new HashMap<>();
		voucher = voucherHeaderRepo.findById(request.getVoucher().getId()).orElseThrow(()->{
			errorMap.put(ReportConstants.INVALID_ID_PASSED, ReportConstants.INVALID_ID_PASSED_MESSAGE);
			throw new ReportServiceException(errorMap);
		});
		voucher.getGeneralLedger().forEach(x->System.out.println(x.getId()));
		return null;
	}
	
	
	private void loadInboxHistoryData(final CVoucherHeader voucher) throws ApplicationRuntimeException {
		List<WorkFlowHistoryItem> inboxHistory = new ArrayList();
        Collections.reverse(voucher.getStateHistory());
        WorkFlowHistoryItem inboxHistoryItem;
        String pos;
        String nextAction;

        for (final StateHistory historyState : voucher.getStateHistory()) {
            pos = historyState.getSenderName().concat(" / ").concat(historyState.getSenderName());
            nextAction = historyState.getNextAction();
            if (!"NEW".equalsIgnoreCase(historyState.getValue())) {
                inboxHistoryItem = new WorkFlowHistoryItem(
                		commonUtils.getFormattedDate(historyState.getCreatedDate(), "dd/MM/yyyy hh:mm a"), pos, nextAction,
                        historyState.getValue(),
                        historyState.getComments() != null ? commonUtils.removeSpecialCharacters(historyState.getComments()) : "");
                inboxHistory.add(inboxHistoryItem);
            }

        }
        pos = voucher.getState().getSenderName().concat(" / ").concat(voucher.getState().getSenderName());
        nextAction = voucher.getState().getNextAction();
        inboxHistoryItem = new WorkFlowHistoryItem(
        		commonUtils.getFormattedDate(voucher.getState().getCreatedDate(), "dd/MM/yyyy hh:mm a"), pos, nextAction,
                voucher.getState().getValue(), voucher.getState().getComments() != null
                        ? commonUtils.removeSpecialCharacters(voucher.getState().getComments()) : "");
        inboxHistory.add(inboxHistoryItem);

	}
	
	
	
}