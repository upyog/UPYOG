package org.upyog.request.service.web.models;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class WaterTankerBookingSearchCriteria {
    @JsonProperty("tenantId")
    private String tenantId;

    @JsonProperty("status")
    private String status;

    @JsonProperty("bookingNo")
    private String bookingNo;

    @JsonProperty("mobileNumber")
    private String mobileNumber;

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

    public boolean isEmpty() {
        return (this.tenantId == null && this.status == null && this.bookingNo == null
                && this.mobileNumber == null
                // && this.offset == null && this.limit == null
                && this.fromDate == null && this.toDate == null);
    }

    public boolean tenantIdOnly() {
        return (this.tenantId != null && this.status == null && this.bookingNo == null
                && this.mobileNumber == null
                // && this.offset == null && this.limit == null
                && this.fromDate == null && this.toDate == null);
    }
}
