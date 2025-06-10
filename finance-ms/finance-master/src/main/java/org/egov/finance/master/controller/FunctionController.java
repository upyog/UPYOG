/**
 * Created on Jun 9, 2025.
 * 
 * @author bdhal
 */
package org.egov.finance.master.controller;

import java.util.Arrays;
import java.util.List;

import org.egov.finance.master.model.FunctionModel;
import org.egov.finance.master.model.FundModel;
import org.egov.finance.master.model.ResponseInfo;
import org.egov.finance.master.model.request.FunctionRequest;
import org.egov.finance.master.model.request.FundRequest;
import org.egov.finance.master.model.response.FunctionResponse;
import org.egov.finance.master.model.response.FundResponse;
import org.egov.finance.master.service.FunctionService;
import org.egov.finance.master.service.FundService;
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
@RequestMapping("/function")
public class FunctionController {
	@Autowired
	private FunctionService functionService;
	@Autowired
	private ResponseInfoFactory responseInfoFactory;
	
	@PostMapping("/_save")
	public ResponseEntity<FunctionResponse> saveFund(@Valid @RequestBody FunctionRequest function) {
		final FunctionModel funcM =null;//= functionService.save(fund);
		ResponseInfo resInfo = responseInfoFactory.createResponseInfoFromRequestInfo(function.getRequestInfo(), true);
		FunctionResponse response = FunctionResponse.builder().responseInfo(resInfo).function(Arrays.asList(funcM)).build();
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	@PostMapping(value = "/_search")
	public ResponseEntity<FunctionResponse> search(@RequestBody FunctionRequest request) {
		final List<FunctionModel> funcM =null;;//= functionService.search(request.getFund());
		ResponseInfo resInfo = responseInfoFactory.createResponseInfoFromRequestInfo(request.getRequestInfo(), true);
		FunctionResponse response = FunctionResponse.builder().responseInfo(resInfo).function(funcM).build();
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PostMapping(value = "/_update")
	public ResponseEntity<FunctionResponse> update(@RequestBody FunctionRequest fundupdate) {
		final FunctionModel funcM=null ;//= fundService.update(fundupdate);
		ResponseInfo resInfo = responseInfoFactory.createResponseInfoFromRequestInfo(fundupdate.getRequestInfo(), true);
		FunctionResponse response = FunctionResponse.builder().responseInfo(resInfo).function(Arrays.asList(funcM)).build();
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

}

