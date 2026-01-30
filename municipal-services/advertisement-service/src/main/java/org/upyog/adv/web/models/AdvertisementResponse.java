package org.upyog.adv.web.models;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;


import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Contract class to send response. Array of  items are used in case of search results or response for create, whereas single  item is used for update
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdvertisementResponse   {
        @JsonProperty("ResponseInfo")
        private ResponseInfo responseInfo;

    

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

