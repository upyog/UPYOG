package org.upyog.adv.web.models;

import java.util.Collections;
import java.util.List;

import org.upyog.adv.validator.ValidDate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString(exclude = "mobileNumber")
@EqualsAndHashCode
public class AdvertisementSearchCriteria {

    @JsonProperty("tenantId")
    private String tenantId;

    @JsonProperty("bookingIds")
    private List<String> bookingIds = Collections.emptyList(); // Default to empty list

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

    @ValidDate
    @JsonProperty("fromDate")
    private String fromDate;

    @ValidDate
    @JsonProperty("toDate")
    private String toDate;

    private boolean isCountCall;

    @JsonProperty("createdBy")
    @JsonIgnore
    private List<String> createdBy = Collections.emptyList(); // Default to empty list

    public boolean isEmpty() {
        return tenantId == null && status == null && bookingIds.isEmpty() &&
               bookingNo == null && mobileNumber == null &&
               fromDate == null && toDate == null && createdBy.isEmpty();
    }

    public boolean tenantIdOnly() {
        return tenantId != null && status == null && bookingIds.isEmpty() &&
               bookingNo == null && mobileNumber == null &&
               fromDate == null && toDate == null && createdBy.isEmpty();
    }
}
