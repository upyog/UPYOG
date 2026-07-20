package org.egov.garbageservice.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.egov.common.contract.request.RequestInfo;
import org.egov.garbageservice.contract.workflow.ValidActionResponce;
import org.egov.garbageservice.contract.workflow.WorkflowService;
import org.egov.garbageservice.model.GarbageAccount;
import org.egov.garbageservice.model.GarbageAccountRequest;
import org.egov.garbageservice.model.GarbageAccountResponse;
import org.egov.garbageservice.util.ExcelUtils;
import org.egov.garbageservice.util.GrbgConstants;
import org.egov.garbageservice.util.RequestInfoUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;

/**
 * Parses uploaded Excel files into GarbageAccount rows for bulk registration.
 * Used by GarbageExcelController; validation and persistence continue in GarbageAccountService.
 */
@Slf4j
@Service
public class GarbageExcelService {

	@Autowired
	private ExcelUtils excelUtils;

	@Autowired
	private GarbageAccountService garbageAccountService;

	@Autowired
	private RequestInfoUtils requestInfoUtils;

	@Autowired
	private WorkflowService workflowService;

	public List<GarbageAccount> createFromExcel(MultipartFile file) {

		List<GarbageAccount> garbageAccountsCreated = new ArrayList<>();
		List<GarbageAccount> garbageAccountList = new ArrayList<>();

		RequestInfo requestInfo = requestInfoUtils.getSystemRequestInfo();

		garbageAccountList = excelUtils.parseExcel(file);

		if (CollectionUtils.isEmpty(garbageAccountList)) {
			return Collections.emptyList();
		}

		if (null != requestInfo && null != requestInfo.getUserInfo()) {
			requestInfo.getUserInfo().setType(GrbgConstants.USER_TYPE_EMPLOYEE);
		}
		GarbageAccountRequest garbageAccountRequest = GarbageAccountRequest.builder()
				.garbageAccounts(garbageAccountList).requestInfo(requestInfo).build();
		GarbageAccountResponse garbageAccountCreateResponse = garbageAccountService.create(garbageAccountRequest);
		if (null != garbageAccountCreateResponse
				&& !CollectionUtils.isEmpty(garbageAccountCreateResponse.getGarbageAccounts())) {
			garbageAccountsCreated.addAll(garbageAccountCreateResponse.getGarbageAccounts());
		}
		
		return garbageAccountsCreated;
	}

	// Helper method to process and collect garbage accounts
	private List<GarbageAccount> processGarbageAccounts(Map<String, GarbageAccount> garbageAccountMap,
			RequestInfo requestInfo) {
		return garbageAccountMap.entrySet().stream().map(entry -> processUntilApproved(entry.getValue(), requestInfo))
				.collect(Collectors.toList());
	}

	private GarbageAccount processUntilApproved(GarbageAccount garbageAccount, RequestInfo requestInfo) {
		if (null != requestInfo && null != requestInfo.getUserInfo()) {
			requestInfo.getUserInfo().setType(GrbgConstants.USER_TYPE_SYSTEM);
		}
		GarbageAccount finalGarbageAccount = garbageAccount;

		while (!finalGarbageAccount.getStatus().equals("APPROVED")) {
			ValidActionResponce validActionResponce = workflowService.getValidAction(requestInfo,
					garbageAccount.getGrbgApplication().getApplicationNo(), garbageAccount.getTenantId());

			if (validActionResponce.getNextValidAction().size() == 3) {
				System.err.println(validActionResponce.getNextValidAction().size());
			}

			if (null != validActionResponce && !CollectionUtils.isEmpty(validActionResponce.getNextValidAction())) {
				GarbageAccount updateGarbageAccount = GarbageAccount.builder().isOnlyWorkflowCall(true)
						.workflowAction(validActionResponce.getNextValidAction().get(0).getAction())
						.grbgApplicationNumber(garbageAccount.getGrbgApplication().getApplicationNo())
						.workflowComment("From Migration").build();

				GarbageAccountRequest garbageAccountUpdateRequest = GarbageAccountRequest.builder()
						.requestInfo(requestInfo).garbageAccounts(Collections.singletonList(updateGarbageAccount))
						.fromMigration(true).build();

				GarbageAccountResponse garbageAccountUpdateResponse = garbageAccountService
						.update(garbageAccountUpdateRequest);
				if (null != garbageAccountUpdateResponse
						&& !CollectionUtils.isEmpty(garbageAccountUpdateResponse.getGarbageAccounts())) {
					garbageAccountUpdateResponse.getGarbageAccounts().get(0).setChildGarbageAccounts(null);
					finalGarbageAccount = garbageAccountUpdateResponse.getGarbageAccounts().get(0);
				}
			}
		}
		return finalGarbageAccount;
	}

}
