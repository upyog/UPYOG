package org.upyog.adv.web.models;

import java.util.ArrayList;
import java.util.List;

<<<<<<< HEAD
import jakarta.validation.Valid;
=======
import javax.validation.Valid;
>>>>>>> master-LTS

import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonProperty;

<<<<<<< HEAD
import io.swagger.v3.oas.annotations.media.Schema;
=======
import io.swagger.annotations.ApiModel;
>>>>>>> master-LTS
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Contract class to send response. Array of  items are used in case of search results or response for create, whereas single  item is used for update
 */
<<<<<<< HEAD
@Schema(description = "Contract class to send response. Array of  items are used in case of search results or response for create, whereas single  item is used for update")
@Validated
@jakarta.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-10-15T13:40:01.245+05:30")
=======
@ApiModel(description = "Contract class to send response. Array of  items are used in case of search results or response for create, whereas single  item is used for update")
@Validated
@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-10-15T13:40:01.245+05:30")
>>>>>>> master-LTS

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

