package org.upyog.mapper;
import org.springframework.stereotype.Component;
import org.upyog.util.CommonDetailUtil;
import org.upyog.web.models.CommonDetails;
import static org.upyog.constants.VerificationSearchConstants.*;
import com.fasterxml.jackson.databind.JsonNode;

@Component
public class BuildingPlanDetailMapper implements CommonDetailsMapper {
	
	@Override
	public String getModuleName() {
		return BPA_MODULE_NAME;
	}

	@Override
	public CommonDetails mapJsonToCommonDetails(JsonNode json) {
		JsonNode bpaDetailNode = json.path(BPA_APPLICATIONS).isArray() && json.path(BPA_APPLICATIONS).size() > 0
				? json.path(BPA_APPLICATIONS).get(0)
				: null;

		if (bpaDetailNode == null) {
			return CommonDetails.builder().fromDate(NA).toDate(NA).address(EMPTY_STRING).status(EMPTY_STRING).applicationNumber(EMPTY_STRING)
					.build();
		}
		
		long approvalDate = bpaDetailNode.path("approvalDate").asLong(0L);
		String validFromString = NA;
		String validToString = NA;
		String status = bpaDetailNode.path("status").asText(EMPTY_STRING);
		String applicationNumber = bpaDetailNode.path("applicationNo").asText(EMPTY_STRING);
		String moduleName = BPA_APPLICATIONS;
		if (!"APPROVED".equalsIgnoreCase(status)) {
			// If not Completed, set status as Pending and other details as N/A
			return CommonDetails.builder().applicationNumber(applicationNumber).fromDate(NA).toDate(NA)
					.address(NA).status("Pending").moduleName(moduleName).build();
		}
		if (approvalDate != 0L) {
			validFromString = CommonDetailUtil.convertToFormattedDate(String.valueOf(approvalDate), DATE_FORMAT);
			validToString = CommonDetailUtil.addOneYearToEpoch(String.valueOf(approvalDate)); // Add one year
		}
		
		return CommonDetails.builder().applicationNumber(applicationNumber).fromDate(validFromString)
				.toDate(validToString).address(NA).moduleName(moduleName)
				.status(status).build();
	}


}
