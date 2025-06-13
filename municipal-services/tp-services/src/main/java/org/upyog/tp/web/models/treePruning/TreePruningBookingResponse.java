package org.upyog.tp.web.models.treePruning;

import javax.validation.Valid;
import org.springframework.validation.annotation.Validated;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.upyog.tp.web.models.ResponseInfo;
/**
 * A Object holds the Tree Pruning for booking
 */
@ApiModel(description = "A Object holds the tree Pruning for booking")
@Validated
@javax.annotation.Generated(value = "org.egov.codegen.SpringBootCodegen", date = "2024-04-19T11:17:29.419+05:30")

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TreePruningBookingResponse {

	private ResponseInfo responseInfo;

	@JsonProperty("treePruningBookingDetail")
	@Valid
	private TreePruningBookingDetail treePruningBookingApplication;

}

