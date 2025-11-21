package org.upyog.rs.web.models.mobileToilet;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class MobileToiletBookingSearchCriteria {
    @JsonProperty("tenantId")
    private String tenantId;

    @JsonProperty("status")
    private String status;

    @JsonProperty("bookingNo")
    private String bookingNo;

    @JsonProperty("mobileNumber")
    private String mobileNumber;

    @JsonProperty("localityCode")
    private String localityCode;

    @JsonProperty("offset")
    private Integer offset;

    @JsonProperty("limit")
    private Integer limit;

    // @ValidDate
    @JsonProperty("fromDate")
    private String fromDate;

    // @ValidDate
    @JsonProperty("toDate")
    private String toDate;

    private boolean isCountCall;

    @JsonProperty("createdBy")
    @JsonIgnore
    private List<String> createdBy;

    public boolean isEmpty() {
        return (this.tenantId == null && this.status == null && this.bookingNo == null
                && this.mobileNumber == null
                // && this.offset == null && this.limit == null
                && this.fromDate == null && this.toDate == null && this.createdBy==null && localityCode==null);
    }

    public boolean tenantIdOnly() {
        return (this.tenantId != null && this.status == null && this.bookingNo == null
                && this.mobileNumber == null
                // && this.offset == null && this.limit == null
                && this.fromDate == null && this.toDate == null && this.createdBy==null && this.localityCode==null);
    }
}
