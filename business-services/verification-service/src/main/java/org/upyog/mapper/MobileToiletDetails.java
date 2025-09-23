package org.upyog.mapper;


import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;
import org.upyog.web.models.CommonDetails;
import static org.upyog.constants.VerificationSearchConstants.*;
import org.upyog.util.CommonDetailUtil;

@Component
public class MobileToiletDetails implements CommonDetailsMapper {

    @Override
    public String getModuleName() {
        return MOBILE_TOILET_MODULE_NAME;
    }

    @Override
    public CommonDetails mapJsonToCommonDetails(JsonNode json) {
        JsonNode mtDetailNode = json.path(MOBILE_TOILET_APPLICATIONS).isArray() && json.path(MOBILE_TOILET_APPLICATIONS).size() > 0
                ? json.path(MOBILE_TOILET_APPLICATIONS).get(0)
                : null;

        if (mtDetailNode == null) {
            return CommonDetails.builder().applicationNumber(NA).fromDate(NA).toDate(NA).address(NA).name(NA).mobileNumber(NA)
                    .status(NA).build();
        }

        // Extract fields based on response structure
        String applicationNumber = mtDetailNode.path("bookingNo").asText(NA);
        String status = mtDetailNode.path("bookingStatus").asText(NA);
        String moduleName = "request-service.water_tanker";
        String deliveryFromDate = mtDetailNode.path("deliveryFromDate").asText(NA);
        String deliveryToDate = mtDetailNode.path("deliveryToDate").asText(NA);

        // Extract applicant details
        JsonNode applicantDetail = mtDetailNode.path("applicantDetail");
        String name = applicantDetail.path("name").asText(NA);
        String mobileNumber = CommonDetailUtil.maskMobileNumber(
                applicantDetail.path("mobileNumber").asText(NA)
        );

        // Extract address
        JsonNode address = mtDetailNode.path("address");
        String fullAddress = String.format("%s, %s, %s", address.path("addressLine1").asText(""), address.path("locality").asText(""),
                address.path("city").asText("")
        ).replaceAll("^,\\s*|,\\s*$", "").replaceAll(",\\s*,", ",");

        if (!"APPROVED".equalsIgnoreCase(status)) {
            // If not APPROVED, set status as Pending and other details as N/A
            return CommonDetails.builder().applicationNumber(applicationNumber).fromDate(NA).toDate(NA).address(NA).name(NA).mobileNumber(NA).status("Pending").moduleName(moduleName)
                    .build();
        }

        return CommonDetails.builder().applicationNumber(applicationNumber).fromDate(deliveryFromDate).toDate(deliveryToDate).address(fullAddress.isEmpty() ? NA : fullAddress).name(name).mobileNumber(mobileNumber).status(status).moduleName(moduleName)
                .build();
    }
}
