package org.egov.garbageservice.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.egov.common.contract.request.RequestInfo;
import org.egov.tracer.annotations.CustomSafeHtml;

import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
/**
 * Scheduler request to generate garbage bills for applications, wards, or ULBs in a period.
 * Posted to /garbage-accounts-scheduler/bill-generator and processed by GarbageAccountSchedulerService.
 */
@AllArgsConstructor
public class GenerateBillRequest {

    @JsonProperty("RequestInfo")
    private RequestInfo requestInfo;

    @JsonProperty("grbgApplicationNumbers")
    private List<String> grbgApplicationNumbers;

    @JsonProperty("ulbNames")
    private List<String> ulbNames;

    @JsonProperty("wardNumbers")
    private List<String> wardNumbers;

    @JsonProperty("mobileNumbers")
    private List<String> mobileNumbers;

    @JsonProperty("months")
    private List<String> months;

    @JsonProperty("year")
    @CustomSafeHtml
    private String year;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private Date fromDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy")
    private Date toDate;

    @Builder.Default
    @JsonProperty("type")
    @CustomSafeHtml
    private String type = "MONTHLY";

    @JsonProperty("additionalDetail")
    private Object additionalDetail;

    @Builder.Default
    private Boolean isRebate = false;

    @Builder.Default
    private Boolean isMultiMonth = false;

    @JsonProperty("fromDateTimestamp")
    private Long fromDateTimestamp;

    @JsonProperty("toDateTimestamp")
    private Long toDateTimestamp;

}
