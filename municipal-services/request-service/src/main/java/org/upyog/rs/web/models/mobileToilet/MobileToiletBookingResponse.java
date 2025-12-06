package org.upyog.rs.web.models.mobileToilet;

import java.util.ArrayList;
import java.util.List;

<<<<<<< HEAD
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
=======
import javax.validation.Valid;
import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
>>>>>>> master-LTS
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.upyog.rs.web.models.ResponseInfo;

/**
 * A Object holds the mobile Toilet for booking
 */
<<<<<<< HEAD
@Schema(description = "A Object holds the mobile Toilet for booking")
@Validated
@jakarta.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-04-19T11:17:29.419+05:30")
=======
@ApiModel(description = "A Object holds the mobile Toilet for booking")
@Validated
@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-04-19T11:17:29.419+05:30")
>>>>>>> master-LTS

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MobileToiletBookingResponse {

	private ResponseInfo responseInfo;

	@JsonProperty("mobileToiletBookingDetail")
	@Valid
	private MobileToiletBookingDetail mobileToiletBookingApplication;

}

