package org.upyog.mapper;


import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Component;
import org.upyog.web.models.CommonDetails;
import static org.upyog.constants.VerificationSearchConstants.*;
import org.upyog.util.CommonDetailUtil;

@Component
public class WaterTankerDetails implements CommonDetailsMapper {
    @Override
    public String getModuleName() {
        return WATER_TANKER_MODULE_NAME;
    }

    @Override
    public CommonDetails mapJsonToCommonDetails(JsonNode json) {
        JsonNode wtDetailNode = json.path(WATER_TANKER_APPLICATIONS).isArray() && json.path(WATER_TANKER_APPLICATIONS).size() > 0
                ? json.path(WATER_TANKER_APPLICATIONS).get(0)
                : null;
        if (wtDetailNode == null) {
            return CommonDetails.builder().applicationNumber(NA).fromDate(NA).toDate(NA).address(NA).name(NA).mobileNumber(NA)
                    .status(NA).build();
        }

        //extract applicant details
        String applicationNumber = wtDetailNode.path("bookingNo").asText(NA);
        String status = wtDetailNode.path("bookingStatus").asText(NA);
        String moduleName = "request-service.water_tanker";
        String fromDate = wtDetailNode.path("deliveryDate").asText(NA);

        JsonNode applicantDetail = wtDetailNode.path("applicantDetail");
        String name = applicantDetail.path("name").asText(NA);
        String mobileNumber = CommonDetailUtil.maskMobileNumber(
                applicantDetail.path("mobileNumber").asText(NA)
        );

        JsonNode address = wtDetailNode.path("address");
        String fullAddress = String.format("%s, %s, %s",
                address.path("addressLine1").asText(""),
                address.path("locality").asText(""),
                address.path("city").asText("")
        ).replaceAll("^,\\s*|,\\s*$", "").replaceAll(",\\s*,", ",");

        if (!"APPROVED".equalsIgnoreCase(status)) {
            // If not APPROVED, set status as Pending and other details as N/A
            return CommonDetails.builder().applicationNumber(applicationNumber).fromDate(NA).toDate(NA).address(NA).name(NA).mobileNumber(NA).status("Pending").moduleName(moduleName)
                    .build();
        }
        return CommonDetails.builder().applicationNumber(applicationNumber).fromDate(fromDate).toDate(NA).address(fullAddress.isEmpty() ? NA : fullAddress).name(name).mobileNumber(mobileNumber).status(status).moduleName(moduleName)
                .build();
    }
}
