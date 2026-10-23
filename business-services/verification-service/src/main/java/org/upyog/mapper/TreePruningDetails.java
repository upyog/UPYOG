package org.upyog.mapper;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;
import org.upyog.web.models.CommonDetails;
import static org.upyog.constants.VerificationSearchConstants.*;
import org.upyog.util.CommonDetailUtil;

@Component
public class TreePruningDetails implements CommonDetailsMapper {

	@Override
	public String getModuleName() {
		return TREE_PRUNING_MODULE_NAME;
	}

	@Override
	public CommonDetails mapJsonToCommonDetails(JsonNode json) {
		JsonNode tpDetailNode = json.path(TREE_PRUNING_APPLICATIONS).isArray()
				&& json.path(TREE_PRUNING_APPLICATIONS).size() > 0
				? json.path(TREE_PRUNING_APPLICATIONS).get(0)
				: null;

		if (tpDetailNode == null) {
			return CommonDetails.builder()
					.applicationNumber(NA).fromDate(NA).toDate(NA)
					.address(NA).name(NA).mobileNumber(NA)
					.status(NA)
					.build();
		}

		String applicationNumber = tpDetailNode.path("bookingNo").asText(NA);
		String status = tpDetailNode.path("bookingStatus").asText(NA);
		String moduleName = "tp-services";

		long epochTime = tpDetailNode.path("applicationDate").asLong(0);
		String applicationDate = epochTime > 0
				? Instant.ofEpochMilli(epochTime).atZone(ZoneId.systemDefault())
						.format(DateTimeFormatter.ofPattern(DATE_FORMAT))
				: NA;

		JsonNode applicantDetail = tpDetailNode.path("applicantDetail");
		String name = applicantDetail.path("name").asText(NA);
		String mobileNumber = CommonDetailUtil.maskMobileNumber(
				applicantDetail.path("mobileNumber").asText(NA)
		);

		JsonNode address = tpDetailNode.path("address");
		String fullAddress = CommonDetailUtil.normalizeCommaSeparatedAddress(String.format("%s, %s, %s, %s",
				address.path("addressLine1").asText(""),
				address.path("locality").asText(""),
				address.path("city").asText(""),
				address.path("pincode").asText("")
		));

		return CommonDetails.builder().applicationNumber(applicationNumber).fromDate(applicationDate).toDate(NA).address(fullAddress.isEmpty() ? NA : fullAddress).name(name).mobileNumber(mobileNumber).status(status).moduleName(moduleName)
				.build();
	}
}
