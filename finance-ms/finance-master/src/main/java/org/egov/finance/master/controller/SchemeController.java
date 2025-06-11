package org.egov.finance.master.controller;

import java.util.Arrays;
import java.util.List;

import org.egov.finance.master.model.ResponseInfo;
import org.egov.finance.master.model.SchemeModel;
import org.egov.finance.master.model.request.SchemeRequest;
import org.egov.finance.master.model.response.SchemeResponse;
import org.egov.finance.master.service.SchemeService;
import org.egov.finance.master.util.ResponseInfoFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/scheme")
public class SchemeController {

	@Autowired
	private SchemeService schemeService;
	@Autowired
	private ResponseInfoFactory responseInfoFactory;

	@PostMapping("/_save")
	public ResponseEntity<SchemeResponse> saveScheme(@Valid @RequestBody SchemeRequest scheme) {
		final SchemeModel schemeM = schemeService.save(scheme);
		ResponseInfo resInfo = responseInfoFactory.createResponseInfoFromRequestInfo(scheme.getRequestInfo(), true);
		SchemeResponse response = SchemeResponse.builder().responseInfo(resInfo).schemes(Arrays.asList(schemeM))
				.build();
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	@PostMapping("/_search")
	public ResponseEntity<SchemeResponse> search(@RequestBody SchemeRequest request) {
		final List<SchemeModel> schemeM = schemeService.search(request.getScheme());
		ResponseInfo resInfo = responseInfoFactory.createResponseInfoFromRequestInfo(request.getRequestInfo(), true);
		SchemeResponse response = SchemeResponse.builder().responseInfo(resInfo).schemes(schemeM).build();
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PostMapping("/_update")
	public ResponseEntity<SchemeResponse> update(@RequestBody SchemeRequest schemeUpdate) {
		final SchemeModel schemeM = schemeService.update(schemeUpdate);
		ResponseInfo resInfo = responseInfoFactory.createResponseInfoFromRequestInfo(schemeUpdate.getRequestInfo(),
				true);
		SchemeResponse response = SchemeResponse.builder().responseInfo(resInfo).schemes(Arrays.asList(schemeM))
				.build();
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

}
