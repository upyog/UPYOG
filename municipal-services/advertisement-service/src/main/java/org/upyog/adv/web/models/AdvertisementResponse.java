package org.upyog.adv.web.models;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Contract class to send response. Array of  items are used in case of search results or response for create, whereas single  item is used for update
 */
@ApiModel(description = "Contract class to send response. Array of  items are used in case of search results or response for create, whereas single  item is used for update")
@Validated
@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-10-15T13:40:01.245+05:30")

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdvertisementResponse   {
        @JsonProperty("ResponseInfo")
        private ResponseInfo responseInfo = null;

        @JsonProperty("EmployeeDashbaord")
        private Object employeeDashbaord = null;

		@JsonProperty("bookingApplication")
	@Valid
	private List<BookingDetail> bookingApplication; 
	
	public void addNewBookingApplication(BookingDetail bookingDetail) {
		if(this.bookingApplication == null) {
			this.bookingApplication = new ArrayList<BookingDetail>();
		}
		this.bookingApplication.add(bookingDetail);
	}
	
	private Integer count;


}

