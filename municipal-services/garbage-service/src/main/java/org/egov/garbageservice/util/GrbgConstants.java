package org.egov.garbageservice.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
public class GrbgConstants {

	public static final String MDMS_MODULE_NAME_FEE_STRUCTURE = "Garbage";

	public static final String MDMS_MASTER_NAME_FEE_STRUCTURE = "FeeStructure";

	public static final String STATE_LEVEL_TENANT_ID = "hp";

	public static final String APPLICATION_PREFIX = "GB/HP/DISTRICT/ULBNAME/MMYYYY/XXXXXX";

	public static final String DOCUMENT_ACCOUNT = "ACCOUNT";

	public static final String STATUS_INITIATED = "INITIATED";

	public static final String STATUS_PENDINGFORMODIFICATION = "PENDINGFORMODIFICATION";

	public static final String STATUS_PENDINGFORVERIFICATION = "PENDINGFORVERIFICATION";

	public static final String STATUS_PENDINGFORPAYMENT = "PENDINGFORPAYMENT";

	public static final String STATUS_PENDINGFORAPPROVAL = "PENDINGFORAPPROVAL";

	public static final String STATUS_APPROVED = "APPROVED";

	public static final String STATUS_REJECTED = "REJECTED";

	public static final String STATUS_CANCELLED = "CANCELLED";

	public static final String WORKFLOW_ACTION_INITIATE = "INITIATE";

	public static final String WORKFLOW_ACTION_VERIFY = "VERIFY";

	public static final String WORKFLOW_ACTION_FORWARD_TO_VERIFIER = "FORWARD_TO_VERIFIER";

	public static final String WORKFLOW_ACTION_APPROVE = "APPROVE";

	public static final String WORKFLOW_ACTION_FORWARD_TO_APPROVER = "FORWARD_TO_APPROVER";

	public static final String WORKFLOW_ACTION_RETURN_TO_INITIATOR = "RETURN_TO_INITIATOR";

	public static final String WORKFLOW_ACTION_RETURN_TO_VERIFIER = "RETURN_TO_VERIFIER";

	public static final String WORKFLOW_ACTION_RETURN_TO_INITIATOR_FOR_PAYMENT = "RETURN_TO_INITIATOR_FOR_PAYMENT";

	public static final String WORKFLOW_BUSINESS_SERVICE = "GB";

	public static final String BUSINESS_SERVICE = "GB";

	public static final String WORKFLOW_MODULE_NAME = "GB";

	public static final String USER_TYPE_CITIZEN = "CITIZEN";

	public static final String USER_TYPE_EMPLOYEE = "EMPLOYEE";

	public static final String BILLING_TAX_HEAD_MASTER_CODE = "LCF.Garbage_Collection_Fee";

	public static final String USER_ROLE_GB_VERIFIER = "GB_VERIFIER";

	public static final String USER_ROLE_GB_APPROVER = "GB_APPROVER";

	public static final String USER_ROLE_SUPERVISOR = "SUPERVISOR";

	public static final String USER_ROLE_SECRETARY = "SECRETARY";

	// Alfresco keys
	public static final Long ALFRESCO_COMMON_DOCUMENT_ID = 0L;
	public static final String ALFRESCO_COMMON_CERTIFICATE_DESCRIPTION = "GB certificate";
	public static final String ALFRESCO_COMMON_CERTIFICATE_ID = "";
	public static final String ALFRESCO_COMMON_CERTIFICATE_TYPE = "PDF";
	public static final String ALFRESCO_DOCUMENT_TYPE = "CERT";
	public static final String ALFRESCO_TL_CERTIFICATE_COMMENT = "Signed Certificate";

	@Value("${frontend.base.uri}")
	public String frontEndBaseUri;

	@Value("${workflow.context.path}")
	public String workflowHost;

	@Value("${workflow.transition.path}")
	public String workflowEndpointTransition;

	@Value("${workflow.business.search}")
	public String workflowBusinessServiceSearchPath;

	@Value("${egov.bill.context.host}")
	public String billHost;

	@Value("${egov.bill.endpoint.fetch}")
	public String fetchBillEndpoint;

	@Value("${egov.bill.endpoint.search}")
	public String searchBillEndpoint;

	@Value("${egov.demand.create.endpoint}")
	public String demandCreateEndpoint;

	@Value("${egov.demand.search.endpoint}")
	public String demandSearchEndpoint;

	@Value("${egov.demand.update.endpoint}")
	public String demandUpdateEndpoint;

	@Value("${egov.report.host}")
	public String reportHost;

	@Value("${egov.report.endpoint.create}")
	public String reportCreateEndPoint;

	@Value("${egov.alfresco.host}")
	public String alfrescoHost;

	@Value("${egov.alfresco.endpoint.upload}")
	public String alfrescoUploadEndPoint;

	@Value("${egov.mdms.host}")
	private String mdmsServiceHostUrl;

	@Value("${egov.mdms.search.endpoint}")
	private String mdmsSearchEndpoint;

	public static String generateApplicationNumberFormat(String id, String ulbName, String district) {
		String appNo = null;
		appNo = GrbgConstants.APPLICATION_PREFIX;

		if (StringUtils.isEmpty(ulbName) || StringUtils.isEmpty(district) || StringUtils.isEmpty(id)) {
			throw new CustomException("APPLICATION_NUMBER_GENERATION_FAILED",
					"Ulb Name, District and Id can't be empty.");
		}

		appNo = appNo.replace("ULBNAME", ulbName);
		appNo = appNo.replace("DISTRICT", district);
		appNo = appNo.replace("XXXXXX", id);

		SimpleDateFormat sf = new SimpleDateFormat("MMYYYY");
		Date date = new Date();
		String mmyyyy = sf.format(date);
		appNo = appNo.replace("MMYYYY", mmyyyy);

		return appNo;
	}

	public static String toCamelCase(String str) {
		// Convert the entire string to lowercase and then capitalize the first letter
		if (str == null || str.isEmpty()) {
			return str;
		}
		// Convert the first letter to uppercase and the rest to lowercase
		return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
	}

	public static String removeFirstAndLastChar(String str) {
		// Check if the string is long enough to remove first and last characters
		if (str == null || str.length() <= 2) {
			return ""; // Return empty string if length is 2 or less
		}

		// Use substring to remove first and last character
		return str.substring(1, str.length() - 1);
	}

	public static String getContentAsString(String filePath) {
		String htmlContent = "";
		try {
			// Read all lines from the file and join them into a single string
			htmlContent = Files.lines(Paths.get(filePath)).collect(Collectors.joining("\n"));
			// Output the content of the HTML file
		} catch (IOException e) {
			// Handle exception if the file is not found or can't be read
//			e.printStackTrace();
		}
		return htmlContent;
	}

}
