package org.egov.waterquality.web.controller;

import java.util.List;

import javax.validation.Valid;

import org.egov.waterquality.service.WaterQualityService;
import org.egov.waterquality.util.ResponseInfoFactory;
import org.egov.waterquality.web.models.collection.RequestInfoWrapper;
import org.egov.waterquality.web.models.collection.SearchCriteria;
import org.egov.waterquality.web.models.collection.WaterQuality;
import org.egov.waterquality.web.models.collection.WaterQualityRequest;
import org.egov.waterquality.web.models.collection.WaterQualityResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@RestController
@RequestMapping("/wq")
public class WaterQualityController {

	@Autowired
	private WaterQualityService waterQualityService;

	@Autowired
	private final ResponseInfoFactory responseInfoFactory;

	@RequestMapping(value = "/_create", method = RequestMethod.POST, produces = "application/json")
	public ResponseEntity<WaterQualityResponse> createWaterConnection(
			@Valid @RequestBody WaterQualityRequest waterQualityApplicationRequest) {
		List<WaterQuality> waterQualities = waterQualityService.createWaterQualityApplication(waterQualityApplicationRequest);
		WaterQualityResponse response = WaterQualityResponse.builder().waterQualityApplications(waterQualities)
				.responseInfo(responseInfoFactory
						.createResponseInfoFromRequestInfo(waterQualityApplicationRequest.getRequestInfo(), true))
				.build();
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@RequestMapping(value = "/_search", method = RequestMethod.POST)
	public ResponseEntity<WaterQualityResponse> search(@Valid @RequestBody RequestInfoWrapper requestInfoWrapper,
			@Valid @ModelAttribute SearchCriteria criteria) {
		List<WaterQuality> waterQualities = waterQualityService.search(criteria, requestInfoWrapper.getRequestInfo());
		WaterQualityResponse response = WaterQualityResponse.builder().waterQualityApplications(waterQualities)
				.responseInfo(responseInfoFactory.createResponseInfoFromRequestInfo(requestInfoWrapper.getRequestInfo(),
						true))
				.build();
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
}