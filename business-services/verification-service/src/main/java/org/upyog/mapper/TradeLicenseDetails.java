package org.upyog.mapper;
import org.springframework.stereotype.Component;
import org.upyog.util.CommonDetailUtil;
import org.upyog.web.models.CommonDetails;
import static org.upyog.constants.VerificationSearchConstants.*;
import com.fasterxml.jackson.databind.JsonNode;

@Component
public class TradeLicenseDetails implements CommonDetailsMapper {
	
	@Override
	public String getModuleName() {
		return TradeLicenseModule;
	}

	@Override
	public CommonDetails mapJsonToCommonDetails(JsonNode json) {
		JsonNode tradeDetailNode = json.path(TradeApplications).isArray() && json.path(TradeApplications).size() > 0
				? json.path(TradeApplications).get(0)
				: null;

		if (tradeDetailNode == null) {
			return CommonDetails.builder().fromDate(NA).toDate(NA).address(EmptyString).status(EmptyString).applicationNumber(EmptyString).name(NA).mobileNumber(NA)
					.build();
		}
		
		long validFrom = tradeDetailNode.path("validFrom").asLong(0L);    //valid to is also available so creating one more variable
		long validTo = tradeDetailNode.path("validTo").asLong(0L);
		String validFromString = NA;
		String validToString = NA;
		String status = tradeDetailNode.path("status").asText(EmptyString);
		String applicationNumber = tradeDetailNode.path("applicationNumber").asText(EmptyString);
		String moduleName = "BPA";
		if (!"APPROVED".equalsIgnoreCase(status)) {
			// If not Completed, set status as Pending and other details as N/A
			return CommonDetails.builder().applicationNumber(applicationNumber).fromDate(NA).toDate(NA).name(NA).mobileNumber(NA)
					.address(NA).status("Pending").moduleName(moduleName).build();
		}
        // Extract owner details (name and mobile number)
        JsonNode ownerNode = tradeDetailNode.path("tradeLicenseDetail").path("owners").isArray()
                && tradeDetailNode.path("tradeLicenseDetail").path("owners").size() > 0
                ? tradeDetailNode.path("tradeLicenseDetail").path("owners").get(0)
                : null;

        String ownerName = ownerNode != null ? ownerNode.path("name").asText(NA) : NA;
        String ownerMobileNumber = ownerNode != null ? CommonDetailUtil.maskMobileNumber (ownerNode.path("mobileNumber").asText(NA)) : NA;
        
		if (validFrom != 0L && validTo != 0L) {
			validFromString = CommonDetailUtil.convertToFormattedDate(String.valueOf(validFrom), Date);
			validToString = CommonDetailUtil.convertToFormattedDate(String.valueOf(validTo), Date);
		}
		
		return CommonDetails.builder().applicationNumber(applicationNumber).fromDate(validFromString)
				.toDate(validToString).address(NA).moduleName(moduleName).name(ownerName).mobileNumber(ownerMobileNumber)
				.status(status).build();
	}

}
