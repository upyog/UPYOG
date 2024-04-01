package org.egov.pt.calculator.web.controller;

import java.util.List;

import javax.validation.Valid;

import org.egov.common.contract.request.RequestInfo;
import org.egov.pt.calculator.service.DefaultersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/property/defaulters")
public class DefaultersController {

	@Autowired
	private DefaultersService defaultersService;

	@PostMapping("/_sendsms")
	public ResponseEntity<Object> create(@Valid @RequestBody RequestInfo requestInfo) {

		List<String> notifiedTenants = defaultersService.fetchAllDefaulterDetails(requestInfo);

		return new ResponseEntity<>(
				"SMS notifiacion has been sent to all the defaulters in the cities :" + notifiedTenants,
				HttpStatus.CREATED);
	}

}
