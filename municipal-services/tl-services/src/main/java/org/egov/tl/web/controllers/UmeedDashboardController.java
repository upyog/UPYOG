package org.egov.tl.web.controllers;

import org.egov.tl.service.UmeedDashboardService;
import org.egov.tl.web.models.RequestInfoWrapper;
import org.egov.tl.web.models.contract.UmeedDashboardResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequestMapping("/umeed-dashbaord")
public class UmeedDashboardController {

	@Autowired
	private UmeedDashboardService umeedDashboardService;

	@PostMapping("/data-metrics")
	public ResponseEntity<?> prepareDataMetrics(@RequestBody RequestInfoWrapper requestInfoWrapper) {

		UmeedDashboardResponse dataResponse = umeedDashboardService.prepareDataMetrics(requestInfoWrapper);

		return ResponseEntity.ok(dataResponse);
	}

}
