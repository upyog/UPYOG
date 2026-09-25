package org.upyog.mapper;
import org.springframework.stereotype.Component;
import org.upyog.util.CommonDetailUtil;
import org.upyog.web.models.CommonDetails;
import static org.upyog.constants.VerificationSearchConstants.*;
import com.fasterxml.jackson.databind.JsonNode;

@Component
public class TradeLicenseDetails implements CommonDetailsMapper {

	private static final String TRADE_LICENSE_DETAIL = "tradeLicenseDetail";
	private static final String OWNERS = "owners";

	@Override
	public String getModuleName() {
		return TRADE_LICENSE_MODULE_NAME;
	}

	@Override
	public CommonDetails mapJsonToCommonDetails(JsonNode json) {
		JsonNode tradeDetailNode = json.path(TRADE_APPLICATIONS).isArray() && json.path(TRADE_APPLICATIONS).size() > 0
				? json.path(TRADE_APPLICATIONS).get(0)
				: null;

		if (tradeDetailNode == null) {
			return CommonDetails.builder().fromDate(NA).toDate(NA).address(EMPTY_STRING).status(EMPTY_STRING).applicationNumber(EMPTY_STRING).name(NA).mobileNumber(NA)
					.build();
		}

		long validFrom = tradeDetailNode.path("validFrom").asLong(0L);
		long validTo = tradeDetailNode.path("validTo").asLong(0L);
		String validFromString = NA;
		String validToString = NA;
		String status = tradeDetailNode.path("status").asText(EMPTY_STRING);
		String applicationNumber = tradeDetailNode.path("applicationNumber").asText(EMPTY_STRING);
		String moduleName = "BPA";
		if (!"APPROVED".equalsIgnoreCase(status)) {
			return CommonDetails.builder().applicationNumber(applicationNumber).fromDate(NA).toDate(NA).name(NA).mobileNumber(NA)
					.address(NA).status("Pending").moduleName(moduleName).build();
		}
		JsonNode ownersNode = tradeDetailNode.path(TRADE_LICENSE_DETAIL).path(OWNERS);
		JsonNode ownerNode = ownersNode.isArray() && ownersNode.size() > 0
				? ownersNode.get(0)
				: null;

		String ownerName = ownerNode != null ? ownerNode.path("name").asText(NA) : NA;
		String ownerMobileNumber = ownerNode != null ? CommonDetailUtil.maskMobileNumber(ownerNode.path("mobileNumber").asText(NA)) : NA;

		if (validFrom != 0L && validTo != 0L) {
			validFromString = CommonDetailUtil.convertToFormattedDate(String.valueOf(validFrom), DATE_FORMAT);
			validToString = CommonDetailUtil.convertToFormattedDate(String.valueOf(validTo), DATE_FORMAT);
		}

		return CommonDetails.builder().applicationNumber(applicationNumber).fromDate(validFromString)
				.toDate(validToString).address(NA).moduleName(moduleName).name(ownerName).mobileNumber(ownerMobileNumber)
				.status(status).build();
	}

}
